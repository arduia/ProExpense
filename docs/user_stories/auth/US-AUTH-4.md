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
* Re-lock on every app resume after background (default behavior).
* Correct-PIN and incorrect-PIN feedback.
* Opt-in Settings toggle ("Stay unlocked while switching apps") that keeps the app unlocked across
  an app-switch instead of re-locking on every background/foreground — off by default, so existing
  installs keep today's always-re-lock behavior unless the user explicitly opts in.
* Manual "Lock now" action in Settings, available whenever PIN is on, to force an immediate re-lock
  regardless of the toggle above.

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

### Scenario 5 — Stay unlocked during app-switch (setting on)

**Given**

* PIN is on and "Stay unlocked while switching apps" is enabled in Settings.

**When**

* The app is sent to background (app-switch, screen off) and resumed.

**Then**

* The app opens straight to its last screen — no PIN prompt. A real process restart (the app
  fully killed and relaunched) still requires the PIN; this setting only skips the prompt across
  an app-switch within the same running process/session.

### Scenario 6 — Manual lock

**Given**

* PIN is on (regardless of the "Stay unlocked while switching apps" setting).

**When**

* I tap "Lock now" in Settings.

**Then**

* The app immediately re-locks — the next screen shown is the PIN entry screen, exactly as if the
  app had just resumed from background under the default (always re-lock) behavior.

---

## Functional Requirements

* [ ] Lock screen has no back-navigation escape.
* [ ] Correct PIN entry routes directly to Home.
* [ ] Incorrect PIN entry shows danger-outline dots, a shake animation, and the retry message.
* [ ] App re-locks on every `ON_STOP` lifecycle event, not only on process cold start — unless the
  "Stay unlocked while switching apps" setting is on.
* [ ] "Stay unlocked while switching apps" defaults to off; toggling it on suppresses the
  `ON_STOP`-triggered re-lock but never suppresses the cold-launch/process-restart PIN gate.
* [ ] "Lock now" is available in Settings whenever PIN is configured and immediately re-locks the
  app on tap, regardless of the toggle's state.

---

## Non-Functional Requirements

* [ ] **Security** — the lock gate cannot be bypassed by backgrounding/foregrounding the app or by
  system back navigation, except via the explicit opt-in "Stay unlocked while switching apps"
  setting — which never weakens the cold-launch/process-restart PIN gate.

---

## Business Rules

* By default, the app must always re-prompt for PIN after being backgrounded, regardless of
  elapsed time.
* When "Stay unlocked while switching apps" is enabled, an app-switch (background → foreground)
  does not re-prompt for PIN; only a real process restart or the manual "Lock now" action does.

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
