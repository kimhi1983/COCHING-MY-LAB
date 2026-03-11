import { computed } from 'vue'
import { useLocalStorage } from '../composables/useLocalStorage.js'

// 사용 가능한 위젯 목록 (카탈로그)
export const WIDGET_CATALOG = [
  { id: 'kpi', label: 'KPI 카드', icon: '⚗', description: '총 처방, 진행중, 완료, 프로젝트 현황', minW: 2, minH: 2, defaultW: 4, defaultH: 2 },
  { id: 'recent', label: '최근 처방', icon: '◉', description: '최근 수정된 처방 목록', minW: 2, minH: 2, defaultW: 2, defaultH: 3 },
  { id: 'quick', label: '빠른 작업', icon: '✦', description: 'AI 가이드, 새 처방 등 빠른 액션', minW: 2, minH: 2, defaultW: 2, defaultH: 3 },
  { id: 'active', label: '진행중 처방', icon: '◎', description: '현재 진행 중인 처방 테이블', minW: 3, minH: 2, defaultW: 4, defaultH: 3 },
  { id: 'projects', label: '프로젝트 요약', icon: '◈', description: '프로젝트별 진행률 요약', minW: 2, minH: 2, defaultW: 2, defaultH: 3 },
  { id: 'chart', label: '상태 차트', icon: '◐', description: '처방 상태별 도넛 차트', minW: 2, minH: 2, defaultW: 2, defaultH: 2 },
  { id: 'memo', label: '메모장', icon: '✎', description: '자유 메모 (자동 저장)', minW: 2, minH: 2, defaultW: 2, defaultH: 2 },
]

// 기본 레이아웃 (첫 방문 시)
const DEFAULT_LAYOUT = [
  { x: 0, y: 0, w: 4, h: 2, i: 'kpi' },
  { x: 0, y: 2, w: 2, h: 3, i: 'recent' },
  { x: 2, y: 2, w: 2, h: 3, i: 'quick' },
  { x: 0, y: 5, w: 4, h: 3, i: 'active' },
  { x: 4, y: 0, w: 2, h: 2, i: 'chart' },
  { x: 4, y: 2, w: 2, h: 3, i: 'projects' },
]

const savedLayout = useLocalStorage('mylab:dashboard-layout', null)

export function useWidgetStore() {
  // 현재 레이아웃
  const layout = computed({
    get: () => savedLayout.value || JSON.parse(JSON.stringify(DEFAULT_LAYOUT)),
    set: (val) => { savedLayout.value = val },
  })

  // 현재 활성 위젯 ID 목록
  const activeWidgetIds = computed(() => layout.value.map(item => item.i))

  // 추가 가능한 위젯 (현재 레이아웃에 없는 것)
  const availableWidgets = computed(() =>
    WIDGET_CATALOG.filter(w => !activeWidgetIds.value.includes(w.id))
  )

  // 레이아웃 저장
  function saveLayout(newLayout) {
    savedLayout.value = newLayout.map(item => ({
      x: item.x, y: item.y, w: item.w, h: item.h, i: item.i,
    }))
  }

  // 위젯 추가
  function addWidget(widgetId) {
    const catalog = WIDGET_CATALOG.find(w => w.id === widgetId)
    if (!catalog || activeWidgetIds.value.includes(widgetId)) return

    // 빈 위치 찾기: 가장 아래에 추가
    const maxY = layout.value.reduce((max, item) => Math.max(max, item.y + item.h), 0)
    const newItem = {
      x: 0,
      y: maxY,
      w: catalog.defaultW,
      h: catalog.defaultH,
      i: widgetId,
    }
    savedLayout.value = [...layout.value, newItem]
  }

  // 위젯 제거
  function removeWidget(widgetId) {
    savedLayout.value = layout.value.filter(item => item.i !== widgetId)
  }

  // 기본 레이아웃으로 복원
  function resetLayout() {
    savedLayout.value = JSON.parse(JSON.stringify(DEFAULT_LAYOUT))
  }

  return {
    layout,
    activeWidgetIds,
    availableWidgets,
    saveLayout,
    addWidget,
    removeWidget,
    resetLayout,
  }
}
