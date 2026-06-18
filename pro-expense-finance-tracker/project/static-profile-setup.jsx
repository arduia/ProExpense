// Flow 04 (cont.) — Profile Setup wizard · first-run, between Onboarding and Home
// Account-free, local-only: name, home currency, default category, optional PIN.
// Reuses PhoneShell, Icon, Button, CatChip, CATEGORIES, DEFAULT_CATEGORY_IDS,
// EdgeBottomSheet — all global.

const CURRENCIES = [
  { code: 'USD', sym: '$', name: 'US Dollar' },
  { code: 'EUR', sym: '€', name: 'Euro' },
  { code: 'GBP', sym: '£', name: 'British Pound' },
  { code: 'JPY', sym: '¥', name: 'Japanese Yen' },
  { code: 'INR', sym: '₹', name: 'Indian Rupee' },
  { code: 'AED', sym: 'د.إ', name: 'UAE Dirham' },
];

// Shared wizard chrome — progress dots + skip
function WizHeader({ step, total = 4, onSkip = true }) {
  return (
    <div style={{ padding: '16px 22px 4px', display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
      <div style={{ display: 'flex', gap: 6 }}>
        {Array.from({ length: total }).map((_, i) => (
          <div key={i} style={{
            width: i + 1 === step ? 22 : 6, height: 6, borderRadius: 99,
            background: i + 1 === step ? 'var(--blue-500)' : i + 1 < step ? 'var(--blue-300)' : 'var(--gray-300)',
            transition: 'width .2s',
          }} />
        ))}
      </div>
      {onSkip && <span style={{ fontSize: 13, color: 'var(--muted)' }}>Skip</span>}
    </div>
  );
}

function WizTitle({ kicker, children, sub }) {
  return (
    <div style={{ padding: '0 26px', marginTop: 8 }}>
      {kicker && <div style={{ fontSize: 11, fontFamily: 'var(--mono)', textTransform: 'uppercase', letterSpacing: '0.08em', color: 'var(--blue-700)', fontWeight: 600, marginBottom: 8 }}>{kicker}</div>}
      <div style={{ fontFamily: 'var(--serif)', fontSize: 30, lineHeight: 1.08, letterSpacing: '-0.015em', color: 'var(--ink)' }}>{children}</div>
      {sub && <div style={{ marginTop: 8, fontSize: 14, color: 'var(--ink-2)', lineHeight: 1.45 }}>{sub}</div>}
    </div>
  );
}

// Step 1 — Name / nickname
function ProfileName() {
  return (
    <PhoneShell>
      <div style={{ height: '100%', display: 'flex', flexDirection: 'column' }}>
        <WizHeader step={1} total={2} />
        <WizTitle kicker="Profile · 1 of 2" sub="Your name personalizes the app — greeting you on the home screen and identifying your records and exports. No account needed; everything stays on your device.">Set up your profile</WizTitle>
        <div style={{ padding: '30px 26px 0' }}>
          <label style={{ display: 'block', fontSize: 11, fontFamily: 'var(--mono)', textTransform: 'uppercase', letterSpacing: '0.08em', color: 'var(--blue-700)', fontWeight: 600, marginBottom: 8 }}>Profile name</label>
          <div style={{
            display: 'flex', alignItems: 'center', gap: 12,
            background: 'var(--card)', border: '1.4px solid var(--blue-500)',
            borderRadius: 14, padding: '0 16px',
            boxShadow: '0 0 0 4px var(--blue-100)',
          }}>
            <Icon name="user" size={20} stroke="var(--blue-700)" strokeWidth={1.8} style={{ flexShrink: 0 }} />
            <input
              type="text"
              defaultValue="Maya"
              placeholder="e.g. Maya"
              style={{
                flex: 1, border: 'none', outline: 'none', background: 'transparent',
                fontFamily: 'var(--sans)', fontSize: 17, color: 'var(--ink)',
                padding: '15px 0', minWidth: 0,
              }}
            />
            <span className="proto-cursor" style={{ position: 'relative', left: -2 }} />
          </div>
          <div style={{ marginTop: 8, fontSize: 12, color: 'var(--muted)' }}>Used on your home screen and CSV exports.</div>
        </div>
        <div style={{ marginTop: 'auto', padding: '0 22px 20px' }}>
          <Button variant="primary" size="lg" fullWidth>Continue</Button>
        </div>
      </div>
    </PhoneShell>
  );
}

// Step 2 — Home currency (selected list)
function ProfileCurrency() {
  return (
    <PhoneShell>
      <div style={{ height: '100%', display: 'flex', flexDirection: 'column' }}>
        <WizHeader step={2} total={2} />
        <WizTitle kicker="Profile · 2 of 2" sub="All entries default to this. You can still log in any currency per-expense.">Pick your home currency</WizTitle>
        <div className="proto-scroll" style={{ flex: 1, padding: '22px 22px 0' }}>
          <div style={{ display: 'flex', flexDirection: 'column', gap: 8 }}>
            {CURRENCIES.map((c, i) => {
              const on = c.code === 'USD';
              return (
                <div key={c.code} style={{
                  display: 'flex', alignItems: 'center', gap: 14, padding: '13px 16px',
                  background: on ? 'var(--blue-100)' : 'var(--card)',
                  border: '1.4px solid ' + (on ? 'var(--blue-500)' : 'var(--line)'),
                  borderRadius: 14,
                }}>
                  <div style={{ width: 38, height: 38, borderRadius: 99, background: on ? 'var(--white)' : 'var(--gray-100)', display: 'flex', alignItems: 'center', justifyContent: 'center', fontFamily: 'var(--serif)', fontSize: 19, color: on ? 'var(--blue-700)' : 'var(--ink-2)' }}>{c.sym}</div>
                  <div style={{ flex: 1 }}>
                    <div style={{ fontSize: 15, fontWeight: 500, color: 'var(--ink)' }}>{c.code}</div>
                    <div style={{ fontSize: 12, color: 'var(--muted)' }}>{c.name}</div>
                  </div>
                  {on && <Icon name="check" size={20} stroke="var(--blue-700)" strokeWidth={2.4} />}
                </div>
              );
            })}
          </div>
        </div>
        <div style={{ padding: '14px 22px 20px' }}>
          <Button variant="primary" size="lg" fullWidth>Start tracking</Button>
        </div>
      </div>
    </PhoneShell>
  );
}

// Step 3 — Default category
function ProfileCategory() {
  return (
    <PhoneShell>
      <div style={{ height: '100%', display: 'flex', flexDirection: 'column' }}>
        <WizHeader step={3} />
        <WizTitle kicker="Profile · 3 of 4" sub="This is pre-selected each time you add an expense — change it anytime.">Set a default category</WizTitle>
        <div className="proto-scroll" style={{ flex: 1, padding: '24px 22px 0' }}>
          <div style={{ display: 'grid', gridTemplateColumns: 'repeat(3, 1fr)', gap: 10 }}>
            {DEFAULT_CATEGORY_IDS.map(id => {
              const cat = CATEGORIES[id];
              const on = id === 'food';
              return (
                <div key={id} style={{
                  display: 'flex', flexDirection: 'column', alignItems: 'center', gap: 8, padding: '16px 8px',
                  background: on ? cat.tint : 'var(--card)',
                  border: '1.4px solid ' + (on ? cat.color : 'var(--line)'),
                  borderRadius: 16,
                }}>
                  <CatBadge cat={id} size={44} />
                  <span style={{ fontSize: 12.5, color: on ? cat.color : 'var(--ink-2)', fontWeight: on ? 600 : 500 }}>{cat.label}</span>
                </div>
              );
            })}
          </div>
        </div>
        <div style={{ padding: '14px 22px 20px' }}>
          <Button variant="primary" size="lg" fullWidth>Continue</Button>
        </div>
      </div>
    </PhoneShell>
  );
}

// Step 4 — Enable PIN? (links to existing PIN Setup, or skip to Home)
function ProfilePin() {
  return (
    <PhoneShell>
      <div style={{ height: '100%', display: 'flex', flexDirection: 'column' }}>
        <WizHeader step={4} onSkip={false} />
        <div style={{ flex: 1, display: 'flex', flexDirection: 'column', alignItems: 'center', justifyContent: 'center', padding: '0 34px', textAlign: 'center' }}>
          <div style={{ width: 92, height: 92, borderRadius: 26, background: 'var(--blue-100)', display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
            <Icon name="eye" size={44} stroke="var(--blue-700)" strokeWidth={1.5} />
          </div>
          <div style={{ marginTop: 22, fontFamily: 'var(--serif)', fontSize: 27, letterSpacing: '-0.015em', lineHeight: 1.1 }}>Lock the app with a PIN?</div>
          <div style={{ marginTop: 10, fontSize: 14, color: 'var(--ink-2)', lineHeight: 1.5 }}>
            Add a 6-digit PIN to keep your finances private. Optional — you can enable it later in More.
          </div>
        </div>
        <div style={{ padding: '0 22px 20px', display: 'flex', flexDirection: 'column', gap: 10 }}>
          <Button variant="primary" size="lg" fullWidth>Set up PIN</Button>
          <button style={{ background: 'transparent', border: 'none', fontSize: 14, color: 'var(--muted)', fontWeight: 500, cursor: 'pointer', padding: 6 }}>Not now — go to Home</button>
        </div>
      </div>
    </PhoneShell>
  );
}

// Currency picker sheet (alt state)
function ProfileCurrencySheet() {
  return (
    <PhoneShell>
      <div style={{ height: '100%', display: 'flex', flexDirection: 'column' }}>
        <WizHeader step={2} total={2} />
        <WizTitle kicker="Profile · 2 of 2">Pick your home currency</WizTitle>
      </div>
      <EdgeBottomSheet title="All currencies" height={520}>
        <div style={{ display: 'flex', alignItems: 'center', gap: 10, padding: '11px 14px', background: 'var(--card)', border: '1.4px solid var(--blue-500)', borderRadius: 13, marginBottom: 14 }}>
          <Icon name="search" size={17} stroke="var(--blue-500)" strokeWidth={1.8} />
          <span style={{ fontSize: 14, color: 'var(--muted)', flex: 1 }}>Search currency…</span>
        </div>
        <div style={{ display: 'flex', flexDirection: 'column', gap: 6 }}>
          {CURRENCIES.map((c) => (
            <div key={c.code} style={{ display: 'flex', alignItems: 'center', gap: 12, padding: '11px 14px', background: 'var(--card)', border: '1px solid var(--line)', borderRadius: 12 }}>
              <div style={{ width: 34, height: 34, borderRadius: 99, background: 'var(--gray-100)', display: 'flex', alignItems: 'center', justifyContent: 'center', fontFamily: 'var(--serif)', fontSize: 17, color: 'var(--ink-2)' }}>{c.sym}</div>
              <div style={{ flex: 1 }}>
                <div style={{ fontSize: 14.5, fontWeight: 500, color: 'var(--ink)' }}>{c.code}</div>
                <div style={{ fontSize: 11.5, color: 'var(--muted)' }}>{c.name}</div>
              </div>
            </div>
          ))}
        </div>
      </EdgeBottomSheet>
    </PhoneShell>
  );
}

Object.assign(window, { ProfileName, ProfileCurrency, ProfileCategory, ProfilePin, ProfileCurrencySheet });
