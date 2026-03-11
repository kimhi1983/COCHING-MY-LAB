<template>
  <div class="chart-widget">
    <div class="donut-chart">
      <svg viewBox="0 0 120 120" class="donut-svg">
        <circle cx="60" cy="60" r="45" fill="none" stroke="var(--border)" stroke-width="12" />
        <circle v-for="(seg, i) in segments" :key="i" cx="60" cy="60" r="45"
          fill="none" :stroke="seg.color" stroke-width="12"
          :stroke-dasharray="seg.dash" :stroke-dashoffset="seg.offset"
          stroke-linecap="round" />
      </svg>
      <div class="donut-center">
        <div class="donut-value">{{ totalCount }}</div>
        <div class="donut-label">총 처방</div>
      </div>
    </div>
    <div class="legend">
      <div class="legend-item" v-for="item in legendItems" :key="item.label">
        <span class="legend-dot" :style="{ background: item.color }"></span>
        <span class="legend-text">{{ item.label }}</span>
        <span class="legend-val">{{ item.value }}</span>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useFormulaStore } from '../../stores/formulaStore.js'

const { totalCount, draftCount, reviewCount, doneCount } = useFormulaStore()

const circumference = 2 * Math.PI * 45 // ~282.74

const legendItems = computed(() => [
  { label: '초안', value: draftCount.value, color: 'var(--amber)' },
  { label: '검토중', value: reviewCount.value, color: 'var(--blue)' },
  { label: '완료', value: doneCount.value, color: 'var(--green)' },
])

const segments = computed(() => {
  const total = totalCount.value || 1
  const items = [
    { value: doneCount.value, color: 'var(--green)' },
    { value: reviewCount.value, color: 'var(--blue)' },
    { value: draftCount.value, color: 'var(--amber)' },
  ]
  let offset = circumference * 0.25 // start at top
  return items.map(item => {
    const pct = item.value / total
    const dash = `${pct * circumference} ${circumference}`
    const seg = { color: item.color, dash, offset: -offset }
    offset -= pct * circumference
    return seg
  })
})
</script>

<style scoped>
.chart-widget { display: flex; align-items: center; justify-content: center; gap: 16px; height: 100%; padding: 8px; }
.donut-chart { position: relative; width: 100px; height: 100px; flex-shrink: 0; }
.donut-svg { width: 100%; height: 100%; transform: rotate(-90deg); }
.donut-center {
  position: absolute; top: 50%; left: 50%; transform: translate(-50%, -50%); text-align: center;
}
.donut-value { font-size: 20px; font-weight: 700; color: var(--text); }
.donut-label { font-size: 8px; font-family: monospace; color: var(--text-dim); text-transform: uppercase; letter-spacing: 1px; }
.legend { display: flex; flex-direction: column; gap: 6px; }
.legend-item { display: flex; align-items: center; gap: 6px; font-size: 11px; }
.legend-dot { width: 8px; height: 8px; border-radius: 2px; flex-shrink: 0; }
.legend-text { color: var(--text-sub); min-width: 36px; }
.legend-val { font-family: monospace; font-weight: 600; color: var(--text); }
</style>
