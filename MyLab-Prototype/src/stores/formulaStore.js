import { reactive, computed } from 'vue'
import { useLocalStorage } from '../composables/useLocalStorage.js'

const formulas = useLocalStorage('mylab:formulas', [])

function generateId() {
  return 'F-' + String(Date.now()).slice(-6) + Math.random().toString(36).slice(2, 5)
}

export function useFormulaStore() {
  const totalCount = computed(() => formulas.value.length)
  const draftCount = computed(() => formulas.value.filter(f => f.status === 'draft').length)
  const reviewCount = computed(() => formulas.value.filter(f => f.status === 'review').length)
  const doneCount = computed(() => formulas.value.filter(f => f.status === 'done').length)

  function recentFormulas(n = 5) {
    return [...formulas.value].sort((a, b) => new Date(b.updated_at) - new Date(a.updated_at)).slice(0, n)
  }

  function byProject(projectId) {
    return formulas.value.filter(f => f.project_id === projectId)
  }

  function byStatus(status) {
    return formulas.value.filter(f => f.status === status)
  }

  function getById(id) {
    return formulas.value.find(f => f.id === id)
  }

  function addFormula(data) {
    const now = new Date().toISOString()
    const formula = {
      id: generateId(),
      user_id: 'local-user',
      title: data.title || '새 처방',
      product_type: data.product_type || '',
      status: 'draft',
      formula_data: data.formula_data || { ingredients: [], total_percentage: 0, notes: '' },
      memo: data.memo || '',
      tags: data.tags || [],
      project_id: data.project_id || '',
      created_at: now,
      updated_at: now,
    }
    formulas.value.push(formula)
    return formula
  }

  function updateFormula(id, data) {
    const idx = formulas.value.findIndex(f => f.id === id)
    if (idx === -1) return null
    const updated = { ...formulas.value[idx], ...data, updated_at: new Date().toISOString() }
    formulas.value[idx] = updated
    // Trigger reactivity
    formulas.value = [...formulas.value]
    return updated
  }

  function deleteFormula(id) {
    formulas.value = formulas.value.filter(f => f.id !== id)
  }

  function changeStatus(id, status) {
    return updateFormula(id, { status })
  }

  function searchFormulas(query, filters = {}) {
    let result = [...formulas.value]
    if (query) {
      const q = query.toLowerCase()
      result = result.filter(f =>
        f.title.toLowerCase().includes(q) ||
        f.product_type.toLowerCase().includes(q) ||
        f.memo.toLowerCase().includes(q)
      )
    }
    if (filters.status) result = result.filter(f => f.status === filters.status)
    if (filters.project_id) result = result.filter(f => f.project_id === filters.project_id)
    return result.sort((a, b) => new Date(b.updated_at) - new Date(a.updated_at))
  }

  return {
    formulas,
    totalCount, draftCount, reviewCount, doneCount,
    recentFormulas, byProject, byStatus, getById,
    addFormula, updateFormula, deleteFormula, changeStatus,
    searchFormulas,
  }
}
