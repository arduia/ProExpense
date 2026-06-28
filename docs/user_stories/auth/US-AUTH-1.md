# User Story

> **ID:** US-AUTH-1 · **Service:** `feature:auth` · **Screen:** 14 PIN Setup
> **Priority:** 🟡 Should · **Status:** ✅ Implemented · **Persona:** 🏠 Siti (Homemaker)

## Title

> Protect the app with a PIN

---

## User Story

**As** Siti 🏠
**I want to** set a 6-digit PIN
**So that** my financial data stays private on a device my family shares

---

## Description

### Background

Pro Expense is a personal notebook, but the phone it lives on isn't always personal — shared
family devices are common. A simple 6-digit PIN gives a meaningful privacy boundary without
introducing accounts or server-side auth, which the product explicitly rules out.

### Scope

**In Scope**

* Enabling PIN protection from off.
* 6-digit entry + confirm (must match).
* Mismatch handling and success confirmation messaging.

**Out of Scope**

* Unlocking on launch — covered by [US-AUTH-4](US-AUTH-4.md).
* Changing/disabling an existing PIN — covered by [US-AUTH-7](US-AUTH-7.md).

---

## Acceptance Criteria

### Scenario 1 — Enabling a PIN

**Given**

* PIN protection is off.

**When**

* I toggle it on.

**Then**

* I enter a 6-digit PIN, then confirm it; confirmation must match the original.

### Scenario 2 — Confirm mismatch

**Given**

* The confirm entry does not match.

**When**

* I submit it.

**Then**

* The dots clear and shake (±4dp), "PINs do not match. Try again." is shown, and the originally
  entered PIN is preserved so I don't have to retype it from scratch.

### Scenario 3 — Setup success

**Given**

* The PIN has been set.

**When**

* Setup completes.

**Then**

* "PIN is now active. You'll be asked to enter it on your next launch." is shown.

---

## Functional Requirements

* [ ] PIN is exactly 6 digits.
* [ ] Confirm step must match the first entry exactly, or the flow reports a mismatch and lets the
  user retry the confirm step without losing the original entry.
* [ ] Mismatch triggers a shake animation (±4dp) and clears only the confirm dots.
* [ ] Successful setup shows the activation confirmation message.

---

## Non-Functional Requirements

* [ ] **Security** — the PIN is never logged, displayed in plaintext outside the entry/reveal
  flow, or stored unhashed.

---

## Business Rules

* PIN length is fixed at 6 digits.
* PIN setup is local-only — no server-side auth, consistent with the product's no-accounts
  constraint.

---

## UI / UX Notes

* **Design / Mockup:** [`14-pin-setup.md`](../../../design-system-spec/screens/14-pin-setup.md).
* **Error Messages:** "PINs do not match. Try again."
* **Success Messages:** "PIN is now active. You'll be asked to enter it on your next launch."

---

## Dependencies

* **Story/Task:** [US-AUTH-2](US-AUTH-2.md), [US-AUTH-3](US-AUTH-3.md), [US-AUTH-4](US-AUTH-4.md).

---

## Definition of Done

* [ ] Acceptance criteria met
* [ ] Code reviewed
* [ ] Unit tests completed
* [ ] Screenshot (Roborazzi) test for PIN setup (mismatch + success) passes
* [ ] Documentation updated
* [ ] Product Owner accepted

---

## Notes

None.
