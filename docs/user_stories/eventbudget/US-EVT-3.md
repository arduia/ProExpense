# User Story

> **ID:** US-EVT-3 · **Service:** `feature:eventbudget` · **Screen:** 07 Event Budget · 08 Event Detail
> **Priority:** 🔵 Phase 2 · **Status:** ✅ Implemented · **Persona:** ✈️ Carlos (Traveler)

## Title

> See when I'm over budget

---

## User Story

**As** Carlos ✈️
**I want to** get a clear over-budget signal
**So that** I can rein in spending before it gets worse

---

## Description

### Background

A progress bar alone doesn't communicate urgency well past 100%. Three visual tiers — normal,
warning, and danger — give an escalating signal as spend approaches and exceeds the budget, with
explicit copy stating the overage amount and percentage.

### Scope

**In Scope**

* Three-tier progress bar coloring (0–100% / 101–110% / >110%).
* Over-budget copy and chip on the event card.

**Out of Scope**

* Budget/event creation — covered by [US-EVT-1](US-EVT-1.md).

---

## Acceptance Criteria

### Scenario 1 — Tiered progress bar

**Given**

* Spend against budget.

**When**

* The progress bar renders.

**Then**

* 0–100% is soft blue, 101–110% is amber with "Over budget by $X (Y%)", and >110% is soft red with a bold warning.

### Scenario 2 — Over-budget card state

**Given**

* An over-budget event.

**When**

* I view its card.

**Then**

* The bar is red, an "Over budget" chip is shown, and the remaining figure flips negative.

---

## Functional Requirements

* [ ] Progress bar color and copy are computed purely from spend ÷ budget, with the three documented thresholds.
* [ ] Remaining displays as a negative figure once spend exceeds budget.
* [ ] An "Over budget" chip renders on the card once the >100% threshold is crossed.

---

## Non-Functional Requirements

* [ ] **Accessibility** — color-coded states are paired with text (chip/copy), not color alone.

---

## Business Rules

* Thresholds: 0–100% normal, 101–110% warning, >110% danger.

---

## UI / UX Notes

* **Design / Mockup:** [`07-event-budget.md`](../../../design-system-spec/screens/07-event-budget.md), [`08-event-detail.md`](../../../design-system-spec/screens/08-event-detail.md) → over-budget states.
* **Error Messages:** "Over budget by $X (Y%)".

---

## Dependencies

* **Story/Task:** [US-EVT-1](US-EVT-1.md), [US-EVT-4](US-EVT-4.md).

---

## Definition of Done

* [ ] Acceptance criteria met
* [ ] Code reviewed
* [ ] Unit tests completed
* [ ] Screenshot (Roborazzi) test for warning/danger tiers passes
* [ ] Documentation updated
* [ ] Product Owner accepted

---

## Notes

Phase 2 per the PRD roadmap, but the screen exists in this build, so it's documented here for
completeness.
