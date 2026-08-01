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
| Date formatting | `shared/src/iosMain/.../PlatformDateFormatter.ios.kt` | `NSDateFormatter` / `NSCalendar` | ✅ done (compile-only — no simulator to run against) |
| PIN storage | `feature/auth` (new `iosMain`) | Keychain | not started |

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

### Shipped

- **New `:shell` module** — the KMP counterpart to `app`: the one module allowed to depend on every
  `core:*` and `feature:*` (it is the composition root, not a feature). Declares
  `binaries.framework { baseName = "ProExpenseKit" }` on both iOS targets, exporting `core:domain`,
  `core:data`, `shared` and `feature:logging` so Swift sees those types by name.
- **Shared screen state, both platforms** — `HomeViewModel` (`shell/commonMain`, on `shared`'s
  `StatefulViewModel`) now owns the whole Home surface: month spend, recent day groups, budget
  planner, active-event card, plus the cross-feature collections the Android shell hands to its
  other tabs. `ExpenseApp.kt` was refactored to consume it rather than deriving that state inline,
  so Compose and SwiftUI render from one computation. The Home mapping moved out of `java.util.Calendar`
  / `SimpleDateFormat` onto `PlatformDateFormatter` + `kotlinx-datetime` to satisfy `commonMain`'s
  no-`java.*` rule.
- **Presentation models in `commonMain`** — `HomeUiState` & friends live in `shell`,
  `ProRowKind`/`ProTransactionRowModel` moved from `shared/androidMain` to `shared/commonMain`.
- **Koin iOS composition root** — `startProExpenseKoin()` (`shell/iosMain/KoinIos.kt`), the
  counterpart to `ExpenseApplication.ensureStarted()`.
- **Flow → Swift bridge** — `StateWatcher<T>` + `Cancellable` (`shell/iosMain/FlowBridge.kt`): a
  closure subscription on `Dispatchers.Main`, hand-rolled rather than adding a Swift-export
  compiler plugin, so it compiles and is verified on Linux.
- **SwiftUI vertical slice** — `iosApp/` now holds an XcodeGen `project.yml` plus Splash → Home →
  Add Expense. **Authored unverified**: no macOS/Xcode in the development environment, so none of
  the Swift has ever been compiled. See `iosApp/README.md`.

### Still open

- **PIN gate on iOS** — `authModule` and `PinAuthRepositoryImpl` (PBKDF2 via `javax.crypto`) are
  `androidMain`-only, so `startProExpenseKoin()` deliberately omits them and the iOS shell launches
  straight into Home. Needs a CommonCrypto/Keychain actual before iOS can ship a lock screen.
- **Google Drive sync on iOS** — `syncModule`/`syncPlatformModule` are Android-only for now.
- **Remaining screens** — 02 Onboarding, 05–15. Each needs its state extracted into a `:shell`
  ViewModel the same way Home was, before the SwiftUI view is written.
- **First real Xcode build** — expect Swift compile fixes; the Kotlin-side API it calls is
  compile-verified, the Swift calling it is not.

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
