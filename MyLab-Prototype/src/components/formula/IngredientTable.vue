<template>
  <div class="ingredient-table-wrap">
    <div class="table-header">
      <span class="section-label">INGREDIENT TABLE</span>
      <span class="section-title">원료 배합표</span>
      <button v-if="editable" class="btn-add" @click="addRow">+ 원료 추가</button>
    </div>
    <table class="ingredient-table">
      <thead>
        <tr>
          <th style="width:40px">#</th>
          <th style="width:60px">Phase</th>
          <th>원료명</th>
          <th>INCI</th>
          <th style="width:80px">%(wt)</th>
          <th>기능</th>
          <th v-if="editable" style="width:40px"></th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="(ing, idx) in ingredients" :key="idx" :class="'phase-row-' + (ing.phase || 'none')">
          <td class="cell-num">{{ idx + 1 }}</td>
          <td class="cell-phase">
            <select v-if="editable" v-model="ing.phase" class="phase-select" @change="onPhaseChange"
              :class="'phase-select-' + (ing.phase || '')">
              <option value="">—</option>
              <option value="A">A</option>
              <option value="B">B</option>
              <option value="C">C</option>
              <option value="D">D</option>
            </select>
            <span v-else-if="ing.phase" class="phase-chip" :class="'phase-chip-' + ing.phase">{{ ing.phase }}</span>
            <span v-else class="phase-chip phase-chip-none">—</span>
          </td>
          <td>
            <input v-if="editable" v-model="ing.name" class="cell-input" placeholder="원료명">
            <span v-else>{{ ing.name }}</span>
          </td>
          <td class="cell-inci">
            <input v-if="editable" v-model="ing.inci_name" class="cell-input" placeholder="INCI">
            <span v-else>{{ ing.inci_name }}</span>
          </td>
          <td class="cell-pct">
            <input v-if="editable" v-model.number="ing.percentage" type="number" step="0.1" class="cell-input pct-input" @input="onPctChange">
            <span v-else>{{ ing.percentage }}</span>
          </td>
          <td>
            <input v-if="editable" v-model="ing.function" class="cell-input" placeholder="기능">
            <span v-else>{{ ing.function }}</span>
          </td>
          <td v-if="editable">
            <button class="btn-del" @click="removeRow(idx)">×</button>
          </td>
        </tr>
        <tr v-if="ingredients.length === 0">
          <td :colspan="editable ? 7 : 6" class="empty-row">원료를 추가해주세요</td>
        </tr>
      </tbody>
      <tfoot v-if="ingredients.length">
        <!-- Phase 소계 -->
        <tr v-for="ps in phaseSummary" :key="'ps-' + ps.phase" class="phase-subtotal-row">
          <td></td>
          <td>
            <span class="phase-chip" :class="'phase-chip-' + ps.phase">{{ ps.phase }}</span>
          </td>
          <td colspan="2" style="font-size:11px; color:var(--text-sub)">Phase {{ ps.phase }} 소계</td>
          <td class="cell-pct" style="font-size:11px; font-weight:600">{{ ps.total.toFixed(1) }}</td>
          <td :colspan="editable ? 2 : 1"></td>
        </tr>
        <!-- 합계 -->
        <tr class="total-row">
          <td></td>
          <td></td>
          <td colspan="2" style="text-align:right; font-weight:600">합계</td>
          <td class="cell-pct total-pct" :class="{ over: totalPct > 100.5, under: totalPct < 99.5 }">
            {{ totalPct.toFixed(1) }}
          </td>
          <td :colspan="editable ? 2 : 1"></td>
        </tr>
      </tfoot>
    </table>
  </div>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  ingredients: { type: Array, default: () => [] },
  editable: { type: Boolean, default: false },
})
const emit = defineEmits(['update:ingredients'])

const PHASES = ['A', 'B', 'C', 'D']

const totalPct = computed(() =>
  props.ingredients.reduce((s, i) => s + (Number(i.percentage) || 0), 0)
)

const phaseSummary = computed(() => {
  return PHASES.map(phase => ({
    phase,
    total: props.ingredients
      .filter(i => i.phase === phase)
      .reduce((s, i) => s + (Number(i.percentage) || 0), 0),
  })).filter(ps => ps.total > 0)
})

function addRow() {
  const updated = [...props.ingredients, { name: '', inci_name: '', percentage: 0, function: '', supplier: '', phase: '' }]
  emit('update:ingredients', updated)
}

function removeRow(idx) {
  const updated = props.ingredients.filter((_, i) => i !== idx)
  emit('update:ingredients', updated)
}

function onPctChange() {
  emit('update:ingredients', [...props.ingredients])
}

function onPhaseChange() {
  emit('update:ingredients', [...props.ingredients])
}
</script>

<style scoped>
.ingredient-table-wrap {
  background: var(--surface);
  border: 1px solid var(--border);
  border-radius: var(--radius);
  overflow: hidden;
}
.table-header {
  padding: 14px 20px;
  display: flex;
  align-items: center;
  gap: 12px;
  border-bottom: 1px solid var(--border);
}
.section-label {
  font-size: 11px;
  font-family: var(--font-mono);
  text-transform: uppercase;
  letter-spacing: 1.5px;
  color: var(--text-dim);
}
.section-title {
  font-size: 13px;
  font-weight: 600;
  color: var(--text);
}
.btn-add {
  margin-left: auto;
  padding: 4px 12px;
  border: none;
  border-radius: 4px;
  background: var(--accent-light);
  color: var(--accent);
  font-size: 11px;
  font-weight: 600;
  cursor: pointer;
}
.btn-add:hover { background: var(--accent-dim); }

.ingredient-table {
  width: 100%;
  border-collapse: collapse;
}
.ingredient-table th {
  background: var(--bg);
  font-size: 11px;
  font-family: var(--font-mono);
  text-transform: uppercase;
  letter-spacing: 1.5px;
  color: var(--text-dim);
  padding: 8px 12px;
  text-align: left;
  font-weight: 600;
}
.ingredient-table td {
  padding: 8px 12px;
  font-size: 12.5px;
  border-bottom: 1px solid var(--border);
  color: var(--text);
}
.ingredient-table tbody tr:hover { background: var(--bg); }

/* Phase 행 배경 — 미세한 컬러 힌트 */
.phase-row-A { background: rgba(58,111,168,0.04); }
.phase-row-B { background: rgba(58,144,104,0.04); }
.phase-row-C { background: rgba(124,92,191,0.04); }
.phase-row-D { background: rgba(184,147,90,0.04); }

/* Phase 칩 */
.cell-phase { text-align: center; }
.phase-chip {
  display: inline-block;
  padding: 1px 6px;
  border-radius: 3px;
  font-size: 10px;
  font-weight: 700;
  font-family: var(--font-mono);
  letter-spacing: 0.5px;
}
.phase-chip-A {
  background: rgba(58,111,168,0.12);
  color: var(--blue, #3a6fa8);
}
.phase-chip-B {
  background: rgba(58,144,104,0.12);
  color: var(--green, #3a9068);
}
.phase-chip-C {
  background: rgba(124,92,191,0.12);
  color: var(--purple, #7c5cbf);
}
.phase-chip-D {
  background: rgba(184,147,90,0.12);
  color: var(--amber, #b8935a);
}
.phase-chip-none {
  background: transparent;
  color: var(--text-dim);
}

/* Phase 선택 드롭다운 */
.phase-select {
  width: 52px;
  padding: 2px 4px;
  border: 1px solid transparent;
  border-radius: 3px;
  font-size: 11px;
  font-weight: 700;
  font-family: var(--font-mono);
  background: transparent;
  cursor: pointer;
  text-align: center;
}
.phase-select:focus { border-color: var(--accent); outline: none; background: var(--surface); }
.phase-select-A { color: var(--blue, #3a6fa8); }
.phase-select-B { color: var(--green, #3a9068); }
.phase-select-C { color: var(--purple, #7c5cbf); }
.phase-select-D { color: var(--amber, #b8935a); }

.cell-num { font-family: var(--font-mono); color: var(--text-dim); font-size: 10px; }
.cell-inci { font-size: 11px; color: var(--text-sub); }
.cell-pct { font-family: var(--font-mono); font-weight: 700; font-size: 13px; text-align: right; }
.cell-input {
  width: 100%;
  border: 1px solid transparent;
  background: transparent;
  padding: 2px 4px;
  font-size: 12px;
  border-radius: 3px;
  color: var(--text);
}
.cell-input:focus { border-color: var(--accent); outline: none; background: var(--surface); }
.pct-input { text-align: right; width: 60px; }
.btn-del {
  background: none; border: none; color: var(--red); cursor: pointer; font-size: 16px; padding: 0 4px;
}

/* Phase 소계 행 */
.phase-subtotal-row td {
  border-bottom: none;
  padding: 4px 12px;
  font-size: 11px;
  opacity: 0.8;
}
.phase-subtotal-row .cell-pct {
  font-size: 11px;
}

.total-row td { border-top: 2px solid var(--border-mid); font-weight: 600; }
.total-pct.over { color: var(--red); }
.total-pct.under { color: var(--amber); }
.empty-row { text-align: center; color: var(--text-dim); padding: 24px !important; font-size: 12px; }
</style>
