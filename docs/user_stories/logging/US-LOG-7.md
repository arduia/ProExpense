# User Story

> **ID:** US-LOG-7 · **Service:** `feature:logging` · **Screen:** 04 Add Expense
> **Priority:** 🟡 Should · **Status:** ✅ Implemented · **Persona:** any user

## Title

> Never lose a half-typed entry

---

## User Story

**As** any user
**I want to** have my in-progress entry restored after a crash or force-close
**So that** I don't have to retype it

---

## Description

### Background

Quick logging happens in distracted, on-the-go moments — a call interrupts, the OS kills the app,
the phone dies. Auto-saving the in-progress draft and offering it back on next launch (before any
PIN gate) protects that effort without requiring the user to do anything extra.

### Scope

**In Scope**

* Auto-save of the in-progress Add-Expense draft on force-close.
* `Continue / Discard` restore prompt on next launch, shown before PIN Entry.

**Out of Scope**

* Splash routing order in general — covered by [US-ONB-5](../onboarding/US-ONB-5.md).

---

## Acceptance Criteria

### Scenario 1 — Draft auto-saves on force-close

**Given**

* I am mid-entry on Add Expense.

**When**

* The app force-closes.

**Then**

* The draft is auto-saved locally.

### Scenario 2 — Restore prompt on relaunch

**Given**

* A saved draft exists.

**When**

* I relaunch the app before any PIN check.

**Then**

* A `Continue / Discard` prompt is shown — no authentication is required to see or act on it.

---

## Functional Requirements

* [ ] In-progress Add-Expense state is persisted as a draft whenever the process may be killed.
* [ ] On next launch, a pending draft triggers a `Continue / Discard` prompt before any PIN gate.
* [ ] `Continue` resumes Add Expense with the draft's values; `Discard` clears the draft and proceeds normally.

---

## Non-Functional Requirements

* [ ] **Reliability** — draft persistence survives full process death, not just backgrounding.
* [ ] **Security** — the draft prompt exposes nothing beyond what the user already typed (no other stored records).

---

## Business Rules

* The restore prompt is never gated behind PIN — it's the user's own unsaved input, not stored financial history (see [US-ONB-5](../onboarding/US-ONB-5.md)).
* Discarding or completing the draft clears it so it never resurfaces.

---

## UI / UX Notes

* **Design / Mockup:** [`04-add-expense.md`](../../../design-system-spec/screens/04-add-expense.md) → "Restore draft".
* **User Flow:** Splash → draft prompt (if any) → `Continue` (Add Expense resumes) / `Discard` (cleared) → PIN Entry/Home.

---

## Dependencies

* **Story/Task:** [US-ONB-5](../onboarding/US-ONB-5.md) (launch routing order).

---

## Definition of Done

* [ ] Acceptance criteria met
* [ ] Code reviewed
* [ ] Unit tests completed
* [ ] Documentation updated
* [ ] Product Owner accepted

---

## Notes

Back from Amount with no value navigates away silently — no save, no prompt — since an empty
Amount screen never produces a meaningful draft to protect.
