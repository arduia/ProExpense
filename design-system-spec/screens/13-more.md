# 13 · More / Settings

**Flow:** Hub  
**Purpose:** Combined hub for secondary features + app configuration. All data is local, no account.

---

## States

### More hub

![More hub](../screenshots/screens/more-hub.png)

### Currency

![Currency](../screenshots/screens/more-currency.png)

### Data export

![Data export](../screenshots/screens/more-export.png)

### Clear data

![Clear data](../screenshots/screens/more-clear.png)

### Google Drive Sync (opt-in)

> No hi-fi mockup PNG yet — built directly from existing hub components below (settings row +
> `ProBottomSheetHost`/dialog patterns already in this spec) rather than a new mockup. Flag for a
> follow-up design pass; do not treat this note as satisfying the mandatory-PNG gate for any
> *future* material change to this screen.

- New settings row (same "Settings list rows" component as Currency/Theme/Language): icon +
  label "Google Drive Sync" + value text "Not connected" / "Connected as {email}".
- Connect screen: disclosure copy (sync is optional; remote copy relies on Drive's own encryption
  + private app-folder scope, not additional app-level encryption) + a primary "Connect" button
  that launches the Google OAuth consent flow (system UI, not in-app).
- Connected state: shows connected account email, last-synced-at (or "Never" pre-Phase 2), a
  "Sync now" action (stubbed until Phase 2), and a "Disconnect" action.
- Disconnect: confirm dialog (same `AlertDialog` pattern as Clear data) — title "Disconnect Google
  Drive?", body clarifies local and remote data are both left untouched.

## Behavior & interactions

- Feature links: Debt Tracker, Shared Costs, Reports, Category List.
- Settings: PIN auth, Biometric (greyed until PIN on), Currency, Monthly budget (drives Budget-Planner header; resets on the 1st), Default category, Language, Theme (Light/Dark/System), Data export, Clear data, Google Drive Sync (opt-in, off by default), App version.
- Google Drive Sync: tapping the row opens Connect (if not connected) or the connected status
  screen (if connected). Connecting never happens implicitly — it always requires the explicit
  OAuth consent flow.
- Currency: single default applied to all entries; selector for common currencies. Tapping a currency only **stages** the pick (Save button disabled until it differs from the current selection); tapping Save opens a confirm dialog — title “Change home currency?”, body “New entries will use {currency name} ({code}) going forward. Existing records keep their original currency.”, actions Save / Cancel. The change only applies on confirm.
- Data export: separate CSVs (expenses / events / debts / shared_costs) zipped into one file — nothing uploaded.
- Clear data: selective — user picks what to wipe; each option requires a confirmation dialog; irreversible.
- Biometric tap while PIN off → “Please enable PIN first to use biometric authentication.”

## Component composition · M3 mapping

| Component | Role | Compose / M3 |
|---|---|---|
| Profile header row | Name + “all data local” | `custom Row` |
| Settings list rows | Links + value/toggle/chevron | `ListItem / custom Row` |
| Switch | PIN / biometric toggles | `Switch` |
| Selectable list (Currency) | Single-select w/ check, staged pick + Save | `custom Surface` |
| Export / Clear actions | ZIP export, destructive clear | `Button (filled / error)` |
| Confirm dialog | Clear-data guard · currency-change guard | `AlertDialog` |

## Tokens applied

**Type**

- Screen title — Inter 30sp
- section eyebrow — Geist Mono 11sp upper
- row label — Manrope 14sp, value — mono/ink3

**Color**

- Clear-selected = danger #EF5350
- Export = primary clay
- toggle on = clay

**Shape · spacing**

- row pad 12dp
- field/list radius 14dp
- icon 18–20dp

---

## Foundations (shared reference)

Applies to every screen. Full source: [`../tokens.md`](../tokens.md) · component specs in [`../components/`](../components/).

### Type — three families, one job each

| Role | Family | Size / Line | Tracking | Weight |
|---|---|---|---|---|
| Display amount | Inter | 64 / 64 | -0.025em | Regular |
| Card amount | Inter | 40 / 40 | -0.02em | Regular |
| Hero greeting | Inter | 30 / 32 | -0.015em | Regular |
| Sheet title | Inter | 22 / 24 | -0.01em | Regular |
| Section / day head | Inter | 18 / 20 | -0.01em | Regular |
| Screen header / app-bar | Inter | 17 / 20 | 0 | Regular |
| Body / emphasis / button | Manrope | 14 / 1.4 | 0 / -0.005em | 400–600 |
| Caption | Manrope | 11.5 / 1.4 | 0 | 400–500 |
| Eyebrow / label | Geist Mono | 11 / 1.3 | 0.10–0.12em (upper) | 500–600 |
| Timestamp / figures | Geist Mono | 11.5–12 | 0.04em | 400–500 |

> Amounts are **always Inter**. Compose: `PlatformTextStyle(includeFontPadding = false)` + `LineHeightStyle(Center, Both)` on every title/amount; Inter amount styles use **Regular only** via bundled variable font. The `$` glyph is ≈0.47× the figure in `clay`; decimals (`.00`) sit in `ink3`.

### Color

**Primary — Material blue**

| Token | Hex | Role |
|---|---|---|
| `blue100` / clayTint | `#B3E5FC` | Tint behind icons, active wash |
| `blue300` / claySoft | `#4FC3F7` | Soft accent |
| `blue500` / **clay** | `#039BE5` | **Primary** — actions, active nav, FAB, links, amount accents |
| `blue700` / clayDeep | `#0288D1` | Pressed / deep primary |

**Signal hues**

| Token | Hex | Role |
|---|---|---|
| `green500` / sage | `#4CAF50` | Success, on-track, **I Lent** |
| `sageTint` | `#C8E6C9` | Success badge tint |
| `red400` / danger | `#EF5350` | Destructive, over-budget, **I Owe** |
| `dangerTint` | `#FFCDD2` | Danger badge tint |
| `tag` | `#FB8C00` | **Event @-tags** — the one warm signal |
| `tagTint` | `#FFE0B2` | Tag tile tint |

**Budget progress system**

| Range | State | Color |
|---|---|---|
| 0–100% | On track | soft blue `#B3D4E8` |
| 101–110% | Over budget | warm yellow `#F5E6A3` |
| 110%+ | Significantly over | soft red `#E07070` |

**Neutrals & semantic**

| Token | Hex | Role |
|---|---|---|
| `paper` | `#F5F5F5` | App background |
| `card` / white | `#FFFFFF` | Cards, sheets, nav, fields |
| `ink` | `#212121` | Primary text |
| `ink2` | `#424242` | Secondary text |
| `ink3` | `#757575` | Captions, labels |
| `muted` | `#9E9E9E` | Placeholders |
| `muted2` | `#BDBDBD` | Disabled glyph / empty amount |
| `lineStrong` | `#E0E0E0` | Outlined borders |
| `line` | `rgba(33,33,33,.10)` | Card & row borders |
| `line2` | `rgba(33,33,33,.06)` | Inner dividers |

### M3 ColorScheme & Typography mapping

- `primary` = blue500 `#039BE5` · **`onPrimary` = warm white `#FFFDF6`** (not pure white) · `surface` = white · `background` = paper · `onSurface` = ink · `onSurfaceVariant` = ink3 · `outline` = lineStrong · `outlineVariant` = line · `error` = danger · `tertiary` = tag.
- Success / highlight / budget-progress colors have **no M3 slot** — expose via a custom `ProColors` object.
- `displayLarge` → display amount · `headlineMedium` → screen title · `titleMedium` → section head · `bodyMedium` → body · `labelMedium` → eyebrow · `bodySmall` → caption.

### Shape · spacing · elevation · motion

- **Radius:** chip / pill / FAB = full · button sm/md/lg = 10 / 12 / 14 · numeric key = 12 · field / tile = 14 · card = 16–18 · sheet top = 22 · bottom-nav top = 8.
- **Spacing:** 8dp base rhythm (6 / 8 / 10 / 12 / 16 / 26) · card inner pad 18dp · row 12dp v / 8dp h · touch targets ≥ 44dp (FAB 64).
- **Elevation = drop-shadow only (no M3 tonal tint):** card `0 1 0 / 0 6 16 rgba(33,33,33,.03–.04)` · FAB `0 8 20 rgba(3,155,229,.28)` · nav `0 -6 24 rgba(0,0,0,.10)` · sheet `0 -8 24 rgba(0,0,0,.15)`.
- **Motion** (curve `cubic-bezier(.22,.61,.36,1)`): screen forward/back 280ms · sheet-up 340ms · toast 2400ms · new-row pulse 1800ms · validation shake ±4dp 280ms · tap scale 0.97 / 80ms.

### Conventions

- Single **light** theme. Surfaces pure white on warm-grey paper; **1 px = 1 dp**; tracking values in `em`.
- Wire as a custom `ProExpenseTheme` wrapping `MaterialTheme` with overridden `ColorScheme`, `Typography`, and custom `Dimens` / `Shapes`.
- `--sans` = **Manrope** · `--mono` = **Geist Mono** · `--display` = **Inter** (titles + amounts — never body or controls).

---

_Pro Expense · Android screen spec · light theme · 1 px = 1 dp · captured at 414 × 868 dp from the shipped Hi-Fi build._
