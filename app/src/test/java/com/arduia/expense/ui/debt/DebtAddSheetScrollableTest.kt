package com.arduia.expense.ui.debt

import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.unit.dp
import com.arduia.expense.feature.debt.ui.DebtAddSheetContent
import com.arduia.expense.feature.debt.ui.preview.previewDebtAddLent
import com.arduia.expense.ui.theme.ProArtboard
import com.arduia.expense.ui.theme.ProExpenseTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

/**
 * Bugfix guard: unlike Category's sheet (fixed header/footer around a scrollable middle),
 * DebtAddSheetContent is one plain Column wrapped in verticalScroll() end to end, on the
 * assumption that ProBottomSheet's content Box always hands it a bounded max height (via
 * `Modifier.weight(1f, fill = fullHeight)`) so the scroll actually clips instead of the Column
 * just growing past the sheet — this proves that assumption holds, and that a tight budget (an
 * open keyboard eating most of the screen) still lets every field, including Save, be reached by
 * scrolling instead of leaving it off-screen with no way back.
 *
 * A directly height-constrained parent stands in for "not enough room" here rather than a real
 * keyboard/IME — Robolectric doesn't drive WindowInsets.ime (see CategoryNewSheetScrollableTest).
 */
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(
    sdk = [33],
    qualifiers = "w${ProArtboard.PIXEL_9_PRO_WIDTH_DP}dp-h${ProArtboard.PIXEL_9_PRO_HEIGHT_DP}dp",
)
class DebtAddSheetScrollableTest {
    @get:Rule
    val rule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun constrainedHeight_topFieldVisibleAndSaveReachableByScrolling() {
        rule.setContent {
            ProExpenseTheme {
                Box(Modifier.fillMaxWidth().height(300.dp)) {
                    DebtAddSheetContent(
                        form = previewDebtAddLent,
                        onSideSelected = {},
                        onPersonChange = {},
                        onAmountChange = {},
                        onPickDate = {},
                        onPickDue = {},
                        onNoteChange = {},
                        onRecordAsTransactionChange = {},
                        onSave = {},
                    )
                }
            }
        }

        // The scroll starts at the top — the Person field label is visible without scrolling.
        rule.onNodeWithText("PERSON").assertIsDisplayed()
        // Save is below the fold in this constrained budget, but still reachable by scrolling —
        // not clipped off with no way to scroll to it.
        rule.onNodeWithText("Save record").performScrollTo().assertIsDisplayed()
    }
}
