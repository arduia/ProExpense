# User Story

> **ID:** US-EVT-1 · **Service:** `feature:eventbudget` · **Screen:** 07 Event Budget
> **Priority:** 🔵 Phase 2 · **Status:** ✅ Implemented · **Persona:** 🎉 James (Event Organizer)

## Title

> Create an event budget

---

## User Story

**As** James 🎉
**I want to** create an event with a budget and date range
**So that** I can track spend for a trip or party against a fixed limit

---

## Description

### Background

Event Budget is a Phase 2 feature for users who plan around a finite pool of money — a trip, a
party, a project. Creating an event captures a name, date range, and total budget up front, which
later expenses can be tagged against (see [US-LOG-5](../logging/US-LOG-5.md)).

### Scope

**In Scope**

* Create-event sheet: name, start/end date, total budget.
* Validation: budget > $0, unique name, name ≤ 30 chars.
* Empty state when no events exist.

**Out of Scope**

* Tracking multiple simultaneous events — covered by [US-EVT-2](US-EVT-2.md).
* Editing/closing — covered by [US-EVT-5](US-EVT-5.md).

---

## Acceptance Criteria

### Scenario 1 — Create sheet fields

**Given**

* I open the Create sheet.

**When**

* I fill it in.

**Then**

* Fields are: name (required, ≤ 30 chars), start date (today default, past allowed), end date (≥ start), total budget (required, > $0).

### Scenario 2 — Zero budget blocked

**Given**

* The budget is $0.

**When**

* I view the sheet.

**Then**

* `Save` is disabled and "Budget must be greater than $0" is shown.

### Scenario 3 — Duplicate name blocked

**Given**

* I enter an existing event name.

**When**

* Validation runs.

**Then**

* It is blocked inline ("An event with this name already exists.") and the name counter caps at 30.

### Scenario 4 — Empty state

**Given**

* I have no events.

**When**

* I open the screen.

**Then**

* An empty state shows a jar illustration, "No active events…", and a single `Create event` CTA.

---

## Functional Requirements

* [ ] Name is required, ≤ 30 chars, unique (case-insensitive) among existing events.
* [ ] Start date defaults to today and allows past dates; end date must be ≥ start date.
* [ ] Total budget is required and must be > $0.
* [ ] `Save` stays disabled until all validation passes.
* [ ] Zero events renders the dedicated empty state with a single CTA.

---

## Non-Functional Requirements

* [ ] **Accessibility** — form fields and the CTA meet the 48dp minimum touch target.
* [ ] **Reliability** — event creation persists fully offline.

---

## Business Rules

* Event names must be unique.
* Budget must be > $0 (consistent with the amount-entry rule used elsewhere in the app).
* End date is reference only — it does not auto-close the event (see [US-EVT-5](US-EVT-5.md)).

---

## UI / UX Notes

* **Design / Mockup:** [`07-event-budget.md`](../../../design-system-spec/screens/07-event-budget.md).
* **Validation Rules:** budget > $0; name ≤ 30 chars, unique; end date ≥ start date.
* **Empty States:** jar illustration + "No active events…" + `Create event` CTA.

---

## Technical Notes

* Persisted via the event repository's create method; budget stored as integer cents like all `Amount` values in this codebase.

---

## Dependencies

* **Story/Task:** [US-EVT-2](US-EVT-2.md), [US-EVT-4](US-EVT-4.md), [US-LOG-5](../logging/US-LOG-5.md).

---

## Definition of Done

* [ ] Acceptance criteria met
* [ ] Code reviewed
* [ ] Unit tests completed
* [ ] Screenshot (Roborazzi) test for the create-sheet and empty states passes
* [ ] Documentation updated
* [ ] Product Owner accepted

---

## Notes

Phase 2 per the PRD roadmap, but the screen exists in this build, so it's documented here for
completeness.

* **Gap fix (2026-07):** the start/end date pills were wired to no-ops (`onPickStart = {}`,
  `onPickEnd = {}`), so the create form's dates were permanently fixed to today regardless of what
  the labels showed. `EventCreateFormState` now carries real `startEpochMillis`/`endEpochMillis`,
  `EventsFlow` opens the shared `DateTimePickerSheet` for each pill and updates both the epoch value
  and its display label, and `CreateEventUseCase` accepts optional `startEpochMillis`/`endEpochMillis`
  (defaulting to now, rejecting end &lt; start) instead of always hardcoding both to "now." Covered by
  `CreateEventUseCaseTest.invoke_usesProvidedStartAndEndDatesWhenGiven` /
  `invoke_returnsFalseWhenEndBeforeStart`.
