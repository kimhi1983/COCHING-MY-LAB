<template>
  <section>
    <div class="container h-100">
      <div class="content">
        <div class="common-content">

          <!-- Research Overview Dashboard (White Lab Theme) -->
          <div class="ro-wrap">
            <!-- 헤더 -->
            <div class="ro-header">
              <div>
                <div class="ro-date">{{ todayFormatted }}</div>
                <div class="ro-title">Research Overview</div>
              </div>
              <div class="ro-actions">
                <button class="ro-btn-ghost" @click="onExport">Export</button>
                <button class="ro-btn-accent" @click="onNewFormula">+ New Formula</button>
                <button class="ro-btn-primary" @click="loadDashboard">Refresh</button>
              </div>
            </div>

            <!-- 통계 카드 4개 -->
            <div class="ro-stats-grid">
              <div class="ro-stat-card" v-for="(stat, idx) in stats" :key="idx">
                <div class="ro-stat-top">
                  <span class="ro-stat-label">{{ stat.label }}</span>
                  <span class="ro-stat-icon" :style="{ background: stat.iconBg }">{{ stat.icon }}</span>
                </div>
                <div class="ro-stat-value-row">
                  <span class="ro-stat-value">{{ stat.value }}</span>
                  <span class="ro-stat-unit">{{ stat.unit }}</span>
                </div>
              </div>
            </div>

            <!-- 2컬럼: Activity Log + Stability Tests -->
            <div class="ro-two-col">
              <!-- Activity Log -->
              <div class="ro-panel">
                <div class="ro-panel-header">
                  <div>
                    <div class="ro-section-label">ACTIVITY</div>
                    <div class="ro-panel-title">Recent Lab Activity</div>
                  </div>
                  <button class="ro-btn-ghost" @click="onViewAllActivity">View All</button>
                </div>
                <div class="ro-divider"></div>
                <div class="ro-log-list">
                  <div
                    v-for="(log, idx) in activityLogs"
                    :key="idx"
                    class="ro-log-row"
                    :class="{ last: idx === activityLogs.length - 1 }"
                  >
                    <span class="ro-log-time">{{ log.time }}</span>
                    <span class="ro-log-bar" :style="{ background: log.color }"></span>
                    <div class="ro-log-body">
                      <div class="ro-chips">
                        <span
                          v-for="(chip, ci) in log.chips"
                          :key="ci"
                          class="ro-chip"
                          :style="{ background: chip.bg, color: chip.color }"
                        >{{ chip.text }}</span>
                      </div>
                      <div class="ro-log-title">{{ log.title }}</div>
                    </div>
                  </div>
                </div>
              </div>

              <!-- Stability Tests -->
              <div class="ro-panel">
                <div class="ro-panel-header">
                  <div>
                    <div class="ro-section-label">TESTING</div>
                    <div class="ro-panel-title">Stability Tests</div>
                  </div>
                  <button class="ro-btn-ghost" @click="onViewAllTests">View All</button>
                </div>
                <div class="ro-divider"></div>
                <div class="ro-stab-list">
                  <div
                    v-for="(test, idx) in stabilityTests"
                    :key="idx"
                    class="ro-stab-row"
                    :class="{ last: idx === stabilityTests.length - 1 }"
                  >
                    <div>
                      <div class="ro-stab-name">{{ test.name }}</div>
                      <div class="ro-stab-sub">{{ test.formula }}</div>
                    </div>
                    <div class="ro-stab-stat">
                      <span
                        class="ro-chip"
                        :style="{ background: test.statusBg, color: test.statusColor }"
                      >{{ test.status }}</span>
                    </div>
                    <div class="ro-stab-stat">{{ test.day }}</div>
                    <div class="ro-stab-stat" :style="{ color: test.scoreColor, fontWeight: 700 }">
                      {{ test.score }}
                    </div>
                  </div>
                </div>
              </div>
            </div>

            <!-- Ingredient Spotlight -->
            <div class="ro-panel">
              <div class="ro-panel-header">
                <div>
                  <div class="ro-section-label">INGREDIENTS</div>
                  <div class="ro-panel-title">Ingredient Spotlight</div>
                </div>
                <button class="ro-btn-accent" @click="onViewIngredients">Browse All</button>
              </div>
              <div class="ro-divider"></div>
              <div class="ro-ingredient-grid">
                <div
                  v-for="(ingr, idx) in ingredients"
                  :key="idx"
                  class="ro-ingredient-card"
                  @click="onClickIngredient(ingr)"
                >
                  <div class="ro-ingr-icon" :style="{ background: ingr.iconBg }">{{ ingr.icon }}</div>
                  <div class="ro-ingr-info">
                    <div class="ro-ingr-name">{{ ingr.name }}</div>
                    <div class="ro-ingr-desc">{{ ingr.desc }}</div>
                  </div>
                  <div class="ro-ingr-score" :style="{ color: ingr.scoreColor }">{{ ingr.score }}</div>
                </div>
              </div>
            </div>

          </div>
        </div>
      </div>

      <AlertModal ref="alertModal"></AlertModal>
    </div>
  </section>
</template>

<script>
import ernsUtils from '@/components/mixins/ernsUtils';

export default {
  name: 'ResearchOverview',
  mixins: [ernsUtils],

  data() {
    return {
      stats: [
        { label: 'ACTIVE FORMULAS', value: '24', unit: 'formulas', icon: '\u2697\ufe0f', iconBg: '#f0e8d8' },
        { label: 'INGREDIENTS DB', value: '1,847', unit: 'items', icon: '\ud83e\uddea', iconBg: '#f0f4fb' },
        { label: 'STABILITY TESTS', value: '12', unit: 'running', icon: '\ud83d\udcca', iconBg: '#f0f8f4' },
        { label: 'AI ANALYSES', value: '156', unit: 'completed', icon: '\u26a1', iconBg: '#f6f2fd' },
      ],
      activityLogs: [
        {
          time: '14:32',
          color: '#b8935a',
          chips: [{ text: 'Formula', bg: '#f0e8d8', color: '#b8935a' }],
          title: 'Hyaluronic Acid Serum v3 — pH stability updated',
        },
        {
          time: '11:15',
          color: '#7c5cbf',
          chips: [
            { text: 'AI', bg: '#f6f2fd', color: '#7c5cbf' },
            { text: 'Gemini', bg: '#f0f8f4', color: '#3a9068' },
          ],
          title: 'Niacinamide Cream — AI compatibility analysis complete',
        },
        {
          time: '09:48',
          color: '#3a6fa8',
          chips: [{ text: 'Test', bg: '#f0f4fb', color: '#3a6fa8' }],
          title: 'Retinol Night Cream — 30-day stability test started',
        },
        {
          time: '08:20',
          color: '#3a9068',
          chips: [{ text: 'Sourcing', bg: '#f0f8f4', color: '#3a9068' }],
          title: 'Centella Asiatica Extract — new supplier evaluation',
        },
      ],
      stabilityTests: [
        { name: 'pH Stability', formula: 'HA Serum v3', status: 'Active', statusBg: '#f0f8f4', statusColor: '#3a9068', day: 'D-14', score: '98.2%', scoreColor: '#3a9068' },
        { name: 'Viscosity', formula: 'Niacinamide Cream', status: 'Active', statusBg: '#f0f8f4', statusColor: '#3a9068', day: 'D-7', score: '95.1%', scoreColor: '#3a9068' },
        { name: 'Color Stability', formula: 'Retinol Night', status: 'Pending', statusBg: '#fdf8f0', statusColor: '#b07820', day: 'D-0', score: '—', scoreColor: '#aba59d' },
        { name: 'Micro Test', formula: 'Sunscreen SPF50', status: 'Complete', statusBg: '#f0f4fb', statusColor: '#3a6fa8', day: 'D-30', score: '99.8%', scoreColor: '#3a9068' },
      ],
      ingredients: [
        { name: 'Hyaluronic Acid', desc: 'Hydration booster', icon: '\ud83d\udca7', iconBg: '#f0f4fb', score: '9.8', scoreColor: '#3a9068' },
        { name: 'Niacinamide', desc: 'Brightening agent', icon: '\u2728', iconBg: '#fdf8f0', score: '9.5', scoreColor: '#3a9068' },
        { name: 'Centella Asiatica', desc: 'Soothing extract', icon: '\ud83c\udf3f', iconBg: '#f0f8f4', score: '9.2', scoreColor: '#b8935a' },
        { name: 'Retinol', desc: 'Anti-aging active', icon: '\ud83d\udd2c', iconBg: '#f6f2fd', score: '8.9', scoreColor: '#b8935a' },
      ],
    };
  },

  computed: {
    todayFormatted() {
      var now = new Date();
      var months = ['JAN','FEB','MAR','APR','MAY','JUN','JUL','AUG','SEP','OCT','NOV','DEC'];
      return months[now.getMonth()] + ' ' + now.getDate() + ', ' + now.getFullYear();
    },
  },

  mounted() {
    this.loadDashboard();
  },

  methods: {
    async loadDashboard() {
      // TODO: API 연동 — 실제 데이터 로딩
      // 현재는 목업 데이터 사용
    },

    onNewFormula() {
      this.ermPushPage({ name: 'coching-mylab' });
    },

    onExport() {
      // TODO: 대시보드 데이터 내보내기
      this.alertSuccess('Export 기능은 준비 중입니다.');
    },

    onViewAllActivity() {
      // TODO: 활동 로그 전체 보기
    },

    onViewAllTests() {
      // TODO: 안정성 테스트 전체 보기
    },

    onViewIngredients() {
      this.ermPushPage({ name: 'coching-search-raws' });
    },

    onClickIngredient(ingr) {
      // TODO: 성분 상세 페이지 이동
    },
  },
};
</script>

<style lang="scss" scoped>
/* ── White Lab Theme Variables ── */
$wl-bg: #f8f7f5;
$wl-surface: #ffffff;
$wl-border: #ece9e3;
$wl-border-mid: #d8d4cc;
$wl-accent: #b8935a;
$wl-accent-lt: #f0e8d8;
$wl-accent-dim: #e8dece;
$wl-text: #1a1814;
$wl-text-sub: #6b6560;
$wl-text-dim: #aba59d;
$wl-shadow: 0 1px 4px rgba(0,0,0,0.04);
$wl-radius: 10px;

/* ── Wrap ── */
.ro-wrap {
  padding: 28px 0;
  max-width: 1100px;
}

/* ── Header ── */
.ro-header {
  display: flex;
  align-items: flex-end;
  justify-content: space-between;
  margin-bottom: 24px;
}
.ro-date {
  font-size: 11px;
  color: $wl-text-dim;
  letter-spacing: 0.08em;
  text-transform: uppercase;
  margin-bottom: 4px;
}
.ro-title {
  font-size: 22px;
  font-weight: 700;
  color: $wl-text;
  letter-spacing: -0.3px;
}
.ro-actions {
  display: flex;
  gap: 8px;
}

/* ── Buttons ── */
.ro-btn-primary {
  background: $wl-text;
  color: #fff;
  border: none;
  border-radius: 8px;
  padding: 9px 18px;
  font-size: 13px;
  font-weight: 600;
  cursor: pointer;
  &:hover { opacity: 0.9; }
}
.ro-btn-ghost {
  background: transparent;
  color: $wl-text-sub;
  border: 1px solid $wl-border-mid;
  border-radius: 7px;
  padding: 6px 13px;
  font-size: 12px;
  font-weight: 500;
  cursor: pointer;
  &:hover { background: rgba(0,0,0,0.02); }
}
.ro-btn-accent {
  background: $wl-accent-lt;
  color: $wl-accent;
  border: 1px solid $wl-accent-dim;
  border-radius: 7px;
  padding: 6px 13px;
  font-size: 12px;
  font-weight: 600;
  cursor: pointer;
  &:hover { background: darken($wl-accent-lt, 3%); }
}

/* ── Stats Grid ── */
.ro-stats-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 14px;
  margin-bottom: 20px;
}
.ro-stat-card {
  background: $wl-surface;
  border: 1px solid $wl-border;
  border-radius: $wl-radius;
  padding: 18px 20px;
  box-shadow: $wl-shadow;
}
.ro-stat-top {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 12px;
}
.ro-stat-label {
  font-size: 10px;
  font-weight: 700;
  letter-spacing: 0.1em;
  color: $wl-text-dim;
}
.ro-stat-icon {
  width: 32px;
  height: 32px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 15px;
}
.ro-stat-value-row {
  display: flex;
  align-items: baseline;
  gap: 5px;
}
.ro-stat-value {
  font-size: 28px;
  font-weight: 800;
  letter-spacing: -1px;
  color: $wl-text;
}
.ro-stat-unit {
  font-size: 12px;
  color: $wl-text-sub;
  font-weight: 500;
}

/* ── Two Column ── */
.ro-two-col {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 16px;
  margin-bottom: 20px;
}

/* ── Panel ── */
.ro-panel {
  background: $wl-surface;
  border: 1px solid $wl-border;
  border-radius: $wl-radius;
  box-shadow: $wl-shadow;
  overflow: hidden;
  margin-bottom: 16px;
}
.ro-panel-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-end;
  padding: 18px 20px 14px;
}
.ro-section-label {
  font-size: 9px;
  font-weight: 700;
  letter-spacing: 0.12em;
  color: $wl-text-dim;
  margin-bottom: 3px;
}
.ro-panel-title {
  font-size: 15px;
  font-weight: 700;
  color: $wl-text;
}
.ro-divider {
  height: 1px;
  background: $wl-border;
}

/* ── Activity Log ── */
.ro-log-row {
  display: grid;
  grid-template-columns: 52px 3px 1fr;
  align-items: center;
  gap: 12px;
  padding: 12px 20px;
  border-bottom: 1px solid $wl-border;
  &.last { border-bottom: none; }
}
.ro-log-time {
  font-size: 11px;
  font-family: 'JetBrains Mono', 'Courier New', monospace;
  color: $wl-text-dim;
  white-space: nowrap;
}
.ro-log-bar {
  width: 3px;
  height: 36px;
  border-radius: 2px;
}
.ro-log-body {
  display: flex;
  flex-direction: column;
  gap: 4px;
}
.ro-chips {
  display: flex;
  gap: 5px;
  flex-wrap: wrap;
}
.ro-log-title {
  font-size: 13px;
  font-weight: 500;
  color: $wl-text;
}
.ro-chip {
  display: inline-flex;
  align-items: center;
  padding: 2px 8px;
  border-radius: 20px;
  font-size: 11px;
  font-weight: 600;
  white-space: nowrap;
}

/* ── Stability Tests ── */
.ro-stab-row {
  display: grid;
  grid-template-columns: 1fr 60px 80px 80px;
  align-items: center;
  gap: 10px;
  padding: 12px 20px;
  border-bottom: 1px solid $wl-border;
  &.last { border-bottom: none; }
}
.ro-stab-name {
  font-size: 13px;
  font-weight: 600;
  color: $wl-text;
}
.ro-stab-sub {
  font-size: 11px;
  color: $wl-text-dim;
  margin-top: 2px;
}
.ro-stab-stat {
  text-align: center;
  font-size: 12px;
  color: $wl-text-sub;
}

/* ── Ingredient Spotlight ── */
.ro-ingredient-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 0;
}
.ro-ingredient-card {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 16px 20px;
  border-right: 1px solid $wl-border;
  border-bottom: 1px solid $wl-border;
  cursor: pointer;
  transition: background 0.15s;
  &:hover { background: rgba(0,0,0,0.015); }
  &:nth-child(4n) { border-right: none; }
  &:nth-last-child(-n+4) { border-bottom: none; }
}
.ro-ingr-icon {
  width: 40px;
  height: 40px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 18px;
  flex-shrink: 0;
}
.ro-ingr-info { flex: 1; min-width: 0; }
.ro-ingr-name {
  font-size: 13px;
  font-weight: 600;
  color: $wl-text;
}
.ro-ingr-desc {
  font-size: 11px;
  color: $wl-text-dim;
  margin-top: 2px;
}
.ro-ingr-score {
  font-size: 16px;
  font-weight: 800;
  letter-spacing: -0.5px;
}
</style>
