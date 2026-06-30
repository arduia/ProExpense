# User Story

> **ID:** US-AUTH-3 · **Service:** `feature:auth` · **Screen:** 14 PIN Setup
> **Priority:** 🟡 Should · **Status:** ✅ Implemented · **Persona:** any user

## Title

> Set a required recovery question

---

## User Story

**As** any user
**I want to** set a security question when I enable my PIN
**So that** I have a way back into my data if I forget my PIN

---

## Description

### Background

There is no account, email, or server-side recovery in this product — the PIN is local-only by
design. Without a recovery mechanism, a forgotten PIN would mean total data loss. Making the
security question mandatory at setup time (not optional, not deferrable) ensures every PIN user has
a recovery path before they need it.

### Scope

**In Scope**

* Mandatory security question (from a predefined list) + answer, captured during PIN setup.
* Setup cannot complete without it.

**Out of Scope**

* The recovery flow itself — covered by [US-AUTH-8](US-AUTH-8.md).

---

## Acceptance Criteria

### Scenario 1 — Mandatory at setup

**Given**

* I am enabling a PIN.

**When**

* I complete setup.

**Then**

* I must pick a security question from a predefined list and provide an answer — the PIN cannot be
  enabled without it.

---

## Functional Requirements

* [ ] PIN setup presents a predefined list of security questions to choose from.
* [ ] An answer is required for the selected question.
* [ ] PIN setup cannot complete (PIN is not enabled) until both question and answer are provided.

---

## Non-Functional Requirements

* [ ] **Security** — the recovery answer is stored using the same local-only protection standard
  as the PIN itself (never sent off-device).

---

## Business Rules

* Security question + answer is mandatory, not optional, for any user enabling a PIN.

---

## UI / UX Notes

* **Design / Mockup:** [`14-pin-setup.md`](../../../design-system-spec/screens/14-pin-setup.md) → security question step.

---

## Dependencies

* **Story/Task:** [US-AUTH-1](US-AUTH-1.md), [US-AUTH-8](US-AUTH-8.md).

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
