# User Story

> **ID:** US-HIS-7 · **Service:** `feature:history` · **Screen:** 06 Journal Detail
> **Priority:** 🔴 Must · **Status:** ✅ Implemented · **Persona:** any user

## Title

> Delete a past entry safely

---

## User Story

**As** any user
**I want to** confirm before deleting an entry
**So that** I don't lose data by accident

---

## Description

### Background

Deletion is irreversible by design (no trash/undo in MVP), so it always sits behind an explicit
confirmation dialog. If the deleted entry was linked to an Event or Debt, that linked balance must
recalculate immediately so nothing is left stale.

### Scope

**In Scope**

* Confirmation dialog before delete commits.
* Immediate recalculation of any linked Event/Debt balance.

**Out of Scope**

* Edit — covered by [US-HIS-6](US-HIS-6.md).

---

## Acceptance Criteria

### Scenario 1 — Confirmed delete

**Given**

* I choose `Delete`.

**When**

* I confirm in the dialog.

**Then**

* The entry is removed and I return to Journal.

### Scenario 2 — Linked balance recalculates

**Given**

* The entry was tagged to an Event or Debt.

**When**

* It is edited or deleted.

**Then**

* The linked Event/Debt recalculates its balance immediately.

---

## Functional Requirements

* [ ] Delete always requires an explicit confirmation dialog — no one-tap delete.
* [ ] Confirmed delete removes the record and returns to Journal.
* [ ] Deleting (or editing) a linked record triggers immediate recalculation of the linked Event's/Debt's balance.

---

## Non-Functional Requirements

* [ ] **Reliability** — delete is atomic; a cancelled confirmation leaves the record fully intact.

---

## Business Rules

* Deletion is permanent — there is no undo/trash in MVP.
* A deleted record's link (if any) is removed along with it; the linked Event/Debt itself is never deleted as a side effect.

---

## UI / UX Notes

* **Design / Mockup:** [`06-journal-detail.md`](../../../design-system-spec/screens/06-journal-detail.md) → "Delete confirmation".
* **User Flow:** Journal Detail → action sheet → `Delete` → confirm dialog → Journal.

---

## Dependencies

* **Story/Task:** [US-HIS-6](US-HIS-6.md) (edit, same action sheet), [US-LOG-5](../logging/US-LOG-5.md) (event/debt linking), [US-EVT-4](../eventbudget/US-EVT-4.md), [US-DEBT-2](../debt/US-DEBT-2.md).

---

## Definition of Done

* [ ] Acceptance criteria met
* [ ] Code reviewed
* [ ] Unit tests completed
* [ ] Screenshot (Roborazzi) test for the confirmation dialog passes
* [ ] Documentation updated
* [ ] Product Owner accepted

---

## Notes

None.
