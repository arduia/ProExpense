// Pro Expense — Android screen specification data
// Consumed by "Android Screen Spec.html". One entry per PRD screen; each pairs
// the captured 414×868 dp artboard(s) with behaviors, component composition
// (+ M3 mapping), applied tokens, and edge/error states.
// Screenshot paths are relative to design-system-spec/.

const SHOT = (id) => `screenshots/screens/${id}.png`;

window.SCREEN_SPECS = [
  // ─────────────────────────────────────────────────────────────── 01 SPLASH
  {
    num: "01", id: "splash", title: "Splash", flow: "First launch",
    purpose: "Brief launch screen. Routes to the right destination, then gets out of the way.",
    shots: [{ src: SHOT("splash"), label: "Splash" }],
    behaviors: [
      "Displays ~1.5–2s, no user interaction.",
      "Routes on dismiss: first launch → Onboarding; returning + PIN on → PIN Entry; returning + PIN off → Home.",
      "If an unfinished Add-Expense draft exists, the restore prompt is shown before PIN Entry (see Add Expense → Draft restore).",
      "No tagline, no auth prompt. Logo + wordmark only.",
    ],
    relations: [
      { c: "App logo", role: "Centered product mark on paper", m3: "Image / painterResource" },
      { c: "Wordmark", role: "“Pro Expense” under the mark", m3: "Text · displaySmall-ish" },
    ],
    tokens: {
      type: ["Wordmark — Inter 30sp / -0.015em (Hero greeting role)"],
      color: ["background = paper #F5F5F5", "logo fill = primary #039BE5", "ink #212121"],
      dims: ["Logo tile radius 14dp", "vertical stack gap 18dp"],
    },
    edges: [],
  },

  // ──────────────────────────────────────────────────────────── 02 ONBOARDING
  {
    num: "02", id: "onboarding", title: "Onboarding", flow: "First launch",
    purpose: "Five swipeable slides introducing MVP features. First launch only.",
    shots: [
      { src: SHOT("onb-1"), label: "1 · Welcome" },
      { src: SHOT("onb-2"), label: "2 · Quick Log" },
      { src: SHOT("onb-3"), label: "3 · Shared Costs" },
      { src: SHOT("onb-4"), label: "4 · Event Budget" },
      { src: SHOT("onb-5"), label: "5 · Journal" },
    ],
    behaviors: [
      "Horizontally swipeable; the page-dot indicator tracks position (active dot widens to 22dp).",
      "Skip (top-right) is present on every slide except the last; it jumps straight to Profile Setup → Home.",
      "“Get started” CTA is bottom-anchored and present on EVERY slide — an eager user can start from anywhere.",
      "Back appears from slide 2 onward; Next is hidden on the last slide.",
      "No use-case selection — features are discovered, not chosen.",
    ],
    relations: [
      { c: "Soft-illustrated hero", role: "One pastel illustration per slide", m3: "Image" },
      { c: "Page indicator", role: "5 dots, active widens", m3: "custom Row + animateDpAsState" },
      { c: "Button (primary, lg, fullWidth)", role: "“Get started” CTA", m3: "Button" },
      { c: "Text button", role: "Skip / Back / Next", m3: "TextButton" },
    ],
    tokens: {
      type: ["Slide title — Inter 38sp / -0.02em (white-space: nowrap)", "Body — Manrope 15sp / 1.45 ink2", "Nav labels — Manrope 13sp"],
      color: ["active dot = clay #039BE5", "idle dot = rgba(43,31,23,.18)", "illustration palette: blue100 #B3E5FC tints"],
      dims: ["Illustration box 280dp", "CTA = Button lg (radius 14dp)", "nav row marginBottom 48dp clears CTA"],
    },
    edges: [],
  },

  // ──────────────────────────────────────────────────── PROFILE SETUP (first-run)
  {
    num: "02·P", id: "profile-setup", title: "Profile Setup", flow: "First launch · between Onboarding & Home",
    purpose: "Account-free personalization — name + home currency on one merged screen, with a live identity card that fuses both inputs. No login; everything stays on-device.",
    shots: [
      { src: SHOT("prof-merged"), label: "P1 · Profile + currency" },
      { src: SHOT("prof-currency-sheet"), label: "P1 · Currency picker" },
    ],
    behaviors: [
      "Identity card (top) is a live preview: greeting updates from the name field, the emblem + “Tracking in … · CODE” line updates from the selected currency.",
      "Name personalizes the Home greeting (“Hi, Maya”) and CSV exports. Optional; the field is pre-focused and the primary action is always enabled.",
      "Home currency applies to every entry (single-currency MVP). Shown as a 2×2 quick grid of the four most common currencies (USD default, selected).",
      "“More currencies” opens the searchable picker bottom sheet; selecting a row applies and closes in one tap, updating the grid + identity card.",
      "“Start tracking” completes setup and lands on Home.",
    ],
    relations: [
      { c: "Stepper header", role: "“PROFILE · ALL SET IN ONE STEP”", m3: "Text · labelMedium (mono)" },
      { c: "Identity card", role: "Live name + currency preview, gradient surface", m3: "custom Surface (clay→clayDeep gradient)" },
      { c: "Text field", role: "Name input with leading user icon", m3: "OutlinedTextField / BasicTextField" },
      { c: "Selectable tile", role: "Currency quick-pick, check on selected", m3: "custom Surface(onClick) · 2-col grid" },
      { c: "Dashed action", role: "“More currencies” → opens picker", m3: "OutlinedButton (dashed outline)" },
      { c: "Bottom sheet + Search field", role: "Full currency picker", m3: "ModalBottomSheet" },
    ],
    tokens: {
      type: ["Screen title — Inter 28sp", "Identity greeting — Inter 22sp on clay", "Eyebrow — Geist Mono 11sp / 0.10–0.12em upper", "Field text — Manrope 14sp · tile text 14sp"],
      color: ["identity card = clayDeep #0288D1 → clay #039BE5 gradient · onPrimary text/emblem at 16–82% white", "selected tile tint = blue100 #B3E5FC wash + clay border", "check = clay #039BE5"],
      dims: ["Identity card radius 20dp · field & tile radius 14dp", "tile grid gap 10dp", "sheet top corners 22dp"],
    },
    edges: [],
  },

  // ──────────────────────────────────────────────────────────────── 03 HOME
  {
    num: "03", id: "home", title: "Home", flow: "Central hub",
    purpose: "Contextual financial snapshot + fast access to logging. Header adapts to persona.",
    shots: [
      { src: SHOT("home-empty"), label: "Fresh user (empty)" },
      { src: SHOT("home-casual"), label: "Casual spender" },
      { src: SHOT("home-budget"), label: "Budget Planner" },
      { src: SHOT("home-event"), label: "Event Organizer" },
    ],
    behaviors: [
      "Contextual header switches by persona: Casual → total spent this month; Budget Planner → spent vs. budget ($320 / $500 + progress); Event Organizer → active event name + remaining balance.",
      "With multiple overlapping active events, the header shows the most recently created one.",
      "Active Event card appears only when an event is running.",
      "Recent transactions: last 5–10 entries (badge, note, meta, amount). “See all” → Journal.",
      "Empty state (fresh user): illustrated, “No expenses yet…”, single CTA “Log your first expense”.",
      "Quick-access tiles deep-link to Reports / Debt / Split / Events.",
      "Bottom nav (Home active) + raised center Add are always present on top-level screens.",
    ],
    relations: [
      { c: "Context header card", role: "Persona-driven summary", m3: "Card (elevation 0, white)" },
      { c: "Quick-access tile ×4", role: "Feature shortcuts", m3: "custom Surface(onClick)" },
      { c: "Transaction row + day grouping", role: "Recent list", m3: "custom Row in LazyColumn" },
      { c: "Bottom navigation + Add FAB", role: "Top-level nav", m3: "NavigationBar + FAB" },
      { c: "Empty state", role: "Fresh-user illustration + CTA", m3: "Column + Image + Button" },
    ],
    tokens: {
      type: ["Greeting — Inter 30sp, emphasis word italic in clay", "Card amount — Inter 40sp / -0.02em", "Eyebrow — Geist Mono 11sp upper", "Row amount — Inter 18sp"],
      color: ["primary clay #039BE5 (amount $, links, active nav, FAB)", "ink #212121 / ink3 #757575", "card #FFFFFF on paper #F5F5F5", "tile tint blue100 #B3E5FC"],
      dims: ["Card radius 16–18dp, padding 18dp", "tile radius 14dp, icon box 36dp", "FAB 64dp raised -24dp", "row pad 12×8dp"],
    },
    edges: [],
  },

  // ────────────────────────────────────────────────────────── 04 ADD EXPENSE
  {
    num: "04", id: "add-expense", title: "Add Expense", flow: "Quick Log · core",
    purpose: "Log an expense in under 5 seconds. Two sub-screens: Amount, then Details.",
    shots: [
      { src: SHOT("add-amount"), label: "Amount · typed" },
      { src: SHOT("add-zero"), label: "Amount · $0 validation" },
      { src: SHOT("add-details"), label: "Details · with @ tag" },
    ],
    behaviors: [
      "Sub-screen 1 (Amount): keypad auto-opens; amount is large + centered. Category chips scroll horizontally, “Food” pre-selected.",
      "Input rules: whole part ≤ 7 digits, fraction ≤ 2; single decimal; leading zeros stripped (except “0.”); commas grouped live.",
      "canProceed = value > 0. Save (quick-commit) and Next are disabled until amount > $0.",
      "Tapping a disabled action shakes the field (±4dp) and shows “Amount must be greater than $0”.",
      "Save → quick-commits with default category, slides back to Home, fires success toast.",
      "Next → Details. Details amount is read-only at top, tap to return and edit (value persists).",
      "Sub-screen 2 (Details): category required; date defaults to today (future allowed); note optional ≤ 200 chars; @ tag optional.",
      "@ tag field is hidden when there are no active events or debts; otherwise optional. One link only (Event OR Debt).",
      "Back from Amount with no value: silent navigation, no save, no prompt.",
    ],
    relations: [
      { c: "Amount entry (keypad + validation)", role: "Numeric input, serif keys, Save/Next", m3: "custom (no M3 keypad)" },
      { c: "Category chip group", role: "Single-select, horizontal scroll", m3: "FilterChip (shape = CircleShape)" },
      { c: "Bottom sheet — Date & time", role: "Date picker, future notice", m3: "ModalBottomSheet" },
      { c: "Bottom sheet — Tag picker", role: "Events/Debts, mutually exclusive", m3: "ModalBottomSheet" },
      { c: "Toast", role: "“Expense saved” confirmation", m3: "Snackbar" },
      { c: "Button (primary, lg)", role: "Save on Details", m3: "Button" },
    ],
    tokens: {
      type: ["Display amount — Inter 64sp / -0.025em", "Keypad keys — Inter 22sp", "$ glyph ≈0.47× figure in clay", "decimal .00 in ink3", "Read-only amount (Details) — Inter 26sp"],
      color: ["empty figure = muted2 #BDBDBD, $ stays clay", "helper / shake text = clay (error affordance)", "tag = #FB8C00"],
      dims: ["Keypad 3-col grid, gap 8dp, key radius 12dp", "Save/Next radius 14dp", "FAB shadow 0 8 20 rgba(3,155,229,.28)"],
    },
    edges: [
      { src: SHOT("edge-draft"), label: "Draft restore", note: "App force-closed mid-entry → draft auto-saved. On relaunch, before PIN: Continue / Discard (no auth required)." },
      { src: SHOT("edge-note"), label: "Note at 200 cap", note: "Note hard-capped at 200 chars; counter turns to error color at the limit; further input ignored." },
      { src: SHOT("edge-tag"), label: "@ tag mutual exclusion", note: "Picking an Event greys out & disables the Debts group (and vice-versa). Clear resets both." },
    ],
  },

  // ──────────────────────────────────────────────────────────────── 05 JOURNAL
  {
    num: "05", id: "journal", title: "Journal", flow: "Browse history",
    purpose: "Diary-style spending history, grouped by day, always expanded. All-time, no cutoff.",
    shots: [{ src: SHOT("journal-list"), label: "Date-grouped" }],
    behaviors: [
      "Entries grouped by expense date (days always expanded — no collapsing). Within a day, newest-created first.",
      "Each day header shows a serif date + mono daily total.",
      "Search (keyword / note / amount) + category filter chips at the top.",
      "When search is active the list flattens (no day grouping); rows show amount, category, date, note.",
      "Tap an entry → Journal Detail. Long-press an entry → Quick-note bottom sheet (type + save without leaving the list).",
      "Category filter mirrors the category catalogue; “All” default.",
    ],
    relations: [
      { c: "Search field", role: "Keyword/amount search, clearable", m3: "BasicTextField" },
      { c: "Filter chip row", role: "Category filter (All + cats + More)", m3: "FilterChip" },
      { c: "Day group header", role: "Serif date + mono total", m3: "custom Row" },
      { c: "Transaction row", role: "Badge · note · meta · amount · tag", m3: "custom Row in LazyColumn" },
      { c: "Bottom sheet — Quick note", role: "Long-press inline note", m3: "ModalBottomSheet" },
    ],
    tokens: {
      type: ["Screen title — Inter 28sp", "Day header — Inter 18sp / -0.01em", "Daily total — Geist Mono 12sp muted", "Row amount — Inter 18sp", "Meta — Manrope 11.5sp muted"],
      color: ["filter chip active = ink fill / paper-warm text", "event tag = #FB8C00", "divider line2 rgba(33,33,33,.06)"],
      dims: ["Search field radius 14dp", "filter chip radius 99dp", "row pad 12×8dp, badge 38dp"],
    },
    edges: [
      { src: SHOT("edge-search"), label: "No results", note: "Search with no matches → centered illustration “No matches”, echoes the query, suggests a different keyword/amount/note." },
      { src: SHOT("edge-quicknote"), label: "Long-press quick note", note: "Long-press opens a Quick-note sheet pinned to that entry; Save writes the note and dismisses without navigating." },
    ],
  },

  // ─────────────────────────────────────────────────────── 06 JOURNAL DETAIL
  {
    num: "06", id: "journal-detail", title: "Journal Detail", flow: "Browse history",
    purpose: "Full detail of a single entry. Edit / delete handled via a bottom sheet to keep the view clean.",
    shots: [
      { src: SHOT("journal-detail"), label: "Detail · linked to Event" },
      { src: SHOT("journal-actions"), label: "Edit / Delete sheet" },
    ],
    behaviors: [
      "Shows amount (large), category icon + label, date & time, note (tap to edit inline), and any @ tag link.",
      "Action button opens a bottom sheet: Edit · Delete · Cancel.",
      "Edit → opens Add Expense (Details) pre-filled; save returns to Journal.",
      "Delete → confirmation dialog first, then removes and returns to Journal.",
      "When the entry is tagged, the linked Event/Debt is shown and recalculates if the entry changes or is deleted.",
      "Back returns to the Journal list.",
    ],
    relations: [
      { c: "Read-only amount", role: "Prominent header amount", m3: "Text (serif)" },
      { c: "Category badge + label", role: "Type identity", m3: "custom + Text" },
      { c: "Linked-tag card", role: "Event/Debt reference", m3: "Card" },
      { c: "Bottom sheet (actions)", role: "Edit / Delete / Cancel", m3: "ModalBottomSheet" },
      { c: "Confirm dialog", role: "Delete guard", m3: "AlertDialog" },
    ],
    tokens: {
      type: ["Detail amount — Inter 26sp / -0.01em", "Header title — Inter 17sp (app-bar)", "Note — Manrope 14sp italic in card", "Labels — Geist Mono 11sp upper"],
      color: ["Delete action = danger #EF5350", "tag card tint = tagTint #FFE0B2", "ink / ink3"],
      dims: ["Sheet top corners 22dp, grab handle 36×4dp", "action buttons gap 10dp weight 1f"],
    },
    edges: [],
  },

  // ──────────────────────────────────────────────────────────── 07 EVENT BUDGET
  {
    num: "07", id: "event-budget", title: "Event Budget", flow: "Events",
    purpose: "Create budgets for specific events and track spending live via @-tagged expenses.",
    shots: [
      { src: SHOT("event-empty"), label: "Empty (fresh)" },
      { src: SHOT("event-create"), label: "Create event · valid" },
      { src: SHOT("event-list"), label: "3 active (1 over)" },
    ],
    behaviors: [
      "Empty state: piggy/jar illustration, “No active events…”, single CTA “Create event”.",
      "Create (bottom sheet): name (req, ≤ 30), start date (today default, past allowed), end date (≥ start), total budget (req, > $0).",
      "Save is disabled until budget > $0; duplicate names are blocked inline.",
      "Multiple events can be active at once (e.g. Bali Trip + John’s Wedding).",
      "Each card: name, date range, live remaining balance, mini progress bar (budget color system).",
      "Over-budget cards show the bar in red + “Over budget” chip.",
      "Tap a card → Event Detail. Expenses link via @ tag in Add Expense; balances update in real time.",
    ],
    relations: [
      { c: "Event card", role: "Name · range · remaining · mini bar", m3: "Card" },
      { c: "Mini progress bar", role: "Spend vs. budget", m3: "LinearProgressIndicator (custom color)" },
      { c: "Bottom sheet — Create event", role: "Name/date/budget form", m3: "ModalBottomSheet" },
      { c: "Date field ×2", role: "Start / end pickers", m3: "DatePicker" },
      { c: "Amount field", role: "Total budget", m3: "OutlinedTextField" },
      { c: "Empty state + Button", role: "Fresh-user CTA", m3: "Column + Button" },
    ],
    tokens: {
      type: ["Screen title — Inter 30sp", "Card amount — Inter 40sp", "Eyebrow — Geist Mono 11sp upper"],
      color: ["on-track bar = soft blue #B3D4E8", "over = warm yellow #F5E6A3 → soft red #E07070", "tag accent #FB8C00"],
      dims: ["Card radius 16dp, padding 18dp", "progress bar h 8dp radius 99dp", "sheet corners 22dp"],
    },
    edges: [
      { src: SHOT("edge-event-errors"), label: "$0 budget + duplicate name", note: "Budget $0 → Save disabled + “Budget must be greater than $0”. Existing name → “An event with this name already exists.” Name capped at 30 chars with counter." },
    ],
  },

  // ──────────────────────────────────────────────────────────── 08 EVENT DETAIL
  {
    num: "08", id: "event-detail", title: "Event Detail", flow: "Events",
    purpose: "Drill-down for one event: budget status + all linked expenses. Edit / close via sheet.",
    shots: [{ src: SHOT("event-detail"), label: "Bali Trip · on track" }],
    behaviors: [
      "Header: event name, date range, budget summary (total / spent / remaining), progress bar.",
      "Linked expense list (all @-tagged entries) + an Add-expense shortcut that pre-tags this event.",
      "Progress color system — 0–100% soft blue (on track); 101–110% warm yellow + “Over budget by $X”; 110%+ soft red + bold warning.",
      "Edit (bottom sheet): name, dates, budget — all editable; budget must stay > $0.",
      "States: Active (fully editable, linkable); Closed < 24h (archived but editable — grace period); Closed > 24h (read-only).",
      "End date passing does NOT auto-close — manual close only; end date is reference.",
      "Deleting a linked expense from Journal recalculates remaining immediately.",
    ],
    relations: [
      { c: "Budget summary card", role: "Total / spent / remaining + bar", m3: "Card + LinearProgressIndicator" },
      { c: "Transaction row", role: "Linked expenses list", m3: "custom Row" },
      { c: "Add-tagged shortcut", role: "Pre-tagged Add Expense", m3: "Button / FAB" },
      { c: "Bottom sheet — Edit / Close", role: "Edit fields, close event", m3: "ModalBottomSheet" },
    ],
    tokens: {
      type: ["Event title — Inter (hero)", "Remaining — Inter 40sp", "Warning text — Manrope 14sp / 600"],
      color: ["bar states #B3D4E8 / #F5E6A3 / #E07070", "warning copy in amber #b26a00 / danger #EF5350"],
      dims: ["bar h 8dp", "card radius 16dp pad 18dp"],
    },
    edges: [
      { src: SHOT("edge-event-warn"), label: "Over budget (105%)", note: "101–110% → amber bar, remaining flips negative, “Over budget by $22 (105%)” notice below the bar." },
      { src: SHOT("edge-event-closed"), label: "Closed (read-only)", note: "After the 24h grace period the event is archived: fields locked, no edit, no new links — read-only." },
    ],
  },

  // ──────────────────────────────────────────────────────────── 09 DEBT TRACKER
  {
    num: "09", id: "debt-tracker", title: "Debt Tracker", flow: "Debt & lending",
    purpose: "Personal record of money lent or owed — no bank connection. I Lent / I Owe.",
    shots: [
      { src: SHOT("debt-lent"), label: "I Lent" },
      { src: SHOT("debt-owe"), label: "I Owe" },
      { src: SHOT("debt-add"), label: "Add record" },
      { src: SHOT("debt-lent-detail"), label: "Detail · Lent" },
      { src: SHOT("debt-owe-detail"), label: "Detail · Owe" },
    ],
    behaviors: [
      "I Lent / I Owe toggle switches the list view; the “+” in the header opens Add Record pre-set to the current side.",
      "Add Record: person (req, ≤ 30), amount (req, > $0), date (today default), optional due date (reference only — no reminders in MVP), optional note (≤ 200), optional @-linked expense.",
      "List: Active records on top (colored by type), Settled below (greyed).",
      "Record actions sheet: Edit & Mark-as-settled for Active; Delete for Settled (with confirm); Cancel always.",
      "Active records are NOT deletable; settle first. Settled records are deletable.",
      "Detail shows person, amount, dates, status, note, and any linked expense reference.",
    ],
    relations: [
      { c: "Segmented toggle", role: "I Lent / I Owe", m3: "SegmentedButton" },
      { c: "Debt summary card", role: "Net lent/owed + count", m3: "Card" },
      { c: "Debt record row", role: "Avatar · name · date · amount", m3: "custom Row" },
      { c: "Bottom sheet — Add / Actions", role: "Create + edit/settle/delete", m3: "ModalBottomSheet" },
      { c: "Confirm dialog", role: "Delete settled guard", m3: "AlertDialog" },
    ],
    tokens: {
      type: ["Person amount — Inter (row/detail)", "Summary amount — Inter 40sp", "Labels — Geist Mono 11sp upper"],
      color: ["I Lent = sage #4CAF50 (success family)", "I Owe = danger #EF5350", "settled rows alpha ~0.5"],
      dims: ["avatar 38–52dp circle", "row pad 12dp", "sheet corners 22dp"],
    },
    edges: [
      { src: SHOT("edge-debt-conflict"), label: "Opposite-side warning", note: "Adding a person who already exists on the other side → soft warning “John already has a record on the other side. Continue?” Yes proceeds, No dismisses." },
      { src: SHOT("edge-debt-settled"), label: "Delete settled", note: "Settled section: Delete shows a confirm dialog before removing. Any linked expense is kept — only the debt link is removed." },
    ],
  },

  // ──────────────────────────────────────────────────────────── 10 SHARED COSTS
  {
    num: "10", id: "shared-costs", title: "Shared Costs", flow: "Bill splitting",
    purpose: "Instant bill splitter — total + people + optional names → per-person share. No group setup.",
    shots: [
      { src: SHOT("shared-input"), label: "Input · equal" },
      { src: SHOT("shared-summary"), label: "Split summary" },
      { src: SHOT("shared-history"), label: "History" },
    ],
    behaviors: [
      "Enter total (large), people count via stepper (min 2, max 20), optional names (default “Person 1…”).",
      "Split mode Equal (default) or Custom (each share manually adjustable, updates live).",
      "Split summary sub-screen shows per-person amounts; Back persists all values.",
      "Save always stores the original TOTAL as the expense (splits are reference only). Total is the source of truth.",
      "Saved splits appear in Shared Costs history only — NOT in Journal.",
      "History: tap to view full split; swipe-left to delete (confirm). Editing not supported (reference only).",
    ],
    relations: [
      { c: "Total amount input", role: "Large prominent figure", m3: "custom + serif Text" },
      { c: "People stepper", role: "−/+ count, min 2 / max 20", m3: "custom Row + IconButtons" },
      { c: "Split-mode toggle", role: "Equal / Custom", m3: "SegmentedButton" },
      { c: "Per-person rows", role: "Live share list", m3: "custom Row" },
      { c: "History list", role: "Past splits", m3: "LazyColumn + SwipeToDismiss" },
      { c: "Button (primary)", role: "Save split", m3: "Button" },
    ],
    tokens: {
      type: ["Total / per-person — Inter 40–64sp", "Eyebrow — Geist Mono 11sp upper", "names — Manrope 14sp"],
      color: ["per-person figure = clay #039BE5", "stepper disabled glyph = muted2 #BDBDBD"],
      dims: ["stepper hit target ≥ 44dp", "row pad 12dp"],
    },
    edges: [
      { src: SHOT("edge-shared-zero"), label: "$0 total", note: "Total $0 → Save disabled + “Total amount must be greater than $0.”" },
      { src: SHOT("edge-shared-limits"), label: "Max 20 + custom", note: "At count = 20 the + button is disabled & greyed (no error). At min 2 the − button is disabled. Custom split lets each share be edited (incl. $0)." },
    ],
  },

  // ──────────────────────────────────────────────────────────── 11 CATEGORY LIST
  {
    num: "11", id: "category-list", title: "Category List", flow: "More · management",
    purpose: "Manage categories. Defaults are locked & first; custom ones are editable, deletable, reorderable.",
    shots: [{ src: SHOT("categories"), label: "Default + custom" }],
    behaviors: [
      "Default categories (Food, Transport, Shopping, Bills, Health, Entertainment) are locked and always first.",
      "Custom categories follow, drag-to-reorder; their order mirrors the chip order in Add Expense.",
      "Add/edit a custom category via icon picker + color picker; name ≤ 20 chars (counter), duplicates blocked.",
      "Edit / Delete via a bottom sheet (custom only). Deleting a category moves its expenses to Uncategorized.",
      "Uncategorized is a system category: not selectable when logging, but shown in Journal & Reports for reference.",
      "Deleting all custom categories leaves defaults visible — no empty state.",
    ],
    relations: [
      { c: "Category badge", role: "Icon + tint per row", m3: "custom Box" },
      { c: "Locked / draggable row", role: "Default vs. custom", m3: "custom Row + ReorderableLazyColumn" },
      { c: "Icon & color picker", role: "Custom category styling", m3: "Grid in sheet" },
      { c: "Bottom sheet — Edit/Delete", role: "Custom row actions", m3: "ModalBottomSheet" },
    ],
    tokens: {
      type: ["Section eyebrow — Geist Mono 11sp upper (“DEFAULT · ALWAYS FIRST”)", "row label — Manrope 14sp"],
      color: ["category accents per catalogue", "“LOCKED” pill in muted", "drag handle muted"],
      dims: ["badge 38dp", "row pad 12dp", "list reorder affordance trailing"],
    },
    edges: [
      { src: SHOT("edge-cat-dup"), label: "Duplicate name", note: "Entering an existing name → “A category with this name already exists.” Name capped at 20 chars with counter; Add disabled until valid." },
    ],
  },

  // ──────────────────────────────────────────────────────────────── 12 REPORTS
  {
    num: "12", id: "reports", title: "Reports", flow: "More · insight",
    purpose: "At-a-glance monthly spending. One chart, one period, the key numbers. Read-only in MVP.",
    shots: [{ src: SHOT("reports"), label: "Monthly" }],
    behaviors: [
      "Monthly period only (MVP). Period selector at top.",
      "Total spent (large, prominent) is the headline number.",
      "Donut chart breaks spending down by category; ranked Top-categories list below with name + amount.",
      "Daily average: current month = total ÷ days elapsed; past months = total ÷ days in month.",
      "Uncategorized appears in chart/list only if such expenses exist.",
      "No data (new user) → empty state “No data yet…”. No data this month → auto-switches to last month with data (no empty state).",
    ],
    relations: [
      { c: "Period selector", role: "Month switcher (pill)", m3: "custom Surface / SegmentedButton" },
      { c: "Total card", role: "Headline spent + daily avg", m3: "Card" },
      { c: "Donut chart", role: "Category breakdown", m3: "Canvas (drawArc)" },
      { c: "Ranked category list", role: "Name + % + amount", m3: "custom Row" },
      { c: "Empty state", role: "No-data illustration", m3: "Column" },
    ],
    tokens: {
      type: ["Total spent — Inter (display/40–64sp)", "ranks — Manrope 14sp + Geist Mono figures", "eyebrow mono 11sp"],
      color: ["donut segments use category accents", "uncategorized = muted grey ring"],
      dims: ["donut diameter ~160dp", "rank row pad 12dp"],
    },
    edges: [
      { src: SHOT("edge-reports-unc"), label: "All uncategorized", note: "If every expense is uncategorized, the donut renders one full-circle grey segment + tip: “categorize your expenses for better insights.”" },
    ],
  },

  // ──────────────────────────────────────────────────────── 13 MORE / SETTINGS
  {
    num: "13", id: "more", title: "More / Settings", flow: "Hub",
    purpose: "Combined hub for secondary features + app configuration. All data is local, no account.",
    shots: [
      { src: SHOT("more-hub"), label: "More hub" },
      { src: SHOT("more-currency"), label: "Currency" },
      { src: SHOT("more-export"), label: "Data export" },
      { src: SHOT("more-clear"), label: "Clear data" },
    ],
    behaviors: [
      "Feature links: Debt Tracker, Shared Costs, Reports, Category List.",
      "Settings: PIN auth, Biometric (greyed until PIN on), Currency, Monthly budget (drives Budget-Planner header; resets on the 1st), Default category, Language, Theme (Light/Dark/System), Data export, Clear data, App version.",
      "Currency: single default applied to all entries; selector for common currencies.",
      "Data export: separate CSVs (expenses / events / debts / shared_costs) zipped into one file — nothing uploaded.",
      "Clear data: selective — user picks what to wipe; each option requires a confirmation dialog; irreversible.",
      "Biometric tap while PIN off → “Please enable PIN first to use biometric authentication.”",
    ],
    relations: [
      { c: "Profile header row", role: "Name + “all data local”", m3: "custom Row" },
      { c: "Settings list rows", role: "Links + value/toggle/chevron", m3: "ListItem / custom Row" },
      { c: "Switch", role: "PIN / biometric toggles", m3: "Switch" },
      { c: "Selectable list (Currency)", role: "Single-select w/ check", m3: "custom Surface" },
      { c: "Export / Clear actions", role: "ZIP export, destructive clear", m3: "Button (filled / error)" },
      { c: "Confirm dialog", role: "Clear-data guard", m3: "AlertDialog" },
    ],
    tokens: {
      type: ["Screen title — Inter 30sp", "section eyebrow — Geist Mono 11sp upper", "row label — Manrope 14sp, value — mono/ink3"],
      color: ["Clear-selected = danger #EF5350", "Export = primary clay", "toggle on = clay"],
      dims: ["row pad 12dp", "field/list radius 14dp", "icon 18–20dp"],
    },
    edges: [],
  },

  // ──────────────────────────────────────────────────────────────── 14 PIN SETUP
  {
    num: "14", id: "pin-setup", title: "PIN Setup", flow: "Security",
    purpose: "Enable & configure an optional 6-digit PIN. Disabled by default; security question is mandatory.",
    shots: [
      { src: SHOT("pin-setup"), label: "Enable PIN" },
      { src: SHOT("pin-security"), label: "Security question (required)" },
    ],
    behaviors: [
      "Toggle to enable → enter 6-digit PIN → confirm (must match). Mismatch → “PINs do not match”, confirm field clears, original PIN kept.",
      "Biometric (Face ID / fingerprint) is offered but requires the PIN to be set first.",
      "Security question is REQUIRED — pick from a predefined list + answer; cannot enable PIN without it.",
      "Success: “PIN is now active. You’ll be asked to enter it on your next launch.”",
      "Change PIN: verify current → new → confirm (same mismatch handling).",
      "Disable PIN: enter current PIN to confirm → disabling also turns biometric off.",
      "Recovery options: security question (verify identity) or reset app (clear all data) as last resort.",
    ],
    relations: [
      { c: "Switch rows", role: "PIN / biometric enable", m3: "Switch" },
      { c: "PIN dots + keypad", role: "6-digit entry / confirm", m3: "custom Row + keypad" },
      { c: "Security-question list", role: "Single-select question", m3: "RadioButton list" },
      { c: "Answer field", role: "Recovery answer", m3: "OutlinedTextField" },
      { c: "Button (primary)", role: "Save / Enable PIN", m3: "Button" },
    ],
    tokens: {
      type: ["Title — Inter 22–24sp (white-space: nowrap)", "helper — Manrope 13sp ink3", "error — Manrope 13sp danger"],
      color: ["filled dots = clay; empty = outline", "error copy = danger #EF5350", "PIN icon tint = clay"],
      dims: ["PIN dot 14dp, gap 14dp", "keypad keys radius 12dp"],
    },
    edges: [
      { src: SHOT("edge-pin-mismatch"), label: "PIN mismatch", note: "On confirm mismatch: dots clear, shake (±4dp), “PINs do not match. Try again.” original PIN preserved." },
      { src: SHOT("edge-pin-recovery"), label: "Recovery", note: "Forgot PIN → security question → correct answer sets a new PIN. Wrong answer “Try again”; 5 wrong → 30s lockout, then attempts reset." },
    ],
  },

  // ──────────────────────────────────────────────────────────────── 15 PIN ENTRY
  {
    num: "15", id: "pin-entry", title: "PIN Entry", flow: "Security",
    purpose: "Session auth at every launch when PIN is enabled. No back navigation — must authenticate.",
    shots: [
      { src: SHOT("pin-entry"), label: "4 of 6 entered" },
      { src: SHOT("pin-lock"), label: "Lockout (30s)" },
    ],
    behaviors: [
      "Six dot indicators fill as digits are entered; full-screen numeric keypad.",
      "Correct PIN → Home immediately. Incorrect → dots shake + “Incorrect PIN, try again”.",
      "5 incorrect attempts → app locks 30s with a countdown; keypad disabled until it completes, then attempts reset.",
      "Biometric (if enabled) auto-prompts; success → Home; failure falls back to PIN.",
      "Forgot PIN → recovery flow (verify security question, then reset PIN).",
      "No back navigation from this screen.",
    ],
    relations: [
      { c: "PIN dots", role: "6-digit progress", m3: "custom Row" },
      { c: "Numeric keypad", role: "Full-screen entry", m3: "custom grid" },
      { c: "Biometric prompt", role: "Face/fingerprint", m3: "BiometricPrompt" },
      { c: "Text button", role: "Forgot PIN → recovery", m3: "TextButton" },
      { c: "Lockout countdown", role: "Disabled state timer", m3: "Text + state" },
    ],
    tokens: {
      type: ["Title — Inter 26–28sp (white-space: nowrap)", "countdown — Geist Mono", "helper — Manrope 13sp"],
      color: ["normal = ink/clay", "locked = amber #b26a00 (title + helper)", "error = danger"],
      dims: ["dot 14dp gap 14dp", "keypad keys radius 12dp", "disabled keypad alpha"],
    },
    edges: [
      { src: SHOT("edge-pin-wrong"), label: "Incorrect PIN", note: "Wrong PIN → dots filled in danger outline + shake + “Incorrect PIN, try again”. Counts toward the 5-attempt lockout." },
    ],
  },
];
