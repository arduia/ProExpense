# CODING AGENTS: READ THIS FIRST

This is a **handoff bundle** from Claude Design (claude.ai/design).

A user mocked up designs in HTML/CSS/JS using an AI design tool, then exported this bundle so a coding agent can implement the designs for real.

## What you should do — IMPORTANT

**Read `pro-expense-finance-tracker/project/Design System.html` in full.** The user had this file open when they triggered the handoff, so it's almost certainly the primary design they want built. Read it top to bottom — don't skim. Then **follow its imports**: open every file it pulls in (shared components, CSS, scripts) so you understand how the pieces fit together before you start implementing.

**If anything is ambiguous, ask the user to confirm before you start implementing.** It's much cheaper to clarify scope up front than to build the wrong thing.

## About the design files

The design medium is **HTML/CSS/JS** — these are prototypes, not production code. Your job is to **recreate them pixel-perfectly** in whatever technology makes sense for the target codebase (React, Vue, native, whatever fits). Match the visual output; don't copy the prototype's internal structure unless it happens to fit.

**Don't render these files in a browser or take screenshots unless the user asks you to.** Everything you need — dimensions, colors, layout rules — is spelled out in the source. Read the HTML and CSS directly; a screenshot won't tell you anything they don't.

## Designing in Claude Design

Use **`CLAUDE_DESIGN_GUIDE.md`** in this folder — copy-paste prompts for design system setup,
per-flow screens, and export/handoff requirements (tokens JSON, PNG references @ 414×868, SVG
icons) so implementations map cleanly to Jetpack Compose and Roborazzi verification.

## Bundle contents

- `pro-expense-finance-tracker/README.md` — this file
- `pro-expense-finance-tracker/CLAUDE_DESIGN_GUIDE.md` — prompts for Claude Design sessions
- `pro-expense-finance-tracker/project/` — the `Pro Expense - Finance Tracker` project files (HTML prototypes, assets, components)
