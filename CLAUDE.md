# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

Pro Expense is an Android expense-tracking app (free, open-source, privacy-focused) available on Google Play, F-Droid, and Amazon Appstore. It supports multi-language, multi-currency, statistics, and Excel backup/restore.

- **Application ID**: `com.arduia.expense` (production) / `com.arduia.expense.dev` (dev flavor)
- **Min SDK**: 24 | **Target/Compile SDK**: 36 | **Kotlin**: 2.2.0 | **Java**: 17
- **Current branch context**: Migrating from XML Fragment-based UI to Jetpack Compose

## Build Commands

```bash
# Assemble dev debug (default for development)
./gradlew assembleDevDebug

# Assemble production release
./gradlew assembleProductionRelease

# Run all unit tests
./gradlew test

# Run unit tests for a specific module
./gradlew :app:testDevDebugUnitTest

# Run a single test class
./gradlew :app:testDevDebugUnitTest --tests "com.arduia.expense.MainViewModelTest"

# Run instrumentation tests (requires connected device/emulator)
./gradlew connectedDevDebugAndroidTest

# Lint
./gradlew lint

# Build all modules
./gradlew build
```

> **Note**: The project requires an `api.properties` file at the root with a `main_url` key (Retrofit base URL). This file is gitignored — create it locally before building.

## Module Structure

| Module | Package | Purpose |
|---|---|---|
| `:app` | `com.arduia.expense` | Main application — all UI, ViewModels, DI, data layer |
| `:shared` | `com.arduia.core` | Core Kotlin/Android utilities shared across modules |
| `:expense-backup` | `com.arduia.expense.backup` | Backup schema definitions and models |
| `:currency-store` | `com.arduia.currency.store` | Currency data store and management |
| `:backup` | `com.arduia.backup` | Excel read/write utilities for import/export |
| `:week-expense-graph` | `com.arduia.graph` | Custom `SpendGraph` view for weekly expense visualization |

## Architecture

**MVVM + Repository pattern** following the standard Android Architecture diagram.

```
ui/          →  ViewModels (LiveData/StateFlow) → Repositories → data/
                                                                   ├── local/   (Room)
                                                                   ├── network/ (Retrofit)
                                                                   └── backup/  (WorkManager)
domain/      →  Value objects and filter models used across layers
di/          →  15+ Hilt modules wiring everything together
```

### Key conventions

**Result type**: All repository methods return `Result<T>` (a custom sealed class in `model/Result.kt`: `SuccessResult` / `ErrorResult`). Flow-returning methods use the `FlowResult<T>` typealias.

**Amount storage**: Expense amounts are stored in the Room database as integers multiplied by 100 (e.g. $12.34 → `1234`). The `Amount` value object in `domain/Amount.kt` encapsulates this. The `AmountTypeConverter` handles Room serialization. Never write raw doubles to the DB.

**LiveData wrapper**: ViewModels use `EventLiveData` from the `mvvm-core` library (`com.arduia.mvvm`) for one-shot events (navigation, toasts). Regular state uses standard `MutableLiveData`.

**UI model mappers**: Data entities are never passed directly to the UI. Each screen has mapper classes (e.g., `CurrencyUiModelMapper`) that convert domain/entity models into UI-specific models.

### Dependency Injection (Hilt)

Hilt is the DI framework. Key modules in `app/src/main/java/com/arduia/expense/di/`:

- `DatabaseModule` — Room database and DAOs (Singleton)
- `RepositoryModule` + abstract `*Module` files — bind repository interfaces to implementations
- `NetworkModule` — Retrofit instance and API service
- `BackgroundModule` — WorkManager tasks
- `FormatModule` — Date/number formatters with custom qualifiers (`@CurrencyDecimalFormat`, `@MonthlyDateRange`)
- `NavHostModule` — Navigation host fragment reference

`@HiltAndroidApp` on `ExpenseApplication`, `@AndroidEntryPoint` on all Fragments and Activities.

### Database (Room)

- **File**: `accounting.db` | **Version**: 6 | **Schema exported**: `app/schemas/`
- **Entities**: `ExpenseEnt` (expenses), `BackupEnt` (backup records)
- **Migrations**: `MIGRATION_3_4` (added backup table), `MIGRATION_4_6` (amount ×100 conversion)
- When adding new migrations, register them in `ProExpenseDatabase` and export schema.

### Navigation

Currently Fragment Navigation Component with a single graph at `app/src/main/res/navigation/main_nav.xml`. Safe Args is enabled for type-safe argument passing.

## Jetpack Compose Migration

The project is actively migrating from XML Fragments to Jetpack Compose. Compose is already enabled in the build with all necessary dependencies in the version catalog.

**Compose dependencies already available** (via `libs.versions.toml`):
- Compose BOM `2025.08.00`, Material3, Foundation, Animation, UI Tooling
- `navigation-compose` 2.9.3
- `lifecycle-viewmodel-compose` 2.9.2
- `hilt-navigation-compose` 1.2.0
- `activity-compose` 1.10.1

**Migration conventions**:
- Reuse existing ViewModels — they are Compose-compatible as-is.
- In Compose screens, collect ViewModel state with `collectAsStateWithLifecycle()` (not `observeAsState` on LiveData where avoidable; prefer converting LiveData to StateFlow at the ViewModel level first).
- Use `hiltViewModel()` from `hilt-navigation-compose` for ViewModel injection inside composables.
- Navigation: migrate the Fragment nav graph to a `NavHost` with composable destinations. Use `NavController` passed down to screens; do not access it from ViewModels.
- While screens are being migrated incrementally, use `ComposeView` inside a Fragment as the interop bridge — avoid mixing both patterns long-term.

**Proposed migration order**: Splash → Home → ExpenseEntry → ExpenseLogs → Statistics → Settings → Backup → Onboarding → remaining dialogs/fragments.

## Testing

Unit tests use **JUnit 4**, **MockK**, **Mockito-Kotlin**, and **Robolectric** (for Android framework access without a device). Instrumentation tests use **Espresso** and **Fragment Testing**.

- Unit tests: `app/src/test/java/com/arduia/expense/`
- Instrumentation tests: `app/src/androidTest/java/`
- `isIncludeAndroidResources = true` is set so Robolectric tests can access resources.
- Hilt testing support is included (`hilt-android-testing`) — use `@HiltAndroidTest` + `HiltAndroidRule` for DI in tests.

## Build Flavors

| Flavor | App ID | Purpose |
|---|---|---|
| `dev` | `com.arduia.expense.dev` | Development builds — can be installed alongside production |
| `production` | `com.arduia.expense` | Play Store / release builds |

Both flavors use the same `debug` / `release` build types, giving four variants: `devDebug`, `devRelease`, `productionDebug`, `productionRelease`.
