# Event Budget — User Stories

> Service: `feature:eventbudget` · Screens: 07 Event Budget · 08 Event Detail
> PRD use case: Event Budget (🔵 Phase 2).
> Legend & format: [`../README.md`](../README.md) · [`../TEMPLATE.md`](../TEMPLATE.md).

### US-EVT-1 — Create an event budget · 🔵 Phase 2
> **As** James 🎉, **I want** to create an event with a budget and date range, **so that** I can track spend for a trip or party.

- **AC1** — **Given** I open the Create sheet, **when** I fill it in, **then** fields are name (req, ≤ 30), start date (today default, past allowed), end date (≥ start), total budget (req, > $0).
- **AC2** — **Given** the budget is $0, **when** I view the sheet, **then** `Save` is disabled and "Budget must be greater than $0" is shown.
- **AC3** — **Given** I enter an existing event name, **when** validation runs, **then** it is blocked inline ("An event with this name already exists.") and the name counter caps at 30.
- **AC4** — **Given** I have no events, **when** I open the screen, **then** an empty state shows a jar illustration, "No active events…", and a single `Create event` CTA.

### US-EVT-2 — Track multiple events at once · 🔵 Phase 2
> **As** James 🎉, **I want** several events active simultaneously, **so that** I can run e.g. a trip and a wedding in parallel.

- **AC1** — **Given** I create more than one event, **when** they are active, **then** multiple events can run at once.
- **AC2** — **Given** an active event, **when** I view its card, **then** it shows name, date range, live remaining balance, and a mini progress bar.
- **AC3** — **Given** overlapping active events, **when** I view Home, **then** the header shows the most recently created one.

### US-EVT-3 — See when I'm over budget · 🔵 Phase 2
> **As** Carlos ✈️, **I want** clear over-budget signals, **so that** I can rein in spending.

- **AC1** — **Given** spend against budget, **when** the progress bar renders, **then** 0–100% is soft blue, 101–110% amber + "Over budget by $X (Y%)", and >110% soft red + bold warning.
- **AC2** — **Given** an over-budget event, **when** I view its card, **then** the bar is red with an "Over budget" chip and remaining flips negative.

### US-EVT-4 — Drill into an event · 🔵 Phase 2
> **As** James 🎉, **I want** an event detail with all linked expenses, **so that** I can see where the money went.

- **AC1** — **Given** I open Event Detail, **when** the header renders, **then** it shows name, date range, budget summary (total / spent / remaining), and a progress bar.
- **AC2** — **Given** I am on Event Detail, **when** I view the body, **then** all `@`-tagged entries are listed plus an Add-expense shortcut that pre-tags this event.
- **AC3** — **Given** a linked expense is deleted from Journal, **when** I view the event, **then** remaining recalculates immediately.

### US-EVT-5 — Edit and close an event · 🔵 Phase 2
> **As** James 🎉, **I want** to edit and manually close an event, **so that** I control its lifecycle.

- **AC1** — **Given** I open the Edit sheet, **when** I change fields, **then** name, dates, and budget are editable (budget must stay > $0).
- **AC2** — **Given** the end date passes, **when** time elapses, **then** the event does not auto-close — closing is manual and the end date is reference only.
- **AC3** — **Given** an event's state, **when** I act on it, **then** Active is fully editable/linkable, Closed < 24h is archived but editable (grace period), and Closed > 24h is read-only (fields locked, no edits, no new links).
