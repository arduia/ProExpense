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
| Amount / keypad | `AmountDisplay`, `NumericKeypad`, `AmountInput` |
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

### Step 5 — Screen fidelity verification (mandatory before push)

**Gate:** Every implemented state has been compared side-by-side to its **screen** PNG from
`design-system-spec/screenshots/screens/` and mismatches are fixed or explicitly accepted.

**Else:** Do not push. Open each PNG referenced in the screen markdown (`## States`, `## Edge
cases`) and walk the checklist in [Screen fidelity verification](#screen-fidelity-verification-mandatory).

Roborazzi baselines prove regression protection; they **do not replace** opening the spec PNGs
during implementation.

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
| **Verify every state against its screen PNG** — full layout in context on `paper` | Trust component gallery PNGs or markdown alone |
| Read PNG — home recent list is **flat** (`DayGroup(cardWrapped = false)`) | Default `cardWrapped = true` everywhere |
| Match spec section structure (DEFAULT / CUSTOM, eyebrow labels) | Invent alternate information architecture |
| Match container vs key-level surfaces (what is `paper` vs `surface` / card) | Wrap sub-regions in `ProCard` when the screen PNG shows none |
| `AmountInput` for keypad rules (7 whole, 2 fraction, comma display) | `(text.toDouble() * 100).toLong()` or ad-hoc parsers |
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

## Screen fidelity verification (mandatory)

**Screen PNGs are the ship gate.** Component specs (`design-system-spec/components/`,
`design-system-spec/screenshots/<component>.png`) describe atoms in isolation. **Embedded layout
always follows the screen PNG** — backgrounds, wrappers, spacing, and what sits on `paper` vs
`surface`.

Before marking a screen done, for **each** state listed in the screen markdown:

1. **Open the screen PNG** — path is in the markdown, usually
   `design-system-spec/screenshots/screens/<state>.png` (e.g. `add-amount.png`,
   `shared-input.png`, `edge-shared-zero.png`).
2. **Compare full layout** — not just individual widgets:
   - App background (`paper`) visible where the PNG shows it
   - Card / sheet / bordered regions only where the PNG shows them
   - Section order, eyebrows, empty vs filled fields, disabled actions
   - Edge states show only the UI the PNG shows (e.g. minimal zero-validation layout)
3. **Resolve conflicts** — when a component gallery PNG disagrees with the screen PNG, **the
   screen PNG wins** for in-flow placement and container styling.
4. **Encode in tests** — Roborazzi test + baseline per state; re-record after intentional fixes.

**Examples of screen-PNG-only details (easy to miss without opening the PNG):**

| Screen PNG | Detail the screen shows (not always in component spec) |
|---|---|
| `add-amount.png` | Keypad grid on `paper`; no outer card shelf — only key cells are white |
| `home-*.png` | Recent list flat — no card wrap around day groups |
| `edge-shared-zero.png` | Minimal layout — amount, validation, disabled save only |

**Anti-pattern:** Implementing from `components/amount-entry.md` or `screenshots/keypad.png` alone
and adding a `ProCard` wrapper that never appears on `add-amount.png`.

---

## Per-screen checklist

Before marking a screen done:

- [ ] **Every spec state PNG opened and compared** — layout, backgrounds, wrappers, section order,
  and edge-case minimalism match `design-system-spec/screenshots/screens/` (screen wins over
  component gallery when they differ)
- [ ] Every spec state has a `@Preview`
- [ ] Every spec state has a Roborazzi test + committed baseline PNG
- [ ] `./gradlew verifyAll` green in session
- [ ] No new hardcoded colors/dp/fonts where theme tokens exist
- [ ] Text actions use `ProTextAction`
- [ ] Sheets use animated `ProBottomSheetHost(visible = …)`
- [ ] Strings in `app/src/main/res/values/strings.xml` (and shared if design component)
- [ ] Run **`compose-product-auditor`** — fix Blockers/High before push

---

## Component gallery cross-check

For atoms/molecules, optionally align with `DesignSystemSpecScreenshotTest` in `:shared`:

- Add `SpecMyComponentCapture()` to `DesignSystemSpecCaptures.kt`
- Add `@Test fun my_component()` to `DesignSystemSpecScreenshotTest`
- Compare against `design-system-spec/screenshots/<component>.png`

**Screen PNGs override component PNGs** for anything about placement, wrappers, and backgrounds
in a real flow. Component gallery proves the atom; screen Roborazzi proves the ship target.

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

---

## Related skills

| Order | Skill | Role |
|---|---|---|
| **Build** | **design-spec-to-compose** (this file) | Spec → tokens → components → screens → screenshots |
| Polish | `compose-motion-polish` | Ripple, press scale, nav transitions (if present) |
| Audit | `compose-product-auditor` | Pre-merge product/UX/resilience review |
