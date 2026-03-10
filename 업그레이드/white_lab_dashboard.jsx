import { useState } from "react";

const C = {
  bg:         "#f8f7f5",
  surface:    "#ffffff",
  card:       "#ffffff",
  sidebar:    "#fafaf8",
  border:     "#ece9e3",
  borderMid:  "#d8d4cc",
  accent:     "#b8935a",
  accentLight:"#f0e8d8",
  accentDim:  "#e8dece",
  text:       "#1a1814",
  textSub:    "#6b6560",
  textDim:    "#aba59d",
  green:      "#3a9068",
  greenBg:    "#f0f8f4",
  greenBorder:"#b8dece",
  red:        "#c44e4e",
  redBg:      "#fdf2f2",
  redBorder:  "#e8c0c0",
  amber:      "#b07820",
  amberBg:    "#fdf8f0",
  amberBorder:"#e8d4a0",
  blue:       "#3a6fa8",
  blueBg:     "#f0f4fb",
  blueBorder: "#b8cce8",
  purple:     "#7c5cbf",
  purpleBg:   "#f6f2fd",
  purpleBorder:"#cfc0ee",
};

const STATS = [
  { label: "진행중 처방",  value: "4",  unit: "건",   color: C.amber,  bg: C.amberBg,  border: C.amberBorder,  icon: "⚗️" },
  { label: "이번 달 완료", value: "7",  unit: "건",   color: C.green,  bg: C.greenBg,  border: C.greenBorder,  icon: "✓" },
  { label: "안정성 진행",  value: "3",  unit: "배치", color: C.blue,   bg: C.blueBg,   border: C.blueBorder,   icon: "🔬" },
  { label: "규제 체크",    value: "12", unit: "건",   color: C.purple, bg: C.purpleBg, border: C.purpleBorder, icon: "§" },
];

const TODAY_ENTRIES = [
  { time: "09:15", type: "formula", title: "쿠션 21호 v3.2 — TiO₂ 비율 재조정",         status: "진행중", priority: true },
  { time: "11:30", type: "test",    title: "선스틱 SPF50+ — 4주 안정성 최종 판정",        status: "완료",   priority: false },
  { time: "14:00", type: "reg",     title: "EU 규제 업데이트 — Benzophenone-3 제한 확인", status: "검토중", priority: true },
  { time: "16:45", type: "idea",    title: "Bakuchiol × 세라마이드 시너지 메모",           status: "메모",   priority: false },
];

const FORMULAS = [
  { id: "F-041", name: "쿠션 파운데이션 21호", ver: "v3.2", type: "W/Si 에멀전",  ph: 5.8, visc: "3,200", status: "개발중",     progress: 65,  pColor: C.amber },
  { id: "F-038", name: "선스틱 SPF50+",        ver: "v2.1", type: "유화스틱",     ph: 6.1, visc: "—",     status: "안정성검증", progress: 88,  pColor: C.blue },
  { id: "F-035", name: "클렌징 폼 약산성",      ver: "v1.4", type: "O/W 폼",      ph: 5.5, visc: "12,000",status: "완료",       progress: 100, pColor: C.green },
  { id: "F-029", name: "세럼 — 바키아 라인",    ver: "v1.1", type: "수성 세럼",   ph: 6.0, visc: "4,500", status: "처방설계",   progress: 30,  pColor: C.purple },
];

const STABILITY = [
  { formula: "쿠션 21호",     temp: "50°C", week: 4, dE: 1.2, visc: "+8%",  result: "PASS" },
  { formula: "쿠션 21호",     temp: "4°C",  week: 4, dE: 0.4, visc: "-2%",  result: "PASS" },
  { formula: "선스틱 SPF50+", temp: "50°C", week: 4, dE: 0.9, visc: "+5%",  result: "PASS" },
  { formula: "세럼 바키아",   temp: "50°C", week: 1, dE: 0.6, visc: "+3%",  result: "진행중" },
];

const REGULATIONS = [
  { region: "KR", ingredient: "Zinc Pyrithione (ZPT)",   status: "제한",     limit: "1~2%", note: "샴푸·린스류", updated: "2025-11" },
  { region: "EU", ingredient: "Benzophenone-3",           status: "제한",     limit: "6%",   note: "자외선차단",  updated: "2026-01" },
  { region: "EU", ingredient: "Titanium Dioxide (나노)",  status: "금지",     limit: "—",    note: "분무제형",    updated: "2022-05" },
  { region: "US", ingredient: "Vitamin A (Retinol)",      status: "모니터링", limit: "—",    note: "야간크림",    updated: "2025-09" },
  { region: "KR", ingredient: "Hydroquinone",             status: "금지",     limit: "—",    note: "전 제형",     updated: "2019-03" },
];

// ── 유틸 ─────────────────────────────────────────────────────
const statusStyle = {
  "완료":       { color: C.green,  bg: C.greenBg,  border: C.greenBorder },
  "진행중":     { color: C.amber,  bg: C.amberBg,  border: C.amberBorder },
  "검토중":     { color: C.purple, bg: C.purpleBg, border: C.purpleBorder },
  "메모":       { color: C.textDim,bg: C.bg,        border: C.border },
  "개발중":     { color: C.amber,  bg: C.amberBg,  border: C.amberBorder },
  "안정성검증": { color: C.blue,   bg: C.blueBg,   border: C.blueBorder },
  "처방설계":   { color: C.purple, bg: C.purpleBg, border: C.purpleBorder },
  "PASS":       { color: C.green,  bg: C.greenBg,  border: C.greenBorder },
  "FAIL":       { color: C.red,    bg: C.redBg,    border: C.redBorder },
};

const typeStyle = {
  formula: { color: C.accent, bg: C.accentLight, label: "처방" },
  test:    { color: C.blue,   bg: C.blueBg,      label: "테스트" },
  reg:     { color: C.red,    bg: C.redBg,       label: "규제" },
  idea:    { color: C.purple, bg: C.purpleBg,    label: "아이디어" },
};

const regionStyle = {
  KR: { color: C.blue,   bg: C.blueBg,   border: C.blueBorder },
  EU: { color: C.purple, bg: C.purpleBg, border: C.purpleBorder },
  US: { color: C.green,  bg: C.greenBg,  border: C.greenBorder },
};

const Chip = ({ label, color, bg, border }) => (
  <span style={{
    display: "inline-block",
    background: bg, color, border: `1px solid ${border}`,
    borderRadius: 3, padding: "2px 8px",
    fontSize: 10, fontWeight: 600, letterSpacing: 0.5,
    fontFamily: "monospace", whiteSpace: "nowrap",
  }}>{label}</span>
);

const Dot = ({ color }) => (
  <span style={{
    display: "inline-block", width: 5, height: 5,
    borderRadius: "50%", background: color,
    marginRight: 5, flexShrink: 0,
  }} />
);

const Bar = ({ value, color }) => (
  <div style={{ height: 3, background: C.border, borderRadius: 99, overflow: "hidden" }}>
    <div style={{ height: "100%", width: `${value}%`, background: color, borderRadius: 99, transition: "width .5s" }} />
  </div>
);

const Divider = () => (
  <div style={{ height: 1, background: C.border, margin: "0" }} />
);

const SectionLabel = ({ text }) => (
  <div style={{ fontSize: 9, color: C.textDim, letterSpacing: 2.5, fontFamily: "monospace", marginBottom: 12, textTransform: "uppercase" }}>
    {text}
  </div>
);

// ── 메인 ─────────────────────────────────────────────────────
export default function WhiteLabDashboard() {
  const [activeNav, setActiveNav] = useState("overview");
  const [hovRow, setHovRow] = useState(null);

  const navItems = [
    { id: "overview",   icon: "◈", label: "Overview" },
    { id: "formulas",   icon: "⚗", label: "Formulas" },
    { id: "stability",  icon: "◎", label: "Stability" },
    { id: "regulation", icon: "§", label: "Regulation" },
    { id: "journal",    icon: "✦", label: "Journal" },
  ];

  return (
    <div style={{
      minHeight: "100vh", display: "flex",
      background: C.bg,
      fontFamily: "'Georgia', 'Noto Serif KR', serif",
      color: C.text,
    }}>

      {/* ══ 사이드바 ══ */}
      <aside style={{
        width: 210, flexShrink: 0,
        background: C.surface,
        borderRight: `1px solid ${C.border}`,
        display: "flex", flexDirection: "column",
      }}>
        {/* 로고 */}
        <div style={{ padding: "26px 22px 22px", borderBottom: `1px solid ${C.border}` }}>
          <div style={{ fontSize: 10, color: C.accent, letterSpacing: 3, fontFamily: "monospace", marginBottom: 4 }}>
            COCHING
          </div>
          <div style={{ fontSize: 17, fontWeight: 700, color: C.text, letterSpacing: 0.5 }}>
            Lab Studio
          </div>
          <div style={{ fontSize: 9, color: C.textDim, letterSpacing: 1.5, fontFamily: "monospace", marginTop: 2 }}>
            Cosmetic R&D
          </div>
        </div>

        {/* 네비 */}
        <nav style={{ padding: "14px 10px", flex: 1 }}>
          {navItems.map(n => {
            const isActive = activeNav === n.id;
            return (
              <button key={n.id} onClick={() => setActiveNav(n.id)} style={{
                width: "100%", padding: "9px 12px",
                display: "flex", alignItems: "center", gap: 9,
                background: isActive ? C.accentLight : "transparent",
                border: "none", borderRadius: 6,
                color: isActive ? C.accent : C.textSub,
                cursor: "pointer", textAlign: "left",
                fontSize: 12, fontFamily: "'Georgia', serif",
                fontWeight: isActive ? 600 : 400,
                marginBottom: 2,
                transition: "all .15s",
              }}
                onMouseEnter={e => { if (!isActive) e.currentTarget.style.background = C.bg; }}
                onMouseLeave={e => { if (!isActive) e.currentTarget.style.background = "transparent"; }}
              >
                <span style={{ fontSize: 13, width: 18, textAlign: "center", opacity: isActive ? 1 : 0.6 }}>
                  {n.icon}
                </span>
                {n.label}
              </button>
            );
          })}
        </nav>

        {/* 프로필 */}
        <div style={{ padding: "16px 22px", borderTop: `1px solid ${C.border}` }}>
          <div style={{ display: "flex", alignItems: "center", gap: 10 }}>
            <div style={{
              width: 32, height: 32, borderRadius: "50%",
              background: C.accentLight, border: `1px solid ${C.accentDim}`,
              display: "flex", alignItems: "center", justifyContent: "center",
              fontSize: 12, color: C.accent, fontWeight: 700,
            }}>R</div>
            <div>
              <div style={{ fontSize: 12, fontWeight: 600, color: C.text }}>연구원</div>
              <div style={{ fontSize: 9, color: C.textDim, fontFamily: "monospace", letterSpacing: 0.5 }}>
                Cosmetic R&D
              </div>
            </div>
          </div>
        </div>
      </aside>

      {/* ══ 콘텐츠 ══ */}
      <main style={{ flex: 1, overflowY: "auto", padding: "32px 36px" }}>

        {/* 헤더 */}
        <div style={{ display: "flex", justifyContent: "space-between", alignItems: "flex-end", marginBottom: 28 }}>
          <div>
            <div style={{ fontSize: 9, color: C.textDim, letterSpacing: 2, fontFamily: "monospace", marginBottom: 5 }}>
              {new Date().toLocaleDateString("ko-KR", { year: "numeric", month: "long", day: "numeric", weekday: "long" })}
            </div>
            <h1 style={{ margin: 0, fontSize: 22, fontWeight: 700, color: C.text, letterSpacing: -0.3 }}>
              Research Overview
            </h1>
          </div>
          <button style={{
            background: C.accent, color: "#fff",
            border: "none", borderRadius: 6,
            padding: "9px 18px", cursor: "pointer",
            fontSize: 12, fontFamily: "'Georgia', serif",
            display: "flex", alignItems: "center", gap: 7,
            boxShadow: `0 2px 12px ${C.accent}33`,
          }}>
            + 새 항목 작성
          </button>
        </div>

        {/* ── STAT 카드 ── */}
        <div style={{ display: "grid", gridTemplateColumns: "repeat(4,1fr)", gap: 14, marginBottom: 24 }}>
          {STATS.map((s, i) => (
            <div key={i} style={{
              background: C.surface, border: `1px solid ${C.border}`,
              borderRadius: 10, padding: "18px 20px",
              boxShadow: "0 1px 4px rgba(0,0,0,0.04)",
            }}>
              <div style={{ display: "flex", justifyContent: "space-between", alignItems: "flex-start" }}>
                <div style={{ fontSize: 10, color: C.textDim, letterSpacing: 1, fontFamily: "monospace" }}>
                  {s.label.toUpperCase()}
                </div>
                <div style={{
                  width: 28, height: 28, borderRadius: 7,
                  background: s.bg, border: `1px solid ${s.border}`,
                  display: "flex", alignItems: "center", justifyContent: "center",
                  fontSize: 13,
                }}>{s.icon}</div>
              </div>
              <div style={{ display: "flex", alignItems: "baseline", gap: 5, marginTop: 12 }}>
                <span style={{ fontSize: 26, fontWeight: 700, color: s.color, lineHeight: 1 }}>{s.value}</span>
                <span style={{ fontSize: 11, color: C.textDim }}>{s.unit}</span>
              </div>
            </div>
          ))}
        </div>

        {/* ── 2열 ── */}
        <div style={{ display: "grid", gridTemplateColumns: "1.1fr 0.9fr", gap: 16, marginBottom: 16 }}>

          {/* 오늘의 업무 일지 */}
          <div style={{
            background: C.surface, border: `1px solid ${C.border}`,
            borderRadius: 10, overflow: "hidden",
            boxShadow: "0 1px 4px rgba(0,0,0,0.04)",
          }}>
            <div style={{ padding: "18px 20px 14px", display: "flex", justifyContent: "space-between", alignItems: "center" }}>
              <div>
                <SectionLabel text="Today's Log" />
                <div style={{ fontSize: 14, fontWeight: 600, color: C.text, marginTop: -4 }}>오늘의 업무</div>
              </div>
              <button style={{ background: "none", border: `1px solid ${C.border}`, borderRadius: 5, padding: "4px 10px", color: C.textSub, fontSize: 10, cursor: "pointer", fontFamily: "monospace" }}>
                전체 보기
              </button>
            </div>
            <Divider />
            {TODAY_ENTRIES.map((e, i) => {
              const ts = typeStyle[e.type];
              const ss = statusStyle[e.status] || statusStyle["메모"];
              return (
                <div key={i} style={{
                  display: "flex", gap: 12, alignItems: "flex-start",
                  padding: "13px 20px",
                  borderBottom: i < TODAY_ENTRIES.length - 1 ? `1px solid ${C.border}` : "none",
                  background: "transparent",
                  transition: "background .12s",
                  cursor: "pointer",
                }}
                  onMouseEnter={e => e.currentTarget.style.background = C.bg}
                  onMouseLeave={e => e.currentTarget.style.background = "transparent"}
                >
                  <span style={{ fontSize: 10, color: C.textDim, fontFamily: "monospace", flexShrink: 0, marginTop: 3, width: 38 }}>
                    {e.time}
                  </span>
                  <div style={{ width: 2.5, alignSelf: "stretch", background: ts.color, borderRadius: 2, flexShrink: 0, opacity: 0.5 }} />
                  <div style={{ flex: 1 }}>
                    <div style={{ display: "flex", gap: 5, marginBottom: 4 }}>
                      <Chip label={ts.label} color={ts.color} bg={ts.bg} border={ts.bg} />
                      {e.priority && <Chip label="우선" color={C.red} bg={C.redBg} border={C.redBorder} />}
                    </div>
                    <div style={{ fontSize: 12.5, color: C.text, lineHeight: 1.45 }}>{e.title}</div>
                  </div>
                  <Chip label={e.status} color={ss.color} bg={ss.bg} border={ss.border} />
                </div>
              );
            })}
          </div>

          {/* 안정성 현황 */}
          <div style={{
            background: C.surface, border: `1px solid ${C.border}`,
            borderRadius: 10, overflow: "hidden",
            boxShadow: "0 1px 4px rgba(0,0,0,0.04)",
          }}>
            <div style={{ padding: "18px 20px 14px", display: "flex", justifyContent: "space-between", alignItems: "center" }}>
              <div>
                <SectionLabel text="Stability Status" />
                <div style={{ fontSize: 14, fontWeight: 600, color: C.text, marginTop: -4 }}>안정성 현황</div>
              </div>
              <button style={{ background: "none", border: `1px solid ${C.border}`, borderRadius: 5, padding: "4px 10px", color: C.textSub, fontSize: 10, cursor: "pointer", fontFamily: "monospace" }}>
                상세 보기
              </button>
            </div>
            <Divider />
            {STABILITY.map((s, i) => {
              const rs = statusStyle[s.result] || statusStyle["진행중"];
              return (
                <div key={i} style={{
                  display: "grid", gridTemplateColumns: "1fr 52px 52px 64px",
                  gap: 8, alignItems: "center",
                  padding: "12px 20px",
                  borderBottom: i < STABILITY.length - 1 ? `1px solid ${C.border}` : "none",
                  cursor: "pointer", transition: "background .12s",
                }}
                  onMouseEnter={e => e.currentTarget.style.background = C.bg}
                  onMouseLeave={e => e.currentTarget.style.background = "transparent"}
                >
                  <div>
                    <div style={{ fontSize: 12, color: C.text, fontWeight: 500 }}>{s.formula}</div>
                    <div style={{ fontSize: 10, color: C.textDim, fontFamily: "monospace", marginTop: 2 }}>
                      {s.temp} · {s.week}주
                    </div>
                  </div>
                  <div style={{ textAlign: "center" }}>
                    <div style={{ fontSize: 9, color: C.textDim, fontFamily: "monospace", marginBottom: 2 }}>ΔE</div>
                    <div style={{ fontSize: 13, fontWeight: 700, color: s.dE < 2 ? C.green : C.red }}>
                      {s.dE}
                    </div>
                  </div>
                  <div style={{ textAlign: "center" }}>
                    <div style={{ fontSize: 9, color: C.textDim, fontFamily: "monospace", marginBottom: 2 }}>점도</div>
                    <div style={{ fontSize: 12, color: C.textSub, fontFamily: "monospace" }}>{s.visc}</div>
                  </div>
                  <Chip label={s.result} color={rs.color} bg={rs.bg} border={rs.border} />
                </div>
              );
            })}
          </div>
        </div>

        {/* ── 처방 목록 ── */}
        <div style={{
          background: C.surface, border: `1px solid ${C.border}`,
          borderRadius: 10, overflow: "hidden",
          boxShadow: "0 1px 4px rgba(0,0,0,0.04)",
          marginBottom: 16,
        }}>
          <div style={{ padding: "18px 20px 14px", display: "flex", justifyContent: "space-between", alignItems: "center" }}>
            <div>
              <SectionLabel text="Active Formulas" />
              <div style={{ fontSize: 14, fontWeight: 600, color: C.text, marginTop: -4 }}>처방 목록</div>
            </div>
            <button style={{
              background: C.accentLight, border: `1px solid ${C.accentDim}`,
              color: C.accent, borderRadius: 5,
              padding: "5px 12px", cursor: "pointer",
              fontSize: 10, fontFamily: "monospace",
            }}>+ 새 처방</button>
          </div>
          <Divider />
          {/* 테이블 헤더 */}
          <div style={{
            display: "grid", gridTemplateColumns: "56px 1fr 90px 64px 64px 90px 200px",
            gap: 12, padding: "8px 20px",
            background: C.bg,
            borderBottom: `1px solid ${C.border}`,
          }}>
            {["ID", "처방명", "제형", "pH", "점도", "상태", "진행률"].map((h, i) => (
              <span key={i} style={{ fontSize: 9, color: C.textDim, letterSpacing: 1.5, fontFamily: "monospace" }}>{h}</span>
            ))}
          </div>
          {FORMULAS.map((f, i) => {
            const ss = statusStyle[f.status] || statusStyle["진행중"];
            const isHov = hovRow === i;
            return (
              <div key={i}
                onMouseEnter={() => setHovRow(i)}
                onMouseLeave={() => setHovRow(null)}
                style={{
                  display: "grid", gridTemplateColumns: "56px 1fr 90px 64px 64px 90px 200px",
                  gap: 12, padding: "13px 20px", alignItems: "center",
                  borderBottom: i < FORMULAS.length - 1 ? `1px solid ${C.border}` : "none",
                  background: isHov ? C.bg : "transparent",
                  cursor: "pointer", transition: "background .12s",
                }}>
                <span style={{ fontSize: 10, color: C.textDim, fontFamily: "monospace" }}>{f.id}</span>
                <div>
                  <div style={{ fontSize: 13, fontWeight: 600, color: C.text }}>{f.name}</div>
                  <div style={{ fontSize: 10, color: C.textDim, marginTop: 1 }}>{f.ver}</div>
                </div>
                <span style={{ fontSize: 11, color: C.textSub }}>{f.type}</span>
                <span style={{ fontSize: 13, fontWeight: 700, color: C.green, fontFamily: "monospace" }}>{f.ph}</span>
                <span style={{ fontSize: 11, color: C.textSub, fontFamily: "monospace" }}>{f.visc}</span>
                <Chip label={f.status} color={ss.color} bg={ss.bg} border={ss.border} />
                <div>
                  <div style={{ display: "flex", justifyContent: "space-between", marginBottom: 4 }}>
                    <span style={{ fontSize: 9, color: C.textDim, fontFamily: "monospace" }}>진행률</span>
                    <span style={{ fontSize: 9, color: f.pColor, fontFamily: "monospace", fontWeight: 700 }}>{f.progress}%</span>
                  </div>
                  <Bar value={f.progress} color={f.pColor} />
                </div>
              </div>
            );
          })}
        </div>

        {/* ── 규제 패널 ── */}
        <div style={{
          background: C.surface, border: `1px solid ${C.border}`,
          borderRadius: 10, overflow: "hidden",
          boxShadow: "0 1px 4px rgba(0,0,0,0.04)",
        }}>
          <div style={{ padding: "18px 20px 14px", display: "flex", justifyContent: "space-between", alignItems: "center" }}>
            <div>
              <SectionLabel text="Regulation Watch" />
              <div style={{ fontSize: 14, fontWeight: 600, color: C.text, marginTop: -4 }}>규제 현황</div>
            </div>
            <button style={{ background: "none", border: `1px solid ${C.border}`, borderRadius: 5, padding: "4px 10px", color: C.textSub, fontSize: 10, cursor: "pointer", fontFamily: "monospace" }}>
              전체 목록
            </button>
          </div>
          <Divider />
          {/* 헤더 */}
          <div style={{
            display: "grid", gridTemplateColumns: "56px 1fr 80px 70px 1fr 80px",
            gap: 12, padding: "8px 20px",
            background: C.bg, borderBottom: `1px solid ${C.border}`,
          }}>
            {["지역", "성분명", "상태", "한도", "비고", "업데이트"].map((h, i) => (
              <span key={i} style={{ fontSize: 9, color: C.textDim, letterSpacing: 1.5, fontFamily: "monospace" }}>{h}</span>
            ))}
          </div>
          {REGULATIONS.map((r, i) => {
            const rs = regionStyle[r.region];
            const ss = {
              "제한":     { color: C.amber,  bg: C.amberBg,  border: C.amberBorder },
              "금지":     { color: C.red,    bg: C.redBg,    border: C.redBorder },
              "모니터링": { color: C.purple, bg: C.purpleBg, border: C.purpleBorder },
            }[r.status];
            return (
              <div key={i} style={{
                display: "grid", gridTemplateColumns: "56px 1fr 80px 70px 1fr 80px",
                gap: 12, padding: "12px 20px", alignItems: "center",
                borderBottom: i < REGULATIONS.length - 1 ? `1px solid ${C.border}` : "none",
                cursor: "pointer", transition: "background .12s",
              }}
                onMouseEnter={e => e.currentTarget.style.background = C.bg}
                onMouseLeave={e => e.currentTarget.style.background = "transparent"}
              >
                <Chip label={r.region} color={rs.color} bg={rs.bg} border={rs.border} />
                <span style={{ fontSize: 12.5, color: C.text }}>{r.ingredient}</span>
                <Chip label={r.status} color={ss.color} bg={ss.bg} border={ss.border} />
                <span style={{ fontSize: 11, color: C.textSub, fontFamily: "monospace" }}>{r.limit}</span>
                <span style={{ fontSize: 11, color: C.textDim }}>{r.note}</span>
                <span style={{ fontSize: 10, color: C.textDim, fontFamily: "monospace" }}>{r.updated}</span>
              </div>
            );
          })}
        </div>

        {/* 푸터 */}
        <div style={{
          marginTop: 28, paddingTop: 16,
          borderTop: `1px solid ${C.border}`,
          display: "flex", justifyContent: "space-between",
          fontFamily: "monospace", fontSize: 9, color: C.textDim, letterSpacing: 0.8,
        }}>
          <span>COCHING LAB STUDIO · COSMETIC R&D</span>
          <span>v2.3 Hybrid · PRECISION-ARITHMETIC SKILL v1.0</span>
        </div>
      </main>
    </div>
  );
}
