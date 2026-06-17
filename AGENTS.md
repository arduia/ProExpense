# Finance Tracker — Agent Instructions

> Authoritative agent instructions for the Finance Tracker project (Android ships as **Pro Expense**).
> Product vision and MVP scope: `docs/finance_tracker_product.md`. Takes precedence over skills
> and general AI knowledge.

---

## Project Overview

### Project Goal

Provide a **finance tracking service** that lets anyone record and track personal finances in the
easiest, most effortless way possible — offline-first, private, and without bank or third-party
integrations.

**Product vision** (`docs/finance_tracker_product.md`):

> To support tracking and recording of personal finances in the easiest and most effortless way possible.

**Core principles:**
- **Simplicity first** — no clutter, no overwhelming dashboards
- **Speed** — logging should take seconds, not minutes
- **No dependencies** — works without bank integrations or account linking
- **Personal & private** — feels like your own notebook; data stays on device (MVP)
- **Accessible to everyone** — not just finance-savvy users
- **Global-ready** — multi-currency support for users worldwide

**Engineering goals:**
- Integrity, user data privacy, security, usefulness, performance, simplicity, UI/UX
- Clean architecture: maintainability, scalability, code quality
- Offline-first: fully functional without internet

**MVP scope** (build toward):
- Quick manual logging · Multi-currency (basic, manual rates) · Record history
- Shared costs · Secure import/export (CSV/JSON) · Auth setup (PIN)
- Local storage only — no cloud sync in MVP

The Android codebase (`refactor/v2-migration`) is the current implementation vehicle — v2 architecture
refresh (Compose migration, improved maintainability) aligned with the product roadmap. Target
platforms: iOS + Android (KMP shared logic per product doc).

**Product constraints (never violate for MVP):**
- No bank or third-party integrations
- No user accounts or server-side auth (PIN is local only)
- No cloud sync or online backup in MVP
- Data ownership — export/import supported; user owns their data
- Max expense amount: 999,999,999.99

**Stack (current Android codebase):** Kotlin 2.2 · KMP · Jetpack Compose · Min SDK 24 / Target SDK 36

**Stack (product target):** Kotlin Multiplatform shared logic · SwiftUI (iOS) · Jetpack Compose (Android) · Room (Android) · CoreData (iOS)

---

## Architecture

```
Compose screen / SwiftUI view
    ↓
ViewModel (platform UI layer)
    ↓
feature:* repository contracts (KMP commonMain)
    ↓
core:data contracts → platform storage impl (Room / CoreData)
```

See `docs/module_structure.md` for the full module map.

### Module Structure

```
ProExpense/
├── app/                         Android Compose shell
├── shared/                      KMP platform utilities
├── core/
│   ├── domain/                  Shared domain models (Amount, FinanceRecord, …)
│   ├── data/                    Repository contracts, Result wrapper
│   └── storage/                 Local persistence contracts
├── feature/
│   ├── logging/                 Quick Manual Logging (MVP)
│   ├── currency/                Multi-Currency (MVP)
│   ├── history/                 Record History (MVP)
│   ├── sharedcost/              Shared Costs (MVP)
│   ├── auth/                    PIN Auth (MVP)
│   └── importexport/            Import & Export (MVP)
└── iosApp/                      SwiftUI shell (future)
```

### Dependency Rules

| Module | Can depend on |
|--------|---------------|
| `app` | all `core:*`, all `feature:*`, `shared` |
| `shared` | nothing (project modules) |
| `core:domain` | `shared` |
| `core:data` | `core:domain`, `shared` |
| `core:storage` | `core:domain`, `shared` |
| `feature:*` | `core:domain`, `core:data`, `shared` |
| `feature:*` | **must not** depend on other `feature:*` modules |

### Key Patterns

- **KMP feature modules** — one module per MVP use case; business rules in `commonMain`
- **Repository pattern** — contracts in `core:data` / `feature:*`; implementations in platform source sets
- **Result wrapper** — sealed `Result<T>` in `core:data` for async outcomes
- **Amount** value object — stored as integer ×100 (see `core:domain`)
- **No cross-feature dependencies** — features compose only at the `app` / UI layer

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

**Prerequisites:** `local.properties` (sdk.dir)

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
AGENTS.md  >  docs/finance_tracker_product.md  >  .cursor/rules/*  >  .cursor/commands/*  >  doc/*  >  AGENTIC_WORKFLOWS_GUIDE.md  >  general AI knowledge
```

**Known conflicts (always follow AGENTS.md):**
- **Architecture:** MVVM + Repository in UI layer; KMP feature modules for shared business logic
- **UI:** Jetpack Compose (Android); SwiftUI (iOS, future)
- **Testing:** MockK/Robolectric/Espresso for Android UI tests
- **Build gate:** `./gradlew :app:compileDevDebugKotlin` or `:core:domain:testDebugUnitTest`

---

## Key File Locations

| File | Purpose |
|------|---------|
| `AGENTS.md` | Master agent instructions (this file) |
| `docs/finance_tracker_product.md` | Authoritative product vision, MVP scope, roadmap |
| `docs/module_structure.md` | KMP module map and dependency rules |
| `AGENTIC_WORKFLOWS_GUIDE.md` | Reference template (OnDeviceLab origin) |
| `.cursor/rules/` | Scoped agent rules |
| `.cursor/commands/` | Slash commands |
| `.cursor/context/project_codebase.md` | Live codebase snapshot |
| `.cursor/context/retrospectives.md` | Append-only post-mortem guard log |
| `app/build.gradle.kts` | App module build config |
| `gradle/libs.versions.toml` | Version catalog |
| `docs/module_structure.md` | KMP module map |
