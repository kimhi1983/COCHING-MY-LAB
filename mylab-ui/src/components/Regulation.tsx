const regulationData = [
  { ingredient: 'Zinc Pyrithione (ZPT)', inci: 'Zinc Pyrithione', kr: '1~2% 허용', eu: '금지 (2022~)', us: 'OTC 1%', status: 'warn', note: 'EU 수출 불가 → Piroctone Olamine 대체 권고' },
  { ingredient: '레티놀', inci: 'Retinol', kr: '2,500 IU/g 이하', eu: '0.3% (일반), 0.05% (바디)', us: '제한 없음', status: 'warn', note: 'EU 강화 규정 2025년 적용' },
  { ingredient: '이산화티타늄 (나노)', inci: 'Titanium Dioxide (nano)', kr: '25% 이하', eu: 'Lung 흡입 경고', us: '25% 이하', status: 'info', note: 'EU 흡입 노출 제품 라벨 필수' },
  { ingredient: '하이드로퀴논', inci: 'Hydroquinone', kr: '2% (의약외품)', eu: '금지', us: 'OTC 1.5~2%', status: 'danger', note: 'EU 전면 금지 성분' },
  { ingredient: 'Phenoxyethanol', inci: 'Phenoxyethanol', kr: '1.0% 이하', eu: '1.0% 이하', us: '제한 없음', status: 'ok', note: '표준 방부제, 농도 관리 필수' },
  { ingredient: 'Niacinamide', inci: 'Niacinamide', kr: '제한 없음', eu: '제한 없음', us: '제한 없음', status: 'ok', note: '안전 성분' },
]

export default function Regulation() {
  return (
    <div style={{ padding: '32px 36px', maxWidth: 1200 }}>

      <div className="fade-in" style={{ marginBottom: 28 }}>
        <div style={{ display: 'flex', alignItems: 'baseline', gap: 12, marginBottom: 4 }}>
          <h1 className="font-display" style={{ fontSize: 26, fontWeight: 700, color: 'var(--text)' }}>
            Regulation Monitor
          </h1>
          <span style={{ fontFamily: 'var(--font-mono)', fontSize: 11, color: 'var(--dim)', letterSpacing: '0.08em' }}>
            — 규제 현황
          </span>
        </div>
        <div style={{ fontFamily: 'var(--font-mono)', fontSize: 11, color: 'var(--dim)' }}>
          KR 식약처 · EU SCCS · US FDA · Gemini 실시간 리서치
        </div>
      </div>

      {/* 요약 배너 */}
      <div className="fade-in delay-1" style={{ display: 'grid', gridTemplateColumns: 'repeat(3, 1fr)', gap: 14, marginBottom: 24 }}>
        {[
          { market: 'KR', flag: '🇰🇷', org: '식품의약품안전처', issues: 1, color: 'var(--accent)' },
          { market: 'EU', flag: '🇪🇺', org: 'SCCS / ECHA',      issues: 3, color: 'var(--amber)' },
          { market: 'US', flag: '🇺🇸', org: 'FDA OTC',          issues: 1, color: 'var(--teal)' },
        ].map(m => (
          <div key={m.market} className="lab-card" style={{ padding: '16px 20px' }}>
            <div style={{ display: 'flex', alignItems: 'center', gap: 10, marginBottom: 10 }}>
              <span style={{ fontSize: 18 }}>{m.flag}</span>
              <div>
                <div style={{ fontFamily: 'var(--font-mono)', fontSize: 11, fontWeight: 600, color: m.color, letterSpacing: '0.06em' }}>{m.market}</div>
                <div style={{ fontFamily: 'var(--font-mono)', fontSize: 9, color: 'var(--dim)' }}>{m.org}</div>
              </div>
            </div>
            <div style={{ display: 'flex', alignItems: 'baseline', gap: 4 }}>
              <span style={{ fontFamily: 'var(--font-mono)', fontSize: 24, fontWeight: 700, color: m.issues > 1 ? 'var(--amber)' : 'var(--text)' }}>{m.issues}</span>
              <span style={{ fontFamily: 'var(--font-mono)', fontSize: 11, color: 'var(--dim)' }}>issues</span>
            </div>
          </div>
        ))}
      </div>

      {/* 규제 테이블 */}
      <div className="fade-in delay-2 lab-card" style={{ overflow: 'hidden' }}>
        <div style={{ padding: '14px 20px', borderBottom: '1px solid var(--border)', display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
          <div style={{ fontFamily: 'var(--font-mono)', fontSize: 10, letterSpacing: '0.1em', color: 'var(--dim)', textTransform: 'uppercase' }}>
            Ingredient Regulation Status
          </div>
          <button className="btn-lab">Gemini로 갱신</button>
        </div>
        <table className="lab-table">
          <thead>
            <tr>
              <th>성분명</th>
              <th>🇰🇷 KR</th>
              <th>🇪🇺 EU</th>
              <th>🇺🇸 US</th>
              <th>상태</th>
              <th>비고</th>
            </tr>
          </thead>
          <tbody>
            {regulationData.map((r, i) => (
              <tr key={i}>
                <td>
                  <div style={{ fontWeight: 600, fontSize: 12 }}>{r.ingredient}</div>
                  <div style={{ fontFamily: 'var(--font-mono)', fontSize: 10, color: 'var(--dim)', marginTop: 2 }}>{r.inci}</div>
                </td>
                <td style={{ fontFamily: 'var(--font-mono)', fontSize: 11 }}>{r.kr}</td>
                <td style={{ fontFamily: 'var(--font-mono)', fontSize: 11, color: r.eu.includes('금지') ? 'var(--red)' : 'var(--text)' }}>{r.eu}</td>
                <td style={{ fontFamily: 'var(--font-mono)', fontSize: 11 }}>{r.us}</td>
                <td>
                  <span className={`tag ${r.status === 'ok' ? 'tag-green' : r.status === 'warn' ? 'tag-amber' : r.status === 'danger' ? 'tag-red' : 'tag-blue'}`}>
                    {r.status === 'ok' ? '정상' : r.status === 'warn' ? '주의' : r.status === 'danger' ? '금지' : '정보'}
                  </span>
                </td>
                <td style={{ fontSize: 11, color: 'var(--text-2)', fontStyle: r.status !== 'ok' ? 'italic' : 'normal' }}>{r.note}</td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  )
}
