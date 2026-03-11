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
.stability-widget { overflow: auto; max-height: 100%; }

.mini-table { width: 100%; border-collapse: collapse; }
.mini-table th {
  background: var(--bg);
  font-size: clamp(9px, 2.5cqi, 12px);
  font-family: var(--font-mono);
  text-transform: uppercase;
  letter-spacing: 0.6px;
  color: var(--text-dim);
  padding: clamp(3px, 1cqi, 7px) clamp(4px, 1.5cqi, 10px);
  text-align: left; position: sticky; top: 0; white-space: nowrap;
}
.mini-table td {
  padding: clamp(4px, 1.2cqi, 8px) clamp(4px, 1.5cqi, 10px);
  font-size: clamp(10px, 2.5cqi, 13px);
  border-bottom: 1px solid var(--border); vertical-align: middle;
}
.mini-table tbody tr:hover { background: var(--bg); }

.cell-name { font-weight: 500; white-space: nowrap; overflow: hidden; text-overflow: ellipsis; max-width: clamp(60px, 20cqi, 140px); }
.cell-cond { font-family: var(--font-mono); font-size: clamp(9px, 2cqi, 11px); color: var(--text-sub); }
.cell-week { font-family: var(--font-mono); font-size: clamp(9px, 2cqi, 11px); color: var(--text-dim); }

.delta-e { font-family: var(--font-mono); font-size: clamp(10px, 2.5cqi, 12px); font-weight: 600; padding: 2px 5px; border-radius: 3px; }
.delta-ok { color: var(--green); background: rgba(58, 144, 104, 0.1); }
.delta-bad { color: var(--red); background: rgba(196, 78, 78, 0.1); }

.cell-visc { font-family: var(--font-mono); font-size: clamp(9px, 2cqi, 11px); }
.visc-ok { color: var(--text-sub); }
.visc-bad { color: var(--red); font-weight: 600; }

.result-chip { display: inline-block; padding: 2px clamp(4px, 1.5cqi, 8px); border-radius: 3px; font-size: clamp(9px, 2cqi, 11px); font-weight: 600; letter-spacing: 0.3px; white-space: nowrap; }
.chip-green { background: rgba(58, 144, 104, 0.15); color: var(--green); }
.chip-red { background: rgba(196, 78, 78, 0.15); color: var(--red); }
.chip-blue { background: rgba(58, 111, 168, 0.15); color: var(--blue); }
</style>
