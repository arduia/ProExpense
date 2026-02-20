# Settings Feature Documentation

## 1. User Story - Acceptance Criteria
**User Story**: As a user, I want to configure the app's preferences such as language, currency, and theme so that I can personalize my experience.

**Acceptance Criteria**:
- I can change the app's language, and the app prompts me to restart to apply changes.
- I can search for and select my preferred currency, which updates instantly.
- I can switch the application theme between Light, Dark, and System Default.
- The settings screen remembers and displays my current active configurations clearly.
- I can easily navigate back or open the side menu.

## 2. Input Fields and Validation
*   **Settings Screen**: Contains no direct text input fields. It is a configuration dashboard built with clickable list items.
*   **Search Inputs (`ChooseCurrencyDialog`, `ChooseLanguageDialog`)**:
    *   **Type**: Custom `SearchBox` view.
    *   **Validation**: No rigid validation blocks saving. Used strictly for filtering the active lists (`viewModel.searchCurrency()`, `viewModel.searchLang()`).
*   **Theme Selection (`ChooseThemeDialog`)**:
    *   **Type**: A RadioGroup (`rgTheme`) displaying three predefined toggle states: Dark Theme, Light Theme, System Default.

## 3. Action Buttons
*   **Navigation Menu Icon (`tb_settings`)**:
    *   **Enable Logic**: Always enabled.
    *   **Click Action**: Opens lateral Navigation Drawer.
*   **Language Row (`flLanguage`)**:
    *   **Click Action**: Dismisses active currency dialogs and opens the `ChooseLanguageDialog`.
*   **Currency Row (`flCurrency`)**:
    *   **Click Action**: Dismisses active currency dialogs and opens the `ChooseCurrencyDialog`.
*   **Theme Mode Row (`flTheme`)**:
    *   **Click Action**: Triggers `viewModel.chooseTheme()` to fetch current setting state.
*   **ChooseLanguageDialog - Restart Button (`btnRestart`)**:
    *   **Enable Logic**: Enabled only when `isRestartEnable` is true (a new language is selected).
    *   **Click Action**: Triggers `viewModel.onRestart()`.
*   **ChooseThemeDialog - Restart/Save Button (`BUTTON_POSITIVE`)**:
    *   **Enable Logic**: Always enabled.
    *   **Click Action**: Triggers `viewModel.setThemeMode()` with the currently selected `currentThemeMode` ID.

## 4. UI and ViewModel Interactions
*   `Fragment` observes `selectedLanguage`: Fetches corresponding language UI data (`LanguageProvider.getLanguageVtoByID()`) and binds the appropriate country flag to the Language list item.
*   `Fragment` observes `currencyValue`: Applies the retrieved currency symbol directly to the Currency list item text view.
*   `Fragment` observes `onThemeOpenToChange`: Constructs the native `ChooseThemeDialog` Alert, passes the current int-based mode value (`mode`), sets listeners, and displays it.
*   `Fragment` observes `onThemeChanged`: Executes an Activity Restart (`restartActivity()`) forcing the Android OS to reload Context with the new Theme constraints.
*   (`ChooseLanguageDialog`) observes `onRestartAndDismiss`: Initiates dismissal and sets internal flag to execute `restartActivity()` when complete.

## 5. ViewModel Logic and Dependencies
*   **Dependencies**:
    *   `SettingsRepository`: Provides access to user preferences: Language Cache, Theme Mode cache, and Software Update status.
    *   `CurrencyRepository`: Provides access to the currently chosen currency format.
    *   `LanguageProvider`: Maps string language identifiers to UI data like localized Strings and Flag resources.
*   **Core Logic**:
    *   **Data Binding Streams**: Subscribes to backend flows on init (`observeSelectedLanguage`, `observeSelectedCurrency`). 
    *   **Action Flow (Theme)**: `chooseTheme()` asynchronously queries the Repo for the user's saved preference `Int`. `setThemeMode()` writes to `SettingsRepository` and broadcasts success so the view can initiate a context restart.
    *   **Background Polling**: Silently initializes `observeVersionStatus` checking for new software iterations directly from the repository, exposing the boolean downstream.

## 6. Required Test Scenarios
*   **UI Correctness**: Verify the list correctly displays the current chosen Language flag, Currency Symbol, and Theme mode upon entering.
*   **Language Selection**:
    *   Verify tapping the language row opens the Bottom Sheet.
    *   Verify the search bar filters countries correctly.
    *   Verify selecting a new language enables the "Restart" button, and clicking it restarts the application, successfully translating the UI texts.
*   **Currency Selection**:
    *   Verify tapping the currency row opens the Bottom Sheet.
    *   Verify selecting a new currency symbol successfully updates the value on the main Settings screen without a restart.
*   **Theme Switcher**:
    *   Verify tapping the Theme item opens the Alert Dialog and correctly pre-selects the system's current applied option.
    *   Verify changing the radio button and clicking "Restart" effectively flips the application into Light/Dark mode universally.
*   **Navigation**: Verify tapping the Toolbar Hamburger icon successfully toggles the lateral Navigation Drawer.
