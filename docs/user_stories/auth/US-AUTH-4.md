# User Story

> **ID:** US-AUTH-4 · **Service:** `feature:auth` · **Screen:** 15 PIN Entry
> **Priority:** 🟡 Should · **Status:** ✅ Implemented · **Persona:** any user with PIN on

## Title

> Unlock the app

---

## User Story

**As** any user with PIN protection on
**I want to** enter my PIN on launch and on resume
**So that** only I can open the app, even if my phone is unlocked elsewhere

---

## Description

### Background

A PIN that only checks at cold launch is a weak guarantee — leaving the app in the background on a
shared device would leave it wide open. Re-locking on every backgrounding event (`ON_STOP`) is what
makes the privacy promise credible.

### Scope

**In Scope**

* Full-screen, no-back-navigation lock entry on launch.
* Re-lock on every app resume after background.
* Correct-PIN and incorrect-PIN feedback.

**Out of Scope**

* Lockout after repeated failures — covered by [US-AUTH-5](US-AUTH-5.md).
* Biometric shortcut — covered by [US-AUTH-6](US-AUTH-6.md).

---

## Acceptance Criteria

### Scenario 1 — Lock screen layout

**Given**

* The lock screen.

**When**

* I enter digits.

**Then**

* Six dot indicators fill as I type, a full-screen numeric keypad is shown, and there is no back
  navigation off this screen.

### Scenario 2 — Correct PIN

**Given**

* I enter the correct PIN.

**When**

* It is accepted.

**Then**

* I am taken to Home immediately.

### Scenario 3 — Incorrect PIN

**Given**

* I enter an incorrect PIN.

**When**

* It is rejected.

**Then**

* The dots show a danger outline, shake, and "Incorrect PIN, try again" is shown.

### Scenario 4 — Re-lock on resume

**Given**

* The app is sent to background.

**When**

* I resume it.

**Then**

* The PIN is required again — the app re-locks on every stop, not just cold launch.

---

## Functional Requirements

* [ ] Lock screen has no back-navigation escape.
* [ ] Correct PIN entry routes directly to Home.
* [ ] Incorrect PIN entry shows danger-outline dots, a shake animation, and the retry message.
* [ ] App re-locks on every `ON_STOP` lifecycle event, not only on process cold start.

---

## Non-Functional Requirements

* [ ] **Security** — the lock gate cannot be bypassed by backgrounding/foregrounding the app or by
  system back navigation.

---

## Business Rules

* The app must always re-prompt for PIN after being backgrounded, regardless of elapsed time.

---

## UI / UX Notes

* **Design / Mockup:** [`15-pin-entry.md`](../../../design-system-spec/screens/15-pin-entry.md).
* **Error Messages:** "Incorrect PIN, try again."

---

## Dependencies

* **Story/Task:** [US-AUTH-1](US-AUTH-1.md), [US-AUTH-5](US-AUTH-5.md), [US-AUTH-6](US-AUTH-6.md).

---

## Definition of Done

* [ ] Acceptance criteria met
* [ ] Code reviewed
* [ ] Unit tests completed
* [ ] Screenshot (Roborazzi) test for correct/incorrect PIN states passes
* [ ] Documentation updated
* [ ] Product Owner accepted

---

## Notes

None.
