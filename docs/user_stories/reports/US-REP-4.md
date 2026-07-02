# User Story

> **ID:** US-REP-4 · **Service:** `feature:reports` · **Screen:** 12 Reports
> **Priority:** 🟡 Should · **Status:** ✅ Implemented · **Persona:** 🎓 Maya (Student)

## Title

> See a weekly breakdown of my spending

---

## User Story

**As** Maya 🎓
**I want to** switch Reports to a weekly granularity
**So that** I can check in on my spending more often than once a month

---

## Description

### Background

PRD Feature #9 ("daily, weekly, monthly summary view") lists weekly/daily granularity as part of
the 🟡 Should-Have Summary View, but Reports only ever grouped by calendar month — a student
checking in every few days had no way to see a tighter window. `GenerateReportPeriodUseCase` was
already granularity-agnostic (it operates on any `[start, end)` window plus a `daysInPeriod`), so
the gap was purely in the orchestration/UI layer, which only ever built monthly windows.

### Scope

**In Scope**

* Month/Week segmented toggle at the top of Reports, above the period pill.
* Weekly period windows (locale-aware week start, e.g. Sunday in en-US) with the same total,
  donut, top-categories, and daily-average breakdown as the monthly view.
* Swipe left/right and the period pill's chevrons navigate week-to-week when Week is selected.
* Switching granularity re-derives the period list and jumps to the current period in the new
  granularity.

**Out of Scope**

* Daily granularity — still out of scope for MVP.
* Yearly granularity — still out of scope for MVP.
* Persisting the last-selected granularity across app restarts — resets to Month on next launch.

---

## Acceptance Criteria

### Scenario 1 — Switching to Week

**Given**

* I am on Reports viewing a month.

**When**

* I tap the "Week" segment of the granularity toggle.

**Then**

* The period pill and content switch to the current calendar week's breakdown (e.g.
  "May 18 – May 24"), using the same total/donut/top-categories/daily-average layout as the
  monthly view.

### Scenario 2 — Navigating weeks

**Given**

* Week granularity is selected.

**When**

* I swipe left/right on the report body, or use the period pill's chevrons.

**Then**

* The displayed week moves to the adjacent week, consistent with monthly navigation.

### Scenario 3 — Daily average for the current week

**Given**

* Week granularity is selected and the current (in-progress) week is shown.

**When**

* The daily average is computed.

**Then**

* It is total ÷ days elapsed so far in that week (inclusive of today); a fully elapsed week uses
  total ÷ 7.

---

## Functional Requirements

* [ ] A Month/Week segmented toggle is shown above the period pill whenever Reports has data.
* [ ] Selecting Week rebuilds the period list as locale-aware calendar weeks instead of months.
* [ ] Period pill label, swipe navigation, and chevrons work identically for both granularities.
* [ ] Daily average for the current week uses elapsed days; a past week uses 7.

---

## Non-Functional Requirements

* [ ] **Consistency** — the weekly view reuses the exact same content composable
  (`ReportsPeriodContent`) as the monthly view; no separate weekly-only layout.

---

## Business Rules

* Week start follows the device locale's first day of week (e.g. Sunday for en-US).
* The granularity toggle does not persist across process death — always starts at Month.

---

## UI / UX Notes

* **Design / Mockup:** extends [`12-reports.md`](../../../design-system-spec/screens/12-reports.md)
  — the toggle reuses the existing `SegmentedToggle` component (already used for the JSON/CSV
  export format and Shared Costs); no new component was introduced.

---

## Technical Notes

* `daysElapsedInWeek(periodStartEpochMillis, periodEndEpochMillis, nowEpochMillis)`
  (`feature:reports` `commonMain`) is the weekly analogue of the existing days-elapsed-in-month
  logic, pure and platform-agnostic.
* `ReportsFeatureEntryImpl` computes `monthlyPeriods` and `weeklyPeriods` up front and swaps
  between them by `granularityIndex`, reusing a shared `PeriodWindow` (label, start, end,
  daysInPeriod) data class and `buildPeriodState` builder for both.
* `weekWindow` derives the week start via an explicit day-offset
  (`(dayOfWeek - firstDayOfWeek + 7) % 7`) rather than `Calendar.set(DAY_OF_WEEK, …)`, avoiding
  month/year rollover ambiguity at week boundaries.

---

## Dependencies

* **Story/Task:** [US-REP-1](US-REP-1.md), [US-REP-2](US-REP-2.md).

---

## Definition of Done

* [x] Acceptance criteria met
* [x] Code reviewed
* [x] Unit tests completed
* [x] Screenshot (Roborazzi) test for the weekly breakdown passes
* [x] Documentation updated
* [ ] Product Owner accepted

---

## Notes

* **Gap fix (2026-07):** PRD Feature #9 calls for "daily, weekly, monthly summary view" as a
  🟡 Should-Have, but Reports had only ever supported monthly grouping. `GenerateReportPeriodUseCase`
  needed no changes — it already worked on any `[start, end)` window; the fix added
  `daysElapsedInWeek`, a `weekWindow` builder, and a Month/Week `SegmentedToggle` in
  `ReportsFeatureEntry.kt` / `ReportsFlow.kt`. Covered by `DaysElapsedInWeekTest` and the new
  `ReportsScreenshotTest.reports_flow_monthly` / `reports_flow_weekly` tests (the latter also
  closed a pre-existing gap where `ReportsScreenshotTest` only ever exercised `ReportsScreen`
  content, never the `ReportsFlow` orchestration composable itself).
