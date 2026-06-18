# Handoff: Pro Expense — Finance Tracker

Developer handoff for implementing **Pro Expense** in **Jetpack Compose** with **Roborazzi** screenshot tests. Pro Expense is an offline-first, account-free personal finance notebook for Android.

## About the design files
The visual sources here are **design references**, not production code. The HTML/JSX that produced them is a prototype showing the intended look, behavior, and motion. Your task is to **recreate these designs natively in Jetpack Compose** using the token/component/screen specs below — not to port HTML. Where this bundle and the reference images disagree, the **reference images win** (they are captures of the shipped Hi-Fi build).

## Fidelity
**High-fidelity.** Final colors, typography, spacing, motion, and all UI states. Recreate pixel-accurately against the 414 × 868 dp reference images using `ProExpenseTheme`.

## What's in this bundle

| File / folder | What it is | Use it for |
|---|---|---|
| `design-tokens.json` | W3C Design Tokens — colors, typography, dimensions, elevation, motion | Generate `ProExpenseTheme` (colors / typography / dimens / shapes / motion). **Start here.** |
| `screens-manifest.yaml` | Every screen: composable name, package, all UI states, primitives, sample data | One `@Preview` / Roborazzi golden per state. **Build order.** |
| `components.yaml` | Component catalog — variants, states, tokens consumed, screens used | Build shared primitives in `ui/design/` before screens |
| `DESIGN-SYSTEM.md` | Human narrative spec: Color, Type, Icons, Categories, Buttons, Surfaces, Lists, Nav, Motion, **Compose mapping** | Reference + the §10 Compose mapping for theme structure |
| `screen-flows.md` | Navigation diagram across all flows | Wire up NavHost / nav graph |
| `reference-images/` | **58 PNGs @ 414 × 868**, sRGB. `{flow}-{screen}-{state}.png` (edge cases `edge-*.png`) | Roborazzi visual targets, one per state |
| `icons/` | 34 stroke SVGs, 24×24 viewBox, 1.6dp weight, rounded caps. `icon-{name}.svg` | Convert to `ImageVector` for `ProIcon` |
| `fonts/` + `fonts.css` + `FONTS.md` | Manrope, Geist Mono, Instrument Serif (woff2) + type-scale & usage rules | Bundle as Compose `FontFamily`s |
| `interactive-prototype-quick-log.html` | Self-contained, offline clickable Quick-Log prototype | Feel the real interactions & motion that static PNGs can't show |

## Implementation order
1. **Theme** — parse `design-tokens.json` → `ProExpenseTheme` (`ProColors`, `ProTypography`, `ProDimens`, shapes, motion). See `DESIGN-SYSTEM.md` §10.
2. **Primitives** — build `components.yaml` items in `ui/design/` (Buttons, Icons, Fields, Category, TopBar, BottomSheet, Toast) + `HomeBottomNav`.
3. **Screens** — implement per `screens-manifest.yaml`, flow by flow; cover **every** listed state (default / empty / filled / error / disabled), not just the happy path.
4. **Tests** — add a Roborazzi golden per state; target = matching `reference-images/` file.

## Non-negotiable rules (from the brief)
- **Manrope for UI and headings.** **Geist Mono for amounts and tabular figures.** **Instrument Serif for occasional flourishes only** (e.g. italic name on Home). Reference PNGs may still show serif amounts from the Hi-Fi export; the Android build uses mono for figures.
- **Signal colors carry meaning, never decoration:** green = on-track/success, yellow = future/events, red = over/owed/destructive, orange = event @-tags.
- **Color tokens are semantic** — reference `color.primary`, `color.surface`, etc., never raw hex per screen.
- **No one-off styles per screen** — every screen composes catalog primitives.
- Amounts use serif and max display `999,999,999.99`. Disabled = 40% opacity. Press = 0.97 scale.
- Bottom nav is a **floating detached pill bar**, white surface, blue active tab, raised circular Add.

## Target codebase structure
```
ProExpenseTheme/            theme tokens — colors, typography, dimensions, shapes, motion
ui/design/                  shared primitives — Buttons, Icons, Fields, TopBar, BottomSheet, Toast, Category
app/ui/                     app screens — Home, Onboarding, More, HomeBottomNav, Splash
feature/logging/ui/         AddAmount, AddDetails, NumericKeypad, AmountDisplay
feature/journal/ui/         Journal, JournalDetail, TransactionRow, DayHeader
feature/currency/ui/        ProfileCurrency, CurrencySetting
feature/auth/ui/            PinSetup, PinEntry, PinKeypad
feature/events/ui/          EventList, EventCreate, EventDetail
feature/debt/ui/            DebtTracker, DebtAdd, DebtDetail
feature/shared/ui/          SharedCosts, SharedSummary, SharedHistory
feature/reports/ui/         Reports
feature/categories/ui/      CategoryList
feature/data/ui/            DataExport, ClearData
```

## Notes & decisions
- The HTML prototype renders an iOS-style device shell; **reference images are rendered as bare 414 × 868 dp Android artboards** (no device bezel, Android status bar) to match Roborazzi capture conventions.
- Currency is multi-currency by design (home currency chosen at setup); amounts shown are USD samples.
- The app is account-free and offline-first — no auth/network screens beyond local PIN + biometric.

---
*Pro Expense · developer handoff · mirrors the shipped Hi-Fi build.*
