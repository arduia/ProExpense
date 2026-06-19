---
description: Polish Compose UI/UX — interaction affordances (touch targets, ripple, press feedback), motion/animation, and navigation transitions. Use when adding, editing, or suggesting animations and screen transitions, or when a UI feels abrupt, static, or unpolished (tiny tap targets, weak ripple, hard-cut navigation, items popping in).
globs: ["**/ui/**/*.kt", "**/presentation/**/*.kt", "**/*Screen.kt", "**/navigation/**/*.kt", "**/*Nav*.kt"]
alwaysApply: false
---

# Compose Motion, Navigation & Interaction Polish

General-purpose rule for making Compose UI feel intentional and seamless. Implementation
that merely compiles and matches a static preview is NOT done — affordances and motion are
what separate "works" from "feels right".

Core principle: **motion must serve comprehension** (continuity, feedback, hierarchy, spatial
model). Decoration-only animation is rejected. When unsure of an exact current API, detect the
project's Compose / Navigation versions and consult current docs (e.g. the Context7 MCP)
rather than guessing — these APIs evolve.

## 1. Interaction affordances — fix these FIRST (highest ROI)

The most common defect: tiny tap targets and weak/missing press feedback.

- **Touch target ≥ 48dp.** A 24dp icon needs a 48dp touch+ripple area. Apply
  `Modifier.minimumInteractiveComponentSize()`, or size the clickable container to `48.dp`
  with the visual centered — the visual stays 24dp, the target grows.
- **Use `IconButton` for icon actions.** It provides the 48dp target and a correctly-sized
  bounded ripple for free. Do NOT put `Modifier.clickable` directly on a 24dp `Icon` — that
  is exactly what produces the tiny ripple.
- **Ripple API:** use `ripple()` (Material3). `rememberRipple()` is deprecated. For a custom
  clickable: `clickable(interactionSource = remember { MutableInteractionSource() }, indication = ripple())`.
- **Bounded vs unbounded:** bounded ripple for rectangular surfaces; `ripple(bounded = false, radius = …)`
  for circular icon actions so the splash is a clean circle.
- **All interactive states:** drive pressed / focused / hovered feedback from an
  `interactionSource` in custom components, not just the click.

## 2. Choosing the right animation API

| Need                                                   | API                              |
|--------------------------------------------------------|----------------------------------|
| Animate one value (color, size, alpha, offset) on state change | `animate*AsState`        |
| Enter/exit a composable (show/hide)                    | `AnimatedVisibility`             |
| Swap between different content by state                | `AnimatedContent` (or `Crossfade` for plain fade) |
| Auto-animate a container's size change                 | `Modifier.animateContentSize()`  |
| Coordinate several values off one state                | `updateTransition`               |
| Gesture-driven / interruptible / imperative            | `Animatable`                     |
| Continuous loop (loaders, pulsing)                     | `rememberInfiniteTransition`     |
| Lazy list item add/remove/move                         | `Modifier.animateItem()`         |

## 3. Motion specs

- Prefer **spring physics** (`spring()`) for gesture-driven and interruptible motion — it
  handles interruption naturally and feels organic. Use `tween()` + easing for deterministic
  timed motion.
- If the project uses Material 3 (expressive) motion, pull specs from `MaterialTheme.motionScheme`
  instead of hand-rolling durations.
- Otherwise **centralize durations/easings as motion tokens** (e.g. fast ≈ 100–150ms, medium
  ≈ 200–300ms, standard easing) — never scatter magic numbers across composables.
- Keep it short. Long animations feel sluggish; sub-300ms for most UI transitions.

## 4. Navigation transitions

Abrupt screen cut-overs are the "not seamless" problem. Fix at the navigation layer.

- **Define transitions on the nav graph, not inside screens.**
- **Navigation 2:** set `enterTransition` / `exitTransition` / `popEnterTransition` /
  `popExitTransition` on `composable(...)`, using `AnimatedContentTransitionScope`
  (`slideIntoContainer`, `fadeIn`/`fadeOut`, `slideOutOfContainer`). Typical: forward =
  slide in from end + fade; back = mirror it.
- **Navigation 3:** configure motion via `NavDisplay` (`transitionSpec` / entry-level specs).
- **Detect which navigation library and version the project uses** before writing code, and
  verify the exact API via Context7 — Nav2 and Nav3 differ.
- Map intent to a Material motion pattern:
  - **Shared axis** (X/Y/Z) — for forward/back or sibling navigation with spatial relationship.
  - **Fade through** — for unrelated destinations (e.g. bottom-nav top-level switches).
  - **Container transform / shared element** — for an item expanding into its detail screen.
- **Support predictive back** — let back be gesture-driven with progress, not an instant pop.

## 5. Shared element transitions (use selectively)

For "tap a list item, it expands into the detail screen" continuity.

- Wrap the `NavHost` / `NavDisplay` in a `SharedTransitionLayout`; pass the
  `SharedTransitionScope` down; mark elements with `Modifier.sharedElement(...)` /
  `sharedBounds(...)` using `rememberSharedContentState(key = …)`.
- **Keys come from stable domain identifiers** (e.g. item id), NEVER list index or position —
  index keys break or mismatch on recomposition.
- **Consistent layout contracts:** matching padding/size/alignment between source and
  destination, or the element visibly jumps. Use `skipToLookaheadSize()` to stop text reflow
  during the transition.
- Apply only where there is a real visual relationship. Overuse = maintenance debt and
  refactor risk.

## 6. Performance

- **Read fast-changing animated values in the latest phase**, not composition: use
  `Modifier.graphicsLayer { }`, `Modifier.offset { }`, `drawBehind { }` lambdas so per-frame
  values don't trigger recomposition.
- Use `Animatable` / `updateTransition` (snapshot state read late) over manually recomposing each frame.
- Don't animate an expensive layout when a draw / `graphicsLayer` animation gives the same result.

## 7. Accessibility & system settings

- **Respect reduced-motion.** When the system animation scale is off / reduced, drop or shorten
  non-essential motion (check the animator duration scale). Critical transitions can remain,
  but tone them down.
- Motion is **additive** — never gate essential information or block interaction behind an
  animation. The UI must be fully usable with motion disabled.
- Avoid large parallax / spinning / aggressive scale that can cause vestibular discomfort.

## 8. Retrofitting existing UI

When editing existing screens, scan for and fix:
- Instant `if/when` content swaps → `AnimatedContent` / `Crossfade`.
- Instant show/hide → `AnimatedVisibility`.
- Size/position jumps → `animate*AsState` / `animateContentSize()`.
- Hard screen cut-overs → nav-graph transitions (section 4).
- Items popping into lists → `Modifier.animateItem()`.
- Tiny tap targets / weak feedback → `IconButton` / `minimumInteractiveComponentSize()` + `ripple()`.

## Forbidden

- `Modifier.clickable` directly on a sub-48dp icon without expanding the touch/ripple target.
- `rememberRipple()` (deprecated) — use `ripple()`.
- Animation durations/easings as scattered magic numbers instead of motion tokens / `motionScheme`.
- Defining navigation transitions inside screen composables instead of on the nav graph.
- Shared-element keys derived from list index/position.
- Reading per-frame animated values in composition (causing per-frame recomposition).
- Motion that hides essential info, blocks interaction, or ignores reduced-motion settings.
- Adding decorative animation the user didn't ask for — motion must serve comprehension.

## Output protocol

**If asked to SUGGEST:** produce a prioritized table — *issue → why it hurts UX → proposed
motion/affordance → API → rough effort* — in this priority order, and write no code until asked:
1. Interaction affordances (touch target, ripple, press feedback) — usability correctness.
2. Navigation transitions — the biggest "seamless" win.
3. Content/state transitions (list items, show/hide, content swaps).
4. Micro-delight (subtle scale/spring on press) — last and least.

**If asked to IMPLEMENT / EDIT:**
1. Detect Compose + Navigation versions and any existing motion tokens / `motionScheme`; reuse them.
2. Verify exact APIs via Context7 if unsure.
3. Change incrementally — one affordance or transition at a time — and preview each.
4. Centralize new durations/specs as motion tokens; never inline magic numbers.

## Pro Expense integration

This project uses **custom state-based navigation** (`ExpenseApp`, `FirstLaunchFlow`, `QuickLogFlow`)
with `AnimatedContent` at orchestrator level — not Navigation 2/3. Reuse existing helpers:

| Concern | Location |
|---|---|
| Motion tokens (`screenDurationMillis`, `fadeDurationMillis`, `sheetDurationMillis`, easing) | `shared/.../theme/Motion.kt` |
| Screen / sheet / step transitions | `shared/.../theme/NavMotion.kt` |
| Route back-stack direction | `app/.../navigation/ProNavTransitions.kt` |
| Interaction affordances (`proIconClickable`, `proClickable`, press scale) | `shared/.../ui/design/Interaction.kt` |
| Reduced-motion probe | `shared/.../theme/MotionAccessibility.kt` → `rememberProReduceMotion()` |

Design handoff tokens (`touchTarget.min` = 44dp) govern visual spacing; use
`minimumInteractiveComponentSize()` on icon actions to meet the 48dp Material accessibility floor
without changing tokenized layout dimensions.
