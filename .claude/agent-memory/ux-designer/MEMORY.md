# UX Designer Agent Memory

## MyLab 디자인 시스템 (2026-03-11)
- 디자인 토큰 원본: `e:/COCHING/업그레이드/white_lab_dashboard.jsx`
- 전체 명세서: `e:/COCHING/docs/mylab-ux-design-spec.md`
- 골드 액센트: #b8935a, 배경: #f8f7f5, 본문: #1a1814
- 세리프 폰트: Georgia + Noto Serif KR (MyLab 전용, 기존 BO는 맑은고딕)
- 상태 색상: green=#3a9068, amber=#b07820, blue=#3a6fa8, purple=#7c5cbf, red=#c44e4e

## 기존 BO-Vue UI 패턴
- Bootstrap-Vue + AG-Grid + v-select + validation-observer
- 폰트: 맑은고딕, Malgun Gothic, Nanum Gothic (sans-serif)
- SCSS 변수: `Coching-BO-Vue/src/assets/scss/variables/_variables.scss`
- 공통 유틸: ernsUtils mixin, ernsAgGrid mixin
- 로딩: loading(true/false), 에러: alertError(err), 성공: alertSuccess(msg)
- AG-Grid 칼럼 상수: `src/constants/agGrid.js`
- AI 처방 관련: AiPrscDemoForm.vue, AiPrscResultTable.vue, AiPrscCard.vue

## 주의사항
- AG-Grid는 모바일에서 사용성 떨어짐 -> 모바일 시 카드 뷰 전환 필요
- MyLab 세리프 폰트와 기존 BO 산세리프 폰트 혼용 시 하이브리드 전략 필요
