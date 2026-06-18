// iOS bottom bar — single direction: edge-to-edge, top-rounded corners,
// embedded center Add (sits flush in a socket, not raised). Shown over sample
// screen content. Reuses global Icon (proto-ui.jsx).

const INACTIVE = '#8e8e93';
const BLUE = 'var(--blue-500)';
const TABS_L = [{ icon: 'home', label: 'Home', on: true }, { icon: 'budget', label: 'Budget' }];
const TABS_R = [{ icon: 'journal', label: 'Journal' }, { icon: 'more', label: 'More' }];

function Tab({ icon, label, on }) {
  return (
    <div style={{ flex: 1, display: 'flex', flexDirection: 'column', alignItems: 'center', gap: 4, padding: '5px 0 3px', color: on ? BLUE : INACTIVE }}>
      <Icon name={icon} size={25} stroke="currentColor" strokeWidth={on ? 2.1 : 1.7} />
      <span style={{ fontSize: 10, fontWeight: on ? 600 : 500, letterSpacing: '0.01em' }}>{label}</span>
    </div>
  );
}

// Edge-to-edge tab bar: full-width glass sheet with 8dp top corners and a
// large center Add raised high above the bar.
function EdgeBar() {
  return (
    <div style={{
      borderTopLeftRadius: 8, borderTopRightRadius: 8,
      background: 'rgba(255,255,255,0.86)',
      backdropFilter: 'saturate(180%) blur(22px)',
      WebkitBackdropFilter: 'saturate(180%) blur(22px)',
      boxShadow: '0 -6px 24px rgba(0,0,0,0.10), inset 0 0.5px 0 rgba(255,255,255,0.7)',
      padding: '12px 12px 8px',
      display: 'flex', flexDirection: 'column',
    }}>
      <div style={{ display: 'flex', alignItems: 'center' }}>
        {TABS_L.map((t, i) => <Tab key={i} {...t} />)}
        {/* center Add — larger circle, raised over the bar, 3dp blue (80%) border */}
        <div style={{ flex: 1, display: 'flex', justifyContent: 'center', position: 'relative' }}>
          <div style={{
            position: 'absolute', top: -24, left: '50%', transform: 'translateX(-50%)',
            display: 'flex', flexDirection: 'column', alignItems: 'center', justifyContent: 'center', gap: 1,
            width: 64, height: 64, borderRadius: 99,
            background: BLUE, border: '3px solid rgba(3,155,229,0.8)',
          }}>
            <Icon name="plus" size={26} stroke="#fff" strokeWidth={2.4} />
            <span style={{ fontSize: 10, fontWeight: 600, color: '#fff', letterSpacing: '0.01em' }}>Add</span>
          </div>
          <div style={{ height: 42 }} />
        </div>
        {TABS_R.map((t, i) => <Tab key={i} {...t} />)}
      </div>
      {/* home indicator inside the sheet */}
      <div style={{ width: 132, height: 5, borderRadius: 99, background: 'var(--ink)', opacity: 0.22, margin: '8px auto 0' }} />
    </div>
  );
}

// Bottom of a screen with sample content behind the bar (flush to edges).
function BarFrame({ children }) {
  return (
    <div style={{ position: 'relative', width: 390, height: 540, borderRadius: 40, overflow: 'hidden', background: 'var(--paper)', boxShadow: 'inset 0 0 0 0.5px rgba(0,0,0,0.06)' }}>
      <div style={{ position: 'absolute', inset: 0, padding: '26px 22px 0' }}>
        <div style={{ fontSize: 22, fontWeight: 700, color: 'var(--ink)', fontFamily: 'var(--serif)' }}>Recent</div>
        <div style={{ marginTop: 16, display: 'flex', flexDirection: 'column', gap: 12 }}>
          {[['Lunch — Sweetgreen', '12.50', 'cat-food'], ['Metro card', '40.00', 'cat-transport'], ['Coffee', '4.20', 'cat-coffee'], ['Groceries', '63.80', 'cat-shopping'], ['Pharmacy', '18.00', 'cat-health'], ['Movie night', '24.00', 'cat-entertainment']].map((r, i) => (
            <div key={i} style={{ display: 'flex', alignItems: 'center', gap: 14, background: 'var(--card)', border: '1px solid var(--line)', borderRadius: 16, padding: '12px 16px' }}>
              <div style={{ width: 40, height: 40, borderRadius: 12, background: 'var(--blue-100)', display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
                <Icon name={r[2]} size={22} stroke="var(--blue-700)" strokeWidth={1.7} />
              </div>
              <div style={{ flex: 1 }}>
                <div style={{ fontSize: 15, fontWeight: 600, color: 'var(--ink)' }}>{r[0]}</div>
                <div style={{ fontSize: 12.5, color: 'var(--muted)', marginTop: 2 }}>Today</div>
              </div>
              <div style={{ fontSize: 15, fontWeight: 700, color: 'var(--ink)' }}>${r[1]}</div>
            </div>
          ))}
        </div>
      </div>
      <div style={{ position: 'absolute', left: 0, right: 0, bottom: 0 }}>{children}</div>
    </div>
  );
}

const AB = { background: 'transparent', borderRadius: 40, overflow: 'visible', boxShadow: 'none', padding: 0 };

function VariantsApp() {
  return (
    <DesignCanvas accent="#039be5" background="#1f1a14">
      <DCSection id="bars" title="iOS Bottom Bar · Center Add" subtitle="Edge-to-edge · 8dp top corners · center Add as a flush filled tab cell, no elevation.">
        <DCArtboard id="edge" label="Edge-to-edge · filled Add cell" width={390} height={540} style={AB}>
          <BarFrame><EdgeBar /></BarFrame>
        </DCArtboard>
      </DCSection>
    </DesignCanvas>
  );
}

if (!window.__noCanvas) {
  ReactDOM.createRoot(document.getElementById('root')).render(<VariantsApp />);
}

Object.assign(window, { EdgeBar, BarFrame, VariantsApp });
