# Entry Feature Documentation

## 1. User Story - Acceptance Criteria
**User Story**: As a user, I want to add new expenses or edit existing ones so that my financial tracking stays up to date.

**Acceptance Criteria**:
- I can provide an expense name, amount, category, and optional note.
- The system enforces the amount field as required and shows an error if left blank.
- I can select a specific date and time for the transaction using native pickers.
- While adding a new entry, I can toggle a "Repeat Entry" mode to add multiple consecutive expenses quickly.
- When saving an entry, the system returns me to the previous screen (unless repeat mode is on).
- Editing an existing entry pre-fills my previously saved data correctly and lets me update it.

## 2. Input Fields and Validation
*   **Name Input (`edt_name`)**: Single line, max length 20 characters. Action "Next" jumps to Amount.
*   **Amount Input (`edt_amount`)**: Decimal Number type, max length 10 characters. Formatted with a `FloatingInputFilter`.
    *   **Validation**: Required field before saving/updating. If empty, displays an error text on the layout.
    *   **Decorator**: The `TextInputLayout` dynamically appends the User's currency symbol as a suffix.
*   **Category Input (`rv_category`)**: Custom horizontal list of Category blocks. Default category (Food) or the entity's existing category is pre-selected.
*   **Note Input (`edt_note`)**: Multi-line input (max 5 visible lines at once), max length 100 characters.

## 3. Action Buttons
*   **Close/Back Button (`Toolbar`)**:
    *   **Enable Logic**: Always enabled.
    *   **Click Action**: Discards changes and navigates back (`popBackStack()`).
*   **Calendar Menu Icon (`R.id.calendar`) / Time Menu Icon (`R.id.time`)**:
    *   **Enable Logic**: Always enabled.
    *   **Click Action**: Invokes the `viewModel.onDateSelect()`/`onTimeSelect()` resulting in native `DatePickerDialog` or `TimePickerDialog`.
*   **Save / Update Button (`btn_save`)**:
    *   **Enable Logic**: Always enabled but halts execution if Amount is empty.
    *   **Click Action**: Invokes `viewModel.saveExpenseData()` or `updateExpenseData()` gathering values from all input fields.
*   **Repeat Entry Switch (`switch_repeat`)**:
    *   **Enable Logic**: Visible only in INSERT (Save) mode.
    *   **Click Action**: Invokes `viewModel.setLockMode()`. When activated, changes the Save button text to "Next".

## 4. UI and ViewModel Interactions
*   `Fragment` observes `onCurrentModeChanged` (Mode `INSERT` or `UPDATE`): Mutates the UI configuration (Toolbar titles, Button labels, Repeat Switch visibility).
*   `Fragment` observes `entryData`: Populates all inputs with existing data when in `UPDATE` mode.
*   `Fragment` observes `currentEntryTime`: Formats the current timestamp into a human-readable date and sets it as the Toolbar Subtitle.
*   `Fragment` observes `onChooseDateShow` / `onChooseTimeShow`: Displays OS-level pickers to manipulate `currentEntryTime`.
*   `Fragment` observes `lockMode`: Forces changes to the "Repeat Entry" Switch and "Save/Next" button text for state persistence.
*   `Fragment` observes navigation events:
    *   `onDataInserted` -> Hides keyboard, pops back stack.
    *   `onNext` -> Empties Name/Amount/Note fields, focuses Name, selects first category, and shows a "Saved" Snackbar without leaving the Fragment.
    *   `onDataUpdated` -> Shows "Data Updated" Snackbar, pops back stack.

## 5. ViewModel Logic and Dependencies
*   **Dependencies**:
    *   `ExpenseRepository`: Handles CRUD for inserting and fetching specific `ExpenseEnt` objects.
    *   `CurrencyRepository`: Determines suffix symbol for the Amount input based on user settings.
    *   `Mapper<ExpenseEnt, ExpenseUpdateDataUiModel>`: Handles translation map from DB rows to editable UI variants.
*   **Core Logic**:
    *   **Mode Detection**: Examines ID argument (`expenseId`). Values `< 0` trigger `chooseSaveMode`. Values `>= 0` trigger `chooseUpdateMode` and fetch previous data.
    *   **Date/Time Manipulation**: Maintains a core internal Long (`_currentEntryTime`). Updating fields via native pickers correctly merges Hours/Mins into the Calendar instance tracking the single point in time. 
    *   **Repeat Logic Execution**: Upon `insertExpense` success, queries `_lockMode`. If `LOCKED`, informs the UI to clean inputs manually (`_onNext`). If `UNLOCK`, behaves standardly (`_onDataInserted`).

## 6. Required Test Scenarios
*   **Save Mode Initialization**: Verify entering Save mode shows "Expense Entry" title, "Save" button, and visible "Repeat Entry" switch.
*   **Validation Check**: Verify providing an empty Amount and clicking Save triggers the red "Empty Cost" error on the layout.
*   **Successful Default Entry**: Verify entering Name, Amount, navigating out saves data natively, returning the user to the previous screen.
*   **Repeat Entry Flow**: Verify toggling "Repeat Entry" changes the Primary Button to "Next". Clicking Next saves the record, resets text inputs, keeps focus on the form, and displays a "Saved" Snackbar.
*   **Update Mode Initialization**: Verify opening an existing log shows "Update Data" title, populates fields with historic values, hides the "Repeat Entry" switch, and changes the Button to "Update".
*   **Successful Update flow**: Verify modifying text/amounts and clicking Update commits the changes, displays an update verification Snackbar, and returns to the prior screen.
*   **Date/Time Override**: Verify using the top-right calendar/time icons effectively changes the explicit created date, dynamically verified in the Toolbar subtitle.
*   **Category Persistence**: Verify a newly selected Category bumps to the front of the horizontal scroll list upon view animations completing.
