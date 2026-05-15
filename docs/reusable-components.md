# Reusable UI Components

This document lists all reusable UI patterns extracted from the existing XML layouts. Each entry maps the current XML implementation to its proposed Compose equivalent and notes which screens use it. Use this as the component backlog for the Compose migration.

---

## Shell / Navigation

### 1. `ProExpenseTopBar`
A scrollable `AppBarLayout` + `MaterialToolbar` with `liftOnScroll=true`.

**XML pattern**: `CoordinatorLayout` > `AppBarLayout` > `MaterialToolbar` (or `Toolbar`) with `app:layout_behavior=AppBarLayout$ScrollingViewBehavior` on the scrollable content.

**Variants**:
- Drawer icon (`ic_menu`) — Home, Logs, Statistics, Settings, Backup
- Back icon (`ic_back`) — Entry, Feedback, About
- With menu (`app:menu`) — Entry (`menu_entry`), Logs (`menu_expense_log`), Home (`menu_home`)
- With subtitle slot — Logs

**Used in**: All 8 main screens.

**Compose**: `TopAppBar` / `CenterAlignedTopAppBar` from Material3 with `TopAppBarScrollBehavior`.

---

### 2. `DrawerHeader`
Navigation drawer header showing app logo, name, and Beta badge.

**XML**: `layout_header.xml` included via `app:headerLayout` on `NavigationView`.

**Contents**: Close `IconButton` (top-end) · 60dp app logo (tinted `?colorPrimary`) · App name (`Headline6`) · "BETA" badge (`bg_small_rounded_warning`, `?colorWarning` fill, `?colorOnWarning` text, uppercase).

**Used in**: `activity_main.xml`.

**Compose**: Custom `@Composable` header passed to `ModalDrawerSheet`.

---

### 3. `FloatingAddButton`
Global FAB visible only on the Home screen, managed by `MainActivity`.

**XML**: `FloatingActionButton` in `activity_main.xml`, bottom-end gravity, 32dp bottom margin, `?colorPrimary` bg, `?colorOnPrimary` tint, `ic_add` icon.

**Used in**: `activity_main.xml`.

**Compose**: Pass as `floatingActionButton` slot of `Scaffold` on the Home screen only.

---

## Cards & Containers

### 4. `DashboardCard`
Full-width surface card with a section title, an optional subtitle (date range), and a main content slot.

**XML pattern**: `MaterialCardView` > `RelativeLayout` with `tv_title` (`?textAppearanceMediumTitle`) + `tv_date_range` (`?textAppearanceSubtitle1`, `textAllCaps`) above the content area.

**Used in**: `layout_expense_in_out.xml` (Totals card), `layout_expense_graph.xml` (Graph card), `layout_recent_lists.xml` (Recent card).

**Compose**:
```kotlin
@Composable
fun DashboardCard(title: String, dateRange: String?, content: @Composable () -> Unit)
```

---

### 5. `CircularCategoryBadge`
Circular card containing a single centered icon, color-coded per category.

**XML**: `MaterialCardView` with `Widget.ProExpense.CircularCardView` style (50% corner radius, 2dp border) at 50–55dp, containing a 30dp `AppCompatImageView` with `dark_gray` tint. Background color currently hardcoded as `#b3e5fc` (`blue_light_100`) in most places.

**Used in**: `item_expense_log.xml`, `item_expense_recent.xml`, `expense_detail_dialog.xml`.

**Compose**:
```kotlin
@Composable
fun CategoryBadge(icon: ImageVector, backgroundColor: Color, size: Dp = 50.dp)
```

---

### 6. `InfoCard`
A `MaterialCardView` presenting label+value field pairs in a vertical list.

**XML pattern**: `MaterialCardView` > `ConstraintLayout` with `Barrier` for alignment. Each field is a label `TextView` (`?textAppearanceBody2`, 70% alpha) paired with a value `TextView` (`?textAppearanceBody1` or `CurrencySmall`).

**Used in**: `expense_detail_dialog.xml` (Amount, Date, Note fields).

**Compose**:
```kotlin
@Composable
fun InfoCard(fields: List<Pair<String, String>>)
```

---

## List Items

### 7. `ExpenseRow`
The core expense list item: category badge + name + date on the left, amount + currency symbol on the right.

**XML**: `item_expense_recent.xml` is the base. `item_expense_log.xml` reuses the same foreground layer inside `SwipeFrameLayout`.

**Key views**: `cv_category` (55dp badge) · `tv_name` (`Body1`, max 20 chars, ellipsize) · `tv_date` (`Caption`) · `tv_amount` (`CurrencySmall`) · `tv_currency_symbol` (`Body2`).

**Used in**: `layout_recent_lists.xml` (Home), `item_expense_log.xml` (Expense Logs).

**Compose**:
```kotlin
@Composable
fun ExpenseRow(item: ExpenseUiModel, onClick: () -> Unit)
```

---

### 8. `SwipeableExpenseRow`
`ExpenseRow` wrapped in swipe-to-delete behaviour with a red back layer.

**XML**: Root is `SwipeFrameLayout` (custom `FrameLayout`). Back layer is a red (`red_400`) `FrameLayout` containing a delete icon+label on the end and a confirm checkmark on the start. Front layer is the `ExpenseRow`.

**Used in**: `item_expense_log.xml`.

**Compose**: `SwipeToDismiss` (Material3) wrapping `ExpenseRow`, with a `DismissBackground` showing the red delete content.

---

### 9. `DateSectionHeader`
A simple date label used as a grouped section separator in lists.

**XML**: `item_expense_date_header.xml` — `FrameLayout` (`?backgroundColor`) with a single `TextView` at 20dp start margin.

**Used in**: Expense Logs list (mixed with `ExpenseRow` items via multi-type adapter).

**Compose**: `Text` composable with `backgroundColor` surface modifier, passed as a `stickyHeader` in `LazyColumn`.

---

### 10. `CategoryChip`
A checkable chip for selecting an expense category.

**XML**: `item_category.xml` — checkable `MaterialCardView` with `category_background_color_statelist` background (26% `colorPrimary` when checked, 11% `colorOnSurface` when unchecked), 2dp corner, 0dp elevation, `?textAppearanceSubtitle1` label.

**Used in**: `fragment_expense_entry.xml` (horizontal category picker row).

**Compose**: `FilterChip` or custom `Card` with `toggleable` modifier and `animateColorAsState`.

---

### 11. `CategoryStatisticRow`
A statistics row showing a category's share of total expenses.

**XML**: `item_category_statistic.xml` — category name (`Body1`) on the left, animated `ProgressView` (skydoves) filling the middle, percentage label (`Subtitle2`, 80% alpha) on the right.

**Used in**: `fragment_statistic.xml`.

**Compose**: `Row` with `LinearProgressIndicator` (animated via `animateFloatAsState`) and `Text` percentage label.

---

### 12. `BackupRow`
A backup file record row with a delete action.

**XML**: `item_backup.xml` — filename (ellipsized) + date + item count on the start; delete `IconButton` on the end; `ProgressBar` shown while the worker is running.

**Used in**: `fragment_backup.xml`.

**Compose**: `ListItem` or custom `Row` with trailing `IconButton` and conditional `CircularProgressIndicator`.

---

### 13. `CurrencyRow`
A selectable currency list item with symbol, name, and check indicator.

**XML**: `item_currency.xml` — `MaterialCardView` > `ConstraintLayout` with a `Guideline` at 25% for the symbol column; currency name fills remaining width; `ic_checked` icon visible when selected.

**Used in**: Currency selector dialog and `fragment_choose_currency.xml`.

**Compose**: `ListItem` with `leadingContent` (symbol) and `trailingContent` (checkmark).

---

### 14. `LanguageRow`
A selectable language list item with flag, name, and check indicator.

**XML**: `item_language.xml` — `MaterialCardView` > `RelativeLayout`: 25dp flag image on the start, ellipsized language name in the middle, `ic_checked` icon (invisible until selected) on the end.

**Used in**: Language selector dialog and `fragment_choose_language.xml`.

**Compose**: `ListItem` with `leadingContent` (flag `Image`) and `trailingContent` (conditional checkmark icon).

---

## Form Elements

### 15. `LabeledTextField`
An outlined `TextInputLayout` + `TextInputEditText` pair.

**XML**: `TextInputLayout` with `style="?attr/textInputStyle"` (OutlinedBox) wrapping `TextInputEditText`. Variants: single-line (name, amount), multi-line/fixed height (note, feedback comment).

**Used in**: `fragment_expense_entry.xml` (name, amount, note), `fragment_feedback.xml` (name, email, comment), `fragment_export_dialog.xml` (filename).

**Compose**: `OutlinedTextField` with `label`, `keyboardOptions`, and `maxLines`.

---

### 16. `CategoryPicker`
A horizontally scrolling row of `CategoryChip` items for selecting an expense category.

**XML**: `RecyclerView` with `android:orientation="horizontal"`, `LinearLayoutManager`, items: `item_category.xml`.

**Used in**: `fragment_expense_entry.xml`.

**Compose**: `LazyRow` of `CategoryChip` composables.

---

### 17. `SearchBox`
A rounded search input field with a trailing search icon.

**XML**: `layout_search_box.xml` — `MaterialCardView` (50dp height, `gray_200` bg, 8dp radius, 0dp elevation) containing `TextInputEditText` and a trailing `ic_search` `ImageView`.

**Custom view**: `MaterialSearchBox.kt` wraps this layout with `backgroundColor`, `cornerRadius`, and `hint` styleable attributes.

**Used in**: `fragment_choose_currency.xml`, `fragment_choose_language.xml`, currency/language dialogs.

**Compose**: `OutlinedTextField` or `TextField` with `trailingIcon = { Icon(Icons.Default.Search) }` and a rounded `shape`.

---

### 18. `SettingsRow`
A tappable preference row with a label on the start and a value/icon on the end, with a bottom divider.

**XML**: `FrameLayout` with `?selectableItemBackground`, `TextView` (start) and an `ImageView` or `TextView` (end, 60dp), followed by a `1dp` `View` divider with `@android:drawable/divider_horizontal_bright`.

**Used in**: `fragment_settings.xml` — Language row, Currency row, Theme row.

**Compose**: `ListItem` with `trailingContent` slot, or custom `Row` with `Divider` below.

---

## Dialogs

### 19. `DialogTitleBar`
Reusable dialog header: title text on the start, close / edit / delete icon buttons on the end.

**XML pattern**: `TextView` (`?textAppearanceHeadline6`) constrained to start; one or more `FrameLayout`-wrapped `IconButton`s constrained to end. Appears identically in 6 dialogs.

**Used in**: `expense_detail_dialog.xml`, `fragment_delete_confirm_dialog.xml`, `fragment_export_dialog.xml`, `fragment_choose_currency_dialog.xml`, `fragment_choose_language_dialog.xml`, `fragment_feedback_status_dialog.xml`.

**Compose**:
```kotlin
@Composable
fun DialogTitleBar(title: String, onClose: () -> Unit, actions: @Composable RowScope.() -> Unit = {})
```

---

### 20. `ExpenseDetailDialog`
Full expense detail bottom sheet: title bar + category + name + info card (amount, date, note) + OK button.

**XML**: `expense_detail_dialog.xml` — `ConstraintLayout` with `DialogTitleBar`, `CircularCategoryBadge`, `InfoCard`, and `Widget.ProExpense.Button`.

**Used in**: Home recent list and Expense Logs on item tap.

**Compose**: `ModalBottomSheet` or `AlertDialog` composable.

---

### 21. `DeleteConfirmDialog`
Confirmation dialog before deleting an expense or backup entry.

**XML**: `fragment_delete_confirm_dialog.xml` — `DialogTitleBar` + description card + red delete `Button` (`?colorNegative`).

**Used in**: Expense Logs, Backup.

**Compose**: `AlertDialog` with `confirmButton` styled in `colorError`.

---

### 22. `ChooseThemeDialog`
Theme mode picker: Light / Dark / System Default.

**XML**: `choose_theme_dialog.xml` — `RadioGroup` with 3 `RadioButton` items, `grid_3` horizontal padding.

**Used in**: `fragment_settings.xml` (on Theme row tap).

**Compose**: `AlertDialog` with a `Column` of `RadioButton` + `Text` rows.

---

### 23. `FilterDialog`
Date-range filter + sort order toggle for the expense log.

**XML**: `filter_expense_dialog.xml` — title + two date-range calendar cards (start/end, each showing month/day/year vertically, `CalendarDayCardView` style) + `MaterialButtonToggleGroup` (ASC/DESC, `singleSelection`) + Apply button.

**Used in**: `fragment_expense_logs.xml`.

**Compose**: `AlertDialog` with a date range picker and `SegmentedButton` for sort order.

---

### 24. `ExportDialog`
Filename entry dialog before exporting a backup.

**XML**: `fragment_export_dialog.xml` — `DialogTitleBar` + `LabeledTextField` (filename) + Save `Button` with icon.

**Used in**: `fragment_backup.xml`.

**Compose**: `AlertDialog` with `OutlinedTextField` and `confirmButton`.

---

### 25. `FeedbackStatusDialog`
Post-submission success/failure status screen.

**XML**: `fragment_feedback_status_dialog.xml` — close button + title + 55dp `ic_done_circle` icon + status message (`Body1`, centered) + "Go Home" `Button`.

**Used in**: `fragment_feedback.xml`.

**Compose**: Full-screen `Dialog` or `AlertDialog` with a success icon composable.

---

## Specialist / Custom

### 26. `SpendGraph`
A custom Canvas-drawn 7-day expense line graph.

**Module**: `:week-expense-graph` (`com.arduia.graph.SpendGraph`)

**XML usage**: `layout_expense_graph.xml` — 160dp height, `app:graph_color="?colorPrimary"`, `app:day_color="?colorPrimary"`.

**Internals**: Draws day labels (Mon–Sun), a connecting line between `SpendPoint` values, circle markers, and a dashed vertical line with a `%` label at the peak. Uses an `Adapter` pattern for data binding.

**Used in**: Home screen graph card.

**Compose**: Reimplement using `Canvas {}` in Compose with `drawPath`, `drawCircle`, and `drawText`. Accept a `List<SpendPoint>` as state. Colors from `MaterialTheme.colorScheme.primary`.

---

### 27. `NoDataPlaceholder`
An empty-state message shown when a list has no items.

**XML**: `layout_no_expense_logs.xml` — included in Expense Logs and Backup screens, toggled between `visible` / `invisible`.

**Used in**: `fragment_expense_logs.xml`, `fragment_backup.xml`.

**Compose**: `Box(contentAlignment = Center)` with a `Text` composable, shown conditionally via `AnimatedVisibility`.

---

### 28. `ToggleButtonGroup`
A two-option single-selection toggle (e.g. ASC / DESC sort).

**XML**: `MaterialButtonToggleGroup` with `app:singleSelection="true"` and two `MaterialButton` children.

**Used in**: `filter_expense_dialog.xml`.

**Compose**: `SegmentedButton` (Material3) or a custom `Row` of `OutlinedButton` with toggled border/fill state.
