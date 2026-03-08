from fastapi import FastAPI, HTTPException, BackgroundTasks
from fastapi.middleware.cors import CORSMiddleware
from pydantic import BaseModel, Field
from typing import List, Optional, Dict, Any, Union
import os
import json
import time
import asyncio
from datetime import datetime
from dotenv import load_dotenv
from elasticsearch import Elasticsearch
import re
from concurrent.futures import ThreadPoolExecutor
import threading
import logging
from logging.handlers import TimedRotatingFileHandler

# 기존 모듈에서 필요한 클래스들 import
from cosmetic_formulator_with_elasticsearch import (
    ElasticsearchIngredientSearcher,
    CosmeticFormulatorWithES
)
# 스키마는 별도 모듈에서 import
from schemas import (
    Ingredient,
    Phase,
    DetailedProcess,
    QualityControl,
    FormulaResponse
)

# .env 로드
load_dotenv()

# OpenAI API 키 확인
api_key = os.getenv("OPENAI_API_KEY")
if not api_key:
    raise ValueError("OpenAI API 키가 설정되지 않았습니다. .env 파일에 OPENAI_API_KEY를 설정하세요.")

# ===== 로깅 설정 =====
from log_config import setup_logging

# 로깅 초기화 (데일리 롤링, 30일 보관)
logger = setup_logging(log_level="INFO", log_dir="logs", max_days=30)

# FastAPI 앱 초기화
app = FastAPI(
    title="화장품 처방 AI API (최적화)",
    description="엘라스틱서치 연동 화장품 처방 생성 API - 성능 최적화 버전",
    version="2.0.0"
)

# CORS 설정
app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

# ===== 성능 최적화 설정 =====
# 스레드 풀 크기 설정 (CPU 코어 수 * 2)
MAX_WORKERS = min(32, (os.cpu_count() or 1) * 2)
executor = ThreadPoolExecutor(max_workers=MAX_WORKERS)

# ===== API 요청/응답 모델 정의 =====
class IngredientInput(BaseModel):
    """성분 입력 모델"""
    id: Optional[int] = Field(None, description="엘라스틱서치 ID")
    repName: str = Field(..., description="대표명 (한글)")
    repNameEn: str = Field(..., description="대표명 (영문)")
    esId: Optional[int] = Field(0, description="엘라스틱서치 ID")

class FormulaRequestBase(BaseModel):
    """화장품 처방 생성 요청 모델 공통 베이스 클래스"""
    direction: str = Field(..., description="개발방향")
    model_name: str = Field(default="gpt-5-mini", description="사용할 AI 모델명")
    temperature: float = Field(default=0.3, description="AI 모델 온도 설정 (0.0-2.0)")
    prompt_type: str = Field(default="default", description="프롬프트 타입 (default, detailed, concise, safety_focused, efficiency_focused)")

class FormulaRequestV1(FormulaRequestBase):
    """화장품 처방 생성 요청 모델 (v1: 성분 목록, 제형, 개발방향 입력)"""
    ingredients: List[IngredientInput] = Field(..., description="성분 목록")
    formulation: str = Field(..., description="제형")

class FormulaRequestV2(FormulaRequestBase):
    """화장품 처방 생성 요청 모델 (v2: 개발방향만 입력)"""
    pass

class FormulaApiResponse(BaseModel):
    """화장품 처방 API 응답 모델"""
    resultCode: str = Field(..., description="처리 성공 여부")
    resultFailMessage: str = Field(..., description="처리 결과 메시지")
    resultData: Optional[FormulaResponse] = Field(None, description="처방 데이터")
    processing_time: float = Field(..., description="처리 시간 (초)")
    elasticsearch_connected: bool = Field(..., description="엘라스틱서치 연결 상태")
    ingredients_found: int = Field(..., description="엘라스틱서치에서 찾은 성분 수")
    total_ingredients: int = Field(..., description="총 성분 수")
    request_id: str = Field(..., description="요청 ID")
    request_data: Optional[Union[FormulaRequestV1, FormulaRequestV2]] = Field(None, description="요청 파라미터 데이터")

class HealthResponse(BaseModel):
    """헬스 체크 응답 모델"""
    status: str = Field(..., description="서비스 상태")
    elasticsearch_connected: bool = Field(..., description="엘라스틱서치 연결 상태")
    timestamp: str = Field(..., description="체크 시간")
    active_requests: int = Field(..., description="현재 처리 중인 요청 수")
    max_workers: int = Field(..., description="최대 워커 수")

# ===== 전역 변수 =====
formulator = None
es_searcher = None
active_requests = 0
request_lock = threading.Lock()

# ===== 성능 모니터링 =====
def increment_active_requests():
    """활성 요청 수 증가"""
    global active_requests
    with request_lock:
        active_requests += 1

def decrement_active_requests():
    """활성 요청 수 감소"""
    global active_requests
    with request_lock:
        active_requests = max(0, active_requests - 1)

# ===== 초기화 함수 =====
def initialize_services():
    """서비스 초기화"""
    global formulator, es_searcher
    
    try:
        # 엘라스틱서치 검색기 초기화
        es_searcher = ElasticsearchIngredientSearcher()
        es_connected = es_searcher.connect()
        
        # 처방 AI 초기화
        formulator = CosmeticFormulatorWithES()
        
        logger.info("✅ 서비스 초기화 완료")
        logger.info(f"   - 엘라스틱서치 연결: {'✅' if es_connected else '⚠️ (연결 실패, 서버 계속 실행)'}")
        logger.info(f"   - AI 모델: {formulator.model_name}")
        logger.info(f"   - 최대 워커 수: {MAX_WORKERS}")
        
        # 엘라스틱서치 연결 실패해도 서버는 계속 실행
        return True  # 항상 True 반환하여 서버 계속 실행
        
    except Exception as e:
        logger.error(f"⚠️ 서비스 초기화 중 오류: {e}")
        logger.info("⚠️ 엘라스틱서치 없이 서버 계속 실행")
        
        # 오류 발생해도 기본 서비스는 초기화
        try:
            formulator = CosmeticFormulatorWithES()
            logger.info("✅ 기본 AI 서비스 초기화 완료")
        except Exception as ai_error:
            logger.error(f"❌ AI 서비스 초기화 실패: {ai_error}")
        
        return True  # 항상 True 반환하여 서버 계속 실행

# ===== 결과 검증 함수 =====
def validate_formula_result(result) -> None:
    """
    AI 모델 결과를 검증하는 함수
    
    Args:
        result: 검증할 FormulaResponse 객체
        
    Raises:
        Exception: 검증 실패 시 예외 발생
    """
    # 결과가 None인지 확인
    if result is None:
        error_msg = "처방 생성에 실패했습니다. (결과가 None입니다)"
        logger.error(f"❌ {error_msg}")
        raise Exception(error_msg)
    
    # 결과 타입 확인
    if not isinstance(result, FormulaResponse):
        error_msg = f"처방 생성 결과가 올바른 형식이 아닙니다. 타입: {type(result)}"
        logger.error(f"❌ {error_msg}")
        logger.error(f"   결과 내용: {str(result)[:500]}")  # 처음 500자만 로깅
        raise Exception(error_msg)
    
    # 필수 필드 검증
    missing_fields = []
    if not hasattr(result, 'overview') or not result.overview:
        missing_fields.append('overview')
    if not hasattr(result, 'total_percentage'):
        missing_fields.append('total_percentage')
    if not hasattr(result, 'phases') or not result.phases:
        missing_fields.append('phases')
    if not hasattr(result, 'detailed_process') or not result.detailed_process:
        missing_fields.append('detailed_process')
    if not hasattr(result, 'quality_control') or not result.quality_control:
        missing_fields.append('quality_control')
    if not hasattr(result, 'precautions') or not result.precautions:
        missing_fields.append('precautions')
    if not hasattr(result, 'pros') or not result.pros:
        missing_fields.append('pros')
    if not hasattr(result, 'cons') or not result.cons:
        missing_fields.append('cons')
    
    if missing_fields:
        error_msg = f"처방 생성 결과에 필수 필드가 누락되었습니다: {', '.join(missing_fields)}"
        logger.error(f"❌ {error_msg}")
        logger.error(f"   결과 객체 타입: {type(result)}")
        logger.error(f"   결과 객체 속성: {dir(result)}")
        # result를 dict로 변환하여 로깅
        try:
            result_dict = result.model_dump() if hasattr(result, 'model_dump') else str(result)
            logger.error(f"   결과 내용 (일부): {str(result_dict)[:1000]}")
        except:
            logger.error(f"   결과 내용: {str(result)[:1000]}")
        raise Exception(error_msg)
    
    # total_percentage가 문자열인 경우 처리
    if isinstance(result.total_percentage, str):
        logger.warning(f"⚠️ total_percentage가 문자열입니다: {result.total_percentage}")
        # '%' 제거 후 숫자로 변환 시도
        try:
            cleaned = result.total_percentage.replace('%', '').strip()
            result.total_percentage = float(cleaned)
            logger.info(f"✅ total_percentage 변환 성공: {result.total_percentage}")
        except Exception as conv_error:
            error_msg = f"total_percentage를 숫자로 변환할 수 없습니다: {result.total_percentage}, 오류: {conv_error}"
            logger.error(f"❌ {error_msg}")
            raise Exception(error_msg)

# ===== 오류 메시지 생성 함수 =====
def create_detailed_error_message(error_msg: str) -> str:
    """
    오류 메시지를 기반으로 상세한 오류 메시지를 생성하는 함수
    
    Args:
        error_msg: 기본 오류 메시지
        
    Returns:
        str: 상세한 오류 메시지
    """
    detailed_error = f"처방 생성 중 오류가 발생했습니다: {error_msg}"
    if "필수 필드가 누락" in error_msg:
        detailed_error += " (LLM이 완전한 응답을 생성하지 못했습니다. 프롬프트나 모델 설정을 확인하세요.)"
    elif "total_percentage" in error_msg:
        detailed_error += " (배합비율 형식 오류입니다. 숫자만 반환되도록 프롬프트를 수정하세요.)"
    elif "결과가 None" in error_msg:
        detailed_error += " (모델 호출이 실패했습니다. 네트워크나 API 키를 확인하세요.)"
    
    return detailed_error

# ===== 비동기 처방 생성 함수 v1 =====
async def create_formula_async_v1(
    ingredients: List[IngredientInput],
    formulation: str,
    direction: str,
    request_id: str,
    request_data: FormulaRequestV1 = None,
    model_name: str = "gpt-5-mini",
    temperature: float = 0.3,
    prompt_type: str = "default"
):
    """비동기 처방 생성 (새로운 입력 형식)"""
    increment_active_requests()

    # 응답 데이터 변수 초기화
    start_time = time.time()
    response_data = None
    result_code = "9999"
    result_message = ""
    es_connected = False
    ingredients_found = 0
    total_ingredients = 0
    
    try:
        # 입력 검증
        if not ingredients:
            raise ValueError("성분 목록이 비어있습니다.")
        if not formulation.strip():
            raise ValueError("제형이 비어있습니다.")
        if not direction.strip():
            raise ValueError("개발방향이 비어있습니다.")

        print("📝 1단계: 성분 정보 수집 준비 중...")
        
        # 엘라스틱서치 연결 상태 확인
        if es_searcher and es_searcher.es:
            try:
                es_connected = es_searcher.es.ping()
            except:
                es_connected = False
        
        # 성분 정보 수집 및 엘라스틱서치 검색
        ingredient_details: List[Dict[str, Any]] = []
        total_ingredients = len(ingredients)

        if es_connected and es_searcher:
            print(f"🔍 엘라스틱서치에서 {len(ingredients)}개 성분 정보 검색 중...")
            for idx, ingredient in enumerate(ingredients):
                print(f"🔍 '{ingredient.repName}' 성분 검색 중...")
                ingredient_payload = {
                    "id": ingredient.id,
                    "repName": ingredient.repName,
                    "repNameEn": ingredient.repNameEn
                }
                detail, found, found_id = await asyncio.get_event_loop().run_in_executor(
                    executor,
                    formulator.fetch_single_ingredient_detail,
                    ingredient_payload
                )
                ingredient.esId = found_id
                ingredient_details.append(detail)
                if found:
                    ingredients_found += 1
                    try:
                        if request_data and 0 <= idx < len(request_data.ingredients):
                            request_data.ingredients[idx].esId = found_id
                    except Exception:
                        pass
        else:
            # 연결되지 않은 경우 기본 정보를 구성
            for ingredient in ingredients:
                ingredient_payload = {
                    "id": ingredient.id,
                    "repName": ingredient.repName,
                    "repNameEn": ingredient.repNameEn
                }
                ingredient_details.append(
                    formulator.build_default_ingredient_detail_from_input(ingredient_payload)
                )
        
        print(f"✅ {ingredients_found}/{len(ingredients)}개 성분 정보를 찾았습니다.")
        print("✅ 성분 정보 수집 완료")
        
        # AI 모델 설정 적용 (프롬프트 타입 포함)
        formulator.set_model_config(model_name, temperature, prompt_type)
        
        # AI 모델 호출 (스레드 풀에서 실행)
        print("📝 2단계: 프롬프트 준비 중...")
        logger.info(f"🤖 AI 모델 호출 시작: {model_name} (temperature: {temperature})")
        
        try:
            result = await asyncio.get_event_loop().run_in_executor(
                executor, formulator.create_formula, ingredient_details, formulation, direction
            )
            print("✅ 프롬프트 준비 완료")
            
            # 결과 검증
            validate_formula_result(result)

            logger.info(f"✅ 처방 생성 성공: {len(result.phases)}개 Phase, {len(result.detailed_process)}개 공정")
            
            # 성공 응답 데이터 할당
            response_data = result
            result_code = "0000"
            result_message = "처방이 성공적으로 생성되었습니다."
            
        except Exception as model_error:
            error_msg = f"AI 모델 호출 중 오류 발생: {str(model_error)}"
            logger.error(f"❌ {error_msg}")
            logger.exception("상세 오류 정보:")  # 전체 스택 트레이스 로깅
            
            # 원본 예외를 다시 발생시켜 상위 예외 처리에서 처리하도록
            raise Exception(error_msg) from model_error
            
    except Exception as e:
        error_msg = str(e)
        logger.error(f"❌ 처방 생성 실패: {error_msg}")
        logger.exception("전체 오류 스택:")
        
        # 오류 응답 데이터 할당
        result_code = "9999"
        result_message = create_detailed_error_message(error_msg)
        
    finally:
        processing_time = time.time() - start_time
        
        # 최종 응답 생성 및 반환
        response = FormulaApiResponse(
            resultCode=result_code,
            resultFailMessage=result_message,
            resultData=response_data,
            processing_time=round(processing_time, 2),
            elasticsearch_connected=es_connected,
            ingredients_found=ingredients_found,
            total_ingredients=total_ingredients,
            request_id=request_id,
            request_data=request_data
        )
        decrement_active_requests()
        return response

# ===== API 엔드포인트 =====

# ===== 비동기 처방 생성 함수 v2 =====
async def create_formula_async_v2(
    direction: str,
    request_id: str,
    request_data: FormulaRequestV2 = None,
    model_name: str = "gpt-5-mini",
    temperature: float = 0.3,
    prompt_type: str = "default"
):
    """비동기 처방 생성 (새로운 입력 형식)"""    
    increment_active_requests()

    # 응답 데이터 변수 초기화
    start_time = time.time()
    response_data = None
    result_code = "9999"
    result_message = ""
    es_connected = False
    ingredients_found = 0
    total_ingredients = 0
    
    try:
        # 입력 검증
        if not direction.strip():
            raise ValueError("개발방향이 비어있습니다.")

        # 엘라스틱서치 연결 상태 확인
        if es_searcher and es_searcher.es:
            try:
                es_connected = es_searcher.es.ping()
            except:
                es_connected = False  

        # 성분 정보 수집 및 엘라스틱서치 검색
        ingredient_details = []                  
        
        # AI 모델 설정 적용 (프롬프트 타입 포함)
        formulator.set_model_config(model_name, temperature, prompt_type)
        
        # AI 모델 호출 (스레드 풀에서 실행)
        print("📝 1단계: 프롬프트 준비 중...")
        logger.info(f"🤖 AI 모델 호출 시작: {model_name} (temperature: {temperature})")
        
        try:
            result = await asyncio.get_event_loop().run_in_executor(
                executor, formulator.create_formula, None, "", direction
            )
            print("✅ 프롬프트 준비 완료")
            
            # 결과 검증
            validate_formula_result(result)

            logger.info(f"✅ 처방 생성 성공: {len(result.phases)}개 Phase, {len(result.detailed_process)}개 공정")
            
            # 성공 응답 데이터 할당
            response_data = result
            result_code = "0000"
            result_message = "처방이 성공적으로 생성되었습니다."
            total_ingredients = len(ingredient_details)
            
        except Exception as model_error:
            error_msg = f"AI 모델 호출 중 오류 발생: {str(model_error)}"
            logger.error(f"❌ {error_msg}")
            logger.exception("상세 오류 정보:")  # 전체 스택 트레이스 로깅
            
            # 원본 예외를 다시 발생시켜 상위 예외 처리에서 처리하도록
            raise Exception(error_msg) from model_error
            
    except Exception as e:
        error_msg = str(e)
        logger.error(f"❌ 처방 생성 실패: {error_msg}")
        logger.exception("전체 오류 스택:")
        
        # 오류 응답 데이터 할당
        result_code = "9999"
        result_message = create_detailed_error_message(error_msg)
        
    finally:
        processing_time = time.time() - start_time
        
        # 최종 응답 생성 및 반환
        response = FormulaApiResponse(
            resultCode=result_code,
            resultFailMessage=result_message,
            resultData=response_data,
            processing_time=round(processing_time, 2),
            elasticsearch_connected=es_connected,
            ingredients_found=ingredients_found,
            total_ingredients=total_ingredients,
            request_id=request_id,
            request_data=request_data
        )
        decrement_active_requests()
        return response

# ===== API 엔드포인트 =====

@app.on_event("startup")
async def startup_event():
    """서버 시작 시 초기화"""
    logger.info("🚀 화장품 처방 AI API 서버 시작 중... (최적화 버전)")
    initialize_services()

@app.get("/", response_model=dict)
async def root():
    """루트 엔드포인트"""
    return {
        "message": "화장품 처방 AI API (최적화 버전)",
        "version": "2.0.0",
        "docs": "/docs",
        "health": "/health",
        "max_workers": MAX_WORKERS
    }

@app.get("/health", response_model=HealthResponse)
async def health_check():
    """헬스 체크 엔드포인트"""
    es_connected = es_searcher and es_searcher.es and es_searcher.es.ping()
    
    return HealthResponse(
        status="healthy" if es_connected else "degraded",
        elasticsearch_connected=es_connected,
        timestamp=datetime.now().isoformat(),
        active_requests=active_requests,
        max_workers=MAX_WORKERS
    )

@app.post("/formula", response_model=FormulaApiResponse)
async def create_formula_v1(request: FormulaRequestV1):
    """화장품 처방 생성 API (v1 입력 형식) - 성분 목록, 제형, 개발방향 입력"""
    # 요청 ID 생성
    request_id = f"req_{int(time.time() * 1000)}"
    
    logger.info(f"📝 요청 수신: {request_id} - {request.formulation}")
    logger.info(f"🚀 화장품 처방 생성 시작 (엘라스틱서치 정보 포함)")
    logger.info(f"📅 실행 시간: {datetime.now().strftime('%Y-%m-%d %H:%M:%S')}")
    logger.info(f"🤖 사용 모델: {request.model_name}")
    logger.info(f"🔧 온도 설정: {request.temperature}")
    logger.info(f"📋 프롬프트 타입: {request.prompt_type}")
    logger.info(f"🔗 엘라스틱서치 연결: {'✅' if es_searcher and es_searcher.es and es_searcher.es.ping() else '❌'}")
    logger.info(f"📝 입력 정보:")
    logger.info(f"   - 개발방향: {request.direction}")
    logger.info(f"   - 제형: {request.formulation}")
    logger.info(f"   - 개발방향: {request.direction}")
    logger.info("-" * 50)
    
    # 비동기 처방 생성
    result = await create_formula_async_v1(
        request.ingredients,
        request.formulation,
        request.direction,
        request_id,
        request,
        request.model_name,
        request.temperature,
        request.prompt_type
    )
    
    logger.info(f"✅ 요청 완료: {request_id} - 처리시간: {result.processing_time}초")
    return result

@app.post("/formula-v2", response_model=FormulaApiResponse)
async def create_formula_v2(request: FormulaRequestV2):
    """화장품 처방 생성 API (v2 입력 형식) - 개발방향만 입력"""
    # 요청 ID 생성
    request_id = f"req_{int(time.time() * 1000)}"
    
    logger.info(f"📝 요청 수신: {request_id} - {request.direction}")
    logger.info(f"🚀 화장품 처방 생성 시작 (엘라스틱서치 정보 포함)")
    logger.info(f"📅 실행 시간: {datetime.now().strftime('%Y-%m-%d %H:%M:%S')}")
    logger.info(f"🤖 사용 모델: {request.model_name}")
    logger.info(f"🔧 온도 설정: {request.temperature}")
    logger.info(f"📋 프롬프트 타입: {request.prompt_type}")
    logger.info(f"🔗 엘라스틱서치 연결: {'✅' if es_searcher and es_searcher.es and es_searcher.es.ping() else '❌'}")
    logger.info(f"📝 입력 정보:")
    logger.info(f"   - 개발방향: {request.direction}")
    logger.info("-" * 50)
    
    # 비동기 처방 생성
    result = await create_formula_async_v2(
        request.direction,
        request_id,
        request,
        request.model_name,
        request.temperature,
        request.prompt_type
    )
    
    logger.info(f"✅ 요청 완료: {request_id} - 처리시간: {result.processing_time}초")
    return result

@app.get("/ingredients/search/{ingredient_name}")
async def search_ingredient(ingredient_name: str):
    """개별 성분 검색 API"""
    try:
        if not es_searcher or not es_searcher.es:
            raise HTTPException(status_code=503, detail="엘라스틱서치가 연결되지 않았습니다.")
        
        # 스레드 풀에서 실행
        loop = asyncio.get_event_loop()
        result = await loop.run_in_executor(
            executor, es_searcher.search_ingredient_info, ingredient_name
        )
        
        return {
            "success": result.get("found", False),
            "ingredient_name": ingredient_name,
            "data": result.get("ingredient_info") if result.get("found") else None,
            "message": result.get("message", "성분을 찾을 수 없습니다.") if not result.get("found") else "성분을 찾았습니다."
        }
        
    except Exception as e:
        raise HTTPException(status_code=500, detail=f"성분 검색 중 오류가 발생했습니다: {str(e)}")

# ===== 서버 실행 =====
if __name__ == "__main__":
    import uvicorn
    
    logger.info("🌟 화장품 처방 AI API 서버 시작 (최적화 버전)")
    logger.info("📖 API 문서: http://localhost:28632/docs")
    logger.info("🔍 헬스 체크: http://localhost:28632/health")
    logger.info(f"⚡ 최대 워커 수: {MAX_WORKERS}")
    logger.info("=" * 50)
    
    uvicorn.run(
        "cosmetic_formulator_api_optimized:app",
        host="0.0.0.0",
        port=28632,
        reload=True,
        log_level="info"
    )
