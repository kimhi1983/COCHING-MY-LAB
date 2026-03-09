import { useState } from 'react'

const notes = [
  {
    id: 'N-089',
    date: '2026.03.08',
    title: '그린티 씨드 오일 — 신규 성분 데이터 확보',
    tag: 'ingredient',
    content: [
      'Camellia Sinensis Seed Oil (동백씨오일) Gemini 리서치 결과',
      'pKa: not_found / recommended pH: 5.0~7.0',
      'HLB: 7 (O/W 적용 확인) / INCI 등록: KR 완료',
      'Memory DB 저장 완료 — 다음 처방부터 자동 적용',
    ],
    color: 'var(--accent)',
    status: 'complete',
  },
  {
    id: 'N-088',
    date: '2026.03.07',
    title: 'ZPT 규제 이슈 — EU 대체 성분 검토',
    tag: 'regulation',
    content: [
      'Zinc Pyrithione (ZPT) 규제 현황 업데이트',
      'KR: 비듬방지 1~2% 허용 / EU: 2022년~ 금지 (린스오프)',
      'US: OTC Drug 1% 허용',
      '대체 권고: Piroctone Olamine (EU 허용) → 처방 반영',
    ],
    color: 'var(--amber)',
    status: 'review',
  },
  {
    id: 'N-087',
    date: '2026.03.06',
    title: 'Precision Arithmetic — 100.00% 정확도 검증',
    tag: 'system',
    content: [
      '정수 연산 원칙 적용 후 첫 처방 생성',
      '47개 성분 → 합계 100.00% (이전: 99.99% 오류)',
      '밸런스 역산 공식: 10000 - Σ(정수) = 정제수 정수값',
      '3단계 검증 모두 PASS — 처방서 출력 완료',
    ],
    color: 'var(--green)',
    status: 'complete',
  },
  {
    id: 'N-086',
    date: '2026.03.05',
    title: 'SPF50+ 처방 — 자외선 차단제 HLB 검토',
    tag: 'formulation',
    content: [
      '유기/무기 복합 자외선 차단 처방 R&D',
      'Titanium Dioxide + Zinc Oxide 물리적 필터 조합',
      'pH 주의: 자외선 차단제 특성상 pH 6.5 유지 필요',
      '점도 보정 필요 — 다음 배치 수정 예정',
    ],
    color: 'var(--teal)',
    status: 'wip',
  },
]

const tagLabels: Record<string, { label: string, cls: string }> = {
  ingredient:  { label: 'Ingredient',  cls: 'tag-blue' },
  regulation:  { label: 'Regulation',  cls: 'tag-amber' },
  system:      { label: 'System',      cls: 'tag-green' },
  formulation: { label: 'Formulation', cls: 'tag-teal' },
}

export default function ResearchNotes() {
  const [activeNote, setActiveNote] = useState(notes[0])
  const [editMode, setEditMode] = useState(false)
  const [newNote, setNewNote] = useState('')

  return (
    <div style={{ padding: '32px 36px', maxWidth: 1200 }}>

      {/* 헤더 */}
      <div className="fade-in" style={{ marginBottom: 28 }}>
        <div style={{ display: 'flex', alignItems: 'baseline', gap: 12, marginBottom: 4 }}>
          <h1 className="font-display" style={{ fontSize: 26, fontWeight: 700, color: 'var(--text)' }}>
            Research Notes
          </h1>
          <span style={{ fontFamily: 'var(--font-mono)', fontSize: 11, color: 'var(--dim)', letterSpacing: '0.08em' }}>
            — 실험 노트
          </span>
        </div>
        <div style={{ fontFamily: 'var(--font-mono)', fontSize: 11, color: 'var(--dim)' }}>
          연구원의 관찰 기록 · 처방 메모 · 성분 노트
        </div>
      </div>

      <div className="fade-in delay-1" style={{ display: 'grid', gridTemplateColumns: '280px 1fr', gap: 20 }}>

        {/* 노트 목록 */}
        <div>
          {/* 새 노트 버튼 */}
          <button
            className="btn-primary"
            style={{ width: '100%', justifyContent: 'center', marginBottom: 16 }}
            onClick={() => setEditMode(true)}
          >
            + 새 노트 작성
          </button>

          {/* 노트 리스트 */}
          <div style={{ display: 'flex', flexDirection: 'column', gap: 8 }}>
            {notes.map(note => (
              <div
                key={note.id}
                className="lab-card"
                onClick={() => setActiveNote(note)}
                style={{
                  padding: '14px 16px', cursor: 'pointer',
                  borderLeft: `3px solid ${note.id === activeNote.id ? note.color : 'transparent'}`,
                  background: note.id === activeNote.id ? 'var(--accent-lt)' : 'var(--card)',
                  transition: 'all 0.15s',
                }}
              >
                <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', marginBottom: 6 }}>
                  <span style={{ fontFamily: 'var(--font-mono)', fontSize: 10, color: 'var(--dim)' }}>{note.id}</span>
                  <span className={`tag ${tagLabels[note.tag].cls}`} style={{ fontSize: 9 }}>
                    {tagLabels[note.tag].label}
                  </span>
                </div>
                <div style={{ fontSize: 12, fontWeight: 600, color: 'var(--text)', lineHeight: 1.4, marginBottom: 4 }}>
                  {note.title}
                </div>
                <div style={{ fontFamily: 'var(--font-mono)', fontSize: 10, color: 'var(--dim)' }}>{note.date}</div>
              </div>
            ))}
          </div>
        </div>

        {/* 노트 상세 뷰 */}
        <div className="lab-card" style={{ overflow: 'hidden' }}>

          {/* 노트 헤더 */}
          <div style={{ padding: '20px 28px', borderBottom: '1px solid var(--border)', display: 'flex', alignItems: 'flex-start', justifyContent: 'space-between' }}>
            <div>
              <div style={{ display: 'flex', alignItems: 'center', gap: 10, marginBottom: 8 }}>
                <span style={{ fontFamily: 'var(--font-mono)', fontSize: 10, color: 'var(--dim)' }}>{activeNote.date}</span>
                <span className={`tag ${tagLabels[activeNote.tag].cls}`}>{tagLabels[activeNote.tag].label}</span>
                <span style={{ fontFamily: 'var(--font-mono)', fontSize: 10, color: 'var(--dim)' }}>{activeNote.id}</span>
              </div>
              <h2 className="font-display" style={{ fontSize: 22, fontWeight: 700, color: 'var(--text)', lineHeight: 1.2 }}>
                {activeNote.title}
              </h2>
            </div>
            <div style={{ display: 'flex', gap: 8 }}>
              <button className="btn-lab" onClick={() => setEditMode(!editMode)}>
                {editMode ? '저장' : '편집'}
              </button>
              <button className="btn-lab">처방 연결</button>
            </div>
          </div>

          {/* 노트 내용 — 연구노트 줄 배경 */}
          <div
            className="note-lines"
            style={{ padding: '24px 28px', minHeight: 320, position: 'relative' }}
          >
            {/* 마진 선 (연구노트 빨간 세로선) */}
            <div style={{
              position: 'absolute', left: 64, top: 0, bottom: 0,
              borderLeft: '1px solid rgba(200,30,30,0.15)',
            }} />

            <div style={{ paddingLeft: 48 }}>
              {activeNote.content.map((line, i) => (
                <div
                  key={i}
                  style={{
                    fontFamily: 'var(--font-mono)', fontSize: 13, color: 'var(--text)',
                    lineHeight: '32px', minHeight: 32,
                    display: 'flex', alignItems: 'center', gap: 12,
                  }}
                >
                  <span style={{ color: 'var(--dim)', fontSize: 10, width: 16, textAlign: 'right', flexShrink: 0 }}>
                    {String(i + 1).padStart(2, '0')}
                  </span>
                  <span style={{ color: i === 0 ? activeNote.color : 'var(--text)', fontWeight: i === 0 ? 600 : 400 }}>
                    {line}
                  </span>
                </div>
              ))}

              {/* 새 줄 입력 */}
              {editMode && (
                <div style={{ display: 'flex', alignItems: 'center', gap: 12, marginTop: 4 }}>
                  <span style={{ color: 'var(--dim)', fontSize: 10, width: 16, textAlign: 'right', flexShrink: 0, fontFamily: 'var(--font-mono)' }}>
                    {String(activeNote.content.length + 1).padStart(2, '0')}
                  </span>
                  <input
                    className="lab-input cursor-blink"
                    style={{ background: 'transparent', border: 'none', borderBottom: '1px solid var(--border2)', borderRadius: 0, fontFamily: 'var(--font-mono)', fontSize: 13, lineHeight: '32px', padding: '0 4px' }}
                    placeholder="새 메모 입력..."
                    value={newNote}
                    onChange={e => setNewNote(e.target.value)}
                    autoFocus
                  />
                </div>
              )}
            </div>
          </div>

          {/* 연결된 처방 */}
          <div style={{ padding: '14px 28px', borderTop: '1px solid var(--border)', background: 'var(--card-alt)', display: 'flex', alignItems: 'center', gap: 16 }}>
            <span style={{ fontFamily: 'var(--font-mono)', fontSize: 9, color: 'var(--dim)', textTransform: 'uppercase', letterSpacing: '0.08em' }}>
              Linked Prescriptions
            </span>
            <span className="tag tag-blue">PRX-2024-089</span>
            <span className="tag tag-gray">+ 연결 추가</span>
          </div>
        </div>
      </div>
    </div>
  )
}
