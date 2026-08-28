# iOS App — Pro Expense (SwiftUI)

SwiftUI shell for Finance Tracker, consuming the KMP modules through the `ProExpenseKit`
framework produced by `:appshell`.

> **Status: compile-unverified.** Every `.swift` file here was authored in a Linux CI container
> with no Swift toolchain (`swiftc`/`xcodebuild` absent), so none of it has been compiled. The
> Kotlin side it binds to *is* verified — `./gradlew verifyAll` cross-compiles the iOS klibs for
> every module on every push, and 93 tests cover the shared ViewModels. Expect to fix Swift-side
> compile errors on first build; the Kotlin API surface it calls is real.
>
> `linkDebugFrameworkIosSimulatorArm64` is **SKIPPED** on a Linux host — Kotlin/Native will not
> produce a Mach-O framework off macOS — so the generated Objective-C header, which is the only
> authoritative source for the exported Swift names, cannot be inspected here. The Swift was
> therefore written to minimise reliance on names that are hard to predict:
>
> - State reaches Swift via `FlowBridgeKt.observeState(viewModel:onEach:)`, which takes the
>   ViewModel rather than its `StateFlow`. A `StateFlow` parameter would force the call site to
>   spell out the name Kotlin/Native generates for a kotlinx-coroutines class, derived from that
>   dependency's module name and the least predictable part of the exported surface.
> - `PinEntryScreenState` exposes `isError` / `isIdle` / `isLockedOut` booleans instead of leaving
>   the view to compare `PinEntryMode` — `Default` is a Swift keyword, so its exported entry name
>   is mangled.
>
> What remains genuinely unverified: enum entry names elsewhere (`AppGate`, `ThemeMode`,
> `DebtDirection`, `SharedSplitMode`, `PinSetupStage`, `OnboardingStep`), `KoinHelper.shared`,
> `KotlinLong` boxing for nullable `Long` parameters, and `KoinIosKt.doInitKoinIos()`'s `do`
> prefix. All follow documented Kotlin/Native rules, but none has been checked against a real
> header.

## What's implemented

All 16 screens in `design-system-spec/screens/`.

| Screen | View | Shared state source |
|---|---|---|
| Launch gate | `Screens/RootView.swift` | `AppShellViewModel.uiState.gate` |
| 01 Splash | `Screens/SplashView.swift` | — |
| 02 Onboarding · 02P Profile Setup | `Screens/OnboardingView.swift` | `OnboardingViewModel` |
| 03 Home | `Screens/HomeView.swift` | `HomeViewModel` |
| 04 Add Expense | `Screens/AddExpenseView.swift` | `AddExpenseViewModel` → `LogExpenseUseCase` |
| 05 Journal | `Screens/JournalView.swift` | `JournalViewModel` |
| 06 Journal Detail | `Screens/JournalDetailView.swift` | `JournalDetailViewModel` |
| 07 Event Budget · 08 Event Detail | `Screens/EventBudgetView.swift` | `EventBudgetViewModel` |
| 09 Debt Tracker | `Screens/DebtTrackerView.swift` | `DebtViewModel` |
| 10 Shared Costs | `Screens/SharedCostsView.swift` | `SharedCostViewModel` |
| 11 Category List | `Screens/CategoryListView.swift` | `CategoriesViewModel` |
| 12 Reports | `Screens/ReportsView.swift` | `ReportsViewModel` |
| 13 More | `Screens/MoreView.swift` | `MoreViewModel` |
| 14 PIN Setup · 15 PIN Entry | `Screens/PinViews.swift` | `PinSetupViewModel` · `PinEntryViewModel` |

**Navigation:** four tabs (Home, Journal, Budgets, More) plus a floating Add button. Reports,
Categories, Debts and Splits hang off More; Journal Detail pushes from Home and Journal rows.

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
  `androidMain`. Import/Export is likewise absent: it needs a `UIDocumentPicker` layer with no
  Android counterpart to lift.
- **No `Info.plist` committed** — XcodeGen generates one from `project.yml`.
- **Journal filters in memory**, not through `getRecordsPage`'s SQL pushdown. See
  `JournalViewModel`'s KDoc.
- **Localized strings are inline English.** The security-question text in `PinViews.swift` and the
  onboarding carousel copy should move to `Localizable.strings` before shipping; the Android side
  already has these in `strings.xml` with `values-th`/`values-my` translations.
- **Biometric unlock is not wired on iOS** — `PinAuthRepository.isBiometricEnrolled()` is read but
  no `LAContext` prompt exists yet.
