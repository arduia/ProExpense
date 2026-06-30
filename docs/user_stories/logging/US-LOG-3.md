# User Story

> **ID:** US-LOG-3 · **Service:** `feature:logging` · **Screen:** 04 Add Expense (Details)
> **Priority:** 🔴 Must · **Status:** ✅ Implemented · **Persona:** 🏠 Siti (Housekeeper)

## Title

> Add context to an expense

---

## User Story

**As** Siti 🏠
**I want to** set category, date, and a note on an expense
**So that** my records are meaningful when I look back at them later

---

## Description

### Background

Quick-commit is fast but generic (default category, today's date). The Details step is the
deliberate path: it lets a user who has the extra few seconds set a real category, backdate the
entry, and leave a note — without ever blocking the faster quick-commit path on Amount.

### Scope

**In Scope**

* Details screen: read-only amount recap, required category, optional note (≤ 200 chars), date/time field defaulting to today.
* Save → return to Home with success toast.

**Out of Scope**

* Backdating mechanics in depth — covered by [US-LOG-4](US-LOG-4.md).
* Note length enforcement in depth — covered by [US-LOG-6](US-LOG-6.md).
* Event/debt tagging — covered by [US-LOG-5](US-LOG-5.md).

---

## Acceptance Criteria

### Scenario 1 — Entering Details from a valid amount

**Given**

* I have entered a valid amount.

**When**

* I tap `Next`.

**Then**

* Details opens with the amount shown read-only at top; tapping it returns to Amount with the value preserved.

### Scenario 2 — Category required, note optional

**Given**

* I am on Details.

**When**

* I save.

**Then**

* A category is required and a note is optional (≤ 200 chars).

### Scenario 3 — Date field defaults to today

**Given**

* I am on Details.

**When**

* I view the date field.

**Then**

* It defaults to today and opens a date/time picker that allows both past and future dates.

### Scenario 4 — Successful save

**Given**

* Details are valid (category selected).

**When**

* I save.

**Then**

* I return to Home with a success toast.

---

## Functional Requirements

* [ ] Amount recap at the top of Details is read-only but tappable to go back and edit.
* [ ] `Save` requires a selected category; note is optional and capped at 200 chars.
* [ ] Date/time field defaults to "now" and opens a picker allowing any past or future date.
* [ ] Successful save returns to Home with a success toast.

---

## Non-Functional Requirements

* [ ] **Performance** — Details adds no more than a couple of seconds to the overall flow versus quick-commit.
* [ ] **Reliability** — save persists fully offline.
* [ ] **Accessibility** — date field and category chips meet the 48dp minimum touch target.

---

## Business Rules

* Category is mandatory on Details (unlike quick-commit, which always uses the default).
* Note is capped at 200 characters (see [US-LOG-6](US-LOG-6.md)).

---

## UI / UX Notes

* **Design / Mockup:** [`04-add-expense.md`](../../../design-system-spec/screens/04-add-expense.md) → "Details".
* **User Flow:** Amount → `Next` → Details → `Save` → Home (toast).
* **Validation Rules:** category required; note ≤ 200 chars.

---

## Technical Notes

* Writes a `FinanceRecord` via `FinanceRecordRepository.upsert(...)` with the picked category, date/time, and note.

---

## Dependencies

* **Story/Task:** [US-LOG-1](US-LOG-1.md) (Amount/quick commit), [US-LOG-4](US-LOG-4.md) (backdating), [US-LOG-6](US-LOG-6.md) (note limit), [US-CAT-1](../categories/US-CAT-1.md) (categories).

---

## Definition of Done

* [ ] Acceptance criteria met
* [ ] Code reviewed
* [ ] Unit tests completed
* [ ] Screenshot (Roborazzi) test for the Details state passes
* [ ] Documentation updated
* [ ] Product Owner accepted

---

## Notes

Tapping the read-only amount to return to Amount must never clear the typed value — this is a
common regression point when wiring two-way navigation between Amount and Details.
