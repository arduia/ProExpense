# User Story

> **ID:** US-LOG-2 · **Service:** `feature:logging` · **Screen:** 04 Add Expense (Amount)
> **Priority:** 🔴 Must · **Status:** ✅ Implemented · **Persona:** any user

## Title

> Block an empty amount

---

## User Story

**As** any user
**I want to** be stopped from saving a $0 entry
**So that** I don't create meaningless records

---

## Description

### Background

The Amount screen's quick-commit `Save` is fast by design, which makes an accidental $0 or
empty save easy to trigger. A `> $0` gate on `Save`/`Next`, paired with live input normalization,
keeps every saved record meaningful without slowing down valid entry.

### Scope

**In Scope**

* `canProceed = value > 0` gating `Save` and `Next`.
* Shake + inline error when a disabled action is tapped.
* Live input normalization while typing (decimal/digit caps, leading zeros, comma grouping).

**Out of Scope**

* What happens after a valid `Save`/`Next` — covered by [US-LOG-1](US-LOG-1.md), [US-LOG-3](US-LOG-3.md).

---

## Acceptance Criteria

### Scenario 1 — Disabled actions on zero/empty

**Given**

* The amount is $0 or empty.

**When**

* I view the Amount screen.

**Then**

* `Save` and `Next` are disabled (`canProceed = value > 0`).

### Scenario 2 — Tapping a disabled action

**Given**

* The amount is $0.

**When**

* I tap the disabled `Save`/`Next`.

**Then**

* The field shakes (±4dp) and shows "Amount must be greater than $0".

### Scenario 3 — Live input normalization

**Given**

* I am typing an amount.

**When**

* I enter digits.

**Then**

* The whole part is capped at 7 digits, the fraction at 2, only a single decimal point is allowed, leading zeros are stripped (except "0."), and commas group live.

---

## Functional Requirements

* [ ] `Save`/`Next` enablement is strictly `amount > 0`.
* [ ] Disabled-tap feedback: ±4dp shake + "Amount must be greater than $0" message.
* [ ] Input normalization enforces 7-digit whole part, 2-digit fraction, single decimal, stripped leading zeros, live comma grouping.

---

## Non-Functional Requirements

* [ ] **Accessibility** — the error message is announced (not purely visual) for screen readers.
* [ ] **Reliability** — normalization runs fully offline, client-side only.

---

## Business Rules

* Amount must be **> $0** to commit (shared gate referenced by [US-LOG-1](US-LOG-1.md)).
* Max amount is **999,999,999.99** (whole part ≤ 7 digits, fraction ≤ 2) — a hard product-wide ceiling.

---

## UI / UX Notes

* **Design / Mockup:** [`04-add-expense.md`](../../../design-system-spec/screens/04-add-expense.md) → "Amount · zero/invalid".
* **Validation Rules:** single decimal; leading zeros stripped (except "0."); commas grouped live; 7-digit whole / 2-digit fraction caps.
* **Error Messages:** "Amount must be greater than $0."

---

## Dependencies

* **Story/Task:** [US-LOG-1](US-LOG-1.md) (quick commit), [US-LOG-3](US-LOG-3.md) (Details).

---

## Definition of Done

* [ ] Acceptance criteria met
* [ ] Code reviewed
* [ ] Unit tests completed
* [ ] Screenshot (Roborazzi) test for the shake/error state passes
* [ ] Documentation updated
* [ ] Product Owner accepted

---

## Notes

The 999,999,999.99 ceiling is a product-wide guard (see AGENTS.md product constraints), not unique
to this screen — any future amount-entry surface must enforce the same cap.
