#!/bin/bash

# 화장품 처방 AI API 서버 관리 스크립트

PID_FILE="api.pid"
LOG_FILE="api.log"
SERVICE_NAME="화장품 처방 AI API"

case "$1" in
    start)
        echo "=== $SERVICE_NAME 시작 ==="
        if [ -f "$PID_FILE" ]; then
            PID=$(cat "$PID_FILE")
            if ps -p $PID > /dev/null 2>&1; then
                echo "⚠️ 서버가 이미 실행 중입니다 (PID: $PID)"
                exit 1
            else
                rm -f "$PID_FILE"
            fi
        fi
        
        # 가상환경 활성화
        source venv/bin/activate
        
        # 서버 시작 (Jenkins 세션과 완전히 분리)
        setsid nohup python cosmetic_formulator_api_optimized.py > "$LOG_FILE" 2>&1 &
        echo $! > "$PID_FILE"
        
        # 시작 확인
        sleep 5
        if [ -f "$PID_FILE" ]; then
            PID=$(cat "$PID_FILE")
            if ps -p $PID > /dev/null 2>&1; then
                echo "✅ 서버 시작 완료 (PID: $PID)"
                echo "📖 API 문서: http://localhost:28632/docs"
                echo "🔍 헬스 체크: http://localhost:28632/health"
                echo "📝 로그 확인: tail -f $LOG_FILE"
            else
                echo "❌ 서버 시작 실패"
                echo "📊 오류 로그:"
                tail -20 "$LOG_FILE"
                exit 1
            fi
        else
            echo "❌ PID 파일 생성 실패"
            exit 1
        fi
        ;;
        
    stop)
        echo "=== $SERVICE_NAME 중지 ==="
        if [ -f "$PID_FILE" ]; then
            PID=$(cat "$PID_FILE")
            if ps -p $PID > /dev/null 2>&1; then
                echo "🛑 서버 중지 중... (PID: $PID)"
                kill $PID
                sleep 3
                if ps -p $PID > /dev/null 2>&1; then
                    echo "⚠️ 강제 종료 중..."
                    kill -9 $PID
                fi
                echo "✅ 서버 중지 완료"
            else
                echo "⚠️ 서버가 실행 중이 아닙니다"
            fi
            rm -f "$PID_FILE"
        else
            echo "⚠️ PID 파일이 없습니다"
        fi
        ;;
        
    restart)
        echo "=== $SERVICE_NAME 재시작 ==="
        $0 stop
        sleep 2
        $0 start
        ;;
        
    status)
        echo "=== $SERVICE_NAME 상태 ==="
        if [ -f "$PID_FILE" ]; then
            PID=$(cat "$PID_FILE")
            if ps -p $PID > /dev/null 2>&1; then
                echo "✅ 서버 실행 중 (PID: $PID)"
                echo "📊 프로세스 정보:"
                ps -p $PID -o pid,ppid,cmd,etime
                echo ""
                echo "🔍 헬스 체크:"
                curl -s http://localhost:28632/health | head -5 || echo "헬스 체크 실패"
            else
                echo "❌ 서버 중지됨 (PID 파일은 존재하지만 프로세스 없음)"
                rm -f "$PID_FILE"
            fi
        else
            echo "❌ 서버 중지됨 (PID 파일 없음)"
        fi
        ;;
        
    logs)
        echo "=== $SERVICE_NAME 로그 ==="
        if [ -f "$LOG_FILE" ]; then
            tail -f "$LOG_FILE"
        else
            echo "❌ 로그 파일이 없습니다: $LOG_FILE"
        fi
        ;;
        
    *)
        echo "사용법: $0 {start|stop|restart|status|logs}"
        echo ""
        echo "명령어 설명:"
        echo "  start   - 서버 시작"
        echo "  stop    - 서버 중지"
        echo "  restart - 서버 재시작"
        echo "  status  - 서버 상태 확인"
        echo "  logs    - 실시간 로그 보기"
        exit 1
        ;;
esac
