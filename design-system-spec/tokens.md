# Design Tokens — Pro Expense

Reference values for a Jetpack **Compose** implementation. The shipped app is built from CSS custom properties (`proto-brand.css`); this table maps each to a Compose `Color` / dimension. **1 CSS px = 1 dp.**

> **Theme note:** the app is a single **light** theme. Surfaces are pure white on warm-grey paper — there is *no* M3 tonal elevation (elevation is expressed only as drop shadow). Wire these as a custom `ProExpenseTheme` wrapping `MaterialTheme` with an overridden `ColorScheme`, `Typography`, and a custom `Dimens`/`Shapes` object.

---

## 1. Color

### Primary — Material blue
| Token | Hex | Compose | Role |
|---|---|---|---|
| `blue100` / `clayTint` | `#B3E5FC` | `Color(0xFFB3E5FC)` | Tint behind icons, active wash |
| `blue200` | `#81D4FA` | `Color(0xFF81D4FA)` | — |
| `blue300` / `claySoft` | `#4FC3F7` | `Color(0xFF4FC3F7)` | Soft accent |
| `blue500` / `clay` | `#039BE5` | `Color(0xFF039BE5)` | **Primary** — actions, active nav, FAB, links, amount accents |
| `blue700` / `clayDeep` | `#0288D1` | `Color(0xFF0288D1)` | Pressed / deep primary |

### Signal hues
| Token | Hex | Compose | Role |
|---|---|---|---|
| `green500` / `sage` | `#4CAF50` | `Color(0xFF4CAF50)` | Success, on-track, lent-back |
| `green400` | `#66BB6A` | `Color(0xFF66BB6A)` | — |
| `green300` / `sageSoft` | `#81C784` | `Color(0xFF81C784)` | Soft success |
| `sageTint` | `#C8E6C9` | `Color(0xFFC8E6C9)` | Success badge tint |
| `yellow500` / `coin` | `#FFEB3B` | `Color(0xFFFFEB3B)` | Highlight, future/events |
| `yellow300` / `coinSoft` | `#FFF176` | `Color(0xFFFFF176)` | Soft highlight |
| `red400` / `danger` | `#EF5350` | `Color(0xFFEF5350)` | Destructive, over-budget, owed |
| `red300` / `dangerSoft` | `#E57373` | `Color(0xFFE57373)` | Soft danger |
| `dangerTint` | `#FFCDD2` | `Color(0xFFFFCDD2)` | Danger badge tint |
| `tag` | `#FB8C00` | `Color(0xFFFB8C00)` | **Event @-tags** — the one warm signal |
| `tagDeep` | `#EF6C00` | `Color(0xFFEF6C00)` | Tag pressed/icon |
| `tagSoft` | `#FFB74D` | `Color(0xFFFFB74D)` | — |
| `tagTint` | `#FFE0B2` | `Color(0xFFFFE0B2)` | Tag tile tint |

### Neutrals & semantic
| Token | Hex | Compose | Role |
|---|---|---|---|
| `paper` (`gray100`) | `#F5F5F5` | `Color(0xFFF5F5F5)` | App background |
| `paper2` (`gray200`) | `#EEEEEE` | `Color(0xFFEEEEEE)` | Secondary surface |
| `card` / `white` | `#FFFFFF` | `Color(0xFFFFFFFF)` | Cards, sheets, nav, fields |
| `ink` (`darkGray`) | `#212121` | `Color(0xFF212121)` | Primary text |
| `ink2` | `#424242` | `Color(0xFF424242)` | Secondary text |
| `ink3` (`gray600`) | `#757575` | `Color(0xFF757575)` | Captions, labels |
| `muted` (`gray500`) | `#9E9E9E` | `Color(0xFF9E9E9E)` | Placeholders |
| `muted2` | `#BDBDBD` | `Color(0xFFBDBDBD)` | Disabled glyph |
| `lineStrong` (`gray300`) | `#E0E0E0` | `Color(0xFFE0E0E0)` | Outlined borders |
| `line` | `rgba(33,33,33,.10)` | `Color(0xFF212121).copy(alpha = 0.10f)` | Card & row borders |
| `line2` | `rgba(33,33,33,.06)` | `Color(0xFF212121).copy(alpha = 0.06f)` | Inner dividers |

> **M3 `ColorScheme` mapping:** `primary = blue500`, `onPrimary = #FFFDF6` (warm white used on filled buttons), `surface = white`, `background = paper`, `onSurface = ink`, `onSurfaceVariant = ink3`, `outline = lineStrong`, `outlineVariant = line`, `error = danger`, `tertiary = tag`. Success/highlight have no M3 slot — expose them through the custom `Dimens`/`ProColors` object.

---

## 2. Typography

Three UI families plus **Inter** for money figures. Load as Compose `FontFamily` from bundled assets.

| Role | Family | Size / Line height | Tracking | Weight |
|---|---|---|---|---|
| Display amount | Inter | 64 / 64 | -0.025em | Regular |
| Card amount | Inter | 40 / 1.0 | -0.02em | Regular |
| Hero greeting | Instrument Serif | 30 / 1.05 | -0.015em | Regular |
| Sheet title | Instrument Serif | 22 / 1.1 | -0.01em | Regular |
| Section / day head | Instrument Serif | 18 / 1.1 | -0.01em | Regular |
| Screen header / app-bar | Instrument Serif | 17 / 1.15 | 0 | Regular |
| Body | Manrope | 14 / 1.4 | 0 | 400 / 500 |
| Emphasis / button | Manrope | 14 / 1.4 | -0.005em | 600 |
| Caption | Manrope | 11.5 / 1.4 | 0 | 400 / 500 |
| Eyebrow / label | Geist Mono | 11 / 1.3 | 0.10–0.12em (uppercase) | 500 / 600 |
| Timestamp / figures | Geist Mono | 11.5–12 | 0.04em | 400 / 500 |

> **Family clarity:** `--sans` resolves to **Manrope** (the in-app reference doc mislabels it "Geist" — the loaded webfont is Manrope, so use **Manrope** in Compose). `--mono` is **Geist Mono**. `--serif` is **Instrument Serif** (titles only — never body, controls, or amounts). `--amount` is **Inter** (money figures only). Tracking is in `em`; convert with `letterSpacing = (-0.025).em` etc.

> **M3 `Typography` mapping:** `displayLarge`→display amount, `headlineMedium`→screen title, `titleMedium`→section head, `bodyMedium`→body, `labelMedium`→eyebrow, `bodySmall`→caption.

### 2a. Android spec — Titles (Instrument Serif) & Amounts (Inter)

> **Android rendering note (match the web/Figma metrics):** Compose adds vertical font padding by default, so title/amount text sits lower and the line box looks taller. On every title/amount `TextStyle` set `platformStyle = PlatformTextStyle(includeFontPadding = false)` and `lineHeightStyle = LineHeightStyle(alignment = Center, trim = Both)`. Instrument Serif ships **Regular + Italic only** — never request bold on titles (it synthesizes/thickens). Inter is bundled as a variable font; amount styles use **Regular (400) only**. `sp` follows the system font scale; compare at 1.0× for a pixel match.

**Titles** use **Instrument Serif**, `FontWeight.Normal`. Provide it via a bundled `res/font/instrument_serif_*.ttf`. Compose `letterSpacing` in `.em` maps 1:1 to the CSS `em` values below.

| Role | Example | `fontFamily` | `fontSize` | `lineHeight` | `letterSpacing` | `fontWeight` |
|---|---|---|---|---|---|---|
| Hero greeting | "Hi, *Maya*" | InstrumentSerif | `30.sp` | `32.sp` (1.05) | `(-0.015).em` | `Normal` |
| Sheet title | "Link to…", "Date & time" | InstrumentSerif | `22.sp` | `24.sp` (1.1) | `(-0.01).em` | `Normal` |
| Section / day head | "Today · May 25" | InstrumentSerif | `18.sp` | `20.sp` (1.1) | `(-0.01).em` | `Normal` |
| Screen header / app-bar | "New expense", "Details" | InstrumentSerif | `17.sp` | `20.sp` (1.15) | `0` (none) | `Normal` |

Color: `onSurface` `#212121` (event/persona accents may recolor to `Primary`). Titles are single-line — truncate with ellipsis, never wrap. The **hero greeting's emphasis word** ("Maya") is **Instrument Serif _italic_** in `Primary` `#039BE5`. Screen-header titles are **center-aligned** in the top bar (back/close on the left, equal spacer on the right).

**Amounts** use **Inter**, `FontWeight.Normal`. Provide via bundled `res/font/inter_variable.ttf`.
| Role | `fontFamily` | `fontSize` | `lineHeight` | `letterSpacing` | `fontWeight` |
|---|---|---|---|---|---|
| Display amount (Add screen) | Inter | `64.sp` | `64.sp` (1.0) | `(-0.025).em` | `Normal` |
| Card amount ("Spent today") | Inter | `40.sp` | `40.sp` (1.0) | `(-0.02).em` | `Normal` |
| Row amount (transaction) | Inter | `18.sp` | `18.sp` | `0` | `Normal` |
| Read-only amount (Details) | Inter | `26.sp` | `28.sp` | `(-0.01).em` | `Normal` |
| Keypad keys | Inter | `22.sp` | — | `0` | `Normal` |

An amount is **not one flat string** — compose it with `AnnotatedString`/`buildAnnotatedString` or a `Row(verticalAlignment = Alignment.Top)`:

- **`$` glyph** — smaller (≈ `0.47×` the figure: `30.sp` at 64, `20.sp` at 40), color `Primary` `#039BE5`, cap/top-aligned to the figure.
- **Whole number** — `onSurface` `#212121`, thousands grouped with commas (`NumberFormat`/`%,d`). Max 7 integer digits.
- **Decimal `.00`** — `onSurfaceVariant` `#757575`, always 2 digits, slightly de-emphasized.
- **Empty / placeholder** — whole figure in `muted2` `#BDBDBD`, `$` stays `Primary`.

```kotlin
// example TextStyle (drop into Typography or use inline)
val DisplayAmount = TextStyle(
    fontFamily = Inter,
    fontWeight = FontWeight.Normal,
    fontSize = 64.sp,
    lineHeight = 64.sp,
    letterSpacing = (-0.025).em,
)
val ScreenHeaderTitle = TextStyle(   // app-bar title — "New expense", "Details"
    fontFamily = InstrumentSerif,
    fontWeight = FontWeight.Normal,
    fontSize = 17.sp,
    lineHeight = 20.sp,
    letterSpacing = 0.sp,
)
val HeroGreeting = TextStyle(         // "Hi, Maya"
    fontFamily = InstrumentSerif,
    fontWeight = FontWeight.Normal,
    fontSize = 30.sp,
    lineHeight = 32.sp,
    letterSpacing = (-0.015).em,
)
```

See [`amount-entry.md`](components/amount-entry.md) for the input/format/validation behavior that drives the display amount.

---

## 3. Shape, spacing & elevation

### Corner radius
| Element | Radius (dp) |
|---|---|
| Chip / pill / FAB | `50%` (fully round) |
| Button — small / medium / large | 10 / 12 / 14 |
| Numeric key | 12 |
| Quick-access tile / field | 14 |
| Card | 16–18 |
| Bottom sheet (top corners) | 22 |
| Bottom-nav top corners | 8 |
| Phone body | 54 |

### Spacing
- Base rhythm: **8 dp**. Common gaps: 6 / 8 / 10 / 12 / 16 / 26.
- Card inner padding: **18 dp**. Row padding: **12 dp** vertical / 8 dp horizontal.
- Touch targets ≥ **44 dp** (FAB is 64, nav tab full-height).

### Elevation (drop shadow, *not* tonal)
| Surface | Shadow |
|---|---|
| Card | `0 1px 0 rgba(33,33,33,.03)`, `0 6px 16px rgba(33,33,33,.04)` |
| FAB / Add | `0 4px 10px rgba(3,155,229,.25)` |
| Bottom nav | `0 -6px 24px rgba(0,0,0,.10)` |
| Bottom sheet | `0 -8px 24px rgba(0,0,0,.15)` |
| Toast | `0 8px 18px rgba(0,0,0,.18)` |

> In Compose use `Modifier.shadow(elevation, shape, ambientColor, spotColor)` or draw the border + a custom shadow; keep `Card`'s `colors = CardDefaults.cardColors(containerColor = white)` and `elevation = 0.dp` so M3 doesn't tint the surface.

---

## 4. Motion
| Action | Transform | Duration | Easing |
|---|---|---|---|
| Screen forward | slide-in-right (24dp) | 280ms | `cubic-bezier(.22,.61,.36,1)` |
| Screen back | slide-in-left | 280ms | same |
| Sheet | sheet-up (100%) | 340ms | same |
| Toast | rise + auto-dismiss | 2400ms | ease |
| New row | tint→transparent pulse | 1800ms | ease |
| Validation | shake (±4dp) | 280ms | ease |
| Tap | scale 0.97 | 80ms | ease |

> Compose: define `val ProEasing = CubicBezierEasing(0.22f, 0.61f, 0.36f, 1f)`. Tap feedback = `Modifier.scale()` driven by `interactionSource.collectIsPressedAsState()` (target 0.97).
