<template>
  <div class="recent-widget">
    <div class="recent-list" v-if="recent.length">
      <div class="recent-item" v-for="f in recent" :key="f.id" @click="$router.push('/formulas/' + f.id)">
        <div class="recent-time">{{ formatTime(f.updated_at) }}</div>
        <div class="recent-body">
          <div class="recent-top">
            <StatusChip :status="f.status" />
            <span class="recent-title">{{ f.title }}</span>
          </div>
          <div class="recent-sub">{{ f.product_type || '미지정' }}</div>
        </div>
      </div>
    </div>
    <div v-else class="empty">아직 작성한 처방이 없습니다</div>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useFormulaStore } from '../../stores/formulaStore.js'
import StatusChip from '../common/StatusChip.vue'

const { recentFormulas } = useFormulaStore()
const recent = computed(() => recentFormulas(6))

function formatTime(iso) {
  const d = new Date(iso)
  return `${String(d.getHours()).padStart(2,'0')}:${String(d.getMinutes()).padStart(2,'0')}`
}
</script>

<style scoped>
.recent-list { overflow-y: auto; max-height: 100%; }
.recent-item {
  display: flex; gap: 10px; padding: 8px 12px; cursor: pointer; border-bottom: 1px solid var(--border);
}
.recent-item:hover { background: var(--bg); }
.recent-item:last-child { border-bottom: none; }
.recent-time { font-size: 10px; font-family: var(--font-mono); color: var(--text-dim); padding-top: 2px; min-width: 36px; }
.recent-top { display: flex; align-items: center; gap: 6px; }
.recent-title { font-size: 12px; font-weight: 500; color: var(--text); }
.recent-sub { font-size: 10px; color: var(--text-dim); margin-top: 1px; }
.empty { text-align: center; color: var(--text-dim); font-size: 12px; padding: 24px; }
</style>
