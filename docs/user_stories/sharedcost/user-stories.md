# Shared Costs — User Stories

> Service: `feature:sharedcost` · Screen: 10 Shared Costs
> PRD use case: Shared Costs (🔴 MVP).
> Legend & format: [`../README.md`](../README.md) · [`../TEMPLATE.md`](../TEMPLATE.md).

### US-SHC-1 — Split a bill equally · 🔴 Must
> **As** Aiko 👫, **I want** to split a total equally among people, **so that** everyone's share is clear.

- **AC1** — **Given** I am on Shared Costs, **when** I enter values, **then** I can type the total (large) and set people count via a stepper (min 2, max 20), optionally naming people (default "Person 1…").
- **AC2** — **Given** Equal split (default), **when** I open the summary sub-screen, **then** per-person amounts are shown.
- **AC3** — **Given** I am entering the total, **when** I type more than one digit, **then** the keypad stays available throughout (regression guard — must not disappear after one digit).

### US-SHC-2 — Split a bill unequally · 🟡 Should
> **As** Aiko 👫, **I want** a custom split, **so that** I can reflect uneven shares.

- **AC1** — **Given** Custom mode, **when** I adjust a share, **then** each share is editable live (including $0).
- **AC2** — **Given** I am on the summary, **when** I tap `Back`, **then** all values persist.

### US-SHC-3 — Stay within sane participant limits · 🟡 Should
> **As** any user, **I want** sensible min/max on people, **so that** the split stays usable.

- **AC1** — **Given** the people count is at 20, **when** I view the stepper, **then** `+` is disabled and greyed (no error); at min 2, `−` is disabled.
- **AC2** — **Given** the total is $0, **when** I view the screen, **then** `Save` is disabled and "Total amount must be greater than $0." is shown.

### US-SHC-4 — Keep shared costs out of my personal journal · 🔴 Must
> **As** Aiko 👫, **I want** the saved total recorded as one expense (splits as reference), **so that** my journal isn't polluted by per-person rows.

- **AC1** — **Given** a split, **when** I `Save`, **then** the original total is stored as the expense and splits are reference only (total is the source of truth).
- **AC2** — **Given** a saved split, **when** I browse the app, **then** it appears in Shared Costs history only — not in Journal.

### US-SHC-5 — Review and remove past splits · 🟡 Should
> **As** Aiko 👫, **I want** a history of past splits I can delete, **so that** I can tidy old records.

- **AC1** — **Given** Shared Costs history, **when** I tap a row, **then** I view the full split; **when** I swipe-left, **then** I can delete (with confirm).
- **AC2** — **Given** a saved split, **when** I try to change it, **then** editing is not supported (reference only).
