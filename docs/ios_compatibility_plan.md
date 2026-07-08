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

| Seam | File | iOS implementation |
|---|---|---|
| Clock | `shared/src/iosMain/.../PlatformClock.ios.kt` | `NSDate().timeIntervalSince1970` |
| Digest | `shared/src/iosMain/.../PlatformDigest.ios.kt` | CommonCrypto (`CC_MD5`/`CC_SHA256`) |
| Date formatting | `shared/src/iosMain/.../PlatformDateFormatter.ios.kt` | `NSDateFormatter` / `NSCalendar` |
| Local DB driver | `core/storage/src/iosMain/.../DatabaseDriverFactory.ios.kt` | SQLDelight `NativeSqliteDriver` + SQLCipher-iOS (key pragma) |
| PIN storage | `feature/auth` (new `iosMain`) | Keychain |

Each actual gets a backbone unit test in `commonTest` exercised through the `expect` contract
(fakes at the repository boundary per `AGENTS.md` testing contract), plus platform-specific checks
where Kotlin/Native test running is available.

## Phase 2 — iosApp shell

- SwiftUI views under `iosApp/` consuming the KMP modules via the generated framework.
- Koin iOS initializer (`KoinIOS.kt` in `shared` or a thin `iosMain` entry point) wiring the same
  repository graph used by `app` on Android.
- ViewModel-equivalent state exposed to SwiftUI (either shared `StateFlow` via a Swift-friendly
  wrapper, or platform-native state holders calling into KMP repositories directly) — decide once
  the first real screen is ported.

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
