// Edge-case artboards — one mega-section. Reuses PhoneShell, NavBar, BackBtn,
// Icon, Button, CatBadge, CATEGORIES, currencyFmt, SectionTitle (all global).
// Each screen centers on a single edge state from the screen-flow spec.

// ─────────────────────────────────────────────────────────────────────
// Shared overlay primitives
// ─────────────────────────────────────────────────────────────────────
function EdgeDialog({ icon = 'sparkle', tone = 'blue', title, body, actions }) {
  const toneColor = tone === 'warn' ? '#b26a00' : tone === 'danger' ? 'var(--danger)' : 'var(--blue-500)';
  const toneTint = tone === 'warn' ? 'rgba(178,106,0,0.12)' : tone === 'danger' ? 'var(--danger-tint)' : 'var(--blue-100)';
  return (
    <div style={{ position: 'absolute', inset: 0, zIndex: 30 }}>
      <div style={{ position: 'absolute', inset: 0, background: 'rgba(33,33,33,0.42)' }} />
      <div style={{
        position: 'absolute', left: 24, right: 24, top: '40%', transform: 'translateY(-50%)',
        background: 'var(--card)', borderRadius: 20, padding: 24,
        boxShadow: '0 18px 40px rgba(0,0,0,0.2)',
      }}>
        <div style={{ width: 42, height: 42, borderRadius: 99, background: toneTint, color: toneColor, display: 'flex', alignItems: 'center', justifyContent: 'center', marginBottom: 14 }}>
          <Icon name={icon} size={20} stroke={toneColor} strokeWidth={1.9} />
        </div>
        <div style={{ fontFamily: 'var(--serif)', fontSize: 23, lineHeight: 1.1, letterSpacing: '-0.01em', marginBottom: 8, color: 'var(--ink)' }}>{title}</div>
        <div style={{ fontSize: 13.5, color: 'var(--ink-2)', lineHeight: 1.5, marginBottom: 20 }}>{body}</div>
        <div style={{ display: 'flex', gap: 8 }}>{actions}</div>
      </div>
    </div>
  );
}

function EdgeBottomSheet({ title, children, height = 460 }) {
  return (
    <div style={{ position: 'absolute', inset: 0, zIndex: 30 }}>
      <div style={{ position: 'absolute', inset: 0, background: 'rgba(33,33,33,0.42)' }} />
      <div style={{
        position: 'absolute', left: 0, right: 0, bottom: 0, height,
        background: 'var(--card)', borderTopLeftRadius: 24, borderTopRightRadius: 24,
        boxShadow: '0 -8px 24px rgba(0,0,0,0.16)', display: 'flex', flexDirection: 'column',
      }}>
        <div style={{ width: 36, height: 4, borderRadius: 99, background: 'rgba(33,33,33,0.18)', margin: '10px auto 4px' }} />
        {title && (
          <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', padding: '6px 22px 10px' }}>
            <div style={{ fontFamily: 'var(--serif)', fontSize: 22, letterSpacing: '-0.01em', color: 'var(--ink)' }}>{title}</div>
            <div style={{ width: 30, height: 30, display: 'flex', alignItems: 'center', justifyContent: 'center' }}><Icon name="close" size={20} stroke="var(--ink-2)" strokeWidth={1.7} /></div>
          </div>
        )}
        <div className="proto-scroll" style={{ flex: 1, padding: '0 22px 38px' }}>{children}</div>
      </div>
    </div>
  );
}

function InlineError({ children }) {
  return (
    <div style={{ display: 'flex', alignItems: 'center', gap: 6, marginTop: 8, color: 'var(--danger)', fontSize: 12.5, fontWeight: 500 }}>
      <Icon name="close" size={13} stroke="var(--danger)" strokeWidth={2.4} />
      {children}
    </div>
  );
}

function Field({ children, error }) {
  return (
    <div style={{
      padding: '13px 15px', background: 'var(--card)',
      border: '1.4px solid ' + (error ? 'var(--danger)' : 'var(--line)'),
      borderRadius: 13, display: 'flex', alignItems: 'center', justifyContent: 'space-between',
    }}>{children}</div>
  );
}

function EdgeTitle({ children, sub }) {
  return (
    <div style={{ padding: '4px 22px 12px' }}>
      <div style={{ fontFamily: 'var(--serif)', fontSize: 26, letterSpacing: '-0.015em', color: 'var(--ink)' }}>{children}</div>
      {sub && <div style={{ fontSize: 12.5, color: 'var(--muted)', marginTop: 2 }}>{sub}</div>}
    </div>
  );
}

// ═════════════════════════════════════════════════════════════════════
// FLOW 01 — QUICK LOG
// ═════════════════════════════════════════════════════════════════════

// Draft restore — shown before PIN on relaunch
function EdgeDraftRestore() {
  return (
    <PhoneShell>
      <div style={{ height: '100%', background: 'var(--paper)' }} />
      <EdgeDialog
        icon="note" tone="blue"
        title="Unfinished expense"
        body={<>You had <strong>$12.50</strong> in progress when the app closed. Continue where you left off, or discard it?</>}
        actions={<>
          <button style={btnSecondary}>Discard</button>
          <button style={btnPrimary}>Continue</button>
        </>}
      />
    </PhoneShell>
  );
}

// Note at 200/200 cap
function EdgeNoteCap() {
  const note = "Team dinner at the rooftop place downtown — split the tasting menu, two bottles of wine, plus the service charge. Reimbursing Alex for my half since he put it all on his card this time around again tonight ok.";
  return (
    <PhoneShell>
      <div style={{ height: '100%', display: 'flex', flexDirection: 'column' }}>
        <NavBar left={<BackBtn label="Amount" />} title="Details" right={<span style={{ width: 50 }} />} />
        <div style={{ flex: 1, padding: '12px 22px' }}>
          <div style={{ display: 'flex', justifyContent: 'flex-end', marginBottom: 6 }}>
            <span style={{ fontSize: 11.5, fontFamily: 'var(--mono)', color: 'var(--danger)', fontWeight: 600 }}>200/200</span>
          </div>
          <div style={{ padding: '12px 14px', background: 'var(--card)', border: '1.4px solid var(--danger)', borderRadius: 14, display: 'flex', gap: 10, alignItems: 'flex-start' }}>
            <Icon name="note" size={18} stroke="var(--ink-3)" strokeWidth={1.6} style={{ marginTop: 2, flexShrink: 0 }} />
            <span style={{ fontSize: 13.5, color: 'var(--ink)', lineHeight: 1.45 }}>{note}</span>
          </div>
          <InlineError>Note can't exceed 200 characters</InlineError>
        </div>
        <div style={{ padding: '0 22px 38px' }}><button style={btnPrimaryFull}>Save expense · $86.00</button></div>
      </div>
    </PhoneShell>
  );
}

// @ tag mutual exclusion — Event picked, Debts greyed
function EdgeTagExclusion() {
  return (
    <PhoneShell>
      <div style={{ height: '100%', background: 'var(--paper)' }} />
      <EdgeBottomSheet title="Link to…" height={520}>
        <div style={{ fontSize: 12.5, color: 'var(--ink-2)', marginBottom: 16, lineHeight: 1.4 }}>
          Pick one event <strong>or</strong> one debt — not both. Choosing one greys out the other.
        </div>
        <SectionTitle>Events</SectionTitle>
        <div style={{ marginTop: 8, marginBottom: 18, display: 'flex', flexDirection: 'column', gap: 6 }}>
          <PickRow icon="feat-events" tint="var(--blue-100)" stroke="var(--blue-700)" title="Bali Trip" sub="May 12 — May 26" selected />
          <PickRow icon="feat-events" tint="var(--blue-100)" stroke="var(--blue-700)" title="John's Wedding" sub="Jun 04 — Jun 06" />
        </div>
        <div style={{ opacity: 0.38, pointerEvents: 'none' }}>
          <div style={{ display: 'flex', alignItems: 'center', gap: 8 }}>
            <SectionTitle>Debts</SectionTitle>
            <span style={{ fontSize: 10.5, color: 'var(--muted)', fontStyle: 'italic' }}>· unavailable while an event is selected</span>
          </div>
          <div style={{ marginTop: 8, display: 'flex', flexDirection: 'column', gap: 6 }}>
            <PickRow icon="feat-debt" tint="var(--gray-100)" stroke="var(--muted)" title="Lent · John" sub="$50" />
            <PickRow icon="feat-debt" tint="var(--gray-100)" stroke="var(--muted)" title="Owe · Sarah" sub="$30" />
          </div>
        </div>
      </EdgeBottomSheet>
    </PhoneShell>
  );
}

function PickRow({ icon, tint, stroke, title, sub, selected }) {
  return (
    <div style={{
      display: 'flex', alignItems: 'center', gap: 12, padding: '12px 14px',
      background: selected ? 'var(--blue-100)' : 'var(--card)',
      border: '1px solid ' + (selected ? 'var(--blue-500)' : 'var(--line)'),
      borderRadius: 12,
    }}>
      <div style={{ width: 34, height: 34, borderRadius: 99, background: tint, display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
        <Icon name={icon} size={16} stroke={stroke} strokeWidth={1.8} />
      </div>
      <div style={{ flex: 1 }}>
        <div style={{ fontSize: 14, fontWeight: 500, color: 'var(--ink)' }}>{title}</div>
        <div style={{ fontSize: 11.5, color: 'var(--muted)' }}>{sub}</div>
      </div>
      {selected && <Icon name="check" size={18} stroke="var(--blue-700)" strokeWidth={2.4} />}
    </div>
  );
}

// ═════════════════════════════════════════════════════════════════════
// FLOW 02 — JOURNAL
// ═════════════════════════════════════════════════════════════════════

// Search active — no results
function EdgeJournalNoResults() {
  return (
    <PhoneShell>
      <div style={{ height: '100%', display: 'flex', flexDirection: 'column' }}>
        <div style={{ padding: '14px 22px 0' }}>
          <div style={{ fontFamily: 'var(--serif)', fontSize: 28, letterSpacing: '-0.015em' }}>Journal</div>
          <div style={{ marginTop: 14, display: 'flex', alignItems: 'center', gap: 10, padding: '11px 14px', background: 'var(--card)', border: '1.4px solid var(--blue-500)', borderRadius: 13 }}>
            <Icon name="search" size={17} stroke="var(--blue-500)" strokeWidth={1.8} />
            <span style={{ fontSize: 14, color: 'var(--ink)', flex: 1 }}>concert tickets</span>
            <Icon name="close" size={16} stroke="var(--muted)" strokeWidth={2} />
          </div>
        </div>
        <div style={{ flex: 1, display: 'flex', flexDirection: 'column', alignItems: 'center', justifyContent: 'center', padding: '0 40px', textAlign: 'center' }}>
          <div style={{ width: 84, height: 84, borderRadius: 99, background: 'var(--gray-100)', display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
            <Icon name="search" size={36} stroke="var(--muted-2)" strokeWidth={1.5} />
          </div>
          <div style={{ marginTop: 18, fontFamily: 'var(--serif)', fontSize: 20, color: 'var(--ink)' }}>No matches</div>
          <div style={{ marginTop: 6, fontSize: 13.5, color: 'var(--muted)', lineHeight: 1.5 }}>
            Nothing found for "concert tickets." Try a different keyword, amount, or note.
          </div>
        </div>
      </div>
    </PhoneShell>
  );
}

// Long-press → quick note bottom sheet
function EdgeQuickNote() {
  return (
    <PhoneShell>
      <div style={{ height: '100%', background: 'var(--paper)' }} />
      <EdgeBottomSheet title="Quick note" height={360}>
        <div style={{ display: 'flex', alignItems: 'center', gap: 12, padding: '12px 0 16px', borderBottom: '1px solid var(--line-2)' }}>
          <CatBadge cat="food" size={40} />
          <div style={{ flex: 1 }}>
            <div style={{ fontSize: 14, fontWeight: 500, color: 'var(--ink)' }}>Lunch with M.</div>
            <div style={{ fontSize: 11.5, color: 'var(--muted)' }}>Food · 12:30 PM</div>
          </div>
          <div style={{ fontFamily: 'var(--serif)', fontSize: 18 }}>$12.40</div>
        </div>
        <div style={{ marginTop: 14, padding: '12px 14px', background: 'var(--card)', border: '1.4px solid var(--blue-500)', borderRadius: 13, minHeight: 86, fontSize: 14, color: 'var(--ink)', lineHeight: 1.5 }}>
          Split the bill — M. covered the tip<span className="proto-cursor" />
        </div>
        <div style={{ marginTop: 16 }}><button style={btnPrimaryFull}>Save note</button></div>
      </EdgeBottomSheet>
    </PhoneShell>
  );
}

// ═════════════════════════════════════════════════════════════════════
// FLOW 03 — REPORTS / CATEGORIES
// ═════════════════════════════════════════════════════════════════════

// Reports — all uncategorized (single full-circle donut + tip)
function EdgeReportsUncategorized() {
  return (
    <PhoneShell>
      <div style={{ height: '100%', display: 'flex', flexDirection: 'column' }}>
        <NavBar left={<BackBtn label="More" />} title="Reports" right={<span style={{ width: 50 }} />} />
        <div style={{ flex: 1, padding: '8px 22px', display: 'flex', flexDirection: 'column', alignItems: 'center' }}>
          <div style={{ fontSize: 11, fontFamily: 'var(--mono)', textTransform: 'uppercase', letterSpacing: '0.08em', color: 'var(--muted)' }}>May 2026 · total spent</div>
          <div style={{ fontFamily: 'var(--serif)', fontSize: 40, letterSpacing: '-0.02em', marginTop: 4 }}>$1,247</div>
          <div style={{ position: 'relative', marginTop: 20 }}>
            <svg width="180" height="180" viewBox="0 0 42 42">
              <circle cx="21" cy="21" r="15.91" fill="transparent" stroke="var(--gray-300)" strokeWidth="5" />
            </svg>
            <div style={{ position: 'absolute', inset: 0, display: 'flex', flexDirection: 'column', alignItems: 'center', justifyContent: 'center' }}>
              <div style={{ fontSize: 12, color: 'var(--muted)' }}>100%</div>
              <div style={{ fontSize: 11, color: 'var(--muted-2)' }}>Uncategorized</div>
            </div>
          </div>
          <div style={{ marginTop: 24, width: '100%', padding: '14px 16px', background: 'var(--blue-100)', borderRadius: 14, display: 'flex', gap: 10, alignItems: 'flex-start' }}>
            <Icon name="sparkle" size={18} stroke="var(--blue-700)" strokeWidth={1.8} style={{ flexShrink: 0, marginTop: 1 }} />
            <span style={{ fontSize: 13, color: 'var(--blue-700)', lineHeight: 1.45 }}>Tip: categorize your expenses to unlock a spending breakdown by category.</span>
          </div>
        </div>
      </div>
    </PhoneShell>
  );
}

// Category — duplicate name inline error
function EdgeCategoryDuplicate() {
  return (
    <PhoneShell>
      <div style={{ height: '100%', background: 'var(--paper)' }} />
      <EdgeBottomSheet title="New category" height={420}>
        <div style={{ marginTop: 4 }}>
          <Field error>
            <span style={{ fontSize: 15, color: 'var(--ink)' }}>Food</span>
            <span style={{ fontSize: 11, fontFamily: 'var(--mono)', color: 'var(--danger)' }}>4/20</span>
          </Field>
          <InlineError>A category with this name already exists</InlineError>
        </div>
        <div style={{ marginTop: 18, display: 'flex', gap: 10, alignItems: 'center' }}>
          <span style={{ fontSize: 12, color: 'var(--muted)' }}>Icon</span>
          <div style={{ display: 'flex', gap: 8 }}>
            {['cat-food','cat-coffee','cat-health','cat-pet'].map((ic,i) => (
              <div key={ic} style={{ width: 40, height: 40, borderRadius: 11, background: i===0?'var(--blue-100)':'var(--gray-100)', border: i===0?'1.4px solid var(--blue-500)':'1px solid var(--line)', display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
                <Icon name={ic} size={18} stroke={i===0?'var(--blue-700)':'var(--ink-3)'} strokeWidth={1.7} />
              </div>
            ))}
          </div>
        </div>
        <div style={{ marginTop: 22 }}><button style={{ ...btnPrimaryFull, opacity: 0.45 }}>Add category</button></div>
      </EdgeBottomSheet>
    </PhoneShell>
  );
}

// ═════════════════════════════════════════════════════════════════════
// FLOW 05 — PIN
// ═════════════════════════════════════════════════════════════════════

// PIN mismatch on confirm
function EdgePinMismatch() {
  return (
    <PhoneShell>
      <div style={{ height: '100%', display: 'flex', flexDirection: 'column' }}>
        <NavBar left={<BackBtn label="Settings" />} title="Set PIN" right={<span style={{ width: 50 }} />} />
        <div style={{ flex: 1, display: 'flex', flexDirection: 'column', alignItems: 'center', paddingTop: 30 }}>
          <div style={{ fontFamily: 'var(--serif)', fontSize: 24, color: 'var(--ink)' }}>Confirm your PIN</div>
          <div style={{ marginTop: 8, fontSize: 13, color: 'var(--danger)', fontWeight: 500 }}>PINs do not match. Try again.</div>
          <div style={{ marginTop: 28, display: 'flex', gap: 14 }} className="proto-shake">
            {[1,2,3,4,5,6].map(i => (
              <div key={i} style={{ width: 14, height: 14, borderRadius: 99, background: 'transparent', border: '1.6px solid var(--danger)' }} />
            ))}
          </div>
          <div style={{ marginTop: 'auto', width: '100%', padding: '0 28px 20px' }}>
            <NumPad />
          </div>
        </div>
      </div>
    </PhoneShell>
  );
}

// Wrong PIN on entry — shake
function EdgePinWrong() {
  return (
    <PhoneShell>
      <div style={{ height: '100%', display: 'flex', flexDirection: 'column', alignItems: 'center' }}>
        <div style={{ marginTop: 64, width: 52, height: 52, borderRadius: 99, background: 'var(--blue-100)', display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
          <Icon name="eye" size={24} stroke="var(--blue-700)" strokeWidth={1.7} />
        </div>
        <div style={{ marginTop: 22, fontFamily: 'var(--serif)', fontSize: 26, letterSpacing: '-0.015em' }}>Enter your PIN</div>
        <div style={{ marginTop: 6, fontSize: 13, color: 'var(--danger)', fontWeight: 500 }}>Incorrect PIN, try again</div>
        <div style={{ marginTop: 24, display: 'flex', gap: 14 }} className="proto-shake">
          {[1,2,3,4,5,6].map(i => (
            <div key={i} style={{ width: 14, height: 14, borderRadius: 99, background: 'transparent', border: '1.6px solid var(--danger)' }} />
          ))}
        </div>
        <div style={{ marginTop: 'auto', width: '100%', padding: '0 28px 20px' }}><NumPad /></div>
      </div>
    </PhoneShell>
  );
}

// Forgot PIN — security question recovery
function EdgePinRecovery() {
  return (
    <PhoneShell>
      <div style={{ height: '100%', display: 'flex', flexDirection: 'column' }}>
        <NavBar left={<BackBtn label="" />} title="Recover PIN" right={<span style={{ width: 50 }} />} />
        <div style={{ flex: 1, padding: '12px 22px' }}>
          <div style={{ width: 48, height: 48, borderRadius: 99, background: 'var(--blue-100)', display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
            <Icon name="sparkle" size={22} stroke="var(--blue-700)" strokeWidth={1.7} />
          </div>
          <div style={{ marginTop: 16, fontFamily: 'var(--serif)', fontSize: 23, letterSpacing: '-0.01em', lineHeight: 1.2 }}>Answer your security question</div>
          <div style={{ marginTop: 14, fontSize: 12, fontFamily: 'var(--mono)', textTransform: 'uppercase', letterSpacing: '0.06em', color: 'var(--muted)' }}>Question</div>
          <div style={{ marginTop: 6, fontSize: 15, color: 'var(--ink)' }}>What was your first pet's name?</div>
          <div style={{ marginTop: 18, padding: '13px 15px', background: 'var(--card)', border: '1.4px solid var(--blue-500)', borderRadius: 13, fontSize: 15, color: 'var(--ink)' }}>
            Biscuit<span className="proto-cursor" />
          </div>
          <div style={{ marginTop: 12, fontSize: 12, color: 'var(--muted)' }}>2 of 5 attempts used</div>
        </div>
        <div style={{ padding: '0 22px 38px' }}><button style={btnPrimaryFull}>Verify &amp; reset PIN</button></div>
      </div>
    </PhoneShell>
  );
}

function NumPad() {
  const keys = ['1','2','3','4','5','6','7','8','9','','0','⌫'];
  return (
    <div style={{ display: 'grid', gridTemplateColumns: 'repeat(3,1fr)', gap: 10 }}>
      {keys.map((k,i) => k === '' ? <div key={i} /> : (
        <div key={i} style={{ padding: '14px 0', textAlign: 'center', fontFamily: 'var(--serif)', fontSize: 24, color: 'var(--ink)', background: 'var(--card)', border: '1px solid var(--line)', borderRadius: 14 }}>{k}</div>
      ))}
    </div>
  );
}

// ═════════════════════════════════════════════════════════════════════
// FLOW 06 — EVENT BUDGET
// ═════════════════════════════════════════════════════════════════════

// Create event — $0 budget + duplicate name errors
function EdgeEventErrors() {
  return (
    <PhoneShell>
      <div style={{ height: '100%', background: 'var(--paper)' }} />
      <EdgeBottomSheet title="New event" height={540}>
        <div style={{ marginTop: 4, display: 'flex', flexDirection: 'column', gap: 18 }}>
          <div>
            <Field error>
              <span style={{ fontSize: 15, color: 'var(--ink)' }}>Bali Trip</span>
              <span style={{ fontSize: 11, fontFamily: 'var(--mono)', color: 'var(--muted)' }}>9/30</span>
            </Field>
            <InlineError>An event with this name already exists</InlineError>
          </div>
          <div style={{ display: 'flex', gap: 10 }}>
            <Field><span style={{ fontSize: 14, color: 'var(--ink-2)' }}>May 12</span></Field>
            <Field><span style={{ fontSize: 14, color: 'var(--ink-2)' }}>May 26</span></Field>
          </div>
          <div>
            <Field error>
              <span style={{ fontSize: 15, color: 'var(--muted-2)' }}>$0</span>
              <Icon name="cat-bills" size={18} stroke="var(--muted)" strokeWidth={1.6} />
            </Field>
            <InlineError>Budget must be greater than $0</InlineError>
          </div>
        </div>
        <div style={{ marginTop: 22 }}><button style={{ ...btnPrimaryFull, opacity: 0.45 }}>Create event</button></div>
      </EdgeBottomSheet>
    </PhoneShell>
  );
}

// Over budget 100–110% warning (amber)
function EdgeEventWarning() {
  return (
    <PhoneShell>
      <div style={{ height: '100%', display: 'flex', flexDirection: 'column' }}>
        <NavBar left={<BackBtn label="Events" />} title="" right={<div style={{ width: 32, height: 32, display: 'flex', alignItems: 'center', justifyContent: 'center' }}><Icon name="more" size={20} stroke="var(--ink-2)" strokeWidth={2} /></div>} />
        <div style={{ flex: 1, padding: '4px 22px' }}>
          <div style={{ fontFamily: 'var(--serif)', fontSize: 24, letterSpacing: '-0.01em' }}>Birthday party</div>
          <div style={{ fontSize: 11.5, color: 'var(--muted)' }}>Apr 28 · active</div>
          <div style={{ marginTop: 18, background: 'var(--card)', border: '1px solid rgba(178,106,0,0.4)', borderRadius: 18, padding: 20 }}>
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start' }}>
              <div>
                <div style={{ fontSize: 10.5, fontFamily: 'var(--mono)', textTransform: 'uppercase', letterSpacing: '0.08em', color: '#b26a00' }}>Over budget</div>
                <div style={{ fontFamily: 'var(--serif)', fontSize: 40, letterSpacing: '-0.02em', marginTop: 4, color: '#b26a00' }}>−$22</div>
              </div>
              <div style={{ textAlign: 'right', fontSize: 11, color: 'var(--muted)' }}>
                <div>Spent $422</div>
                <div style={{ marginTop: 6 }}>Budget $400</div>
              </div>
            </div>
            <div style={{ marginTop: 16, height: 8, background: 'var(--gray-200)', borderRadius: 99, overflow: 'hidden' }}>
              <div style={{ width: '100%', height: '100%', background: '#d99100', borderRadius: 99 }} />
            </div>
            <div style={{ marginTop: 10, fontSize: 12, color: '#b26a00', fontWeight: 500 }}>Over budget by $22 (105%)</div>
          </div>
        </div>
      </div>
    </PhoneShell>
  );
}

// Closed event — read-only
function EdgeEventClosed() {
  return (
    <PhoneShell>
      <div style={{ height: '100%', display: 'flex', flexDirection: 'column' }}>
        <NavBar left={<BackBtn label="Events" />} title="" right={<span style={{ width: 32 }} />} />
        <div style={{ flex: 1, padding: '4px 22px' }}>
          <div style={{ display: 'flex', alignItems: 'center', gap: 8 }}>
            <div style={{ fontFamily: 'var(--serif)', fontSize: 24, letterSpacing: '-0.01em', color: 'var(--ink-2)' }}>John's Wedding</div>
            <span style={{ fontSize: 9.5, fontFamily: 'var(--mono)', textTransform: 'uppercase', letterSpacing: '0.08em', padding: '3px 8px', border: '1px solid var(--line-strong)', borderRadius: 99, color: 'var(--muted)' }}>Closed</span>
          </div>
          <div style={{ fontSize: 11.5, color: 'var(--muted)' }}>Jun 04 — Jun 06 · archived</div>
          <div style={{ marginTop: 18, background: 'var(--gray-100)', border: '1px solid var(--line)', borderRadius: 18, padding: 20 }}>
            <div style={{ fontSize: 10.5, fontFamily: 'var(--mono)', textTransform: 'uppercase', letterSpacing: '0.08em', color: 'var(--muted)' }}>Final · remaining</div>
            <div style={{ fontFamily: 'var(--serif)', fontSize: 38, letterSpacing: '-0.02em', marginTop: 4, color: 'var(--ink-2)' }}>$340</div>
            <div style={{ marginTop: 14, height: 8, background: 'var(--gray-200)', borderRadius: 99, overflow: 'hidden' }}>
              <div style={{ width: '58%', height: '100%', background: 'var(--muted-2)', borderRadius: 99 }} />
            </div>
          </div>
          <div style={{ marginTop: 16, padding: '12px 14px', background: 'var(--gray-100)', borderRadius: 12, display: 'flex', gap: 10, alignItems: 'center' }}>
            <Icon name="eye" size={17} stroke="var(--muted)" strokeWidth={1.7} />
            <span style={{ fontSize: 12.5, color: 'var(--muted)', lineHeight: 1.4 }}>Read-only — the 24-hour edit window has passed. No new expenses can be linked.</span>
          </div>
        </div>
        <div style={{ padding: '0 22px 38px' }}>
          <button style={{ ...btnSecondaryFull, opacity: 0.5 }}>Add expense</button>
        </div>
      </div>
    </PhoneShell>
  );
}

// ═════════════════════════════════════════════════════════════════════
// FLOW 07 — DEBT
// ═════════════════════════════════════════════════════════════════════

// Same person on opposite side — soft warning
function EdgeDebtConflict() {
  return (
    <PhoneShell>
      <div style={{ height: '100%', background: 'var(--paper)' }} />
      <EdgeDialog
        icon="feat-debt" tone="warn"
        title="John already has a record"
        body={<>John is on your <strong>"I Owe"</strong> list for $30. Do you want to add this <strong>"I Lent"</strong> record too?</>}
        actions={<>
          <button style={btnSecondary}>Cancel</button>
          <button style={{ ...btnPrimary, background: '#b26a00', borderColor: '#b26a00' }}>Continue</button>
        </>}
      />
    </PhoneShell>
  );
}

// Settled section — delete confirm
function EdgeDebtSettled() {
  return (
    <PhoneShell>
      <div style={{ height: '100%', background: 'var(--paper)' }}>
        <div style={{ padding: '14px 22px' }}>
          <div style={{ fontFamily: 'var(--serif)', fontSize: 26, letterSpacing: '-0.015em' }}>Debt</div>
          <div style={{ marginTop: 14 }}><SectionTitle>Settled</SectionTitle></div>
          <div style={{ marginTop: 8, display: 'flex', flexDirection: 'column', gap: 8 }}>
            {[['Aiko','$20','Apr 14'],['Liam','$15','Apr 02']].map(([n,a,d]) => (
              <div key={n} style={{ display: 'flex', alignItems: 'center', gap: 12, padding: '12px 14px', background: 'var(--card)', border: '1px solid var(--line)', borderRadius: 13, opacity: 0.6 }}>
                <div style={{ width: 34, height: 34, borderRadius: 99, background: 'var(--gray-100)', display: 'flex', alignItems: 'center', justifyContent: 'center', fontFamily: 'var(--serif)', fontWeight: 600, color: 'var(--muted)' }}>{n[0]}</div>
                <div style={{ flex: 1 }}>
                  <div style={{ fontSize: 14, color: 'var(--ink-2)', textDecoration: 'line-through' }}>{n}</div>
                  <div style={{ fontSize: 11, color: 'var(--muted)' }}>Settled · {d}</div>
                </div>
                <div style={{ fontFamily: 'var(--serif)', fontSize: 15, color: 'var(--muted)' }}>{a}</div>
              </div>
            ))}
          </div>
        </div>
      </div>
      <EdgeDialog
        icon="close" tone="danger"
        title="Delete this record?"
        body="Removing Aiko's settled record can't be undone."
        actions={<>
          <button style={btnSecondary}>Cancel</button>
          <button style={{ ...btnPrimary, background: 'var(--danger)', borderColor: 'var(--danger)' }}>Delete</button>
        </>}
      />
    </PhoneShell>
  );
}

// ═════════════════════════════════════════════════════════════════════
// FLOW 08 — SHARED COSTS
// ═════════════════════════════════════════════════════════════════════

// $0 total — Save disabled
function EdgeSharedZero() {
  return (
    <PhoneShell>
      <div style={{ height: '100%', display: 'flex', flexDirection: 'column' }}>
        <NavBar left={<BackBtn label="More" />} title="Split a bill" right={<span style={{ width: 50 }} />} />
        <div style={{ flex: 1, padding: '12px 22px', display: 'flex', flexDirection: 'column', alignItems: 'center' }}>
          <div style={{ fontSize: 11, fontFamily: 'var(--mono)', textTransform: 'uppercase', letterSpacing: '0.08em', color: 'var(--muted)' }}>Total bill</div>
          <div style={{ fontFamily: 'var(--serif)', fontSize: 56, letterSpacing: '-0.025em', color: 'var(--muted-2)', marginTop: 4 }}>$0</div>
          <InlineError>Total amount must be greater than $0</InlineError>
        </div>
        <div style={{ padding: '0 22px 38px' }}><button style={{ ...btnPrimaryFull, opacity: 0.45 }}>Save split</button></div>
      </div>
    </PhoneShell>
  );
}

// Stepper limits — max 20 reached + custom split
function EdgeSharedLimits() {
  return (
    <PhoneShell>
      <div style={{ height: '100%', display: 'flex', flexDirection: 'column' }}>
        <NavBar left={<BackBtn label="More" />} title="Split a bill" right={<span style={{ width: 50 }} />} />
        <div style={{ flex: 1, padding: '12px 22px' }}>
          <div style={{ textAlign: 'center' }}>
            <div style={{ fontSize: 11, fontFamily: 'var(--mono)', textTransform: 'uppercase', letterSpacing: '0.08em', color: 'var(--muted)' }}>Total bill</div>
            <div style={{ fontFamily: 'var(--serif)', fontSize: 44, letterSpacing: '-0.02em', marginTop: 2 }}>$240</div>
          </div>
          {/* stepper at max */}
          <div style={{ marginTop: 18, display: 'flex', alignItems: 'center', justifyContent: 'space-between', background: 'var(--card)', border: '1px solid var(--line)', borderRadius: 14, padding: '10px 14px' }}>
            <div style={{ width: 46, height: 46, borderRadius: 12, border: '1.4px solid var(--line-strong)', display: 'flex', alignItems: 'center', justifyContent: 'center', color: 'var(--ink)' }}>
              <Icon name="back" size={20} stroke="var(--ink)" strokeWidth={2} style={{ transform: 'rotate(-90deg)' }} />
            </div>
            <div style={{ textAlign: 'center' }}>
              <div style={{ fontFamily: 'var(--serif)', fontSize: 30, lineHeight: 1 }}>20</div>
              <div style={{ fontSize: 10, color: 'var(--muted)' }}>people</div>
            </div>
            <div style={{ width: 46, height: 46, borderRadius: 12, border: '1.4px solid var(--line)', display: 'flex', alignItems: 'center', justifyContent: 'center', opacity: 0.4 }}>
              <Icon name="plus" size={20} stroke="var(--muted)" strokeWidth={2} />
            </div>
          </div>
          <div style={{ marginTop: 6, fontSize: 11.5, color: 'var(--muted)', textAlign: 'center' }}>Maximum of 20 people reached</div>
          {/* custom split rows */}
          <div style={{ marginTop: 18, display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
            <SectionTitle>Custom split</SectionTitle>
            <span style={{ fontSize: 11, color: 'var(--blue-700)', fontWeight: 500 }}>Switch to equal</span>
          </div>
          <div style={{ marginTop: 8, display: 'flex', flexDirection: 'column', gap: 8 }}>
            {[['Person 1','$80'],['Person 2','$60'],['Person 3','$100']].map(([n,a],i) => (
              <div key={i} style={{ display: 'flex', alignItems: 'center', gap: 10, background: 'var(--card)', border: '1px solid var(--line)', borderRadius: 12, padding: '10px 14px' }}>
                <div style={{ width: 26, height: 26, borderRadius: 99, background: 'var(--blue-100)', color: 'var(--blue-700)', display: 'flex', alignItems: 'center', justifyContent: 'center', fontSize: 11, fontFamily: 'var(--serif)', fontWeight: 600 }}>{i+1}</div>
                <span style={{ flex: 1, fontSize: 13.5, color: 'var(--ink)' }}>{n}</span>
                <span style={{ fontFamily: 'var(--serif)', fontSize: 16, color: 'var(--ink)' }}>{a}</span>
              </div>
            ))}
          </div>
        </div>
        <div style={{ padding: '0 22px 38px' }}><button style={btnPrimaryFull}>Save split · $240</button></div>
      </div>
    </PhoneShell>
  );
}

// ─────────────────────────────────────────────────────────────────────
// Button styles
// ─────────────────────────────────────────────────────────────────────
const btnPrimary = { flex: 1, padding: '13px 0', background: 'var(--clay)', color: '#fffdf6', border: '1.4px solid var(--clay)', borderRadius: 12, fontSize: 14, fontWeight: 600, cursor: 'pointer' };
const btnSecondary = { flex: 1, padding: '13px 0', background: 'transparent', color: 'var(--ink)', border: '1.4px solid var(--line-strong)', borderRadius: 12, fontSize: 14, fontWeight: 600, cursor: 'pointer' };
const btnPrimaryFull = { ...btnPrimary, width: '100%', flex: 'none', padding: '16px 0', fontSize: 15 };
const btnSecondaryFull = { ...btnSecondary, width: '100%', flex: 'none', padding: '16px 0', fontSize: 15 };

Object.assign(window, {
  EdgeDraftRestore, EdgeNoteCap, EdgeTagExclusion,
  EdgeJournalNoResults, EdgeQuickNote,
  EdgeReportsUncategorized, EdgeCategoryDuplicate,
  EdgePinMismatch, EdgePinWrong, EdgePinRecovery,
  EdgeEventErrors, EdgeEventWarning, EdgeEventClosed,
  EdgeDebtConflict, EdgeDebtSettled,
  EdgeSharedZero, EdgeSharedLimits,
});
