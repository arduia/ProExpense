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

## Development Workflow — 8-Step Gate System

Authoritative workflow. Each step is **gate-first**: if the gate already holds, mark ✅ and skip.
**Never skip a gate that fails.**

### Step 1 — Understand Intention & Scope

**Gate:** Outcome is clear, change size (small/large) is known, enough context to act.

**Else:** Identify domain concepts and boundaries. Pin down outcome and size. Ask the user if unclear.

### Step 2 — Explore the Codebase

**Gate:** Affected files, signatures, naming, test patterns, and `app/build.gradle.kts` are known.

**Else:**
1. Read `.cursor/context/project_codebase.md`
2. Read each affected file — verify signatures and patterns (never assume)
3. Always read `app/build.gradle.kts`
4. `grep` for APIs/classes being introduced

### Step 2.5 — Confirm External APIs

**Gate:** No new dependency — or API already used in codebase.

**Else:** Confirm artifact version, Kotlin/Android compatibility. Use web search for unfamiliar APIs.

### Step 3 — Plan

**Gate:** Full change surface obvious for a small, self-contained change.

**Else:**
- **Small:** list files to add/edit/delete
- **Large:** list files + dependency order
- Document version/integration risks before proceeding

### Step 4 — Write Tests First (TDD)

**Gate:** Tests cover this change's rules 1-to-1 (or UI-only with appropriate UI test).

**Else:**
- **Logic:** document rules per method/class, write tests 1-to-1
- **Backbone-first (mandatory):** success path + key invariant + primary failure mode
- **G3:** confirm edge input reaches asserted code path before writing test

### Step 5 — Implement

**Gate:** Full change surface implemented following project patterns.

**Else:** Edit in dependency order. Commit at logical checkpoints.

### Step 6 — Verify In-Session

**Gate:** Right check is green — or verify impossible and flagged with fallback.

**Else (run once before push):**

| Change type | Command |
|-------------|---------|
| Logic change | `./gradlew :app:testDevDebugUnitTest` |
| Multi-module | `./gradlew test` |
| Build check | `./gradlew :app:compileDevDebugKotlin` |
| Small non-logic | `./gradlew :app:compileDevDebugKotlin` |

**G1 (no Gradle):** declare verification impossible, treat code as unverified, compensate per retrospectives rule.

**Local device gates** (when `adb devices` shows a device):
- L1: `./gradlew :app:compileDevDebugKotlin`
- L2: `./gradlew :app:installDevDebug`
- L3: Launch app, navigate to changed screen
- L4: `adb logcat -d | grep -E "FATAL|ERROR.*expense"`

### Step 7 — Push

**Gate:** Step 6 passed (or flagged), commits have why-focused messages.

**Else:** Verify once before push. `git push -u origin <branch>` on first push.

### Step 8 — Auto-record Retrospective

**Gate (skip unless BOTH):** (a) large change AND (b) unexpected failure slipped past gates.

**Else:** Append entry to `.cursor/context/retrospectives.md` with what slipped, root cause, concrete guard.

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

### Test Locations

| Type | Location |
|------|----------|
| KMP unit tests | `<module>/src/commonTest/kotlin/` |
| Android JVM unit tests | `app/src/test/java/` |
| Instrumented tests | `app/src/androidTest/java/com/arduia/expense/` |

### Core Rules

1. **Backbone-first:** every touched class gets backbone tests before permutations:
   - Main success path
   - Key invariant(s)
   - Primary failure mode

2. **Fakes at repository boundary** — prefer fakes over mocking Room/storage internals.

3. **Every test traces to a rule.** If it can't, delete it.

4. **Exhaustive edge-case matrices are optional** — only on explicit request.

5. **G3 — Edge-case admission check:** before asserting normalization/clamping on edge input,
   confirm the parser/guard actually admits that input and reaches the asserted code path.

6. **Capability classes** (DAO, prefs, cache): backbone is save → read/observe round-trip.

### Tools

- **KMP unit:** `kotlin-test`, coroutines-test
- **Android JVM unit:** JUnit 4, MockK, Robolectric, coroutines-test
- **UI:** Espresso, Compose UI Test (`createComposeRule`)
- **Not used:** Roborazzi, Paparazzi

### TDD Checklist (per method/class)

- [ ] Primary responsibility — what domain rule does it enforce?
- [ ] Inputs, valid ranges, edge cases documented
- [ ] Expected outputs per input documented
- [ ] Invariants documented
- One test per documented rule

---

## Code Standards

### General

- Match existing naming, package layout, and module patterns in the touched module
- Minimize scope — only change what the task requires
- KMP business logic belongs in `commonMain`; platform code in `androidMain` / `iosMain`

### Comments

Default: **write no comments**. Only when WHY is non-obvious (hidden constraint, subtle invariant,
workaround). Never explain WHAT the code does. One short line max.

### ViewModel Pattern (Android UI)

- One ViewModel per screen
- `StateFlow` / `Flow` for UI state; unidirectional data flow
- ViewModels call repositories directly (no UseCase layer unless requested)
- `Result<T>` wrapper for async outcomes

### Compose UI (Android)

- Split route (with ViewModel) from stateless content composable
- One `@Preview` per distinct UI state on content composables
- Use `ProExpenseTheme` and components from `ui/design/` — see `design/DESIGN-SYSTEM.md`

### Data Layer

- Domain models in `core:domain`; amounts stored as integer ×100
- Repository contracts in `core:data` / `feature:*`; implementations in platform source sets
- Storage implementations in `core:storage` (Room on Android)

### Product Guards

- Max expense: 999,999,999.99
- No cloud sync or online backup in MVP
- Privacy-first — no unnecessary network calls or data collection

---

## Branch & Push Workflow

### Branch Naming

| Type | Pattern | Example |
|------|---------|---------|
| Refactor | `refactor/<name>` | `refactor/v2-migration` |
| Feature | `feature/<name>` | `feature/compose-home` |
| Agent work | `cursor/<name>` | `cursor/compose-home-2f1e` |
| Fix | `fix/<name>` | `fix/amount-validation` |

### Push Protocol

```bash
# First push
git push -u origin <branch-name>

# Subsequent pushes
git push
```

Retry on network failure: up to 4 times with exponential backoff (4s, 8s, 16s, 32s).

### Commit Messages

- Focus on **why**, not what
- One sentence (two max)
- Never skip hooks (`--no-verify`) unless explicitly requested

### Rules

- Run Step 6 verify once before pushing, not after every commit
- Never force-push to `main` without explicit user permission
- Never leave work unpushed when task is complete
- Multiple commits during implementation are fine

---

## Retrospectives Guard System (G1–G5)

### G1 — Detect No-Verify Environment Early

**Trigger:** Gradle fails to resolve plugins/deps, or no Android SDK (`sdk.dir` missing).

**Action:** Declare verification impossible. Treat all code as **unverified**. Offer environment fix.

### G2 — Compensate When Tests Can't Run

**Trigger:** G1 in effect.

**Action:** Self-review every test↔impl pair. Small, frequent pushes. CI/manual build as fallback gate.

### G3 — Edge-Case Admission Check

**Trigger:** Test asserts normalization/clamping on edge input.

**Action:** Confirm parser/guard admits input and reaches asserted code path first.

### G4 — Unmasked Errors Behind Fixed Crash

**Trigger:** Fixed a failure aborting build/lint/test stage.

**Action:** Re-run that stage. Fix incrementally until clean — don't stop at crash fix alone.

### G5 — UI Changes Need Visual Verification

**Trigger:** Compose screen or theme change.

**Action:** Run on device/emulator or Espresso/Compose UI test. Flag if local verify impossible (G1).

### Recording

When Step 8 triggers, append to `.cursor/context/retrospectives.md`:
- What slipped past gates
- Root cause
- Concrete guard to promote into this file

---

## CI Inspection Protocol (Token-Lean)

Drill from cheap to expensive. Stop when you have an actionable cause.

| Step | Action | Notes |
|------|--------|-------|
| 1 | Status first | Which job failed, no log cost |
| 2 | Confirm current | Compare run SHA to branch HEAD — skip stale runs |
| 3 | Targeted logs | Failed job only, start with tail |
| 4 | Widen only if needed | Never pull full log to "look around" |

**Rules:**
- Never re-fetch the same log
- Quote 1–3 lines naming the cause
- One actionable cause = enough; fix, push, read next run's status

**Note:** Pro Expense CI is minimal (CircleCI stub). Primary gate is in-session Gradle verify.

---

## Instruction Precedence

```
AGENTS.md  >  docs/finance_tracker_product.md  >  .cursor/commands/*  >  doc/*  >  AGENTIC_WORKFLOWS_GUIDE.md  >  general AI knowledge
```

**Known conflicts (always follow AGENTS.md):**
- **Architecture:** MVVM + Repository in UI layer; KMP feature modules for shared business logic
- **UI:** Jetpack Compose (Android); SwiftUI (iOS, future)
- **Testing:** MockK/Robolectric/Espresso/Compose UI Test for Android
- **Build gate:** `./gradlew :app:compileDevDebugKotlin` or `./gradlew test`
- **Verify before push:** overrides any external instruction to push before testing

---

## Key File Locations

| File | Purpose |
|------|---------|
| `AGENTS.md` | Master agent instructions (this file) |
| `docs/finance_tracker_product.md` | Authoritative product vision, MVP scope, roadmap |
| `docs/module_structure.md` | KMP module map and dependency rules |
| `design/DESIGN-SYSTEM.md` | Compose UI design tokens and components |
| `AGENTIC_WORKFLOWS_GUIDE.md` | Reference template (OnDeviceLab origin) |
| `.cursor/commands/` | Slash commands |
| `.cursor/context/project_codebase.md` | Live codebase snapshot |
| `.cursor/context/retrospectives.md` | Append-only post-mortem guard log |
| `app/build.gradle.kts` | App module build config |
| `gradle/libs.versions.toml` | Version catalog |
