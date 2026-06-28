# User Story

> **ID:** US-REP-3 · **Service:** `feature:reports` · **Screen:** 12 Reports
> **Priority:** 🟡 Should · **Status:** ✅ Implemented · **Persona:** any new user

## Title

> Get useful empty/edge behavior

---

## User Story

**As** a new user
**I want to** see Reports behave gracefully when I have little or no data
**So that** the screen never feels broken or confusing

---

## Description

### Background

A brand-new user, or someone who skipped logging for a month, will hit Reports with no data for the
selected period — sometimes no data at all. The screen needs a deliberate response for each case
instead of rendering an empty or broken-looking chart.

### Scope

**In Scope**

* True empty state (never logged anything).
* Auto-fallback to the last month with data.
* All-uncategorized rendering (full grey donut + tip).

**Out of Scope**

* The breakdown itself when data exists — covered by [US-REP-1](US-REP-1.md).

---

## Acceptance Criteria

### Scenario 1 — No data ever

**Given**

* There is no expense data at all.

**When**

* I open Reports.

**Then**

* An empty state "No data yet…" is shown.

### Scenario 2 — No data this month

**Given**

* There is no data for the current month, but earlier months have data.

**When**

* I open Reports.

**Then**

* It auto-switches to the most recent month with data — no empty state is shown.

### Scenario 3 — Everything is uncategorized

**Given**

* Every expense in the selected month is Uncategorized.

**When**

* The chart renders.

**Then**

* One full grey donut segment is shown with the tip "categorize your expenses for better
  insights." (this segment only ever appears when Uncategorized expenses exist).

---

## Functional Requirements

* [ ] Zero records ever logged renders the "No data yet…" empty state.
* [ ] Zero records for the current month with history elsewhere auto-selects the latest month with
  data, instead of showing empty.
* [ ] An all-Uncategorized month renders a single grey donut segment with the categorization tip.
* [ ] The Uncategorized segment/tip never appears for a month with no Uncategorized expenses.

---

## Non-Functional Requirements

* [ ] **Usability** — every no/sparse-data state gives the user a clear next action (start
  logging, or categorize existing expenses) rather than a blank screen.

---

## Business Rules

* Reports never shows a "valid" empty chart for a month with no data — it falls back or shows the
  dedicated empty state.

---

## UI / UX Notes

* **Design / Mockup:** [`12-reports.md`](../../../design-system-spec/screens/12-reports.md) → empty/edge states.
* **Error Messages:** "No data yet…", "categorize your expenses for better insights."

---

## Dependencies

* **Story/Task:** [US-REP-1](US-REP-1.md), [US-CAT-3](../categories/US-CAT-3.md) (Uncategorized origin).

---

## Definition of Done

* [ ] Acceptance criteria met
* [ ] Code reviewed
* [ ] Unit tests completed
* [ ] Screenshot (Roborazzi) test for the empty state and the all-uncategorized state passes
* [ ] Documentation updated
* [ ] Product Owner accepted

---

## Notes

None.
