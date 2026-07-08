# User Story

> **ID:** US-SHC-1 · **Service:** `feature:sharedcost` · **Screen:** 10 Shared Costs
> **Priority:** 🔴 Must · **Status:** ✅ Implemented · **Persona:** 👫 Aiko (Cost Sharer)

## Title

> Split a bill equally

---

## User Story

**As** Aiko 👫
**I want to** split a total equally among people
**So that** everyone's share is clear without manual division

---

## Description

### Background

Group expenses are common (dinners, trips, rent) and equal split is the default, fastest path: a
large total amount, a people-count stepper, and optional naming, then a per-person breakdown on the
summary sub-screen.

### Scope

**In Scope**

* Total amount entry (large, keypad-driven).
* People-count stepper (min 2, max 20), optional naming (default "Person 1…").
* Equal split summary showing per-person amounts.

**Out of Scope**

* Custom/unequal split — covered by [US-SHC-2](US-SHC-2.md).
* Participant limit edge cases — covered by [US-SHC-3](US-SHC-3.md).

---

## Acceptance Criteria

### Scenario 1 — Entering the total and people

**Given**

* I am on Shared Costs.

**When**

* I enter values.

**Then**

* I can type the total (large) and set people count via a stepper (min 2, max 20), optionally naming people (default "Person 1…").

### Scenario 2 — Equal split summary

**Given**

* Equal split (default).

**When**

* I open the summary sub-screen.

**Then**

* Per-person amounts are shown.

### Scenario 3 — Keypad stays available

**Given**

* I am entering the total.

**When**

* I type more than one digit.

**Then**

* The keypad stays available throughout — it must never disappear after a single digit.

---

## Functional Requirements

* [ ] Total amount entry uses the same large-keypad pattern as Add Expense.
* [ ] People-count stepper ranges 2–20, with optional per-person naming.
* [ ] Default mode is Equal split; summary divides the total evenly across the people count.
* [ ] The keypad remains visible/usable through the full amount-entry phase regardless of digit count typed.

---

## Non-Functional Requirements

* [ ] **Reliability** — equal-split arithmetic never loses cents due to rounding (remainder distributed deterministically).
* [ ] **Accessibility** — stepper buttons and keypad keys meet the 48dp minimum touch target.

---

## Business Rules

* People count bounds: minimum 2, maximum 20.
* Unnamed people default to "Person 1", "Person 2", etc.

---

## UI / UX Notes

* **Design / Mockup:** [`10-shared-costs.md`](../../../design-system-spec/screens/10-shared-costs.md) → "Equal split".
* **Validation Rules:** total > $0 to proceed; people count 2–20.

---

## Dependencies

* **Story/Task:** [US-SHC-2](US-SHC-2.md), [US-SHC-3](US-SHC-3.md), [US-SHC-4](US-SHC-4.md).

---

## Definition of Done

* [ ] Acceptance criteria met
* [ ] Code reviewed
* [ ] Unit tests completed
* [ ] Screenshot (Roborazzi) test for the keypad-stays-open regression passes
* [ ] Documentation updated
* [ ] Product Owner accepted

---

## Notes

Scenario 3 is a regression guard: the keypad previously disappeared as soon as the amount became
valid (even a single digit), with no explicit confirm step — fixed by gating the Details/summary
transition behind an explicit confirm action rather than auto-advancing on validity.

* **Gap fix (2026-07):** "optionally naming people" was unimplemented — the draft carried a
  `names` list all the way to save, but every UI surface rendered the name as a read-only `Text`
  and no `onNameChange` path existed. Participant names are now editable inline on the input
  screen in both Equal and Custom modes (`SharedCostParticipantRow` gains an editable-name field;
  a cleared name shows the "Person N" placeholder and falls back to that default at save via
  `SharedCostSplitLogic.resolveNames`). The custom-share amount field also now requests the
  decimal number-pad IME (`KeyboardType.Decimal`) and filters input to digits plus one decimal
  point — previously it opened the full text keyboard. Covered by `SharedCostNameEditingTest`
  (both modes) and `SharedCostNameResolutionTest` (blank/whitespace/trim fallback rules).
