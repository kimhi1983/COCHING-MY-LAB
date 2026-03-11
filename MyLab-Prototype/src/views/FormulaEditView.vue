<template>
  <div class="formula-edit-page">
    <div class="page-top">
      <router-link to="/formulas" class="back-link">← 목록으로</router-link>
      <div class="page-title-row">
        <div>
          <h2 class="page-title">{{ isNew ? '새 처방 작성' : formula.title }}</h2>
          <div class="page-sub" v-if="!isNew">{{ formula.id }} · {{ formula.product_type || '미지정' }}</div>
        </div>
        <div class="title-actions">
          <!-- 버전 배지 -->
          <span v-if="!isNew" class="version-badge">
            v{{ currentVersion }}
          </span>

          <!-- 버전 히스토리 드롭다운 -->
          <div v-if="!isNew && versionHistory.length > 0" class="version-dropdown-wrap">
            <button class="btn-version-history" @click="toggleVersionPanel">
              히스토리 ({{ versionHistory.length }}) ▾
            </button>
            <div v-if="showVersionPanel" class="version-panel">
              <div class="version-panel-header">
                <span class="version-panel-title">버전 히스토리</span>
                <button class="version-panel-close" @click="showVersionPanel = false">×</button>
              </div>
              <div class="version-list">
                <div
                  v-for="(snap, i) in [...versionHistory].reverse()"
                  :key="i"
                  class="version-item"
                >
                  <div class="version-item-info">
                    <span class="version-item-num">v{{ snap.version }}</span>
                    <span class="version-item-title">{{ snap.title }}</span>
                    <span class="version-item-date">{{ formatDate(snap.saved_at) }}</span>
                  </div>
                  <button
                    class="btn-restore"
                    @click="onRestoreVersion(versionHistory.length - 1 - i)"
                  >복원</button>
                </div>
              </div>
            </div>
          </div>

          <!-- 버전 저장 버튼 -->
          <button v-if="!isNew" class="btn-save-version" @click="onSaveVersion">
            버전 저장
          </button>

          <StatusChip v-if="!isNew" :status="formula.status" />
          <button v-if="!isNew" class="btn-export" @click="onExportCsv" title="CSV 파일로 다운로드">CSV 내보내기</button>
          <button v-if="!isNew" class="btn-export" @click="onExportPdf" title="인쇄용 PDF 미리보기">PDF 인쇄</button>
          <button v-if="!isNew" class="btn-danger" @click="onDelete">삭제</button>
        </div>
      </div>
    </div>

    <!-- Basic Info -->
    <div class="form-grid">
      <div class="panel">
        <div class="panel-header">
          <span class="section-label">FORMULA INFO</span>
          <span class="section-title">기본 정보</span>
        </div>
        <div class="form-body">
          <div class="form-group">
            <label class="form-label">처방명 *</label>
            <input v-model="form.title" class="form-input" placeholder="처방명을 입력하세요">
          </div>
          <div class="form-group">
            <label class="form-label">제품 유형</label>
            <select v-model="form.product_type" class="form-input">
              <option value="">선택</option>
              <option v-for="t in productTypes" :key="t.value" :value="t.label">{{ t.label }}</option>
            </select>
          </div>
          <div class="form-group">
            <label class="form-label">프로젝트</label>
            <select v-model="form.project_id" class="form-input">
              <option value="">없음</option>
              <option v-for="p in projects" :key="p.id" :value="p.id">{{ p.name }}</option>
            </select>
          </div>
        </div>
      </div>

      <!-- Status -->
      <div class="panel" v-if="!isNew">
        <div class="panel-header">
          <span class="section-label">STATUS</span>
          <span class="section-title">상태 변경</span>
        </div>
        <div class="status-stepper">
          <button v-for="s in statuses" :key="s.value" class="step-btn"
            :class="{ active: form.status === s.value }"
            :style="form.status === s.value ? { background: s.bg, color: s.color, borderColor: s.border } : {}"
            @click="form.status = s.value">
            {{ s.label }}
          </button>
        </div>
      </div>
    </div>

    <!-- Ingredient Table -->
    <div style="margin-top: 16px">
      <IngredientTable :ingredients="form.formula_data.ingredients" :editable="true"
        @update:ingredients="val => form.formula_data.ingredients = val" />
    </div>

    <!-- Tags & Memo -->
    <div class="form-grid" style="margin-top: 16px">
      <div class="panel">
        <div class="panel-header">
          <span class="section-label">TAGS</span>
          <span class="section-title">태그</span>
        </div>
        <div class="tags-body">
          <span class="tag" v-for="(tag, i) in form.tags" :key="i">
            {{ tag }} <button class="tag-del" @click="form.tags.splice(i, 1)">×</button>
          </span>
          <div class="tag-input-wrap">
            <input v-model="newTag" class="tag-input" placeholder="태그 추가..." @keydown.enter.prevent="addTag">
            <button class="btn-add-tag" @click="addTag">+</button>
          </div>
        </div>
      </div>

      <div class="panel">
        <div class="panel-header">
          <span class="section-label">MEMO</span>
          <span class="section-title">메모</span>
        </div>
        <div class="memo-body">
          <textarea v-model="form.memo" class="memo-textarea" placeholder="메모를 입력하세요..." rows="5"></textarea>
        </div>
      </div>
    </div>

    <!-- Actions -->
    <div class="form-actions">
      <router-link to="/formulas" class="btn btn-ghost">취소</router-link>
      <button class="btn btn-primary" @click="onSave">{{ isNew ? '초안으로 저장' : '저장' }}</button>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useFormulaStore } from '../stores/formulaStore.js'
import { useProjectStore } from '../stores/projectStore.js'
import { productTypes, statusStyles } from '../tokens.js'
import { useExport } from '../composables/useExport.js'
import StatusChip from '../components/common/StatusChip.vue'
import IngredientTable from '../components/formula/IngredientTable.vue'

const route = useRoute()
const router = useRouter()
const { getById, addFormula, updateFormula, deleteFormula, saveVersion, restoreVersion } = useFormulaStore()
const { projects } = useProjectStore()
const { exportFormulaCsv, exportFormulaPdf } = useExport()

const isNew = computed(() => route.name === 'formula-new')
const formula = ref({})
const newTag = ref('')
const showVersionPanel = ref(false)

// 버전 관련 computed
const currentVersion = computed(() => formula.value.version || 1)
const versionHistory = computed(() => formula.value.version_history || [])

const form = reactive({
  title: '',
  product_type: '',
  project_id: '',
  status: 'draft',
  formula_data: { ingredients: [], total_percentage: 0, notes: '' },
  memo: '',
  tags: [],
})

const statuses = [
  { value: 'draft', label: '초안', ...statusStyles.draft },
  { value: 'review', label: '검토중', ...statusStyles.review },
  { value: 'done', label: '완료', ...statusStyles.done },
]

onMounted(() => {
  if (!isNew.value && route.params.id) {
    const f = getById(route.params.id)
    if (f) {
      formula.value = f
      loadForm(f)
    }
  }
})

function loadForm(f) {
  Object.assign(form, {
    title: f.title,
    product_type: f.product_type,
    project_id: f.project_id,
    status: f.status,
    formula_data: JSON.parse(JSON.stringify(f.formula_data || { ingredients: [], total_percentage: 0, notes: '' })),
    memo: f.memo,
    tags: [...(f.tags || [])],
  })
  // ingredients에 phase 필드가 없는 기존 데이터 호환성 보장
  form.formula_data.ingredients = form.formula_data.ingredients.map(ing => ({
    phase: '',
    ...ing,
  }))
}

function addTag() {
  const tag = newTag.value.trim()
  if (tag && !form.tags.includes(tag)) {
    form.tags.push(tag)
    newTag.value = ''
  }
}

function onSave() {
  if (!form.title.trim()) {
    alert('처방명을 입력하세요')
    return
  }
  form.formula_data.total_percentage = form.formula_data.ingredients.reduce((s, i) => s + (Number(i.percentage) || 0), 0)

  if (isNew.value) {
    const created = addFormula({ ...form })
    router.push('/formulas/' + created.id)
  } else {
    const updated = updateFormula(route.params.id, { ...form })
    if (updated) formula.value = updated
    router.push('/formulas')
  }
}

function onDelete() {
  if (confirm('이 처방을 삭제하시겠습니까?')) {
    deleteFormula(route.params.id)
    router.push('/formulas')
  }
}

function onSaveVersion() {
  if (!route.params.id) return
  const updated = saveVersion(route.params.id)
  if (updated) {
    formula.value = updated
    alert(`v${updated.version - 1} 버전이 저장되었습니다. 현재 버전: v${updated.version}`)
  }
}

function onRestoreVersion(versionIndex) {
  if (!confirm('이 버전으로 복원하시겠습니까? 현재 편집 중인 내용은 덮어씌워집니다.')) return
  const updated = restoreVersion(route.params.id, versionIndex)
  if (updated) {
    formula.value = updated
    loadForm(updated)
    showVersionPanel.value = false
  }
}

function toggleVersionPanel() {
  showVersionPanel.value = !showVersionPanel.value
}

function formatDate(iso) {
  if (!iso) return ''
  const d = new Date(iso)
  return `${d.getMonth() + 1}/${d.getDate()} ${String(d.getHours()).padStart(2, '0')}:${String(d.getMinutes()).padStart(2, '0')}`
}

function onExportCsv() {
  if (!formula.value?.id) return
  exportFormulaCsv(formula.value)
}

function onExportPdf() {
  if (!formula.value?.id) return
  exportFormulaPdf(formula.value)
}
</script>

<style scoped>
.page-top { margin-bottom: 16px; }
.back-link { font-size: 12px; color: var(--text-sub); text-decoration: none; }
.back-link:hover { color: var(--accent); }
.page-title-row { display: flex; justify-content: space-between; align-items: flex-start; margin-top: 4px; }
.page-title { font-size: 20px; font-weight: 700; color: var(--text); }
.page-sub { font-size: 12px; color: var(--text-dim); margin-top: 2px; }
.title-actions { display: flex; align-items: center; gap: 8px; flex-wrap: wrap; }

/* 버전 배지 */
.version-badge {
  display: inline-block;
  padding: 2px 8px;
  border-radius: 3px;
  background: rgba(184,147,90,0.15);
  color: var(--amber, #b8935a);
  font-size: 11px;
  font-weight: 700;
  font-family: var(--font-mono);
  letter-spacing: 0.5px;
}

/* 버전 저장 버튼 */
.btn-save-version {
  padding: 5px 12px;
  border: 1px solid var(--border-mid, var(--border));
  border-radius: 4px;
  background: transparent;
  color: var(--text-sub);
  font-size: 12px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.15s;
}
.btn-save-version:hover {
  background: var(--bg);
  border-color: var(--amber, #b8935a);
  color: var(--amber, #b8935a);
}

/* 버전 히스토리 드롭다운 */
.version-dropdown-wrap {
  position: relative;
}
.btn-version-history {
  padding: 5px 12px;
  border: 1px solid var(--border-mid, var(--border));
  border-radius: 4px;
  background: transparent;
  color: var(--text-sub);
  font-size: 12px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.15s;
  font-family: var(--font-mono);
}
.btn-version-history:hover { background: var(--bg); }

.version-panel {
  position: absolute;
  top: calc(100% + 6px);
  right: 0;
  width: 320px;
  background: var(--surface);
  border: 1px solid var(--border-mid, var(--border));
  border-radius: var(--radius);
  box-shadow: var(--shadow, 0 4px 16px rgba(0,0,0,0.3));
  z-index: 100;
}
.version-panel-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 12px 16px;
  border-bottom: 1px solid var(--border);
}
.version-panel-title {
  font-size: 12px;
  font-weight: 600;
  color: var(--text-sub);
  font-family: var(--font-mono);
  text-transform: uppercase;
  letter-spacing: 1px;
}
.version-panel-close {
  background: none;
  border: none;
  color: var(--text-dim);
  cursor: pointer;
  font-size: 16px;
  padding: 0;
  line-height: 1;
}
.version-list {
  max-height: 280px;
  overflow-y: auto;
}
.version-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 10px 16px;
  border-bottom: 1px solid var(--border);
  gap: 8px;
}
.version-item:last-child { border-bottom: none; }
.version-item-info {
  display: flex;
  align-items: baseline;
  gap: 8px;
  flex: 1;
  min-width: 0;
}
.version-item-num {
  font-size: 11px;
  font-weight: 700;
  font-family: var(--font-mono);
  color: var(--amber, #b8935a);
  flex-shrink: 0;
}
.version-item-title {
  font-size: 12px;
  color: var(--text);
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
.version-item-date {
  font-size: 10px;
  color: var(--text-dim);
  font-family: var(--font-mono);
  flex-shrink: 0;
}
.btn-restore {
  padding: 3px 10px;
  border: 1px solid var(--border);
  border-radius: 3px;
  background: transparent;
  color: var(--text-sub);
  font-size: 11px;
  font-weight: 600;
  cursor: pointer;
  flex-shrink: 0;
}
.btn-restore:hover {
  border-color: var(--accent);
  color: var(--accent);
}

.form-grid { display: grid; grid-template-columns: 1fr 1fr; gap: 16px; }

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

.form-body { padding: 16px 20px; }
.form-group { margin-bottom: 12px; }
.form-label { font-size: 12px; color: var(--text-sub); margin-bottom: 4px; display: block; font-weight: 600; }
.form-input {
  width: 100%;
  padding: 8px 12px;
  border: 1px solid var(--border);
  border-radius: 6px;
  font-size: 13px;
  color: var(--text);
  background: var(--surface);
}
.form-input:focus { border-color: var(--accent); outline: none; }

.status-stepper {
  padding: 16px 20px;
  display: flex;
  gap: 8px;
}
.step-btn {
  flex: 1;
  padding: 10px;
  border: 1px solid var(--border);
  border-radius: 6px;
  background: transparent;
  font-size: 12px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.15s;
}
.step-btn:hover { background: var(--bg); }

.tags-body { padding: 16px 20px; display: flex; flex-wrap: wrap; gap: 6px; align-items: center; }
.tag {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  padding: 3px 8px;
  border-radius: 3px;
  background: var(--accent-light);
  color: var(--accent);
  font-size: 12px;
  font-weight: 600;
}
.tag-del { background: none; border: none; color: var(--accent); cursor: pointer; font-size: 13px; padding: 0; }
.tag-input-wrap { display: flex; gap: 4px; }
.tag-input {
  padding: 4px 8px;
  border: 1px solid var(--border);
  border-radius: 4px;
  font-size: 12px;
  width: 120px;
}
.tag-input:focus { border-color: var(--accent); outline: none; }
.btn-add-tag {
  padding: 4px 8px;
  border: none;
  border-radius: 4px;
  background: var(--accent-light);
  color: var(--accent);
  cursor: pointer;
  font-weight: 700;
}

.memo-body { padding: 16px 20px; }
.memo-textarea {
  width: 100%;
  padding: 10px 12px;
  border: 1px solid var(--border);
  border-radius: 6px;
  font-size: 13px;
  color: var(--text);
  resize: vertical;
  line-height: 1.5;
  font-family: var(--font);
}
.memo-textarea:focus { border-color: var(--accent); outline: none; }

.form-actions {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
  gap: 8px;
}
.btn {
  padding: 10px 20px;
  border-radius: 6px;
  border: none;
  font-size: 13px;
  font-weight: 600;
  cursor: pointer;
  text-decoration: none;
}
.btn-primary { background: var(--accent); color: #fff; box-shadow: 0 2px 8px rgba(184,147,90,0.3); }
.btn-primary:hover { background: #a68350; }
.btn-ghost { border: 1px solid var(--border); background: transparent; color: var(--text-sub); }
.btn-ghost:hover { background: var(--bg); }
.btn-export { padding: 6px 12px; border: 1px solid var(--border); border-radius: 4px; background: transparent; color: var(--text-sub); font-size: 12px; font-weight: 600; cursor: pointer; }
.btn-export:hover { background: var(--bg); border-color: var(--accent); color: var(--accent); }
.btn-danger { padding: 6px 12px; border: 1px solid var(--red); border-radius: 4px; background: transparent; color: var(--red); font-size: 12px; cursor: pointer; }
.btn-danger:hover { background: var(--red-bg); }

@media (max-width: 1199px) { .form-grid { grid-template-columns: 1fr; } }
</style>
