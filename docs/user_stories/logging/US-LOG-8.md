# User Story

> **ID:** US-LOG-8 · **Service:** `feature:logging` (schema only, no screen)
> **Priority:** 🔵 Phase 2 · **Status:** 🏗️ Groundwork only · **Persona:** Any user

## Title

> Reserve a wallet identifier on each expense for future multi-wallet support

---

## User Story

**As a** product evolving toward multi-wallet tracking
**I want to** reserve a nullable wallet reference on every `FinanceRecord` now
**So that** a later multi-wallet feature can link existing and new records without a schema migration

---

## Description

### Background

Multi-wallet support (e.g. "Cash", "Bank", "Credit Card") is not part of the MVP and has no design,
screen, or entity yet. This story exists solely to reserve the storage/domain seam ahead of that
feature, the same way `RecordLink` reserved event/debt/shared-cost tagging before those features
existed. Landing the column now — while the v2 schema is still pre-launch (see
`pro-expense-data-layer-design.md` §9) — avoids a migration later.

### Scope

**In Scope**

* Nullable `finance_record.wallet_id` (`INTEGER`) column.
* Nullable `FinanceRecord.walletId: WalletId?` domain field (`WalletId` wraps `Int`, unlike every
  other domain ID which wraps a `String` UUID — wallets are expected to be a small, locally-numbered
  set, not globally unique).
* Round-trip through `FinanceRecordRepository.upsert` / `getById` / `getAll`.
* Included in the record's integrity checksum (`canonicalPayload`, bumped to `v2`).

**Out of Scope**

* A `wallet` table/entity, `WalletRepository`, or any wallet CRUD.
* Any UI to create, pick, or filter by a wallet.
* Backfilling or defaulting `walletId` on existing records.
* Debt / Shared Cost tables getting the same column — this story covers `finance_record` only
  ("expenses"), per the current ask.

---

## Acceptance Criteria

No user-facing scenario exists yet — there is no screen or behavior to exercise. Covered instead
by the Functional Requirements below and their backing unit tests.

---

## Functional Requirements

* [x] `finance_record` has a nullable `wallet_id INTEGER` column; existing rows/new inserts default
      to `NULL` with no behavior change.
* [x] `FinanceRecord.walletId: WalletId?` defaults to `null` and round-trips through
      `SqlDelightFinanceRecordRepository`.
* [x] `WalletId` rejects non-positive values (`0` or negative).
* [x] `walletId` participates in the record's integrity checksum so tampering with it is detectable.

---

## Non-Functional Requirements

* [ ] **Reliability** — no migration required; column ships as part of the still-pre-launch v1
      schema (see `pro-expense-data-layer-design.md` §9).

---

## Business Rules

* `walletId` is informational-only until a wallet feature exists — nothing reads or writes it yet
  outside of direct repository calls.

---

## API / Technical Notes

* **Database Changes:** `finance_record.wallet_id INTEGER` (nullable), added directly to
  `FinanceRecord.sq` — no `.sqm` migration needed pre-launch.
* **Dependencies:** `core:domain` (`WalletId`, `FinanceRecord.walletId`), `core:storage`
  (`FinanceRecordMapper`, `SqlDelightFinanceRecordRepository`).

---

## Dependencies

* **Story/Task:** none yet — this is the seam a future wallet story will build on.

---

## Definition of Done

* [x] Functional Requirements met
* [x] Unit tests completed (`IdsTest`, `FinanceRecordTest`, `RecordIntegrityTest`,
      `SqlDelightFinanceRecordRepositoryTest`)
* [ ] Documentation updated (PRD Future-Compatible Design Notes — done)
* [ ] Product Owner accepted

---

## Notes

Purely preparatory — no wallet feature module, screen, or repository exists yet. This story exists
so the schema/domain change traces to documented Functional Requirements instead of only chat
history, per the project's PRD/Story audit gate.
