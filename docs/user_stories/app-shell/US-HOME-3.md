# User Story

> **ID:** US-HOME-3 · **Service:** `app` (Home) · **Screen:** 03 Home
> **Priority:** 🔴 Must · **Status:** ✅ Implemented · **Persona:** any fresh user

## Title

> Get started when empty

---

## User Story

**As** a fresh user
**I want to** see a clear first action on an empty Home
**So that** I know exactly what to do next instead of facing a blank screen

---

## Description

### Background

A brand-new install has no expenses, no budget, no events — every Home variant from
[US-HOME-1](US-HOME-1.md) would otherwise render as zeros or blanks. A dedicated empty state with
one obvious CTA turns that into a clear invitation to log the first expense.

### Scope

**In Scope**

* Empty-state illustration + message + single CTA when there are zero expenses.

**Out of Scope**

* The populated Home states — covered by [US-HOME-1](US-HOME-1.md), [US-HOME-2](US-HOME-2.md).

---

## Acceptance Criteria

### Scenario 1 — Empty state on Home

**Given**

* I have no expenses.

**When**

* I view Home.

**Then**

* An illustration, "No expenses yet…", and a single CTA "Log your first expense" are shown.

---

## Functional Requirements

* [ ] Home renders the empty state only when zero expenses exist.
* [ ] The empty state's single CTA navigates directly into the add-expense flow.

---

## Non-Functional Requirements

* [ ] **Usability** — the empty state offers exactly one action, avoiding decision paralysis for a
  first-time user.

---

## Business Rules

* Empty state is shown purely based on expense count being zero, independent of other data
  (events, debts, etc.).

---

## UI / UX Notes

* **Design / Mockup:** [`03-home.md`](../../../design-system-spec/screens/03-home.md) → empty state.
* **Success Messages:** "No expenses yet…", CTA "Log your first expense".

---

## Dependencies

* **Story/Task:** [US-HOME-1](US-HOME-1.md), [US-LOG-1](../logging/US-LOG-1.md) (the CTA's destination).

---

## Definition of Done

* [ ] Acceptance criteria met
* [ ] Code reviewed
* [ ] Unit tests completed
* [ ] Screenshot (Roborazzi) test for the empty state passes
* [ ] Documentation updated
* [ ] Product Owner accepted

---

## Notes

None.
