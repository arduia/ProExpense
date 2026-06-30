# User Story

> **ID:** US-LOG-5 · **Service:** `feature:logging` · **Screen:** 04 Add Expense (Details)
> **Priority:** 🔵 Phase 2 · **Status:** ✅ Implemented · **Persona:** ✈️ Carlos (Traveler)

## Title

> Link an expense to an event or debt

---

## User Story

**As** Carlos ✈️
**I want to** tag an expense to an active event or debt
**So that** balances update automatically without double entry

---

## Description

### Background

Expenses logged during an active event (a trip) or against a debt (money someone owes) often need
to count toward that event's/debt's balance too. The `@` tag on Details links the single expense
record to one of those without creating a duplicate entry, and the two link types are mutually
exclusive to avoid ambiguous accounting.

### Scope

**In Scope**

* `@` tag field on Details (only visible when an active event or debt exists).
* Mutually exclusive Event vs. Debt linking.
* `Clear` resets both groups.

**Out of Scope**

* Event Budget / Debt Tracker themselves — covered by `eventbudget`/`debt` stories.

---

## Acceptance Criteria

### Scenario 1 — Tag field visibility

**Given**

* There are no active events or debts.

**When**

* I view Details.

**Then**

* The `@` tag field is hidden; otherwise it is shown and optional.

### Scenario 2 — Mutual exclusivity

**Given**

* I pick an Event tag.

**When**

* It is applied.

**Then**

* The Debts group is greyed out and disabled (and vice-versa) — only one link is allowed per expense.

### Scenario 3 — Clearing a tag

**Given**

* A tag is selected.

**When**

* I tap `Clear`.

**Then**

* Both the Event and Debt groups reset to unselected.

---

## Functional Requirements

* [ ] `@` tag field renders only when at least one active event or debt exists.
* [ ] Selecting an Event disables the Debt group; selecting a Debt disables the Event group.
* [ ] `Clear` resets the tag selection entirely.
* [ ] A saved link recalculates the linked event's/debt's balance immediately.

---

## Non-Functional Requirements

* [ ] **Reliability** — link recalculation happens synchronously with save; no stale balances.

---

## Business Rules

* An expense can be linked to at most one Event or one Debt, never both.
* Deleting or editing a linked expense recalculates the linked Event/Debt balance immediately (see [US-HIS-7](../history/US-HIS-7.md)).

---

## UI / UX Notes

* **Design / Mockup:** [`04-add-expense.md`](../../../design-system-spec/screens/04-add-expense.md) → "Details · `@` tag".
* **User Flow:** Details → `@` tag → pick Event or Debt → Save.

---

## Dependencies

* **Story/Task:** [US-LOG-3](US-LOG-3.md) (Details), [US-EVT-4](../eventbudget/US-EVT-4.md) (event drill-in), [US-DEBT-2](../debt/US-DEBT-2.md) (debt record), [US-HIS-7](../history/US-HIS-7.md) (recalculation on delete/edit).

---

## Definition of Done

* [ ] Acceptance criteria met
* [ ] Code reviewed
* [ ] Unit tests completed
* [ ] Screenshot (Roborazzi) test for tag-selected/disabled states passes
* [ ] Documentation updated
* [ ] Product Owner accepted

---

## Notes

This is a Phase 2 story per the PRD roadmap, but the underlying linking screen already exists in
this build, so it's documented here for completeness.
