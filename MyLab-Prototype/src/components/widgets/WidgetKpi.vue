<template>
  <div class="kpi-widget">
    <div class="kpi-grid">
      <div class="kpi-item" v-for="kpi in kpis" :key="kpi.label">
        <div class="kpi-top">
          <span class="kpi-label">{{ kpi.label }}</span>
          <span class="kpi-icon" :style="{ background: kpi.iconBg, color: kpi.iconColor }">{{ kpi.icon }}</span>
        </div>
        <div class="kpi-value" :style="{ color: kpi.iconColor }">
          {{ kpi.value }} <span class="kpi-unit">{{ kpi.unit }}</span>
        </div>
        <div class="kpi-sub">{{ kpi.sub }}</div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useFormulaStore } from '../../stores/formulaStore.js'

const { draftCount, reviewCount, doneCount, formulas } = useFormulaStore()

// 이번 달 완료 처방 수
const thisMonthDone = computed(() => {
  const now = new Date()
  return formulas.value.filter(f => {
    if (f.status !== 'done') return false
    const d = new Date(f.updated_at)
    return d.getFullYear() === now.getFullYear() && d.getMonth() === now.getMonth()
  }).length
})

// 진행중 처방 (draft + review 합계)
const activeCount = computed(() => draftCount.value + reviewCount.value)

// 안정성 진행 배치 수 (mock: review 처방 기준)
const stabilityCount = computed(() => reviewCount.value)

// 규제 체크 건수 (mock: 총 처방 × 원료 평균 수 기반 추정)
const regulationCount = computed(() => {
  const total = formulas.value.reduce((sum, f) => {
    return sum + (f.formula_data?.ingredients?.length || 0)
  }, 0)
  return total
})

const kpis = computed(() => [
  {
    label: '진행중 처방',
    value: activeCount.value,
    unit: '건',
    icon: '◎',
    iconColor: '#b8935a',
    iconBg: '#f0e8d8',
    sub: `초안 ${draftCount.value} · 검토 ${reviewCount.value}`,
  },
  {
    label: '이번 달 완료',
    value: thisMonthDone.value,
    unit: '건',
    icon: '✓',
    iconColor: '#3a9068',
    iconBg: '#f0f8f4',
    sub: `누적 완료 ${doneCount.value}건`,
  },
  {
    label: '안정성 진행',
    value: stabilityCount.value,
    unit: '배치',
    icon: '⏱',
    iconColor: '#3a6fa8',
    iconBg: '#f0f4fb',
    sub: '50°C / RT 조건',
  },
  {
    label: '규제 체크',
    value: regulationCount.value,
    unit: '건',
    icon: '⚠',
    iconColor: '#7c5cbf',
    iconBg: '#f4f0fb',
    sub: '성분 규제 스캔 완료',
  },
])
</script>

<style scoped>
.kpi-grid { display: grid; grid-template-columns: repeat(2, 1fr); gap: 10px; height: 100%; }
.kpi-item {
  background: var(--bg);
  border: 1px solid var(--border);
  border-radius: 8px;
  padding: 12px 14px;
  display: flex;
  flex-direction: column;
}
.kpi-top { display: flex; justify-content: space-between; align-items: flex-start; }
.kpi-label { font-size: 11px; font-family: var(--font-mono); text-transform: uppercase; color: var(--text-dim); letter-spacing: 0.8px; }
.kpi-icon {
  width: 22px; height: 22px; border-radius: 5px;
  display: flex; align-items: center; justify-content: center; font-size: 11px;
}
.kpi-value { font-size: 22px; font-weight: 700; margin-top: 6px; }
.kpi-unit { font-size: 10px; color: var(--text-dim); font-weight: 400; }
.kpi-sub { font-size: 11px; color: var(--text-dim); margin-top: 4px; font-family: var(--font-mono); }
</style>
