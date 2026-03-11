<template>
  <div class="regulation-widget">
    <!-- 지역 필터 탭 -->
    <div class="region-tabs">
      <button
        v-for="tab in regionTabs"
        :key="tab.value"
        class="region-tab"
        :class="{ active: selectedSource === tab.value }"
        @click="selectedSource = tab.value"
      >
        {{ tab.label }}
      </button>
    </div>

    <table class="mini-table">
      <thead>
        <tr>
          <th>지역</th>
          <th>성분명</th>
          <th>상태</th>
          <th>한도</th>
          <th>업데이트</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="row in filteredData" :key="row.id">
          <td>
            <span class="region-chip" :class="getRegionClass(row.region)">
              {{ row.region }}
            </span>
          </td>
          <td class="cell-name">{{ row.ingredient }}</td>
          <td>
            <span class="status-chip" :class="getStatusClass(row.status)">
              {{ getStatusLabel(row.status) }}
            </span>
          </td>
          <td class="cell-limit">{{ row.limit || '-' }}</td>
          <td class="cell-date">{{ row.updatedAt }}</td>
        </tr>
      </tbody>
    </table>

    <div v-if="!filteredData.length" class="empty">
      해당 지역의 규제 데이터가 없습니다
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import { useIngredientStore } from '../../stores/ingredientStore.js'

const store = useIngredientStore()
const selectedSource = ref('ALL')
const regulations = ref([])
const totalCount = ref(0)
const sources = ref([])

const regionTabs = computed(() => {
  const tabs = [{ value: 'ALL', label: '전체' }]
  for (const s of sources.value) {
    // 소스명에서 짧은 라벨 생성
    const label = mapSource(s.source)
    if (!tabs.find(t => t.label === label)) {
      tabs.push({ value: s.source, label, count: s.count })
    }
  }
  return tabs
})

const filteredData = computed(() => regulations.value)

onMounted(async () => {
  await store.init()
  sources.value = store.regulationSources.value || []
  await loadData()
})

async function loadData() {
  const source = selectedSource.value === 'ALL' ? undefined : selectedSource.value
  const data = await store.searchRegulations({ source, limit: 20 })
  if (data) {
    regulations.value = data.items
      .filter(r => r.ingredient || r.inci_name)
      .map((r, i) => ({
        id: i,
        region: mapSource(r.source),
        ingredient: r.ingredient || r.inci_name,
        status: getRegulationStatus(r),
        limit: r.max_concentration || '-',
        updatedAt: r.updated_at ? new Date(r.updated_at).toISOString().slice(0, 7) : '-',
      }))
    totalCount.value = data.total
  }
}

function mapSource(src) {
  const map = { MFDS_SEED: 'KR', GEMINI_KR: 'KR', GEMINI_EU: 'EU', gem2_kb: 'DB', gemini_kb: 'DB', UNKNOWN: '기타' }
  return map[src] || src
}

// source 변경 시 재로드
watch(selectedSource, loadData)

function getRegulationStatus(r) {
  const restriction = (r.restriction || '').toLowerCase()
  const maxConc = (r.max_concentration || '').toLowerCase()
  if (restriction.includes('금지') || restriction.includes('ban')) return 'ban'
  if (maxConc && maxConc !== '-') return 'limit'
  return 'monitor'
}

function getRegionClass(region) {
  if (region === 'KR') return 'region-kr'
  if (region === 'EU') return 'region-eu'
  if (region === 'US') return 'region-us'
  if (region === 'DB') return 'region-db'
  return ''
}

function getStatusClass(status) {
  if (status === 'limit') return 'chip-amber'
  if (status === 'ban') return 'chip-red'
  if (status === 'monitor') return 'chip-purple'
  return ''
}

function getStatusLabel(status) {
  if (status === 'limit') return '제한'
  if (status === 'ban') return '금지'
  if (status === 'monitor') return '모니터링'
  return status
}
</script>

<style scoped>
.regulation-widget {
  display: flex;
  flex-direction: column;
  max-height: 100%;
  overflow: hidden;
}

/* 지역 필터 탭 */
.region-tabs {
  display: flex;
  gap: 4px;
  margin-bottom: 8px;
  flex-shrink: 0;
}

.region-tab {
  padding: clamp(2px, 0.8cqi, 3px) clamp(6px, 2cqi, 10px);
  border: 1px solid var(--border);
  border-radius: 4px;
  background: transparent;
  font-size: clamp(9px, 2.5cqi, 11px);
  font-family: var(--font-mono);
  font-weight: 600;
  color: var(--text-dim);
  cursor: pointer;
  transition: all 0.15s;
}

.region-tab:hover {
  border-color: var(--accent);
  color: var(--accent);
}

.region-tab.active {
  background: var(--accent);
  border-color: var(--accent);
  color: #fff;
}

/* 테이블 */
.mini-table {
  width: 100%;
  border-collapse: collapse;
}

.mini-table th {
  background: var(--bg);
  font-size: clamp(9px, 2.5cqi, 11px);
  font-family: var(--font-mono);
  text-transform: uppercase;
  letter-spacing: 0.6px;
  color: var(--text-dim);
  padding: clamp(3px, 1cqi, 6px) clamp(4px, 1.5cqi, 8px);
  text-align: left;
  position: sticky;
  top: 0;
  white-space: nowrap;
}

.mini-table td {
  padding: clamp(4px, 1.2cqi, 7px) clamp(4px, 1.5cqi, 8px);
  font-size: clamp(10px, 2.5cqi, 12px);
  border-bottom: 1px solid var(--border);
  vertical-align: middle;
}

.mini-table tbody tr:hover {
  background: var(--bg);
}

.cell-name {
  font-weight: 500;
  max-width: clamp(80px, 25cqi, 130px);
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.cell-limit {
  font-family: var(--font-mono);
  font-size: clamp(9px, 2.2cqi, 11px);
  color: var(--text-sub);
  white-space: nowrap;
}

.cell-date {
  font-family: var(--font-mono);
  font-size: clamp(9px, 2.2cqi, 11px);
  color: var(--text-dim);
  white-space: nowrap;
}

/* 지역 칩 */
.region-chip {
  display: inline-block;
  padding: clamp(1px, 0.5cqi, 2px) clamp(4px, 1.2cqi, 6px);
  border-radius: 3px;
  font-size: clamp(9px, 2.2cqi, 11px);
  font-weight: 700;
  font-family: var(--font-mono);
}

.region-kr {
  background: rgba(58, 111, 168, 0.15);
  color: var(--blue);
}

.region-eu {
  background: rgba(124, 92, 191, 0.15);
  color: var(--purple);
}

.region-us {
  background: rgba(58, 144, 104, 0.15);
  color: var(--green);
}

/* 상태 칩 */
.status-chip {
  display: inline-block;
  padding: clamp(1px, 0.5cqi, 2px) clamp(4px, 1.5cqi, 8px);
  border-radius: 3px;
  font-size: clamp(9px, 2.2cqi, 11px);
  font-weight: 600;
}

.chip-amber {
  background: rgba(184, 147, 90, 0.15);
  color: var(--amber);
}

.chip-red {
  background: rgba(196, 78, 78, 0.15);
  color: var(--red);
}

.chip-purple {
  background: rgba(124, 92, 191, 0.15);
  color: var(--purple);
}

.empty {
  text-align: center;
  color: var(--text-dim);
  font-size: clamp(10px, 2.8cqi, 12px);
  padding: clamp(12px, 5cqi, 24px);
}
</style>
