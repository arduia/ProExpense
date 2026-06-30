# User Story

> **ID:** US-LOG-4 · **Service:** `feature:logging` · **Screen:** 04 Add Expense (Details)
> **Priority:** 🟡 Should · **Status:** ✅ Implemented · **Persona:** 🎓 Maya (Student)

## Title

> Backdate an expense

---

## User Story

**As** Maya 🎓
**I want to** set a past date on an expense I forgot to log
**So that** my history stays accurate

---

## Description

### Background

Real logging rarely happens at the exact moment of spending. The date/time picker on Details
lets a user correct the timestamp to when the spending actually happened, and that timestamp
drives where the entry groups in Journal and Home — not the entry's creation time.

### Scope

**In Scope**

* Date + time picker pre-filled with the entry's current timestamp.
* Saving a past date regroups the entry under that day everywhere it's shown.

**Out of Scope**

* The Details screen's other fields — covered by [US-LOG-3](US-LOG-3.md).
* Editing an already-saved record's date — covered by [US-HIS-6](../history/US-HIS-6.md).

---

## Acceptance Criteria

### Scenario 1 — Picker opens pre-filled

**Given**

* I am on Details.

**When**

* I tap the date field.

**Then**

* A date + time picker opens, pre-filled with the entry's current timestamp.

### Scenario 2 — A past date regroups the entry

**Given**

* I pick a past date.

**When**

* I save.

**Then**

* The entry stores that timestamp and groups under that day in Journal and Home — not under today.

---

## Functional Requirements

* [ ] Date/time picker pre-fills with the entry's current timestamp on open.
* [ ] The picker allows any past date (no lower bound beyond a sane calendar minimum).
* [ ] The saved timestamp — not the save-time clock — determines day-grouping everywhere the entry appears.

---

## Non-Functional Requirements

* [ ] **Reliability** — the picked timestamp persists exactly as chosen; no timezone drift on relaunch.

---

## Business Rules

* Day-grouping (Journal, Home) is always keyed off `recordedAtEpochMillis`, never creation time.

---

## UI / UX Notes

* **Design / Mockup:** [`04-add-expense.md`](../../../design-system-spec/screens/04-add-expense.md) → "Details · date picker".
* **User Flow:** Details → date field → picker → confirm → Details (updated date shown).

---

## Dependencies

* **Story/Task:** [US-LOG-3](US-LOG-3.md) (Details), [US-HIS-1](../history/US-HIS-1.md) (day grouping in Journal), [US-HOME-2](../app-shell/US-HOME-2.md) (day grouping on Home).

---

## Definition of Done

* [ ] Acceptance criteria met
* [ ] Code reviewed
* [ ] Unit tests completed
* [ ] Screenshot (Roborazzi) test for the picker-open state passes
* [ ] Documentation updated
* [ ] Product Owner accepted

---

## Notes

A future date is technically permitted by the picker (no PRD rule blocking it); revisit if product
wants to cap entries at "today" for true expense tracking.
