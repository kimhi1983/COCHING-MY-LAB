<template>
  <div class="dashboard">
    <!-- Toolbar -->
    <div class="dash-toolbar">
      <div class="toolbar-left">
        <span v-if="editMode" class="edit-badge">편집 모드</span>
      </div>
      <div class="toolbar-right">
        <button v-if="editMode" class="btn btn-sm btn-ghost" @click="showAddPanel = !showAddPanel">
          + 위젯 추가
        </button>
        <button v-if="editMode" class="btn btn-sm btn-ghost" @click="onReset">기본값 복원</button>
        <button class="btn btn-sm" :class="editMode ? 'btn-primary' : 'btn-ghost'" @click="toggleEdit">
          {{ editMode ? '✓ 편집 완료' : '⚙ 레이아웃 편집' }}
        </button>
      </div>
    </div>

    <!-- Add Widget Panel -->
    <div v-if="showAddPanel && editMode" class="add-panel">
      <div class="add-panel-title">위젯 추가</div>
      <div class="add-grid">
        <div class="add-card" v-for="w in availableWidgets" :key="w.id" @click="onAddWidget(w.id)">
          <div class="add-icon">{{ w.icon }}</div>
          <div class="add-label">{{ w.label }}</div>
          <div class="add-desc">{{ w.description }}</div>
        </div>
      </div>
      <div v-if="!availableWidgets.length" class="add-empty">
        모든 위젯이 이미 추가되어 있습니다
      </div>
    </div>

    <!-- Grid Layout -->
    <GridLayout
      :layout="currentLayout"
      :col-num="6"
      :row-height="60"
      :is-draggable="editMode"
      :is-resizable="editMode"
      :margin="[12, 12]"
      :use-css-transforms="true"
      @layout-updated="onLayoutUpdated"
    >
      <GridItem
        v-for="item in currentLayout"
        :key="item.i"
        :x="item.x" :y="item.y" :w="item.w" :h="item.h" :i="item.i"
        :min-w="getMinW(item.i)" :min-h="getMinH(item.i)"
        class="widget-item"
        :class="{ editing: editMode }"
      >
        <div class="widget-card">
          <div class="widget-header">
            <span class="widget-title">{{ getWidgetLabel(item.i) }}</span>
            <button v-if="editMode" class="widget-remove" @click="onRemoveWidget(item.i)" title="위젯 제거">×</button>
          </div>
          <div class="widget-body">
            <component :is="widgetComponents[item.i]" />
          </div>
        </div>
      </GridItem>
    </GridLayout>
  </div>
</template>

<script setup>
import { ref, computed, markRaw } from 'vue'
import { GridLayout, GridItem } from 'grid-layout-plus'
import { useWidgetStore, WIDGET_CATALOG } from '../stores/widgetStore.js'

import WidgetKpi from '../components/widgets/WidgetKpi.vue'
import WidgetRecentFormulas from '../components/widgets/WidgetRecentFormulas.vue'
import WidgetQuickActions from '../components/widgets/WidgetQuickActions.vue'
import WidgetActiveFormulas from '../components/widgets/WidgetActiveFormulas.vue'
import WidgetProjectSummary from '../components/widgets/WidgetProjectSummary.vue'
import WidgetStatusChart from '../components/widgets/WidgetStatusChart.vue'
import WidgetMemo from '../components/widgets/WidgetMemo.vue'
import WidgetStability from '../components/widgets/WidgetStability.vue'
import WidgetRegulation from '../components/widgets/WidgetRegulation.vue'
import WidgetTodayLog from '../components/widgets/WidgetTodayLog.vue'

const widgetComponents = {
  kpi: markRaw(WidgetKpi),
  recent: markRaw(WidgetRecentFormulas),
  quick: markRaw(WidgetQuickActions),
  active: markRaw(WidgetActiveFormulas),
  projects: markRaw(WidgetProjectSummary),
  chart: markRaw(WidgetStatusChart),
  memo: markRaw(WidgetMemo),
  stability: markRaw(WidgetStability),
  regulation: markRaw(WidgetRegulation),
  todaylog: markRaw(WidgetTodayLog),
}

const { layout, availableWidgets, saveLayout, addWidget, removeWidget, resetLayout } = useWidgetStore()

const editMode = ref(false)
const showAddPanel = ref(false)

const currentLayout = computed({
  get: () => layout.value,
  set: (val) => saveLayout(val),
})

function toggleEdit() {
  editMode.value = !editMode.value
  if (!editMode.value) showAddPanel.value = false
}

function onLayoutUpdated(newLayout) {
  saveLayout(newLayout)
}

function onAddWidget(id) {
  addWidget(id)
}

function onRemoveWidget(id) {
  removeWidget(id)
}

function onReset() {
  if (confirm('레이아웃을 기본값으로 복원하시겠습니까?')) {
    resetLayout()
  }
}

function getWidgetLabel(id) {
  return WIDGET_CATALOG.find(w => w.id === id)?.label || id
}

function getMinW(id) {
  return WIDGET_CATALOG.find(w => w.id === id)?.minW || 1
}

function getMinH(id) {
  return WIDGET_CATALOG.find(w => w.id === id)?.minH || 1
}
</script>

<style scoped>
.dash-toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}
.toolbar-right { display: flex; gap: 6px; }
.edit-badge {
  font-size: 12px;
  font-weight: 600;
  color: var(--accent);
  background: var(--accent-light);
  padding: 4px 10px;
  border-radius: 4px;
  border: 1px solid var(--accent-dim);
  animation: pulse-badge 2s ease-in-out infinite;
}
@keyframes pulse-badge {
  0%, 100% { opacity: 1; }
  50% { opacity: 0.7; }
}

.btn { border: none; border-radius: 6px; font-size: 13px; font-weight: 600; cursor: pointer; transition: all 0.15s; }
.btn-sm { padding: 6px 12px; }
.btn-primary { background: var(--accent); color: #fff; box-shadow: 0 2px 6px rgba(184,147,90,0.25); }
.btn-primary:hover { background: #a68350; }
.btn-ghost { background: transparent; border: 1px solid var(--border); color: var(--text-sub); }
.btn-ghost:hover { background: var(--bg); }

/* Add Widget Panel */
.add-panel {
  background: var(--surface);
  border: 1px solid var(--accent-dim);
  border-radius: var(--radius);
  padding: 16px;
  margin-bottom: 12px;
  box-shadow: 0 4px 12px rgba(0,0,0,0.06);
}
.add-panel-title {
  font-size: 14px;
  font-weight: 600;
  color: var(--text);
  margin-bottom: 10px;
}
.add-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(160px, 1fr));
  gap: 8px;
}
.add-card {
  padding: 12px;
  border: 1px dashed var(--border-mid);
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.15s;
  text-align: center;
}
.add-card:hover {
  border-color: var(--accent);
  background: var(--accent-light);
}
.add-icon { font-size: 20px; margin-bottom: 4px; }
.add-label { font-size: 13px; font-weight: 600; color: var(--text); }
.add-desc { font-size: 11px; color: var(--text-dim); margin-top: 2px; }
.add-empty { text-align: center; font-size: 13px; color: var(--text-dim); padding: 12px; }

/* Widget Cards */
.widget-item.editing {
  cursor: grab;
}
.widget-item.editing:active {
  cursor: grabbing;
}

.widget-card {
  background: var(--surface);
  border: 1px solid var(--border);
  border-radius: var(--radius);
  box-shadow: var(--shadow);
  height: 100%;
  display: flex;
  flex-direction: column;
  overflow: hidden;
  transition: border-color 0.15s, box-shadow 0.15s;
}
.editing .widget-card {
  border: 2px dashed var(--accent-dim);
  box-shadow: 0 0 0 1px var(--accent-dim), var(--shadow);
}
.editing .widget-card:hover {
  border-color: var(--accent);
  box-shadow: 0 0 0 2px rgba(184,147,90,0.3), 0 4px 16px rgba(0,0,0,0.1);
}

.widget-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 8px 12px;
  border-bottom: 1px solid var(--border);
  flex-shrink: 0;
}
.widget-title {
  font-size: 11px;
  font-family: var(--font-mono);
  text-transform: uppercase;
  letter-spacing: 1.2px;
  color: var(--text-dim);
}
.widget-remove {
  width: 20px; height: 20px;
  border: none;
  border-radius: 4px;
  background: var(--red-bg);
  color: var(--red);
  cursor: pointer;
  font-size: 14px;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.15s;
}
.widget-remove:hover {
  background: var(--red);
  color: #fff;
}

.widget-body {
  flex: 1;
  overflow: auto;
  padding: 8px;
  container-type: inline-size;
  container-name: widget;
}

/* grid-layout-plus overrides */
:deep(.vue-grid-item) {
  transition: all 0.2s ease;
}
:deep(.vue-grid-item.vue-grid-placeholder) {
  background: var(--accent-light) !important;
  border: 2px dashed var(--accent) !important;
  border-radius: var(--radius);
  opacity: 0.6;
}
:deep(.vue-grid-item > .vue-resizable-handle) {
  width: 24px;
  height: 24px;
  bottom: 0;
  right: 0;
  background: none;
  cursor: se-resize;
  z-index: 10;
}
:deep(.vue-grid-item > .vue-resizable-handle::after) {
  content: '';
  position: absolute;
  right: 4px;
  bottom: 4px;
  width: 12px;
  height: 12px;
  border-right: 3px solid var(--accent);
  border-bottom: 3px solid var(--accent);
  border-radius: 0 0 3px 0;
  opacity: 0.7;
  transition: opacity 0.15s;
}
:deep(.vue-grid-item > .vue-resizable-handle:hover::after) {
  opacity: 1;
  border-color: #a07030;
}
</style>
