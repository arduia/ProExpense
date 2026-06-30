# User Story

> **ID:** US-LOG-1 · **Service:** `feature:logging` · **Screen:** 04 Add Expense (Amount)
> **Priority:** 🔴 Must · **Status:** ✅ Implemented · **Persona:** 🛒 Amara (Vendor) / any user

## Title

> Log an expense in seconds (one-tap quick commit)

---

## User Story

**As a** busy user logging on the go
**I want to** enter an amount and save in a single move
**So that** recording a transaction takes seconds, not minutes

---

## Description

### Background

Speed is the product's core differentiator — the PRD goal is to "log an expense in under 5 seconds."
The Add Expense **Amount** sub-screen is the fastest path: the keypad is already open, a sensible
default category is pre-selected, and a quick-commit `Save` writes the record without forcing the user
through the Details step.

### Scope

**In Scope**

* Amount sub-screen with auto-opened numeric keypad and large, centered amount.
* Quick-commit `Save` that stores the entry with the default category and returns to Home.
* Success confirmation (toast) after save.

**Out of Scope**

* Setting category / date / note / tag — covered by [US-LOG-3](US-LOG-3.md) (Details).
* Zero / invalid amount handling — covered by [US-LOG-2](US-LOG-2.md).
* Editing an existing record — covered by [US-HIS-6](../history/US-HIS-6.md).

---

## Acceptance Criteria

### Scenario 1 — Open Add Expense ready to type

**Given**

* I am on Home.

**When**

* I tap the raised center `+`.

**Then**

* Add Expense opens on the Amount sub-screen.
* The numeric keypad is already open.
* The amount is large and centered; "Food" is pre-selected in the horizontal category chips.

### Scenario 2 — Quick-commit a valid amount

**Given**

* I have entered an amount greater than $0.

**When**

* I tap `Save` (quick commit).

**Then**

* The entry is stored with the default category and today's date/time.
* I slide back to Home.
* A success toast confirms the save.

---

## Functional Requirements

* [ ] Tapping `+` on Home opens Add Expense → Amount with the keypad open.
* [ ] "Food" (default category) is pre-selected on entry.
* [ ] `Save` is enabled only when amount > $0 and quick-commits with the default category.
* [ ] On save, persist the record locally and return to Home with a success toast.

---

## Non-Functional Requirements

* [ ] **Performance** — full happy path (open → type → save) completes in under 5 seconds.
* [ ] **Reliability** — the save works fully offline (local persistence only).
* [ ] **Accessibility** — keypad keys and `Save` meet the 48dp minimum touch target.

---

## Business Rules

* Quick commit uses the **default category** (Food unless changed in Settings → Default category).
* Date defaults to **today** when committed from the Amount step.
* Amount must be **> $0** to commit (see [US-LOG-2](US-LOG-2.md)).
* Max amount is **999,999,999.99** (whole part ≤ 7 digits, fraction ≤ 2).

---

## UI / UX Notes

* **Design / Mockup:** [`04-add-expense.md`](../../../design-system-spec/screens/04-add-expense.md) → "Amount · typed".
* **User Flow:** Home → `+` → Amount → `Save` → Home (toast).
* **Validation Rules:** single decimal; leading zeros stripped (except "0."); commas grouped live.
* **Empty States:** `Save`/`Next` disabled until amount > $0.

---

## Technical Notes

* Writes a `FinanceRecord` via `FinanceRecordRepository.upsert(...)` (local store; no network).
* Default category resolved from the Settings "Default category" preference, falling back to Food.

---

## Success Metrics

| Metric | Target |
| --- | --- |
| Install → first record (activation) | 60%+ |
| Time to log (open → save) | < 5s |
| Avg records logged / user / week | 5+ |

---

## Assumptions

* Most logs are quick captures where category/date can stay at sensible defaults.
* A default category always exists (defaults are non-deletable).

---

## Risks

* Accidental taps on `+` could create stray entries — mitigated by the > $0 gate and easy delete.

---

## Dependencies

* **Story/Task:** [US-LOG-2](US-LOG-2.md) (amount validation), [US-CAT-1](../categories/US-CAT-1.md) (default categories).
* **Service:** `core:data` / `core:storage` local persistence.

---

## Definition of Done

* [ ] Acceptance criteria met
* [ ] Code reviewed
* [ ] Unit tests completed
* [ ] Screenshot (Roborazzi) test for the Amount state passes
* [ ] Documentation updated
* [ ] Product Owner accepted

---

## Notes

Back from Amount with no value navigates away silently — no save, no prompt.
