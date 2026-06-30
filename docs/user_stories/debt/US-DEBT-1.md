# User Story

> **ID:** US-DEBT-1 · **Service:** `feature:debt` · **Screen:** 09 Debt Tracker
> **Priority:** 🔵 Phase 2 · **Status:** ✅ Implemented · **Persona:** 🎓 Maya (Student)

## Title

> Switch between lent and owed

---

## User Story

**As** Maya 🎓
**I want to** toggle between "I Lent" and "I Owe"
**So that** I can see each side of my debts separately

---

## Description

### Background

A debt list mixing money owed to me with money I owe is confusing at a glance. A simple toggle
splits the Debt Tracker into two clean views, each with its own `+` shortcut pre-set to the
correct side so the user never has to manually pick the type when adding.

### Scope

**In Scope**

* "I Lent" / "I Owe" toggle.
* `+` pre-sets Add Record to the current side.
* Active-on-top, Settled-below ordering within each side.

**Out of Scope**

* Adding a record's fields in depth — covered by [US-DEBT-2](US-DEBT-2.md).

---

## Acceptance Criteria

### Scenario 1 — Toggle switches the list and add-shortcut

**Given**

* I am on Debt Tracker.

**When**

* I switch the toggle.

**Then**

* The list view switches and `+` opens Add Record pre-set to the current side.

### Scenario 2 — Active vs. settled ordering

**Given**

* I have records.

**When**

* I view the list.

**Then**

* Active records are on top (colored by type) and Settled records are below (greyed).

---

## Functional Requirements

* [ ] Toggle has exactly two states: "I Lent" and "I Owe".
* [ ] `+` always pre-sets the new record's type to the currently selected toggle side.
* [ ] Within a side, Active records sort above Settled records.

---

## Non-Functional Requirements

* [ ] **Accessibility** — toggle and list items meet the 48dp minimum touch target.

---

## Business Rules

* Each debt record has exactly one type: Lent or Owed, fixed at creation by which side was active.

---

## UI / UX Notes

* **Design / Mockup:** [`09-debt-tracker.md`](../../../design-system-spec/screens/09-debt-tracker.md).

---

## Dependencies

* **Story/Task:** [US-DEBT-2](US-DEBT-2.md), [US-DEBT-3](US-DEBT-3.md).

---

## Definition of Done

* [ ] Acceptance criteria met
* [ ] Code reviewed
* [ ] Unit tests completed
* [ ] Screenshot (Roborazzi) test for both toggle states passes
* [ ] Documentation updated
* [ ] Product Owner accepted

---

## Notes

Phase 2 per the PRD roadmap, but the screen exists in this build, so it's documented here for
completeness.
