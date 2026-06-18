package com.arduia.expense.ui.logging

import androidx.activity.ComponentActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onRoot
import com.arduia.expense.testing.ScreenshotTests
import com.arduia.expense.ui.budget.BudgetScreenContent
import com.arduia.expense.ui.budget.previewEvents
import com.arduia.expense.ui.theme.ProExpenseTheme
import com.github.takahirom.roborazzi.captureRoboImage
import org.junit.Rule
import org.junit.Test
import org.junit.experimental.categories.Category
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [33], qualifiers = "w427dp-h952dp")
@Category(ScreenshotTests::class)
class QuickLogScreenshotTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun add_amount_zero() {
        composeTestRule.setContent {
            ProExpenseTheme {
                Box(
                    Modifier
                        .fillMaxSize()
                        .background(ProExpenseTheme.colors.paper),
                ) {
                    AddAmountScreen(
                        amount = "",
                        selectedCategoryId = "food",
                        onAmountChange = {},
                        onCategorySelected = {},
                        onClose = {},
                        onSave = {},
                        onNext = {},
                    )
                }
            }
        }
        composeTestRule.onRoot().captureRoboImage()
    }

    @Test
    fun add_amount_typed() {
        composeTestRule.setContent {
            ProExpenseTheme {
                Box(
                    Modifier
                        .fillMaxSize()
                        .background(ProExpenseTheme.colors.paper),
                ) {
                    AddAmountScreen(
                        amount = "12.50",
                        selectedCategoryId = "food",
                        onAmountChange = {},
                        onCategorySelected = {},
                        onClose = {},
                        onSave = {},
                        onNext = {},
                    )
                }
            }
        }
        composeTestRule.onRoot().captureRoboImage()
    }

    @Test
    fun add_details_tagged() {
        composeTestRule.setContent {
            ProExpenseTheme {
                Box(
                    Modifier
                        .fillMaxSize()
                        .background(ProExpenseTheme.colors.paper),
                ) {
                    AddDetailsScreen(
                        amountLabel = "$12.50",
                        selectedCategoryId = "food",
                        note = "Lunch with M.",
                        dateLabel = "Today, May 25",
                        timeLabel = "12:30 PM",
                        eventTag = "Bali Trip",
                        onNoteChange = {},
                        onCategorySelected = {},
                        onBack = {},
                        onEditAmount = {},
                        onClearTag = {},
                        onSave = {},
                    )
                }
            }
        }
        composeTestRule.onRoot().captureRoboImage()
    }
}

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [33], qualifiers = "w427dp-h952dp")
@Category(ScreenshotTests::class)
class BudgetScreenScreenshotTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun budget_events_list() {
        composeTestRule.setContent {
            ProExpenseTheme {
                Box(
                    Modifier
                        .fillMaxSize()
                        .background(ProExpenseTheme.colors.paper),
                ) {
                    BudgetScreenContent(
                        events = previewEvents,
                        onNewEvent = {},
                    )
                }
            }
        }
        composeTestRule.onRoot().captureRoboImage()
    }
}
