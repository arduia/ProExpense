# User Story

> **ID:** US-HIS-8 · **Service:** `feature:history` · **Screen:** 05 Journal
> **Priority:** 🔴 Must · **Status:** ✅ Implemented · **Persona:** any user

## Title

> Filter by date range

---

## User Story

**As** any user
**I want to** narrow the Journal to a specific date range
**So that** I can review spending for a trip, a pay period, or any span that isn't "everything"

---

## Description

### Background

PRD Record History Feature #8 promises "filter by date, category, or currency." Category filtering
already existed ([US-HIS-3](US-HIS-3.md)); date never did — Journal had no way to narrow beyond
scrolling day-by-day or the free-text search. A date-range chip alongside the existing category
chips keeps the affordance consistent with how filtering already works on this screen.

### Scope

**In Scope**

* A date-range picker reachable from a chip in the existing filter row.
* Narrowing the day-grouped list to an inclusive start–end range.
* Clearing the range from the same chip.

**Out of Scope**

* Currency filter — not applicable until a Journal can contain mixed currencies in practice at
  scale; multi-currency logging itself is [US-CUR-4](../currency/US-CUR-4.md).

---

## Acceptance Criteria

### Scenario 1 — Opening the picker

**Given**

* No date range is active.

**When**

* I tap the calendar icon in the filter row.

**Then**

* A date-range picker sheet opens with Clear/Apply actions.

### Scenario 2 — Applying a range

**Given**

* I pick a start and end date and tap Apply.

**When**

* The sheet closes.

**Then**

* The calendar icon is replaced by a chip showing the picked range (e.g. "May 1 – May 15"), and
  the list narrows to that inclusive range, combined with any active category filter or search.

### Scenario 3 — Clearing the range

**Given**

* A date range is active.

**When**

* I tap the chip's clear (×) affordance, or Clear inside the sheet.

**Then**

* The range is removed and the full list (subject to any other active filters) returns.

---

## Functional Requirements

* [ ] A calendar-icon chip in the filter row opens a date-range picker sheet.
* [ ] Applying a range filters the day-grouped list to that inclusive range.
* [ ] An active range renders as a labeled chip with an inline clear action, replacing the icon.
* [ ] The date filter combines with category filter and search (AND semantics).

---

## Non-Functional Requirements

* [ ] **Performance** — filtering by range is instant at typical record counts (client-side, no
  re-query).

---

## Business Rules

* The range comparison is inclusive of both the start and end day.
* Range comparison uses the same day-grouping key (`dayKey`) as the rest of Journal, not raw
  timestamps — a day belongs to the range as a whole unit.

---

## UI / UX Notes

* **Design / Mockup:** [`05-journal.md`](../../../design-system-spec/screens/05-journal.md) filter
  row — extends it with a calendar icon-chip; no dedicated spec screen existed for the picker
  sheet, so it reuses Material3's `DateRangePicker` inside the app's standard `ProBottomSheetHost`.

---

## Technical Notes

* `filterJournalDays()` (`JournalFlow.kt`) takes optional `startDayKey`/`endDayKey` and drops any
  `JournalDayUi` whose `id` (the day's `dayKey` grouping string) falls outside the inclusive range
  — a plain string comparison, since `dayKey` is a fixed-width, zero-padded `"YYYY-DDD"` key that
  sorts lexicographically the same as chronologically.

---

## Dependencies

* **Story/Task:** [US-HIS-1](US-HIS-1.md) (day grouping), [US-HIS-2](US-HIS-2.md) (search),
  [US-HIS-3](US-HIS-3.md) (category filter) — all three filters combine.

---

## Definition of Done

* [x] Acceptance criteria met
* [x] Code reviewed
* [x] Unit tests completed
* [x] Screenshot (Roborazzi) tests for icon/active-chip/picker-sheet states pass
* [x] Documentation updated
* [ ] Product Owner accepted

---

## Notes

* **Gap fix (2026-07):** Journal had no date filter at all, despite PRD Feature #8's "filter by
  date, category, or currency." Added a calendar-icon chip that opens a Material3 `DateRangePicker`
  sheet; picking a range renders it as a clearable chip and narrows `filterJournalDays()`'s output
  via inclusive `dayKey` string comparison, composing with the existing category/search filters.
  Covered by `JournalFlowFilterTest.invoke_dateRangeKeepsDaysWithinRangeInclusive` /
  `invoke_dateRangeExcludesDaysOutsideRange` / `invoke_dateRangeCombinesWithCategoryFilterAndSearch`
  / `invoke_noDateRangeSetReturnsAllDays`.
