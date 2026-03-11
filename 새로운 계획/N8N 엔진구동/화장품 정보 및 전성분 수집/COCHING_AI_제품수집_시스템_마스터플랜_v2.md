# COCHING AI — 화장품 제품 정보 수집 시스템 v2.0

> **프로젝트 총괄책임자 마스터 플랜**
> 작성일: 2026-03-12 | 상태: 설계 완료 → 구현 진행 중
> 실행 환경: VS Code + n8n + Claude Code Max + Gemini 2.5 Flash

---

## 1. 프로젝트 개요

### 1.1 목적

전세계에서 출시·판매 중인 화장품 제품의 **정확한 정보**를 자동으로 수집·검증·저장하여,
COCHING 마이랩의 **AI 가이드처방 생성**에 필요한 제품 참조 DB를 구축한다.

### 1.2 수집 대상 데이터

| 구분 | 항목 | 필수/선택 | 설명 |
|------|------|-----------|------|
| **기본** | 제품명 (영문/현지어) | 필수 | brand_name + product_name + product_name_local |
| **기본** | INCI 전성분 | 필수 | 국제 표준 INCI 명명법 기반 전체 성분 목록 |
| **기본** | 제품 이미지 | 필수 | 공식 제품 이미지 URL (최소 400x400px) |
| **기본** | 카테고리/제형 | 필수 | cream/serum/lotion/gel/foam/powder/liquid 등 |
| **물성** | pH | 선택 | 추정 또는 실측 pH 값 (소수점 1자리) |
| **물성** | 점도 (Viscosity) | 선택 | centipoise (cP) 단위 추정값 |
| **물성** | 경도 (Hardness) | 선택 | 고형 제품(스틱, 비누 등) 해당 시 |
| **물성** | 비중 (Specific Gravity) | 선택 | 밀도 추정값 |
| **부가** | 피부 타입 | 선택 | oily/dry/combination/sensitive/normal/all |
| **부가** | 용량, 가격, 원산지 | 선택 | 시장 분석용 |
| **부가** | 제조사 웹사이트 | 선택 | 데이터 검증 출처 |
| **부가** | 출시 연도 | 선택 | 제품 최신성 판단 |

### 1.3 수집 대상 지역 및 카테고리

**지역 (5개):**
한국, 일본, 미국, 유럽, 중국

**카테고리 (11개):**
수분크림/페이스크림, 선크림, 샴푸, 클렌저, 바디로션, 토너/스킨,
립밤/립스틱, 파운데이션, 아이크림, 마스크팩, 컨디셔너

---

## 2. 에이전트 팀 구성

최고의 정확도를 달성하기 위해 **6개 전문 에이전트**로 팀을 구성한다.
각 에이전트는 독립적인 역할을 가지며, 파이프라인 순서대로 작업을 수행한다.

### 2.1 에이전트 아키텍처

```
┌─────────────────────────────────────────────────────────────────────┐
│                    🎯 총괄 오케스트레이터 (n8n)                       │
│              3시간마다 자동 실행 / 수동 실행 가능                      │
└──────────┬──────────────────────────────────────┬───────────────────┘
           │                                      │
    ┌──────▼──────┐                      ┌────────▼────────┐
    │ 🌍 Agent 1  │                      │ 🔎 Agent 2      │
    │ OBF 수집관  │                      │ Gemini 리서처   │
    │ (실측 데이터)│                      │ (AI 검색 보충)  │
    └──────┬──────┘                      └────────┬────────┘
           │                                      │
           └────────────┬─────────────────────────┘
                        │
                ┌───────▼───────┐
                │ 🔗 Agent 3   │
                │ 데이터 통합관 │
                │ (중복제거/정규화) │
                └───────┬───────┘
                        │
                ┌───────▼───────┐
                │ 🔬 Agent 4   │
                │ 물성 분석관  │
                │ (pH/점도/비중) │
                └───────┬───────┘
                        │
                ┌───────▼───────┐
                │ 🧠 Agent 5   │
                │ Claude 검증관 │
                │ (INCI 정확성) │
                └───────┬───────┘
                        │
                ┌───────▼───────┐
                │ 📊 Agent 6   │
                │ 리포트 관리자 │
                │ (로그/모니터링)│
                └───────────────┘
```

### 2.2 에이전트별 상세 역할

#### Agent 1: 🌍 OBF 수집관 (Open Beauty Facts)

**역할:** 실제 검증된 제품 데이터를 공개 API에서 수집
**사용 API:** Open Beauty Facts API v2
**엔드포인트:** `https://world.openbeautyfacts.org/api/v2/search`

```
요청 파라미터:
  - categories_tags: 카테고리 필터 (face-creams, sunscreens, shampoos 등)
  - page_size: 배치당 수집량 (10건)
  - fields: product_name, brands, ingredients_text, image_url, categories, countries_tags, quantity
  - page: 랜덤 페이지 (데이터 다양성 확보)
```

**수집 가능 데이터:**
- 제품명, 브랜드명: ✅ 정확 (사용자 제보 + 검증)
- INCI 전성분: ✅ 정확 (제품 라벨 기반)
- 제품 이미지: ✅ 실제 제품 사진
- pH/점도 등 물성: ❌ 미제공 → Agent 4에서 추정

**법적 상태:** ✅ 완전 합법 (Open Database License, CC-BY-SA)

**한계:**
- 한국/일본 제품 비율이 낮음 → Agent 2가 보충
- 전성분이 없는 제품이 30~50% 존재 → 필터링 필요
- 카테고리별 데이터 편차: shampoos 1,781건 vs face-creams 382건

#### Agent 2: 🔎 Gemini 리서처 (Gemini 2.5 Flash)

**역할:** OBF에 없는 한국/일본/지역 브랜드 제품 수집
**사용 API:** Gemini 2.5 Flash (generativelanguage.googleapis.com)
**방식:** AI 기반 웹 검색 + 학습 데이터 활용

```
수집 전략:
  1차: OBF에 없는 K-beauty 브랜드 (설화수, 라네즈, COSRX, 이니스프리, 닥터자르트 등)
  2차: J-beauty 브랜드 (시세이도, SK-II, 하다라보, 캔메이크 등)
  3차: 지역별 인기 제품 (미국 CeraVe, 유럽 La Roche-Posay 등)
```

**수집 가능 데이터:**
- 제품명: ✅ 정확도 높음
- INCI 전성분: ⚠️ 주의 필요 (AI 추정값 → Agent 5에서 검증 필수)
- pH/점도 추정: ⚠️ 대략적 범위만 제공
- 이미지 URL: ⚠️ URL 유효성 검증 필요

**법적 상태:** ✅ 합법 (AI 학습 데이터 + 검색 기반, 직접 크롤링 아님)

**정확도 향상 전략:**
- temperature: 0.3 (낮은 창의성 = 사실 기반 응답 유도)
- responseMimeType: 'application/json' (구조화된 출력 강제)
- 프롬프트에 "Only REAL products with REAL ingredients" 명시
- Agent 5(Claude)에서 교차 검증 실시

#### Agent 3: 🔗 데이터 통합관

**역할:** OBF + Gemini 결과를 합치고 중복 제거 및 데이터 정규화
**실행 위치:** n8n Code 노드

```
처리 로직:
  1. OBF 데이터 + Gemini 데이터 병합
  2. 중복 제거: (brand_name + product_name) 소문자 기준
  3. 데이터 정규화:
     - 브랜드명 통일 (예: "COSRX" = "cosrx" = "Cosrx")
     - INCI 표기 통일 (대소문자, 구분자)
     - 빈 필드 처리 (NULL vs 빈 문자열)
  4. 출처 태깅: source = 'open_beauty_facts' / 'gemini_search'
```

**품질 규칙:**
- brand_name 1글자 이하 → 제거
- product_name 없음 → 제거
- full_ingredients 20자 이하 → 물성 분석 제외 (불완전 데이터)

#### Agent 4: 🔬 물성 분석관 (Gemini 2.5 Flash)

**역할:** 전성분 기반 pH, 점도, 비중 등 물리적 특성 추정
**방식:** Gemini에 전성분을 제공하고 화학적 추론으로 물성 예측

```
추정 항목:
  - pH (산도): 0.0 ~ 14.0 범위, 소수점 1자리
  - 점도 (Viscosity): centipoise 단위 (물=1, 크림=10,000~50,000)
  - 비중 (Specific Gravity): 일반적으로 0.95~1.10
  - 외관 (Appearance): 색상, 투명도
  - 질감 (Texture): 촉감 설명
  - 유통기한 (Shelf Life): 개월 수
```

**정확도 한계 및 보완:**
- Gemini 추정 pH 오차: ±0.5~1.0 (전문가 실측 대비)
- 점도 추정: 제형 카테고리 기반 대략적 범위만 가능
- 보완 방법: 향후 실측 데이터 피드백 시 학습 개선

**temperature: 0.2** (과학적 정확도 우선)

#### Agent 5: 🧠 Claude 검증관 (Claude Code Max)

**역할:** 수집된 데이터의 최종 품질 검증 + 가이드처방 DB 연계 분석
**방식:** SSH로 Claude CLI 호출 (Claude Code Max 플랜)

```
검증 항목:
  1. INCI 표기 정확성
     - 표준 INCI 사전과 대조
     - 오타/비표준 표기 감지 (예: "Aqua" vs "Water")
  2. 성분 호환성 기본 체크
     - 명백한 배합 금기 성분 감지
  3. 가이드처방 연계 분석
     - ingredient_master DB와의 매칭률
     - 신규 원료 발견 시 원료 수집 워크플로우에 피드백
  4. 데이터 신뢰도 등급 부여
     - A등급: OBF 출처 + Claude 검증 통과
     - B등급: Gemini 출처 + Claude 검증 통과
     - C등급: 검증 미완료 또는 부분 통과
```

#### Agent 6: 📊 리포트 관리자

**역할:** 실행 로그 기록 + 수집 현황 모니터링
**저장:** workflow_log 테이블

```
리포트 항목:
  - 실행 시간, 소요 시간
  - OBF 수집 건수 / Gemini 수집 건수
  - 중복 제거 건수
  - DB 저장 성공/실패 건수
  - Claude 검증 결과 요약
  - 다음 실행 예정 시간
```

---

## 3. 데이터 소스 전략

### 3.1 소스별 비교

| 소스 | API | 전성분 | 이미지 | 물성 | 한국 제품 | 법적 안전 |
|------|-----|--------|--------|------|-----------|-----------|
| **Open Beauty Facts** | ✅ 무료 공개 | ✅ 실제 라벨 | ✅ 실제 사진 | ❌ | ⚠️ 적음 | ✅ 완전 합법 |
| **Gemini 검색** | ✅ API 키 | ⚠️ AI 추정 | ⚠️ URL 불확실 | ⚠️ 추정 | ✅ 풍부 | ✅ 합법 |
| **화해 (HWAHAE)** | ❌ 비공개 | ✅ 정확 | ✅ | ❌ | ✅ 최다 | ❌ 크롤링 금지 |
| **@cosme** | ❌ 비공개 | ✅ | ✅ | ❌ | ❌ | ❌ 크롤링 금지 |
| **EWG Skin Deep** | ❌ 비공개 | ✅ 13만+ | ❌ | ❌ | ⚠️ | ❌ 직접 불가 |
| **PubChem** | ✅ 무료 | ❌ (성분별) | ❌ | ✅ 물성 | N/A | ✅ 합법 |

### 3.2 채택 전략: Layer 구조

```
Layer 1 (메인) ─── Open Beauty Facts API
    ↓                 실측 데이터, 합법, 전성분+이미지
    │
Layer 2 (보충) ─── Gemini 2.5 Flash
    ↓                 K-beauty/J-beauty 보충, 물성 추정
    │
Layer 3 (검증) ─── Claude Code Max (SSH)
    ↓                 INCI 정확성, 호환성, 신뢰도 등급
    │
Layer 4 (물성) ─── Gemini + PubChem (기존 v7 연계)
                      pH, 점도, 비중 추정 및 교차 확인
```

### 3.3 향후 추가 가능 소스

| 소스 | 접근 방법 | 우선순위 | 예상 시기 |
|------|-----------|----------|-----------|
| **INCI Decoder API** | 공개 API 확인 필요 | 높음 | 1개월 내 |
| **CosDNA** | Gemini 기반 간접 수집 | 중간 | 2개월 내 |
| **식약처 공개 데이터** | 공공 데이터 포털 API | 높음 | 1개월 내 |
| **EU CPNP 공개 정보** | 공개 DB | 중간 | 3개월 내 |
| **Kaggle OBF 데이터셋** | 일괄 다운로드 | 높음 | 즉시 가능 |

---

## 4. 워크플로우 설계

### 4.1 전체 파이프라인

```
┌──────────────────────────────────────────────────────────────────────┐
│                   n8n 워크플로우 (19개 노드)                          │
│                   3시간마다 자동 실행                                  │
│                                                                      │
│  ⏰ 트리거 ──→ ⚙️ 수집 설정 (카테고리/지역 로테이션)                  │
│                    │                                                 │
│              ┌─────┴─────┐                                          │
│              ↓           ↓         ← 병렬 실행                      │
│         🌍 OBF API   🔄 Gemini 구성                                │
│              ↓           ↓                                          │
│         🔄 OBF 파싱   🌐 Gemini 호출                               │
│              ↓           ↓                                          │
│              └─────┬─────┘                                          │
│                    ↓                                                │
│              🔗 데이터 통합 (중복 제거)                               │
│                    ↓                                                │
│              💾 PostgreSQL 저장 (product_master)                     │
│                    ↓                                                │
│              🔍 물성 미확인 제품 조회                                 │
│                    ↓                                                │
│              🔄 물성 구성 → 🌐 Gemini 물성분석 → 🔄 물성 파싱         │
│                    ↓                                                │
│              💾 물성 저장 (UPDATE product_master)                     │
│                    ↓                                                │
│              🧠 Claude 준비 → 🧠 Claude SSH 분석                     │
│                    ↓                                                │
│              📊 실행 리포트                                          │
└──────────────────────────────────────────────────────────────────────┘
```

### 4.2 카테고리 로테이션 전략

매 실행(3시간)마다 다른 카테고리와 지역 조합으로 수집:

```
실행 1: face-creams × korea
실행 2: sunscreens × japan
실행 3: shampoos × usa
...
실행 55: hair-conditioners × china
→ 약 7일(165시간)에 전체 55개 조합 1회전
→ 1개월에 약 4회전 = 약 2,200건 이상 수집 목표
```

### 4.3 데이터베이스 스키마

```sql
-- 제품 마스터 테이블
CREATE TABLE IF NOT EXISTS product_master (
    id SERIAL PRIMARY KEY,
    brand_name VARCHAR(255) NOT NULL,
    product_name VARCHAR(500) NOT NULL,
    product_name_local VARCHAR(500),
    category VARCHAR(255),
    subcategory VARCHAR(255),

    -- 전성분 (핵심)
    full_ingredients TEXT,
    key_ingredients JSONB,

    -- 제품 분류
    product_type VARCHAR(50),
    target_skin_type VARCHAR(50) DEFAULT 'all',

    -- 물성 데이터
    ph_range VARCHAR(20),
    ph_value NUMERIC(4,2),
    viscosity_cp NUMERIC(10,2),
    specific_gravity NUMERIC(6,4),
    hardness VARCHAR(50),

    -- 외관/질감
    appearance VARCHAR(500),
    texture VARCHAR(500),

    -- 제품 정보
    country_of_origin VARCHAR(100),
    retail_price_usd NUMERIC(10,2),
    volume_ml VARCHAR(50),
    image_url TEXT,
    brand_website VARCHAR(500),
    notable_claims TEXT,
    launch_year INTEGER,
    shelf_life_months INTEGER,
    storage_temp VARCHAR(50),

    -- 메타 데이터
    source VARCHAR(50),
    data_quality_grade CHAR(1) DEFAULT 'C',
    collected_at TIMESTAMP,
    updated_at TIMESTAMP DEFAULT NOW(),
    verified_by_claude BOOLEAN DEFAULT FALSE,

    -- 유니크 제약
    UNIQUE(brand_name, product_name)
);

-- 인덱스
CREATE INDEX idx_product_category ON product_master(category);
CREATE INDEX idx_product_country ON product_master(country_of_origin);
CREATE INDEX idx_product_source ON product_master(source);
CREATE INDEX idx_product_quality ON product_master(data_quality_grade);
CREATE INDEX idx_product_ph ON product_master(ph_value) WHERE ph_value IS NOT NULL;

-- 워크플로우 로그 테이블
CREATE TABLE IF NOT EXISTS workflow_log (
    id SERIAL PRIMARY KEY,
    workflow_name VARCHAR(100),
    run_id VARCHAR(100),
    status VARCHAR(20),
    products_collected INTEGER,
    details JSONB,
    created_at TIMESTAMP DEFAULT NOW()
);
```

---

## 5. 정확도 향상 전략

### 5.1 다단계 검증 파이프라인

```
[수집] → [1차 필터] → [정규화] → [2차 검증] → [등급 부여] → [저장]

1차 필터 (자동):
  - 빈 필드 제거
  - INCI 최소 길이 체크 (20자 이상)
  - 브랜드/제품명 존재 여부

정규화 (자동):
  - 브랜드명 통일
  - INCI 표기 표준화
  - 중복 성분 병합

2차 검증 (Claude):
  - INCI 사전 대조
  - 배합 금기 체크
  - 출처 신뢰도 평가

등급 부여:
  A: OBF 출처 + Claude 검증 통과 (신뢰도 95%+)
  B: Gemini 출처 + Claude 검증 통과 (신뢰도 80%+)
  C: 미검증 또는 부분 통과 (수동 확인 필요)
```

### 5.2 교차 검증 방법

| 검증 방법 | 대상 | 도구 |
|-----------|------|------|
| OBF ↔ Gemini 교차 | 동일 제품 다른 소스 비교 | Code 노드 |
| INCI 사전 대조 | 성분명 표준 여부 | Claude |
| ingredient_master 매칭 | 기존 원료 DB와 연결 | PostgreSQL JOIN |
| PubChem 물성 교차 | pH/분자량 검증 | PubChem API (v7 연계) |

### 5.3 피드백 루프

```
제품 수집 워크플로우 ──→ product_master (저장)
       ↑                        │
       │                        ↓
       │                ingredient_master (원료 DB, v7)
       │                        │
       └──── 신규 원료 발견 시 ──┘
             원료 수집 워크플로우에 피드백
```

---

## 6. 크롤링/수집 방식 상세

### 6.1 합법적 수집 원칙

```
✅ 허용:
  - Open Beauty Facts API (오픈 라이선스)
  - 공공 데이터 포털 API (식약처 등)
  - Gemini/Claude AI 기반 검색 (학습 데이터 활용)
  - PubChem API (공개 과학 DB)

❌ 금지:
  - 화해, @cosme 등 상업 사이트 직접 크롤링
  - robots.txt 무시하는 스크래핑
  - 로그인 필요 데이터 무단 수집
  - API 이용약관 위반
```

### 6.2 API 호출 최적화

```
비용 관리:
  - Gemini 2.5 Flash: $0.15/1M input, $0.60/1M output (가장 저렴)
  - 1회 실행당 약 2회 Gemini 호출 (제품검색 + 물성분석)
  - 1일 8회 실행 × 2회 = 16회/일 → 월 약 480회
  - 예상 월 비용: ~$5 이하 (Gemini Flash 기준)

  - Claude Code Max: 월정액 플랜 포함 (추가 비용 없음)

속도 최적화:
  - OBF API + Gemini 병렬 실행 (n8n 분기)
  - 타임아웃: OBF 30초, Gemini 60초
  - 실패 시 다음 실행에서 자동 재시도 (로테이션)
```

### 6.3 데이터 갱신 전략

```
신규 수집: 3시간마다 자동 (카테고리/지역 로테이션)
업데이트: ON CONFLICT UPSERT (기존 데이터 보완, 덮어쓰기 아님)
물성 보충: 전성분이 있지만 물성이 없는 제품 우선 처리 (LIMIT 5/실행)
검증 갱신: Claude 검증 미완료 제품 순차 처리
```

---

## 7. VS Code 작업 환경

### 7.1 프로젝트 구조

```
E:\COCHING\새로운 계획\
├── workflows/
│   ├── product-collector-v2-obf-gemini.json    ← 이 워크플로우
│   └── ingredient-collector-v7.json             ← 기존 원료 수집
├── sql/
│   ├── create_product_master.sql
│   └── create_workflow_log.sql
├── docs/
│   ├── PRODUCT-COLLECTION-SYSTEM.md             ← 이 문서
│   └── INGREDIENT-COLLECTION-GUIDE.md
└── skills/
    └── (기존 SKILL.md 파일들)
```

### 7.2 VS Code에서의 작업 흐름

```
1. 워크플로우 JSON 편집: VS Code에서 직접 수정
2. n8n Import: ⋯ → Import from file로 적용
3. 테스트: n8n에서 ▶ 수동 실행
4. 로그 확인: n8n Executions 탭 또는 PostgreSQL 조회
5. 코드 수정: VS Code에서 Code 노드 jsCode 편집 → JSON 재Import
```

### 7.3 Claude Code Max 연동

```
SSH 연결 정보:
  - Credential: "Claude Code WSL2 SSH" (ID: uK4wQlmywxsEwHyU)
  - 호스트: localhost (WSL2 내부)
  - 사용자: kpros
  - CLI 경로: /home/kpros/.npm-global/bin/claude

호출 방식:
  echo '프롬프트' | claude -p --output-format json 2>/dev/null | head -c 5000
```

---

## 8. 실행 계획 (로드맵)

### Phase 1: 기반 구축 (즉시)
- [x] 워크플로우 JSON 생성 완료
- [ ] product_master 테이블 생성 (SQL 실행)
- [ ] workflow_log 테이블 생성
- [ ] 워크플로우 n8n Import
- [ ] Gemini API 키 확인 및 노드 연결
- [ ] PostgreSQL 자격증명 연결
- [ ] SSH 자격증명 연결

### Phase 2: 테스트 실행 (1~2일)
- [ ] ▶ 수동 실행 테스트
- [ ] OBF API 응답 확인
- [ ] Gemini 응답 파싱 검증
- [ ] DB 저장 정상 확인
- [ ] Claude 분석 결과 확인
- [ ] 오류 노드 수정

### Phase 3: 자동 운영 (1주)
- [ ] ⏰ 3시간 자동실행 활성화
- [ ] 1주일 동안 모니터링
- [ ] 수집 건수 목표 달성 확인 (주 500건+)
- [ ] 데이터 품질 샘플 검수

### Phase 4: 개선 및 확장 (2~4주)
- [ ] 식약처 공개 데이터 API 추가
- [ ] INCI Decoder 연동 검토
- [ ] Kaggle OBF 데이터셋 일괄 Import
- [ ] 이미지 다운로드 및 로컬 저장 기능
- [ ] 가이드처방 시스템과 연동 테스트

### Phase 5: 고도화 (1~3개월)
- [ ] 데이터 품질 등급 자동화 (A/B/C)
- [ ] 원료 수집 워크플로우(v7)와 양방향 연계
- [ ] 대시보드 UI 구축 (수집 현황 모니터링)
- [ ] 월간 수집 리포트 자동 생성

---

## 9. 성과 지표 (KPI)

| 지표 | 목표 | 측정 방법 |
|------|------|-----------|
| 월간 수집 건수 | 2,000건+ | product_master COUNT |
| 전성분 보유율 | 80%+ | full_ingredients IS NOT NULL |
| 이미지 보유율 | 70%+ | image_url IS NOT NULL |
| 물성 보유율 | 50%+ | ph_value IS NOT NULL |
| A등급 데이터 비율 | 40%+ | data_quality_grade = 'A' |
| 워크플로우 성공률 | 95%+ | workflow_log status = 'completed' |
| OBF:Gemini 비율 | 60:40 | source 기준 |

---

## 10. 기존 시스템과의 연계

### 10.1 원료 수집 워크플로우 (v7)와의 관계

```
[원료 수집 v7]                    [제품 수집 v2]
ingredient_master ←──────────── product_master
  (원료 정보)        JOIN          (제품 정보)
  - INCI명         ingredient    - 전성분
  - 물성 (PubChem) 매칭          - pH/점도
  - 규제 정보                    - 이미지
       ↓                              ↓
       └──────── 가이드처방 생성 ────────┘
                 (마이랩 AI)
```

### 10.2 가이드처방 생성 연계

수집된 제품 데이터는 가이드처방 생성의 **참조 데이터**로 활용:
- 유사 제품의 배합비 역추정
- 카테고리별 표준 pH/점도 범위 설정
- 인기 성분 조합 패턴 분석
- 시장가격 기반 원가 추정

---

> **문서 버전:** v2.0
> **최종 수정:** 2026-03-12
> **다음 리뷰:** Phase 2 완료 후
