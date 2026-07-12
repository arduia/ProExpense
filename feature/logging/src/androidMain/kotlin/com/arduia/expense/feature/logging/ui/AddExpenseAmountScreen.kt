package com.arduia.expense.feature.logging.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.arduia.expense.domain.RecordType
import com.arduia.expense.feature.logging.R
import com.arduia.expense.feature.logging.ui.preview.ExpenseEntryState
import com.arduia.expense.feature.logging.ui.preview.previewExpenseAmountForeignCurrency
import com.arduia.expense.feature.logging.ui.preview.previewExpenseAmountTyped
import com.arduia.expense.feature.logging.ui.preview.previewExpenseAmountZeroValidation
import com.arduia.expense.feature.logging.ui.preview.previewIncomeAmountTyped
import com.arduia.expense.ui.design.AmountDisplay
import com.arduia.expense.ui.design.AmountInput
import com.arduia.expense.ui.design.CategoryPicker
import com.arduia.expense.ui.design.NumericKeypad
import com.arduia.expense.ui.design.ProIcon
import com.arduia.expense.ui.design.ProIconGlyph
import com.arduia.expense.ui.design.ProTextAction
import com.arduia.expense.ui.design.ProTopBar
import com.arduia.expense.ui.design.ProTopBarAction
import com.arduia.expense.ui.design.currencySymbol
import com.arduia.expense.ui.design.customExpenseCategories
import com.arduia.expense.ui.design.defaultExpenseCategories
import com.arduia.expense.ui.theme.ProArtboard
import com.arduia.expense.ui.theme.ProExpenseTheme

@Composable
fun AddExpenseAmountScreen(
    state: ExpenseEntryState,
    onClose: () -> Unit,
    onKey: (String) -> Unit,
    onBackspace: () -> Unit,
    onCategorySelected: (String) -> Unit,
    onSave: () -> Unit,
    onNext: () -> Unit,
    modifier: Modifier = Modifier,
    onOpenCurrencySheet: () -> Unit = {},
    defaultCategories: List<Pair<String, String>> = defaultExpenseCategories,
    customCategories: List<Pair<String, String>> = customExpenseCategories,
) {
    val colors = ProExpenseTheme.colors
    val dimens = ProExpenseTheme.dimensions
    val displayAmount = AmountInput.formatDisplay(state.rawAmount.ifEmpty { "0" })
    val canProceed = AmountInput.canProceed(state.rawAmount)
    val isZero = !canProceed
    val titleRes = if (state.type == RecordType.INCOME) R.string.new_income else R.string.new_expense

    Column(
        modifier =
            modifier
                .fillMaxSize()
                .background(colors.paper)
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(horizontal = dimens.screenPadding),
    ) {
        ProTopBar(
            title = stringResource(titleRes),
            onBack = null,
            action = ProTopBarAction.Close,
            onAction = onClose,
        )

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
            ProTextAction(
                text = state.currencyCode,
                onClick = onOpenCurrencySheet,
                color = colors.primary,
                trailing = {
                    ProIcon(
                        glyph = ProIconGlyph.ChevronDown,
                        contentDescription = null,
                        tint = colors.primary,
                        size = dimens.iconInline,
                    )
                },
            )
        }

        AmountDisplay(
            amountText = displayAmount,
            currencySymbol = currencySymbol(state.currencyCode),
            currencyCode = state.currencyCode,
            isZero = isZero,
            showZeroValidation = state.showZeroValidation,
            zeroHelperMessage = stringResource(R.string.amount_must_be_greater_than_zero),
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(top = dimens.space8, bottom = dimens.space16),
        )

        // Weighted + independently scrollable (not the whole screen, see removed
        // verticalScroll above) so a long custom-category list never pushes NumericKeypad's
        // Save/Next buttons off-screen — this area fills whatever space remains above the
        // keypad and the user swipes within it to see more chips.
        Column(
            modifier =
                Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState()),
        ) {
            CategoryPicker(
                defaultCategories = defaultCategories,
                customCategories = customCategories,
                selectedCategoryId = state.selectedCategoryId,
                onCategorySelected = onCategorySelected,
                showCustomSection = true,
                modifier = Modifier.padding(bottom = dimens.space16),
            )
        }

        NumericKeypad(
            actionsEnabled = canProceed,
            onKey = onKey,
            onBackspace = onBackspace,
            onSave = onSave,
            onNext = onNext,
            saveLabel = stringResource(R.string.save),
            nextLabel = stringResource(R.string.next_with_chevron),
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(bottom = dimens.space18),
        )
    }
}

@Preview(
    name = "Add expense — amount typed",
    widthDp = ProArtboard.PIXEL_9_PRO_WIDTH_DP,
    heightDp = ProArtboard.PIXEL_9_PRO_HEIGHT_DP,
    showBackground = true,
)
@Composable
private fun AddExpenseAmountTypedPreview() {
    ProExpenseTheme {
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

@Preview(
    name = "Add expense — zero validation",
    widthDp = ProArtboard.PIXEL_9_PRO_WIDTH_DP,
    heightDp = ProArtboard.PIXEL_9_PRO_HEIGHT_DP,
    showBackground = true,
)
@Composable
private fun AddExpenseAmountZeroValidationPreview() {
    ProExpenseTheme {
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

@Preview(
    name = "Add expense — foreign currency",
    widthDp = ProArtboard.PIXEL_9_PRO_WIDTH_DP,
    heightDp = ProArtboard.PIXEL_9_PRO_HEIGHT_DP,
    showBackground = true,
)
@Composable
private fun AddExpenseAmountForeignCurrencyPreview() {
    ProExpenseTheme {
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

@Preview(
    name = "Add income — amount typed",
    widthDp = ProArtboard.PIXEL_9_PRO_WIDTH_DP,
    heightDp = ProArtboard.PIXEL_9_PRO_HEIGHT_DP,
    showBackground = true,
)
@Composable
private fun AddIncomeAmountTypedPreview() {
    ProExpenseTheme {
        AddExpenseAmountScreen(
            state = previewIncomeAmountTyped,
            onClose = {},
            onKey = {},
            onBackspace = {},
            onCategorySelected = {},
            onSave = {},
            onNext = {},
            defaultCategories = listOf("income" to "Income", "salary" to "Salary", "gift" to "Gift"),
        )
    }
}
