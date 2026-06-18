// Static hi-fi mockups — render each screen + state on its own artboard.
// Reuses the screen components from flow-01-screens.jsx with fixed props.

const SAMPLE_EXPENSES = [
  { id: 'e1', amount: 12.40, category: 'food',          note: 'Lunch with M.',  date: 'Today',     time: '12:30 PM', tag: null },
  { id: 'e2', amount: 3.50,  category: 'transport',     note: '',               date: 'Today',     time: '09:15 AM', tag: null },
  { id: 'e3', amount: 5.00,  category: 'coffee',        note: 'Oat latte',      date: 'Today',     time: '08:40 AM', tag: null },
  { id: 'e4', amount: 18.00, category: 'entertainment', note: 'Movie · Dune',   date: 'Yesterday', time: '08:10 PM', tag: { type: 'event', id: 'bali' } },
  { id: 'e5', amount: 42.00, category: 'food',          note: 'Groceries',      date: 'Yesterday', time: '05:30 PM', tag: null },
];

// ── Static wrappers ──────────────────────────────────────────────────────
function StaticHome({ persona = 'casual' }) {
  return (
    <PhoneShell>
      <HomeScreen
        navDir="fwd"
        expenses={SAMPLE_EXPENSES}
        persona={persona}
        newRowId={null}
        activeNav="home"
        onTab={() => {}}
        onAdd={() => {}}
        onPersona={() => {}}
      />
      <ProtoBottomNav active="home" onTab={() => {}} onAdd={() => {}} />
    </PhoneShell>
  );
}

function StaticHomePostSave() {
  // freshly-added expense at the top
  const fresh = [
    { id: 'new-1', amount: 8.50, category: 'food', note: 'Bagel + coffee', date: 'Today', time: '09:42 AM', tag: null },
    ...SAMPLE_EXPENSES,
  ];
  return (
    <PhoneShell>
      <HomeScreen
        navDir="fwd"
        expenses={fresh}
        persona="casual"
        newRowId="new-1"
        activeNav="home"
        onTab={() => {}}
        onAdd={() => {}}
        onPersona={() => {}}
      />
      <ProtoBottomNav active="home" onTab={() => {}} onAdd={() => {}} />
      <div className="proto-toast">
        <Icon name="check" size={16} stroke="var(--paper-warm)" strokeWidth={2.2} />
        <span>Saved $8.50 to Food</span>
      </div>
    </PhoneShell>
  );
}

function StaticAddAmount({ amount = '', shake = false, category = 'food' }) {
  return (
    <PhoneShell>
      <AddAmountScreen
        navDir="fwd"
        draft={{ amount, category, note: '', date: 'Today, May 25', time: '12:30 PM', tag: null }}
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

function StaticAddDetails({ filled = false, withTag = false }) {
  const draft = filled
    ? {
        amount: '12.40',
        category: 'food',
        note: 'Lunch at the noodle place. Split with M.',
        date: 'Today, May 25',
        time: '12:30 PM',
        timeIsFuture: false,
        tag: withTag ? { type: 'event', id: 'bali' } : null,
      }
    : {
        amount: '12.40',
        category: 'food',
        note: '',
        date: 'Today, May 25',
        time: '12:30 PM',
        timeIsFuture: false,
        tag: null,
      };
  return (
    <PhoneShell>
      <AddDetailsScreen
        navDir="fwd"
        draft={draft}
        setDraft={() => {}}
        amountValue={amountToFloat(draft.amount)}
        onBack={() => {}}
        onSave={() => {}}
        onEditDateTime={() => {}}
        onOpenTag={() => {}}
        onClearTag={() => {}}
        showTagPicker={true}
        onMore={() => {}}
      />
    </PhoneShell>
  );
}

function StaticTagSheet() {
  const draft = { amount: '12.40', category: 'food', note: '', date: 'Today, May 25', time: '12:30 PM', tag: { type: 'event', id: 'bali' } };
  return (
    <PhoneShell>
      <AddDetailsScreen
        navDir="fwd"
        draft={draft}
        setDraft={() => {}}
        amountValue={12.40}
        onBack={() => {}}
        onSave={() => {}}
        onEditDateTime={() => {}}
        onOpenTag={() => {}}
        onClearTag={() => {}}
        showTagPicker={true}
        onMore={() => {}}
      />
      <TagPickerSheet
        current={{ type: 'event', id: 'bali' }}
        onClose={() => {}}
        onSelect={() => {}}
        onClear={() => {}}
      />
    </PhoneShell>
  );
}

function StaticDateSheet({ future = false }) {
  const draft = {
    amount: '12.40', category: 'food', note: '',
    date: future ? 'Tomorrow, May 26' : 'Today, May 25',
    time: '12:30 PM', timeIsFuture: future, tag: null,
  };
  return (
    <PhoneShell>
      <AddDetailsScreen
        navDir="fwd"
        draft={draft}
        setDraft={() => {}}
        amountValue={12.40}
        onBack={() => {}}
        onSave={() => {}}
        onEditDateTime={() => {}}
        onOpenTag={() => {}}
        onClearTag={() => {}}
        showTagPicker={true}
        onMore={() => {}}
      />
      <DateTimeSheet
        draft={draft}
        onClose={() => {}}
        onApply={() => {}}
      />
    </PhoneShell>
  );
}

function StaticCategorySheet() {
  return (
    <PhoneShell>
      <AddAmountScreen
        navDir="fwd"
        draft={{ amount: '12.40', category: 'food', note: '', date: 'Today, May 25', time: '12:30 PM', tag: null }}
        amountValue={12.40}
        shake={false}
        canProceed={true}
        onKey={() => {}}
        setCategory={() => {}}
        onCancel={() => {}}
        onNext={() => {}}
        onQuickSave={() => {}}
        onMore={() => {}}
      />
      <CategoryPickerSheet
        current="food"
        onClose={() => {}}
        onSelect={() => {}}
      />
    </PhoneShell>
  );
}

function StaticDraftRestore() {
  return (
    <PhoneShell>
      <HomeScreen
        navDir="fwd"
        expenses={SAMPLE_EXPENSES}
        persona="casual"
        newRowId={null}
        activeNav="home"
        onTab={() => {}}
        onAdd={() => {}}
        onPersona={() => {}}
      />
      <ProtoBottomNav active="home" onTab={() => {}} onAdd={() => {}} />
      <div className="proto-backdrop" />
      <div style={{
        position: 'absolute', left: 24, right: 24, top: '38%', transform: 'translateY(-50%)',
        background: 'var(--card)', borderRadius: 18, padding: 22,
        zIndex: 31,
        boxShadow: '0 18px 40px rgba(0,0,0,0.18)',
      }}>
        <div style={{ display: 'inline-flex', alignItems: 'center', justifyContent: 'center', width: 38, height: 38, borderRadius: 99, background: 'var(--clay-tint)', color: 'var(--clay)', marginBottom: 12 }}>
          <Icon name="sparkle" size={18} stroke="var(--clay)" />
        </div>
        <div style={{ fontFamily: 'var(--serif)', fontSize: 22, lineHeight: 1.1, letterSpacing: '-0.01em', marginBottom: 6 }}>Unfinished expense</div>
        <div style={{ fontSize: 13.5, color: 'var(--ink-2)', lineHeight: 1.45, marginBottom: 16 }}>
          You had <strong>$12.40</strong> in progress when the app closed. Continue or discard?
        </div>
        <div style={{ display: 'flex', gap: 8 }}>
          <Button variant="secondary" size="md" fullWidth>Discard</Button>
          <Button variant="primary" size="md" fullWidth>Continue</Button>
        </div>
      </div>
    </PhoneShell>
  );
}

Object.assign(window, {
  StaticHome, StaticHomePostSave, StaticAddAmount, StaticAddDetails,
  StaticTagSheet, StaticDateSheet, StaticCategorySheet, StaticDraftRestore,
});
