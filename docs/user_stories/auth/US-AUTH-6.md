# User Story

> **ID:** US-AUTH-6 · **Service:** `feature:auth` · **Screen:** 15 PIN Entry
> **Priority:** 🔵 Phase 2 · **Status:** ✅ Implemented · **Persona:** ✈️ Carlos (Frequent Traveler)

## Title

> Unlock with biometrics

---

## User Story

**As** Carlos ✈️
**I want to** unlock with Face ID or fingerprint
**So that** I can open the app faster while traveling, without typing a PIN every time

---

## Description

### Background

Typing a 6-digit PIN dozens of times a day is friction for a frequent logger. Biometric unlock
removes that friction, but it must never become a substitute for having a PIN at all — biometric
auth is layered on top of, and dependent on, a PIN already being set.

### Scope

**In Scope**

* Biometric enable, gated on PIN already being set.
* Auto-prompt at the lock screen with fallback to PIN.

**Out of Scope**

* PIN setup itself — covered by [US-AUTH-1](US-AUTH-1.md).

---

## Acceptance Criteria

### Scenario 1 — Enabling requires PIN first

**Given**

* Biometric unlock is offered.

**When**

* I try to enable it.

**Then**

* Enabling requires a PIN to already be set.

### Scenario 2 — Auto-prompt with fallback

**Given**

* Biometric is enabled.

**When**

* The lock screen appears.

**Then**

* It auto-prompts for biometric; success goes to Home, failure falls back to PIN entry.

### Scenario 3 — Blocked without PIN

**Given**

* PIN protection is off.

**When**

* I tap the Biometric option.

**Then**

* "Please enable PIN first to use biometric authentication." is shown.

---

## Functional Requirements

* [ ] Biometric enable control is disabled/blocked while PIN is off.
* [ ] Lock screen auto-triggers the biometric prompt when biometric is enabled.
* [ ] Biometric failure or cancellation falls back to standard PIN entry, never to a dead end.
* [ ] Disabling PIN ([US-AUTH-7](US-AUTH-7.md)) also disables biometric.

---

## Non-Functional Requirements

* [ ] **Security** — biometric unlock never replaces the underlying PIN; it is always an
  additional, revocable unlock path.

---

## Business Rules

* Biometric requires an active PIN as a precondition; it cannot exist independently.

---

## UI / UX Notes

* **Design / Mockup:** [`15-pin-entry.md`](../../../design-system-spec/screens/15-pin-entry.md) → biometric prompt.
* **Error Messages:** "Please enable PIN first to use biometric authentication."

---

## Dependencies

* **Story/Task:** [US-AUTH-1](US-AUTH-1.md), [US-AUTH-4](US-AUTH-4.md), [US-AUTH-7](US-AUTH-7.md).

---

## Definition of Done

* [ ] Acceptance criteria met
* [ ] Code reviewed
* [ ] Unit tests completed
* [ ] Documentation updated
* [ ] Product Owner accepted

---

## Notes

* **Gap fix (2026-07):** Scenario 2 (auto-prompt) was unimplemented — `PinLockFlow` required the
  user to tap into biometric manually even when enabled. Added a `LaunchedEffect` in
  `PinLockFlow.kt` keyed on `(canUseBiometric, lockoutUntil, step)` that triggers `startBiometric()`
  automatically once per lock-screen visit when biometric is available, no lockout is active, and
  the screen is on the entry step; a `hasAutoPromptedBiometric` flag prevents re-firing on
  recomposition. Falls back to PIN entry unchanged on cancel/failure.
