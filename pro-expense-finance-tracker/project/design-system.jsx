// Pro Expense — Design System reference
// Mirrors the live implementation: tokens from proto-brand.css,
// primitives (Icon, CatBadge, CatChip, Button, CATEGORIES) from proto-ui.jsx,
// ProtoBottomNav from proto-phone.jsx. Single source of truth.

// ── doc-chrome helpers (uniquely-named styles to avoid global collision) ──
const ds = {
  page: { background: '#f3f1ec', color: 'var(--ink)', minHeight: '100vh', fontFamily: 'var(--sans)' },
  wrap: { maxWidth: 1080, margin: '0 auto', padding: '0 40px' },
  eyebrow: { fontFamily: 'var(--mono)', fontSize: 11, textTransform: 'uppercase', letterSpacing: '0.14em', color: 'var(--blue-700)', fontWeight: 500 },
  sectionLabel: { fontFamily: 'var(--mono)', fontSize: 11, textTransform: 'uppercase', letterSpacing: '0.12em', color: 'var(--ink-3)', fontWeight: 600 },
  h2: { fontFamily: 'var(--serif)', fontSize: 36, letterSpacing: '-0.02em', lineHeight: 1.05, margin: 0, color: 'var(--ink)' },
  lede: { fontSize: 15, lineHeight: 1.6, color: 'var(--ink-2)', maxWidth: 620, margin: '10px 0 0' },
  card: { background: 'var(--card)', border: '1px solid var(--line)', borderRadius: 16, boxShadow: '0 1px 0 rgba(33,33,33,0.03), 0 6px 16px rgba(33,33,33,0.04)' },
  mono: { fontFamily: 'var(--mono)', fontSize: 11.5 },
};

function Section({ id, num, title, children }) {
  return (
    <section id={id} style={{ padding: '0 0 76px' }}>
      <div style={ds.wrap}>
        <div style={{ display: 'flex', alignItems: 'baseline', gap: 14, marginBottom: 26, paddingBottom: 16, borderBottom: '1px solid var(--line)' }}>
          <span style={{ ...ds.eyebrow, color: 'var(--blue-700)' }}>{num}</span>
          <h2 style={ds.h2}>{title}</h2>
        </div>
        {children}
      </div>
    </section>
  );
}

function SubLabel({ children, style = {} }) {
  return <div style={{ ...ds.sectionLabel, marginBottom: 14, ...style }}>{children}</div>;
}

// ── 1. Hero ──
function Hero() {
  return (
    <header style={{ background: 'var(--ink)', color: '#fff', padding: '0 0 56px' }}>
      <div style={{ ...ds.wrap, paddingTop: 56 }}>
        <div style={{ display: 'flex', alignItems: 'center', gap: 12, marginBottom: 40 }}>
          <div style={{ width: 38, height: 38, borderRadius: 11, background: 'var(--blue-500)', display: 'flex', alignItems: 'center', justifyContent: 'center', boxShadow: '0 6px 16px rgba(3,155,229,0.5)' }}>
            <Icon name="plus" size={22} stroke="#fff" strokeWidth={2.2} />
          </div>
          <span style={{ fontFamily: 'var(--mono)', fontSize: 12, letterSpacing: '0.12em', textTransform: 'uppercase', color: 'rgba(255,255,255,0.7)' }}>Pro Expense</span>
        </div>
        <div style={{ fontFamily: 'var(--mono)', fontSize: 11, letterSpacing: '0.16em', textTransform: 'uppercase', color: 'var(--blue-300)', marginBottom: 16 }}>Design System · v1</div>
        <h1 style={{ fontFamily: 'var(--serif)', fontSize: 76, lineHeight: 0.98, letterSpacing: '-0.03em', margin: 0, maxWidth: 760 }}>
          The system behind<br />the finance tracker.
        </h1>
        <p style={{ fontSize: 16.5, lineHeight: 1.6, color: 'rgba(255,255,255,0.72)', maxWidth: 600, marginTop: 24 }}>
          Tokens, type, iconography and components extracted from the shipped Hi-Fi build — a Material-blue palette on warm paper, with an Instrument Serif display voice. Use this as the reference for any new screen.
        </p>
        <div style={{ display: 'flex', flexWrap: 'wrap', gap: 8, marginTop: 36 }}>
          {[
            ['Geist', 'var(--sans)'], ['Geist Mono', 'var(--mono)'], ['Instrument Serif', 'var(--serif)'],
          ].map(([n, f]) => (
            <span key={n} style={{ fontFamily: f, fontSize: 13, padding: '7px 14px', borderRadius: 99, border: '1px solid rgba(255,255,255,0.18)', color: 'rgba(255,255,255,0.85)' }}>{n}</span>
          ))}
          <span style={{ fontFamily: 'var(--mono)', fontSize: 12, padding: '7px 14px', borderRadius: 99, background: 'var(--blue-500)', color: '#fff' }}>#039BE5 primary</span>
        </div>
      </div>
    </header>
  );
}

// ── Nav strip ──
function NavStrip() {
  const links = [
    ['foundations', 'Foundations'], ['color', 'Color'], ['type', 'Type'],
    ['icons', 'Icons'], ['categories', 'Categories'], ['buttons', 'Buttons'],
    ['surfaces', 'Surfaces'], ['data', 'Data'], ['nav', 'Navigation'], ['motion', 'Motion'],
  ];
  return (
    <nav style={{ position: 'sticky', top: 0, zIndex: 20, background: 'rgba(243,241,236,0.86)', backdropFilter: 'blur(10px)', borderBottom: '1px solid var(--line)' }}>
      <div style={{ ...ds.wrap, display: 'flex', gap: 4, overflowX: 'auto', padding: '11px 40px' }}>
        {links.map(([href, label]) => (
          <a key={href} href={'#' + href} style={{ fontFamily: 'var(--mono)', fontSize: 11.5, letterSpacing: '0.04em', textTransform: 'uppercase', color: 'var(--ink-3)', textDecoration: 'none', padding: '6px 12px', borderRadius: 8, whiteSpace: 'nowrap' }}>{label}</a>
        ))}
      </div>
    </nav>
  );
}

window.DSChrome = { ds, Section, SubLabel, Hero, NavStrip };
