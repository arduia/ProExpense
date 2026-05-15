# Design Implementation Reference

This document captures the existing XML/View-based design implementation as the authoritative reference for the Jetpack Compose migration. Every design token, component pattern, and layout structure described here should be faithfully reproduced in Compose equivalents.

---

## 1. Theme System

### Base Theme

`Base.Theme.ProExpense` inherits from `Theme.MaterialComponents.DayNight.NoActionBar` and is defined in `res/values/base_theme.xml`. It wires all text appearances, shape appearances, and widget styles globally.

The active theme is `Theme.ProExpense` (`res/values/theme.xml` for light, `res/values-night/theme.xml` for dark). Theme switching is applied at runtime in `MainActivity.attachBaseContext` by reading the stored theme mode and calling `delegate.localNightMode`.

### Custom Theme Attributes (`res/values/attr.xml`)

These attributes are used throughout layouts via `?attr/…` — they must all have Compose equivalents in `MaterialTheme.colorScheme` or a custom `ProExpenseColors` object:

| Attribute | Type | Purpose |
|---|---|---|
| `colorPositive` | color | Income / success states |
| `colorOnPositive` | color | Content on positive backgrounds |
| `colorNegative` | color | Expense / error states |
| `colorOnNegative` | color | Content on negative backgrounds |
| `colorWarning` | color | Warning badges (Beta label, force upgrade) |
| `colorOnWarning` | color | Content on warning backgrounds |
| `dividerColor` | color | List and section dividers |
| `iconColor` | color | Default tint for all icon buttons |
| `textAppearanceMediumTitle` | reference | Section titles (18sp, sans-serif-medium, 75% alpha) |
| `textAppearanceCurrencyLarge` | reference | Primary currency values (Headline6 + Poppins) |
| `textAppearanceCurrencySmall` | reference | Secondary currency values (Body1 + Poppins, 80% alpha) |
| `proExpenseNavigationViewStyle` | reference | NavigationView style |
| `iconButtonStyle` | reference | Borderless icon button style |

### Color Palette (`res/values/colors.xml`)

These are the raw color constants. Theme attributes reference these — never use raw colors in layouts directly.

| Name | Value |
|---|---|
| `blue_light_100` | `#b3e5fc` |
| `blue_light_200` | `#81d4fa` |
| `blue_light_300` | `#4fc3f7` |
| `blue_light_500` | `#039be5` ← **primary (light mode)** |
| `blue_light_700` | `#0288d1` |
| `yellow_300` | `#fff176` |
| `yellow_500` | `#ffeb3b` |
| `red_300` | `#e57373` |
| `red_400` | `#ef5350` |
| `green_300` | `#81c784` |
| `green_400` | `#66bb6a` |
| `green_500` | `#4caf50` |
| `gray_100` | `#f5f5f5` |
| `gray_200` | `#eeeeee` |
| `gray_300` | `#e0e0e0` |
| `dark_gray` | `#212121` |
| `dark` | `#000000` |
| `white` | `#ffffff` |

### Light vs Dark Token Mapping

| Token | Light | Dark |
|---|---|---|
| `colorPrimary` | `blue_light_500` | `blue_light_200` |
| `colorOnPrimary` | `white` | `dark_gray` |
| `colorSurface` | `white` | `dark_gray` |
| `colorOnSurface` | `dark` | `gray_100` |
| `backgroundColor` | `gray_200` | `dark_gray` |
| `colorPositive` | `green_500` | `green_300` |
| `colorNegative` | `red_400` | `red_300` |
| `colorWarning` | `yellow_500` | `yellow_300` |
| `iconColor` | `dark_gray` | `gray_300` |
| `dividerColor` | `gray_300` | `dark_gray` |
| `rippleColor` | `darker_gray` | `gray_200` |
| Status bar | transparent, light icons | `blue_light_500`, dark icons |

The `Splash` theme overrides `windowBackground` and `statusBarColor` to `blue_light_500` in both modes.

---

## 2. Typography

### Font Families

Two custom font families are defined:

- **Poppins** (`res/font/poppins.xml`) — weights: Light, Medium. Used for currency values and button labels.
- **Pyidaungsu** (`res/font/pyidaungsu.xml`) — weights: Regular, Bold. Used for subtitle text. Myanmar locale overrides `font_config.xml` to apply this as the default body font.

### Text Appearances (`res/values/font_config.xml`, `base_theme.xml`)

All text appearances inherit from MaterialComponents equivalents. Alpha values are baked in.

| Style name | Base | Font | Size | Alpha | Usage |
|---|---|---|---|---|---|
| `TextAppearance.ProExpense.Headline6` | `Headline6` | default | — | 1.0 | Section and dialog titles |
| `TextAppearance.ProExpense.Body1` | `Body1` | default | — | 0.70 | General body text |
| `TextAppearance.ProExpense.Body2` | `Body2` | default | — | 0.70 | Secondary body text |
| `TextAppearance.ProExpense.Subtitle1` | `Subtitle1` | Pyidaungsu | — | 0.70 | Date ranges, subtitles (all-caps in some places) |
| `TextAppearance.ProExpense.Button` | `Button` | Poppins | 16sp | 1.0 | Primary buttons |
| `TextAppearance.ProExpense.MediumTitle` | `Headline5` | sans-serif-medium | 18sp | 0.75 | Card section headers ("Totals", "Recent", etc.) |
| `TextAppearance.ProExpense.CurrencyLarge` | `Headline6` | Poppins | — | 1.0 | Income/outcome primary values |
| `TextAppearance.ProExpense.CurrencySmall` | `Body1` | Poppins | — | 0.80 | Expense list amounts |

---

## 3. Spacing & Dimensions (`res/values/dimen.xml`)

A 4dp grid system is used throughout:

| Token | Value | Usage |
|---|---|---|
| `grid_1` | 4dp | Micro spacing, hairline padding |
| `grid_2` | 8dp | List item vertical padding, card margins |
| `grid_3` | 16dp | Standard horizontal margin, primary padding |
| `grid_4` | 32dp | Large section spacing |
| `size_radius` | 2dp | Default card corner radius |
| `height_button` | 65dp | Primary action button height |
| `standard_icon_size` | 35dp | Toolbar and action icons |
| `small_icon_size` | 20dp | Navigation drawer icons |
| `size_nav_icon` | 20dp | NavigationView item icon size |
| `width_layout_min` | 450dp | Max width for content on tablets |
| `with_layout_min_margin` | 482dp | Max constrained width including margins |

---

## 4. Shape System

| Style | Corner | Used on |
|---|---|---|
| `ShapeAppearance.ProExpense.MediumComponent` | `rounded`, 4dp | Default `MaterialCardView` global override |
| `ShapeAppearanceOverlay.ProExpense.Circular` | `rounded`, 50%p | Category icon badges (circular cards) |
| `Widget.ProExpense.CalendarDayCardView` | 0dp | Calendar day cells |

**Global card override**: `Widget.ProExpense.CardView` sets `cornerSize = 0dp` and `cardBackgroundColor = ?colorSurface` for all `MaterialCardView` instances by default. Individual cards override as needed (e.g. category chips use `size_radius = 2dp`).

---

## 5. Component Styles

### Buttons
`Widget.ProExpense.Button` — `height_button` (65dp), `colorOnPrimary` text, `TextAppearance.ProExpense.Button` (Poppins 16sp). Applied globally via `materialButtonStyle`.

### Icon Buttons
`Widget.ProExpense.IconButton` — borderless background (`selectableItemBackgroundBorderless`), `?iconColor` tint. Applied globally via `iconButtonStyle`. Used for toolbar actions, drawer close, delete/edit in dialogs.

### Text Input
Global override uses `Widget.MaterialComponents.TextInputLayout.OutlinedBox` via `textInputStyle`. Used in `ExpenseEntry` for Name, Amount, Note fields.

### Navigation Drawer
`Widget.ProExpense.NavigationView.Overlay` — `?colorSurface` background, `navigation_menu_text_color` (color state list: primary when checked, onSurface when unchecked), `small_icon_size` icons, `grid_4` horizontal item padding.

---

## 6. Navigation & Chrome

### App Shell (`activity_main.xml`)

```
DrawerLayout (id: dl_main)
├── CoordinatorLayout (id: cl_main)
│   ├── NavHostFragment (id: fc_main) ← full screen, nav graph: main_nav
│   └── FloatingActionButton (id: fb_main_add)
│       └── ic_add icon, ?colorPrimary bg, ?colorOnPrimary tint
│       └── bottom-end gravity, 32dp bottom margin
└── NavigationView (id: nv_main)
    └── header: layout_header (logo + app name + Beta badge)
    └── menu: menu_home
```

**DrawerLayout behavior (MainActivity)**:
- Drawer is only unlocked for **top-level destinations**: Home, Expense Logs, Statistics, Backup, Settings, Feedback, About.
- FAB (`fb_main_add`) is shown **only on Home**, hidden on all other destinations.
- Navigation item selection closes the drawer first, then navigates (via `DrawerListener.onDrawerClosed`).
- Navigation within drawer uses `NavOptions` with `setLaunchSingleTop(true)`. Home uses `popBackStack` to clear the back stack.

### Drawer Header (`layout_header.xml`)
- Close (`ic_left`) icon button — top-end corner
- App logo (60dp, `?colorPrimary` tint)
- App name (`Headline6`)
- Beta badge: `bg_small_rounded_warning` background (rounded rectangle, `?colorWarning`), `?colorOnWarning` text, uppercase

### Navigation Graph (`main_nav.xml`)

Start destination: `splashFragment`

| Destination ID | Fragment | Label |
|---|---|---|
| `splashFragment` | `SplashFragment` | — |
| `dest_home` | `HomeFragment` | Home |
| `dest_expense_logs` | `ExpenseFragment` | Expense Logs |
| `dest_expense_entry` | `ExpenseEntryFragment` | Expense Entry — arg: `expense_id: Int = -100` (−100 = new) |
| `dest_statistics` | `StatisticsFragment` | Statistics |
| `dest_settings` | `SettingsFragment` | Settings |
| `dest_backup` | `BackupFragment` | Backup |
| `dest_feedback` | `FeedbackFragment` | Feedback |
| `dest_about` | `AboutFragment` | About |
| `dest_web` | `WebFragment` | — args: `url: String`, `title: String` |
| `dest_language` | `OnBoardingConfigFragment` | Language (onboarding) |

---

## 7. Screen Layouts

All main screens follow the same shell pattern:
```
CoordinatorLayout (bg: ?backgroundColor)
└── AppBarLayout (bg: ?colorSurface, liftOnScroll=true)
│   └── MaterialToolbar / Toolbar (bg: ?colorSurface)
│       └── navigationIcon: ic_menu (opens drawer)
└── [content] (app:layout_behavior: AppBarLayout$ScrollingViewBehavior)
```

### Splash (`fragment_splash.xml`)
- `FrameLayout`, bg: `blue_light_500`, theme: `Theme.Splash`
- Centered `AppCompatImageView` (100dp×100dp): `ic_launcher_foreground`, white tint

### Home (`fragment_home.xml`)
CoordinatorLayout with AppBarLayout + `NestedScrollView` containing a vertical `LinearLayout` of three `include` cards:
1. **`layout_expense_in_out`** — Income/Outcome card (always visible)
2. **`layout_expense_graph`** — Weekly spend graph card (always visible)
3. **`layout_recent_lists`** — Recent expenses card (visibility toggled by ViewModel)

#### Income/Outcome Card (`layout_expense_in_out.xml`)
`MaterialCardView` → `RelativeLayout`:
- `tv_expense_in_out_title`: `?textAppearanceMediumTitle` → "Totals"
- `tv_date_range`: `?textAppearanceSubtitle1`, all-caps
- `tv_income` label + `tv_income_value` (`?textAppearanceCurrencyLarge`) + `tv_income_symobol`
- `tv_outcome` label + `tv_outcome_value` (`?textAppearanceCurrencyLarge`) + `tv_outcome_symbol`

#### Expense Graph Card (`layout_expense_graph.xml`)
`MaterialCardView` → `RelativeLayout`:
- `tv_title`: `?textAppearanceMediumTitle`, `?colorOnSurface`
- `tv_date_range`: `?textAppearanceSubtitle1`, all-caps
- `SpendGraph` (id: `expense_graph`): 160dp height, `app:graph_color="?colorPrimary"`, `app:day_color="?colorPrimary"`

#### Recent Lists Card (`layout_recent_lists.xml`)
`MaterialCardView` → `LinearLayout`:
- `tv_recent_lists`: `?textAppearanceMediumTitle`
- `RecyclerView` (id: `rv_recent_lists`): `item_expense_log` items
- `MaterialButton` text button (id: `btn_more_logs`): "More" → navigates to Expense Logs

### Expense Entry (`fragment_expense_entry.xml`)
CoordinatorLayout with AppBarLayout (back arrow: `ic_back`, menu: `menu_entry`) + `RelativeLayout`:
- **Upper scroll area** (`NestedScrollView`, bg: `?colorSurface`):
  - `edl_name`: `TextInputLayout` (outlined) + `TextInputEditText`, maxLength=20
  - `edl_amount`: `TextInputLayout` + `TextInputEditText`, `inputType=numberDecimal`, maxLength=10, `?textAppearanceCurrencySmall`
  - `rv_category`: horizontal `RecyclerView`, items: `item_category`
  - `edl_description` / `edt_note`: `TextInputLayout` 100dp height, maxLength=100
- **Bottom action bar** (`RelativeLayout`, `?colorSurface` bg, 4dp elevation):
  - `switch_repeat` + `tv_repeat` label: "Repeat Entry" toggle row
  - `btn_save`: `Widget.ProExpense.Button`, full-width minus grid_3 margins

### Expense Logs (`fragment_expense_logs.xml`)
CoordinatorLayout with AppBarLayout (drawer icon, menu: `menu_expense_log`) + `FrameLayout`:
- `rv_expense`: full-screen `RecyclerView`, 80dp bottom padding, items: `item_expense_log`
- `layout_no_data`: `layout_no_expense_logs` include, initially invisible

### Statistics (`fragment_statistic.xml`)
CoordinatorLayout with AppBarLayout + `NestedScrollView` → `LinearLayout`:
- `MaterialCardView` containing:
  - `tv_category_statistics`: `?textAppearanceMediumTitle`
  - `rv_category_statistics`: vertical `RecyclerView`, items: `item_category_statistic`
  - `tv_no_data`: shown when empty

### Settings (`fragment_settings.xml`)
CoordinatorLayout with AppBarLayout + `NestedScrollView` → `LinearLayout` (`?colorSurface` bg, `grid_3` horizontal padding):
Three `FrameLayout` rows with `?selectableItemBackground` + `View` dividers between them:
1. **Language row** — `tv_language` + `imv_language` flag (60dp×25dp, start: `flag_myanmar`)
2. **Currency row** — `tv_currency` + `tv_currency_value` (Headline6, 60dp wide)
3. **Theme row** — `tv_choose_theme` + `ic_theme` icon (35dp)

### Backup (`fragment_backup.xml`)
CoordinatorLayout with AppBarLayout + `ConstraintLayout`:
- **Export card** (`cv_export`, 130dp height, half width): `ic_export` icon (30dp) + "Export" label
- **Import card** (`cv_import`, 130dp height, half width): `ic_import` icon (30dp) + "Import" label
- `tv_backup_logs`: `?textAppearanceMediumTitle`
- `rv_backup_logs`: full remaining height `RecyclerView`, items: `item_backup`
- `tv_no_data`: shown when empty

### Onboarding (`fragment_onboard_config.xml`)
`ConstraintLayout` (`?colorSurface` bg):
- `imv_app`: 130dp app logo, `blue_light_500` tint, top-center
- `tv_welcome`: `?textAppearanceHeadline6`
- `vp_config`: `ViewPager2` — pages: `ChooseLanguageFragment`, `ChooseCurrencyFragment`
- `btn_continue`: `MaterialButton`, full-width, bottom-pinned

---

## 8. List Item Layouts

### Expense Item (`item_expense_log.xml`)
Root: `SwipeFrameLayout` (custom, see §10)

**Back layer** (`fl_back`, bg: `red_400`):
- End side: `ic_delete` icon (35dp) + "Delete" label (`white` text) — swipe-to-delete reveal
- Start side: `ic_check` icon (35dp, `?colorOnPositive` tint) — swipe-to-confirm reveal

**Front layer** (`cd_expense`, bg: `?colorSurface`):
- `cv_category` (`Widget.ProExpense.CircularCardView`, 50dp×50dp, start margin 20dp): category icon (30dp, `dark_gray` tint)
- `tv_name`: `?textAppearanceBody1`, 0.9 alpha, end-truncate at 20 chars
- `tv_date`: `?textAppearanceCaption`, 0.9 alpha
- `linear_amount` (end-aligned): `tv_amount` (`?textAppearanceCurrencySmall`) + `tv_currency_symbol` (`?textAppearanceCaption`)

### Recent Expense Item (`item_expense_recent.xml`)
Similar to expense log item but simpler (no swipe layer):
- `cv_category` (`Widget.ProExpense.CircularCardView`, 55dp×55dp): category icon
- `tv_name` + `tv_date` on start side
- `tv_amount` (`?textAppearanceCurrencySmall`) + `tv_currency_symbol` (`?textAppearanceBody2`) on end side

### Category Chip (`item_category.xml`)
`MaterialCardView`, checkable, bg: `@color/category_background_color_statelist`:
- Checked: `colorPrimary` at 26% alpha
- Unchecked: `colorOnSurface` at 11% alpha
- `tv_name`: `?textAppearanceSubtitle1`

### Category Statistic Item (`item_category_statistic.xml`)
`RelativeLayout`:
- `tv_category_name`: `?textAppearanceBody1`
- `pv_progress` (`ProgressView` by skydoves, 20dp height): `?colorPrimary` progress, `color_statistic_background` bg, autoAnimate=true
- `tv_progress`: percentage label, `?textAppearanceSubtitle2`, 80% alpha

### Date Header (`item_expense_date_header.xml`)
`FrameLayout`, bg: `?backgroundColor`:
- `tv_date`: plain `TextView`, 20dp start margin

---

## 9. Dialogs

### Expense Detail Dialog (`expense_detail_dialog.xml`)
`ConstraintLayout` (`?colorSurface` bg):
- Title "Expense Detail" (`?textAppearanceHeadline6`, 70% alpha)
- Edit icon button (`ic_edit`) and Delete icon button (`ic_delete`) — end-aligned in title row
- `cd_info` card with `ConstraintLayout`:
  - `cv_category` (55dp circular): category icon
  - `tv_name_value`: `?textAppearanceHeadline6`
  - Labels (`?textAppearanceBody2`, 70% alpha) + values for Amount, Date, Note
  - Currency symbol next to amount value
- `btn_okay`: `Widget.ProExpense.Button`, full-width, bottom-pinned

### Delete Confirm Dialog (`fragment_delete_confirm_dialog.xml`)
Bottom sheet style dialog.

### Choose Theme Dialog (`choose_theme_dialog.xml`)
Dialog for selecting Light / Dark / System theme.

---

## 10. Custom Views

### `SpendGraph` (`:week-expense-graph` module)

A fully custom `View` drawn with `Canvas`. Renders a 7-day line graph with:
- Line connecting `SpendPoint` data points (one per day, 1–7)
- Dotted vertical line at the highest point with percentage label
- Day name labels (Mon–Sun) along the bottom

**Key internals**:
- All drawing via `Paint` objects: `dayPaint`, `linePaint`, `linePaint`, `labelPaint`, `linePointPaint`
- Graph color and day label color both driven by `?colorPrimary` from the layout attribute `app:graph_color` and `app:day_color`
- Uses an `Adapter` pattern (similar to `RecyclerView.Adapter`) — call `adapter.notifyDataChanged()` to refresh
- `SpendPoint(day: Int, rate: Float)` — rate is 0–100 (percentage of max), −1 means no data
- Layout: 16dp horizontal padding, 30dp bottom area for day labels

**XML attributes**: `app:graph_color`, `app:day_color`

**Compose migration**: Replace with a Compose `Canvas` implementation using `drawPath`, `drawCircle`, and `drawText`. Bind data through a `GraphState` class passed as a parameter. Colors should come from `MaterialTheme.colorScheme.primary`.

### `SwipeFrameLayout` (`ui/expenselogs/swipe/`)

Custom `FrameLayout` implementing swipe-to-reveal for expense list items. The front layer translates horizontally on touch; the back layer (delete/confirm) is revealed underneath. Used as the root of `item_expense_log.xml`.

**Compose migration**: Replace with `SwipeToDismiss` from `androidx.compose.material` or a custom `Modifier.pointerInput` implementation.

### `MaterialSearchBox` (`ui/common/customview/`)

Custom compound view wrapping a `MaterialCardView` + `TextInputEditText` + search icon. Defined as a `layout_search_box.xml` include. Has custom `attrs`: `backgroundColor`, `cornerRadius`, `hint`.

**Compose migration**: Replace with a `TextField` or `OutlinedTextField` composable with a trailing icon.

---

## 11. Animations

All animations are defined as XML sets in `res/anim/`. Durations are resource integers.

| File | Duration | Effect | Used for |
|---|---|---|---|
| `expense_enter_left.xml` | 250ms (`duration_left_animation`) | Slide in from right + fade in | Expense Entry screen enter |
| `expense_exit_right.xml` | 400ms (`duration_right_animation`) | Slide out to right + fade out | Expense Entry screen exit |
| `pop_down_up.xml` | 350ms (`duration_entry_pop_up`) | Slide up from 80% + fade in | Expense Entry pop-up enter |
| `pop_up_down.xml` | 400ms (`duration_entry_pop_drop`) | Slide down to 80% + fade out (delayed 300ms) | Expense Entry pop-up exit |

The `@TopDropNavOption` Hilt qualifier provides the `NavOptions` with these animations for the FAB → Entry navigation transition.

**Compose migration**: Use `AnimatedContentTransitionScope` in the `NavHost` composable or `AnimatedVisibility` for equivalent transitions. The entry screen uses a "drop down" modal-style animation rather than a horizontal slide.

---

## 12. Drawables & Icons

All icons are vector drawables (`res/drawable/ic_*.xml`). Icon set is custom/Material-style. Default tint is applied via `?iconColor` at the widget level, not in the drawable itself.

**Category icons** (used in circular card badges):

| Category | Icon |
|---|---|
| Food | `ic_food` |
| Household | `ic_household` |
| Education | `ic_education` |
| Transportation | `ic_transportation` |
| Healthcare | `ic_healthcare` |
| Entertainment | `ic_entertainment` |
| Clothes | `ic_clothes` |
| Donation | `ic_donation` |
| Social | `ic_social` |
| Income | `ic_income` |
| Borrow | `ic_borrow` |
| Accomplish | `ic_accomplish` |

Category icon backgrounds use a fixed hardcoded color in several places (`#b3e5fc` = `blue_light_100`) with `dark_gray` icon tint. This should be migrated to a proper color token or category color map.

**Background shapes**:
- `bg_small_rounded_warning`: rectangle, 16dp radius, `?colorWarning` fill — Beta badge
- `bg_large_rounded_warning`: rectangle, 16dp radius, `?colorWarning` fill — warning dialogs

**Flag icons** (for language selection): `flag_myanmar`, `flag_china`, `flag_germany`, `flag_united_states` — vector SVG-style drawables.

---

## 13. Localization & Font Overrides

Supported locales: `en` (default), `my` (Myanmar), `cn` (Chinese), `de` (German), `ru` (Russian).

Myanmar locale (`values-my/font_config.xml`) overrides `TextAppearance.ProExpense.Subtitle1` to use Pyidaungsu font, which is required for correct Myanmar script rendering.

Language is set at runtime by calling `Context.updateResource(locale)` in `MainActivity.attachBaseContext` — this reconstructs the Context with the stored locale before the Activity inflates any views.

**Compose migration**: Use `CompositionLocalProvider(LocalContext provides updatedContext)` or handle locale via `AppCompatDelegate.setApplicationLocales()` (API 33+) with fallback.
