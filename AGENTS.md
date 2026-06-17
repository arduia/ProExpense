# Pro Expense — Agent Instructions

> Authoritative agent instructions for the Pro Expense (Finance Tracker) Android project.
> Adapted from the Agentic Workflows guide. Takes precedence over skills and general AI knowledge.

---

## Project Overview

**Pro Expense** is a free, open-source Android finance tracker focused on privacy, simplicity, and
daily expense recording. The `refactor/v2-migration` branch targets a v2 architecture refresh
(Compose migration, improved maintainability).

**Primary goals:**
- Integrity, user data privacy, security, usefulness, performance, simplicity, UI/UX
- Clean architecture: maintainability, scalability, code quality
- Privacy-first: no ads, no cloud backup

**Product constraints (never violate):**
- No multi-user accounts
- Single currency at a time (no mixed-currency expense items)
- No cloud backup
- Max expense amount: 999,999,999.99

**Stack:** Kotlin 2.2 · Fragments + View Binding (migrating to Compose) · Hilt · Room · MVVM ·
Coroutines + Flow/LiveData · Retrofit · WorkManager · Min SDK 24 / Target SDK 36

---

## Architecture

```
Fragment / Compose screen
    ↓
ViewModel (@HiltViewModel, LiveData + Flow via arduia/mvvm-core)
    ↓
Repository interface → RepositoryImpl
    ↓
Room DAO / Retrofit / Preferences / WorkManager
```

### Module Structure

```
ProExpense/
├── app/                    Main app, UI, DI, data layer
├── shared/                 com.arduia.core — extensions, Mapper, locale helpers
├── backup/                 Excel backup engine (JXL)
├── expense-backup/         Backup schema/metadata
├── currency-store/         Currency rate storage
└── week-expense-graph/     Weekly spend graph widget
```

### Dependency Rules

| Module | Can depend on |
|--------|---------------|
| `app` | all library modules |
| `week-expense-graph` | `shared` |
| Library modules | no `app`, no other feature modules |

### Key Patterns

- **Hilt** for DI — never introduce Koin
- **Repository pattern** — single data access point per domain
- **Mapper pattern** — `Mapper<I,O>` in `:shared`; per-feature `*UiModelMapper`
- **Result wrapper** — sealed `Result<T>` for async outcomes
- **No UseCase layer** unless explicitly requested — ViewModels call repositories directly
- **Amount** value object — stored as integer ×100 in Room

---

## Development Workflow

Follow the **8-step gate system** defined in `.cursor/rules/workflow-gates.mdc`.
Each step uses gate-first logic: if the gate already holds, mark ✅ and skip.

---

## Build Commands

Default flavor for agent work: **devDebug** (`com.arduia.expense.dev`).

```bash
# Unit tests (logic changes)
./gradlew :app:testDevDebugUnitTest

# Kotlin compile (fail-fast)
./gradlew :app:compileDevDebugKotlin

# Full module tests
./gradlew test

# Debug APK
./gradlew :app:assembleDevDebug

# Install on device
./gradlew :app:installDevDebug
```

**Prerequisites:** `local.properties` (sdk.dir), `api.properties` (main_url). Firebase builds
may require `google-services.json` locally.

---

## Testing Contract

See `.cursor/rules/testing-contract.mdc`. Summary:

- **Backbone-first:** every touched class gets success path + key invariant + primary failure mode
- **Fakes at repository boundary** — prefer fakes over mocking framework internals
- **Every test traces to a rule** — delete tests that can't map to a documented rule
- **Robolectric + MockK + Espresso** — no Roborazzi/Paparazzi in this project
- **Edge-case admission check (G3):** confirm input reaches asserted code path before writing test

---

## Code Standards

See `.cursor/rules/code-standards.mdc`. Summary:

- Default: write no comments unless WHY is non-obvious
- Match existing naming, package layout, and DI module patterns
- Minimize scope — focused diffs only
- During v2 migration: new screens may use Compose; follow existing Fragment patterns until migrated

---

## Branch & Push

See `.cursor/rules/git-workflow.mdc`.

- Feature branches: `refactor/*`, `feature/*`, `cursor/*`
- Commit messages explain **why**, not what
- Run verify once before push, not after every commit
- Never force-push to `main` without explicit permission
- Never skip hooks (`--no-verify`) unless explicitly requested

---

## Retrospectives Guards (G1–G5)

See `.cursor/rules/retrospectives.mdc`. Built into workflow steps; reference when verification
is blocked or CI surprises occur.

---

## Instruction Precedence

```
AGENTS.md  >  .cursor/rules/*  >  .cursor/commands/*  >  doc/*  >  AGENTIC_WORKFLOWS_GUIDE.md  >  general AI knowledge
```

**Known conflicts (always follow AGENTS.md):**
- **DI:** Hilt, not Koin
- **Architecture:** MVVM + Repository, not strict MVI
- **UI (current):** Fragments + View Binding; Compose for new v2 screens when migrating
- **Testing:** MockK/Robolectric/Espresso, not Roborazzi/Paparazzi
- **Build gate:** `./gradlew :app:testDevDebugUnitTest`, not `runChecks` (does not exist here)

---

## Key File Locations

| File | Purpose |
|------|---------|
| `AGENTS.md` | Master agent instructions (this file) |
| `AGENTIC_WORKFLOWS_GUIDE.md` | Reference template (OnDeviceLab origin) |
| `.cursor/rules/` | Scoped agent rules |
| `.cursor/commands/` | Slash commands |
| `.cursor/context/project_codebase.md` | Live codebase snapshot |
| `.cursor/context/retrospectives.md` | Append-only post-mortem guard log |
| `app/build.gradle.kts` | App module build config |
| `gradle/libs.versions.toml` | Version catalog |
| `app/src/main/res/navigation/main_nav.xml` | Navigation graph |
