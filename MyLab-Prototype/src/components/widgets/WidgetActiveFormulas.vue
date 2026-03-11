<template>
  <div class="active-widget">
    <table class="mini-table" v-if="activeFormulas.length">
      <thead>
        <tr><th>ID</th><th>처방명</th><th>제형</th><th>상태</th><th>수정일</th></tr>
      </thead>
      <tbody>
        <tr v-for="f in activeFormulas" :key="f.id" @click="$router.push('/formulas/' + f.id)">
          <td class="cell-id">{{ f.id }}</td>
          <td class="cell-title">{{ f.title }}</td>
          <td>{{ f.product_type || '-' }}</td>
          <td><StatusChip :status="f.status" /></td>
          <td class="cell-date">{{ formatDate(f.updated_at) }}</td>
        </tr>
      </tbody>
    </table>
    <div v-else class="empty">진행 중인 처방이 없습니다</div>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useFormulaStore } from '../../stores/formulaStore.js'
import StatusChip from '../common/StatusChip.vue'

const { formulas } = useFormulaStore()
const activeFormulas = computed(() =>
  formulas.value.filter(f => f.status !== 'done')
    .sort((a, b) => new Date(b.updated_at) - new Date(a.updated_at)).slice(0, 8)
)
function formatDate(iso) {
  const d = new Date(iso)
  return `${d.getMonth()+1}/${d.getDate()}`
}
</script>

<style scoped>
.active-widget { overflow: auto; max-height: 100%; }
.mini-table { width: 100%; border-collapse: collapse; }
.mini-table th {
  background: var(--bg); font-size: 9px; font-family: monospace; text-transform: uppercase;
  letter-spacing: 1px; color: var(--text-dim); padding: 6px 10px; text-align: left; position: sticky; top: 0;
}
.mini-table td { padding: 7px 10px; font-size: 11px; border-bottom: 1px solid var(--border); }
.mini-table tbody tr { cursor: pointer; }
.mini-table tbody tr:hover { background: var(--bg); }
.cell-id { font-family: monospace; font-size: 9px; color: var(--text-dim); }
.cell-title { font-weight: 500; }
.cell-date { font-family: monospace; font-size: 10px; color: var(--text-dim); }
.empty { text-align: center; color: var(--text-dim); font-size: 12px; padding: 24px; }
</style>
