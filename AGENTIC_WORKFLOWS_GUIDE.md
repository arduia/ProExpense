# OnDeviceLab - Agentic Workflows, Config & Instructions Guide

> A complete reference for using Claude Code (or any Claude Agent) with the OnDeviceLab Android
> project. Use this as a drop-in guideline for other projects using a similar Claude Code setup.

---

## Table of Contents

1. [Project Overview](#1-project-overview)
2. [Architecture](#2-architecture)
3. [Session Configuration](#3-session-configuration)
4. [Development Workflow (8-Step Gate System)](#4-development-workflow-8-step-gate-system)
5. [Build Commands Reference](#5-build-commands-reference)
6. [Build Verification Gate](#6-build-verification-gate)
7. [Testing Contract](#7-testing-contract)
8. [Code Standards](#8-code-standards)
9. [CI/CD Pipeline](#9-cicd-pipeline)
10. [Branch & Push Workflow](#10-branch--push-workflow)
11. [CI Inspection Protocol (Token-Lean)](#11-ci-inspection-protocol-token-lean)
12. [Agent Skills Reference](#12-agent-skills-reference)
13. [Permissions & Allowed Commands](#13-permissions--allowed-commands)
14. [Retrospectives Guard System (G1–G5)](#14-retrospectives-guard-system-g1g5)

---

## 1. Project Overview

**OnDeviceLab** is an Android learning lab for on-device AI models (ML Kit, MediaPipe, LLM
Inference, TFLite). It is an educational sandbox, not a production app.

**Primary goals:**
- Hands-on practice with ML Kit, MediaPipe, LLM Inference, TFLite
- Understand model behavior, performance, edge cases, and limitations
- Compare models to build intuition about when to use each
- Document insights for personal skill improvement

**Philosophy:**
- Prioritize understanding over optimization
- Document *why* something works, not just *that* it works
- Embrace failures as learning opportunities
- No production constraints — experiment freely

**Stack:** Kotlin 2.0 · Jetpack Compose · Hilt · Material 3 · ML Kit · MediaPipe LLM Inference
SDK · Coroutines + StateFlow · Roborazzi · Min SDK 26 / Target SDK 35

---

## 2. Architecture

```
UI (Jetpack Compose)
    ↓
ViewModel (StateFlow — one per screen)
    ↓
Use Case (validation + orchestration)
    ↓
Repository interface (Hilt-injected)
    ↓
On-device model (ML Kit / MediaPipe / LLM / TFLite)
```

### Module Structure

```
OnDeviceLab/
├── build-logic/convention/     7 Gradle convention plugins
├── core/                       KMP commonMain: domain types, repository interfaces, ModelCatalog
│   └── androidMain/            InitializeLlmUseCase (uses java.io.File)
├── design-system/              Android: Material 3 theme + shared Compose components
├── preferences/                Android: ModelPreferences (DataStore) — shared across features
├── feature/
│   ├── language-detection/     Screen + VM + UseCase + RepoImpl + Hilt module
│   ├── llm-chat/               Screen + VM + UseCases + LlmRepoImpl + ThinkingParser + Hilt module
│   ├── prompt-lab/             Screen + VM + UseCase + domain logic (MD parser, WordDiff, etc.)
│   ├── gallery/                Screen + VM + DownloadRepoImpl + DownloadWorker + Hilt module
│   └── settings/               Screen + VM
└── app/                        MainActivity, OnDeviceLabApplication, AppNavHost
```

### Dependency Rules (Enforced)

| Module | Can depend on |
|--------|---------------|
| `app` | `feature/*`, `preferences/`, `design-system/`, `core/` |
| `feature/*` | `core/`, `design-system/`, `preferences/` — **NEVER other features** |
| `preferences/` | `core/` |
| `design-system/` | `core/` |

### New File Order

Always add files in dependency order within a feature module:

```
data/model/XxxResult.kt          (if truly feature-local) or core/commonMain/
  ↓
data/repository/XxxRepoImpl.kt
  ↓
di/XxxModule.kt
  ↓
domain/usecase/XxxUseCase.kt
  ↓
ui/viewmodel/XxxViewModel.kt
  ↓
ui/screen/XxxScreen.kt
```

### Key DI Rules

- **Always use Hilt** — never Koin.
- `@HiltAndroidApp` on the Application class.
- Hilt module per feature in `di/XxxModule.kt`, scoped to the right lifecycle.

---

## 3. Session Configuration

### SessionStart Hook (`.claude/hooks/session-start.sh`)

Runs automatically at the start of every remote (web) Claude Code session. Skipped on local machines.

**What it does:**
1. Installs Android SDK command-line tools if missing (`$HOME/android-sdk`)
2. Accepts SDK licenses (enables AGP to auto-download platforms/build-tools)
3. Regenerates `local.properties` (gitignored, needed per-clone)
4. Sets `ANDROID_HOME` and `ANDROID_SDK_ROOT` environment variables
5. Warms Gradle distribution, dependency, and SDK-component caches via `compileDebugKotlin`

**Key detail:** Cache warming is best-effort — a source compile error does not block session start.

```bash
# local.properties regenerated each fresh clone:
echo "sdk.dir=$SDK_DIR" > "$CLAUDE_PROJECT_DIR/local.properties"

# Gradle warm-up (best-effort):
./gradlew --quiet compileDebugKotlin compileDebugUnitTestKotlin
```

### Settings Files

**`.claude/settings.json`** — project-level (checked into git):
```json
{
  "hooks": {
    "SessionStart": [
      {
        "hooks": [
          {
            "type": "command",
            "command": "$CLAUDE_PROJECT_DIR/.claude/hooks/session-start.sh",
            "timeout": 1800
          }
        ]
      }
    ]
  }
}
```

**`.claude/settings.local.json`** — local machine overrides (gitignored):
```json
{
  "permissions": {
    "allow": [
      "Bash(git commit *)",
      "Bash(./gradlew runChecks)",
      "Bash(./gradlew :app:assembleDebug)",
      "mcp__7b0476b8-570b-4cdb-aab1-def3d0309345__resolve-library-id",
      "mcp__7b0476b8-570b-4cdb-aab1-def3d0309345__query-docs"
    ]
  }
}
```

### Local vs Remote Context

At session start, run `adb devices`:
- **Connected device detected** → Step 6's local-machine gates (L1–L5) apply.
- **No device** (remote session) → Skip L1–L5; in-session Gradle build gate is the runtime gate.

---

## 4. Development Workflow (8-Step Gate System)

This is the authoritative development workflow. Each step uses a **gate-first** pattern: read the
Gate, and if it already holds, mark it ✅ and skip — only run the Else actions when the gate fails.
**Never skip a gate that fails.**

---

### Step 1 — Understand Intention & Scope

**Gate:** The asked outcome is clear, the change size (small/large) is known, and there is enough
context to act.

**Else:** Apply DDD:
- Identify domain concepts, boundaries, and invariants
- Name things in domain language (not technical jargon)
- Pin down the outcome and size
- Ask the user if it's still unclear after analysis

**DDD Checklist:**
- [ ] Domain concepts and boundaries named in domain language
- [ ] Invariants identified (what must always be true?)
- [ ] Feature understood from user perspective before deciding how to build

---

### Step 2 — Explore the Codebase

**Gate:** The affected files (signatures, naming, test patterns) and `app/build.gradle.kts` are
already known this session, and any new API's existing usage is confirmed.

**Else:**
1. Read `.claude/context/project_codebase.md` — locate affected files from the snapshot
2. Read each affected file directly — verify signatures, naming, and test patterns (never assume)
3. Always read `app/build.gradle.kts`
4. If the area isn't in the snapshot, `find`/`grep` to locate it, then update the snapshot
5. `grep` for any API/class being introduced — note if already used in codebase

---

### Step 2.5 — Confirm External APIs

**Gate:** No new dependency/API — or the API is already in session context or grep found existing
usage.

**Else:**
- Dep exists but API is new to this project → Context7 MCP (`query-docs`)
- Dep is new → find artifact ID + version; confirm Kotlin 2.0 + API 26+ compatible; no alpha
  unless intentional
- Context7 has no results → fall back to WebSearch

---

### Step 3 — Plan

**Gate:** The full change surface is obvious from Step 2 — files to add/edit/delete, their order,
and any risk — for a small, self-contained change.

**Else:**
- **Small change:** list files to add/edit/delete only
- **Large change:** list files + note dependency order between them
- New files must follow the pattern: model → repository → use case → ViewModel → screen
- Document any version/integration risk from Step 2.5 with a mitigation before proceeding

---

### Step 4 — Write Tests First (DDD → TDD)

**Gate:** Tests already cover this change's rules 1-to-1 (or it's UI-only with a Roborazzi
screenshot per affected state).

**Else:**
- **UI-only change:** add/update Roborazzi screenshot per affected UI state; skip domain doc
- **Logic change:** document per method/class in the test file (responsibility, inputs, outputs,
  invariants), then write tests 1-to-1 with documented rules

**Backbone-first testing (mandatory):**
For every touched class, write backbone tests first:
- Core responsibility: main success path
- Key invariant(s)
- Primary failure mode

These are never optional. Exhaustive permutations only on explicit request.

**Capability test pattern:** For classes whose job is enabling a capability (prefs helper,
database/DAO, cache), the backbone is a **save → read/observe round-trip test**.

**Rules:**
- Every test must trace to a rule — if it can't, delete it
- Never mock on-device models (ML Kit, MediaPipe) — use fakes at the repository boundary only
- **Edge-case admission check (G3):** when a test asserts normalization/clamping/coercion on an
  edge input, confirm the parser/guard/regex actually *admits* that input and reaches the asserted
  code path first

**TDD Checklist (per method/class):**
- [ ] Primary responsibility — what domain rule does it enforce?
- [ ] Inputs, valid ranges, and edge cases documented
- [ ] Expected outputs or state transitions per input documented
- [ ] Invariants that must always hold documented
- One test per documented rule; if a test can't trace to a rule, delete it

---

### Step 5 — Implement

**Gate:** The full change surface is implemented, following the file pattern and the Compose
preview + code-comment standards.

**Else:**
- **Small change:** edit the specific file only
- **Large change:** edit in dependency order — models → repositories → use cases → ViewModels → UI
- Follow Compose preview rule (one `@Preview` per distinct UI state, wrapped in `OnDeviceLabTheme{}`)
- Follow code comment standard (see §8)
- Commit at logical checkpoints during implementation — multiple commits are fine

---

### Step 6 — Verify In-Session

**Gate:** The right check is green and goldens are current — *or* in-session verify is impossible
and it has been flagged with a fallback.

**Else (run only once, after Step 5, immediately before push):**

| Change type | Command |
|-------------|---------|
| Logic change | `./gradlew runChecks` (tests + coverage ≥ 50% + APK) |
| UI change | `./gradlew recordRoborazziDebug` → sync goldens → `./gradlew runChecks` |
| Small change (no logic/UI) | `./gradlew testDebugUnitTest` is sufficient |

**If Gradle can't resolve plugins/deps (G1)** — e.g. Google Maven blocked (403):
- In-session verification is impossible — say so up front
- Offer to loosen the network policy
- Treat all code as **unverified**
- Compensate (G2): self-review every test↔impl pair, push small commits, use manual CI run

**After fixing a stage-aborting failure (G4):** assume real errors were hidden behind it —
re-check that stage rather than declaring victory on the crash alone.

**Local-machine extra gates** *(only when a device is detected via `adb devices`)*:

| Gate | Command | Purpose |
|------|---------|---------|
| L1 | `./gradlew compileDebugKotlin` | Fail fast on Kotlin errors before full runChecks |
| L2 | `./gradlew installDebug` | Catch APK/manifest/SDK rejections tests can't |
| L3 | Launch app, navigate to changed screen | Verify golden path renders without crash |
| L4 | `adb logcat -d \| grep -E "FATAL\|ERROR.*ondevicelab"` | Confirm no silent Hilt/init crashes |
| L5 (UI only) | `./gradlew recordRoborazziDebug` | Re-record goldens on this machine; JVM rendering differs between environments |

---

### Step 7 — Push

**Gate:** Step 6 passed (or was flagged), and commits are on the `claude/` sub-branch with
why-focused messages.

**Else:**
- Run Step 6 verify once before pushing, not after every commit
- Commit messages explain *why*, not *what*
- First push: `git push -u origin claude/<name>`
- Subsequent pushes: `git push`

---

### Step 8 — Auto-record Retrospective *(automatic — no user prompt needed)*

**Gate (skip unless BOTH hold):**
- (a) The change is **large**, AND
- (b) A build/CI failure occurred that the step gates should have caught but didn't — an
  **unexpected** failure that slipped the gates

**Else (both hold):** Immediately append one entry to `.claude/context/retrospectives.md`:
- What slipped past the gates
- Root cause
- The concrete guard

Then promote any durable guard into the steps above so the lesson is enforced, not just logged.

**Skip for:** small changes; failures predicted/flagged in advance; pre-existing unrelated failures.

---

## 5. Build Commands Reference

```bash
# Full CI gate (tests + coverage ≥ 50% + APK)
./gradlew runChecks

# Unit tests only
./gradlew testDebugUnitTest

# Coverage report → app/build/reports/jacoco/
./gradlew jacocoTestReport

# Record new screenshot goldens (Roborazzi)
./gradlew recordRoborazziDebug

# Verify screenshots match goldens
./gradlew verifyRoborazziDebug

# Build debug APK
./gradlew assembleDebug

# Install on device/emulator
./gradlew installDebug

# Build release APK
./gradlew assembleRelease

# Fail-fast Kotlin compile (local L1 gate)
./gradlew compileDebugKotlin
```

---

## 6. Build Verification Gate

The authoritative gate is the working session itself.

- Remote Claude sessions ship the Android SDK with warmed Gradle caches
- CLI sessions use the local machine
- Either way: run the gate **in-session before every push**

### Golden Sync After Re-recording

Roborazzi writes to `app/build/outputs/roborazzi/`; committed goldens live in
`app/src/test/snapshots/images/`.

After re-recording:
1. Copy new/changed images from build output to `app/src/test/snapshots/images/`
2. Delete goldens whose tests no longer exist
3. Review diffs visually (don't blind-accept)
4. Commit golden changes with the feature change

### CI Status

GitHub CI is paused (no automatic runner cost). It is an **optional secondary check**.
Trigger manually via Actions tab → `workflow_dispatch` only when cross-environment verification is
genuinely needed.

---

## 7. Testing Contract

### Test Source Locations

| Test type | Location |
|-----------|----------|
| Unit + screenshot tests | `<module>/src/test/kotlin/com/ondevicelab/` |
| Roborazzi goldens | `<module>/src/test/snapshots/images/` |
| Fake repositories | `<module>/src/test/kotlin/com/ondevicelab/fake/` |
| `MainDispatcherRule.kt` | `<module>/src/test/kotlin/com/ondevicelab/util/` |
| Instrumented tests | `app/src/androidTest/kotlin/com/ondevicelab/` |

> Fake repositories are **copied** to each consumer module, not shared via testFixtures.
> Instrumented tests require a device/emulator — use sparingly.

### Core Rules

1. **Never mock on-device models** (ML Kit, MediaPipe) in unit tests — use fakes or test doubles
   at the **repository boundary only**. Tests must exercise real domain logic.

2. **Backbone-first:** every class a change touches gets backbone tests before anything else:
   - Main success path
   - Key invariant(s)
   - Primary failure mode

3. **Capability test for enabling classes:** prove the save → read/observe round-trip works.

4. **Every test traces to a rule.** If a test can't map to a documented domain rule, delete it.

5. **Exhaustive edge-case matrices are optional** — only add them when explicitly requested.
   AI-token budget goes to backbone first.

### ML Kit Task Bridge Pattern

```kotlin
// ML Kit Task API requires main thread; suspendCancellableCoroutine bridges to coroutines
suspendCancellableCoroutine { cont ->
    task.addOnSuccessListener { cont.resume(it) }
        .addOnFailureListener { cont.resumeWithException(it) }
}
```

### Screenshot Testing (Roborazzi)

- JVM goldens (no emulator needed)
- One golden per distinct UI state: Idle, Loading, Success, Error, Empty, etc.
- Always use `dynamicColor = false` in Roborazzi runs
- Goldens committed to `app/src/test/snapshots/images/`

---

## 8. Code Standards

### Compose Preview Rule

Every `@Composable` must have a `@Preview` per distinct UI state:
- Idle, Loading, Success, Error (minimum)
- Wrapped in `OnDeviceLabTheme { }`
- Placed at the **bottom of the same file** as the composable
- Route composables (those that call `hiltViewModel()`) are **NOT previewable** — split into a
  stateless content composable

```kotlin
// Route composable (NOT previewable)
@Composable
fun HomeRoute(viewModel: HomeViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    HomeContent(uiState = uiState, onTextChanged = viewModel::onTextChanged)
}

// Content composable (IS previewable)
@Composable
fun HomeContent(uiState: HomeUiState, onTextChanged: (String) -> Unit) { ... }

@Preview @Composable
private fun HomeContentIdlePreview() {
    OnDeviceLabTheme { HomeContent(uiState = HomeUiState.Idle, onTextChanged = {}) }
}
```

### Code Comment Standard

Default: **write no comments**. Only add when the WHY is non-obvious:
- A hidden constraint
- A subtle invariant
- A workaround for a specific bug
- Behavior that would surprise a reader

For ML/AI integration points, every integration must document:
- **WHY** — why this model/approach was chosen (trade-offs, limitations)
- **HOW** — API usage, initialization, threading model
- **WHAT** — which performance metrics are being measured
- **WHEN** — model constraints (input size, language support, confidence thresholds)

Never write:
- Multi-paragraph docstrings or multi-line comment blocks (one short line max)
- Comments that explain WHAT the code does (well-named identifiers do that)
- Comments referencing the current task/fix/issue number (belongs in PR description)

### UseCase Input Validation

- Reject empty strings before calling the repository (avoid unnecessary inference)
- Document input length constraints based on model capabilities

### ViewModel Pattern

- One ViewModel per screen
- StateFlow for all state
- Sealed `...UiState` class (not strict MVI)
- ViewModel delegates business logic to UseCases

---

## 9. CI/CD Pipeline

**File:** `.github/workflows/ci.yml`

### Status

CI is currently **paused** (manual-only via `workflow_dispatch`). Re-enable by uncommenting
the `push`/`pull_request` triggers in the workflow file.

### Jobs

#### 1. `build-and-test` (ubuntu-latest)

1. Checkout repository
2. Set up JDK 21 (Temurin) with Gradle cache
3. Run unit tests: `./gradlew testDebugUnitTest`
4. Generate Jacoco coverage report: `./gradlew jacocoTestReport`
5. Verify 50% coverage threshold: `./gradlew jacocoTestCoverageVerification`
6. Build debug APK: `./gradlew assembleDebug`
7. Upload coverage report artifact (14-day retention)

#### 2. `verify-screenshots` (ubuntu-latest, needs: build-and-test)

1. Record all screenshots: `./gradlew recordRoborazziDebug`
2. Detect regressions vs. committed goldens — fail if any diff found
3. Auto-commit new goldens (for tests that had no committed golden yet)
4. Upload screenshot diffs artifact on failure (7-day retention)

#### 3. `release-apk` (ubuntu-latest, PR-only)

1. Build release APK: `./gradlew assembleRelease`
2. Upload release APK artifact (14-day retention)
3. Post (or update) a sticky PR comment with the download link

### GitHub MCP Access

For inspecting CI from Claude Code: use `mcp__github__*` tools. The repository scope is
`arduia/ondevicelab`.

---

## 10. Branch & Push Workflow

### Branch Naming

| Type | Pattern | Example |
|------|---------|---------|
| Base branch | `enhance/ai-edge-gallery-integration` | — |
| Claude sub-branches | `claude/<short-description>` | `claude/add-ocr-screen` |

### Push Protocol

```bash
# First push
git push -u origin claude/<branch-name>

# Subsequent pushes
git push
```

**Retry on network failure:** up to 4 times with exponential backoff (2s, 4s, 8s, 16s).

### Commit Messages

- Focus on **why**, not what
- One sentence (two max) — the PR description is the place for details
- Reference session URL at the end (managed by Claude Code automatically)

### Rules

- **Always push** every commit — never leave work unpushed
- Run Step 6 verify once before pushing, not after every commit
- Never force-push to `main`/`enhance/**` without explicit user permission
- Never skip hooks (`--no-verify`) unless explicitly requested

---

## 11. CI Inspection Protocol (Token-Lean)

Drill from cheap to expensive. Stop the moment you have an actionable cause.

| Step | Action | Notes |
|------|--------|-------|
| 1 | **Status first** — `get_check_runs` / `get_status` | Tells you which job failed, no log cost |
| 2 | **Confirm it's current** — compare run's head SHA to PR HEAD | Skip stale/superseded runs |
| 3 | **Targeted logs** — `failed_only: true`, start with `tail_lines: 30` | Failure summary is almost always at the tail |
| 4 | **Widen only if needed** | Never pull a full log to "look around" |

**Rules:**
- Never re-fetch the same log
- Quote just the 1-3 lines that name the cause
- Huge list/JSON results → read small slices, don't pull the whole thing into context
- One actionable cause = enough; fix, push, read the next run's *status* — don't re-dump logs

---

## 12. Agent Skills Reference

Skills are stored in `.claude/skills/` and loaded via the `Skill` tool. Project conventions in
`CLAUDE.md` take precedence over any skill's guidance where they conflict.

### Conflict Overrides (always follow CLAUDE.md)

| Topic | Skill may say | Project uses |
|-------|--------------|--------------|
| DI | Koin | **Hilt** |
| Screenshot tests | Paparazzi | **Roborazzi** |
| Architecture | MVI | **MVVM + UseCase + StateFlow** with sealed `…UiState` |
| Test doubles | Mock on-device models | **Fakes at repository boundary only** |

### Official Android Skills (Google `android/skills`)

| Skill | When to use |
|-------|-------------|
| `edge-to-edge` | Adaptive edge-to-edge, system bar / IME insets, target SDK 35 + Compose |
| `adaptive` | Adaptive layouts for varied window sizes, phones/tablets/foldables |
| `styles` | Jetpack Compose Styles API + Material 3 theming |
| `testing-setup` | Unit / UI / screenshot test infrastructure (complements Roborazzi workflow) |
| `appfunctions` | Expose on-device workflows as App Functions for AI agents |

### Community Android Skills

| Skill | When to use |
|-------|-------------|
| `compose-editor` | Idiomatic, accessible Compose patterns |
| `compose-performance-auditor` | Recomposition/stability audit, Compose UI performance |
| `kotlin-coroutines` | Coroutine + Flow/StateFlow correctness |
| `kotlin-convention` | Kotlin idioms and code conventions |
| `android-unit-test-editor` | Unit-test templates (MockK + coroutines-test) |
| `android-modularization` | Multi-module layering reference |
| `gradle-configuration` | Version catalogs, dependency scopes, build performance |
| `gradle-convention-plugin` | Centralizing build logic via convention plugins |
| `github-action-editor` | GitHub Actions for Android CI/CD |
| `android-permissions-editor` | Runtime-permission architecture and Compose wiring |

**Intentionally excluded (conflict with project stack):** `koin-editor`, `paparazzi-editor`,
`mvi-editor`

### Project-Authored Skills

| Skill | Purpose |
|-------|---------|
| `ui-ux-review` | Review Roborazzi goldens against `DESIGN_SYSTEM.md`; adjust Compose impl |
| `code-review` (`.claude/commands/code-review.md`) | Full structured PR review for OnDeviceLab |

### Code Review Command (`/code-review`)

The custom `/code-review` command performs a structured review with 10 sections:

1. **Overview** — what the change does + quality verdict
2. **Product Perspective** — UX states, happy-path assumptions, error messaging
3. **Architecture & Conventions** — layer violations, missing `@Preview`, missing Hilt module
4. **Security** — file path handling, URI access, network downloads, permissions, LLM prompt
   injection
5. **Performance** — memory (Bitmap in composition), threading (blocking on Main), model lifecycle
   (singleton), low-end device fallback, API level gates, streaming/backpressure, battery
6. **Kotlin & Coroutines Quality** — race conditions, `callbackFlow`, `@Volatile` misuse,
   `StateFlow` vs `SharedFlow`, blocking suspend functions
7. **Compose Best Practices** — heavy work in composition, lambda captures, `LaunchedEffect`
   keys, `rememberCoroutineScope` misuse, missing `@Preview`
8. **Test Quality** — domain rules in tests, fake repository contracts, golden images, coverage
9. **Issues & Risks** — table with Severity / File / Finding (Critical/Major/Minor/Speculative)
10. **Recommended Follow-ups** — ordered by priority

---

## 13. Permissions & Allowed Commands

### Project-Level Permissions (`.claude/settings.json`)

Configured via the SessionStart hook — no explicit allow list at project level beyond the hook.

### Local-Level Permissions (`.claude/settings.local.json`)

```
Bash(git commit *)
Bash(gradle wrapper *)
Bash(mkdir -p gradle/wrapper)
Bash(curl -L -o gradle-wrapper.jar ...)
Bash(curl -sL .../gradlew ...)
Bash(chmod +x gradlew)
Bash(./gradlew --version)
Bash(./gradlew :app:assembleDebug)
Bash(./gradlew --stop)
Bash(xargs cat -n)
Bash(./gradlew runChecks)
mcp__*__resolve-library-id
mcp__*__query-docs
```

### Implicit Trust Boundaries

- **Read freely:** any file in the repository
- **Edit freely:** local, reversible file edits
- **Ask before:** destructive git operations, force-push, push to `main`, external service calls
- **Never without explicit permission:** `--no-verify`, `reset --hard`, `push --force` to main

---

## 14. Retrospectives Guard System (G1–G5)

These guards were derived from post-mortem analysis of CI failures. They are built into the
development workflow steps above. This section documents them for reference.

### G1 — Detect the No-Verify Environment Early

**Trigger:** `./gradlew help` (or any Gradle task) fails to resolve plugins/deps (e.g. Google
Maven returns 403 on `dl.google.com` / `maven.google.com`).

**Action:**
- Declare local verification impossible up front
- Tell the user immediately
- Offer to loosen the network policy
- Treat *all* code produced in this session as **unverified** until CI confirms it

### G2 — Compensate When You Can't Run Tests

**Trigger:** G1 is in effect — no local Gradle execution possible.

**Action:**
- Self-review every test↔implementation pair for mutual consistency
- Prefer small, frequently-pushed commits so CI is the fast feedback loop
- Use `workflow_dispatch` CI run as the fallback correctness gate
- Read the first CI run as the real correctness gate, not local intuition

### G3 — Edge-Case Admission Check (TDD)

**Trigger:** A test asserts normalization, clamping, or coercion on an edge input.

**Action before writing the test:**
- Confirm the parser/guard/regex actually *admits* that input and reaches the asserted code path
- A `{1,6}` cap, a length guard, or a precondition can **reject** the input before the asserted
  logic ever runs — in that case the test would pass vacuously or fail with a misleading error

**Example failure mode:** A test fed `"####### Deep"` (7 `#`) expecting clamping to heading level
6, but the heading regex `^(#{1,6})\s+` never matched 7 hashes, so the line parsed as a
`Paragraph`, and the cast to `Heading` threw `ClassCastException`.

### G4 — Expect Unmasked Errors Behind a Fixed Crash

**Trigger:** You fix a failure that was aborting a build/lint/test stage (e.g. an
`IncompatibleClassChangeError` crashing `lintVitalRelease`).

**Action:**
- Assume real errors were hidden behind the crash — do not declare victory
- Re-run that stage and fix incrementally
- Continue until the stage passes cleanly, not just until the original crash is gone

### G5 — UI Changes Require Goldens

**Trigger:** Any UI change (Compose screen, component, or theme modification).

**Action:**
- Goldens **must** be re-recorded with `./gradlew recordRoborazziDebug` in a Maven-capable
  environment
- If that is not possible locally (G1 is in effect), flag it as a known remaining CI blocker
- Never assume screenshots are green without re-recording in the environment where Maven works

---

## Appendix A — Key File Locations

| File | Purpose |
|------|---------|
| `CLAUDE.md` | Master project instructions (authoritative over all skills) |
| `doc/ENGINEERING.md` | Implementation patterns and code snippets |
| `doc/PHILOSOPHY.md` | Project goals and learning philosophy |
| `doc/ROADMAP.md` | Learning phases and planned features |
| `.claude/settings.json` | SessionStart hook registration |
| `.claude/settings.local.json` | Local machine permission allowlist (gitignored) |
| `.claude/hooks/session-start.sh` | Android SDK install + Gradle cache warm |
| `.claude/context/project_codebase.md` | Live codebase snapshot (updated per session) |
| `.claude/context/retrospectives.md` | Append-only post-mortem guard log |
| `.claude/commands/code-review.md` | `/code-review` slash command definition |
| `.claude/skills/README.md` | Skills index + conflict override table |
| `.github/workflows/ci.yml` | GitHub Actions CI pipeline |

---

## Appendix B — Instruction Precedence

When there is a conflict between any source of guidance, follow this order:

```
CLAUDE.md  >  doc/ENGINEERING.md  >  .claude/commands/*  >  .claude/skills/**  >  general AI knowledge
```

**Specific known conflicts (skills to ignore on these topics):**
- **DI:** Hilt, not Koin
- **Screenshot tests:** Roborazzi, not Paparazzi
- **Architecture:** MVVM + UseCase + StateFlow, not strict MVI
- **Test doubles:** fakes at repository boundary, never mock on-device models

---

*Document generated from the OnDeviceLab project on 2026-06-15.*
