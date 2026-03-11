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
            <input
              v-model="form.product_type"
              list="product-type-list"
              class="form-input"
              placeholder="선택 또는 직접 입력"
            >
            <datalist id="product-type-list">
              <option v-for="t in allProductOptions" :key="t.value" :value="t.label">{{ t.group }} — {{ t.label }}</option>
            </datalist>
          </div>
          <div class="form-group">
            <label class="form-label">프로젝트</label>
            <select v-model="form.project_id" class="form-input">
              <option value="">없음</option>
              <option v-for="p in projects" :key="p.id" :value="p.id">{{ p.name }}</option>
            </select>
          </div>

          <!-- 전성분 자동 채우기 -->
          <div class="form-group ai-fill-group">
            <label class="form-label">추가 요구사항 (선택)</label>
            <input v-model="aiRequirements" class="form-input" placeholder="예: 고보습, 민감성 피부, 비건">
            <button
              class="btn-ai-fill"
              :disabled="!form.product_type || isAiFilling"
              @click="onAiFill"
            >
              <span v-if="isAiFilling" class="ai-spinner"></span>
              {{ isAiFilling ? aiFillStep : '전성분 자동 채우기' }}
            </button>
            <div v-if="!form.product_type" class="ai-hint">제품 유형을 먼저 선택하세요</div>
          </div>
        </div>
      </div>

      <!-- 물성 / 안정성 스펙 패널 (항상 표시, 편집 가능) -->
      <div class="panel">
        <div class="panel-header">
          <span class="section-label">PHYSICAL SPEC</span>
          <span class="section-title">물성 · 안정성</span>
          <span v-if="!form.product_type" class="props-hint">제품 유형을 선택하면 기본값이 채워집니다</span>
        </div>
        <div class="props-body">
          <div class="props-grid">
            <div class="prop-item" v-for="p in physicalPropsFields" :key="p.key">
              <span class="prop-label">{{ p.label }}</span>
              <input
                v-model="physicalProps[p.key]"
                class="prop-input"
                :placeholder="p.placeholder || '—'"
              >
            </div>
          </div>
          <div class="props-section">
            <div class="props-section-title">안정성 시험 조건</div>
            <div class="stability-item" v-for="(st, i) in physicalProps.stability" :key="i">
              <span class="stab-cond">{{ st.condition }}</span>
              <input v-model="st.period" class="stab-input" placeholder="기간">
              <button
                class="stab-toggle"
                :class="st.expected === 'pass' ? 'pass' : 'warn'"
                @click="st.expected = st.expected === 'pass' ? 'warn' : 'pass'"
                :title="st.expected === 'pass' ? '적합 → 관찰로 변경' : '관찰 → 적합으로 변경'"
              >{{ st.expected === 'pass' ? '적합' : '관찰' }}</button>
            </div>
          </div>
          <div class="props-section">
            <div class="props-section-title">미생물 한도</div>
            <input v-model="physicalProps.micro" class="micro-input" placeholder="예: 총 호기성 생균수 ≤ 500 CFU/g">
          </div>
        </div>
      </div>

      <!-- Status (기존 처방) -->
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

    <!-- Memo -->
    <div class="panel" style="margin-top: 16px">
      <div class="panel-header">
        <span class="section-label">MEMO</span>
        <span class="section-title">메모</span>
        <button v-if="form.memo" class="btn-memo-expand" @click="showMemoModal = true" title="메모 확대 보기">확대 ↗</button>
      </div>
      <div class="memo-body">
        <textarea v-model="form.memo" class="memo-textarea" placeholder="메모를 입력하세요..." rows="8"></textarea>
      </div>
    </div>

    <!-- 메모 확대 모달 -->
    <Teleport to="body">
      <div v-if="showMemoModal" class="memo-modal-overlay" @click.self="showMemoModal = false">
        <div class="memo-modal">
          <div class="memo-modal-header">
            <span class="memo-modal-title">메모 상세</span>
            <button class="memo-modal-close" @click="showMemoModal = false">×</button>
          </div>
          <div class="memo-modal-body">
            <textarea v-model="form.memo" class="memo-modal-textarea"></textarea>
          </div>
        </div>
      </div>
    </Teleport>

    <!-- Actions -->
    <div class="form-actions">
      <router-link to="/formulas" class="btn btn-ghost">취소</router-link>
      <button class="btn btn-primary" @click="onSave">{{ isNew ? '초안으로 저장' : '저장' }}</button>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed, watch, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useFormulaStore } from '../stores/formulaStore.js'
import { useProjectStore } from '../stores/projectStore.js'
import { useAPI } from '../composables/useAPI.js'
import { productCategories, statusStyles } from '../tokens.js'
import { useExport } from '../composables/useExport.js'
import StatusChip from '../components/common/StatusChip.vue'
import IngredientTable from '../components/formula/IngredientTable.vue'

const route = useRoute()
const router = useRouter()
const { getById, addFormula, updateFormula, deleteFormula, saveVersion, restoreVersion } = useFormulaStore()
const { projects } = useProjectStore()
const { exportFormulaCsv, exportFormulaPdf } = useExport()
const api = useAPI()

const allProductOptions = computed(() =>
  productCategories.flatMap(cat => cat.items.map(t => ({ ...t, group: cat.group })))
)
const isNew = computed(() => route.name === 'formula-new')
const formula = ref({})
const newTag = ref('')
const showVersionPanel = ref(false)

// AI 자동 채우기
const aiRequirements = ref('')
const isAiFilling = ref(false)
const aiFillStep = ref('')

// 메모 모달
const showMemoModal = ref(false)

// 물성·안정성
const physicalProps = reactive({
  ph: '', viscosity: '', hardness: '', specificGravity: '',
  color: '', odor: '', appearance: '', spreadability: '',
  shelfLife: '', storage: '', micro: '',
  stability: [
    { condition: '고온 (45±2℃)', period: '8주', expected: 'pass' },
    { condition: '실온 (25±2℃)', period: '12주', expected: 'pass' },
    { condition: '저온 (4±2℃)', period: '8주', expected: 'pass' },
    { condition: '냉동-해동 반복 (-15℃↔25℃)', period: '3 cycle', expected: 'warn' },
    { condition: '광안정성 (UV 조사)', period: '4주', expected: 'pass' },
  ],
})
const physicalPropsFields = [
  { key: 'ph', label: 'pH', placeholder: '5.5 ~ 7.0' },
  { key: 'viscosity', label: '점도 (cps)', placeholder: '5,000 ~ 50,000' },
  { key: 'hardness', label: '경도', placeholder: '—' },
  { key: 'specificGravity', label: '비중', placeholder: '0.98 ~ 1.05' },
  { key: 'appearance', label: '외관', placeholder: '크림/로션' },
  { key: 'color', label: '색상', placeholder: '백색~미색' },
  { key: 'odor', label: '향', placeholder: '고유의 향' },
  { key: 'spreadability', label: '도포성', placeholder: '양호' },
  { key: 'shelfLife', label: '유통기한', placeholder: '제조일로부터 30개월' },
  { key: 'storage', label: '보관조건', placeholder: '직사광선 차단, 상온(15~25℃)' },
]

// 제품 유형 변경 시 물성 기본값 자동 채움
watch(() => form.product_type, (newType) => {
  if (newType) generatePhysicalProps(newType)
})

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

function getProcessByType(productType) {
  const processes = {
    '토너': [
      '1. Phase A: 정제수에 수용성 원료(글리세린, 히알루론산 등) 투입 → 상온 교반',
      '2. Phase C: 활성성분(나이아신아마이드 등) 투입 → 저속 교반',
      '3. Phase D: 방부제, 향료 투입 → 균일 혼합',
      '4. pH 확인 (5.0~6.5) → 필요시 pH 조절제로 보정',
      '5. 여과 → 충진',
    ],
    '로션': [
      '1. Phase A(수상): 정제수 + 수용성 원료 → 75°C 가열 용해',
      '2. Phase B(유상): 오일 + 유화제 + 왁스류 → 별도 용기 75°C 가열 용해',
      '3. 유화: B상을 A상에 서서히 투입 → 호모믹서 5,000rpm, 5분',
      '4. 냉각: 교반하며 40°C까지 냉각',
      '5. Phase C: 활성성분 투입 → 저속 교반',
      '6. Phase D: 방부제, 향료 투입 → 균일 혼합',
      '7. pH 확인 → 탈포 → 충진',
    ],
    '크림': [
      '1. Phase A(수상): 정제수 + 수용성 원료 → 75~80°C 가열 용해',
      '2. Phase B(유상): 오일 + 유화제 + 버터/왁스 → 별도 용기 75~80°C 가열 용해',
      '3. 유화: B상을 A상에 서서히 투입 → 호모믹서 6,000rpm, 10분',
      '4. 냉각: 패들 교반하며 45°C까지 서서히 냉각',
      '5. Phase C: 활성성분, 증점제 투입 → 저속 교반 5분',
      '6. Phase D: 방부제, 향료 투입 → 균일 혼합',
      '7. pH 확인 (5.5~7.0) → 점도 확인 → 탈포 → 충진',
    ],
    '세럼': [
      '1. Phase A: 정제수에 수용성 고분자(카보머 등) 분산 → 상온 팽윤 30분',
      '2. 가열: 70°C까지 승온 → 글리세린, BG 등 투입',
      '3. 냉각: 40°C까지 냉각',
      '4. Phase C: 활성성분(펩타이드, 비타민 등) 투입 → 저속 교반',
      '5. 중화: TEA 또는 NaOH로 pH 보정 → 겔화 확인',
      '6. Phase D: 방부제, 향료 투입 → 탈포 → 충진',
    ],
    '클렌징': [
      '1. Phase A: 정제수 + 계면활성제 → 50°C 가열 용해',
      '2. Phase B: 오일상 원료 투입 → 교반 혼합',
      '3. 냉각: 상온까지 교반하며 냉각',
      '4. Phase D: 방부제, 향료 투입 → 균일 혼합',
      '5. pH 확인 → 점도 확인 → 충진',
    ],
    '선크림': [
      '1. Phase A(수상): 정제수 + 수용성 원료 → 75°C 가열',
      '2. Phase B(유상): 오일 + UV 필터(유기자차) + 유화제 → 75°C 가열',
      '3. 분산: 무기자차(TiO2, ZnO)를 B상에 3-roll mill 또는 비드밀로 분산',
      '4. 유화: B상을 A상에 투입 → 호모믹서 7,000rpm, 10분',
      '5. 냉각: 40°C까지 냉각 → Phase C/D 투입',
      '6. SPF 측정용 시료 채취 → pH 확인 → 충진',
    ],
    '샴푸': [
      '1. Phase A: 정제수를 60°C로 가열',
      '2. 계면활성제(SLES, 코카미도프로필베타인 등) 순차 투입 → 저속 교반',
      '3. 증점: 소금(NaCl) 또는 증점제로 점도 조절',
      '4. 냉각: 40°C까지 냉각',
      '5. Phase C/D: 컨디셔닝제, 방부제, 향료 투입',
      '6. pH 확인 (5.0~7.0) → 충진',
    ],
  }

  // 부분 매칭
  const pt = productType || ''
  for (const [key, steps] of Object.entries(processes)) {
    if (pt.includes(key) || key.includes(pt)) return steps
  }
  // 에멀전/로션 계열 기본
  if (pt.includes('에멀') || pt.includes('바디')) return processes['로션']
  if (pt.includes('에센스') || pt.includes('앰플')) return processes['세럼']
  if (pt.includes('자외선') || pt.includes('선')) return processes['선크림']
  if (pt.includes('폼') || pt.includes('워터') || pt.includes('오일')) return processes['클렌징']

  // 범용 크림/로션 공정
  return processes['크림']
}

function buildAiMemo(source, data, existingMemo) {
  const lines = []
  lines.push(`[${source} 자동 생성] ${data.description || ''}`)
  lines.push('')

  // 복합원료만 기록
  const ings = data.ingredients || []
  const compounds = ings.filter(i => i.is_compound)

  if (compounds.length > 0) {
    lines.push('━━ 사용 복합원료 ━━')
    for (const ing of compounds) {
      const name = ing.compound_name || ing.korean_name || ing.name || ''
      const inci = ing.inci_name || ''
      const pct = ing.percentage ?? '—'
      lines.push(`▸ ${name} (${pct}%)`)
      lines.push(`  INCI: ${inci}`)
      lines.push('')
    }
  }

  // 간략 제조방법
  const productType = data._productType || ''
  const steps = getProcessByType(productType)
  lines.push('━━ 제조방법 (Lab 시험용) ━━')
  for (const step of steps) {
    lines.push(step)
  }
  lines.push('')

  lines.push(`---`)
  lines.push(`총 ${ings.length}종 | ${new Date(data.generatedAt || Date.now()).toLocaleString('ko-KR')}`)

  if (existingMemo) {
    lines.push('')
    lines.push('--- 이전 메모 ---')
    lines.push(existingMemo)
  }

  return lines.join('\n')
}

function generatePhysicalProps(productType) {
  // 제품 유형별 기본 물성 스펙
  const specs = {
    '토너/스킨':     { ph: '5.0 ~ 6.5', viscosity: '5 ~ 50', hardness: '—', appearance: '투명~반투명 액상', spreadability: '우수' },
    '로션/에멀전':    { ph: '5.5 ~ 7.0', viscosity: '3,000 ~ 10,000', hardness: '—', appearance: '유백색 에멀전', spreadability: '양호' },
    '크림':          { ph: '5.5 ~ 7.0', viscosity: '15,000 ~ 80,000', hardness: '중간', appearance: '유백색~백색 크림', spreadability: '보통' },
    '세럼/에센스/앰플': { ph: '5.0 ~ 6.5', viscosity: '500 ~ 5,000', hardness: '—', appearance: '투명~반투명 겔', spreadability: '우수' },
    '아이크림':       { ph: '5.5 ~ 7.0', viscosity: '20,000 ~ 60,000', hardness: '중간~높음', appearance: '백색 크림', spreadability: '보통' },
    '마스크/팩':      { ph: '5.0 ~ 7.0', viscosity: '10,000 ~ 50,000', hardness: '—', appearance: '겔~크림 타입', spreadability: '양호' },
    '미스트':         { ph: '5.0 ~ 6.5', viscosity: '1 ~ 10', hardness: '—', appearance: '투명 액상', spreadability: '우수' },
    '클렌징 폼':      { ph: '6.0 ~ 8.0', viscosity: '5,000 ~ 30,000', hardness: '—', appearance: '백색 페이스트', spreadability: '양호' },
    '클렌징 오일/밤':  { ph: '—', viscosity: '50 ~ 500', hardness: '—', appearance: '투명 오일', spreadability: '우수' },
    '샴푸':          { ph: '5.0 ~ 7.0', viscosity: '3,000 ~ 15,000', hardness: '—', appearance: '투명~반투명 액상', spreadability: '우수' },
    '자외선 차단':    { ph: '6.0 ~ 8.0', viscosity: '10,000 ~ 50,000', hardness: '—', appearance: '백색 크림/로션', spreadability: '양호' },
    '파운데이션/베이스': { ph: '6.0 ~ 8.0', viscosity: '10,000 ~ 50,000', hardness: '—', appearance: '피부색 에멀전', spreadability: '양호' },
    '바디로션/크림':   { ph: '5.5 ~ 7.0', viscosity: '5,000 ~ 30,000', hardness: '—', appearance: '유백색 에멀전', spreadability: '양호' },
    '쿠션':          { ph: '5.5 ~ 7.0', viscosity: '5,000 ~ 50,000', hardness: '—', appearance: '크림/로션', spreadability: '양호' },
    '립':            { ph: '—', viscosity: '—', hardness: '높음', appearance: '고체/반고체', spreadability: '양호' },
  }

  // 매칭 시도 (부분 일치)
  let matched = null
  for (const [key, val] of Object.entries(specs)) {
    if (productType.includes(key) || key.includes(productType)) {
      matched = val
      break
    }
  }
  // 추가 매칭
  if (!matched) {
    if (productType.includes('에멀') || productType.includes('바디')) matched = specs['로션/에멀전']
    else if (productType.includes('에센스') || productType.includes('앰플')) matched = specs['세럼/에센스/앰플']
    else if (productType.includes('선') || productType.includes('자외선')) matched = specs['자외선 차단']
    else matched = { ph: '5.5 ~ 7.0', viscosity: '5,000 ~ 50,000', hardness: '—', appearance: '크림/로션', spreadability: '양호' }
  }

  // 기본값 채움 (기존 사용자 입력이 있으면 유지)
  const defaults = {
    ph: matched.ph,
    viscosity: matched.viscosity,
    hardness: matched.hardness,
    specificGravity: '0.98 ~ 1.05',
    color: '백색~미색',
    odor: '고유의 향',
    appearance: matched.appearance,
    spreadability: matched.spreadability,
    shelfLife: '제조일로부터 30개월',
    storage: '직사광선 차단, 상온(15~25℃)',
    micro: '총 호기성 생균수 ≤ 500 CFU/g, 대장균·녹농균·황색포도상구균 불검출',
  }
  for (const [key, val] of Object.entries(defaults)) {
    physicalProps[key] = val
  }
}

async function onAiFill() {
  if (!form.product_type || isAiFilling.value) return

  const hasIngredients = form.formula_data.ingredients.length > 0
  if (hasIngredients && !confirm('기존 원료 배합표를 덮어씌웁니다. 계속하시겠습니까?')) return

  isAiFilling.value = true
  aiFillStep.value = '원료 검색 중...'

  try {
    aiFillStep.value = '처방 생성 중...'
    // 물성 조건을 AI에 전달
    const specEntries = physicalPropsFields
      .filter(f => physicalProps[f.key])
      .map(f => `${f.label}: ${physicalProps[f.key]}`)
    const res = await api.generateAiFormula({
      productType: form.product_type,
      requirements: aiRequirements.value || form.product_type,
      physicalSpecs: specEntries.length ? specEntries : undefined,
    })

    if (res?.success && res.data?.ingredients) {
      aiFillStep.value = '배합표 적용 중...'
      form.formula_data.ingredients = res.data.ingredients.map(ing => ({
        name: ing.korean_name || ing.name || '',
        inci_name: ing.inci_name || '',
        percentage: ing.percentage || 0,
        function: ing.function || '',
        phase: ing.phase || '',
      }))
      form.formula_data.total_percentage = res.data.totalPercentage || 100

      // 처방명이 비어있으면 자동 설정
      if (!form.title.trim()) {
        form.title = `${form.product_type} 처방`
      }
      // 태그 추가
      if (!form.tags.includes('자동처방')) form.tags.push('자동처방')
      // 메모에 복합원료 + 제조방법 생성
      const source = res.data.source?.includes('gemini') ? 'Gemini' : res.data.source?.includes('openai') ? 'LLM' : 'DB'
      res.data._productType = form.product_type
      form.memo = buildAiMemo(source, res.data, form.memo)

    } else {
      alert('처방 생성에 실패했습니다. 다시 시도해주세요.')
    }
  } catch (err) {
    alert('서버 연결 오류: ' + (err.message || '알 수 없는 오류'))
  } finally {
    isAiFilling.value = false
    aiFillStep.value = ''
  }
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
  display: flex;
  align-items: center;
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

/* AI 자동 채우기 */
.ai-fill-group {
  margin-top: 4px;
  padding-top: 12px;
  border-top: 1px dashed var(--border);
}
.btn-ai-fill {
  margin-top: 8px;
  width: 100%;
  padding: 10px 16px;
  border: 1px solid var(--accent);
  border-radius: 6px;
  background: linear-gradient(135deg, var(--accent-light) 0%, var(--surface) 100%);
  color: var(--accent);
  font-size: 13px;
  font-weight: 700;
  cursor: pointer;
  transition: all 0.2s;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
}
.btn-ai-fill:hover:not(:disabled) {
  background: var(--accent);
  color: #fff;
  box-shadow: 0 2px 12px rgba(184,147,90,0.35);
}
.btn-ai-fill:disabled {
  opacity: 0.45;
  cursor: not-allowed;
  border-color: var(--border);
  color: var(--text-dim);
  background: var(--bg);
}
.ai-spinner {
  width: 14px; height: 14px;
  border: 2px solid var(--accent-dim, rgba(184,147,90,0.3));
  border-top-color: var(--accent);
  border-radius: 50%;
  animation: ai-spin 0.8s linear infinite;
  flex-shrink: 0;
}
@keyframes ai-spin { to { transform: rotate(360deg); } }
.ai-hint {
  margin-top: 4px;
  font-size: 11px;
  color: var(--text-dim);
}

/* 메모 확대 버튼 */
.btn-memo-expand {
  margin-left: auto;
  padding: 2px 10px;
  border: 1px solid var(--border);
  border-radius: 4px;
  background: transparent;
  color: var(--text-sub);
  font-size: 11px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.15s;
}
.btn-memo-expand:hover {
  border-color: var(--accent);
  color: var(--accent);
  background: var(--accent-light);
}

/* 메모 모달 */
.memo-modal-overlay {
  position: fixed;
  inset: 0;
  background: rgba(0,0,0,0.5);
  z-index: 1000;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 32px;
}
.memo-modal {
  width: 100%;
  max-width: 800px;
  max-height: 90vh;
  background: var(--surface, #fff);
  border-radius: var(--radius-lg, 12px);
  box-shadow: 0 16px 48px rgba(0,0,0,0.2);
  display: flex;
  flex-direction: column;
  overflow: hidden;
}
.memo-modal-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 16px 24px;
  border-bottom: 1px solid var(--border);
}
.memo-modal-title {
  font-size: 15px;
  font-weight: 700;
  color: var(--text);
}
.memo-modal-close {
  background: none;
  border: none;
  font-size: 22px;
  color: var(--text-dim);
  cursor: pointer;
  padding: 0 4px;
  line-height: 1;
}
.memo-modal-close:hover { color: var(--text); }
.memo-modal-body {
  flex: 1;
  padding: 20px 24px;
  overflow: auto;
}
.memo-modal-textarea {
  width: 100%;
  min-height: 60vh;
  padding: 16px;
  border: 1px solid var(--border);
  border-radius: 8px;
  font-size: 13px;
  line-height: 1.7;
  color: var(--text);
  font-family: var(--font-mono, monospace);
  resize: vertical;
  background: var(--bg, #f8f7f5);
}
.memo-modal-textarea:focus { border-color: var(--accent); outline: none; }

/* 물성·안정성 패널 */
.props-body {
  padding: 14px 20px;
}
.props-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 8px;
}
.prop-item {
  display: flex;
  justify-content: space-between;
  align-items: baseline;
  padding: 6px 10px;
  border-radius: 6px;
  background: var(--bg, #f8f7f5);
}
.prop-label {
  font-size: 11px;
  font-weight: 600;
  color: var(--text-sub);
  white-space: nowrap;
}
.prop-input {
  flex: 1;
  min-width: 0;
  padding: 4px 8px;
  border: 1px solid transparent;
  border-radius: 4px;
  font-size: 12px;
  font-weight: 600;
  color: var(--text);
  font-family: var(--font-mono, monospace);
  text-align: right;
  background: transparent;
  transition: border-color 0.15s;
}
.prop-input:hover { border-color: var(--border); }
.prop-input:focus { border-color: var(--accent); outline: none; background: var(--surface); }
.prop-input::placeholder { color: var(--text-dim); font-weight: 400; }
.props-hint {
  margin-left: auto;
  font-size: 11px;
  color: var(--text-dim);
  font-style: italic;
}
.props-section {
  margin-top: 12px;
  padding-top: 12px;
  border-top: 1px dashed var(--border);
}
.props-section-title {
  font-size: 11px;
  font-weight: 700;
  color: var(--text-sub);
  text-transform: uppercase;
  letter-spacing: 0.5px;
  margin-bottom: 8px;
}
.stability-item {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 4px 0;
  font-size: 12px;
}
.stab-cond {
  flex: 1;
  color: var(--text);
}
.stab-period {
  color: var(--text-sub);
  font-family: var(--font-mono);
  font-size: 11px;
}
.stab-input {
  width: 80px;
  padding: 2px 6px;
  border: 1px solid transparent;
  border-radius: 3px;
  font-size: 11px;
  font-family: var(--font-mono);
  color: var(--text-sub);
  text-align: center;
  background: transparent;
}
.stab-input:hover { border-color: var(--border); }
.stab-input:focus { border-color: var(--accent); outline: none; }
.stab-toggle {
  padding: 1px 8px;
  border: none;
  border-radius: 3px;
  font-size: 11px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.15s;
}
.stab-toggle.pass {
  background: var(--green-bg, #f0f8f4);
  color: var(--green, #3a9068);
}
.stab-toggle.warn {
  background: var(--amber-bg, #fdf8f0);
  color: var(--amber, #b07820);
}
.stab-toggle:hover { opacity: 0.8; }
.micro-input {
  width: 100%;
  padding: 6px 10px;
  border: 1px solid transparent;
  border-radius: 4px;
  font-size: 12px;
  color: var(--text);
  line-height: 1.5;
  background: transparent;
}
.micro-input:hover { border-color: var(--border); }
.micro-input:focus { border-color: var(--accent); outline: none; background: var(--surface); }

@media (max-width: 1199px) { .form-grid { grid-template-columns: 1fr; } }
</style>
