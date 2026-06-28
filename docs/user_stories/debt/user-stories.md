# Debt & Lending Tracker — User Stories

> Service: `feature:debt` · Screen: 09 Debt Tracker
> PRD use case: Debt & Lending (🔵 Phase 2). Legend & format: [`../README.md`](../README.md).

### US-DEBT-1 — Switch between lent and owed · 🔵 Phase 2
> **As** Maya 🎓, **I want** to toggle "I Lent" / "I Owe", **so that** I can see each side separately.

- AC1: The toggle switches the list; `+` opens Add Record pre-set to the current side.
- AC2: Active records are on top (colored by type); Settled records are below (greyed).

### US-DEBT-2 — Record money lent or owed · 🔵 Phase 2
> **As** Raj 💼, **I want** to record a debt with person and amount, **so that** I remember who owes what.

- AC1: Fields: person (req, ≤ 30), amount (req, > $0), date (today default), optional due date (reference only — no reminders in MVP), optional note (≤ 200), optional `@`-linked expense.
- AC2: A detail view shows person, amount, dates, status, note, and any linked expense reference.

### US-DEBT-3 — Settle and clean up debts · 🔵 Phase 2
> **As** Maya 🎓, **I want** to mark a debt settled and later delete it, **so that** my list stays current.

- AC1: Active records offer Edit & Mark-as-settled; they are **not** deletable (settle first).
- AC2: Settled records offer Delete (with a confirm dialog); deleting keeps any linked expense, removing only the debt link.

### US-DEBT-4 — Be warned about conflicting records · 🔵 Phase 2
> **As** any user, **I want** a warning when a person exists on the opposite side, **so that** I don't double-record.

- AC1: Adding a person already on the other side shows a soft warning: "John already has a record on the other side. Continue?"
- AC2: `Yes` proceeds, `No` dismisses.
