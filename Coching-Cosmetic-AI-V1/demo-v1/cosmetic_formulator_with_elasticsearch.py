from langchain_openai import ChatOpenAI
from langchain.prompts import ChatPromptTemplate
from langchain.output_parsers import PydanticOutputParser
from typing import List, Optional, Dict, Any, Tuple
import os
import json
import time
from datetime import datetime
from dotenv import load_dotenv
from elasticsearch import Elasticsearch
import re

# 분리된 모듈 import
from schemas import (
    Ingredient,
    Phase,
    DetailedProcess,
    QualityControl,
    FormulaResponse
)
from prompt_templates import get_prompt_templates
from model_config import get_model_config, get_ai_model_list, get_default_model_config

# .env 로드
load_dotenv()

# OpenAI API 키 확인
api_key = os.getenv("OPENAI_API_KEY")
if not api_key:
    print("ERROR: OpenAI API 키가 설정되지 않았습니다.")
    print("다음 중 하나의 방법으로 API 키를 설정하세요:")
    print("1. .env 파일에 OPENAI_API_KEY=your_api_key 추가")
    print("2. 환경변수로 설정: set OPENAI_API_KEY=your_api_key (Windows)")
    print("3. 또는 $env:OPENAI_API_KEY='your_api_key' (PowerShell)")
    exit(1)

# 엘라스틱서치 연결 설정
ELASTICSEARCH_HOST = os.getenv("ELASTICSEARCH_HOST", "localhost")
ELASTICSEARCH_PORT = os.getenv("ELASTICSEARCH_PORT", "9200")
ELASTICSEARCH_USERNAME = os.getenv("ELASTICSEARCH_USERNAME", "")
ELASTICSEARCH_PASSWORD = os.getenv("ELASTICSEARCH_PASSWORD", "")

# JSON 스키마는 schemas.py에서 import

class ElasticsearchIngredientSearcher:
    """엘라스틱서치에서 성분 정보를 검색하는 클래스"""
    
    def __init__(self):
        # 엘라스틱서치 검색기 초기화
        self.es = None
        self.index_name = "coching_ingredient_v2"  # 기본 인덱스명
        
    def connect(self):
        # 엘라스틱서치 서버에 연결
        try:
            # 인증 정보가 있는 경우와 없는 경우를 구분
            if ELASTICSEARCH_USERNAME and ELASTICSEARCH_PASSWORD:
                self.es = Elasticsearch(
                    [f"{ELASTICSEARCH_HOST}:{ELASTICSEARCH_PORT}"],
                    basic_auth=(ELASTICSEARCH_USERNAME, ELASTICSEARCH_PASSWORD),
                    verify_certs=False,
                    ssl_show_warn=False
                )
            else:
                self.es = Elasticsearch(
                    [f"{ELASTICSEARCH_HOST}:{ELASTICSEARCH_PORT}"],
                    verify_certs=False,
                    ssl_show_warn=False
                )
            
            # 연결 테스트
            if self.es.ping():
                print(f"✅ 엘라스틱서치 연결 성공: {ELASTICSEARCH_HOST}:{ELASTICSEARCH_PORT}")
                return True
            else:
                print(f"❌ 엘라스틱서치 연결 실패: {ELASTICSEARCH_HOST}:{ELASTICSEARCH_PORT}")
                return False
                
        except Exception as e:
            print(f"❌ 엘라스틱서치 연결 오류: {e}")
            return False
    
    def search_ingredient_info(self, ingredient_name: str) -> Dict[str, Any]:
        # 단일 성분의 상세 정보를 엘라스틱서치에서 검색
        if not self.es:
            return {"found": False, "error": "엘라스틱서치 연결이 없습니다."}
        
        try:
            # 성분명으로 검색 (정확한 매치와 부분 매치 모두 시도)
            query = {
                "query": {
                    "bool": {
                        "should": [
                            {"match": {"rep_name": {"query": ingredient_name, "boost": 5}}},
                            {"match": {"rep_name_en": {"query": ingredient_name, "boost": 4}}},
                            {"match": {"names_ko": {"query": ingredient_name, "boost": 3}}},
                            {"match": {"names_en": {"query": ingredient_name, "boost": 3}}},
                            {"match": {"nick_name": {"query": ingredient_name, "boost": 2}}},
                            {"match": {"inci": {"query": ingredient_name, "boost": 2}}},
                            {"wildcard": {"rep_name": {"value": f"*{ingredient_name}*", "boost": 1}}},
                            {"wildcard": {"rep_name_en": {"value": f"*{ingredient_name}*", "boost": 1}}},
                            {"wildcard": {"names_ko": {"value": f"*{ingredient_name}*", "boost": 1}}},
                            {"wildcard": {"names_en": {"value": f"*{ingredient_name}*", "boost": 1}}}
                        ]
                    }
                },
                "size": 1
            }
            
            response = self.es.search(index=self.index_name, body=query)
            
            if response['hits']['total']['value'] > 0:
                hit = response['hits']['hits'][0]['_source']
                return {
                    "found": True,
                    "ingredient_info": hit,
                    "score": response['hits']['hits'][0]['_score']
                }
            else:
                return {"found": False, "message": f"'{ingredient_name}' 성분을 찾을 수 없습니다."}
                
        except Exception as e:
            return {"found": False, "error": f"검색 중 오류 발생: {e}"}
    
    def search_by_field(self, ingredient: Dict[str, Any], field_name: str, search_value: Any) -> Dict[str, Any]:
        """특정 필드로 성분 검색"""
        if not self.es:
            return {"found": False, "error": "엘라스틱서치 연결이 없습니다."}
        
        try:
            print(f"🔍 {field_name}으로 검색 중: {search_value}")
            
            # 필드 타입에 따른 검색 쿼리 구성
            if field_name == "id":
                # ID는 정확한 매치만
                query = {
                    "query": {"term": {"id": search_value}},
                    "size": 1
                }
            else:
                # 텍스트 필드는 정확한 매치만 (keyword 필드 사용)
                query = {
                    "query": {"term": {f"{field_name}.keyword": search_value}},
                    "size": 1
                }
            
            response = self.es.search(index=self.index_name, body=query)
            
            if response['hits']['total']['value'] > 0:
                hit = response['hits']['hits'][0]['_source']
                print(f"✅ {field_name}으로 성분 발견: {hit.get('rep_name', 'N/A')}")
                return {
                    "found": True,
                    "ingredient_info": hit,
                    "search_method": field_name,
                    "score": response['hits']['hits'][0]['_score']
                }
            else:
                return {"found": False, "search_method": field_name}
                
        except Exception as e:
            print(f"❌ {field_name} 검색 중 오류 발생: {e}")
            return {"found": False, "error": f"{field_name} 검색 중 오류 발생: {e}"}
    
    def search_ingredient_by_priority(self, ingredient: Dict[str, Any]) -> Dict[str, Any]:
        """ID → rep_name_en → rep_name 순서로 성분 검색 (리팩토링된 버전)"""
        if not self.es:
            return {"found": False, "error": "엘라스틱서치 연결이 없습니다."}
        
        try:
            # 검색 우선순위: id → repNameEn → repName
            search_fields = [
                ("id", ingredient.get("id")),
                ("rep_name_en", ingredient.get("repNameEn")),
                ("rep_name", ingredient.get("repName"))
            ]
            
            for field_name, field_value in search_fields:
                if field_value is not None:
                    result = self.search_by_field(ingredient, field_name, field_value)
                    if result.get("found"):
                        return result
            
            # 모든 검색 실패
            rep_name = ingredient.get("repName", "알 수 없음")
            rep_name_en = ingredient.get("repNameEn", "알 수 없음")
            print(f"❌ 성분을 찾을 수 없음: {rep_name} ({rep_name_en})")
            return {
                "found": False, 
                "message": f"'{rep_name}' ({rep_name_en}) 성분을 찾을 수 없습니다.",
                "search_method": "none"
            }
                
        except Exception as e:
            print(f"❌ 검색 중 오류 발생: {e}")
            return {"found": False, "error": f"검색 중 오류 발생: {e}"}

class CosmeticFormulatorWithES:
    """엘라스틱서치 연동 화장품 처방 AI 클래스"""
    
    def __init__(self):
        # 처방 AI 초기화 및 엘라스틱서치 연결
        self.model_name = "gpt-5-mini"
        self.temperature = 0.3
        
        # 엘라스틱서치 검색기 초기화
        self.es_searcher = ElasticsearchIngredientSearcher()
        self.es_connected = self.es_searcher.connect()
        # 기본 프롬프트 설정
        self.prompt = self.get_prompt_template("default")
        # PydanticOutputParser 초기화 (FormulaResponse 스키마 사용)
        self.parser = PydanticOutputParser(pydantic_object=FormulaResponse)
        # 모델 설정 적용
        self.set_model_config(self.model_name, self.temperature, "default")
    
    def get_prompt_template(self, prompt_type: str = "default") -> ChatPromptTemplate:
        """프롬프트 타입에 따라 적절한 프롬프트 템플릿 반환"""
        # 프롬프트 템플릿 딕셔너리 가져오기
        prompt_templates = get_prompt_templates()
        
        # 기본값은 "default"
        if prompt_type not in prompt_templates:
            prompt_type = "default"
        
        system_prompt, user_prompt = prompt_templates[prompt_type]
        
        return ChatPromptTemplate.from_messages([
            ("system", system_prompt),
            ("user", user_prompt)
        ])
    
    def set_model_config(self, model_name: str = "gpt-5-mini", temperature: float = 0.3, prompt_type: str = "default"):
        """모델명, 온도, 프롬프트 타입 설정"""
        config = get_model_config(model_name)

        temp_min = config.get("temp_min", 0)
        temp_max = config.get("temp_max", 2)
        adjusted_temperature = max(temp_min, min(temp_max, temperature))
        if adjusted_temperature != temperature:
            print(f"⚠️ 온도 범위 조정: {temperature} -> {adjusted_temperature} (허용 범위 {temp_min}-{temp_max})")

        max_tokens = config.get("max_tokens", 0)

        llm_kwargs = {
            "model": model_name,
            "temperature": adjusted_temperature,
            "api_key": api_key,
        }
        if max_tokens and max_tokens > 0:
            llm_kwargs["max_tokens"] = max_tokens
        else:
            print(f"ℹ️ 모델 {model_name}은 max_tokens 옵션을 사용하지 않습니다.")

        self.model_name = model_name
        self.temperature = adjusted_temperature
        self.llm = ChatOpenAI(**llm_kwargs)

        # 프롬프트 타입에 따라 프롬프트 템플릿 설정
        self.prompt = self.get_prompt_template(prompt_type)
    
    def parse_ingredients(self, ingredients_str: str) -> List[str]:
        # 입력된 성분 문자열을 파싱하여 성분명 리스트로 변환
        # 쉼표, 세미콜론, 줄바꿈 등으로 구분된 성분들을 파싱
        ingredients = re.split(r'[,;\n\r]+', ingredients_str)
        # 공백 제거 및 빈 문자열 제거
        ingredients = [ingredient.strip() for ingredient in ingredients if ingredient.strip()]
        return ingredients
    
    def _normalize_ai_response(self, data: Dict[str, Any]) -> Dict[str, Any]:
        """AI 응답을 우리 스키마 형식으로 정규화"""
        normalized = data.copy()
        
        # 불필요한 필드 제거
        normalized.pop('es_id_field_note', None)

        # 1. phases 변환
        if 'phases' in normalized:
            phases_list = []
            
            # 딕셔너리 형태인 경우 (예: {"A상": {...}, "B상": {...}})
            if isinstance(normalized['phases'], dict):
                for phase_key, phase_data in normalized['phases'].items():
                    phase_obj = {
                        'phase_name': phase_key,
                        'ingredients': []
                    }
                    
                    # temperature 변환 (temperature_range -> temperature)
                    if 'temperature_range' in phase_data:
                        phase_obj['temperature'] = phase_data['temperature_range']
                    elif 'temperature' in phase_data:
                        phase_obj['temperature'] = phase_data['temperature']
                    
                    # ingredients 변환
                    if 'ingredients' in phase_data and isinstance(phase_data['ingredients'], list):
                        for ing in phase_data['ingredients']:
                            ing_obj = {
                                'name': ing.get('name', ''),
                                'percentage': ing.get('percentage', 0.0),
                                'function': ing.get('function', ing.get('purpose', '기능성 성분')),
                                'es_id': ing.get('es_id', ing.get('esId', None))
                            }
                            phase_obj['ingredients'].append(ing_obj)
                    
                    phases_list.append(phase_obj)
            
            # 리스트 형태인 경우 (예: [{"name": "A상-수상층공정", ...}, ...])
            elif isinstance(normalized['phases'], list):
                for phase_data in normalized['phases']:
                    phase_obj = {
                        'phase_name': phase_data.get('phase_name', phase_data.get('name', '')),
                        'ingredients': []
                    }
                    
                    # temperature 변환
                    if 'temperature_range' in phase_data:
                        phase_obj['temperature'] = phase_data['temperature_range']
                    elif 'temperature' in phase_data:
                        phase_obj['temperature'] = phase_data['temperature']
                    
                    # ingredients 변환
                    if 'ingredients' in phase_data and isinstance(phase_data['ingredients'], list):
                        for ing in phase_data['ingredients']:
                            ing_obj = {
                                'name': ing.get('name', ''),
                                'percentage': ing.get('percentage', 0.0),
                                'function': ing.get('function', ing.get('purpose', '기능성 성분')),
                                'es_id': ing.get('es_id', ing.get('esId', None))
                            }
                            phase_obj['ingredients'].append(ing_obj)
                    
                    phases_list.append(phase_obj)
            
            normalized['phases'] = phases_list
        
        # 2. detailed_process 변환
        if 'detailed_process' in normalized and isinstance(normalized['detailed_process'], list):
            process_list = []
            
            # null 문자열 처리 헬퍼 함수
            def clean_value(val):
                if val is None or val == 'null' or val == 'None':
                    return None
                return val
            
            for idx, process in enumerate(normalized['detailed_process']):
                # step_number 추출 또는 생성
                step_number = process.get('step_number', process.get('step'))
                if step_number:
                    # step이 문자열인 경우 숫자 추출 시도
                    if isinstance(step_number, str):
                        import re
                        numbers = re.findall(r'\d+', step_number)
                        step_number = int(numbers[0]) if numbers else idx + 1
                    else:
                        step_number = int(step_number)
                else:
                    step_number = idx + 1
                
                # phase 추론
                phase = process.get('phase', process.get('phase_name'))
                if not phase:
                    # step, process_name, procedure에서 Phase 추론 시도
                    step_name = process.get('step', process.get('process_name', process.get('procedure', '')))
                                        
                    if 'A상' in step_name or '수상' in step_name or '유화' in step_name or '가용화' in step_name:
                        phase = 'A상'
                    elif 'B상' in step_name or '유상' in step_name:
                        phase = 'B상'
                    elif 'C상' in step_name or '첨가' in step_name or '보습' in step_name or '냉각' in step_name:
                        phase = 'C상'
                    elif 'D상' in step_name or '특수' in step_name or '기능성' in step_name:
                        phase = 'D상'
                    elif 'E상' in step_name or '보존' in step_name or '완료' in step_name or '최종' in step_name or '탈포' in step_name or '안정화' in step_name or '여과' in step_name:
                        phase = 'E상'
                    else:
                        phase = 'A상'  # 기본값
                
                # description 추출
                description = process.get('description', process.get('procedure', process.get('process_name', process.get('step', '공정'))))
                
                # temperature, time, notes 추출
                temperature = clean_value(process.get('temperature')) or '상온'
                time = clean_value(process.get('time')) or '10분'
                notes = clean_value(process.get('notes', process.get('note'))) or '균일하게 혼합'
                
                # notes에 추가 정보 포함 (mixing_speed, duration 등)
                mixing_speed = clean_value(process.get('mixing_speed'))
                duration = clean_value(process.get('duration'))
                
                if mixing_speed:
                    if notes and notes != '균일하게 혼합':
                        notes += f", 교반속도: {mixing_speed}"
                    else:
                        notes = f"교반속도: {mixing_speed}"
                
                if duration and duration != time:
                    if notes and notes != '균일하게 혼합':
                        notes += f", 소요시간: {duration}"
                
                process_obj = {
                    'step_number': step_number,
                    'phase': phase,
                    'description': description,
                    'temperature': temperature,
                    'time': time,
                    'notes': notes
                }
                
                process_list.append(process_obj)
            normalized['detailed_process'] = process_list
        
        # 3. quality_control.stability_test 변환: 문자열 -> 리스트
        if 'quality_control' in normalized:
            qc = normalized['quality_control']
            if isinstance(qc, dict):
                if 'stability_test' in qc:
                    if isinstance(qc['stability_test'], str):
                        # 문자열을 리스트로 변환 (쉼표로 구분된 경우)
                        if ',' in qc['stability_test']:
                            qc['stability_test'] = [s.strip() for s in qc['stability_test'].split(',')]
                        else:
                            qc['stability_test'] = [qc['stability_test']]
                    elif not isinstance(qc['stability_test'], list):
                        qc['stability_test'] = [str(qc['stability_test'])]
                # appearance가 리스트인 경우 문자열로 변환
                if 'appearance' in qc:
                    appearance = qc['appearance']
                    if isinstance(appearance, list):
                        qc['appearance'] = ", ".join(str(item) for item in appearance)
                    elif appearance is None:
                        qc['appearance'] = "외관 정보 없음"
                normalized['quality_control'] = qc
        
        # 4. Ingredient의 function 필드 보완 (phases 내부)
        if 'phases' in normalized and isinstance(normalized['phases'], list):
            for phase in normalized['phases']:
                if 'ingredients' in phase and isinstance(phase['ingredients'], list):
                    for ing in phase['ingredients']:
                        if 'function' not in ing or not ing['function']:
                            # function이 없으면 기본값 설정
                            ing['function'] = '기능성 성분'
        
        return normalized
    
    def _validate_formula_response(self, parsed: FormulaResponse) -> List[str]:
        """
        FormulaResponse 객체의 필수 필드를 검증합니다.
        
        Args:
            parsed: 검증할 FormulaResponse 객체
            
        Returns:
            List[str]: 검증 오류 목록. 빈 리스트면 검증 통과
        """
        validation_errors = []
        
        # overview 검증
        if not hasattr(parsed, 'overview') or not parsed.overview:
            validation_errors.append("overview 필드가 없거나 비어있습니다")
        
        # total_percentage 검증 및 변환
        if not hasattr(parsed, 'total_percentage'):
            validation_errors.append("total_percentage 필드가 없습니다")
        elif isinstance(parsed.total_percentage, str):
            # 문자열인 경우 숫자로 변환 시도
            try:
                cleaned = parsed.total_percentage.replace('%', '').strip()
                parsed.total_percentage = float(cleaned)
                print(f"   ⚠️ total_percentage를 숫자로 변환: {parsed.total_percentage}")
            except:
                validation_errors.append(f"total_percentage를 숫자로 변환할 수 없습니다: {parsed.total_percentage}")
        
        # phases 검증
        if not hasattr(parsed, 'phases') or not parsed.phases:
            validation_errors.append("phases 필드가 없거나 비어있습니다")
        
        # detailed_process 검증
        if not hasattr(parsed, 'detailed_process') or not parsed.detailed_process:
            validation_errors.append("detailed_process 필드가 없거나 비어있습니다")
        
        # quality_control 검증
        if not hasattr(parsed, 'quality_control') or not parsed.quality_control:
            validation_errors.append("quality_control 필드가 없거나 비어있습니다")
        
        # precautions 검증
        if not hasattr(parsed, 'precautions') or not parsed.precautions:
            validation_errors.append("precautions 필드가 없거나 비어있습니다")
        
        # pros 검증
        if not hasattr(parsed, 'pros') or not parsed.pros:
            validation_errors.append("pros 필드가 없거나 비어있습니다")
        
        # cons 검증
        if not hasattr(parsed, 'cons') or not parsed.cons:
            validation_errors.append("cons 필드가 없거나 비어있습니다")
        
        return validation_errors
    
    def _parse_ai_response(self, result: Any) -> Tuple[bool, Optional[FormulaResponse], Optional[str]]:
        """
        AI API 응답을 파싱하여 FormulaResponse 객체로 변환합니다.
        
        Args:
            result: AI API 응답 (dict, AIMessage, 또는 FormulaResponse)
            
        Returns:
            Tuple[bool, Optional[FormulaResponse], Optional[str]]: 
                - 성공 여부 (bool)
                - 파싱된 FormulaResponse 객체 (성공 시)
                - 오류 메시지 (실패 시)
        """
        try:
            # include_raw=True인 경우 dict 형태로 반환됨
            if isinstance(result, dict):
                raw_response = result.get('raw')
                parsed = result.get('parsed')
                parsing_error = result.get('parsing_error')
                
                # 원본 응답 로깅
                if raw_response:
                    print(f"   📋 원본 응답 타입: {type(raw_response)}")
                    if hasattr(raw_response, 'content'):
                        print(f"   📋 원본 응답 내용 (처음 1000자): {str(raw_response.content)[:1000]}")
                    if hasattr(raw_response, 'additional_kwargs'):
                        print(f"   📋 추가 정보: {raw_response.additional_kwargs}")
                
                # 파싱 오류가 있는 경우
                if parsing_error:
                    error_msg = f"파싱 오류 발생: {str(parsing_error)}"
                    print(f"   ❌ {error_msg}")
                    print(f"   📋 파싱 오류 상세: {type(parsing_error).__name__}")
                    import traceback
                    print(f"   📋 파싱 오류 스택:")
                    for line in traceback.format_exception(type(parsing_error), parsing_error, parsing_error.__traceback__)[:10]:
                        if line.strip():
                            print(f"      {line.strip()}")
                    return False, None, error_msg
                
                # parsed가 None인 경우
                if parsed is None:
                    error_msg = "파싱된 결과가 None입니다."
                    print(f"   ❌ {error_msg}")
                    if raw_response and hasattr(raw_response, 'content'):
                        print(f"   📋 원본 응답 전체: {raw_response.content}")
                    return False, None, error_msg
                
                # parsed가 dict인 경우 FormulaResponse로 변환 시도
                if isinstance(parsed, dict):
                    print(f"   ⚠️ 파싱 결과가 dict입니다. FormulaResponse로 변환 시도...")
                    print(f"   📋 dict 키 목록: {list(parsed.keys())}")
                    
                    # AI 응답 형식을 우리 스키마 형식으로 변환
                    parsed = self._normalize_ai_response(parsed)
                    print(f"   ✅ AI 응답 형식 정규화 완료")
                    
                    # 필수 필드 확인
                    required_fields = ['overview', 'total_percentage', 'phases', 'detailed_process', 
                                     'quality_control', 'precautions', 'storage_conditions', 
                                     'shelf_life', 'pros', 'cons']
                    missing_fields = [field for field in required_fields if field not in parsed]
                    
                    if missing_fields:
                        error_msg = f"필수 필드가 누락되었습니다: {', '.join(missing_fields)}"
                        print(f"   ❌ {error_msg}")
                        print(f"   📋 현재 dict에 있는 필드: {list(parsed.keys())}")
                        print(f"   📋 원본 응답 전체 (raw_response.content):")
                        if raw_response and hasattr(raw_response, 'content'):
                            print(f"      {raw_response.content}")
                        return False, None, error_msg
                    
                    try:
                        parsed = FormulaResponse(**parsed)
                        print(f"   ✅ dict를 FormulaResponse로 변환 성공")
                    except Exception as conv_error:
                        error_msg = f"dict를 FormulaResponse로 변환 실패: {str(conv_error)}"
                        print(f"   ❌ {error_msg}")
                        print(f"   📋 정규화된 dict 전체 내용: {json.dumps(parsed, indent=2, ensure_ascii=False)[:2000]}")
                        print(f"   📋 원본 응답 전체 (raw_response.content):")
                        if raw_response and hasattr(raw_response, 'content'):
                            print(f"      {raw_response.content}")
                        return False, None, error_msg
                
                # parsed가 이미 FormulaResponse인 경우
                if isinstance(parsed, FormulaResponse):
                    return True, parsed, None
                
                # 예상치 못한 타입
                error_msg = f"예상치 못한 파싱 결과 타입: {type(parsed)}"
                print(f"   ❌ {error_msg}")
                return False, None, error_msg
                
            # include_raw=False인 경우 직접 FormulaResponse 객체 반환
            # 또는 format_instructions에서 dict로 변환되지 않은 경우
            elif hasattr(result, 'content'):
                # AIMessage인 경우 파싱 필요
                print(f"   📋 AIMessage 응답 파싱 중...")
                try:
                    parsed = self.parser.parse(result.content)
                    if isinstance(parsed, dict):
                        parsed = self._normalize_ai_response(parsed)
                        parsed = FormulaResponse(**parsed)
                    print(f"   ✅ AIMessage 응답 파싱 성공")
                    return True, parsed, None
                except Exception as parse_error:
                    error_msg = f"AIMessage 응답 파싱 실패: {str(parse_error)}"
                    print(f"   ❌ {error_msg}")
                    print(f"   📋 원본 응답 내용: {result.content[:1000] if hasattr(result, 'content') else str(result)[:1000]}")
                    return False, None, error_msg
            else:
                # 이미 FormulaResponse인 경우
                if isinstance(result, FormulaResponse):
                    return True, result, None
                
                # 예상치 못한 타입
                error_msg = f"예상치 못한 응답 타입: {type(result)}"
                print(f"   ❌ {error_msg}")
                return False, None, error_msg
                
        except Exception as e:
            error_msg = f"응답 파싱 중 예외 발생: {str(e)}"
            print(f"   ❌ {error_msg}")
            import traceback
            print(f"   📋 예외 스택:")
            for line in traceback.format_exception(type(e), e, e.__traceback__)[:10]:
                if line.strip():
                    print(f"      {line.strip()}")
            return False, None, error_msg
    
    def format_ingredient_details(self, ingredient_details: List[Dict[str, Any]]) -> str:
        """이미 조회된 성분 정보 리스트를 받아 프롬프트용 문자열로 변환"""
        if not ingredient_details:
            return "※ 성분 상세 정보를 찾을 수 없습니다."

        formatted_parts = []
        for detail in ingredient_details:
            name = detail.get("name", "알 수 없음")
            info = detail.get("info", {}) or {}

            part_lines = [f"📌 {name}:"]

            rep_name = info.get("rep_name") or info.get("repName")
            if rep_name:
                part_lines.append(f"   • 대표명: {rep_name}")

            rep_name_en = info.get("rep_name_en") or info.get("repNameEn")
            if rep_name_en:
                part_lines.append(f"   • 영문명: {rep_name_en}")

            inci = info.get("inci")
            if isinstance(inci, list) and inci:
                part_lines.append(f"   • INCI명: {', '.join(inci)}")

            purposes = info.get("purposes")
            if isinstance(purposes, list) and purposes:
                part_lines.append(f"   • 기능: {', '.join(purposes)}")

            ewg_score = info.get("ewg_score")
            if ewg_score is not None:
                part_lines.append(f"   • EWG 점수: {ewg_score}")

            ewg_label = info.get("ewg_data_label")
            if ewg_label:
                part_lines.append(f"   • 안전성 등급: {ewg_label}")

            formula = info.get("formula")
            if formula:
                part_lines.append(f"   • 화학식: {formula}")

            cas_no = info.get("cas_no")
            if isinstance(cas_no, list) and cas_no:
                part_lines.append(f"   • CAS 번호: {', '.join(cas_no)}")

            es_id = info.get("id")
            if es_id is not None:
                part_lines.append(f"   • ES ID: {es_id}")

            if len(part_lines) == 1:
                part_lines.append("   • 상세 정보가 없습니다.")

            formatted_parts.append("\n".join(part_lines))

        return "**성분 상세 정보:**\n" + "\n\n".join(formatted_parts)

    def build_default_ingredient_detail_from_input(self, ingredient_data: Dict[str, Any]) -> Dict[str, Any]:
        """성분을 찾지 못했을 때 기본 정보를 생성"""
        rep_name = ingredient_data.get("repName") or ingredient_data.get("name") or "알 수 없음"
        rep_name_en = ingredient_data.get("repNameEn") or ingredient_data.get("rep_name_en") or ""
        ingredient_id = ingredient_data.get("id")

        return {
            "name": rep_name,
            "info": {
                "id": ingredient_id,
                "rep_name": rep_name,
                "rep_name_en": rep_name_en,
                "purposes": [],
                "ewg_score": None
            },
            "search_method": "none"
        }

    def fetch_single_ingredient_detail(
        self,
        ingredient_data: Dict[str, Any],
        print_detail_log: bool = False
    ) -> Tuple[Dict[str, Any], bool, Optional[int]]:
        """단일 성분에 대해 엘라스틱서치 정보를 검색"""
        if not self.es_searcher or not self.es_searcher.es:
            return self.build_default_ingredient_detail_from_input(ingredient_data), False, None

        search_payload = {
            "id": ingredient_data.get("id"),
            "repName": ingredient_data.get("repName") or ingredient_data.get("name"),
            "repNameEn": ingredient_data.get("repNameEn") or ingredient_data.get("rep_name_en"),
        }
        search_payload["rep_name"] = search_payload.get("repName")
        search_payload["rep_name_en"] = search_payload.get("repNameEn")

        result = self.es_searcher.search_ingredient_by_priority(search_payload)

        rep_name_display = search_payload.get("repName") or "알 수 없음"

        if result.get("found"):
            ingredient_info = result["ingredient_info"]
            print(f"   ✅ '{rep_name_display}' 성분 정보 발견:")
                
            if print_detail_log:                
                print(f"      - ID: {ingredient_info.get('id', 'N/A')}")
                print(f"      - 대표명: {ingredient_info.get('rep_name', 'N/A')}")
                print(f"      - 영문명: {ingredient_info.get('rep_name_en', 'N/A')}")
                if ingredient_info.get('purposes'):
                    print(f"      - 기능: {', '.join(ingredient_info.get('purposes', []))}")
                if ingredient_info.get('ewg_score'):
                    print(f"      - EWG 점수: {ingredient_info.get('ewg_score')}")
            
            found_id = ingredient_info.get('id')
            detail = {
                "name": rep_name_display,
                "info": ingredient_info,
                "search_method": result.get("search_method", "unknown")
            }
            return detail, True, found_id

        print(f"   ❌ '{rep_name_display}' 성분 정보 없음")
        detail = self.build_default_ingredient_detail_from_input(ingredient_data)
        return detail, False, None
    
    def create_formula(
        self,
        ingredients: Optional[List[Dict[str, Any]]] = None,
        formulation: str = "",
        direction: str = ""
    ):
        # 엘라스틱서치 정보를 활용한 화장품 처방 생성
        print(f"\n🚀 화장품 처방 생성 시작 (엘라스틱서치 정보 포함)")
        print(f"📅 실행 시간: {datetime.now().strftime('%Y-%m-%d %H:%M:%S')}")
        print(f"🤖 사용 모델: {self.model_name}")
        print(f"🔧 온도 설정: {self.temperature}")
        print(f"🔗 엘라스틱서치 연결: {'✅' if self.es_connected else '❌'}")
        ingredient_names = []
        if ingredients:
            ingredient_names = [ingredient.get("name", "알 수 없음") for ingredient in ingredients]
        ingredients_str = ", ".join(ingredient_names)

        print(f"📝 입력 정보:")
        print(f"   - 전성분: {ingredients_str}")
        print(f"   - 제형: {formulation}")
        print(f"   - 개발방향: {direction}")
        print("-" * 50)

        aiModel = get_model_config(self.model_name)
        
        try:
            print("📝 1단계: 성분정보 처리")
            # 1단계: 성분 정보 전달 확인
            if ingredients is None:
                print("- 성분 정보가 제공되지 않아 기본 검색을 수행합니다.")
                ingredient_details = []
            else:
                ingredient_details = ingredients
                print("- 외부에서 전달된 성분 상세 정보 사용")
            ingredient_details_text = self.format_ingredient_details(ingredient_details)
            print("✅ 성분 정보 준비 완료")
            
            # 2단계: 프롬프트 준비
            print("\n📝 2단계: 프롬프트 준비 중...")

            format_params = {
                "ingredients": "- 전성분 : 창의적으로 필요한 성분을 선택하여 작성하라",
                "formulation": "- 제형 : 창의적으로 화장품 제형을 선택하여 작성하라",
                "direction": "- 개발방향 : 창의적으로 개발방향을 선택하여 작성하라",
                "ingredient_details": "",                
            }

            if ingredients_str.strip() != "":
                format_params["ingredients"] = "- 반드시 사용해야하는 전성분 : " + ingredients_str
                format_params["ingredient_details"] = "입력 전성분의 상세 정보 : \n" + ingredient_details_text
                
            
            if formulation.strip() != "":
                format_params["formulation"] = "- 제형 : " + formulation
            
            if direction.strip() != "":
                format_params["direction"] = "- 개발방향 : " + direction
                            
            format_instructions = self.parser.get_format_instructions()
            format_params["format_instructions"] = "출력은 아래 스키마를 정확히 따라야 한다:\n" + format_instructions

            final_prompt = self.prompt.format(**format_params)

            print(f"📋 생성된 프롬프트: \n{final_prompt}")
            
            print("✅ 프롬프트 준비 완료")


            # 3단계: AI 모델 호출 (with_structured_output 사용)
            print(f"\n🤖 3단계: {self.model_name} 모델 호출 중...")
            start_time = time.time()
            print("   - API 요청 전송 중...")
            
            try:
                result = None
                last_error = None

                if aiModel.get("method") == "with_structured_output":
                    # structured output을 사용하여 직접 FormulaResponse 객체로 받기
                    # include_raw=True로 원본 응답도 함께 받아서 디버깅 가능하도록
                    # 모델에 따라 적절한 메서드 선택
                    # gpt-4.1-nano는 json_format을 지원하므로 시도 순서 조정
                    methods_to_try = ["json_schema", "json_format", "json_mode", "function_calling"]
                    for method in methods_to_try:
                        try:
                            print(f"   🔧 {method} 메서드로 시도 중...")
                            structured_llm = self.llm.with_structured_output(
                                FormulaResponse,
                                include_raw=True,
                                method=method
                            )
                            result = structured_llm.invoke(final_prompt)
                            print(f"   ✅ {method} 메서드로 성공")
                            break
                        except Exception as method_error:
                            last_error = method_error
                            error_msg = str(method_error)
                            # json_format이 지원되지 않는 경우 json_mode로 자동 전환
                            if "json_format" in error_msg and "json_mode" in methods_to_try:
                                print(f"   ⚠️ {method} 메서드 실패, json_mode로 자동 전환...")
                                continue
                            print(f"   ⚠️ {method} 메서드 실패: {error_msg[:200]}")
                            continue
                
                else: # format_instructions
                    try:
                        print(f"   🔧 format_instructions 로 시도 중...")                        
                        result = self.llm.invoke(final_prompt)
                        print(f"   ✅ format_instructions 메서드로 성공")
                        
                        # format_instructions를 사용하는 경우 AIMessage가 반환되므로 파싱 필요
                        if hasattr(result, 'content'):
                            print(f"   📋 응답 파싱 중...")
                            try:
                                # PydanticOutputParser를 사용하여 JSON 문자열을 FormulaResponse로 파싱
                                parsed = self.parser.parse(result.content)
                                print(f"   ✅ 응답 파싱 성공")
                                
                                # parsed가 dict인 경우 FormulaResponse로 변환
                                if isinstance(parsed, dict):
                                    parsed = self._normalize_ai_response(parsed)
                                    parsed = FormulaResponse(**parsed)
                                
                                # result를 dict 형태로 변환하여 이후 로직과 일관성 유지
                                result = {
                                    'raw': result,
                                    'parsed': parsed,
                                    'parsing_error': None
                                }
                            except Exception as parse_error:
                                error_msg = f"응답 파싱 실패: {str(parse_error)}"
                                print(f"   ❌ {error_msg}")
                                print(f"   📋 원본 응답 내용: {result.content[:1000] if hasattr(result, 'content') else str(result)[:1000]}")
                                # 파싱 오류가 있어도 원본 응답은 유지
                                result = {
                                    'raw': result,
                                    'parsed': None,
                                    'parsing_error': parse_error
                                }
                        
                    except Exception as method_error:
                        last_error = method_error
                        error_msg = str(method_error)
                        print(f"   ⚠️ format_instructions 메서드 실패: {error_msg[:200]}")

                if result is None:
                    error_msg = f"모든 메서드 시도 실패. 마지막 오류: {str(last_error)}"
                    print(f"   ❌ {error_msg}")
                    raise Exception(error_msg) from last_error

                api_time = time.time() - start_time
                print(f"   - API 응답 수신 완료 (소요시간: {api_time:.2f}초)")
                
                # AI 응답 파싱
                success, parsed, parse_error_msg = self._parse_ai_response(result)
                
                if not success:
                    raise Exception(parse_error_msg or "응답 파싱 실패")
                
                # 결과 검증
                if parsed is None:
                    raise Exception("모델이 None을 반환했습니다.")
                
                if not isinstance(parsed, FormulaResponse):
                    error_msg = f"예상된 FormulaResponse 타입이 아닙니다. 실제 타입: {type(parsed)}"
                    print(f"   ❌ {error_msg}")
                    print(f"   📋 실제 값: {str(parsed)}")
                    raise Exception(error_msg)
                
                # 필수 필드 검증
                validation_errors = self._validate_formula_response(parsed)
                if validation_errors:
                    error_msg = f"응답 검증 실패: {', '.join(validation_errors)}"
                    print(f"   ❌ {error_msg}")
                    # 결과 내용 일부 출력 (디버깅용)
                    try:
                        result_dict = parsed.model_dump() if hasattr(parsed, 'model_dump') else str(parsed)
                        print(f"   📋 응답 내용 (일부): {str(result_dict)[:500]}")
                    except:
                        print(f"   📋 응답 내용: {str(parsed)[:500]}")
                    raise Exception(error_msg)
                
                print(f"✅ 구조화된 출력 파싱 완료")
                print(f"   - Phase 수: {len(parsed.phases)}")
                print(f"   - 공정 수: {len(parsed.detailed_process)}")
                
                # 성공 정보 출력
                total_time = time.time() - start_time
                print(f"\n🎉 처방 생성 완료!")
                print(f"🤖 사용 모델: {self.model_name}")
                print(f"⏱️ 총 소요시간: {total_time:.2f}초")
                print(f"   - API 호출 및 파싱: {api_time:.2f}초")
                
                return parsed
                
            except Exception as model_error:
                api_time = time.time() - start_time
                error_type = type(model_error).__name__
                error_msg = str(model_error)
                print(f"\n❌ 모델 호출 오류 발생 (소요시간: {api_time:.2f}초)")
                print(f"   오류 타입: {error_type}")
                print(f"   오류 내용: {error_msg}")
                
                # 상세 오류 정보 출력
                import traceback
                print(f"   📋 상세 오류 스택:")
                for line in traceback.format_exc().split('\n')[:10]:  # 처음 10줄만
                    if line.strip():
                        print(f"      {line}")
                
                # 원본 예외를 다시 발생시켜 상위에서 처리하도록
                raise Exception(f"모델 호출 중 오류 발생: {error_msg}") from model_error
            
        except Exception as e:
            total_time = time.time() - start_time if 'start_time' in locals() else 0
            error_type = type(e).__name__
            error_msg = str(e)
            print(f"\n❌ 오류 발생 (총 소요시간: {total_time:.2f}초)")
            print(f"   오류 타입: {error_type}")
            print(f"   오류 내용: {error_msg}")
            
            # 상세 오류 정보 출력
            import traceback
            print(f"   📋 상세 오류 스택:")
            for line in traceback.format_exc().split('\n')[:10]:  # 처음 10줄만
                if line.strip():
                    print(f"      {line}")
            
            return None
    
    def display_result(self, result: FormulaResponse):
        # 생성된 처방 결과를 사용자 친화적으로 출력
        if not result:
            print("결과를 생성할 수 없습니다.")
            return
            
        print("\n" + "="*60)
        print("🧴 화장품 처방서 상세 결과 (엘라스틱서치 정보 포함)")
        print("="*60)
        
        print(f"\n📋 개요:")
        print(f"   {result.overview}")
        print(f"   총 배합비: {result.total_percentage}%")
        
        print(f"\n🧪 Phase별 처방 구성:")
        for phase in result.phases:
            print(f"\n   📌 {phase.phase_name}")
            if phase.temperature:
                print(f"      온도: {phase.temperature}")
            for ingredient in phase.ingredients:
                print(f"      • {ingredient.name}: {ingredient.percentage}% - {ingredient.function}")
        
        print(f"\n⚙️ 상세 제조 공정:")
        for process in result.detailed_process:
            print(f"\n   {process.step_number}단계 [{process.phase}상]:")
            print(f"      {process.description}")
            if process.temperature:
                print(f"      온도: {process.temperature}")
            if process.time:
                print(f"      시간: {process.time}")
            if process.notes:
                print(f"      주의: {process.notes}")
        
        print(f"\n🔍 품질 관리:")
        print(f"   • pH: {result.quality_control.ph_range}")
        print(f"   • 외관: {result.quality_control.appearance}")
        print(f"   • 안정성 테스트:")
        for test in result.quality_control.stability_test:
            print(f"     - {test}")
        
        print(f"\n📦 보관 및 유통:")
        print(f"   • 보관조건: {result.storage_conditions}")
        print(f"   • 유통기한: {result.shelf_life}")
        
        print(f"\n⚠️ 주의사항:")
        for precaution in result.precautions:
            print(f"   • {precaution}")
        
        print(f"\n✅ 장점:")
        for pro in result.pros:
            print(f"   • {pro}")
        
        print(f"\n❌ 단점:")
        for con in result.cons:
            print(f"   • {con}")
        
        print("\n" + "="*60)
        
        # JSON 형태로도 출력
        print("\n📄 JSON 형태 결과:")
        result_dict = result.model_dump()
        print(json.dumps(result_dict, indent=2, ensure_ascii=False))

def main():
    # 메인 실행 함수 - 사용자 인터페이스 및 처방 생성 루프
    print("🌟 화장품 처방 AI 프로그램 (엘라스틱서치 연동)")
    print("="*50)
    
    formulator = CosmeticFormulatorWithES()
    
    while True:
        print("\n💡 화장품 정보를 입력해주세요:")
        
        # 사용자 입력 받기 (기본값 제공)
        ingredients = input("📝 전성분 목록 (쉼표로 구분) [기본값: 정제수, 글리세린, 니아신아마이드, 알부틴, 아스코르빅애시드, 하이드로퀴논, 글리콜산, 히알루론산, 판테놀, 토코페롤]: ").strip()
        if not ingredients:
            ingredients = "정제수, 글리세린, 니아신아마이드, 알부틴, 아스코르빅애시드, 하이드로퀴논, 글리콜산, 히알루론산, 판테놀, 토코페롤"
            print(f"기본값 사용: {ingredients}")
            
        formulation = input("🧴 제형 (예: 크림, 로션, 세럼 등) [기본값: 미백세럼]: ").strip()
        if not formulation:
            formulation = "미백세럼"
            print(f"기본값 사용: {formulation}")
            
        direction = input("🎯 개발방향 (예: 미백, 주름개선, 보습 등) [기본값: 집중 미백 및 피부톤 개선]: ").strip()
        if not direction:
            direction = "집중 미백 및 피부톤 개선"
            print(f"기본값 사용: {direction}")
        
        # 성분 상세 정보 준비 후 처방 생성
        ingredient_list = formulator.parse_ingredients(ingredients)
        ingredient_details: List[Dict[str, Any]] = []
        
        if formulator.es_connected and formulator.es_searcher:
            print(f"🔍 엘라스틱서치에서 {len(ingredient_list)}개 성분 정보 검색 중...")
            for ingredientName in ingredient_list:
                print(f"🔍 '{ingredientName}' 성분 검색 중...")
                ingredient_payload = {
                    "id": 0,
                    "repName": ingredientName,
                    "repNameEn": ingredientName
                }
                detail, found, found_id = formulator.fetch_single_ingredient_detail(ingredient_payload)
                ingredient_details.append(detail)                
        else:
            # 연결되지 않은 경우 기본 정보를 구성
            for ingredientName in ingredient_list:
                ingredient_payload = {
                    "id": 0,
                    "repName": ingredientName,
                    "repNameEn": ""
                }
                ingredient_details.append(
                    formulator.build_default_ingredient_detail_from_input(ingredient_payload)
                )

        ingredients_found = 0
        for ingredient_detail in ingredient_details:
            info = ingredient_detail.get("info", {}) if isinstance(ingredient_detail, dict) else {}
            es_id = info.get("id")
            if es_id is not None and isinstance(es_id, (int, float)) and es_id > 0:
                ingredients_found += 1

        print(f"✅ {ingredients_found}/{len(ingredients)}개 성분 정보를 찾았습니다.")
        print("✅ 성분 정보 수집 완료")

        result = formulator.create_formula(ingredient_details, formulation, direction)
        
        # 결과 출력
        formulator.display_result(result)
        
        # 계속 여부 확인
        continue_choice = input("\n다른 처방을 생성하시겠습니까? (y/n): ").strip().lower()
        if continue_choice not in ['y', 'yes', '예', 'ㅇ']:
            break
    
    print("\n👋 화장품 처방 AI를 이용해주셔서 감사합니다!")

if __name__ == "__main__":
    main()
