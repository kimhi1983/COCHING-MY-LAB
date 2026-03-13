# COCHING DB 로드맵 v1.5 (2026-03-13)

> v1.4 + Phase 1A/1B 완료, Phase 3 로컬 LLM 계획 추가

---

## 현재 DB 현황 (v1.5 기준 — 실측값)

| 테이블 | 건수 | 상태 |
|--------|------|------|
| ingredient_master | 25,694 | 22,870건 type='single' (미분류) / 2,694건 분류 완료 |
| product_master | ~16,026 | 정상 |
| regulation_cache | 3,875+ | 아래 상세 (batch_safety 재수집 중) |
| purpose_ingredient_map | **3,387** | **10개 목적, 자동 확장 완료** |
| product_categories | **45** | **Phase 1A 확장 완료** |
| formulation_purposes | **14** | **Phase 1A 신규 생성** |
| category_purpose_link | **124** | **Phase 1A 신규 생성 (45카테고리×평균2.8목적)** |
| guide_cache | 215 | Track 1 완료 |
| guide_cache_copy | 20+ | Track 2 진행 중 |
| coching_knowledge_base | 462 | 정상 |
| compound_master | 15 | 정상 |

### regulation_cache 소스별 상세

| source | 건수 | 상태 | 비고 |
|--------|------|------|------|
| coching_legacy | 2,914 | **활성화 완료** | EU Annex II/III/IV/V/VI → banned/restricted 매핑 |
| GEMINI_SAFETY | 315 | 부분 유효 | 5개국 덮어쓰기 버그로 CN 위주만 남음 → v2 재수집 필요 |
| GEMINI_KR | 300 | 정상 | 한국 규제 |
| GEMINI_EU | 300 | 정상 | EU 규제 |
| REG_MONITOR_US | 37 | 정상 | 비화장품 17건 삭제 후 |
| MFDS_SEED | 6 | 시드 | 식약처 초기 |
| gem2_kb / gemini_kb / UNKNOWN | 3 | 무시 | 테스트 잔여 |

---

## 완료된 수정 (v1.3 → v1.4)

### 1. GEMINI_SAFETY 에러 데이터 정리
- **문제**: restriction에 에러 메시지 저장된 10건 발견 (3% 오류율)
- **조치**: `restriction LIKE '%error%'` 10건 삭제
- **잔존 이슈**: 5개국 덮어쓰기 버그로 315건 중 대부분 CN 국가 데이터만 남음

### 2. REG_MONITOR_US 비화장품 필터링
- **문제**: drug_withdrawal, tobacco, food 등 비화장품 문서 17건 + ERROR 1건 혼입
- **조치**: 18건 삭제, 37건 화장품 관련만 잔존

### 3. batch_safety.py v2 재작성
- **핵심 버그 수정**: UNIQUE(source, ingredient) 제약조건에서 source='GEMINI_SAFETY' 단일값 사용 → 5개국이 같은 키로 덮어씀 → 마지막 국가(CN)만 남음
- **v2 변경**:
  - source를 `GEMINI_SAFETY_KR`, `GEMINI_SAFETY_EU`, `GEMINI_SAFETY_US`, `GEMINI_SAFETY_JP`, `GEMINI_SAFETY_CN`으로 분리
  - 데이터 품질 게이트 (validate_data: 최소 1개국 규제 필수)
  - SQL 파라미터 이스케이프 (run_sql_params)
  - API 재시도 로직
  - 환경변수 GEMINI_API_KEY 우선 사용

### 4. batch_regulation.py v2 재작성
- **핵심 버그 수정**: Federal Register에서 FDA 전체 문서 수집 → 약품/담배/식품 혼입
- **v2 변경**:
  - `is_cosmetic_relevant()` 함수: 제목/초록에서 화장품 키워드 1차 필터
  - `EXCLUDED_CATEGORIES` 집합: Gemini 분류 후 비화장품 카테고리 2차 필터
  - 검색어 특화 (cosmetic safety, sunscreen monograph 등)
  - SQL 파라미터 이스케이프

### 5. coching_legacy EU Annex 매핑
- **이전**: restriction에 `"#REF Annex II/123"` 같은 평문 → 활용 불가
- **조치**: 2,914건 전체를 구조화된 JSON으로 변환
  ```json
  {"status":"banned","annex_type":"II","annex_ref":"원본참조","region":"EU","note":"EU Annex II - 사용금지 성분"}
  ```
- **결과**: banned 1,046건 / restricted 1,550건 / colorant 258건 / preservative 44건 / uv_filter 16건

### 6. purpose_ingredient_map 확장
- **이전**: 59건 (수동 입력)
- **v1.3 오류**: `purpose_code` 컬럼 사용 (실제는 `purpose_key`), `ALLOWED` role 사용 (실제 CHECK는 REQUIRED/RECOMMENDED/FORBIDDEN만)
- **조치**: 실제 스키마 기반으로 ingredient_master.ingredient_type → purpose_key 매핑
  - HUMECTANT/EMOLLIENT/THICKENER/EMULSIFIER/FILM_FORMER → 보습
  - ANTIOXIDANT → 안티에이징
  - UV_FILTER → 자외선차단
  - SURFACTANT → 세정
  - PRESERVATIVE/PH_ADJUSTER/CHELATING → 방부공통
- **결과**: 241건 추가 → 총 300건 (8개 목적)

---

## Phase 0.5: 긴급 데이터 품질 복구

### Task A: GEMINI_SAFETY 5개국 재수집 ⏳ 진행 중
- **목표**: 기존 315건(CN 위주) → 5개국 × N건으로 재수집
- **방법**: batch_safety.py v2 실행 (source 분리 저장)
- **상태**: 배치 실행 중 (50라운드)

### Task B: ingredient_master 기능 분류 ✅ 완료
- **결과**: 1,119건 분류 (EMOLLIENT 303 / SURFACTANT 202 / EMULSIFIER 197 / ACTIVE 1,081 / FILM_FORMER 158 등)
- **잔존**: 22,870건 type='single' → Phase 3 로컬 LLM으로 전량 처리 예정

### Task C: 프론트엔드 안정성 위젯 DB 연결
- **현황**: WidgetStability.vue가 localStorage 시드 데이터 사용, DB 미연결
- **목표**: regulation_cache + Gemini 분석 결과를 실시간 표시

---

## Phase 1: DB 구조 확장 ✅ 완료 (2026-03-13)

### Phase 1A — 테이블 구조
- **product_categories**: 7 → 45개 (스킨케어/선케어/색조/두피모발/바디/특수 전 카테고리)
- **formulation_purposes**: 신규 14개 목적 코드 (MOISTURIZING~COLOR)
- **category_purpose_link**: 124건 (카테고리↔목적 weight 매핑)

### Phase 1B — 데이터 자동 확장
- **purpose_ingredient_map**: 300 → **3,387건** (+3,087건)
  - batch_classify 결과(ingredient_type) → +2,477건
  - GEMINI_SAFETY primary_function → +610건

---

## Phase 1-규제: 규제 데이터 확장

### 규제-1.1 EU Annex 심화 (coching_legacy 활용)
- coching_legacy 2,914건 이미 매핑 완료
- 추가 작업: Annex III restricted 성분의 max_concentration 정보 보강

### 규제-1.2 5개국 규제 커버리지 확대
- batch_safety.py v2로 미수집 원료 순차 처리
- 목표: 5개국 × 5,000건 = 25,000건

### 규제-1.3 REG_MONITOR 다국가 확장
- 현재: US FDA Federal Register만
- 추가: EU SCCS, KR 식약처 고시, JP MHLW, CN NMPA

---

## Phase 2: AI 처방 품질 향상

### 2.1 guide_cache 확장
- Track 1: 215건 완료 (6개 카테고리)
- Track 2: guide_cache_copy 진행 중 (제품카피 가이드)
- 목표: 1,000건+ 가이드 처방

### 2.2 purpose_ingredient_map 고도화
- 300건 → 1,000건+ 목표
- ingredient_master 기능 분류 완료 후 자동 매핑 재실행

---

## 스키마 참고

### regulation_cache
```sql
UNIQUE(source, ingredient)
-- source: 'GEMINI_SAFETY_KR', 'GEMINI_SAFETY_EU', 'REG_MONITOR_US', 'coching_legacy' 등
-- restriction: JSON (status, annex_type, severity, concerns 등)
```

### purpose_ingredient_map
```sql
-- purpose_key: '보습', '안티에이징', '자외선차단', '세정', '방부공통' 등
-- role: CHECK('REQUIRED','RECOMMENDED','FORBIDDEN')
-- inci_key: ingredient_master.inci_name 참조
```

---

## Phase 3: 로컬 LLM 구축 — Qwen 2.5 14B

> **목적**: Gemini/Claude API 비용 없이 대량 처방 생성·원료 분류 처리

### 3.1 모델 선택 — Qwen 2.5 14B

| 항목 | 내용 |
|------|------|
| 모델 | Qwen/Qwen2.5-14B-Instruct |
| 파라미터 | 14B |
| 선택 이유 | 한국어·영어 동시 우수, 화장품 INCI명 처리 정확도, 비용 무료 |
| 양자화 | Q4_K_M (GGUF) — VRAM 8~10GB 목표 |
| 실행 환경 | Ollama (Windows 11 네이티브) |
| GPU 요구사항 | NVIDIA RTX (VRAM 8GB+) 또는 CPU 추론 (속도 저하) |

### 3.2 설치 계획

```
# 1. Ollama 설치
winget install Ollama.Ollama

# 2. Qwen 2.5 14B 모델 다운로드
ollama pull qwen2.5:14b

# 3. API 서버 자동 실행 (localhost:11434)
ollama serve
```

### 3.3 적용 대상

| 용도 | 현재 | 로컬 LLM 대체 후 |
|------|------|-----------------|
| ingredient_master 기능 분류 | Gemini API (유료) | Qwen 2.5 14B (무료) |
| 처방 가이드 생성 (guide_cache) | Claude API (Max 플랜) | 보조 생성 + Claude 검수 |
| 5개국 안전성 분석 | Gemini API | Qwen 2.5 14B |
| 원료 설명 자동 생성 | Gemini API | Qwen 2.5 14B |

### 3.4 batch_classify v3 계획 (로컬 LLM 버전)

```python
# Ollama API 호출 (Gemini → 로컬)
import requests

def call_local_llm(prompt):
    resp = requests.post("http://localhost:11434/api/generate", json={
        "model": "qwen2.5:14b",
        "prompt": prompt,
        "stream": False,
        "options": {"temperature": 0.1, "num_predict": 2048}
    })
    return resp.json()["response"]
```

- DELAY=0 (API 제한 없음) → 처리 속도 대폭 향상
- 25,000건 미분류 원료 전체 처리 가능 (예상 ~5시간, CPU 추론 시 ~20시간)

### 3.5 우선순위 및 의존성

- **선행 조건**: GPU 확인 (VRAM 확인 후 양자화 레벨 결정)
- **우선순위**: Phase 2 완료 후 진행 (단, API 비용 절감 시급 시 조기 착수 가능)
- **참고**: Ollama 모델 저장 경로 `C:\Users\user\.ollama\models` (~9GB)

---

## 배치 스크립트 위치
```
E:\COCHING-WORKFLOW\scripts\batch\
├── batch_safety.py      # v2 — 5개국 분리 저장, 품질 게이트
├── batch_regulation.py  # v2 — 화장품 필터, 카테고리 제외
├── batch_classify.py    # v2 — ingredient_master 기능 분류 (Gemini)
└── (향후) batch_classify_local.py  # v3 — Qwen 2.5 14B 로컬 LLM 버전
```

---

## 변경 이력
| 버전 | 날짜 | 주요 변경 |
|------|------|----------|
| v1.2 | 2026-03-13 | 초기 로드맵 |
| v1.3 | 2026-03-13 | 문제점 인식 + 수정 계획 |
| v1.4 | 2026-03-13 | 실측 DB 기반 전면 교정, 6개 수정 완료 반영 |
| v1.5 | 2026-03-13 | Phase 3 추가 — 로컬 LLM Qwen 2.5 14B / Phase 1A/1B 완료 반영 |
