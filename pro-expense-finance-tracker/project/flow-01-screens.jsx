// Flow 01 screens — Home, Add Amount, Add Details
// + Bottom sheets (Tag picker, Date+Time, Category picker)

// ────────────────────────────────────────────────────────────────────
// HOME
// ────────────────────────────────────────────────────────────────────
function HomeScreen({ navDir, expenses, persona, newRowId, activeNav, onTab, onAdd, onPersona }) {
  const monthTotal = expenses.reduce((s, e) => s + e.amount, 0);
  return (
    <div className={'proto-scroll ' + (navDir === 'back' ? 'proto-screen-back' : 'proto-screen-fwd')} style={{ height: '100%', display: 'flex', flexDirection: 'column' }}>
      <div style={{ padding: '14px 22px 0', flexShrink: 0 }}>
        {/* greeting row */}
        <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
          <div>
            <div style={{ fontSize: 11, color: 'var(--muted)', fontFamily: 'var(--mono)', letterSpacing: '0.06em', textTransform: 'uppercase' }}>Wed · May 25</div>
            <div style={{ fontFamily: 'var(--serif)', fontSize: 30, lineHeight: 1.05, letterSpacing: '-0.015em', marginTop: 4 }}>
              Hi, <em style={{ color: 'var(--clay)', fontStyle: 'italic' }}>Maya</em>
            </div>
          </div>
          <button className="proto-tap" style={{
            width: 40, height: 40, borderRadius: 99, border: '1px solid var(--line)',
            background: 'var(--card)', display: 'flex', alignItems: 'center', justifyContent: 'center', cursor: 'pointer',
          }}>
            <Icon name="bell" size={18} stroke="var(--ink-2)" strokeWidth={1.6} />
          </button>
        </div>

        {/* contextual header card */}
        <ContextHeader persona={persona} total={monthTotal} onCycle={() => {
          const next = persona === 'casual' ? 'budget' : persona === 'budget' ? 'event' : 'casual';
          onPersona(next);
        }} />

        {/* quick access */}
        <div style={{ marginTop: 22 }}>
          <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', marginBottom: 10 }}>
            <div style={{ fontSize: 11, fontFamily: 'var(--mono)', textTransform: 'uppercase', letterSpacing: '0.08em', color: 'var(--ink-3)', fontWeight: 600 }}>Quick access</div>
            <div style={{ fontSize: 11, color: 'var(--muted)' }}>Customize</div>
          </div>
          <div style={{ display: 'grid', gridTemplateColumns: 'repeat(4, 1fr)', gap: 8 }}>
            <QuickAccess label="Reports" icon="feat-reports" tint="var(--blue-100)" stroke="var(--blue-700)" />
            <QuickAccess label="Debt"    icon="feat-debt"    tint="var(--blue-100)" stroke="var(--blue-700)" />
            <QuickAccess label="Split"   icon="feat-split"   tint="var(--blue-100)" stroke="var(--blue-700)" />
            <QuickAccess label="Events"  icon="feat-events"  tint="var(--blue-100)" stroke="var(--blue-700)" />
          </div>
        </div>
      </div>

      {/* recent transactions */}
      <div style={{ flex: 1, marginTop: 24, display: 'flex', flexDirection: 'column', minHeight: 0 }}>
        <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', padding: '0 22px 10px' }}>
          <div style={{ fontSize: 11, fontFamily: 'var(--mono)', textTransform: 'uppercase', letterSpacing: '0.08em', color: 'var(--ink-3)', fontWeight: 600 }}>Recent</div>
          <div style={{ fontSize: 12, color: 'var(--clay)', fontWeight: 500 }}>See all</div>
        </div>
        <div className="proto-scroll" style={{ flex: 1, padding: '0 22px 104px' }}>
          {expenses.map((e) => (
            <TxnRow key={e.id} txn={e} fresh={e.id === newRowId} />
          ))}
          <div style={{ height: 16 }} />
        </div>
      </div>
    </div>
  );
}

function ContextHeader({ persona, total, onCycle }) {
  if (persona === 'casual') {
    return (
      <Card style={{ marginTop: 18 }} onCycle={onCycle}>
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start' }}>
          <div>
            <div style={{ fontSize: 10.5, fontFamily: 'var(--mono)', textTransform: 'uppercase', letterSpacing: '0.08em', color: 'var(--muted)' }}>Spent · May</div>
            <div style={{ fontFamily: 'var(--serif)', fontSize: 42, lineHeight: 1, letterSpacing: '-0.02em', marginTop: 6, color: 'var(--ink)' }}>
              {currencyFmt(total)}
            </div>
            <div style={{ marginTop: 6, fontSize: 12, color: 'var(--ink-3)' }}>
              <span style={{ color: 'var(--sage)' }}>↓ 12%</span> from last month
            </div>
          </div>
          <SparkLine />
        </div>
      </Card>
    );
  }
  if (persona === 'budget') {
    const budget = 500;
    const pct = Math.min(100, (total / budget) * 100);
    return (
      <Card style={{ marginTop: 18 }} onCycle={onCycle}>
        <div style={{ fontSize: 10.5, fontFamily: 'var(--mono)', textTransform: 'uppercase', letterSpacing: '0.08em', color: 'var(--muted)' }}>This month</div>
        <div style={{ display: 'flex', alignItems: 'baseline', gap: 8, marginTop: 4 }}>
          <div style={{ fontFamily: 'var(--serif)', fontSize: 36, lineHeight: 1, letterSpacing: '-0.02em' }}>{currencyFmt(total)}</div>
          <div style={{ fontFamily: 'var(--serif)', fontSize: 18, color: 'var(--muted)' }}>/ {currencyFmt(budget)}</div>
        </div>
        <div style={{ marginTop: 14, height: 8, background: 'rgba(43,31,23,0.08)', borderRadius: 99, overflow: 'hidden' }}>
          <div style={{ width: pct + '%', height: '100%', background: 'var(--clay)', borderRadius: 99 }} />
        </div>
        <div style={{ marginTop: 8, fontSize: 11, color: 'var(--ink-3)', display: 'flex', justifyContent: 'space-between' }}>
          <span>{Math.round(pct)}% used</span>
          <span>{currencyFmt(budget - total)} left</span>
        </div>
      </Card>
    );
  }
  // event
  const ev = { name: 'Bali Trip', remaining: 1240, budget: 2000, days: 14 };
  const pct = Math.min(100, ((ev.budget - ev.remaining) / ev.budget) * 100);
  return (
    <Card style={{ marginTop: 18 }} onCycle={onCycle}>
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
        <div style={{ fontSize: 10.5, fontFamily: 'var(--mono)', textTransform: 'uppercase', letterSpacing: '0.08em', color: 'var(--muted)' }}>Active event</div>
        <div style={{ fontSize: 10.5, color: 'var(--muted)', fontFamily: 'var(--mono)' }}>{ev.days}d left</div>
      </div>
      <div style={{ fontFamily: 'var(--serif)', fontSize: 18, marginTop: 6, color: 'var(--blue-500)' }}>{ev.name}</div>
      <div style={{ display: 'flex', alignItems: 'baseline', gap: 8, marginTop: 6 }}>
        <div style={{ fontFamily: 'var(--serif)', fontSize: 34, lineHeight: 1, letterSpacing: '-0.02em', color: 'var(--ink)' }}>{currencyFmt(ev.remaining)}</div>
        <div style={{ fontSize: 11, color: 'var(--muted)' }}>left of {currencyFmt(ev.budget)}</div>
      </div>
      <div style={{ marginTop: 14, height: 8, background: 'var(--gray-200)', borderRadius: 99, overflow: 'hidden' }}>
        <div style={{ width: pct + '%', height: '100%', background: 'var(--blue-500)', borderRadius: 99 }} />
      </div>
    </Card>
  );
}

function Card({ children, style = {}, onCycle }) {
  return (
    <div
      onClick={onCycle}
      style={{
        background: 'var(--card)', borderRadius: 18, padding: 18,
        boxShadow: '0 1px 0 rgba(43,31,23,0.04), 0 6px 14px rgba(43,31,23,0.04)',
        border: '1px solid var(--line)',
        cursor: onCycle ? 'pointer' : 'default',
        ...style,
      }}
    >
      {children}
    </div>
  );
}

function SparkLine() {
  // simple decorative spark
  return (
    <svg width="86" height="40" viewBox="0 0 86 40" fill="none">
      <path d="M2 30 L14 22 L26 26 L38 14 L50 18 L62 10 L74 14 L84 6" stroke="var(--clay)" strokeWidth="1.6" strokeLinecap="round" strokeLinejoin="round"/>
      <circle cx="84" cy="6" r="3" fill="var(--clay)"/>
    </svg>
  );
}

function QuickAccess({ label, icon, tint, stroke }) {
  return (
    <div className="proto-tap" style={{
      display: 'flex', flexDirection: 'column', alignItems: 'center', gap: 7,
      padding: '12px 4px',
      background: 'var(--card)', border: '1px solid var(--line)', borderRadius: 14,
    }}>
      <div style={{
        width: 36, height: 36, borderRadius: 11, background: tint,
        display: 'flex', alignItems: 'center', justifyContent: 'center',
      }}>
        <Icon name={icon} size={18} stroke={stroke} strokeWidth={1.8} />
      </div>
      <span style={{ fontSize: 11, color: 'var(--ink-2)', fontWeight: 500 }}>{label}</span>
    </div>
  );
}

function TxnRow({ txn, fresh }) {
  const cat = CATEGORIES[txn.category] || CATEGORIES.food;
  return (
    <div className={fresh ? 'proto-new-row' : ''} style={{
      display: 'flex', alignItems: 'center', gap: 12, padding: '12px 8px', borderRadius: 12,
      borderBottom: '1px solid var(--line-2)',
    }}>
      <CatBadge cat={txn.category} size={38} />
      <div style={{ flex: 1, minWidth: 0 }}>
        <div style={{ fontSize: 14, color: 'var(--ink)', fontWeight: 500, whiteSpace: 'nowrap', overflow: 'hidden', textOverflow: 'ellipsis' }}>
          {txn.note || cat.label}
        </div>
        <div style={{ fontSize: 11.5, color: 'var(--muted)', display: 'flex', gap: 6, alignItems: 'center' }}>
          <span>{cat.label}</span>
          <span>·</span>
          <span>{txn.time}</span>
          {txn.tag && txn.tag.type === 'event' && (
            <>
              <span>·</span>
              <span style={{ color: 'var(--tag)', display: 'inline-flex', alignItems: 'center', gap: 2 }}>
                <Icon name="at" size={10} stroke="var(--tag)" strokeWidth={2} />
                {(ACTIVE_EVENTS.find(e => e.id === txn.tag.id) || {}).name}
              </span>
            </>
          )}
        </div>
      </div>
      <div style={{ fontFamily: 'var(--serif)', fontSize: 18, color: 'var(--ink)' }}>{currencyFmt(txn.amount)}</div>
    </div>
  );
}

// ────────────────────────────────────────────────────────────────────
// ADD EXPENSE · AMOUNT
// ────────────────────────────────────────────────────────────────────
function AddAmountScreen({ navDir, draft, amountValue, shake, canProceed, onKey, setCategory, onCancel, onNext, onQuickSave, onMore }) {
  const fmt = formatAmount(draft.amount);
  return (
    <div className={'proto-screen-' + (navDir === 'back' ? 'back' : 'fwd')} style={{ height: '100%', display: 'flex', flexDirection: 'column' }}>
      {/* nav */}
      <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', padding: '14px 18px 6px' }}>
        <button className="proto-tap" onClick={onCancel} style={{ background: 'transparent', border: 'none', display: 'flex', alignItems: 'center', gap: 4, color: 'var(--ink-2)', cursor: 'pointer', padding: 6 }}>
          <Icon name="close" size={20} stroke="var(--ink-2)" strokeWidth={1.8} />
        </button>
        <div style={{ fontFamily: 'var(--serif)', fontSize: 17, color: 'var(--ink)' }}>New expense</div>
        <div style={{ width: 32 }} />
      </div>

      {/* amount */}
      <div style={{ flex: 1, display: 'flex', flexDirection: 'column', alignItems: 'center', justifyContent: 'flex-start', padding: '12px 22px 0', minHeight: 0 }}>
        <div style={{ fontSize: 10.5, fontFamily: 'var(--mono)', textTransform: 'uppercase', letterSpacing: '0.1em', color: 'var(--muted)', marginBottom: 8 }}>Amount · USD</div>
        <div
          className={shake ? 'proto-shake' : ''}
          style={{
            fontFamily: 'var(--serif)', fontSize: 64, lineHeight: 1, letterSpacing: '-0.025em',
            color: fmt.isPlaceholder ? 'var(--muted-2)' : 'var(--ink)',
            display: 'flex', alignItems: 'baseline', gap: 2,
          }}
        >
          <span style={{ fontSize: 30, color: fmt.isPlaceholder ? 'var(--muted-2)' : 'var(--clay)', alignSelf: 'flex-start', marginTop: 8 }}>$</span>
          <span className="proto-cursor">
            {fmt.whole}
            {fmt.decimal !== null && (
              <span style={{ color: 'var(--ink-3)' }}>.{fmt.decimal.padEnd(2, '0').slice(0, 2)}</span>
            )}
          </span>
        </div>
        {shake && (
          <div style={{ marginTop: 6, fontSize: 12, color: 'var(--clay)', fontWeight: 500, animation: 'proto-fade-in 200ms ease' }}>
            Amount must be greater than $0
          </div>
        )}

        {/* categories */}
        <div style={{ marginTop: 28, width: '100%' }}>
          <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', marginBottom: 8 }}>
            <span style={{ fontSize: 10.5, fontFamily: 'var(--mono)', textTransform: 'uppercase', letterSpacing: '0.08em', color: 'var(--ink-3)', fontWeight: 600 }}>Default</span>
            <button className="proto-tap" onClick={onMore} style={{ background: 'transparent', border: 'none', fontSize: 11, color: 'var(--clay)', cursor: 'pointer', fontWeight: 500 }}>+ More</button>
          </div>
          <div style={{ display: 'flex', flexWrap: 'wrap', gap: 6 }}>
            {DEFAULT_CATEGORY_IDS.map(id => (
              <CatChip key={id} cat={id} selected={draft.category === id} onClick={() => setCategory(id)} />
            ))}
          </div>
          <div style={{ fontSize: 10.5, fontFamily: 'var(--mono)', textTransform: 'uppercase', letterSpacing: '0.08em', color: 'var(--ink-3)', fontWeight: 600, marginTop: 14, marginBottom: 8 }}>Custom</div>
          <div style={{ display: 'flex', flexWrap: 'wrap', gap: 6 }}>
            {CUSTOM_CATEGORY_IDS.map(id => (
              <CatChip key={id} cat={id} selected={draft.category === id} onClick={() => setCategory(id)} />
            ))}
          </div>
        </div>
      </div>

      {/* keypad */}
      <Keypad onKey={onKey} canProceed={canProceed} onNext={onNext} onSave={onQuickSave} />
    </div>
  );
}

function Keypad({ onKey, canProceed, onNext, onSave }) {
  const keys = ['1','2','3','4','5','6','7','8','9','.','0','⌫'];
  return (
    <div style={{ padding: '8px 16px 38px' }}>
      {/* standard 3-column numeric keypad */}
      <div style={{ display: 'grid', gridTemplateColumns: 'repeat(3, 1fr)', gap: 8 }}>
        {keys.map((k) => <KeyBtn key={k} k={k} onKey={onKey} />)}
      </div>
      {/* action row — Save (secondary) + Next (primary), equal width */}
      <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: 8, marginTop: 8 }}>
        <button
          type="button"
          className="proto-tap"
          onClick={onSave}
          disabled={!canProceed}
          style={{
            background: 'transparent',
            color: canProceed ? 'var(--ink)' : 'var(--muted)',
            border: '1.4px solid ' + (canProceed ? 'var(--line-strong)' : 'var(--line)'),
            borderRadius: 14,
            padding: '15px 0',
            fontSize: 14, fontWeight: 600,
            cursor: canProceed ? 'pointer' : 'not-allowed',
            opacity: canProceed ? 1 : 0.6,
          }}
        >
          Save
        </button>
        <button
          type="button"
          className="proto-tap"
          onClick={onNext}
          disabled={!canProceed}
          style={{
            background: canProceed ? 'var(--clay)' : 'var(--clay-soft)',
            color: '#fffdf6',
            border: 'none',
            borderRadius: 14,
            padding: '15px 0',
            fontSize: 15, fontWeight: 600,
            display: 'flex', alignItems: 'center', justifyContent: 'center', gap: 4,
            cursor: canProceed ? 'pointer' : 'not-allowed',
            opacity: canProceed ? 1 : 0.55,
            boxShadow: canProceed ? '0 4px 10px rgba(3,155,229,0.25)' : 'none',
          }}
        >
          Next
          <Icon name="chevron-right" size={18} stroke="#fffdf6" strokeWidth={2} />
        </button>
      </div>
    </div>
  );
}

function KeyBtn({ k, onKey }) {
  return (
    <button
      type="button"
      className="proto-tap"
      onClick={() => onKey(k)}
      style={{
        background: 'var(--card)',
        border: '1px solid var(--line)',
        borderRadius: 12,
        padding: '14px 0',
        fontSize: 22, fontFamily: 'var(--serif)', color: 'var(--ink)',
        cursor: 'pointer',
      }}
    >
      {k}
    </button>
  );
}

// ────────────────────────────────────────────────────────────────────
// ADD EXPENSE · DETAILS
// ────────────────────────────────────────────────────────────────────
function AddDetailsScreen({ navDir, draft, setDraft, amountValue, onBack, onSave, onEditDateTime, onOpenTag, onClearTag, showTagPicker, onMore }) {
  const noteCount = (draft.note || '').length;
  const onNoteChange = (e) => {
    const v = e.target.value.slice(0, 200);
    setDraft(d => ({ ...d, note: v }));
  };
  return (
    <div className={'proto-screen-' + (navDir === 'back' ? 'back' : 'fwd')} style={{ height: '100%', display: 'flex', flexDirection: 'column' }}>
      {/* nav */}
      <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', padding: '14px 18px 6px' }}>
        <button className="proto-tap" onClick={onBack} style={{ background: 'transparent', border: 'none', display: 'flex', alignItems: 'center', gap: 4, color: 'var(--ink-2)', cursor: 'pointer', padding: 6 }}>
          <Icon name="back" size={20} stroke="var(--ink-2)" strokeWidth={1.8} />
          <span style={{ fontSize: 14 }}>Amount</span>
        </button>
        <div style={{ fontFamily: 'var(--serif)', fontSize: 17, color: 'var(--ink)' }}>Details</div>
        <div style={{ width: 60 }} />
      </div>

      <div className="proto-scroll" style={{ flex: 1, padding: '8px 22px 16px', display: 'flex', flexDirection: 'column', gap: 16 }}>
        {/* read-only amount */}
        <button
          type="button"
          className="proto-tap"
          onClick={onBack}
          style={{
            display: 'flex', alignItems: 'center', justifyContent: 'space-between',
            padding: '14px 16px', background: 'var(--card)', border: '1px solid var(--line)',
            borderRadius: 14, cursor: 'pointer', width: '100%',
          }}
        >
          <div style={{ textAlign: 'left' }}>
            <div style={{ fontSize: 10, fontFamily: 'var(--mono)', textTransform: 'uppercase', letterSpacing: '0.08em', color: 'var(--muted)' }}>Amount</div>
            <div style={{ fontFamily: 'var(--serif)', fontSize: 26, lineHeight: 1.1, marginTop: 2, color: 'var(--ink)' }}>{currencyFmt(amountValue)}</div>
          </div>
          <div style={{ fontSize: 11, color: 'var(--clay)', fontWeight: 500, display: 'flex', alignItems: 'center', gap: 4 }}>
            <Icon name="back" size={14} stroke="var(--clay)" strokeWidth={1.8} />
            Edit
          </div>
        </button>

        {/* category — chips speak for themselves, no labels */}
        <div>
          <div style={{ display: 'flex', flexWrap: 'wrap', gap: 6 }}>
            {[...DEFAULT_CATEGORY_IDS, ...CUSTOM_CATEGORY_IDS].map(id => (
              <CatChip key={id} cat={id} selected={draft.category === id} onClick={() => setDraft(d => ({ ...d, category: id }))} />
            ))}
            <button className="proto-tap" onClick={onMore} style={{
              padding: '7px 12px', background: 'transparent',
              border: '1.2px dashed var(--line-strong)', borderRadius: 99,
              fontSize: 12.5, color: 'var(--ink-3)', cursor: 'pointer', fontWeight: 500,
            }}>+ Add</button>
          </div>
        </div>

        {/* date & time combined */}
        <div>
          <button
            type="button"
            className="proto-tap"
            onClick={onEditDateTime}
            style={{
              width: '100%', display: 'flex', alignItems: 'center', justifyContent: 'space-between',
              padding: '14px 16px', background: 'var(--card)', border: '1px solid var(--line)',
              borderRadius: 14, cursor: 'pointer',
            }}
          >
            <div style={{ display: 'flex', alignItems: 'center', gap: 12 }}>
              <div style={{ width: 36, height: 36, borderRadius: 10, background: 'var(--paper-warm)', display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
                <Icon name="calendar" size={18} stroke="var(--ink-2)" strokeWidth={1.7} />
              </div>
              <div style={{ textAlign: 'left' }}>
                <div style={{ fontSize: 14, color: 'var(--ink)', fontWeight: 500 }}>{draft.date}</div>
                <div style={{ fontSize: 12, color: 'var(--muted)', display: 'flex', alignItems: 'center', gap: 4 }}>
                  <Icon name="clock" size={12} stroke="var(--muted)" strokeWidth={1.8} />
                  {draft.time}
                  {draft.timeIsFuture && <span style={{ color: 'var(--coin)', marginLeft: 6 }}>· future</span>}
                </div>
              </div>
            </div>
            <Icon name="chevron-right" size={16} stroke="var(--muted)" strokeWidth={1.6} />
          </button>
        </div>

        {/* note */}
        <div>
          <div style={{ padding: '10px 14px', background: 'var(--card)', border: '1px solid var(--line)', borderRadius: 14, display: 'flex', gap: 10, alignItems: 'flex-start' }}>
            <Icon name="note" size={18} stroke="var(--ink-3)" strokeWidth={1.6} style={{ marginTop: 2, flexShrink: 0 }} />
            <textarea
              value={draft.note}
              onChange={onNoteChange}
              placeholder="Add a note (optional)"
              rows={2}
              style={{
                flex: 1,
                border: 'none', outline: 'none', resize: 'none',
                fontFamily: 'var(--sans)', fontSize: 14, lineHeight: 1.4,
                background: 'transparent', color: 'var(--ink)',
              }}
            />
            <span style={{ fontSize: 10, color: noteCount >= 200 ? 'var(--clay)' : 'var(--muted-2)', fontFamily: 'var(--mono)', flexShrink: 0, marginTop: 4 }}>{noteCount}/200</span>
          </div>
        </div>

        {/* @ tag */}
        {showTagPicker && (
          <div>
            {draft.tag ? (
              <div style={{ marginTop: 6, padding: '12px 14px', background: 'var(--card)', border: '1px solid var(--line)', borderRadius: 14, display: 'flex', alignItems: 'center', gap: 12 }}>
                <Icon name="at" size={18} stroke="var(--muted)" strokeWidth={2} />
                <div style={{ flex: 1, fontSize: 13, color: 'var(--tag-deep)', fontWeight: 600 }}>
                  {tagLabel(draft.tag)}
                </div>
                <button className="proto-tap" onClick={onClearTag} style={{
                  background: 'transparent', border: '1px solid var(--line-strong)', borderRadius: 99,
                  padding: '4px 10px', color: 'var(--muted)', fontSize: 11, cursor: 'pointer',
                }}>Clear</button>
              </div>
            ) : (
              <button className="proto-tap" onClick={onOpenTag} style={{
                marginTop: 6, width: '100%', padding: '14px 16px',
                background: 'transparent', border: '1.4px dashed var(--line-strong)', borderRadius: 14,
                display: 'flex', alignItems: 'center', gap: 10, cursor: 'pointer',
              }}>
                <Icon name="at" size={18} stroke="var(--muted)" strokeWidth={1.8} />
                <span style={{ fontSize: 13, color: 'var(--muted)' }}>Link to event or debt</span>
              </button>
            )}
          </div>
        )}
      </div>

      {/* save */}
      <div style={{ padding: '0 22px 38px' }}>
        <Button variant="primary" size="lg" fullWidth onClick={onSave}>
          Save expense · {currencyFmt(amountValue)}
        </Button>
      </div>
    </div>
  );
}

function FieldLabel({ children }) {
  return (
    <div style={{
      fontSize: 10.5, fontFamily: 'var(--mono)', textTransform: 'uppercase',
      letterSpacing: '0.08em', color: 'var(--ink-3)', fontWeight: 600,
    }}>{children}</div>
  );
}

function tagLabel(tag) {
  if (!tag) return '';
  if (tag.type === 'event') {
    const ev = ACTIVE_EVENTS.find(e => e.id === tag.id);
    return ev ? `@ ${ev.name}` : '@ Event';
  }
  const d = ACTIVE_DEBTS.find(x => x.id === tag.id);
  return d ? `@ ${d.label}` : '@ Debt';
}

// ────────────────────────────────────────────────────────────────────
// BOTTOM SHEETS
// ────────────────────────────────────────────────────────────────────
function SheetShell({ title, onClose, children, height }) {
  return (
    <>
      <div className="proto-backdrop" onClick={onClose} />
      <div className="proto-sheet proto-sheet-card" style={height ? { height } : {}}>
        <div className="proto-sheet-handle" />
        <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', padding: '6px 22px 10px' }}>
          <div style={{ fontFamily: 'var(--serif)', fontSize: 22, letterSpacing: '-0.01em' }}>{title}</div>
          <button className="proto-tap" onClick={onClose} style={{ background: 'transparent', border: 'none', cursor: 'pointer', padding: 4 }}>
            <Icon name="close" size={20} stroke="var(--ink-2)" strokeWidth={1.7} />
          </button>
        </div>
        <div className="proto-scroll" style={{ flex: 1, padding: '0 22px 38px' }}>
          {children}
        </div>
      </div>
    </>
  );
}

function TagPickerSheet({ current, onClose, onSelect, onClear }) {
  const hasEvents = ACTIVE_EVENTS.length > 0;
  const hasDebts = ACTIVE_DEBTS.length > 0;
  const selectedType = current?.type;
  return (
    <SheetShell title="Link to…" onClose={onClose} height={520}>
      <div style={{ fontSize: 12.5, color: 'var(--ink-2)', marginBottom: 16, lineHeight: 1.4 }}>
        Pick one event <strong>or</strong> one debt. Tagged expenses appear in both Journal and the linked record.
      </div>

      {/* Events */}
      <div style={{ marginBottom: 18, opacity: selectedType === 'debt' ? 0.4 : 1, pointerEvents: selectedType === 'debt' ? 'none' : 'auto' }}>
        <FieldLabel>Events</FieldLabel>
        <div style={{ marginTop: 8, display: 'flex', flexDirection: 'column', gap: 6 }}>
          {ACTIVE_EVENTS.map(ev => {
            const on = current?.type === 'event' && current?.id === ev.id;
            return (
              <button key={ev.id} className="proto-tap" onClick={() => onSelect('event', ev.id)} style={{
                display: 'flex', alignItems: 'center', gap: 12, padding: '12px 14px',
                background: on ? 'var(--clay-tint)' : 'var(--card)',
                border: '1px solid ' + (on ? 'var(--clay)' : 'var(--line)'),
                borderRadius: 12, cursor: 'pointer', textAlign: 'left', width: '100%',
              }}>
                <div style={{ width: 34, height: 34, borderRadius: 99, background: 'var(--coin-soft)', display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
                  <Icon name="feat-events" size={16} stroke="#212121" strokeWidth={1.8} />
                </div>
                <div style={{ flex: 1 }}>
                  <div style={{ fontSize: 14, fontWeight: 500, color: 'var(--ink)' }}>{ev.name}</div>
                  <div style={{ fontSize: 11.5, color: 'var(--muted)' }}>{ev.range}</div>
                </div>
                {on && <Icon name="check" size={18} stroke="var(--clay)" strokeWidth={2.2} />}
              </button>
            );
          })}
        </div>
      </div>

      {/* Debts */}
      <div style={{ opacity: selectedType === 'event' ? 0.4 : 1, pointerEvents: selectedType === 'event' ? 'none' : 'auto' }}>
        <FieldLabel>Debts</FieldLabel>
        <div style={{ marginTop: 8, display: 'flex', flexDirection: 'column', gap: 6 }}>
          {ACTIVE_DEBTS.map(d => {
            const on = current?.type === 'debt' && current?.id === d.id;
            return (
              <button key={d.id} className="proto-tap" onClick={() => onSelect('debt', d.id)} style={{
                display: 'flex', alignItems: 'center', gap: 12, padding: '12px 14px',
                background: on ? 'var(--clay-tint)' : 'var(--card)',
                border: '1px solid ' + (on ? 'var(--clay)' : 'var(--line)'),
                borderRadius: 12, cursor: 'pointer', textAlign: 'left', width: '100%',
              }}>
                <div style={{ width: 34, height: 34, borderRadius: 99, background: d.label.startsWith('Lent') ? 'var(--sage-soft)' : 'var(--clay-soft)', display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
                  <Icon name="feat-debt" size={16} stroke={d.label.startsWith('Lent') ? 'var(--sage)' : 'var(--clay-deep)'} strokeWidth={1.8} />
                </div>
                <div style={{ flex: 1 }}>
                  <div style={{ fontSize: 14, fontWeight: 500, color: 'var(--ink)' }}>{d.label}</div>
                  <div style={{ fontSize: 11.5, color: 'var(--muted)' }}>{currencyFmt(d.amount)}</div>
                </div>
                {on && <Icon name="check" size={18} stroke="var(--clay)" strokeWidth={2.2} />}
              </button>
            );
          })}
        </div>
      </div>

      {current && (
        <button className="proto-tap" onClick={onClear} style={{
          marginTop: 16, width: '100%', padding: '12px',
          background: 'transparent', border: '1px solid var(--line-strong)', borderRadius: 12,
          color: 'var(--ink-2)', fontSize: 13, cursor: 'pointer',
        }}>Clear tag</button>
      )}
    </SheetShell>
  );
}

function DateTimeSheet({ draft, onClose, onApply }) {
  const [date, setDate] = useState(draft.date);
  const [hour, setHour] = useState(parseInt(draft.time?.split(':')[0]) || 12);
  const [minute, setMinute] = useState(parseInt(draft.time?.split(':')[1]) || 30);
  const [meridiem, setMeridiem] = useState(draft.time?.includes('AM') ? 'AM' : 'PM');
  const dates = ['Today, May 25', 'Yesterday, May 24', 'Mon, May 23', 'Sun, May 22', 'Tomorrow, May 26', 'Fri, May 29'];

  const apply = () => {
    const time = `${String(hour).padStart(2, '0')}:${String(minute).padStart(2, '0')} ${meridiem}`;
    const isFuture = /Tomorrow|Fri/.test(date);
    onApply({ date, time, timeIsFuture: isFuture });
  };

  return (
    <SheetShell title="Date & time" onClose={onClose} height={560}>
      <FieldLabel>Date</FieldLabel>
      <div style={{ marginTop: 8, display: 'flex', flexWrap: 'wrap', gap: 6 }}>
        {dates.map(d => {
          const on = date === d;
          const future = /Tomorrow|Fri/.test(d);
          return (
            <button key={d} className="proto-tap" onClick={() => setDate(d)} style={{
              padding: '8px 12px',
              background: on ? 'var(--ink)' : 'var(--card)',
              color: on ? 'var(--paper-warm)' : 'var(--ink-2)',
              border: '1px solid ' + (on ? 'var(--ink)' : 'var(--line)'),
              borderRadius: 99, fontSize: 12.5, cursor: 'pointer', fontWeight: on ? 600 : 500,
            }}>
              {d.split(',')[0]}
              {future && <span style={{ marginLeft: 4, color: on ? 'var(--coin)' : 'var(--coin)' }}>·</span>}
            </button>
          );
        })}
      </div>

      <FieldLabel style={{ marginTop: 18 }}>Time</FieldLabel>
      <div style={{ marginTop: 8, display: 'flex', justifyContent: 'center', gap: 14, alignItems: 'center', padding: '20px', background: 'var(--card)', border: '1px solid var(--line)', borderRadius: 14 }}>
        <Spinner value={hour} setValue={(v) => setHour(((v - 1) % 12) + 1 || 12)} min={1} max={12} />
        <span style={{ fontFamily: 'var(--serif)', fontSize: 28, color: 'var(--muted-2)' }}>:</span>
        <Spinner value={minute} setValue={(v) => setMinute((v + 60) % 60)} min={0} max={59} pad />
        <div style={{ display: 'flex', flexDirection: 'column', gap: 4, marginLeft: 10 }}>
          {['AM', 'PM'].map(m => (
            <button key={m} className="proto-tap" onClick={() => setMeridiem(m)} style={{
              padding: '6px 12px',
              background: meridiem === m ? 'var(--clay)' : 'transparent',
              color: meridiem === m ? '#fffdf6' : 'var(--ink-3)',
              border: '1px solid ' + (meridiem === m ? 'var(--clay)' : 'var(--line)'),
              borderRadius: 8, fontSize: 12, fontWeight: 600, cursor: 'pointer',
            }}>{m}</button>
          ))}
        </div>
      </div>

      {(/Tomorrow|Fri/.test(date)) && (
        <div style={{ marginTop: 12, padding: '10px 14px', background: 'var(--card)', border: '1px solid var(--line)', borderRadius: 10, fontSize: 12, color: 'var(--ink-2)', display: 'flex', gap: 8, alignItems: 'center' }}>
          <Icon name="sparkle" size={16} stroke="var(--blue-500)" strokeWidth={1.8} />
          Future date — entries are ordered by created date, not expense date.
        </div>
      )}

      <div style={{ marginTop: 18 }}>
        <Button variant="primary" size="lg" fullWidth onClick={apply}>Apply</Button>
      </div>
    </SheetShell>
  );
}

function Spinner({ value, setValue, min, max, pad }) {
  return (
    <div style={{ display: 'flex', flexDirection: 'column', alignItems: 'center', gap: 6 }}>
      <button className="proto-tap" onClick={() => setValue(value + 1)} style={{ background: 'transparent', border: 'none', color: 'var(--muted)', cursor: 'pointer', padding: 4 }}>
        <Icon name="chevron-down" size={14} stroke="var(--muted)" strokeWidth={1.8} style={{ transform: 'rotate(180deg)' }} />
      </button>
      <div style={{ fontFamily: 'var(--serif)', fontSize: 36, lineHeight: 1, color: 'var(--ink)', minWidth: 44, textAlign: 'center' }}>
        {pad ? String(value).padStart(2, '0') : value}
      </div>
      <button className="proto-tap" onClick={() => setValue(value - 1)} style={{ background: 'transparent', border: 'none', color: 'var(--muted)', cursor: 'pointer', padding: 4 }}>
        <Icon name="chevron-down" size={14} stroke="var(--muted)" strokeWidth={1.8} />
      </button>
    </div>
  );
}

function CategoryPickerSheet({ current, onClose, onSelect }) {
  return (
    <SheetShell title="Choose category" onClose={onClose} height={520}>
      <div style={{ marginTop: 6, display: 'grid', gridTemplateColumns: 'repeat(3, 1fr)', gap: 8 }}>
        {[...DEFAULT_CATEGORY_IDS, ...CUSTOM_CATEGORY_IDS].map(id => {
          const cat = CATEGORIES[id];
          const on = current === id;
          return (
            <button key={id} className="proto-tap" onClick={() => onSelect(id)} style={{
              display: 'flex', flexDirection: 'column', alignItems: 'center', gap: 8,
              padding: '14px 8px',
              background: on ? cat.tint : 'var(--card)',
              border: '1.4px solid ' + (on ? cat.color : 'var(--line)'),
              borderRadius: 14, cursor: 'pointer',
            }}>
              <CatBadge cat={id} size={42} />
              <span style={{ fontSize: 12, color: on ? cat.color : 'var(--ink-2)', fontWeight: on ? 600 : 500, textAlign: 'center' }}>{cat.label}</span>
              {cat.custom && <span style={{ fontSize: 9, fontFamily: 'var(--mono)', color: 'var(--muted)', textTransform: 'uppercase', letterSpacing: '0.1em' }}>custom</span>}
            </button>
          );
        })}
      </div>
      <button className="proto-tap" style={{
        marginTop: 14, width: '100%', padding: '14px',
        background: 'transparent', border: '1.4px dashed var(--line-strong)', borderRadius: 14,
        color: 'var(--ink-3)', fontSize: 13, fontWeight: 500, cursor: 'pointer',
      }}>+ Create new category</button>
    </SheetShell>
  );
}

Object.assign(window, {
  HomeScreen, AddAmountScreen, AddDetailsScreen,
  TagPickerSheet, DateTimeSheet, CategoryPickerSheet,
});
