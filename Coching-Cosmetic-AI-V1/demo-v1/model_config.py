"""AI 모델 설정 정의"""
from typing import List, Dict, Any


def get_ai_model_list() -> List[Dict[str, Any]]:
    """
    사용 가능한 AI 모델 목록을 반환
    
    Returns:
        List[Dict[str, Any]]: 모델 설정 딕셔너리 리스트
    """
    return [
        {"code": "gpt-5"        , "name": "gpt-5"       , "max_tokens": 22000   , "method": "format_instructions"    ,"temp_min": 0 , "temp_max": 1},
        {"code": "gpt-5-mini"   , "name": "gpt-5-mini"  , "max_tokens": 22000   , "method": "format_instructions"    ,"temp_min": 0 , "temp_max": 1},
        {"code": "gpt-5-nano"   , "name": "gpt-5-nano"  , "max_tokens": 0       , "method": "format_instructions"    ,"temp_min": 0 , "temp_max": 1},
        {"code": "gpt-4.1"      , "name": "gpt-4.1"     , "max_tokens": 8000    , "method": "format_instructions"    ,"temp_min": 0 , "temp_max": 1},
        {"code": "gpt-4.1-mini" , "name": "gpt-4.1-mini", "max_tokens": 8000    , "method": "format_instructions"    ,"temp_min": 0 , "temp_max": 1},
        {"code": "gpt-4.1-nano" , "name": "gpt-4.1-nano", "max_tokens": 8000    , "method": "format_instructions"    ,"temp_min": 0 , "temp_max": 1},
        {"code": "gpt-4o"       , "name": "gpt-4o"      , "max_tokens": 16384   , "method": "format_instructions"    ,"temp_min": 0 , "temp_max": 1},
        {"code": "gpt-4o-mini"  , "name": "gpt-4o-mini" , "max_tokens": 8000    , "method": "format_instructions"    ,"temp_min": 0 , "temp_max": 1},
    ]


def get_default_model_config() -> Dict[str, Any]:
    """
    기본 모델 설정을 반환
    
    Returns:
        Dict[str, Any]: 기본 모델 설정 딕셔너리
    """
    return {"code": "default", "name": "default", "max_tokens": 4000, "temp_min": 0, "temp_max": 2}


def get_model_config(model_code: str) -> Dict[str, Any]:
    """
    모델 코드에 해당하는 설정을 반환
    
    Args:
        model_code: 모델 코드
        
    Returns:
        Dict[str, Any]: 모델 설정 딕셔너리, 없으면 기본 설정 반환
    """
    model_list = get_ai_model_list()
    default_config = get_default_model_config()
    return next((cfg for cfg in model_list if cfg["code"] == model_code), default_config)

