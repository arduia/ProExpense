# User Story

> **ID:** US-DEBT-4 · **Service:** `feature:debt` · **Screen:** 09 Debt Tracker
> **Priority:** 🔵 Phase 2 · **Status:** ✅ Implemented · **Persona:** any user

## Title

> Be warned about conflicting records

---

## User Story

**As** any user
**I want to** be warned when a person already exists on the opposite side
**So that** I don't accidentally double-record the same debt from both directions

---

## Description

### Background

It's easy to forget you already logged "John owes me $20" and later add "I owe John $20" by
mistake (or vice versa). A soft, dismissible warning catches this without hard-blocking a genuinely
valid second record (people can owe each other different things at once).

### Scope

**In Scope**

* Soft warning when adding a person who already exists on the opposite side.
* `Yes` proceeds, `No` dismisses — never a hard block.

**Out of Scope**

* The Add Record form fields themselves — covered by [US-DEBT-2](US-DEBT-2.md).

---

## Acceptance Criteria

### Scenario 1 — Warning on conflicting person

**Given**

* A person already exists as a record on the other side.

**When**

* I add them again.

**Then**

* A soft warning shows: "John already has a record on the other side. Continue?"

### Scenario 2 — Responding to the warning

**Given**

* The warning is shown.

**When**

* I respond.

**Then**

* `Yes` proceeds with saving; `No` dismisses without saving.

---

## Functional Requirements

* [ ] Matching is by person name across the opposite "Lent"/"Owe" side.
* [ ] The warning never blocks saving outright — it's a confirm, not a hard validation error.
* [ ] `Yes` completes the save exactly as if no warning had been shown; `No` returns to the form unsaved.

---

## Non-Functional Requirements

* [ ] **Accessibility** — the warning dialog is screen-reader accessible with clear Yes/No actions.

---

## Business Rules

* The conflict check is informational only — having records on both sides for the same person is a valid, supported state.

---

## UI / UX Notes

* **Design / Mockup:** [`09-debt-tracker.md`](../../../design-system-spec/screens/09-debt-tracker.md) → "Conflict warning".
* **Error Messages:** "John already has a record on the other side. Continue?"

---

## Dependencies

* **Story/Task:** [US-DEBT-1](US-DEBT-1.md), [US-DEBT-2](US-DEBT-2.md).

---

## Definition of Done

* [ ] Acceptance criteria met
* [ ] Code reviewed
* [ ] Unit tests completed
* [ ] Screenshot (Roborazzi) test for the warning dialog passes
* [ ] Documentation updated
* [ ] Product Owner accepted

---

## Notes

Phase 2 per the PRD roadmap, but the screen exists in this build, so it's documented here for
completeness.
