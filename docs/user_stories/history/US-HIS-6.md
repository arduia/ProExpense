# User Story

> **ID:** US-HIS-6 · **Service:** `feature:history` · **Screen:** 06 Journal Detail
> **Priority:** 🔴 Must · **Status:** ✅ Implemented · **Persona:** any user

## Title

> Edit a past entry

---

## User Story

**As** any user
**I want to** edit a logged entry
**So that** I can correct mistakes after the fact

---

## Description

### Background

Mistakes happen — wrong amount, wrong category, wrong date. Edit reuses the same Add Expense
(Details) surface, pre-filled with the existing record's values, and updates the record in place
rather than creating a duplicate.

### Scope

**In Scope**

* Action sheet (Edit · Delete · Cancel) from Journal Detail.
* Edit opens Details pre-filled with the record's values.
* Save updates the same record (no duplicate) and regroups it if the date changed.

**Out of Scope**

* Delete itself — covered by [US-HIS-7](US-HIS-7.md).

---

## Acceptance Criteria

### Scenario 1 — Action sheet

**Given**

* I am on Journal Detail.

**When**

* I open the action sheet.

**Then**

* It offers Edit · Delete · Cancel.

### Scenario 2 — Edit pre-fills existing values

**Given**

* I tap `Edit`.

**When**

* The editor opens.

**Then**

* Add Expense (Details) is pre-filled with the record's existing values: amount, category, date/time, note, tag.

### Scenario 3 — Save updates in place

**Given**

* I change fields and save.

**When**

* The update commits.

**Then**

* The same record is updated (no duplicate created), I return to Journal, and a changed date regroups it under the new day.

---

## Functional Requirements

* [ ] Journal Detail exposes an action sheet with Edit, Delete, Cancel.
* [ ] Edit pre-fills amount, category, date/time, note, and any `@` tag from the existing record.
* [ ] Save calls `FinanceRecordRepository.upsert(...)` on the existing record's id — never inserts a new record.
* [ ] A changed date moves the record to the correct day group in Journal/Home on next render.

---

## Non-Functional Requirements

* [ ] **Reliability** — edit-then-save round-trips exactly once; no duplicate rows, no orphaned drafts.

---

## Business Rules

* Edit preserves the record's `id`, `type`, and creation metadata — only mutable fields (amount, category, note, date, link) change.

---

## UI / UX Notes

* **Design / Mockup:** [`06-journal-detail.md`](../../../design-system-spec/screens/06-journal-detail.md) → "Edit".
* **User Flow:** Journal Detail → action sheet → `Edit` → Details (pre-filled) → `Save` → Journal.

---

## Technical Notes

* Implemented at the app layer (`ExpenseApp`) since `feature:logging` and `feature:history` cannot
  depend on each other: `feature:logging` exposes an `EditExpenseFlow(recordId, ...)` entry point
  that loads the record via `FinanceRecordRepository.getById(...)`, reuses the existing
  `QuickLogFlow` composable pre-filled, and saves via `upsert(existing.copy(...))`.

---

## Dependencies

* **Story/Task:** [US-HIS-5](US-HIS-5.md) (Detail), [US-LOG-3](../logging/US-LOG-3.md) (Details screen reused for editing).

---

## Definition of Done

* [ ] Acceptance criteria met
* [ ] Code reviewed
* [ ] Unit tests completed
* [ ] Screenshot (Roborazzi) test for the pre-filled edit state passes
* [ ] Documentation updated
* [ ] Product Owner accepted

---

## Notes

Edit deliberately reuses the Add Expense composable rather than building a separate edit screen,
to avoid duplicating validation/UI logic between "create" and "edit" paths.
