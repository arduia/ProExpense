// Top-level Hi-Fi Designs page — 8 flow sections, each is a DCSection
// with multiple DCArtboards (one per screen state).
// DCArtboard MUST appear as a DIRECT child of DCSection (no wrapper).

const ABW = 414;
const ABH = 868;
const ABS = { background: 'transparent', borderRadius: 56, overflow: 'visible', boxShadow: 'none', padding: 0, display: 'flex', alignItems: 'center', justifyContent: 'center' };

function DesignsApp() {
  return (
    <DesignCanvas accent="#c8482a" background="#1f1a14">

      {/* ─── FLOW 04 — First Launch ─── */}
      <DCSection id="first-launch" title="Flow 04 — First Launch" subtitle="Splash → onboarding (Get started on every slide) → Home">
        <DCArtboard id="splash" label="01 · Splash" width={ABW} height={ABH} style={ABS}><ScreenSplashHi /></DCArtboard>
        <DCArtboard id="onb-1" label="02 · Welcome" width={ABW} height={ABH} style={ABS}><ScreenOnboardingHi slide={1} /></DCArtboard>
        <DCArtboard id="onb-2" label="02 · Quick Log" width={ABW} height={ABH} style={ABS}><ScreenOnboardingHi slide={2} /></DCArtboard>
        <DCArtboard id="onb-3" label="02 · Shared Costs" width={ABW} height={ABH} style={ABS}><ScreenOnboardingHi slide={3} /></DCArtboard>
        <DCArtboard id="onb-4" label="02 · Event Budget" width={ABW} height={ABH} style={ABS}><ScreenOnboardingHi slide={4} /></DCArtboard>
        <DCArtboard id="onb-5" label="02 · Journal · Get started" width={ABW} height={ABH} style={ABS}><ScreenOnboardingHi slide={5} /></DCArtboard>
      </DCSection>

      {/* ─── PROFILE SETUP (first run, between Onboarding and Home) ─── */}
      <DCSection id="profile-setup" title="Flow 04 — Profile Setup" subtitle="First run · between Onboarding and Home · account-free personalization">
        <DCArtboard id="prof-name" label="P1 · Profile name" width={ABW} height={ABH} style={ABS}><ProfileName /></DCArtboard>
        <DCArtboard id="prof-currency" label="P2 · Home currency" width={ABW} height={ABH} style={ABS}><ProfileCurrency /></DCArtboard>
        <DCArtboard id="prof-currency-sheet" label="P2 · Currency picker" width={ABW} height={ABH} style={ABS}><ProfileCurrencySheet /></DCArtboard>
      </DCSection>

      {/* ─── FLOW 05 — PIN Auth ─── */}
      <DCSection id="pin-auth" title="Flow 05 — PIN Auth" subtitle="Setup → security question → Entry → Lockout (validation state)">
        <DCArtboard id="pin-setup" label="14 · PIN Setup" width={ABW} height={ABH} style={ABS}><ScreenPinSetupHi /></DCArtboard>
        <DCArtboard id="pin-security" label="14a · Security question (required)" width={ABW} height={ABH} style={ABS}><PinSecurityQuestion /></DCArtboard>
        <DCArtboard id="pin-entry" label="15 · PIN Entry · 4 of 6 entered" width={ABW} height={ABH} style={ABS}><ScreenPinEntryHi filled={4} /></DCArtboard>
        <DCArtboard id="pin-lock" label="15 · PIN Entry · Lockout" width={ABW} height={ABH} style={ABS}><ScreenPinEntryHi filled={0} locked attempts={3} /></DCArtboard>
      </DCSection>

      {/* ─── FLOW 01 — Quick Log ─── */}
      <DCSection id="quick-log" title="Flow 01 — Quick Log" subtitle="Home → Add Expense → Save · with $0 validation state">
        <DCArtboard id="home-empty" label="03 · Home · Fresh user (empty)" width={ABW} height={ABH} style={ABS}><StaticHomeEmpty /></DCArtboard>
        <DCArtboard id="home-casual" label="03 · Home · Casual" width={ABW} height={ABH} style={ABS}><StaticHome persona="casual" /></DCArtboard>
        <DCArtboard id="home-budget" label="03 · Home · Budget Planner" width={ABW} height={ABH} style={ABS}><StaticHome persona="budget" /></DCArtboard>
        <DCArtboard id="home-event" label="03 · Home · Event Organizer" width={ABW} height={ABH} style={ABS}><StaticHome persona="event" /></DCArtboard>
        <DCArtboard id="add-zero" label="04a · Amount · $0 (validation)" width={ABW} height={ABH} style={ABS}><StaticAddAmount amount="" shake /></DCArtboard>
        <DCArtboard id="add-amount" label="04a · Amount · typed" width={ABW} height={ABH} style={ABS}><StaticAddAmount amount="12.50" /></DCArtboard>
        <DCArtboard id="add-details" label="04b · Details · with @ tag" width={ABW} height={ABH} style={ABS}><StaticAddDetails /></DCArtboard>
      </DCSection>

      {/* ─── FLOW 02 — Journal ─── */}
      <DCSection id="journal" title="Flow 02 — Browse Journal" subtitle="Diary view → entry detail → edit / delete sheet">
        <DCArtboard id="journal-list" label="05 · Journal · date-grouped" width={ABW} height={ABH} style={ABS}><ScreenJournalHi /></DCArtboard>
        <DCArtboard id="journal-detail" label="06 · Journal Detail · linked to Event" width={ABW} height={ABH} style={ABS}><ScreenJournalDetailHi /></DCArtboard>
        <DCArtboard id="journal-actions" label="06 · Journal Detail · edit / delete" width={ABW} height={ABH} style={ABS}><JournalDetailActions /></DCArtboard>
      </DCSection>

      {/* ─── FLOW 03 — More / Reports / Categories ─── */}
      <DCSection id="more" title="Flow 03 — More" subtitle="Settings hub → Reports · Categories · Currency · Data Export · Clear Data">
        <DCArtboard id="more-hub" label="13 · More" width={ABW} height={ABH} style={ABS}><ScreenMoreHi /></DCArtboard>
        <DCArtboard id="reports" label="12 · Reports · monthly" width={ABW} height={ABH} style={ABS}><ScreenReportsHi /></DCArtboard>
        <DCArtboard id="categories" label="11 · Category List" width={ABW} height={ABH} style={ABS}><ScreenCategoryListHi /></DCArtboard>
        <DCArtboard id="more-currency" label="13a · Currency setting" width={ABW} height={ABH} style={ABS}><MoreCurrency /></DCArtboard>
        <DCArtboard id="more-export" label="13b · Data Export" width={ABW} height={ABH} style={ABS}><MoreDataExport /></DCArtboard>
        <DCArtboard id="more-clear" label="13c · Clear Data" width={ABW} height={ABH} style={ABS}><MoreClearData /></DCArtboard>
      </DCSection>

      {/* ─── FLOW 06 — Event Budget ─── */}
      <DCSection id="event-budget" title="Flow 06 — Event Budget" subtitle="Empty → Create event → list → detail · includes over-budget state">
        <DCArtboard id="event-empty" label="07 · Events · empty (fresh)" width={ABW} height={ABH} style={ABS}><ScreenEventEmptyHi /></DCArtboard>
        <DCArtboard id="event-create" label="07 · Create event · filled" width={ABW} height={ABH} style={ABS}><ScreenEventCreateHi /></DCArtboard>
        <DCArtboard id="event-list" label="07 · Event Budget · 3 active (1 over)" width={ABW} height={ABH} style={ABS}><ScreenEventBudgetHi /></DCArtboard>
        <DCArtboard id="event-detail" label="08 · Event Detail · Bali Trip" width={ABW} height={ABH} style={ABS}><ScreenEventDetailHi /></DCArtboard>
      </DCSection>

      {/* ─── FLOW 07 — Debt Tracker ─── */}
      <DCSection id="debt" title="Flow 07 — Debt & Lending" subtitle="I Lent / I Owe toggle · add record · active + settled">
        <DCArtboard id="debt-lent" label="09 · Debt · I Lent" width={ABW} height={ABH} style={ABS}><ScreenDebtTrackerHi view="lent" /></DCArtboard>
        <DCArtboard id="debt-add" label="09 · Add Record" width={ABW} height={ABH} style={ABS}><DebtAddRecord /></DCArtboard>
        <DCArtboard id="debt-lent-detail" label="09 · Debt Detail · Lent · John" width={ABW} height={ABH} style={ABS}><ScreenDebtDetailHi view="lent" /></DCArtboard>
        <DCArtboard id="debt-owe" label="09 · Debt · I Owe" width={ABW} height={ABH} style={ABS}><ScreenDebtTrackerHi view="owe" /></DCArtboard>
        <DCArtboard id="debt-owe-detail" label="09 · Debt Detail · Owe · David" width={ABW} height={ABH} style={ABS}><ScreenDebtDetailHi view="owe" /></DCArtboard>
      </DCSection>

      {/* ─── FLOW 08 — Shared Costs ─── */}
      <DCSection id="shared" title="Flow 08 — Shared Costs" subtitle="Input → split summary → save → history">
        <DCArtboard id="shared-input" label="10 · Split a bill · equal" width={ABW} height={ABH} style={ABS}><ScreenSharedCostsHi /></DCArtboard>
        <DCArtboard id="shared-summary" label="10a · Split summary" width={ABW} height={ABH} style={ABS}><SharedSplitSummary /></DCArtboard>
        <DCArtboard id="shared-history" label="10b · Shared Costs · history" width={ABW} height={ABH} style={ABS}><SharedHistory /></DCArtboard>
      </DCSection>

      {/* ─── EDGE CASES (all flows) ─── */}
      <DCSection id="edge-cases" title="Edge Cases" subtitle="Validation, errors, empty & boundary states across every flow">
        {/* Flow 01 */}
        <DCArtboard id="edge-draft" label="Draft restore" width={ABW} height={ABH} style={ABS}><EdgeDraftRestore /></DCArtboard>
        <DCArtboard id="edge-note" label="Note at 200 cap" width={ABW} height={ABH} style={ABS}><EdgeNoteCap /></DCArtboard>
        <DCArtboard id="edge-tag" label="@ tag mutual-exclusion" width={ABW} height={ABH} style={ABS}><EdgeTagExclusion /></DCArtboard>
        {/* Flow 02 */}
        <DCArtboard id="edge-search" label="Journal · no results" width={ABW} height={ABH} style={ABS}><EdgeJournalNoResults /></DCArtboard>
        <DCArtboard id="edge-quicknote" label="Long-press quick note" width={ABW} height={ABH} style={ABS}><EdgeQuickNote /></DCArtboard>
        {/* Flow 03 */}
        <DCArtboard id="edge-reports-unc" label="Reports · all uncategorized" width={ABW} height={ABH} style={ABS}><EdgeReportsUncategorized /></DCArtboard>
        <DCArtboard id="edge-cat-dup" label="Category · duplicate name" width={ABW} height={ABH} style={ABS}><EdgeCategoryDuplicate /></DCArtboard>
        {/* Flow 05 */}
        <DCArtboard id="edge-pin-mismatch" label="PIN · mismatch" width={ABW} height={ABH} style={ABS}><EdgePinMismatch /></DCArtboard>
        <DCArtboard id="edge-pin-wrong" label="PIN · incorrect" width={ABW} height={ABH} style={ABS}><EdgePinWrong /></DCArtboard>
        <DCArtboard id="edge-pin-recovery" label="PIN · recovery" width={ABW} height={ABH} style={ABS}><EdgePinRecovery /></DCArtboard>
        {/* Flow 06 */}
        <DCArtboard id="edge-event-errors" label="Event · $0 + duplicate" width={ABW} height={ABH} style={ABS}><EdgeEventErrors /></DCArtboard>
        <DCArtboard id="edge-event-warn" label="Event · over budget (105%)" width={ABW} height={ABH} style={ABS}><EdgeEventWarning /></DCArtboard>
        <DCArtboard id="edge-event-closed" label="Event · closed (read-only)" width={ABW} height={ABH} style={ABS}><EdgeEventClosed /></DCArtboard>
        {/* Flow 07 */}
        <DCArtboard id="edge-debt-conflict" label="Debt · opposite-side warning" width={ABW} height={ABH} style={ABS}><EdgeDebtConflict /></DCArtboard>
        <DCArtboard id="edge-debt-settled" label="Debt · delete settled" width={ABW} height={ABH} style={ABS}><EdgeDebtSettled /></DCArtboard>
        {/* Flow 08 */}
        <DCArtboard id="edge-shared-zero" label="Shared · $0 total" width={ABW} height={ABH} style={ABS}><EdgeSharedZero /></DCArtboard>
        <DCArtboard id="edge-shared-limits" label="Shared · max 20 + custom" width={ABW} height={ABH} style={ABS}><EdgeSharedLimits /></DCArtboard>
      </DCSection>

    </DesignCanvas>
  );
}

ReactDOM.createRoot(document.getElementById('root')).render(<DesignsApp />);
