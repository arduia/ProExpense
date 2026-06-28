# User Story

> **ID:** US-REP-1 · **Service:** `feature:reports` · **Screen:** 12 Reports
> **Priority:** 🟡 Should · **Status:** ✅ Implemented · **Persona:** 🎓 Maya (Student)

## Title

> See where my money went this month

---

## User Story

**As** Maya 🎓
**I want to** see a monthly breakdown of my spending by category
**So that** I understand where my money actually went

---

## Description

### Background

Logging expenses is only half the value — the other half is seeing the pattern. A single headline
number plus a category breakdown turns a list of entries into an answer to "where did my money go?"
without requiring the user to do any manual tallying.

### Scope

**In Scope**

* Monthly total headline number.
* Donut chart broken down by category.
* Ranked Top-categories list (name + amount).
* Daily average calculation.

**Out of Scope**

* Switching between periods — covered by [US-REP-2](US-REP-2.md).
* Empty/sparse-data behavior — covered by [US-REP-3](US-REP-3.md).

---

## Acceptance Criteria

### Scenario 1 — Headline total

**Given**

* Reports is open.

**When**

* A month is selected.

**Then**

* The total spent that month is shown as the large headline number.

### Scenario 2 — Category breakdown

**Given**

* Spending exists for the selected month.

**When**

* The report renders.

**Then**

* A donut chart breaks the total down by category, with a ranked Top-categories list (name +
  amount) shown below it.

### Scenario 3 — Daily average

**Given**

* A selected month.

**When**

* The daily average is shown.

**Then**

* For the current (in-progress) month it is total ÷ days elapsed; for a past (completed) month it
  is total ÷ days in that month.

---

## Functional Requirements

* [ ] Headline total reflects the sum of all expenses recorded in the selected month.
* [ ] Donut chart segments are proportional to each category's share of the monthly total.
* [ ] Top-categories list ranks categories by amount, descending.
* [ ] Daily average uses elapsed days for the current month, full days-in-month for past months.

---

## Non-Functional Requirements

* [ ] **Performance** — the monthly breakdown renders without a visible delay on typical local
  data volumes (hundreds of records).

---

## Business Rules

* Daily average denominator depends on whether the selected month is the current, in-progress
  month or a fully elapsed past month.

---

## UI / UX Notes

* **Design / Mockup:** [`12-reports.md`](../../../design-system-spec/screens/12-reports.md).

---

## Dependencies

* **Story/Task:** [US-REP-2](US-REP-2.md), [US-REP-3](US-REP-3.md), [US-CAT-3](../categories/US-CAT-3.md) (Uncategorized in the breakdown).

---

## Definition of Done

* [ ] Acceptance criteria met
* [ ] Code reviewed
* [ ] Unit tests completed
* [ ] Screenshot (Roborazzi) test for the monthly breakdown passes
* [ ] Documentation updated
* [ ] Product Owner accepted

---

## Notes

None.
