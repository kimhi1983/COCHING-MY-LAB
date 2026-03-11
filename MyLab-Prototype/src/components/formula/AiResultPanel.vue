<template>
  <div class="ai-result-panel">
    <div class="panel-header">
      <div>
        <span class="section-label">AI RESULT</span>
        <span class="section-title">생성 결과</span>
      </div>
      <div class="header-actions">
        <span class="gen-time">처리시간: {{ elapsed }}초</span>
        <button class="btn-ghost" @click="$emit('regenerate')">재생성</button>
      </div>
    </div>

    <div class="result-body">
      <div class="result-desc">{{ result.description }}</div>

      <!-- Phases -->
      <div v-for="phase in result.phases" :key="phase.phase" class="phase-section">
        <div class="phase-title">Phase {{ phase.phase }} — {{ phase.name }} ({{ phase.temp }})</div>
        <table class="phase-table">
          <thead>
            <tr><th>성분명</th><th>%</th><th>기능</th></tr>
          </thead>
          <tbody>
            <tr v-for="ingName in phase.items" :key="ingName">
              <td>{{ ingName }}</td>
              <td class="cell-pct">{{ getIngPct(ingName) }}</td>
              <td class="cell-fn">{{ getIngFn(ingName) }}</td>
            </tr>
          </tbody>
        </table>
      </div>

      <!-- Process -->
      <div class="process-section">
        <div class="section-label" style="margin-bottom:8px">MANUFACTURING PROCESS · 제조 과정</div>
        <table class="process-table">
          <thead>
            <tr><th>단계</th><th>상</th><th>설명</th><th>온도</th><th>시간</th><th>참고</th></tr>
          </thead>
          <tbody>
            <tr v-for="s in result.process" :key="s.step">
              <td class="cell-num">{{ s.step }}</td>
              <td>{{ s.phase }}</td>
              <td>{{ s.desc }}</td>
              <td>{{ s.temp }}</td>
              <td>{{ s.time }}</td>
              <td class="cell-note">{{ s.note }}</td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>

    <div class="panel-footer">
      <button class="btn-ghost" @click="$emit('regenerate')">재생성</button>
      <button class="btn-primary" @click="$emit('save')">처방으로 저장</button>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({ result: Object })
defineEmits(['save', 'regenerate'])

const elapsed = computed(() => {
  if (!props.result?.generatedAt) return '-'
  return ((Date.now() - new Date(props.result.generatedAt).getTime()) / 1000).toFixed(1)
})

function getIngPct(name) {
  const ing = props.result.ingredients?.find(i => i.name === name)
  return ing ? ing.percentage.toFixed(1) : '-'
}
function getIngFn(name) {
  const ing = props.result.ingredients?.find(i => i.name === name)
  return ing?.function || ''
}
</script>

<style scoped>
.ai-result-panel {
  background: var(--surface);
  border: 1px solid var(--border);
  border-radius: var(--radius);
  overflow: hidden;
}
.panel-header {
  padding: 14px 20px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  border-bottom: 1px solid var(--border);
}
.section-label { font-size: 9px; font-family: monospace; text-transform: uppercase; letter-spacing: 2.5px; color: var(--text-dim); }
.section-title { font-size: 13px; font-weight: 600; color: var(--text); margin-left: 8px; }
.header-actions { display: flex; align-items: center; gap: 12px; }
.gen-time { font-size: 10px; font-family: monospace; color: var(--text-dim); }

.result-body { padding: 20px; }
.result-desc {
  font-size: 13px;
  color: var(--text-sub);
  line-height: 1.6;
  padding: 12px 16px;
  background: var(--accent-light);
  border: 1px solid var(--accent-dim);
  border-radius: 6px;
  margin-bottom: 20px;
  white-space: pre-line;
}

.phase-section { margin-bottom: 16px; }
.phase-title {
  font-size: 12px;
  font-weight: 600;
  color: var(--accent);
  margin-bottom: 6px;
  padding: 4px 8px;
  background: var(--accent-light);
  border-radius: 4px;
  display: inline-block;
}
.phase-table, .process-table { width: 100%; border-collapse: collapse; margin-bottom: 8px; }
.phase-table th, .process-table th {
  background: var(--bg);
  font-size: 9px;
  font-family: monospace;
  text-transform: uppercase;
  letter-spacing: 1.5px;
  color: var(--text-dim);
  padding: 6px 10px;
  text-align: left;
}
.phase-table td, .process-table td {
  padding: 6px 10px;
  font-size: 12px;
  border-bottom: 1px solid var(--border);
  color: var(--text);
}
.cell-pct { font-family: monospace; font-weight: 700; text-align: right; }
.cell-fn { font-size: 11px; color: var(--text-sub); }
.cell-num { font-family: monospace; color: var(--text-dim); font-size: 10px; }
.cell-note { font-size: 11px; color: var(--text-dim); }

.process-section { margin-top: 20px; }

.panel-footer {
  padding: 14px 20px;
  border-top: 1px solid var(--border);
  display: flex;
  justify-content: flex-end;
  gap: 8px;
}
.btn-primary {
  padding: 8px 16px;
  border-radius: 6px;
  border: none;
  background: var(--accent);
  color: #fff;
  font-size: 13px;
  font-weight: 600;
  cursor: pointer;
  box-shadow: 0 2px 8px rgba(184,147,90,0.3);
}
.btn-primary:hover { background: #a68350; }
.btn-ghost {
  padding: 8px 16px;
  border-radius: 6px;
  border: 1px solid var(--border);
  background: transparent;
  color: var(--text-sub);
  font-size: 13px;
  cursor: pointer;
}
.btn-ghost:hover { background: var(--bg); }
</style>
