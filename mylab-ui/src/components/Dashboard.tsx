import { useState } from 'react'

const recentFormulations = [
  { id: 'PRX-2024-089', name: '이니스프리 그린티 씨드 크림', category: 'O/W 크림', status: 'complete', accuracy: 97, date: '2026-03-08', ph: '5.8', ingredients: 23 },
  { id: 'PRX-2024-088', name: 'ZPT 비듬 방지 샴푸', category: '샴푸', status: 'review', accuracy: 91, date: '2026-03-07', ph: '5.2', ingredients: 18 },
  { id: 'PRX-2024-087', name: '수분 토너 (히알루론산 복합체)', category: '토너', status: 'complete', accuracy: 99, date: '2026-03-06', ph: '6.0', ingredients: 14 },
  { id: 'PRX-2024-086', name: '자외선 차단 크림 SPF50+', category: '선크림', status: 'warn', accuracy: 88, date: '2026-03-05', ph: '6.5', ingredients: 31 },
  { id: 'PRX-2024-085', name: '레티놀 나이트 크림', category: '크림', status: 'complete', accuracy: 95, date: '2026-03-04', ph: '5.5', ingredients: 27 },
]

const stats = [
  { label: 'Total Formulations', value: '89', unit: '건', delta: '+12 this month', color: 'var(--accent)' },
  { label: 'Avg. Accuracy', value: '94.2', unit: '%', delta: '+1.8% vs v2.2', color: 'var(--green)' },
  { label: 'Avg. Process Time', value: '38', unit: 'sec', delta: 'Target: <60s ✓', color: 'var(--teal)' },
  { label: 'Regulation Alerts', value: '3', unit: '건', delta: 'KR·EU·US', color: 'var(--amber)' },
]

const systemLog = [
  { time: '09:42', type: 'info',    msg: 'Gemini 2.5 Pro — TDS 리서치 완료 (Camellia Sinensis Seed Oil)' },
  { time: '09:38', type: 'success', msg: 'PRX-2024-089 처방 생성 완료 — 합계 100.00% ✓' },
  { time: '09:35', type: 'warn',    msg: 'ZPT 성분 EU 규제 감지 — 대체 성분 권고 (Piroctone Olamine)' },
  { time: '09:21', type: 'info',    msg: 'QA 17개 자동 체크 통과 — PRX-2024-087' },
  { time: '08:55', type: 'success', msg: 'Memory DB 갱신 — 3개 신규 성분 저장' },
]

export default function Dashboard() {
  return (
    <div style={{ padding: '32px 36px', maxWidth: 1200 }}>

      {/* 페이지 헤더 */}
      <div className="fade-in" style={{ marginBottom: 32 }}>
        <div style={{ display: 'flex', alignItems: 'baseline', gap: 12, marginBottom: 4 }}>
          <h1 className="font-display" style={{ fontSize: 26, fontWeight: 700, color: 'var(--text)', lineHeight: 1 }}>
            Research Overview
          </h1>
          <span style={{ fontFamily: 'var(--font-mono)', fontSize: 11, color: 'var(--dim)', letterSpacing: '0.08em' }}>
            — 연구 현황
          </span>
        </div>
        <div style={{ fontFamily: 'var(--font-mono)', fontSize: 11, color: 'var(--dim)', letterSpacing: '0.06em' }}>
          COCHING AI v2.3 · Claude × Gemini Hybrid · {new Date().toLocaleDateString('ko-KR', { year: 'numeric', month: 'long', day: 'numeric' })}
        </div>
      </div>

      {/* 통계 카드 */}
      <div className="fade-in delay-1" style={{ display: 'grid', gridTemplateColumns: 'repeat(4, 1fr)', gap: 16, marginBottom: 28 }}>
        {stats.map((s, i) => (
          <div key={i} className="lab-card" style={{ padding: '20px 22px' }}>
            <div style={{ fontFamily: 'var(--font-mono)', fontSize: 9, letterSpacing: '0.12em', color: 'var(--dim)', textTransform: 'uppercase', marginBottom: 12 }}>
              {s.label}
            </div>
            <div style={{ display: 'flex', alignItems: 'baseline', gap: 4, marginBottom: 8 }}>
              <span style={{ fontFamily: 'var(--font-mono)', fontSize: 32, fontWeight: 600, color: s.color, lineHeight: 1 }}>
                {s.value}
              </span>
              <span style={{ fontFamily: 'var(--font-mono)', fontSize: 13, color: 'var(--dim)' }}>{s.unit}</span>
            </div>
            <div style={{ fontFamily: 'var(--font-mono)', fontSize: 10, color: 'var(--text-2)' }}>
              {s.delta}
            </div>
          </div>
        ))}
      </div>

      {/* 최근 처방 + 시스템 로그 */}
      <div className="fade-in delay-2" style={{ display: 'grid', gridTemplateColumns: '1fr 340px', gap: 20, marginBottom: 28 }}>

        {/* 최근 처방 목록 */}
        <div className="lab-card" style={{ overflow: 'hidden' }}>
          <div style={{
            padding: '14px 20px', borderBottom: '1px solid var(--border)',
            display: 'flex', alignItems: 'center', justifyContent: 'space-between',
          }}>
            <div>
              <div style={{ fontFamily: 'var(--font-mono)', fontSize: 10, letterSpacing: '0.1em', color: 'var(--dim)', textTransform: 'uppercase' }}>Recent Prescriptions</div>
              <div style={{ fontSize: 13, fontWeight: 600, color: 'var(--text)', marginTop: 2 }}>최근 처방 기록</div>
            </div>
            <button className="btn-lab" style={{ fontSize: 10 }}>전체 보기 →</button>
          </div>
          <table className="lab-table">
            <thead>
              <tr>
                <th>ID</th>
                <th>처방명</th>
                <th>카테고리</th>
                <th>pH</th>
                <th>정확도</th>
                <th>상태</th>
              </tr>
            </thead>
            <tbody>
              {recentFormulations.map(f => (
                <tr key={f.id} style={{ cursor: 'pointer' }}>
                  <td className="mono" style={{ color: 'var(--accent)', fontSize: 11 }}>{f.id}</td>
                  <td style={{ fontWeight: 500, maxWidth: 200 }}>
                    <div style={{ fontSize: 12, overflow: 'hidden', textOverflow: 'ellipsis', whiteSpace: 'nowrap' }}>{f.name}</div>
                    <div style={{ fontFamily: 'var(--font-mono)', fontSize: 9, color: 'var(--dim)', marginTop: 1 }}>{f.ingredients}개 성분 · {f.date}</div>
                  </td>
                  <td><span className="tag tag-teal">{f.category}</span></td>
                  <td className="mono" style={{ fontSize: 12, color: 'var(--text-2)' }}>{f.ph}</td>
                  <td>
                    <div style={{ display: 'flex', alignItems: 'center', gap: 8 }}>
                      <div style={{ width: 48, height: 4, background: 'var(--bg)', borderRadius: 2, overflow: 'hidden' }}>
                        <div style={{
                          width: `${f.accuracy}%`, height: '100%',
                          background: f.accuracy >= 95 ? 'var(--green)' : f.accuracy >= 90 ? 'var(--amber)' : 'var(--red)',
                          borderRadius: 2,
                        }} />
                      </div>
                      <span style={{ fontFamily: 'var(--font-mono)', fontSize: 11, color: 'var(--text-2)' }}>{f.accuracy}%</span>
                    </div>
                  </td>
                  <td>
                    <span className={`tag ${f.status === 'complete' ? 'tag-green' : f.status === 'review' ? 'tag-blue' : 'tag-amber'}`}>
                      {f.status === 'complete' ? 'Done' : f.status === 'review' ? 'Review' : 'Warn'}
                    </span>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>

        {/* 시스템 로그 */}
        <div className="lab-card" style={{ display: 'flex', flexDirection: 'column' }}>
          <div style={{ padding: '14px 18px', borderBottom: '1px solid var(--border)' }}>
            <div style={{ fontFamily: 'var(--font-mono)', fontSize: 10, letterSpacing: '0.1em', color: 'var(--dim)', textTransform: 'uppercase' }}>System Log</div>
            <div style={{ fontSize: 13, fontWeight: 600, color: 'var(--text)', marginTop: 2 }}>실시간 로그</div>
          </div>
          <div style={{ flex: 1, padding: '12px 0', overflowY: 'auto' }}>
            {systemLog.map((log, i) => (
              <div key={i} style={{
                padding: '10px 18px',
                borderBottom: '1px solid var(--border)',
                borderLeft: `3px solid ${log.type === 'success' ? 'var(--green)' : log.type === 'warn' ? 'var(--amber)' : 'var(--accent-mid)'}`,
              }}>
                <div style={{ fontFamily: 'var(--font-mono)', fontSize: 9, color: 'var(--dim)', marginBottom: 4 }}>{log.time}</div>
                <div style={{ fontSize: 11, color: 'var(--text-2)', lineHeight: 1.5 }}>{log.msg}</div>
              </div>
            ))}
          </div>
        </div>
      </div>

      {/* 카테고리 커버리지 */}
      <div className="fade-in delay-3" style={{ display: 'grid', gridTemplateColumns: 'repeat(3, 1fr)', gap: 16 }}>
        {[
          { group: '기초/세정/바디', count: 16, coverage: '16/16', color: 'var(--accent)' },
          { group: '헤어/선/색조', count: 14, coverage: '14/14', color: 'var(--teal)' },
          { group: '생활용품', count: 13, coverage: '13/13', color: 'var(--purple)' },
        ].map((cat, i) => (
          <div key={i} className="lab-card" style={{ padding: '18px 20px' }}>
            <div style={{ fontFamily: 'var(--font-mono)', fontSize: 9, letterSpacing: '0.1em', color: 'var(--dim)', textTransform: 'uppercase', marginBottom: 8 }}>
              Category Coverage
            </div>
            <div style={{ fontSize: 14, fontWeight: 600, color: 'var(--text)', marginBottom: 12 }}>{cat.group}</div>
            <div style={{ display: 'flex', alignItems: 'center', gap: 12 }}>
              <div style={{ flex: 1, height: 6, background: 'var(--bg)', borderRadius: 1, overflow: 'hidden' }}>
                <div style={{ width: '100%', height: '100%', background: cat.color, borderRadius: 1 }} />
              </div>
              <span style={{ fontFamily: 'var(--font-mono)', fontSize: 11, color: cat.color, whiteSpace: 'nowrap' }}>{cat.coverage}</span>
            </div>
          </div>
        ))}
      </div>

    </div>
  )
}
