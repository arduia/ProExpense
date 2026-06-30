# User Story

> **ID:** US-MORE-2 · **Service:** `app` (More) · **Screen:** 13 More
> **Priority:** 🟡 Should · **Status:** ✅ Implemented · **Persona:** 🏠 Siti (Homemaker)

## Title

> Set a monthly budget

---

## User Story

**As** Siti 🏠
**I want to** set a monthly budget
**So that** Home shows me how I'm tracking against it, not just raw spend

---

## Description

### Background

Raw "total spent" is useful, but a budget-conscious user wants to know how much room is left.
Setting a monthly figure in More switches Home into its Budget-Planner header
([US-HOME-1](../app-shell/US-HOME-1.md)) and the figure must reset cleanly at the start of each
month rather than accumulating across months.

### Scope

**In Scope**

* Monthly budget entry in More.
* Monthly reset on the 1st.

**Out of Scope**

* The Budget-Planner header rendering itself — covered by [US-HOME-1](US-HOME-1.md).

---

## Acceptance Criteria

### Scenario 1 — Budget drives the header

**Given**

* I set a monthly budget.

**When**

* The month progresses.

**Then**

* It drives the Budget-Planner Home header (spent vs. budget with progress) and resets at the
  start of each new month.

---

## Functional Requirements

* [ ] More exposes a monthly budget entry that accepts a positive amount.
* [ ] Setting a budget switches the Home header into Budget-Planner mode.
* [ ] Spend-vs-budget progress recalculates from zero at the start of each calendar month, while
  the budget figure itself persists unchanged across months.

---

## Non-Functional Requirements

* [ ] **Reliability** — the monthly reset happens automatically without requiring the user to
  re-enter or re-confirm the budget each month.

---

## Business Rules

* Monthly budget persists across months; only the spend-tracking progress resets on the 1st.

---

## UI / UX Notes

* **Design / Mockup:** [`13-more.md`](../../../design-system-spec/screens/13-more.md) → Monthly budget.

---

## Dependencies

* **Story/Task:** [US-HOME-1](US-HOME-1.md), [US-MORE-1](US-MORE-1.md).

---

## Definition of Done

* [ ] Acceptance criteria met
* [ ] Code reviewed
* [ ] Unit tests completed
* [ ] Documentation updated
* [ ] Product Owner accepted

---

## Notes

None.
