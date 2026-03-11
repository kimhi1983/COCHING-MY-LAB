<template>
  <div class="formula-edit-page">
    <div class="page-top">
      <router-link to="/formulas" class="back-link">← 목록으로</router-link>
      <div class="page-title-row">
        <div>
          <h2 class="page-title">{{ isNew ? '새 처방 작성' : formula.title }}</h2>
          <div class="page-sub" v-if="!isNew">{{ formula.id }} · {{ formula.product_type || '미지정' }}</div>
        </div>
        <div class="title-actions" v-if="!isNew">
          <StatusChip :status="formula.status" />
          <button class="btn-danger" @click="onDelete">삭제</button>
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
import StatusChip from '../components/common/StatusChip.vue'
import IngredientTable from '../components/formula/IngredientTable.vue'

const route = useRoute()
const router = useRouter()
const { getById, addFormula, updateFormula, deleteFormula } = useFormulaStore()
const { projects } = useProjectStore()

const isNew = computed(() => route.name === 'formula-new')
const formula = ref({})
const newTag = ref('')

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
      Object.assign(form, {
        title: f.title,
        product_type: f.product_type,
        project_id: f.project_id,
        status: f.status,
        formula_data: JSON.parse(JSON.stringify(f.formula_data || { ingredients: [], total_percentage: 0, notes: '' })),
        memo: f.memo,
        tags: [...(f.tags || [])],
      })
    }
  }
})

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
    updateFormula(route.params.id, { ...form })
    router.push('/formulas')
  }
}

function onDelete() {
  if (confirm('이 처방을 삭제하시겠습니까?')) {
    deleteFormula(route.params.id)
    router.push('/formulas')
  }
}
</script>

<style scoped>
.page-top { margin-bottom: 16px; }
.back-link { font-size: 12px; color: var(--text-sub); text-decoration: none; }
.back-link:hover { color: var(--accent); }
.page-title-row { display: flex; justify-content: space-between; align-items: flex-start; margin-top: 4px; }
.page-title { font-size: 20px; font-weight: 700; color: var(--text); }
.page-sub { font-size: 11px; color: var(--text-dim); margin-top: 2px; }
.title-actions { display: flex; align-items: center; gap: 8px; }

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
.section-label { font-size: 9px; font-family: monospace; text-transform: uppercase; letter-spacing: 2.5px; color: var(--text-dim); }
.section-title { font-size: 13px; font-weight: 600; color: var(--text); margin-left: 8px; }

.form-body { padding: 16px 20px; }
.form-group { margin-bottom: 12px; }
.form-label { font-size: 11px; color: var(--text-sub); margin-bottom: 4px; display: block; font-weight: 600; }
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
  font-size: 11px;
  font-weight: 600;
}
.tag-del { background: none; border: none; color: var(--accent); cursor: pointer; font-size: 13px; padding: 0; }
.tag-input-wrap { display: flex; gap: 4px; }
.tag-input {
  padding: 4px 8px;
  border: 1px solid var(--border);
  border-radius: 4px;
  font-size: 11px;
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
  font-size: 12.5px;
  color: var(--text);
  resize: vertical;
  line-height: 1.5;
  font-family: inherit;
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
.btn-danger { padding: 6px 12px; border: 1px solid var(--red); border-radius: 4px; background: transparent; color: var(--red); font-size: 11px; cursor: pointer; }
.btn-danger:hover { background: var(--red-bg); }

@media (max-width: 1199px) { .form-grid { grid-template-columns: 1fr; } }
</style>
