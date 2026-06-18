// Root storyboard app — orchestrates flows, arrows, callouts, tweaks panel.

const { useState } = React;

// ─────────────────────────────────────────────────────────────
// Arrow between screens
// ─────────────────────────────────────────────────────────────
function Arrow({ label }) {
  return (
    <div style={{ display: 'flex', flexDirection: 'column', alignItems: 'center', justifyContent: 'center', flexShrink: 0, padding: '0 8px', minWidth: 80 }}>
      <div style={{ fontFamily: 'var(--mono)', fontSize: 9, color: W_ACCENT, textTransform: 'uppercase', letterSpacing: '0.16em', marginBottom: 8, textAlign: 'center', maxWidth: 110 }}>
        {label}
      </div>
      <svg width="80" height="22" viewBox="0 0 80 22">
        <line x1="0" y1="11" x2="70" y2="11" stroke={W_INK} strokeWidth="1.2" strokeDasharray="3 3" />
        <path d="M70 5 L78 11 L70 17 Z" fill={W_INK} />
      </svg>
    </div>
  );
}

// ─────────────────────────────────────────────────────────────
// Screen card — phone w/ overlay pins + caption with callout list
// ─────────────────────────────────────────────────────────────
function ScreenCard({ num, title, label, children, pins = [], callouts = [], showCallouts = true, statusBar = true, homeBar = true }) {
  return (
    <div style={{ display: 'flex', flexDirection: 'column', alignItems: 'center', flexShrink: 0, width: 340 }}>
      <div style={{ position: 'relative' }}>
        <WirePhone num={num} title={title} label={label} statusBar={statusBar} homeBar={homeBar}>
          {children}
        </WirePhone>
        {showCallouts && pins.map((p, i) => <Pin key={i} n={p.n} top={p.top} left={p.left} right={p.right} bottom={p.bottom} />)}
      </div>
      {showCallouts && callouts.length > 0 && (
        <div style={{ marginTop: 18, width: 320, display: 'flex', flexDirection: 'column', gap: 10 }}>
          {callouts.map((c, i) => (
            <div key={i} style={{ display: 'flex', gap: 10, alignItems: 'flex-start' }}>
              <div style={{ width: 18, height: 18, borderRadius: 99, background: W_ACCENT, color: '#fbf2dd', display: 'flex', alignItems: 'center', justifyContent: 'center', fontSize: 10, fontWeight: 600, fontFamily: 'var(--mono)', flexShrink: 0, marginTop: 1 }}>{c.n}</div>
              <div style={{ fontSize: 12, lineHeight: 1.45, color: W_INK }}>
                {c.title && <span style={{ fontWeight: 600 }}>{c.title} — </span>}
                <span style={{ color: '#3a3a32' }}>{c.text}</span>
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  );
}

// ─────────────────────────────────────────────────────────────
// Flow row — title + scrollable horizontal strip of cards/arrows
// ─────────────────────────────────────────────────────────────
function FlowRow({ num, title, blurb, children }) {
  return (
    <section style={{ marginTop: 72 }}>
      <div style={{ display: 'grid', gridTemplateColumns: '220px 1fr', gap: 48, paddingBottom: 18, borderBottom: `1px solid ${W_LINE}`, marginBottom: 32 }}>
        <div style={{ fontFamily: 'var(--mono)', fontSize: 11, letterSpacing: '0.12em', textTransform: 'uppercase', color: W_MUTED, paddingTop: 10 }}>
          Flow {num}
        </div>
        <div>
          <h2 style={{ fontFamily: 'var(--serif)', fontWeight: 400, fontSize: 36, lineHeight: 1, letterSpacing: '-0.015em', margin: '0 0 10px' }}>
            {title}
          </h2>
          <div style={{ color: W_MUTED, fontSize: 14, maxWidth: '64ch' }}>{blurb}</div>
        </div>
      </div>
      <div style={{
        display: 'flex',
        alignItems: 'flex-start',
        gap: 4,
        overflowX: 'auto',
        padding: '12px 0 32px',
        // give the scrollbar a tiny breathing room
      }}>
        <div style={{ width: 24, flexShrink: 0 }} />
        {children}
        <div style={{ width: 24, flexShrink: 0 }} />
      </div>
    </section>
  );
}

// ─────────────────────────────────────────────────────────────
// Tweaks panel — wraps the host protocol
// ─────────────────────────────────────────────────────────────
const TWEAK_DEFAULTS = /*EDITMODE-BEGIN*/{
  "header_variant": "casual",
  "empty_state": false,
  "show_callouts": true
}/*EDITMODE-END*/;

function App() {
  const [t, setTweak] = useTweaks(TWEAK_DEFAULTS);
  const variant = t.header_variant;
  const empty = t.empty_state;
  const showCallouts = t.show_callouts;

  return (
    <>
      <TweaksPanel title="Tweaks">
        <TweakSection label="Home header">
          <TweakRadio
            label="Persona variant"
            value={variant}
            onChange={(v) => setTweak('header_variant', v)}
            options={[
              { value: 'casual', label: 'Casual' },
              { value: 'budget', label: 'Budget' },
              { value: 'event', label: 'Event' },
            ]}
          />
        </TweakSection>
        <TweakSection label="State">
          <TweakToggle
            label="Empty state"
            value={empty}
            onChange={(v) => setTweak('empty_state', v)}
          />
        </TweakSection>
        <TweakSection label="Document">
          <TweakToggle
            label="Show annotation callouts"
            value={showCallouts}
            onChange={(v) => setTweak('show_callouts', v)}
          />
        </TweakSection>
      </TweaksPanel>

      <div className="page">
        {/* DOC META */}
        <div className="doc-meta">
          <div className="lhs">
            <span>Pro Expense</span>
            <span>Wireframes · MVP Flow</span>
            <span>v0.1 · May 2026</span>
          </div>
          <div className="pill">LOW-FI · INTERNAL</div>
        </div>

        {/* HERO */}
        <div className="hero">
          <h1 className="title">User <em>flow</em><br/>wireframes.</h1>
          <div className="deck">
            <p className="lede">A storyboard of all fifteen screens — laid out in the order a real user moves through them, across eight flows.</p>
            <p style={{ margin: 0, color: '#3a3a32' }}>Grayscale boxes, no color. Numbered callouts capture behaviors and edge cases pulled directly from the screen-flow spec. Use the <span style={{ color: W_ACCENT, fontWeight: 500 }}>Tweaks</span> panel to swap the Home header per persona or flip to empty states.</p>
          </div>
        </div>

        {/* LEGEND */}
        <div style={{ marginTop: 56, display: 'flex', gap: 32, padding: '20px 24px', border: `1px solid ${W_LINE}`, borderRadius: 8, flexWrap: 'wrap', background: '#fbf8ee' }}>
          <LegendItem swatch={<div style={{ width: 16, height: 16, background: W_FILL, borderRadius: 3 }} />} label="Content block" />
          <LegendItem swatch={<div style={{ width: 16, height: 16, border: `1.2px dashed ${W_LINE}`, borderRadius: 3 }} />} label="Empty / placeholder" />
          <LegendItem swatch={<div style={{ width: 18, height: 18, borderRadius: 99, background: W_ACCENT }} />} label="Annotation pin" />
          <LegendItem swatch={
            <svg width="28" height="10" viewBox="0 0 28 10">
              <line x1="0" y1="5" x2="20" y2="5" stroke={W_INK} strokeWidth="1.2" strokeDasharray="3 3" />
              <path d="M20 1 L26 5 L20 9 Z" fill={W_INK} />
            </svg>
          } label="Screen transition" />
          <LegendItem swatch={
            <div style={{ width: 18, height: 18, border: `1.2px solid ${W_INK}`, borderRadius: 99, display: 'flex', alignItems: 'center', justifyContent: 'center', fontSize: 14, lineHeight: 1 }}>+</div>
          } label="Add Expense — centered in bottom nav on every screen" />
        </div>

        {/* ───────────────── FLOW 1: Quick Log ───────────────── */}
        <FlowRow
          num="01"
          title={<span>Quick <em style={{ color: W_ACCENT, fontStyle: 'italic' }}>manual log</em></span>}
          blurb="The fastest path through the app — tap +, type amount, pick category, save. Target: under 5 seconds."
        >
          <ScreenCard
            num="03"
            title="Home"
            label={empty ? 'empty' : variant}
            showCallouts={showCallouts}
            pins={[
              { n: 1, top: 70, left: 20 },
              { n: 2, top: 240, left: 20 },
              { n: 3, bottom: 70, left: '50%' },
              { n: 4, bottom: 30, left: '20%' },
            ]}
            callouts={[
              { n: 1, title: 'Contextual header', text: `Adapts per persona. Currently: ${variant === 'casual' ? '"Total spent this month" — no budget set.' : variant === 'budget' ? 'Spent / Budget when monthly budget is configured.' : 'Active event card with remaining balance + countdown.'}` },
              { n: 2, title: 'Quick access menu', text: 'Surfaces the most-used secondary features (Reports, Debt, Split, Events) right on Home — same destinations as More, just one tap shorter. User-customizable via "Customize".' },
              { n: 3, title: 'Centered Add (+)', text: 'Sits in the middle of the bottom nav across every screen except PIN. The most-used action gets the best thumb spot.' },
              { n: 4, title: 'Bottom nav', text: 'Home · Budget · [ + ] · Journal · More — 4 tabs flanking the centered Add. Persistent across primary screens.' },
              ...(empty ? [{ n: '✱', title: 'Empty state', text: '"No expenses yet. Start by logging your first one!" — single CTA arrow points at the + button.' }] : [              { n: '✱', title: 'Recent list', text: 'Last 5–10 entries with amount, category, time. Tap row → Journal Detail (same screen as journal entry). Above it, Quick Access surfaces features that otherwise live in More.' }]),
            ]}
          >
            <ScreenHome variant={variant} empty={empty} />
          </ScreenCard>

          <Arrow label="tap +" />

          <ScreenCard
            num="04 · a"
            title="Add Expense"
            label="amount"
            showCallouts={showCallouts}
            pins={[
              { n: 1, top: 100, left: '50%' },
              { n: 2, top: 230, right: 24 },
              { n: 3, bottom: 220, right: 24 },
            ]}
            callouts={[
              { n: 1, title: 'Amount-first', text: 'Numeric keypad auto-opens on load. Large centered display — the only thing on screen until the user has typed.' },
              { n: 2, title: 'Category chips', text: 'Horizontal scroll. "Food" auto-selected. Changeable here, fine-tunable on next screen.' },
              { n: 3, title: 'Reachable Next / Save', text: 'Next button is embedded directly inside the keypad (top-right column, spans 2 rows) so it\'s in thumb range while typing. Save (smaller) sits below to commit with the default category in one tap. Both disabled until amount > $0.' },
              { n: '⌫', title: 'Edge case', text: 'Back with no amount → silent navigate. App force-close mid-entry → draft restored on next launch, before PIN.' },
            ]}
          >
            <ScreenAddAmount />
          </ScreenCard>

          <Arrow label="tap next" />

          <ScreenCard
            num="04 · b"
            title="Add Expense"
            label="details"
            showCallouts={showCallouts}
            pins={[
              { n: 1, top: 90, left: 20 },
              { n: 2, top: 200, right: 24 },
              { n: 3, top: 340, left: 20 },
              { n: 4, bottom: 70, right: 16 },
            ]}
            callouts={[
              { n: 1, title: 'Read-only amount', text: 'Tappable to go back and edit. Pre-filled from Amount screen and persisted on back navigation.' },
              { n: 2, title: 'Category — default + custom', text: 'Six locked defaults plus any custom categories the user has added. "+ Add" jumps to Category List to create a new one inline.' },
              { n: 3, title: '@ Tag', text: 'Optional. Hidden if no active events or debts. Single selection — Event OR Debt, not both. Greyed type after pick; clear to reset.' },
              { n: 4, title: 'Save', text: 'Returns to Home — new entry appears at top of Recent. Note max 200 chars; future dates allowed. Date + time both editable.' },
            ]}
          >
            <ScreenAddDetails />
          </ScreenCard>

          <Arrow label="save" />

          <ScreenCard
            num="03"
            title="Home"
            label="filled · post-save"
            showCallouts={showCallouts}
            pins={[
              { n: 1, top: 280, left: 20 },
            ]}
            callouts={[
              { n: 1, title: 'New entry on top', text: 'Recent transactions list updates immediately. Header total recalculates. Floating + persists.' },
              { n: '↺', title: 'Loop', text: 'User can keep logging — flow is designed to be repeated dozens of times a week.' },
            ]}
          >
            <ScreenHome variant={variant} empty={false} />
          </ScreenCard>
        </FlowRow>

        {/* ───────────────── FLOW 2: Journal ───────────────── */}
        <FlowRow
          num="02"
          title={<span>Browse the <em style={{ color: W_ACCENT, fontStyle: 'italic' }}>journal</em></span>}
          blurb="Diary-style history — date-grouped, always expanded. Tap any entry for full detail and editing."
        >
          <ScreenCard
            num="03"
            title="Home"
            label="any state"
            showCallouts={showCallouts}
            pins={[{ n: 1, bottom: 36, left: 230 }]}
            callouts={[
              { n: 1, title: 'Journal tab', text: 'Right of the centered Add — one of four tabs. Tap to enter the diary view.' },
            ]}
          >
            <ScreenHome variant={variant} empty={empty} />
          </ScreenCard>

          <Arrow label="journal tab" />

          <ScreenCard
            num="05"
            title="Journal"
            label={empty ? 'empty' : 'date-grouped'}
            showCallouts={showCallouts}
            pins={[
              { n: 1, top: 130, left: 20 },
              { n: 2, top: 180, left: 20 },
              { n: 3, top: 250, left: 20 },
              { n: 4, bottom: 60, left: '50%' },
            ]}
            callouts={[
              { n: 1, title: 'Search', text: 'Keyword, note or amount. When active, list flattens — no date grouping, shows amount + category + date + note.' },
              { n: 2, title: 'Category filter', text: 'Chips above the list. "All" is default.' },
              { n: 3, title: 'Day groups', text: 'Always expanded — no collapse. Header shows date + daily total. Ordered by expense date; within day, by created date (newest first).' },
              { n: 4, title: 'Centered Add', text: 'Long-press an entry instead → quick-note bottom sheet without leaving the list.' },
            ]}
          >
            <ScreenJournal empty={empty} />
          </ScreenCard>

          <Arrow label="tap entry" />

          <ScreenCard
            num="06"
            title="Journal Detail"
            label="single entry"
            showCallouts={showCallouts}
            pins={[
              { n: 1, top: 90, right: 24 },
              { n: 2, top: 200, left: '50%' },
              { n: 3, top: 350, left: 24 },
              { n: 4, bottom: 60, left: 20 },
            ]}
            callouts={[
              { n: 1, title: 'Action menu (⋯)', text: 'Opens bottom sheet — Edit (→ Add Expense Details, pre-filled), Delete (confirm dialog), Cancel.' },
              { n: 2, title: 'Amount + meta', text: 'Large prominent number. Category icon + label, full timestamp.' },
              { n: 3, title: 'Inline note edit', text: 'Tap the note to edit in place — no separate screen.' },
              { n: 4, title: 'Edit / Delete', text: 'Edit jumps to Add Expense Details with this entry loaded. Delete returns to Journal after confirm.' },
            ]}
          >
            <ScreenJournalDetail />
          </ScreenCard>
        </FlowRow>

        {/* ───────────────── FLOW 3: More → Reports & Categories ───────────────── */}
        <FlowRow
          num="03"
          title={<span>More · <em style={{ color: W_ACCENT, fontStyle: 'italic' }}>reports & categories</em></span>}
          blurb="The fourth tab is the hub for everything not on the main canvas — secondary features, settings, data."
        >
          <ScreenCard
            num="03"
            title="Home"
            label="any state"
            showCallouts={showCallouts}
            pins={[{ n: 1, bottom: 38, right: 30 }]}
            callouts={[
              { n: 1, title: 'More tab', text: 'Bottom-right tab — the catch-all for Debt, Shared Costs, Reports, Categories, settings.' },
            ]}
          >
            <ScreenHome variant={variant} empty={empty} />
          </ScreenCard>

          <Arrow label="more tab" />

          <ScreenCard
            num="13"
            title="More"
            label="hub"
            showCallouts={showCallouts}
            pins={[
              { n: 1, top: 150, left: 20 },
              { n: 2, top: 280, left: 20 },
              { n: 3, bottom: 160, left: 20 },
            ]}
            callouts={[
              { n: 1, title: 'Features', text: 'Reports + Category List live here for MVP. Debt + Shared Costs join post-MVP.' },
              { n: 2, title: 'Settings', text: 'Currency, monthly budget (drives Budget Planner header), PIN, language, theme.' },
              { n: 3, title: 'Data', text: 'Export as zipped CSVs (expenses, events, debts, shared_costs). Clear data is selective with confirmation per item.' },
            ]}
          >
            <ScreenMore />
          </ScreenCard>

          <Arrow label="tap reports" />

          <ScreenCard
            num="12"
            title="Reports"
            label={empty ? 'empty' : 'monthly'}
            showCallouts={showCallouts}
            pins={[
              { n: 1, top: 100, left: '50%' },
              { n: 2, top: 220, left: '50%' },
              { n: 3, bottom: 200, left: 20 },
            ]}
            callouts={[
              { n: 1, title: 'Monthly only (MVP)', text: 'Weekly/custom ranges are post-MVP. If no data this month, auto-falls-back to last month with data.' },
              { n: 2, title: 'Donut by category', text: 'Single chart. Uncategorized only appears if such entries exist. All-uncategorized → tip surfaces.' },
              { n: 3, title: 'Top categories', text: 'Ranked list with %. Daily average = total ÷ days elapsed (current month) or ÷ days in month (past).' },
            ]}
          >
            <ScreenReports empty={empty} />
          </ScreenCard>

          <Arrow label="back · tap categories" />

          <ScreenCard
            num="11"
            title="Category List"
            label="manage"
            showCallouts={showCallouts}
            pins={[
              { n: 1, top: 100, right: 24 },
              { n: 2, top: 180, left: 20 },
              { n: 3, bottom: 220, left: 20 },
            ]}
            callouts={[
              { n: 1, title: 'Add custom (+)', text: 'Name max 20 chars, icon picker, color picker. Duplicate name blocked inline.' },
              { n: 2, title: 'Defaults locked', text: 'Six built-ins. Always first, not editable, not deletable, not reorderable.' },
              { n: 3, title: 'Custom · drag-to-reorder', text: 'Drag handle ≡. Order here mirrors chip order in Add Expense. Deleting a category re-assigns its expenses to "Uncategorized" (system).' },
            ]}
          >
            <ScreenCategoryList />
          </ScreenCard>
        </FlowRow>

        {/* ───────────────── FLOW 4: First Launch ───────────────── */}
        <FlowRow
          num="04"
          title={<span>First <em style={{ color: W_ACCENT, fontStyle: 'italic' }}>launch</em></span>}
          blurb="Splash → 5 onboarding slides → Home. Shown once, on first install. Skippable from every slide."
        >
          <ScreenCard
            num="01"
            title="Splash"
            label="~1.5s"
            showCallouts={showCallouts}
            pins={[{ n: 1, top: 260, left: '50%' }]}
            callouts={[
              { n: 1, title: 'Auto-dismiss', text: '~1.5–2s, no interaction. Routes to Onboarding (first launch), PIN Entry (PIN on), or Home.' },
              { n: '✱', title: 'No tagline', text: 'Logo + app name only. No login/signup CTA — there are no accounts.' },
            ]}
          >
            <ScreenSplash />
          </ScreenCard>

          <Arrow label="first launch" />

          <ScreenCard
            num="02 · 1/5"
            title="Onboarding"
            label="Welcome"
            showCallouts={showCallouts}
            pins={[{ n: 1, top: 50, right: 24 }, { n: 2, bottom: 90, left: '50%' }]}
            callouts={[
              { n: 1, title: 'Skip · always visible', text: 'Tap Skip on any slide → jumps straight to Home.' },
              { n: 2, title: 'Pagination dots', text: 'Swipeable. 5 slides total. No use-case selection — features discovered naturally.' },
            ]}
          >
            <ScreenOnboarding slide={1} />
          </ScreenCard>

          <Arrow label="swipe" />

          <ScreenCard num="02 · 2/5" title="Onboarding" label="Quick Log" showCallouts={showCallouts} pins={[]} callouts={[
            { n: '·', title: 'Slide 2', text: '"Log expenses in seconds" — sets expectation for one-tap entry.' },
          ]}>
            <ScreenOnboarding slide={2} />
          </ScreenCard>

          <Arrow label="swipe" />

          <ScreenCard num="02 · 3/5" title="Onboarding" label="Shared Costs" showCallouts={showCallouts} pins={[]} callouts={[
            { n: '·', title: 'Slide 3', text: '"Split bills instantly" — pitches Shared Costs early as a wow-moment use case.' },
          ]}>
            <ScreenOnboarding slide={3} />
          </ScreenCard>

          <Arrow label="swipe" />

          <ScreenCard num="02 · 4/5" title="Onboarding" label="Event Budget" showCallouts={showCallouts} pins={[]} callouts={[
            { n: '·', title: 'Slide 4', text: '"Plan and track any event budget" — trips, weddings, parties.' },
          ]}>
            <ScreenOnboarding slide={4} />
          </ScreenCard>

          <Arrow label="swipe" />

          <ScreenCard
            num="02 · 5/5"
            title="Onboarding"
            label="Journal"
            showCallouts={showCallouts}
            pins={[{ n: 1, bottom: 60, left: '50%' }]}
            callouts={[
              { n: '·', title: 'Slide 5', text: '"Review your spending like a diary" — closes on Journal, the daily-return surface.' },
              { n: 1, title: 'Get started CTA', text: 'Only appears on the last slide. Replaces the Back / Next row. Tap → Home.' },
            ]}
          >
            <ScreenOnboarding slide={5} />
          </ScreenCard>
        </FlowRow>

        {/* ───────────────── FLOW 5: PIN Auth ───────────────── */}
        <FlowRow
          num="05"
          title={<span>PIN <em style={{ color: W_ACCENT, fontStyle: 'italic' }}>auth</em></span>}
          blurb="Optional 6-digit PIN — fully local, no account. Setup happens in Settings. Entry runs on every launch when enabled."
        >
          <ScreenCard
            num="13"
            title="More"
            label="entry point"
            showCallouts={showCallouts}
            pins={[{ n: 1, top: 290, left: 20 }]}
            callouts={[
              { n: 1, title: 'Settings → PIN', text: 'PIN setup is buried in Settings — opt-in, not forced. Disabled by default.' },
            ]}
          >
            <ScreenMore />
          </ScreenCard>

          <Arrow label="tap PIN auth" />

          <ScreenCard
            num="14"
            title="PIN Setup"
            label="enable"
            showCallouts={showCallouts}
            pins={[
              { n: 1, top: 130, right: 24 },
              { n: 2, top: 220, right: 24 },
              { n: 3, top: 370, left: 20 },
              { n: 4, bottom: 60, right: 20 },
            ]}
            callouts={[
              { n: 1, title: 'Enable toggle', text: 'Flip on → prompts 6-digit PIN, then confirm. Mismatch shows "PINs do not match" inline.' },
              { n: 2, title: 'Biometric', text: 'Greyed until PIN is set. Tapping while disabled shows: "Please enable PIN first to use biometric authentication."' },
              { n: 3, title: 'Recovery required', text: 'Security question is mandatory. Cannot enable PIN without setting Q + A.' },
              { n: 4, title: 'Save', text: 'Success message: "PIN is now active. You\'ll be asked to enter it on your next launch."' },
            ]}
          >
            <ScreenPinSetup />
          </ScreenCard>

          <Arrow label="next launch" />

          <ScreenCard
            num="01"
            title="Splash"
            label="returning · PIN on"
            showCallouts={showCallouts}
            pins={[]}
            callouts={[
              { n: '·', title: 'Splash routes to PIN Entry', text: 'When PIN is enabled, splash auto-navigates to PIN Entry instead of Home.' },
            ]}
          >
            <ScreenSplash />
          </ScreenCard>

          <Arrow label="auto" />

          <ScreenCard
            num="15"
            title="PIN Entry"
            label="lock screen"
            showCallouts={showCallouts}
            pins={[
              { n: 1, top: 160, left: '50%' },
              { n: 2, top: 220, left: '50%' },
              { n: 3, bottom: 38, left: '50%' },
            ]}
            callouts={[
              { n: 1, title: '6 dot indicators', text: 'Fill as digits are entered. No back navigation — user must authenticate.' },
              { n: 2, title: 'Biometric', text: 'Auto-triggered if enabled. Falls back to PIN on failure.' },
              { n: 3, title: 'Forgot PIN', text: 'Opens recovery flow — answer security question, then reset. 5 wrong answers → 30s lockout.' },
              { n: '⚠', title: 'Lockout', text: '5 wrong PINs → "Too many attempts. Try again in 30s" with countdown. Keypad disabled until expiry.' },
            ]}
          >
            <ScreenPinEntry filled={3} />
          </ScreenCard>
        </FlowRow>

        {/* ───────────────── FLOW 6: Event Budget ───────────────── */}
        <FlowRow
          num="06"
          title={<span>Event <em style={{ color: W_ACCENT, fontStyle: 'italic' }}>budget</em></span>}
          blurb="Set a budget for a trip / wedding / party. Log expenses with @tag to link them in real time. Multiple events run in parallel."
        >
          <ScreenCard
            num="03"
            title="Home"
            label="Budget tab"
            showCallouts={showCallouts}
            pins={[{ n: 1, bottom: 38, left: 120 }]}
            callouts={[{ n: 1, title: 'Budget tab', text: 'Second tab in bottom nav. Direct entry point to Event Budget list.' }]}
          >
            <ScreenHome variant={variant} empty={empty} />
          </ScreenCard>

          <Arrow label="budget tab" />

          <ScreenCard
            num="07"
            title="Event Budget"
            label={empty ? 'empty' : 'list'}
            showCallouts={showCallouts}
            pins={[
              { n: 1, top: 80, right: 24 },
              { n: 2, top: 200, left: 24 },
              { n: 3, top: 420, right: 24 },
            ]}
            callouts={[
              { n: 1, title: '+ Create event', text: 'Bottom sheet — name (max 30), date range, total budget. Duplicate names blocked inline.' },
              { n: 2, title: 'Live progress', text: 'Updates immediately as tagged expenses are logged or deleted. Multiple active events allowed.' },
              { n: 3, title: 'Over budget', text: 'Soft red at 110%+, warning text "Over budget by $X". Yellow at 100–110%.' },
            ]}
          >
            <ScreenEventBudget empty={empty} />
          </ScreenCard>

          <Arrow label="tap event" />

          <ScreenCard
            num="08"
            title="Event Detail"
            label="Bali Trip"
            showCallouts={showCallouts}
            pins={[
              { n: 1, top: 75, right: 24 },
              { n: 2, top: 180, left: '50%' },
              { n: 3, top: 360, right: 24 },
            ]}
            callouts={[
              { n: 1, title: '⋯ menu', text: 'Edit event, close event, delete. Closed events stay editable for 24h grace period.' },
              { n: 2, title: 'Budget summary', text: 'Total / spent / remaining. Live progress bar. Event end date is reference only — manual close.' },
              { n: 3, title: '+ Add tagged', text: 'Shortcut → Add Expense pre-tagged to this event.' },
            ]}
          >
            <ScreenEventDetail />
          </ScreenCard>

          <Arrow label="+ add expense" />

          <ScreenCard
            num="04 · b"
            title="Add Expense"
            label="pre-tagged"
            showCallouts={showCallouts}
            pins={[{ n: 1, top: 340, left: 20 }]}
            callouts={[
              { n: 1, title: '@ Bali Trip pre-filled', text: 'Coming from Event Detail, the @ tag arrives pre-selected. User can clear it or pick a different event/debt.' },
            ]}
          >
            <ScreenAddDetails />
          </ScreenCard>
        </FlowRow>

        {/* ───────────────── FLOW 7: Debt Tracker ───────────────── */}
        <FlowRow
          num="07"
          title={<span>Debt &amp; <em style={{ color: W_ACCENT, fontStyle: 'italic' }}>lending</em></span>}
          blurb="Personal record of money lent or owed. No bank linking. Settled records stay visible but greyed out."
        >
          <ScreenCard
            num="13"
            title="More"
            label="hub"
            showCallouts={showCallouts}
            pins={[{ n: 1, top: 150, left: 20 }]}
            callouts={[{ n: 1, title: 'Debt Tracker entry', text: 'Lives under "Features" in More. Joins Reports + Category List + Shared Costs.' }]}
          >
            <ScreenMore />
          </ScreenCard>

          <Arrow label="tap debt" />

          <ScreenCard
            num="09"
            title="Debt Tracker"
            label={empty ? 'empty' : 'I Lent'}
            showCallouts={showCallouts}
            pins={[
              { n: 1, top: 95, left: '50%' },
              { n: 2, top: 200, right: 24 },
              { n: 3, top: 300, left: 20 },
              { n: 4, bottom: 130, left: 20 },
            ]}
            callouts={[
              { n: 1, title: 'I Lent / I Owe toggle', text: 'Two separate lists. Color-coded in hi-fi (Sage / Warm Peach). Add (+) pre-sets type by current view.' },
              { n: 2, title: 'Summary', text: 'Sum of active records in current view. Count shown beside it.' },
              { n: 3, title: 'Active records', text: 'Tap → bottom sheet: Edit / Mark as Settled / Cancel. No reminders in MVP — due date is reference only.' },
              { n: 4, title: 'Settled', text: 'Greyed out, deletable (active records are NOT deletable). Same person on opposite side → soft warning.' },
            ]}
          >
            <ScreenDebtTracker empty={empty} />
          </ScreenCard>
        </FlowRow>

        {/* ───────────────── FLOW 8: Shared Costs ───────────────── */}
        <FlowRow
          num="08"
          title={<span>Shared <em style={{ color: W_ACCENT, fontStyle: 'italic' }}>costs</em></span>}
          blurb="Quick bill splitter — total, count, optional names → per-person share. No group setup. Saves the total bill amount as the entry."
        >
          <ScreenCard
            num="13"
            title="More"
            label="hub"
            showCallouts={showCallouts}
            pins={[{ n: 1, top: 178, left: 20 }]}
            callouts={[{ n: 1, title: 'Shared Costs entry', text: 'Second Features link under More.' }]}
          >
            <ScreenMore />
          </ScreenCard>

          <Arrow label="tap split" />

          <ScreenCard
            num="10"
            title="Shared Costs"
            label="input"
            showCallouts={showCallouts}
            pins={[
              { n: 1, top: 110, left: '50%' },
              { n: 2, top: 220, right: 24 },
              { n: 3, top: 310, left: '50%' },
              { n: 4, top: 380, right: 24 },
              { n: 5, bottom: 50, right: 20 },
            ]}
            callouts={[
              { n: 1, title: 'Total = source of truth', text: 'No balance check — feature is a note tool, not a payment enforcer. $0 disables Save.' },
              { n: 2, title: 'People stepper', text: 'Min 2, max 20. Buttons disable at limits (no error message).' },
              { n: 3, title: 'Equal / Custom', text: 'Equal divides automatically. Custom = manually adjustable amounts. Saving always stores the original total.' },
              { n: 4, title: 'Per-person', text: 'Updates live as people count or custom amounts change. Names default to "Person N" if omitted.' },
              { n: 5, title: 'Save', text: 'Stores the bill in Shared Costs history only — does NOT show up in Journal. History is reference-only (no edit, swipe to delete).' },
            ]}
          >
            <ScreenSharedCosts />
          </ScreenCard>
        </FlowRow>

        {/* FOOTER */}
        <footer style={{ marginTop: 96, paddingTop: 24, borderTop: `1px solid ${W_LINE}`, display: 'grid', gridTemplateColumns: '1fr 1fr 1fr', gap: 32, color: W_MUTED, fontSize: 12 }}>
          <div>
            <div style={{ fontFamily: 'var(--mono)', fontSize: 10, letterSpacing: '0.12em', textTransform: 'uppercase', color: W_INK }}>Scope</div>
            <div style={{ marginTop: 8 }}>All 15 screens across 8 flows — Quick Log, Journal, More, First Launch, PIN Auth, Event Budget, Debt, Shared Costs.</div>
          </div>
          <div>
            <div style={{ fontFamily: 'var(--mono)', fontSize: 10, letterSpacing: '0.12em', textTransform: 'uppercase', color: W_INK }}>Next pass</div>
            <div style={{ marginTop: 8 }}>Hi-fi mocks with the committed clay / sage palette. Bottom-sheet variants (Create Event, Add Record, Action sheet). PIN recovery flow.</div>
          </div>
          <div>
            <div style={{ fontFamily: 'var(--mono)', fontSize: 10, letterSpacing: '0.12em', textTransform: 'uppercase', color: W_INK }}>Open questions</div>
            <div style={{ marginTop: 8 }}>Does Recent list show absolute date or relative ("Today")? Does Home header animate when user crosses budget threshold? Voice input deferred to post-MVP — confirm.</div>
          </div>
        </footer>
      </div>
    </>
  );
}

function LegendItem({ swatch, label }) {
  return (
    <div style={{ display: 'flex', alignItems: 'center', gap: 10 }}>
      {swatch}
      <span style={{ fontSize: 11, color: W_INK, fontFamily: 'var(--mono)', letterSpacing: '0.04em' }}>{label}</span>
    </div>
  );
}

ReactDOM.createRoot(document.getElementById('root')).render(<App />);
