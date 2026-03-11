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
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useFormulaStore } from '../../stores/formulaStore.js'
import { useProjectStore } from '../../stores/projectStore.js'

const { totalCount, reviewCount, doneCount } = useFormulaStore()
const { projects } = useProjectStore()

const kpis = computed(() => [
  { label: '총 처방', value: totalCount.value, unit: '건', icon: '⚗', iconColor: '#b8935a', iconBg: '#f0e8d8' },
  { label: '진행중', value: reviewCount.value, unit: '건', icon: '◎', iconColor: '#b07820', iconBg: '#fdf8f0' },
  { label: '완료', value: doneCount.value, unit: '건', icon: '✓', iconColor: '#3a9068', iconBg: '#f0f8f4' },
  { label: '프로젝트', value: projects.value.length, unit: '개', icon: '◈', iconColor: '#3a6fa8', iconBg: '#f0f4fb' },
])
</script>

<style scoped>
.kpi-grid { display: grid; grid-template-columns: repeat(2, 1fr); gap: 10px; height: 100%; }
.kpi-item {
  background: var(--bg);
  border: 1px solid var(--border);
  border-radius: 8px;
  padding: 12px 14px;
}
.kpi-top { display: flex; justify-content: space-between; align-items: flex-start; }
.kpi-label { font-size: 9px; font-family: monospace; text-transform: uppercase; color: var(--text-dim); letter-spacing: 0.8px; }
.kpi-icon {
  width: 22px; height: 22px; border-radius: 5px;
  display: flex; align-items: center; justify-content: center; font-size: 11px;
}
.kpi-value { font-size: 22px; font-weight: 700; margin-top: 6px; }
.kpi-unit { font-size: 10px; color: var(--text-dim); font-weight: 400; }
</style>
