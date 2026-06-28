# User Story

> **ID:** US-MORE-4 · **Service:** `app` (More) · **Screen:** 13 More
> **Priority:** 🔴 Must · **Status:** ✅ Implemented · **Persona:** any user

## Title

> Clear my data deliberately

---

## User Story

**As** any user
**I want to** clear my data selectively and only with explicit confirmation
**So that** I never lose data by accident

---

## Description

### Background

This is a privacy-first, local-only notebook — once data is cleared, it is gone (no cloud copy to
restore from, short of a prior export). Selective wipe lets a user remove, say, only debts without
nuking expenses, and every wipe option requires its own confirmation so a stray tap can't destroy
data.

### Scope

**In Scope**

* Selective wipe (user chooses which data category to remove).
* Mandatory confirmation dialog per wipe option.

**Out of Scope**

* The full-data-reset last-resort flow used during PIN recovery — covered by [US-AUTH-8](../auth/US-AUTH-8.md).

---

## Acceptance Criteria

### Scenario 1 — Selective wipe

**Given**

* I open Clear data.

**When**

* I choose what to wipe.

**Then**

* The wipe is selective — I pick exactly which category of data to remove, not an all-or-nothing
  action.

### Scenario 2 — Confirmed and irreversible

**Given**

* I confirm a wipe.

**When**

* It runs.

**Then**

* Each option requires its own confirmation dialog, and the action is irreversible once confirmed.

---

## Functional Requirements

* [ ] Clear data presents distinct, individually selectable wipe categories.
* [ ] Each category requires a separate confirmation dialog before executing.
* [ ] A confirmed wipe is permanent — there is no undo.

---

## Non-Functional Requirements

* [ ] **Data integrity** — a wipe of one category never touches unrelated categories' data.

---

## Business Rules

* No wipe executes without an explicit confirmation step.

---

## UI / UX Notes

* **Design / Mockup:** [`13-more.md`](../../../design-system-spec/screens/13-more.md) → Clear data.

---

## Dependencies

* **Story/Task:** [US-MORE-1](US-MORE-1.md), [US-AUTH-8](../auth/US-AUTH-8.md) (related but separate full-reset flow).

---

## Definition of Done

* [ ] Acceptance criteria met
* [ ] Code reviewed
* [ ] Unit tests completed
* [ ] Screenshot (Roborazzi) test for the confirmation dialog passes
* [ ] Documentation updated
* [ ] Product Owner accepted

---

## Notes

None.
