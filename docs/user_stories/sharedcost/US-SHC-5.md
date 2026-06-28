# User Story

> **ID:** US-SHC-5 · **Service:** `feature:sharedcost` · **Screen:** 10 Shared Costs
> **Priority:** 🟡 Should · **Status:** ✅ Implemented · **Persona:** 👫 Aiko (Cost Sharer)

## Title

> Review and remove past splits

---

## User Story

**As** Aiko 👫
**I want to** see a history of past splits and delete the ones I don't need
**So that** I can keep my Shared Costs list tidy

---

## Description

### Background

Past splits are reference records, not editable financial history — once saved, the breakdown is
fixed. The only lifecycle action available afterward is reviewing the full split or deleting it
outright (with confirmation, consistent with delete behavior elsewhere in the app).

### Scope

**In Scope**

* Shared Costs history list.
* Tap to view full split detail.
* Swipe-left to delete (with confirm).

**Out of Scope**

* Editing a saved split — explicitly not supported.

---

## Acceptance Criteria

### Scenario 1 — View and delete from history

**Given**

* Shared Costs history.

**When**

* I tap a row.

**Then**

* I view the full split; when I swipe-left instead, I can delete it (with confirmation).

### Scenario 2 — Editing is unsupported

**Given**

* A saved split.

**When**

* I try to change it.

**Then**

* Editing is not supported — splits are reference only once saved.

---

## Functional Requirements

* [ ] Tapping a history row opens the full split detail (read-only).
* [ ] Swipe-left exposes a delete action requiring confirmation.
* [ ] No edit entry point exists for a saved split.

---

## Non-Functional Requirements

* [ ] **Reliability** — delete is atomic and removes both the split metadata and (per [US-SHC-4](US-SHC-4.md)) its underlying `FinanceRecord`.

---

## Business Rules

* Saved splits are immutable except for deletion.

---

## UI / UX Notes

* **Design / Mockup:** [`10-shared-costs.md`](../../../design-system-spec/screens/10-shared-costs.md) → "History".

---

## Dependencies

* **Story/Task:** [US-SHC-4](US-SHC-4.md).

---

## Definition of Done

* [ ] Acceptance criteria met
* [ ] Code reviewed
* [ ] Unit tests completed
* [ ] Screenshot (Roborazzi) test for the swipe-to-delete state passes
* [ ] Documentation updated
* [ ] Product Owner accepted

---

## Notes

None.
