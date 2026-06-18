# Pro Expense — Claude Design Guide

> Copy-paste prompts for [Claude Design](https://claude.ai/design) to produce handoff bundles that
> map cleanly to **Jetpack Compose**, `ProExpenseTheme`, and **Roborazzi** screenshot verification.

**How to use:** Run prompts **in order** — (1) design system → (2) per screen/flow → (3) export
handoff. Replace bracketed `[...]` placeholders in Prompt 2 before pasting.

**Related files in this repo:**

| File | Role |
|---|---|
| `project/DESIGN-SYSTEM.md` | Human-readable design spec (mirror in exports) |
| `project/proto-brand.css` | CSS token source |
| `project/FONTS.md` | Typography families and usage rules |
| `project/uploads/finance_tracker_screen_flows.md` | Screen index and navigation flows |
| `../shared/.../ui/theme/` | Kotlin theme tokens (`ProExpenseTheme`) |
| `../AGENTS.md` | Compose UI implementation gates (`@Preview`, Roborazzi) |

---

## Quick start

1. Paste **Prompt 1** into Claude Design → confirm design system + primitives.
2. Paste **Prompt 2** once per flow (Quick Log, First Launch, Home, …).
3. Paste **Prompt 3** before exporting the handoff bundle.
4. Use **Prompt 4** for small single-screen tweaks.

---

## Prompt 1 — Design system foundation (run once)

```
You are designing **Pro Expense** (Finance Tracker) — an offline-first, account-free personal finance notebook for Android (Jetpack Compose).

## Product principles (non-negotiable)
- Simplicity first — no cluttered dashboards
- Log an expense in under 5 seconds
- Calm, financial clarity — color carries meaning, not decoration
- Personal & private — feels like a notebook, not a bank app
- Global-ready — multi-currency is core
- No bank integrations, no cloud sync, no user accounts (PIN is local only)
- Max expense amount: 999,999,999.99

## Visual identity (keep consistent across all screens)
- **Material blue** primary (#039BE5 actions, #0288D1 deep)
- **Warm paper** canvas (#F5F5F5 app bg, #FFFFFF cards)
- **Serif for money & titles** — Instrument Serif (display amounts, screen titles, day headers)
- **Sans for all UI** — Manrope (body, buttons, list rows, 500–600 emphasis)
- **Mono for metadata** — Geist Mono (uppercase eyebrows, timestamps, tab labels; letter-spacing 0.08–0.12em)
- **Signal colors only when meaningful:**
  - Green #4CAF50 = on-track / success
  - Yellow #FFEB3B = future / events highlight
  - Red #EF5350 = over-budget / owe / destructive
  - Orange #FB8C00 = event @-tags only
- **Icons:** 24×24 stroke grid, 1.6px weight (2px when active), rounded caps
- **Motion:** brisk, not bouncy — 280ms screen transitions, 0.97 scale on press, 340ms bottom sheets

## Platform & frame
- Target: **Android phone, Jetpack Compose**
- Artboard: **414 × 868 dp** (implementation & screenshot reference size)
- Annotate all specs in **dp** (layout) and **sp** (text) — not px-only
- Bottom nav: floating detached pill bar, white surface, blue active tab, circular elevated Add button

## Design system deliverables (build as reusable components, not one-offs)
Create and document these primitives before any screens:

1. **Color tokens** — semantic: primary, onPrimary, surface, onSurface, paper, ink, ink2, ink3, muted, line, success, danger, tag, category tints
2. **Typography scale** — displayAmount (64sp serif), screenTitle (32sp serif), sectionHead (18sp serif), body (14sp sans), caption (11.5sp sans), eyebrow (11sp mono uppercase)
3. **Buttons** — primary filled, secondary outlined, ghost; heights 48–56dp; disabled opacity 0.4; 0.97 press scale
4. **Category badge** — circular tinted bg + accent icon (38dp in lists)
5. **Category chip** — pill; idle outlined, selected filled
6. **Transaction row** — badge + 2-line label/meta + serif amount right
7. **Search field, filter chips, bottom sheet, toast**
8. **Numeric keypad** — for quick amount entry

## Rules for coding handoff
- Every screen must list which **primitives** it uses (no new button styles per screen)
- Every screen needs **all UI states**: default, empty, filled, error, disabled (where applicable)
- Amounts always use **serif**; controls always use **sans**
- Empty states: friendly illustrated style, one clear CTA — never "Error" or "Nothing found" tone

Confirm you understand, then build the design system page first. Do not design MVP screens until the token table and component library are complete.
```

---

## Prompt 2 — Screen / flow design (repeat per flow)

Replace all `[...]` placeholders, then paste.

```
Continue **Pro Expense** design system. Design this flow for Android Compose handoff.

## Flow
[Flow name, e.g. "Flow 01 — Quick Log" / "Flow 04 — First Launch" / "Screen 03 — Home"]

## User story
[e.g. "Maya logs a $12.50 lunch expense in under 5 seconds"]

## Screens in this flow (design every screen)
[e.g. Splash → Onboarding (3 slides) → Profile name → Home currency picker → Home]

## Navigation
[e.g. Splash auto-advances; onboarding swipeable; profile wizard with back; Home has bottom nav + Add opens sheet]

## Required UI states (design EACH as a separate frame)
For every screen in this flow, show:
- [ ] Default / populated
- [ ] Empty (friendly illustration + single CTA)
- [ ] Validation error (e.g. zero amount, shake + helper text in primary blue)
- [ ] Loading (if applicable)
- [ ] Disabled / inactive (if applicable)

## Content samples (use realistic copy)
- Profile name: **Maya**
- Home currency: **USD**
- Sample expense: **Lunch · Food · $12.50 · 12:30 PM**
- Date header: **Wed · May 25**
- Empty home message: **"No expenses yet. Start by logging your first one!"**

## Layout constraints
- Artboard: **414 × 868 dp**
- Reuse design system primitives only — list which components each screen uses
- Annotate every frame with:
  - Spacing (dp) between major blocks
  - Typography token per text element (displayAmount / sectionHead / body / caption / eyebrow)
  - Color token per fill/stroke/text
  - Corner radius (chip 99dp, card 18dp, sheet top 22dp)
  - Icon name from the icon set (home, plus, cat-food, close, etc.)

## Compose mapping (add a note per screen)
| Screen | Suggested composable name | Parent flow |
|--------|--------------------------|-------------|
| [e.g. Add Amount] | AddAmountScreen | QuickLogFlow |

## Out of scope for this flow
[e.g. no bank linking, no cloud sync, no account signup]

Design all frames at Hi-Fi fidelity. Keep the visual language identical to the Pro Expense design system (Material blue, warm paper, serif amounts).
```

---

## Prompt 3 — Export / handoff bundle (run before exporting)

```
Prepare the **Pro Expense** design for developer handoff to Jetpack Compose. The coding agent will implement pixel-accurate Android UI with Roborazzi screenshot tests at 414×868 dp.

## Export requirements — include ALL of these in the handoff bundle

### 1. Design tokens file (`design-tokens.json`)
Export machine-readable tokens (W3C Design Tokens format). Use Kotlin-aligned names:

**Colors:** color.primary, color.onPrimary, color.surface, color.onSurface, color.paper, color.success, color.danger, color.tag, color.muted, color.line, color.category.* (tint + accent per category)

**Typography:** typography.displayAmount, typography.screenTitle, typography.sectionHead, typography.body, typography.caption, typography.eyebrow — each with fontFamily, fontSize (sp), lineHeight, letterSpacing, fontWeight

**Dimensions:** dimension.space.* (2–44dp scale), dimension.button.filled.height, dimension.icon.nav (24dp), dimension.card.radius (18dp), dimension.sheet.radiusTop (22dp)

**Motion:** motion.duration.screen (280ms), motion.duration.sheet (340ms), motion.pressedScale (0.97)

Use explicit units: **dp** for layout, **sp** for text, **ms** for duration, **hex** for colors.

### 2. Component catalog (`components.yaml` or markdown table)
For each primitive, document:
- Component name (align with: ProFilledButton, LogCategoryBadge, HomeBottomNav, NumericKeypad, etc.)
- Variants and states
- Which tokens it consumes
- Which screens use it

### 3. Screen manifest (`screens-manifest.yaml`)
Per screen:
- screen_id, flow_id, composable_name
- viewport: 414 × 868 dp
- list of UI states (empty, default, error, …)
- primitives used
- sample data for @Preview composables

### 4. Reference images
Export **PNG @ 414×868** for every distinct UI state:
- Full-screen captures, sRGB, minimal compression
- File naming: `{flow}-{screen}-{state}.png` (e.g. `flow-01-add-amount-zero-error.png`)

### 5. Icons
Export all icons as **SVG** on 24×24 viewBox, stroke-based, rounded caps.
Naming: `icon-{name}.svg` (home, plus, cat-food, close, chevron-right, etc.)

### 6. Fonts
Bundle: Manrope, Geist Mono, Instrument Serif (woff2) + `FONTS.md` with type scale and usage rules (serif = display/money only).

### 7. Human spec (`DESIGN-SYSTEM.md`)
Narrative spec with sections: Color, Typography, Icons, Categories, Buttons, Surfaces, Lists, Navigation, Motion, Compose mapping.

### 8. Screen flows doc
Navigation for MVP: Splash → Onboarding → Home → Quick Log | Journal | Budget | More | PIN setup

## Do NOT
- Rely on screenshots alone without token JSON
- Use one-off styles per screen
- Use px without dp/sp equivalents in the token file
- Skip empty/error states

## Target codebase structure (for naming alignment)
- Theme tokens → ProExpenseTheme (colors, typography, dimensions, shapes, motion)
- Primitives → shared/ui/design/ (Buttons, Icons, Fields, TopBar)
- App screens → app/ui/ (Home, Onboarding)
- Feature screens → feature/logging/ui/, feature/currency/ui/

Generate the token JSON and screen manifest first, then export the visual bundle.
```

---

## Prompt 4 — Quick one-liner (single screen iteration)

```
Design [screen name] for Pro Expense (414×868dp Android Compose). States: default, empty, error. Use existing tokens only (Material blue #039BE5, Manrope UI, Instrument Serif amounts, 24dp stroke icons). Annotate spacing in dp, typography token per text, and list primitives used. Sample user: Maya, USD, lunch $12.50.
```

---

## MVP screen list

Paste into Prompt 2 when designing the full app.

```
MVP screens to design (in priority order):
01 Splash
02 Onboarding (3 slides)
03 Home (recent activity, month spend, bottom nav)
04 Quick Log — Add Amount (keypad + category chips)
05 Quick Log — Add Details (note, save)
06 Profile setup — Name
07 Profile setup — Home currency (+ currency sheet)
08 Journal list (day groups)
09 Journal detail / day view
10 More / Settings hub
11 PIN setup
12 PIN entry
13 Record history filters (optional sheet)
14 Import / Export (simple)
15 Shared costs — split bill (MVP)
```

---

## Pre-filled flow examples

Copy a block below into Prompt 2 (replace the Flow / Screens / Navigation sections).

### Flow 01 — Quick Log

```
## Flow
Flow 01 — Quick Log

## User story
Maya taps Add on Home and logs a $12.50 lunch in under 5 seconds.

## Screens in this flow
Home (Add tapped) → Add Amount (keypad + categories) → Add Details (optional note) → back to Home with new row highlighted

## Navigation
Add opens full-screen flow; close cancels; Next on amount → details; Save on details or Quick Save on amount dismisses to Home

## Compose mapping
| Screen | Composable | Parent |
|--------|------------|--------|
| Add Amount | AddAmountScreen | QuickLogFlow |
| Add Details | AddDetailsScreen | QuickLogFlow |
```

### Flow 04 — First Launch

```
## Flow
Flow 04 — First Launch

## User story
New user opens the app for the first time, sees onboarding, sets name and home currency, lands on Home.

## Screens in this flow
Splash → Onboarding (3 slides) → Profile name → Home currency (+ currency search sheet) → Home (empty)

## Navigation
Splash ~1.5s → onboarding swipe + Get started → profile wizard (back allowed) → Home

## Compose mapping
| Screen | Composable | Parent |
|--------|------------|--------|
| Splash | SplashScreen | FirstLaunchFlow |
| Onboarding | OnboardingScreen | FirstLaunchFlow |
| Profile name | ProfileNameScreen | FirstLaunchFlow |
| Home currency | ProfileCurrencyScreen | FirstLaunchFlow |
| Currency sheet | ProfileCurrencySheet | ProfileCurrencyScreen |
```

### Screen 03 — Home

```
## Flow
Screen 03 — Home

## User story
Maya reviews today's spend and recent activity; taps Add to log or See all for history.

## Screens in this flow
Home (empty) · Home (with records) · Home (new record pulse highlight)

## Navigation
Bottom nav: Home | Budget | Journal | More; center Add FAB; See all → history (future)

## Compose mapping
| Screen | Composable | Parent |
|--------|------------|--------|
| Home content | HomeScreen | HomeShell |
| Chrome | HomeShell | ExpenseApp |
```

---

## Handoff bundle checklist

Before exporting from Claude Design, confirm:

- [ ] `design-tokens.json` with dp/sp/ms/hex units
- [ ] `components.yaml` or component table
- [ ] `screens-manifest.yaml` with all UI states
- [ ] PNG reference @ 414×868 per state
- [ ] SVG icons (24×24 stroke)
- [ ] Font files + `FONTS.md`
- [ ] `DESIGN-SYSTEM.md` updated
- [ ] Screen flow navigation doc
- [ ] HTML prototypes (optional exploration layer)

---

## Implementation notes (for coding agents)

After handoff, the Android agent should:

1. Map `design-tokens.json` → `shared/.../ui/theme/` (`Color.kt`, `Type.kt`, `Dimensions.kt`, …)
2. Map primitives → `shared/.../ui/design/`
3. Implement stateless content composables + `@Preview` per state (414×868 dp)
4. Add Roborazzi tests → `app/src/test/.../*ScreenshotTest.kt`
5. Run `./gradlew :app:verifyRoborazziDevDebug` before push

---

*Pro Expense · Claude Design Guide · for Compose-accurate handoffs.*
