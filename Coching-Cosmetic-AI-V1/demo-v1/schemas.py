"""화장품 처방서 JSON 스키마 정의"""
from pydantic import BaseModel, Field
from typing import List, Optional


class Ingredient(BaseModel):
    """성분 모델"""
    name: str = Field(..., description="성분명")
    percentage: float = Field(..., description="배합 비율 (숫자)")
    function: str = Field(..., description="성분의 주요 기능")
    es_id: Optional[int] = Field(None, description="엘라스틱서치 ID")


class Phase(BaseModel):
    """Phase 모델"""
    phase_name: str = Field(..., description="Phase 명 (예: A상_수상, B상_유상, C상_첨가/보습, D상_특수성분, E상_보존/완료)")
    temperature: Optional[str] = Field(None, description="온도 조건 (예: 70°C, 40°C, 상온)")
    ingredients: List[Ingredient] = Field(..., description="해당 Phase에 포함된 성분들")


class DetailedProcess(BaseModel):
    """상세 제조 공정 모델"""
    step_number: int = Field(..., description="단계 번호")
    phase: str = Field(..., description="해당 Phase (A~Z)")
    description: str = Field(..., description="상세 공정 설명")
    temperature: Optional[str] = Field("상온", description="온도 조건")
    time: Optional[str] = Field("10분", description="소요 시간")
    notes: Optional[str] = Field("균일하게 혼합", description="특별 주의사항")


class QualityControl(BaseModel):
    """품질 관리 기준 모델"""
    ph_range: str = Field(..., description="pH 범위")
    appearance: str = Field(..., description="외관 (색상, 질감)")
    stability_test: List[str] = Field(..., description="안정성 테스트 항목")


class FormulaResponse(BaseModel):
    """화장품 처방서 응답 모델"""
    overview: str = Field(..., description="처방 개요")
    total_percentage: float = Field(..., description="총 배합비 (100%)")
    phases: List[Phase] = Field(..., description="Phase별 구성")
    detailed_process: List[DetailedProcess] = Field(..., description="상세 제조 공정")
    quality_control: QualityControl = Field(..., description="품질 관리 기준")
    precautions: List[str] = Field(..., description="제조 및 사용 주의사항")
    storage_conditions: str = Field(..., description="보관 조건")
    shelf_life: str = Field(..., description="유통기한")
    pros: List[str] = Field(..., description="장점")
    cons: List[str] = Field(..., description="단점")

