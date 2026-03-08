# COCHING-MY-LAB

화장품 원료 매칭 플랫폼 — 원료사(Partner)와 화장품 개발사(User)를 연결하고, AI가 화장품 처방을 자동 생성합니다.

## Quick Start (다른 컴퓨터에서 시작하기)

### 1. 클론
```bash
git clone https://github.com/kimhi1983/COCHING-MY-LAB.git
cd COCHING-MY-LAB
```

### 2. Claude Code 설정
```bash
# Claude Code CLI 설치 (Node.js 18+ 필요)
npm install -g @anthropic-ai/claude-code

# 프로젝트 폴더에서 Claude Code 실행
claude
```

> `.claude/agents/`에 12개 에이전트 팀이 자동 로드됩니다.

### 3. 개별 프로젝트 실행

#### Frontend (Vue 2 앱)
```bash
# BO (관리자)
cd Coching-BO-Vue && npm install && npm run serve

# User (사용자)
cd Coching-User-Vue && npm install && npm run serve

# Partner (원료사)
cd Coching-Partner-Vue && npm install && npm run serve
```

#### Backend (Spring Boot)
```bash
# BO 백엔드
cd Coching-BO-Web && ./gradlew bootRun

# User 백엔드
cd Coching-User-Web && ./gradlew bootRun

# Partner 백엔드
cd Coching-Partner-Web && ./gradlew bootRun

# Elasticsearch API
cd ERNS-ES-API && ./gradlew bootRun
```

#### AI 처방 서버
```bash
cd Coching-Cosmetic-AI-V1/demo-v1
pip install -r requirements.txt
uvicorn main:app --reload
```

## 프로젝트 구조

```
COCHING-MY-LAB/
├── Coching-BO-Vue/           # 관리자 프론트엔드 (Vue 2)
├── Coching-BO-Web/           # 관리자 백엔드 (Spring Boot)
├── Coching-User-Vue/         # 사용자 프론트엔드 (Vue 2)
├── Coching-User-Web/         # 사용자 백엔드 (Spring Boot)
├── Coching-Partner-Vue/      # 원료사 프론트엔드 (Vue 2)
├── Coching-Partner-Web/      # 원료사 백엔드 (Spring Boot)
├── Coching-Cosmetic-AI-V1/   # AI 처방 생성 (FastAPI + LangChain)
├── ERNS-ES-API/              # 원료 검색 엔진 (Elasticsearch)
├── .claude/agents/           # 12-Agent Ultimate Team
├── .claude/commands/         # 슬래시 커맨드
├── CLAUDE.md                 # 에이전트 팀 헌법
└── docs/                     # 문서
```

## 기술 스택

| 레이어 | 기술 |
|--------|------|
| Frontend | Vue 2.6 + Vuex + Vue Router + Bootstrap-Vue |
| Backend | Spring Boot 2.5.4 + Java 8 + MyBatis |
| Database | PostgreSQL |
| 검색 | Elasticsearch 8.15 |
| AI | Python FastAPI + LangChain + OpenAI GPT |
| 인증 | JWT + JWE + Spring Security |

## 에이전트 팀 (12-Agent Ultimate)

이 프로젝트는 Claude Code의 에이전트 시스템으로 관리됩니다.

| 레이어 | 에이전트 | 모델 | 역할 |
|--------|---------|------|------|
| 전략 | PM, Architect, UX | Opus | 분석·설계·디자인 |
| 구현 | Backend, Frontend, Automation | Sonnet | 코드 작성 (worktree 격리) |
| 검증 | QA, Security | Opus | Triple Quality Gate |
| 지원 | DevOps, Explorer, Docs | Sonnet/Haiku | 빌드·탐색·문서화 |

## 환경 요구사항

- **Node.js** 18+ (Vue 프론트엔드)
- **Java** 8+ (Spring Boot 백엔드)
- **Python** 3.10+ (AI 서버)
- **PostgreSQL** 14+
- **Elasticsearch** 8.x
- **Claude Code CLI** (에이전트 팀 사용 시)
