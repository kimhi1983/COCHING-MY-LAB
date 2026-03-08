#!/bin/bash
set -e

echo "=== 화장품 처방 AI API 배포 시작 ==="
echo "작업 디렉토리: $(pwd)"
echo "Python 버전: $(python3 --version)"

# 1. demo-v1 디렉토리로 이동
cd demo-v1

# 2. 가상환경 생성 및 활성화
rm -rf venv
python3 -m venv venv
source venv/bin/activate

# 3. pip 설정
pip config set global.trusted-host "pypi.org files.pythonhosted.org pypi.python.org"
pip install --upgrade pip
pip install --retries 5 --timeout 600 -r requirements.txt

# 4. 환경변수 확인
echo "=== 환경변수 확인 ==="
echo "ELASTICSEARCH_HOST: ${ELASTICSEARCH_HOST:-localhost}"
echo "ELASTICSEARCH_PORT: ${ELASTICSEARCH_PORT:-9200}"
echo "ELASTICSEARCH_USERNAME: ${ELASTICSEARCH_USERNAME:-없음}"
echo "ELASTICSEARCH_PASSWORD: ${ELASTICSEARCH_PASSWORD:+설정됨}"

# 5. 엘라스틱서치 연결 테스트 (선택사항)
echo "=== 엘라스틱서치 연결 테스트 (선택사항) ==="

# 5.1 인증 없이 테스트
echo "1. 인증 없이 테스트:"
if curl -s -k --connect-timeout 10 https://localhost:9200 > /dev/null; then
    echo "✅ 인증 없이 연결 성공"
else
    echo "⚠️ 인증 없이 연결 실패 (서버 계속 실행)"
fi

# 5.2 인증 정보로 테스트
if [ ! -z "$ELASTICSEARCH_USERNAME" ] && [ ! -z "$ELASTICSEARCH_PASSWORD" ]; then
    echo "2. 인증 정보로 테스트:"
    if curl -s -k -u "$ELASTICSEARCH_USERNAME:$ELASTICSEARCH_PASSWORD" --connect-timeout 10 https://localhost:9200 > /dev/null; then
        echo "✅ 인증 정보로 연결 성공"
    else
        echo "⚠️ 인증 정보로 연결 실패 (서버 계속 실행)"
    fi
else
    echo "⚠️ 인증 정보가 설정되지 않았습니다. (서버 계속 실행)"
fi

# 6. OpenAI 모델 확인
echo "=== OpenAI 모델 확인 ==="
if [ ! -z "$OPENAI_API_KEY" ]; then
    echo "OpenAI API 키가 설정되어 있습니다."
    echo "사용 가능한 모델 확인 중..."
    
    # Python으로 모델 목록 확인
    python3 -c "
import os
from openai import OpenAI

try:
    client = OpenAI(api_key='$OPENAI_API_KEY')
    models = client.models.list()
    
    print('사용 가능한 모델들:')
    for model in models.data:
        if 'gpt' in model.id.lower():
            print(f'  - {model.id}')
            
    # gpt-5-mini와 gpt-4o-mini 확인
    model_ids = [model.id for model in models.data]
    if 'gpt-5-mini' in model_ids:
        print('✅ gpt-5-mini 사용 가능')
    else:
        print('❌ gpt-5-mini 사용 불가')
        
    if 'gpt-4o-mini' in model_ids:
        print('✅ gpt-4o-mini 사용 가능')
    else:
        print('❌ gpt-4o-mini 사용 불가')
        
except Exception as e:
    print(f'❌ 모델 확인 실패: {e}')
"
else
    echo "⚠️ OPENAI_API_KEY가 설정되지 않았습니다."
fi

# 7. 환경변수 파일 생성
if [ ! -z "$OPENAI_API_KEY" ]; then
    cat > .env << EOF
OPENAI_API_KEY=$OPENAI_API_KEY
ELASTICSEARCH_HOST=${ELASTICSEARCH_HOST:-localhost}
ELASTICSEARCH_PORT=${ELASTICSEARCH_PORT:-9200}
ELASTICSEARCH_USERNAME=${ELASTICSEARCH_USERNAME:-}
ELASTICSEARCH_PASSWORD=${ELASTICSEARCH_PASSWORD:-}
EOF
    echo "환경변수 파일 생성 완료"
    cat .env
fi

# 7. 기존 프로세스 정리
if [ -f "api.pid" ]; then
    PID=$(cat api.pid)
    if ps -p $PID > /dev/null 2>&1; then
        echo "기존 API 서버 종료 중..."
        kill $PID
        sleep 2
    fi
    rm -f api.pid
fi

# 8. API 서버 시작 (엘라스틱서치 연결 실패해도 계속 실행)
echo "API 서버 시작 중... (엘라스틱서치 연결 실패해도 서버 계속 실행)"
# setsid와 nohup을 함께 사용하여 Jenkins 세션과 완전히 분리
setsid nohup python cosmetic_formulator_api_optimized.py > api.log 2>&1 &
echo $! > api.pid

# 9. 서버 상태 확인
sleep 10
echo "=== 서버 상태 확인 ==="
if [ -f "api.pid" ]; then
    PID=$(cat api.pid)
    if ps -p $PID > /dev/null 2>&1; then
        echo "✅ API 서버 실행 중 (PID: $PID)"
        echo "📊 서버 로그 (최근 20줄):"
        tail -20 api.log
        echo ""
        echo "🔍 헬스 체크:"
        curl -s http://localhost:28632/health | head -5 || echo "헬스 체크 실패"
        
        # Jenkins 세션이 종료되어도 서버가 계속 실행되도록 설정
        echo "🔄 Jenkins 세션과 프로세스 분리 중..."
        disown $PID
        echo "✅ 프로세스 분리 완료 - Jenkins 종료되어도 서버 계속 실행"
    else
        echo "❌ API 서버 중지됨"
        echo "📊 오류 로그 (최근 50줄):"
        tail -50 api.log
        exit 1
    fi
else
    echo "❌ PID 파일 없음"
    exit 1
fi
echo "=== 배포 완료 ==="
echo "✅ API 서버가 정상적으로 실행 중입니다."
echo "📖 API 문서: http://localhost:28632/docs"
echo "🔍 헬스 체크: http://localhost:28632/health"
echo "⚠️ 엘라스틱서치 연결 실패해도 서버는 정상 작동합니다."
echo ""
echo "=== 서버 관리 명령어 ==="
echo "서버 상태 확인: ./manage_server.sh status"
echo "서버 중지: ./manage_server.sh stop"
echo "서버 재시작: ./manage_server.sh restart"
echo "실시간 로그: ./manage_server.sh logs"
echo ""
echo "🔄 Jenkins 세션이 종료되어도 서버는 계속 실행됩니다!"

