package com.arduia.expense.ui.home

import android.content.Context
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createEmptyComposeRule
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.arduia.expense.R
import com.arduia.expense.launchFragmentInHiltContainer
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.HiltTestApplication
import org.robolectric.shadows.ShadowLooper
import io.mockk.mockk
import io.mockk.verify
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
@Config(application = HiltTestApplication::class, sdk = [33], instrumentedPackages = ["androidx.loader.content"])
class HomeFragmentTest {

    @get:Rule(order = 0)
    var hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createEmptyComposeRule()

    private lateinit var mockNavController: NavController
    private lateinit var context: Context

    @Before
    fun setup() {
        hiltRule.inject()
        context = ApplicationProvider.getApplicationContext()
        mockNavController = mockk(relaxed = true)
    }

    @Test
    fun homeFragment_launches_and_displays_home_title() {
        launchFragmentInHiltContainer<HomeFragment> {
            Navigation.setViewNavController(requireView(), mockNavController)
        }

        composeTestRule.onNodeWithText("Home", ignoreCase = true).assertIsDisplayed()
        composeTestRule.onNodeWithText("Totals", ignoreCase = true).assertIsDisplayed()
        composeTestRule.onNodeWithText("Expenses In This Week", ignoreCase = true).assertIsDisplayed()
    }

    @Test
    fun homeFragment_click_add_navigates_to_expense_entry() {
        launchFragmentInHiltContainer<HomeFragment> {
            Navigation.setViewNavController(requireView(), mockNavController)
        }

        // Wait for Compose to settle
        composeTestRule.waitForIdle()

        // Click on Add Expense FAB
        composeTestRule.onNodeWithContentDescription("Add Expense").performClick()

        // Wait for Coroutines / Looper
        composeTestRule.waitForIdle()
        ShadowLooper.idleMainLooper()

        // Verify the navigation interaction
        verify(timeout = 3000) {
            mockNavController.navigate(any<androidx.navigation.NavDirections>(), any<androidx.navigation.NavOptions>())
        }
    }
}