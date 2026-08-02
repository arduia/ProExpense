# iOS Compatibility Plan

Phased roadmap for turning the KMP module graph into a real iOS-capable codebase. This is the
detail doc referenced by `AGENTS.md` → "iOS Compatibility". Product vision and MVP scope live in
`docs/finance_tracker_product.md`; module map lives in `docs/module_structure.md`.

## Phase 0 — iOS targets + compile-time enforcement (this change)

- Every KMP module (`shared`, `core:domain`, `core:data`, `core:storage`, all `feature:*`) declares
  `iosArm64()` and `iosSimulatorArm64()` alongside `androidTarget()`.
- Root `build.gradle.kts` adds two gates:
  - `checkIosTargets` — fails the build if any KMP-plugin module is missing either iOS target.
    This is the enforcement mechanism for **new** modules: forgetting iOS targets breaks the build,
    not just a lint warning.
  - `verifyIosCompat` — cross-compiles iOS klibs (`compileKotlinIosArm64`,
    `compileKotlinIosSimulatorArm64`) for every KMP module. This only needs the Kotlin/Native
    compiler, which runs on Linux — no macOS or Xcode required for this phase.
- `verifyAll` depends on `verifyIosCompat`, so the default agent verification gate proves iOS
  compile-compatibility on every push.
- No new `iosMain` implementations are required here: modules without `expect` declarations need
  no actuals, and the existing stubs (`shared/src/iosMain/…`, `core/storage/.../DatabaseDriverFactory.ios.kt`)
  compile as `TODO()`-bodied actuals.

**Boundary:** klib compilation is what's verifiable without macOS. Linking a real iOS framework
(`.xcframework`) or running XCTest requires Xcode and is out of scope until Phase 3.

## Phase 1 — Real iOS actuals

Replace `TODO()` stubs with working implementations, one seam at a time:

| Seam | File | iOS implementation | Status |
|---|---|---|---|
| Clock | `shared/src/iosMain/.../PlatformClock.ios.kt` | `NSDate().timeIntervalSince1970` | ✅ done |
| Digest | `shared/src/iosMain/.../PlatformDigest.ios.kt` | CommonCrypto (`CC_MD5`/`CC_SHA256`) | ✅ done |
| Local DB driver | `core/storage/src/iosMain/.../DatabaseDriverFactory.ios.kt` | SQLDelight `NativeSqliteDriver` + `encryptionConfig` key pragma | ✅ done (compile-only — see caveat below) |
| DB key manager | `core/storage/src/iosMain/.../IosDatabaseKeyManager.kt` | Keychain generic-password item (`SecItemAdd`/`SecItemCopyMatching`) | ✅ done (compile-only — see caveat below) |
| Fast key-value cache | `core/storage/src/{common,ios}Main/.../PlatformKeyValueStore.kt` | `NSUserDefaults` | ✅ done |
| Storage repository implementations | `core/storage/src/commonMain/.../repository/*` | moved from androidMain — now shared | ✅ done (15 of 22 files were already platform-free; moved as-is) |
| Date formatting | `shared/src/iosMain/.../PlatformDateFormatter.ios.kt` | `NSDateFormatter` / `NSCalendar` | ✅ done (compile-only) |
| PIN hashing / CSPRNG | `shared/src/{common,android,ios}Main/.../PlatformCrypto.kt` | CommonCrypto `CCKeyDerivationPBKDF` + `SecRandomCopyBytes` | ✅ done (compile-only) |

**PIN storage note:** no `feature:auth` `iosMain` was needed. The PIN hash lives in `AppMetaStore`
(already shared), so only the KDF and CSPRNG were platform-specific — those became `expect`/`actual`
seams in `shared`, and `PinAuthRepositoryImpl` + `authModule` moved from `androidMain` to
`commonMain`. Both platforms now run one implementation and the `v2:` hash wire format cannot drift.
(The Keychain is used for the *database* passphrase, not the PIN — see `IosDatabaseKeyManager`.)

Each actual gets a backbone unit test in `commonTest` exercised through the `expect` contract
(fakes at the repository boundary per `AGENTS.md` testing contract), plus platform-specific checks
where Kotlin/Native test running is available.

**Storage layer notes (from the `core:storage` migration):**

- `core:storage`'s concrete implementations (`SqlDelight*Repository`, `AppMeta*Repository`,
  `ProExpenseStorage`, the Koin bindings) now live in `commonMain` and are shared by both
  platforms — only the driver, key manager, and key-value store differ per platform. Each platform
  keeps its own `ProExpenseStorage.create(...)` convenience overload (`create(context)` on Android,
  `create()` on iOS) that builds those three pieces and delegates to the portable
  `ProExpenseStorage.create(driverFactory, keyManager, keyValueStore, dispatcher)`.
- **Runtime-unverified pieces**: `NativeSqliteDriver`/`DatabaseConfiguration.Encryption` and the
  Keychain `SecItem*` calls only klib-compile in this environment (no macOS/simulator to run
  against) — verify the actual read/write round-trip on a real iOS run before relying on them.
- **SQLCipher-iOS linking caveat**: `NativeSqliteDriver` links the system `sqlite3` by default. The
  `encryptionConfig` PRAGMA set in `DatabaseDriverFactory.ios.kt` only takes effect once the iosApp
  build links a SQLCipher-iOS binary in its place — that's Phase 3 work (needs Xcode/macOS); the
  Kotlin code itself is already final and needs no further change for that switch.
- Two portability bugs surfaced and were fixed while moving code to `commonMain`: `Dispatchers.IO`
  isn't available on Kotlin/Native (use `Dispatchers.Default`), and `System.currentTimeMillis()`
  isn't portable (use `shared`'s `currentEpochMillis()` seam).

## Phase 2 — iosApp shell (in progress)

The **Splash → Home → Add Expense → Journal** vertical slice is in. What landed:

- **New `:appshell` module** — the one module allowed to depend on all `core:*` + all `feature:*`
  (package `com.arduia.expense.shell`). It exists because app-shell orchestration needs several
  features at once, which `feature:*` isolation forbids and `core:*`/`shared` forbid outright;
  `app` could do it before only because it is Android-only.
- **`ProExpenseKit` framework** — `binaries.framework` on `:appshell`, exporting `core:domain`,
  `core:data`, `shared` and the slice's features. Configuration only; `link*` needs macOS.
- **Koin iOS initializer** — `appshell/src/iosMain/.../KoinIos.kt`, mirroring
  `ExpenseApplication.ensureStarted()` minus `androidContext()`. `:feature:sync` is excluded (its
  bindings need `syncPlatformModule`, which is androidMain-only).
- **Flow → Swift bridge** — `FlowBridge.kt` / `FlowSubscription.kt`. **Decision: hand-written, not
  SKIE.** SKIE runs at framework-link time and so could not be compiled or exercised without macOS,
  whereas the hand-written bridge klib-compiles and is therefore covered by `verifyIosCompat` on
  every push. Revisit SKIE for ergonomics once a macOS runner exists.
- **Shared ViewModels** on `StatefulViewModel` — `AppShellViewModel` (launch gate: splash,
  onboarding, PIN, re-lock), `HomeViewModel`, `JournalViewModel`, `AddExpenseViewModel`, plus
  `RecordRowProjection` shared by Home and Journal. `ProRowKind`/`ProTransactionRowModel` moved to
  `shared/commonMain` (same package, so no import churn) so both UIs bind one row model.
- **SwiftUI views** under `iosApp/ProExpense/` — see that README. Authored without a Swift
  toolchain and therefore **compile-unverified**.

**Remaining for Phase 2:** Android still renders Home and Journal from its own Compose-bound
`*FeatureEntry.kt` state, so those screens have two derivations until `ExpenseApp.kt` and
`HistoryFeatureEntry.kt` adopt the shared ViewModels. That migration — plus Onboarding and PIN
Entry in SwiftUI — is the next increment. `AppShellViewModel` is the reference for how an
entry-file's state should collapse into `commonMain`.

## Phase 3 — Framework linking & device/XCTest CI

- Add a `binaries.framework` (or XCFramework) target for `shared`/aggregation module once iosApp
  needs to link.
- macOS CI runner (or local Xcode) to run `linkDebugFrameworkIosSimulatorArm64` + XCTest — this is
  the first phase that requires actual macOS/Xcode, not just Kotlin/Native.

## Constraints carried into every phase

- No cross-feature dependencies — iOS work does not relax `feature:*` isolation.
- No new product surface (bank integrations, cloud sync) — this is a compile/architecture track,
  not a feature track.
- `commonMain` must stay free of `java.*`/`android.*` imports; platform code only in
  `androidMain`/`iosMain` via `expect`/`actual`.
