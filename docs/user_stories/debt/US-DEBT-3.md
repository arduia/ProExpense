# User Story

> **ID:** US-DEBT-3 · **Service:** `feature:debt` · **Screen:** 09 Debt Tracker
> **Priority:** 🔵 Phase 2 · **Status:** ✅ Implemented · **Persona:** 🎓 Maya (Student)

## Title

> Settle and clean up debts

---

## User Story

**As** Maya 🎓
**I want to** mark a debt settled and later delete it
**So that** my list stays current without losing the linked expense

---

## Description

### Background

A debt's lifecycle is Active → Settled → (optionally) deleted. Deletion is only allowed after
settling, to avoid accidentally erasing a still-open debt. Deleting a settled record removes only
the debt link, never the underlying linked expense.

### Scope

**In Scope**

* Edit & Mark-as-settled actions on an Active record.
* Delete (with confirm) on a Settled record only.

**Out of Scope**

* Adding a new record — covered by [US-DEBT-2](US-DEBT-2.md).

---

## Acceptance Criteria

### Scenario 1 — Active record actions

**Given**

* An Active record.

**When**

* I open its actions.

**Then**

* Edit and Mark-as-settled are offered; it is not deletable (must settle first).

### Scenario 2 — Deleting a settled record

**Given**

* A Settled record.

**When**

* I tap Delete and confirm.

**Then**

* It is removed while any linked expense is kept — only the debt link is removed.

---

## Functional Requirements

* [ ] Active records expose Edit and Mark-as-settled, never Delete.
* [ ] Settled records expose Delete (behind a confirmation dialog).
* [ ] Deleting a debt record never deletes its linked expense — only the link.

---

## Non-Functional Requirements

* [ ] **Reliability** — delete is atomic; a cancelled confirmation leaves the record intact.

---

## Business Rules

* Deletion is gated behind Settled status — an Active debt cannot be deleted directly.

---

## UI / UX Notes

* **Design / Mockup:** [`09-debt-tracker.md`](../../../design-system-spec/screens/09-debt-tracker.md) → "Active" / "Settled" actions.

---

## Dependencies

* **Story/Task:** [US-DEBT-1](US-DEBT-1.md), [US-DEBT-2](US-DEBT-2.md).

---

## Definition of Done

* [ ] Acceptance criteria met
* [ ] Code reviewed
* [ ] Unit tests completed
* [ ] Screenshot (Roborazzi) test for Active/Settled action sheets passes
* [ ] Documentation updated
* [ ] Product Owner accepted

---

## Notes

Phase 2 per the PRD roadmap, but the screen exists in this build, so it's documented here for
completeness.

* **Gap fix (2026-07):** Scenario 1's "Edit" action was wired to a no-op (`onEdit = {}`). Added
  `UpdateDebtUseCase` (same `amount > 0` guard as create) and wired `DebtDetailScreen`'s Edit tap to
  prefill the Add-Record sheet from the tapped record — including converting the persisted
  `amountCents` back to the whole-dollar string the amount field expects
  (`(record.amountCents / 100).toString()`, matching `Amount.parseOrNull`'s cents conversion) — and
  route Save through `UpdateDebtUseCase` instead of creating a duplicate record. Covered by
  `UpdateDebtUseCaseTest`.
