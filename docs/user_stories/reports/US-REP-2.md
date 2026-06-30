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

* Monthly period selector (monthly granularity only in MVP).
* Swipe left/right to move between months.

**Out of Scope**

* Weekly/yearly granularity — not in MVP scope.

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

None.
