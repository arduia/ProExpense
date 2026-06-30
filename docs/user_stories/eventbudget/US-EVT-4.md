# User Story

> **ID:** US-EVT-4 · **Service:** `feature:eventbudget` · **Screen:** 08 Event Detail
> **Priority:** 🔵 Phase 2 · **Status:** ✅ Implemented · **Persona:** 🎉 James (Event Organizer)

## Title

> Drill into an event

---

## User Story

**As** James 🎉
**I want to** open an event's full detail with all linked expenses
**So that** I can see exactly where the money went

---

## Description

### Background

The Event Budget list view is a summary; Event Detail is where the actual spend breakdown lives —
every `@`-tagged expense, the running budget summary, and a shortcut to add a new expense that's
automatically pre-tagged to this event.

### Scope

**In Scope**

* Event Detail header: name, date range, budget/spent/remaining, progress bar.
* List of all `@`-tagged entries for the event.
* Add-expense shortcut pre-tagging this event.

**Out of Scope**

* Editing the event itself — covered by [US-EVT-5](US-EVT-5.md).

---

## Acceptance Criteria

### Scenario 1 — Header summary

**Given**

* I open Event Detail.

**When**

* The header renders.

**Then**

* It shows name, date range, budget summary (total / spent / remaining), and a progress bar.

### Scenario 2 — Linked expenses list

**Given**

* I am on Event Detail.

**When**

* I view the body.

**Then**

* All `@`-tagged entries are listed, plus an Add-expense shortcut that pre-tags this event.

### Scenario 3 — Live recalculation

**Given**

* A linked expense is deleted from Journal.

**When**

* I view the event.

**Then**

* Remaining recalculates immediately.

---

## Functional Requirements

* [ ] Header shows total, spent (sum of linked entries), and remaining (total − spent).
* [ ] Body lists every entry currently `@`-tagged to this event.
* [ ] The Add-expense shortcut opens Add Expense with this event pre-selected as the `@` tag.
* [ ] Deleting/editing a linked entry from Journal recalculates this event's totals immediately.

---

## Non-Functional Requirements

* [ ] **Reliability** — totals always reflect the live set of linked entries, never a stale snapshot.

---

## Business Rules

* Spent = sum of all entries currently linked to this event via `@` tag.

---

## UI / UX Notes

* **Design / Mockup:** [`08-event-detail.md`](../../../design-system-spec/screens/08-event-detail.md).
* **User Flow:** Event Budget list → tap event → Event Detail → Add-expense shortcut → Add Expense (pre-tagged).

---

## Dependencies

* **Story/Task:** [US-EVT-1](US-EVT-1.md), [US-LOG-5](../logging/US-LOG-5.md) (`@` tag linking), [US-HIS-7](../history/US-HIS-7.md) (delete recalculation).

---

## Definition of Done

* [ ] Acceptance criteria met
* [ ] Code reviewed
* [ ] Unit tests completed
* [ ] Screenshot (Roborazzi) test for Event Detail passes
* [ ] Documentation updated
* [ ] Product Owner accepted

---

## Notes

Phase 2 per the PRD roadmap, but the screen exists in this build, so it's documented here for
completeness.
