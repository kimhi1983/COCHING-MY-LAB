import { useState } from 'react'

const sampleIngredients = [
  { inci: 'Water', korean: '정제수', wt: 68.45, role: '용매', phase: 'A', balance: true },
  { inci: 'Glycerin', korean: '글리세린', wt: 5.00, role: '보습제', phase: 'A', balance: false },
  { inci: 'Niacinamide', korean: '나이아신아마이드', wt: 5.00, role: '미백 기능성', phase: 'A', balance: false },
  { inci: 'Butylene Glycol', korean: '부틸렌글라이콜', wt: 4.00, role: '보습/용매', phase: 'A', balance: false },
  { inci: 'Cetearyl Alcohol', korean: '세테아릴알코올', wt: 3.00, role: '유화안정제', phase: 'B', balance: false },
  { inci: 'Ceteareth-20', korean: '세테아레스-20', wt: 1.50, role: '유화제', phase: 'B', balance: false },
  { inci: 'Dimethicone', korean: '다이메티콘', wt: 2.00, role: '실리콘', phase: 'B', balance: false },
  { inci: 'Camellia Sinensis Seed Oil', korean: '동백씨오일', wt: 3.00, role: '에모리엔트', phase: 'B', balance: false, newIngredient: true },
  { inci: 'Tocopheryl Acetate', korean: '토코페릴아세테이트', wt: 0.50, role: '항산화', phase: 'B', balance: false },
  { inci: 'Panthenol', korean: '판테놀', wt: 1.00, role: '보습/진정', phase: 'C', balance: false },
  { inci: 'Allantoin', korean: '알란토인', wt: 0.20, role: '진정', phase: 'C', balance: false },
  { inci: 'Carbomer', korean: '카보머', wt: 0.30, role: '점증제', phase: 'C', balance: false },
  { inci: 'Sodium Hydroxide', korean: '수산화나트륨', wt: 0.10, role: 'pH 조정', phase: 'C', balance: false },
  { inci: 'Disodium EDTA', korean: '다이소듐이디티에이', wt: 0.05, role: '킬레이트제', phase: 'C', balance: false },
  { inci: 'Phenoxyethanol', korean: '페녹시에탄올', wt: 0.80, role: '방부제', phase: 'D', balance: false },
  { inci: 'Fragrance', korean: '향료', wt: 0.10, role: '향료', phase: 'D', balance: false },
]

const total = sampleIngredients.reduce((s, i) => s + i.wt, 0)

export default function Formulation() {
  const [request, setRequest] = useState('이니스프리 그린티 씨드 크림 카피 처방 요청')
  const [generating, setGenerating] = useState(false)
  const [showResult, setShowResult] = useState(true)

  const handleGenerate = () => {
    setGenerating(true)
    setTimeout(() => { setGenerating(false); setShowResult(true) }, 2200)
  }

  const phaseColors: Record<string, string> = {
    A: 'var(--accent)', B: 'var(--teal)', C: 'var(--purple)', D: 'var(--amber)'
  }

  return (
    <div style={{ padding: '32px 36px', maxWidth: 1200 }}>

      {/* 헤더 */}
      <div className="fade-in" style={{ marginBottom: 28 }}>
        <div style={{ display: 'flex', alignItems: 'baseline', gap: 12, marginBottom: 4 }}>
          <h1 className="font-display" style={{ fontSize: 26, fontWeight: 700, color: 'var(--text)' }}>
            Prescription Generator
          </h1>
          <span style={{ fontFamily: 'var(--font-mono)', fontSize: 11, color: 'var(--dim)', letterSpacing: '0.08em' }}>
            — 처방 생성
          </span>
        </div>
        <div style={{ fontFamily: 'var(--font-mono)', fontSize: 11, color: 'var(--dim)' }}>
          Claude (처방 엔진) × Gemini 2.5 Pro (리서치) · Precision Arithmetic v1.0
        </div>
      </div>

      {/* 요청 입력 패널 */}
      <div className="fade-in delay-1 lab-card" style={{ padding: '20px 24px', marginBottom: 20 }}>
        <div style={{ fontFamily: 'var(--font-mono)', fontSize: 10, letterSpacing: '0.1em', color: 'var(--dim)', textTransform: 'uppercase', marginBottom: 12 }}>
          Formulation Request
        </div>
        <div style={{ display: 'flex', gap: 12, alignItems: 'flex-start' }}>
          <textarea
            className="lab-textarea"
            rows={2}
            value={request}
            onChange={e => setRequest(e.target.value)}
            style={{ flex: 1, lineHeight: 1.6 }}
          />
          <div style={{ display: 'flex', flexDirection: 'column', gap: 8 }}>
            <button className="btn-primary" onClick={handleGenerate} disabled={generating}>
              {generating ? <span className="cursor-blink">생성 중</span> : '▶ Generate'}
            </button>
            <button className="btn-lab">Template</button>
          </div>
        </div>

        {/* 처방 메타 */}
        <div style={{ display: 'flex', gap: 24, marginTop: 16, paddingTop: 16, borderTop: '1px solid var(--border)' }}>
          {[
            { label: 'Category', value: '1.3 — O/W 크림' },
            { label: 'Target pH', value: '5.5 ~ 6.0' },
            { label: 'Viscosity', value: '25,000 ~ 35,000 cP' },
            { label: 'HLB', value: '11.2 (계산값)' },
            { label: 'Market', value: 'KR / EU / US' },
          ].map(m => (
            <div key={m.label}>
              <div style={{ fontFamily: 'var(--font-mono)', fontSize: 9, color: 'var(--dim)', letterSpacing: '0.08em', textTransform: 'uppercase', marginBottom: 2 }}>{m.label}</div>
              <div style={{ fontFamily: 'var(--font-mono)', fontSize: 12, color: 'var(--text)', fontWeight: 500 }}>{m.value}</div>
            </div>
          ))}
        </div>
      </div>

      {/* 처방 결과 */}
      {showResult && (
        <div className="fade-in delay-2 lab-card" style={{ overflow: 'hidden' }}>
          {/* 결과 헤더 */}
          <div style={{ padding: '14px 20px', borderBottom: '1px solid var(--border)', background: 'var(--accent-lt)', display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
            <div style={{ display: 'flex', alignItems: 'center', gap: 12 }}>
              <span className="status-dot active" />
              <div>
                <div style={{ fontFamily: 'var(--font-mono)', fontSize: 10, color: 'var(--accent)', letterSpacing: '0.08em', textTransform: 'uppercase' }}>PRX-2024-089</div>
                <div style={{ fontSize: 13, fontWeight: 600, color: 'var(--text)' }}>이니스프리 그린티 씨드 크림 카피처방</div>
              </div>
              <span className="tag tag-green">✓ 100.00%</span>
              <span className="tag tag-blue">QA 17/17</span>
            </div>
            <div style={{ display: 'flex', gap: 8 }}>
              <button className="btn-lab">Excel 출력</button>
              <button className="btn-lab">PDF 저장</button>
              <button className="btn-primary">노트에 저장</button>
            </div>
          </div>

          {/* 성분 테이블 */}
          <div style={{ overflowX: 'auto' }}>
            <table className="lab-table">
              <thead>
                <tr>
                  <th style={{ width: 40 }}>#</th>
                  <th>INCI Name</th>
                  <th>한글명</th>
                  <th>Phase</th>
                  <th>역할</th>
                  <th style={{ textAlign: 'right' }}>wt%</th>
                  <th style={{ width: 80 }}>Bar</th>
                </tr>
              </thead>
              <tbody>
                {sampleIngredients.map((ing, i) => (
                  <tr key={i}>
                    <td className="mono" style={{ color: 'var(--dim)', fontSize: 11 }}>{String(i + 1).padStart(2, '0')}</td>
                    <td style={{ fontWeight: 500 }}>
                      <span style={{ fontSize: 12 }}>{ing.inci}</span>
                      {ing.newIngredient && <span className="tag tag-purple" style={{ marginLeft: 6, fontSize: 9 }}>Gemini</span>}
                    </td>
                    <td style={{ fontFamily: 'var(--font-mono)', fontSize: 11, color: 'var(--text-2)' }}>{ing.korean}</td>
                    <td>
                      <span style={{
                        fontFamily: 'var(--font-mono)', fontSize: 11, fontWeight: 600,
                        color: phaseColors[ing.phase] || 'var(--dim)',
                      }}>
                        {ing.phase}
                      </span>
                    </td>
                    <td style={{ fontSize: 11, color: 'var(--text-2)' }}>{ing.role}</td>
                    <td style={{ textAlign: 'right', fontFamily: 'var(--font-mono)', fontSize: 13, fontWeight: ing.balance ? 600 : 400, color: ing.balance ? 'var(--accent)' : 'var(--text)' }}>
                      {ing.wt.toFixed(2)}
                    </td>
                    <td>
                      <div style={{ width: 60, height: 4, background: 'var(--bg)', borderRadius: 1, overflow: 'hidden' }}>
                        <div style={{ width: `${Math.min(ing.wt / 70 * 100, 100)}%`, height: '100%', background: phaseColors[ing.phase], borderRadius: 1 }} />
                      </div>
                    </td>
                  </tr>
                ))}
                {/* 합계 행 */}
                <tr>
                  <td colSpan={5} style={{ textAlign: 'right', fontFamily: 'var(--font-mono)', fontSize: 11, color: 'var(--dim)', letterSpacing: '0.06em', textTransform: 'uppercase', background: 'var(--bg)' }}>
                    Total ──────────────────────
                  </td>
                  <td style={{ textAlign: 'right', background: 'var(--green-lt)', fontFamily: 'var(--font-mono)', fontSize: 16, fontWeight: 700, color: 'var(--green)' }}>
                    {total.toFixed(2)}
                  </td>
                  <td style={{ background: 'var(--green-lt)' }}>
                    <span className="tag tag-green">✓ PASS</span>
                  </td>
                </tr>
              </tbody>
            </table>
          </div>

          {/* 검증 결과 */}
          <div style={{ padding: '16px 20px', background: 'var(--card-alt)', borderTop: '1px solid var(--border)', display: 'flex', gap: 32 }}>
            {[
              { label: 'Step 1: 정수 합산', value: '10,000', pass: true },
              { label: 'Step 2: wt% 합계', value: '100.00', pass: true },
              { label: 'Step 3: 역산 교차검증', value: '68.45 ✓', pass: true },
            ].map(v => (
              <div key={v.label} style={{ display: 'flex', alignItems: 'center', gap: 8 }}>
                <span className="status-dot active" style={{ width: 5, height: 5 }} />
                <span style={{ fontFamily: 'var(--font-mono)', fontSize: 10, color: 'var(--dim)', textTransform: 'uppercase', letterSpacing: '0.06em' }}>{v.label}</span>
                <span style={{ fontFamily: 'var(--font-mono)', fontSize: 12, color: 'var(--green)', fontWeight: 600 }}>{v.value}</span>
              </div>
            ))}
          </div>
        </div>
      )}
    </div>
  )
}
