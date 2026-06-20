# CLAUDE.md

This file orients Claude Code (claude.ai/code) when working in this repository.

## Source of truth

**[`AGENTS.md`](AGENTS.md) is the authoritative agent instruction file.** It is shared by
all AI agents (Claude Code, Cursor, etc.). Read it in full before acting — it takes precedence
over general AI knowledge and over any skill.

@AGENTS.md

## Quick orientation for Claude Code

The sections below summarize what AGENTS.md covers; follow AGENTS.md for the full detail.

- **What this is** — Finance Tracker (Android ships as *Pro Expense*): an offline-first,
  privacy-first personal finance notebook. KMP-shared business logic, Jetpack Compose (Android),
  SwiftUI (iOS, future). Active branch for the v2 architecture refresh: `refactor/v2-migration`.
- **Architecture** — Compose/SwiftUI → ViewModel → `feature:*` repository contracts (KMP
  `commonMain`) → `core:data` contracts → platform storage (Room/CoreData). Features never depend
  on other features. See `docs/module_structure.md`.
- **Workflow** — Follow the 8-step gate system in AGENTS.md. Verify in-session before pushing;
  end every implementation task with the **Workflow status** block (Step 7.5).
- **Toolchain** — Run `bash scripts/setup-android-toolchain.sh` once per fresh environment before
  any Gradle command (writes `local.properties`, installs SDK 36 / build-tools 36.0.0).
- **Verify** — Default flavor `devDebug`. Preferred gate: `./gradlew verifyAll`. UI changes also
  require `@Preview` per state and a green `./gradlew :app:verifyRoborazziDevDebug` before push.
- **Branches** — When the user names a branch, check it out; don't create a new one. One working
  branch per session. Never open pull requests unless explicitly asked.

## Common commands

```bash
bash scripts/setup-android-toolchain.sh    # one-time toolchain setup (fresh env)
./gradlew verifyAll                         # build + unit tests + screenshot tests
./gradlew :app:testDevDebugUnitTest         # unit tests (logic changes)
./gradlew :app:compileDevDebugKotlin        # fast compile check
./gradlew :app:verifyRoborazziDevDebug      # screenshot verify (UI changes)
./gradlew :app:recordRoborazziDevDebug      # record new screenshot baselines
```

## Skills

Project Compose workflow skills live under `.agents/skills/`
(`design-spec-to-compose`, `compose-motion-polish`, `compose-product-auditor`) alongside the
official Android skills. AGENTS.md and the product docs take precedence when a skill conflicts.
