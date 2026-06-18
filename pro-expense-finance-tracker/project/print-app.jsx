// A4 print layout — every Pro Expense screen, grouped flow by flow.
// Re-renders the same screen components (window globals) at 0.52 scale in a
// 3-up grid. Each flow starts on a new printed page.

const PRINT_SCALE = 0.55;

function PhoneCard({ label, children }) {
  return (
    <div className="pcard">
      <div className="pframe" style={{ width: 390 * PRINT_SCALE, height: 844 * PRINT_SCALE }}>
        <div className="pscale" style={{ transform: `scale(${PRINT_SCALE})` }}>
          {children}
        </div>
      </div>
      <div className="pcap">{label}</div>
    </div>
  );
}

const PRINT_FLOWS = [
  {
    title: 'Flow 04 — First Launch',
    subtitle: 'Splash → onboarding (Get started on every slide) → Home',
    screens: [
      { label: '01 · Splash', node: <ScreenSplashHi /> },
      { label: '02 · Welcome', node: <ScreenOnboardingHi slide={1} /> },
      { label: '02 · Quick Log', node: <ScreenOnboardingHi slide={2} /> },
      { label: '02 · Shared Costs', node: <ScreenOnboardingHi slide={3} /> },
      { label: '02 · Event Budget', node: <ScreenOnboardingHi slide={4} /> },
      { label: '02 · Journal · Get started', node: <ScreenOnboardingHi slide={5} /> },
    ],
  },
  {
    title: 'Flow 04 — Profile Setup',
    subtitle: 'First run · between Onboarding and Home · account-free personalization',
    screens: [
      { label: 'P1 · Profile name', node: <ProfileName /> },
      { label: 'P2 · Home currency', node: <ProfileCurrency /> },
      { label: 'P2 · Currency picker', node: <ProfileCurrencySheet /> },
    ],
  },
  {
    title: 'Flow 05 — PIN Auth',
    subtitle: 'Setup → security question → Entry → Lockout (validation state)',
    screens: [
      { label: '14 · PIN Setup', node: <ScreenPinSetupHi /> },
      { label: '14a · Security question (required)', node: <PinSecurityQuestion /> },
      { label: '15 · PIN Entry · 4 of 6 entered', node: <ScreenPinEntryHi filled={4} /> },
      { label: '15 · PIN Entry · Lockout', node: <ScreenPinEntryHi filled={0} locked attempts={3} /> },
    ],
  },
  {
    title: 'Flow 01 — Quick Log',
    subtitle: 'Home → Add Expense → Save · with $0 validation state',
    screens: [
      { label: '03 · Home · Fresh user (empty)', node: <StaticHomeEmpty /> },
      { label: '03 · Home · Casual', node: <StaticHome persona="casual" /> },
      { label: '03 · Home · Budget Planner', node: <StaticHome persona="budget" /> },
      { label: '03 · Home · Event Organizer', node: <StaticHome persona="event" /> },
      { label: '04a · Amount · $0 (validation)', node: <StaticAddAmount amount="" shake /> },
      { label: '04a · Amount · typed', node: <StaticAddAmount amount="12.50" /> },
      { label: '04b · Details · with @ tag', node: <StaticAddDetails /> },
    ],
  },
  {
    title: 'Flow 02 — Browse Journal',
    subtitle: 'Diary view → entry detail → edit / delete sheet',
    screens: [
      { label: '05 · Journal · date-grouped', node: <ScreenJournalHi /> },
      { label: '06 · Journal Detail · linked to Event', node: <ScreenJournalDetailHi /> },
      { label: '06 · Journal Detail · edit / delete', node: <JournalDetailActions /> },
    ],
  },
  {
    title: 'Flow 03 — More',
    subtitle: 'Settings hub → Reports · Categories · Currency · Data Export · Clear Data',
    screens: [
      { label: '13 · More', node: <ScreenMoreHi /> },
      { label: '12 · Reports · monthly', node: <ScreenReportsHi /> },
      { label: '11 · Category List', node: <ScreenCategoryListHi /> },
      { label: '13a · Currency setting', node: <MoreCurrency /> },
      { label: '13b · Data Export', node: <MoreDataExport /> },
      { label: '13c · Clear Data', node: <MoreClearData /> },
    ],
  },
  {
    title: 'Flow 06 — Event Budget',
    subtitle: 'Empty → Create event → list → detail · includes over-budget state',
    screens: [
      { label: '07 · Events · empty (fresh)', node: <ScreenEventEmptyHi /> },
      { label: '07 · Create event · filled', node: <ScreenEventCreateHi /> },
      { label: '07 · Event Budget · 3 active (1 over)', node: <ScreenEventBudgetHi /> },
      { label: '08 · Event Detail · Bali Trip', node: <ScreenEventDetailHi /> },
    ],
  },
  {
    title: 'Flow 07 — Debt & Lending',
    subtitle: 'I Lent / I Owe toggle · add record · active + settled',
    screens: [
      { label: '09 · Debt · I Lent', node: <ScreenDebtTrackerHi view="lent" /> },
      { label: '09 · Add Record', node: <DebtAddRecord /> },
      { label: '09 · Debt Detail · Lent · John', node: <ScreenDebtDetailHi view="lent" /> },
      { label: '09 · Debt · I Owe', node: <ScreenDebtTrackerHi view="owe" /> },
      { label: '09 · Debt Detail · Owe · David', node: <ScreenDebtDetailHi view="owe" /> },
    ],
  },
  {
    title: 'Flow 08 — Shared Costs',
    subtitle: 'Input → split summary → save → history',
    screens: [
      { label: '10 · Split a bill · equal', node: <ScreenSharedCostsHi /> },
      { label: '10a · Split summary', node: <SharedSplitSummary /> },
      { label: '10b · Shared Costs · history', node: <SharedHistory /> },
    ],
  },
  {
    title: 'Edge Cases',
    subtitle: 'Validation, errors, empty & boundary states across every flow',
    screens: [
      { label: 'F01 · Draft restore', node: <EdgeDraftRestore /> },
      { label: 'F01 · Note at 200 cap', node: <EdgeNoteCap /> },
      { label: 'F01 · @ tag mutual-exclusion', node: <EdgeTagExclusion /> },
      { label: 'F02 · Journal · no results', node: <EdgeJournalNoResults /> },
      { label: 'F02 · Long-press quick note', node: <EdgeQuickNote /> },
      { label: 'F03 · Reports · all uncategorized', node: <EdgeReportsUncategorized /> },
      { label: 'F03 · Category · duplicate name', node: <EdgeCategoryDuplicate /> },
      { label: 'F05 · PIN · mismatch', node: <EdgePinMismatch /> },
      { label: 'F05 · PIN · incorrect', node: <EdgePinWrong /> },
      { label: 'F05 · PIN · recovery', node: <EdgePinRecovery /> },
      { label: 'F06 · Event · $0 + duplicate', node: <EdgeEventErrors /> },
      { label: 'F06 · Event · over budget (105%)', node: <EdgeEventWarning /> },
      { label: 'F06 · Event · closed (read-only)', node: <EdgeEventClosed /> },
      { label: 'F07 · Debt · opposite-side warning', node: <EdgeDebtConflict /> },
      { label: 'F07 · Debt · delete settled', node: <EdgeDebtSettled /> },
      { label: 'F08 · Shared · $0 total', node: <EdgeSharedZero /> },
      { label: 'F08 · Shared · max 20 + custom', node: <EdgeSharedLimits /> },
    ],
  },
];

function Flow({ flow, index }) {
  return (
    <section className="flow" data-screen-label={flow.title}>
      <header className="flowhead">
        <div className="flownum">{String(index + 1).padStart(2, '0')}</div>
        <div>
          <h2>{flow.title}</h2>
          <p>{flow.subtitle}</p>
        </div>
        <div className="flowcount">{flow.screens.length} screens</div>
      </header>
      <div className="pgrid">
        {flow.screens.map((s, i) => (
          <PhoneCard key={i} label={s.label}>{s.node}</PhoneCard>
        ))}
      </div>
    </section>
  );
}

function PrintDoc() {
  return (
    <div className="doc">
      <header className="cover">
        <div className="covertag">Pro Expense · Finance Tracker</div>
        <h1>Hi-Fi Designs — All Flows</h1>
        <p>{PRINT_FLOWS.length} flows · {PRINT_FLOWS.reduce((n, f) => n + f.screens.length, 0)} screens · A4</p>
      </header>
      {PRINT_FLOWS.map((f, i) => <Flow key={i} flow={f} index={i} />)}
    </div>
  );
}

ReactDOM.createRoot(document.getElementById('root')).render(<PrintDoc />);
