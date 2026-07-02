# User Story

> **ID:** US-REP-2 · **Service:** `feature:reports` · **Screen:** 12 Reports
> **Priority:** 🟡 Should · **Status:** ✅ Implemented · **Persona:** any user

## Title

> Move between months

---

## User Story

**As** any user
**I want to** switch between reporting periods easily
**So that** I can compare how this month stacks up against earlier ones

---

## Description

### Background

A single month's snapshot is useful, but comparison is where the insight is — "did I spend more
this month than last?" Both a tappable selector and a swipe gesture are supported so the
interaction feels as fast as the rest of the app.

### Scope

**In Scope**

* Monthly period selector.
* Swipe left/right to move between periods.

**Out of Scope**

* Weekly granularity — covered by [US-REP-4](US-REP-4.md). Yearly granularity is still out of scope.

---

## Acceptance Criteria

### Scenario 1 — Period selector present

**Given**

* I am on Reports.

**When**

* I view the top of the screen.

**Then**

* A period selector is present; in MVP it operates at monthly granularity only.

### Scenario 2 — Two ways to change period

**Given**

* I want to view another month.

**When**

* I swipe left/right on the report, or use the selector's controls.

**Then**

* Both methods change the displayed period to the equivalent result.

---

## Functional Requirements

* [ ] Period selector exposes previous/next controls for monthly navigation.
* [ ] Swiping left/right on the report content also navigates to the adjacent month.
* [ ] Both navigation methods stay in sync with the same underlying selected period.

---

## Non-Functional Requirements

* [ ] **Usability** — swipe and selector-button navigation produce identical results, so users can
  use whichever is more convenient.

---

## Business Rules

* MVP supports monthly periods only — no weekly or yearly rollups.

---

## UI / UX Notes

* **Design / Mockup:** [`12-reports.md`](../../../design-system-spec/screens/12-reports.md).

---

## Dependencies

* **Story/Task:** [US-REP-1](US-REP-1.md).

---

## Definition of Done

* [ ] Acceptance criteria met
* [ ] Code reviewed
* [ ] Unit tests completed
* [ ] Screenshot (Roborazzi) test for the period-swipe interaction passes
* [ ] Documentation updated
* [ ] Product Owner accepted

---

## Notes

* **Gap fix (2026-07):** "Out of Scope" previously said weekly/yearly granularity was not in MVP
  scope at all; weekly granularity is now implemented (see [US-REP-4](US-REP-4.md)) so the language
  here was updated to reflect that the period selector is no longer month-only — swipe/chevron
  navigation works identically regardless of which granularity is active. Yearly granularity
  remains out of scope.
* **Gap fix (2026-07, follow-up audit):** the period pill's chevrons were reversed —
  "Previous period" (left, pointing left) moved to a *newer* period and "Next period" (right)
  moved to an *older* one, since the underlying period list is newest-first while the chevron
  labels assumed calendar-forward semantics. TalkBack announced the opposite of what the tap did.
  Swapped the chevron actions so "Previous" always moves further into the past and "Next" always
  moves back toward the present, and replaced the chevrons' modulo wrap-around with clamping
  (disabling + dimming at the oldest/newest period) to match `HorizontalPager`'s own swipe
  behavior, which never wrapped. Covered by `ReportsPeriodNavigationTest`.
