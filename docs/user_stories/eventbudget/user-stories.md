# Event Budget — User Stories

> Service: `feature:eventbudget` · Screens: 07 Event Budget · 08 Event Detail
> PRD use case: Event Budget (🔵 Phase 2). Legend & format: [`../README.md`](../README.md).

### US-EVT-1 — Create an event budget · 🔵 Phase 2
> **As** James 🎉, **I want** to create an event with a budget and date range, **so that** I can track spend for a trip or party.

- AC1: Create sheet fields: name (req, ≤ 30), start date (today default, past allowed), end date (≥ start), total budget (req, > $0).
- AC2: `Save` is disabled until budget > $0 ("Budget must be greater than $0").
- AC3: Duplicate names are blocked inline ("An event with this name already exists."); name counter caps at 30.
- AC4: Empty state shows a jar illustration, "No active events…", and a single `Create event` CTA.

### US-EVT-2 — Track multiple events at once · 🔵 Phase 2
> **As** James 🎉, **I want** several events active simultaneously, **so that** I can run e.g. a trip and a wedding in parallel.

- AC1: Multiple events can be active at once.
- AC2: Each card shows name, date range, live remaining balance, and a mini progress bar.
- AC3: With overlapping active events, the Home header shows the most recently created one.

### US-EVT-3 — See when I'm over budget · 🔵 Phase 2
> **As** Carlos ✈️, **I want** clear over-budget signals, **so that** I can rein in spending.

- AC1: Progress color system: 0–100% soft blue (on track); 101–110% amber + "Over budget by $X (Y%)"; >110% soft red + bold warning.
- AC2: Over-budget cards show the bar in red and an "Over budget" chip; remaining flips negative.

### US-EVT-4 — Drill into an event · 🔵 Phase 2
> **As** James 🎉, **I want** an event detail with all linked expenses, **so that** I can see where the money went.

- AC1: Header shows name, date range, budget summary (total / spent / remaining), and progress bar.
- AC2: A linked expense list shows all `@`-tagged entries, plus an Add-expense shortcut that pre-tags this event.
- AC3: Deleting a linked expense from Journal recalculates remaining immediately.

### US-EVT-5 — Edit and close an event · 🔵 Phase 2
> **As** James 🎉, **I want** to edit and manually close an event, **so that** I control its lifecycle.

- AC1: Edit sheet allows name, dates, and budget (budget must stay > $0).
- AC2: End date passing does **not** auto-close — close is manual; end date is reference only.
- AC3: Lifecycle: Active (fully editable/linkable) → Closed < 24h (archived but editable, grace period) → Closed > 24h (read-only: fields locked, no edits, no new links).
