# Shared Costs — User Stories

> Service: `feature:sharedcost` · Screen: 10 Shared Costs
> PRD use case: Shared Costs (🔴 MVP). Legend & format: [`../README.md`](../README.md).

### US-SHC-1 — Split a bill equally · 🔴 Must
> **As** Aiko 👫, **I want** to split a total equally among people, **so that** everyone's share is clear.

- AC1: Enter total (large), set people count via stepper (min 2, max 20), optionally name people (default "Person 1…").
- AC2: Equal split is the default; the summary sub-screen shows per-person amounts.
- AC3: The keypad stays available while typing a multi-digit total (regression guard — keypad must not disappear after one digit).

### US-SHC-2 — Split a bill unequally · 🟡 Should
> **As** Aiko 👫, **I want** a custom split, **so that** I can reflect uneven shares.

- AC1: Custom mode lets each share be edited live (including $0).
- AC2: `Back` from the summary persists all values.

### US-SHC-3 — Stay within sane participant limits · 🟡 Should
> **As** any user, **I want** sensible min/max on people, **so that** the split stays usable.

- AC1: At count = 20 the `+` button is disabled and greyed (no error); at min 2 the `−` is disabled.
- AC2: Total $0 → `Save` disabled + "Total amount must be greater than $0."

### US-SHC-4 — Keep shared costs out of my personal journal · 🔴 Must
> **As** Aiko 👫, **I want** the saved total recorded as one expense (splits as reference), **so that** my journal isn't polluted by per-person rows.

- AC1: `Save` always stores the original **total** as the expense; splits are reference only (total is the source of truth).
- AC2: Saved splits appear in Shared Costs **history only** — not in Journal.

### US-SHC-5 — Review and remove past splits · 🟡 Should
> **As** Aiko 👫, **I want** a history of past splits I can delete, **so that** I can tidy old records.

- AC1: History rows tap to view the full split; swipe-left deletes (with confirm).
- AC2: Editing is not supported (reference only).
