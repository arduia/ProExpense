// Low-fi wireframe primitives — grayscale boxes, dashed lines, sketch chrome.
// All visual atoms used by screens.jsx live here.

const W_PAPER = '#fbfaf6';
const W_INK = '#1a1a1a';
const W_MUTED = '#8a8a82';
const W_LINE = '#cfcdc2';
const W_LINE_2 = '#e4e1d6';
const W_FILL = '#ececdf';
const W_FILL_2 = '#f3f1e6';
const W_ACCENT = '#c8482a';

// ─────────────────────────────────────────────────────────────
// Wireframe phone shell — simplified iOS silhouette, low-fi inside
// ─────────────────────────────────────────────────────────────
function WirePhone({ children, width = 320, height = 680, title, label, num, statusBar = true, homeBar = true }) {
  return (
    <div style={{ display: 'flex', flexDirection: 'column', alignItems: 'center', gap: 14, flexShrink: 0 }}>
      {/* screen index + label */}
      <div style={{ display: 'flex', alignItems: 'baseline', gap: 10, fontFamily: 'var(--mono)', textTransform: 'uppercase', letterSpacing: '0.12em', fontSize: 10, color: W_MUTED }}>
        <span style={{ color: W_ACCENT, fontWeight: 500 }}>{num}</span>
        <span style={{ color: W_INK, letterSpacing: '0.06em', fontFamily: 'var(--serif)', fontStyle: 'italic', fontSize: 16, textTransform: 'none' }}>{title}</span>
        {label && <span style={{ color: W_MUTED }}>· {label}</span>}
      </div>
      <div
        style={{
          width,
          height,
          background: W_PAPER,
          border: `1.5px solid ${W_INK}`,
          borderRadius: 44,
          position: 'relative',
          overflow: 'hidden',
          boxShadow: '0 12px 32px rgba(20,18,10,0.06), 0 2px 6px rgba(20,18,10,0.04)',
          fontFamily: 'var(--sans)',
        }}
      >
        {/* dynamic island */}
        <div
          style={{
            position: 'absolute',
            top: 10,
            left: '50%',
            transform: 'translateX(-50%)',
            width: 96,
            height: 28,
            borderRadius: 20,
            background: W_INK,
            zIndex: 5,
          }}
        />
        {/* status bar */}
        {statusBar && (
          <div
            style={{
              position: 'absolute',
              top: 0,
              left: 0,
              right: 0,
              height: 48,
              display: 'flex',
              alignItems: 'center',
              justifyContent: 'space-between',
              padding: '0 26px',
              fontSize: 12,
              fontWeight: 600,
              color: W_INK,
              zIndex: 4,
            }}
          >
            <span>9:41</span>
            <span style={{ display: 'flex', gap: 4, alignItems: 'center', fontSize: 10, color: W_INK }}>
              <span>•••</span>
              <span style={{ width: 18, height: 9, border: `1px solid ${W_INK}`, borderRadius: 2 }}></span>
            </span>
          </div>
        )}
        <div style={{ position: 'absolute', inset: 0, paddingTop: statusBar ? 48 : 0, paddingBottom: homeBar ? 28 : 0, display: 'flex', flexDirection: 'column' }}>
          {children}
        </div>
        {/* home indicator */}
        {homeBar && (
          <div
            style={{
              position: 'absolute',
              bottom: 8,
              left: '50%',
              transform: 'translateX(-50%)',
              width: 124,
              height: 4,
              borderRadius: 99,
              background: W_INK,
              opacity: 0.7,
            }}
          />
        )}
      </div>
    </div>
  );
}

// ─────────────────────────────────────────────────────────────
// Bottom nav — Home · Budget · [ + ] · Journal · More
// Center slot is the always-accessible Add Expense button.
// ─────────────────────────────────────────────────────────────
function BottomNav({ active = 'home' }) {
  const items = [
    { id: 'home', label: 'Home' },
    { id: 'budget', label: 'Budget' },
    { id: 'add', label: null },
    { id: 'journal', label: 'Journal' },
    { id: 'more', label: 'More' },
  ];
  return (
    <div
      style={{
        borderTop: `1px solid ${W_LINE}`,
        background: W_PAPER,
        padding: '8px 8px 4px',
        display: 'flex',
        alignItems: 'flex-start',
        position: 'relative',
      }}
    >
      {items.map((it) => {
        if (it.id === 'add') {
          return (
            <div key="add" style={{ flex: 1, display: 'flex', justifyContent: 'center', position: 'relative' }}>
              <div
                style={{
                  position: 'absolute',
                  top: -14,
                  left: '50%',
                  transform: 'translateX(-50%)',
                  width: 46,
                  height: 46,
                  borderRadius: 14,
                  background: W_INK,
                  color: W_PAPER,
                  display: 'flex',
                  alignItems: 'center',
                  justifyContent: 'center',
                  fontSize: 24,
                  fontWeight: 300,
                  lineHeight: 1,
                  boxShadow: '0 3px 8px rgba(20,18,10,0.18)',
                  zIndex: 3,
                }}
              >
                +
              </div>
              {/* spacer to keep nav row height */}
              <div style={{ width: 22, height: 38 }} />
            </div>
          );
        }
        const on = it.id === active;
        return (
          <div key={it.id} style={{ flex: 1, display: 'flex', flexDirection: 'column', alignItems: 'center', gap: 4, padding: '4px 0' }}>
            <div
              style={{
                width: 22,
                height: 22,
                border: `1.2px solid ${on ? W_INK : W_LINE}`,
                borderRadius: 4,
                background: on ? W_FILL : 'transparent',
              }}
            />
            <span style={{ fontSize: 10, color: on ? W_INK : W_MUTED, fontWeight: on ? 600 : 400 }}>{it.label}</span>
          </div>
        );
      })}
    </div>
  );
}

// Legacy no-op — FAB was replaced by embedded Add button in BottomNav.
function FAB() { return null; }

// Generic boxes
function Box({ w = '100%', h = 14, r = 4, fill = W_FILL, style = {}, children }) {
  return <div style={{ width: w, height: h, background: fill, borderRadius: r, ...style }}>{children}</div>;
}
function Line({ w = '60%', style = {} }) {
  return <div style={{ width: w, height: 6, background: W_FILL_2, borderRadius: 99, ...style }} />;
}
function Stack({ children, gap = 8, style = {} }) {
  return <div style={{ display: 'flex', flexDirection: 'column', gap, ...style }}>{children}</div>;
}
function Row({ children, gap = 8, style = {} }) {
  return <div style={{ display: 'flex', gap, alignItems: 'center', ...style }}>{children}</div>;
}
function Pad({ children, p = 16, style = {} }) {
  return <div style={{ padding: p, ...style }}>{children}</div>;
}

// Chip
function Chip({ children, active = false, style = {} }) {
  return (
    <div
      style={{
        padding: '5px 10px',
        border: `1px solid ${active ? W_INK : W_LINE}`,
        borderRadius: 99,
        fontSize: 10,
        color: active ? W_INK : W_MUTED,
        background: active ? W_FILL : 'transparent',
        fontWeight: active ? 600 : 400,
        whiteSpace: 'nowrap',
        ...style,
      }}
    >
      {children}
    </div>
  );
}

// Sketch icon — diagonal-slash square (used as low-fi icon stand-in)
function Glyph({ size = 22, style = {} }) {
  return (
    <div
      style={{
        width: size,
        height: size,
        border: `1px solid ${W_LINE}`,
        borderRadius: 6,
        background: W_FILL_2,
        position: 'relative',
        overflow: 'hidden',
        ...style,
      }}
    >
      <div style={{ position: 'absolute', inset: 0, background: `linear-gradient(135deg, transparent calc(50% - 0.5px), ${W_LINE} 50%, transparent calc(50% + 0.5px))` }} />
    </div>
  );
}

// Section divider w/ optional label
function SectionHead({ children, mute = false, style = {} }) {
  return (
    <div
      style={{
        fontSize: 10,
        fontFamily: 'var(--mono)',
        textTransform: 'uppercase',
        letterSpacing: '0.1em',
        color: mute ? W_MUTED : W_INK,
        padding: '2px 0',
        ...style,
      }}
    >
      {children}
    </div>
  );
}

// Numbered annotation pin (placed over a phone)
function Pin({ n, top, left, right, bottom }) {
  return (
    <div
      style={{
        position: 'absolute',
        top,
        left,
        right,
        bottom,
        width: 22,
        height: 22,
        borderRadius: 99,
        background: W_ACCENT,
        color: '#fbf2dd',
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'center',
        fontSize: 11,
        fontWeight: 600,
        fontFamily: 'var(--mono)',
        boxShadow: '0 2px 6px rgba(200,72,42,0.35), 0 0 0 3px rgba(251,250,246,0.9)',
        zIndex: 20,
      }}
    >
      {n}
    </div>
  );
}

Object.assign(window, {
  WirePhone, BottomNav, FAB, Box, Line, Stack, Row, Pad, Chip, Glyph, SectionHead, Pin,
  W_PAPER, W_INK, W_MUTED, W_LINE, W_LINE_2, W_FILL, W_FILL_2, W_ACCENT,
});
