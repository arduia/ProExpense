package com.arduia.expense.ui.more

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.arduia.expense.ui.design.ProExpensePaperBackground
import com.arduia.expense.ui.theme.ProExpenseTheme
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

@RunWith(AndroidJUnit4::class)
@Config(sdk = [33], qualifiers = "w414dp-h868dp")
class MoreClearDataScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private fun setScreen(
        initial: Set<ClearScope>,
        onConfirmClear: (Set<ClearScope>) -> Unit = {},
    ) {
        composeTestRule.setContent {
            var selected by remember { mutableStateOf(initial) }
            ProExpenseTheme {
                ProExpensePaperBackground(modifier = Modifier.fillMaxSize()) {
                    MoreClearDataScreen(
                        selected = selected,
                        onToggle = { scope ->
                            selected = if (scope in selected) selected - scope else selected + scope
                        },
                        onConfirmClear = onConfirmClear,
                        onBack = {},
                    )
                }
            }
        }
    }

    @Test
    fun clearData_confirmInvokesCallbackWithSelection() {
        var confirmed: Set<ClearScope>? = null
        setScreen(initial = setOf(ClearScope.Expenses), onConfirmClear = { confirmed = it })

        // Add a second scope, then trigger the destructive flow.
        composeTestRule.onNodeWithContentDescription("Events").performClick()
        composeTestRule.onNodeWithText("Clear selected…").performClick()

        composeTestRule.onNodeWithText("Clear selected data?").assertIsDisplayed()
        composeTestRule.onNodeWithText("Clear").performClick()

        assertEquals(setOf(ClearScope.Expenses, ClearScope.Events), confirmed)
    }

    @Test
    fun clearData_emptySelectionDoesNotOpenDialog() {
        var confirmed: Set<ClearScope>? = null
        setScreen(initial = emptySet(), onConfirmClear = { confirmed = it })

        composeTestRule.onNodeWithText("Clear selected…").performClick()

        composeTestRule.onNodeWithText("Clear selected data?").assertDoesNotExist()
        assertNull(confirmed)
    }
}
