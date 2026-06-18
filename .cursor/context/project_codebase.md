# Pro Expense — Codebase Snapshot

> Updated by agents during Step 2 (Explore the Codebase). Keep concise — file paths and key patterns only.

**Last updated:** 2026-06-18  
**Branch:** `refactor/v2-migration`  
**PR base (v2 migration):** All PRs target `refactor/v2-migration` until the migration is complete — not `main`.  
**Session branch:** One working branch per agent session unless the user explicitly requests another.

---

## Modules

| Module | Package root | Purpose |
|--------|-------------|---------|
| `:app` | `com.arduia.expense` | Main app — UI, data, DI |
| `:shared` | `com.arduia.core` | Extensions, `Mapper`, locale |
| `:backup` | `com.arduia.backup` | Excel backup (JXL) |
| `:expense-backup` | `com.arduia.expense.backup` | Backup schema |
| `:currency-store` | `com.arduia.currencystore` | Currency rates |
| `:week-expense-graph` | `com.arduia.graph` | Weekly spend graph |

## App Package Layout (`com.arduia.expense`)

```
ExpenseApplication.kt          @HiltAndroidApp entry
di/                            18 Hilt modules
data/
  local/                       Room (ProExpenseDatabase v6), DAOs, entities
  network/                     Retrofit DTOs
  backup/                      Backup workers/repos
  update/                      Version check
domain/                        Amount, ExpenseStore, filters
model/                         Result, FlowResult
ui/
  MainActivity.kt              NavHost + drawer
  home/                        Home screen
  entry/                       Expense entry
  expenselogs/                 Expense list (Paging)
  statistics/                  Category statistics
  backup/                      Import/export
  settings/                    App settings
  onboarding/                  First-run setup
  splash/                      Splash screen
  about/                       About + force upgrade
  feedback/                    User feedback
  common/                      Shared UI utilities
```

## Key Files

| File | Role |
|------|------|
| `app/build.gradle.kts` | Flavors (dev/production), deps, test config |
| `gradle/libs.versions.toml` | Version catalog |
| `app/src/main/res/navigation/main_nav.xml` | Navigation graph |
| `app/src/main/java/com/arduia/expense/data/local/ProExpenseDatabase.kt` | Room DB |
| `app/src/main/java/com/arduia/expense/di/RepositoryModule.kt` | Repository bindings |
| `api.properties` | `main_url` for Retrofit |

## UI State (current)

- **Primary:** Fragments + View Binding + Navigation Component
- **Compose:** Dependencies wired, zero `@Composable` in main source yet
- **State:** LiveData + Flow via `arduia/mvvm-core`

## Test Patterns

- Unit: `app/src/test/java/com/arduia/expense/` — MockK, Robolectric
- Substantive: `ExpenseRepositoryTest`, `AmountTest`, `MainViewModelTest`
- Fragment smoke tests: instantiation checks
- Instrumented: placeholder only

## Build Variants

- `devDebug` / `devRelease` — `com.arduia.expense.dev`
- `productionDebug` / `productionRelease` — `com.arduia.expense`

## Repositories

- `ExpenseRepository` — core expense CRUD
- `SettingsRepository` — preferences
- `CurrencyRepository` — currency selection
- `BackupRepository` — local backup import/export
- `ProExpenseServerRepository` — feedback, version check
