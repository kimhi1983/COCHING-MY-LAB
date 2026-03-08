# 🧴 화장품 처방 AI (Cosmetic Formulation AI)

화장품 전성분 목록을 바탕으로 전문적인 화장품 처방서를 생성하는 AI 프로그램입니다.

## ✨ 주요 기능

- **Phase별 원료 분류**: A상(수상), B상(유상), C상(첨가/보습), D상(특수성분), E상(보존/완료)
- **정확한 배합비율**: 소수점 단위까지 정밀한 배합비율 제공
- **상세한 제조공정**: 온도, 시간, 주의사항을 포함한 단계별 제조 가이드
- **품질관리 기준**: pH, 외관, 안정성 테스트 기준 제시
- **구조화된 JSON 출력**: 체계적이고 활용 가능한 데이터 형태
- **🔗 엘라스틱서치 연동**: 성분 데이터베이스에서 상세 정보 조회 및 AI 모델에 제공

## 🛠 기술 스택

- **Python**: 메인 프로그래밍 언어
- **LangChain**: LLM 체인 관리 및 프롬프트 엔지니어링
- **OpenAI GPT**: 화장품 처방 생성을 위한 언어모델
- **Pydantic**: JSON 스키마 강제화 및 데이터 검증
- **Elasticsearch**: 성분 데이터베이스 검색 및 정보 조회

## 📦 설치 및 실행

### 1. 프로젝트 클론
```bash
git clone <repository-url>
cd Coching-Cosmetic-AI-V1
```

### 2. 가상환경 생성 및 활성화
```bash
# Windows
python -m venv venv
.\venv\Scripts\Activate.ps1

# macOS/Linux
python -m venv venv
source venv/bin/activate
```

### 3. 패키지 설치
```bash
pip install -r requirements.txt
```

### 4. 환경변수 설정
`.env` 파일을 생성하고 필요한 API 키를 설정하세요:
```
# OpenAI API 설정
OPENAI_API_KEY=your_openai_api_key_here

# 엘라스틱서치 연결 설정 (선택사항)
ELASTICSEARCH_HOST=localhost
ELASTICSEARCH_PORT=9200
ELASTICSEARCH_USERNAME=
ELASTICSEARCH_PASSWORD=
```

## 🚀 사용 방법

### 옵션 1: 기본 테스트
```bash
cd llm-test
python test_llm.py
```

### 옵션 2: 사용자 입력 모드
```bash
cd llm-test
python cosmetic_formulator.py
```

### 옵션 3: 데모 모드 (여러 테스트 케이스)
```bash
cd llm-test
python demo_formulator.py
```

### 옵션 4: 엘라스틱서치 연동 모드
```bash
cd llm-test
python cosmetic_formulator_with_elasticsearch.py
```

### 옵션 5: 엘라스틱서치 연동 데모
```bash
cd llm-test
python demo_elasticsearch_formulator.py
```

### 옵션 6: HTTP API 서버
```bash
cd llm-test
python run_api_server.py
```

### 옵션 7: API 클라이언트 테스트
```bash
cd llm-test
python api_client_example.py
```

## 🌐 HTTP API 사용법

### API 서버 실행
```bash
cd llm-test
python run_api_server.py
```

### API 엔드포인트

#### 1. 헬스 체크
```bash
GET http://localhost:8000/health
```

#### 2. 화장품 처방 생성
```bash
POST http://localhost:8000/formula
Content-Type: application/json

{
  "ingredients": [
    {
      "id": 2700,
      "repName": "정제수",
      "repNameEn": "Water"
    },
    {
      "id": 4445,
      "repName": "글리세린",
      "repNameEn": "Glycerin"
    },
    {
      "id": 1880,
      "repName": "알부틴",
      "repNameEn": "Arbutin"
    }
  ],
  "formulation": "미백세럼",
  "direction": "집중 미백 및 피부톤 개선",
  "model_name": "gpt-5-mini",
  "temperature": 0.3
}
```

#### 파라미터 설명

- **ingredients**: 성분 목록 (필수)
  - **id**: 엘라스틱서치 ID (선택사항)
  - **repName**: 대표명 (한글, 필수)
  - **repNameEn**: 대표명 (영문, 필수)
- **formulation**: 제형 (필수)
- **direction**: 개발방향 (필수)
- **model_name**: AI 모델명 (선택사항, 기본값: "gpt-5-mini")
- **temperature**: AI 모델 온도 설정 (선택사항, 기본값: 0.3, 범위: 0.0-2.0)

#### 3. 개별 성분 검색
```bash
GET http://localhost:8000/ingredients/search/정제수
```

#### 4. 여러 성분 일괄 검색
```bash
GET http://localhost:8000/ingredients/batch?ingredients=정제수,글리세린,니아신아마이드
```

### API 문서
- **Swagger UI**: http://localhost:8000/docs
- **ReDoc**: http://localhost:8000/redoc

## 📋 출력 예시

```json
{
  "overview": "미백 및 주름 개선 기능을 갖춘 수분크림 처방서입니다.",
  "total_percentage": 100.0,
  "phases": [
    {
      "phase_name": "A상_수상",
      "temperature": "상온",
      "ingredients": [
        {
          "name": "정제수",
          "percentage": 60.0,
          "function": "기초 용매"
        }
      ]
    }
  ],
  "detailed_process": [
    {
      "step_number": 1,
      "phase": "A",
      "description": "정제수와 글리세린을 혼합합니다.",
      "temperature": "상온",
      "time": "10분",
      "notes": "균일하게 혼합하세요."
    }
  ],
  "quality_control": {
    "ph_range": "5.0 - 6.5",
    "appearance": "흰색 크림",
    "stability_test": ["온도안정성", "광안정성"]
  }
}
```

## 📁 프로젝트 구조

```
Coching-Cosmetic-AI-V1/
├── llm-test/
│   ├── test_llm.py                              # 기본 테스트 파일
│   ├── cosmetic_formulator.py                   # 사용자 입력 프로그램
│   ├── demo_formulator.py                       # 데모 실행 파일
│   ├── cosmetic_formulator_with_elasticsearch.py # 엘라스틱서치 연동 프로그램
│   ├── demo_elasticsearch_formulator.py         # 엘라스틱서치 연동 데모
│   ├── cosmetic_formulator_api.py               # HTTP API 서버
│   ├── run_api_server.py                        # API 서버 실행 스크립트
│   └── api_client_example.py                    # API 클라이언트 예제
├── venv/                     # 가상환경 (Git에서 제외)
├── .gitignore               # Git 무시 파일 목록
├── requirements.txt         # Python 패키지 의존성
└── README.md               # 프로젝트 설명서
```

## ⚠️ 주의사항

1. **API 키 보안**: `.env` 파일을 절대 Git에 커밋하지 마세요
2. **가상환경**: 반드시 가상환경을 활성화한 후 실행하세요
3. **인터넷 연결**: OpenAI API 호출을 위해 안정된 인터넷 연결이 필요합니다
4. **엘라스틱서치**: 엘라스틱서치 연동 기능을 사용하려면 엘라스틱서치 서버가 실행 중이어야 합니다

## 🔄 개발 단계

- [x] 1단계: LLM 기본 호출 테스트
- [x] 2단계: JSON Schema 강제화
- [x] 3단계: 사용자 입력 → LLM → 결과 반환
- [x] 4단계: 엘라스틱서치 연동 및 성분 정보 조회
- [x] 5단계: HTTP API 서버 구현
- [ ] 6단계: MCP 연동
- [ ] 7단계: 고도화 (웹 인터페이스 등)

## 📝 라이선스

이 프로젝트는 MIT 라이선스 하에 배포됩니다.

## 📝 로깅 시스템

### 로그 파일 관리
- **데일리 롤링**: 매일 자정에 새로운 로그 파일 생성
- **자동 정리**: 30일 이전 로그 파일 자동 삭제
- **UTF-8 인코딩**: 한글 로그 메시지 완벽 지원

### 로그 파일 위치
```
logs/
├── cosmetic_api.log              # 현재 로그 파일
├── cosmetic_api.log.2024-10-21   # 롤링된 로그 파일들
└── ...
```

### 로그 관리 명령어
```bash
# 로그 파일 통계 확인
python manage_logs.py show

# 오래된 로그 파일 정리 (30일 이전)
python manage_logs.py clean

# 최신 로그 50줄 확인
python manage_logs.py tail

# 최신 로그 100줄 확인
python manage_logs.py tail --lines 100

# 7일 이전 로그 정리
python manage_logs.py clean --days 7
```

### 로그 레벨 설정
- **INFO**: 일반적인 정보 메시지
- **WARNING**: 경고 메시지
- **ERROR**: 오류 메시지
- **DEBUG**: 디버깅 정보 (개발 시에만 사용)

## 🤝 기여하기

버그 리포트, 기능 제안, 풀 리퀘스트를 환영합니다!

---

**개발자**: Cosmetic AI Development Team  
**최종 업데이트**: 2025년 10월
