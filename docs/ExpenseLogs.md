# Expense Logs Feature Documentation

## 1. User Story - Acceptance Criteria
**User Story**: As a user, I want to view, manage, and delete my expense history so that I can maintain an accurate log of my financial transactions.

**Acceptance Criteria**:
- I can view a paginated list of all my logged transactions.
- I can filter the logs by date range to find specific transactions.
- I can swipe on an individual log entry to quickly delete it.
- I can enter a "Selection Mode" to delete multiple transactions at once.
- The UI handles states smoothly when there are no logs to display by showing a "No Data" placeholder.
- I can select any log to view and edit its rich details.

## 2. Input Fields and Validation
*   **No Direct Input Fields**: This screen displays a paginated list of expense transactions. There are no form input fields for text or validation rules directly on the screen. Data filtering is handled via a dialog.

## 3. Action Buttons
*   **Navigation / Contextual Action Icon (`tb_expense` NavigationIcon)**:
    *   **Normal Mode**: Shows Menu icon (`ic_menu`). **Click Action**: Opens lateral Navigation Drawer.
    *   **Selection Mode**: Shows Back icon (`ic_back`). **Click Action**: Clears selection state (`viewModel.clearState()`) and returns to Normal Mode.
*   **Filter Menu Icon (`R.id.filter`)**:
    *   **Enable Logic**: Visible only in Normal Mode. Disabled if `isEmptyExpenseCount == true` (no items in DB).
    *   **Click Action**: Triggers `viewModel.onFilterPrepare()` -> opens `ExpenseFilterDialogFragment`.
*   **Delete Menu Icon (`R.id.delete`)**:
    *   **Enable Logic**: Visible only in Selection Mode.
    *   **Click Action**: Triggers `viewModel.onDeletePrepared()` -> shows batch `DeleteConfirmFragment`.
*   **Expense Log Item (Row/Adapter)**:
    *   **Click Action**: Triggers `viewModel.onShowItemDetail(item)` -> opens `ExpenseDetailDialog`.
    *   **Swipe/Delete Action**: Triggers `viewModel.onSingleDeletePrepared(id)`.
*   **Detail Dialog Actions**:
    *   **Edit Button Click**: Navigates to Expense Entry screen (`dest_expense_entry`).
    *   **Delete Button Click**: Triggers `viewModel.onSingleDeletePrepared(id)` -> shows single `DeleteConfirmFragment`.

## 4. UI and ViewModel Interactions
*   `Fragment` observes `expenseLogMode`: Dictates Toolbar modifications (Icons, Title strings, visible Menu items).
    *   `ExpenseMode.NORMAL`: Default UI configuration.
    *   `ExpenseMode.SELECTION`: Hide filter, show delete, Toolbar title changes to "X Selected".
*   `Fragment` observes `expenseList` (`PagedList`): Submits the list to `ExpenseLogAdapter`.
*   `Fragment` observes `isCurrentListEmpty`: Triggers "Empty State" UI visibility (`layout_no_data`) and unregisters Toolbar subtitle if true.
*   `Fragment` observes `onRestoreSwipeState`: Re-applies the multi-item selection state after configuration changes (like rotation).
*   `Fragment` handles dialog prompts via Single (`onSingleDeleteConfirm`) and Multi (`onMultiDeleteConfirm`) delete event LiveData.

## 5. ViewModel Logic and Dependencies
*   **Dependencies**: 
    *   `ExpenseRepository`: Provides PagingSource arrays for Descending/Ascending items, fetches date boundaries, and processes deletion.
    *   `CurrencyRepository`: Provides current user currency symbol formatting.
    *   `ExpenseEntToLogVoMapperFactory`, `ExpenseDetailUiModelMapperFactory`: Factory classes to map Data Entities (`ExpenseEnt`) to UI state models (`ExpenseLogUiModel`, `ExpenseDetailUiModel`).
*   **Core Logic**:
    *   **Pagination Layer**: Exposes `expenseList` via `createSourcePagingLiveData()`, automatically configured to trigger fetch calls depending on scroll position. The source relies on a `.switchMap(filterConstraint)`, automatically generating fresh UI items whenever users apply a new date range filter.
    *   **Selection State System**: Maintains `swipeStateHolder` that tracks exactly which items are locked/selected via right-swiping. Calling `onSwipeStateChanged()` calculates selected counts, automatically switching `_expenseLogMode` when count > 0.

## 6. Required Test Scenarios
*   **Initial Load & Pagination**: Verify the screen loads a list of transactions successfully, correctly paginating (loading more items) as the user scrolls downwards.
*   **Empty State**: Verify the "No Data" placeholder properly appears when there are no transactions (or the filter returns 0 records), and the toolbar filters are disabled.
*   **Filter Logic**:
    *   Verify checking the Filter icon opens the Dialog.
    *   Verify applying a new date limit reflects only corresponding records in the updated list view, alongside the new Date range set as the Toolbar subtitle.
*   **Selection Mode**:
    *   Verify swiping a transaction enables "Selection Mode." Toolbar title should reflect selection count, Menu icon converts to 'Back', Filter disappears, Delete appears.
    *   Verify tapping the 'Back' icon immediately clears all items natively and terminates Selection Mode.
*   **Deletion Actions**:
    *   Verify single deletion through the Log Item Swipe action correctly removes the item from the DB.
    *   Verify Batch Deletion processes selected items effectively after clicking "Delete" on the Toolbar in Selection Mode and accepting the prompt.
*   **Detail Interactions**: Verify tapping an item invokes the correct, detailed Dialog. Verify jumping to the 'Edit' phase passes the correct arguments to the Entry module.
