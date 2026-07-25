# Design Tokens — Pro Expense (Blue Banking)

Reference values for a Jetpack **Compose** implementation of the adopted **Blue Banking** visual
system (source: Claude Design canvas "Pro Expense - Finance Tracker", `Hi-Fi Variant - Blue
Banking.html` / `variant-blue-app.jsx`). **1 CSS px = 1 dp.**

> **Theme note:** the app ships **light and dark** themes (user-selectable Light/Dark/System via
> More → Theme, `ThemeMode` in `core:data`). Both palettes share the same blue brand; dark swaps
> paper/card for navy-tinted charcoal surfaces with light ink and alpha-wash tints. There is *no*
> M3 tonal elevation — elevation is drop shadow only. Wire as the custom `ProExpenseTheme`
> wrapping `MaterialTheme` (`ProColors`, `ProTypography`, `ProDimens`, `ProShapes`).

---

## 1. Color

### Light palette
| Token | Hex | Compose | Role |
|---|---|---|---|
| `navy` | `#01579B` | `Color(0xFF01579B)` | Gradient anchor, deep brand accents |
| `primaryDeep` / `deep` | `#0288D1` | `Color(0xFF0288D1)` | Pressed / deep primary |
| `primary` | `#039BE5` | `Color(0xFF039BE5)` | **Primary** — actions, active nav, Add button, links, amount `$` accents |
| `primarySoft` / `soft` | `#4FC3F7` | `Color(0xFF4FC3F7)` | Soft accent, gradient highlight |
| `primaryTint` / `tint` | `#E1F5FE` | `Color(0xFFE1F5FE)` | Tint behind icons, progress tracks, active wash |
| `primaryTintSoft` / `tint2` | `#F0FAFF` | `Color(0xFFF0FAFF)` | Faintest blue wash |
| `highlight` / `gold` | `#F2B33D` | `Color(0xFFF2B33D)` | Gold signal — badges, notification dot, over-budget chip |
| `highlightSoft` | `#F7CE7A` | `Color(0xFFF7CE7A)` | Soft gold |
| `highlightDeep` / `goldDeep` | `#B97D13` | `Color(0xFFB97D13)` | Gold icon/text on tint |
| `highlightTint` / `goldTint` | `#FDF3DF` | `Color(0xFFFDF3DF)` | Gold tile tint |
| `success` / `sage` | `#4CAF50` | `Color(0xFF4CAF50)` | Success, on-track, income, "left" amounts |
| `successTint` / `sageTint` | `#E6F4E7` | `Color(0xFFE6F4E7)` | Success tile tint |
| `tag` | `#FB8C00` | `Color(0xFFFB8C00)` | **Event @-tags** — the one warm signal |
| `tagDeep` | `#EF6C00` | `Color(0xFFEF6C00)` | Tag pressed/icon |
| `tagTint` | `#FFF1E0` | `Color(0xFFFFF1E0)` | Tag tile tint |
| `danger` | `#EF5350` | `Color(0xFFEF5350)` | Destructive, over-budget, owed |
| `dangerTint` | `#FDEAEA` | `Color(0xFFFDEAEA)` | Danger badge tint |
| `paper` | `#F5F5F5` | `Color(0xFFF5F5F5)` | App background / sheet surface |
| `card` | `#FFFFFF` | `Color(0xFFFFFFFF)` | Cards, sheets, nav, fields, keys |
| `ink` | `#212121` | `Color(0xFF212121)` | Primary text; selected filter chip bg |
| `ink2` | `#424242` | `Color(0xFF424242)` | Secondary text, icon buttons |
| `ink3` | `#757575` | `Color(0xFF757575)` | Captions, labels, de-emphasized decimals |
| `muted` | `#9E9E9E` | `Color(0xFF9E9E9E)` | Placeholders, inactive nav |
| `line` | `rgba(33,33,33,.10)` | `Color(0x19212121)` | Card & row borders |
| `lineSoft` / `line2` | `rgba(33,33,33,.06)` | `Color(0x0F212121)` | Inner dividers |

### Dark palette
| Token | Hex | Compose | Role |
|---|---|---|---|
| `navy` | `#081726` | `Color(0xFF081726)` | Gradient anchor |
| `primaryDeep` | `#0D4368` | `Color(0xFF0D4368)` | Deep primary |
| `primary` | `#2BA9E8` | `Color(0xFF2BA9E8)` | Primary — brightened for dark surfaces |
| `primarySoft` | `#4FC3F7` | `Color(0xFF4FC3F7)` | Soft accent |
| `primaryTint` | `#2BA9E8` α0.15 | `Color(0x262BA9E8)` | Tint wash |
| `primaryTintSoft` | `#2BA9E8` α0.08 | `Color(0x142BA9E8)` | Faintest wash |
| `highlight` | `#F2B33D` | `Color(0xFFF2B33D)` | Gold (unchanged hue) |
| `highlightDeep` | `#F0B84A` | `Color(0xFFF0B84A)` | Gold text/icon on dark |
| `highlightTint` | `#F2B33D` α0.16 | `Color(0x29F2B33D)` | Gold wash |
| `success` | `#5CC86A` | `Color(0xFF5CC86A)` | Success |
| `successTint` | `#5CC86A` α0.16 | `Color(0x295CC86A)` | Success wash |
| `tag` | `#FF9A3D` | `Color(0xFFFF9A3D)` | @-tag |
| `tagDeep` | `#FFB15E` | `Color(0xFFFFB15E)` | Tag text on dark |
| `tagTint` | `#FF9A3D` α0.16 | `Color(0x29FF9A3D)` | Tag wash |
| `danger` | `#FF6F6B` | `Color(0xFFFF6F6B)` | Destructive |
| `dangerTint` | `#FF6F6B` α0.18 | `Color(0x2EFF6F6B)` | Danger wash |
| `paper` | `#0D1622` | `Color(0xFF0D1622)` | App background |
| `card` | `#18232F` | `Color(0xFF18232F)` | Cards, sheets, nav, fields |
| `ink` | `#F2F6FB` | `Color(0xFFF2F6FB)` | Primary text |
| `ink2` | `#C2CEDB` | `Color(0xFFC2CEDB)` | Secondary text |
| `ink3` | `#8A97A7` | `Color(0xFF8A97A7)` | Captions |
| `muted` | `#69768A` | `Color(0xFF69768A)` | Placeholders, inactive nav |
| `line` | `rgba(255,255,255,.10)` | `Color(0x19FFFFFF)` | Card borders |
| `lineSoft` | `rgba(255,255,255,.055)` | `Color(0x0EFFFFFF)` | Inner dividers |

> **Alpha-tint caution (dark):** dark tints are translucent washes — don't stack tint-on-tint, and
> never use a tint as the only separator between two surfaces; pair with `line`.

### Hero gradient (`165°`, top-left → bottom-right)
| Stop | Light | Dark |
|---|---|---|
| 0% | `#01579B` | `#062339` |
| 20% | `#015D9F` | `#072D49` |
| 40% | `#0472B3` | `#083550` |
| 58% (48% dark) | `#0288D1` | `#0A3A5C` |
| 78% (68% dark) | `#0293DC` | `#0B4568` |
| 100% | `#039BE5` | `#0D4F79` |

Exposed as `ProColors.heroGradientStops`; draw with `Brush.linearGradient`. Decorative ring:
`1.5dp` stroke, white α0.08–0.10, large circle offset past the top-right corner.

> **On-gradient ink:** title white, eyebrow white α0.7 (keep eyebrows over the darker half of the
> gradient — small text on the `#039BE5` end fails contrast), glass surfaces white α0.12–0.16 with
> white α0.18 border.

> **M3 `ColorScheme` mapping:** `primary = primary`, `onPrimary = white` (both modes — white text
> on `#2BA9E8` per canvas), `surface = card`, `background = paper`, `onSurface = ink`,
> `onSurfaceVariant = ink3`, `outline = lineStrong`, `outlineVariant = line`, `error = danger`,
> `tertiary = tag`. Gold/success have no M3 slot — exposed through `ProColors`.

---

## 2. Typography

Three UI families with **Prompt** for display titles and amounts (the Blue Banking display face —
geometric, SemiBold). Load as Compose `FontFamily` from bundled static TTFs.

| Role | Family | Size / Line height | Tracking | Weight |
|---|---|---|---|---|
| Display amount | Prompt | 58–64 / 1.0 | -0.02em | SemiBold 600 |
| Card amount | Prompt | 38–42 / 1.0 | -0.02em | SemiBold 600 |
| Hero greeting | Prompt | 24–26 / 1.1 | -0.01em | SemiBold 600 |
| Sheet title | Prompt | 22 / 1.1 | -0.01em | SemiBold 600 |
| Section / day head | Prompt | 15–15.5 / 1.15 | 0 | SemiBold 600 |
| Screen header / app-bar | Prompt | 16–17 / 1.15 | 0 | SemiBold 600 |
| Row amount | Prompt | 15 / 1.0 | 0 | SemiBold 600 |
| Nav label | Prompt | 10.5 / 1.2 | 0.01em | Medium 500 / SemiBold 600 (active) |
| Chip label | Prompt | 12.5 / 1.3 | 0 | Medium 500 / SemiBold 600 (selected) |
| Keypad key | Prompt | 21 / 1.0 | 0 | SemiBold 600 |
| Body | Manrope | 13.5–14 / 1.4 | 0 | 400 / 500 / 600 |
| Caption | Manrope | 11.5 / 1.4 | 0 | 400 / 500 |
| Button | Manrope | 14–14.5 / 1.4 | -0.005em | 600 |
| Eyebrow / label | Geist Mono | 10.5–11 / 1.3 | 0.08–0.12em (uppercase) | 500 / 600 |
| Timestamp / figures | Geist Mono | 10.5–12 | 0.04–0.08em | 400 / 500 |

> **Family clarity:** `--display` = **Prompt** (titles, amounts, nav/chip labels, keypad).
> `--sans` = **Manrope** (body, controls, buttons). `--mono` = **Geist Mono** (eyebrows,
> timestamps, figures). Prompt covers Latin + Thai only — other scripts (e.g. Burmese) fall back
> to the system face; verify locale screenshots when they matter.

> **Android rendering note:** keep `platformStyle = PlatformTextStyle(includeFontPadding = false)`
> and `lineHeightStyle = LineHeightStyle(Center, Trim.Both)` on every title/amount style. Prompt
> ships as static weights (`prompt_medium.ttf`, `prompt_semibold.ttf`) — plain `Font(resId,
> weight)` entries, not variable-font axes.

An amount is **not one flat string** — compose with `buildAnnotatedString`:
- **`$` glyph** — smaller (≈ `0.45×` the figure), color `primary` (placeholder keeps `primary`).
- **Whole number** — `ink`; income amounts `success` with `+` prefix.
- **Decimal `.00`** — `ink3`, always 2 digits.
- **Empty / placeholder** — figure in `muted`.

See [`amount-entry.md`](components/amount-entry.md) for input/format/validation behavior.

---

## 3. Shape, spacing & elevation

### Corner radius
| Element | Radius (dp) |
|---|---|
| Chip / pill / avatar / icon-button | `50%` (fully round) |
| Numeric key | 12 |
| Icon tile (34–38dp) | 10–11 |
| Quick-access tile / field / list card | 14–16 |
| Card (hero/floating) | 16–20 |
| Bottom sheet & content-sheet top corners | 26 |
| Bottom-nav top corners | 18 |
| CTA button | 14–16 |

### Key metrics
- **Content sheet** overlaps the gradient header by **-14 dp** (floating home card: **-30 dp**).
- **Center Add button:** 64 dp circle, floats **-24 dp** above the nav bar, `3 dp` ring border
  `primary` α0.8, plus icon 26 + "Add" label 10 inside. The nav container must include the
  overflow zone in its bounds (Compose clips hit-testing to parent bounds).
- Base rhythm **8 dp**; screen padding 20–24; card inner padding 14–20.
- Touch targets ≥ **44 dp** (icon buttons 36–40 visual within ≥44 target).

### Elevation (drop shadow, *not* tonal)
| Surface | Shadow (light) |
|---|---|
| Floating card (home) | `0 12px 28px rgba(1,87,155,.10)` |
| List card | `0 4px 12px rgba(1,87,155,.05)` |
| Event/hero card | `0 8px 22px rgba(1,87,155,.08)` |
| Primary CTA | `0 8px 20px rgba(3,155,229,.30)` |
| Bottom nav | `0 -6px 24px rgba(0,0,0,.10)` |

> Shadows are **blue-tinted** (`navy`-based) in light via `ProColors.cardShadowTint`; in dark use
> neutral black — blue shadows are invisible on navy surfaces, so dark relies on surface contrast
> (`card` vs `paper`) + `line` borders.

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

> Compose: `val ProEasing = CubicBezierEasing(0.22f, 0.61f, 0.36f, 1f)`. Tap feedback =
> `Modifier.scale()` driven by `interactionSource.collectIsPressedAsState()` (target 0.97).
