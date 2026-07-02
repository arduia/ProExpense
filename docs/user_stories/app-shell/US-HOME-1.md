# User Story

> **ID:** US-HOME-1 · **Service:** `app` (Home) · **Screen:** 03 Home
> **Priority:** 🔴 Must · **Status:** ✅ Implemented · **Persona:** any persona

## Title

> See a home that fits how I use the app

---

## User Story

**As** any persona
**I want to** see a Home header that reflects how I actually use the app
**So that** the single most relevant number is front and center the moment I open it

---

## Description

### Background

Different personas care about different numbers: a casual logger wants "how much did I spend,"
a budget planner wants "how much do I have left," and an event organizer wants "how's this trip
going." A one-size header would bury the number that matters most to each of them.

### Scope

**In Scope**

* Persona-driven Home header variants (Casual / Budget Planner / Event Organizer).
* Active Event card, shown only while an event is running.

**Out of Scope**

* Recent activity list — covered by [US-HOME-2](US-HOME-2.md).
* Setting the monthly budget that drives the Budget-Planner header — covered by [US-MORE-2](US-MORE-2.md).

---

## Acceptance Criteria

### Scenario 1 — Header reflects usage pattern

**Given**

* My usage pattern.

**When**

* Home renders.

**Then**

* The header switches accordingly: Casual shows total spent this month; Budget Planner shows spent
  vs. budget with a progress indicator; Event Organizer shows the active event's name and remaining
  budget.

### Scenario 2 — Active Event card visibility

**Given**

* An event is currently running.

**When**

* I view Home.

**Then**

* An Active Event card appears; otherwise it is hidden entirely.

---

## Functional Requirements

* [ ] Home header renders exactly one of three variants based on the user's usage pattern: total
  spent this month, budget progress, or active event remaining.
* [ ] Active Event card is rendered only while at least one event is currently active.

---

## Non-Functional Requirements

* [ ] **Usability** — the header variant change is deterministic and doesn't flicker between
  variants on the same session.

---

## Business Rules

* Header variant selection follows the persona/usage classification, not a manual user toggle.

---

## UI / UX Notes

* **Design / Mockup:** [`03-home.md`](../../../design-system-spec/screens/03-home.md).

---

## Dependencies

* **Story/Task:** [US-HOME-2](US-HOME-2.md), [US-MORE-2](US-MORE-2.md), [US-EVT-1](../eventbudget/US-EVT-1.md) (Active Event source).

---

## Definition of Done

* [ ] Acceptance criteria met
* [ ] Code reviewed
* [ ] Unit tests completed
* [ ] Screenshot (Roborazzi) test for each header variant passes
* [ ] Documentation updated
* [ ] Product Owner accepted

---

## Notes

* **Gap fix (2026-07):** Scenario 2's Active Event card had a real `HomeUiState.activeEvent` field
  and rendering, but `ExpenseApp.kt` never populated it from real `EventRepository` data — it was
  always `null` outside of preview fixtures, so the card never appeared in production regardless of
  active events. `ExpenseApp` now picks the most-recently-started `EventStatus.ACTIVE` event, fetches
  its spend via `EventRepository.getSpent`, and reuses `ComputeEventProgressUseCase` (already used by
  `feature:eventbudget`) to build the card. Tapping the card now also navigates to Budget and opens
  that event's detail (`EventsFlow`/`EventBudgetFeatureEntry.EventsTab` gained
  `initialSelectedEventId`), and Event Detail's "Add expense" action now pre-tags the newly logged
  expense to that event instead of opening a blank Quick Log (`onAddTaggedExpense` threaded through
  to `QuickLogFlow`'s new `initialLinkedEventId` param).
