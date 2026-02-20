# Onboarding Feature Documentation

## 1. User Story - Acceptance Criteria
**User Story**: As a new user, I want to set my preferred language and currency during my first launch so that the app is tailored to my locale immediately.

**Acceptance Criteria**:
- I am presented with a language selection screen on my first launch.
- I can search for a specific language and select it.
- After choosing a language, I can continue to a currency selection screen.
- I can search for a specific currency and select it.
- Completing the onboarding flow saves my preferences and seamlessly takes me to the main dashboard.
- If I close and reopen the app after completing onboarding, I am taken straight to the dashboard, skipping the onboarding screens.

## 2. Input Fields and Validation
*   **Search Input (`ChooseLanguageFragment` / `ChooseCurrencyFragment`)**:
    *   **Type**: Custom `SearchBox` component.
    *   **Validation**: Real-time filtering without explicit validation. Every character keystroke pushes a new search term to the ViewModel to filter the list via Flow combinations.

## 3. Action Buttons
*   **List Item Rows (Language / Currency)**:
    *   **Enable Logic**: Always enabled.
    *   **Click Action**: Invokes `viewModel.selectLang(item)` or `viewModel.selectCurrency(item)`, instantly persisting the user's choice to the physical `SettingsRepository`.
*   **Primary Action Button (`btnContinue`)**:
    *   **State 1 (Language Screen)**: 
        *   Text: "Continue". 
        *   Action: Invokes `viewModel.continued()`.
    *   **State 2 (Currency Screen)**: 
        *   Text: "Continue to Home" (handled via `onChooseCurrency()`). 
        *   Action: Invokes `viewModel.finishedConfig()`.

## 4. UI and ViewModel Interactions
*   `OnBoardingConfigFragment` observes `onContinued`: Pushes the core `ViewPager2` to item index `1` (Currency selection screen), morphs the Primary Action button's text, and re-attaches a new click listener.
*   `OnBoardingConfigFragment` observes `onRestart`: Performs an Activity Restart (`restartActivity()`), effectively closing the Onboarding node because the `firstUser` flag has been mutated.
*   `ChooseCurrencyFragment`/`ChooseLanguageFragment` observe `currencies`/`language`: Binds the continually updated list (factoring in active search operations) to the RecyclerView adapters. Manages visibility of the "No Item Found" placeholder.

## 5. ViewModel Logic and Dependencies
*   **Dependencies**:
    *   `SettingsRepository`: Controls global User preferences and lifecycle flags.
    *   `CurrencyRepository`: Provides the backend JSON/Room listing of valid global currencies.
*   **Core Logic (`OnBoardingConfigViewModel`)**:
    *   **Completion**: `finishedConfig()` safely writes `false` to the `settingRepo.setFirstUser()` flag on an IO thread and broadcasts the restart event.
*   **Core Logic (`ChooseCurrencyViewModel` / `ChooseLanguageViewModel`)**:
    *   **Reactive Filtering**: Uses `Flow.combine()` merging three distinct paths:
        1.  Base List flow (All Supported Currencies/Languages).
        2.  Search Key flow (Live Search query text).
        3.  Selected Identifier flow (Which one is Currently Saved).
    *   Maps each entry applying a visibility Boolean to the "Checkmark" icon dynamically based on the Selected flow. 

## 6. Required Test Scenarios
*   **Initial Boot Experience**: Verify opening the Onboarding flow naturally starts with the ViewPager frozen on Index 0 (Language Selection) and keyboard input adjusts to prevent layout resize shifts (`SOFT_INPUT_ADJUST_NOTHING`).
*   **List Interaction**: Verify tapping different item rows seamlessly updates the checkmark indicator without requiring manual UI refreshes.
*   **Search Constraint Filtration**: Verify entering text strings accurately narrows the displayed item list locally, ignoring case sensitivities. Verify entering garbage text effectively displays the empty state placeholder layout.
*   **Slide Interaction**: Verify pressing "Continue" forcefully transitions the ViewPager to Index 1 (Currency Config) and updates the Primary Button to read "Continue to Home" visually.
*   **Lifecycle Persistence**: Verify pressing "Continue to Home" accurately executes the backend database flag shift, ensuring the application permanently bypasses the Onboarding Navigation Graph during subsequent device reboots.
