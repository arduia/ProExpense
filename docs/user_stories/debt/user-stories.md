# Debt & Lending Tracker — User Stories

> Service: `feature:debt` · Screen: 09 Debt Tracker
> PRD use case: Debt & Lending (🔵 Phase 2).
> Legend & format: [`../README.md`](../README.md) · [`../TEMPLATE.md`](../TEMPLATE.md).

### US-DEBT-1 — Switch between lent and owed · 🔵 Phase 2
> **As** Maya 🎓, **I want** to toggle "I Lent" / "I Owe", **so that** I can see each side separately.

- **AC1** — **Given** I am on Debt Tracker, **when** I switch the toggle, **then** the list view switches and `+` opens Add Record pre-set to the current side.
- **AC2** — **Given** I have records, **when** I view the list, **then** Active records are on top (colored by type) and Settled records are below (greyed).

### US-DEBT-2 — Record money lent or owed · 🔵 Phase 2
> **As** Raj 💼, **I want** to record a debt with person and amount, **so that** I remember who owes what.

- **AC1** — **Given** I open Add Record, **when** I fill it in, **then** fields are person (req, ≤ 30), amount (req, > $0), date (today default), optional due date (reference only — no reminders in MVP), optional note (≤ 200), optional `@`-linked expense.
- **AC2** — **Given** a saved record, **when** I open its detail, **then** it shows person, amount, dates, status, note, and any linked expense reference.

### US-DEBT-3 — Settle and clean up debts · 🔵 Phase 2
> **As** Maya 🎓, **I want** to mark a debt settled and later delete it, **so that** my list stays current.

- **AC1** — **Given** an Active record, **when** I open its actions, **then** Edit & Mark-as-settled are offered and it is not deletable (settle first).
- **AC2** — **Given** a Settled record, **when** I tap Delete and confirm, **then** it is removed while any linked expense is kept (only the debt link is removed).

### US-DEBT-4 — Be warned about conflicting records · 🔵 Phase 2
> **As** any user, **I want** a warning when a person exists on the opposite side, **so that** I don't double-record.

- **AC1** — **Given** a person already exists on the other side, **when** I add them again, **then** a soft warning shows "John already has a record on the other side. Continue?".
- **AC2** — **Given** the warning is shown, **when** I respond, **then** `Yes` proceeds and `No` dismisses.
