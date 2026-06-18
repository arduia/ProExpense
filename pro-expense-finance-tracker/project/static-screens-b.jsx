// Hi-fi static screens for flows 05-08

// ════════════════════════════════════════════════════════════════════
// FLOW 05 — PIN AUTH
// ════════════════════════════════════════════════════════════════════
function ScreenPinSetupHi() {
  return (
    <PhoneShell>
      <div style={{ height: '100%', display: 'flex', flexDirection: 'column' }}>
        <NavBar left={<BackBtn label="Settings" />} title="PIN authentication" right={null} />

        <div className="proto-scroll" style={{ flex: 1, padding: '8px 22px 22px', display: 'flex', flexDirection: 'column', gap: 16 }}>
          {/* hero */}
          <div style={{ textAlign: 'center', marginTop: 12 }}>
            <div style={{ width: 56, height: 56, borderRadius: 16, background: 'var(--clay-tint)', display: 'inline-flex', alignItems: 'center', justifyContent: 'center' }}>
              <Icon name="fingerprint" size={28} stroke="var(--clay)" strokeWidth={1.7} />
            </div>
            <div style={{ fontFamily: 'var(--serif)', fontSize: 22, marginTop: 12 }}>Set up your PIN</div>
            <div style={{ fontSize: 13, color: 'var(--ink-3)', marginTop: 4, padding: '0 24px' }}>Optional 6-digit lock · fully on-device</div>
          </div>

          {/* toggle group */}
          <div style={{ background: 'var(--card)', border: '1px solid var(--line)', borderRadius: 16, overflow: 'hidden' }}>
            <ToggleRow icon="eye" label="PIN authentication" on />
            <div style={{ height: 1, background: 'var(--line-2)' }} />
            <ToggleRow icon="fingerprint" label="Use Touch ID" detail="Requires PIN" on={false} />
          </div>

          {/* PIN input */}
          <div>
            <SectionTitle>New PIN · 6 digits</SectionTitle>
            <div style={{ marginTop: 8, padding: '16px 18px', background: 'var(--card)', border: '1.4px solid var(--clay)', borderRadius: 14, display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
              <div style={{ display: 'flex', gap: 12 }}>
                {[1,2,3,4,5,6].map(i => (
                  <div key={i} style={{
                    width: 14, height: 14, borderRadius: 99,
                    background: i <= 6 ? 'var(--clay)' : 'transparent',
                    border: '1.5px solid ' + (i <= 6 ? 'var(--clay)' : 'var(--line-strong)'),
                  }} />
                ))}
              </div>
              <Icon name="eye" size={20} stroke="var(--ink-2)" strokeWidth={1.7} />
            </div>
          </div>

          <div>
            <SectionTitle>Confirm PIN</SectionTitle>
            <div style={{ marginTop: 8, padding: '16px 18px', background: 'var(--card)', border: '1px solid var(--line)', borderRadius: 14, display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
              <div style={{ display: 'flex', gap: 12 }}>
                {[1,2,3,4,5,6].map(i => (
                  <div key={i} style={{
                    width: 14, height: 14, borderRadius: 99,
                    background: i <= 3 ? 'var(--ink)' : 'transparent',
                    border: '1.5px solid ' + (i <= 3 ? 'var(--ink)' : 'var(--line-strong)'),
                  }} />
                ))}
              </div>
              <EyeOff />
            </div>
          </div>

          {/* recovery */}
          <div>
            <SectionTitle>Recovery · required</SectionTitle>
            <div style={{ marginTop: 8, padding: '14px 16px', background: 'var(--card)', border: '1px solid var(--line)', borderRadius: 14, display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
              <div>
                <div style={{ fontSize: 13, color: 'var(--ink)', fontWeight: 500 }}>Security question</div>
                <div style={{ fontSize: 11, color: 'var(--muted)' }}>Pick one from a list</div>
              </div>
              <Icon name="chevron-right" size={16} stroke="var(--muted)" strokeWidth={1.6} />
            </div>
          </div>

          {/* save */}
          <Button variant="primary" size="lg" fullWidth>Save PIN</Button>
        </div>
      </div>
    </PhoneShell>
  );
}

function ToggleRow({ icon, label, detail, on }) {
  return (
    <div style={{ display: 'flex', alignItems: 'center', gap: 12, padding: '14px 16px' }}>
      <div style={{ width: 32, height: 32, borderRadius: 9, background: 'var(--paper-warm)', display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
        <Icon name={icon} size={16} stroke="var(--ink-2)" strokeWidth={1.7} />
      </div>
      <div style={{ flex: 1 }}>
        <div style={{ fontSize: 14, color: 'var(--ink)', fontWeight: 500 }}>{label}</div>
        {detail && <div style={{ fontSize: 11, color: 'var(--muted)' }}>{detail}</div>}
      </div>
      <div style={{
        width: 42, height: 26, borderRadius: 99, background: on ? 'var(--sage)' : 'rgba(43,31,23,0.18)',
        position: 'relative', transition: 'background 200ms ease',
      }}>
        <div style={{
          position: 'absolute', top: 3, left: on ? 19 : 3,
          width: 20, height: 20, borderRadius: 99, background: '#fffdf6',
          boxShadow: '0 1px 3px rgba(0,0,0,0.18)', transition: 'left 200ms ease',
        }} />
      </div>
    </div>
  );
}

function EyeOff() {
  return (
    <svg width="20" height="20" viewBox="0 0 24 24" fill="none">
      <path d="M2 12C4 7 8 4 12 4C16 4 20 7 22 12C20 17 16 20 12 20C8 20 4 17 2 12Z" stroke="var(--muted)" strokeWidth="1.6"/>
      <circle cx="12" cy="12" r="3" stroke="var(--muted)" strokeWidth="1.6"/>
      <line x1="4" y1="4" x2="20" y2="20" stroke="var(--muted)" strokeWidth="1.6"/>
    </svg>
  );
}

function ScreenPinEntryHi({ filled = 4, locked = false, attempts = 3 }) {
  return (
    <PhoneShell>
      <div style={{ height: '100%', display: 'flex', flexDirection: 'column' }}>
        <div style={{ flex: 1, display: 'flex', flexDirection: 'column', alignItems: 'center', justifyContent: 'flex-start', paddingTop: 60 }}>
          {/* logo */}
          <div style={{ width: 56, height: 56, borderRadius: 12, background: 'var(--clay)', display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
            {/* app icon glyph — ic_pro_expense */}
            <svg width="56" height="56" viewBox="8 8 176 176" fill="none">
              <path fillRule="evenodd" d="M140,126V66a10,10,0,1,0-14-14H66A10,10,0,1,0,52,66v60a10,10,0,1,0,13.17,15h61.66A10,10,0,1,0,140,126Zm-14.63,3H66.63A9.91,9.91,0,0,0,64,126V66a9.39,9.39,0,0,0,2-2h60a9.39,9.39,0,0,0,2,2v60A9.91,9.91,0,0,0,125.37,129Z" fill="var(--paper-warm)"/>
            </svg>
          </div>

          <div style={{ marginTop: 24, fontFamily: 'var(--serif)', fontSize: 28, letterSpacing: '-0.015em', color: locked ? '#b26a00' : 'var(--ink)' }}>{locked ? 'App locked' : 'Enter your PIN'}</div>
          <div style={{ marginTop: 6, fontSize: 13, color: locked ? '#b26a00' : 'var(--muted)' }}>
            {locked ? 'Too many attempts · try again shortly' : '6 digits to unlock'}
          </div>

          {/* PIN dots */}
          <div style={{ marginTop: 38, display: 'flex', gap: 16 }}>
            {[1,2,3,4,5,6].map(i => (
              <div key={i} style={{
                width: 14, height: 14, borderRadius: 99,
                background: !locked && i <= filled ? 'var(--clay)' : 'transparent',
                border: '1.6px solid ' + (locked ? 'rgba(178,106,0,0.4)' : i <= filled ? 'var(--clay)' : 'var(--line-strong)'),
                transition: 'all 120ms ease',
              }} />
            ))}
          </div>

          {/* biometric */}
          {!locked && (
            <div style={{ marginTop: 30 }}>
              <div style={{ width: 56, height: 56, borderRadius: 99, border: '1.4px solid var(--ink)', background: 'var(--paper-warm)', display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
                <Icon name="fingerprint" size={30} stroke="var(--ink)" strokeWidth={1.5} />
              </div>
            </div>
          )}
          {locked && (
            <div style={{ marginTop: 30, display: 'flex', flexDirection: 'column', alignItems: 'center', gap: 4 }}>
              <div style={{ fontFamily: 'var(--mono)', fontSize: 30, color: '#b26a00', letterSpacing: '0.02em' }}>0:{String(30 - attempts * 5).padStart(2, '0')}</div>
              <div style={{ fontSize: 11, color: 'rgba(178,106,0,0.7)' }}>Keypad disabled until countdown ends</div>
            </div>
          )}
        </div>

        {/* keypad */}
        <div style={{ padding: '0 22px 38px', display: 'grid', gridTemplateColumns: 'repeat(3, 1fr)', gap: 10, opacity: locked ? 0.3 : 1 }}>
          {['1','2','3','4','5','6','7','8','9'].map(k => (
            <div key={k} style={{
              padding: '16px 0', textAlign: 'center', borderRadius: 14,
              background: 'var(--card)', border: '1px solid var(--line)',
              fontSize: 24, fontFamily: 'var(--serif)', color: 'var(--ink)',
            }}>{k}</div>
          ))}
          <div />
          <div style={{
            padding: '16px 0', textAlign: 'center', borderRadius: 14,
            background: 'var(--card)', border: '1px solid var(--line)',
            fontSize: 24, fontFamily: 'var(--serif)', color: 'var(--ink)',
          }}>0</div>
          <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'center', color: 'var(--muted)' }}>
            <svg width="22" height="18" viewBox="0 0 22 18" fill="none">
              <path d="M7 1L1 9l6 8h13a1 1 0 0 0 1-1V2a1 1 0 0 0-1-1Z" stroke="currentColor" strokeWidth="1.5"/>
              <path d="M11 6l6 6M17 6l-6 6" stroke="currentColor" strokeWidth="1.5"/>
            </svg>
          </div>
        </div>

        <div style={{ textAlign: 'center', fontSize: 13, color: 'var(--clay)', fontWeight: 500, padding: '0 0 16px' }}>
          Forgot PIN?
        </div>
      </div>
    </PhoneShell>
  );
}

// ════════════════════════════════════════════════════════════════════
// FLOW 06 — EVENT BUDGET
// ════════════════════════════════════════════════════════════════════
function ScreenEventBudgetHi() {
  const events = [
    { name: 'Bali Trip', range: 'May 12 — May 26', remaining: 1240, budget: 2000, state: 'on' },
    { name: "John's Wedding", range: 'Jun 04 — Jun 06', remaining: 460, budget: 800, state: 'on' },
    { name: 'Birthday party', range: 'Apr 28', remaining: -45, budget: 400, state: 'over' },
  ];

  return (
    <PhoneShell>
      <div style={{ height: '100%', display: 'flex', flexDirection: 'column' }}>
        <div style={{ padding: '14px 22px 8px', display: 'flex', alignItems: 'flex-end', justifyContent: 'space-between' }}>
          <div>
            <div style={{ fontSize: 11, color: 'var(--muted)', fontFamily: 'var(--mono)', letterSpacing: '0.06em', textTransform: 'uppercase' }}>Budget tracker</div>
            <div style={{ fontFamily: 'var(--serif)', fontSize: 32, lineHeight: 1, letterSpacing: '-0.015em', marginTop: 4 }}>Events</div>
          </div>
          <Button variant="primary" size="sm" style={{ display: 'flex', alignItems: 'center', gap: 4 }}>
            <Icon name="plus" size={14} stroke="#fffdf6" strokeWidth={2.4}/> New event
          </Button>
        </div>

        <div className="proto-scroll" style={{ flex: 1, padding: '8px 22px 16px', display: 'flex', flexDirection: 'column', gap: 12 }}>
          {events.map(ev => {
            const pct = Math.min(100, ((ev.budget - ev.remaining) / ev.budget) * 100);
            const over = ev.state === 'over';
            return (
              <div key={ev.name} style={{
                background: 'var(--card)', border: '1px solid var(--line)', borderRadius: 18,
                padding: 18, position: 'relative', overflow: 'hidden',
              }}>
                {over && (
                  <div style={{ position: 'absolute', top: 16, right: 16, padding: '3px 9px', background: 'var(--card)', border: '1px solid var(--danger)', color: 'var(--danger)', borderRadius: 99, fontSize: 9.5, fontFamily: 'var(--mono)', textTransform: 'uppercase', letterSpacing: '0.08em', fontWeight: 600 }}>
                    Over budget
                  </div>
                )}
                <div style={{ display: 'flex', alignItems: 'center', gap: 12 }}>
                  <div style={{ width: 38, height: 38, borderRadius: 11, background: 'var(--blue-100)', display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
                    <Icon name="feat-events" size={18} stroke="var(--blue-700)" strokeWidth={1.8}/>
                  </div>
                  <div style={{ flex: 1 }}>
                    <div style={{ fontFamily: 'var(--serif)', fontSize: 19, letterSpacing: '-0.01em', color: 'var(--ink)' }}>{ev.name}</div>
                    <div style={{ fontSize: 11.5, color: 'var(--muted)' }}>{ev.range}</div>
                  </div>
                </div>
                <div style={{ marginTop: 14, display: 'flex', alignItems: 'baseline', justifyContent: 'space-between' }}>
                  <div style={{ fontFamily: 'var(--serif)', fontSize: 26, lineHeight: 1, color: over ? 'var(--danger)' : 'var(--ink)' }}>
                    {over ? `−${currencyFmt(Math.abs(ev.remaining))}` : currencyFmt(ev.remaining)}
                  </div>
                  <div style={{ fontSize: 11, color: 'var(--muted)' }}>{over ? 'over' : `of ${currencyFmt(ev.budget)}`}</div>
                </div>
                <div style={{ marginTop: 12, height: 6, background: 'var(--gray-200)', borderRadius: 99, overflow: 'hidden' }}>
                  <div style={{ width: pct + '%', height: '100%', background: over ? 'var(--danger)' : 'var(--blue-500)', borderRadius: 99 }} />
                </div>
              </div>
            );
          })}
        </div>

        <ProtoBottomNav active="budget" />
      </div>
    </PhoneShell>
  );
}

function ScreenEventDetailHi() {
  const tagged = [
    { id: 'b1', amount: 180, cat: 'bills', note: 'Hotel night' },
    { id: 'b2', amount: 22, cat: 'food', note: 'Lunch · beach' },
    { id: 'b3', amount: 15, cat: 'transport', note: 'Scooter rental' },
    { id: 'b4', amount: 48, cat: 'shopping', note: 'Souvenirs' },
    { id: 'b5', amount: 64, cat: 'food', note: 'Dinner · seafood' },
  ];
  return (
    <PhoneShell>
      <div style={{ height: '100%', display: 'flex', flexDirection: 'column' }}>
        <NavBar left={<BackBtn label="Events" />} title="" right={<Icon name="more" size={20} stroke="var(--ink-2)" strokeWidth={1.8}/>} />

        <div className="proto-scroll" style={{ flex: 1, padding: '0 22px 38px' }}>
          <div style={{ marginTop: 8 }}>
            <div style={{ display: 'flex', alignItems: 'center', gap: 8 }}>
              <div style={{ width: 8, height: 8, borderRadius: 99, background: 'var(--sage)' }} />
              <span style={{ fontSize: 10.5, fontFamily: 'var(--mono)', textTransform: 'uppercase', letterSpacing: '0.1em', color: 'var(--sage)', fontWeight: 600 }}>Active</span>
            </div>
            <div style={{ fontFamily: 'var(--serif)', fontSize: 38, letterSpacing: '-0.02em', lineHeight: 1.05, marginTop: 6 }}>Bali Trip</div>
            <div style={{ fontSize: 13, color: 'var(--muted)', marginTop: 4 }}>May 12 — May 26 · 14 days left</div>
          </div>

          {/* hero card */}
          <div style={{ marginTop: 20, background: 'var(--card)', borderRadius: 22, padding: 22, border: '1px solid var(--line)' }}>
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start' }}>
              <div>
                <div style={{ fontSize: 10.5, fontFamily: 'var(--mono)', textTransform: 'uppercase', letterSpacing: '0.08em', color: 'var(--muted)' }}>Remaining</div>
                <div style={{ fontFamily: 'var(--serif)', fontSize: 44, lineHeight: 1, letterSpacing: '-0.02em', marginTop: 6, color: 'var(--blue-500)' }}>$1,240</div>
              </div>
              <div style={{ textAlign: 'right' }}>
                <div style={{ fontSize: 11, color: 'var(--muted)' }}>Spent</div>
                <div style={{ fontFamily: 'var(--serif)', fontSize: 18, color: 'var(--ink)' }}>$760</div>
                <div style={{ fontSize: 11, color: 'var(--muted)', marginTop: 6 }}>Budget</div>
                <div style={{ fontFamily: 'var(--serif)', fontSize: 18, color: 'var(--ink)' }}>$2,000</div>
              </div>
            </div>
            <div style={{ marginTop: 16, height: 8, background: 'var(--gray-200)', borderRadius: 99, overflow: 'hidden' }}>
              <div style={{ width: '38%', height: '100%', background: 'var(--blue-500)', borderRadius: 99 }} />
            </div>
            <div style={{ marginTop: 8, fontSize: 11, color: 'var(--muted)', display: 'flex', justifyContent: 'space-between' }}>
              <span>38% spent</span>
              <span>$88/day pace</span>
            </div>
          </div>

          {/* linked expenses */}
          <div style={{ marginTop: 22, display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
            <SectionTitle>Linked expenses · 5</SectionTitle>
            <button style={{ background: 'transparent', border: 'none', display: 'flex', alignItems: 'center', gap: 4, color: 'var(--clay)', fontSize: 12, fontWeight: 500, cursor: 'pointer' }}>
              <Icon name="plus" size={12} stroke="var(--clay)" strokeWidth={2.2}/> Add tagged
            </button>
          </div>
          <div style={{ marginTop: 8, background: 'var(--card)', border: '1px solid var(--line)', borderRadius: 14, overflow: 'hidden' }}>
            {tagged.map((t, i) => (
              <div key={t.id} style={{ display: 'flex', alignItems: 'center', gap: 12, padding: '12px 14px', borderTop: i ? '1px solid var(--line-2)' : 'none' }}>
                <CatBadge cat={t.cat} size={32} />
                <div style={{ flex: 1, minWidth: 0 }}>
                  <div style={{ fontSize: 13, fontWeight: 500 }}>{t.note}</div>
                  <div style={{ fontSize: 11, color: 'var(--muted)' }}>{CATEGORIES[t.cat].label}</div>
                </div>
                <div style={{ fontFamily: 'var(--serif)', fontSize: 15 }}>{currencyFmt(t.amount)}</div>
              </div>
            ))}
          </div>
        </div>
      </div>
    </PhoneShell>
  );
}

// ════════════════════════════════════════════════════════════════════
// FLOW 07 — DEBT TRACKER
// ════════════════════════════════════════════════════════════════════
function ScreenDebtDetailHi({ view = 'lent' }) {
  const lent = view === 'lent';
  const accent = lent ? 'var(--sage)' : 'var(--danger)';
  const tint = lent ? 'var(--sage-soft)' : 'var(--danger-tint)';
  const rec = lent
    ? { name: 'John', initial: 'J', amount: 50, date: 'May 12, 2026', due: 'May 30, 2026', note: 'Dinner at Nobu — covered his share.', linked: 'Dinner · seafood', linkedAmt: 64 }
    : { name: 'David', initial: 'D', amount: 30, date: 'May 14, 2026', due: 'No due date', note: 'Taxi share back from the airport.', linked: null, linkedAmt: 0 };

  const Field = ({ label, value, sub }) => (
    <div style={{ padding: '14px 16px', borderTop: '1px solid var(--line-2)', display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
      <span style={{ fontSize: 12.5, color: 'var(--muted)' }}>{label}</span>
      <span style={{ fontSize: 13.5, color: 'var(--ink)', fontWeight: 500, textAlign: 'right' }}>{value}{sub && <span style={{ display: 'block', fontSize: 11, color: 'var(--muted)', fontWeight: 400 }}>{sub}</span>}</span>
    </div>
  );

  return (
    <PhoneShell>
      <div style={{ height: '100%', display: 'flex', flexDirection: 'column' }}>
        <NavBar
          left={<BackBtn label="Debt" />}
          title=""
          right={<div className="proto-tap" style={{ display: 'flex', alignItems: 'center', justifyContent: 'center', width: 32, height: 32 }}><Icon name="more" size={20} stroke="var(--ink-2)" strokeWidth={2}/></div>}
        />

        <div className="proto-scroll" style={{ flex: 1, padding: '4px 22px 16px' }}>
          {/* hero */}
          <div style={{ display: 'flex', flexDirection: 'column', alignItems: 'center', textAlign: 'center', paddingTop: 8 }}>
            <div style={{ width: 64, height: 64, borderRadius: 99, background: tint, color: accent, display: 'flex', alignItems: 'center', justifyContent: 'center', fontFamily: 'var(--serif)', fontSize: 28, fontWeight: 600 }}>{rec.initial}</div>
            <div style={{ marginTop: 12, fontFamily: 'var(--serif)', fontSize: 24, letterSpacing: '-0.01em', color: 'var(--ink)' }}>{rec.name}</div>
            <div style={{ marginTop: 4, fontSize: 11.5, fontFamily: 'var(--mono)', textTransform: 'uppercase', letterSpacing: '0.08em', color: accent }}>{lent ? 'You lent' : 'You owe'}</div>
            <div style={{ marginTop: 8, fontFamily: 'var(--serif)', fontSize: 46, lineHeight: 1, letterSpacing: '-0.02em', color: accent }}>{currencyFmt(rec.amount)}</div>
          </div>

          {/* fields */}
          <div style={{ marginTop: 22, background: 'var(--card)', border: '1px solid var(--line)', borderRadius: 16, overflow: 'hidden' }}>
            <div style={{ padding: '14px 16px', display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
              <span style={{ fontSize: 12.5, color: 'var(--muted)' }}>Date recorded</span>
              <span style={{ fontSize: 13.5, color: 'var(--ink)', fontWeight: 500 }}>{rec.date}</span>
            </div>
            <Field label="Due date" value={rec.due} />
            <Field label="Status" value="Active" />
          </div>

          {/* note */}
          <div style={{ marginTop: 16, background: 'var(--card)', border: '1px solid var(--line)', borderRadius: 16, padding: '14px 16px', display: 'flex', gap: 10, alignItems: 'flex-start' }}>
            <Icon name="note" size={18} stroke="var(--ink-3)" strokeWidth={1.6} style={{ marginTop: 1, flexShrink: 0 }} />
            <span style={{ fontSize: 13.5, color: 'var(--ink-2)', lineHeight: 1.45, fontFamily: 'var(--serif)', fontStyle: 'italic' }}>{rec.note}</span>
          </div>

          {/* linked expense */}
          {rec.linked && (
            <div style={{ marginTop: 16 }}>
              <SectionTitle>Linked expense</SectionTitle>
              <div style={{ marginTop: 8, background: 'var(--card)', border: '1px solid var(--line)', borderRadius: 14, padding: '12px 14px', display: 'flex', alignItems: 'center', gap: 12 }}>
                <div style={{ width: 30, height: 30, borderRadius: 8, background: 'var(--blue-100)', display: 'flex', alignItems: 'center', justifyContent: 'center', flexShrink: 0 }}>
                  <Icon name="at" size={15} stroke="var(--tag-deep)" strokeWidth={2} />
                </div>
                <div style={{ flex: 1 }}>
                  <div style={{ fontSize: 13.5, color: 'var(--ink)', fontWeight: 500 }}>{rec.linked}</div>
                  <div style={{ fontSize: 11, color: 'var(--muted)' }}>Reference only</div>
                </div>
                <div style={{ fontFamily: 'var(--serif)', fontSize: 15, color: 'var(--ink)' }}>{currencyFmt(rec.linkedAmt)}</div>
              </div>
            </div>
          )}
        </div>

        {/* actions */}
        <div style={{ padding: '0 22px 38px', display: 'flex', gap: 8 }}>
          <button className="proto-tap" style={{ flex: 1, padding: '15px 0', background: 'transparent', border: '1.4px solid var(--line-strong)', borderRadius: 14, fontSize: 14, fontWeight: 600, color: 'var(--ink)', cursor: 'pointer' }}>Edit</button>
          <button className="proto-tap" style={{ flex: 1.4, padding: '15px 0', background: accent, border: 'none', borderRadius: 14, fontSize: 14, fontWeight: 600, color: '#fff', cursor: 'pointer', display: 'flex', alignItems: 'center', justifyContent: 'center', gap: 6 }}>
            <Icon name="check" size={16} stroke="#fff" strokeWidth={2.4} />
            Mark as settled
          </button>
        </div>
      </div>
    </PhoneShell>
  );
}

// ════════════════════════════════════════════════════════════════════
// FLOW 07 — DEBT TRACKER
// ════════════════════════════════════════════════════════════════════
function ScreenDebtTrackerHi({ view = 'lent' }) {
  const lent = [
    { name: 'John', amount: 50, date: 'May 12', note: 'dinner at Nobu', initial: 'J' },
    { name: 'Maya', amount: 25, date: 'May 08', note: 'due May 30', initial: 'M' },
    { name: 'Sarah', amount: 60, date: 'Apr 28', note: '', initial: 'S' },
  ];
  const owe = [
    { name: 'David', amount: 30, date: 'May 14', note: 'taxi share', initial: 'D' },
    { name: 'Lin', amount: 15, date: 'May 02', note: '', initial: 'L' },
  ];
  const settled = [{ name: 'Aiko', amount: 20, date: 'Apr 14', initial: 'A' }];
  const active = view === 'lent' ? lent : owe;
  const total = active.reduce((s, r) => s + r.amount, 0);
  const accentColor = view === 'lent' ? 'var(--sage)' : 'var(--danger)';
  const accentTint = view === 'lent' ? 'var(--sage-soft)' : 'var(--danger-tint)';

  return (
    <PhoneShell>
      <div style={{ height: '100%', display: 'flex', flexDirection: 'column' }}>
        <NavBar
          left={<BackBtn label="More" />}
          title="Debt"
          right={<div style={{ width: 32, height: 32, borderRadius: 9, background: 'var(--clay)', display: 'flex', alignItems: 'center', justifyContent: 'center' }}><Icon name="plus" size={18} stroke="#fffdf6" strokeWidth={2.2}/></div>}
        />

        <div className="proto-scroll" style={{ flex: 1, padding: '8px 22px 16px' }}>
          {/* toggle */}
          <div style={{ display: 'flex', background: 'var(--card)', border: '1px solid var(--line)', borderRadius: 99, padding: 4, marginTop: 4 }}>
            <Toggle on={view === 'lent'} label="I Lent" />
            <Toggle on={view === 'owe'} label="I Owe" />
          </div>

          {/* summary */}
          <div style={{ marginTop: 18, background: 'var(--card)', border: '1px solid var(--line)', borderRadius: 16, padding: 18 }}>
            <div style={{ fontSize: 10.5, fontFamily: 'var(--mono)', textTransform: 'uppercase', letterSpacing: '0.08em', color: 'var(--muted)' }}>{view === 'lent' ? 'You\'re owed' : 'You owe'}</div>
            <div style={{ display: 'flex', alignItems: 'baseline', justifyContent: 'space-between', marginTop: 4 }}>
              <div style={{ fontFamily: 'var(--serif)', fontSize: 38, lineHeight: 1, letterSpacing: '-0.02em', color: accentColor }}>{currencyFmt(total)}</div>
              <div style={{ fontSize: 12, color: 'var(--muted)' }}>{active.length} active</div>
            </div>
          </div>

          {/* active list */}
          <div style={{ marginTop: 22 }}>
            <SectionTitle>Active</SectionTitle>
            <div style={{ marginTop: 8, background: 'var(--card)', border: '1px solid var(--line)', borderRadius: 14, overflow: 'hidden' }}>
              {active.map((r, i) => (
                <div key={r.name} style={{ display: 'flex', alignItems: 'center', gap: 12, padding: '14px', borderTop: i ? '1px solid var(--line-2)' : 'none' }}>
                  <div style={{
                    width: 38, height: 38, borderRadius: 99, background: accentTint, color: accentColor,
                    display: 'flex', alignItems: 'center', justifyContent: 'center',
                    fontFamily: 'var(--serif)', fontSize: 18, fontWeight: 600,
                  }}>{r.initial}</div>
                  <div style={{ flex: 1, minWidth: 0 }}>
                    <div style={{ fontSize: 14, fontWeight: 500, color: 'var(--ink)' }}>{r.name}</div>
                    <div style={{ fontSize: 11.5, color: 'var(--muted)' }}>{r.date}{r.note && ` · ${r.note}`}</div>
                  </div>
                  <div style={{ fontFamily: 'var(--serif)', fontSize: 17, color: accentColor }}>{currencyFmt(r.amount)}</div>
                </div>
              ))}
            </div>
          </div>

          {/* settled */}
          {view === 'lent' && (
            <div style={{ marginTop: 22 }}>
              <SectionTitle>Settled</SectionTitle>
              <div style={{ marginTop: 8, background: 'transparent', border: '1px solid var(--line-2)', borderRadius: 14, overflow: 'hidden', opacity: 0.55 }}>
                {settled.map((r, i) => (
                  <div key={r.name} style={{ display: 'flex', alignItems: 'center', gap: 12, padding: '14px' }}>
                    <div style={{ width: 32, height: 32, borderRadius: 99, background: 'transparent', border: '1.4px solid var(--line-strong)', color: 'var(--muted)', display: 'flex', alignItems: 'center', justifyContent: 'center', fontFamily: 'var(--serif)', fontSize: 16 }}>
                      {r.initial}
                    </div>
                    <div style={{ flex: 1 }}>
                      <div style={{ fontSize: 14, color: 'var(--ink-2)' }}>{r.name}</div>
                      <div style={{ fontSize: 11, color: 'var(--muted)' }}>{r.date}</div>
                    </div>
                    <span style={{ fontSize: 10, fontFamily: 'var(--mono)', color: 'var(--muted)', textTransform: 'uppercase', letterSpacing: '0.08em', border: '1px solid var(--line)', borderRadius: 99, padding: '2px 8px' }}>settled</span>
                  </div>
                ))}
              </div>
            </div>
          )}
        </div>
      </div>
    </PhoneShell>
  );
}

function Toggle({ on, label }) {
  return (
    <div style={{
      flex: 1, textAlign: 'center', padding: '8px 0',
      background: on ? 'var(--blue-500)' : 'transparent',
      color: on ? 'var(--white)' : 'var(--muted)',
      borderRadius: 99, fontSize: 13, fontWeight: on ? 600 : 500,
    }}>{label}</div>
  );
}

// ════════════════════════════════════════════════════════════════════
// FLOW 08 — SHARED COSTS
// ════════════════════════════════════════════════════════════════════
function ScreenSharedCostsHi() {
  const people = 4;
  const total = 120;
  const perPerson = total / people;
  const names = ['Aiko', 'Ben', 'Carlos', 'Dee'];
  return (
    <PhoneShell>
      <div style={{ height: '100%', display: 'flex', flexDirection: 'column' }}>
        <NavBar left={<BackBtn label="More" />} title="Split a bill" right={null} />

        <div className="proto-scroll" style={{ flex: 1, padding: '0 22px 38px' }}>
          {/* total */}
          <div style={{ textAlign: 'center', marginTop: 18 }}>
            <div style={{ fontSize: 10.5, fontFamily: 'var(--mono)', textTransform: 'uppercase', letterSpacing: '0.1em', color: 'var(--muted)' }}>Total bill</div>
            <div style={{ display: 'flex', alignItems: 'baseline', justifyContent: 'center', gap: 4, marginTop: 4 }}>
              <span style={{ fontFamily: 'var(--serif)', fontSize: 26, color: 'var(--clay)' }}>$</span>
              <span style={{ fontFamily: 'var(--serif)', fontSize: 56, letterSpacing: '-0.025em', lineHeight: 1 }}>120</span>
            </div>
          </div>

          {/* note */}
          <div style={{ marginTop: 14, padding: '10px 14px', background: 'var(--card)', border: '1px solid var(--line)', borderRadius: 12, display: 'flex', alignItems: 'center', gap: 10 }}>
            <Icon name="note" size={16} stroke="var(--ink-3)" strokeWidth={1.7} />
            <span style={{ fontFamily: 'var(--serif)', fontStyle: 'italic', fontSize: 13.5, color: 'var(--ink-2)' }}>Dinner at Nobu</span>
          </div>

          {/* people stepper */}
          <div style={{ marginTop: 18 }}>
            <SectionTitle>People</SectionTitle>
            <div style={{ marginTop: 8, padding: '16px 20px', background: 'var(--card)', border: '1px solid var(--line)', borderRadius: 16, display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
              <div style={{ width: 52, height: 52, borderRadius: 16, background: 'var(--paper-warm)', border: '1.5px solid var(--line-strong)', display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
                <Icon name="minus" size={24} stroke="var(--gray-500)" strokeWidth={2.6} />
              </div>
              <div style={{ display: 'flex', flexDirection: 'column', alignItems: 'center', lineHeight: 1 }}>
                <span style={{ fontFamily: 'var(--serif)', fontSize: 40, letterSpacing: '-0.01em' }}>{people}</span>
                <span style={{ fontSize: 10, color: 'var(--muted)', fontFamily: 'var(--mono)', textTransform: 'uppercase', letterSpacing: '0.08em', marginTop: 2 }}>people</span>
              </div>
              <div style={{ width: 52, height: 52, borderRadius: 16, background: 'var(--clay)', display: 'flex', alignItems: 'center', justifyContent: 'center', boxShadow: '0 4px 10px rgba(3,155,229,0.3)' }}>
                <Icon name="plus" size={24} stroke="#ffffff" strokeWidth={2.6} />
              </div>
            </div>
          </div>

          {/* split mode */}
          <div style={{ marginTop: 14, display: 'flex', background: 'var(--card)', border: '1px solid var(--line)', borderRadius: 99, padding: 4 }}>
            <Toggle on label="Equal" />
            <Toggle on={false} label="Custom" />
          </div>

          {/* per person card */}
          <div style={{ marginTop: 18, padding: '20px', background: 'var(--card)', borderRadius: 18, border: '1px solid var(--line)' }}>
            <div style={{ fontSize: 10.5, fontFamily: 'var(--mono)', textTransform: 'uppercase', letterSpacing: '0.08em', color: 'var(--muted)' }}>Per person</div>
            <div style={{ fontFamily: 'var(--serif)', fontSize: 44, lineHeight: 1, letterSpacing: '-0.02em', marginTop: 4, color: 'var(--blue-500)' }}>${perPerson.toFixed(0)}</div>
            <div style={{ marginTop: 14, display: 'flex', flexDirection: 'column', gap: 6 }}>
              {names.map((nm, i) => (
                <div key={i} style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', gap: 10, borderTop: i ? '1px solid var(--line-2)' : 'none', paddingTop: i ? 10 : 0 }}>
                  <div style={{ display: 'flex', alignItems: 'center', gap: 10, flex: 1, minWidth: 0 }}>
                    <div style={{ width: 30, height: 30, borderRadius: 99, background: 'var(--blue-100)', color: 'var(--blue-700)', display: 'flex', alignItems: 'center', justifyContent: 'center', fontSize: 15, fontFamily: 'var(--serif)', fontWeight: 600, flexShrink: 0 }}>{i+1}</div>
                    {/* editable name field */}
                    <div style={{ flex: 1, display: 'flex', alignItems: 'center', gap: 6, borderBottom: '1px dashed var(--line-strong)', paddingBottom: 4 }}>
                      <span style={{ fontSize: 14, color: 'var(--ink)' }}>{nm}</span>
                      <Icon name="note" size={12} stroke="var(--muted)" strokeWidth={1.6} />
                    </div>
                  </div>
                  <div style={{ fontFamily: 'var(--serif)', fontSize: 16, color: 'var(--ink)', flexShrink: 0 }}>${perPerson.toFixed(0)}</div>
                </div>
              ))}
            </div>
          </div>

          <div style={{ marginTop: 18 }}>
            <Button variant="primary" size="lg" fullWidth>Save split</Button>
          </div>
        </div>
      </div>
    </PhoneShell>
  );
}

Object.assign(window, {
  ScreenPinSetupHi, ScreenPinEntryHi,
  ScreenEventBudgetHi, ScreenEventDetailHi,
  ScreenDebtTrackerHi, ScreenDebtDetailHi, ScreenSharedCostsHi,
});
