---
description: Convert a Claude Design / Figma handoff (HTML/JSX export + PNG) into Jetpack Compose UI. Use whenever implementing a design handoff, building a screen from a mockup, or translating exported markup into composables. Enforces foundations-first build order.
globs: ["**/ui/**/*.kt", "**/presentation/**/*.kt", "**/*Screen.kt", "**/*Composable.kt"]
alwaysApply: false
---

# Design Handoff → Jetpack Compose

You are translating a design handoff into production Jetpack Compose. The handoff is a
*lossy* source: an HTML/JSX export carries web layout idioms, and prose specs carry
adjectives. Your job is to recover exact values and rebuild them as idiomatic Compose,
**bottom-up**, verifying against the rendered image at every step.

Never implement a whole screen in one pass. Fidelity collapses when you do.

## Required inputs — refuse to guess

Before writing any composable, confirm you have:
1. The exported markup (HTML/JSX) — source of exact values (spacing, color, radii, weights).
2. The rendered PNG/screenshot — the visual ground truth you check yourself against.
3. The project theme files — `Theme.kt`, color/type/shape definitions, any `Dimens`/spacing tokens.

If the rendered image is missing, say so and ask for it. The markup alone underdetermines
the pixels; that is the single biggest cause of wrong output.

## Build order (non-negotiable)

Build in this sequence. Do not start a layer until the layer below it compiles and has a
verified `@Preview`.

### 1. Foundations (tokens) — FIRST, always
Extract raw values from the markup into a token table, then map them onto the existing theme.
- Colors → `MaterialTheme.colorScheme` (or the project's color tokens). Never introduce a new hex literal if an existing token matches within tolerance.
- Typography → `MaterialTheme.typography` styles. Map font size, weight, line-height, letter-spacing.
- Shapes / corner radii → `MaterialTheme.shapes` or shape tokens.
- Spacing → the project spacing scale (e.g. `Dimens`/4dp grid). Snap exported px to the nearest grid step; flag anything that doesn't fit.

Output of this step is a short mapping table (design value → token) for the user to confirm.
Do NOT create new tokens silently — list proposed additions and ask.

### 2. Component triage — classify every element BEFORE building
Most elements that *look* custom are not. For each distinct element in the handoff, climb
DOWN this ladder and stop at the first rung that fits:

1. **Reuse** — a component already exists in this codebase. Search first; reuse it.
2. **Material, re-themed** — a Material 3 component matches with only token/param changes
   (`colors`, `shape`, `contentPadding`, sizes via public params). Most "custom" buttons,
   cards, chips, and fields are really this.
3. **Material, composed** — reachable via a Material component's slot APIs, or by wrapping /
   combining a few (e.g. a bespoke row is usually `ListItem`, or `Surface` + `Row`).
4. **Material foundations** — drop to `Surface`, `BasicText`, `BasicTextField`, etc. Lower
   level, but still carry theming, elevation, and content-color propagation.
5. **Truly custom** — none of the above fits. Build from Compose primitives (see
   "Building custom components" below).

Only reach rung 5 when the *anatomy or behavior* — not just the styling — has no Material
analogue (selection indicators, segmented/stacked layouts, gauges/rings, charts, bespoke
gestures, layered overlapping layouts).

You often cannot tell the rung from the image alone — and guessing wrong is the main failure
mode. So:
- Use the **markup as evidence**, not just the picture. Unusual class structure, custom SVG,
  `clip-path`, layered `position:absolute`, or custom scroll/snap signal "custom" more
  reliably than the rendered look.
- **Declare and confirm.** Emit a classification table — *element → rung → one-line reason →
  proposed Material base (if any)* — and pause on ambiguous cases before writing code.
- When unsure about an unfamiliar Compose API while classifying, consult current docs
  (e.g. the Context7 MCP) rather than improvising.

### 3. Atoms
Smallest reusable composables: buttons, text fields, icons, labels, badges, chips.
- Stateless. State in, events out (lambdas).
- One composable per file, named after its role.
- Each gets a `@Preview` (light + dark).

### 4. Molecules
Small combinations: labeled input, list-item row, card header, form field with error.
Compose atoms; add no new tokens.

### 5. Organisms
Sections: a full card, a form, a top app bar with content, a populated list.
Compose molecules + atoms. Use slot APIs (content lambdas) for anything variable.

### 6. Screen
Assemble organisms into the full screen. Split into two:
- A **stateless** `XxxScreenContent(state, onEvent, modifier)` — fully previewable.
- A **stateful** `XxxScreen(viewModel)` wrapper that hoists state and wires events.

### 7. Verify
For each layer, render the `@Preview` and compare against the PNG before moving up.
Report mismatches explicitly; fix at the correct layer (a spacing bug is a token bug, not a screen bug).

## Compose correctness rules

- **State hoisting:** stateless composables receive state + callbacks; only the outer wrapper holds `remember` / `collectAsStateWithLifecycle` / ViewModel.
- **Modifier discipline:** every composable takes `modifier: Modifier = Modifier` as the last optional param and applies it to its root, untouched, before its own modifiers.
- **Tokens, not literals:** read color/type/shape/spacing from the theme. A raw `Color(0xFF...)`, hardcoded `16.dp` where a token exists, or inline `TextStyle` is a defect.
- **Units:** `dp` for size/space, `sp` for text. Respect the spacing grid.
- **Accessibility:** real `contentDescription` (or explicit `null` for decorative), ≥48.dp touch targets, `semantics` where needed.
- **Recomposition:** stable parameters; hoist/`remember` derived values; don't allocate lambdas or lists in hot paths.
- **Previews:** every public composable has at least one `@Preview`; screens get light/dark and a representative state set (loading/empty/error/content).

## Compose implementation best practices

Deeper guidance for turning UI into Compose code. These are where generated Compose most
often goes wrong.

### State & unidirectional data flow
- One source of truth. State flows down, events flow up. Expose a single immutable
  `data class XxxUiState`; never pass mutable fields or `MutableState` as parameters.
- Hoist state to the lowest common ancestor that needs it — no higher.
- `remember` for transient UI state; `rememberSaveable` for state that must survive config
  change / process death.
- Collect flows with `collectAsStateWithLifecycle()`, not `collectAsState()`.
- Never mutate state during composition.

### Stability & recomposition performance
- Mark UI state holders `@Immutable` / `@Stable`. A bare `List`/`Map` parameter is *unstable*
  and forces recomposition — use `ImmutableList` / `PersistentList`
  (kotlinx.collections.immutable) for collection params.
- Defer fast-changing reads to the latest phase: prefer lambda modifiers
  (`Modifier.offset { }`, `graphicsLayer { }`, `drawBehind { }`) for scroll/animation values
  so reads happen in layout/draw, not composition.
- `derivedStateOf` when a value is computed from state that changes more often than the result.
- Pass the specific values a composable needs, not whole objects, to keep recomposition scopes small.

### Side effects
- `LaunchedEffect(key)` for suspend work tied to composition; key it so it restarts only when intended.
- `rememberCoroutineScope()` to launch from event callbacks (e.g. onClick).
- `DisposableEffect` for resources needing cleanup; `rememberUpdatedState` for values
  captured by long-lived effects. Never `GlobalScope`; never run effects inline in the composition body.

### Lists
- `LazyColumn` / `LazyRow` for any scrollable collection — never `Column` + `verticalScroll`
  for long/unbounded lists.
- Always give items a stable `key` (not the index); set `contentType` for mixed item types.
- Never nest two scrollables on the same axis.

### Modifier order & reuse
- Order is semantic — `padding` then `background` differs from the reverse; `clip` before
  `background` clips the fill. Match the design's intent, don't apply by habit.
- Apply the incoming `modifier` to the root first, then chain component-specific modifiers.
- Extract repeated modifier chains into factory functions; don't rebuild large chains inline.

### Resources & strings
- User-facing text via `stringResource(...)` (and `pluralStringResource` for counts) — no
  hardcoded string literals.
- Images via `painterResource` / `ImageVector` with a real `contentDescription`.

### Naming & file conventions
- UI-emitting composables: PascalCase, noun-named, return `Unit`. State-producing helpers: `rememberXxx`.
- One component per file. `@Preview` functions are `private` and suffixed `Preview`.

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
| CSS `gradient` / `box-shadow` | `Brush` / `Modifier.shadow` + elevation tokens        |

## Building custom components (triage rung 5)

Reach here only after triage confirms no Material path fits. "Correct" means two things:
pick the right primitive, and make the result indistinguishable from a Material component in
how it is themed and used.

### Pick the primitive by what is actually custom
- **Custom arrangement** → start with `Row` / `Column` / `Box`. Drop to a custom `Layout`
  (own measure/place policy) only if positioning genuinely can't be expressed otherwise.
  Use `SubcomposeLayout` ONLY when one child's composition depends on another child's
  measured size — it is expensive, never the default.
- **Custom drawing** (shapes, rings, gauges, decorative backgrounds) → `Canvas`,
  `Modifier.drawBehind` / `drawWithContent`, or a custom `Shape` via `GenericShape`.
- **Custom visual treatment** on otherwise-normal layout → a custom `Modifier`
  (clip, border, brush background, shadow).
- **Custom interaction** → `Modifier.pointerInput` with gesture detectors, or
  `toggleable` / `selectable` / `AnchoredDraggable` with an `interactionSource`.

### Make it behave like a Material component
- **Build ON foundations, not from zero.** Wrap the root in `Surface` so it inherits color,
  elevation, shape, and content-color propagation; pull values from `MaterialTheme`; use the
  standard `ripple()` indication. This is what keeps it correct in dark mode and under theme
  changes without extra work.
- **Same API shape as everything else:** `modifier: Modifier = Modifier` last and applied to
  the root first; state hoisted (value in, `onXxx` out); slot APIs
  (`content: @Composable () -> Unit`, leading/trailing slots) for variable parts.
- **Expose its own tokens** the Material way: a `XxxDefaults` object plus a small
  `XxxColors` / dimensions holder with theme-derived defaults — instead of hardcoding custom
  values inside. Any value not already in the design system is surfaced for approval as a
  token, never buried as a literal.
- **Add semantics explicitly** — Material gives these for free, a custom component does not:
  `Modifier.semantics` with the right `role` / `stateDescription`, correct merge/clear, and
  `minimumInteractiveComponentSize()` for touch targets.
- **Preview every state** (default / pressed / selected / disabled / error) and compare each
  against the PNG.
- When unsure of an API, consult current Compose docs (e.g. Context7 MCP) rather than guess.

## Forbidden

- Implementing the full screen before atoms/molecules exist.
- Inventing components that duplicate existing ones — search the codebase and reuse first.
- Building a custom component before checking rungs 1–4 of the triage ladder.
- Hardcoding a custom component's values as literals instead of exposing a `Defaults` token holder.
- New color/spacing/type values without surfacing them for approval.
- Copying CSS layout structure 1:1 instead of mapping to Compose layout primitives.
- Skipping previews or skipping the image comparison.
- Passing a bare `List`/`Map` (or `MutableState`) as a composable parameter.
- `Column` + `verticalScroll` for long lists, or lazy items without a stable `key`.
- `collectAsState()` without lifecycle, or launching coroutines from `GlobalScope` / the composition body.
- Hardcoded user-facing strings instead of `stringResource`.

## Output protocol

1. Restate the inputs you have (and flag any missing).
2. Produce the token mapping table; pause for confirmation on new tokens.
3. Produce the component triage table (element → rung → reason → Material base); pause on ambiguous cases.
4. Build layer by layer; after each layer, show the file(s) + preview and state how it matches the PNG.
5. Only then compose upward.
