# Web Feature Documentation

## 1. User Story - Acceptance Criteria
**User Story**: As a user, I want to open external web links seamlessly inside the app so that I don't lose context.

**Acceptance Criteria**:
- I can read web pages, such as privacy policies or specific links, quickly within a built-in browser view.
- I can see a progress indicator showing me when a page is loading.
- If I tap a link inside the web page that goes to a different domain, the app safely opens it in my device's native browser instead of navigating inside the app.
- I cannot accidentally invoke the side navigation menu while viewing a web page.
- I can easily return to the previous screen using a back button.

## 2. Input Fields and Validation
*   None. The feature is entirely parameterized via Jetpack Navigation Safe Args (`WebFragmentArgs`) receiving `url` and `title` variables passively.

## 3. Action Buttons
*   **Back Navigation Icon**:
    *   **Enable Logic**: Always enabled.
    *   **Click Action**: Invokes `findNavController().popBackStack()` routing the user up the graph tree.

## 4. UI and ViewModel Interactions
*   **ViewModel**: This feature is deliberately simple and lacks a dedicated ViewModel.
*   **WebView Loading Hooks**: 
    *   `onPageStarted` forces the indeterminate Linear Progress Indicator (Loader) to become visible.
    *   `onPageFinished` immediately toggles the Loader invisible.
*   **Navigation Override Constraint**: 
    *   Within `shouldOverrideUrlLoading`, if the user selects a hyperlink inside the web page, the feature compares the target URL against the originally passed URL. 
    *   If they differ, it explicitly aborts internal rendering and delegates the request to the OS native Browser via `Intent.ACTION_VIEW`.

## 5. ViewModel Logic and Dependencies
*   **Dependencies**: Relies directly on the core Android `WebView` engine.
*   **Security & Navigation Lock**: While the `WebFragment` is active, it calls `lockDrawer()` on the host Main Activity to explicitly prevent swipe-based activation of the lateral Navigation Drawer, ensuring focus remains on the web structure. It successfully unlocks the Drawer within `onDestroy`.

## 6. Required Test Scenarios
*   **Parameter Passing Validation**: Verify routing into the Web graph dynamically paints the Toolbar header with the provided Title and successfully loads the intended HTML document.
*   **Progress Indicator**: Verify the loader initiates synchronously with the page request and dissolves smoothly once the full HTML tree finishes rendering.
*   **Outbound Hyperlink Intercept**: Verify that clicking embedded links pointing to differing domains safely pushes an OS Intent routing the exact URI strings to Chrome (or the default system browser) rather than mutating the internal Web view constraint.
*   **Navigation Drawer Constraint**: Verify swiping aggressively from the left screen boundary fails to pull the `NavigationDrawer` open while the Web context is alive.
