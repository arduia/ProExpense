# User Story

> **ID:** US-CAT-2 · **Service:** `feature:categories` · **Screen:** 11 Category List
> **Priority:** 🔴 Must · **Status:** ✅ Implemented · **Persona:** 💼 Raj (Freelancer)

## Title

> Create my own category

---

## User Story

**As** Raj 💼
**I want to** add a custom category with my own icon, color, and name
**So that** my spending categories actually fit how I spend

---

## Description

### Background

The default six categories don't cover every user's life — freelancers, parents, and hobbyists all
have spending buckets the defaults don't name. Custom categories need to feel as first-class as the
defaults: same picker, same styling, same place in Add Expense.

### Scope

**In Scope**

* Add/edit form: icon picker, color picker, name field (≤ 20 chars, with counter).
* Duplicate-name validation.
* Drag-to-reorder for custom categories, freely interleaved with default categories in one merged list.

**Out of Scope**

* Default categories — covered by [US-CAT-1](US-CAT-1.md).
* Edit/delete of an existing custom category — covered by [US-CAT-3](US-CAT-3.md).

---

## Acceptance Criteria

### Scenario 1 — Building a category

**Given**

* The add/edit category form.

**When**

* I configure it.

**Then**

* I can pick an icon and a color, and enter a name of up to 20 characters with a live counter;
  entering a name that duplicates an existing category is blocked with "A category with this name
  already exists."

### Scenario 2 — Invalid form blocks save

**Given**

* The form is invalid (e.g. empty name, or a duplicate name).

**When**

* I view the form.

**Then**

* `Add` is disabled until the form is valid.

### Scenario 3 — Reordering custom categories

**Given**

* Custom categories exist.

**When**

* I reorder them.

**Then**

* They support drag-to-reorder and can be placed anywhere in the list, including ahead of or between
  default categories; the resulting order is mirrored per-type in the Add Expense chip sections.

---

## Functional Requirements

* [ ] Add/edit form requires an icon, a color, and a non-empty name ≤ 20 characters.
* [ ] Name uniqueness is validated against all existing categories (default + custom); duplicates
  show "A category with this name already exists." and block save.
* [ ] `Add` is disabled while the form is invalid.
* [ ] Custom categories support drag-to-reorder, freely interleaved with default categories.
* [ ] Category order (per type) is shared with (mirrored by) the Add Expense category chip order.

---

## Non-Functional Requirements

* [ ] **Usability** — character counter updates live as the user types the name.

---

## Business Rules

* Category names ≤ 20 characters, unique across default + custom categories.
* Category order (default and custom) is fully user-configurable via one shared drag-to-reorder list.

---

## UI / UX Notes

* **Design / Mockup:** [`11-category-list.md`](../../../design-system-spec/screens/11-category-list.md).
* **Error Messages:** "A category with this name already exists."

---

## Dependencies

* **Story/Task:** [US-CAT-1](US-CAT-1.md), [US-LOG-3](../logging/US-LOG-3.md) (category chip order in Add Expense).

---

## Definition of Done

* [ ] Acceptance criteria met
* [ ] Code reviewed
* [ ] Unit tests completed
* [ ] Screenshot (Roborazzi) test for the add/edit category form passes
* [ ] Documentation updated
* [ ] Product Owner accepted

---

## Notes

None.
