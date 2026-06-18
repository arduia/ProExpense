// iPhone shell + bottom nav for Pro Expense prototype
// Custom shell — not the iOS 26 Liquid Glass starter, because we want a
// warm-paper background and matched status-bar tint.

const PHONE_W = 390;
const PHONE_H = 844;
const STATUS_H = 50;
const HOME_BAR_H = 34;

function PhoneShell({ children, bottomBar = null }) {
  return (
    <div
      style={{
        width: PHONE_W,
        height: PHONE_H,
        background: 'var(--paper)',
        borderRadius: 54,
        position: 'relative',
        overflow: 'hidden',
        boxShadow: '0 30px 60px rgba(0,0,0,0.32), 0 0 0 8px #1a1611, 0 0 0 9px #2a231a',
        fontFamily: 'var(--sans)',
      }}
    >
      {/* dynamic island */}
      <div style={{
        position: 'absolute', top: 11, left: '50%', transform: 'translateX(-50%)',
        width: 124, height: 36, borderRadius: 22, background: '#0a0807', zIndex: 50,
      }} />
      {/* status bar */}
      <div style={{
        position: 'absolute', top: 0, left: 0, right: 0,
        height: STATUS_H,
        display: 'flex', alignItems: 'center', justifyContent: 'space-between',
        padding: '18px 32px 0',
        fontSize: 15, fontWeight: 600, color: 'var(--ink)',
        zIndex: 10, pointerEvents: 'none',
      }}>
        <span>9:41</span>
        <span style={{ display: 'flex', gap: 6, alignItems: 'center' }}>
          {/* signal bars */}
          <svg width="18" height="11" viewBox="0 0 18 11"><rect x="0" y="7" width="3" height="4" rx="0.5" fill="currentColor"/><rect x="5" y="5" width="3" height="6" rx="0.5" fill="currentColor"/><rect x="10" y="3" width="3" height="8" rx="0.5" fill="currentColor"/><rect x="15" y="0" width="3" height="11" rx="0.5" fill="currentColor"/></svg>
          {/* battery */}
          <svg width="28" height="13" viewBox="0 0 28 13"><rect x="0.5" y="0.5" width="23" height="12" rx="3" fill="none" stroke="currentColor" strokeOpacity="0.4"/><rect x="2" y="2" width="20" height="9" rx="1.6" fill="currentColor"/><rect x="25" y="4" width="2" height="5" rx="0.5" fill="currentColor" opacity="0.4"/></svg>
        </span>
      </div>

      {/* content — fills to the bottom; CTAs/keypads carry their own safe-area
          padding, and bottom bars sit flush against the screen edge */}
      <div style={{ position: 'absolute', inset: 0, paddingTop: STATUS_H, paddingBottom: 8, display: 'flex', flexDirection: 'column' }}>
        <div className="proto-app" style={{ flex: 1, position: 'relative' }}>
          {children}
        </div>
      </div>

      {/* edge-to-edge bottom bar — pinned flush to the phone bottom, outside the
          padded content so it spans the full height incl. the safe area */}
      {bottomBar && (
        <div style={{ position: 'absolute', left: 0, right: 0, bottom: 8, zIndex: 40 }}>
          {bottomBar}
        </div>
      )}

      {/* home indicator */}
      <div style={{
        position: 'absolute', bottom: 8, left: '50%', transform: 'translateX(-50%)',
        width: 134, height: 5, borderRadius: 99, background: 'var(--ink)', opacity: 0.85, zIndex: 60,
      }} />
    </div>
  );
}

// Bottom tab bar — edge-to-edge glass sheet flush to the screen sides with
// 8dp top corners and a center Add that sits raised over the bar (matching
// the iOS Bottom Bar · Edge-to-edge variant).
function ProtoBottomNav({ active, onTab, onAdd }) {
  const tabs = [
    { id: 'home', label: 'Home', icon: 'home' },
    { id: 'budget', label: 'Budget', icon: 'budget' },
    { id: 'add', isAdd: true },
    { id: 'journal', label: 'Journal', icon: 'journal' },
    { id: 'more', label: 'More', icon: 'more' },
  ];
  const INACTIVE = '#8e8e93'; // iOS secondary label
  return (
    <div style={{
      borderTopLeftRadius: 8, borderTopRightRadius: 8,
      background: 'rgba(255,255,255,0.86)',
      backdropFilter: 'saturate(180%) blur(22px)',
      WebkitBackdropFilter: 'saturate(180%) blur(22px)',
      boxShadow: '0 -6px 24px rgba(0,0,0,0.10), inset 0 0.5px 0 rgba(255,255,255,0.7)',
      padding: '8px 12px 16px',
      display: 'flex',
      alignItems: 'center',
    }}>
      {tabs.map(t => {
        if (t.isAdd) {
          return (
            <div key="add" style={{ flex: 1, display: 'flex', justifyContent: 'center', position: 'relative' }}>
              <button
                type="button"
                className="proto-tap"
                onClick={onAdd}
                style={{
                  position: 'absolute',
                  top: -24,
                  left: '50%',
                  transform: 'translateX(-50%)',
                  width: 64, height: 64, borderRadius: 99,
                  background: 'var(--blue-500)',
                  color: 'var(--white)',
                  border: '3px solid rgba(3,155,229,0.8)',
                  display: 'flex', flexDirection: 'column', alignItems: 'center', justifyContent: 'center', gap: 1,
                  cursor: 'pointer',
                  zIndex: 5,
                }}
              >
                <Icon name="plus" size={26} stroke="var(--white)" strokeWidth={2.4} />
                <span style={{ fontSize: 10, fontWeight: 600, letterSpacing: '0.01em' }}>Add</span>
              </button>
              <div style={{ height: 42 }} />
            </div>
          );
        }
        const on = t.id === active;
        return (
          <button
            type="button"
            key={t.id}
            className="proto-tap"
            onClick={() => onTab && onTab(t.id)}
            style={{
              flex: 1, display: 'flex', flexDirection: 'column', alignItems: 'center', gap: 4,
              padding: '5px 0 3px',
              background: 'transparent', border: 'none',
              color: on ? 'var(--blue-500)' : INACTIVE,
              cursor: 'pointer',
            }}
          >
            <Icon name={t.icon} size={25} stroke="currentColor" strokeWidth={on ? 2.1 : 1.7} />
            <span style={{ fontSize: 10, fontWeight: on ? 600 : 500, letterSpacing: '0.01em' }}>{t.label}</span>
          </button>
        );
      })}
    </div>
  );
}

Object.assign(window, { PhoneShell, ProtoBottomNav, PHONE_W, PHONE_H });
