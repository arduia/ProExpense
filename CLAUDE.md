# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build Commands

```bash
# Debug build (dev flavor)
./gradlew :app:assembleDevDebug

# Release build (production flavor)
./gradlew :app:assembleProductionRelease

# Run all unit tests
./gradlew test

# Run unit tests for a specific module
./gradlew :app:test

# Run a single test class
./gradlew :app:test --tests "com.arduia.expense.ui.home.molecule.HomePresenterTest"

# Install dev debug on connected device
./gradlew :app:installDevDebug
```

The app has two product flavors: `dev` (app ID `com.arduia.expense.dev`) and `production` (app ID `com.arduia.expense`). Use `dev` for local development.

`api.properties` at the root must exist with a `main_url` key — it is read by `app/build.gradle.kts` to set `BASE_URL` as a BuildConfig field.

## Architecture

**MVVM + Molecule Presenter pattern.** Each feature screen has three layers:

1. **Presenter** (`molecule/` subpackage) — a `@Composable` function that holds all state via `remember`/`LaunchedEffect`, consumes a `Flow<Event>` and returns a `State` data class. This is the core business logic unit; it runs on the Compose runtime via [Molecule](https://github.com/cashapp/molecule).
2. **ViewModel** — thin wrapper that instantiates the Presenter, drives `moleculeFlow`, and exposes `state: StateFlow<State>` and a `take(event)` function.
3. **Fragment** — hosts a `ComposeView`, collects `viewModel.state`, renders the Compose screen, and imperatively handles navigation (via `findNavController()`) and legacy side-effects by observing `LaunchedEffect` blocks on navigation-related state fields.

Example: `HomePresenter` / `HomeViewModel` / `HomeFragment` / `HomeScreen` (in `ui/home/`).

## Module Structure

| Module | Purpose |
|---|---|
| `:app` | Main app module — all UI, ViewModels, DI modules, Room database |
| `:design-system` | Jetpack Compose design tokens (`ProExpenseTheme`, `Color`, `Type`, `Shape`) and reusable components (`ProExpenseButton`, `ProExpenseTextField`, `ProExpenseCard`, `ProExpenseSearchField`) |
| `:shared` | Shared utilities and common logic |
| `:backup` / `:expense-backup` | Export/import and data persistence logic |
| `:currency-store` | Currency data and exchange rate handling |
| `:week-expense-graph` | Graph widget for weekly spend visualisation |

Always import theme and components from `:design-system` (`com.arduia.design.*`) rather than redefining them in `:app`.

## Navigation

Navigation is **Fragment-based** (single-activity, `main_nav.xml`). The flow is:

```
SplashFragment → OnBoardingConfigFragment (first launch)
              → HomeFragment (returning users)
HomeFragment  → ExpenseEntryFragment (add/edit)
              → ExpenseFragment (logs)
```

`MainActivity` uses `setContent { MainScreen(...) }` with a Compose `ModalNavigationDrawer`, but embeds the actual Fragment NavHost via `AndroidView` (file: `ui/compose/MainScreen.kt`). Navigation between destinations still uses `findNavController().navigate(...)` inside Fragments.

Top-level drawer destinations are declared in both `MainActivity.TOP_DESTINATIONS` (companion object) and `ui/compose/MainScreen.kt`'s `TOP_DESTINATIONS` — keep them in sync.

## Compose Migration Status

The codebase is mid-migration from XML Fragments to Jetpack Compose. The pattern for migrated screens is:

- Fragment `onCreateView` returns a `ComposeView`
- Compose screen handles UI
- Fragment handles navigation side-effects by observing state fields like `openDrawer`, `navigateToLogs`, `navigateToEntryId`

**Fragments still using legacy ViewBinding** (not yet migrated):

- `SplashFragment`, `WebFragment`
- `ExportDialogFragment`, `ImportDialogFragment`
- `OnBoardingConfigFragment`, `ChooseCurrencyFragment`
- `ExpenseDetailDialog`, `DeleteConfirmFragment`, `ExpenseFilterDialogFragment`
- `ChooseCurrencyDialog` (settings)

See `docs/unrefactored-fragments.md` for the full list.

## Testing

Presenter tests use **Molecule + Turbine**:

```kotlin
moleculeFlow(RecompositionMode.Immediate) {
    presenter.present(events)
}.test {
    val state = awaitItem()
    // assert on state
}
```

Use `MainDispatcherRule` (in `utils/`) to replace `Dispatchers.Main` in unit tests. Use MockK for mocking. The helper `testMolecule { }` in `utils/MoleculeTestExt.kt` wraps the boilerplate.

Fragment tests use Robolectric + Hilt testing. Annotate with `@HiltAndroidTest` and use `launchFragmentInHiltContainer`.

## Dependency Injection

Hilt is the DI framework. All Hilt modules live in `app/src/main/java/com/arduia/expense/di/`. Modules are abstract (`AbstractXxxModule`) for bindings and concrete for `@Provides`. Room is provided via `DatabaseModule`. Navigation options (`NavOptions`) with qualifiers like `@TopDropNavOption` are injected into Fragments.

## Key Conventions

- **Amount** is a domain type (`domain/Amount.kt`) — never use raw `Double` for money values.
- `FlowResult<T>` / `SuccessResult<T>` / `ErrorResult` wrap all repository emissions. Always check for `SuccessResult` before accessing `.data`.
- Expense categories include `INCOME` as a special category — filter it out when computing outcome totals (`category != ExpenseCategory.INCOME`).
- The database is Room (`ProExpenseDatabase`). Schema export path is `app/schemas/`.