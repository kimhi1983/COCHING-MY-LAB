# Figma → Vue 컴포넌트 변환 가이드

## 개요
Figma 디자인을 COCHING Vue 2 프로젝트의 실제 컴포넌트로 변환하는 워크플로우입니다.

---

## 1단계: Figma에서 디자인 정보 추출

### Figma for VS Code 활용
1. VS Code 사이드바 > Figma 아이콘 클릭
2. 변환할 컴포넌트 선택
3. **Inspect** 패널에서 확인:
   - 크기 (width, height)
   - 색상값 → `design-tokens.json`의 토큰과 매핑
   - 간격 (padding, margin, gap)
   - 폰트 크기, 굵기
   - 보더 라운드

### CSS 추출 방법
- Figma에서 요소 선택 → 우클릭 > "Copy as CSS"
- 또는 VS Code Figma 패널의 "Code" 탭에서 CSS 복사

---

## 2단계: Design Tokens 매핑

Figma 색상값을 COCHING CSS 변수로 매핑:

| Figma 값 | CSS 변수 | SCSS 변수 |
|---|---|---|
| `#ff385c` | `var(--color--primary)` | `$coching-primary` |
| `#222222` | `var(--color--bk)` | `$coching-black` |
| `#666666` | `var(--color--gray-666)` | `$coching-gray666` |
| `#999999` | `var(--color--gray-999)` | `$coching-gray999` |
| `#dddddd` | `var(--color--border-ddd)` | `$coching-border-ddd` |
| `#eeeeee` | `var(--color--border-eee)` | `$coching-border-eee` |
| `#ffffff` | `var(--color--wh)` | `$coching-white` |
| `rgba(255,56,92,0.08)` | — | `$coching-primary-hover` |
| `rgba(0,0,0,0.03)` | `var(--color--bg-hover)` | `$coching-bg-hover` |

### White Lab 테마 토큰 (실험실 페이지용)
| Figma 값 | CSS 변수 |
|---|---|
| `#f8f7f5` | `var(--wl-bg)` |
| `#b8935a` | `var(--wl-accent)` |
| `#1a1814` | `var(--wl-text)` |
| `#6b6560` | `var(--wl-text-sub)` |
| `#aba59d` | `var(--wl-text-dim)` |

---

## 3단계: Vue 컴포넌트 작성

### 기본 규칙
1. **COCHING Vue 2 프로젝트 구조를 따른다**
2. 아이콘은 CSS `background-image: url()` 패턴 사용
3. 스타일은 `<style lang="scss" scoped>` 또는 기존 style.css 클래스 활용
4. API 호출은 기존 패턴 (`@/api/coching/...`) 준수
5. mixin은 `ernsUtils` 사용

### 파일 배치
```
src/
├── views/coching/          # 페이지 컴포넌트
│   └── lab/                # 실험실 관련
│       └── ResearchOverview.vue
├── components/             # 공통 컴포넌트
│   ├── cards/
│   │   └── StatCard.vue
│   └── panels/
│       └── LogPanel.vue
└── assets/
    ├── css/style.css       # 글로벌 스타일 (기존)
    ├── scss/
    │   ├── _design-tokens.scss  # 자동 생성 토큰 변수
    │   └── style.scss      # 프로젝트 SCSS
    └── images/ic-*.svg     # 아이콘 파일
```

---

## 4단계: 변환 예시

### Figma 카드 → Vue StatCard 컴포넌트

**Figma 디자인 속성:**
- 배경: `#ffffff`, 테두리: `#ece9e3`, 라운드: `10px`
- 그림자: `0 1px 4px rgba(0,0,0,0.04)`
- 내부 패딩: `18px 20px`

**Vue 컴포넌트:**
```vue
<template>
  <div class="stat-card">
    <div class="stat-top">
      <span class="stat-label">{{ label }}</span>
      <span class="stat-icon" :style="{ background: iconBg }">{{ icon }}</span>
    </div>
    <div class="stat-value-row">
      <span class="stat-value">{{ value }}</span>
      <span class="stat-unit">{{ unit }}</span>
    </div>
  </div>
</template>

<script>
export default {
  name: 'StatCard',
  props: {
    label: { type: String, required: true },
    value: { type: [String, Number], required: true },
    unit: { type: String, default: '' },
    icon: { type: String, default: '' },
    iconBg: { type: String, default: '#f0e8d8' }
  }
}
</script>

<style lang="scss" scoped>
.stat-card {
  background: var(--color--wh);
  border: 1px solid #ece9e3;
  border-radius: 10px;
  padding: 18px 20px;
  box-shadow: 0 1px 4px rgba(0,0,0,0.04);
}
.stat-top {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 12px;
}
.stat-label {
  font-size: 10px;
  font-weight: 700;
  letter-spacing: 0.1em;
  text-transform: uppercase;
  color: #aba59d;
}
.stat-icon {
  width: 32px;
  height: 32px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 15px;
}
.stat-value-row {
  display: flex;
  align-items: baseline;
  gap: 5px;
}
.stat-value {
  font-size: 28px;
  font-weight: 800;
  letter-spacing: -1px;
  color: #1a1814;
}
.stat-unit {
  font-size: 12px;
  color: #6b6560;
  font-weight: 500;
}
</style>
```

### GNB 사이드바 아이콘 규칙

아이콘은 **CSS `::before` pseudo-element + `background-image`** 패턴:

```css
/* 기본 */
nav a.ic-home-lg::before {
  background-image: url(../images/ic-home-lg.svg);
}
/* 활성 상태 */
nav a.ic-home-lg.active::before {
  background-image: url(../images/ic-home-active-lg.svg);
}
```

새 아이콘 추가 시:
1. `src/assets/images/`에 `ic-{name}-lg.svg` 파일 추가
2. `src/assets/images/`에 `ic-{name}-active-lg.svg` (활성 상태) 추가
3. `style.css`에 위 패턴으로 CSS 규칙 추가

---

## 5단계: 로컬 토큰 동기화

Figma에서 색상 변경 시:

```bash
# 1. design-tokens.json 수정
# 2. SCSS 변수 파일 재생성
node scripts/sync-tokens-local.js --to-scss

# 또는 CSS 수정 후 토큰에 반영
node scripts/sync-tokens-local.js --to-json
```

GitHub에 push하면 GitHub Actions가 자동으로 양방향 동기화를 실행합니다.

---

## 체크리스트

Figma → Vue 변환 완료 시 확인사항:

- [ ] Design Tokens의 색상값 사용 (하드코딩 ✕)
- [ ] 기존 style.css의 클래스 재활용 (중복 스타일 ✕)
- [ ] 아이콘은 SVG + CSS background-image 패턴
- [ ] 반응형 고려 (모바일 breakpoint)
- [ ] 컴포넌트 props 정의 (재사용성)
- [ ] ernsUtils mixin 사용 (라우팅, API 에러 처리)
