// Pro Expense — component capture registry
// Renders ONE component (or state group) centered on paper so each can be
// screenshotted in isolation for the spec docs. Mirrors the shipped
// implementations in proto-ui.jsx / proto-phone.jsx / flow screens.

const cap = {
  card: { background: 'var(--card)', border: '1px solid var(--line)', borderRadius: 16, boxShadow: '0 1px 0 rgba(33,33,33,0.03), 0 6px 16px rgba(33,33,33,0.04)' },
  mono: { fontFamily: 'var(--mono)' },
  label: { fontFamily: 'var(--mono)', fontSize: 10.5, textTransform: 'uppercase', letterSpacing: '0.12em', color: 'var(--ink-3)', fontWeight: 600, marginBottom: 12 },
};

// ── local primitives mirrored from the live build ──
function TxnRow({ cat, note, meta, amount, tag }) {
  return (
    <div style={{ display: 'flex', alignItems: 'center', gap: 12, padding: '12px 8px', borderBottom: '1px solid var(--line-2)' }}>
      <CatBadge cat={cat} size={38} />
      <div style={{ flex: 1, minWidth: 0 }}>
        <div style={{ fontSize: 14, color: 'var(--ink)', fontWeight: 500 }}>{note}</div>
        <div style={{ fontSize: 11.5, color: 'var(--muted)', display: 'flex', gap: 6, alignItems: 'center' }}>
          <span>{meta}</span>
          {tag && (<><span>·</span><span style={{ color: 'var(--tag)', display: 'inline-flex', alignItems: 'center', gap: 2 }}><Icon name="at" size={10} stroke="var(--tag)" strokeWidth={2} />{tag}</span></>)}
        </div>
      </div>
      <div style={{ fontFamily: 'var(--serif)', fontSize: 18, color: 'var(--ink)' }}>{amount}</div>
    </div>
  );
}

function DayGroup() {
  return (
    <div style={{ ...cap.card, padding: '6px 14px', width: 360 }}>
      <div style={{ display: 'flex', alignItems: 'baseline', justifyContent: 'space-between', padding: '12px 8px 6px' }}>
        <span style={{ fontFamily: 'var(--serif)', fontSize: 18, letterSpacing: '-0.01em' }}>Today · May 25</span>
        <span style={{ fontFamily: 'var(--mono)', fontSize: 12, color: 'var(--muted)' }}>$42</span>
      </div>
      <TxnRow cat="food" note="Lunch with M." meta="Food · 12:30 PM" amount="$12.40" />
      <TxnRow cat="coffee" note="Oat latte" meta="Coffee runs · 08:40 AM" amount="$5.00" />
      <TxnRow cat="entertainment" note="Movie · Dune" meta="Entertainment" amount="$18.00" tag="Bali Trip" />
    </div>
  );
}

function FilterChip({ label, active }) {
  return (
    <div style={{ padding: '6px 12px', border: '1px solid ' + (active ? 'var(--ink)' : 'var(--line-strong)'), borderRadius: 99, fontSize: 12, fontWeight: active ? 600 : 500, background: active ? 'var(--ink)' : 'transparent', color: active ? 'var(--paper-warm)' : 'var(--ink-2)', whiteSpace: 'nowrap' }}>{label}</div>
  );
}

function SearchField({ filled }) {
  return (
    <div style={{ width: 320, padding: '12px 14px', background: 'var(--card)', border: '1px solid var(--line)', borderRadius: 14, display: 'flex', alignItems: 'center', gap: 10 }}>
      <Icon name="search" size={16} stroke="var(--muted)" strokeWidth={1.7} />
      <span style={{ fontSize: 13, color: filled ? 'var(--ink)' : 'var(--muted)' }}>{filled ? 'Lunch' : 'Search notes, amount, category…'}</span>
      {filled && <span style={{ marginLeft: 'auto' }}><Icon name="close" size={15} stroke="var(--muted)" strokeWidth={1.8} /></span>}
    </div>
  );
}

function KpKey({ k }) {
  return (
    <div style={{ background: 'var(--card)', border: '1px solid var(--line)', borderRadius: 12, padding: '13px 0', textAlign: 'center', fontSize: 22, fontFamily: 'var(--serif)', color: 'var(--ink)' }}>
      {k === '⌫' ? <Icon name="back" size={18} stroke="var(--ink-2)" strokeWidth={1.8} /> : k}
    </div>
  );
}

function Keypad() {
  const keys = ['1','2','3','4','5','6','7','8','9','.','0','⌫'];
  return (
    <div style={{ ...cap.card, padding: 16, width: 300 }}>
      <div style={{ display: 'grid', gridTemplateColumns: 'repeat(3, 1fr)', gap: 8 }}>
        {keys.map((k) => <KpKey key={k} k={k} />)}
      </div>
      <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: 8, marginTop: 8 }}>
        <div style={{ background: 'transparent', color: 'var(--ink)', border: '1.4px solid var(--line-strong)', borderRadius: 14, padding: '14px 0', textAlign: 'center', fontSize: 14, fontWeight: 600 }}>Save</div>
        <div style={{ background: 'var(--clay)', color: '#fffdf6', borderRadius: 14, padding: '14px 0', textAlign: 'center', fontSize: 15, fontWeight: 600, display: 'flex', alignItems: 'center', justifyContent: 'center', gap: 4, boxShadow: '0 4px 10px rgba(3,155,229,0.25)' }}>
          Next<Icon name="chevron-right" size={18} stroke="#fffdf6" strokeWidth={2} />
        </div>
      </div>
    </div>
  );
}

function QuickAccess({ label, icon, tint, stroke }) {
  return (
    <div style={{ display: 'flex', flexDirection: 'column', alignItems: 'center', gap: 7, padding: '12px 4px', background: 'var(--card)', border: '1px solid var(--line)', borderRadius: 14, width: 78 }}>
      <div style={{ width: 36, height: 36, borderRadius: 11, background: tint, display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
        <Icon name={icon} size={18} stroke={stroke} strokeWidth={1.8} />
      </div>
      <span style={{ fontSize: 11, color: 'var(--ink-2)', fontWeight: 500 }}>{label}</span>
    </div>
  );
}

function SpentCard() {
  return (
    <div style={{ background: 'var(--card)', borderRadius: 18, padding: 18, width: 320, boxShadow: '0 1px 0 rgba(43,31,23,0.04), 0 6px 14px rgba(43,31,23,0.04)', border: '1px solid var(--line)' }}>
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start' }}>
        <div>
          <div style={{ fontFamily: 'var(--mono)', fontSize: 10.5, textTransform: 'uppercase', letterSpacing: '0.08em', color: 'var(--ink-3)', fontWeight: 600 }}>Spent today</div>
          <div style={{ fontFamily: 'var(--serif)', fontSize: 40, letterSpacing: '-0.02em', color: 'var(--ink)', marginTop: 4 }}>$42.00</div>
        </div>
        <svg width="86" height="40" viewBox="0 0 86 40" fill="none"><path d="M2 30 L14 22 L26 26 L38 14 L50 18 L62 10 L74 14 L84 6" stroke="var(--clay)" strokeWidth="1.6" strokeLinecap="round" strokeLinejoin="round"/><circle cx="84" cy="6" r="3" fill="var(--clay)"/></svg>
      </div>
    </div>
  );
}

// ── capture registry: key → element (sized to fit) ──
const CAPTURES = {
  // BUTTONS
  'button-variants': (
    <div style={{ display: 'grid', gridTemplateColumns: 'repeat(3, auto)', gap: 16, alignItems: 'center' }}>
      <Button variant="primary">Primary</Button>
      <Button variant="primary-deep">Primary deep</Button>
      <Button variant="sage">Confirm</Button>
      <Button variant="dark">Dark</Button>
      <Button variant="secondary">Secondary</Button>
      <Button variant="ghost">Ghost</Button>
    </div>
  ),
  'button-sizes': (
    <div style={{ display: 'flex', gap: 14, alignItems: 'center' }}>
      <Button variant="primary" size="sm">Small</Button>
      <Button variant="primary" size="md">Medium</Button>
      <Button variant="primary" size="lg">Large</Button>
    </div>
  ),
  'button-states': (
    <div style={{ display: 'flex', gap: 18, alignItems: 'center' }}>
      <Button variant="primary">Enabled</Button>
      <Button variant="primary" disabled>Disabled</Button>
    </div>
  ),
  // CATEGORY
  'cat-badges': (
    <div style={{ display: 'flex', flexWrap: 'wrap', gap: 26, maxWidth: 520, justifyContent: 'center' }}>
      {[...DEFAULT_CATEGORY_IDS, ...CUSTOM_CATEGORY_IDS].map(id => (
        <div key={id} style={{ display: 'flex', flexDirection: 'column', alignItems: 'center', gap: 8, width: 78 }}>
          <CatBadge cat={id} size={48} />
          <span style={{ fontSize: 11.5, color: 'var(--ink-2)', fontWeight: 500 }}>{CATEGORIES[id].label}</span>
          <span style={{ fontFamily: 'var(--mono)', fontSize: 9.5, color: 'var(--muted)' }}>{CATEGORIES[id].color}</span>
        </div>
      ))}
    </div>
  ),
  'cat-chips': (
    <div style={{ display: 'flex', flexWrap: 'wrap', gap: 10, maxWidth: 460, justifyContent: 'center' }}>
      {[...DEFAULT_CATEGORY_IDS, ...CUSTOM_CATEGORY_IDS].map((id, i) => <CatChip key={id} cat={id} selected={i % 3 === 0} onClick={() => {}} />)}
    </div>
  ),
  // FILTER CHIPS
  'filter-chips': (
    <div style={{ display: 'flex', gap: 6, flexWrap: 'wrap', maxWidth: 360, justifyContent: 'center' }}>
      <FilterChip label="All" active />
      <FilterChip label="Food" />
      <FilterChip label="Transport" />
      <FilterChip label="Bills" />
      <FilterChip label="More" />
    </div>
  ),
  // DATA
  'txn-daygroup': <DayGroup />,
  'spent-card': <SpentCard />,
  'keypad': <Keypad />,
  'search-empty': <SearchField filled={false} />,
  'search-filled': <SearchField filled={true} />,
  'quick-access': (
    <div style={{ display: 'flex', gap: 12 }}>
      <QuickAccess label="Reports" icon="feat-reports" tint="var(--blue-100)" stroke="var(--blue-700)" />
      <QuickAccess label="Debts" icon="feat-debt" tint="#c8e6c9" stroke="var(--green-500)" />
      <QuickAccess label="Split" icon="feat-split" tint="var(--tag-tint)" stroke="var(--tag-deep)" />
      <QuickAccess label="Events" icon="feat-events" tint="var(--yellow-300)" stroke="#f9a825" />
    </div>
  ),
  // VALIDATION
  'validation': (
    <div style={{ ...cap.card, padding: 22, width: 260, textAlign: 'center' }}>
      <div style={{ fontFamily: 'var(--serif)', fontSize: 44, color: 'var(--muted-2)', letterSpacing: '-0.025em' }}><span style={{ fontSize: 22, color: 'var(--clay)' }}>$</span>0</div>
      <div style={{ fontSize: 12, color: 'var(--clay)', fontWeight: 500, marginTop: 4 }}>Amount must be greater than $0</div>
    </div>
  ),
  // NAV (real-capture only — backdrop blur)
  'nav-home': (
    <div style={{ width: 390, paddingTop: 34 }}>
      <ProtoBottomNav active="home" />
    </div>
  ),
  'nav-budget': (
    <div style={{ width: 390, paddingTop: 34 }}>
      <ProtoBottomNav active="budget" />
    </div>
  ),
  // SHEET
  'bottom-sheet': (
    <div style={{ position: 'relative', width: 360, height: 200, borderRadius: 16, overflow: 'hidden', background: 'rgba(43,31,23,0.42)', border: '1px solid var(--line)' }}>
      <div style={{ position: 'absolute', left: 0, right: 0, bottom: 0, background: 'var(--card)', borderTopLeftRadius: 22, borderTopRightRadius: 22, boxShadow: '0 -8px 24px rgba(0,0,0,0.15)', padding: '0 20px 20px' }}>
        <div style={{ width: 36, height: 4, borderRadius: 99, background: 'rgba(43,31,23,0.18)', margin: '10px auto 14px' }} />
        <div style={{ fontFamily: 'var(--serif)', fontSize: 18 }}>Edit expense</div>
        <div style={{ display: 'flex', gap: 10, marginTop: 14 }}>
          <Button variant="ghost" size="md" style={{ flex: 1 }}>Cancel</Button>
          <Button variant="primary" size="md" style={{ flex: 1 }}>Save</Button>
        </div>
      </div>
    </div>
  ),
  // TOAST
  'toast': (
    <div style={{ background: 'var(--ink)', color: 'var(--paper-warm)', padding: '10px 16px', borderRadius: 99, fontSize: 13, display: 'flex', alignItems: 'center', gap: 8, whiteSpace: 'nowrap', boxShadow: '0 8px 18px rgba(0,0,0,0.18)' }}>
      <Icon name="check" size={16} stroke="var(--green-300)" strokeWidth={2.2} />
      Expense saved
    </div>
  ),
  // ICONS
  'icons': (
    <div style={{ display: 'grid', gridTemplateColumns: 'repeat(8, 1fr)', gap: 10, width: 560 }}>
      {['home','budget','journal','more','plus','minus','back','close','chevron-down','chevron-right','search','bell','check','sparkle','at','calendar','clock','note','user','eye','fingerprint','feat-reports','feat-debt','feat-split'].map(n => (
        <div key={n} style={{ ...cap.card, display: 'flex', flexDirection: 'column', alignItems: 'center', gap: 8, padding: '16px 6px' }}>
          <Icon name={n} size={24} stroke="var(--ink)" strokeWidth={1.6} />
          <span style={{ fontFamily: 'var(--mono)', fontSize: 9, color: 'var(--ink-3)', textAlign: 'center' }}>{n}</span>
        </div>
      ))}
    </div>
  ),
};

window.__captureKeys = Object.keys(CAPTURES);
window.CAPTURES = CAPTURES;

function showCapture(key) {
  const stage = document.getElementById('stage');
  if (!stage) return;
  const root = window.__capRoot || (window.__capRoot = ReactDOM.createRoot(stage));
  root.render(CAPTURES[key] || <div style={{ color: 'red' }}>missing: {key}</div>);
}
window.showCapture = showCapture;

// default render so the harness page isn't blank (no-op on the gallery page)
if (document.getElementById('stage')) showCapture('button-variants');
