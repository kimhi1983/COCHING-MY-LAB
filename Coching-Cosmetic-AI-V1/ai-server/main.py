"""
COCHING AI 처방 서버 v2.3 (최적화)
속도 최적화:
  1. 카테고리별 SKILL 선택 로드 (78K → ~25K)
  2. Prompt Caching (5분 내 재호출 시 90% 비용/속도 절감)
  3. 모델 자동 선택 (Haiku/Sonnet/Opus)
  4. 메모리 학습 (반복 성분 캐시 → Gemini 호출 감소)
  5. 처방 결과 캐시 (동일 성분 조합 재사용)
"""

import os, time, json, hashlib, logging
from pathlib import Path
from typing import Optional, AsyncGenerator
from datetime import datetime, timedelta

import anthropic
from google import genai
from google.genai import types as genai_types
from fastapi import FastAPI, HTTPException
from fastapi.middleware.cors import CORSMiddleware
from fastapi.responses import StreamingResponse
from pydantic import BaseModel
from dotenv import load_dotenv

# Redis (옵션 — 없으면 메모리 딕셔너리로 폴백)
try:
    import redis as redis_lib
    _redis_client = redis_lib.Redis(host="localhost", port=6379, db=0,
                                    socket_connect_timeout=1, socket_timeout=1)
    _redis_client.ping()
    REDIS_AVAILABLE = True
except Exception:
    _redis_client = None
    REDIS_AVAILABLE = False

# ─── 환경변수 ──────────────────────────────────────────────────────────────────
load_dotenv(Path(__file__).parent / ".env")

ANTHROPIC_API_KEY = os.getenv("ANTHROPIC_API_KEY", "")
GEMINI_API_KEY    = os.getenv("GEMINI_API_KEY", "")
LOG_LEVEL         = os.getenv("LOG_LEVEL", "info").upper()
PORT              = int(os.getenv("PORT", 8420))

logging.basicConfig(level=getattr(logging, LOG_LEVEL, logging.INFO),
                    format="%(asctime)s [%(levelname)s] %(message)s")
log = logging.getLogger("coching-ai")

# ─── SKILL 파일 로드 ───────────────────────────────────────────────────────────
BASE_DIR = Path(__file__).parent

def load_file(path: Path) -> str:
    return path.read_text(encoding="utf-8") if path.exists() else ""

# 항상 로드 (필수)
SYSTEM_PROMPT   = load_file(BASE_DIR / "prompts/system-prompt.md")
SKILL_PRECISION = load_file(BASE_DIR / "skills/precision-arithmetic/SKILL.md")
SKILL_FORMULA   = load_file(BASE_DIR / "skills/formulation/SKILL.md")
SKILL_INGRED    = load_file(BASE_DIR / "skills/ingredients/SKILL.md")
SKILL_QA        = load_file(BASE_DIR / "skills/qa-validation/SKILL.md")
SKILL_REGULATION= load_file(BASE_DIR / "skills/regulation/SKILL.md")

# 카테고리별 선택 로드 (요청에 따라 1개만 사용)
SKILL_CAT = {
    "basic":     load_file(BASE_DIR / "skills/category-guide/SKILL.md"),          # 기초화장품 15종
    "hair_sun":  load_file(BASE_DIR / "skills/category-guide/SKILL-hair-sun-color.md"),  # 헤어/선케어/색조
    "household": load_file(BASE_DIR / "skills/category-guide/SKILL-household.md"), # 생활용품
}
SKILL_PHYSICAL  = load_file(BASE_DIR / "skills/physical-properties/SKILL.md")

# 핵심 SKILL (항상 포함, ~30K)
CORE_SKILLS = "\n\n".join([
    "# [최우선] PRECISION-ARITHMETIC\n" + SKILL_PRECISION,
    "# FORMULATION\n" + SKILL_FORMULA,
    "# INGREDIENTS\n" + SKILL_INGRED,
    "# QA-VALIDATION\n" + SKILL_QA,
    "# REGULATION\n" + SKILL_REGULATION,
    "# PHYSICAL-PROPERTIES\n" + SKILL_PHYSICAL,
])

MEMORY_MD   = load_file(BASE_DIR / "memories/MEMORY.md")
PATTERNS_MD = load_file(BASE_DIR / "memories/patterns.md")

log.info(f"CORE SKILL 로드 완료: {len(CORE_SKILLS):,} chars")
log.info(f"카테고리 SKILL — basic:{len(SKILL_CAT['basic']):,} | hair_sun:{len(SKILL_CAT['hair_sun']):,} | household:{len(SKILL_CAT['household']):,}")

# ─── 캐시 시스템 (Redis 우선, 없으면 메모리 폴백) ─────────────────────────────
PRESCRIPTION_CACHE: dict[str, dict] = {}   # 메모리 폴백
RESEARCH_CACHE: dict[str, dict]     = {}   # 메모리 폴백
INGREDIENT_MEMORY: dict[str, dict]  = {}   # 성분별 물성 메모리

CACHE_TTL_PRESCRIPTION = 86400   # 24시간 (초)
CACHE_TTL_RESEARCH     = 604800  # 7일 (초)
CACHE_TTL_HOURS        = 168     # 7일 (시간, is_cache_valid용)

log.info(f"Redis: {'사용 가능 ✓' if REDIS_AVAILABLE else '미설치 — 메모리 캐시 사용'}")

def cache_key(text: str) -> str:
    """정규화된 해시 키 생성"""
    normalized = ",".join(sorted(x.strip().lower() for x in text.split(",")))
    return hashlib.md5(normalized.encode()).hexdigest()[:16]

# ── Redis 캐시 헬퍼 ──
def rcache_get(prefix: str, key: str) -> dict | None:
    """Redis에서 가져오기. 없으면 None"""
    if not REDIS_AVAILABLE:
        return None
    try:
        raw = _redis_client.get(f"coching:{prefix}:{key}")
        return json.loads(raw) if raw else None
    except Exception:
        return None

def rcache_set(prefix: str, key: str, value: dict, ttl: int):
    """Redis에 저장. 실패해도 무시"""
    if not REDIS_AVAILABLE:
        return
    try:
        _redis_client.setex(f"coching:{prefix}:{key}", ttl, json.dumps(value, ensure_ascii=False))
    except Exception:
        pass

def rcache_del_all(prefix: str):
    """Redis에서 prefix 전체 삭제"""
    if not REDIS_AVAILABLE:
        return
    try:
        keys = _redis_client.keys(f"coching:{prefix}:*")
        if keys:
            _redis_client.delete(*keys)
    except Exception:
        pass

def is_cache_valid(entry: dict, hours: int = CACHE_TTL_HOURS) -> bool:
    if "timestamp" not in entry:
        return False
    ts = datetime.fromisoformat(entry["timestamp"])
    return datetime.now() - ts < timedelta(hours=hours)

# ─── 카테고리 자동 판별 ────────────────────────────────────────────────────────
HOUSEHOLD_KEYWORDS = ["세제", "세정제", "청소", "섬유", "주방", "치약", "가글", "소독", "탈취", "디퓨저", "캔들"]
HAIR_SUN_KEYWORDS  = ["샴푸", "린스", "헤어", "선크림", "선스틱", "선밀크", "파운데이션", "립스틱", "틴트", "아이라이너"]

def detect_category(product_name: str, ingredients: str) -> str:
    text = (product_name + " " + ingredients).lower()
    for kw in HOUSEHOLD_KEYWORDS:
        if kw in text:
            return "household"
    for kw in HAIR_SUN_KEYWORDS:
        if kw in text:
            return "hair_sun"
    return "basic"

def get_model_for_task(complexity: str) -> str:
    """복잡도에 따른 모델 자동 선택"""
    return {
        "simple":  "claude-haiku-4-5-20251001",   # 단순 처방 (빠름, 저비용)
        "normal":  "claude-sonnet-4-6",            # 일반 처방
        "complex": "claude-opus-4-6",              # 복합 활성 처방
    }.get(complexity, "claude-sonnet-4-6")

def estimate_complexity(ingredients: str) -> str:
    """
    복잡도 판단 (속도 최적화 기준)
    - simple       → Haiku  (≤12개, 고복잡 성분 없음 — 15-25초)
    - normal       → Sonnet (13-20개 또는 고복잡 성분 — 40-60초)
    - complex      → Opus   (20개 초과 AND 다중 활성 성분 — 60-90초)
    """
    items = [x.strip() for x in ingredients.split(",") if x.strip()]
    lower = ingredients.lower()
    # 진짜 복잡한 활성 성분 (pH 민감, 다중 충돌 — Sonnet/Opus 필요)
    high_complex = ["ascorbic acid", "비타민c", "retinol", "레티놀",
                    "glycolic acid", "salicylic acid", "lactic acid",
                    "aha ", "bha ", " aha", " bha"]

    has_high = any(kw in lower for kw in high_complex)

    if len(items) > 20 and has_high:
        return "complex"        # Opus
    elif len(items) > 12 or has_high:
        return "normal"         # Sonnet
    else:
        return "simple"         # Haiku (≤12개, 고복잡 성분 없음)

# ─── API 클라이언트 ────────────────────────────────────────────────────────────
claude_client = anthropic.Anthropic(api_key=ANTHROPIC_API_KEY)
gemini_client = genai.Client(api_key=GEMINI_API_KEY) if GEMINI_API_KEY else None

# ─── FastAPI ───────────────────────────────────────────────────────────────────
app = FastAPI(title="COCHING AI Formulator", version="2.3.1-optimized")
app.add_middleware(CORSMiddleware, allow_origins=["*"], allow_methods=["*"], allow_headers=["*"])

# ─── 요청/응답 모델 ────────────────────────────────────────────────────────────
class FormulationRequest(BaseModel):
    product_name: str
    ingredients_list: str
    product_type: Optional[str] = None
    target_pH: Optional[float] = None
    target_viscosity: Optional[int] = None
    use_gemini: bool = True
    use_cache: bool = True   # 처방 캐시 사용 여부

class FeedbackRequest(BaseModel):
    formulation_id: str
    score: int          # 1~5
    comment: str
    actual_pH: Optional[float] = None
    actual_viscosity: Optional[int] = None

# ─── Gemini 리서치 ─────────────────────────────────────────────────────────────
REGULATION_WATCHLIST = ["zpt", "zinc pyrithione", "트리클로산", "파라벤",
                        "포름알데히드", "hydroquinone", "하이드로퀴논", "mercury", "수은"]

# 일반 화장품 성분 사전 등록 (Gemini 불필요 호출 방지)
KNOWN_INGREDIENTS: set[str] = {
    # 기제 / 용매
    "water", "aqua", "ethanol", "alcohol", "glycerin", "glycerol",
    "butylene glycol", "propylene glycol", "pentylene glycol", "hexylene glycol",
    "dipropylene glycol", "1,2-hexanediol", "1,3-butylene glycol",
    # 유화제 / 계면활성제
    "cetearyl alcohol", "cetyl alcohol", "stearyl alcohol", "behenyl alcohol",
    "glyceryl stearate", "peg-100 stearate", "peg-40 hydrogenated castor oil",
    "polysorbate 20", "polysorbate 60", "polysorbate 80",
    "sodium lauryl sulfate", "sodium laureth sulfate", "cocamidopropyl betaine",
    "disodium cocoyl glutamate", "sodium cocoyl isethionate",
    "ceteareth-20", "ceteth-20", "steareth-20", "laureth-7", "laureth-9",
    "glyceryl caprylate", "lecithin", "sorbitan stearate",
    # 오일 / 에모리엔트
    "dimethicone", "cyclopentasiloxane", "cyclomethicone", "phenyl trimethicone",
    "mineral oil", "petrolatum", "paraffin", "microcrystalline wax",
    "jojoba oil", "simmondsia chinensis seed oil", "argan oil",
    "argania spinosa kernel oil", "rosehip oil", "rosa canina fruit oil",
    "shea butter", "butyrospermum parkii butter", "cocoa butter",
    "theobroma cacao seed butter", "sunflower seed oil",
    "helianthus annuus seed oil", "sweet almond oil", "prunus amygdalus dulcis oil",
    "squalane", "caprylic/capric triglyceride", "isododecane", "isohexadecane",
    "c12-15 alkyl benzoate", "isopropyl myristate", "isopropyl palmitate",
    "cetyl ethylhexanoate", "neopentyl glycol diheptanoate",
    # 보습제 / 수분 보유
    "hyaluronic acid", "sodium hyaluronate", "hydrolyzed hyaluronic acid",
    "panthenol", "allantoin", "urea", "lactic acid", "sodium lactate",
    "sorbitol", "betaine", "trehalose", "sodium pca",
    "niacinamide", "nicotinamide", "adenosine",
    # 점증제 / 텍스처
    "carbomer", "acrylates/c10-30 alkyl acrylate crosspolymer",
    "xanthan gum", "hydroxypropyl methylcellulose", "cellulose gum",
    "hydroxyethylcellulose", "sodium carboxymethyl cellulose",
    "acrylates copolymer", "polyacrylate-13",
    "glyceryl polyacrylate", "sodium acrylates copolymer",
    "carrageenan", "sclerotium gum", "pullulan",
    # 방부제
    "phenoxyethanol", "ethylhexylglycerin", "benzyl alcohol",
    "sodium benzoate", "potassium sorbate", "chlorphenesin",
    "caprylyl glycol", "methylisothiazolinone", "benzalkonium chloride",
    # 자외선 차단
    "titanium dioxide", "zinc oxide", "octinoxate", "ethylhexyl methoxycinnamate",
    "avobenzone", "butyl methoxydibenzoylmethane", "octocrylene",
    "homosalate", "octisalate", "ethylhexyl salicylate",
    "bisoctrizole", "methylene bis-benzotriazolyl tetramethylbutylphenol",
    "diethylamino hydroxybenzoyl hexyl benzoate",
    # pH 조절제
    "citric acid", "sodium citrate", "sodium hydroxide", "potassium hydroxide",
    "triethanolamine", "lactic acid", "sodium lactate",
    "arginine", "lysine", "trisodium edta", "disodium edta",
    # 활성 성분
    "retinol", "retinyl palmitate", "retinal", "tretinoin",
    "ascorbic acid", "vitamin c", "sodium ascorbyl phosphate",
    "ascorbyl glucoside", "3-o-ethyl ascorbic acid",
    "tocopherol", "vitamin e", "tocopheryl acetate",
    "alpha arbutin", "beta-arbutin", "arbutin",
    "resveratrol", "azelaic acid", "kojic acid",
    "glycolic acid", "salicylic acid", "mandelic acid",
    "ceramide np", "ceramide ap", "ceramide eop",
    "ceramide ng", "ceramide ns", "ceramide eg",
    "peptide", "palmitoyl tripeptide-1", "palmitoyl tetrapeptide-7",
    "acetyl hexapeptide-3", "copper tripeptide-1",
    "epidermal growth factor", "egf",
    # 식물 추출물 (자주 쓰이는)
    "centella asiatica extract", "centella asiatica leaf extract",
    "green tea extract", "camellia sinensis leaf extract",
    "aloe vera", "aloe barbadensis leaf extract",
    "chamomile extract", "bisabolol", "alpha-bisabolol",
    "licorice root extract", "glycyrrhiza glabra root extract",
    "madecassoside", "asiaticoside", "asiatic acid", "madecassic acid",
    "portulaca oleracea extract", "houttuynia cordata extract",
    "mugwort extract", "artemisia vulgaris extract",
    "raspberry extract", "rubus idaeus fruit extract",
    "witch hazel", "hamamelis virginiana extract",
    # 색소 / 진주광택
    "mica", "titanium dioxide", "iron oxides", "ultramarines",
    # 기타
    "dimethyl sulfone", "methylsulfonylmethane", "msm",
    "sodium chloride", "magnesium sulfate", "zinc sulfate",
    "propanediol", "caprylyl methicone",
}

def should_call_gemini(ingredients: str) -> tuple[bool, str]:
    """Gemini 호출 필요 여부 판단 (최적화: 알려진 성분 제외)"""
    lower = ingredients.lower()
    # 규제 성분 감지 (최우선)
    for kw in REGULATION_WATCHLIST:
        if kw in lower:
            return True, f"'{kw}' 성분의 한국/EU/미국 최신 규제 상태와 허용 농도를 JSON으로 반환해줘"
    # 진짜 모르는 성분 (KNOWN + INGREDIENT_MEMORY 둘 다 없는 것만)
    items = [x.strip() for x in ingredients.split(",")]
    unknown = [
        x for x in items
        if (any(c.isalpha() and c.isascii() for c in x)
            and x.lower() not in KNOWN_INGREDIENTS
            and x.lower() not in INGREDIENT_MEMORY
            and len(x) > 5)
    ]
    if len(unknown) >= 5:   # 임계값 3→5 (일반 처방 Gemini 스킵)
        query_items = unknown[:4]
        return True, (f"다음 성분들의 pKa, 권장 pH 범위, 최대 사용 농도, HLB 값을 "
                      f"JSON 형식으로 반환해줘: {', '.join(query_items)}")
    return False, ""

def gemini_research(query: str) -> dict:
    """Gemini Google Search 리서치 (캐시 적용)"""
    key = cache_key(query)
    if key in RESEARCH_CACHE and is_cache_valid(RESEARCH_CACHE[key]):
        log.info("Gemini 캐시 히트 → 스킵")
        return {"status": "cached", "data": RESEARCH_CACHE[key]["data"]}
    if not gemini_client:
        return {"status": "skipped", "reason": "no_api_key"}
    try:
        response = gemini_client.models.generate_content(
            model="gemini-2.5-pro",
            contents=query,
            config=genai_types.GenerateContentConfig(
                tools=[genai_types.Tool(google_search=genai_types.GoogleSearch())],
                temperature=0.1,
                max_output_tokens=2048,  # 절반으로 줄임 (속도 향상)
            ),
        )
        result = {"status": "ok", "data": response.text}
        RESEARCH_CACHE[key] = {"data": response.text, "timestamp": datetime.now().isoformat()}
        return result
    except Exception as e:
        log.warning(f"Gemini 실패 → Fallback: {e}")
        return {"status": "fallback", "reason": str(e)}

# ─── Claude 처방 생성 ──────────────────────────────────────────────────────────
def run_formulation(req: FormulationRequest, gemini_data: str, model: str) -> str:
    """
    최적화 포인트:
    - 카테고리별 SKILL만 포함 (78K → ~30K)
    - cache_control로 Prompt Caching 활성화
    - max_tokens 최적화 (요청 복잡도 기반)
    """
    category = detect_category(req.product_name, req.ingredients_list)
    category_skill = SKILL_CAT.get(category, SKILL_CAT["basic"])

    # 메모리에서 관련 패턴 추출 (전체 MEMORY.md 대신 핵심만)
    memory_snippet = MEMORY_MD[:2000] if MEMORY_MD else "(메모리 없음 — 초기 상태)"

    system_content = (
        f"{SYSTEM_PROMPT}\n\n"
        f"--- CORE SKILLS ---\n{CORE_SKILLS}\n\n"
        f"--- CATEGORY GUIDE ({category.upper()}) ---\n{category_skill}\n\n"
        f"--- MEMORY ---\n{memory_snippet}"
    )

    complexity = estimate_complexity(req.ingredients_list)
    max_tokens = {"simple": 3500, "normal": 5000, "complex": 7168}.get(complexity, 5000)

    user_message = f"""카피 가이드처방 생성 요청:

[제품명] {req.product_name}
[전성분] {req.ingredients_list}
[제형] {req.product_type or "자동 판별"}
[목표 pH] {req.target_pH or "자동 계산"}
[목표 점도] {req.target_viscosity or "자동 계산"} cP
[카테고리] {category}

[Gemini 리서치]
{gemini_data if gemini_data else "해당 없음"}

PRECISION-ARITHMETIC 최우선 적용. 배합비 합계 100.00% 보장.
8개 필수 섹션 포함 처방전을 출력해줘. 불필요한 설명 최소화, 핵심만."""

    response = claude_client.messages.create(
        model=model,
        max_tokens=max_tokens,
        temperature=0.1,
        system=[{
            "type": "text",
            "text": system_content,
            "cache_control": {"type": "ephemeral"}  # ★ Prompt Caching
        }],
        messages=[{"role": "user", "content": user_message}]
    )

    # 캐시 효율 로깅
    usage = response.usage
    if hasattr(usage, "cache_read_input_tokens") and usage.cache_read_input_tokens:
        log.info(f"Prompt Cache 히트: {usage.cache_read_input_tokens:,} tokens 절감")

    return response.content[0].text

# ─── 메모리 학습 저장 ──────────────────────────────────────────────────────────
def learn_from_result(req: FormulationRequest, result: str, elapsed: float):
    """처방 결과에서 패턴 학습 → 메모리 파일 업데이트"""
    global MEMORY_MD, PATTERNS_MD

    entry = {
        "timestamp": datetime.now().isoformat(),
        "product": req.product_name,
        "type": req.product_type,
        "ingredient_count": len(req.ingredients_list.split(",")),
        "elapsed_sec": elapsed,
    }

    # patterns.md 파일에 처방 이력 추가
    patterns_path = BASE_DIR / "memories/patterns.md"
    existing = patterns_path.read_text(encoding="utf-8") if patterns_path.exists() else ""

    new_entry = (f"\n## {entry['timestamp'][:10]} — {req.product_name}\n"
                 f"- 성분 수: {entry['ingredient_count']}개\n"
                 f"- 처리 시간: {elapsed:.1f}초\n"
                 f"- 제형: {req.product_type or '자동'}\n")

    # 최근 50건만 유지 (파일 크기 제한)
    lines = existing.split("\n")
    if len(lines) > 300:
        lines = lines[-300:]
        existing = "\n".join(lines)

    patterns_path.write_text(existing + new_entry, encoding="utf-8")
    PATTERNS_MD = patterns_path.read_text(encoding="utf-8")

    log.info(f"메모리 학습 저장 완료: {req.product_name}")

# ─── 엔드포인트 ───────────────────────────────────────────────────────────────
@app.get("/health")
def health():
    redis_status = "connected" if REDIS_AVAILABLE else "not_installed"
    return {
        "status": "ok",
        "version": "2.3.2",
        "claude": "connected" if ANTHROPIC_API_KEY else "no_key",
        "gemini": "connected" if GEMINI_API_KEY else "no_key",
        "redis": redis_status,
        "skills_loaded": len(CORE_SKILLS) > 1000,
        "cache_stats": {
            "prescription_cache": len(PRESCRIPTION_CACHE),
            "research_cache": len(RESEARCH_CACHE),
            "ingredient_memory": len(INGREDIENT_MEMORY),
        }
    }

@app.post("/api/v1/formulate")
def create_formulation(req: FormulationRequest):
    start = time.time()
    log.info(f"처방 요청: {req.product_name}")

    # ① 처방 캐시 확인 (Redis → 메모리 순)
    if req.use_cache:
        ck = cache_key(req.ingredients_list)
        cached = rcache_get("prsc", ck)  # Redis 먼저
        if cached is None and ck in PRESCRIPTION_CACHE and is_cache_valid(PRESCRIPTION_CACHE[ck], hours=24):
            cached = PRESCRIPTION_CACHE[ck]  # 메모리 폴백
        if cached:
            log.info(f"처방 캐시 히트({'Redis' if REDIS_AVAILABLE else 'Memory'}) → {time.time()-start:.2f}초")
            return {**cached, "processing_time": round(time.time()-start, 2), "from_cache": True}

    # ② 복잡도 판단 → 모델 선택
    complexity = estimate_complexity(req.ingredients_list)
    model = get_model_for_task(complexity)
    log.info(f"복잡도: {complexity} → 모델: {model}")

    # ③ Gemini 리서치 (필요 시)
    gemini_data = ""
    gemini_used = False
    if req.use_gemini and gemini_client:
        need, query = should_call_gemini(req.ingredients_list)
        if need:
            log.info(f"Gemini 리서치: {query[:50]}...")
            result = gemini_research(query)
            if result["status"] in ("ok", "cached"):
                gemini_data = result["data"]
                gemini_used = result["status"] == "ok"

    # ④ Claude 처방 생성
    try:
        prescription = run_formulation(req, gemini_data, model)
    except anthropic.APIError as e:
        raise HTTPException(status_code=502, detail=f"Claude API 오류: {e}")

    elapsed = round(time.time() - start, 2)
    log.info(f"처방 완료: {elapsed}초 | 모델:{model} | Gemini:{gemini_used}")

    response_data = {
        "resultCode": "00",
        "resultMessage": "SUCCESS",
        "processing_time": elapsed,
        "model": model,
        "complexity": complexity,
        "gemini_used": gemini_used,
        "from_cache": False,
        "resultData": prescription,
    }

    # ⑤ 캐시 저장 (Redis + 메모리) + 메모리 학습
    if req.use_cache:
        ck = cache_key(req.ingredients_list)
        save_data = {**response_data, "timestamp": datetime.now().isoformat()}
        rcache_set("prsc", ck, save_data, CACHE_TTL_PRESCRIPTION)  # Redis (24h)
        PRESCRIPTION_CACHE[ck] = save_data                          # 메모리 폴백

    learn_from_result(req, prescription, elapsed)
    return response_data


@app.post("/api/v1/formulate/stream")
def create_formulation_stream(req: FormulationRequest):
    """
    스트리밍 처방 엔드포인트 (SSE)
    - 첫 토큰이 2-3초 내 도착 → 체감 속도 대폭 개선
    - 캐시 히트 시 즉시 전체 반환
    """
    start = time.time()

    # 캐시 확인 (Redis → 메모리)
    if req.use_cache:
        ck = cache_key(req.ingredients_list)
        cached = rcache_get("prsc", ck)
        if cached is None and ck in PRESCRIPTION_CACHE and is_cache_valid(PRESCRIPTION_CACHE[ck], hours=24):
            cached = PRESCRIPTION_CACHE[ck]
        if cached:
            def cache_stream():
                meta = json.dumps({
                    "type": "meta",
                    "model": cached.get("model", ""),
                    "complexity": cached.get("complexity", ""),
                    "from_cache": True,
                    "processing_time": 0.0,
                }, ensure_ascii=False)
                yield f"data: {meta}\n\n"
                yield f"data: {json.dumps({'type':'text','text': cached.get('resultData','')}, ensure_ascii=False)}\n\n"
                yield "data: [DONE]\n\n"
            return StreamingResponse(cache_stream(), media_type="text/event-stream")

    complexity = estimate_complexity(req.ingredients_list)
    model = get_model_for_task(complexity)

    # Gemini (병목 주의 — 스트리밍에서는 비동기 처리 안 됨)
    gemini_data = ""
    gemini_used = False
    if req.use_gemini and gemini_client:
        need, query = should_call_gemini(req.ingredients_list)
        if need:
            result = gemini_research(query)
            if result["status"] in ("ok", "cached"):
                gemini_data = result["data"]
                gemini_used = result["status"] == "ok"

    # 시스템 프롬프트 구성 (run_formulation과 동일)
    category = detect_category(req.product_name, req.ingredients_list)
    category_skill = SKILL_CAT.get(category, SKILL_CAT["basic"])
    memory_snippet = MEMORY_MD[:2000] if MEMORY_MD else "(메모리 없음)"
    system_content = (
        f"{SYSTEM_PROMPT}\n\n"
        f"--- CORE SKILLS ---\n{CORE_SKILLS}\n\n"
        f"--- CATEGORY GUIDE ({category.upper()}) ---\n{category_skill}\n\n"
        f"--- MEMORY ---\n{memory_snippet}"
    )
    max_tokens = {"simple": 3500, "normal": 5000, "complex": 7168}.get(complexity, 5000)
    user_message = (
        f"카피 가이드처방 생성 요청:\n\n"
        f"[제품명] {req.product_name}\n[전성분] {req.ingredients_list}\n"
        f"[제형] {req.product_type or '자동 판별'}\n"
        f"[목표 pH] {req.target_pH or '자동 계산'}\n"
        f"[카테고리] {category}\n\n"
        f"[Gemini 리서치]\n{gemini_data if gemini_data else '해당 없음'}\n\n"
        "PRECISION-ARITHMETIC 최우선 적용. 배합비 합계 100.00% 보장.\n"
        "8개 필수 섹션 포함 처방전을 출력해줘. 불필요한 설명 최소화, 핵심만."
    )

    def stream_generator():
        # 메타 정보 먼저 전송
        meta = json.dumps({
            "type": "meta",
            "model": model,
            "complexity": complexity,
            "gemini_used": gemini_used,
            "from_cache": False,
        }, ensure_ascii=False)
        yield f"data: {meta}\n\n"

        # Claude 스트리밍
        full_text = []
        try:
            with claude_client.messages.stream(
                model=model,
                max_tokens=max_tokens,
                temperature=0.1,
                system=[{"type": "text", "text": system_content, "cache_control": {"type": "ephemeral"}}],
                messages=[{"role": "user", "content": user_message}],
            ) as stream:
                for text_chunk in stream.text_stream:
                    full_text.append(text_chunk)
                    chunk_data = json.dumps({"type": "text", "text": text_chunk}, ensure_ascii=False)
                    yield f"data: {chunk_data}\n\n"

        except anthropic.APIError as e:
            err = json.dumps({"type": "error", "message": str(e)}, ensure_ascii=False)
            yield f"data: {err}\n\n"
            return

        elapsed = round(time.time() - start, 2)
        prescription = "".join(full_text)
        log.info(f"스트리밍 처방 완료: {elapsed}초 | 모델:{model}")

        done_data = json.dumps({"type": "done", "processing_time": elapsed}, ensure_ascii=False)
        yield f"data: {done_data}\n\n"
        yield "data: [DONE]\n\n"

        # 캐시 저장 (Redis + 메모리) + 메모리 학습
        if req.use_cache:
            ck = cache_key(req.ingredients_list)
            save_data = {
                "resultCode": "00", "resultMessage": "SUCCESS",
                "processing_time": elapsed, "model": model,
                "complexity": complexity, "gemini_used": gemini_used,
                "from_cache": False, "resultData": prescription,
                "timestamp": datetime.now().isoformat(),
            }
            rcache_set("prsc", ck, save_data, CACHE_TTL_PRESCRIPTION)
            PRESCRIPTION_CACHE[ck] = save_data
        learn_from_result(req, prescription, elapsed)

    return StreamingResponse(stream_generator(), media_type="text/event-stream",
                             headers={"X-Accel-Buffering": "no", "Cache-Control": "no-cache"})


@app.post("/api/v1/feedback")
def receive_feedback(fb: FeedbackRequest):
    """전문가 피드백 → 메모리에 학습"""
    feedback_path = BASE_DIR / "memories/feedback.md"
    existing = feedback_path.read_text(encoding="utf-8") if feedback_path.exists() else "# 전문가 피드백 기록\n\n"
    entry = (f"\n## {datetime.now().strftime('%Y-%m-%d')} — ID:{fb.formulation_id}\n"
             f"- 점수: {fb.score}/5\n"
             f"- 코멘트: {fb.comment}\n"
             f"- 실측 pH: {fb.actual_pH or '미측정'}\n"
             f"- 실측 점도: {fb.actual_viscosity or '미측정'} cP\n")
    feedback_path.write_text(existing + entry, encoding="utf-8")
    log.info(f"피드백 저장: ID={fb.formulation_id} 점수={fb.score}")
    return {"status": "ok", "message": "피드백이 메모리에 저장되었습니다."}


@app.get("/api/v1/memory/status")
def memory_status():
    patterns_path = BASE_DIR / "memories/patterns.md"
    patterns_lines = len(patterns_path.read_text(encoding="utf-8").split("\n")) if patterns_path.exists() else 0
    return {
        "core_skills_chars": len(CORE_SKILLS),
        "memory_chars": len(MEMORY_MD),
        "patterns_lines": patterns_lines,
        "prescription_cache_count": len(PRESCRIPTION_CACHE),
        "research_cache_count": len(RESEARCH_CACHE),
    }


@app.delete("/api/v1/cache")
def clear_cache():
    PRESCRIPTION_CACHE.clear()
    RESEARCH_CACHE.clear()
    rcache_del_all("prsc")
    rcache_del_all("res")
    log.info("캐시 초기화 완료 (Redis + 메모리)")
    return {"status": "ok", "redis": REDIS_AVAILABLE}


@app.post("/api/v1/research")
def manual_research(query: str):
    return gemini_research(query)


if __name__ == "__main__":
    import uvicorn
    uvicorn.run("main:app", host="0.0.0.0", port=PORT, reload=False)
