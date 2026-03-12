# 가이드 처방 생성 스킬 (Compound Expansion + Precision Arithmetic)

$ARGUMENTS 제품의 가이드 처방를 생성합니다.

## 핵심 원칙
- 화장품 제조 현장의 **복합성분 블렌드(Compound/Blend)** 원료를 INCI 성분으로 전개·합산
- 소수점 부동소수 오차 방지를 위해 **정수 연산(wt% × 100)** 필수
- 처방서(제조용) + 전성분(규제용) **이중 문서** 동시 생성
- 두 문서의 총 wt% 합계는 반드시 **100.00%** 일치

## 실행 순서

### Phase 1: 처방 설계 (투입 기준)
1. 사용자 요청 분석 — 제품타입, 피부타입, 특수 요구사항 파악
2. DB 조회 — `ingredient_master`, `guide_cache` 기존 처방 확인
3. 투입 원료 선정 + wt% 배분 (처방서 초안)

### Phase 2: 복합성분 전개 + INCI 합산
4. 각 투입 원료 분류:
   - **SINGLE**: 단일 INCI 원료
   - **COMPOUND**: 복합성분 블렌드 (DB 또는 TDS 기반)
   - **BALANCE**: 정제수(Aqua) — 밸런스 역산 대상
   - **UNKNOWN**: 미확인 → 리서치 트리거
5. COMPOUND 원료 전개:
   ```
   구성_INCI_wt% = 복합원료_투입wt% × 구성_INCI_비율(fraction)
   ```
   - 구성 비율 합계 = 1.000 검증
   - 반올림 오차 → Largest Remainder Method로 조정
6. INCI 합산:
   - 동일 INCI 중복 → 전부 합산 후 단일 항목
   - 동일 INCI 다른 등급(예: Dimethicone 5cSt + 350cSt) → 합산
   - 향료(Fragrance/Parfum) → 단일 항목 표기 (전개 안 함)
   - 복합성분 내 Aqua → 밸런스 Aqua와 별도 관리

### Phase 3: 정수 연산 + 밸런스 역산 (PRECISION-ARITHMETIC)
7. 정수 변환: `int_value = round(wt% × 100)`
8. 밸런스 역산:
   ```
   balance_excluded_sum = sum(비-Aqua 성분 int_value)
   aqua_int = 10000 - balance_excluded_sum
   ```
   - 복합 전개 Aqua는 밸런스 역산에서 **제외** 후 최종 출력 시 합산
9. **3단계 검증**:
   - 검증1: `sum(all_int_values) == 10000`
   - 검증2: `sum / 100 == 100.00`
   - 검증3: `Aqua_int == 역산값` 교차 검증

### Phase 4: 출력 + 품질 검증
10. **출력 A — 처방서 (제조용)**: 투입 원료명 기준, 복합성분 상품명 표기, 제조 순서 포함
11. **출력 B — 전성분 (규제용)**: INCI명 기준, 구성 INCI 전개·합산, 함량 내림차순 정렬 (1% 이하 순서 무관)
12. 최종 **10항목 체크리스트** 검증:
    - [ ] 모든 투입 원료를 SINGLE/COMPOUND/BALANCE 분류
    - [ ] COMPOUND 구성 비율 합계 = 1.000
    - [ ] 정수 연산 사용 (소수점 직접 합산 금지)
    - [ ] Largest Remainder Method 반올림 조정
    - [ ] 동일 INCI 모두 합산 (단일 + 전개값)
    - [ ] 향료 단일 항목 처리
    - [ ] 복합성분 내 Aqua를 밸런스 역산에서 제외
    - [ ] 3단계 검증 통과
    - [ ] 처방서 + 전성분 두 문서 생성
    - [ ] 두 문서 총 wt% = 100.00% 일치

## 복합성분 DB (등록 목록)

| # | 상품명 | 공급사 | 구성 성분 |
|---|--------|--------|-----------|
| 1 | Bentone Gel MIO | Elementis | Cyclopentasiloxane(0.850), Disteardimonium Hectorite(0.100), Propylene Carbonate(0.050) |
| 2 | Dow Corning 9040 | Dow | 실리콘 에멀전 (2종) |
| 3 | Olivem 1000 | Hallstar | O/W 유화제 (2종) |
| 4 | Emulsimousse | Gattefossé | O/W 유화제 (3종) |
| 5 | Euxyl PE 9010 | Schülke | 방부제 블렌드 (2종) |
| 6 | Optiphen Plus | Ashland | 방부제 블렌드 (3종) |
| 7 | Tinosorb M | BASF | 자외선차단 분산체 (3종) |
| 8 | Sepigel 305 | Seppic | 증점 유화제 (3종) |
| 9 | Lanol 99 | Seppic | 에스터 오일 블렌드 (2종) |
| 10 | 향료 (Fragrance) | — | 단일 표기 처리 |

> DB 미등록 복합성분 감지 시 → 리서치 트리거 (TDS 기반 구성 비율 확인)

## 주의사항
- 소수점 직접 합산 **절대 금지** → 정수(×100) 변환 후 합산
- 복합성분 내 Aqua와 밸런스 Aqua **별도 관리** 필수
- 처방서와 전성분 wt% 합계 불일치 시 → 즉시 재검증
- 10항목 체크리스트 전부 통과해야만 최종 출력
