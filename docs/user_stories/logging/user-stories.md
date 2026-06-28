# Quick Manual Logging — User Stories

> Service: `feature:logging` · Screen: 04 Add Expense
> PRD use case: Quick Manual Logging (🔴 core differentiator — "log in under 5s").
> Legend & format: [`../README.md`](../README.md) · [`../TEMPLATE.md`](../TEMPLATE.md).

### US-LOG-1 — Log an expense in seconds · 🔴 Must
> **As** Amara 🛒, **I want** to log an amount and save in one move, **so that** recording cash flow takes seconds.

- **AC1** — **Given** I am on Home, **when** I tap `+`, **then** Add Expense opens with the numeric keypad already open and "Food" pre-selected.
- **AC2** — **Given** a valid amount is entered, **when** I tap `Save` (quick-commit), **then** the entry is stored with the default category, I slide back to Home, and a success toast fires.
- **AC3** — **Given** the Amount screen is open, **when** I view it, **then** the amount is large and centered and entry requires no scrolling.

### US-LOG-2 — Block an empty amount · 🔴 Must
> **As** any user, **I want** the app to stop me saving a $0 entry, **so that** I don't create meaningless records.

- **AC1** — **Given** the amount is $0 or empty, **when** I view the Amount screen, **then** `Save` and `Next` are disabled (`canProceed = value > 0`).
- **AC2** — **Given** the amount is $0, **when** I tap a disabled `Save`/`Next`, **then** the field shakes (±4dp) and shows "Amount must be greater than $0".
- **AC3** — **Given** I am typing an amount, **when** I enter digits, **then** the whole part is capped at 7 digits, the fraction at 2, a single decimal is allowed, leading zeros are stripped (except "0."), and commas group live.

### US-LOG-3 — Add context to an expense · 🔴 Must
> **As** Siti 🏠, **I want** to set category, date, and a note, **so that** my records are meaningful later.

- **AC1** — **Given** a valid amount, **when** I tap `Next`, **then** Details opens with the amount read-only at top (tap to return and edit; value persists).
- **AC2** — **Given** I am on Details, **when** I save, **then** a category is required and a note is optional (≤ 200 chars).
- **AC3** — **Given** I am on Details, **when** I view the date field, **then** it defaults to today and opens a date/time picker allowing past and future dates.
- **AC4** — **Given** Details are valid, **when** I save, **then** I return to Home with a success toast.

### US-LOG-4 — Backdate an expense · 🟡 Should
> **As** Maya 🎓, **I want** to set a past date on an expense I forgot to log, **so that** my history is accurate.

- **AC1** — **Given** I am on Details, **when** I tap the date field, **then** a date + time picker opens pre-filled with the entry's current timestamp.
- **AC2** — **Given** I pick a past date, **when** I save, **then** the entry stores that timestamp and groups under that day in Journal/Home (not today).

### US-LOG-5 — Link an expense to an event or debt · 🔵 Phase 2
> **As** Carlos ✈️, **I want** to tag an expense to an active event or debt, **so that** balances update automatically.

- **AC1** — **Given** there are no active events or debts, **when** I view Details, **then** the `@` tag field is hidden; otherwise it is shown and optional.
- **AC2** — **Given** I pick an Event tag, **when** it is applied, **then** the Debts group is greyed out and disabled (and vice-versa) — only one link allowed.
- **AC3** — **Given** a tag is selected, **when** I tap `Clear`, **then** both groups reset.

### US-LOG-6 — Avoid runaway notes · 🟡 Should
> **As** any user, **I want** a clear limit on note length, **so that** the field stays manageable.

- **AC1** — **Given** I am typing a note, **when** I reach 200 chars, **then** the counter turns to the error color.
- **AC2** — **Given** the note is at 200 chars, **when** I type more, **then** further input is ignored.

### US-LOG-7 — Never lose a half-typed entry · 🟡 Should
> **As** any user, **I want** my in-progress entry restored after a crash, **so that** I don't retype it.

- **AC1** — **Given** I am mid-entry, **when** the app force-closes, **then** the draft is auto-saved.
- **AC2** — **Given** a saved draft exists, **when** I relaunch before PIN, **then** a `Continue / Discard` prompt is shown (no auth required).

**Notes / edge cases**
- Back from Amount with no value navigates away silently — no save, no prompt.
