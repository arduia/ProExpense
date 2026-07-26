package com.arduia.expense.ui.logging

import androidx.activity.ComponentActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.unit.LayoutDirection
import com.arduia.expense.feature.logging.ui.AddExpenseAmountScreen
import com.arduia.expense.feature.logging.ui.AddExpenseDetailsScreen
import com.arduia.expense.feature.logging.ui.QuickLogFlow
import com.arduia.expense.feature.logging.ui.preview.previewExpenseAmountForeignCurrency
import com.arduia.expense.feature.logging.ui.preview.previewExpenseAmountManyCategories
import com.arduia.expense.feature.logging.ui.preview.previewExpenseAmountTyped
import com.arduia.expense.feature.logging.ui.preview.previewExpenseAmountZeroValidation
import com.arduia.expense.feature.logging.ui.preview.previewExpenseDetails
import com.arduia.expense.feature.logging.ui.preview.previewExpenseDetailsForeignCurrency
import com.arduia.expense.feature.logging.ui.preview.previewExpenseDetailsForeignCurrencyNoRate
import com.arduia.expense.feature.logging.ui.preview.previewExpenseDetailsNoteLimit
import com.arduia.expense.testing.ScreenshotTests
import com.arduia.expense.testing.captureRoboImageWithTolerance
import com.arduia.expense.ui.design.DateTimePickerScreen
import com.arduia.expense.ui.theme.ProArtboard
import com.arduia.expense.ui.theme.ProExpenseTheme
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
class AddExpenseScreenshotTest {
    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    private fun capture(
        layoutDirection: LayoutDirection = LayoutDirection.Ltr,
        darkTheme: Boolean = false,
        content: @androidx.compose.runtime.Composable () -> Unit,
    ) {
        composeTestRule.setContent {
            CompositionLocalProvider(LocalLayoutDirection provides layoutDirection) {
                ProExpenseTheme(darkTheme = darkTheme) {
                    Box(
                        Modifier
                            .fillMaxSize()
                            .background(ProExpenseTheme.colors.paper),
                    ) {
                        content()
                    }
                }
            }
        }
        composeTestRule.onRoot().captureRoboImageWithTolerance()
    }

    @Test
    fun add_amount() {
        capture {
            AddExpenseAmountScreen(
                state = previewExpenseAmountTyped,
                onClose = {},
                onKey = {},
                onBackspace = {},
                onCategorySelected = {},
                onSave = {},
                onNext = {},
            )
        }
    }

    @Test
    fun add_amount_dark() {
        capture(darkTheme = true) {
            AddExpenseAmountScreen(
                state = previewExpenseAmountTyped,
                onClose = {},
                onKey = {},
                onBackspace = {},
                onCategorySelected = {},
                onSave = {},
                onNext = {},
            )
        }
    }

    @Test
    fun add_zero() {
        capture {
            AddExpenseAmountScreen(
                state = previewExpenseAmountZeroValidation,
                onClose = {},
                onKey = {},
                onBackspace = {},
                onCategorySelected = {},
                onSave = {},
                onNext = {},
            )
        }
    }

    @Test
    fun add_amount_foreign_currency() {
        capture {
            AddExpenseAmountScreen(
                state = previewExpenseAmountForeignCurrency,
                onClose = {},
                onKey = {},
                onBackspace = {},
                onCategorySelected = {},
                onSave = {},
                onNext = {},
            )
        }
    }

    @Test
    fun edge_add_many_custom_categories() {
        capture {
            AddExpenseAmountScreen(
                state = previewExpenseAmountManyCategories,
                onClose = {},
                onKey = {},
                onBackspace = {},
                onCategorySelected = {},
                onSave = {},
                onNext = {},
                customCategories = (1..20).map { "custom-$it" to "Custom category $it" },
            )
        }
    }

    @Test
    fun add_details() {
        capture {
            AddExpenseDetailsScreen(
                state = previewExpenseDetails,
                onBackToAmount = {},
                onCategorySelected = {},
                onNoteChange = {},
                onDateClick = {},
                onOpenTagSheet = {},
                onCloseTagSheet = {},
                onTagSelected = {},
                onClearTag = {},
                onSave = {},
            )
        }
    }

    @Test
    fun add_details_dark() {
        capture(darkTheme = true) {
            AddExpenseDetailsScreen(
                state = previewExpenseDetails,
                onBackToAmount = {},
                onCategorySelected = {},
                onNoteChange = {},
                onDateClick = {},
                onOpenTagSheet = {},
                onCloseTagSheet = {},
                onTagSelected = {},
                onClearTag = {},
                onSave = {},
            )
        }
    }

    @Test
    fun add_details_foreign_currency() {
        capture {
            AddExpenseDetailsScreen(
                state = previewExpenseDetailsForeignCurrency,
                onBackToAmount = {},
                onCategorySelected = {},
                onNoteChange = {},
                onDateClick = {},
                onOpenTagSheet = {},
                onCloseTagSheet = {},
                onTagSelected = {},
                onClearTag = {},
                onSave = {},
            )
        }
    }

    @Test
    fun add_details_foreign_currency_no_rate() {
        capture {
            AddExpenseDetailsScreen(
                state = previewExpenseDetailsForeignCurrencyNoRate,
                onBackToAmount = {},
                onCategorySelected = {},
                onNoteChange = {},
                onDateClick = {},
                onOpenTagSheet = {},
                onCloseTagSheet = {},
                onTagSelected = {},
                onClearTag = {},
                onSave = {},
            )
        }
    }

    @Test
    fun edge_draft() {
        capture {
            QuickLogFlow(
                onDismiss = {},
                showDraftPrompt = true,
                draftAmountLabel = "$12.50",
            )
        }
    }

    @Test
    fun edge_note() {
        capture {
            AddExpenseDetailsScreen(
                state = previewExpenseDetailsNoteLimit,
                onBackToAmount = {},
                onCategorySelected = {},
                onNoteChange = {},
                onDateClick = {},
                onOpenTagSheet = {},
                onCloseTagSheet = {},
                onTagSelected = {},
                onClearTag = {},
                onSave = {},
            )
        }
    }

    @Test
    fun edge_tag() {
        capture {
            AddExpenseDetailsScreen(
                state = previewExpenseDetails.copy(showTagSheet = true),
                onBackToAmount = {},
                onCategorySelected = {},
                onNoteChange = {},
                onDateClick = {},
                onOpenTagSheet = {},
                onCloseTagSheet = {},
                onTagSelected = {},
                onClearTag = {},
                onSave = {},
            )
        }
    }

    @Test
    fun add_date_time_sheet() {
        capture {
            // Fixed instant keeps the baseline deterministic across runs.
            DateTimePickerScreen(
                visible = true,
                initialEpochMillis = 1_716_600_000_000L,
                onConfirm = {},
                onDismiss = {},
            )
        }
    }

    @Test
    @Config(qualifiers = "w${ProArtboard.COMPACT_PHONE_WIDTH_DP}dp-h${ProArtboard.COMPACT_PHONE_HEIGHT_DP}dp")
    fun add_amount_compactPhone() {
        capture {
            AddExpenseAmountScreen(
                state = previewExpenseAmountTyped,
                onClose = {},
                onKey = {},
                onBackspace = {},
                onCategorySelected = {},
                onSave = {},
                onNext = {},
            )
        }
    }

    @Test
    @Config(qualifiers = "w${ProArtboard.TABLET_WIDTH_DP}dp-h${ProArtboard.TABLET_HEIGHT_DP}dp")
    fun add_amount_tablet() {
        capture {
            AddExpenseAmountScreen(
                state = previewExpenseAmountTyped,
                onClose = {},
                onKey = {},
                onBackspace = {},
                onCategorySelected = {},
                onSave = {},
                onNext = {},
            )
        }
    }

    @Test
    fun add_amount_rtl() {
        capture(layoutDirection = LayoutDirection.Rtl) {
            AddExpenseAmountScreen(
                state = previewExpenseAmountTyped,
                onClose = {},
                onKey = {},
                onBackspace = {},
                onCategorySelected = {},
                onSave = {},
                onNext = {},
            )
        }
    }

    @Test
    @Config(qualifiers = "w${ProArtboard.COMPACT_PHONE_WIDTH_DP}dp-h${ProArtboard.COMPACT_PHONE_HEIGHT_DP}dp")
    fun add_details_compactPhone() {
        capture {
            AddExpenseDetailsScreen(
                state = previewExpenseDetails,
                onBackToAmount = {},
                onCategorySelected = {},
                onNoteChange = {},
                onDateClick = {},
                onOpenTagSheet = {},
                onCloseTagSheet = {},
                onTagSelected = {},
                onClearTag = {},
                onSave = {},
            )
        }
    }

    @Test
    @Config(qualifiers = "w${ProArtboard.TABLET_WIDTH_DP}dp-h${ProArtboard.TABLET_HEIGHT_DP}dp")
    fun add_details_tablet() {
        capture {
            AddExpenseDetailsScreen(
                state = previewExpenseDetails,
                onBackToAmount = {},
                onCategorySelected = {},
                onNoteChange = {},
                onDateClick = {},
                onOpenTagSheet = {},
                onCloseTagSheet = {},
                onTagSelected = {},
                onClearTag = {},
                onSave = {},
            )
        }
    }

    @Test
    fun add_details_rtl() {
        capture(layoutDirection = LayoutDirection.Rtl) {
            AddExpenseDetailsScreen(
                state = previewExpenseDetails,
                onBackToAmount = {},
                onCategorySelected = {},
                onNoteChange = {},
                onDateClick = {},
                onOpenTagSheet = {},
                onCloseTagSheet = {},
                onTagSelected = {},
                onClearTag = {},
                onSave = {},
            )
        }
    }
}
