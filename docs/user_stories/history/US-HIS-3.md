# User Story

> **ID:** US-HIS-3 · **Service:** `feature:history` · **Screen:** 05 Journal
> **Priority:** 🔴 Must · **Status:** ✅ Implemented · **Persona:** 🎓 Maya (Student)

## Title

> Filter by category

---

## User Story

**As** Maya 🎓
**I want to** filter the Journal list to one category
**So that** I can see, for example, only my food spending

---

## Description

### Background

Category chips mirror the category catalogue so filtering feels consistent with how categories are
presented everywhere else in the app (Add Expense, Category List). "All" is the default, neutral
state.

### Scope

**In Scope**

* Category filter chip row on Journal, mirroring the category catalogue.
* "All" default; single-category selection narrows the list.

**Out of Scope**

* Search — covered by [US-HIS-2](US-HIS-2.md).

---

## Acceptance Criteria

### Scenario 1 — Chips mirror the catalogue

**Given**

* I open Journal.

**When**

* I view the filter chips.

**Then**

* They mirror the category catalogue with "All" as the default selection.

### Scenario 2 — Selecting a category narrows the list

**Given**

* I select a category chip.

**When**

* The list updates.

**Then**

* It narrows to show only entries in that category.

---

## Functional Requirements

* [ ] Filter chip set mirrors the live category catalogue (defaults + custom, in their configured order).
* [ ] "All" is selected by default.
* [ ] Selecting a chip filters the visible list to that category only; selecting "All" clears the filter.

---

## Non-Functional Requirements

* [ ] **Accessibility** — chips meet the 48dp minimum touch target and have clear selected-state contrast.

---

## Business Rules

* Filter chip order matches the category catalogue order used in Add Expense (see [US-CAT-2](../categories/US-CAT-2.md)).

---

## UI / UX Notes

* **Design / Mockup:** [`05-journal.md`](../../../design-system-spec/screens/05-journal.md) → "Category filter".

---

## Dependencies

* **Story/Task:** [US-CAT-1](../categories/US-CAT-1.md) (default categories), [US-HIS-1](US-HIS-1.md) (default grouped view).

---

## Definition of Done

* [ ] Acceptance criteria met
* [ ] Code reviewed
* [ ] Unit tests completed
* [ ] Screenshot (Roborazzi) test for a filtered state passes
* [ ] Documentation updated
* [ ] Product Owner accepted

---

## Notes

* **Gap fix (2026-07):** the filter chips were a hardcoded static list (`journalFilters`:
  All/Food/Transport/Bills/More) with zero filtering effect — tapping a chip only changed which
  chip looked selected; `filteredDays` never read `selectedFilterId`. `HistoryFeatureEntryImpl` now
  builds the chip list from live `CategoryRepository` data (an "All" chip plus one per real
  category, ordered by `sortOrder`), and `filterJournalDays()` filters rows by
  `selectedFilterId` before display. Covered by
  `JournalFlowFilterTest.invoke_filtersByCategoryChip` / `invoke_combinesFilterAndSearch`.
