# User Story

> **ID:** US-LOG-6 · **Service:** `feature:logging` · **Screen:** 04 Add Expense (Details)
> **Priority:** 🟡 Should · **Status:** ✅ Implemented · **Persona:** any user

## Title

> Avoid runaway notes

---

## User Story

**As** any user
**I want to** see a clear limit on note length
**So that** the note field stays manageable and readable later

---

## Description

### Background

An unbounded note field would make rows in Journal unreadable and bloat exports. A simple 200-char
cap with a visible counter and hard stop keeps notes useful without surprising the user mid-type.

### Scope

**In Scope**

* 200-character cap on the Details note field.
* Counter that turns to the error color near/at the limit.
* Input ignored once the cap is reached.

**Out of Scope**

* Other Details fields — covered by [US-LOG-3](US-LOG-3.md).

---

## Acceptance Criteria

### Scenario 1 — Counter warns near the limit

**Given**

* I am typing a note.

**When**

* I reach 200 characters.

**Then**

* The counter turns to the error color.

### Scenario 2 — Input stops at the cap

**Given**

* The note is at 200 characters.

**When**

* I type more.

**Then**

* Further input is ignored — the note never exceeds 200 characters.

---

## Functional Requirements

* [ ] Note field enforces a hard 200-character cap.
* [ ] A live counter is visible while typing.
* [ ] The counter switches to the error color at the 200-character limit.

---

## Non-Functional Requirements

* [ ] **Accessibility** — the counter's color change is paired with a non-color cue (e.g. text) for color-blind users.

---

## Business Rules

* Note max length is 200 characters everywhere a note is entered (Details, quick-note edits in Journal).

---

## UI / UX Notes

* **Design / Mockup:** [`04-add-expense.md`](../../../design-system-spec/screens/04-add-expense.md) → "Details · note".
* **Validation Rules:** 200-char hard cap; counter turns error-colored at the limit.

---

## Dependencies

* **Story/Task:** [US-LOG-3](US-LOG-3.md) (Details note field), [US-HIS-4](../history/US-HIS-4.md) (quick-note inline edit, same 200-char rule).

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
