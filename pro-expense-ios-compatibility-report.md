# Pro Expense — Data Layer iOS-Compatibility Report

**Date:** 2026-07-08
**Scope:** Audit of the implemented data layer (`core:domain`, `core:data`, `core:storage`,
`shared`, `feature:*` data code, app composition root) against the iOS-Readiness Principles and
Phase 0 requirements in `pro-expense-detail-design-plan.md` and the companion
`pro-expense-data-layer-design.md`.
**Method:** Every source set, Gradle target declaration, expect/actual seam, repository
implementation, and DI module was read directly; commonMain was scanned for JVM-only APIs.

---

## 1. Verdict

**The data layer is substantially iOS-ready in code shape, but not yet iOS-provable in build
shape.** The commonMain/androidMain discipline held for domain models, contracts, mappers,
use cases, and the flagship ViewModel. However, **no module declares an iOS target**, so nothing
enforces that discipline at compile time; the SQLDelight repository implementations sit in
`androidMain` despite being ~95% platform-neutral; and `feature:auth` carries real business logic
(lockout policy, hash-format handling) in `androidMain`. One material — though documented —
deviation from the design plan exists in the security model (PIN is a hash gate, not the
SQLCipher key source).

Scorecard against the plan's five iOS-Readiness Principles:

| # | Principle | Status |
|---|-----------|--------|
| 1 | No business logic in `androidMain` | ⚠️ **Partial** — auth lockout/hash logic and repository coordination logic (event-cache recompute) live in `androidMain` |
| 2 | `expect/actual` seams declared from day one | ✅ Seams exist (`DatabaseDriverFactory`, `platformDigestHex`, `Platform`) with iOS stub files checked in — ⚠️ but stubs are **not compiled** (no iOS target) |
| 3 | ViewModels expose platform-agnostic state | ✅ `ProViewModel`/`StatefulViewModel` are pure-KMP (no `androidx.lifecycle`); `LoggingViewModel` is commonMain; no Android types found in any commonMain state |
| 4 | Repository pattern is the only data access path | ✅ No screen/ViewModel touches SQLDelight; `StorageModule` exposes only `core:data` contracts — ⚠️ one exception: `feature:auth` reaches into `core:storage`'s internal `AppMetaStore` |
| 5 | Periodic self-check per phase | ⚠️ Not evidenced; the compile gate that would automate it (iOS targets) is absent |

---

## 2. What Holds (Compliant with the Design)

- **commonMain purity is real.** A scan of every `commonMain` source set across all modules found
  **zero** JVM-only APIs (`java.*`, `javax.*`, `System.currentTimeMillis`, `String.format`). Kotlin/Native
  compilation would not be blocked by the shared code as written today.
- **Driver seam matches design §5.1.** `DatabaseDriverFactory` is an `expect class` in
  `core:storage/commonMain`; the Android `actual` wires SQLCipher via `SupportOpenHelperFactory`
  into SQLDelight's `AndroidSqliteDriver`; a documented iOS `actual` stub
  (`DatabaseDriverFactory.ios.kt`) is checked in pointing at `NativeSqliteDriver` + SQLCipher-iOS.
- **Digest seam matches design.** `platformDigestHex` is an `expect fun` in `shared` with an
  Android `MessageDigest` actual and an iOS stub referencing CommonCrypto — exactly the pattern
  the plan's PBKDF2 decision anticipated for crypto primitives.
- **Row↔domain mappers are in commonMain** (`core:storage/commonMain/mapping/*`) — *better* than
  design §4.3, which had them in androidMain. Stable enum codecs (`EXPENSE=0`, never `.ordinal`)
  and constant link tags are shared and unit-tested in `commonTest`.
- **Record integrity is portable, per design §4.1.** `RecordChecksum`, `canonicalPayload()` (v1
  versioned, length-prefixed note), and `RecordIntegrityVerifier` live in `core:domain/commonMain`
  on top of `platformDigestHex` — deliberately not device-bound, so the same checksum works on iOS
  and in export/import.
- **`AppMetaLocalStore` implements design §3.2** — single mutex-guarded owner of the `app_meta`
  row; `BudgetRepository`/`LockoutRepository`/`SecurityStateReader`/locale/profile/currency
  implementations all delegate to it. The predicted race condition is designed out.
- **Flow scope matches design §4.4** — `observeAll()` added to exactly `FinanceRecordRepository`
  and `EventRepository` (via `sqldelight-coroutines`), not sprayed everywhere.
- **Category gap closed per design §4.2** — `Category.sq` exists (with `sort_order` via `3.sqm`),
  and `ProExpenseStorage.seedDefaultCategories()` does an idempotent `INSERT OR IGNORE`-style seed
  from a commonMain `DEFAULT_CATEGORIES` list every launch.
- **ID generation per design §6** — `Identifiers.newId()` uses multiplatform `kotlin.uuid.Uuid` in
  commonMain; no expect/actual needed.
- **Feature repositories are thin commonMain adapters per design §3.1** —
  `DefaultLoggingRepository`, `DefaultHistoryRepository`, use cases (`AuthUseCases`,
  `ImportExportUseCases`, `JournalUseCases`, …) are all commonMain and depend only on `core:data`
  contracts. Platform file I/O (Zip4j, SAF) is correctly isolated in
  `feature:importexport/androidMain` (`ExportFileWriter`, `ImportZipReader`).
- **Testing strategy per design §8** — storage repositories are backbone-tested on the pure-JVM
  `JdbcSqliteDriver` (real SQL + real mappers, no SQLCipher), mapper tests run in `commonTest`,
  and an export/import ZIP round-trip test exists.
- **Migrations per design §9** — schema evolves through `2.sqm`–`7.sqm`; v1→v7 history is intact.
- **Composition root leaks nothing SQL-shaped** — `ProExpenseStorage` + Koin `storageModule`
  expose only `core:data` contract types to the graph; no `storage.db.*` import exists anywhere in
  `app/` or feature UI code.

**DI note:** Koin was chosen over the design's "manual `AppContainer` now" recommendation. This is
a benign, arguably iOS-favorable deviation — the design itself named Koin as the KMP upgrade path,
Koin core is multiplatform, and feature DI modules (e.g. `loggingModule`) already sit in
commonMain. Only `storageModule` (needs `androidContext()`) is platform-bound, which is correct.

---

## 3. Gaps and Deviations (ranked by iOS impact)

### G1 — No iOS target is declared in any module ⚠️ *biggest structural gap*

Every KMP module (`shared`, `core:*`, `feature:*`) declares `androidTarget()` only. The `iosMain`
stub files in `shared` and `core:storage` are **not compiled** — `core/storage/build.gradle.kts`
says so explicitly ("no iOS target is configured yet, so it is intentionally not compiled").

The detail design plan's Phase 0 (E13) required the `iosMain` source set to be *created from the
start* so "the Gradle/module structure already expects three targets." What exists is the weaker
half: stub *files* without stub *source sets*. Consequences:

- Nothing proves commonMain compiles for Kotlin/Native. It is clean today (§2), but there is no
  compiler gate keeping it clean — a single `java.util.*` import in commonMain would go unnoticed
  until the iOS phase.
- SQLDelight generates no native database interface, so the "add iOS = implement the driver"
  promise is unverified.

**Recommendation:** add `iosArm64()`/`iosSimulatorArm64()` to `shared`, `core:domain`, `core:data`,
`core:storage` (the data-layer spine) and make `verifyAll`/CI compile the iOS klibs. Feature and
UI modules can follow later. This is the cheapest moment to do it — commonMain is currently clean.

### G2 — SQLDelight repository implementations live in `androidMain` unnecessarily

All eight `SqlDelight*Repository` classes plus `AppMetaLocalStore` are in
`core:storage/androidMain`, yet their code uses only multiplatform APIs (SQLDelight runtime,
coroutines) — except two JVM-isms: `System.currentTimeMillis()` (audit/cache timestamps,
`updated_at`) and `Dispatchers.IO` defaults. The design doc (§3.1) did place them in androidMain,
so this matches the *letter* of the design, but principle #1 says coordination logic (e.g. the
event spend-cache recompute inside `SqlDelightFinanceRecordRepository.upsert/delete`) belongs in
commonMain. As placed, the entire repository layer would need to be moved or duplicated for iOS.

**Recommendation:** introduce a commonMain clock seam (`kotlin.time.Clock` or a one-function
`expect fun epochMillis()`) and move the repositories + `AppMetaLocalStore` to
`core:storage/commonMain`. After that, the iOS delta for the whole storage layer is genuinely just
`DatabaseDriverFactory.actual` + a key manager.

### G3 — Business logic in `feature:auth/androidMain` (principle #1 violation)

`PinAuthRepositoryImpl` (androidMain) contains logic that is not platform-specific:

- **Lockout escalation policy** (`≤4` → none, `5` → 30 s, `6` → 60 s, `7+` → 5 min) — pure product
  rules that iOS must replicate identically.
- **Versioned hash format** (`v2:<iterations>:<saltHex>:<hashHex>`) construction and parsing —
  format knowledge duplicated into a platform source set means an iOS reimplementation could
  drift and lock users out of PIN verification after an export/import.
- Orchestration (read-modify-write of attempts/lockout via `AppMetaStore`).

Only the PBKDF2 call itself (`javax.crypto.SecretKeyFactory`) is genuinely platform-bound — and
the design *chose* PBKDF2 precisely because iOS has it natively in CommonCrypto, anticipating a
seam. It also uses `System.currentTimeMillis()` directly.

**Recommendation:** add `expect fun platformPbkdf2Sha256(input, salt, iterations, keyLenBits): ByteArray`
next to `platformDigestHex` in `shared`; move `PinAuthRepositoryImpl` (policy, format, orchestration)
to `feature:auth/commonMain`. This is the highest-value single refactor for iOS parity of the
security screens (14/15).

### G4 — `feature:* → core:storage` dependency-rule violations

All 12 feature modules declare `implementation(project(":core:storage"))` in `androidMain`
(`feature:importexport` also in `commonMain` — the one sanctioned exception per the data-layer
doc). `docs/module_structure.md` forbids this. In practice only `feature:auth` actually *imports*
storage classes (`AppMetaStore`, `ProExpenseStorage`); the other ~10 declarations are unused —
but they normalize the violation and would drag SQLCipher/driver types into every feature's
compile graph on iOS.

**Recommendation:** delete the unused `core:storage` dependencies from ~10 feature build files
(mechanical, zero-risk); fix `feature:auth` as part of G3 (its data needs should surface as a
`core:data` contract — e.g. a `PinCredentialStore` — implemented in `core:storage`, instead of
exporting the internal `AppMetaStore`).

### G5 — Security model deviates from the design plan (documented, but reconcile it)

The plan (and data-layer design §5.2) specifies: PIN → PBKDF2 → **SQLCipher key**, `PRAGMA rekey`
rotation at PIN Setup, reverse rotation on disable, biometric-wrapped key with
`setUserAuthenticationRequired(true)`.

The implementation instead: the DB key is **always** a random 32-byte Keystore-wrapped key
(`AndroidDatabaseKeyManager.getOrCreateDatabaseKey()` is the interface's *only* method); the PIN
is stored as a **hash** in `app_meta.pin_hash` and acts as a UI gate; there is no rekey path; the
biometric wrapped-key column is a placeholder. `DatabaseKeyManager`'s KDoc explicitly declares
rotation/wrapping "intentionally deferred to a later phase," so this is a scoped deferral, not an
accident — but it changes the meaning of Screens 14/15 ("verification = successful DB open" no
longer holds) and it moots-for-now the plan's rekey-atomicity open question (which returns the
moment rotation is implemented).

**iOS impact:** smaller seam than designed (good), but the eventual iOS `DatabaseKeyManager`
(Keychain instead of Keystore) and the PIN-gate model must be reconciled with the plan *before*
the iOS phase, or the two platforms will implement different security semantics.
**Recommendation:** update the two design docs to state the current model + the deferred rotation
plan, so the docs and code stop disagreeing.

### G6 — Dead and contradictory integrity artifacts

`IntegrityKeyManager` (commonMain) + `AndroidIntegrityKeyManager` (device-bound Keystore
HMAC-SHA256) are **entirely unused** — superseded by the portable `RecordIntegrityVerifier`
(§2). Meanwhile the header comment in `FinanceRecord.sq` still describes `integrity_hash` as
"keyed by dedicated Android Keystore HMAC key," which is no longer true and directly contradicts
design §4.1 ("deliberately **not** a device-bound Keystore key"). A future iOS implementer reading
either would build the wrong (non-portable) `actual`.

**Recommendation:** delete both `IntegrityKeyManager` files and fix the `.sq` comment.

### G7 — Minor items

- `ProExpenseStorage` publicly exposes `database: ProExpenseDatabase` and
  `appMetaStore: AppMetaLocalStore` — storage internals available to any module that (improperly)
  depends on `core:storage` (currently exploited only by `feature:auth`, see G3/G4). Make them
  `internal` once G3 lands.
- `LocalDataStore` (`isAvailable()`) is a leftover stub with no implementation or caller — delete.
- `System.loadLibrary("sqlcipher")` in the Android driver factory is correct and correctly placed
  (androidMain) — no action; noted because the iOS actual needs the equivalent
  SQLCipher-link-step documented when written.

---

## 4. Suggested Order of Work (pre-iOS hardening)

| Step | Item | Size | Gap |
|------|------|------|-----|
| 1 | Remove unused `core:storage` deps from feature build files | XS | G4 |
| 2 | Delete dead `IntegrityKeyManager` pair; fix `FinanceRecord.sq` comment | XS | G6 |
| 3 | Add iOS targets to `shared`, `core:domain`, `core:data`, `core:storage`; compile klibs in CI | S | G1 |
| 4 | Clock seam; move `SqlDelight*Repository` + `AppMetaLocalStore` to commonMain | M | G2 |
| 5 | `platformPbkdf2Sha256` expect/actual; move `PinAuthRepositoryImpl` logic to commonMain behind a `core:data` contract | M | G3 |
| 6 | Reconcile security-model deferral in both design docs | S | G5 |

Steps 1–3 are safe now and make every later step compiler-enforced. After step 5, the honest
answer to the plan's Phase-6 self-check — "could this commonMain code run unmodified if we swapped
in iOS tomorrow?" — becomes **yes** for the entire data layer, with the iOS phase reduced to:
`DatabaseDriverFactory` actual (SQLCipher-iOS), `DatabaseKeyManager` actual (Keychain),
`platformDigestHex`/`platformPbkdf2Sha256` actuals (CommonCrypto), and file I/O for import/export.
