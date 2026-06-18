// Flow 01 — Quick Log
// State machine: home → add-amount → add-details → home (with new entry)
// Handles every spec edge case: $0 validation, draft restore, future date,
// note 200 cap, @-tag picker with greyed mutual-exclusion, etc.

const { useState, useEffect, useRef, useMemo } = React;

const STORAGE_KEY = 'pro-expense-flow01-v1';

// ── Seed data ────────────────────────────────────────────────────────────
const SEED_EXPENSES = [
  { id: 'e1', amount: 12.40, category: 'food',          note: 'Lunch with M.',  date: 'Today',     time: '12:30 PM', tag: null },
  { id: 'e2', amount: 3.50,  category: 'transport',     note: '',               date: 'Today',     time: '09:15 AM', tag: null },
  { id: 'e3', amount: 5.00,  category: 'coffee',        note: 'Oat latte',      date: 'Today',     time: '08:40 AM', tag: null },
  { id: 'e4', amount: 18.00, category: 'entertainment', note: 'Movie · Dune',   date: 'Yesterday', time: '08:10 PM', tag: { type: 'event', id: 'bali' } },
  { id: 'e5', amount: 42.00, category: 'food',          note: 'Groceries',      date: 'Yesterday', time: '05:30 PM', tag: null },
];

const ACTIVE_EVENTS = [
  { id: 'bali', name: 'Bali Trip', range: 'May 12 — May 26' },
  { id: 'john', name: "John's Wedding", range: 'Jun 04 — Jun 06' },
];
const ACTIVE_DEBTS = [
  { id: 'lent-john', label: 'Lent · John', amount: 50 },
  { id: 'owe-sarah', label: 'Owe · Sarah', amount: 30 },
];

// ── Persistence ──────────────────────────────────────────────────────────
function loadState() {
  try {
    const raw = localStorage.getItem(STORAGE_KEY);
    if (!raw) return null;
    return JSON.parse(raw);
  } catch { return null; }
}
function saveState(s) {
  try { localStorage.setItem(STORAGE_KEY, JSON.stringify(s)); } catch {}
}
function clearState() {
  try { localStorage.removeItem(STORAGE_KEY); } catch {}
}

// ── Empty draft ──────────────────────────────────────────────────────────
function emptyDraft() {
  return {
    amount: '',
    category: 'food',
    note: '',
    date: 'Today, May 25',
    time: '12:30 PM',
    timeIsFuture: false,
    tag: null, // { type: 'event'|'debt', id: '...' }
  };
}

// ── Main flow component ──────────────────────────────────────────────────
function Flow01({ resetCounter = 0 }) {
  // Persisted state
  const initial = useMemo(() => loadState(), []);
  const [expenses, setExpenses] = useState(initial?.expenses || SEED_EXPENSES);
  const [persona, setPersona] = useState(initial?.persona || 'casual');
  const [draft, setDraft] = useState(initial?.draft || emptyDraft());
  const [hasDraft, setHasDraft] = useState(!!initial?.draft && initial.draft.amount !== '');
  // Step (ephemeral — does NOT persist; the draft does)
  const [step, setStep] = useState('home');
  const [navDir, setNavDir] = useState('fwd');
  const [showDraftRestore, setShowDraftRestore] = useState(false);
  const [toast, setToast] = useState(null);
  const [newRowId, setNewRowId] = useState(null);
  // Sheets
  const [sheet, setSheet] = useState(null); // 'tag' | 'date' | 'category-picker' | null
  const [activeNav, setActiveNav] = useState('home');
  // Validation pulse on amount = $0 attempt
  const [amountShake, setAmountShake] = useState(false);

  // Apply external reset
  useEffect(() => {
    if (resetCounter > 0) {
      clearState();
      setExpenses(SEED_EXPENSES);
      setPersona('casual');
      setDraft(emptyDraft());
      setHasDraft(false);
      setStep('home');
      setNavDir('fwd');
      setShowDraftRestore(false);
      setToast(null);
      setSheet(null);
      setActiveNav('home');
    }
  }, [resetCounter]);

  // Persist
  useEffect(() => {
    saveState({ expenses, persona, draft: hasDraft ? draft : null });
  }, [expenses, persona, draft, hasDraft]);

  // Toast lifecycle
  useEffect(() => {
    if (toast) {
      const t = setTimeout(() => setToast(null), 2500);
      return () => clearTimeout(t);
    }
  }, [toast]);

  // Draft restore check on initial mount
  useEffect(() => {
    if (initial?.draft && initial.draft.amount !== '' && step === 'home') {
      setShowDraftRestore(true);
    }
  }, []);

  // Navigation helpers
  const goto = (next, dir = 'fwd') => {
    setNavDir(dir);
    setStep(next);
  };

  const startAdd = () => {
    setDraft(emptyDraft());
    setHasDraft(false);
    goto('add-amount', 'fwd');
  };
  const continueDraft = () => {
    setShowDraftRestore(false);
    goto('add-amount', 'fwd');
  };
  const discardDraft = () => {
    setDraft(emptyDraft());
    setHasDraft(false);
    setShowDraftRestore(false);
  };

  // Amount input
  const onKey = (k) => {
    setDraft(d => {
      let a = d.amount;
      if (k === '⌫') a = a.slice(0, -1);
      else if (k === '.') { if (!a.includes('.')) a = a === '' ? '0.' : a + '.'; }
      else {
        // limit 2 decimals
        if (a.includes('.')) {
          const [w, dec = ''] = a.split('.');
          if (dec.length >= 2) return d;
        }
        // limit whole part to 7 digits
        const [w] = a.split('.');
        if (!a.includes('.') && w.length >= 7) return d;
        a = a + k;
      }
      // strip leading zeros unless decimal
      if (/^0\d/.test(a)) a = a.replace(/^0+/, '');
      setHasDraft(a !== '');
      return { ...d, amount: a };
    });
  };

  const amountValue = amountToFloat(draft.amount);
  const canProceed = amountValue > 0;

  const goToDetails = () => {
    if (!canProceed) {
      setAmountShake(true);
      setTimeout(() => setAmountShake(false), 320);
      return;
    }
    goto('add-details', 'fwd');
  };
  const quickSave = () => {
    if (!canProceed) {
      setAmountShake(true);
      setTimeout(() => setAmountShake(false), 320);
      return;
    }
    commitExpense();
  };
  const commitExpense = () => {
    const id = 'e' + Date.now();
    const entry = {
      id,
      amount: amountValue,
      category: draft.category,
      note: draft.note,
      date: 'Today',
      time: draft.time,
      tag: draft.tag,
    };
    setExpenses(es => [entry, ...es]);
    setNewRowId(id);
    setTimeout(() => setNewRowId(null), 1800);
    setDraft(emptyDraft());
    setHasDraft(false);
    setActiveNav('home');
    goto('home', 'back');
    setToast({ kind: 'saved', msg: `Saved ${currencyFmt(amountValue)} to ${CATEGORIES[entry.category]?.label}` });
  };

  const cancelAdd = () => {
    // Silent back when no amount per spec; with amount, keep draft so user can resume from home
    if (draft.amount === '') {
      setDraft(emptyDraft());
      setHasDraft(false);
    }
    goto('home', 'back');
  };

  // Tag selection — single, mutually exclusive
  const selectTag = (type, id) => {
    setDraft(d => ({ ...d, tag: { type, id } }));
    setSheet(null);
  };
  const clearTag = () => setDraft(d => ({ ...d, tag: null }));

  const showTagPicker = ACTIVE_EVENTS.length + ACTIVE_DEBTS.length > 0;

  return (
    <PhoneShell bottomBar={step === 'home' ? (
      <ProtoBottomNav
        active={activeNav}
        onTab={(t) => setActiveNav(t)}
        onAdd={startAdd}
      />
    ) : null}>
      {/* === Screens === */}
      {step === 'home' && (
        <HomeScreen
          key="home"
          navDir={navDir}
          expenses={expenses}
          persona={persona}
          newRowId={newRowId}
          activeNav={activeNav}
          onTab={(t) => setActiveNav(t)}
          onAdd={startAdd}
          onPersona={setPersona}
        />
      )}
      {step === 'add-amount' && (
        <AddAmountScreen
          key="add-amount"
          navDir={navDir}
          draft={draft}
          amountValue={amountValue}
          shake={amountShake}
          canProceed={canProceed}
          onKey={onKey}
          setCategory={(c) => setDraft(d => ({ ...d, category: c }))}
          onCancel={cancelAdd}
          onNext={goToDetails}
          onQuickSave={quickSave}
          onMore={() => setSheet('category-picker')}
        />
      )}
      {step === 'add-details' && (
        <AddDetailsScreen
          key="add-details"
          navDir={navDir}
          draft={draft}
          setDraft={setDraft}
          amountValue={amountValue}
          onBack={() => goto('add-amount', 'back')}
          onSave={commitExpense}
          onEditDateTime={() => setSheet('date')}
          onOpenTag={() => setSheet('tag')}
          onClearTag={clearTag}
          showTagPicker={showTagPicker}
          onMore={() => setSheet('category-picker')}
        />
      )}

      {/* === Bottom nav now rendered via PhoneShell's bottomBar prop (edge-to-edge, pinned flush) === */}

      {/* === Draft restore modal === */}
      {showDraftRestore && (
        <>
          <div className="proto-backdrop" />
          <div style={{
            position: 'absolute', left: 24, right: 24, top: '38%', transform: 'translateY(-50%)',
            background: 'var(--card)', borderRadius: 18, padding: 22,
            zIndex: 31, animation: 'proto-fade-up 240ms cubic-bezier(0.22, 0.61, 0.36, 1)',
            boxShadow: '0 18px 40px rgba(0,0,0,0.18)',
          }}>
            <div style={{ display: 'inline-flex', alignItems: 'center', justifyContent: 'center', width: 38, height: 38, borderRadius: 99, background: 'var(--clay-tint)', color: 'var(--clay)', marginBottom: 12 }}>
              <Icon name="sparkle" size={18} stroke="var(--clay)" />
            </div>
            <div style={{ fontFamily: 'var(--serif)', fontSize: 22, lineHeight: 1.1, letterSpacing: '-0.01em', marginBottom: 6 }}>Unfinished expense</div>
            <div style={{ fontSize: 13.5, color: 'var(--ink-2)', lineHeight: 1.45, marginBottom: 16 }}>
              You had {draft.amount ? <strong>{currencyFmt(amountToFloat(draft.amount))}</strong> : 'a draft'} in progress when the app closed. Continue or discard?
            </div>
            <div style={{ display: 'flex', gap: 8 }}>
              <Button variant="secondary" size="md" fullWidth onClick={discardDraft}>Discard</Button>
              <Button variant="primary" size="md" fullWidth onClick={continueDraft}>Continue</Button>
            </div>
          </div>
        </>
      )}

      {/* === Bottom sheets === */}
      {sheet === 'tag' && (
        <TagPickerSheet
          current={draft.tag}
          onClose={() => setSheet(null)}
          onSelect={selectTag}
          onClear={() => { clearTag(); setSheet(null); }}
        />
      )}
      {sheet === 'date' && (
        <DateTimeSheet
          draft={draft}
          onClose={() => setSheet(null)}
          onApply={(d) => { setDraft(prev => ({ ...prev, ...d })); setSheet(null); }}
        />
      )}
      {sheet === 'category-picker' && (
        <CategoryPickerSheet
          current={draft.category}
          onClose={() => setSheet(null)}
          onSelect={(c) => { setDraft(d => ({ ...d, category: c })); setSheet(null); }}
        />
      )}

      {/* === Toast === */}
      {toast && (
        <div className="proto-toast">
          <Icon name="check" size={16} stroke="var(--paper-warm)" strokeWidth={2.2} />
          <span>{toast.msg}</span>
        </div>
      )}
    </PhoneShell>
  );
}

// ── Currency formatter ──────────────────────────────────────────────────
function currencyFmt(n) {
  if (n == null || isNaN(n)) return '$0';
  return '$' + n.toLocaleString('en-US', { minimumFractionDigits: n % 1 === 0 ? 0 : 2, maximumFractionDigits: 2 });
}

Object.assign(window, {
  Flow01, currencyFmt, ACTIVE_EVENTS, ACTIVE_DEBTS,
});
