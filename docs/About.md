# About Feature Documentation

## 1. User Story - Acceptance Criteria
**User Story**: As a user, I want to view information about the application so that I can find support, view policies, and check for updates.

**Acceptance Criteria**:
- I can see the current version number of the app I have installed.
- I can read the Privacy Policy and Open Source Licenses via an internal web browser.
- I can easily navigate to the project's GitHub repository to contribute or view the code.
- I can tap to quickly send an email to the developer for feedback or support.
- If a new version of the app is available, I see a prominent update alert letting me install it.

## 2. Input Fields and Validation
*   None. The screen strictly acts as a read-only metadata display and routing hub for external requests.

## 3. Action Buttons
*   **Navigation Menu Icon**:
    *   **Enable Logic**: Always enabled.
    *   **Click Action**: Opens the lateral Navigation Drawer.
*   **Privacy Policy Row**:
    *   **Click Action**: Invokes Jetpack Navigation component (`findNavController().navigate`), routing to the `WebFragment` with bundled `url` and `title` arguments.
*   **Open Source Libraries Row**:
    *   **Click Action**: Similarly navigates internally to the `WebFragment` with corresponding open-source arguments.
*   **Contribute App Row**:
    *   **Click Action**: Invokes an implicit Intent (`ACTION_VIEW`) to route the user out of the app directly to the official Github repository URL.
*   **Developer Row**:
    *   **Click Action**: Invokes an implicit Intent (`ACTION_SENDTO`) configured with the `mailto:` URI to launch native email clients with a prefilled recipient.
*   **New Update Available Row**:
    *   **Enable Logic**: Visible and enabled only if `state.isNewVersionAvailable` evaluates to true via `SettingsRepository` synchronizations.
    *   **Click Action**: Posts `AboutEvent.OpenUpdateInfo` to the Molecule presenter.
*   **Update Dialog - Install Button (`btnInstall`)**:
    *   **Click Action**: Triggers `VersionUpdateUtil.openAppStoreLink` pushing the Google Play intent to the OS.
*   **Update Dialog - Cancel Button (`btnCancel`)**:
    *   **Click Action**: Posts `AboutEvent.ClearUpdateInfo` to the presenter, effectively dismissing the UI.

## 4. UI and ViewModel Interactions
*   The UI is entirely Compose-driven (`AboutScreen`). Rather than traditional LiveData, this fragment delegates states mapped by CashApp's Molecule.
*   `Fragment` observes Molecule's `state.collectAsState()`:
    *   Checks `state.isNewVersionAvailable` to conditionally assemble the Update list item.
    *   Monitors `state.updateInfo`; when populated (non-null), the `AboutUpdateDialog` composable is built and anchored over the scaffold.

## 5. ViewModel Logic and Dependencies
*   **Dependencies**:
    *   `SettingsRepository`: Provides the current release update state locally.
    *   `AboutPresenter`: Houses the pure Compose state logic.
*   **Core Logic (`AboutPresenter`)**:
    *   **Startup Monitoring**: Kicks off a `LaunchedEffect` coroutine collecting `getUpdateStatus()`. Emits `true` to `isNewVersionAvailable` if a normal update or force upgrade is flagged.
    *   **Action Handling**: Reacts directly to `Flow<AboutEvent>`. `OpenUpdateInfo` fetches complex DB DTOs and maps them to UI models (`updateInfo`). `ClearUpdateInfo` nullifies the snapshot variable forcing a cleanup recomposition.

## 6. Required Test Scenarios
*   **Version Identification**: Verify the screen header accurately reflects the natively derived OS `versionName` string upon loading.
*   **External Linking Validation**: Verify clicking Contribute App resolves gracefully switching to a browser/Github app without crashing or malforming the URL.
*   **Email Intent Formulation**: Verify clicking the Developer block launches an Email Chooser intent, specifically capturing the developer's address inside the standard "To:" field natively.
*   **Internal Web Routing**: Verify clicking Privacy Policy successfully utilizes Jetpack Navigation to transition onto the `WebScreen`, verifying the WebView URL matches expectations.
*   **Update Detection Sequence**: Verify that mocking a "Normal Update" DB entry successfully unhides the specific Update menu row. Ensure clicking it shows the Alert Dialog containing dynamic changelogs.
*   **Dialog Dismissal**: Verify pressing "Cancel" on the Update Alert securely resets the presenter state back to null, dissolving the modal window.
