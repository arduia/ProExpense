# Pro Expense Design System Components

This document details the reusable Jetpack Compose components created in the `:design-system` module. These components are designed to replace the existing XML layouts and ensure consistency across the application.

## 🧱 Atomic Components

### 1. StandardCard (`StandardCard.kt`)
- **Description**: A base card component that enforces consistent shape (`MaterialTheme.shapes.medium`) and elevation (`0.dp` or custom).
- **Usage**: Use as a container for most content blocks.
- **Parameters**: `modifier`, `shape`, `containerColor`, `elevation`, `content`.

### 2. CategoryIcon (`CategoryIcon.kt`)
- **Description**: Displays an icon (either `Painter` or `ImageVector`) on a circular background. Used heavily for expense categories.
- **Usage**: In expense lists, detail dialogs, and category grids.
- **Parameters**: `painter`/`imageVector`, `backgroundColor`, `iconTint`, `size` (default 50.dp), `iconSize`.

### 3. ProSearchBox (`ProSearchBox.kt`)
- **Description**: A custom-styled search input field with no underline indicator and a rounded background.
- **Usage**: In dialogs (e.g., Language Picker) or search screens.
- **Parameters**: `query`, `onQueryChange`, `placeholder`, `leadingIcon`.

### 4. ProExpenseButton (`ProExpenseButton.kt` - *Pre-existing*)
- **Description**: Standard button with app styling.

### 5. ProExpenseTextField (`ProExpenseTextField.kt` - *Pre-existing*)
- **Description**: Standard text input field.

## 🧬 Molecular Components

### 1. ExpenseLogItem (`ExpenseLogItem.kt`)
- **Description**: Represents a single expense entry in a list. Displays category icon, title, date, and formatted amount.
- **Usage**: Main Home screen list, recent expenses list.
- **Parameters**: `title`, `date`, `amount`, `currencySymbol`, `categoryIcon` (resId), `categoryColor`, `onItemClick`.

### 2. IncomeOutcomeCard (`IncomeOutcomeCard.kt`)
- **Description**: A dashboard summary card showing total Income vs Outcome for a period.
- **Usage**: Top of the Home screen.
- **Parameters**: `dateRange`, `incomeAmount`, `incomeSymbol`, `outcomeAmount`, `outcomeSymbol`.

### 3. CategoryGridItem (`CategoryGridItem.kt`)
- **Description**: A selectable square card representing a category. Background tint changes on selection.
- **Usage**: Category selection grids (Expense Entry, Filtering).
- **Parameters**: `title`, `icon` (resId), `isSelected`, `onCategorySelected`.

### 4. CategoryStatisticItem (`CategoryStatisticItem.kt`)
- **Description**: Displays a category's spending percentage with a progress bar.
- **Usage**: Statistics screen.
- **Parameters**: `categoryName`, `percentage` (0.0-1.0), `percentageText`.

### 5. BackupItem (`BackupItem.kt`)
- **Description**: Represents a backup file entry with name, date, item count, and delete action.
- **Usage**: Backup & Restore screen.
- **Parameters**: `name`, `date`, `itemCount`, `isSyncing`, `onItemClick`, `onDeleteClick`.

### 6. SettingsItem (`SettingsItem.kt`)
- **Description**: A generic row for settings options, supporting a title, optional value text, leading icon, chevron, and click action.
- **Usage**: Settings screen, About screen.
- **Parameters**: `title`, `value`, `icon` (resId), `showChevron`, `onClick`, `showDivider`, `trailingElement`.

### 7. WeekGraphCard (`WeekGraphCard.kt`)
- **Description**: A summary card showing spending bars for each day of the week.
- **Usage**: Dashboard / Home screen.
- **Parameters**: `dateRange`, `days` (list of strings), `values` (list of floats 0.0-1.0).

### 8. SelectionItem (`SelectionItem.kt`)
- **Description**: A generic selectable row with a checkmark for the active item.
- **Usage**: Language picker, Currency picker dialogs.
- **Parameters**: `title`, `icon` (resId, optional), `isSelected`, `onItemSelected`.

### 9. DateHeader (`DateHeader.kt`)
- **Description**: A simple text header used to group list items by date.
- **Usage**: Expense lists using sticky headers.
- **Parameters**: `date`.

## 📄 Utility Components

### 1. EmptyState (`EmptyState.kt`)
- **Description**: A full-size placeholder showing an icon and message when no data is available.
- **Usage**: Empty lists, search results.
- **Parameters**: `message` (resId), `icon` (resId).

## ⚠️ Action Items (Resource Migration)
Some components (`BackupItem`, `SettingsItem`, `EmptyState`) reference drawable (e.g., `ic_delete`, `ic_theme`) and string resources that currently reside in the `:app` module.
**Next Step**: Move common resources from `:app` to `:design-system` to ensure these components compile and preview correctly.
