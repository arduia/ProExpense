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

## Precedence

Project `AGENTS.md` takes precedence over skill guidance when they conflict.

## Skills most relevant to Pro Expense

| Skill | When to use |
|---|---|
| `android-cli` | SDK, emulator, docs search, layout inspection |
| `testing-setup` | Unit, instrumented, screenshot test setup |
| `edge-to-edge` | System bar insets and window layout |
| `navigation-3` | Navigation 3 / Compose navigation patterns |
| `migrate-xml-views-to-jetpack-compose` | Legacy View → Compose migration |
| `r8-analyzer` | APK size / R8 shrinker analysis |
| `perfetto-trace-analysis` | Performance trace debugging |
