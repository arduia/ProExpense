# User Story

> **ID:** US-SHC-4 · **Service:** `feature:sharedcost` · **Screen:** 10 Shared Costs
> **Priority:** 🔴 Must · **Status:** ✅ Implemented · **Persona:** 👫 Aiko (Cost Sharer)

## Title

> Keep shared costs out of my personal journal

---

## User Story

**As** Aiko 👫
**I want to** have the total recorded as a single expense and the splits kept as reference only
**So that** my personal journal isn't polluted with one row per person

---

## Description

### Background

A shared bill is fundamentally one real expense from the user's point of view — only the total
matters for their own spending history. The per-person breakdown is useful context but must never
appear as separate Journal rows, or the journal would massively over-count actual spend.

### Scope

**In Scope**

* Saving the original total as a single `FinanceRecord`.
* Splits stored as reference data, visible only in Shared Costs history.

**Out of Scope**

* Equal/custom split math — covered by [US-SHC-1](US-SHC-1.md), [US-SHC-2](US-SHC-2.md).

---

## Acceptance Criteria

### Scenario 1 — Total is the stored expense

**Given**

* A split.

**When**

* I `Save`.

**Then**

* The original total is stored as the expense and the splits are reference only — the total is the source of truth.

### Scenario 2 — Visibility scoped to Shared Costs

**Given**

* A saved split.

**When**

* I browse the app.

**Then**

* It appears in Shared Costs history only — never as additional rows in Journal.

---

## Functional Requirements

* [ ] Saving a split writes exactly one `FinanceRecord` for the total amount.
* [ ] Per-person shares are stored as split metadata, not as separate expense records.
* [ ] Journal never shows per-person split rows — only the single total expense (if it would otherwise appear there at all).

---

## Non-Functional Requirements

* [ ] **Reliability** — Journal's total spend figure is unaffected by the number of people in a split.

---

## Business Rules

* A shared cost is exactly one expense record; the split is presentation/reference data layered on top.

---

## UI / UX Notes

* **Design / Mockup:** [`10-shared-costs.md`](../../../design-system-spec/screens/10-shared-costs.md).

---

## Technical Notes

* Splits are persisted alongside (not instead of) the single `FinanceRecord`, queried only by the Shared Costs history screen.

---

## Dependencies

* **Story/Task:** [US-SHC-1](US-SHC-1.md), [US-SHC-5](US-SHC-5.md), [US-HIS-1](../history/US-HIS-1.md) (Journal totals must stay accurate).

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
