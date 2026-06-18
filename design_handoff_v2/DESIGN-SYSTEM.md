# Pro Expense — Design System

> Account-free, offline-first expense tracker. The language pairs a **Material-blue** core with a **warm-paper** canvas and an **editorial serif** voice — built to make money feel calm and legible.
>
> This document mirrors the shipped Hi-Fi build. Source of truth: `proto-brand.css` (tokens), `proto-ui.jsx` (primitives), `proto-phone.jsx` (chrome). Use it as the reference for any new screen.

---

## 0. Foundations — principles

| Principle | What it means |
|---|---|
| **Calm, financial clarity** | Cool blues and quiet neutrals keep the focus on numbers. Color is reserved for meaning — never decoration. |
| **Serif for money, sans for UI** | Instrument Serif gives amounts and titles a considered, editorial weight; Geist keeps the interface neutral and legible. |
| **Signal over noise** | Green, yellow, red and orange each carry one job — on-track, future, over/owed, and event tags. Nothing competes. |
| **One stroke language** | 24px stroke icons at a consistent 1.6px weight (2px active), rounded caps, paired with tinted category badges. |

---

## 1. Color

A cool Material-blue core, three signal hues (green / yellow / red) for financial state, and a single warm orange reserved for event tags. Everything else is a calibrated neutral ramp.

### Primary · Material blue
> Actions, active nav, links, the floating Add button, amount accents.

| Token | Hex | Role |
|---|---|---|
| `--blue-100` | `#b3e5fc` | Tint |
| `--blue-200` | `#81d4fa` | 200 |
| `--blue-300` | `#4fc3f7` | Soft |
| `--blue-500` | `#039be5` | **Primary** |
| `--blue-700` | `#0288d1` | Deep |

### Success · Green
> On-track budgets, money lent back, positive confirmations.

| Token | Hex | Role |
|---|---|---|
| `--green-300` | `#81c784` | Soft |
| `--green-400` | `#66bb6a` | 400 |
| `--green-500` | `#4caf50` | **Success** |

### Highlight · Yellow
> Future/event highlights, coin accents.

| Token | Hex | Role |
|---|---|---|
| `--yellow-300` | `#fff176` | Soft |
| `--yellow-500` | `#ffeb3b` | **Highlight** |

### Destructive · Red
> Over-budget, money owed, delete & clear-data actions.

| Token | Hex | Role |
|---|---|---|
| `--red-300` | `#e57373` | Soft |
| `--red-400` | `#ef5350` | **Danger** |

### Tag accent · Orange
> Event @-tags on transactions — the one warm signal in a cool palette.

| Token | Hex | Role |
|---|---|---|
| `--tag-tint` | `#ffe0b2` | Tint |
| `--tag-soft` | `#ffb74d` | Soft |
| `--tag` | `#fb8c00` | **Tag** |
| `--tag-deep` | `#ef6c00` | Deep |

### Neutrals

| Token | Hex | Alias |
|---|---|---|
| `--gray-100` | `#f5f5f5` | paper |
| `--gray-200` | `#eeeeee` | paper-2 |
| `--gray-300` | `#e0e0e0` | line-strong |
| `--gray-500` | `#9e9e9e` | muted |
| `--gray-600` | `#757575` | ink-3 |
| `--dark-gray` | `#212121` | ink |
| `--dark` | `#000000` | — |

### Semantic tokens
> The rest of the codebase references these, not the raw scales.

| Role | Token | Value | Used for |
|---|---|---|---|
| Surface | `--card` | `#ffffff` | Cards, sheets, nav |
| App bg | `--paper` | `#f5f5f5` | Phone screen base |
| Text | `--ink` | `#212121` | Primary text |
| Text 2 | `--ink-2` | `#424242` | Secondary text |
| Text 3 | `--ink-3` | `#757575` | Captions, labels |
| Muted | `--muted` | `#9e9e9e` | Placeholders |
| Line | `--line` | `rgba(33,33,33,.10)` | Card & row borders |
| Line 2 | `--line-2` | `rgba(33,33,33,.06)` | Inner dividers |

**Aliased semantic names** (legacy color-name → role): `--clay` = primary blue, `--clay-deep` = deep blue, `--clay-soft` = soft blue, `--clay-tint` = blue tint; `--sage` = success green; `--coin` = highlight yellow; `--indigo` = secondary deep blue; `--danger` = red; `--nav-bg` = white, `--nav-text` = dark gray.

---

## 2. Typography

Three families with clear jobs.

| Family | Token | Stack | Job |
|---|---|---|---|
| **Instrument Serif** | `--serif` | `"SF Pro Display", -apple-system, "Plus Jakarta Sans", "Manrope", system-ui` | Display only — screen titles, day headers, large amounts. Never body or controls. Weights: Regular 400, Italic. |
| **Geist** (Manrope) | `--sans` | `"Manrope", "Roboto", -apple-system, system-ui, sans-serif` | All body, buttons, list rows. 500–600 for emphasis, 400 for secondary. Weights: 300–800. |
| **Geist Mono** | `--mono` | `"Geist Mono", "Roboto Mono", ui-monospace, monospace` | Uppercase eyebrows, tab labels, timestamps & tabular figures. Letter-spacing 0.08–0.12em. Weights: 400–600. |

### Type scale

| Role | Family | Size | Line height | Letter-spacing | Example |
|---|---|---|---|---|---|
| Display amount | serif | 64px | 1.0 | -0.025em | `$1,240` |
| Screen title | serif | 32px | 1.0 | -0.015em | `Journal` |
| Section head | serif | 18px | 1.1 | -0.01em | `Today · May 25` |
| Body | sans | 14px | 1.4 | 0 | `Lunch with M.` |
| Caption | sans | 11.5px | 1.4 | 0 | `Food · 12:30 PM` |
| Eyebrow / label | mono | 11px | 1.3 | 0.1em (uppercase) | `AMOUNT · USD` |

**Google Fonts import:**
```
Manrope:wght@300;400;500;600;700;800
Plus+Jakarta+Sans:wght@400;500;600;700;800
Geist+Mono:wght@400;500;600
Instrument+Serif:ital@0;1
```

---

## 3. Iconography

One stroke-based set, drawn on a **24×24 grid at 1.6px weight** (2px when active). Rounded caps and joins throughout. Active nav and primary actions bump the stroke to feel heavier.

| Group | Icons |
|---|---|
| **Navigation & chrome** | `home` `budget` `journal` `more` `plus` `minus` `back` `close` `chevron-down` `chevron-right` `search` `bell` `check` |
| **Detail & meta** | `sparkle` `at` `calendar` `clock` `note` `user` `eye` `fingerprint` |
| **Feature shortcuts** | `feat-reports` `feat-debt` `feat-split` `feat-events` |
| **Category** | `cat-food` `cat-transport` `cat-shopping` `cat-bills` `cat-health` `cat-entertainment` `cat-coffee` `cat-pet` |

---

## 4. Category system

Every expense maps to a category, each with a dedicated icon, accent and matching tint. **Badges** (circular, tinted background + accent icon) appear in lists; **chips** drive selection (idle = outlined, selected = filled accent). Defaults ship with the app; users add custom ones.

| Category | Accent | Tint | Icon | Default? |
|---|---|---|---|---|
| Food | `#039be5` | `#e1f5fe` | `cat-food` | default |
| Transport | `#0288d1` | `#b3e5fc` | `cat-transport` | default |
| Shopping | `#ef5350` | `#ffcdd2` | `cat-shopping` | default |
| Bills | `#757575` | `#eeeeee` | `cat-bills` | default |
| Health | `#4caf50` | `#c8e6c9` | `cat-health` | default |
| Entertainment | `#0277bd` | `#81d4fa` | `cat-entertainment` | default |
| Coffee runs | `#9e9e9e` | `#e0e0e0` | `cat-coffee` | **custom** |
| Pet care | `#66bb6a` | `#dcedc8` | `cat-pet` | **custom** |

- **CatBadge** — circular, `border-radius: 50%`, `background: tint`, `color: accent`.
- **CatChip** — pill; *idle*: transparent bg, `--ink-2` text, `--line-strong` border; *selected*: accent bg, `#fffdf6` text, accent border.

---

## 5. Buttons

Six variants over three sizes. Primary blue drives the main action on any screen; sage confirms; dark is the high-contrast alternative; secondary and ghost recede. Font: Geist **600**, letter-spacing -0.005em. Every button scales to **0.97 on press**.

### Variants

| Variant | Background | Text | Border | Use |
|---|---|---|---|---|
| `primary` | `--clay` (#039be5) | `#fffdf6` | 1.4px `--clay` | Main action — save, continue, get started |
| `primary-deep` | `--clay-deep` (#0288d1) | `#fffdf6` | 1.4px `--clay-deep` | Primary on tinted/active surfaces |
| `sage` | `--sage` (#4caf50) | `#fffdf6` | 1.4px `--sage` | Positive confirm — mark settled, on-track |
| `dark` | `--ink` (#212121) | `--paper-warm` | 1.4px `--ink` | High-contrast alt on paper |
| `secondary` | transparent | `--ink` | 1.4px `--line-strong` | Outlined secondary action |
| `ghost` | transparent | `--ink` | 1.4px transparent | Tertiary / cancel — no chrome |

### Sizes

| Size | Padding | Font | Radius |
|---|---|---|---|
| `sm` | 8px 14px | 12px | 10px |
| `md` | 12px 18px | 14px | 12px |
| `lg` | 16px 22px | 15px | 14px |

**State:** disabled → `opacity: 0.4`, `cursor: not-allowed`.

---

## 6. Surfaces & elevation

White cards float on warm paper with a soft two-layer shadow and a hairline border. Radius scales with element size.

- **Card** — `background: --card`, `radius 18`, `padding 18`, `border 1px --line`, shadow `0 1px 0 rgba(33,33,33,.04), 0 6px 14px rgba(33,33,33,.04)`.

### Radius scale

| Element | Radius |
|---|---|
| Chip / pill | 99px (fully round) |
| Quick-access tile | 14px |
| Card | 18px (docs use 16) |
| Sheet | 22px |
| Phone body | 54px |

**Quick-access tiles** — 36px tinted icon square over label, in a `--card` tile with 14px radius. Examples: Reports (blue tint / blue-700), Debts (green tint / green-500), Split (orange tint / tag-deep), Events (yellow tint / #f9a825).

---

## 7. Data & list patterns

Transactions are the core unit: a tinted **category badge**, a two-line **label / meta** block, and a **serif amount** on the right. Day groups carry a serif header and a mono running total. Event tags glow orange.

- **Transaction row** — CatBadge (38px) · note (14px sans, 500) over meta (11.5px muted) · amount (18px serif). Optional `@tag` in `--tag` orange with `at` icon.
- **Day header** — serif 18px title + mono 12px muted total.
- **Search field** — `--card` bg, 14px radius, 1px `--line`, `search` icon (16px muted) + placeholder.
- **Filter chips** — pill; active = `--ink` fill / `--paper-warm` text / 600; idle = transparent / `--ink-2` / `--line-strong` border.
- **Numeric keypad** — amount-entry primitive, auto-opens on Add. 3-column grid, **serif keys** (1·9, decimal, ⌫ backspace), 12px radius `--card` keys on `--line`. Action row inside the pad: **Save** (secondary outline, quick-log) + **Next** (primary `--clay`, to details), both disabled below $0 (opacity 0.55). Max display 999,999,999.99.
- **Validation** — large serif amount goes `--muted-2`; helper text in `--clay` (e.g. "Amount must be greater than $0"), paired with a shake.

---

## 8. Navigation & feedback

A modern iOS **floating tab bar** — a detached, fully-rounded glass bar inset from the screen edges with a soft drop shadow, SF-style tabs, and the Add action raised into a circular button. Bottom sheets slide up over a scrim for create/edit flows; toasts confirm and auto-dismiss.

- **Bottom nav** — `--nav-bg` white surface, `--nav-text` dark-gray inactive, blue active.
- **Bottom sheet** — `--card`, top radius 22px, shadow `0 -8px 24px rgba(0,0,0,.15)`, max-height 78%, over a `rgba(43,31,23,.42)` scrim. Drag handle: 36×4px, `rgba(43,31,23,.18)`.
- **Toast** — `--ink` pill on `--paper-warm` text, 99px radius, bottom-center, shadow `0 8px 18px rgba(0,0,0,.18)`, auto-dismiss. Check icon in success green.

---

## 9. Motion

Movement is brisk and eased, never bouncy. Screens slide on a shared curve, sheets rise from the bottom, and feedback (toasts, row pulses, shake) is short and self-clearing.

| Role | Keyframe | Duration | Easing |
|---|---|---|---|
| Screen · forward | `slide-in-right` | 280ms | `cubic-bezier(.22,.61,.36,1)` |
| Screen · back | `slide-in-left` | 280ms | `cubic-bezier(.22,.61,.36,1)` |
| Sheet | `sheet-up` | 340ms | `cubic-bezier(.22,.61,.36,1)` |
| Toast | `toast-up` | 2400ms | ease (auto-dismiss) |
| Fade up | `fade-up` | 200ms | ease |
| New row pulse | `pulse-hl` | 1800ms | ease (tint → transparent) |
| Validation | `shake` | 280ms | ease |
| Tap | `scale 0.97` | 80ms | ease |

---

## 10. Compose mapping

How the tokens and primitives land in `ProExpenseTheme` and the `ui/design/` + `feature/*/ui/` packages. Machine-readable values live in `design-tokens.json`; this is the human view.

### Theme structure
| Layer | Compose home | Source token group |
|---|---|---|
| **Colors** | `ProExpenseTheme.colors` (custom `ProColors` data class via `staticCompositionLocalOf`) | `color.*` |
| **Typography** | `ProExpenseTheme.typography` (`ProTypography`) | `typography.*`, `fontFamily.*` |
| **Dimensions** | `ProExpenseTheme.dimens` (`ProDimens`) | `dimension.space.*`, button/icon/card/sheet |
| **Shapes** | `ProExpenseTheme.shapes` | `dimension.card.radius`, `chip.radius`, `tile.radius`, `sheet.radiusTop` |
| **Motion** | `ProExpenseTheme.motion` | `motion.duration.*`, `motion.easing.standard`, `pressedScale` |

> Material 3 `ColorScheme` doesn't carry the signal hues (success / highlight / tag) or category tints — keep a **custom `ProColors`** alongside (or layered over) `MaterialTheme` rather than forcing everything into M3 slots. Map `primary → color.primary`, `surface → color.surface`, `background → color.paper`, `onSurface → color.onSurface`, `error → color.danger`.

### Units
- `dimension.*` → `.dp` (layout) — e.g. `card.radius` → `RoundedCornerShape(18.dp)`.
- `typography.*.fontSize` / `lineHeight` → `.sp`. `letterSpacing` em → `.em` (`TextStyle(letterSpacing = (-0.025).em)`).
- `motion.duration.*` → `tween(durationMillis = 280, easing = CubicBezierEasing(0.22f, 0.61f, 0.36f, 1f))`.
- `motion.pressedScale` → `Modifier.scale()` driven by `interactionSource.collectIsPressedAsState()` (or `0.97f` on press).

### Fonts
Bundle the `fonts/*.woff2` as Compose `FontFamily`s (convert to `.ttf`/`.otf` for Android `res/font/` if woff2 isn't accepted by your toolchain). `fontFamily.serif = Instrument Serif`, `fontFamily.sans = Manrope`, `fontFamily.mono = Geist Mono`. **Serif is display-only — amounts, titles, day headers.** Never set body, buttons, or controls in serif.

### Primitive → composable
| Doc primitive | Composable (`components.yaml`) | Package |
|---|---|---|
| Buttons | `ProButton` (variant + size enums) | `ui/design/Buttons.kt` |
| Category badge | `LogCategoryBadge` | `ui/design/Category.kt` |
| Category chip | `CategoryChip` | `ui/design/Category.kt` |
| Transaction row | `TransactionRow` | `feature/journal/ui/` |
| Search / filter | `SearchField`, `FilterChip` | `ui/design/Fields.kt` |
| Bottom sheet | `BottomSheet` | `ui/design/BottomSheet.kt` |
| Toast | `Toast` | `ui/design/Toast.kt` |
| Numeric keypad | `NumericKeypad`, `PinKeypad` | `feature/logging/ui/`, `feature/auth/ui/` |
| Bottom nav | `HomeBottomNav` (+ raised Add FAB) | `app/ui/` |
| Top bar | `ProTopBar` | `ui/design/TopBar.kt` |
| Icons | `ProIcon` (from `icons/*.svg` → `ImageVector`) | `ui/design/Icons.kt` |

### Screenshot tests (Roborazzi)
Reference images in `reference-images/` are full 414 × 868 dp captures named `{flow}-{screen}-{state}.png` (edge cases: `edge-*.png`). Use them as the visual target per `@Preview` / Roborazzi golden — one preview per state listed in `screens-manifest.yaml`. Render previews at 414 × 868 dp with `ProExpenseTheme` applied.

---

*Pro Expense · Design System · mirrors the shipped Hi-Fi build.*
