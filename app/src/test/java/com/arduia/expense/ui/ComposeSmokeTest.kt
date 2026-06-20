package com.arduia.expense.ui

import androidx.compose.material3.Text
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import com.arduia.expense.testing.ComposeUiTests
import org.junit.Rule
import org.junit.Test
import org.junit.experimental.categories.Category
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [33])
@Category(ComposeUiTests::class)
class ComposeSmokeTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun composeUiTestRule_rendersContent() {
        composeTestRule.setContent {
            Text("Pro Expense")
        }
        composeTestRule.onNodeWithText("Pro Expense").assertExists()
    }
}
