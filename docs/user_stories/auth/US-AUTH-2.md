# User Story

> **ID:** US-AUTH-2 · **Service:** `feature:auth` · **Screen:** 14 PIN Setup
> **Priority:** 🟡 Should · **Status:** ✅ Implemented · **Persona:** 👴 Mr. Chen (Retiree)

## Title

> See my digits while creating a PIN

---

## User Story

**As** Mr. Chen 👴
**I want to** reveal the digits while I'm creating my PIN
**So that** I can confirm I typed the right thing before locking myself out of my own data

---

## Description

### Background

Always-hidden PIN dots are good security hygiene at the unlock screen, but during creation they
make typos invisible — and with no account recovery beyond a security question, a typo'd PIN is a
real risk. A reveal toggle, scoped only to the creation flow, lets users like Mr. Chen double-check
without weakening the unlock screen's security posture.

### Scope

**In Scope**

* Eye-icon reveal/hide toggle on the PIN creation (set + confirm) screens.
* Default hidden state on first render.

**Out of Scope**

* Any reveal affordance on the lock/unlock screen — explicitly not supported.

---

## Acceptance Criteria

### Scenario 1 — Toggling reveal during creation

**Given**

* I am creating a PIN (set or confirm step).

**When**

* I tap the eye toggle.

**Then**

* The entered digits reveal or hide accordingly.

### Scenario 2 — Hidden by default

**Given**

* The creation screen.

**When**

* It first renders.

**Then**

* Digits are hidden by default; revealing shows the typed digits in place of the dot indicators.

### Scenario 3 — No toggle on unlock

**Given**

* The unlock/lock screen.

**When**

* I view it.

**Then**

* No reveal toggle exists — this affordance is scoped to PIN creation only.

---

## Functional Requirements

* [ ] PIN creation screens (set, confirm) render an eye/eye-off icon toggle next to the dot row.
* [ ] Default state on render is hidden (dots), regardless of how many digits are already entered.
* [ ] Toggling reveals the actual entered digits in place of dots, and toggling again re-hides them.
* [ ] The lock/unlock screen has no reveal toggle.

---

## Non-Functional Requirements

* [ ] **Accessibility** — the reveal toggle is reachable as a standard icon button with adequate
  touch target size.

---

## Business Rules

* Reveal/hide is available only during PIN creation, never during unlock.

---

## UI / UX Notes

* **Design / Mockup:** [`14-pin-setup.md`](../../../design-system-spec/screens/14-pin-setup.md) → reveal toggle.

---

## Dependencies

* **Story/Task:** [US-AUTH-1](US-AUTH-1.md), [US-AUTH-7](US-AUTH-7.md) (change-PIN reuses the same creation entry).

---

## Definition of Done

* [ ] Acceptance criteria met
* [ ] Code reviewed
* [ ] Unit tests completed
* [ ] Screenshot (Roborazzi) test for revealed vs. hidden states passes
* [ ] Documentation updated
* [ ] Product Owner accepted

---

## Notes

This story tracks a fix shipped after the toggle was found missing during PIN creation entry —
dots were always shown with no way to reveal the typed digits. Out of scope per the original
report: the lock-screen (`PinLockFlow`/`PinEntryScreen`) reveal toggle is intentionally not built.
