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

* Its per-person breakdown appears in Shared Costs history only — never as additional rows in
  Journal (only the split's own single summary row can ever appear there — see Scenario 4).

### Scenario 3 — Opt in to counting the total toward personal spend

**Given**

* I am on Split Summary, about to save.

**When**

* I look at the "Also record as an expense" toggle, off by default, then optionally turn it on.

**Then**

* Off (default): the split saves with no linked `FinanceRecord` — it never counts toward
  Reports/budget totals, and it's excluded from Home Recents (see Scenario 4).
* On: the split saves with a linked `FinanceRecord` for the total, same as Scenario 1.
* Editing a saved split and flipping the toggle updates that linkage immediately — turning it off
  removes the previously-linked record; turning it on creates one.

### Scenario 4 — Journal always shows the split; Home Recents only when it counts

**Given**

* A saved split, either toggle state.

**When**

* I browse Journal vs. Home's Recents section.

**Then**

* Journal always shows the split's own summary row, regardless of the toggle — Journal is the full
  history browse, same as it already does for a toggle-off Debt (US-DEBT). Toggling off doesn't
  hide the split, it only stops it from counting toward totals.
* Home Recents shows the split only when the toggle is on (it has a real linked `FinanceRecord`) —
  mirroring how a toggle-on Debt is visible there and a toggle-off Debt is not.

---

## Functional Requirements

* [ ] Saving a split writes exactly one `FinanceRecord` for the total amount, only when the
  "record as transaction" toggle is on.
* [ ] The toggle defaults to off — a split doesn't count toward personal spend totals until the
  user explicitly opts in, mirroring Debt's "Also record as expense/income" toggle.
* [ ] Per-person shares are stored as split metadata, not as separate expense records — they never
  appear as their own Journal rows, regardless of the toggle.
* [ ] Journal shows the split's own summary row unconditionally, independent of the toggle.
* [ ] Home Recents shows the split only when the toggle is on.

---

## Non-Functional Requirements

* [ ] **Reliability** — Journal's total spend figure is unaffected by the number of people in a split.

---

## Business Rules

* A shared cost is at most one expense record; the split is presentation/reference data layered on
  top. Whether that one record counts toward spend totals is the user's explicit choice (default
  off), not automatic — but the split itself is always visible in Journal, toggle state aside.

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

* **Gap fix (2026-07):** Scenario 1 was unimplemented — `SqlDelightSharedCostRepository.persist()`
  only wrote the `shared_cost` table row; nothing ever created the promised `FinanceRecord` for the
  total, so shared costs never counted toward Journal/Reports spend at all. It now injects
  `FinanceRecordRepository` and upserts one `FinanceRecord` (category defaults to "shopping" — not
  yet user-selectable) with `link = RecordLink.ToSharedCost(id)`, reusing the `SharedCostId` string
  as the `RecordId` so create/update always target the same record instead of accumulating
  duplicates, and `delete()` removes both rows atomically (satisfying
  [US-SHC-5](US-SHC-5.md)'s atomicity requirement). The read side (mapping, tag columns, import/export,
  `tagLabel()`) already existed and needed no changes. Covered by
  `SqlDelightSharedCostRepositoryTest.create_withRecordAsTransactionTrue_writesLinkedFinanceRecordForTheTotal` /
  `update_updatesTheSameLinkedRecordRatherThanCreatingASecondOne` /
  `delete_alsoDeletesTheLinkedFinanceRecord`.

* **Gap fix (2026-07 v2):** the `FinanceRecord` link from the fix above was unconditional — every
  split counted toward personal spend with no way to opt out, which is wrong for a bill that's
  purely reference/reconciliation data (e.g. tracking who owes what on a trip without wanting it to
  also show up as the user's own expense). Added a `recordAsTransaction` field to `SharedCost`
  (domain), `SharedCostInput` (data), and the `shared_cost.record_as_transaction` column (storage,
  migration `17.sqm` — existing rows backfill to `1`/true so upgrading doesn't silently delete
  their already-linked record and change historical totals; only new installs default to `0`),
  mirroring Debt's own `recordAsTransaction` toggle exactly. `SqlDelightSharedCostRepository`
  now only upserts the linked `FinanceRecord` when the flag is true, and deletes any existing one
  when it's false (toggle-off case). Split Summary gained a "Also record as an expense" switch
  (off by default) next to Save, wired through `SaveSharedCostInput.recordAsTransaction`. Covered
  by `SqlDelightSharedCostRepositoryTest.create_withRecordAsTransactionFalse_writesNoLinkedFinanceRecord`
  / `update_togglingRecordAsTransactionOff_removesAPreviouslyLinkedRecord` and
  `SharedCostUseCasesTest`'s `invoke_defaultsRecordAsTransactionToFalse` /
  `invoke_passesThroughAnExplicitRecordAsTransactionOptIn` (both use cases).

* **Gap fix (2026-07 v3):** v2 above made a toggle-off split invisible everywhere outside Shared
  Costs history — including Journal — since Journal's row list was purely `FinanceRecord`-sourced
  and a toggle-off split has none. That's inconsistent with Debt's own precedent: a toggle-off Debt
  already merges into Journal directly from `DebtRepository` (bypassing `FinanceRecord`/`RecordKind`
  entirely — see `visibleUnrecordedDebts`/`Debt.toDebtRowModel()` in
  `feature/history/entry/HistoryFeatureEntry.kt`) precisely so Journal keeps showing every entry
  regardless of the toggle, per `de50568f`'s original "Journal is the full history browse" rule
  (predates the split toggle). Added the mirroring `visibleUnrecordedSplits`
  (`feature/history/JournalUseCases.kt`) and `SharedCost.toSplitRowModel()`
  (`HistoryFeatureEntry.kt`), merged into `groupByDay()` alongside the existing debt merge, plus a
  reciprocal `includesUnrecordedSplits()` so the Split filter chip still surfaces these. Separately,
  `RecordKind.isVisibleInHomeRecents()` previously hid `SPLIT` behind `SHOW_SPLIT_AND_DEBT_ROWS`
  even when toggle-on (unlike `DEBT_LENT`/`DEBT_OWED`, already unconditionally visible once
  toggle-on) — now `SPLIT` is unconditionally visible too, so a toggle-on split reaches Home
  Recents like any other counted transaction; a toggle-off split still never reaches Home Recents
  at all, since it has no `FinanceRecord` and Home Recents (unlike Journal) never merges in
  toggle-off splits directly. Covered by `VisibleUnrecordedSplitsTest`
  (`JournalUseCasesTest.kt`) and `RecordKindTest.isVisibleInHomeRecents_toggleOnSplitIsAlwaysVisible`.
