---
name: design-spec-to-compose
description: Implement Jetpack Compose UI from design-system-spec screen and component specs. Use when building or updating screens from design-system-spec/screens/*.md, matching spec PNGs, adding Roborazzi baselines, or wiring flows (sheets, sub-screens, edge states). Tokens-first, bottom-up, screenshot-verified.
globs: ["**/ui/**/*.kt", "**/design/**/*.kt", "design-system-spec/**"]
alwaysApply: false
---

# Design Spec → Compose UI

Implement Compose screens from **`design-system-spec/`** — the authoritative visual and
behavior reference for Pro Expense. Pair with **`compose-motion-polish`** (affordances,
transitions) and **`compose-product-auditor`** (pre-merge gate).

**Precedence:** `AGENTS.md` > `docs/finance_tracker_product.md` > this skill.

---

## When to use

- Adding or changing a screen listed in `design-system-spec/screens/README.md`
- Matching a spec PNG under `design-system-spec/screenshots/screens/`
- Implementing a component spec under `design-system-spec/components/`
- Recording or fixing Roborazzi baselines after intentional visual changes

**Do not use for:** logic-only refactors, KMP repository work, or screens with no spec entry
(ask whether to add a spec first).

---

## Spec sources (read in this order)

| Priority | Path | Purpose |
|---|---|---|
| 1 | `design-system-spec/screens/<id>-<name>.md` | States, behavior, composition table, edge cases |
| 2 | `design-system-spec/screenshots/screens/<state>.png` | Pixel truth for each state |
| 3 | `design-system-spec/components/<name>.md` | Atom/molecule rules (button, keypad, sheet, …) |
| 4 | `design-system-spec/tokens.md` | Color, type, shape, spacing, motion |
| 5 | Existing `shared/.../ui/design/*.kt` | Reuse before inventing |

Open **every PNG** for the states you implement. The markdown describes behavior; the PNG
decides layout nuance (flat list vs card wrap, spacing, which variant is selected).

---

## Workflow (gate-first)

### Step 0 — Scope the screen

From the screen markdown, list:

- **States** — every `###` under `## States` and `## Edge cases` (each maps to a preview + screenshot)
- **Interactions** — taps, sheets, validation, back behavior, toasts
- **Components** — map each row in “Component composition · M3 mapping” to an existing or new composable

Mark ✅ for states already implemented; implement only what is missing or wrong vs PNG.

### Step 1 — Reuse design-system primitives

Before writing UI, grep `shared/src/androidMain/kotlin/com/arduia/expense/ui/design/`:

| Need | Start here |
|---|---|
| Buttons | `ProButton`, `ProButtonVariant`, `ProButtonSize` |
| Text links (See all, Skip, + More) | `ProTextAction` — **never** bare `Text` + tiny padding |
| Sheets | `ProBottomSheetHost(visible = …)` + `ProBottomSheet` |
| Lists / rows | `TransactionRow`, `DayGroup(cardWrapped = …)` |
| Amount / keypad | `AmountDisplay`, `NumericKeypad`, `AmountInput` — see [Numeric keypad fidelity](#numeric-keypad-fidelity-mandatory) |
| Categories | `CategoryPicker`, `CategoryChip` |
| Detail fields | `DetailFieldCard`, `DetailNoteField`, `DetailDateTimeField`, `DetailTagField` |
| Top bar | `ProTopBar`, `ProTopBarAction` |
| Toast | `ProToastHost` |
| Motion | `ProExpenseTheme.motion`, `NavMotion.kt` (`stepTransition`, `sheetEnter`/`sheetExit`) |

**Extend tokens first** (`shared/.../ui/theme/Type.kt`, `Dimensions.kt`, `Color.kt`,
`Motion.kt`) when a spec value has no token — do not hardcode dp/colors/fonts in screens.

### Step 2 — Bottom-up, then screen

1. **Missing atom?** Add to `ui/design/` with `@Preview` + optional `Spec*Capture()` in
   `DesignSystemSpecCaptures.kt` and a row in `DesignSystemSpecScreenshotTest`.
2. **Screen content composable** — stateless `*ScreenContent` accepting a state data class +
   callbacks (no ViewModel inside content composable).
3. **Flow orchestrator** (if multi-step) — `*Flow` with `AnimatedContent` +
   `motion.stepTransition()` for forward/back; hoist state in the flow.
4. **Preview fakes** — `app/.../ui/preview/*PreviewData.kt` with one object per spec state.

### Step 3 — Previews (mandatory)

Every public UI composable file under `app/src/main` or `feature/*/androidMain`:

- Wrap in `ProExpenseTheme`
- Default artboard: `ProArtboard.PIXEL_9_PRO_WIDTH_DP` × `PIXEL_9_PRO_HEIGHT_DP`
- **One `@Preview` per distinct spec state** in the same file (name previews after spec state,
  e.g. `Add expense — zero validation`)

### Step 4 — Screenshot tests (mandatory for UI changes)

Mirror the spec state list 1:1 in `app/src/test/.../*ScreenshotTest.kt`:

```kotlin
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [33], qualifiers = "w${ProArtboard.PIXEL_9_PRO_WIDTH_DP}dp-h${ProArtboard.PIXEL_9_PRO_HEIGHT_DP}dp")
@Category(ScreenshotTests::class)
class MyScreenScreenshotTest {
    @get:Rule val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    private fun capture(content: @Composable () -> Unit) {
        composeTestRule.setContent {
            ProExpenseTheme {
                Box(Modifier.fillMaxSize().background(ProExpenseTheme.colors.paper)) {
                    content()
                }
            }
        }
        composeTestRule.onRoot().captureRoboImage()
    }

    @Test fun add_amount() = capture { /* content composable with preview state */ }
}
```

- Test method names: snake_case matching spec state ids (`add_zero`, `edge_draft`, `home_casual`)
- Pass **preview state** that already encodes the edge case (e.g. `showZeroValidation = true`)
- After intentional visual changes: `./gradlew :app:recordRoborazziDevDebug` and commit baselines
  under `app/src/test/screenshots/`
- Gate: `./gradlew :app:verifyRoborazziDevDebug` or `./gradlew verifyAll` before push

---

## Patterns learned (do / don't)

### Interaction affordances

| Do | Don't |
|---|---|
| `ProTextAction` for inline text buttons | `Text` + `padding(2.dp)` + `clickable` |
| `proClickable` / `proIconClickable` with theme shapes | Bare `clickable` on icons |
| `minimumInteractiveComponentSize()` inside `ProTextAction` | Touch targets smaller than 44dp |
| Sheet: `ProBottomSheetHost(visible = showSheet, …)` always mounted | `if (showSheet) { ProBottomSheetHost(...) }` — kills exit animation |
| Scrim tap → `onClose` on sheets | Sheet only closable via X |

### Layout fidelity

| Do | Don't |
|---|---|
| Read PNG — home recent list is **flat** (`DayGroup(cardWrapped = false)`) | Default `cardWrapped = true` everywhere |
| Match spec section structure (DEFAULT / CUSTOM, eyebrow labels) | Invent alternate information architecture |
| `AmountInput` for keypad rules (7 whole, 2 fraction, comma display) | `(text.toDouble() * 100).toLong()` or ad-hoc parsers |
| **`NumericKeypad` on transparent container** — keys sit on `paper`; only each key cell is white | `ProCard` / `surface` wrapper around the whole keypad grid |
| Verify keypad against **screen** PNG (`add-amount.png`) when embedded in a flow | Rely on isolated component PNG alone (`keypad.png`) |
| `stringResource(R.string.…)` for all user-visible copy | Hardcoded English in composables |

### Navigation & motion

| Do | Don't |
|---|---|
| `AnimatedContent` + `motion.stepTransition(from, to)` for Amount ↔ Details | Instant `when(step)` without transition |
| `motion.sheetEnter` / `sheetExit` (340ms) for bottom sheets | Instant sheet pop-in/out |
| Back from Amount with empty value → silent dismiss (per spec) | Confirm dialog when nothing entered |

### Architecture

| Do | Don't |
|---|---|
| `*ScreenContent(state, callbacks)` + optional `*Flow` | Business rules inside `@Composable` bodies |
| Preview data in `app/.../ui/preview/` | `@Preview` that requires production DI |
| Feature modules stay free of other features; compose in `app` | Cross-feature imports in KMP modules |

---

## File placement

```
design-system-spec/          ← read-only spec (markdown + PNG)
shared/.../ui/design/        ← reusable atoms/molecules + Spec*Capture()
shared/.../ui/theme/         ← ProExpenseTheme tokens
app/.../ui/<feature>/        ← *ScreenContent, *Flow, route wiring
app/.../ui/preview/          ← HandoffPreviewData-style fake states
app/src/test/.../            ← *ScreenshotTest + baselines
```

---

## Numeric keypad fidelity (mandatory)

When a screen embeds `NumericKeypad` (Add Expense amount, Shared Costs zero-total entry, etc.):

1. **Open the screen PNG** — e.g. `design-system-spec/screenshots/screens/add-amount.png`.
   The keypad area uses the app **`paper`** background; there is no white card shelf behind the
   grid.
2. **Container is transparent** — `NumericKeypad` is a padded `Column` only (16dp padding per
   `amount-entry.md`). Do **not** wrap it in `ProCard`, `Surface`, or `.background(surface)`.
3. **Keys stay white** — each key cell keeps `surface` + `line` border (radius 12dp); Save/Next
   sit below the grid unchanged.
4. **Screenshot gate** — if keypad container styling changes, re-record
   `AddExpenseScreenshotTest.add_amount` (and any screen test that shows the keypad) before push.

**Anti-pattern caught in review:** `ProCard { NumericKeypad(...) }` adds a bordered white panel
that does not appear on `add-amount.png`.

---

## Per-screen checklist

Before marking a screen done:

- [ ] Every spec state has a `@Preview`
- [ ] Every spec state has a Roborazzi test + committed baseline PNG
- [ ] `./gradlew verifyAll` green in session
- [ ] No new hardcoded colors/dp/fonts where theme tokens exist
- [ ] Text actions use `ProTextAction`
- [ ] Sheets use animated `ProBottomSheetHost(visible = …)`
- [ ] **Numeric keypad (if present): container transparent on `paper`; no `ProCard` wrapper; verified against screen PNG (`add-amount.png` or equivalent)**
- [ ] Strings in `app/src/main/res/values/strings.xml` (and shared if design component)
- [ ] Run **`compose-product-auditor`** — fix Blockers/High before push

---

## Component gallery cross-check

For atoms/molecules, optionally align with `DesignSystemSpecScreenshotTest` in `:shared`:

- Add `SpecMyComponentCapture()` to `DesignSystemSpecCaptures.kt`
- Add `@Test fun my_component()` to `DesignSystemSpecScreenshotTest`
- Compare against `design-system-spec/screenshots/<component>.png`

Screens use **full-device** captures in `:app`; components use **SpecCaptureHost** width in
`:shared`.

---

## Verification commands

```bash
./gradlew :app:compileDevDebugKotlin          # fast compile
./gradlew :app:verifyRoborazziDevDebug          # screenshot gate (UI work)
./gradlew verifyAll                             # preferred pre-push gate
./gradlew :app:recordRoborazziDevDebug          # after intentional visual changes
```

---

## Example: Add Expense (reference implementation)

| Spec state | Preview state | Screenshot test |
|---|---|---|
| `add-amount.png` | `previewExpenseAmountTyped` | `add_amount` |
| `add-zero.png` | `showZeroValidation = true` | `add_zero` |
| `add-details.png` | `previewExpenseDetails` | `add_details` |
| `edge-draft.png` | `showDraftPrompt = true` | `edge_draft` |
| `edge-note.png` | note length 200 | `edge_note` |
| `edge-tag.png` | `showTagSheet = true` | `edge_tag` |

Files: `AddExpenseAmountScreen.kt`, `AddExpenseDetailsScreen.kt`, `QuickLogFlow.kt`,
`ExpenseEntryPreviewData.kt`, `AddExpenseScreenshotTest.kt`.

**Keypad note:** `NumericKeypad.kt` uses a transparent container; `add_amount` screenshot is the
regression guard for embedded keypad layout on `paper`.

---

## Related skills

| Order | Skill | Role |
|---|---|---|
| **Build** | **design-spec-to-compose** (this file) | Spec → tokens → components → screens → screenshots |
| Polish | `compose-motion-polish` | Ripple, press scale, nav transitions (if present) |
| Audit | `compose-product-auditor` | Pre-merge product/UX/resilience review |
