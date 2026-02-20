# Backup Feature Documentation

## 1. User Story - Acceptance Criteria
**User Story**: As a user, I want to back up and restore my expense data so that I don't lose my financial records if I change devices or uninstall the app.

**Acceptance Criteria**:
- I can export my current expense data to an Excel file with a custom name.
- I cannot export data if my database is currently empty.
- I can import my previously exported Excel files back into the application.
- I can view a history of my past backup/export actions.
- I can delete historical backup records from the list.

## 2. Input Fields and Validation
*   **Export File Name (`ExportDialogFragment` - `edt_name`)**: A text input to assign a name to the generated backup file. It is pre-filled via `ExportViewModel` and lacks strict character constraints prior to launching the OS file browser.
*   **Import Details (`ImportDialogFragment`)**: Read-only text views displaying the chosen file name (`tvNameValue`) and parsed item count (`tvItemsValue`).

## 3. Action Buttons
*   **Navigation Menu Icon (`toolbar`)**:
    *   **Enable Logic**: Always enabled.
    *   **Click Action**: Opens the lateral Navigation Drawer.
*   **Export Card Button (`cvExport`)**:
    *   **Enable Logic**: Enabled strictly when the user has at least 1 expense logged in the system (`!isEmptyExpenseLogs`).
    *   **Click Action**: Invokes the `ExportDialogFragment`.
*   **Import Card Button (`cvImport`)**:
    *   **Enable Logic**: Always enabled.
    *   **Click Action**: Invokes the implicit Intent `ACTION_OPEN_DOCUMENT` restricting MIME type strictly to Excel formats to allow picking a backup file.
*   **Backup Log Item - Delete Icon**:
    *   **Enable Logic**: Always enabled.
    *   **Click Action**: Constructs a `DeleteConfirmFragment` to prompt the user. Affirmation calls `viewModel.onBackupDeleteConfirmed(item)`.
*   **Export Dialog - Export Now Button (`btnExportNow`)**:
    *   **Enable Logic**: Always enabled.
    *   **Click Action**: Invokes `ACTION_CREATE_DOCUMENT` Intent pushing the desired filename to Android's Storage Access Framework.
*   **Import Dialog - Import Button (`btnImport`)**:
    *   **Enable Logic**: Disabled programmatically during `loadingEvent` emissions.
    *   **Click Action**: Invokes `viewModel.startImportData()`.

## 4. UI and ViewModel Interactions
*   `Fragment` observes `backupList`: Refreshes the `BackupListAdapter` with historical backup metadata UI models.
*   `Fragment` observes `isEmptyBackupLogs`: Manipulates the visibility of the "No Data" placeholder message.
*   `Fragment` observes `isEmptyExpenseLogs`: Enables/Disables the physical Export Card button.
*   `Fragment` observes `backupFilePath`: Fired after a successful `onActivityResult` from the Storage Framework; receives the target `Uri` and orchestrates the opening of the `ImportDialogFragment`.
*   `ImportDialogFragment` observes `backupTaskEvent`: Forwards the ID to the `MainActivity` (acting as `BackupMessageReceiver`) to handle background tracking updates.

## 5. ViewModel Logic and Dependencies
*   **Dependencies**: 
    *   `BackupRepository`: Interacts with historical backup entries.
    *   `ExpenseRepository`: Validates existence of logs to permit exporting.
*   **Core Logic**:
    *   **Polling**: Constantly monitors total expense counts to disable features on the fly. Continually emits fresh lists of Backups.
    *   **Dialog Orchestration**: Sub-ViewModels exist specifically to govern logic wrapped inside their respective BottomSheets (`ExportViewModel`, `ImportViewModel`). 

## 6. Required Test Scenarios
*   **Empty DB Export Deny**: Verify that starting the application with zero expenses physically disables the Export Action block, preventing interactions.
*   **Export Workflow Constraint**: Verify clicking "Export Now" effectively launches the OS-level storage selector with the prefilled `.xls` target. 
*   **Import Dialog Construction**: Verify selecting a valid Excel file by clicking Import Data processes the Uri correctly, launching the Import Dialog presenting the file title and aggregate transaction count effectively.
*   **Import Data Launch**: Verify that pressing Import within the dialog throws the background processing event out to `MainActivity`, hides interactive buttons, and displays a circular progress UI.
*   **Removal of Historical Records**: Verify that targeting the delete icon inside the Backup list prompts a warning dialog, and confirming extinguishes the visual row and underlying database record synchronously.
