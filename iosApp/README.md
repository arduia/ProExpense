# iOS App — SwiftUI shell

Native SwiftUI shell for Finance Tracker, rendering state computed by the shared KMP code in
[`:shell`](../shell). Phase 2 of [`docs/ios_compatibility_plan.md`](../docs/ios_compatibility_plan.md).

> ## ⚠️ This Swift code has never been compiled
>
> It was authored on Linux, where there is no Xcode toolchain. Kotlin/Native *klib*
> cross-compilation is verified in CI (`./gradlew verifyIosCompat`), but framework **linking**,
> Swift compilation, and simulator runs all require macOS. Expect to fix Swift compile errors on
> the first real build — treat the files here as a reviewed starting point, not a green build.

## What's here

| Path | Role |
|---|---|
| `project.yml` | XcodeGen project definition (no committed `.pbxproj` — see below) |
| `Sources/ProExpenseApp.swift` | `@main`; starts Koin, routes Splash → Home |
| `Sources/Bridge/ShellStore.swift` | Subscribes to the KMP `StateFlow` via `StateWatcher`, republishes as `@Published` |
| `Sources/DesignTokens.swift` | Colors/type/spacing/shape transcribed from `design-system-spec/tokens.md` |
| `Sources/Screens/SplashView.swift` | Screen 01 |
| `Sources/Screens/HomeView.swift` | Screen 03 |
| `Sources/Screens/AddExpenseView.swift` | Screen 04, amount step |

## Building (macOS only)

```bash
brew install xcodegen                                       # once
cd iosApp && xcodegen generate                              # produces iosApp.xcodeproj
cd .. && ./gradlew :shell:linkDebugFrameworkIosSimulatorArm64
open iosApp/iosApp.xcodeproj                                # or: xcodebuild -scheme iosApp
```

The Xcode target also runs the Gradle link step as a pre-build script, so an in-Xcode build picks
up Kotlin changes on its own.

### Why XcodeGen instead of a committed `.pbxproj`

A `.pbxproj` is a large generated file that cannot be opened, validated, or meaningfully reviewed
from the Linux environment where the Kotlin half of this app is developed. A declarative
`project.yml` can be — and regenerates deterministically on any Mac.

## How state reaches SwiftUI

```
HomeViewModel (shell/commonMain)      ← same instance type the Compose shell uses
  └─ uiState: StateFlow<ShellUiState>
       └─ StateWatcher (shell/iosMain, FlowBridge.kt)   ← closure subscription, main dispatcher
            └─ ShellStore (@Published)                  ← this target
                 └─ HomeView
```

No mapping logic lives in Swift: labels, amounts, day grouping, budget progress and the active-event
summary are all computed in `commonMain`, so both shells show identical figures by construction.

## Not implemented yet

- **PIN lock** — `PinAuthRepositoryImpl` uses `javax.crypto` PBKDF2 and lives in `androidMain`; the
  iOS shell launches straight into Home. Needs a CommonCrypto/Keychain actual.
- **Google Drive sync** — Android-only for now; `syncModule` is deliberately not registered in
  `startProExpenseKoin()`.
- **Onboarding, Journal, Budget, Debt, Split, Reports, More** — screens 02, 05–15.
- **Bundled fonts** — Inter / Manrope / Geist Mono are Android `res/font` assets; `DesignTokens.swift`
  falls back to system faces at the specified sizes until they are added to this target.
- **SQLCipher** — `NativeSqliteDriver` links the system `sqlite3`; the encryption PRAGMA only takes
  effect once a SQLCipher-iOS binary is linked in its place.
