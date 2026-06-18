// android-onboarding.jsx — Android (Material 3) renditions of the Flow 04
// First-Launch + Profile-Setup screens, for the platform-comparison canvas.
// Mirrors the content of the iOS screens (static-screens-a / static-profile-setup)
// but uses Material chrome: splash w/ centered icon + circular spinner, Material
// filled buttons, outlined fields, radio selection, top app bar + linear progress.
//
// Reuses globals: AndroidShell, MD, Md* (android-frame.jsx), Icon, ONBOARD_ILLOS.

const AND_CURRENCIES = [
  { code: 'USD', sym: '$', name: 'US Dollar' },
  { code: 'EUR', sym: '€', name: 'Euro' },
  { code: 'GBP', sym: '£', name: 'British Pound' },
  { code: 'JPY', sym: '¥', name: 'Japanese Yen' },
  { code: 'INR', sym: '₹', name: 'Indian Rupee' },
  { code: 'AED', sym: 'د.إ', name: 'UAE Dirham' },
];

// ── Splash (Android 12+ splash spec: centered icon, bottom branding) ──
function AndSplash() {
  return (
    <AndroidShell>
      <div style={{ height: '100%', display: 'flex', flexDirection: 'column', alignItems: 'center', justifyContent: 'center', position: 'relative', background: MD.surface }}>
        <div style={{ width: 96, height: 96, borderRadius: 24, background: MD.primary, display: 'flex', alignItems: 'center', justifyContent: 'center', boxShadow: '0 8px 22px rgba(2,136,209,0.30)' }}>
          <svg width="96" height="96" viewBox="8 8 176 176" fill="none">
            <path fillRule="evenodd" d="M140,126V66a10,10,0,1,0-14-14H66A10,10,0,1,0,52,66v60a10,10,0,1,0,13.17,15h61.66A10,10,0,1,0,140,126Zm-14.63,3H66.63A9.91,9.91,0,0,0,64,126V66a9.39,9.39,0,0,0,2-2h60a9.39,9.39,0,0,0,2,2v60A9.91,9.91,0,0,0,125.37,129Z" fill="#fff"/>
          </svg>
        </div>
        {/* Material indeterminate spinner */}
        <div style={{ position: 'absolute', bottom: 116 }}>
          <MdCircularProgress size={36} stroke={3.5} />
        </div>
        {/* bottom branding wordmark (Android splash branding slot) */}
        <div style={{ position: 'absolute', bottom: 54, textAlign: 'center' }}>
          <div style={{ fontSize: 15, fontWeight: 600, color: MD.onSurface, letterSpacing: '0.2px' }}>Pro Expense</div>
          <div style={{ fontSize: 11.5, color: MD.onSurfaceVariant, marginTop: 3 }}>Your finance notebook</div>
        </div>
      </div>
    </AndroidShell>
  );
}

// ── Onboarding (5 slides) ────────────────────────────────────────────
function AndOnboarding({ slide = 1 }) {
  const slides = [
    { title: 'Welcome',      body: 'Your personal finance notebook.' },
    { title: 'Quick Log',    body: 'Log expenses in seconds — amount, category, done.' },
    { title: 'Shared Costs', body: 'Split bills instantly — total, people, done.' },
    { title: 'Event Budget', body: 'Plan and track any event budget — trips, weddings, parties.' },
    { title: 'Journal',      body: 'Review your spending like a diary, day by day.' },
  ];
  const idx = slide - 1;
  const isLast = slide === 5;
  const s = slides[idx];
  const Illo = ONBOARD_ILLOS[slide] || ONBOARD_ILLOS[1];

  return (
    <AndroidShell>
      <div style={{ height: '100%', display: 'flex', flexDirection: 'column', background: MD.surface }}>
        {/* top: skip text button (right) */}
        <div style={{ height: 56, display: 'flex', alignItems: 'center', justifyContent: 'flex-end', padding: '0 8px', flexShrink: 0 }}>
          {!isLast && <MdTextButton tone="muted">Skip</MdTextButton>}
        </div>

        {/* hero */}
        <div style={{ flex: 1, display: 'flex', flexDirection: 'column', alignItems: 'center', justifyContent: 'center', padding: '0 32px', textAlign: 'center' }}>
          <div style={{ width: 280, maxWidth: '100%', display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
            <Illo />
          </div>
          {/* Material headline + body */}
          <div style={{ fontSize: 28, fontWeight: 600, color: MD.onSurface, letterSpacing: '0', lineHeight: 1.15, marginTop: 30 }}>{s.title}</div>
          <div style={{ fontSize: 15, color: MD.onSurfaceVariant, lineHeight: 1.5, marginTop: 12, maxWidth: 280 }}>{s.body}</div>
        </div>

        {/* page indicator */}
        <div style={{ padding: '0 28px 22px' }}>
          <MdPageDots count={5} active={idx} />
        </div>

        {/* Material stepper row: Back (text) · Next (text) — lifted clear of CTA */}
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', padding: '0 16px', marginBottom: 16 }}>
          <span style={{ visibility: slide === 1 ? 'hidden' : 'visible' }}>
            <MdTextButton tone="muted"><Icon name="back" size={18} stroke={MD.onSurfaceVariant} strokeWidth={2} />Back</MdTextButton>
          </span>
          <span style={{ visibility: isLast ? 'hidden' : 'visible' }}>
            <MdTextButton>Next<Icon name="chevron-right" size={18} stroke={MD.primaryDeep} strokeWidth={2} /></MdTextButton>
          </span>
        </div>

        {/* primary CTA */}
        <div style={{ padding: '0 20px 22px' }}>
          <MdFilledButton>Get started</MdFilledButton>
        </div>
      </div>
    </AndroidShell>
  );
}

// ── Profile · 1 of 2 — name (outlined text field) ────────────────────
function AndProfileName() {
  return (
    <AndroidShell>
      <div style={{ height: '100%', display: 'flex', flexDirection: 'column', background: MD.surface }}>
        <MdTopBar title="Set up profile" />
        <div style={{ padding: '0 16px' }}><MdLinearProgress value={0.5} /></div>

        <div style={{ padding: '28px 24px 0' }}>
          <div style={{ fontSize: 24, fontWeight: 600, color: MD.onSurface, lineHeight: 1.2 }}>What should we call you?</div>
          <div style={{ fontSize: 14, color: MD.onSurfaceVariant, lineHeight: 1.5, marginTop: 10 }}>
            Your name personalizes the app and identifies your records and exports. No account needed — everything stays on your device.
          </div>

          <div style={{ marginTop: 30 }}>
            <MdOutlinedField label="Profile name" value="Maya" leading="user" focused helper="Used on your home screen and CSV exports." />
          </div>
        </div>

        <div style={{ marginTop: 'auto', padding: '0 20px 22px' }}>
          <MdFilledButton>Continue</MdFilledButton>
        </div>
      </div>
    </AndroidShell>
  );
}

// ── Profile · 2 of 2 — home currency (radio list) ────────────────────
function AndProfileCurrency() {
  return (
    <AndroidShell>
      <div style={{ height: '100%', display: 'flex', flexDirection: 'column', background: MD.surface }}>
        <MdTopBar title="Home currency" />
        <div style={{ padding: '0 16px' }}><MdLinearProgress value={1} /></div>

        <div style={{ padding: '20px 24px 6px' }}>
          <div style={{ fontSize: 14, color: MD.onSurfaceVariant, lineHeight: 1.5 }}>
            All entries default to this. You can still log in any currency per-expense.
          </div>
        </div>

        <div className="proto-scroll" style={{ flex: 1, padding: '6px 12px 0', overflow: 'auto' }}>
          {AND_CURRENCIES.map((c) => {
            const on = c.code === 'USD';
            return (
              <div key={c.code} style={{
                display: 'flex', alignItems: 'center', gap: 16, padding: '12px 12px',
                background: on ? MD.primaryContainer : 'transparent', borderRadius: 14, marginBottom: 2,
              }}>
                <div style={{ width: 40, height: 40, borderRadius: 99, background: on ? MD.surface : MD.surfaceVariant, display: 'flex', alignItems: 'center', justifyContent: 'center', fontSize: 18, fontWeight: 600, color: on ? MD.primaryDeep : MD.onSurfaceVariant }}>{c.sym}</div>
                <div style={{ flex: 1 }}>
                  <div style={{ fontSize: 16, fontWeight: 500, color: MD.onSurface }}>{c.code}</div>
                  <div style={{ fontSize: 13, color: MD.onSurfaceVariant }}>{c.name}</div>
                </div>
                <MdRadio checked={on} />
              </div>
            );
          })}
        </div>

        <div style={{ padding: '12px 20px 22px' }}>
          <MdFilledButton>Start tracking</MdFilledButton>
        </div>
      </div>
    </AndroidShell>
  );
}

// ── Currency picker — Material modal bottom sheet (alt state) ─────────
function AndProfileCurrencySheet() {
  return (
    <AndroidShell>
      {/* dimmed base */}
      <div style={{ height: '100%', display: 'flex', flexDirection: 'column', background: MD.surface }}>
        <MdTopBar title="Home currency" />
        <div style={{ padding: '0 16px' }}><MdLinearProgress value={1} /></div>
        <div style={{ flex: 1 }} />
      </div>
      {/* scrim */}
      <div style={{ position: 'absolute', inset: 0, background: 'rgba(0,0,0,0.32)', zIndex: 20 }} />
      {/* sheet */}
      <div style={{
        position: 'absolute', left: 0, right: 0, bottom: 0, zIndex: 21,
        background: MD.surface, borderTopLeftRadius: 28, borderTopRightRadius: 28,
        padding: '12px 0 0', maxHeight: '74%', display: 'flex', flexDirection: 'column',
        boxShadow: '0 -8px 28px rgba(0,0,0,0.18)',
      }}>
        {/* drag handle */}
        <div style={{ width: 32, height: 4, borderRadius: 99, background: MD.outlineVariant, margin: '0 auto 14px' }} />
        <div style={{ padding: '0 24px', fontSize: 18, fontWeight: 600, color: MD.onSurface, marginBottom: 14 }}>All currencies</div>
        {/* Material filled search field */}
        <div style={{ padding: '0 16px 8px' }}>
          <div style={{ display: 'flex', alignItems: 'center', gap: 12, height: 48, background: MD.surfaceVariant, borderRadius: 99, padding: '0 18px' }}>
            <Icon name="search" size={20} stroke={MD.onSurfaceVariant} strokeWidth={1.8} />
            <span style={{ fontSize: 15, color: MD.onSurfaceVariant }}>Search currency…</span>
          </div>
        </div>
        <div className="proto-scroll" style={{ flex: 1, overflow: 'auto', padding: '6px 8px 18px' }}>
          {AND_CURRENCIES.map((c) => (
            <div key={c.code} style={{ display: 'flex', alignItems: 'center', gap: 16, padding: '12px 16px', borderRadius: 14 }}>
              <div style={{ width: 40, height: 40, borderRadius: 99, background: MD.surfaceVariant, display: 'flex', alignItems: 'center', justifyContent: 'center', fontSize: 17, fontWeight: 600, color: MD.onSurfaceVariant }}>{c.sym}</div>
              <div style={{ flex: 1 }}>
                <div style={{ fontSize: 16, fontWeight: 500, color: MD.onSurface }}>{c.code}</div>
                <div style={{ fontSize: 13, color: MD.onSurfaceVariant }}>{c.name}</div>
              </div>
            </div>
          ))}
        </div>
      </div>
    </AndroidShell>
  );
}

Object.assign(window, { AndSplash, AndOnboarding, AndProfileName, AndProfileCurrency, AndProfileCurrencySheet });
