// android-frame.jsx — Material 3 (Android 14 / Material You) device shell +
// Material components, sized for visual parity with the iOS PhoneShell so the
// two sit side-by-side in the platform-comparison canvas.
//
// Distinct Android tells vs the iOS shell:
//   • centered punch-hole camera (not a Dynamic Island)
//   • status bar clock on the LEFT, system icons on the right
//   • gesture-nav handle (wide + thin) instead of the iOS home indicator
//   • Material filled pill buttons, outlined text fields w/ notched labels,
//     radio selection (not checkmarks), linear progress, circular spinner
//
// Reuses global Icon from proto-ui.jsx. Exports its components to window.

const AND_W = 390;
const AND_H = 844;
const AND_STATUS_H = 36;
const AND_NAV_H = 28;

// Material palette mapped onto the existing brand tokens
const MD = {
  primary: 'var(--blue-500)',
  onPrimary: '#ffffff',
  primaryDeep: 'var(--blue-700)',
  primaryContainer: 'var(--blue-100)',
  onPrimaryContainer: 'var(--blue-700)',
  surface: '#ffffff',
  surfaceVariant: '#eef1f4',
  surfaceDim: '#f3f4f6',
  outline: '#c2c7ce',
  outlineVariant: '#dfe3e8',
  onSurface: '#1a1c1e',
  onSurfaceVariant: '#5a5f66',
};

function AndroidShell({ children, navColor = 'var(--ink)' }) {
  return (
    <div
      style={{
        width: AND_W,
        height: AND_H,
        background: 'var(--paper)',
        borderRadius: 44,
        position: 'relative',
        overflow: 'hidden',
        boxShadow: '0 30px 60px rgba(0,0,0,0.32), 0 0 0 6px #0e0e10, 0 0 0 7px #2b2d31',
        fontFamily: 'var(--sans)',
      }}
    >
      {/* punch-hole camera — centered */}
      <div style={{
        position: 'absolute', top: 12, left: '50%', transform: 'translateX(-50%)',
        width: 11, height: 11, borderRadius: 99, background: '#0a0a0c', zIndex: 50,
        boxShadow: 'inset 0 0 0 1.5px #23252a',
      }} />

      {/* status bar — clock LEFT, system icons right */}
      <div style={{
        position: 'absolute', top: 0, left: 0, right: 0,
        height: AND_STATUS_H,
        display: 'flex', alignItems: 'center', justifyContent: 'space-between',
        padding: '10px 20px 0',
        fontSize: 13.5, fontWeight: 600, color: 'var(--ink)',
        zIndex: 10, pointerEvents: 'none',
      }}>
        <span style={{ fontVariantNumeric: 'tabular-nums' }}>9:41</span>
        <span style={{ display: 'flex', gap: 6, alignItems: 'center' }}>
          {/* wifi (fan) */}
          <svg width="16" height="13" viewBox="0 0 16 13" fill="currentColor"><path d="M8 12.2 1 4.6a10 10 0 0 1 14 0L8 12.2Z" opacity="0.92"/></svg>
          {/* signal (triangle) */}
          <svg width="16" height="13" viewBox="0 0 16 13" fill="currentColor"><path d="M14 1v11H2L14 1Z"/></svg>
          {/* battery (Android vertical-ish) */}
          <svg width="13" height="13" viewBox="0 0 13 13"><rect x="3" y="1.2" width="7" height="11" rx="1.6" fill="none" stroke="currentColor" strokeWidth="1.2" strokeOpacity="0.55"/><rect x="5.4" y="0.2" width="2.2" height="1.4" rx="0.5" fill="currentColor" opacity="0.55"/><rect x="4.2" y="5.2" width="4.6" height="6" rx="0.8" fill="currentColor"/></svg>
        </span>
      </div>

      {/* content */}
      <div style={{ position: 'absolute', inset: 0, paddingTop: AND_STATUS_H, paddingBottom: AND_NAV_H, display: 'flex', flexDirection: 'column' }}>
        <div className="proto-app" style={{ flex: 1, position: 'relative', background: 'var(--card)' }}>
          {children}
        </div>
      </div>

      {/* gesture-nav handle */}
      <div style={{
        position: 'absolute', bottom: 9, left: '50%', transform: 'translateX(-50%)',
        width: 132, height: 4.5, borderRadius: 99, background: navColor, opacity: 0.9, zIndex: 60,
      }} />
    </div>
  );
}

// ── Material top app bar (small) ─────────────────────────────────────
function MdTopBar({ title, onBack = true, trailing = null }) {
  return (
    <div style={{ height: 56, display: 'flex', alignItems: 'center', padding: '0 4px 0 6px', flexShrink: 0 }}>
      {onBack && (
        <div style={{ width: 48, height: 48, borderRadius: 99, display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
          <Icon name="back" size={24} stroke={MD.onSurface} strokeWidth={2} />
        </div>
      )}
      <div style={{ flex: 1, fontSize: 22, fontWeight: 500, color: MD.onSurface, letterSpacing: '0', paddingLeft: onBack ? 4 : 12 }}>{title}</div>
      {trailing}
    </div>
  );
}

// ── Material linear progress (determinate) ───────────────────────────
function MdLinearProgress({ value = 0.5 }) {
  return (
    <div style={{ height: 4, background: MD.primaryContainer, borderRadius: 99, overflow: 'hidden', margin: '0 0 0 0' }}>
      <div style={{ width: (value * 100) + '%', height: '100%', background: MD.primary, borderRadius: 99 }} />
    </div>
  );
}

// ── Material circular progress (indeterminate snapshot) ──────────────
function MdCircularProgress({ size = 40, stroke = 4 }) {
  const r = (size - stroke) / 2;
  const c = 2 * Math.PI * r;
  return (
    <svg width={size} height={size} viewBox={`0 0 ${size} ${size}`} style={{ transform: 'rotate(-90deg)' }}>
      <circle cx={size/2} cy={size/2} r={r} fill="none" stroke={MD.primary} strokeWidth={stroke}
        strokeLinecap="round" strokeDasharray={`${c*0.28} ${c}`} />
    </svg>
  );
}

// ── Material filled (pill) button ────────────────────────────────────
function MdFilledButton({ children, full = true, tone = 'primary' }) {
  const bg = tone === 'primary' ? MD.primary : MD.surfaceVariant;
  const fg = tone === 'primary' ? MD.onPrimary : MD.onSurface;
  return (
    <button style={{
      height: 56, width: full ? '100%' : 'auto', padding: full ? 0 : '0 28px',
      borderRadius: 28, border: 'none', background: bg, color: fg,
      fontFamily: 'var(--sans)', fontSize: 15.5, fontWeight: 600, letterSpacing: '0.3px',
      cursor: 'pointer', boxShadow: tone === 'primary' ? '0 1px 3px rgba(2,136,209,0.35)' : 'none',
      display: 'flex', alignItems: 'center', justifyContent: 'center', gap: 8,
    }}>{children}</button>
  );
}

// ── Material text button ─────────────────────────────────────────────
function MdTextButton({ children, tone = 'primary', style = {} }) {
  const fg = tone === 'primary' ? MD.primaryDeep : MD.onSurfaceVariant;
  return (
    <button style={{
      height: 40, padding: '0 12px', borderRadius: 20, border: 'none', background: 'transparent',
      color: fg, fontFamily: 'var(--sans)', fontSize: 14, fontWeight: 600, letterSpacing: '0.2px',
      cursor: 'pointer', display: 'inline-flex', alignItems: 'center', gap: 4, ...style,
    }}>{children}</button>
  );
}

// ── Material outlined text field w/ notched floating label ───────────
function MdOutlinedField({ label, value, leading, focused = false, helper, trailing }) {
  const ol = focused ? MD.primary : MD.outline;
  const lc = focused ? MD.primaryDeep : MD.onSurfaceVariant;
  return (
    <div>
      <div style={{ position: 'relative' }}>
        {/* notched label */}
        <span style={{
          position: 'absolute', top: -8, left: leading ? 44 : 14, padding: '0 4px',
          background: MD.surface, fontSize: 12, fontWeight: 500, color: lc, zIndex: 1, letterSpacing: '0.1px', whiteSpace: 'nowrap',
        }}>{label}</span>
        <div style={{
          display: 'flex', alignItems: 'center', gap: 12, minHeight: 56,
          border: `${focused ? 2 : 1.4}px solid ${ol}`, borderRadius: 6, padding: leading ? '0 16px 0 14px' : '0 16px',
          background: MD.surface,
        }}>
          {leading && <Icon name={leading} size={22} stroke={lc} strokeWidth={1.8} style={{ flexShrink: 0 }} />}
          <span style={{ flex: 1, fontSize: 16, color: value ? MD.onSurface : MD.onSurfaceVariant }}>
            {value || ''}{focused && <span className="proto-cursor" />}
          </span>
          {trailing}
        </div>
      </div>
      {helper && <div style={{ fontSize: 12, color: MD.onSurfaceVariant, margin: '5px 16px 0' }}>{helper}</div>}
    </div>
  );
}

// ── Material radio (single-select) ───────────────────────────────────
function MdRadio({ checked }) {
  return (
    <div style={{ width: 22, height: 22, borderRadius: 99, border: `2px solid ${checked ? MD.primary : MD.outline}`, display: 'flex', alignItems: 'center', justifyContent: 'center', flexShrink: 0 }}>
      {checked && <div style={{ width: 11, height: 11, borderRadius: 99, background: MD.primary }} />}
    </div>
  );
}

// ── Material page indicator (worm dots) ──────────────────────────────
function MdPageDots({ count, active }) {
  return (
    <div style={{ display: 'flex', justifyContent: 'center', gap: 7 }}>
      {Array.from({ length: count }).map((_, i) => (
        <div key={i} style={{
          width: i === active ? 22 : 7, height: 7, borderRadius: 99,
          background: i === active ? MD.primary : MD.outlineVariant, transition: 'width .2s',
        }} />
      ))}
    </div>
  );
}

Object.assign(window, {
  AndroidShell, MD, AND_W, AND_H,
  MdTopBar, MdLinearProgress, MdCircularProgress,
  MdFilledButton, MdTextButton, MdOutlinedField, MdRadio, MdPageDots,
});
