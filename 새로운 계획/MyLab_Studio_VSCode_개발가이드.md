# 🧪 MyLab Studio — VS Code 개발 환경 & 프로젝트 설정 가이드

> **프로젝트**: MyLab Studio (COCHING 화장품 R&D 통합 플랫폼)  
> **환경**: Windows + WSL + VS Code + Claude Code  
> **기술 스택**: React + TypeScript + Tailwind + Cloudflare (Pages/Workers/D1/KV/R2) + Hono  
> **날짜**: 2026.03.12  

---

## 목차

1. [프로젝트 개요](#1-프로젝트-개요)
2. [VS Code 환경 셋업](#2-vs-code-환경-셋업)
3. [프로젝트 디렉토리 구조](#3-프로젝트-디렉토리-구조)
4. [CLAUDE.md 마스터 설정](#4-claudemd-마스터-설정)
5. [에이전트팀 구성 (MyLab 특화)](#5-에이전트팀-구성)
6. [DB 스키마 설계](#6-db-스키마-설계)
7. [위젯별 개발 명세](#7-위젯별-개발-명세)
8. [개발 워크플로우](#8-개발-워크플로우)
9. [배포 파이프라인](#9-배포-파이프라인)
10. [Phase별 실행 체크리스트](#10-phase별-실행-체크리스트)

---

## 1. 프로젝트 개요

### 현재 상태 (2026.03.12 기준)

```
MyLab Studio 대시보드 — 이미 구현된 기능:
├── KPI 카드 (DB 원료 430종, 규제정보 329건, 진행중 처방 3건, 참조제품 20종)
├── 상태 차트 (도넛 — 초안 1, 검토중 2, 완료 1)
├── 오늘의 업무 타임라인 (처방/테스트/규제 작업 표시)
├── 안정성 현황 테이블 (처방명, 조건, 주차, ΔE, 점도변화, PASS/FAIL)
├── 진행중 처방 테이블 (ID, 처방명, 체형, 상태, 수정일)
├── 빠른 작업 버튼 (MyLab 가이드 처방 등)
└── 상단 네비게이션 (대시보드, 처방목록, +생성, 일지, 프로젝트, MyLab가이드, 성분DB, 연구노트, 품질검증)
```

### 기술 스택 확정

| 레이어 | 기술 | 용도 |
|--------|------|------|
| **Frontend** | React 18 + TypeScript + Tailwind CSS | SPA 대시보드 |
| **UI 컴포넌트** | shadcn/ui + Recharts + Lucide Icons | 위젯 UI |
| **Backend API** | Cloudflare Workers + Hono | REST API |
| **Database** | Cloudflare D1 (SQLite) | 처방/원료/규제/시험 데이터 |
| **Cache** | Cloudflare KV | 규제 캐시, 세션, 빈번 조회 |
| **File Storage** | Cloudflare R2 | 안정성 사진, PDF, 첨부파일 |
| **AI Engine** | Claude API (Sonnet) | 처방 상담, 보고서 요약 |
| **Automation** | n8n (Self-hosted) | 원료 리서치, 뉴스, 보고서 |
| **Domain** | kpros.kr (Gabia DNS → CF Pages) | 서비스 도메인 |

---

## 2. VS Code 환경 셋업

### 2.1 필수 설치

```bash
# WSL 터미널에서 실행

# 1. Node.js 확인 (18+ 필수)
node --version  # v18.x 이상

# 2. npm이 WSL 내부인지 확인 (Windows npm과 충돌 방지!)
which npm   # /usr/bin/npm 이어야 함

# 3. Claude Code CLI
npm install -g @anthropic-ai/claude-code

# 4. Wrangler CLI (Cloudflare)
npm install -g wrangler

# 5. 프로젝트 생성 (이미 있으면 스킵)
cd ~/projects
# 이미 MyLab 프로젝트가 있다면 해당 디렉토리로 이동
```

### 2.2 VS Code 필수 확장

```
claudeCode (Anthropic 공식)          — Claude Code 사이드바
ESLint                               — 코드 린팅
Tailwind CSS IntelliSense            — Tailwind 자동완성
Prettier                             — 코드 포매팅
Remote - WSL                         — WSL 연동
Thunder Client                       — API 테스트 (Postman 대체)
SQLite Viewer                        — D1 로컬 DB 확인
```

### 2.3 VS Code settings.json

```jsonc
{
  // Claude Code
  "claudeCode.preferredLocation": "sidebar",

  // WSL 환경변수
  "terminal.integrated.env.linux": {
    "CLAUDE_CODE_SUBAGENT_MODEL": "claude-sonnet-4-5-20250929",
    "CLAUDE_CODE_EXPERIMENTAL_AGENT_TEAMS": "1"
  },

  // TypeScript
  "typescript.tsdk": "node_modules/typescript/lib",
  "typescript.enablePromptUseWorkspaceTsdk": true,

  // Tailwind
  "tailwindCSS.experimental.classRegex": [
    ["cva\\(([^)]*)\\)", "[\"'`]([^\"'`]*).*?[\"'`]"]
  ],

  // 파일 정리
  "editor.formatOnSave": true,
  "editor.defaultFormatter": "esbenp.prettier-vscode",
  "[typescript]": { "editor.defaultFormatter": "esbenp.prettier-vscode" },
  "[typescriptreact]": { "editor.defaultFormatter": "esbenp.prettier-vscode" }
}
```

### 2.4 핵심 단축키

| 단축키 | 기능 |
|--------|------|
| `Alt+K` | 선택 코드를 Claude에 @-mention |
| `Ctrl+Shift+P` → "Claude" | Claude 명령어 목록 |
| `Ctrl+`` ` | 터미널 토글 |
| `Ctrl+Shift+5` | 터미널 분할 (멀티 에이전트) |

---

## 3. 프로젝트 디렉토리 구조

```
mylab-studio/
│
├── .claude/                          # Claude Code 에이전트 설정
│   ├── agents/
│   │   ├── architect.md              # 설계 에이전트
│   │   ├── frontend-dev.md           # 프론트엔드 에이전트
│   │   ├── backend-dev.md            # 백엔드 API 에이전트
│   │   ├── formula-specialist.md     # 처방 전문 에이전트 ★ MyLab 특화
│   │   ├── tester.md                 # 테스트 에이전트
│   │   └── docs-manager.md          # 문서 에이전트
│   └── settings.json
│
├── CLAUDE.md                         # 프로젝트 마스터 설정 (모든 에이전트의 헌법)
│
├── frontend/                         # React SPA (Cloudflare Pages)
│   ├── public/
│   │   └── favicon.ico
│   ├── src/
│   │   ├── App.tsx
│   │   ├── main.tsx
│   │   ├── index.css
│   │   │
│   │   ├── components/               # UI 컴포넌트
│   │   │   ├── ui/                   # shadcn/ui 기반 공통
│   │   │   │   ├── Button.tsx
│   │   │   │   ├── Card.tsx
│   │   │   │   ├── Table.tsx
│   │   │   │   ├── Slider.tsx
│   │   │   │   ├── Dialog.tsx
│   │   │   │   └── ...
│   │   │   │
│   │   │   ├── layout/               # 레이아웃
│   │   │   │   ├── Sidebar.tsx
│   │   │   │   ├── Header.tsx
│   │   │   │   ├── DashboardGrid.tsx # 위젯 그리드 시스템
│   │   │   │   └── WidgetWrapper.tsx # 위젯 공통 래퍼
│   │   │   │
│   │   │   └── widgets/              # ★ 핵심 — 위젯 컴포넌트
│   │   │       ├── KpiCards.tsx              # KPI 카드 (기존)
│   │   │       ├── StatusChart.tsx           # 상태 도넛차트 (기존)
│   │   │       ├── TodayTasks.tsx            # 오늘의 업무 (기존)
│   │   │       ├── StabilityTable.tsx        # 안정성 현황 (기존)
│   │   │       ├── ActiveFormulas.tsx        # 진행중 처방 (기존)
│   │   │       ├── QuickActions.tsx          # 빠른 작업 (기존)
│   │   │       │
│   │   │       ├── FormulaCalculator/        # Phase 1: 처방 계산기
│   │   │       │   ├── index.tsx
│   │   │       │   ├── BatchScaler.tsx       # 배치 스케일 업/다운
│   │   │       │   ├── PhaseTable.tsx        # 수상/유상 분리 테이블
│   │   │       │   └── VersionDiff.tsx       # 처방 버전 비교
│   │   │       │
│   │   │       ├── HlbCalculator/            # Phase 1: HLB 계산기
│   │   │       │   ├── index.tsx
│   │   │       │   ├── EmulsifierDb.tsx      # 유화제 DB
│   │   │       │   └── HlbChart.tsx          # HLB 그래프
│   │   │       │
│   │   │       ├── SensoryEval/              # Phase 1: 관능평가
│   │   │       │   ├── index.tsx
│   │   │       │   ├── EvalForm.tsx          # 평가 입력 폼
│   │   │       │   ├── BlindMode.tsx         # 블라인드 테스트
│   │   │       │   ├── RadarChart.tsx        # 레이더 차트
│   │   │       │   └── PanelManager.tsx      # 패널 관리
│   │   │       │
│   │   │       ├── CompatibilityMatrix/      # Phase 1: 호환성 매트릭스
│   │   │       │   ├── index.tsx
│   │   │       │   ├── ConflictDb.tsx        # 충돌 DB
│   │   │       │   └── AlertBadge.tsx        # 경고 배지
│   │   │       │
│   │   │       ├── StabilityPro/             # Phase 2: 안정성 고도화
│   │   │       │   ├── index.tsx
│   │   │       │   ├── PhotoTimeline.tsx     # 사진 타임라인
│   │   │       │   ├── DeltaEChart.tsx       # ΔE 추이 그래프
│   │   │       │   ├── AutoReminder.tsx      # 자동 알림
│   │   │       │   └── ChallengeTest.tsx     # 방부력 시험
│   │   │       │
│   │   │       ├── RegulatoryChecker/        # Phase 2: 규제 체크
│   │   │       │   ├── index.tsx
│   │   │       │   ├── CountryRules.tsx      # 국가별 규제
│   │   │       │   ├── InciGenerator.tsx     # INCI 리스트 생성
│   │   │       │   └── AllergenAlert.tsx     # 알레르겐 감지
│   │   │       │
│   │   │       ├── IngredientFinder/         # Phase 2: 원료 대체 추천
│   │   │       │   ├── index.tsx
│   │   │       │   └── AlternativeCard.tsx
│   │   │       │
│   │   │       ├── LabNotebook/              # Phase 2: 실험 노트
│   │   │       │   ├── index.tsx
│   │   │       │   ├── NoteEditor.tsx
│   │   │       │   └── TagSearch.tsx
│   │   │       │
│   │   │       ├── CostCalculator/           # Phase 3: 원가 계산기
│   │   │       │   ├── index.tsx
│   │   │       │   └── PriceSimulator.tsx
│   │   │       │
│   │   │       ├── ClaimMatcher/             # Phase 3: 클레임 매칭
│   │   │       │   └── index.tsx
│   │   │       │
│   │   │       ├── AiAssistant/              # Phase 3: AI 처방 어시스턴트
│   │   │       │   ├── index.tsx
│   │   │       │   ├── ChatPanel.tsx
│   │   │       │   └── DiagnosticView.tsx
│   │   │       │
│   │   │       ├── ReportGenerator/          # Phase 3: 보고서 생성기
│   │   │       │   └── index.tsx
│   │   │       │
│   │   │       ├── WeeklySummary.tsx          # Phase 4: 주간 요약
│   │   │       ├── ProjectGantt.tsx           # Phase 4: 프로젝트 간트
│   │   │       └── NotificationHub.tsx        # Phase 4: 알림 센터
│   │   │
│   │   ├── pages/                    # 라우트별 페이지
│   │   │   ├── Dashboard.tsx         # 대시보드 (위젯 그리드)
│   │   │   ├── FormulaList.tsx       # 처방 목록
│   │   │   ├── FormulaCreate.tsx     # 처방 생성 (+생성)
│   │   │   ├── Journal.tsx           # 일지
│   │   │   ├── Projects.tsx          # 프로젝트
│   │   │   ├── MylabGuide.tsx        # MyLab 가이드
│   │   │   ├── IngredientDb.tsx      # 성분 DB
│   │   │   ├── ResearchNote.tsx      # 연구 노트
│   │   │   └── QualityCheck.tsx      # 품질 검증
│   │   │
│   │   ├── hooks/                    # 커스텀 훅
│   │   │   ├── useFormula.ts         # 처방 CRUD
│   │   │   ├── useIngredients.ts     # 원료 DB 조회
│   │   │   ├── useStability.ts       # 안정성 데이터
│   │   │   ├── useRegulation.ts      # 규제 데이터
│   │   │   └── useWidgetLayout.ts    # 위젯 레이아웃 상태
│   │   │
│   │   ├── lib/                      # 유틸리티
│   │   │   ├── api.ts                # API 클라이언트 (Workers 연결)
│   │   │   ├── hlb.ts                # HLB 계산 로직
│   │   │   ├── deltaE.ts             # Delta E 색차 계산
│   │   │   ├── inci.ts               # INCI 리스트 생성 유틸
│   │   │   ├── costCalc.ts           # 원가 계산 유틸
│   │   │   └── formatters.ts         # 숫자/날짜 포맷
│   │   │
│   │   ├── constants/                # 상수
│   │   │   ├── ko.ts                 # 한국어 UI 텍스트
│   │   │   ├── emulsifiers.ts        # 유화제 HLB 데이터
│   │   │   ├── allergens.ts          # 26종 알레르겐 목록
│   │   │   └── regulations.ts        # 규제 기준값
│   │   │
│   │   └── types/                    # TypeScript 타입
│   │       ├── formula.ts            # 처방 타입
│   │       ├── ingredient.ts         # 원료 타입
│   │       ├── stability.ts          # 안정성 시험 타입
│   │       ├── sensory.ts            # 관능평가 타입
│   │       └── widget.ts             # 위젯 레이아웃 타입
│   │
│   ├── package.json
│   ├── tsconfig.json
│   ├── tailwind.config.ts
│   ├── vite.config.ts
│   └── .env.local                    # 로컬 환경변수
│
├── workers/                          # Cloudflare Workers (API)
│   ├── src/
│   │   ├── index.ts                  # Hono 메인 라우터
│   │   │
│   │   ├── routes/                   # API 라우트
│   │   │   ├── formulas.ts           # /api/formulas — 처방 CRUD
│   │   │   ├── ingredients.ts        # /api/ingredients — 원료 DB
│   │   │   ├── stability.ts          # /api/stability — 안정성 시험
│   │   │   ├── sensory.ts            # /api/sensory — 관능평가
│   │   │   ├── regulations.ts        # /api/regulations — 규제 검사
│   │   │   ├── costs.ts              # /api/costs — 원가 계산
│   │   │   ├── reports.ts            # /api/reports — 보고서 생성
│   │   │   ├── ai.ts                 # /api/ai — Claude AI 프록시
│   │   │   ├── notifications.ts      # /api/notifications — 알림
│   │   │   └── auth.ts               # /api/auth — 인증
│   │   │
│   │   ├── services/                 # 비즈니스 로직
│   │   │   ├── formulaService.ts     # 처방 관련 로직
│   │   │   ├── compatibilityService.ts # 원료 호환성 검사
│   │   │   ├── regulatoryService.ts  # 규제 검증 로직
│   │   │   ├── stabilityService.ts   # 안정성 판정 로직
│   │   │   ├── aiService.ts          # Claude API 연동
│   │   │   └── reportService.ts      # PDF 보고서 생성
│   │   │
│   │   ├── models/                   # D1 스키마 & 쿼리
│   │   │   ├── schema.sql            # 전체 DB 스키마
│   │   │   ├── migrations/           # 마이그레이션 파일
│   │   │   │   ├── 001_initial.sql
│   │   │   │   ├── 002_stability.sql
│   │   │   │   └── 003_sensory.sql
│   │   │   └── queries/              # 재사용 쿼리
│   │   │       ├── formula.ts
│   │   │       ├── ingredient.ts
│   │   │       └── stability.ts
│   │   │
│   │   └── middleware/
│   │       ├── auth.ts               # JWT 인증
│   │       ├── cors.ts               # CORS 설정
│   │       └── rateLimit.ts          # Rate Limiting
│   │
│   ├── wrangler.toml                 # Workers 설정
│   └── package.json
│
├── docs/                             # 프로젝트 문서
│   ├── architecture/
│   │   ├── overview.md               # 아키텍처 개요
│   │   └── data-flow.md              # 데이터 흐름
│   ├── api/
│   │   └── endpoints.md              # API 명세
│   ├── widgets/
│   │   ├── formula-calculator.md     # 위젯별 상세 스펙
│   │   ├── hlb-calculator.md
│   │   └── ...
│   └── decisions/                    # ADR (Architecture Decision Records)
│
├── scripts/                          # 유틸리티 스크립트
│   ├── seed-ingredients.ts           # 원료 DB 초기 데이터
│   ├── seed-regulations.ts           # 규제 데이터 시드
│   ├── seed-emulsifiers.ts           # 유화제 HLB 데이터
│   └── migrate.ts                    # DB 마이그레이션
│
├── .gitignore
├── package.json                      # 루트 (workspaces)
├── turbo.json                        # Turborepo (선택)
└── README.md
```

---

## 4. CLAUDE.md 마스터 설정

> **이 파일을 프로젝트 루트에 두세요. 모든 에이전트가 이 파일을 "헌법"으로 따릅니다.**

```markdown
# MyLab Studio — CLAUDE.md

## 프로젝트 개요
COCHING 화장품 R&D 연구소의 통합 실험 관리 플랫폼.
처방 개발, 안정성 시험, 관능평가, 규제 검토, 원가 계산을 하나의 웹 시스템으로 통합.

## 도메인 지식 (★ 매우 중요)
- "처방" = 화장품 제조 배합 레시피 (Formula/Formulation)
- "배합비" = 원료의 중량 퍼센트 (wt%), 전체 합 = 100%
- "INCI" = International Nomenclature of Cosmetic Ingredients (국제 화장품 성분 명명법)
- "HLB" = Hydrophilic-Lipophilic Balance (친수-친유 균형, 유화제 선정 기준)
- "ΔE" (Delta E) = 색차값, CIE L*a*b* 기준. 1.0 이하 = 육안 구별 불가, 2.0 이상 = 주의
- "Challenge Test" = ISO 11930 기준 방부력 시험
- "안정성 시험" = 4°C/25°C/45°C/50°C 조건에서 시간 경과에 따른 변화 관찰
- "수상/유상" = Water Phase / Oil Phase (유화 제품의 두 구성 상)

## 기술 스택
- Frontend: React 18 + TypeScript (strict) + Tailwind CSS + Vite
- Backend: Cloudflare Workers + Hono
- Database: Cloudflare D1 (SQLite)
- Cache: Cloudflare KV
- Storage: Cloudflare R2 (사진, PDF)
- AI: Claude API (Sonnet 모델)
- Automation: n8n (self-hosted)
- 배포: Cloudflare Pages (frontend) + Workers (API)
- 도메인: kpros.kr

## 코딩 규칙
- 언어: TypeScript strict mode, 절대 any 사용 금지
- 스타일: Prettier (세미콜론 있음, 싱글쿼트) + ESLint
- 네이밍: camelCase (변수/함수), PascalCase (컴포넌트/타입)
- 커밋: Conventional Commits (feat:, fix:, docs:, chore:)
- UI 텍스트: 모두 한국어, constants/ko.ts에 분리
- 컴포넌트: 함수형 컴포넌트 + React hooks만 사용
- API 응답: { success: boolean, data?: T, error?: string } 형식 통일

## 서브에이전트 위임 규칙

### Parallel dispatch (모든 조건 충족 시):
- 3개 이상의 독립적 작업
- 작업 간 공유 상태 없음
- 파일 경계 명확

### Sequential dispatch:
- 작업 간 의존성 있음 (B가 A의 출력 필요)
- 공유 파일 수정 위험

### Background dispatch:
- 리서치/분석 (파일 수정 없음)
- 테스트 실행, 문서 업데이트

## 자주 틀리는 패턴 (지속 갱신!)
- D1은 복잡한 JOIN이 제한적 → 여러 번 조회 후 JS에서 조합
- Cloudflare Workers에서 fs 모듈 사용 불가
- wrangler.toml binding 이름은 대문자 (DB, KV, R2)
- Tailwind 동적 클래스 → safelist에 추가 필수
- R2 업로드 시 Content-Type 반드시 명시
- D1 TEXT 컬럼에 JSON 저장 시 JSON.stringify/parse 필수
- 배합비 계산 시 반드시 소수점 2자리 반올림 (toFixed(2))
- HLB 값은 0~20 범위, 소수점 1자리

## 참고할 기존 패턴
- KPI 카드: src/components/widgets/KpiCards.tsx
- 안정성 테이블: src/components/widgets/StabilityTable.tsx
- 오늘의 업무: src/components/widgets/TodayTasks.tsx
- API 클라이언트: src/lib/api.ts
```

---

## 5. 에이전트팀 구성 (MyLab 특화)

### 5.1 에이전트 구조도

```
┌─────────────────────────────────────────────────────────────┐
│  Team Lead (Opus) — VS Code 메인 세션                        │
│  역할: 작업 분배, 아키텍처 판단, 최종 검증                     │
├─────────────────────────────────────────────────────────────┤
│                                                              │
│  ┌─────────────┐  ┌──────────────┐  ┌──────────────────┐    │
│  │ architect    │  │ frontend-dev │  │ formula-specialist│   │
│  │ (Opus)       │  │ (Sonnet)     │  │ (Sonnet) ★특화    │   │
│  │ 설계/검증    │  │ React/UI     │  │ 화장품 도메인     │    │
│  └─────────────┘  └──────────────┘  └──────────────────┘    │
│                                                              │
│  ┌─────────────┐  ┌──────────────┐  ┌──────────────────┐    │
│  │ backend-dev  │  │ tester       │  │ docs-manager     │   │
│  │ (Sonnet)     │  │ (Sonnet)     │  │ (Haiku)          │   │
│  │ Workers/D1   │  │ 테스트 작성  │  │ 문서 업데이트     │    │
│  └─────────────┘  └──────────────┘  └──────────────────┘    │
│                                                              │
└─────────────────────────────────────────────────────────────┘
```

### 5.2 에이전트 파일 상세

#### `.claude/agents/architect.md`

```yaml
---
name: architect
description: >
  시스템 설계, 데이터 흐름 검증, 위젯 간 연동 아키텍처,
  D1 스키마 설계 리뷰 시 호출
tools: Read, Glob, Grep
model: opus
maxTurns: 15
---

당신은 MyLab Studio의 수석 아키텍트입니다.

## 도메인 컨텍스트
- 화장품 R&D 실험실 관리 플랫폼
- 처방(Formula) 개발 → 안정성 시험 → 관능평가 → 규제 검토 → 양산

## 핵심 원칙
- 위젯은 독립적이되, 데이터는 D1을 통해 연결
- 모든 위젯은 DashboardGrid에 드래그앤드롭 배치 가능
- API는 RESTful, /api/{resource} 패턴 통일
- 처방 ID(formula_id)가 전체 시스템의 핵심 FK
```

#### `.claude/agents/frontend-dev.md`

```yaml
---
name: frontend-dev
description: >
  React 컴포넌트, 위젯 UI, Tailwind 스타일링,
  차트/그래프, 폼 구현 시 호출
tools: Read, Write, Edit, Glob, Grep, Bash
model: sonnet
maxTurns: 40
---

당신은 React + TypeScript 프론트엔드 전문가입니다.

## 기술 규칙
- React 18 함수형 컴포넌트 + hooks만 사용
- Tailwind CSS만 (inline style 금지)
- shadcn/ui 컴포넌트 우선 사용
- 차트: Recharts 라이브러리
- 아이콘: Lucide React
- 모든 UI 텍스트는 한국어, constants/ko.ts 참조

## 위젯 컴포넌트 규칙
- 모든 위젯은 WidgetWrapper로 감쌈
- props: { title, size: '1x1'|'2x1'|'2x2', onExpand, onCollapse }
- 로딩 상태: Skeleton UI 필수
- 에러 상태: 에러 카드 표시 (재시도 버튼 포함)

## COCHING 디자인 시스템
- 메인 색상: #5B4A3F (브라운), #C9956B (골드 악센트)
- 배경: #FAF7F4 (크림), 카드: #FFFFFF
- 폰트: Pretendard (한글 기본)
- 둥근 모서리: rounded-xl (카드), rounded-lg (버튼)
- 그림자: shadow-sm (카드), shadow-md (모달)
```

#### `.claude/agents/backend-dev.md`

```yaml
---
name: backend-dev
description: >
  Cloudflare Workers API, Hono 라우팅, D1 쿼리,
  KV/R2 연동, 마이그레이션 작업 시 호출
tools: Read, Write, Edit, Glob, Grep, Bash
model: sonnet
maxTurns: 40
---

당신은 Cloudflare Workers + Hono 백엔드 전문가입니다.

## API 설계 규칙
- Hono 프레임워크 사용
- RESTful: GET/POST/PUT/DELETE /api/{resource}
- 응답 형식: { success: boolean, data?: T, error?: string }
- 에러 코드: 400 (입력 오류), 401 (인증), 404 (없음), 500 (서버)

## D1 규칙
- complex JOIN 대신 여러 번 쿼리 후 JS 조합
- TEXT 컬럼에 JSON 저장 시 JSON.stringify/parse 필수
- batch() 메서드로 여러 쿼리 한 번에 실행
- DATETIME은 ISO 8601 문자열로 저장

## wrangler.toml 바인딩
- DB = D1 데이터베이스
- KV = KV 네임스페이스
- R2 = R2 버킷
- AI_API_KEY = 시크릿 (Claude API 키)
```

#### `.claude/agents/formula-specialist.md` (★ MyLab 특화)

```yaml
---
name: formula-specialist
description: >
  처방 계산 로직, HLB 계산, 원료 호환성 검사,
  규제 검증 로직, 안정성 판정 기준 구현 시 호출.
  화장품 R&D 도메인 지식이 필요한 모든 작업.
tools: Read, Write, Edit, Glob, Grep
model: sonnet
maxTurns: 30
---

당신은 화장품 R&D 10년차 시니어 연구원 겸 소프트웨어 엔지니어입니다.

## 핵심 도메인 지식

### 처방 계산
- 전체 배합비 합계 = 정확히 100.00%
- 수상(Water Phase) + 유상(Oil Phase) + 기타 = 100%
- 원료 투입량(g) = 배합비(%) × 배치사이즈(g) / 100
- ppm 변환: 1% = 10,000 ppm

### HLB (Hydrophilic-Lipophilic Balance)
- Griffin 공식: HLB = 20 × (Mh / M)
  - Mh: 친수성 부분 분자량, M: 전체 분자량
- Required HLB: 오일별 고유값 (예: 미네랄오일 10-12, 시어버터 8)
- 유화제 혼합 HLB = (HLB_A × %A + HLB_B × %B) / (%A + %B)
- 목표: Required HLB와 혼합 HLB의 차이 ≤ 1.0

### 안정성 판정 기준
- ΔE (색차): < 1.0 PASS, 1.0~2.0 주의, > 2.0 FAIL
- 점도변화: ±10% 이내 PASS, ±10~15% 주의, > ±15% FAIL
- pH변화: ±0.5 이내 PASS, > ±0.5 FAIL
- 외관: 분리/침전/변취 발생 시 FAIL

### 규제 기준
- 한국 식약처: 화장품법 시행규칙 별표 1 (사용 제한 원료)
- EU SCCS: Annex II (금지), Annex III (제한), Annex IV (색소), Annex V (보존제), Annex VI (자외선차단제)
- 26종 알레르겐: EU Cosmetics Regulation Annex III 향료 알레르겐

### Challenge Test (ISO 11930)
- 접종 균: S.aureus, E.coli, P.aeruginosa, C.albicans, A.brasiliensis
- Criteria A (표준): 세균 D7 -3log, D14 유지, D28 유지 / 진균 D14 -1log, D28 유지
- Criteria B (완화): 세균 D7 -3log, D14 유지, D28 유지 / 진균 D14 정지, D28 유지
```

#### `.claude/agents/tester.md`

```yaml
---
name: tester
description: >
  유닛 테스트, 통합 테스트, 계산 로직 검증,
  E2E 테스트 작성 시 호출
tools: Read, Write, Edit, Glob, Grep, Bash
model: sonnet
maxTurns: 30
background: true
---

당신은 QA 엔지니어입니다.

## 테스트 프레임워크
- Vitest (유닛/통합)
- Testing Library (React 컴포넌트)
- Miniflare (Workers 로컬 테스트)

## 필수 테스트 항목
- 처방 계산: 배합비 합계 = 100%, 배치 변환 정확도
- HLB 계산: 알려진 유화제 HLB값 검증
- Delta E 계산: CIE76 공식 정확도
- 안정성 판정: 경계값 테스트 (PASS/FAIL 기준)
- API 응답: 상태코드, 데이터 형식
```

---

## 6. DB 스키마 설계

### schema.sql (Cloudflare D1)

```sql
-- ═══════════════════════════════════════════
-- MyLab Studio — D1 Database Schema
-- ═══════════════════════════════════════════

-- 1. 원료 마스터
CREATE TABLE ingredients (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  name_ko TEXT NOT NULL,             -- 한글명
  name_inci TEXT NOT NULL,           -- INCI명
  cas_number TEXT,                   -- CAS 번호
  category TEXT,                     -- 카테고리 (유화제/보습제/방부제/...)
  phase TEXT DEFAULT 'water',        -- water/oil/other
  hlb_value REAL,                    -- HLB 값 (유화제만)
  max_usage_pct REAL,                -- 최대 사용량 (%)
  supplier TEXT,                     -- 공급사
  unit_price REAL,                   -- kg당 단가 (원)
  regulatory_status TEXT DEFAULT '{}', -- JSON: { kr: "허용", eu: "제한", cn: "금지" }
  allergen_flag INTEGER DEFAULT 0,   -- 26종 알레르겐 여부
  notes TEXT,
  created_at TEXT DEFAULT (datetime('now')),
  updated_at TEXT DEFAULT (datetime('now'))
);

-- 2. 처방 (Formula)
CREATE TABLE formulas (
  id TEXT PRIMARY KEY,               -- F-YYYYMMDD-XXX 형식
  name TEXT NOT NULL,                -- 처방명
  version TEXT DEFAULT 'v1.0',       -- 버전
  form_type TEXT,                    -- 제형 (에멀전/세럼/크림/스틱/...)
  status TEXT DEFAULT 'draft',       -- draft/review/approved/production
  target_ph_min REAL,
  target_ph_max REAL,
  total_batch_g REAL DEFAULT 1000,   -- 기본 배치 사이즈 (g)
  notes TEXT,
  created_by TEXT,
  created_at TEXT DEFAULT (datetime('now')),
  updated_at TEXT DEFAULT (datetime('now'))
);

-- 3. 처방 상세 (처방-원료 연결)
CREATE TABLE formula_ingredients (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  formula_id TEXT NOT NULL REFERENCES formulas(id),
  ingredient_id INTEGER NOT NULL REFERENCES ingredients(id),
  percentage REAL NOT NULL,          -- 배합비 (%)
  phase TEXT,                        -- water/oil/other (원료 기본값 오버라이드 가능)
  addition_order INTEGER,            -- 투입 순서
  notes TEXT
);

-- 4. 안정성 시험
CREATE TABLE stability_tests (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  formula_id TEXT NOT NULL REFERENCES formulas(id),
  condition TEXT NOT NULL,           -- 'RT' / '4C' / '25C' / '45C' / '50C'
  start_date TEXT NOT NULL,
  status TEXT DEFAULT 'in_progress', -- in_progress / pass / fail
  notes TEXT,
  created_at TEXT DEFAULT (datetime('now'))
);

-- 5. 안정성 시험 결과 (시점별)
CREATE TABLE stability_results (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  test_id INTEGER NOT NULL REFERENCES stability_tests(id),
  week_number INTEGER NOT NULL,      -- W0, W1, W2, W4, W8, W12
  delta_e REAL,                      -- 색차값
  viscosity_change_pct REAL,         -- 점도변화율 (%)
  ph_value REAL,
  appearance TEXT,                   -- 외관 메모
  photo_url TEXT,                    -- R2 사진 URL
  result TEXT,                       -- pass / warning / fail
  measured_at TEXT DEFAULT (datetime('now'))
);

-- 6. 관능평가 세션
CREATE TABLE sensory_sessions (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  formula_id TEXT NOT NULL REFERENCES formulas(id),
  session_date TEXT NOT NULL,
  is_blind INTEGER DEFAULT 0,        -- 블라인드 테스트 여부
  panel_count INTEGER DEFAULT 0,
  status TEXT DEFAULT 'open',        -- open / closed
  created_at TEXT DEFAULT (datetime('now'))
);

-- 7. 관능평가 결과 (패널별)
CREATE TABLE sensory_results (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  session_id INTEGER NOT NULL REFERENCES sensory_sessions(id),
  panel_code TEXT NOT NULL,          -- 패널 식별자 (블라인드)
  spreadability REAL,                -- 발림성 (1-10)
  absorption REAL,                   -- 흡수력 (1-10)
  stickiness REAL,                   -- 끈적임 (1-10, 낮을수록 좋음)
  fragrance_intensity REAL,          -- 향 강도 (1-10)
  moisturizing REAL,                 -- 보습감 (1-10)
  whitening REAL,                    -- 백탁 (1-10, 낮을수록 좋음)
  overall REAL,                      -- 종합 만족도 (1-10)
  comment TEXT,
  submitted_at TEXT DEFAULT (datetime('now'))
);

-- 8. 원료 호환성 규칙
CREATE TABLE compatibility_rules (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  ingredient_a TEXT NOT NULL,        -- INCI명 또는 카테고리
  ingredient_b TEXT NOT NULL,
  severity TEXT NOT NULL,            -- 'forbidden' / 'caution' / 'recommended'
  reason TEXT NOT NULL,              -- 충돌 사유
  ph_condition TEXT,                 -- pH 조건 (선택)
  source TEXT                        -- 출처
);

-- 9. 규제 데이터
CREATE TABLE regulations (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  ingredient_inci TEXT NOT NULL,
  country TEXT NOT NULL,             -- 'KR' / 'EU' / 'CN' / 'US'
  regulation_type TEXT,              -- 'banned' / 'restricted' / 'allowed'
  max_concentration REAL,            -- 최대 허용 농도 (%)
  conditions TEXT,                   -- JSON: 사용 조건
  reference TEXT,                    -- 규제 문서 참조
  updated_at TEXT DEFAULT (datetime('now'))
);

-- 10. 프로젝트
CREATE TABLE projects (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  name TEXT NOT NULL,
  description TEXT,
  status TEXT DEFAULT 'active',      -- active / completed / on_hold
  start_date TEXT,
  target_date TEXT,
  created_at TEXT DEFAULT (datetime('now'))
);

-- 11. 알림
CREATE TABLE notifications (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  type TEXT NOT NULL,                -- 'stability' / 'regulation' / 'review' / 'deadline'
  priority TEXT DEFAULT 'normal',    -- 'urgent' / 'normal' / 'low'
  title TEXT NOT NULL,
  message TEXT,
  link_url TEXT,                     -- 관련 페이지 링크
  is_read INTEGER DEFAULT 0,
  created_at TEXT DEFAULT (datetime('now'))
);

-- 12. 실험 노트
CREATE TABLE lab_notes (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  formula_id TEXT REFERENCES formulas(id),
  title TEXT NOT NULL,
  content TEXT,                      -- 마크다운 형식
  tags TEXT,                         -- JSON 배열: ["쿠션", "변색", "pH"]
  photos TEXT,                       -- JSON 배열: R2 URL 목록
  created_at TEXT DEFAULT (datetime('now')),
  updated_at TEXT DEFAULT (datetime('now'))
);

-- ═══ 인덱스 ═══
CREATE INDEX idx_fi_formula ON formula_ingredients(formula_id);
CREATE INDEX idx_fi_ingredient ON formula_ingredients(ingredient_id);
CREATE INDEX idx_st_formula ON stability_tests(formula_id);
CREATE INDEX idx_sr_test ON stability_results(test_id);
CREATE INDEX idx_ss_formula ON sensory_sessions(formula_id);
CREATE INDEX idx_reg_inci ON regulations(ingredient_inci);
CREATE INDEX idx_reg_country ON regulations(country);
CREATE INDEX idx_notif_read ON notifications(is_read);
CREATE INDEX idx_notes_formula ON lab_notes(formula_id);
```

---

## 7. 위젯별 개발 명세 (Phase 1 우선)

### 7.1 처방 계산기 (FormulaCalculator)

| 항목 | 상세 |
|------|------|
| **파일** | `src/components/widgets/FormulaCalculator/` |
| **API** | `GET/POST /api/formulas`, `GET /api/ingredients` |
| **DB** | formulas, formula_ingredients, ingredients |
| **핵심 기능** | 원료 추가/삭제, 배합비 입력, 100% 자동 보정, 배치 스케일링 |
| **계산 로직** | `lib/costCalc.ts` — 투입량 = 배합비 × 배치사이즈 / 100 |
| **UI** | 편집 가능한 테이블 + 수상/유상 탭 + 합계 바 |

### 7.2 HLB 계산기 (HlbCalculator)

| 항목 | 상세 |
|------|------|
| **파일** | `src/components/widgets/HlbCalculator/` |
| **DB** | ingredients (hlb_value 컬럼) |
| **핵심 기능** | Required HLB 계산, 유화제 혼합비 슬라이더, 실시간 그래프 |
| **계산 로직** | `lib/hlb.ts` — 혼합 HLB = Σ(HLBi × %i) / Σ(%i) |
| **데이터** | `constants/emulsifiers.ts` — 대표 유화제 50종 HLB값 내장 |

### 7.3 관능평가 폼 (SensoryEval)

| 항목 | 상세 |
|------|------|
| **파일** | `src/components/widgets/SensoryEval/` |
| **API** | `POST /api/sensory/sessions`, `POST /api/sensory/results` |
| **DB** | sensory_sessions, sensory_results |
| **핵심 기능** | 슬라이더 입력, 블라인드 모드, 레이더 차트, 통계 |
| **UI** | 모바일 친화적 슬라이더 폼 + 결과 대시보드 |

### 7.4 호환성 매트릭스 (CompatibilityMatrix)

| 항목 | 상세 |
|------|------|
| **파일** | `src/components/widgets/CompatibilityMatrix/` |
| **DB** | compatibility_rules |
| **핵심 기능** | 처방 입력 시 자동 충돌 검사, 3단계 경고 배지 |
| **데이터** | 초기 DB에 30+ 대표 충돌 조합 시드 필요 |

---

## 8. 개발 워크플로우

### 8.1 일상 개발 사이클

```
1. VS Code에서 Claude Code 사이드바 열기
2. 작업 설명 입력 (예: "처방 계산기 위젯의 배치 스케일링 기능 구현")
3. Claude가 작업 분석 → 적절한 에이전트에 위임
   - frontend-dev: UI 컴포넌트 구현
   - backend-dev: API 엔드포인트 생성  
   - formula-specialist: 계산 로직 검증
4. 병렬 실행 (worktree 격리)
5. 결과 머지 → tester가 백그라운드 테스트
6. docs-manager가 문서 자동 업데이트
```

### 8.2 새 위젯 추가 순서

```bash
# 1단계: 스키마 확인/추가
workers/src/models/migrations/00X_new_widget.sql

# 2단계: API 라우트 추가
workers/src/routes/newWidget.ts
workers/src/index.ts (라우트 등록)

# 3단계: 프론트엔드 컴포넌트
frontend/src/components/widgets/NewWidget/index.tsx

# 4단계: 커스텀 훅
frontend/src/hooks/useNewWidget.ts

# 5단계: 대시보드 등록
frontend/src/pages/Dashboard.tsx (그리드에 추가)

# 6단계: 테스트
__tests__/newWidget.test.ts

# 7단계: 문서 업데이트
docs/widgets/new-widget.md
```

### 8.3 로컬 개발 명령어

```bash
# 프론트엔드 개발 서버 (현재 localhost:5173에서 실행 중)
cd frontend && npm run dev

# Workers 로컬 개발
cd workers && wrangler dev

# D1 로컬 마이그레이션
cd workers && wrangler d1 execute mylab-db --local --file=src/models/migrations/001_initial.sql

# D1 운영 마이그레이션
cd workers && wrangler d1 execute mylab-db --file=src/models/migrations/001_initial.sql

# 전체 빌드
cd frontend && npm run build

# 배포
wrangler pages deploy frontend/dist
```

---

## 9. 배포 파이프라인

```
┌──────────────┐     ┌─────────────────┐     ┌──────────────────┐
│  VS Code     │     │  GitHub          │     │  Cloudflare      │
│  (개발)      │────▶│  (코드 저장소)   │────▶│  (자동 배포)      │
│              │     │                  │     │                  │
│  - frontend  │     │  main 브랜치     │     │  Pages: 프론트엔드│
│  - workers   │     │  push 시 트리거  │     │  Workers: API    │
│  - 테스트    │     │                  │     │  D1: 데이터베이스 │
└──────────────┘     └─────────────────┘     │  KV: 캐시        │
                                             │  R2: 파일 저장    │
                                             └──────────────────┘
```

### wrangler.toml 예시

```toml
name = "mylab-api"
main = "src/index.ts"
compatibility_date = "2024-01-01"

[[d1_databases]]
binding = "DB"
database_name = "mylab-db"
database_id = "xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx"

[[kv_namespaces]]
binding = "KV"
id = "xxxxxxxx"

[[r2_buckets]]
binding = "R2"
bucket_name = "mylab-files"
```

---

## 10. Phase별 실행 체크리스트

### Phase 1 — 핵심 실무 위젯 (3월 W3 ~ 4월 W2)

```
Week 1:
  □ 프로젝트 구조 초기화 (이 문서대로)
  □ D1 스키마 생성 (schema.sql 실행)
  □ 원료 DB 시드 데이터 (430종 n8n에서 가져오기)
  □ 유화제 HLB 데이터 시드 (50종)
  □ 호환성 충돌 규칙 시드 (30+조합)
  □ Hono API 기본 라우트 설정

Week 2:
  □ FormulaCalculator 위젯 구현
    □ 원료 추가/삭제 테이블
    □ 배합비 입력 + 100% 자동 보정
    □ 배치 스케일 업/다운
    □ 수상/유상 분리 표시
  □ HlbCalculator 위젯 구현
    □ Required HLB 계산
    □ 유화제 혼합비 슬라이더
    □ 실시간 차트

Week 3:
  □ SensoryEval 위젯 구현
    □ 평가 입력 폼 (슬라이더)
    □ 블라인드 모드
    □ 결과 레이더 차트
  □ CompatibilityMatrix 위젯 구현
    □ 충돌 자동 검사
    □ 3단계 경고 배지

Week 4:
  □ 대시보드 그리드에 새 위젯 배치
  □ 전체 통합 테스트
  □ 배포 및 QA
```

### Phase 2 — 분석 & 품질 (4월 W3 ~ 5월 W4)

```
  □ StabilityPro: 사진 타임라인 + ΔE 차트 + 자동 알림
  □ RegulatoryChecker: 국가별 규제 검사 + INCI 생성
  □ IngredientFinder: 대체 원료 추천
  □ LabNotebook: 실험 노트 에디터 + 태그 검색
```

### Phase 3 — 비즈니스 & AI (6월 W1 ~ 7월 W4)

```
  □ CostCalculator: 원가 계산 + ERP 연동
  □ ClaimMatcher: 클레임 근거 매칭
  □ AiAssistant: Claude AI 처방 상담
  □ ReportGenerator: PDF 자동 생성
```

### Phase 4 — 대시보드 완성 (8월)

```
  □ WeeklySummary: 주간 성과 요약
  □ ProjectGantt: 프로젝트 타임라인
  □ NotificationHub: 통합 알림 센터
  □ 레이아웃 편집 고도화 (드래그앤드롭)
```

---

> **이 문서를 프로젝트 루트에 `DEV_GUIDE.md`로 저장하고, VS Code에서 항상 참조하세요.**  
> CLAUDE.md와 함께 버전 관리하면 에이전트팀이 일관된 품질로 작업합니다.
