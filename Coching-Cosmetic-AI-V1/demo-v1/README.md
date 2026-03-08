# 화장품 처방 AI API 데모 v1

이 폴더는 화장품 처방 AI API의 실행에 필요한 핵심 파일들을 포함합니다.

## 📁 파일 구성

- `cosmetic_formulator_api_optimized.py` - 메인 API 서버
- `cosmetic_formulator_with_elasticsearch.py` - 엘라스틱서치 연동 AI 클래스
- `log_config.py` - 로깅 설정
- `manage_logs.py` - 로그 관리 도구
- `api_client_new_format.py` - API 테스트 클라이언트
- `run_api_server.py` - API 서버 실행 스크립트
- `requirements.txt` - 필요한 패키지 목록

## 🚀 빠른 시작

### 1. 의존성 설치
```bash
pip install -r requirements.txt
```

### 2. API 서버 실행
```bash
python run_api_server.py
```

### 3. API 테스트
```bash
python api_client_new_format.py
```

## 📊 API 엔드포인트

- `POST /formula` - 화장품 처방 생성
- `GET /health` - 서버 상태 확인
- `GET /ingredients/search/{name}` - 개별 성분 검색
- `GET /ingredients/batch` - 여러 성분 일괄 검색

## 📝 로그 관리

```bash
# 로그 파일 통계 확인
python manage_logs.py show

# 오래된 로그 파일 정리
python manage_logs.py clean

# 최신 로그 확인
python manage_logs.py tail
```

## 🔧 설정

- 기본 AI 모델: `gpt-5-mini`
- 기본 온도: `0.3`
- 로그 파일: `logs/cosmetic_api.log`
- API 포트: `8000`

## 📖 상세 문서

전체 프로젝트 문서는 상위 디렉토리의 `README.md`를 참조하세요.


## 수동실행행
cd demo-v1
source venv/bin/activate
nohup python cosmetic_formulator_api_optimized.py > api.log 2>&1 &