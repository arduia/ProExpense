// Static-design wrappers around Flow 01 interactive screens.
// We pass inert handlers + frozen draft to render them in fixed states.

function StaticHome({ persona = 'casual', expenses, withNewRow = false }) {
  const seed = expenses || [
    { id: 'e1', amount: 12.40, category: 'food',          note: 'Lunch with M.', date: 'Today', time: '12:30 PM', tag: null },
    { id: 'e2', amount: 3.50,  category: 'transport',     note: '',              date: 'Today', time: '09:15 AM', tag: null },
    { id: 'e3', amount: 5.00,  category: 'coffee',        note: 'Oat latte',     date: 'Today', time: '08:40 AM', tag: null },
    { id: 'e4', amount: 18.00, category: 'entertainment', note: 'Movie · Dune',  date: 'Yesterday', time: '08:10 PM', tag: { type: 'event', id: 'bali' } },
    { id: 'e5', amount: 42.00, category: 'food',          note: 'Groceries',     date: 'Yesterday', time: '05:30 PM', tag: null },
  ];
  return (
    <PhoneShell>
      <div style={{ height: '100%', display: 'flex', flexDirection: 'column' }}>
        <HomeScreen
          navDir="static"
          expenses={seed}
          persona={persona}
          newRowId={withNewRow ? seed[0].id : null}
          activeNav="home"
          onTab={() => {}}
          onAdd={() => {}}
          onPersona={() => {}}
        />
        <ProtoBottomNav active="home" onTab={() => {}} onAdd={() => {}} />
      </div>
    </PhoneShell>
  );
}

function StaticAddAmount({ amount = '12.50', category = 'food', shake = false }) {
  const draft = { amount, category, note: '', date: 'Today, May 25', time: '12:30 PM', tag: null };
  return (
    <PhoneShell>
      <AddAmountScreen
        navDir="static"
        draft={draft}
        amountValue={amountToFloat(amount)}
        shake={shake}
        canProceed={amountToFloat(amount) > 0}
        onKey={() => {}}
        setCategory={() => {}}
        onCancel={() => {}}
        onNext={() => {}}
        onQuickSave={() => {}}
        onMore={() => {}}
      />
    </PhoneShell>
  );
}

function StaticAddDetails({ amount = '12.50', category = 'food', note = "Lunch with M.", tag = { type: 'event', id: 'bali' } }) {
  const draft = { amount, category, note, date: 'Today, May 25', time: '12:30 PM', timeIsFuture: false, tag };
  return (
    <PhoneShell>
      <AddDetailsScreen
        navDir="static"
        draft={draft}
        setDraft={() => {}}
        amountValue={amountToFloat(amount)}
        onBack={() => {}}
        onSave={() => {}}
        onEditDateTime={() => {}}
        onOpenTag={() => {}}
        onClearTag={() => {}}
        showTagPicker
        onMore={() => {}}
      />
    </PhoneShell>
  );
}

function StaticHomeEmpty() {
  return (
    <PhoneShell>
      <div style={{ height: '100%', display: 'flex', flexDirection: 'column' }}>
        <div style={{ flex: 1, display: 'flex', flexDirection: 'column' }}>
          {/* header */}
          <div style={{ padding: '14px 22px 0' }}>
            <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
              <div>
                <div style={{ fontSize: 11, color: 'var(--muted)', fontFamily: 'var(--mono)', letterSpacing: '0.06em', textTransform: 'uppercase' }}>Wed · May 25</div>
                <div style={{ fontFamily: 'var(--serif)', fontSize: 30, lineHeight: 1.05, letterSpacing: '-0.015em', marginTop: 4 }}>
                  Welcome, <em style={{ color: 'var(--blue-500)', fontStyle: 'italic' }}>Maya</em>
                </div>
              </div>
              <div style={{ width: 40, height: 40, borderRadius: 99, border: '1px solid var(--line)', background: 'var(--card)', display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
                <Icon name="bell" size={18} stroke="var(--ink-2)" strokeWidth={1.6} />
              </div>
            </div>

            {/* zero-state summary card */}
            <div style={{ marginTop: 18, background: 'var(--card)', borderRadius: 18, padding: 18, border: '1px solid var(--line)' }}>
              <div style={{ fontSize: 10.5, fontFamily: 'var(--mono)', textTransform: 'uppercase', letterSpacing: '0.08em', color: 'var(--muted)' }}>Spent · May</div>
              <div style={{ fontFamily: 'var(--serif)', fontSize: 42, lineHeight: 1, letterSpacing: '-0.02em', marginTop: 6, color: 'var(--ink)' }}>$0</div>
              <div style={{ marginTop: 6, fontSize: 12, color: 'var(--muted)' }}>No spending logged yet this month</div>
            </div>

            {/* quick access (still available to a new user) */}
            <div style={{ marginTop: 22 }}>
              <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', marginBottom: 10 }}>
                <div style={{ fontSize: 11, fontFamily: 'var(--mono)', textTransform: 'uppercase', letterSpacing: '0.08em', color: 'var(--ink-3)', fontWeight: 600 }}>Quick access</div>
              </div>
              <div style={{ display: 'grid', gridTemplateColumns: 'repeat(4, 1fr)', gap: 8 }}>
                {[
                  ['Reports', 'feat-reports'],
                  ['Debt', 'feat-debt'],
                  ['Split', 'feat-split'],
                  ['Events', 'feat-events'],
                ].map(([label, icon]) => (
                  <div key={label} style={{ display: 'flex', flexDirection: 'column', alignItems: 'center', gap: 7, padding: '12px 4px', background: 'var(--card)', border: '1px solid var(--line)', borderRadius: 14 }}>
                    <div style={{ width: 36, height: 36, borderRadius: 11, background: 'var(--blue-100)', display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
                      <Icon name={icon} size={18} stroke="var(--blue-700)" strokeWidth={1.8} />
                    </div>
                    <span style={{ fontSize: 11, color: 'var(--ink-2)', fontWeight: 500 }}>{label}</span>
                  </div>
                ))}
              </div>
            </div>
          </div>

          {/* empty recent — illustration + CTA */}
          <div style={{ flex: 1, display: 'flex', flexDirection: 'column', alignItems: 'center', justifyContent: 'center', textAlign: 'center', padding: '24px 40px 40px' }}>
            <svg width="148" height="120" viewBox="0 0 148 120" fill="none">
              <ellipse cx="74" cy="104" rx="58" ry="9" fill="var(--blue-100)" opacity="0.5"/>
              {/* open wallet */}
              <rect x="30" y="44" width="88" height="56" rx="12" fill="var(--white)" stroke="var(--ink)" strokeWidth="3"/>
              <path d="M30 60 h88" stroke="var(--ink)" strokeWidth="3"/>
              <rect x="92" y="68" width="26" height="18" rx="5" fill="var(--blue-100)" stroke="var(--ink)" strokeWidth="3"/>
              <circle cx="100" cy="77" r="2.6" fill="var(--ink)"/>
              {/* coin dropping in */}
              <circle cx="74" cy="30" r="15" fill="var(--blue-300)" stroke="var(--ink)" strokeWidth="3"/>
              <path d="M74 22 v16 M69 27 h7 a3 3 0 0 1 0 6 h-5 a3 3 0 0 0 0 6 h7" stroke="var(--white)" strokeWidth="2.4" strokeLinecap="round" strokeLinejoin="round" fill="none"/>
              {/* motion ticks */}
              <line x1="50" y1="22" x2="44" y2="16" stroke="var(--blue-300)" strokeWidth="3" strokeLinecap="round"/>
              <line x1="98" y1="22" x2="104" y2="16" stroke="var(--blue-300)" strokeWidth="3" strokeLinecap="round"/>
            </svg>
            <div style={{ fontFamily: 'var(--serif)', fontSize: 22, letterSpacing: '-0.01em', marginTop: 18 }}>No expenses yet</div>
            <div style={{ fontSize: 13.5, color: 'var(--ink-2)', lineHeight: 1.45, marginTop: 8, maxWidth: 240 }}>
              Start by logging your first one — it takes about five seconds.
            </div>
            <div style={{ marginTop: 18, display: 'inline-flex', alignItems: 'center', gap: 8, padding: '12px 20px', background: 'var(--clay)', color: 'var(--white)', borderRadius: 99, fontSize: 14, fontWeight: 600, boxShadow: '0 6px 14px rgba(3,155,229,0.3)' }}>
              <Icon name="plus" size={16} stroke="var(--white)" strokeWidth={2.4} />
              Log your first expense
            </div>
            {/* hint pointing to the + */}
            <div style={{ marginTop: 14, display: 'flex', alignItems: 'center', gap: 6, fontSize: 11, color: 'var(--muted)' }}>
              <span>or tap</span>
              <span style={{ display: 'inline-flex', alignItems: 'center', justifyContent: 'center', width: 20, height: 20, borderRadius: 6, background: 'var(--clay)' }}>
                <Icon name="plus" size={12} stroke="var(--white)" strokeWidth={2.6} />
              </span>
              <span>below</span>
            </div>
          </div>
        </div>
        <ProtoBottomNav active="home" onTab={() => {}} onAdd={() => {}} />
      </div>
    </PhoneShell>
  );
}

Object.assign(window, { StaticHome, StaticHomeEmpty, StaticAddAmount, StaticAddDetails });
