# User Story

> **ID:** US-SHC-2 · **Service:** `feature:sharedcost` · **Screen:** 10 Shared Costs
> **Priority:** 🟡 Should · **Status:** ✅ Implemented · **Persona:** 👫 Aiko (Cost Sharer)

## Title

> Split a bill unequally

---

## User Story

**As** Aiko 👫
**I want to** customize each person's share
**So that** I can reflect uneven contributions (someone ordered more, someone paid less)

---

## Description

### Background

Equal split doesn't fit every situation. Custom mode lets each person's share be adjusted
independently — including down to $0 for someone who didn't owe anything that round — while still
keeping the original total as the source of truth.

### Scope

**In Scope**

* Custom split mode with live-editable per-person shares (including $0).
* Persisting edited values across `Back` navigation.

**Out of Scope**

* Equal split — covered by [US-SHC-1](US-SHC-1.md).

---

## Acceptance Criteria

### Scenario 1 — Editing a share live

**Given**

* Custom mode.

**When**

* I adjust a share.

**Then**

* Each share is editable live (including down to $0).

### Scenario 2 — Values persist on Back

**Given**

* I am on the summary.

**When**

* I tap `Back`.

**Then**

* All values persist — re-entering the summary does not reset my edits.

---

## Functional Requirements

* [ ] Custom mode allows any individual share from $0 up to the full total.
* [ ] Editing one share does not auto-adjust the others (no forced rebalancing).
* [ ] Navigating back and forward preserves all entered custom values.

---

## Non-Functional Requirements

* [ ] **Reliability** — custom shares survive process death the same as any other draft input.

---

## Business Rules

* Custom shares need not sum exactly to the total — the original total remains the stored source of truth (see [US-SHC-4](US-SHC-4.md)).

---

## UI / UX Notes

* **Design / Mockup:** [`10-shared-costs.md`](../../../design-system-spec/screens/10-shared-costs.md) → "Custom split".

---

## Dependencies

* **Story/Task:** [US-SHC-1](US-SHC-1.md), [US-SHC-4](US-SHC-4.md).

---

## Definition of Done

* [ ] Acceptance criteria met
* [ ] Code reviewed
* [ ] Unit tests completed
* [ ] Screenshot (Roborazzi) test for the Custom split summary passes
* [ ] Documentation updated
* [ ] Product Owner accepted

---

## Notes

* **Gap fix (2026-07):** despite this documented business rule, `buildSplitStrategy` silently
  rebalanced the *last* participant's share to force the sum to match the total whenever it didn't
  — directly contradicting "editing one share does not auto-adjust the others." The deeper cause
  was `SplitStrategy.resolve()`'s `CustomSplit` branch (`core:domain`) hard-`require`-ing
  `sum == total.amount`, which is what the rebalancing was silently working around. Removed that
  requirement (now only validates matching participant keys and currency) and stopped the
  use-case-level rebalancing — shares are now stored exactly as entered. Covered by
  `CreateSharedCostUseCaseTest.invoke_buildsCustomSplitStoringSharesExactlyAsEnteredWithoutRebalancing`
  and `SplitStrategyTest`'s `custom split allows shares that do not sum to total`.

* **Gap fix (2026-07):** the NFR "custom shares survive process death the same as any other draft
  input" was unmet — `SharedCostsFlow`'s entire draft (total, note, people count, mode, names,
  custom shares) lived in a bare `remember`, wiped on rotation or the process being reclaimed in
  the background; only the step navigation flag used `rememberSaveable`. Added a `listSaver`-based
  `SharedCostDraftSaver` (flattens the draft into Bundle-safe primitives) and switched `draft` to
  `rememberSaveable(stateSaver = ...)`. Covered by
  `SharedCostDraftPersistenceTest.draft_survivesSimulatedProcessDeath` (uses
  `StateRestorationTester` to simulate process death mid-entry).

* **Gap fix (2026-07):** the input/summary screens hardcoded a literal `"$"` for every amount
  shown during split creation (`SharedCostSplitLogic.formatCents/formatRawTotal/formatShareRaw`
  and both screens' `AmountDisplay` calls never received the caller's currency symbol) — while the
  saved-history view correctly showed the user's real home-currency symbol. A non-USD user saw
  `$` throughout entry, then a different symbol once saved. Worse, `CreateSharedCostUseCase`/
  `UpdateSharedCostUseCase` defaulted `currencyCode` to a hardcoded `"USD"` and
  `SharedCostFeatureEntry` never overrode it, so every split's linked `FinanceRecord` was
  permanently persisted as USD regardless of the actual home currency — corrupting Journal/Reports
  currency labels for any non-USD user. Threaded `homeCurrencySymbol`/`homeCurrencyCode` from
  `ExpenseApp` through `SharedCostFeatureEntry` → `SharedCostsFlow` → the input/summary screens and
  into the use-case `currencyCode` argument. Covered by `SharedCostCurrencyFormatTest` and
  `SharedCostCurrencySymbolWiringTest`.
