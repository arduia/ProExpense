# User Story

> **ID:** US-EVT-2 · **Service:** `feature:eventbudget` · **Screen:** 07 Event Budget
> **Priority:** 🔵 Phase 2 · **Status:** ✅ Implemented · **Persona:** 🎉 James (Event Organizer)

## Title

> Track multiple events at once

---

## User Story

**As** James 🎉
**I want to** run several events simultaneously
**So that** I can track e.g. a trip and a wedding in parallel without them interfering

---

## Description

### Background

Real life doesn't run one event at a time — a user might be tracking a trip budget while also
planning a party. Each event card shows its own live progress independently; when more than one is
active, Home's contextual header picks the most recently created one to avoid ambiguity.

### Scope

**In Scope**

* Multiple concurrently active events.
* Per-event card: name, date range, live remaining balance, mini progress bar.
* Home header tie-breaking rule (most recently created).

**Out of Scope**

* Creating an event — covered by [US-EVT-1](US-EVT-1.md).

---

## Acceptance Criteria

### Scenario 1 — Multiple active events

**Given**

* I create more than one event.

**When**

* They are active.

**Then**

* Multiple events can run at once, each tracked independently.

### Scenario 2 — Card contents

**Given**

* An active event.

**When**

* I view its card.

**Then**

* It shows name, date range, live remaining balance, and a mini progress bar.

### Scenario 3 — Home header tie-break

**Given**

* Overlapping active events.

**When**

* I view Home.

**Then**

* The header shows the most recently created one.

---

## Functional Requirements

* [ ] No limit on the number of concurrently active events (beyond reasonable device performance).
* [ ] Each event card independently computes remaining = budget − linked spend.
* [ ] Home's Active Event header selects the most recently created active event when more than one exists.

---

## Non-Functional Requirements

* [ ] **Performance** — multiple active event cards render and update without visible lag.

---

## Business Rules

* "Most recently created" is the deterministic tie-break for Home's single-event header slot.

---

## UI / UX Notes

* **Design / Mockup:** [`07-event-budget.md`](../../../design-system-spec/screens/07-event-budget.md), [`03-home.md`](../../../design-system-spec/screens/03-home.md) → "Active Event card".

---

## Dependencies

* **Story/Task:** [US-EVT-1](US-EVT-1.md), [US-HOME-1](../app-shell/US-HOME-1.md) (Home header).

---

## Definition of Done

* [ ] Acceptance criteria met
* [ ] Code reviewed
* [ ] Unit tests completed
* [ ] Screenshot (Roborazzi) test for multiple active event cards passes
* [ ] Documentation updated
* [ ] Product Owner accepted

---

## Notes

Phase 2 per the PRD roadmap, but the screen exists in this build, so it's documented here for
completeness.
