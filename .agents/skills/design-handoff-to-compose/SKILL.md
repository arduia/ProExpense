---
name: design-handoff-to-compose
description: Convert a Claude Design / Figma handoff (HTML/JSX export + PNG) into Jetpack Compose UI. Use whenever implementing a design handoff, building a screen from a mockup, or translating exported markup into composables. Enforces foundations-first build order.
license: Pro Expense project skill
metadata:
  author: Pro Expense
  last-updated: '2026-06-18'
  keywords:
  - Jetpack Compose
  - design handoff
  - Figma
  - mockup
  - ProExpenseTheme
  - Roborazzi
globs:
  - '**/ui/**/*.kt'
  - '**/presentation/**/*.kt'
  - '**/*Screen.kt'
  - '**/*Composable.kt'
alwaysApply: false
---

# Design Handoff → Jetpack Compose

You are translating a design handoff into production Jetpack Compose. The handoff is a
*lossy* source: an HTML/JSX export carries web layout idioms, and prose specs carry
adjectives. Your job is to recover exact values and rebuild them as idiomatic Compose,
**bottom-up**, verifying against the rendered image at every step.

Never implement a whole screen in one pass. Fidelity collapses when you do.

## Pro Expense handoff bundle

For this repo, the authoritative handoff lives in `design_handoff_pro_expense/`:

| Asset | Path | Use |
|---|---|---|
| Tokens | `design-tokens.json` | Colors, typography, dimensions, motion → `ProExpenseTheme` |
| Components | `components.yaml` | Primitive catalog — reuse before inventing |
| Screens | `screens-manifest.yaml` | Composable names, states, sample data, preview targets |
| Reference PNGs | `reference-images/*.png` | Visual ground truth (58 states @ 414×868 dp) |
| Narrative spec | `DESIGN-SYSTEM.md` | Human-readable rules + Compose mapping §10 |
| Prototype | `interactive-prototype-quick-log.html` | Motion/interaction feel (optional) |
| Icons | `icons/*.svg` | Convert to vector drawables → `ProIcon` |

**Theme implementation:** `shared/src/androidMain/kotlin/com/arduia/expense/ui/theme/` (`ProExpenseTheme`, `ProColors`, `ProTypography`, `ProDimens`, shapes, motion).

**Shared primitives:** `shared/src/androidMain/kotlin/com/arduia/expense/ui/design/` (`ProButton`, `SearchField`, `TransactionRow`, `HomeBottomNav`, …).

**Roborazzi:** screenshot baselines at **427×952 dp** (Pixel 9 Pro). Record with `./gradlew :app:recordRoborazziDevDebug`; verify before push.

When bundle tokens and reference PNGs disagree, **reference PNGs win** (per handoff README).

## Required inputs — refuse to guess

Before writing any composable, confirm you have:
1. The exported markup (HTML/JSX) — source of exact values (spacing, color, radii, weights).
2. The rendered PNG/screenshot — the visual ground truth you check yourself against.
3. The project theme files — `ProExpenseTheme` (`Theme.kt`, `Color.kt`, `Type.kt`, `Dimensions.kt`, `Shape.kt`, `Motion.kt`).

If the rendered image is missing, say so and ask for it. The markup alone underdetermines
the pixels; that is the single biggest cause of wrong output.

## Build order (non-negotiable)

Build in this sequence. Do not start a layer until the layer below it compiles and has a
verified `@Preview`.

### 1. Foundations (tokens) — FIRST, always
Extract raw values from the markup into a token table, then map them onto `ProExpenseTheme`.
- Colors → `ProExpenseTheme.colors` / `ProColors`. Never introduce a new hex literal if an existing token matches within tolerance.
- Typography → `ProExpenseTheme.typography` / `ProTypography`. Map font size, weight, line-height, letter-spacing.
- Shapes / corner radii → `ProExpenseTheme.shapes`. 
- Spacing → `ProExpenseTheme.dimensions` (`ProDimens`). Snap exported px to the nearest grid step; flag anything that doesn't fit.

Cross-check against `design-tokens.json`. Output of this step is a short mapping table (design value → token) for the user to confirm.
Do NOT create new tokens silently — list proposed additions and ask.

### 2. Atoms
Smallest reusable composables: buttons, text fields, icons, labels, badges, chips.
- Search `ui/design/` and `components.yaml` first — **reuse** existing primitives (`ProButton`, `FilterChip`, `LogCategoryBadge`, …).
- Stateless. State in, events out (lambdas).
- One composable per file, named after its role.
- Each gets a `@Preview` wrapped in `ProExpenseTheme` (default artboard 427×952 dp unless the screen spec defines otherwise).

### 3. Molecules
Small combinations: labeled input, list-item row, card header, form field with error.
Compose atoms; add no new tokens.

### 4. Organisms
Sections: a full card, a form, a top app bar with content, a populated list.
Compose molecules + atoms. Use slot APIs (content lambdas) for anything variable.

### 5. Screen
Assemble organisms into the full screen. Split into two:
- A **stateless** `XxxScreenContent(state, onEvent, modifier)` — fully previewable.
- A **stateful** `XxxScreen(viewModel)` wrapper that hoists state and wires events.

Match `screens-manifest.yaml` for composable package, states, and sample data.

### 6. Verify
For each layer, render the `@Preview` and compare against the reference PNG before moving up.
Add a Roborazzi `captureRoboImage` test per distinct UI state; run `./gradlew :app:verifyRoborazziDevDebug`.
Report mismatches explicitly; fix at the correct layer (a spacing bug is a token bug, not a screen bug).

## Compose correctness rules

- **State hoisting:** stateless composables receive state + callbacks; only the outer wrapper holds `remember` / `collectAsStateWithLifecycle` / ViewModel.
- **Modifier discipline:** every composable takes `modifier: Modifier = Modifier` as the last optional param and applies it to its root, untouched, before its own modifiers.
- **Tokens, not literals:** read color/type/shape/spacing from `ProExpenseTheme`. A raw `Color(0xFF...)`, hardcoded `16.dp` where a token exists, or inline `TextStyle` is a defect.
- **Units:** `dp` for size/space, `sp` for text. Respect the spacing grid.
- **Accessibility:** real `contentDescription` (or explicit `null` for decorative), ≥44.dp touch targets (`ProDimens.touchTargetMin`), `semantics` where needed.
- **Recomposition:** stable parameters; hoist/`remember` derived values; don't allocate lambdas or lists in hot paths.
- **Previews:** every public composable file under `app/src/main` or `feature/*/androidMain` has at least one `@Preview`; screens get representative states (loading/empty/error/content) per `screens-manifest.yaml`.

## Web idiom → Compose mapping

The HTML/JSX export will carry CSS reasoning. Translate, don't transliterate:

| Export (web)                  | Compose                                              |
|-------------------------------|------------------------------------------------------|
| `display:flex; row`           | `Row` + `horizontalArrangement` / `verticalAlignment`|
| `display:flex; column`        | `Column` + `verticalArrangement`                     |
| `flex: 1` / `flex-grow`       | `Modifier.weight(1f)`                                 |
| `justify-content`             | `Arrangement.*`                                       |
| `align-items`                 | `Alignment.*`                                         |
| `position: absolute` overlap  | `Box` + alignment                                     |
| `gap`                         | `Arrangement.spacedBy(x.dp)`                          |
| `padding` / `margin`          | `Modifier.padding(...)` (no margin concept — use padding/spacing) |
| `px`                          | `dp` (snap to grid)                                   |
| media queries / breakpoints   | `WindowSizeClass` / `BoxWithConstraints`              |
| CSS `gradient` / `box-shadow` | `Brush` / `Modifier.shadow` + `ProExpenseTheme.elevation` |

## Forbidden

- Implementing the full screen before atoms/molecules exist.
- Inventing components that duplicate existing ones — search `ui/design/` and `components.yaml` first.
- New color/spacing/type values without surfacing them for approval.
- Copying CSS layout structure 1:1 instead of mapping to Compose layout primitives.
- Skipping previews or skipping the image comparison.
- Pushing Compose UI without green Roborazzi verify (see `AGENTS.md` Step 6 UI gate).

## Output protocol

1. Restate the inputs you have (and flag any missing).
2. Produce the token mapping table; pause for confirmation on new tokens.
3. Build layer by layer; after each layer, show the file(s) + preview and state how it matches the PNG.
4. Only then compose upward.

## Precedence

`AGENTS.md` and `docs/project_philosophy.md` override this skill when they conflict (architecture, module boundaries, verify-before-push, no cross-feature dependencies).
