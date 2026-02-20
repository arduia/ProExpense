package com.arduia.expense.utils

import androidx.compose.ui.test.junit4.createComposeRule
import org.junit.Rule
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * Base test class for Jetpack Compose UI Tests and Molecule Presenters tests
 * using Robolectric.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [33], instrumentedPackages = ["androidx.loader.content"]) // Robolectric may require specifying an SDK supported by current test environment
abstract class BaseComposeTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()
}
