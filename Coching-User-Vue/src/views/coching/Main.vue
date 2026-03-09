<!--
  @figma https://www.figma.com/design/4bwvHThbLk5xTpwNfkoyNh/COCHING-UI-Design
  Page: Main | Frame: Main-Desktop (1440px)
  디자인 참고: 업그레이드/white_lab_dashboard.jsx
-->
<template>
  <section id="coching-main" class="full main wl-root">
    <div class="wl-layout">

      <!-- ══ 메인 콘텐츠 ══ -->
      <main class="wl-content">

        <!-- 헤더 -->
        <div class="wl-header">
          <div>
            <div class="wl-date">{{ todayDate }}</div>
            <h1 class="wl-title">Research Overview</h1>
          </div>
          <button class="wl-btn-primary" @click="$refs.prescriptionModal.open()">⚗ 새 처방 생성</button>
        </div>

        <!-- KPI 스탯 카드 4개 -->
        <div class="wl-stats-grid">
          <div v-for="(s, i) in labStats" :key="i" class="wl-stat-card">
            <div class="wl-stat-top">
              <div class="wl-stat-label">{{ s.label.toUpperCase() }}</div>
              <div class="wl-stat-icon" :style="{ background: s.bg, border: '1px solid ' + s.border }">
                {{ s.icon }}
              </div>
            </div>
            <div class="wl-stat-value-row">
              <span class="wl-stat-value" :style="{ color: s.color }">{{ s.value }}</span>
              <span class="wl-stat-unit">{{ s.unit }}</span>
            </div>
          </div>
        </div>

        <!-- 2열: 오늘의 업무 + 안정성 현황 -->
        <div class="wl-two-col">

          <!-- 오늘의 업무 -->
          <div class="wl-panel">
            <div class="wl-panel-header">
              <div>
                <div class="wl-section-label">TODAY'S LOG</div>
                <div class="wl-panel-title">오늘의 업무</div>
              </div>
              <button class="wl-btn-ghost">전체 보기</button>
            </div>
            <div class="wl-divider"></div>
            <div
              v-for="(e, i) in todayEntries" :key="i"
              class="wl-log-row"
              :class="{ last: i === todayEntries.length - 1 }"
            >
              <span class="wl-log-time">{{ e.time }}</span>
              <div class="wl-log-bar" :style="{ background: typeStyle[e.type].color }"></div>
              <div class="wl-log-body">
                <div class="wl-chips">
                  <span class="wl-chip" :style="chipStyle(typeStyle[e.type].color, typeStyle[e.type].bg)">
                    {{ typeStyle[e.type].label }}
                  </span>
                  <span v-if="e.priority" class="wl-chip" style="color:#c44e4e;background:#fdf2f2;border:1px solid #e8c0c0;">우선</span>
                </div>
                <div class="wl-log-title">{{ e.title }}</div>
              </div>
              <span class="wl-chip" :style="chipStyle(statusStyle[e.status].color, statusStyle[e.status].bg)">
                {{ e.status }}
              </span>
            </div>
          </div>

          <!-- 안정성 현황 -->
          <div class="wl-panel">
            <div class="wl-panel-header">
              <div>
                <div class="wl-section-label">STABILITY STATUS</div>
                <div class="wl-panel-title">안정성 현황</div>
              </div>
              <button class="wl-btn-ghost">상세 보기</button>
            </div>
            <div class="wl-divider"></div>
            <div
              v-for="(s, i) in stability" :key="i"
              class="wl-stab-row"
              :class="{ last: i === stability.length - 1 }"
            >
              <div>
                <div class="wl-stab-name">{{ s.formula }}</div>
                <div class="wl-stab-sub">{{ s.temp }} · {{ s.week }}주</div>
              </div>
              <div class="wl-stab-stat">
                <div class="wl-micro-label">ΔE</div>
                <div class="wl-stab-val" :style="{ color: s.dE < 2 ? 'var(--wl-green)' : 'var(--wl-red)' }">{{ s.dE }}</div>
              </div>
              <div class="wl-stab-stat">
                <div class="wl-micro-label">점도</div>
                <div class="wl-stab-visc">{{ s.visc }}</div>
              </div>
              <span class="wl-chip" :style="chipStyle(
                (statusStyle[s.result] || statusStyle['진행중']).color,
                (statusStyle[s.result] || statusStyle['진행중']).bg
              )">{{ s.result }}</span>
            </div>
          </div>

        </div>

        <!-- 처방 목록 테이블 -->
        <div class="wl-panel wl-formula-panel">
          <div class="wl-panel-header">
            <div>
              <div class="wl-section-label">ACTIVE FORMULAS</div>
              <div class="wl-panel-title">처방 목록</div>
            </div>
            <button class="wl-btn-accent" @click="$refs.prescriptionModal.open()">+ 새 처방</button>
          </div>
          <div class="wl-divider"></div>
          <div class="wl-table-head">
            <span>ID</span>
            <span>처방명</span>
            <span>제형</span>
            <span>pH</span>
            <span>점도</span>
            <span>상태</span>
            <span>진행률</span>
          </div>
          <div
            v-for="(f, i) in formulas" :key="i"
            class="wl-table-row"
            :class="{ hovered: hovRow === i, last: i === formulas.length - 1 }"
            @mouseenter="hovRow = i"
            @mouseleave="hovRow = null"
          >
            <span class="wl-id">{{ f.id }}</span>
            <div>
              <div class="wl-formula-name">{{ f.name }}</div>
              <div class="wl-formula-ver">{{ f.ver }}</div>
            </div>
            <span class="wl-formula-type">{{ f.type }}</span>
            <span class="wl-ph">{{ f.ph }}</span>
            <span class="wl-visc">{{ f.visc }}</span>
            <span class="wl-chip" :style="chipStyle(statusStyle[f.status].color, statusStyle[f.status].bg)">
              {{ f.status }}
            </span>
            <div>
              <div class="wl-progress-row">
                <span class="wl-micro-label">진행률</span>
                <span class="wl-progress-pct" :style="{ color: f.pColor }">{{ f.progress }}%</span>
              </div>
              <div class="wl-bar-bg">
                <div class="wl-bar-fill" :style="{ width: f.progress + '%', background: f.pColor }"></div>
              </div>
            </div>
          </div>
        </div>

        <!-- 규제 현황 -->
        <div class="wl-panel">
          <div class="wl-panel-header">
            <div>
              <div class="wl-section-label">REGULATION WATCH</div>
              <div class="wl-panel-title">규제 현황</div>
            </div>
            <button class="wl-btn-ghost">전체 목록</button>
          </div>
          <div class="wl-divider"></div>
          <div class="wl-reg-head">
            <span>지역</span>
            <span>성분명</span>
            <span>상태</span>
            <span>한도</span>
            <span>비고</span>
            <span>업데이트</span>
          </div>
          <div
            v-for="(r, i) in regulations" :key="i"
            class="wl-reg-row"
            :class="{ last: i === regulations.length - 1 }"
          >
            <span class="wl-chip" :style="chipStyle(regionStyle[r.region].color, regionStyle[r.region].bg)">{{ r.region }}</span>
            <span class="wl-reg-ingredient">{{ r.ingredient }}</span>
            <span class="wl-chip" :style="chipStyle(regStatusStyle[r.status].color, regStatusStyle[r.status].bg)">{{ r.status }}</span>
            <span class="wl-reg-limit">{{ r.limit }}</span>
            <span class="wl-reg-note">{{ r.note }}</span>
            <span class="wl-reg-date">{{ r.updated }}</span>
          </div>
        </div>

        <!-- 푸터 -->
        <div class="wl-footer">
          <span>COCHING LAB STUDIO · COSMETIC R&amp;D</span>
          <span>v2.3 Hybrid · COMPOUND-EXPANSION SKILL v1.0</span>
        </div>

      </main>
    </div>

    <PrescriptionModal ref="prescriptionModal" @onPrescriptionDone="onPrescriptionDone" />
    <AlertModal ref="alertModal"></AlertModal>
    <ConfirmModal ref="confirmModal"></ConfirmModal>
    <RawInfoModal ref="rawInfoModal" @onClickRequest="onClickRequest" />
    <RequestRawModal v-if="isLoggedIn" ref="requestRawModal"></RequestRawModal>
  </section>
</template>

<script>
import ernsUtils from '@/components/mixins/ernsUtils';
import { isUserLoggedIn } from '@/auth/utils';

import AlertModal from '@/components/dialog/AlertModal.vue';
import ConfirmModal from '@/components/dialog/ConfirmModal.vue';
import RawInfoModal from '@/components/modal/RawInfoModal.vue';
import RequestRawModal from '@/components/modal/RequestRawModal.vue';
import PrescriptionModal from '@/components/modal/PrescriptionModal.vue';

export default {
  name: 'coching-main',
  mixins: [ernsUtils],
  components: { AlertModal, ConfirmModal, RawInfoModal, RequestRawModal, PrescriptionModal },

  data() {
    return {
      isLoggedIn: false,
      activeNav: 'overview',
      hovRow: null,

      navItems: [
        { id: 'overview',   icon: '◈', label: 'Overview'  },
        { id: 'formulas',   icon: '⚗', label: 'Formulas'  },
        { id: 'stability',  icon: '◎', label: 'Stability' },
        { id: 'regulation', icon: '§', label: 'Regulation'},
        { id: 'journal',    icon: '✦', label: 'Journal'   },
      ],

      labStats: [
        { label: '진행중 처방',  value: '4',  unit: '건',   color: '#b07820', bg: '#fdf8f0', border: '#e8d4a0', icon: '⚗️' },
        { label: '이번 달 완료', value: '7',  unit: '건',   color: '#3a9068', bg: '#f0f8f4', border: '#b8dece', icon: '✓' },
        { label: '안정성 진행',  value: '3',  unit: '배치', color: '#3a6fa8', bg: '#f0f4fb', border: '#b8cce8', icon: '🔬' },
        { label: '규제 체크',    value: '12', unit: '건',   color: '#7c5cbf', bg: '#f6f2fd', border: '#cfc0ee', icon: '§' },
      ],

      todayEntries: [
        { time: '09:15', type: 'formula', title: '쿠션 21호 v3.2 — TiO₂ 비율 재조정',         status: '진행중', priority: true  },
        { time: '11:30', type: 'test',    title: '선스틱 SPF50+ — 4주 안정성 최종 판정',        status: '완료',   priority: false },
        { time: '14:00', type: 'reg',     title: 'EU 규제 업데이트 — Benzophenone-3 제한 확인', status: '검토중', priority: true  },
        { time: '16:45', type: 'idea',    title: 'Bakuchiol × 세라마이드 시너지 메모',           status: '메모',   priority: false },
      ],

      formulas: [
        { id: 'F-041', name: '쿠션 파운데이션 21호', ver: 'v3.2', type: 'W/Si 에멀전',  ph: 5.8, visc: '3,200',  status: '개발중',     progress: 65,  pColor: '#b07820' },
        { id: 'F-038', name: '선스틱 SPF50+',        ver: 'v2.1', type: '유화스틱',     ph: 6.1, visc: '—',      status: '안정성검증', progress: 88,  pColor: '#3a6fa8' },
        { id: 'F-035', name: '클렌징 폼 약산성',      ver: 'v1.4', type: 'O/W 폼',      ph: 5.5, visc: '12,000', status: '완료',       progress: 100, pColor: '#3a9068' },
        { id: 'F-029', name: '세럼 — 바키아 라인',    ver: 'v1.1', type: '수성 세럼',   ph: 6.0, visc: '4,500',  status: '처방설계',   progress: 30,  pColor: '#7c5cbf' },
      ],

      stability: [
        { formula: '쿠션 21호',     temp: '50°C', week: 4, dE: 1.2, visc: '+8%', result: 'PASS'  },
        { formula: '쿠션 21호',     temp: '4°C',  week: 4, dE: 0.4, visc: '-2%', result: 'PASS'  },
        { formula: '선스틱 SPF50+', temp: '50°C', week: 4, dE: 0.9, visc: '+5%', result: 'PASS'  },
        { formula: '세럼 바키아',   temp: '50°C', week: 1, dE: 0.6, visc: '+3%', result: '진행중' },
      ],

      regulations: [
        { region: 'KR', ingredient: 'Zinc Pyrithione (ZPT)',  status: '제한',     limit: '1~2%', note: '샴푸·린스류', updated: '2025-11' },
        { region: 'EU', ingredient: 'Benzophenone-3',          status: '제한',     limit: '6%',   note: '자외선차단',  updated: '2026-01' },
        { region: 'EU', ingredient: 'Titanium Dioxide (나노)', status: '금지',     limit: '—',    note: '분무제형',    updated: '2022-05' },
        { region: 'US', ingredient: 'Vitamin A (Retinol)',     status: '모니터링', limit: '—',    note: '야간크림',    updated: '2025-09' },
        { region: 'KR', ingredient: 'Hydroquinone',            status: '금지',     limit: '—',    note: '전 제형',     updated: '2019-03' },
      ],

      typeStyle: {
        formula: { color: '#b8935a', bg: '#f0e8d8', label: '처방'    },
        test:    { color: '#3a6fa8', bg: '#f0f4fb', label: '테스트'  },
        reg:     { color: '#c44e4e', bg: '#fdf2f2', label: '규제'    },
        idea:    { color: '#7c5cbf', bg: '#f6f2fd', label: '아이디어'},
      },

      statusStyle: {
        '완료':       { color: '#3a9068', bg: '#f0f8f4' },
        '진행중':     { color: '#b07820', bg: '#fdf8f0' },
        '검토중':     { color: '#7c5cbf', bg: '#f6f2fd' },
        '메모':       { color: '#aba59d', bg: '#f8f7f5' },
        '개발중':     { color: '#b07820', bg: '#fdf8f0' },
        '안정성검증': { color: '#3a6fa8', bg: '#f0f4fb' },
        '처방설계':   { color: '#7c5cbf', bg: '#f6f2fd' },
        'PASS':       { color: '#3a9068', bg: '#f0f8f4' },
        'FAIL':       { color: '#c44e4e', bg: '#fdf2f2' },
      },

      regionStyle: {
        KR: { color: '#3a6fa8', bg: '#f0f4fb' },
        EU: { color: '#7c5cbf', bg: '#f6f2fd' },
        US: { color: '#3a9068', bg: '#f0f8f4' },
      },

      regStatusStyle: {
        '제한':     { color: '#b07820', bg: '#fdf8f0' },
        '금지':     { color: '#c44e4e', bg: '#fdf2f2' },
        '모니터링': { color: '#7c5cbf', bg: '#f6f2fd' },
      },
    };
  },

  computed: {
    todayDate() {
      return new Date().toLocaleDateString('ko-KR', {
        year: 'numeric', month: 'long', day: 'numeric', weekday: 'long',
      });
    },
  },

  mounted() {
    this.isLoggedIn = isUserLoggedIn() ? true : false;
  },

  methods: {
    chipStyle(color, bg) {
      return { background: bg, color, border: `1px solid ${color}33` };
    },

    onPrescriptionDone(result) {
      this.formulas.unshift(result);
      this.labStats[0].value = String(this.formulas.filter(f => f.status !== '완료').length);
    },

    async onClickRequest(rawInfo, detailItem) {
      const _vm = this;
      if (!_vm.isLoggedIn) {
        _vm.onClickConfirmLogin();
        return;
      }
      await _vm.$refs.requestRawModal.open({
        rawInfo: {
          rawSeq: rawInfo.rawSeq,
          rawDetailSeq: detailItem.rawDetailSeq,
          membSeq: detailItem.membSeq,
        },
      });
    },
  },
};
</script>

<style lang="scss" scoped>
/* ══════════════════════════════════════════
   WHITE LAB DASHBOARD — warm beige palette
   (참고: 업그레이드/white_lab_dashboard.jsx)
══════════════════════════════════════════ */

/* CSS 변수는 :root (style.scss)에서 전역 관리 */
.wl-root {}

/* ── 레이아웃 (COCHING GNB 사용 — 자체 사이드바 없음) ── */
.wl-layout {
  display: block;
  background: var(--wl-bg);
  font-family: var(--wl-font);
  color: var(--wl-text);
  font-size: 14px;
  -webkit-font-smoothing: antialiased;
  min-height: 100%;
}

/* ── 사이드바 ── */
.wl-sidebar {
  width: 210px;
  flex-shrink: 0;
  background: var(--wl-surface);
  border-right: 1px solid var(--wl-border);
  display: flex;
  flex-direction: column;
  position: sticky;
  top: 0;
  height: 100vh;
}

.wl-logo {
  padding: 26px 22px 22px;
  border-bottom: 1px solid var(--wl-border);

  .wl-logo-brand {
    font-size: 10px;
    color: var(--wl-accent);
    letter-spacing: 3px;
    font-family: var(--wl-mono);
    margin-bottom: 4px;
  }
  .wl-logo-title {
    font-size: 18px;
    font-weight: 700;
    color: var(--wl-text);
    letter-spacing: -0.2px;
  }
  .wl-logo-sub {
    font-size: 10px;
    color: var(--wl-text-dim);
    letter-spacing: 1.5px;
    font-family: var(--wl-mono);
    margin-top: 2px;
  }
}

.wl-nav {
  padding: 14px 10px;
  flex: 1;
}

.wl-nav-btn {
  width: 100%;
  padding: 9px 12px;
  display: flex;
  align-items: center;
  gap: 9px;
  background: transparent;
  border: none;
  border-radius: 6px;
  color: var(--wl-text-sub);
  cursor: pointer;
  text-align: left;
  font-size: 13px;
  font-family: var(--wl-font);
  font-weight: 400;
  margin-bottom: 2px;
  transition: all 0.15s;

  &:hover { background: var(--wl-bg); }

  &.active {
    background: var(--wl-accent-lt);
    color: var(--wl-accent);
    font-weight: 600;

    .wl-nav-icon { opacity: 1; }
  }
}

.wl-nav-icon {
  font-size: 13px;
  width: 18px;
  text-align: center;
  opacity: 0.6;
}

.wl-profile {
  padding: 16px 22px;
  border-top: 1px solid var(--wl-border);
  display: flex;
  align-items: center;
  gap: 10px;
}

.wl-avatar {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  background: var(--wl-accent-lt);
  border: 1px solid var(--wl-accent-dim);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 12px;
  color: var(--wl-accent);
  font-weight: 700;
}

.wl-profile-name {
  font-size: 12px;
  font-weight: 600;
  color: var(--wl-text);
}

.wl-profile-role {
  font-size: 9px;
  color: var(--wl-text-dim);
  font-family: monospace;
  letter-spacing: 0.5px;
}

/* ── 메인 콘텐츠 ── */
.wl-content {
  display: block;
  padding: 28px 32px;
}

/* ── 헤더 ── */
.wl-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-end;
  margin-bottom: 28px;
}

.wl-date {
  font-size: 9px;
  color: var(--wl-text-dim);
  letter-spacing: 2px;
  font-family: monospace;
  margin-bottom: 5px;
}

.wl-title {
  margin: 0;
  font-size: 22px;
  font-weight: 700;
  color: var(--wl-text);
  letter-spacing: -0.3px;
}

/* ── 버튼 ── */
.wl-btn-primary {
  background: var(--wl-accent);
  color: #fff;
  border: none;
  border-radius: 7px;
  padding: 10px 20px;
  cursor: pointer;
  font-size: 13px;
  font-weight: 600;
  font-family: var(--wl-font);
  letter-spacing: 0.2px;
  box-shadow: 0 2px 12px rgba(184,147,90,0.28);
  transition: background 0.15s, box-shadow 0.15s;

  &:hover {
    background: #a67d47;
    box-shadow: 0 4px 18px rgba(184,147,90,0.38);
  }
}

.wl-btn-ghost {
  background: none;
  border: 1px solid var(--wl-border-mid);
  border-radius: 6px;
  padding: 6px 12px;
  color: var(--wl-text-sub);
  font-size: 12px;
  font-weight: 500;
  cursor: pointer;
  font-family: var(--wl-font);
  transition: all 0.15s;

  &:hover {
    border-color: var(--wl-accent);
    color: var(--wl-accent);
    background: var(--wl-accent-lt);
  }
}

.wl-btn-accent {
  background: var(--wl-accent-lt);
  border: 1px solid var(--wl-accent-dim);
  color: var(--wl-accent);
  border-radius: 6px;
  padding: 6px 14px;
  cursor: pointer;
  font-size: 12px;
  font-weight: 600;
  font-family: var(--wl-font);
  transition: all 0.15s;

  &:hover {
    background: var(--wl-accent);
    color: #fff;
    border-color: var(--wl-accent);
  }
}

/* ── KPI 카드 ── */
.wl-stats-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 14px;
  margin-bottom: 24px;
}

.wl-stat-card {
  background: var(--wl-surface);
  border: 1px solid var(--wl-border);
  border-radius: var(--wl-radius);
  padding: 18px 20px;
  box-shadow: var(--wl-shadow);
}

.wl-stat-top {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
}

.wl-stat-label {
  font-size: 10px;
  color: var(--wl-text-dim);
  letter-spacing: 1px;
  font-family: monospace;
}

.wl-stat-icon {
  width: 28px;
  height: 28px;
  border-radius: 7px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 13px;
}

.wl-stat-value-row {
  display: flex;
  align-items: baseline;
  gap: 5px;
  margin-top: 12px;
}

.wl-stat-value {
  font-size: 26px;
  font-weight: 700;
  line-height: 1;
}

.wl-stat-unit {
  font-size: 11px;
  color: var(--wl-text-dim);
}

/* ── 2열 레이아웃 ── */
.wl-two-col {
  display: grid;
  grid-template-columns: 1.1fr 0.9fr;
  gap: 16px;
  margin-bottom: 16px;
}

/* ── 패널 공통 ── */
.wl-panel {
  background: var(--wl-surface);
  border: 1px solid var(--wl-border);
  border-radius: var(--wl-radius);
  overflow: hidden;
  box-shadow: var(--wl-shadow);
  margin-bottom: 16px;

  &.wl-formula-panel { margin-bottom: 16px; }
}

.wl-panel-header {
  padding: 18px 20px 14px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.wl-section-label {
  font-size: 9px;
  color: var(--wl-text-dim);
  letter-spacing: 2.5px;
  font-family: monospace;
  margin-bottom: 4px;
}

.wl-panel-title {
  font-size: 14px;
  font-weight: 600;
  color: var(--wl-text);
  margin-top: -4px;
}

.wl-divider {
  height: 1px;
  background: var(--wl-border);
}

/* ── 칩 ── */
.wl-chip {
  display: inline-block;
  border-radius: 3px;
  padding: 2px 8px;
  font-size: 10px;
  font-weight: 600;
  letter-spacing: 0.5px;
  font-family: monospace;
  white-space: nowrap;
}

/* ── 오늘의 업무 로그 ── */
.wl-log-row {
  display: flex;
  gap: 12px;
  align-items: flex-start;
  padding: 13px 20px;
  border-bottom: 1px solid var(--wl-border);
  cursor: pointer;
  transition: background 0.12s;

  &.last { border-bottom: none; }
  &:hover { background: var(--wl-bg); }
}

.wl-log-time {
  font-size: 10px;
  color: var(--wl-text-dim);
  font-family: monospace;
  flex-shrink: 0;
  margin-top: 3px;
  width: 38px;
}

.wl-log-bar {
  width: 2.5px;
  align-self: stretch;
  border-radius: 2px;
  flex-shrink: 0;
  opacity: 0.5;
}

.wl-log-body { flex: 1; }

.wl-chips {
  display: flex;
  gap: 5px;
  margin-bottom: 4px;
}

.wl-log-title {
  font-size: 12.5px;
  color: var(--wl-text);
  line-height: 1.45;
}

/* ── 안정성 ── */
.wl-stab-row {
  display: grid;
  grid-template-columns: 1fr 52px 52px 64px;
  gap: 8px;
  align-items: center;
  padding: 12px 20px;
  border-bottom: 1px solid var(--wl-border);
  cursor: pointer;
  transition: background 0.12s;

  &.last { border-bottom: none; }
  &:hover { background: var(--wl-bg); }
}

.wl-stab-name {
  font-size: 12px;
  color: var(--wl-text);
  font-weight: 500;
}

.wl-stab-sub {
  font-size: 10px;
  color: var(--wl-text-dim);
  font-family: monospace;
  margin-top: 2px;
}

.wl-stab-stat { text-align: center; }

.wl-micro-label {
  font-size: 9px;
  color: var(--wl-text-dim);
  font-family: monospace;
  margin-bottom: 2px;
}

.wl-stab-val {
  font-size: 13px;
  font-weight: 700;
}

.wl-stab-visc {
  font-size: 12px;
  color: var(--wl-text-sub);
  font-family: monospace;
}

/* ── 처방 테이블 ── */
.wl-table-head,
.wl-table-row {
  display: grid;
  grid-template-columns: 56px 1fr 90px 56px 72px 90px 180px;
  gap: 12px;
  padding: 8px 20px;
  align-items: center;
}

.wl-table-head {
  background: var(--wl-bg);
  border-bottom: 1px solid var(--wl-border);

  span {
    font-size: 9px;
    color: var(--wl-text-dim);
    letter-spacing: 1.5px;
    font-family: monospace;
  }
}

.wl-table-row {
  padding-top: 13px;
  padding-bottom: 13px;
  border-bottom: 1px solid var(--wl-border);
  cursor: pointer;
  transition: background 0.12s;

  &.last { border-bottom: none; }
  &.hovered, &:hover { background: var(--wl-bg); }
}

.wl-id {
  font-size: 10px;
  color: var(--wl-text-dim);
  font-family: monospace;
}

.wl-formula-name {
  font-size: 13px;
  font-weight: 600;
  color: var(--wl-text);
}

.wl-formula-ver {
  font-size: 10px;
  color: var(--wl-text-dim);
  margin-top: 1px;
}

.wl-formula-type {
  font-size: 11px;
  color: var(--wl-text-sub);
}

.wl-ph {
  font-size: 13px;
  font-weight: 700;
  color: var(--wl-green);
  font-family: monospace;
}

.wl-visc {
  font-size: 11px;
  color: var(--wl-text-sub);
  font-family: monospace;
}

.wl-progress-row {
  display: flex;
  justify-content: space-between;
  margin-bottom: 4px;
}

.wl-progress-pct {
  font-size: 9px;
  font-family: monospace;
  font-weight: 700;
}

.wl-bar-bg {
  height: 3px;
  background: var(--wl-border);
  border-radius: 99px;
  overflow: hidden;
}

.wl-bar-fill {
  height: 100%;
  border-radius: 99px;
  transition: width 0.5s;
}

/* ── 규제 ── */
.wl-reg-head,
.wl-reg-row {
  display: grid;
  grid-template-columns: 56px 1fr 72px 64px 1fr 80px;
  gap: 12px;
  padding: 8px 20px;
  align-items: center;
}

.wl-reg-head {
  background: var(--wl-bg);
  border-bottom: 1px solid var(--wl-border);

  span {
    font-size: 9px;
    color: var(--wl-text-dim);
    letter-spacing: 1.5px;
    font-family: monospace;
  }
}

.wl-reg-row {
  padding-top: 12px;
  padding-bottom: 12px;
  border-bottom: 1px solid var(--wl-border);
  cursor: pointer;
  transition: background 0.12s;

  &.last { border-bottom: none; }
  &:hover { background: var(--wl-bg); }
}

.wl-reg-ingredient {
  font-size: 12.5px;
  color: var(--wl-text);
}

.wl-reg-limit,
.wl-reg-date {
  font-size: 11px;
  color: var(--wl-text-sub);
  font-family: monospace;
}

.wl-reg-note {
  font-size: 11px;
  color: var(--wl-text-dim);
}

/* ── 푸터 ── */
.wl-footer {
  margin-top: 28px;
  padding-top: 16px;
  border-top: 1px solid var(--wl-border);
  display: flex;
  justify-content: space-between;
  font-family: monospace;
  font-size: 9px;
  color: var(--wl-text-dim);
  letter-spacing: 0.8px;
}
</style>
