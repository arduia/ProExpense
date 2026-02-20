package com.arduia.expense.ui.backup

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.arduia.design.theme.ProExpenseTheme
import com.arduia.expense.ui.backup.molecule.BackupEvent
import com.arduia.expense.ui.backup.molecule.BackupState
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import androidx.test.ext.junit.runners.AndroidJUnit4

@RunWith(AndroidJUnit4::class)
class BackupScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun verifyUIElementsAreRendered() {
        val state = BackupState(isEmptyExpenseLogs = false, isEmptyBackupLogs = true)

        composeTestRule.setContent {
            ProExpenseTheme {
                BackupScreen(state = state, onEvent = {})
            }
        }

        composeTestRule.onNodeWithText("Export").assertIsDisplayed()
        composeTestRule.onNodeWithText("Import").assertIsDisplayed()
        composeTestRule.onNodeWithText("Backup Logs").assertIsDisplayed()
        composeTestRule.onNodeWithText("No Data").assertIsDisplayed()
    }

    @Test
    fun verifyClickingExportEmitsExportEvent() {
        var clicked = false
        val state = BackupState(isEmptyExpenseLogs = false)

        composeTestRule.setContent {
            ProExpenseTheme {
                BackupScreen(state = state, onEvent = { event ->
                    if (event is BackupEvent.ExportClicked) {
                        clicked = true
                    }
                })
            }
        }

        composeTestRule.onNodeWithText("Export").performClick()
        assertTrue(clicked)
    }
}
