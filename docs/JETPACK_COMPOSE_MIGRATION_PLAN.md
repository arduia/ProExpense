# Jetpack Compose Migration Plan: UI Components

This document outlines the plan for migrating the existing XML-based UI components to Jetpack Compose, based on the analysis of the `app/src/main/res/layout` directory and the `UI_COMPONENT_SPEC.md`.

## 🎨 Theme & Design System

The core design system should be implemented first to ensure consistency across all Composables.

### 1. Colors (`Color.kt`)
Map the XML theme attributes to Compose `Color` definitions.
- `?backgroundColor` -> `MaterialTheme.colors.background`
- `?colorSurface` -> `MaterialTheme.colors.surface`
- `?colorPrimary` -> `MaterialTheme.colors.primary`
- `?colorOnSurface` -> `MaterialTheme.colors.onSurface`
- `?colorOnPositive` (Green) -> `Color(0xFF...)` (Define semantic color)
- `?colorOnNegative` (Red) -> `Color(0xFF...)` (Define semantic color)

### 2. Typography (`Type.kt`)
Map the XML `textAppearance` styles to Compose `Typography`.
- `?textAppearanceHeadline6` -> `MaterialTheme.typography.h6`
- `?textAppearanceSubtitle1` -> `MaterialTheme.typography.subtitle1`
- `?textAppearanceBody1` -> `MaterialTheme.typography.body1`
- `?textAppearanceBody2` -> `MaterialTheme.typography.body2`
- `?textAppearanceCaption` -> `MaterialTheme.typography.caption`
- `?textAppearanceOverline` -> `MaterialTheme.typography.overline`
- **Custom**: `CurrencyLarge`, `CurrencySmall` (Define custom `TextStyle` extensions).

### 3. Shapes (`Shape.kt`)
Map the XML dimensions to Compose `Shapes`.
- `@dimen/size_radius` (Backgrounds) -> `RoundedCornerShape(size_radius.dp)`
- `@dimen/grid_2` (Search Box) -> `RoundedCornerShape(grid_2.dp)`
- **Circular**: `CircleShape` for category icons.

## 🧩 Reusable Components (Composables)

These components will replace the existing XML layouts and Custom Views.

### 1. Cards
- **`StandardCard`**:
    - Usage: General content container.
    - Implementation: `Card(elevation = 0.dp, shape = MaterialTheme.shapes.medium)`
- **`CategoryIcon`** (`item_category.xml`):
    - Usage: Displays category icon with circular background.
    - Implementation:
      ```kotlin
      @Composable
      fun CategoryIcon(
          icon: ImageVector,
          backgroundColor: Color,
          tint: Color = MaterialTheme.colors.onSurface,
          modifier: Modifier = Modifier
      ) {
          Surface(
              shape = CircleShape,
              color = backgroundColor,
              modifier = modifier.size(50.dp)
          ) {
              Icon(imageVector = icon, tint = tint, ...)
          }
      }
      ```

### 2. Lists & Items
- **`ExpenseLogItem`** (`item_expense_log.xml`):
    - Usage: Displays a single expense entry with swipe-to-delete.
    - Implementation:
        - Use `SwipeToDismiss` (Material 2/3) or a custom implementation if specific "Check/Edit" actions are needed in the background layer.
        - Foreground: `Row` with `CategoryIcon`, `Column` (Title + Date), and `Row` (Amount + Currency).
- **`CategoryGridItem`** (`item_category.xml`):
    - Usage: Selectable category item in grids.
    - Implementation: `Card` with `clickable` modifier, handling selection state to change background tint.

### 3. Headers & Summaries
- **`IncomeOutcomeCard`** (`layout_expense_in_out.xml`):
    - Usage: Dashboard summary.
    - Implementation:
        - `Card` containing a `Column` (Title + Date).
        - `Row` with two `Column`s for Income and Outcome.
        - Use `CurrencyLarge` style for values.
- **`WeekGraphCard`** (`layout_expense_graph.xml`):
    - Usage: Wrapper for the graph.
    - Implementation: `Card` containing the Chart Composable (migrated `SpendGraph`) and a Title/Date header.

### 4. Inputs
- **`ProSearchBox`** (`layout_search_box.xml`):
    - Usage: Search input field.
    - Implementation:
      ```kotlin
      @Composable
      fun ProSearchBox(
          query: String,
          onQueryChange: (String) -> Unit,
          modifier: Modifier = Modifier
      ) {
          TextField(
              value = query,
              onValueChange = onQueryChange,
              leadingIcon = { Icon(Icons.Default.Search, ...) },
              shape = MaterialTheme.shapes.medium,
              colors = TextFieldDefaults.textFieldColors(
                  backgroundColor = Color.LightGray.copy(alpha = 0.2f),
                  focusedIndicatorColor = Color.Transparent,
                  unfocusedIndicatorColor = Color.Transparent
              ),
              modifier = modifier.fillMaxWidth().height(50.dp)
          )
      }
      ```

## 🏗 Architectural Changes

### Navigation
Migrate from `fragment_*.xml` layouts to full-screen Composables.
- `HomeFragment` -> `HomeScreen`
- `StatisticFragment` -> `StatisticScreen`
- `SettingsFragment` -> `SettingsScreen`
- **Dialogs**: Use `AlertDialog` or `Dialog` composables instead of separate custom views.

### Screen Layouts
- Replace `CoordinatorLayout` + `AppBarLayout` with `Scaffold`:
  ```kotlin
  Scaffold(
      topBar = {
          TopAppBar(
              title = { Text("Home") },
              navigationIcon = { IconButton(...) { ... } },
              elevation = 0.dp,
              backgroundColor = MaterialTheme.colors.surface
          )
      }
  ) { padding ->
      // Content (LazyColumn, etc.)
  }
  ```

## 🚀 Migration Steps
1.  **Foundation**: Create `Theme`, `Type`, `Color`, and `Shape` definitions in `:design-system`.
2.  **Atoms**: Build `CategoryIcon`, `ProSearchBox`.
3.  **Molecules**: Build `ExpenseLogItem`, `IncomeOutcomeCard`.
4.  **Organisms**: Build `WeekGraph` (porting `SpendGraph` logic).
5.  **Screens**: Assemble `HomeScreen` and `StatisticScreen`.
6.  **Navigation**: Integrate screens into the Compose Navigation Graph.
