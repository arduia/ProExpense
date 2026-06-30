# User Story

> **ID:** US-AUTH-5 · **Service:** `feature:auth` · **Screen:** 15 PIN Entry
> **Priority:** 🟡 Should · **Status:** ✅ Implemented · **Persona:** any security-conscious user

## Title

> Be protected against brute force

---

## User Story

**As** a security-conscious user
**I want to** be locked out after repeated PIN failures
**So that** my 6-digit PIN can't simply be guessed by trial and error

---

## Description

### Background

A 6-digit numeric PIN has a small enough keyspace that unlimited guessing is a real threat on a
lost or shared device. A short, fixed lockout after 5 failures raises the cost of brute-forcing
without being punitive to a user who just fat-fingered their own PIN.

### Scope

**In Scope**

* Lockout trigger at 5 consecutive incorrect attempts.
* 30-second countdown with disabled keypad.
* Attempt counter reset after the lockout completes.

**Out of Scope**

* The underlying unlock flow — covered by [US-AUTH-4](US-AUTH-4.md).
* Recovery-question lockout — covered by [US-AUTH-8](US-AUTH-8.md), same pattern.

---

## Acceptance Criteria

### Scenario 1 — Lockout after 5 failures

**Given**

* 5 incorrect PIN attempts in a row.

**When**

* The 5th attempt fails.

**Then**

* The app locks for 30 seconds with a visible countdown, the keypad is disabled for that duration,
  and the attempt counter resets once the countdown completes.

---

## Functional Requirements

* [ ] Incorrect-attempt counter increments on each failed PIN entry.
* [ ] On the 5th consecutive failure, the keypad is disabled and a 30-second countdown starts.
* [ ] The attempt counter resets to zero once the countdown completes.
* [ ] A correct entry before the 5th failure resets the counter immediately.

---

## Non-Functional Requirements

* [ ] **Security** — lockout state and countdown survive process death (e.g. the app being killed
  during the countdown) so it cannot be bypassed by force-closing the app.

---

## Business Rules

* Lockout threshold: 5 consecutive incorrect attempts → 30-second lockout.

---

## UI / UX Notes

* **Design / Mockup:** [`15-pin-entry.md`](../../../design-system-spec/screens/15-pin-entry.md) → lockout state.

---

## Dependencies

* **Story/Task:** [US-AUTH-4](US-AUTH-4.md), [US-AUTH-8](US-AUTH-8.md) (same lockout family).

---

## Definition of Done

* [ ] Acceptance criteria met
* [ ] Code reviewed
* [ ] Unit tests completed
* [ ] Screenshot (Roborazzi) test for the lockout/countdown state passes
* [ ] Documentation updated
* [ ] Product Owner accepted

---

## Notes

None.
