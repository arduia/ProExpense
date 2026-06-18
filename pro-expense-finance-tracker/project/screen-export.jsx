// Screen export harness — renders one screen at a time, full-bleed in a
// 414×868 frame matching the Hi-Fi artboard. Drives capture via window.showScreen(i).

const EXP_W = 414, EXP_H = 868;

// id · label · flow · element  — mirrors designs-app.jsx exactly
const SCREENS = [
  // FLOW 04 — First Launch
  ['splash',            '01 · Splash',                         'Flow 04 — First Launch',   <ScreenSplashHi />],
  ['onb-1',             '02 · Welcome',                        'Flow 04 — First Launch',   <ScreenOnboardingHi slide={1} />],
  ['onb-2',             '02 · Quick Log',                      'Flow 04 — First Launch',   <ScreenOnboardingHi slide={2} />],
  ['onb-3',             '02 · Shared Costs',                   'Flow 04 — First Launch',   <ScreenOnboardingHi slide={3} />],
  ['onb-4',             '02 · Event Budget',                   'Flow 04 — First Launch',   <ScreenOnboardingHi slide={4} />],
  ['onb-5',             '02 · Journal · Get started',          'Flow 04 — First Launch',   <ScreenOnboardingHi slide={5} />],
  // Profile Setup
  ['prof-name',         'P1 · Profile name',                   'Flow 04 — Profile Setup',  <ProfileName />],
  ['prof-currency',     'P2 · Home currency',                  'Flow 04 — Profile Setup',  <ProfileCurrency />],
  ['prof-currency-sheet','P2 · Currency picker',               'Flow 04 — Profile Setup',  <ProfileCurrencySheet />],
  // FLOW 05 — PIN Auth
  ['pin-setup',         '14 · PIN Setup',                      'Flow 05 — PIN Auth',       <ScreenPinSetupHi />],
  ['pin-security',      '14a · Security question (required)',  'Flow 05 — PIN Auth',       <PinSecurityQuestion />],
  ['pin-entry',         '15 · PIN Entry · 4 of 6 entered',     'Flow 05 — PIN Auth',       <ScreenPinEntryHi filled={4} />],
  ['pin-lock',          '15 · PIN Entry · Lockout',            'Flow 05 — PIN Auth',       <ScreenPinEntryHi filled={0} locked attempts={3} />],
  // FLOW 01 — Quick Log
  ['home-empty',        '03 · Home · Fresh user (empty)',      'Flow 01 — Quick Log',      <StaticHomeEmpty />],
  ['home-casual',       '03 · Home · Casual',                  'Flow 01 — Quick Log',      <StaticHome persona="casual" />],
  ['home-budget',       '03 · Home · Budget Planner',          'Flow 01 — Quick Log',      <StaticHome persona="budget" />],
  ['home-event',        '03 · Home · Event Organizer',         'Flow 01 — Quick Log',      <StaticHome persona="event" />],
  ['add-zero',          '04a · Amount · $0 (validation)',      'Flow 01 — Quick Log',      <StaticAddAmount amount="" shake />],
  ['add-amount',        '04a · Amount · typed',                'Flow 01 — Quick Log',      <StaticAddAmount amount="12.50" />],
  ['add-details',       '04b · Details · with @ tag',          'Flow 01 — Quick Log',      <StaticAddDetails />],
  // FLOW 02 — Journal
  ['journal-list',      '05 · Journal · date-grouped',         'Flow 02 — Browse Journal', <ScreenJournalHi />],
  ['journal-detail',    '06 · Journal Detail · linked to Event','Flow 02 — Browse Journal', <ScreenJournalDetailHi />],
  ['journal-actions',   '06 · Journal Detail · edit / delete', 'Flow 02 — Browse Journal', <JournalDetailActions />],
  // FLOW 03 — More
  ['more-hub',          '13 · More',                           'Flow 03 — More',           <ScreenMoreHi />],
  ['reports',           '12 · Reports · monthly',              'Flow 03 — More',           <ScreenReportsHi />],
  ['categories',        '11 · Category List',                  'Flow 03 — More',           <ScreenCategoryListHi />],
  ['more-currency',     '13a · Currency setting',              'Flow 03 — More',           <MoreCurrency />],
  ['more-export',       '13b · Data Export',                   'Flow 03 — More',           <MoreDataExport />],
  ['more-clear',        '13c · Clear Data',                    'Flow 03 — More',           <MoreClearData />],
  // FLOW 06 — Event Budget
  ['event-empty',       '07 · Events · empty (fresh)',         'Flow 06 — Event Budget',   <ScreenEventEmptyHi />],
  ['event-create',      '07 · Create event · filled',          'Flow 06 — Event Budget',   <ScreenEventCreateHi />],
  ['event-list',        '07 · Event Budget · 3 active (1 over)','Flow 06 — Event Budget',   <ScreenEventBudgetHi />],
  ['event-detail',      '08 · Event Detail · Bali Trip',       'Flow 06 — Event Budget',   <ScreenEventDetailHi />],
  // FLOW 07 — Debt
  ['debt-lent',         '09 · Debt · I Lent',                  'Flow 07 — Debt & Lending', <ScreenDebtTrackerHi view="lent" />],
  ['debt-add',          '09 · Add Record',                     'Flow 07 — Debt & Lending', <DebtAddRecord />],
  ['debt-lent-detail',  '09 · Debt Detail · Lent · John',      'Flow 07 — Debt & Lending', <ScreenDebtDetailHi view="lent" />],
  ['debt-owe',          '09 · Debt · I Owe',                   'Flow 07 — Debt & Lending', <ScreenDebtTrackerHi view="owe" />],
  ['debt-owe-detail',   '09 · Debt Detail · Owe · David',      'Flow 07 — Debt & Lending', <ScreenDebtDetailHi view="owe" />],
  // FLOW 08 — Shared Costs
  ['shared-input',      '10 · Split a bill · equal',           'Flow 08 — Shared Costs',   <ScreenSharedCostsHi />],
  ['shared-summary',    '10a · Split summary',                 'Flow 08 — Shared Costs',   <SharedSplitSummary />],
  ['shared-history',    '10b · Shared Costs · history',        'Flow 08 — Shared Costs',   <SharedHistory />],
  // EDGE CASES
  ['edge-draft',        'Draft restore',                       'Edge Cases',               <EdgeDraftRestore />],
  ['edge-note',         'Note at 200 cap',                     'Edge Cases',               <EdgeNoteCap />],
  ['edge-tag',          '@ tag mutual-exclusion',              'Edge Cases',               <EdgeTagExclusion />],
  ['edge-search',       'Journal · no results',                'Edge Cases',               <EdgeJournalNoResults />],
  ['edge-quicknote',    'Long-press quick note',               'Edge Cases',               <EdgeQuickNote />],
  ['edge-reports-unc',  'Reports · all uncategorized',         'Edge Cases',               <EdgeReportsUncategorized />],
  ['edge-cat-dup',      'Category · duplicate name',           'Edge Cases',               <EdgeCategoryDuplicate />],
  ['edge-pin-mismatch', 'PIN · mismatch',                      'Edge Cases',               <EdgePinMismatch />],
  ['edge-pin-wrong',    'PIN · incorrect',                     'Edge Cases',               <EdgePinWrong />],
  ['edge-pin-recovery', 'PIN · recovery',                      'Edge Cases',               <EdgePinRecovery />],
  ['edge-event-errors', 'Event · $0 + duplicate',              'Edge Cases',               <EdgeEventErrors />],
  ['edge-event-warn',   'Event · over budget (105%)',          'Edge Cases',               <EdgeEventWarning />],
  ['edge-event-closed', 'Event · closed (read-only)',          'Edge Cases',               <EdgeEventClosed />],
  ['edge-debt-conflict','Debt · opposite-side warning',        'Edge Cases',               <EdgeDebtConflict />],
  ['edge-debt-settled', 'Debt · delete settled',               'Edge Cases',               <EdgeDebtSettled />],
  ['edge-shared-zero',  'Shared · $0 total',                   'Edge Cases',               <EdgeSharedZero />],
  ['edge-shared-limits','Shared · max 20 + custom',            'Edge Cases',               <EdgeSharedLimits />],
];

window.SCREENS_META = SCREENS.map(([id, label, flow]) => ({ id, label, flow }));

const expRoot = ReactDOM.createRoot(document.getElementById('root'));

// The capture window is ~540px tall, shorter than the 868 frame, so we render
// the frame anchored top-left at a fixed scale that fits, then crop to the
// opaque bounding box in post. Scale chosen so 868·s ≈ 505 (safe margin).
const EXP_SCALE = 0.58;

function Frame({ node }) {
  return (
    <div style={{ position: 'fixed', top: 0, left: 0, width: EXP_W * EXP_SCALE, height: EXP_H * EXP_SCALE, overflow: 'hidden' }}>
      <div style={{ width: EXP_W, height: EXP_H, background: '#1f1a14', display: 'flex', alignItems: 'center', justifyContent: 'center', transform: `scale(${EXP_SCALE})`, transformOrigin: 'top left' }}>
        {node}
      </div>
    </div>
  );
}

window.showScreen = function (i) {
  const entry = SCREENS[i];
  if (!entry) return null;
  expRoot.render(<Frame node={entry[3]} />);
  return entry[0];
};

window.showScreen(0);
