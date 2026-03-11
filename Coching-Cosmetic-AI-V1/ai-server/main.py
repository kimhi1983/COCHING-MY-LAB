"""
COCHING AI 처방 서버 v2.4 (DB 연동 + 가이드처방)
v2.3 최적화 유지:
  1. 카테고리별 SKILL 선택 로드 (78K → ~25K)
  2. Prompt Caching (5분 내 재호출 시 90% 비용/속도 절감)
  3. 모델 자동 선택 (Haiku/Sonnet/Opus)
  4. 메모리 학습 (반복 성분 캐시 → Gemini 호출 감소)
  5. 처방 결과 캐시 (동일 성분 조합 재사용)
v2.4 신규:
  6. PostgreSQL DB 연동 (ingredient_master, regulation_cache 등)
  7. 가이드처방 생성 (제품유형 입력 → DB 원료 자동선택 → 처방)
  8. 원료 검색 API
"""

import os, time, json, hashlib, logging, asyncio
from pathlib import Path
from typing import Optional, AsyncGenerator
from datetime import datetime, timedelta
from contextlib import asynccontextmanager

import anthropic
from google import genai
from google.genai import types as genai_types
from fastapi import FastAPI, HTTPException, Query
from fastapi.middleware.cors import CORSMiddleware
from fastapi.responses import StreamingResponse
from pydantic import BaseModel
from dotenv import load_dotenv

from db import (
    init_db_pool, close_db_pool, DB_AVAILABLE,
    search_ingredients as db_search_ingredients,
    get_ingredients_by_types, get_regulation_data,
    get_reference_products, get_ingredient_details,
    get_db_known_ingredients, get_db_stats,
    format_db_enrichment, format_reference_products,
)

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

# ─── 가이드처방 학습 인덱스 ──────────────────────────────────────────────────────
LEARNING_DIR = BASE_DIR / "data" / "learning"
LEARNING_DIR.mkdir(parents=True, exist_ok=True)

GUIDE_INDEX: list[dict] = []  # [{product_type, skin_type, ingredients, filepath}, ...]

def load_guide_index():
    """ai-server/data/learning/ 에서 마이랩 학습용 가이드처방 로드"""
    global GUIDE_INDEX
    dirs = [
        LEARNING_DIR,
    ]
    seen_files = set()
    entries = []
    for d in dirs:
        if not d.exists():
            continue
        for f in sorted(d.glob("guide_*.json"), key=lambda p: p.stat().st_mtime, reverse=True):
            if f.name in seen_files:
                continue
            seen_files.add(f.name)
            try:
                data = json.loads(f.read_text(encoding="utf-8"))
                # 처방 텍스트에서 성분명 추출
                formulation_text = data.get("formulation", "")
                ingredients = set()
                for line in formulation_text.split("\n"):
                    if "inci_name" in line.lower():
                        # JSON 값에서 INCI명 추출
                        import re
                        matches = re.findall(r'"inci_name":\s*"([^"]+)"', line)
                        ingredients.update(m.lower().strip() for m in matches)
                entries.append({
                    "product_type": data.get("product_type", ""),
                    "skin_type": data.get("skin_type", ""),
                    "ingredients": ingredients,
                    "metadata": data.get("metadata", {}),
                    "formulation": formulation_text[:8000],  # 프롬프트용 (크기 제한)
                    "filepath": str(f),
                })
            except Exception:
                continue
    GUIDE_INDEX = entries
    log.info(f"가이드처방 인덱스 로드: {len(GUIDE_INDEX)}건")

def find_similar_guides(product_type: str = None, skin_type: str = None,
                        ingredients: list[str] = None, top_k: int = 2) -> list[dict]:
    """입력 조건에 가장 유사한 가이드처방 검색"""
    if not GUIDE_INDEX:
        return []
    scored = []
    input_ings = {x.strip().lower() for x in (ingredients or [])}
    for entry in GUIDE_INDEX:
        score = 0
        # 제품유형 일치
        if product_type and entry["product_type"] == product_type:
            score += 10
        elif product_type:
            # 유사 카테고리 보너스
            pt_map = {"수분크림": "로션", "에센스": "세럼", "아이크림": "수분크림"}
            if pt_map.get(product_type) == entry["product_type"]:
                score += 5
        # 피부타입 일치
        if skin_type and entry["skin_type"] == skin_type:
            score += 3
        elif skin_type and entry["skin_type"] == "all":
            score += 1
        # 성분 겹침 (Jaccard-like)
        if input_ings and entry["ingredients"]:
            overlap = len(input_ings & entry["ingredients"])
            if overlap:
                score += overlap * 2
        scored.append((score, entry))
    scored.sort(key=lambda x: -x[0])
    return [e for s, e in scored[:top_k] if s > 0]

def format_guide_reference(guides: list[dict]) -> str:
    """유사 가이드처방을 프롬프트 텍스트로 변환"""
    if not guides:
        return ""
    lines = ["\n[참조 가이드처방 — 학습 데이터]"]
    for i, g in enumerate(guides, 1):
        lines.append(f"\n### 참조 #{i}: {g['product_type']} / {g['skin_type']}")
        lines.append(f"모델: {g['metadata'].get('model', '?')} | "
                     f"DB원료: {g['metadata'].get('db_ingredients', '?')}종")
        # 처방 텍스트 중 배합표 부분만 추출 (토큰 절약)
        text = g["formulation"]
        # formulation_table 또는 배합표 섹션 추출
        start_idx = text.find('"2_formulation_table"')
        if start_idx == -1:
            start_idx = text.find('"formulation"')
        if start_idx >= 0:
            end_idx = text.find('"3_physical_properties', start_idx)
            if end_idx == -1:
                end_idx = start_idx + 3000
            lines.append(text[start_idx:end_idx][:3000])
        else:
            lines.append(text[:2000])
    return "\n".join(lines)

# 서버 시작 시 인덱스 로드
load_guide_index()

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

# ─── FastAPI (lifespan으로 DB 연결 관리) ────────────────────────────────────────
@asynccontextmanager
async def lifespan(application: FastAPI):
    await init_db_pool()
    if DB_AVAILABLE:
        db_ingredients = await get_db_known_ingredients()
        KNOWN_INGREDIENTS.update(db_ingredients)
        log.info(f"DB 원료 {len(db_ingredients)}개 → KNOWN_INGREDIENTS 통합 (총 {len(KNOWN_INGREDIENTS)}개)")
    yield
    await close_db_pool()

app = FastAPI(title="COCHING AI Formulator", version="2.4.0-db",
              lifespan=lifespan)
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

class GuideFormulationRequest(BaseModel):
    product_type: str           # "수분크림", "토너", "선크림", "에센스", "클렌저" 등
    skin_type: str = "all"      # "건성", "지성", "복합성", "민감성", "all"
    target_pH: Optional[float] = None
    target_viscosity: Optional[int] = None
    special_requirements: Optional[str] = None  # "비건", "EWG그린등급", "무향" 등
    use_gemini: bool = True
    use_cache: bool = True

class FeedbackRequest(BaseModel):
    formulation_id: str
    score: int          # 1~5
    comment: str
    actual_pH: Optional[float] = None
    actual_viscosity: Optional[int] = None

# ─── 제품유형 → 원료유형 매핑 ──────────────────────────────────────────────────
PRODUCT_TYPE_MAP = {
    "수분크림": {
        "required": ["HUMECTANT", "EMULSIFIER", "THICKENER", "EMOLLIENT"],
        "optional": ["ACTIVE", "ANTIOXIDANT", "PRESERVATIVE", "PH_ADJUSTER"],
        "default_pH": 5.5, "default_viscosity": 25000, "category": "basic",
    },
    "토너": {
        "required": ["HUMECTANT", "PH_ADJUSTER"],
        "optional": ["ACTIVE", "ANTIOXIDANT", "PRESERVATIVE"],
        "default_pH": 5.5, "default_viscosity": 50, "category": "basic",
    },
    "에센스": {
        "required": ["HUMECTANT", "ACTIVE"],
        "optional": ["THICKENER", "ANTIOXIDANT", "PRESERVATIVE", "PH_ADJUSTER"],
        "default_pH": 5.5, "default_viscosity": 500, "category": "basic",
    },
    "세럼": {
        "required": ["HUMECTANT", "ACTIVE", "ANTIOXIDANT"],
        "optional": ["THICKENER", "PRESERVATIVE", "PH_ADJUSTER", "EMOLLIENT"],
        "default_pH": 5.5, "default_viscosity": 800, "category": "basic",
    },
    "로션": {
        "required": ["HUMECTANT", "EMULSIFIER", "EMOLLIENT"],
        "optional": ["ACTIVE", "THICKENER", "PRESERVATIVE", "PH_ADJUSTER"],
        "default_pH": 5.5, "default_viscosity": 5000, "category": "basic",
    },
    "클렌저": {
        "required": ["SURFACTANT", "HUMECTANT"],
        "optional": ["THICKENER", "PRESERVATIVE", "PH_ADJUSTER"],
        "default_pH": 5.5, "default_viscosity": 3000, "category": "basic",
    },
    "선크림": {
        "required": ["UV_FILTER", "EMULSIFIER", "EMOLLIENT"],
        "optional": ["HUMECTANT", "THICKENER", "ANTIOXIDANT", "PRESERVATIVE"],
        "default_pH": 6.5, "default_viscosity": 20000, "category": "hair_sun",
    },
    "샴푸": {
        "required": ["SURFACTANT", "HUMECTANT", "THICKENER"],
        "optional": ["PRESERVATIVE", "FRAGRANCE", "PH_ADJUSTER", "ACTIVE"],
        "default_pH": 5.5, "default_viscosity": 3000, "category": "hair_sun",
    },
    "마스크팩": {
        "required": ["HUMECTANT", "THICKENER", "ACTIVE"],
        "optional": ["ANTIOXIDANT", "PRESERVATIVE", "PH_ADJUSTER"],
        "default_pH": 5.5, "default_viscosity": 15000, "category": "basic",
    },
    "아이크림": {
        "required": ["HUMECTANT", "EMULSIFIER", "EMOLLIENT", "ACTIVE"],
        "optional": ["THICKENER", "ANTIOXIDANT", "PRESERVATIVE"],
        "default_pH": 5.5, "default_viscosity": 30000, "category": "basic",
    },
    "립밤": {
        "required": ["EMOLLIENT", "FILM_FORMER"],
        "optional": ["HUMECTANT", "ACTIVE", "FRAGRANCE", "COLORANT"],
        "default_pH": 6.0, "default_viscosity": 50000, "category": "basic",
    },
}

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
def run_formulation(req: FormulationRequest, gemini_data: str, model: str,
                    db_enrichment: str = "") -> str:
    """
    최적화 포인트:
    - 카테고리별 SKILL만 포함 (78K → ~30K)
    - cache_control로 Prompt Caching 활성화
    - max_tokens 최적화 (요청 복잡도 기반)
    - DB 원료 데이터 주입 (v2.4)
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
{db_enrichment}
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


def run_formulation_gemini(req: FormulationRequest, gemini_data: str,
                           db_enrichment: str = "", guide_ref: str = "") -> str:
    """Gemini 기반 처방 생성 (가이드처방 학습 데이터 참조)"""
    category = detect_category(req.product_name, req.ingredients_list)
    category_skill = SKILL_CAT.get(category, SKILL_CAT["basic"])
    memory_snippet = MEMORY_MD[:2000] if MEMORY_MD else "(메모리 없음)"

    system_content = (
        f"{SYSTEM_PROMPT}\n\n"
        f"--- CORE SKILLS ---\n{CORE_SKILLS}\n\n"
        f"--- CATEGORY GUIDE ({category.upper()}) ---\n{category_skill}\n\n"
        f"--- MEMORY ---\n{memory_snippet}"
    )

    user_message = f"""카피 가이드처방 생성 요청:

[제품명] {req.product_name}
[전성분] {req.ingredients_list}
[제형] {req.product_type or "자동 판별"}
[목표 pH] {req.target_pH or "자동 계산"}
[목표 점도] {req.target_viscosity or "자동 계산"} cP
[카테고리] {category}
{db_enrichment}
{guide_ref}
[Gemini 리서치]
{gemini_data if gemini_data else "해당 없음"}

PRECISION-ARITHMETIC 최우선 적용. 배합비 합계 100.00% 보장.
8개 필수 섹션 포함 처방전을 출력해줘. 불필요한 설명 최소화, 핵심만."""

    full_prompt = f"{system_content}\n\n---\n\n{user_message}"
    response = gemini_client.models.generate_content(
        model="gemini-2.5-pro",
        contents=full_prompt,
        config=genai_types.GenerateContentConfig(
            temperature=0.1,
            max_output_tokens=6000,
        ),
    )
    return response.text


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


# ─── 가이드처방 저장 (백업 + 학습 데이터) ─────────────────────────────────────
BACKUP_DIRS = [
    Path("/home/kpros/backup/formulations"),
    Path("/mnt/e/COCHING/backup/formulations"),
]

def save_formulation_backup(product_type: str, skin_type: str, result_text: str,
                            metadata: dict):
    """가이드처방 결과를 백업(2중) + 학습 데이터(마이랩용)로 저장"""
    ts = datetime.now().strftime("%Y%m%d_%H%M%S")
    filename = f"guide_{product_type}_{skin_type}_{ts}.json"

    backup_data = {
        "timestamp": datetime.now().isoformat(),
        "product_type": product_type,
        "skin_type": skin_type,
        "metadata": metadata,
        "formulation": result_text,
    }

    json_text = json.dumps(backup_data, ensure_ascii=False, indent=2)

    # 1) 마이랩 학습 데이터 저장 (AI 서버 로컬)
    try:
        learning_path = LEARNING_DIR / filename
        learning_path.write_text(json_text, encoding="utf-8")
        log.info(f"학습 데이터 저장: {learning_path}")
    except Exception as e:
        log.warning(f"학습 데이터 저장 실패: {e}")

    # 2) 백업 2중 저장 (안전 보관)
    for backup_dir in BACKUP_DIRS:
        try:
            backup_dir.mkdir(parents=True, exist_ok=True)
            filepath = backup_dir / filename
            filepath.write_text(json_text, encoding="utf-8")
            log.info(f"처방 백업 저장: {filepath}")
        except Exception as e:
            log.warning(f"처방 백업 실패 ({backup_dir}): {e}")


# ─── 엔드포인트 ───────────────────────────────────────────────────────────────
@app.get("/health")
async def health():
    from db import DB_AVAILABLE as _db_avail
    redis_status = "connected" if REDIS_AVAILABLE else "not_installed"
    db_stats = await get_db_stats() if _db_avail else {}
    return {
        "status": "ok",
        "version": "2.4.0",
        "claude": "connected" if ANTHROPIC_API_KEY else "no_key",
        "gemini": "connected" if GEMINI_API_KEY else "no_key",
        "redis": redis_status,
        "database": "connected" if _db_avail else "not_connected",
        "db_stats": db_stats,
        "skills_loaded": len(CORE_SKILLS) > 1000,
        "guide_index_count": len(GUIDE_INDEX),
        "cache_stats": {
            "prescription_cache": len(PRESCRIPTION_CACHE),
            "research_cache": len(RESEARCH_CACHE),
            "ingredient_memory": len(INGREDIENT_MEMORY),
        }
    }

@app.post("/api/v1/formulate")
async def create_formulation(req: FormulationRequest):
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
    model = "gemini-2.5-pro"
    log.info(f"복잡도: {complexity} → 모델: {model}")

    # ②-b DB 원료 데이터 조회 (v2.4)
    db_enrichment = ""
    from db import DB_AVAILABLE as _db_avail
    if _db_avail:
        names = [x.strip() for x in req.ingredients_list.split(",") if x.strip()]
        details = await get_ingredient_details(names)
        db_enrichment = format_db_enrichment(details)
        if details:
            log.info(f"DB 원료 매칭: {len(details)}건")

    # ②-c 유사 가이드처방 참조 (학습 데이터)
    guide_ref = ""
    ingredient_names = [x.strip() for x in req.ingredients_list.split(",") if x.strip()]
    similar_guides = find_similar_guides(
        product_type=req.product_type,
        ingredients=ingredient_names,
        top_k=2,
    )
    if similar_guides:
        guide_ref = format_guide_reference(similar_guides)
        log.info(f"가이드처방 참조: {len(similar_guides)}건 매칭")

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

    # ④ Gemini 처방 생성 (DB 데이터 + 가이드처방 참조 주입)
    try:
        prescription = await asyncio.to_thread(
            run_formulation_gemini, req, gemini_data, db_enrichment, guide_ref)
    except Exception as e:
        raise HTTPException(status_code=502, detail=f"Gemini API 오류: {e}")

    elapsed = round(time.time() - start, 2)
    log.info(f"처방 완료: {elapsed}초 | 모델:{model} | Gemini:{gemini_used} | DB:{bool(db_enrichment)}")

    response_data = {
        "resultCode": "00",
        "resultMessage": "SUCCESS",
        "processing_time": elapsed,
        "model": model,
        "complexity": complexity,
        "gemini_used": gemini_used,
        "db_enriched": bool(db_enrichment),
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
    model = "gemini-2.5-pro"

    # Gemini 리서치 (필요 시)
    gemini_data = ""
    gemini_used = False
    if req.use_gemini and gemini_client:
        need, query = should_call_gemini(req.ingredients_list)
        if need:
            result = gemini_research(query)
            if result["status"] in ("ok", "cached"):
                gemini_data = result["data"]
                gemini_used = result["status"] == "ok"

    # 유사 가이드처방 참조
    ingredient_names = [x.strip() for x in req.ingredients_list.split(",") if x.strip()]
    similar_guides = find_similar_guides(
        product_type=req.product_type, ingredients=ingredient_names, top_k=2)
    guide_ref = format_guide_reference(similar_guides) if similar_guides else ""

    # 시스템 프롬프트 구성
    category = detect_category(req.product_name, req.ingredients_list)
    category_skill = SKILL_CAT.get(category, SKILL_CAT["basic"])
    memory_snippet = MEMORY_MD[:2000] if MEMORY_MD else "(메모리 없음)"
    system_content = (
        f"{SYSTEM_PROMPT}\n\n"
        f"--- CORE SKILLS ---\n{CORE_SKILLS}\n\n"
        f"--- CATEGORY GUIDE ({category.upper()}) ---\n{category_skill}\n\n"
        f"--- MEMORY ---\n{memory_snippet}"
    )
    user_message = (
        f"카피 가이드처방 생성 요청:\n\n"
        f"[제품명] {req.product_name}\n[전성분] {req.ingredients_list}\n"
        f"[제형] {req.product_type or '자동 판별'}\n"
        f"[목표 pH] {req.target_pH or '자동 계산'}\n"
        f"[카테고리] {category}\n"
        f"{guide_ref}\n\n"
        f"[Gemini 리서치]\n{gemini_data if gemini_data else '해당 없음'}\n\n"
        "PRECISION-ARITHMETIC 최우선 적용. 배합비 합계 100.00% 보장.\n"
        "8개 필수 섹션 포함 처방전을 출력해줘. 불필요한 설명 최소화, 핵심만."
    )

    def stream_generator():
        meta = json.dumps({
            "type": "meta", "model": model, "complexity": complexity,
            "gemini_used": gemini_used, "from_cache": False,
            "guide_references": len(similar_guides),
        }, ensure_ascii=False)
        yield f"data: {meta}\n\n"

        try:
            if not gemini_client:
                yield f"data: {json.dumps({'type':'error','message':'Gemini API 미설정'}, ensure_ascii=False)}\n\n"
                return
            full_prompt = f"{system_content}\n\n---\n\n{user_message}"
            response = gemini_client.models.generate_content(
                model=model, contents=full_prompt,
                config=genai_types.GenerateContentConfig(
                    temperature=0.1, max_output_tokens=6000),
            )
            prescription = response.text
            # 청크 분할 전송
            chunk_size = 80
            for i in range(0, len(prescription), chunk_size):
                chunk = prescription[i:i+chunk_size]
                yield f"data: {json.dumps({'type':'text','text':chunk}, ensure_ascii=False)}\n\n"

        except Exception as e:
            yield f"data: {json.dumps({'type':'error','message':str(e)}, ensure_ascii=False)}\n\n"
            return

        elapsed = round(time.time() - start, 2)
        log.info(f"스트리밍 처방 완료: {elapsed}초 | 모델:{model} | 가이드참조:{len(similar_guides)}")

        yield f"data: {json.dumps({'type':'done','processing_time':elapsed}, ensure_ascii=False)}\n\n"
        yield "data: [DONE]\n\n"

        # 캐시 저장 + 메모리 학습
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


# ─── 가이드처방 엔드포인트 (v2.4) ────────────────────────────────────────────
@app.post("/api/v1/guide-formulate")
async def guide_formulation(req: GuideFormulationRequest):
    """
    제품유형만 입력하면 DB 원료를 자동 선택하여 가이드처방 생성
    """
    start = time.time()
    from db import DB_AVAILABLE as _db_avail
    log.info(f"가이드처방 요청: {req.product_type} / {req.skin_type}")

    # ① 제품유형 매핑
    type_config = PRODUCT_TYPE_MAP.get(req.product_type)
    if not type_config:
        available = list(PRODUCT_TYPE_MAP.keys())
        raise HTTPException(
            status_code=400,
            detail=f"지원하지 않는 제품유형: '{req.product_type}'. "
                   f"사용 가능: {', '.join(available)}")

    all_types = type_config["required"] + type_config["optional"]
    category = type_config["category"]
    target_pH = req.target_pH or type_config["default_pH"]
    target_viscosity = req.target_viscosity or type_config["default_viscosity"]

    # ② DB에서 원료 후보 조회
    ingredient_candidates = {}
    regulation_info = []
    kb_data = {}
    reference_products = []

    if _db_avail:
        ingredient_candidates = await get_ingredients_by_types(all_types)
        total_candidates = sum(len(v) for v in ingredient_candidates.values())
        log.info(f"DB 원료 후보: {total_candidates}건 ({len(ingredient_candidates)}개 유형)")

        # 규제 정보 조회
        all_names = []
        for items in ingredient_candidates.values():
            all_names.extend(i["inci_name"] for i in items if i.get("inci_name"))
        if all_names:
            result = await get_regulation_data(all_names[:100])
            if isinstance(result, tuple):
                regulation_info, kb_data = result
            else:
                regulation_info = result

        # 참조 제품 조회
        reference_products = await get_reference_products(req.product_type)
    else:
        log.warning("DB 미연결 — SKILL 기반으로 가이드처방 생성")

    # ③ Claude 프롬프트 구성
    category_skill = SKILL_CAT.get(category, SKILL_CAT["basic"])
    memory_snippet = MEMORY_MD[:2000] if MEMORY_MD else "(메모리 없음)"
    system_content = (
        f"{SYSTEM_PROMPT}\n\n"
        f"--- CORE SKILLS ---\n{CORE_SKILLS}\n\n"
        f"--- CATEGORY GUIDE ({category.upper()}) ---\n{category_skill}\n\n"
        f"--- MEMORY ---\n{memory_snippet}"
    )

    # DB 원료 후보 → 프롬프트 텍스트
    db_section = ""
    if ingredient_candidates:
        lines = ["\n[DB 원료 후보 — 유형별]"]
        for itype, items in ingredient_candidates.items():
            names = [f"{i['inci_name']} ({i.get('korean_name','')})" for i in items[:15]]
            is_req = "필수" if itype in type_config["required"] else "선택"
            lines.append(f"\n### {itype} ({is_req}) — {len(items)}종")
            lines.append(", ".join(names))
        db_section = "\n".join(lines)

    # 규제 정보 텍스트
    reg_section = ""
    if regulation_info:
        reg_lines = ["\n[규제 정보]"]
        seen = set()
        for r in regulation_info[:30]:
            key = r.get("inci_name", r.get("ingredient", ""))
            if key in seen:
                continue
            seen.add(key)
            maxc = r.get("max_concentration", "")
            src = r.get("source", "")
            reg_lines.append(f"- {key}: {maxc} ({src})")
        reg_section = "\n".join(reg_lines)

    # 참조 제품 텍스트
    ref_section = format_reference_products(reference_products)

    # 사용자 메시지 조합
    skin_desc = {
        "건성": "건조한 피부, 보습력 강화 필요",
        "지성": "피지 과다, 가벼운 텍스처 필요",
        "복합성": "T존 유분 + U존 건조, 밸런싱 필요",
        "민감성": "자극 최소화, 순한 성분 위주",
        "all": "모든 피부 타입 적합",
    }.get(req.skin_type, req.skin_type)

    special = f"\n[특별 요구사항] {req.special_requirements}" if req.special_requirements else ""

    user_message = f"""가이드처방 자동 생성 요청:

[제품유형] {req.product_type}
[대상 피부] {req.skin_type} — {skin_desc}
[목표 pH] {target_pH}
[목표 점도] {target_viscosity} cP{special}

{db_section}
{reg_section}
{ref_section}

위 DB 원료 후보에서 최적의 성분을 선택하여 완전한 가이드처방을 생성해줘.
필수 유형({', '.join(type_config['required'])})에서 반드시 성분을 선택하고,
선택 유형({', '.join(type_config['optional'])})에서 적절히 추가해.
정제수(Water/Aqua)를 기제로 반드시 포함.

PRECISION-ARITHMETIC 최우선 적용. 배합비 합계 100.00% 보장.
8개 필수 섹션 포함 처방전을 출력해줘. 불필요한 설명 최소화, 핵심만."""

    # ④ Gemini 호출 (가이드처방)
    model = "gemini-2.5-pro"
    if not gemini_client:
        raise HTTPException(status_code=503, detail="Gemini API 미설정")

    try:
        full_prompt = f"{system_content}\n\n---\n\n{user_message}"
        prescription = await asyncio.to_thread(
            lambda: gemini_client.models.generate_content(
                model=model,
                contents=full_prompt,
                config=genai_types.GenerateContentConfig(
                    temperature=0.15,
                    max_output_tokens=6000,
                ),
            )
        )
        result_text = prescription.text

    except Exception as e:
        raise HTTPException(status_code=502, detail=f"Gemini API 오류: {e}")

    elapsed = round(time.time() - start, 2)
    log.info(f"가이드처방 완료: {elapsed}초 | 모델:gemini-2.5-pro | DB원료:{sum(len(v) for v in ingredient_candidates.values())}종")

    response_data = {
        "resultCode": "00",
        "resultMessage": "SUCCESS",
        "processing_time": elapsed,
        "model": "gemini-2.5-pro",
        "product_type": req.product_type,
        "skin_type": req.skin_type,
        "db_ingredients_count": sum(len(v) for v in ingredient_candidates.values()),
        "regulation_count": len(regulation_info),
        "reference_products_count": len(reference_products),
        "resultData": result_text,
    }

    # ⑤ 가이드처방 이중 백업 + 인덱스 갱신
    save_formulation_backup(
        req.product_type, req.skin_type, result_text,
        {"model": model, "elapsed": elapsed,
         "db_ingredients": sum(len(v) for v in ingredient_candidates.values()),
         "regulations": len(regulation_info)})
    # 학습 인덱스 갱신 (새 가이드처방 즉시 반영)
    load_guide_index()

    return response_data


@app.post("/api/v1/guide-formulate/stream")
async def guide_formulation_stream(req: GuideFormulationRequest):
    """가이드처방 스트리밍 (SSE)"""
    from db import DB_AVAILABLE as _db_avail
    start = time.time()

    type_config = PRODUCT_TYPE_MAP.get(req.product_type)
    if not type_config:
        raise HTTPException(status_code=400,
                            detail=f"지원하지 않는 제품유형: '{req.product_type}'")

    all_types = type_config["required"] + type_config["optional"]
    category = type_config["category"]
    target_pH = req.target_pH or type_config["default_pH"]
    target_viscosity = req.target_viscosity or type_config["default_viscosity"]

    # DB 조회 (스트리밍 시작 전에 완료)
    ingredient_candidates = {}
    regulation_info = []
    reference_products = []

    if _db_avail:
        ingredient_candidates = await get_ingredients_by_types(all_types)
        all_names = []
        for items in ingredient_candidates.values():
            all_names.extend(i["inci_name"] for i in items if i.get("inci_name"))
        if all_names:
            result = await get_regulation_data(all_names[:100])
            regulation_info = result[0] if isinstance(result, tuple) else result
        reference_products = await get_reference_products(req.product_type)

    # 프롬프트 구성
    category_skill = SKILL_CAT.get(category, SKILL_CAT["basic"])
    memory_snippet = MEMORY_MD[:2000] if MEMORY_MD else "(메모리 없음)"
    system_content = (
        f"{SYSTEM_PROMPT}\n\n"
        f"--- CORE SKILLS ---\n{CORE_SKILLS}\n\n"
        f"--- CATEGORY GUIDE ({category.upper()}) ---\n{category_skill}\n\n"
        f"--- MEMORY ---\n{memory_snippet}"
    )

    db_section = ""
    if ingredient_candidates:
        lines = ["\n[DB 원료 후보 — 유형별]"]
        for itype, items in ingredient_candidates.items():
            names = [f"{i['inci_name']} ({i.get('korean_name','')})" for i in items[:15]]
            is_req = "필수" if itype in type_config["required"] else "선택"
            lines.append(f"\n### {itype} ({is_req}) — {len(items)}종")
            lines.append(", ".join(names))
        db_section = "\n".join(lines)

    ref_section = format_reference_products(reference_products)

    skin_desc = {"건성": "건조한 피부", "지성": "피지 과다", "복합성": "T존/U존",
                 "민감성": "자극 최소화", "all": "모든 피부"}.get(req.skin_type, req.skin_type)
    special = f"\n[특별 요구사항] {req.special_requirements}" if req.special_requirements else ""

    user_message = (
        f"가이드처방 자동 생성 요청:\n\n"
        f"[제품유형] {req.product_type}\n[대상 피부] {req.skin_type} — {skin_desc}\n"
        f"[목표 pH] {target_pH}\n[목표 점도] {target_viscosity} cP{special}\n"
        f"{db_section}\n{ref_section}\n\n"
        f"위 DB 원료 후보에서 최적 성분을 선택하여 완전한 가이드처방을 생성해줘.\n"
        f"정제수(Water/Aqua) 기제 필수 포함.\n"
        "PRECISION-ARITHMETIC 최우선 적용. 배합비 합계 100.00% 보장.\n"
        "8개 필수 섹션 포함 처방전을 출력해줘."
    )

    model = "gemini-2.5-pro"

    def stream_generator():
        meta = json.dumps({
            "type": "meta", "model": model, "product_type": req.product_type,
            "db_ingredients": sum(len(v) for v in ingredient_candidates.values()),
        }, ensure_ascii=False)
        yield f"data: {meta}\n\n"

        try:
            if not gemini_client:
                yield f"data: {json.dumps({'type':'error','message':'Gemini API 미설정'}, ensure_ascii=False)}\n\n"
                return
            full_prompt = f"{system_content}\n\n---\n\n{user_message}"
            response = gemini_client.models.generate_content(
                model=model,
                contents=full_prompt,
                config=genai_types.GenerateContentConfig(
                    temperature=0.15,
                    max_output_tokens=6000,
                ),
            )
            result_text = response.text
            # Gemini는 스트리밍 대신 전체 텍스트를 청크로 분할 전송
            chunk_size = 100
            for i in range(0, len(result_text), chunk_size):
                chunk = result_text[i:i+chunk_size]
                yield f"data: {json.dumps({'type':'text','text':chunk}, ensure_ascii=False)}\n\n"
        except Exception as e:
            yield f"data: {json.dumps({'type':'error','message':str(e)}, ensure_ascii=False)}\n\n"
            return

        elapsed = round(time.time() - start, 2)
        yield f"data: {json.dumps({'type':'done','processing_time':elapsed}, ensure_ascii=False)}\n\n"
        yield "data: [DONE]\n\n"
        log.info(f"가이드처방 스트리밍 완료: {elapsed}초")

    return StreamingResponse(stream_generator(), media_type="text/event-stream",
                             headers={"X-Accel-Buffering": "no", "Cache-Control": "no-cache"})


# ─── 원료 검색 API (v2.4) ────────────────────────────────────────────────────
@app.get("/api/v1/ingredients/search")
async def search_ingredients_api(
    q: str = Query(..., min_length=1, description="검색어 (INCI명/한글명)"),
    type: Optional[str] = Query(None, description="원료 유형 필터 (HUMECTANT, EMULSIFIER 등)"),
    limit: int = Query(20, ge=1, le=100),
):
    """DB 원료 검색"""
    from db import DB_AVAILABLE as _db_avail
    if not _db_avail:
        raise HTTPException(status_code=503, detail="DB 미연결")
    results = await db_search_ingredients(q, ingredient_type=type, limit=limit)
    return {"count": len(results), "ingredients": results}


@app.get("/api/v1/ingredients/types")
async def list_ingredient_types():
    """사용 가능한 제품유형 목록"""
    return {
        "product_types": list(PRODUCT_TYPE_MAP.keys()),
        "product_type_details": {
            k: {"required": v["required"], "optional": v["optional"],
                "default_pH": v["default_pH"], "default_viscosity": v["default_viscosity"]}
            for k, v in PRODUCT_TYPE_MAP.items()
        }
    }


if __name__ == "__main__":
    import uvicorn
    uvicorn.run("main:app", host="0.0.0.0", port=PORT, reload=False)
