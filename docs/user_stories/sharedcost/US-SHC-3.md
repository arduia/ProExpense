# User Story

> **ID:** US-SHC-3 · **Service:** `feature:sharedcost` · **Screen:** 10 Shared Costs
> **Priority:** 🟡 Should · **Status:** ✅ Implemented · **Persona:** any user

## Title

> Stay within sane participant limits

---

## User Story

**As** any user
**I want to** have sensible min/max bounds on the people count
**So that** the split stays usable and the input never produces nonsense

---

## Description

### Background

A split with 1 person isn't a split, and an unbounded count would break the per-person summary
layout and arithmetic. Min 2 / max 20 keeps the feature usable; a $0 total is blocked the same way
amount entry is blocked everywhere else in the app.

### Scope

**In Scope**

* Stepper min/max enforcement (2–20) with disabled, non-error styling at bounds.
* $0-total validation message.

**Out of Scope**

* The split arithmetic itself — covered by [US-SHC-1](US-SHC-1.md), [US-SHC-2](US-SHC-2.md).

---

## Acceptance Criteria

### Scenario 1 — Stepper bounds

**Given**

* The people count is at 20.

**When**

* I view the stepper.

**Then**

* `+` is disabled and greyed (no error state); at the minimum of 2, `−` is disabled the same way.

### Scenario 2 — Zero total blocked

**Given**

* The total is $0.

**When**

* I view the screen.

**Then**

* `Save` is disabled and "Total amount must be greater than $0." is shown.

---

## Functional Requirements

* [ ] People-count stepper hard-bounds at 2 (min) and 20 (max), disabling the respective button without an error message.
* [ ] `Save` requires total > $0; below that, it's disabled with the documented message.

---

## Non-Functional Requirements

* [ ] **Accessibility** — disabled stepper buttons are visually distinct (greyed) and not focusable as actionable.

---

## Business Rules

* People count: 2 ≤ n ≤ 20.
* Total must be > $0 to save (same rule family as [US-LOG-2](../logging/US-LOG-2.md)).

---

## UI / UX Notes

* **Design / Mockup:** [`10-shared-costs.md`](../../../design-system-spec/screens/10-shared-costs.md) → "Stepper bounds".
* **Error Messages:** "Total amount must be greater than $0."

---

## Dependencies

* **Story/Task:** [US-SHC-1](US-SHC-1.md), [US-LOG-2](../logging/US-LOG-2.md) (same > $0 family of rule).

---

## Definition of Done

* [ ] Acceptance criteria met
* [ ] Code reviewed
* [ ] Unit tests completed
* [ ] Screenshot (Roborazzi) test for the bounded-stepper state passes
* [ ] Documentation updated
* [ ] Product Owner accepted

---

## Notes

None.
