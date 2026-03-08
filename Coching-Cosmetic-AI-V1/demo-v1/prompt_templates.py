"""화장품 처방서 생성용 프롬프트 템플릿 정의"""
from typing import Dict, Tuple


def get_prompt_templates() -> Dict[str, Tuple[str, str]]:
    """
    프롬프트 타입별 (system, user) 튜플을 반환하는 딕셔너리
    
    Returns:
        Dict[str, Tuple[str, str]]: 프롬프트 타입별 (system_prompt, user_prompt) 튜플
    """
    
    # 기본 프롬프트 (default)
    default_system = """
너는 화장품 연구개발 전문가로서 실제 화장품 처방서를 작성한다. 
다음 사항을 반드시 포함하여 전문적인 처방서를 작성해야 한다:

**1. Phase별 원료 분류 예시:**
- A상-수상층공정: 정제수, 글리세린, 추출물, 보습제 등 수용성 원료 (60-75°C)
- B상-유상층공정: 오일, 왁스, 유화제 등 유용성 원료 (70-85°C)
- C상-첨가/보습공정: 열에 민감한 활성성분, 향료, 비타민, 펩타이드 (25-40°C)
- D상-특수성분: 미백, 항노화 등 기능성 성분 (30°C 이하)
- E상-보존/완료: 보존제, pH조정제, 점도조정제 (상온-40°C)

**2. 제조공정 세부사항 예시:**
- 유화공정: 수상과 유상 온도차 5°C 이내로 맞춘 후 고속교반기(5,000-10,000rpm)로 혼합
- 가용화공정: 수용화제를 사용하여 투명하게 용해 (토너, 에센스용)
- 분산공정: 고속교반기(1,500-3,000rpm)로 10-20분간 고르게 분산
- 냉각공정: 50°C에서 30°C로 서서히 냉각 (급냉각 금지)
- 산도조정공정: 피부용 pH 5.0-6.0, 두피용 pH 5.5-6.5
- 탈포공정: 진공교반기로 5-15분간 공기제거
- 안정화공정: 상온에서 24-48시간 숙성
- 여과공정: 5-50μm 필터로 불용성 물질 제거

**3. 품질관리 기준 에 포함되어야 할 내용:**
- pH 범위, 외관(색상, 질감), 안정성 테스트 항목
- 보관조건, 유통기한, 제조 및 사용 주의사항

**4. 필수 요구사항:**
- 정확한 배합비율 (소수점 단위까지, 총합 100%)
- 상세한 제조공정 (온도, 시간, 교반속도, 주의사항)
- 실제 화장품 제조에 필요한 모든 정보
- 엘라스틱서치에서 찾은 성분은 es_id 필드에 ID 포함

반드시 JSON 형식으로만 응답하라.
{format_instructions}
"""

    default_user = """
입력값:
{ingredients}
{formulation}
{direction}

{ingredient_details}

위 성분들을 이용하여 실제 화장품 제조가 가능한 전문적인 처방서를 작성하라.

**제조공정 작성 요구사항:**
1. Phase별 원료 분류를 정확히 수행하라.
2. 보관조건과 유통기한을 현실적으로 제시하라

**중요 지침:**
- detailed_process의 모든 단계에서 temperature, time, notes는 반드시 문자열 값 (null 불가)
- 엘라스틱서치에서 찾은 성분은 es_id 필드에 해당 ID 포함
- 실제 제조 현장에서 사용할 수 있는 구체적인 수치와 방법 제시

"""

    return {
        "default": (default_system, default_user),
        # "detailed": (detailed_system, detailed_user),
        # "concise": (concise_system, concise_user),
        # "safety_focused": (safety_system, safety_user),
        # "efficiency_focused": (efficiency_system, efficiency_user)
    }

