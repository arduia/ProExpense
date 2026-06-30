# User Story

> **ID:** US-CAT-3 · **Service:** `feature:categories` · **Screen:** 11 Category List
> **Priority:** 🔴 Must · **Status:** ✅ Implemented · **Persona:** any user

## Title

> Edit or delete a custom category

---

## User Story

**As** any user
**I want to** manage the custom categories I created
**So that** my category list stays relevant as my spending changes

---

## Description

### Background

Custom categories will go stale — a freelancer's "Client X" category might outlive the client.
Editing and deleting need to be safe: deleting a category must never delete the expenses logged
under it, and "Uncategorized" exists precisely to catch those orphaned records.

### Scope

**In Scope**

* Bottom-sheet Edit/Delete actions, custom categories only.
* Reassigning a deleted category's expenses to Uncategorized.
* Uncategorized visibility rules (Journal/Reports yes, logging picker no).

**Out of Scope**

* Creating a new category — covered by [US-CAT-2](US-CAT-2.md).

---

## Acceptance Criteria

### Scenario 1 — Managing a custom category

**Given**

* A custom category.

**When**

* I open its bottom sheet.

**Then**

* Edit and Delete actions are available (default categories never show these actions).

### Scenario 2 — Deleting reassigns expenses

**Given**

* I delete a category.

**When**

* It is removed.

**Then**

* Its existing expenses move to Uncategorized rather than being deleted.

### Scenario 3 — Uncategorized visibility

**Given**

* Uncategorized.

**When**

* I log a new expense or review history.

**Then**

* Uncategorized is not selectable while logging, but is shown for reference in Journal and Reports.

---

## Functional Requirements

* [ ] Bottom-sheet Edit/Delete actions appear only for custom categories.
* [ ] Deleting a category reassigns its linked `FinanceRecord`s to Uncategorized; no record is
  deleted as a side effect.
* [ ] Uncategorized is excluded from the category picker in Add Expense.
* [ ] Uncategorized is included in Journal filters and Reports breakdowns when expenses exist under it.

---

## Non-Functional Requirements

* [ ] **Reliability** — category deletion is atomic with the expense reassignment; no record is
  left pointing at a deleted category.

---

## Business Rules

* Default categories have no Edit/Delete entry point.
* Deleting a category never deletes the expenses under it — they become Uncategorized.
* Uncategorized cannot be chosen explicitly when logging; it is purely a fallback bucket.

---

## UI / UX Notes

* **Design / Mockup:** [`11-category-list.md`](../../../design-system-spec/screens/11-category-list.md).

---

## Dependencies

* **Story/Task:** [US-CAT-1](US-CAT-1.md), [US-CAT-2](US-CAT-2.md), [US-HIS-1](../history/US-HIS-1.md) (Uncategorized in Journal), [US-REP-1](../reports/US-REP-1.md) (Uncategorized in Reports).

---

## Definition of Done

* [ ] Acceptance criteria met
* [ ] Code reviewed
* [ ] Unit tests completed
* [ ] Screenshot (Roborazzi) test for the category bottom-sheet actions passes
* [ ] Documentation updated
* [ ] Product Owner accepted

---

## Notes

None.
