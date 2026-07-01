# User Story

> **ID:** US-EVT-5 · **Service:** `feature:eventbudget` · **Screen:** 08 Event Detail
> **Priority:** 🔵 Phase 2 · **Status:** ✅ Implemented · **Persona:** 🎉 James (Event Organizer)

## Title

> Edit and close an event

---

## User Story

**As** James 🎉
**I want to** edit and manually close an event
**So that** I control its lifecycle instead of the app guessing when it's "done"

---

## Description

### Background

Plans change — budgets get revised, dates shift. And because an end date passing doesn't mean
spending has stopped (receipts trickle in after a trip ends), closing is always a deliberate user
action, with a short grace period after closing where the event stays editable before locking.

### Scope

**In Scope**

* Edit sheet: name, dates, budget (must stay > $0).
* Manual close action; end date is reference only (no auto-close).
* Lifecycle states: Active, Closed < 24h (grace period), Closed > 24h (read-only).

**Out of Scope**

* Initial creation — covered by [US-EVT-1](US-EVT-1.md).

---

## Acceptance Criteria

### Scenario 1 — Editing fields

**Given**

* I open the Edit sheet.

**When**

* I change fields.

**Then**

* Name, dates, and budget are editable (budget must stay > $0).

### Scenario 2 — End date does not auto-close

**Given**

* The end date passes.

**When**

* Time elapses.

**Then**

* The event does not auto-close — closing is a manual action and the end date remains reference only.

### Scenario 3 — Lifecycle states

**Given**

* An event's current state.

**When**

* I act on it.

**Then**

* Active is fully editable/linkable; Closed < 24h is archived but still editable (grace period); Closed > 24h is read-only (fields locked, no edits, no new links).

---

## Functional Requirements

* [ ] Edit sheet allows changing name, start/end date, and budget, with budget re-validated as > $0.
* [ ] Closing an event is an explicit user action — passing the end date never auto-closes it.
* [ ] A closed event remains editable for 24h, then locks permanently (read-only, no new `@` links).

---

## Non-Functional Requirements

* [ ] **Reliability** — the 24h grace-period boundary is computed consistently (no timezone drift).

---

## Business Rules

* Lifecycle: Active → Closed (manual) → Closed (read-only after 24h).
* A read-only closed event accepts no further edits or new expense links.

---

## UI / UX Notes

* **Design / Mockup:** [`08-event-detail.md`](../../../design-system-spec/screens/08-event-detail.md) → "Edit" / "Closed" states.

---

## Dependencies

* **Story/Task:** [US-EVT-1](US-EVT-1.md), [US-EVT-4](US-EVT-4.md).

---

## Definition of Done

* [ ] Acceptance criteria met
* [ ] Code reviewed
* [ ] Unit tests completed
* [ ] Screenshot (Roborazzi) test for Active/Closed-grace/Closed-locked states passes
* [ ] Documentation updated
* [ ] Product Owner accepted

---

## Notes

Phase 2 per the PRD roadmap, but the screen exists in this build, so it's documented here for
completeness.

* **Gap fix (2026-07, partial):** the Event Detail "..." menu (`onMore`) was a no-op with zero UI
  entry point for either action, despite `UpdateEventUseCase`/`CloseEventUseCase` already existing
  and being tested. `EventsFlow` now opens a new `EventActionsSheetContent` (Edit / Close / Cancel)
  from `onMore`; Edit reopens the create sheet prefilled from the real `Event` (via a new
  `Event.toEditFormState()` in `EventBudgetFeatureEntry.kt`) and routes Save through
  `UpdateEventUseCase`; Close shows a confirmation `ProAlertDialog` before calling
  `CloseEventUseCase`. **Not yet implemented:** Scenario 3's 24h grace period — `readOnly` is still
  the pre-existing simple `status == CLOSED` check, so a just-closed event locks immediately instead
  of staying editable for 24h. That nuance needs a `closedAtEpochMillis` timestamp on `Event` plus a
  grace-period calculation and is left as a follow-up; only the binary Active/Closed states are
  covered by this fix.
