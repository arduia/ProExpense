// Connective screens — fills navigation dead-ends found in the flow audit.
// Reuses PhoneShell, NavBar, BackBtn, Button, Icon, EdgeBottomSheet, Field,
// InlineError, SectionTitle, currencyFmt, CatBadge, ProtoBottomNav (all global).

function NavLabel({ children }) {
  return <div style={{ fontSize: 10.5, fontFamily: 'var(--mono)', textTransform: 'uppercase', letterSpacing: '0.08em', color: 'var(--ink-3)', fontWeight: 600, marginBottom: 6 }}>{children}</div>;
}

// ═════════════════════════════════════════════════════════════════════
// FLOW 08 — SHARED COSTS · Split Summary (sub-screen) + History
// ═════════════════════════════════════════════════════════════════════

// Split Summary — input → THIS → save → history
function SharedSplitSummary() {
  const people = ['Aiko', 'Kenji', 'Sora', 'You'];
  const per = 60;
  return (
    <PhoneShell>
      <div style={{ height: '100%', display: 'flex', flexDirection: 'column' }}>
        <NavBar left={<BackBtn label="Split" />} title="Split summary" right={<span style={{ width: 50 }} />} />
        <div className="proto-scroll" style={{ flex: 1, padding: '4px 22px 16px' }}>
          {/* hero per-person */}
          <div style={{ textAlign: 'center', padding: '12px 0 18px' }}>
            <div style={{ fontSize: 11, fontFamily: 'var(--mono)', textTransform: 'uppercase', letterSpacing: '0.08em', color: 'var(--muted)' }}>Each person pays</div>
            <div style={{ fontFamily: 'var(--serif)', fontSize: 54, letterSpacing: '-0.025em', color: 'var(--blue-700)', lineHeight: 1, marginTop: 4 }}>{currencyFmt(per)}</div>
            <div style={{ fontSize: 12.5, color: 'var(--muted)', marginTop: 6 }}>{currencyFmt(240)} total · split 4 ways · equal</div>
          </div>

          {/* per-person rows */}
          <div style={{ background: 'var(--card)', border: '1px solid var(--line)', borderRadius: 16, overflow: 'hidden' }}>
            {people.map((p, i) => (
              <div key={p} style={{ display: 'flex', alignItems: 'center', gap: 12, padding: '13px 16px', borderTop: i ? '1px solid var(--line-2)' : 'none' }}>
                <div style={{ width: 32, height: 32, borderRadius: 99, background: 'var(--blue-100)', color: 'var(--blue-700)', display: 'flex', alignItems: 'center', justifyContent: 'center', fontFamily: 'var(--serif)', fontWeight: 600, fontSize: 14 }}>{i + 1}</div>
                <span style={{ flex: 1, fontSize: 14.5, color: 'var(--ink)' }}>{p}</span>
                <span style={{ fontFamily: 'var(--serif)', fontSize: 17, color: 'var(--ink)' }}>{currencyFmt(per)}</span>
              </div>
            ))}
          </div>

          <div style={{ marginTop: 14, display: 'flex', gap: 8, alignItems: 'center', justifyContent: 'center' }}>
            <span style={{ fontSize: 12.5, color: 'var(--muted)' }}>Need an uneven split?</span>
            <span style={{ fontSize: 12.5, color: 'var(--blue-700)', fontWeight: 600 }}>Switch to custom</span>
          </div>
        </div>
        <div style={{ padding: '0 22px 38px' }}>
          <Button variant="primary" size="lg" fullWidth>Save split · {currencyFmt(240)}</Button>
        </div>
      </div>
    </PhoneShell>
  );
}

// Shared Costs history — where saved splits land
function SharedHistory() {
  const items = [
    { note: 'Dinner at Nobu', total: 240, n: 4, date: 'May 24' },
    { note: 'Airbnb · Bali', total: 880, n: 4, date: 'May 18' },
    { note: 'Taxi to airport', total: 36, n: 3, date: 'May 12' },
  ];
  return (
    <PhoneShell>
      <div style={{ height: '100%', display: 'flex', flexDirection: 'column' }}>
        <div style={{ padding: '14px 22px 8px' }}>
          <div style={{ fontSize: 11, color: 'var(--muted)', fontFamily: 'var(--mono)', letterSpacing: '0.06em', textTransform: 'uppercase' }}>Bill splitter</div>
          <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
            <div style={{ fontFamily: 'var(--serif)', fontSize: 32, lineHeight: 1, letterSpacing: '-0.015em', marginTop: 4 }}>Shared Costs</div>
            <button style={{ height: 40, padding: '0 16px 0 12px', borderRadius: 99, border: 'none', background: 'var(--blue-500)', display: 'flex', alignItems: 'center', gap: 6, cursor: 'pointer' }}>
              <Icon name="plus" size={18} stroke="#fff" strokeWidth={2.2} />
              <span style={{ fontSize: 14, fontWeight: 600, color: '#fff' }}>New split</span>
            </button>
          </div>
        </div>
        <div className="proto-scroll" style={{ flex: 1, padding: '8px 22px 16px' }}>
          <SectionTitle>Recent splits</SectionTitle>
          <div style={{ marginTop: 10, display: 'flex', flexDirection: 'column', gap: 8 }}>
            {items.map((it, i) => (
              <div key={i} style={{ display: 'flex', alignItems: 'center', gap: 12, padding: '14px 16px', background: 'var(--card)', border: '1px solid var(--line)', borderRadius: 14 }}>
                <div style={{ width: 38, height: 38, borderRadius: 11, background: 'var(--blue-100)', display: 'flex', alignItems: 'center', justifyContent: 'center', flexShrink: 0 }}>
                  <Icon name="feat-split" size={19} stroke="var(--blue-700)" strokeWidth={1.7} />
                </div>
                <div style={{ flex: 1, minWidth: 0 }}>
                  <div style={{ fontSize: 14.5, fontWeight: 500, color: 'var(--ink)' }}>{it.note}</div>
                  <div style={{ fontSize: 11.5, color: 'var(--muted)' }}>{it.n} people · {currencyFmt(it.total / it.n)} each · {it.date}</div>
                </div>
                <div style={{ fontFamily: 'var(--serif)', fontSize: 17, color: 'var(--ink)' }}>{currencyFmt(it.total)}</div>
              </div>
            ))}
          </div>
        </div>
        <ProtoBottomNav active="more" />
      </div>
    </PhoneShell>
  );
}

// ═════════════════════════════════════════════════════════════════════
// FLOW 07 — DEBT · Add Record (bottom sheet)
// ═════════════════════════════════════════════════════════════════════
function DebtAddRecord() {
  return (
    <PhoneShell>
      <div style={{ height: '100%', background: 'var(--paper)' }} />
      <EdgeBottomSheet title="New record" height={560}>
        {/* I Lent / I Owe toggle */}
        <div style={{ display: 'flex', background: 'var(--gray-100)', borderRadius: 12, padding: 4, marginTop: 2, marginBottom: 18 }}>
          <div style={{ flex: 1, textAlign: 'center', padding: '10px 0', borderRadius: 9, background: 'var(--white)', boxShadow: '0 1px 3px rgba(0,0,0,0.08)', fontSize: 13.5, fontWeight: 600, color: 'var(--sage)' }}>I Lent</div>
          <div style={{ flex: 1, textAlign: 'center', padding: '10px 0', fontSize: 13.5, fontWeight: 500, color: 'var(--muted)' }}>I Owe</div>
        </div>

        <div style={{ display: 'flex', flexDirection: 'column', gap: 16 }}>
          <div>
            <NavLabel>Person</NavLabel>
            <Field><span style={{ fontSize: 15, color: 'var(--ink)' }}>John</span><span style={{ fontSize: 11, fontFamily: 'var(--mono)', color: 'var(--muted)' }}>4/30</span></Field>
          </div>
          <div>
            <NavLabel>Amount</NavLabel>
            <Field><span style={{ fontFamily: 'var(--serif)', fontSize: 24, letterSpacing: '-0.01em', color: 'var(--ink)' }}>$50</span><span style={{ fontSize: 11, fontFamily: 'var(--mono)', color: 'var(--muted)' }}>USD</span></Field>
          </div>
          <div style={{ display: 'flex', gap: 10 }}>
            <div style={{ flex: 1 }}>
              <NavLabel>Date</NavLabel>
              <Field><div style={{ display: 'flex', alignItems: 'center', gap: 8 }}><Icon name="calendar" size={16} stroke="var(--ink-3)" strokeWidth={1.7} /><span style={{ fontSize: 13.5, color: 'var(--ink)' }}>May 12</span></div></Field>
            </div>
            <div style={{ flex: 1 }}>
              <NavLabel>Due · optional</NavLabel>
              <Field><div style={{ display: 'flex', alignItems: 'center', gap: 8 }}><Icon name="calendar" size={16} stroke="var(--ink-3)" strokeWidth={1.7} /><span style={{ fontSize: 13.5, color: 'var(--muted)' }}>None</span></div></Field>
            </div>
          </div>
        </div>
        <div style={{ marginTop: 22 }}><Button variant="primary" size="lg" fullWidth>Save record</Button></div>
      </EdgeBottomSheet>
    </PhoneShell>
  );
}

// ═════════════════════════════════════════════════════════════════════
// FLOW 02 — JOURNAL DETAIL · Action bottom sheet (edit / delete)
// ═════════════════════════════════════════════════════════════════════
function JournalDetailActions() {
  return (
    <PhoneShell>
      {/* dimmed detail behind */}
      <div style={{ height: '100%', display: 'flex', flexDirection: 'column' }}>
        <NavBar left={<BackBtn label="Journal" />} title="" right={<span style={{ width: 50 }} />} />
        <div style={{ flex: 1, padding: '8px 22px', textAlign: 'center' }}>
          <div style={{ display: 'flex', justifyContent: 'center', marginTop: 8 }}><CatBadge cat="food" size={56} /></div>
          <div style={{ fontFamily: 'var(--serif)', fontSize: 48, letterSpacing: '-0.02em', marginTop: 14 }}>$12.40</div>
          <div style={{ fontSize: 13, color: 'var(--muted)', marginTop: 4 }}>Food · May 25, 12:30 PM</div>
        </div>
      </div>
      <EdgeBottomSheet title="" height={280}>
        <div style={{ display: 'flex', flexDirection: 'column', gap: 8, marginTop: -4 }}>
          <SheetAction icon="note" label="Edit expense" sub="Opens Add Expense, pre-filled" />
          <SheetAction icon="close" label="Delete expense" danger sub="Asks to confirm first" />
        </div>
        <div style={{ marginTop: 12 }}><Button variant="secondary" size="lg" fullWidth>Cancel</Button></div>
      </EdgeBottomSheet>
    </PhoneShell>
  );
}

function SheetAction({ icon, label, sub, danger }) {
  const c = danger ? 'var(--danger)' : 'var(--ink)';
  return (
    <div style={{ display: 'flex', alignItems: 'center', gap: 14, padding: '13px 14px', background: 'var(--card)', border: '1px solid var(--line)', borderRadius: 14 }}>
      <div style={{ width: 38, height: 38, borderRadius: 11, background: danger ? 'var(--danger-tint)' : 'var(--gray-100)', display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
        <Icon name={icon} size={18} stroke={c} strokeWidth={1.8} />
      </div>
      <div style={{ flex: 1 }}>
        <div style={{ fontSize: 15, fontWeight: 600, color: c }}>{label}</div>
        <div style={{ fontSize: 11.5, color: 'var(--muted)' }}>{sub}</div>
      </div>
    </div>
  );
}

// ═════════════════════════════════════════════════════════════════════
// FLOW 03 — MORE · Currency · Data Export · Clear Data
// ═════════════════════════════════════════════════════════════════════

function MoreCurrency() {
  const list = [
    ['$', 'USD', 'US Dollar'], ['€', 'EUR', 'Euro'], ['£', 'GBP', 'British Pound'],
    ['¥', 'JPY', 'Japanese Yen'], ['₹', 'INR', 'Indian Rupee'], ['د.إ', 'AED', 'UAE Dirham'],
  ];
  return (
    <PhoneShell>
      <div style={{ height: '100%', display: 'flex', flexDirection: 'column' }}>
        <NavBar left={<BackBtn label="More" />} title="Currency" right={<span style={{ width: 50 }} />} />
        <div className="proto-scroll" style={{ flex: 1, padding: '8px 22px 16px' }}>
          <div style={{ fontSize: 12.5, color: 'var(--muted)', marginBottom: 14, lineHeight: 1.4 }}>Your home currency. Applied to all entries — multi-currency per entry comes later.</div>
          <div style={{ display: 'flex', flexDirection: 'column', gap: 8 }}>
            {list.map(([sym, code, name], i) => {
              const on = code === 'USD';
              return (
                <div key={code} style={{ display: 'flex', alignItems: 'center', gap: 14, padding: '13px 16px', background: on ? 'var(--blue-100)' : 'var(--card)', border: '1.4px solid ' + (on ? 'var(--blue-500)' : 'var(--line)'), borderRadius: 14 }}>
                  <div style={{ width: 38, height: 38, borderRadius: 99, background: on ? 'var(--white)' : 'var(--gray-100)', display: 'flex', alignItems: 'center', justifyContent: 'center', fontFamily: 'var(--serif)', fontSize: 18, color: on ? 'var(--blue-700)' : 'var(--ink-2)' }}>{sym}</div>
                  <div style={{ flex: 1 }}><div style={{ fontSize: 15, fontWeight: 500, color: 'var(--ink)' }}>{code}</div><div style={{ fontSize: 12, color: 'var(--muted)' }}>{name}</div></div>
                  {on && <Icon name="check" size={20} stroke="var(--blue-700)" strokeWidth={2.4} />}
                </div>
              );
            })}
          </div>
        </div>
      </div>
    </PhoneShell>
  );
}

function MoreDataExport() {
  const files = [
    ['expenses.csv', 'All logged expenses + @ tags'],
    ['events.csv', 'Event budgets & status'],
    ['debts.csv', 'Lent / owed records'],
    ['shared_costs.csv', 'Saved bill splits'],
  ];
  return (
    <PhoneShell>
      <div style={{ height: '100%', display: 'flex', flexDirection: 'column' }}>
        <NavBar left={<BackBtn label="More" />} title="Data Export" right={<span style={{ width: 50 }} />} />
        <div className="proto-scroll" style={{ flex: 1, padding: '8px 22px 16px' }}>
          <div style={{ width: 64, height: 64, borderRadius: 18, background: 'var(--blue-100)', display: 'flex', alignItems: 'center', justifyContent: 'center', margin: '6px 0 14px' }}>
            <Icon name="note" size={30} stroke="var(--blue-700)" strokeWidth={1.6} />
          </div>
          <div style={{ fontFamily: 'var(--serif)', fontSize: 23, letterSpacing: '-0.01em' }}>Export your data</div>
          <div style={{ fontSize: 13, color: 'var(--muted)', marginTop: 6, lineHeight: 1.5 }}>One zip with a CSV per feature. Everything stays on your device — nothing is uploaded.</div>
          <div style={{ marginTop: 18, background: 'var(--card)', border: '1px solid var(--line)', borderRadius: 16, overflow: 'hidden' }}>
            {files.map(([f, d], i) => (
              <div key={f} style={{ display: 'flex', alignItems: 'center', gap: 12, padding: '13px 16px', borderTop: i ? '1px solid var(--line-2)' : 'none' }}>
                <Icon name="note" size={18} stroke="var(--ink-3)" strokeWidth={1.6} />
                <div style={{ flex: 1 }}><div style={{ fontSize: 13.5, fontWeight: 500, color: 'var(--ink)', fontFamily: 'var(--mono)' }}>{f}</div><div style={{ fontSize: 11.5, color: 'var(--muted)' }}>{d}</div></div>
              </div>
            ))}
          </div>
        </div>
        <div style={{ padding: '0 22px 38px' }}><Button variant="primary" size="lg" fullWidth>Export as ZIP</Button></div>
      </div>
    </PhoneShell>
  );
}

function MoreClearData() {
  const opts = [
    ['Expenses', 'All logged entries', true],
    ['Events', 'Budgets & linked tags', false],
    ['Debts', 'Lent / owed records', false],
    ['Shared costs', 'Saved splits', false],
    ['Everything', 'Reset the app fully', false],
  ];
  return (
    <PhoneShell>
      <div style={{ height: '100%', display: 'flex', flexDirection: 'column' }}>
        <NavBar left={<BackBtn label="More" />} title="Clear Data" right={<span style={{ width: 50 }} />} />
        <div className="proto-scroll" style={{ flex: 1, padding: '8px 22px 16px' }}>
          <div style={{ fontSize: 12.5, color: 'var(--muted)', marginBottom: 14, lineHeight: 1.4 }}>Choose what to wipe. Each clear asks for confirmation and can't be undone.</div>
          <div style={{ display: 'flex', flexDirection: 'column', gap: 8 }}>
            {opts.map(([label, sub, on], i) => (
              <div key={i} style={{ display: 'flex', alignItems: 'center', gap: 14, padding: '13px 16px', background: 'var(--card)', border: '1px solid var(--line)', borderRadius: 14 }}>
                <div style={{ flex: 1 }}><div style={{ fontSize: 14.5, fontWeight: 500, color: i === 4 ? 'var(--danger)' : 'var(--ink)' }}>{label}</div><div style={{ fontSize: 11.5, color: 'var(--muted)' }}>{sub}</div></div>
                <div style={{ width: 22, height: 22, borderRadius: 6, border: '1.6px solid ' + (on ? 'var(--blue-500)' : 'var(--line-strong)'), background: on ? 'var(--blue-500)' : 'transparent', display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
                  {on && <Icon name="check" size={13} stroke="#fff" strokeWidth={2.6} />}
                </div>
              </div>
            ))}
          </div>
        </div>
        <div style={{ padding: '0 22px 38px' }}><Button variant="primary" size="lg" fullWidth style={{ background: 'var(--danger)', borderColor: 'var(--danger)' }}>Clear selected…</Button></div>
      </div>
    </PhoneShell>
  );
}

// ═════════════════════════════════════════════════════════════════════
// FLOW 05 — PIN SETUP · Security question (mandatory step)
// ═════════════════════════════════════════════════════════════════════
function PinSecurityQuestion() {
  const qs = [
    "What was your first pet's name?",
    'What city were you born in?',
    'What was the name of your first school?',
    "What is your mother's maiden name?",
    'What was your childhood nickname?',
  ];
  return (
    <PhoneShell>
      <div style={{ height: '100%', display: 'flex', flexDirection: 'column' }}>
        <NavBar left={<BackBtn label="" />} title="Recovery" right={<span style={{ width: 50 }} />} />
        <div className="proto-scroll" style={{ flex: 1, padding: '8px 22px 16px' }}>
          <div style={{ fontFamily: 'var(--serif)', fontSize: 24, letterSpacing: '-0.01em', lineHeight: 1.15 }}>Set a security question</div>
          <div style={{ fontSize: 13, color: 'var(--muted)', marginTop: 6, lineHeight: 1.5 }}>Required — this is the only way to recover access if you forget your PIN.</div>

          <div style={{ marginTop: 20 }}><NavLabel>Question</NavLabel></div>
          <div style={{ marginTop: 6, display: 'flex', flexDirection: 'column', gap: 7 }}>
            {qs.map((q, i) => {
              const on = i === 0;
              return (
                <div key={i} style={{ display: 'flex', alignItems: 'center', gap: 12, padding: '12px 14px', background: on ? 'var(--blue-100)' : 'var(--card)', border: '1.4px solid ' + (on ? 'var(--blue-500)' : 'var(--line)'), borderRadius: 12 }}>
                  <div style={{ width: 18, height: 18, borderRadius: 99, border: '1.6px solid ' + (on ? 'var(--blue-500)' : 'var(--line-strong)'), background: on ? 'var(--blue-500)' : 'transparent', display: 'flex', alignItems: 'center', justifyContent: 'center', flexShrink: 0 }}>
                    {on && <div style={{ width: 7, height: 7, borderRadius: 99, background: '#fff' }} />}
                  </div>
                  <span style={{ fontSize: 13.5, color: 'var(--ink)' }}>{q}</span>
                </div>
              );
            })}
          </div>

          <div style={{ marginTop: 16 }}>
            <NavLabel>Your answer</NavLabel>
            <div style={{ padding: '13px 15px', background: 'var(--card)', border: '1.4px solid var(--blue-500)', borderRadius: 13, fontSize: 15, color: 'var(--ink)' }}>Biscuit<span className="proto-cursor" /></div>
          </div>
        </div>
        <div style={{ padding: '0 22px 38px' }}><Button variant="primary" size="lg" fullWidth>Enable PIN</Button></div>
      </div>
    </PhoneShell>
  );
}

Object.assign(window, {
  SharedSplitSummary, SharedHistory, DebtAddRecord, JournalDetailActions,
  MoreCurrency, MoreDataExport, MoreClearData, PinSecurityQuestion,
});
