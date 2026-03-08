"""
화장품 처방 AI API 서버 실행 스크립트
"""
import uvicorn
import os
from dotenv import load_dotenv

def main():
    """API 서버 실행"""
    # .env 파일 로드
    load_dotenv()
    
    # OpenAI API 키 확인
    api_key = os.getenv("OPENAI_API_KEY")
    if not api_key:
        print("❌ ERROR: OpenAI API 키가 설정되지 않았습니다.")
        print("다음 중 하나의 방법으로 API 키를 설정하세요:")
        print("1. .env 파일에 OPENAI_API_KEY=your_api_key 추가")
        print("2. 환경변수로 설정: set OPENAI_API_KEY=your_api_key (Windows)")
        print("3. 또는 $env:OPENAI_API_KEY='your_api_key' (PowerShell)")
        return
    
    print("화장품 처방 AI API 서버 시작")
    print("=" * 50)
    print("API 문서: http://localhost:8000/docs")
    print("헬스 체크: http://localhost:8000/health")
    print("테스트 클라이언트: python api_client_example.py")
    print("=" * 50)
    
    # 서버 실행
    uvicorn.run(
        "cosmetic_formulator_api:app",
        host="0.0.0.0",
        port=8000,
        reload=True,
        log_level="info"
    )

if __name__ == "__main__":
    main()
