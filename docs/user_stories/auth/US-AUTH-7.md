# User Story

> **ID:** US-AUTH-7 · **Service:** `feature:auth` · **Screen:** 14 PIN Setup
> **Priority:** 🟡 Should · **Status:** ✅ Implemented · **Persona:** any user

## Title

> Change or disable my PIN

---

## User Story

**As** any user
**I want to** change or turn off my PIN
**So that** I stay in control of my own security settings over time

---

## Description

### Background

Security preferences change — a user might move to a private device and no longer want the
friction of a PIN, or might want to rotate their PIN periodically. Both paths require proving
ownership with the current PIN first, so a PIN can't be silently changed or removed by someone
else holding the unlocked phone.

### Scope

**In Scope**

* Change PIN: verify current → enter new → confirm new.
* Disable PIN: verify current PIN, then turn off PIN and biometric together.

**Out of Scope**

* Forgotten-PIN recovery — covered by [US-AUTH-8](US-AUTH-8.md).

---

## Acceptance Criteria

### Scenario 1 — Changing the PIN

**Given**

* I want to change my PIN.

**When**

* I proceed through the flow.

**Then**

* I verify my current PIN, then enter a new PIN, then confirm it, with the same mismatch handling
  as initial setup ([US-AUTH-1](US-AUTH-1.md)).

### Scenario 2 — Disabling the PIN

**Given**

* I want to disable my PIN.

**When**

* I confirm with my current PIN.

**Then**

* PIN protection is turned off, and biometric unlock is turned off along with it.

---

## Functional Requirements

* [ ] Change-PIN flow requires correct current-PIN verification before accepting a new PIN.
* [ ] New PIN + confirm mismatch handling matches [US-AUTH-1](US-AUTH-1.md) (shake, message, retry without re-entering the new PIN from scratch).
* [ ] Disable-PIN requires correct current-PIN verification.
* [ ] Disabling PIN also disables biometric unlock, since biometric cannot exist without an active PIN.

---

## Non-Functional Requirements

* [ ] **Security** — neither change nor disable is possible without successfully verifying the
  current PIN first.

---

## Business Rules

* Disabling PIN always disables biometric in the same action.

---

## UI / UX Notes

* **Design / Mockup:** [`14-pin-setup.md`](../../../design-system-spec/screens/14-pin-setup.md) → change/disable.

---

## Dependencies

* **Story/Task:** [US-AUTH-1](US-AUTH-1.md), [US-AUTH-6](US-AUTH-6.md).

---

## Definition of Done

* [ ] Acceptance criteria met
* [ ] Code reviewed
* [ ] Unit tests completed
* [ ] Documentation updated
* [ ] Product Owner accepted

---

## Notes

None.
