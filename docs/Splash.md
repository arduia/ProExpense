# Splash Feature Documentation

## 1. User Story - Acceptance Criteria
**User Story**: As a user, I want to see a branded splash screen when launching the application so that I know the app is starting up and loading.

**Acceptance Criteria**:
- I am greeted with a branded logo immediately upon launching the app.
- The splash screen remains on screen for at least a brief moment to present a polished experience.
- The system correctly identifies whether I am a brand new user or a returning user while the splash screen displays.
- If I am a new user, I am seamlessly transitioned to the onboarding flow.
- If I am a returning user, I am seamlessly taken straight to my main dashboard without seeing onboarding screens.

## 2. Input Fields and Validation
*   None. This module strictly handles intermediate background routing and presents a static splash branding logo.

## 3. Action Buttons
*   None. There are no interactive components blockading the timer workflow.

## 4. UI and ViewModel Interactions
*   `Fragment` observes `firstTimeEvent`: Executes a `findNavController().popBackStack()` to kill the Splash node, then pushes the user into the Onboarding subgraph (`dest_language`).
*   `Fragment` observes `normalUserEvent`: Analogously pops the back stack, routing existing returning users straight to the main Dashboard node (`dest_home`). 

## 5. ViewModel Logic and Dependencies
*   **Dependencies**: 
    *   `SettingsRepository`: Queries the data store for the `firstUser` persistent boolean flag.
*   **Core Logic (`SplashViewModel`)**:
    *   **Automated Lifecycle Boot**: Triggered natively during construction (`init`), a coroutine fetches the user type flag from disk (`awaitValueOrError`).
    *   **Artificial Delay**: Mandates a strict `delay(1000L)` coroutine suspension to ensure branding is visible to the user regardless of hardware speed.
    *   **Decision Post**: Emits the localized decision downward via isolated Event channels.
*   **Fragment-Level Logic**:
    *   **System UI Mutation**: Intercepts the core OS Context (`requireActivity().window.statusBarColor`), recoloring the notification shade purely blue upon creation (`changeSplashStatusBarColor`). 
    *   **System UI Restoration**: Captures the original color beforehand and defensively restores the OS aesthetic once the view tears down.

## 6. Required Test Scenarios
*   **System Level Branding**: Verify that anchoring onto the Splash map temporarily masks out the Android status bar matching the background blue. Ensure leaving the Fragment reverts the status bar back to standard White.
*   **Artificial Load Test**: Verify that launching the application enforces the visual branding logo to remain actively visible for approximately exactly 1 second minimum before yielding navigation.
*   **First Run Resolution**: Verify a fresh, clear-data installation of the application accurately drops the user off at the beginning of the Onboarding graph.
*   **Warm Boot Resolution**: Verify that re-opening the application after actively traversing through onboarding deposits the user cleanly onto the Home Dashboard.
