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
          <th>원료명</th>
          <th>INCI</th>
          <th style="width:80px">%(wt)</th>
          <th>기능</th>
          <th v-if="editable" style="width:40px"></th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="(ing, idx) in ingredients" :key="idx">
          <td class="cell-num">{{ idx + 1 }}</td>
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
          <td :colspan="editable ? 6 : 5" class="empty-row">원료를 추가해주세요</td>
        </tr>
      </tbody>
      <tfoot v-if="ingredients.length">
        <tr class="total-row">
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

const totalPct = computed(() => props.ingredients.reduce((s, i) => s + (Number(i.percentage) || 0), 0))

function addRow() {
  const updated = [...props.ingredients, { name: '', inci_name: '', percentage: 0, function: '', supplier: '' }]
  emit('update:ingredients', updated)
}

function removeRow(idx) {
  const updated = props.ingredients.filter((_, i) => i !== idx)
  emit('update:ingredients', updated)
}

function onPctChange() {
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
.total-row td { border-top: 2px solid var(--border-mid); font-weight: 600; }
.total-pct.over { color: var(--red); }
.total-pct.under { color: var(--amber); }
.empty-row { text-align: center; color: var(--text-dim); padding: 24px !important; font-size: 12px; }
</style>
