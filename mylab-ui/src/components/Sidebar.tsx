import { useState } from 'react'

type Page = 'dashboard' | 'formulation' | 'ingredients' | 'notes' | 'validation' | 'regulation'

interface SidebarProps {
  activePage: Page
  onNavigate: (page: Page) => void
}

const nav = [
  {
    group: 'LAB NOTEBOOK',
    items: [
      { id: 'dashboard',    icon: '⬡', label: 'Overview',       sub: '연구 현황' },
      { id: 'notes',        icon: '◎', label: 'Research Notes',  sub: '실험 노트' },
    ]
  },
  {
    group: 'FORMULATION',
    items: [
      { id: 'formulation',  icon: '⬡', label: 'Prescriptions',   sub: '처방 생성' },
      { id: 'ingredients',  icon: '◈', label: 'Ingredients',      sub: '성분 DB' },
      { id: 'validation',   icon: '◉', label: 'QA Validation',    sub: '품질 검증' },
    ]
  },
  {
    group: 'COMPLIANCE',
    items: [
      { id: 'regulation',   icon: '◇', label: 'Regulation',       sub: '규제 현황' },
    ]
  },
]

export default function Sidebar({ activePage, onNavigate }: SidebarProps) {
  return (
    <aside style={{
      width: 220,
      minWidth: 220,
      background: 'var(--sidebar)',
      borderRight: '1px solid var(--border)',
      display: 'flex',
      flexDirection: 'column',
      height: '100vh',
      position: 'sticky',
      top: 0,
    }}>
      {/* 로고 */}
      <div style={{ padding: '24px 20px 20px', borderBottom: '1px solid var(--border)' }}>
        <div style={{ display: 'flex', alignItems: 'center', gap: 10 }}>
          <div style={{
            width: 28, height: 28,
            background: 'var(--accent)',
            borderRadius: 2,
            display: 'flex', alignItems: 'center', justifyContent: 'center',
          }}>
            <span style={{ color: '#fff', fontSize: 13, fontFamily: 'var(--font-mono)' }}>C</span>
          </div>
          <div>
            <div style={{ fontFamily: 'var(--font-display)', fontSize: 15, fontWeight: 700, color: 'var(--text)', lineHeight: 1 }}>
              COCHING
            </div>
            <div style={{ fontFamily: 'var(--font-mono)', fontSize: 9, letterSpacing: '0.12em', color: 'var(--dim)', marginTop: 2 }}>
              AI · MY LAB v2.3
            </div>
          </div>
        </div>
      </div>

      {/* 연구원 프로필 */}
      <div style={{ padding: '14px 20px', borderBottom: '1px solid var(--border)', background: 'var(--accent-lt)' }}>
        <div style={{ fontFamily: 'var(--font-mono)', fontSize: 9, letterSpacing: '0.1em', color: 'var(--accent)', marginBottom: 6, textTransform: 'uppercase' }}>
          Researcher
        </div>
        <div style={{ fontSize: 13, fontWeight: 600, color: 'var(--text)' }}>Daniel Lab</div>
        <div style={{ fontFamily: 'var(--font-mono)', fontSize: 10, color: 'var(--dim)', marginTop: 2 }}>
          Cosmetic Formulator
        </div>
        <div style={{ display: 'flex', alignItems: 'center', gap: 6, marginTop: 8 }}>
          <span className="status-dot active" />
          <span style={{ fontFamily: 'var(--font-mono)', fontSize: 10, color: 'var(--green)' }}>System Online</span>
        </div>
      </div>

      {/* 네비게이션 */}
      <nav style={{ flex: 1, padding: '12px 0', overflowY: 'auto' }}>
        {nav.map(section => (
          <div key={section.group} style={{ marginBottom: 4 }}>
            <div style={{
              fontFamily: 'var(--font-mono)', fontSize: 9, letterSpacing: '0.12em',
              color: 'var(--dim2)', padding: '8px 20px 4px', textTransform: 'uppercase'
            }}>
              {section.group}
            </div>
            {section.items.map(item => {
              const active = activePage === item.id
              return (
                <button
                  key={item.id}
                  onClick={() => onNavigate(item.id as Page)}
                  style={{
                    width: '100%', display: 'flex', alignItems: 'center', gap: 10,
                    padding: '9px 20px', border: 'none', cursor: 'pointer',
                    background: active ? 'var(--accent-lt)' : 'transparent',
                    borderLeft: active ? '2px solid var(--accent)' : '2px solid transparent',
                    transition: 'all 0.15s', textAlign: 'left',
                  }}
                  onMouseEnter={e => { if (!active) e.currentTarget.style.background = 'var(--bg)' }}
                  onMouseLeave={e => { if (!active) e.currentTarget.style.background = 'transparent' }}
                >
                  <span style={{ fontSize: 12, color: active ? 'var(--accent)' : 'var(--dim)', lineHeight: 1 }}>
                    {item.icon}
                  </span>
                  <div>
                    <div style={{
                      fontSize: 12, fontWeight: active ? 600 : 400,
                      color: active ? 'var(--accent)' : 'var(--text)',
                      lineHeight: 1.2,
                    }}>
                      {item.label}
                    </div>
                    <div style={{ fontFamily: 'var(--font-mono)', fontSize: 9, color: 'var(--dim)', marginTop: 1 }}>
                      {item.sub}
                    </div>
                  </div>
                </button>
              )
            })}
          </div>
        ))}
      </nav>

      {/* 하단 시스템 상태 */}
      <div style={{ padding: '12px 20px', borderTop: '1px solid var(--border)' }}>
        <div style={{ fontFamily: 'var(--font-mono)', fontSize: 9, color: 'var(--dim)', letterSpacing: '0.08em', marginBottom: 8, textTransform: 'uppercase' }}>
          System Status
        </div>
        {[
          { label: 'Claude API', status: 'active' },
          { label: 'Gemini 2.5', status: 'active' },
          { label: 'Memory DB', status: 'active' },
        ].map(s => (
          <div key={s.label} style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', marginBottom: 5 }}>
            <span style={{ fontFamily: 'var(--font-mono)', fontSize: 10, color: 'var(--text-2)' }}>{s.label}</span>
            <div style={{ display: 'flex', alignItems: 'center', gap: 4 }}>
              <span className={`status-dot ${s.status}`} style={{ width: 5, height: 5 }} />
              <span style={{ fontFamily: 'var(--font-mono)', fontSize: 9, color: 'var(--green)' }}>OK</span>
            </div>
          </div>
        ))}
      </div>
    </aside>
  )
}
