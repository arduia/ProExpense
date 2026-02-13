# Pro Expense - UI Component Specifications

## 🎨 Design System Tokens

### Colors (Theme Attributes)
The app relies heavily on theme attributes for dynamic coloring (likely supporting Dark Mode).
- `?backgroundColor`: Main background color for screens.
- `?colorSurface`: Background for cards, toolbars, and dialogs.
- `?colorPrimary`: Primary brand color.
- `?colorOnSurface`: Text color on surface backgrounds.
- `?colorOnPositive` / `?colorOnNegative`: Semantic colors for success/error (e.g., in Swipe actions).
- `?selectableItemBackground` / `?selectableItemBackgroundBorderless`: Ripple effects.

### Typography (Theme Attributes)
- `?textAppearanceHeadline6`: Screen titles, dialog headers.
- `?textAppearanceMediumTitle`: Section headers (e.g., "Expenses in this week").
- `?textAppearanceSubtitle1`: Subtitles, date ranges.
- `?textAppearanceBody1`: Primary content text.
- `?textAppearanceBody2`: Secondary content/labels (e.g., "Amount", "Date").
- `?textAppearanceCaption`: Captions, currency symbols.
- `?textAppearanceOverline`: Small labels (e.g., "INCOME", "OUTCOME").
- `?textAppearanceCurrencyLarge` / `?textAppearanceCurrencySmall`: Specific styles for monetary values.

### Dimensions
- **Grid System**:
    - `@dimen/grid_1` (likely 4dp or 8dp)
    - `@dimen/grid_2`
    - `@dimen/grid_3` (Standard margin/padding)
    - `@dimen/grid_4`
- **Component Sizes**:
    - `@dimen/standard_icon_size`: Standard size for icons in toolbars/rows.
    - `@dimen/height_button`: Standard height for buttons.
    - `50dp`: Height for Search Box.
    - `55dp` / `50dp`: Circular Card View sizes.

## 📐 Layout Patterns

### Screen Structure
Most screens follow a standard **CoordinatorLayout** pattern:
```xml
<androidx.coordinatorlayout.widget.CoordinatorLayout>
    <com.google.android.material.appbar.AppBarLayout>
        <com.google.android.material.appbar.MaterialToolbar />
    </com.google.android.material.appbar.AppBarLayout>

    <androidx.core.widget.NestedScrollView>
        <!-- Content -->
    </androidx.core.widget.NestedScrollView>
</androidx.coordinatorlayout.widget.CoordinatorLayout>
```

### Dialogs
- Utilizes `ConstraintLayout` or `RelativeLayout` wrapped in a container.
- **Header**: Text title with close button (`AppCompatImageButton`) or simple text.
- **Content**: Often forms or information details.
- **Actions**: Bottom buttons (e.g., "Okay").

## 🧩 Components

### Cards
- **MaterialCardView** is the primary container for grouped content.
- **Styles**:
    - Standard Card: Radius `@dimen/size_radius` (or similar), elevation `0dp` often used with colored background.
    - Circular Card (`Widget.ProExpense.CircularCardView`): Used for category icons.

### Lists & Items
- **Expense Log Item** (`item_expense_log.xml`):
    - **Swipe-to-delete**: Uses custom `SwipeFrameLayout`.
    - **Background Layer**: Contains Delete (Red) and Check/Edit actions.
    - **Foreground Layer**: `MaterialCardView` with category icon, title, date, and amount.
- **Category Item** (`item_category.xml`):
    - `MaterialCardView` with `checkable="true"`.
    - Background tint changes state list `@color/category_background_color_statelist`.

### Headers & Summaries
- **In/Out Summary** (`layout_expense_in_out.xml`):
    - Displays Income vs Outcome.
    - Uses `?textAppearanceCurrencyLarge` for values.
- **Graph Card** (`layout_expense_graph.xml`):
    - Embeds Custom `SpendGraph`.

### Inputs
- **Search Box**:
    - Implementation 1: Custom View `MaterialSearchBox`.
    - Implementation 2: `layout_search_box.xml` with `MaterialCardView` + `TextInputEditText` + `ImageView` (Search Icon).
    - Background: `@color/gray_200`.
    - Corner Radius: `@dimen/grid_2` (or similar).

## 🛠 Custom Views (Legacy to be Migrated)
| Component | Class | XML Usage | Notes |
|-----------|-------|-----------|-------|
| **Swipe Layout** | `SwipeFrameLayout` | `<com.arduia...SwipeFrameLayout>` | Handles swipe gestures for list items. |
| **Spend Graph** | `SpendGraph` | `<com.arduia.graph.SpendGraph>` | Draws weekly expense charts. |
| **Search Box** | `MaterialSearchBox` | `<com.arduia...MaterialSearchBox>` | Encapsulated search UI logic. |

## 🚀 Reusable Jetpack Compose Components (Created)

| Component | File | XML Equivalent |
| :--- | :--- | :--- |
| **StandardCard** | `StandardCard.kt` | `MaterialCardView` |
| **CategoryIcon** | `CategoryIcon.kt` | `MaterialCardView` (Circular) |
| **ProSearchBox** | `ProSearchBox.kt` | `layout_search_box.xml` |
| **ExpenseLogItem** | `ExpenseLogItem.kt` | `item_expense_log.xml` |
| **IncomeOutcomeCard** | `IncomeOutcomeCard.kt` | `layout_expense_in_out.xml` |
| **CategoryGridItem** | `CategoryGridItem.kt` | `item_category.xml` |
| **CategoryStatisticItem** | `CategoryStatisticItem.kt` | `item_category_statistic.xml` |
| **BackupItem** | `BackupItem.kt` | `item_backup.xml` |
| **SettingsItem** | `SettingsItem.kt` | `fragment_settings.xml` (row) |
| **SelectionItem** | `SelectionItem.kt` | `item_language.xml`, `item_currency.xml` |
| **DateHeader** | `DateHeader.kt` | `item_expense_date_header.xml` |
| **EmptyState** | `EmptyState.kt` | `layout_no_expense_logs.xml` |
