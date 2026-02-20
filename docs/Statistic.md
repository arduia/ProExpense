# Statistic Feature Documentation

## 1. User Story - Acceptance Criteria
**User Story**: As a user, I want to see my expenses categorized and visualized by percentage so that I can track where I am spending the most money.

**Acceptance Criteria**:
- I can view a list of expense categories ordered by amount spent.
- I can see a progress bar and percentage for each category to understand its proportion of total expenses.
- I can filter the statistics by a specific date range if there is data available.
- If no data exists, a prominent "No Data" message is displayed and filters are disabled.

## 2. Input Fields and Validation
*   **No Direct Input Fields**: The Statistic screen is a dashboard dedicated to visualizing expenses grouped by category. There are no text input fields or form validations on this screen.

## 3. Action Buttons
*   **Navigation Menu Icon (`tb_statistic`)**:
    *   **Enable Logic**: Always enabled.
    *   **Click Action**: Opens the lateral Navigation Drawer (`navigationDrawer.openDrawer()`).
*   **Calendar Menu Icon (`Filter`)**:
    *   **Enable Logic**: Conditionally enabled based on the presence of expense data (`!isEmptyExpenseData`). If there are zero total expenses in the database, this button is disabled.
    *   **Click Action**: Triggers `viewModel.onFilterSelected()`, which calculates limits and posts to `onFilterShow` to open the `ExpenseFilterDialogFragment`.
*   **Filter Dialog - Apply Filter**:
    *   **Enable Logic**: Managed within the `ExpenseFilterDialogFragment`.
    *   **Click Action**: Passes the selected filter info to `viewModel.setFilter()`.

## 4. UI and ViewModel Interactions
*   `Fragment` observes `categoryStatisticList`: Triggers an update to the `CategoryStatisticListAdapter`. Also toggles the visibility of the "No Data" message based on whether the list is empty.
*   `Fragment` observes `onFilterShow`: Constructs and displays the `ExpenseFilterDialogFragment` with the provided configuration data.
*   `Fragment` observes `isEmptyExpenseData`: If true, disables the Toolbar menu actions (Calendar Filter) and hides the date range subtitle. If false, enables them.
*   `Fragment` conditionally registers/unregisters an observer on `dateRange` based on `isEmptyExpenseData` to update the Toolbar subtitle.

## 5. ViewModel Logic and Dependencies
*   **Dependencies**: 
    *   `ExpenseRepository`: Fetches total count, max/min date ranges, and fetches expenses by date range.
    *   `CategoryAnalyzer`: Domain logic class that accepts a list of expenses and outputs a calculated/analyzed list of `CategoryStatisticUiModel` objects.
    *   `DateRangeFormatter` (`@StatisticDateRange`): DI formatting tool for displaying the current filter constraint.
*   **Core Logic**:
    *   **Lifecycle**: `observeDateRangeInfo()` fetches the absolute minimum and maximum expense dates to set the `filterLimit` and default `filterConstraint`. `observeIsEmptyData()` constantly watches the total expense count.
    *   **Data Transformation**: The public `categoryStatisticList` leverages a `switchMap` on `filterConstraint`. Whenever the filter changes, it fetches `ExpenseEnt` from `ExpenseRepository.getExpenseRangeAsc()`, and hands them to the `CategoryAnalyzer` to return the updated UI models.

## 6. Required Test Scenarios
*   **Data Rendering**: Verify that the Category Statistics list correctly renders rows with category names, progress percentages, and correct progress bar lengths.
*   **Empty State (No Data in DB)**: Verify that when the database is entirely empty, the Calendar filter icon is disabled, the Date Range subtitle is hidden, and the "No Data" text is visible.
*   **Empty State (Filtered)**: Verify that when a date range is selected with no corresponding expenses, the "No Data" text is properly displayed.
*   **Filter Interaction**:
    *   Verify tapping the Calendar Filter icon opens the `ExpenseFilterDialogFragment`.
    *   Verify that applying a new Date Range accurately updates the category statistics list and displays the new date range in the Toolbar subtitle.
*   **Navigation**:
    *   Verify tapping the Toolbar Menu icon opens the Navigation Drawer.
