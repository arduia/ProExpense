# Home Feature Documentation

## 1. User Story - Acceptance Criteria
**User Story**: As a user, I want to view a summary dashboard of my recent expenses and financial health so that I can quickly understand my current weekly income and outcome.

**Acceptance Criteria**:
- The dashboard displays the total income and total outcome for the current week.
- A weekly expense graph visualizes my financial data accurately.
- I can see a list of my most recent transactions.
- The recent transactions list hides itself if I haven't logged any expenses yet.
- I can tap a recent transaction to view its details, edit it, or delete it.
- I can easily navigate to the full Expense Logs screen to see more history.
- I can open the main menu using the navigation icon.

## 2. Input Fields and Validation
*   **No Direct Input Fields**: The Home screen is primarily a dashboard displaying summarized data (balance, graph, recent transactions). It does not contain direct text input fields with validation. 

## 3. Action Buttons
*   **Navigation Menu Icon (`Toolbar`)**: 
    *   **Enable Logic**: Always enabled.
    *   **Click Action**: Opens the lateral Navigation Drawer (`navigationDrawer.openDrawer()`).
*   **More Logs Button (`btnMoreLogs`)**:
    *   **Enable Logic**: Always enabled if the Recent Logs list is visible.
    *   **Click Action**: Navigates to the Expense Logs screen (`dest_expense_logs`).
*   **Recent Transaction Item**:
    *   **Enable Logic**: Always enabled.
    *   **Click Action**: Triggers `viewModel.selectItemForDetail(it)`, which fetches detailed data and opens the `ExpenseDetailDialog`.
*   **Detail Dialog - Edit Button**:
    *   **Enable Logic**: Always enabled when viewing details.
    *   **Click Action**: Navigates to the Add/Edit Expense Entry screen (`dest_expense_entry`), passing the selected expense ID.
*   **Detail Dialog - Delete Button**:
    *   **Enable Logic**: Conditionally enabled based on configuration (`isDeleteEnabled = true`).
    *   **Click Action**: Triggers `viewModel.onDeletePrepared(id)`, leading to the `DeleteConfirmFragment`.
*   **Delete Confirm Dialog - Confirm Button**:
    *   **Enable Logic**: Always enabled.
    *   **Click Action**: Triggers `viewModel.onDeleteConfirmed()`, deleting the record from the repository.

## 4. UI and ViewModel Interactions
*   `Fragment` observes `recentData`: Submits list to `RecentListAdapter`. Toggles visibility of the recent logs section if empty.
*   `Fragment` observes `graphUiModel`: Updates `ExpenseGraphAdapter` rates and the displayed Date Range text.
*   `Fragment` observes `incomeOutcomeData`: Updates labels for Income Value, Outcome Value, Currency Symbols, and Date Range.
*   `Fragment` observes `detailData`: Displays `ExpenseDetailDialog` upon successful data retrieval for a selected item.
*   `Fragment` observes `onDeleteConfirm`: Dismisses the Detail Dialog and shows the `DeleteConfirmFragment`.
*   `Fragment` observes `onExpenseItemDeleted`: Shows a "Item Deleted" snackbar message via the Activity Host.
*   `Fragment` triggers `viewModel.updateRecentData()` when `onResume()` is called to ensure data is fresh.

## 5. ViewModel Logic and Dependencies
*   **Dependencies**: 
    *   `ExpenseRepository`: Provides current week's expenses and recent expenses. Handles deletion.
    *   `CurrencyRepository`: Provides the user's selected/cached currency definition.
    *   `ExpenseRateCalculator`: Computes the rates/data points for the weekly graph based on expenses.
    *   `NumberFormat` (`@CurrencyDecimalFormat`), `DateRangeFormatter` (`@MonthlyDateRange`): DI formatting tools.
    *   `ExpenseUiModelMapperFactory`, `ExpenseDetailUiModelMapperFactory`: Maps domain models (`ExpenseEnt`) to UI models.
*   **Core Logic**:
    *   Calculates total income and total outcome asynchronously by filtering `ExpenseEnt` lists by `ExpenseCategory.INCOME`.
    *   Computes current week's start and end times dynamically utilizing `java.util.Calendar`.
    *   Combines `recentData` flows with `currencySymbol` flows to map objects seamlessly when either updates.

## 6. Required Test Scenarios
*   **Data Loading**: Verify that the Home screen displays the correct Total Income and Total Outcome for the current calendar week.
*   **Graph Rendering**: Verify that the Weekly Expense Graph plots data accurately corresponding to the week's expenses.
*   **Empty States**: Verify that the "Recent Logs" section is hidden when there are no recent transactions.
*   **Navigation**: 
    *   Verify tapping the Toolbar Menu icon opens the Navigation Drawer.
    *   Verify tapping "More" next to recent transactions navigates to the Expense Logs screen.
*   **Item Interaction**:
    *   Verify tapping a recent transaction row displays the `ExpenseDetailDialog`.
    *   Verify tapping "Edit" in the Detail Dialog navigates to the Entry form with the correct existing data.
    *   Verify tapping "Delete" invokes the confirmation dialog, and confirming it removes the item and shows a success Snackbar.
