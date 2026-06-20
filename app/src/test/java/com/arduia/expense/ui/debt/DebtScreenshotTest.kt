package com.arduia.expense.ui.debt

import androidx.activity.ComponentActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.text.AnnotatedString
import com.arduia.expense.R
import com.arduia.expense.testing.ScreenshotTests
import com.arduia.expense.ui.design.ProAlertDialog
import com.arduia.expense.ui.design.ProBottomSheetHost
import com.arduia.expense.ui.design.ProButtonVariant
import com.arduia.expense.ui.design.ProIconGlyph
import com.arduia.expense.ui.preview.previewDebtAddLent
import com.arduia.expense.ui.preview.previewDebtLent
import com.arduia.expense.ui.preview.previewDebtLentDetail
import com.arduia.expense.ui.preview.previewDebtOwe
import com.arduia.expense.ui.preview.previewDebtOweDetail
import com.arduia.expense.ui.preview.previewDebtSettled
import com.arduia.expense.ui.theme.ProArtboard
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
@Config(
    sdk = [33],
    qualifiers = "w${ProArtboard.PIXEL_9_PRO_WIDTH_DP}dp-h${ProArtboard.PIXEL_9_PRO_HEIGHT_DP}dp",
)
@Category(ScreenshotTests::class)
class DebtScreenshotTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    private fun capture(content: @Composable () -> Unit) {
        composeTestRule.setContent {
            ProExpenseTheme {
                Box(
                    Modifier
                        .fillMaxSize()
                        .background(ProExpenseTheme.colors.paper),
                ) {
                    content()
                }
            }
        }
        composeTestRule.onRoot().captureRoboImage()
    }

    @Test
    fun debt_lent() = capture {
        DebtListScreen(
            state = previewDebtLent,
            onSideSelected = {},
            onAddRecord = {},
            onRecordClick = {},
            onBack = {},
        )
    }

    @Test
    fun debt_owe() = capture {
        DebtListScreen(
            state = previewDebtOwe,
            onSideSelected = {},
            onAddRecord = {},
            onRecordClick = {},
            onBack = {},
        )
    }

    @Test
    fun debt_add() = capture {
        Box(Modifier.fillMaxSize()) {
            DebtListScreen(
                state = previewDebtLent,
                onSideSelected = {},
                onAddRecord = {},
                onRecordClick = {},
                onBack = {},
            )
            ProBottomSheetHost(
                visible = true,
                title = stringResource(R.string.debt_new_record),
                onClose = {},
            ) {
                DebtAddSheetContent(
                    form = previewDebtAddLent,
                    onSideSelected = {},
                    onPersonChange = {},
                    onAmountChange = {},
                    onPickDate = {},
                    onPickDue = {},
                    onSave = {},
                )
            }
        }
    }

    @Test
    fun debt_lent_detail() = capture {
        DebtDetailScreen(
            state = previewDebtLentDetail,
            onBack = {},
            onMore = {},
            onEdit = {},
            onMarkSettled = {},
        )
    }

    @Test
    fun debt_owe_detail() = capture {
        DebtDetailScreen(
            state = previewDebtOweDetail,
            onBack = {},
            onMore = {},
            onEdit = {},
            onMarkSettled = {},
        )
    }

    @Test
    fun edge_debt_conflict() = capture {
        Box(Modifier.fillMaxSize()) {
            DebtListScreen(
                state = previewDebtLent,
                onSideSelected = {},
                onAddRecord = {},
                onRecordClick = {},
                onBack = {},
            )
            ProAlertDialog(
                visible = true,
                icon = ProIconGlyph.User,
                iconTint = ProExpenseTheme.colors.warning,
                iconBackground = ProExpenseTheme.colors.warningTint,
                title = stringResource(R.string.debt_conflict_title, "John"),
                body = AnnotatedString(
                    stringResource(
                        R.string.debt_conflict_body,
                        "John",
                        "\"" + stringResource(R.string.debt_tab_owe) + "\"",
                        "\"" + stringResource(R.string.debt_tab_lent) + "\"",
                    ),
                ),
                confirmLabel = stringResource(R.string.debt_continue),
                onConfirm = {},
                dismissLabel = stringResource(R.string.debt_cancel),
                onDismiss = {},
                confirmVariant = ProButtonVariant.Warning,
            )
        }
    }

    @Test
    fun edge_debt_settled() = capture {
        Box(Modifier.fillMaxSize()) {
            DebtListScreen(
                state = previewDebtSettled,
                onSideSelected = {},
                onAddRecord = {},
                onRecordClick = {},
                onBack = {},
            )
            ProAlertDialog(
                visible = true,
                icon = ProIconGlyph.Close,
                iconTint = ProExpenseTheme.colors.danger,
                iconBackground = ProExpenseTheme.colors.dangerTint,
                title = stringResource(R.string.debt_delete_title),
                body = AnnotatedString(stringResource(R.string.debt_delete_body, "Aiko")),
                confirmLabel = stringResource(R.string.debt_delete),
                onConfirm = {},
                dismissLabel = stringResource(R.string.debt_cancel),
                onDismiss = {},
                confirmVariant = ProButtonVariant.Danger,
            )
        }
    }
}
