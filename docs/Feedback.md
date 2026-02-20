# Feedback Feature Documentation

## 1. User Story - Acceptance Criteria
**User Story**: As a user, I want to submit feedback or report issues so that I can help improve the application.

**Acceptance Criteria**:
- I can provide my name, an optional email address, and my comments.
- I cannot submit feedback if my comments field is empty.
- If I provide an email, it must be a valid email format.
- Submitting feedback queues a background task to send the data when I have an internet connection.
- After a successful submission, I see a confirmation dialog and can easily navigate back to the home screen.

## 2. Input Fields and Validation
*   **Name Input**: Single line generic text field. Modifies the internal `state.name`. Lacks restrictive validation constraints.
*   **Email Input**: Single line email-formatted text field. Modifies internal `state.email`.
    *   **Validation**: It is nominally optional. However, if text exists, it's evaluated natively against `Patterns.EMAIL_ADDRESS`. Malformed strings (e.g., `user@.com`) generate a red helper text bound to `state.emailErrorResId`.
*   **Comment Input**: Multi-line textbox capped structurally at 5 rows spanning 120dp.
    *   **Validation**: Strictly required. Form submission checks for emptiness; if true, assigns a red helper block bound to `state.commentErrorResId`.

## 3. Action Buttons
*   **Navigation Menu Icon (`TopAppBar`)**:
    *   **Enable Logic**: Always enabled.
    *   **Click Action**: Pops the lateral Navigation Drawer open.
*   **Send Feedback Button**:
    *   **Enable Logic**: Strictly controlled by `state.isSubmitEnabled`. Resolves to `true` dynamically as long as the Comment is hydrated AND the Email block is either fully blank or correctly formatted.
    *   **Click Action**: Retracts the user's OS keyboard (via `LocalSoftwareKeyboardController`), then delegates the heavy lifting to the Molecule Presenter via `FeedbackEvent.SubmitFeedback`.
*   **FeedbackStatusDialog - Go Home Button**:
    *   **Enable Logic**: Always enabled once the modal is visible.
    *   **Click Action**: Uses Jetpack Navigation (`findNavController().navigate(R.id.dest_home)`) to shift the execution context entirely out of the feature map onto the main dashboard.
*   **FeedbackStatusDialog - Top-Right Close Icon**:
    *   **Enable Logic**: Always enabled.
    *   **Click Action**: Dual-fires `DismissSuccessDialog` onto the ViewModel and simultaneously triggers the Go Home visual exit node.

## 4. UI and ViewModel Interactions
*   Entire feature hinges on Compose + CashApp Molecule. `FeedbackFragment` observes `state.collectAsState()` passing the massive chunk to `FeedbackScreen`.
*   `FeedbackScreen` continuously polls `state.isSuccessDialogVisible`. When evaluated `true`, an interactive `ModalBottomSheet` envelops the display bearing the positive reinforcement UI (`FeedbackStatusDialogScreen`).

## 5. ViewModel Logic and Dependencies
*   **Dependencies**: 
    *   `androidx.work.WorkManager`: Replaces conventional API calls or repository insertions tying heavy operations strictly to native OS job queues.
*   **Core Logic (`FeedbackPresenter`)**:
    *   **State Reducer**: Collects events on a unified `Flow`. Keystrokes (`UpdateEmail`, `UpdateComment`) immediately clear lingering `ErrorResId` blocks to produce a reactive feeling.
    *   **Submission Interceptor**: The `SubmitFeedback` command performs the final hard-trim validations. Upon success, bundles the text chunks into a `Data.Builder()` payload. Constructs a `OneTimeWorkRequest` constraining `FeedbackWorker` exclusively to `NetworkType.CONNECTED`. Immediately wipes local composable variables and toggles the Success Modal flag post-emission.

## 6. Required Test Scenarios
*   **Disabled Form Button**: Verify initializing the fragment presents a correctly disabled "Send Feedback" button, unresponsive to tapping due to the empty required Comment field.
*   **Email Strict Regex Filter**: Verify padding the optional email field with non-standard formations dynamically disables the Send block until rectified or completely emptied.
*   **Successful Worker Deferment**: Verify filling proper combinations successfully queues the generic Worker. Ensure all text boxes visually wipe themselves blank upon emitting the Job toward the OS.
*   **Asynchronous Completion Dialog**: Verify queuing the background Feedback intent successfully toggles the native Android Bottom Sheet bearing the checkmark hero graphic.  
*   **Escape Route Navigation**: Verify manipulating internal dismiss triggers built into the Modal Bottom Sheet forcefully evacuates the Fragment pushing the application structure safely back to `dest_home`.
