import { useState } from 'react'
import Sidebar from '../components/Sidebar'
import Dashboard from '../components/Dashboard'
import Formulation from '../components/Formulation'
import ResearchNotes from '../components/ResearchNotes'
import Regulation from '../components/Regulation'

type Page = 'dashboard' | 'formulation' | 'ingredients' | 'notes' | 'validation' | 'regulation'

export default function Home() {
  const [page, setPage] = useState<Page>('dashboard')

  const renderPage = () => {
    switch (page) {
      case 'dashboard':    return <Dashboard />
      case 'formulation':  return <Formulation />
      case 'notes':        return <ResearchNotes />
      case 'regulation':   return <Regulation />
      default: return (
        <div style={{ padding: '60px 36px', textAlign: 'center' }}>
          <div className="font-display" style={{ fontSize: 22, color: 'var(--dim)', marginBottom: 12 }}>Coming Soon</div>
          <div style={{ fontFamily: 'var(--font-mono)', fontSize: 12, color: 'var(--dim2)' }}>이 페이지는 준비 중입니다</div>
        </div>
      )
    }
  }

  return (
    <div style={{ display: 'flex', minHeight: '100vh', background: 'var(--bg)' }}>

      {/* 사이드바 */}
      <Sidebar activePage={page} onNavigate={setPage} />

      {/* 메인 콘텐츠 */}
      <main style={{ flex: 1, overflowY: 'auto', overflowX: 'hidden' }}>

        {/* 상단 툴바 */}
        <div style={{
          position: 'sticky', top: 0, zIndex: 10,
          background: 'rgba(244,246,249,0.92)',
          backdropFilter: 'blur(8px)',
          borderBottom: '1px solid var(--border)',
          padding: '10px 36px',
          display: 'flex', alignItems: 'center', justifyContent: 'space-between',
        }}>
          <div style={{ display: 'flex', alignItems: 'center', gap: 6 }}>
            <span style={{ fontFamily: 'var(--font-mono)', fontSize: 11, color: 'var(--dim)' }}>
              Daniel Lab
            </span>
            <span style={{ color: 'var(--dim2)' }}>/</span>
            <span style={{ fontFamily: 'var(--font-mono)', fontSize: 11, color: 'var(--text-2)', textTransform: 'capitalize' }}>
              {page}
            </span>
          </div>
          <div style={{ display: 'flex', alignItems: 'center', gap: 16 }}>
            <div style={{ fontFamily: 'var(--font-mono)', fontSize: 10, color: 'var(--dim)', letterSpacing: '0.06em' }}>
              {new Date().toLocaleDateString('ko-KR')} · {new Date().toLocaleTimeString('ko-KR', { hour: '2-digit', minute: '2-digit' })}
            </div>
            <div style={{ display: 'flex', alignItems: 'center', gap: 6 }}>
              <span className="status-dot active" style={{ width: 5, height: 5 }} />
              <span style={{ fontFamily: 'var(--font-mono)', fontSize: 10, color: 'var(--green)' }}>COCHING AI v2.3</span>
            </div>
          </div>
        </div>

        {/* 페이지 렌더링 */}
        {renderPage()}
      </main>
    </div>
  )
}
