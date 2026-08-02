# iOS App — Pro Expense (SwiftUI)

SwiftUI shell for Finance Tracker, consuming the KMP modules through the `ProExpenseKit`
framework produced by `:appshell`.

> **Status: compile-unverified.** Every `.swift` file here was authored in a Linux CI container
> with no Swift toolchain (`swiftc`/`xcodebuild` absent), so none of it has been compiled. The
> Kotlin side it binds to *is* verified — `./gradlew verifyAll` cross-compiles the iOS klibs for
> every module on every push. Expect to fix Swift-side compile errors on first build; the Kotlin
> API surface it calls is real.

## What's implemented

The **Splash → Home → Add Expense → Journal** vertical slice.

| Screen | View | Shared state source |
|---|---|---|
| Launch gate | `Screens/RootView.swift` | `AppShellViewModel.uiState.gate` |
| 01 Splash | `Screens/SplashView.swift` | — |
| 03 Home | `Screens/HomeView.swift` | `HomeViewModel` |
| 04 Add Expense | `Screens/AddExpenseView.swift` | `AddExpenseViewModel` → `LogExpenseUseCase` |
| 05 Journal | `Screens/JournalView.swift` | `JournalViewModel` |

Onboarding and PIN Entry are routed by the shared gate but still render placeholders — porting
them does not require touching gate logic.

## Architecture

No presentation logic lives in Swift. Each view holds a `KotlinViewModel<State, VM>`
(`Support/KotlinViewModel.swift`), which subscribes to the Kotlin `StateFlow` through
`FlowBridgeKt.subscribe` and republishes values as `@Published`. Formatting (amounts, dates, day
labels) is done in `commonMain` via `AmountInput` / `PlatformDateFormatter`, so both platforms
render byte-identical strings.

Design tokens are transcribed from `design-system-spec/tokens.md` into `Theme/ProTheme.swift`
(light + dark). They are duplicated deliberately — they're pure presentation constants, and the
Compose side reads the same table from `ProExpenseTheme`.

## Build

Requires macOS + Xcode. The project file is generated, not committed:

```bash
brew install xcodegen          # once
cd iosApp
xcodegen generate              # produces ProExpense.xcodeproj
open ProExpense.xcodeproj
```

Building the app runs `:appshell:embedAndSignAppleFrameworkForXcode` as a pre-build step, which
compiles and links `ProExpenseKit.framework`. To build the framework by hand:

```bash
./gradlew :appshell:linkDebugFrameworkIosSimulatorArm64
```

Compile-only verification, which works without macOS and is what CI runs:

```bash
./gradlew verifyIosCompat      # cross-compiles iOS klibs for every KMP module
```

## Known gaps

- **Runtime-unverified storage.** `NativeSqliteDriver` + `DatabaseConfiguration.Encryption` and the
  Keychain `SecItem*` calls have only ever been klib-compiled. Verify the DB and Keychain
  round-trips on a simulator before trusting them — see `docs/ios_compatibility_plan.md`.
- **SQLCipher-iOS is not linked.** `NativeSqliteDriver` links the system `sqlite3`, so the
  `encryptionConfig` PRAGMA is inert until a SQLCipher-iOS binary is linked in its place (Phase 3).
  The Kotlin code needs no further change for that switch.
- **`:feature:sync` is excluded** from the iOS Koin graph — Drive OAuth/transport exists only in
  `androidMain`.
- **No `Info.plist` committed** — XcodeGen generates one from `project.yml`.
- **Journal filters in memory**, not through `getRecordsPage`'s SQL pushdown. See
  `JournalViewModel`'s KDoc.
