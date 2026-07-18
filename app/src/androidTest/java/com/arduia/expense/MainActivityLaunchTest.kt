package com.arduia.expense

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onRoot
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.arduia.expense.ui.MainActivity
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Real-device smoke test — everything else in this suite runs on Robolectric, which never
 * exercises real SQLCipher native-lib loading, real Android Keystore, or real DI/database
 * bootstrapping (`ExpenseApplication.ensureStarted()`). This is the only test in the project that
 * launches the actual app on an actual device/emulator and confirms it renders instead of
 * crashing during startup.
 */
@RunWith(AndroidJUnit4::class)
class MainActivityLaunchTest {
    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun app_launchesAndRendersWithoutCrashing() {
        // Deliberately not asserting specific screen content (onboarding vs. PIN vs. Home depends
        // on fresh-install state) — the point of this test is startup survival, not screen choice.
        composeTestRule.onRoot().fetchSemanticsNode()
    }
}
