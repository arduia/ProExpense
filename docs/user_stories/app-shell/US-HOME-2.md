# User Story

> **ID:** US-HOME-2 · **Service:** `app` (Home) · **Screen:** 03 Home
> **Priority:** 🔴 Must · **Status:** ✅ Implemented · **Persona:** any user

## Title

> Glance at recent activity

---

## User Story

**As** any user
**I want to** see my most recent transactions right on Home
**So that** I can review what I just logged without navigating to Journal

---

## Description

### Background

Home is the first screen after launch — surfacing the last handful of entries, grouped by day the
same way Journal does, lets a user confirm "did that expense actually save?" in one glance. Tapping
through reuses Journal's detail view entirely rather than duplicating it on Home.

### Scope

**In Scope**

* Last 5–10 entries, grouped by day with day header + badge/note/meta/amount per row.
* `See all` → Journal.
* Row tap → that record's Journal Detail.

**Out of Scope**

* The Journal screen itself — covered by [US-HIS-1](../history/US-HIS-1.md).

---

## Acceptance Criteria

### Scenario 1 — Recent list grouped by day

**Given**

* I have logged entries.

**When**

* I view Home.

**Then**

* Recent shows the last 5–10 entries, grouped by day with a per-day header (Today / Yesterday /
  date) and badge, note, meta, and amount per row.

### Scenario 2 — See all

**Given**

* Recent is shown.

**When**

* I tap `See all`.

**Then**

* Journal opens.

### Scenario 3 — Row tap opens detail

**Given**

* A recent row.

**When**

* I tap it.

**Then**

* That record's Journal Detail opens — the same behavior as tapping the equivalent row in Journal.

---

## Functional Requirements

* [ ] Recent section shows the most recent 5–10 records, grouped by day using the shared day-label
  logic (Today/Yesterday/date) used by Journal.
* [ ] `See all` navigates to the Journal tab/screen.
* [ ] Tapping a recent row navigates into Journal's detail view for that exact record (same
  component, not a duplicate).

---

## Non-Functional Requirements

* [ ] **Consistency** — day grouping and row tap behavior are identical to Journal's, so the same
  record looks and behaves the same in both places.

---

## Business Rules

* Recent list cap: 5–10 entries.

---

## UI / UX Notes

* **Design / Mockup:** [`03-home.md`](../../../design-system-spec/screens/03-home.md) → Recent section.

---

## Technical Notes

* Row tap routes into the Journal tab with the tapped record pre-selected, reusing Journal's
  existing detail screen and back-navigation rather than building a second detail UI on Home.

---

## Dependencies

* **Story/Task:** [US-HOME-1](US-HOME-1.md), [US-HIS-1](../history/US-HIS-1.md), [US-HIS-2](../history/US-HIS-2.md) (Journal detail reused here).

---

## Definition of Done

* [ ] Acceptance criteria met
* [ ] Code reviewed
* [ ] Unit tests completed
* [ ] Screenshot (Roborazzi) test for the day-grouped Recent list passes
* [ ] Documentation updated
* [ ] Product Owner accepted

---

## Notes

This story tracks two fixes shipped after the original implementation: (1) Home previously
rendered every record under a single hardcoded "Today" group regardless of actual date; (2) Home
rows had no tap target into Journal detail. Both now match Journal's behavior exactly.
