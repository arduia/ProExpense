# Quick Manual Logging — User Stories

> Service: `feature:logging` · Screen: 04 Add Expense
> PRD use case: Quick Manual Logging (🔴 core differentiator — "log in under 5s").
> Legend & format: [`../README.md`](../README.md).

### US-LOG-1 — Log an expense in seconds · 🔴 Must
> **As** Amara 🛒, **I want** to log an amount and save in one move, **so that** recording cash flow takes seconds.

- AC1: Tapping `+` opens Add Expense with the numeric keypad already open and "Food" pre-selected.
- AC2: `Save` (quick-commit) stores the entry with the default category, slides back to Home, and fires a success toast.
- AC3: The amount is large and centered; entry requires no scrolling.

### US-LOG-2 — Be stopped from saving an empty amount · 🔴 Must
> **As** any user, **I want** the app to block a $0 entry, **so that** I don't create meaningless records.

- AC1: `Save` and `Next` are disabled until amount > $0 (`canProceed = value > 0`).
- AC2: Tapping a disabled action shakes the field (±4dp) and shows "Amount must be greater than $0".
- AC3: Input rules enforced: whole part ≤ 7 digits, fraction ≤ 2, single decimal, leading zeros stripped (except "0."), commas grouped live.

### US-LOG-3 — Add context to an expense · 🔴 Must
> **As** Siti 🏠, **I want** to set category, date, and a note, **so that** my records are meaningful later.

- AC1: `Next` opens Details with the amount read-only at top (tap to return and edit; value persists).
- AC2: Category is required; note is optional (≤ 200 chars).
- AC3: Date defaults to today and is editable via a date/time picker (past **and** future allowed).
- AC4: Saving from Details returns to Home with a success toast.

### US-LOG-4 — Backdate an expense · 🟡 Should
> **As** Maya 🎓, **I want** to set a past date on an expense I forgot to log, **so that** my history is accurate.

- AC1: The date field opens a date + time picker pre-filled with the entry's current timestamp.
- AC2: Choosing a past date stores that timestamp; the entry then groups under that day in Journal/Home, not today.

### US-LOG-5 — Link an expense to an event or debt · 🔵 Phase 2
> **As** Carlos ✈️, **I want** to tag an expense to an active event or debt, **so that** balances update automatically.

- AC1: The `@` tag field is hidden when there are no active events or debts; otherwise it is optional.
- AC2: Only **one** link is allowed (Event **OR** Debt). Picking an Event greys out and disables the Debts group, and vice-versa.
- AC3: `Clear` resets both groups.

### US-LOG-6 — Avoid runaway notes · 🟡 Should
> **As** any user, **I want** a clear limit on note length, **so that** the field stays manageable.

- AC1: Note is hard-capped at 200 chars; the counter turns to the error color at the limit.
- AC2: Input beyond 200 chars is ignored.

### US-LOG-7 — Never lose a half-typed entry · 🟡 Should
> **As** any user, **I want** my in-progress entry restored after a crash, **so that** I don't retype it.

- AC1: If the app force-closes mid-entry, the draft is auto-saved.
- AC2: On relaunch, before PIN, a `Continue / Discard` prompt is shown (no auth required).
- AC3: Back from Amount with no value navigates away silently — no save, no prompt.
