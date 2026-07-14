# User Story

> **ID:** US-CAT-1 · **Service:** `feature:categories` · **Screen:** 11 Category List
> **Priority:** 🔴 Must · **Status:** ✅ Implemented · **Persona:** any user

## Title

> Use sensible default categories

---

## User Story

**As** any user
**I want to** have ready-made categories available from first launch
**So that** I can log an expense without setting anything up first

---

## Description

### Background

Forcing a new user to build a category list before they can log their first expense would break
the "log in under 5 seconds" promise. A small, locked default set covers the common cases for
everyone and guarantees the category picker is never empty.

### Scope

**In Scope**

* Default category set (Food, Transport, Shopping, Bills, Health, Entertainment, Income, Salary, Gift),
  present from first launch.
* Defaults can be freely reordered — including interleaved with custom categories — via the same
  drag-to-reorder list; only their name/icon/color/existence are fixed.
* Defaults remain visible even with zero custom categories.

**Out of Scope**

* Creating/editing/deleting categories — covered by [US-CAT-2](US-CAT-2.md), [US-CAT-3](US-CAT-3.md).

---

## Acceptance Criteria

### Scenario 1 — Defaults on a fresh install

**Given**

* A fresh install.

**When**

* I view categories.

**Then**

* The defaults (Food, Transport, Shopping, Bills, Health, Entertainment, Income, Salary, Gift) are
  present and draggable, in their seeded order.

### Scenario 2 — Defaults survive deleting all custom categories

**Given**

* I delete all custom categories.

**When**

* I view the list.

**Then**

* Defaults remain visible — there is no empty state.

---

## Functional Requirements

* [ ] App seeds the default categories on first launch.
* [ ] Default categories cannot be edited, renamed, recolored, or deleted — tapping a default row is a
  no-op.
* [ ] Default categories can be reordered via drag-to-reorder, freely interleaved with custom
  categories in one merged list.

---

## Non-Functional Requirements

* [ ] **Reliability** — the category list is never empty, regardless of how many custom categories
  the user creates or deletes.

---

## Business Rules

* Default categories are fixed and not user-editable (name, icon, color, existence).
* Category order (default and custom, freely interleaved) is entirely user-defined via drag-to-reorder.

---

## UI / UX Notes

* **Design / Mockup:** [`11-category-list.md`](../../../design-system-spec/screens/11-category-list.md).

---

## Dependencies

* **Story/Task:** [US-CAT-2](US-CAT-2.md), [US-CAT-3](US-CAT-3.md), [US-LOG-3](../logging/US-LOG-3.md) (category picker in Add Expense).

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
