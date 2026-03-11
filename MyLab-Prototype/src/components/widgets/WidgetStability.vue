<template>
  <div class="stability-widget">
    <table class="mini-table">
      <thead>
        <tr>
          <th>처방명</th>
          <th>조건</th>
          <th>주차</th>
          <th>ΔE</th>
          <th>점도변화</th>
          <th>결과</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="row in stabilityData" :key="row.id">
          <td class="cell-name">{{ row.name }}</td>
          <td class="cell-cond">{{ row.condition }}</td>
          <td class="cell-week">W{{ row.week }}</td>
          <td>
            <span class="delta-e" :class="getDeltaEClass(row.deltaE)">
              {{ row.deltaE.toFixed(1) }}
            </span>
          </td>
          <td class="cell-visc" :class="getViscClass(row.viscChange)">
            {{ row.viscChange > 0 ? '+' : '' }}{{ row.viscChange }}%
          </td>
          <td>
            <span class="result-chip" :class="getResultClass(row.result)">
              {{ getResultLabel(row.result) }}
            </span>
          </td>
        </tr>
      </tbody>
    </table>
  </div>
</template>

<script setup>
const stabilityData = [
  { id: 1, name: '쿠션 21호', condition: '50°C', week: 8, deltaE: 0.8, viscChange: -3.2, result: 'pass' },
  { id: 2, name: '쿠션 21호', condition: 'RT', week: 8, deltaE: 0.3, viscChange: -0.5, result: 'pass' },
  { id: 3, name: '선스틱 SPF50+', condition: '45°C', week: 4, deltaE: 2.4, viscChange: -12.1, result: 'fail' },
  { id: 4, name: '세럼 바쿠치아', condition: '50°C', week: 2, deltaE: 1.1, viscChange: -1.8, result: 'ongoing' },
]

function getDeltaEClass(val) {
  return val >= 2 ? 'delta-bad' : 'delta-ok'
}

function getViscClass(val) {
  return Math.abs(val) > 10 ? 'visc-bad' : 'visc-ok'
}

function getResultClass(result) {
  if (result === 'pass') return 'chip-green'
  if (result === 'fail') return 'chip-red'
  return 'chip-blue'
}

function getResultLabel(result) {
  if (result === 'pass') return 'PASS'
  if (result === 'fail') return 'FAIL'
  return '진행중'
}
</script>

<style scoped>
.stability-widget {
  overflow: auto;
  max-height: 100%;
}

.mini-table {
  width: 100%;
  border-collapse: collapse;
}

.mini-table th {
  background: var(--bg);
  font-size: 11px;
  font-family: var(--font-mono);
  text-transform: uppercase;
  letter-spacing: 0.8px;
  color: var(--text-dim);
  padding: 6px 8px;
  text-align: left;
  position: sticky;
  top: 0;
  white-space: nowrap;
}

.mini-table td {
  padding: 7px 8px;
  font-size: 12px;
  border-bottom: 1px solid var(--border);
  vertical-align: middle;
}

.mini-table tbody tr:hover {
  background: var(--bg);
}

.cell-name {
  font-weight: 500;
  font-size: 12px;
  max-width: 100px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.cell-cond {
  font-family: var(--font-mono);
  font-size: 10px;
  color: var(--text-sub);
}

.cell-week {
  font-family: var(--font-mono);
  font-size: 10px;
  color: var(--text-dim);
}

.delta-e {
  font-family: var(--font-mono);
  font-size: 11px;
  font-weight: 600;
  padding: 2px 5px;
  border-radius: 3px;
}

.delta-ok {
  color: var(--green);
  background: rgba(58, 144, 104, 0.1);
}

.delta-bad {
  color: var(--red);
  background: rgba(196, 78, 78, 0.1);
}

.cell-visc {
  font-family: var(--font-mono);
  font-size: 10px;
}

.visc-ok { color: var(--text-sub); }
.visc-bad { color: var(--red); font-weight: 600; }

/* 결과 칩 */
.result-chip {
  display: inline-block;
  padding: 2px 8px;
  border-radius: 3px;
  font-size: 10px;
  font-weight: 600;
  letter-spacing: 0.5px;
}

.chip-green {
  background: rgba(58, 144, 104, 0.15);
  color: var(--green);
}

.chip-red {
  background: rgba(196, 78, 78, 0.15);
  color: var(--red);
}

.chip-blue {
  background: rgba(58, 111, 168, 0.15);
  color: var(--blue);
}
</style>
