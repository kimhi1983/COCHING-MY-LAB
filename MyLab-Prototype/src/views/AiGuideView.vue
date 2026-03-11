<template>
  <div class="ai-guide-page">
    <!-- Request Form -->
    <div class="panel">
      <div class="panel-header">
        <div>
          <span class="section-label">REQUEST</span>
          <span class="section-title">처방 요청</span>
        </div>
      </div>
      <div class="form-body">
        <div class="form-row">
          <div class="form-group">
            <label class="form-label">처방명 *</label>
            <input v-model="request.title" class="form-input" placeholder="예: 고보습 세럼">
          </div>
          <div class="form-group">
            <label class="form-label">제품 유형 *</label>
            <select v-model="request.productType" class="form-input">
              <option value="">선택하세요</option>
              <option v-for="t in productTypes" :key="t.value" :value="t.value">{{ t.label }}</option>
            </select>
          </div>
        </div>
        <div class="form-group">
          <label class="form-label">처방 방향</label>
          <input v-model="request.requirements" class="form-input" placeholder="예: 보습 강화, 민감 피부용, 약산성">
        </div>
        <div class="form-actions">
          <button class="btn btn-primary btn-lg" @click="onGenerate" :disabled="isGenerating">
            {{ isGenerating ? '생성 중...' : '✦ MyLab 처방 생성하기' }}
          </button>
        </div>
      </div>
    </div>

    <!-- Loading -->
    <div v-if="isGenerating" class="loading-panel">
      <div class="loading-box">
        <div class="spinner"></div>
        <div class="loading-title">MyLab 처방 생성 중...</div>
        <div class="loading-step">{{ progress }}</div>
      </div>
    </div>

    <!-- Result -->
    <div v-if="result" style="margin-top: 16px">
      <AiResultPanel :result="result" @save="onSave" @regenerate="onGenerate" />
    </div>

    <!-- History -->
    <div v-if="history.length" class="history-section">
      <div class="section-label" style="margin-bottom: 12px">MYLAB HISTORY · 생성 이력</div>
      <div class="history-grid">
        <div class="history-card" v-for="(h, i) in history" :key="i" @click="result = h">
          <div class="hist-date">{{ formatDate(h.generatedAt) }}</div>
          <div class="hist-title">{{ h._title || '처방' }}</div>
          <div class="hist-type">{{ h._productLabel }}</div>
          <div class="hist-count">성분 {{ h.ingredients?.length || 0 }}개</div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { useMockAI } from '../composables/useMockAI.js'
import { useFormulaStore } from '../stores/formulaStore.js'
import { productTypes } from '../tokens.js'
import AiResultPanel from '../components/formula/AiResultPanel.vue'

const router = useRouter()
const { isGenerating, progress, generateGuideFormula } = useMockAI()
const { addFormula } = useFormulaStore()

const request = reactive({
  title: '',
  productType: '',
  requirements: '',
})

const result = ref(null)
const history = ref([])

async function onGenerate() {
  if (!request.title.trim() || !request.productType) {
    alert('처방명과 제품 유형을 입력하세요')
    return
  }
  const res = await generateGuideFormula(request.productType, request.requirements)
  result.value = res

  const typeLabel = productTypes.find(t => t.value === request.productType)?.label || request.productType
  const histEntry = { ...res, _title: request.title, _productLabel: typeLabel }
  history.value.unshift(histEntry)
}

function onSave() {
  if (!result.value) return
  const typeLabel = productTypes.find(t => t.value === request.productType)?.label || request.productType
  const created = addFormula({
    title: request.title || 'MyLab 생성 처방',
    product_type: typeLabel,
    formula_data: {
      ingredients: result.value.ingredients,
      total_percentage: result.value.totalPercentage,
      notes: result.value.description,
    },
    memo: `MyLab 생성 처방\n${result.value.description}`,
    tags: ['MyLab생성'],
  })
  router.push('/formulas/' + created.id)
}

function formatDate(iso) {
  if (!iso) return '-'
  const d = new Date(iso)
  return `${d.getMonth()+1}/${d.getDate()} ${String(d.getHours()).padStart(2,'0')}:${String(d.getMinutes()).padStart(2,'0')}`
}
</script>

<style scoped>
.panel {
  background: var(--surface);
  border: 1px solid var(--border);
  border-radius: var(--radius);
  box-shadow: var(--shadow);
}
.panel-header {
  padding: 14px 20px;
  border-bottom: 1px solid var(--border);
}
.section-label { font-size: 11px; font-family: var(--font-mono); text-transform: uppercase; letter-spacing: 1.5px; color: var(--text-dim); }
.section-title { font-size: 13px; font-weight: 600; color: var(--text); margin-left: 8px; }

.form-body { padding: 20px; }
.form-row { display: grid; grid-template-columns: 1fr 1fr; gap: 16px; }
.form-group { margin-bottom: 12px; }
.form-label { font-size: 12px; color: var(--text-sub); margin-bottom: 4px; display: block; font-weight: 600; }
.form-input {
  width: 100%; padding: 8px 12px; border: 1px solid var(--border); border-radius: 6px;
  font-size: 13px; color: var(--text); background: var(--surface);
}
.form-input:focus { border-color: var(--accent); outline: none; }

.form-actions { margin-top: 8px; display: flex; justify-content: center; }
.btn { padding: 10px 20px; border-radius: 6px; border: none; font-size: 13px; font-weight: 600; cursor: pointer; }
.btn-primary { background: var(--accent); color: #fff; box-shadow: 0 2px 8px rgba(184,147,90,0.3); }
.btn-primary:hover { background: #a68350; }
.btn-primary:disabled { opacity: 0.6; cursor: not-allowed; }
.btn-lg { padding: 14px 32px; font-size: 14px; }

.loading-panel {
  margin-top: 16px;
  background: var(--accent-light);
  border: 1px solid var(--accent-dim);
  border-radius: var(--radius);
  padding: 40px 20px;
  text-align: center;
}
.loading-box { display: inline-block; }
.spinner {
  width: 32px; height: 32px;
  border: 3px solid var(--accent-dim);
  border-top-color: var(--accent);
  border-radius: 50%;
  animation: spin 1s linear infinite;
  margin: 0 auto 12px;
}
@keyframes spin { to { transform: rotate(360deg); } }
.loading-title { font-size: 14px; font-weight: 600; color: var(--accent); margin-bottom: 4px; }
.loading-step { font-size: 13px; color: var(--text-sub); }

.history-section { margin-top: 24px; }
.history-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 12px;
}
.history-card {
  background: var(--surface);
  border: 1px solid var(--border);
  border-radius: var(--radius);
  padding: 14px 16px;
  cursor: pointer;
  transition: all 0.15s;
}
.history-card:hover { border-color: var(--accent-dim); }
.hist-date { font-size: 11px; font-family: var(--font-mono); color: var(--text-dim); }
.hist-title { font-size: 14px; font-weight: 600; margin-top: 4px; color: var(--text); }
.hist-type { font-size: 12px; color: var(--text-sub); }
.hist-count { font-size: 11px; color: var(--text-dim); margin-top: 4px; }

@media (max-width: 1199px) {
  .form-row { grid-template-columns: 1fr; }
  .history-grid { grid-template-columns: repeat(2, 1fr); }
}
@media (max-width: 767px) {
  .history-grid { grid-template-columns: 1fr; }
}
</style>
