# MyLab 프로덕션 운영 계획서

> 작성일: 2026-03-11
> 작성자: Architect Agent
> 상태: 초안 (사용자 검토 필요)

---

## 1. 시스템 아키텍처 (외부 접속 구조)

### 1.1 현재 구조 vs 프로덕션 구조

**현재 (개발 모드)**
```
사용자 브라우저 → localhost:5175 (Vite Dev Server)
                → localhost:3001 (Express API)
                → localhost:5432 (PostgreSQL, WSL2)
```

**프로덕션 목표**
```
┌──────────────────────────────────────────────────────────────────┐
│                         인터넷                                    │
│                           │                                       │
│                    [도메인: mylab.coching.kr]                      │
│                           │                                       │
│                   ┌───────┴───────┐                               │
│                   │ Cloudflare    │                               │
│                   │ Tunnel        │                               │
│                   │ (HTTPS 종단)  │                               │
│                   └───────┬───────┘                               │
│                           │ (암호화된 터널)                       │
│                           │                                       │
│  ┌────────────────────────┴────────────────────────────┐         │
│  │              개발자 PC (192.168.0.72)                │         │
│  │                                                      │         │
│  │   ┌──────────────────────────────────────────────┐  │         │
│  │   │  Nginx (Windows, port 80/443)                │  │         │
│  │   │                                              │  │         │
│  │   │  /           → dist/ (정적 파일 서빙)        │  │         │
│  │   │  /api/*      → localhost:3001 (리버스 프록시) │  │         │
│  │   │  /health     → localhost:3001/api/health      │  │         │
│  │   │                                              │  │         │
│  │   │  + gzip 압축                                 │  │         │
│  │   │  + 정적 파일 캐시 (1일)                      │  │         │
│  │   │  + API Rate Limit (IP당 60req/min)           │  │         │
│  │   └──────────────────────────────────────────────┘  │         │
│  │                                                      │         │
│  │   ┌──────────────────┐  ┌────────────────────────┐  │         │
│  │   │ Express API      │  │ WSL2 Ubuntu 22.04      │  │         │
│  │   │ (PM2, port 3001) │  │                        │  │         │
│  │   │                  │  │ PostgreSQL 17 (5432)   │  │         │
│  │   │ - 원료 검색      │  │ n8n (PM2, 5678)        │  │         │
│  │   │ - 규제 조회      │  │                        │  │         │
│  │   │ - 가이드 처방    │  │ coching_db             │  │         │
│  │   │ - KPI 통계      │  │  ├ knowledge_base 228  │  │         │
│  │   │                  │←→│  └ regulation_cache329 │  │         │
│  │   └──────────────────┘  └────────────────────────┘  │         │
│  └──────────────────────────────────────────────────────┘         │
└──────────────────────────────────────────────────────────────────┘
```

### 1.2 네트워크 구성 -- 두 가지 대안

#### 대안 A: Cloudflare Tunnel (권장)

| 항목 | 내용 |
|------|------|
| 방식 | `cloudflared` 데몬이 Cloudflare Edge로 아웃바운드 터널 생성 |
| 장점 | 공유기 포트포워딩 불필요, DDoS 방어, 무료 HTTPS, IP 비노출 |
| 단점 | Cloudflare 계정 필요, 도메인 NS를 Cloudflare로 이전 |
| 비용 | 무료 (Zero Trust Free 플랜) |
| 설정 | `cloudflared tunnel --url http://localhost:80` |

```
인터넷 사용자
    │
    ▼
Cloudflare Edge (HTTPS, WAF, DDoS 방어)
    │ (아웃바운드 터널, 포트포워딩 불필요)
    ▼
cloudflared (개발자 PC)
    │
    ▼
Nginx (localhost:80)
```

#### 대안 B: 공유기 포트포워딩 + Let's Encrypt

| 항목 | 내용 |
|------|------|
| 방식 | 공유기에서 80/443 포트를 192.168.0.72로 포워딩 |
| 장점 | 외부 의존성 없음, 직접 제어 |
| 단점 | 동적 IP 문제 (DDNS 필요), 포트포워딩 보안 위험, 인증서 갱신 필요 |
| 비용 | 도메인 비용만 (연 1~2만원) |

**트레이드오프 분석 결과: 대안 A 권장.**
개발자 PC가 프로덕션 서버인 상황에서 IP 비노출과 DDoS 방어는 필수적이다. Cloudflare Tunnel은 공유기 설정 없이 즉시 배포 가능하다.

### 1.3 프로세스 구성

| 프로세스 | 포트 | 관리 | 역할 |
|----------|------|------|------|
| Nginx (Windows) | 80 | Windows 서비스 | 리버스 프록시 + 정적 파일 서빙 |
| Express API | 3001 | PM2 (Windows) | REST API 서버 |
| PostgreSQL 17 | 5432 | WSL2 서비스 | 데이터베이스 |
| n8n | 5678 | PM2 (WSL2) | 데이터 수집 워크플로우 |
| cloudflared | - | Windows 서비스 | HTTPS 터널 |

**PM2 ecosystem 설정 (Windows측)**
```javascript
// ecosystem.config.cjs
module.exports = {
  apps: [{
    name: 'mylab-api',
    script: './MyLab-Prototype/server/index.js',
    cwd: 'E:/COCHING',
    env: {
      NODE_ENV: 'production',
      API_PORT: 3001,
      DB_HOST: '127.0.0.1',
      DB_PORT: 5432,
      DB_NAME: 'coching_db',
      DB_USER: 'coching_user',
      // DB_PASS는 환경변수로 별도 주입
    },
    instances: 1,
    autorestart: true,
    max_memory_restart: '500M',
    log_date_format: 'YYYY-MM-DD HH:mm:ss',
  }]
}
```

### 1.4 보안 고려사항

| 위협 | 대응 | 우선순위 |
|------|------|----------|
| DB 비밀번호 하드코딩 | `server/db.js`에서 환경변수 필수화, 기본값 제거 | P0 (즉시) |
| API 무제한 접근 | Nginx rate-limit (60 req/min/IP) | P0 |
| CORS 전체 허용 | `cors({ origin: 'https://mylab.coching.kr' })` 으로 제한 | P0 |
| SQL Injection | 현재 Parameterized Query 사용 중 (양호) | - |
| XSS | Vue 3 기본 이스케이핑 (양호), API 응답 Content-Type 확인 | P1 |
| 사용자 인증 없음 | Phase 1에서 최소 인증 구현 필요 | P0 |
| HTTPS 미적용 | Cloudflare Tunnel이 자동 처리 | P0 |
| n8n 외부 노출 | n8n은 WSL2 내부에서만 접근, Nginx에서 프록시하지 않음 | P1 |
| DB 포트 외부 노출 | PostgreSQL listen_addresses는 localhost만 허용 확인 | P0 |

**즉시 수정 필요 사항:**
```javascript
// server/db.js -- DB_PASS 기본값 제거
const pool = new pg.Pool({
  host: process.env.DB_HOST || '127.0.0.1',
  port: parseInt(process.env.DB_PORT || '5432'),
  database: process.env.DB_NAME || 'coching_db',
  user: process.env.DB_USER || 'coching_user',
  password: process.env.DB_PASS,  // 기본값 없이 환경변수 필수
  max: 10,
  idleTimeoutMillis: 30000,
})
```

---

## 2. 기능 로드맵 (우선순위별)

### Phase 1: 프로덕션 배포 기반 (1~2주)

| # | 작업 | 설명 | 난이도 |
|---|------|------|--------|
| 1.1 | Vite 프로덕션 빌드 | `vite build` → `dist/` 생성, base URL 설정 | 하 |
| 1.2 | Nginx 설정 | 정적 파일 서빙 + API 리버스 프록시 | 중 |
| 1.3 | PM2 프로세스 관리 | Express API를 PM2로 데몬화, 자동 재시작 | 하 |
| 1.4 | 환경변수 분리 | `.env.production` 파일 생성, DB 비밀번호 하드코딩 제거 | 하 |
| 1.5 | Cloudflare Tunnel | HTTPS + 도메인 연결 | 중 |
| 1.6 | 최소 인증 | 단순 비밀번호 게이트 (환경변수 기반, JWT 미사용) | 중 |
| 1.7 | CORS 제한 | 프로덕션 도메인만 허용 | 하 |
| 1.8 | API Rate Limit | `express-rate-limit` 미들웨어 추가 | 하 |

**Phase 1 최소 인증 설계:**
외부 접속 시 첫 화면에 비밀번호 입력 → 맞으면 세션 쿠키 발급 → 이후 API 호출 시 쿠키 검증. JWT나 OAuth는 Phase 4에서 구현.

```
GET /              → 비밀번호 입력 화면 (쿠키 없을 때)
POST /api/auth     → 비밀번호 검증 → Set-Cookie: session=<token>
GET  /api/*        → Cookie 검증 미들웨어
```

### Phase 2: 핵심 기능 완성 (2~4주)

| # | 작업 | 설명 | 난이도 |
|---|------|------|--------|
| 2.1 | AI 처방 생성 (LLM 연동) | DB 원료 + 규제 데이터를 LLM에 전달하여 정밀 배합 생성 | 상 |
| 2.2 | 카피 처방 기능 | 시중 제품명 입력 → 전성분 분석 → 유사 배합비 역산 | 상 |
| 2.3 | 성분 DB 페이지 | 전체 원료 목록 + 상세 (EWG, 규제, 물성) 전용 뷰 | 중 |
| 2.4 | 안정성 시험 위젯 실 데이터 | mock 제거, DB 스키마 추가 (stability_tests 테이블) | 중 |
| 2.5 | 오늘의 로그 위젯 실 데이터 | mock 제거, 활동 로그 API 추가 | 하 |
| 2.6 | Phase A/B/C/D 구분 | 처방 원료를 상(Phase)별로 분류하여 표시 | 중 |

**AI 처방 생성 설계 (2.1):**
현재 `server/index.js`의 `/api/guide-formula`는 DB에서 랜덤 선택 후 수학적 배분만 수행한다. 이를 다음과 같이 개선한다:

```
사용자 요청 (제품 유형 + 처방 방향 + 지정 원료)
    │
    ▼
[Express API] /api/ai-formula (POST)
    │
    ├─ 1. DB에서 관련 원료 후보 검색 (coching_knowledge_base)
    ├─ 2. 규제 정보 매핑 (regulation_cache)
    ├─ 3. 프롬프트 구성 (원료 목록 + 규제 제약 + 사용자 요청)
    │
    ▼
[LLM API 호출] (OpenAI GPT-4o / Claude)
    │
    ├─ 응답: Phase별 원료 + 배합비 + 제조 과정 + 주의사항
    │
    ▼
[후처리]
    ├─ 배합비 합계 100% 검증
    ├─ 규제 최대 농도 초과 검증
    ├─ 결과 구조화 (JSON)
    │
    ▼
[응답 반환] → Frontend AiGuideView.vue
```

### Phase 3: 부가 기능 (4~6주)

| # | 작업 | 설명 | 난이도 |
|---|------|------|--------|
| 3.1 | 연구 노트 (Research Notes) | 실험 노트 CRUD + 처방 연결 | 중 |
| 3.2 | 품질 검증 (QA Validation) | QA 체크리스트 + 규제 자동 교차검증 | 중 |
| 3.3 | 내보내기 (Excel/PDF) | 처방 데이터를 xlsx/pdf로 다운로드 | 중 |
| 3.4 | 다중 시장 규제 비교 | KR/EU/US 동시 비교 위젯 | 중 |
| 3.5 | 시스템 로그 페이지 | 실시간 작업 로그 (n8n 수집 상태, API 호출 등) | 하 |
| 3.6 | 처방 버전 관리 | 동일 처방의 v1, v2, v3... 히스토리 | 중 |

### Phase 4: 고도화 (6주+)

| # | 작업 | 설명 | 난이도 |
|---|------|------|--------|
| 4.1 | 사용자 인증 (JWT) | 회원가입/로그인, JWT 토큰 기반 인증 | 상 |
| 4.2 | 다중 사용자 지원 | 사용자별 처방/프로젝트 격리, 권한 관리 | 상 |
| 4.3 | 실시간 알림 | WebSocket 기반 n8n 수집 완료/규제 변경 알림 | 중 |
| 4.4 | 모바일 최적화 | 반응형 강화, PWA 지원 | 중 |
| 4.5 | 처방 공유/협업 | 링크로 처방 공유, 코멘트 기능 | 상 |
| 4.6 | COCHING 본 플랫폼 통합 | MyLab을 Coching-User-Vue 모듈로 편입 | 상 |

### 로드맵 타임라인 (Gantt)

```
2026-03                          2026-04                          2026-05
W2        W3        W4          W1        W2        W3        W4  W1
├─────────┤
│ Phase 1 │
│ 배포기반 │
├─────────┼─────────────────────┤
           │      Phase 2       │
           │    핵심 기능 완성   │
           ├─────────────────────┼─────────────────────┤
                                 │      Phase 3       │
                                 │    부가 기능       │
                                 ├─────────────────────┼──────────
                                                       │ Phase 4
                                                       │ 고도화
```

---

## 3. 데이터 흐름도

### 3.1 전체 파이프라인 (n8n → DB → API → Frontend)

```
┌──────────────────────────────────────────────────────────────────────┐
│                        데이터 수집 파이프라인                          │
│                                                                      │
│  ┌─────────────┐     ┌──────────────┐     ┌────────────────────┐    │
│  │  외부 소스   │     │  n8n 워크플로 │     │   PostgreSQL       │    │
│  │             │     │  (WSL2:5678) │     │   coching_db       │    │
│  │ Gemini API  │────▶│              │────▶│                    │    │
│  │ Claude API  │     │ 규제 정규화  │     │ knowledge_base     │    │
│  │ 식약처 DB   │     │ 물성 정규화  │     │  (228건)           │    │
│  │ EWG DB      │     │ 통합 머지    │     │ regulation_cache   │    │
│  │ EU CosIng   │     │ 중복 체크    │     │  (329건)           │    │
│  └─────────────┘     └──────────────┘     └────────┬───────────┘    │
│                                                     │                │
│                                                     │ TCP 5432       │
│                                                     ▼                │
│  ┌──────────────────────────────────────────────────────────────┐    │
│  │                   Express API (port 3001)                    │    │
│  │                                                              │    │
│  │  GET  /api/stats              → KPI 위젯 데이터             │    │
│  │  GET  /api/ingredients        → 원료 목록/검색              │    │
│  │  GET  /api/ingredients/:id    → 원료 상세 + 규제 정보       │    │
│  │  GET  /api/regulations        → 규제 데이터 목록            │    │
│  │  GET  /api/regulation-sources → 규제 출처 목록              │    │
│  │  GET  /api/knowledge          → 지식베이스 검색             │    │
│  │  POST /api/guide-formula      → 가이드 처방 생성 (현재)    │    │
│  │  POST /api/ai-formula         → AI 정밀 처방 (Phase 2)     │    │
│  │  POST /api/copy-formula       → 카피 처방 (Phase 2)        │    │
│  └──────────────────────────────┬───────────────────────────────┘    │
│                                  │ HTTP JSON                         │
│                                  ▼                                   │
│  ┌──────────────────────────────────────────────────────────────┐    │
│  │                Vue 3 Frontend (Vite Build → dist/)           │    │
│  │                                                              │    │
│  │  useAPI.js (composable)                                      │    │
│  │     │                                                        │    │
│  │     ├─▶ ingredientStore.js  →  위젯 (KPI, 규제 모니터링)    │    │
│  │     ├─▶ formulaStore.js     →  처방 CRUD (LocalStorage)     │    │
│  │     ├─▶ projectStore.js     →  프로젝트 관리 (LocalStorage) │    │
│  │     └─▶ widgetStore.js      →  대시보드 위젯 레이아웃       │    │
│  │                                                              │    │
│  │  Views: Dashboard / FormulaList / FormulaEdit /              │    │
│  │         AiGuide / Projects / Journal                         │    │
│  └──────────────────────────────────────────────────────────────┘    │
└──────────────────────────────────────────────────────────────────────┘
```

### 3.2 AI 처방 생성 흐름 (Phase 2 목표)

```
사용자 입력
┌────────────────────────────────┐
│ 처방명: 고보습 시카 세럼       │
│ 제형: O/W 세럼                 │
│ 방향: 보습 + 진정, 약산성      │
│ 지정 원료: 나이아신아마이드 5% │
│ 시장: KR / EU                  │
└────────────┬───────────────────┘
             │
             ▼
┌────────────────────────────────┐
│ Express API: POST /api/ai-formula │
│                                   │
│ Step 1. 원료 후보 검색            │
│   coching_knowledge_base에서      │
│   제형(O/W 세럼)에 적합한 원료    │
│   카테고리별 검색                  │
│   → 보습제, 유화제, 증점제 등     │
│                                   │
│ Step 2. 규제 제약 조건 수집       │
│   regulation_cache에서            │
│   KR + EU 규제 데이터 매핑        │
│   → 최대 허용 농도, 제한 사항     │
│                                   │
│ Step 3. LLM 프롬프트 구성         │
│   시스템: 화장품 처방 전문가       │
│   사용자: 원료 목록 + 규제 제약   │
│          + 사용자 요청             │
│   지시: Phase A/B/C/D 분류,      │
│         배합비 합계 100%,         │
│         제조 공정 포함             │
└────────────┬───────────────────┘
             │
             ▼
┌────────────────────────────────┐
│ LLM API (GPT-4o / Claude)     │
│                                │
│ 응답 (JSON):                   │
│ {                              │
│   phases: [                    │
│     { phase: "A", temp: "75C", │
│       ingredients: [...] },    │
│     { phase: "B", ... },       │
│   ],                           │
│   manufacturing: [...],        │
│   quality_checks: [...],       │
│   cautions: [...]              │
│ }                              │
└────────────┬───────────────────┘
             │
             ▼
┌────────────────────────────────┐
│ 후처리 검증                    │
│                                │
│ 1. 배합비 합계 = 100.00% ?    │
│    → 오차 시 정제수 자동 조정  │
│                                │
│ 2. 규제 최대 농도 초과 검사    │
│    → 위반 시 경고 플래그       │
│                                │
│ 3. 구조 정규화                 │
│    → 프론트엔드 표시용 변환    │
└────────────┬───────────────────┘
             │
             ▼
┌────────────────────────────────┐
│ Frontend: AiGuideView.vue     │
│                                │
│ - Phase별 원료 테이블          │
│ - 제조 공정 단계 표시          │
│ - 규제 위반 경고 하이라이트    │
│ - [처방으로 저장] → formulaStore│
│ - [재생성] → 재요청            │
└────────────────────────────────┘
```

### 3.3 카피 처방 흐름 (Phase 2 목표)

```
사용자 입력
┌────────────────────────────────────────┐
│ "이니스프리 그린티 씨드 크림 카피 처방" │
└──────────────┬─────────────────────────┘
               │
               ▼
┌──────────────────────────────────────┐
│ Step 1. 전성분 정보 수집             │
│                                      │
│ LLM에게 해당 제품의 공개 전성분 요청  │
│ (식약처 공개 DB 또는 제품 패키지 기반)│
│                                      │
│ 결과:                                │
│  Water, Glycerin, Niacinamide,       │
│  Camellia Sinensis Seed Oil, ...     │
└──────────────┬───────────────────────┘
               │
               ▼
┌──────────────────────────────────────┐
│ Step 2. DB 원료 매칭                 │
│                                      │
│ 전성분 INCI → coching_knowledge_base │
│ 에서 매칭 검색                        │
│ → EWG 등급, 규제 정보, 물성 보강     │
│                                      │
│ 매칭 결과:                           │
│  16/16건 DB 매칭 성공                │
│   2건 규제 제한 확인                 │
└──────────────┬───────────────────────┘
               │
               ▼
┌──────────────────────────────────────┐
│ Step 3. 배합비 역산 (LLM 추론)      │
│                                      │
│ 프롬프트:                            │
│  - 전성분 목록 (표기 순서 = 함량순)  │
│  - 각 원료의 규제 최대 농도          │
│  - 제품 유형 (O/W 크림)             │
│  - 공개된 주요 성분 함량 (있으면)    │
│                                      │
│ LLM이 전성분 순서 + 규제 + 제형을   │
│ 기반으로 배합비 추정                 │
│                                      │
│ 결과: Phase A/B/C/D + wt%           │
└──────────────┬───────────────────────┘
               │
               ▼
┌──────────────────────────────────────┐
│ Step 4. 검증 + 표시                  │
│                                      │
│ - 합계 100% 검증                     │
│ - 규제 초과 검사                     │
│ - "카피 처방" 라벨 + 원본 제품명    │
│ - [처방으로 저장] 가능               │
│                                      │
│ UI: mylab-ui Formulation.tsx 패턴    │
│  PRX-ID, Phase 색상, QA 태그 포함   │
└──────────────────────────────────────┘
```

### 3.4 n8n 데이터 수집 → 위젯 연동 상세

```
n8n 워크플로우 (FW6GUTq0AzBXjJQ5)
│
├─ Trigger: 수동 실행 또는 Cron (추후 일 1회)
│
├─ Node 1: Gemini/Claude로 원료 정보 수집
│   → INCI Name, EWG 등급, 규제 정보, 물성
│
├─ Node 2: 규제 정규화
│   → KR/EU/US 규제 파싱 → regulation_cache INSERT
│
├─ Node 3: 물성 정규화
│   → 분자량, 용해도, 안전 노트 구조화
│
├─ Node 4: 통합 머지
│   → JSONB로 coching_knowledge_base UPSERT
│
├─ Node 5: 중복 체크
│   → 기존 데이터와 비교, 신규만 INSERT
│
└─ Node 6: 실행 리포트
    → 수집 건수, 오류 건수 기록

위젯별 데이터 출처:
┌──────────────────────────┬───────────────────────────────────────┐
│ 위젯                     │ 데이터 소스                            │
├──────────────────────────┼───────────────────────────────────────┤
│ KPI (총 원료/규제 수)    │ GET /api/stats → count 쿼리           │
│ 규제 모니터링            │ GET /api/regulations → regulation_cache│
│ 최근 처방                │ formulaStore (LocalStorage)            │
│ 진행 중 처방             │ formulaStore (LocalStorage)            │
│ 프로젝트 요약            │ projectStore (LocalStorage)            │
│ 안정성 시험 (mock)       │ Phase 2에서 DB 연동 예정               │
│ 오늘의 로그 (mock)       │ Phase 2에서 활동 로그 API 추가 예정    │
│ 상태 차트                │ formulaStore 집계 (LocalStorage)       │
│ 메모                     │ widgetStore (LocalStorage)             │
│ 빠른 작업                │ 정적 링크 (라우터 연결)                │
└──────────────────────────┴───────────────────────────────────────┘
```

---

## 4. 추가 제안 위젯/UI

### 4.1 mylab-ui에 있지만 현재 없는 기능 (구현 제안)

| 기능 | mylab-ui 참고 | 구현 제안 | Phase |
|------|---------------|-----------|-------|
| 처방 생성 (자연어 입력) | `Formulation.tsx` -- textarea + Generate 버튼 | AiGuideView.vue에 자연어 입력 모드 추가. 현재 드롭다운 선택 방식과 병행 | 2 |
| 카피 처방 | `Formulation.tsx` -- "이니스프리 그린티 씨드 크림 카피 처방" | AiGuideView.vue 내 탭으로 추가: [가이드 처방] / [카피 처방] | 2 |
| 성분 DB 페이지 | `Sidebar.tsx` -- Ingredients 메뉴 | `/ingredients` 라우트 신규. 테이블 + 검색 + 상세 패널 | 2 |
| 연구 노트 | `Sidebar.tsx` -- Research Notes 메뉴 | `/notes` 라우트 신규. 에디터 + 처방 링크 + 타임라인 | 3 |
| 품질 검증 | `Sidebar.tsx` -- QA Validation 메뉴 | `/validation` 라우트 신규. 체크리스트 + 자동 규제 검증 | 3 |
| Phase A/B/C/D 표시 | `Formulation.tsx` -- Phase 컬럼 + 색상 코드 | IngredientTable.vue에 Phase 컬럼 추가, 색상 적용 | 2 |
| 시스템 상태 표시 | `Sidebar.tsx` -- System Status (하단) | AppSidebar.vue 하단에 API/DB 연결 상태 표시 | 1 |

### 4.2 새로 제안하는 위젯/기능

| 제안 | 설명 | 가치 | Phase |
|------|------|------|-------|
| n8n 수집 상태 위젯 | 마지막 수집 시각, 성공/실패 건수, 다음 실행 예정. `/api/collection-status` API가 이미 존재 | 데이터 신선도 확인 | 1 |
| 원료 트렌드 위젯 | 최근 n8n으로 신규 추가된 원료 목록 (최근 7일) | 데이터 증가 확인 | 2 |
| 처방 비교 기능 | 2개 처방을 나란히 비교 (원료 차이, 배합비 차이 하이라이트) | 버전 비교에 유용 | 3 |
| 처방 템플릿 | 자주 사용하는 기본 처방 템플릿 저장/불러오기 | 반복 작업 감소 | 3 |
| 원가 계산 위젯 | 원료별 단가 입력 → 처방 원가 자동 계산 | 비즈니스 의사결정 | 4 |
| HLB 자동 계산 | 유화제 HLB값 기반 최적 HLB 자동 계산 | 처방 정확도 향상 | 2 |

---

## 5. 기술 결정 사항

### 5.1 Vite 빌드 vs Nginx 정적 서빙

**결정: Vite 빌드 + Nginx 정적 서빙**

| 항목 | Vite Dev Server | Vite Build + Nginx |
|------|-----------------|---------------------|
| 성능 | HMR 오버헤드, 비최적화 | gzip, 코드 분할, 트리셰이킹 |
| 보안 | 소스맵 노출, 디버그 정보 | 프로덕션 빌드, 소스맵 비포함 |
| 안정성 | 메모리 릭 가능 | Nginx는 검증된 프로덕션 서버 |
| 구성 | 단순 | Nginx 설정 필요 (일회성) |

```nginx
# nginx.conf (Windows용)
server {
    listen 80;
    server_name mylab.coching.kr;

    # 정적 파일 서빙
    root E:/COCHING/MyLab-Prototype/dist;
    index index.html;

    # SPA 라우팅 (Vue Router history mode)
    location / {
        try_files $uri $uri/ /index.html;
    }

    # API 리버스 프록시
    location /api/ {
        proxy_pass http://127.0.0.1:3001;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;

        # Rate Limit
        limit_req zone=api burst=20 nodelay;
    }

    # 정적 파일 캐시
    location ~* \.(js|css|png|jpg|svg|woff2)$ {
        expires 1d;
        add_header Cache-Control "public, immutable";
    }

    # gzip 압축
    gzip on;
    gzip_types text/css application/javascript application/json;
    gzip_min_length 1024;
}

# Rate Limit 정의
limit_req_zone $binary_remote_addr zone=api:10m rate=60r/m;
```

### 5.2 Express API 프로세스 관리

**결정: PM2 (Windows)**

- 자동 재시작 (crash recovery)
- 로그 관리 (pm2 logs)
- 모니터링 (pm2 monit)
- 시스템 시작 시 자동 실행 (`pm2 startup`)

```bash
# 설치 및 시작
npm install -g pm2
cd E:/COCHING
pm2 start ecosystem.config.cjs
pm2 save
pm2 startup   # Windows 서비스 등록
```

### 5.3 HTTPS 인증서

**결정: Cloudflare Tunnel (권장)**

| 방식 | 장점 | 단점 |
|------|------|------|
| **Cloudflare Tunnel** | 포트포워딩 불필요, IP 비노출, 무료 HTTPS, WAF, DDoS 방어 | Cloudflare 계정/도메인 NS 이전 필요 |
| Let's Encrypt + certbot | 무료, 표준 | 공유기 포트포워딩 필요, 90일 갱신, IP 노출 |
| Self-signed | 즉시 사용 | 브라우저 경고, 신뢰도 없음 |

**Cloudflare Tunnel 설정 절차:**
```bash
# 1. cloudflared 설치 (Windows)
winget install cloudflare.cloudflared

# 2. 로그인
cloudflared tunnel login

# 3. 터널 생성
cloudflared tunnel create mylab

# 4. DNS 연결
cloudflared tunnel route dns mylab mylab.coching.kr

# 5. 설정 파일 (~/.cloudflared/config.yml)
# tunnel: <TUNNEL_ID>
# credentials-file: ~/.cloudflared/<TUNNEL_ID>.json
# ingress:
#   - hostname: mylab.coching.kr
#     service: http://localhost:80
#   - service: http_status:404

# 6. 서비스 등록 + 실행
cloudflared service install
cloudflared tunnel run mylab
```

### 5.4 사용자 인증 방식

**Phase별 진화 계획:**

| Phase | 방식 | 설명 |
|-------|------|------|
| Phase 1 | 공유 비밀번호 | 환경변수에 비밀번호 1개. 쿠키 세션. 사용자 구분 없음 |
| Phase 2 | 동일 유지 | 기능 개발 집중 |
| Phase 4 | JWT 인증 | 개별 사용자 계정, 회원가입/로그인, 처방 격리 |

Phase 1 최소 구현:
```javascript
// server/auth.js
import crypto from 'crypto'

const SESSION_SECRET = process.env.SESSION_SECRET
const AUTH_PASSWORD = process.env.AUTH_PASSWORD

function generateToken() {
  return crypto.randomBytes(32).toString('hex')
}

const sessions = new Map() // 메모리 세션 (재시작 시 초기화됨)

export function authMiddleware(req, res, next) {
  const token = req.cookies?.session
  if (token && sessions.has(token)) {
    return next()
  }
  res.status(401).json({ error: 'Unauthorized' })
}

export function loginHandler(req, res) {
  const { password } = req.body
  if (password === AUTH_PASSWORD) {
    const token = generateToken()
    sessions.set(token, { loginAt: new Date() })
    res.cookie('session', token, {
      httpOnly: true,
      secure: true,
      sameSite: 'strict',
      maxAge: 24 * 60 * 60 * 1000, // 24시간
    })
    return res.json({ ok: true })
  }
  res.status(403).json({ error: 'Wrong password' })
}
```

### 5.5 DB 백업 전략

**현재 상태:** 백업 없음. DB 손실 시 n8n 재수집으로 복구 가능하나, 시간 소요.

**권장 백업 전략:**

| 주기 | 방법 | 보관 |
|------|------|------|
| 매일 02:00 | `pg_dump` → gzip 압축 | 로컬 `E:/COCHING/backups/` (최근 7일) |
| 매주 일요일 | `pg_dump` → 클라우드 업로드 | Google Drive 또는 S3 (최근 4주) |

```bash
#!/bin/bash
# backup-db.sh (WSL2 cron)
BACKUP_DIR="/mnt/e/COCHING/backups"
TIMESTAMP=$(date +%Y%m%d_%H%M%S)
FILENAME="coching_db_${TIMESTAMP}.sql.gz"

mkdir -p "$BACKUP_DIR"

PGPASSFILE=/tmp/pgpass pg_dump -h 127.0.0.1 -U coching_user coching_db \
  | gzip > "${BACKUP_DIR}/${FILENAME}"

# 7일 이상 된 백업 삭제
find "$BACKUP_DIR" -name "coching_db_*.sql.gz" -mtime +7 -delete

echo "[Backup] ${FILENAME} created at $(date)"
```

```bash
# WSL2 crontab 등록
# crontab -e
0 2 * * * /home/kpros/backup-db.sh >> /var/log/coching-backup.log 2>&1
```

### 5.6 추가 DB 스키마 (Phase 2 이후)

현재 처방 데이터는 LocalStorage에 저장된다. 프로덕션에서는 다음 테이블 추가를 고려해야 한다.

```sql
-- Phase 2: 처방 데이터 DB 이관
CREATE TABLE formulas (
    id TEXT PRIMARY KEY,
    user_id TEXT NOT NULL DEFAULT 'default',
    title TEXT NOT NULL,
    product_type TEXT,
    status TEXT DEFAULT 'draft',  -- draft, review, done
    formula_data JSONB,           -- ingredients, notes
    memo TEXT,
    tags TEXT[],
    project_id TEXT,
    created_at TIMESTAMPTZ DEFAULT NOW(),
    updated_at TIMESTAMPTZ DEFAULT NOW()
);

CREATE TABLE projects (
    id TEXT PRIMARY KEY,
    user_id TEXT NOT NULL DEFAULT 'default',
    name TEXT NOT NULL,
    description TEXT,
    created_at TIMESTAMPTZ DEFAULT NOW(),
    updated_at TIMESTAMPTZ DEFAULT NOW()
);

-- Phase 2: 안정성 시험
CREATE TABLE stability_tests (
    id SERIAL PRIMARY KEY,
    formula_id TEXT REFERENCES formulas(id),
    test_type TEXT,       -- temperature, light, pH
    condition TEXT,       -- "50C 8weeks"
    result TEXT,          -- pass, fail, pending
    notes TEXT,
    tested_at TIMESTAMPTZ,
    created_at TIMESTAMPTZ DEFAULT NOW()
);

-- Phase 2: 활동 로그
CREATE TABLE activity_logs (
    id SERIAL PRIMARY KEY,
    user_id TEXT,
    action TEXT,          -- formula_created, ai_generated, etc.
    target_type TEXT,     -- formula, project, ingredient
    target_id TEXT,
    details JSONB,
    created_at TIMESTAMPTZ DEFAULT NOW()
);

-- Phase 3: 연구 노트
CREATE TABLE research_notes (
    id TEXT PRIMARY KEY,
    user_id TEXT NOT NULL DEFAULT 'default',
    title TEXT NOT NULL,
    content TEXT,         -- Markdown 또는 HTML
    formula_ids TEXT[],   -- 연결된 처방 ID 배열
    tags TEXT[],
    created_at TIMESTAMPTZ DEFAULT NOW(),
    updated_at TIMESTAMPTZ DEFAULT NOW()
);

-- 인덱스
CREATE INDEX idx_formulas_user ON formulas(user_id);
CREATE INDEX idx_formulas_status ON formulas(status);
CREATE INDEX idx_formulas_project ON formulas(project_id);
CREATE INDEX idx_activity_logs_created ON activity_logs(created_at DESC);
```

---

## 6. 배포 체크리스트

### Phase 1 배포 전 체크리스트

- [ ] `server/db.js` DB 비밀번호 하드코딩 제거
- [ ] `.env.production` 파일 생성 (DB_PASS, AUTH_PASSWORD, SESSION_SECRET)
- [ ] `.gitignore`에 `.env.production` 추가 확인
- [ ] `vite build` 성공 확인
- [ ] Nginx 설치 및 설정 (Windows)
- [ ] PM2로 Express API 데몬화
- [ ] Cloudflare Tunnel 설정 및 도메인 연결
- [ ] 최소 인증 (비밀번호 게이트) 구현
- [ ] CORS 프로덕션 도메인으로 제한
- [ ] API Rate Limit 설정
- [ ] PostgreSQL listen_addresses 확인 (localhost only)
- [ ] n8n 포트(5678) 외부 접근 차단 확인
- [ ] DB 백업 스크립트 등록 (cron)
- [ ] 전체 흐름 테스트 (외부 네트워크에서 접속)

---

## 7. 리스크 및 제약사항

| 리스크 | 영향 | 완화 방안 |
|--------|------|-----------|
| 개발자 PC 전원 꺼짐 | 서비스 전체 중단 | Wake-on-LAN 설정, UPS 고려, 장기적으로 클라우드 이전 |
| Windows 업데이트 자동 재시작 | 서비스 중단 | 업데이트 시간 제어 (야간), PM2 자동 시작 |
| DB 데이터 228건 한정 | AI 처방 품질 제한 | n8n 워크플로우 정기 실행으로 데이터 지속 확충 |
| 단일 사용자 인증 | 다중 사용자 불가 | Phase 4에서 JWT 인증 구현 |
| LocalStorage 처방 데이터 | 브라우저 초기화 시 소실 | Phase 2에서 DB 이관, 그 전까지 수동 백업 안내 |
| LLM API 비용 | AI 처방 생성 시 비용 발생 | Rate Limit + 일일 사용량 제한 설정 |
| WSL2 네트워크 불안정 | DB 연결 끊김 | PM2 자동 재시작 + DB connection pool retry |

---

## 부록: 현재 코드베이스 주요 파일 참조

| 파일 | 역할 |
|------|------|
| `E:\COCHING\MyLab-Prototype\server\index.js` | Express API 서버 (13개 엔드포인트) |
| `E:\COCHING\MyLab-Prototype\server\db.js` | PostgreSQL 연결 Pool |
| `E:\COCHING\MyLab-Prototype\src\composables\useAPI.js` | Frontend API 클라이언트 |
| `E:\COCHING\MyLab-Prototype\src\stores\formulaStore.js` | 처방 CRUD (LocalStorage) |
| `E:\COCHING\MyLab-Prototype\src\stores\ingredientStore.js` | 원료/규제 데이터 Store |
| `E:\COCHING\MyLab-Prototype\src\stores\widgetStore.js` | 대시보드 위젯 레이아웃 |
| `E:\COCHING\MyLab-Prototype\src\router.js` | Vue Router (7개 라우트) |
| `E:\COCHING\MyLab-Prototype\vite.config.js` | Vite 빌드 설정 |
| `E:\COCHING\mylab-ui\src\components\Formulation.tsx` | 참고 UI: 카피 처방 패턴 |
| `E:\COCHING\mylab-ui\src\components\Sidebar.tsx` | 참고 UI: 네비게이션 구조 |
| `E:\COCHING\docs\mylab-ux-design-spec.md` | UI/UX 설계 명세서 |
