# Reusable UI Components

This document lists all reusable UI patterns extracted from the existing XML layouts, cross-referenced with their usage in fragments, activities, adapters, and ViewModels. Each entry documents the possible UI states, the events it emits, and the proposed Compose equivalent.

---

## Shell / Navigation

### 1. `ProExpenseTopBar`
A scrollable `AppBarLayout` + `MaterialToolbar` with `liftOnScroll=true`.

**XML pattern**: `CoordinatorLayout` > `AppBarLayout` > `MaterialToolbar` + `AppBarLayout$ScrollingViewBehavior` on the scrollable child.

**UI States**:

| State | Description | Trigger |
|---|---|---|
| `Drawer` | `ic_menu` navigation icon, unlocks drawer | Top-level destination |
| `Back` | `ic_back` navigation icon, locks drawer | Detail/entry destination |
| `SelectionMode` | Back icon + "N selected" title + delete menu icon shown, filter hidden | `expenseLogMode = SELECTION` (Logs) |
| `NormalMode` | Menu icon + screen title + filter icon shown, delete hidden | `expenseLogMode = NORMAL` (Logs) |
| `MenuDisabled` | Menu items (`filter`, `delete`) greyed out | `isEmptyExpenseCount = true` (Logs) or `isEmptyExpenseData = true` (Stats) |
| `WithSubtitle` | Secondary subtitle row visible | `filterInfo` set (Logs), `dateRange` set (Stats), datetime set (Entry) |

**Events**: navigation icon click (open drawer or pop back), menu item clicks (filter, delete, calendar).

**Used in**: Home, Expense Logs, Statistics, Settings, Backup, Entry, Feedback, About, Web (9 screens).

**Compose**: `TopAppBar` (Material3) with `TopAppBarScrollBehavior`. Pass `navigationIcon`, `actions`, and optional `subtitle` slot.

---

### 2. `DrawerHeader`
Navigation drawer header with app logo, name, and Beta badge.

**XML**: `layout_header.xml` included via `NavigationView.app:headerLayout`.

**UI States**:

| State | Description |
|---|---|
| `Default` | Logo (60dp, `?colorPrimary` tint) + "Pro Expense" name + "BETA" badge always visible |

No dynamic states — entirely static. Close `IconButton` dismisses drawer.

**Events**: close button click → `MainActivity.closeDrawer()`.

**Used in**: `activity_main.xml`.

**Compose**: Custom header `@Composable` passed into `ModalDrawerSheet`.

---

### 3. `FloatingAddButton`
Global FAB shown only on the Home destination.

**XML**: `FloatingActionButton` in `activity_main.xml`.

**UI States**:

| State | Description | Trigger |
|---|---|---|
| `Hidden` | FAB invisible and not clickable (default) | Any non-Home destination |
| `Visible` | FAB visible and clickable | `dest_home` active |
| `DelayedShow` | FAB held back while Snackbar is displayed, then shown after its duration + 300ms | `showSnackMessage()` active |

**Events**: click → navigate to `dest_expense_entry` with `@TopDropNavOption` (pop-down animation).

**Used in**: `activity_main.xml`, controlled via `MainHost` interface from all fragments.

**Compose**: Pass as `floatingActionButton` slot of the Home screen's `Scaffold`.

---

## Cards & Containers

### 4. `DashboardCard`
Full-width surface card with a section title, optional date-range subtitle, and a content slot.

**XML pattern**: `MaterialCardView` > `RelativeLayout` with `tv_title` (`?textAppearanceMediumTitle`) + `tv_date_range` (`?textAppearanceSubtitle1`, `textAllCaps`) above content.

**UI States**:

| State | Description | Trigger |
|---|---|---|
| `Loading` | Content area empty / placeholder | ViewModel data not yet emitted |
| `Populated` | Title + date range + content visible | `graphUiModel` / `incomeOutcomeData` emitted |

**Events**: none — container only. Child content handles interactions.

**Used in**: `layout_expense_in_out.xml` (Totals), `layout_expense_graph.xml` (Graph), `layout_recent_lists.xml` (Recent) — all on Home.

**Compose**:
```kotlin
@Composable
fun DashboardCard(title: String, dateRange: String? = null, content: @Composable () -> Unit)
```

---

### 5. `CircularCategoryBadge`
Circular card containing a centered category icon, color-coded per category.

**XML**: `MaterialCardView` with `Widget.ProExpense.CircularCardView` style (50% corner radius) at 50–55dp, containing a 30dp `AppCompatImageView`. Background currently hardcoded `#b3e5fc` (`blue_light_100`) in most places.

**UI States**:

| State | Description |
|---|---|
| `Default` | Icon + background tint for the given category |
| `Selected` *(future)* | Highlighted border or elevated state in selection mode |

Background color is set per-item from `categoryProvider.getCategoryDrawableByID(category)`. No runtime state changes on this view itself.

**Used in**: `item_expense_log.xml`, `item_expense_recent.xml`, `expense_detail_dialog.xml`.

**Compose**:
```kotlin
@Composable
fun CategoryBadge(icon: ImageVector, backgroundColor: Color, modifier: Modifier = Modifier, size: Dp = 50.dp)
```

---

### 6. `InfoCard`
A `MaterialCardView` presenting aligned label+value field pairs.

**XML**: `MaterialCardView` > `ConstraintLayout` with `Barrier`. Each field is a `Body2` label (70% alpha) paired with a `Body1` or `CurrencySmall` value.

**UI States**:

| State | Description | Trigger |
|---|---|---|
| `NoteVisible` | Note row displayed | `note` is non-empty |
| `NoteHidden` | Note row visibility = `INVISIBLE` | `note` is null or blank — set in `ExpenseDetailDialog.setDetail()` |

**Used in**: `expense_detail_dialog.xml` (Amount, Date, Note fields).

**Compose**:
```kotlin
@Composable
fun InfoCard(amount: String, currency: String, date: String, note: String?)
```

---

## List Items

### 7. `ExpenseRow`
Core expense item: category badge + name + date + amount + currency.

**XML**: `item_expense_recent.xml` is the base. The same foreground layer appears in `item_expense_log.xml` inside `SwipeFrameLayout`.

**UI States**:

| State | Description | Trigger |
|---|---|---|
| `Default` | All fields populated, normal background | Standard list display |
| `EmptyName` | Name shows empty string | `expense.name` is null → mapped to `""` in `ExpenseUiModelMapper` |

Amount is always formatted with `@CurrencyDecimalFormat` — never raw. Date is formatted by `DateFormatter`.

**Events**: item click → `onItemClickListener.invoke(item)` → ViewModel opens detail dialog.

**Used in**: `layout_recent_lists.xml` (Home), `item_expense_log.xml` (Expense Logs).

**Compose**:
```kotlin
@Composable
fun ExpenseRow(item: ExpenseUiModel, onClick: () -> Unit, modifier: Modifier = Modifier)
```

---

### 8. `SwipeableExpenseRow`
`ExpenseRow` wrapped in swipe-to-reveal with a red delete back-layer.

**XML**: Root is `SwipeFrameLayout`. Back layer: red `FrameLayout` with delete icon+label (end) and confirm checkmark (start). Front layer: `ExpenseRow` content.

**UI States** (driven by `SwipeFrameLayout.currentState`):

| State | Constant | Visual | Event emitted |
|---|---|---|---|
| `Idle` | `STATE_IDLE` | Front layer centred, no back visible | — |
| `StartLocked` | `STATE_START_LOCKED` | Front slides right, green confirm visible on left | `OnSelectedChangedListener(true)` → item selected for multi-delete |
| `EndLocked` | `STATE_END_LOCKED` | Front slides left, red delete area revealed on right | `OnPrepareChangedListener(true)` → `viewModel.onSingleDeletePrepared(id)` |

Swipe gesture activated by long-press. Animation uses `ValueAnimator` + `LinearOutSlowInInterpolator`. Lock margins: 46dp start, 80dp end. State is persisted per-item in `SwipeStateHolder` and restored via `onRestoreSwipeState` event from ViewModel.

**Events**:
- `EndLocked` confirmed → delete icon tap → `onItemDeleteListener(item)`
- `StartLocked` → item enters selection mode, `expenseLogMode → SELECTION`

**Used in**: `item_expense_log.xml` (Expense Logs only).

**Compose**: `SwipeToDismiss` (Material3) with a `DismissBackground` composable showing the red delete content.

---

### 9. `DateSectionHeader`
A date label used as a grouped section separator between expense items.

**XML**: `item_expense_date_header.xml` — `FrameLayout` (`?backgroundColor`) with single `TextView`, 20dp start margin.

**UI States**:

| State | Description |
|---|---|
| `Default` | Date string populated from `ExpenseLogUiModel.Header.date` |

No dynamic state — content-only.

**Used in**: Expense Logs mixed-type list (`TYPE_HEADER` in `ExpenseLogAdapter`).

**Compose**: Plain `Text` with `Modifier.background(MaterialTheme.colorScheme.background)`, used as `stickyHeader` in `LazyColumn`.

---

### 10. `CategoryChip`
Checkable chip for selecting an expense category in the entry form.

**XML**: `item_category.xml` — checkable `MaterialCardView` with `category_background_color_statelist` (26% `colorPrimary` when checked, 11% `colorOnSurface` when unchecked), 2dp corner, `?textAppearanceSubtitle1` label.

**UI States**:

| State | Description | Trigger |
|---|---|---|
| `Unchecked` | Faint surface tint | Not selected |
| `Checked` | Primary-tinted background | `viewModel.selectCategory(category)` called; adapter moves selected item to index 0 post-animation |

Selection is single-choice; the adapter updates via `selectedCategory` LiveData.

**Events**: click → `viewModel.selectCategory(ExpenseCategory)`.

**Used in**: `fragment_expense_entry.xml` (horizontal `CategoryPicker` RecyclerView).

**Compose**: `FilterChip` or custom `Card` with `toggleable` modifier + `animateColorAsState`.

---

### 11. `CategoryStatisticRow`
Statistics row showing a category's share of total expenses.

**XML**: `item_category_statistic.xml` — category name (`Body1`) + animated `ProgressView` (skydoves, `autoAnimate=true`) + percentage label (`Subtitle2`, 80% alpha).

**UI States**:

| State | Description | Trigger |
|---|---|---|
| `Loading` | No data shown | `categoryStatisticList` not yet emitted |
| `Populated` | Name, progress value (0–100), percentage string | `categoryStatisticList` emitted from ViewModel |
| `AnimatingIn` | ProgressView animates from 0 to target value | `autoAnimate=true` on `ProgressView` whenever bound |

Progress bar uses `?colorPrimary` fill and `color_statistic_background` track.

**Used in**: `fragment_statistic.xml` RecyclerView.

**Compose**: `Row` with `LinearProgressIndicator(progress = animateFloatAsState(target))` and `Text` labels.

---

### 12. `BackupRow`
A backup file record with filename, metadata, and a delete action.

**XML**: `item_backup.xml` — filename (ellipsized, `Body1`) + created date + item count; delete `IconButton` on the end; `ProgressBar` shown while the WorkManager task is running.

**UI States**:

| State | Description | Trigger |
|---|---|---|
| `InProgress` | `ProgressBar` visible, delete icon hidden | `BackupEnt.isCompleted = false` |
| `Completed` | `ProgressBar` gone, delete icon visible | `BackupEnt.isCompleted = true` |

The `BackupMessageViewModel` monitors WorkManager task UUIDs and emits `finishedEvent` when work completes.

**Events**: click → opens delete confirmation dialog; delete icon click → `viewModel.onBackupDeleteConfirmed(item)`.

**Used in**: `fragment_backup.xml` RecyclerView.

**Compose**: `ListItem` with `trailingContent` switching between `IconButton` and `CircularProgressIndicator`.

---

### 13. `CurrencyRow`
A selectable currency list item.

**XML**: `item_currency.xml` — `MaterialCardView` > `ConstraintLayout` with a `Guideline` at 25% for the symbol column; name fills remaining width; `ic_checked` visible when selected.

**UI States**:

| State | Description |
|---|---|
| `Unselected` | Check icon invisible |
| `Selected` | Check icon (`ic_checked`) visible |

Selection driven by matching the item's currency code against the stored preference.

**Events**: click → saves selected currency, updates `currencyValue` LiveData in `SettingsViewModel`.

**Used in**: Currency selector dialog (`fragment_choose_currency_dialog.xml`) and `fragment_choose_currency.xml`.

**Compose**: `ListItem` with `leadingContent` (symbol `Text`) and `trailingContent` (conditional `Icon`).

---

### 14. `LanguageRow`
A selectable language list item with flag.

**XML**: `item_language.xml` — `MaterialCardView` > `RelativeLayout`: 25dp flag image (start), ellipsized language name (middle), `ic_checked` (end, `invisible` by default).

**UI States**:

| State | Description |
|---|---|
| `Unselected` | Checkmark invisible |
| `Selected` | Checkmark visible |

Selection driven by matching item's language code against `selectedLanguage` from `SettingsViewModel`.

**Events**: click → saves selected language; triggers `onRestart` event in `OnBoardingConfigFragment` or activity recreation.

**Used in**: Language selector dialog (`fragment_choose_language_dialog.xml`) and `fragment_choose_language.xml`.

**Compose**: `ListItem` with `leadingContent` (flag `Image`) and `trailingContent` (conditional checkmark icon).

---

## Form Elements

### 15. `LabeledTextField`
An outlined `TextInputLayout` + `TextInputEditText` pair.

**XML**: `TextInputLayout` with `style="?attr/textInputStyle"` (OutlinedBox) wrapping `TextInputEditText`.

**UI States**:

| State | Description | Trigger |
|---|---|---|
| `Empty` | Hint shown, no value | Field is blank |
| `Filled` | User input shown | User typing |
| `Error` | Error message shown below field, outline turns red | Validation failure on save (amount empty, email invalid, comment empty) |
| `WithSuffix` | Currency symbol shown as suffix text | `currencySymbol` LiveData emitted (Amount field only) |
| `Focused` | Outline highlighted | Field has input focus |
| `Disabled` *(amount)* | Amount field restricted via `FloatingInputFilter` | Input filter rejects non-decimal characters |

Validation errors are set via `textInputLayout.error = "message"` on the save/send button click. Amount field uses `FloatingInputFilter` to restrict input to valid decimal values.

**Events**: text change → picked up on save/update button click; ime action (Next/Done) moves focus.

**Used in**: `fragment_expense_entry.xml` (name, amount, note), `fragment_feedback.xml` (name, email, comment), `fragment_export_dialog.xml` (filename).

**Compose**: `OutlinedTextField` with `isError`, `supportingText`, `suffix`, `keyboardOptions`, and `maxLines`.

---

### 16. `CategoryPicker`
Horizontal scrolling row of `CategoryChip` items for selecting a category.

**XML**: `RecyclerView` with `android:orientation="horizontal"`, `LinearLayoutManager`, items: `item_category.xml`.

**UI States**:

| State | Description | Trigger |
|---|---|---|
| `Default` | All categories shown, last-used or first category checked | On screen open |
| `CategorySelected` | Tapped chip becomes checked; selected item animated to index 0 | `viewModel.selectCategory()` → `selectedCategory` LiveData |

**Events**: chip click → `viewModel.selectCategory(ExpenseCategory)`.

**Used in**: `fragment_expense_entry.xml`.

**Compose**: `LazyRow` of `CategoryChip` composables with `selectedCategory` state.

---

### 17. `SearchBox`
Rounded search input field with a trailing search icon.

**XML**: `layout_search_box.xml` — `MaterialCardView` (50dp height, `gray_200` bg, 8dp corner, 0 elevation) with `TextInputEditText` and trailing `ic_search` `ImageView`.

**Custom view**: `MaterialSearchBox.kt` wraps this layout; accepts `backgroundColor`, `cornerRadius`, `hint` styled attributes.

**UI States**:

| State | Description | Trigger |
|---|---|---|
| `Empty` | Hint text shown, search icon visible | No input |
| `Typing` | User text shown, icon still visible | User typing |

Text changes emitted via `SearchTextChangeListener.onChanged(String)` → filters the currency/language adapter list in real time.

**Used in**: `fragment_choose_currency.xml`, `fragment_choose_language.xml`, currency/language dialogs.

**Compose**: `TextField` or `OutlinedTextField` with `trailingIcon = { Icon(Icons.Default.Search) }` and rounded `shape`.

---

### 18. `SettingsRow`
A tappable preference row with a label, a trailing value/icon, and a bottom divider.

**XML**: `FrameLayout` (`?selectableItemBackground`) with start `TextView` and end `ImageView`/`TextView`, followed by a 1dp `View` divider.

**UI States**:

| Row | Trailing content | State |
|---|---|---|
| Language | `ImageView` (flag drawable) | Updates to flag of `selectedLanguage` from SettingsViewModel |
| Currency | `TextView` (`Headline6`) | Updates to currency symbol string from `currencyValue` LiveData |
| Theme | `ImageView` (`ic_theme`, static) | No runtime update |

**Events**: click → opens corresponding dialog (language, currency, or theme chooser).

**Used in**: `fragment_settings.xml` (3 rows).

**Compose**: `ListItem` with `trailingContent` slot, `Divider` below each row.

---

## Dialogs

### 19. `DialogTitleBar`
Reusable dialog header: title on the start, close and optional action icon buttons on the end.

**XML pattern**: `TextView` (`?textAppearanceHeadline6`) + one or more `FrameLayout`-wrapped `IconButton`s constrained to the end.

**UI States**:

| State | Description | Where |
|---|---|---|
| `CloseOnly` | Only close `IconButton` visible | Export, Currency, Language, Feedback Status dialogs |
| `CloseWithActions` | Close + edit + delete icon buttons | Expense Detail dialog (`isDeleteEnabled` controls delete visibility) |

Delete button frame visibility: `VISIBLE` if `isDeleteEnabled = true` (set from `HomeFragment`/`ExpenseFragment` call site), `INVISIBLE` otherwise.

**Events**: close → dismiss; edit → `editOnClickListener(item)`; delete → `deleteOnClickListener(item)`.

**Used in**: `expense_detail_dialog.xml`, `fragment_delete_confirm_dialog.xml`, `fragment_export_dialog.xml`, `fragment_choose_currency_dialog.xml`, `fragment_choose_language_dialog.xml`, `fragment_feedback_status_dialog.xml`.

**Compose**:
```kotlin
@Composable
fun DialogTitleBar(title: String, onClose: () -> Unit, actions: @Composable RowScope.() -> Unit = {})
```

---

### 20. `ExpenseDetailDialog`
Full expense detail bottom sheet shown on item tap.

**XML**: `expense_detail_dialog.xml` — `DialogTitleBar` + `CircularCategoryBadge` + `InfoCard` (amount, date, note) + OK `Button`.

**UI States**:

| State | Description | Trigger |
|---|---|---|
| `NoteHidden` | Note row visibility = `INVISIBLE` | `detail.note` is null or blank |
| `NoteVisible` | Note row shown | `detail.note` non-empty |
| `DeleteEnabled` | Delete `IconButton` visible | `isDeleteEnabled = true` passed from call site |
| `DeleteHidden` | Delete `IconButton` invisible | `isDeleteEnabled = false` (e.g. read-only contexts) |

**Events**: edit click → navigate to `dest_expense_entry` with `expense_id`; delete click → opens `DeleteConfirmFragment`; OK → dismiss; dismiss → `dismissListener()`.

**Used in**: `HomeFragment` (recent list tap), `ExpenseFragment` (log list tap).

**Compose**: `ModalBottomSheet` or `AlertDialog` composable containing `InfoCard` and action buttons.

---

### 21. `DeleteConfirmDialog`
Confirmation dialog before destructive deletion.

**XML**: `fragment_delete_confirm_dialog.xml` — `DialogTitleBar` + description card + red delete `Button`.

**UI States**:

| State | Description | Trigger |
|---|---|---|
| `Singular` | "1 item will be deleted" | `DeleteInfoUiModel.itemTotal == 1` |
| `Plural` | "N items will be deleted" | `DeleteInfoUiModel.itemTotal > 1` |

**Events**: confirm → `onConfirmListener()` → ViewModel executes delete; close → dismiss only.

**Used in**: `ExpenseFragment` (single swipe-delete and multi-select delete), `BackupFragment` (backup record delete).

**Compose**: `AlertDialog` with dynamic message text and `confirmButton` styled in `colorError`.

---

### 22. `ChooseThemeDialog`
Theme mode picker: Light / Dark / System Default.

**XML**: `choose_theme_dialog.xml` — `RadioGroup` with 3 `RadioButton` items.

**UI States**:

| State | Description | Trigger |
|---|---|---|
| `LightSelected` | Light radio checked | Current theme = `MODE_NIGHT_NO` |
| `DarkSelected` | Dark radio checked | Current theme = `MODE_NIGHT_YES` |
| `SystemSelected` | System default radio checked | Current theme = `MODE_NIGHT_FOLLOW_SYSTEM` |

Initial selection passed via `onThemeOpenToChange` event from `SettingsViewModel`. On save → `viewModel.onThemeChanged()` → triggers activity recreation via `onThemeChanged` EventLiveData.

**Events**: radio selection + save → persists theme mode, triggers `onThemeChanged` event.

**Used in**: `fragment_settings.xml` (Theme row tap).

**Compose**: `AlertDialog` with `Column` of `RadioButton` + `Text` rows.

---

### 23. `FilterDialog`
Date-range filter and sort-order toggle for the expense log and statistics screens.

**XML**: `filter_expense_dialog.xml` — title + two `CalendarDayCardView` date cards (start/end, each showing month/day/year) + `MaterialButtonToggleGroup` (ASC/DESC, `singleSelection`) + Apply button.

**UI States**:

| State | Description | Trigger |
|---|---|---|
| `StartDateSelected` | Start date card shows selected date | Date picker confirms start date |
| `EndDateSelected` | End date card shows selected date | Date picker confirms end date |
| `AscSelected` | ASC button toggled | User taps ASC |
| `DescSelected` | DESC button toggled (default) | User taps DESC |
| `Populated` | Pre-filled from current `filterInfo` | `onFilterShow` event carries `ExpenseLogFilterInfo` |

**Events**: Apply → `viewModel.setFilter(filter)`; date card tap → opens `DatePickerDialog`.

**Used in**: `fragment_expense_logs.xml` (filter menu tap), `fragment_statistic.xml` (calendar icon tap).

**Compose**: `AlertDialog` with a date range picker and `SegmentedButton` for sort order.

---

### 24. `ExportDialog`
Filename entry dialog before exporting a backup.

**XML**: `fragment_export_dialog.xml` — `DialogTitleBar` + `LabeledTextField` (filename) + Save `Button` with icon.

**UI States**:

| State | Description | Trigger |
|---|---|---|
| `Idle` | Empty filename field, Save enabled | Dialog opens |
| `Exporting` | WorkManager task enqueued; dialog closes optimistically | Save tapped with valid filename |

**Events**: Save → triggers WorkManager export task via `BackupFragment`; close → dismiss.

**Used in**: `fragment_backup.xml` (Export card tap).

**Compose**: `AlertDialog` with `OutlinedTextField` and `confirmButton`.

---

### 25. `FeedbackStatusDialog`
Post-submission status screen shown after feedback is sent.

**XML**: `fragment_feedback_status_dialog.xml` — close button + title + 55dp `ic_done_circle` icon + status message (`Body1`, centered) + "Go Home" `Button`.

**UI States**:

| State | Description | Trigger |
|---|---|---|
| `Success` | Done icon shown, positive status message | `feedbackSubmittedEvent` emitted |

Only one state is shown (the dialog appears only on success). The WorkManager task is enqueued with a `CONNECTED` network constraint; the UI updates optimistically.

**Events**: "Go Home" → `navController.popBackStack(dest_home)`; close → dismiss.

**Used in**: `fragment_feedback.xml`.

**Compose**: `Dialog` with centered success icon and action button.

---

## Specialist / Custom

### 26. `SpendGraph`
Canvas-drawn 7-day expense line graph with day labels and a peak marker.

**Module**: `:week-expense-graph` (`com.arduia.graph.SpendGraph`).

**XML usage**: `layout_expense_graph.xml` — 160dp height, `app:graph_color="?colorPrimary"`, `app:day_color="?colorPrimary"`.

**UI States**:

| State | Description | Trigger |
|---|---|---|
| `Empty` | Graph draws no line; day labels still shown | `adapter.getRate(day)` returns value outside 0–100 → stored as `rate = -1f` |
| `Populated` | Line graph drawn between data points | `adapter.notifyDataChanged()` called with valid rates |
| `Animating` | View invalidated and redrawn | `notifyDataChanged()` / `notifyPointChanged()` |

The adapter pattern: `SpendGraph.Adapter.getRate(day: Int): Int` returns 0–100 per day. Rates outside this range are treated as no-data (skipped in drawing). The highest-rate point gets a dashed vertical line and `%` label — omitted if it would overflow the canvas edge.

Colors come from `app:graph_color` and `app:day_color` XML attributes (both bound to `?colorPrimary` in the layout).

**Events**: none — display-only. Data is pushed via `Adapter.notifyDataChanged()`.

**Used in**: Home screen graph card (`layout_expense_graph.xml`).

**Compose**: Reimplement using Compose `Canvas {}` with `drawPath`, `drawCircle`, `drawText`. Accept `List<SpendPoint>` as a parameter. Colors from `MaterialTheme.colorScheme.primary`.

---

### 27. `NoDataPlaceholder`
Empty-state message shown when a list has no items.

**XML**: `layout_no_expense_logs.xml` — included in screens, toggled between `VISIBLE` / `INVISIBLE`.

**UI States**:

| State | Description | Trigger |
|---|---|---|
| `Hidden` | `visibility = INVISIBLE` | `isCurrentListEmpty = false` (Logs) / `isEmptyBackupLogs = false` (Backup) |
| `Visible` | `visibility = VISIBLE` | `isCurrentListEmpty = true` or `isEmptyBackupLogs = true` |

Note: uses `INVISIBLE` (not `GONE`) so the layout still occupies space and doesn't cause a reflow when the list populates.

**Used in**: `fragment_expense_logs.xml`, `fragment_backup.xml`.

**Compose**: `AnimatedVisibility` wrapping a centred `Text`, shown conditionally via `isListEmpty` state.

---

### 28. `ToggleButtonGroup`
Two-option single-selection toggle (e.g. ASC / DESC sort order).

**XML**: `MaterialButtonToggleGroup` with `app:singleSelection="true"` and two `MaterialButton` children.

**UI States**:

| State | Description | Trigger |
|---|---|---|
| `AscSelected` | ASC button filled/checked | User taps ASC |
| `DescSelected` | DESC button filled/checked (pre-populated from `filterInfo.sortType`) | User taps DESC or dialog opens with existing filter |

Initial state pre-filled from `ExpenseLogFilterInfo.sortType` when `onFilterShow` event fires.

**Events**: selection change → updates local filter state; Apply button commits via `viewModel.setFilter()`.

**Used in**: `filter_expense_dialog.xml`.

**Compose**: `SegmentedButton` (Material3) or custom `Row` of `OutlinedButton` with toggled fill state.
