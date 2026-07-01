# User Story

> **ID:** US-AUTH-8 · **Service:** `feature:auth` · **Screen:** 15 PIN Entry
> **Priority:** 🟡 Should · **Status:** ✅ Implemented · **Persona:** 👴 Mr. Chen (Retiree)

## Title

> Recover a forgotten PIN

---

## User Story

**As** Mr. Chen 👴
**I want to** recover access if I forget my PIN
**So that** I'm not permanently locked out of my own data

---

## Description

### Background

Because there is no account or server-side recovery in this offline-first product, the security
question captured at setup ([US-AUTH-3](US-AUTH-3.md)) is the only path back in. It needs the same
brute-force protection as the PIN itself, and a last-resort option must exist for the case where
even recovery fails — otherwise the user's data becomes permanently inaccessible.

### Scope

**In Scope**

* "Forgot PIN" entry point → security question challenge → new PIN set on success.
* Wrong-answer handling and lockout, mirroring PIN lockout.
* Last-resort full data reset when recovery is exhausted.

**Out of Scope**

* Setting the security question itself — covered by [US-AUTH-3](US-AUTH-3.md).

---

## Acceptance Criteria

### Scenario 1 — Successful recovery

**Given**

* I tap Forgot PIN.

**When**

* I answer the security question correctly.

**Then**

* I can set a new PIN.

### Scenario 2 — Wrong answer and lockout

**Given**

* I give a wrong answer.

**When**

* I submit it.

**Then**

* "Try again" is shown; after 5 wrong answers, a 30-second lockout applies, after which attempts
  reset — the same pattern as PIN lockout ([US-AUTH-5](US-AUTH-5.md)).

### Scenario 3 — Last resort

**Given**

* Recovery has failed and I have no other option.

**When**

* I reach the end of the recovery flow.

**Then**

* "Reset app (clear all data)" is offered as a last resort.

---

## Functional Requirements

* [ ] Forgot-PIN entry point challenges the user with their stored security question.
* [ ] Correct answer routes into new-PIN setup.
* [ ] Wrong answer shows "Try again"; 5 consecutive wrong answers trigger a 30-second lockout with
  attempt-counter reset afterward.
* [ ] A full-data-reset option is offered as the final fallback when recovery cannot succeed.

---

## Non-Functional Requirements

* [ ] **Security** — recovery lockout cannot be bypassed by restarting the app mid-lockout.
* [ ] **Data integrity** — the last-resort reset is explicit, irreversible, and requires
  confirmation before wiping data.

---

## Business Rules

* Recovery lockout uses the same threshold/duration as PIN lockout (5 attempts / 30s).
* Full data reset is the only fallback when the security question itself is also forgotten.

---

## UI / UX Notes

* **Design / Mockup:** [`15-pin-entry.md`](../../../design-system-spec/screens/15-pin-entry.md) → recovery flow.
* **Error Messages:** "Try again."

---

## Dependencies

* **Story/Task:** [US-AUTH-3](US-AUTH-3.md), [US-AUTH-5](US-AUTH-5.md).

---

## Definition of Done

* [ ] Acceptance criteria met
* [ ] Code reviewed
* [ ] Unit tests completed
* [ ] Documentation updated
* [ ] Product Owner accepted

---

## Notes

* **Gap fix (2026-07):** Scenario 2's lockout and Scenario 3's last-resort reset were both
  unimplemented — wrong recovery answers had no attempt limit, and there was no reset-app option.
  `PinLockFlow.kt` now shares `pinAuthRepository.incrementFailedAttempts()` /
  `getLockoutUntilMs()` with the standard PIN lockout on each wrong recovery answer, tracks
  `recoveryExhausted`, and shows a "Reset app" `ProTextAction` (danger styling) via
  `PinRecoveryScreen`'s new `showResetOption`/`onResetApp` params once exhausted. Confirming reset
  runs `DisablePinUseCase` + `ClearDataRepository.clearAll()` behind a confirmation `ProAlertDialog`
  before unlocking into a clean app. Covered by the new `PinRecoveryExhaustedPreview`.
