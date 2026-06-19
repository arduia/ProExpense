# Android Agent Skills

Official [Android skills](https://github.com/android/skills) installed for Cursor via the
[Agent Skills](https://agentskills.io/home) open standard.

**Source:** https://github.com/android/skills (Apache 2.0)

## Update

```bash
./scripts/install-android-skills.sh
```

Requires the [Android CLI](https://developer.android.com/tools/agents/android-cli). The install
script bootstraps it when missing.

## Layout

Each subdirectory contains a `SKILL.md` with YAML frontmatter. Cursor discovers these under
`.agents/skills/` automatically.

**Project skills** (not overwritten by `install-android-skills.sh`):

Compose implementation workflow — run in order for new UI; linked from `AGENTS.md` § Compose UI:

| Step | Skill | Path |
|------|-------|------|
| 1 — Build | `design-handoff-to-compose` | `.agents/skills/design-handoff-to-compose/SKILL.md` |
| 2 — Polish | `compose-motion-polish` | `.agents/skills/compose-motion-polish/SKILL.md` |
| 3 — Audit | `compose-product-auditor` | `.agents/skills/compose-product-auditor/SKILL.md` |

**Official Android skills** (installed/updated via `install-android-skills.sh`):

## Precedence

Project `AGENTS.md` takes precedence over skill guidance when they conflict.

## Skills most relevant to Pro Expense

| Skill | When to use |
|---|---|
| **`design-handoff-to-compose`** | **Step 1 — Building Compose UI from `design_handoff_pro_expense/` mockups, PNGs, or HTML prototype** |
| **`compose-motion-polish`** | **Step 2 — Touch targets, ripple/press feedback, motion tokens, navigation transitions** |
| **`compose-product-auditor`** | **Step 3 — Pre-merge audit: states, wiring, a11y, i18n, performance, sensitive data** |
| `android-cli` | SDK, emulator, docs search, layout inspection |
| `testing-setup` | Unit, instrumented, screenshot test setup |
| `edge-to-edge` | System bar insets and window layout |
| `navigation-3` | Navigation 3 / Compose navigation patterns |
| `migrate-xml-views-to-jetpack-compose` | Legacy View → Compose migration |
| `r8-analyzer` | APK size / R8 shrinker analysis |
| `perfetto-trace-analysis` | Performance trace debugging |
