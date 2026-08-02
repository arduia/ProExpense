# Finance Tracker — Agent Instructions

> Authoritative agent instructions for the Finance Tracker project (Android ships as **Pro Expense**).
> **Project goal & beliefs:** `docs/project_philosophy.md`. **Feature scope & MVP:** `docs/finance_tracker_product.md`.
> Philosophy and PRD take precedence over skills and general AI knowledge.

---

## Project Overview

### Project Goal

**Build the world's most effortless personal finance notebook** — a Finance Tracker that lets anyone
record and review money in seconds, fully offline, with full data ownership and no bank or
third-party integrations.

| Layer | Goal |
|---|---|
| **Product** | Easiest possible tracking and recording of personal finances (PRD vision) |
| **User** | Log an expense in under 5 seconds; trust the app like a private notebook |
| **Engineering** | KMP-shared business logic, native Compose/SwiftUI shells, offline-first local storage |
| **This codebase** | Pro Expense on `refactor/v2-migration` is the Android vehicle for the v2 architecture refresh toward the Finance Tracker MVP |

Read **`docs/project_philosophy.md`** for beliefs, decision framework, and non-negotiables. Read
**`docs/finance_tracker_product.md`** for personas, feature list, roadmap, and success metrics.

**North star** (philosophy + PRD):

> To support tracking and recording of personal finances in the easiest and most effortless way possible.

**Core principles** (decisions must align — full rationale in philosophy doc):

- **Simplicity first** — no clutter, no overwhelming dashboards
- **Speed** — logging in seconds, not minutes
- **No dependencies** — no bank integrations or account linking
- **Personal & private** — your notebook; data on device (MVP)
- **Accessible to everyone** — not only finance-savvy users
- **Global-ready** — multi-currency as core, not add-on
- **Integrity** — trust over growth hacks; MVP fully free

**Engineering goals:**

- Integrity, user data privacy, security, usefulness, performance, simplicity, UI/UX
- Clean architecture: maintainability, scalability, code quality
- Offline-first: fully functional without internet

**MVP scope** (build toward — details in PRD):

- Quick manual logging · Multi-currency (basic, manual rates) · Record history
- Shared costs · Secure import/export (CSV/JSON) · Auth setup (PIN)
- Local storage is the default and source of truth — Google Drive cloud sync (`feature:sync`) is
  an opt-in addition, off until the user connects an account; see `docs/user_stories/sync/`

**Product constraints (never violate for MVP):**

- No bank or third-party integrations
- No server-side auth for the app itself (PIN is local only) — Google Sign-In is scoped narrowly
  to Drive access for the opt-in sync feature below, not an app identity/login mechanism
- Cloud sync (Google Drive, opt-in) is in scope as a manually-connected, off-by-default feature —
  see `docs/user_stories/sync/`; nothing syncs until the user explicitly connects, so the app still
  ships fully offline-capable and MVP's "no cloud sync" default experience is unchanged for anyone
  who doesn't opt in
- Data ownership — export/import supported; user owns their data
- Max expense amount: 999,999,999.99

**Stack (current Android codebase):** Kotlin 2.4 · KMP · Jetpack Compose · Min SDK 24 / Target SDK 36

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
│   ├── importexport/            Import & Export (MVP)
│   └── sync/                    Google Drive Cloud Sync (opt-in)
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

### iOS Compatibility (mandatory)

Every KMP module (`shared`, `core:*`, `feature:*`) must build for iOS, not just Android. Full
roadmap: `docs/ios_compatibility_plan.md`. Rules that apply now:

- **Every KMP module declares `iosArm64()` and `iosSimulatorArm64()`** alongside `androidTarget()`
  in its `build.gradle.kts`. Enforced by the `checkIosTargets` Gradle task — a module missing
  either target **fails the build**, it is not a warning.
- **New-module checklist** (any new `feature:*` or `core:*` KMP module): Kotlin Multiplatform
  plugin + both iOS targets + business logic in `commonMain` + platform seams via `expect`/`actual`
  (`androidMain` / `iosMain`) + zero `java.*` / `android.*` imports in `commonMain`.
- **`./gradlew verifyIosCompat`** cross-compiles iOS klibs for every KMP module (Kotlin/Native only
  — runs without macOS/Xcode). It is part of `verifyAll`, so the default Step 6 gate already covers
  it. Run it standalone when iterating on `commonMain` or module build files without touching
  Android UI.
- iOS actuals may remain `TODO()` stubs (see `shared/src/iosMain/`, `core/storage/src/iosMain/`)
  until the corresponding Phase 1 work in `docs/ios_compatibility_plan.md` — the gate proves
  compile-compatibility, not feature completeness.

---

## Development Workflow — 8-Step Gate System

Authoritative workflow. Each step is **gate-first**: if the gate already holds, mark ✅ and skip.
**Never skip a gate that fails.**

### Plan Mode & Model Selection Workflow

When a task requires planning:

1. **Agent enters plan mode** and designs an implementation approach
2. **User reviews and approves the plan** (using ExitPlanMode)
3. **Session pauses for model selection** — agent asks user which AI model to use for implementation
4. **User selects model** (e.g., Haiku for straightforward impl, Opus for complex decisions)
5. **User confirms readiness** (e.g., "let's go")
6. **Agent proceeds with implementation** on the selected model

This pattern ensures you have explicit control over which model handles each phase of work and can match model capability to task complexity.

### Step 1 — Understand Intention & Scope

**Gate:** Outcome is clear, change size (small/large) is known, enough context to act, and the
working branch is settled.

**Else:** Identify domain concepts and boundaries. Pin down outcome and size. Ask the user if unclear.

**Branch gate (before any implementation):**

- If the user asked to **check out**, **switch to**, or **work on** a **named branch** from the
  console (e.g. `refactor/v2-migration`, `feature/compose-home`), **use that branch** —
  `git fetch` + `git checkout <branch>` — and **do not** create a new branch (`cursor/*` or otherwise).
- Create a new branch only when the user did **not** name a branch and the task needs an isolated
  line of work — then follow [Branch naming](#branch-naming) below.
- When unsure, confirm with the user before `git checkout -b`.

### Step 2 — Explore the Codebase

**Gate:** Affected files, signatures, naming, test patterns, and `app/build.gradle.kts` are known.

**Else:**
1. Read `.cursor/context/project_codebase.md`
2. Read each affected file — verify signatures and patterns (never assume)
3. Always read `app/build.gradle.kts`
4. `grep` for APIs/classes being introduced

### Step 2.1 — Compose spec skill (mandatory for spec-driven UI)

**Gate:** For new or materially changed Compose screens, the agent has read
[`.agents/skills/design-spec-to-compose/SKILL.md`](.agents/skills/design-spec-to-compose/SKILL.md)
**in full** and is following it for the rest of the session.

**Else:** Open the screen markdown and every listed state PNG under
`design-system-spec/screenshots/screens/`, then read the skill file before Step 3.
Do not implement spec-backed UI from memory or general Compose knowledge alone.
If no entry exists in `design-system-spec/screens/`, add or extend the spec before implementing.

**Push is blocked** for spec-driven UI until the skill’s per-screen checklist is satisfied
(previews, Roborazzi baselines, `verifyAll`, tokenized styling — see skill body) **and** Step 3.1's
canvas render-and-compare has been run and passed.

### Step 2.5 — Confirm External APIs

**Gate:** No new dependency — or API already used in codebase.

**Else:** Confirm artifact version, Kotlin/Android compatibility. Use web search for unfamiliar APIs.

### Step 2.25 — Prepare Toolchain (before first Gradle command)

**Gate:** Android SDK is configured and Gradle can resolve project dependencies.

Check (all must pass — skip setup if they do):

1. `local.properties` exists at repo root with a valid `sdk.dir=…` path
2. That SDK path exists on disk (contains `platform-tools/`)
3. `./gradlew --version` exits 0

**Else (run once per environment / session when the gate fails):**

```bash
bash scripts/setup-android-toolchain.sh
```

This script:

- Ensures Java 17+
- Downloads Android command-line tools (into `android-sdk/` at repo root by default)
- Installs platform **36** and build-tools **36.0.0**
- Accepts SDK licenses and writes `local.properties`
- Warms Gradle dependency resolution (`buildEnvironment`, module `dependencies`)

**When to run:**

| Situation | Action |
|-----------|--------|
| Fresh clone, cloud agent, or CI runner | Run setup before Step 6 |
| `SDK location not found` / missing `sdk.dir` | Run setup, then retry Gradle |
| Gradle plugin or dependency resolution fails on first run | Run setup, then retry |
| SDK already configured and `./gradlew verifyAll` works | ✅ skip |

**Do not declare G1** until setup has been attempted when the failure is environmental.

### Step 3 — Plan

**Gate:** Full change surface obvious for a small, self-contained change.

**Else:**
- **Small:** list files to add/edit/delete
- **Large:** list files + dependency order
- Document version/integration risks before proceeding
- **Product UX lens (mandatory):** before finalizing scope, ask — does this screen already
  anticipate what the user is about to do next? Minimize taps, typing, and waiting wherever the
  intent is obvious (e.g. a screen whose whole purpose is typing a name should already have that
  field focused with the keyboard up; a screen whose purpose is picking a value should present
  the most likely choice first). Not limited to creation screens — applies to edit, search, and
  picker flows too. Rooted in `docs/project_philosophy.md` belief #2 — speed beats completeness
  at log time. When the answer is auto-focus/keyboard, reuse `rememberAutoFocusRequester()`
  (`shared/.../ui/design/Focus.kt`) rather than a local copy.
- **UI/UX audit gate (mandatory whenever the change has UI/UX impact):** if the requested change
  touches a Compose screen, layout, navigation flow, or design-system component — not only
  spec-driven builds (Step 2.1) — **invoke the `Skill` tool for `compose-product-auditor`**
  (planning mode) **before finalizing scope**. Informally reasoning about UI/UX quality, or
  summarizing the skill file from memory, does not satisfy this gate — the skill must actually
  run, and its advisory output (even a one-line "clean" verdict) must be stated in the plan. The
  goal is to catch, at design time, anything that would leave the user confused or the layout
  incomplete — e.g. a fixed horizontal padding/row cap that could crop or truncate content
  instead of scrolling it, **a scrollable row that overflows but has no edge affordance (fade,
  peek, or indicator) signaling there's more — a hard clip at the scroll boundary reads as
  broken content, not "swipe for more"**, an affordance that's inconsistent with the rest of the
  screen, a flow that leaves stale state visible. Pick the approach the audit lens supports and
  note the choice in the plan; this is a general UI/UX check, not limited to any one kind of
  defect.

### Step 3.1 — Visual verification against the design canvas (mandatory for every canvas-sourced UI implementation)

**Gate:** The Blue Banking canvas (`variant-blue-*.jsx`, see Key File Locations) is the adopted
design system for this whole app — not a reference consulted only when someone explicitly asks
for a "fidelity audit." **Any** implementation or change of a screen/component that has a canvas
counterpart — a new spec-driven build (Step 2.1), a restyle, a bug fix that touches layout, or an
explicit alignment/audit task — is verified against an actual **rendered screenshot** of the
canvas source, not literal JSX/CSS values alone, and not a screenshot already sitting in the
canvas project's own `screenshots/`/`design-system-spec/` folders (those can be stale from a
pre-Blue-Banking iteration — confirmed once; don't trust them without checking what they
actually show). This applies whether or not the user names "the canvas" explicitly — if the
screen has a `variant-blue-*.jsx` counterpart, this gate applies.

**Else:** Use `scripts/canvas-render/` (see its README.md) to render the real canvas component
with React/Babel and screenshot it, then compare directly against the matching Roborazzi PNG.
Token/CSS-value comparison alone catches spacing drift but misses layout-level differences that
only show up rendered — e.g. a card meant to float up into the header on canvas but sitting
flush in the implementation is invisible in a values diff, obvious in a rendered one. When
picking which canvas component to render, use `variant-blue-app.jsx`'s own
`VariantBlueApp`/`<DCArtboard>` tree as the source of truth for which variant is actually
adopted — the file defines unused alternate variants alongside the wired ones (e.g.
`VBHomeClassic`/`VBCardCasual` exist in source but only `VBHomeSpendTrip`/`VBCardSpendTrip` is
in the artboard tree, i.e. actually shipped in the canvas). State the render-and-compare result
explicitly (clean, or defects found + fixed) — the same standard as the `compose-product-auditor`
pass; don't silently skip it or substitute a values-only comparison.

### Step 4 — Write Tests First (TDD)

**Gate:** Tests cover this change's rules 1-to-1 (or UI-only with appropriate UI test).

**Else:**
- **Logic:** document rules per method/class, write tests 1-to-1
- **Backbone-first (mandatory):** success path + key invariant + primary failure mode
- **G3:** confirm edge input reaches asserted code path before writing test
- **PRD/user-story traceability (mandatory for logic changes — see Step 4.5):** the "rules" tests
  are written against must trace to a documented user story's Acceptance Criteria or Functional
  Requirements, not just the agent's own informal read of the task. Don't defer this to Step 6 —
  identify the relevant story file(s) while writing tests, since it changes which scenarios get
  a test.

### Step 4.5 — PRD / User Story Test Audit (mandatory for logic + unit test changes)

**Gate:** Every new/changed unit test for this change traces to a specific Acceptance Criteria
scenario or Functional Requirement in a `docs/user_stories/<service>/US-*.md` file (or the PRD,
`docs/finance_tracker_product.md`, for cross-cutting rules no single story owns) — and every
Acceptance Criteria scenario the change touches has a corresponding test. Skip this gate entirely
for changes with no logic/unit-test surface (pure UI, docs, config, build files).

**Else:**
1. **Identify the story.** Find the `docs/user_stories/<service>/` file(s) covering the feature
   being changed (see `docs/user_stories/README.md`'s service index).
2. **New requirement, not yet in any story?** If the change implements something that came from
   this session's chat/user input and isn't described in an existing story's Acceptance Criteria
   or Functional Requirements — **update the story first.** Add a new Scenario (Given/When/Then)
   or Functional Requirement to the existing story file, or create a new `US-<SERVICE>-<n>.md`
   from `docs/user_stories/TEMPLATE.md` if it's a genuinely new use case, and add it to that
   service's `user-stories.md` index. **Do not audit test cases against a requirement that only
   exists in chat history** — the story is the durable source of truth; chat is not.
3. **Then audit.** With the story current, check each new/changed test case against it:
   - Does the test's assertion match a specific Scenario's Given/When/Then, or a specific
     Functional Requirement checkbox? If a test can't be traced to either, it's untethered from
     the story — flag it (matches the existing "every test traces to a rule" principle in the
     Testing Contract, now traced to a documented story specifically, not just an informal rule).
   - Does every Acceptance Criteria scenario the change touches have a test? An untested scenario
     is a coverage gap — flag it, and add the missing test before Step 6.
4. Report the verdict in the Step 7.5 close-out's **PRD/Story audit** line — cite the story
   ID(s), and whether the story needed updating first.

### Step 5 — Implement

**Gate:** Full change surface implemented following project patterns.

**Else:** Edit in dependency order. Commit at logical checkpoints.

### Step 6 — Verify In-Session

**Gate:** Right check is green — or verify impossible and flagged with fallback.

**Else (run once before push):**

1. **Toolchain gate:** if Step 2.25 is not ✅, run `bash scripts/setup-android-toolchain.sh` first.
2. Run the verification command for your change type:

| Change type | Command |
|-------------|---------|
| Any agent change (preferred) | `./gradlew verifyAll` |
| Logic change | `./gradlew :app:testDevDebugUnitTest` |
| Multi-module | `./gradlew test` |
| Build check | `./gradlew :app:compileDevDebugKotlin` |
| Screenshot / Compose UI | `./gradlew :app:verifyRoborazziDevDebug` |
| Small non-logic | `./gradlew :app:compileDevDebugKotlin` |
| KMP module / `commonMain` / `expect`-`actual` change or new module | `./gradlew verifyIosCompat` (included in `verifyAll`) |

`verifyAll` includes ktlint + detekt for every module. Before writing/editing any `.kt`
file, and before treating a ktlint/detekt failure as a real bug, read
[`.agents/skills/kotlin-lint-style/SKILL.md`](.agents/skills/kotlin-lint-style/SKILL.md) —
most detekt failures on an agent diff are pre-existing findings de-baselined by a signature
change, not new problems, and the skill covers the exact regeneration workflow.

**Lint retrospective (mandatory both directions):** before diagnosing any ktlint/detekt/
Android-lint failure, check
[`.agents/skills/kotlin-lint-style/lint-retrospective.md`](.agents/skills/kotlin-lint-style/lint-retrospective.md)
for the rule ID — it's an append-only log of every such finding hit before, with the fix that
applied, so the same mistake or the same wasted diagnosis doesn't repeat across sessions. After
resolving a lint failure, add a row there if the rule/situation isn't already covered.

**UI change gate (mandatory before push):** Any change to Compose screens, themes, or
`ui/design/` components **must** pass screenshot verification in-session before `git push`:

1. `./gradlew :app:verifyRoborazziDevDebug` (or `./gradlew verifyAll`) exits 0
2. If visuals changed intentionally, run `./gradlew :app:recordRoborazziDevDebug` and commit
   updated baselines under `app/src/test/screenshots/` in the same branch
3. Every touched content-composable file has `@Preview` (see Compose UI standards)
4. **Product/UI-UX audit pass (mandatory whenever the change has UI/UX impact):** once Roborazzi
   is green, **invoke the `Skill` tool for `compose-product-auditor`** (post-implementation mode)
   against the actual rendered screenshots (or a device/emulator view when Roborazzi can't cover
   the change). This is a distinct check from confirming your own diff rendered — verifying "my
   code change took effect" is not the same as auditing "would this confuse a user," and doing
   the former does not satisfy this gate. A green pixel-diff only proves the screen matches its
   own baseline — it says nothing about whether that baseline itself looks right (cropped/
   incomplete content from a fixed padding or row cap, a scrollable row whose overflow edge is a
   hard clip instead of a fade/peek affordance, crowded or misaligned elements, an inconsistent
   affordance). State the audit's finding in-session (clean, or defects found + fixed) before
   moving to Step 7 — don't silently skip the pass. Treat any finding this pass surfaces as a
   required fix before push, not a follow-up task.
5. **Any screen with a canvas counterpart:** re-run the Step 3.1 render-and-compare against
   `scripts/canvas-render/` on the changed screen and confirm visually, not just by re-reading the
   token values that were changed — a token fix can be numerically correct and still not be the
   layout-level thing the canvas actually shows.

**Step 7 is blocked for UI work until this gate is ✅** (or G1 is declared with compensation).

**G1 (no Gradle):** declare verification impossible, treat code as unverified, compensate per retrospectives rule.
Run `bash scripts/setup-android-toolchain.sh` first when the failure is missing SDK or unresolved
dependencies — only declare G1 if setup was attempted and Gradle still cannot run.

**Local device gates** (when `adb devices` shows a device):
- L1: `./gradlew :app:compileDevDebugKotlin`
- L2: `./gradlew :app:installDevDebug`
- L3: Launch app, navigate to changed screen
- L4: `adb logcat -d | grep -E "FATAL|ERROR.*expense"`

### Step 7 — Push

**Gate:** Step 6 passed (or flagged), commits have why-focused messages.

**Else:** Verify once before push. `git push -u origin <branch>` on first push.

**UI work:** never push Compose UI changes until Step 6 screenshot verify is ✅
(`verifyRoborazziDevDebug` or `verifyAll`). Previews and screenshot baselines must be
committed with the UI diff — not in a follow-up.

**Do not open pull requests.** Push the branch only. PRs are created manually by the team — never call PR-management tools or open draft/ready PRs unless the user explicitly asks in that session.

### Step 7.5 — Session close-out (mandatory before ending)

**Gate:** Agent has explicitly reported Step 6 status and push status in the final response.

**Else:** Do not end the session. Run missing verification or push, then report.

Every task completion **must** include a **Workflow status** block in the final message:

```markdown
## Workflow status
- Step 2.25 — Toolchain: ✅ already configured | ✅ `scripts/setup-android-toolchain.sh` | ⚠️ skipped (reason)
- Step 6 — Verify: ✅ `<command run>` | ⚠️ G1 flagged (reason) | ❌ not run
- PRD/Story audit: ✅ traced — `<story ID(s)>` | ✅ traced — `<story ID>` updated first (new/extended scenario) | ⚠️ gap found — `<what, and fix>` | ❌ not run (reason)
- UI audit — compose-product-auditor: ✅ clean | ⚠️ `<n>` finding(s) — fixed | ⚠️ `<n>` finding(s) — deferred (reason) | ❌ not run (reason)
- Feedback fix: ✅ `<design/fidelity mismatch the user flagged → what changed>` | — none this session
- Step 7 — Push: ✅ `origin/<branch>` @ `<short-sha>` | ❌ not pushed
- PR: manual (not opened by agent)
```

Rules:
- **Step 2.25 ✅** when SDK is configured and Gradle resolves dependencies, or after `setup-android-toolchain.sh` succeeds.
- **Step 6 ✅** only after the matching command exits 0 in this session (preferred: `./gradlew verifyAll`), or **G1** is declared with reason and compensation.
- **PRD/Story audit** line is required whenever the change touches business logic and its unit tests (Step 4.5) — cite the specific `US-<SERVICE>-<n>` story ID(s) the tests were checked against. If a requirement came from chat and wasn't in any story, the story must have been updated *first* (say so explicitly — "story updated first") before the audit counts as done; auditing against a requirement that only exists in chat is not a valid pass. **Omit this line entirely** (don't report n/a) for changes with zero logic/unit-test surface (pure UI, docs, config).
- **UI audit** line is required whenever the change touches Compose UI (Step 6's UI change gate / G5) — report the `compose-product-auditor` pass's actual verdict, not a self-assessment of "does my diff render." **Omit this line entirely** (don't report n/a) for changes with zero UI surface (pure logic, tests, docs, config). A finding logged as "fixed" must have been re-verified (re-screenshot/re-audit), not just edited and assumed fixed.
- **Feedback fix** line is required whenever the user pointed out a design/fidelity/behavior mismatch after something was already delivered — name the specific thing that was wrong and what changed, so drift between "shipped" and "actually matches" stays visible in the close-out instead of buried in chat history. Use `— none this session` when nothing was corrected on user feedback (this line always stays, unlike the two audit lines above).
- **Step 7 ✅** only after `git push` succeeds and local `HEAD` matches `origin/<branch>`.
- **PR:** always report `manual (not opened by agent)` unless the user explicitly requested PR creation in that turn.
- If Step 6 is ✅ and commits exist, **push before close-out** — do not leave verified work only on disk.
- On follow-up turns where nothing changed since push, cite the prior Step 6 command and confirm `git status` clean + branch up to date; do not re-run Gradle unless the gate broke (new commits, CI failure, user request).
- **Never** claim Step 6/7 passed without evidence (command output or `git log` / `git status`).

### Step 8 — Auto-record Retrospective

**Gate (skip unless BOTH):** (a) large change AND (b) unexpected failure slipped past gates.

**Else:** Append entry to `.cursor/context/retrospectives.md` with what slipped, root cause, concrete guard.

---

## Build Commands

Default flavor for agent work: **devDebug** (`com.arduia.expense.dev`).

**First-time / fresh environment — run before any command below:**

```bash
bash scripts/setup-android-toolchain.sh
```

```bash
# Unified verification (build + unit tests + screenshot tests + iOS klib compile)
./gradlew verifyAll

# iOS compile-time compatibility only (cross-compiles klibs for every KMP module)
./gradlew verifyIosCompat

# Fails fast if any KMP module is missing iOS targets (no compile)
./gradlew checkIosTargets

# Unit tests (logic changes)
./gradlew :app:testDevDebugUnitTest

# Screenshot tests — record new baselines after intentional UI changes
./gradlew :app:recordRoborazziDevDebug

# Screenshot tests — compare against committed baselines
./gradlew :app:verifyRoborazziDevDebug

# Kotlin compile (fail-fast)
./gradlew :app:compileDevDebugKotlin

# Full module tests
./gradlew test

# Debug APK
./gradlew :app:assembleDevDebug

# Install on device
./gradlew :app:installDevDebug
```

**Prerequisites:** `local.properties` (`sdk.dir`) — created automatically by `scripts/setup-android-toolchain.sh`.

---

## Testing Contract

### Test Locations

| Type | Location |
|------|----------|
| KMP unit tests | `<module>/src/commonTest/kotlin/` |
| Android JVM unit tests | `app/src/test/java/` |
| Screenshot baselines | `app/src/test/screenshots/` |
| Instrumented tests | `app/src/androidTest/java/com/arduia/expense/` |

### Core Rules

1. **Backbone-first:** every touched class gets backbone tests before permutations:
   - Main success path
   - Key invariant(s)
   - Primary failure mode

2. **Fakes at repository boundary** — prefer fakes over mocking Room/storage internals.

3. **Every test traces to a rule** — and for logic changes, that rule traces to a documented
   user story's Acceptance Criteria or Functional Requirement (`docs/user_stories/`), audited per
   **Step 4.5**. If a test can't be traced to either, delete it.

4. **Exhaustive edge-case matrices are optional** — only on explicit request.

5. **G3 — Edge-case admission check:** before asserting normalization/clamping on edge input,
   confirm the parser/guard actually admits that input and reaches the asserted code path.

6. **Capability classes** (DAO, prefs, cache): backbone is save → read/observe round-trip.

7. **Compose `@Preview` is mandatory** — every Kotlin source file under `app/src/main` **or**
   `feature/*/src/androidMain` that defines a **public** `@Composable` used for UI (screens, flows,
   shells, or reusable view components) **must** ship at least one relevant `@Preview` per distinct
   UI state. Missing previews block Step 5 (implement) and Step 7 (push).
   - Flow orchestrators (`*Flow`, `*App`, `*Shell`) preview their representative routes/states via
     dedicated preview composables in the **same file** (static hosts are OK).
   - Private helpers-only files with no preview-worthy surface (e.g. pure illustration geometry)
     are the only exception — flag in review if unsure.

8. **Screenshot verify before push on UI changes** — when Compose UI is touched, Roborazzi
   verify must pass in-session before `git push`. Intentional visual changes require
   `recordRoborazziDevDebug` and committed baselines in the same branch.

9. **`@Category` every Robolectric Compose test that launches a real activity** — any `app` module
   test using `createAndroidComposeRule`/`createComposeRule`/`ActivityScenarioRule` needs
   `compose-ui-test-manifest`, which is `debugImplementation`-only, so it fails to resolve an
   activity under a release unit-test variant ("Unable to resolve activity for
   ComponentActivity"). Tag the class `@Category(ScreenshotTests::class)` (Roborazzi) or
   `@Category(ComposeUiTests::class)` (plain Compose interaction test) — both markers live in
   `app/src/test/java/com/arduia/expense/testing/` and are excluded from `*ReleaseUnitTest` in
   `app/build.gradle.kts`. A file with multiple test classes only tags the ones that actually use
   the rule — pure-logic classes in the same file don't need it. `verifyAll` runs
   `:app:testDevReleaseUnitTest` specifically so an untagged test surfaces immediately instead of
   silently rotting until someone runs the bare `./gradlew test`.

### Tools

- **KMP unit:** `kotlin-test`, coroutines-test
- **Android JVM unit:** JUnit 4, MockK, Robolectric, coroutines-test
- **Screenshot:** Roborazzi + Robolectric (`captureRoboImage`, `@Category(ScreenshotTests::class)`)
- **UI:** Espresso, Compose UI Test (`createComposeRule`, `@Category(ComposeUiTests::class)` — see
  Core Rule 9)

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

#### Compose skills workflow (mandatory for new UI)

For every new or materially changed Compose screen, follow this order. Full skill bodies live under
`.agents/skills/` — **read the linked file at each step; do not skip Step 1.**

| Step | When | Skill | Path |
|------|------|-------|------|
| **1 — Build** | New or materially changed Compose screen | Design spec → Compose | [`.agents/skills/design-spec-to-compose/SKILL.md`](.agents/skills/design-spec-to-compose/SKILL.md) |
| **2 — Polish** | Affordances, animations, navigation transitions feel abrupt or static | Motion, navigation & interaction polish | [`.agents/skills/compose-motion-polish/SKILL.md`](.agents/skills/compose-motion-polish/SKILL.md) |
| **3 — Audit** | Twice: (a) planning (8-step Step 3), advisory, whenever the change has UI/UX impact; (b) visual verification (8-step Step 6 / G5), after Roborazzi is green and before push. Also on request. | Compose product auditor | [`.agents/skills/compose-product-auditor/SKILL.md`](.agents/skills/compose-product-auditor/SKILL.md) |

**Step 1 is mandatory** for spec-backed UI. Open every state PNG listed in
`design-system-spec/screens/<id>-<name>.md` before writing composables. Step 2 applies during
polish passes or when touch targets, ripple, or transitions are in scope. **Step 3 runs twice**
for any UI/UX-impacting change, general beyond spec-driven builds: an advisory pass during
planning (pick the approach that avoids confusing/incomplete layouts before writing code), and a
blocking pre-push audit against the actual rendered screens (produce a findings report; fix
before push, don't defer to a follow-up) — see 8-step Step 3 and Step 6 for the exact gates.

**Quick references (step 1):** `design-system-spec/screens/*.md`,
`design-system-spec/screenshots/screens/*.png`, `design-system-spec/components/`,
`design-system-spec/tokens.md`. Build order: scope states → reuse `ui/design/` → bottom-up
components → screen content → flow → preview fakes → Roborazzi. See skill for do/don’t patterns
(`ProTextAction`, `ProBottomSheetHost(visible = …)`, `AmountInput`, etc.).

**Quick references (step 2):** reuse `Interaction.kt` (`proIconClickable`, `proClickable`),
`NavMotion.kt`, `ProNavTransitions.kt`, `rememberProReduceMotion()` — no magic durations or bare-icon
`clickable`.

**Quick references (step 3):** product scope in `docs/finance_tracker_product.md`; distinguish
preview-fake wiring from real ViewModel/repository integration when reporting findings.

- Split route (with ViewModel) from stateless content composable
- **Mandatory `@Preview` on every UI composable file** — any file under `app/src/main` or
  `feature/*/src/androidMain` that exposes a public `@Composable` for screens, flows, shells, or
  reusable view components **must** include at least one **relevant** `@Preview` per distinct UI
  state in the **same file**. Previews are part of done, not optional polish. Agent-delivered UI
  without previews is incomplete and must not be pushed.
  - Wrap previews in `ProExpenseTheme`; default artboard `ProArtboard.PIXEL_9_PRO_WIDTH_DP` ×
    `PIXEL_9_PRO_HEIGHT_DP` (427×952 dp) unless the screen spec defines another size.
  - Flow orchestrators (`ExpenseApp`, `FirstLaunchFlow`, `QuickLogFlow`, …) use static preview
    hosts that show each meaningful step/state (child screen previews in the same file are OK).
  - Use preview fakes/stub repositories when a composable needs DI — never skip previews because
    of missing production wiring.
- Pair every new/changed screen with a Roborazzi screenshot test (`captureRoboImage`) in
  `app/src/test/`; verify (and record when intentional) before push
- Use `ProExpenseTheme` and components from `ui/design/` — authoritative visual reference:
  `design-system-spec/` (screens, PNGs, tokens)
- **Product UX lens carries into implementation** — see Step 3's Product UX lens check above; the
  most common concrete fix is auto-focusing the primary input (reuse
  `rememberAutoFocusRequester()` from `shared/.../ui/design/Focus.kt`), but the lens isn't limited
  to that one pattern.

#### Design system alignment

Components and screens must align with the shared design system. **Do not hardcode common UI
attributes when a theme token exists.**

| Attribute | Read from |
|---|---|
| Colors | `ProExpenseTheme.colors` / `ProExpensePalette` |
| Typography | `ProExpenseTheme.typography` — no inline `fontSize`, `fontWeight`, or `fontSize * scale` hacks |
| Spacing & sizing | `ProExpenseTheme.dimensions` — no inline `dp` for standard padding, heights, icon sizes |
| Corner radii | `ProExpenseTheme.shapes` |
| Motion | `ProExpenseTheme.motion` |

Rules:

- **`ui/design` primitives must be fully tokenized** — shared components consume theme tokens only.
- **Missing token? Add it first** — extend `shared/src/androidMain/kotlin/com/arduia/expense/ui/theme/`
  (`Type.kt`, `Dimensions.kt`, `Shape.kt`, `Color.kt`, `Motion.kt`) with a named token, then use it.
- **Exceptions (inline values OK):** illustration/scene coordinates, one-off animation math, or
  layout that has no semantic token yet — flag for tokenization when touching that area.

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

### Branch selection

| Situation | Action |
|-----------|--------|
| User names a branch in the console (“checkout `refactor/v2-migration`”, “work on `feature/foo`”) | `git fetch` + `git checkout <named-branch>`. **Do not** create a new branch. |
| User selected a branch in the Cursor/Cloud UI and the session starts on it | Stay on that branch. **Do not** create `cursor/*` or any other branch unless the user asks. |
| New isolated work and **no** branch named by the user | `git checkout -b <new-branch>` per [Branch naming](#branch-naming). |
| User explicitly asks for a new branch name | Create exactly that branch (or the name they give). |

#### One branch per session (mandatory)

For a single agent session, use **one working branch** for all implementation work:

- Create **at most one** new branch when the session starts and no user-named branch exists.
- **Stay on that branch** for every follow-up task, fix, and iteration in the same session — commit and push there.
- **Do not** create a second `cursor/*`, `feature/*`, or other branch mid-session unless the user **explicitly** asks to create another new branch (e.g. “open a new branch for this”).
- If the session already has a registered or pushed branch with prior work, **continue on it** rather than branching again.

**Anti-pattern:** Task 1 creates `cursor/compose-home-quicklog-cae7`, task 2 in the same session creates `cursor/v2-migration-pr-base-rule-cae7` instead of committing to the existing branch.

**Anti-pattern:** User says “checkout `refactor/v2-migration` and fix X” → agent runs
`git checkout -b cursor/fix-x-e0d8`. **Always work on the branch the user chose.**

### Branch naming

| Type | Pattern | Example |
|------|---------|---------|
| Refactor | `refactor/<name>` | `refactor/v2-migration` |
| Feature | `feature/<name>` | `feature/compose-home` |
| Agent work | `cursor/<name>` | `cursor/compose-home-2f1e` |
| Fix | `fix/<name>` | `fix/amount-validation` |

Use `cursor/*` only for **new** agent-initiated branches when the user did not specify an existing
branch to check out.

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

- **One branch per session:** at most one new branch unless the user explicitly requests another — see [One branch per session](#one-branch-per-session-mandatory)
- Run Step 6 verify once before pushing, not after every commit
- **Compose UI:** `@Preview` on every touched UI composable file (`app/src/main`, `feature/*/androidMain`);
  screenshot verify green before push — see Step 6 UI change gate
- End every implementation task with the **Workflow status** block (Step 7.5) — flag Step 6 and push explicitly
- **Do not create pull requests** — push only; PRs are opened manually (not as draft, not as ready) unless the user explicitly asks
- **PR creation requires intentional user request** — never create or update any pull request (including draft PRs) unless the user clearly and intentionally requests PR action in that session.
- **v2 migration PRs:** target `refactor/v2-migration`, not `main` — see [v2 migration base branch](#v2-migration-base-branch-mandatory-until-migration-completes)
- Never force-push to `main` without explicit user permission
- Never leave work unpushed when task is complete
- Multiple commits during implementation are fine

### Pull requests

- **Default:** agent pushes branch, user opens the PR in GitHub/GitLab.
- **Never** auto-open draft PRs or use PR-management tooling on task completion.
- Treat PR tooling as **opt-in only**: if the user did not intentionally ask for PR creation/update, do not call PR-management tools.
- **Only** create or update a PR when the user explicitly requests it in that session (then follow their ready/draft preference).

#### v2 migration base branch (mandatory until migration completes)

While the **`refactor/v2-migration`** branch is active and the v2 architecture refresh is in progress:

- **Every pull request must target `refactor/v2-migration`** — not `main`, `develop`, or any other default branch.
- This applies to agent-created PRs, `cursor/*` branches, `feature/*` branches, and manual PRs.
- When opening or updating a PR via tooling, set `base_branch` to `refactor/v2-migration` unless the user **explicitly** names a different base in that session.
- Do **not** merge v2 migration work into `main` until the team declares the migration complete.

**Anti-pattern:** Opening a v2 PR against `main` because it is the repository default branch.

---

## Retrospectives Guard System (G1–G5)

### G1 — Detect No-Verify Environment Early

**Trigger:** Gradle fails to resolve plugins/deps, or no Android SDK (`sdk.dir` missing).

**Action:** Run `bash scripts/setup-android-toolchain.sh` and retry Step 6. If Gradle still cannot run,
declare verification impossible. Treat all code as **unverified**. Compensate per G2.

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

**Trigger:** Compose screen, theme, or design-system component change.

**Action (all required before push):**

- `@Preview` on every touched content-composable file (one per distinct UI state)
- `./gradlew :app:verifyRoborazziDevDebug` green; run `recordRoborazziDevDebug` and commit
  baselines when visuals change intentionally
- Device/emulator check only when Roborazzi cannot cover the change; flag if verify impossible (G1)
- **Sibling-size check on new/changed baselines:** when a container repeats components (chip
  row, button group, tab bar), **measure** their rendered heights in the recorded screenshot
  (pixel-measure bounding boxes — do not eyeball); unequal siblings are a defect. A wrong
  baseline is self-consistent, so `verifyRoborazzi` will never flag it — guard size invariants
  with a Compose UI test on layout bounds (pattern: `JournalChipRowConsistencyTest`).
- **Never nest `minimumInteractiveComponentSize`-based modifiers (`proIconClickable`,
  `proSelectable`) inside compact components** (chips, pills, dense rows) — the 48dp floor
  inflates the parent's layout. For a secondary micro-target inside an already-tappable
  surface use `clip(CircleShape)` + `proCircularRippleClickable` instead.
- **Scrollable-row edge affordance (mandatory on any new/changed `horizontalScroll`/`LazyRow`):**
  when content overflows the viewport, the clipped edge must fade, peek, or otherwise signal
  "more content" — never a flat hard cut through a tile/chip with no visual cue, which reads as
  broken content rather than scrollable overflow. Pixel-check the recorded baseline at the clip
  boundary (sample colors near the edge — a hard cut shows an abrupt color jump; a correct fade
  ramps gradually to the surrounding background) rather than eyeballing a shrunk screenshot.
  Reference implementation: `horizontalFadingEdge` in
  `feature/categories/.../ui/CategoryNewSheet.kt` (`drawWithContent` + `BlendMode.DstIn` gradient
  keyed off `ScrollState.value`/`maxValue`) — **the fade/edge-affordance modifier must wrap the
  scroll modifier (apply outside it in the chain), not nest inside it**, or it measures the full
  unclipped content width and draws off-screen instead of at the visible viewport edge.
- **Product/UX audit pass, not just pixel-diff (invoke the skill, don't self-reason):** run the
  `compose-product-auditor` skill (post-implementation mode) against the rendered screenshots for
  user-confusing layout defects — incomplete/cropped content from a fixed padding or row cap, a
  scrollable row's hard-cut edge (see bullet above), crowded or misaligned elements, inconsistent
  affordances. Confirming that your own code change rendered as intended is not the same check as
  this audit — running the former is not a substitute for running the latter. A wrong baseline is
  self-consistent, so `verifyRoborazzi` alone will never flag it; this pass is what catches it.

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

## Android Agent Skills

Official Android skills from [android/skills](https://github.com/android/skills) are installed
under `.agents/skills/` (Cursor Agent Skills standard). See `.agents/skills/README.md`.

### Install / update

```bash
./scripts/install-android-skills.sh
```

Bootstraps the [Android CLI](https://developer.android.com/tools/agents/android-cli) when missing,
then runs `android skills add --all --agent=cursor --project=.`.

The Cursor `session-start` hook installs skills automatically when `.agents/skills/` is absent.

### When to use

Agents should consult relevant skills for specialized Android workflows (testing setup, edge-to-edge,
navigation, R8 analysis, AGP upgrades, **design spec → Compose**, etc.). **This file and product
docs take precedence** when they conflict with a skill.

| Skill | Relevance |
|---|---|
| **`design-spec-to-compose`** | **Step 1 — Implementing UI from `design-system-spec/` screen + component specs and PNGs** |
| **`compose-motion-polish`** | **Step 2 — Touch targets, ripple/press feedback, motion tokens, navigation transitions** |
| **`compose-product-auditor`** | **Step 3, twice — planning advisory pass (UI/UX-impacting changes) and pre-push visual-verification audit (states, wiring, a11y, i18n, resilience)** |
| **`kotlin-lint-style`** | **Writing/editing any `.kt` file — ktlint formatting + detekt baseline workflow, avoids Step 6 verify failures** |
| `android-cli` | SDK, emulator, docs, layout inspection |
| `testing-setup` | Unit / instrumented / screenshot tests |
| `edge-to-edge` | System bar insets |
| `navigation-3` | Compose navigation |
| `migrate-xml-views-to-jetpack-compose` | View → Compose migration |
| `r8-analyzer` | APK size / shrinker |
| `perfetto-trace-analysis` | Performance traces |

---

## Instruction Precedence

```
AGENTS.md  >  docs/project_philosophy.md  >  docs/finance_tracker_product.md  >  .cursor/commands/*  >  .agents/skills/*  >  doc/*  >  AGENTIC_WORKFLOWS_GUIDE.md  >  general AI knowledge
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
| `docs/project_philosophy.md` | Project goal, beliefs, decision framework (derived from PRD) |
| `docs/finance_tracker_product.md` | Authoritative product vision, MVP scope, roadmap |
| `docs/user_stories/` | Per-service user stories (`US-<SERVICE>-<n>.md`, Given/When/Then Acceptance Criteria + Functional Requirements) — audited against for every logic/unit-test change, see Step 4.5 |
| `docs/module_structure.md` | KMP module map and dependency rules |
| `design-system-spec/` | **Authoritative screen specs** (markdown + PNGs + component specs + tokens) |
| Claude Design project **"Pro Expense - Finance Tracker"** | Hi-Fi mockup canvas mirroring `design-system-spec/` — find it via the design-sync tool's project listing (matched by name, scoped to the maintainer's account); no link is committed here since this repo is public |
| Claude Design project **"Pro Expense - Finance Tracker"** (canvas variant) | A second, same-named Claude Design project — regular canvas type, not the design-system project above, so it does **not** appear in the design-sync tool's project listing; only reachable via a maintainer-shared link. Holds `Hi-Fi Variant - Blue Banking.html` (`variant-blue-*.jsx` sources) — **the adopted visual system**: `design-system-spec/tokens.md` is derived from it, and where a screen spec's visuals conflict with this canvas, the canvas wins (see `design-system-spec/screens/README.md`); no link is committed here since this repo is public |
| `.agents/skills/design-spec-to-compose/` | Step 1 — Design spec → Compose workflow |
| `.agents/skills/compose-motion-polish/` | Step 2 — Motion, navigation transitions, interaction affordances |
| `.agents/skills/compose-product-auditor/` | Step 3, twice — planning advisory pass + pre-push visual-verification Compose product/UX auditor |
| `scripts/canvas-render/` | Step 3.1 / Step 6 gate 5 — renders a real canvas component (React/Babel) for direct screenshot comparison against Roborazzi output, instead of relying on token-value diffs or stale pre-rendered assets in the canvas project. See its `README.md`. |
| `.agents/skills/kotlin-lint-style/` | ktlint formatting rules + detekt baseline regeneration workflow |
| `.agents/skills/kotlin-lint-style/lint-retrospective.md` | Append-only lookup table of ktlint/detekt/Android-lint findings hit before, keyed by rule ID, with the fix that applied — check before diagnosing, append after resolving |
| `AGENTIC_WORKFLOWS_GUIDE.md` | Reference template (OnDeviceLab origin) |
| `.cursor/commands/` | Slash commands |
| `.agents/skills/` | Agent skills — Android (`install-android-skills.sh`) + project Compose workflow (`design-spec-to-compose`, `compose-motion-polish`, `compose-product-auditor`) + `kotlin-lint-style` |
| `.cursor/context/project_codebase.md` | Live codebase snapshot |
| `.cursor/context/retrospectives.md` | Append-only post-mortem guard log |
| `app/build.gradle.kts` | App module build config |
| `gradle/libs.versions.toml` | Version catalog |
