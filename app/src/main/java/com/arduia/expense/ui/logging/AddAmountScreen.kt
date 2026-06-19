package com.arduia.expense.ui.logging

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.arduia.expense.R
import com.arduia.expense.feature.logging.AmountInputLogic
import com.arduia.expense.feature.logging.customLogCategories
import com.arduia.expense.feature.logging.defaultLogCategories
import com.arduia.expense.ui.design.AmountDisplay
import com.arduia.expense.ui.design.CategoryPicker
import com.arduia.expense.ui.design.NumericKeypad
import com.arduia.expense.ui.design.ProIcon
import com.arduia.expense.ui.design.ProIconGlyph
import com.arduia.expense.ui.design.proIconClickable
import com.arduia.expense.ui.theme.ProArtboard
import com.arduia.expense.ui.theme.ProExpenseTheme

@Composable
fun AddAmountScreen(
    amount: String,
    selectedCategoryId: String,
    onAmountChange: (String) -> Unit,
    onCategorySelected: (String) -> Unit,
    onClose: () -> Unit,
    onSave: () -> Unit,
    onNext: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = ProExpenseTheme.colors
    val dimens = ProExpenseTheme.dimensions
    val typography = ProExpenseTheme.typography
    var showZeroValidation by rememberSaveable { mutableStateOf(false) }
    val canProceed = AmountInputLogic.canProceed(amount)
    val displayAmount = formatAmountForDisplay(amount)

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(colors.paper)
            .statusBarsPadding()
            .navigationBarsPadding(),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = dimens.space18, vertical = dimens.space8),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            ProIcon(
                glyph = ProIconGlyph.Close,
                contentDescription = stringResource(R.string.close),
                tint = colors.onSurface,
                modifier = Modifier.proIconClickable(onClick = onClose),
            )
            Text(
                text = stringResource(R.string.new_expense),
                style = typography.sectionHead,
                color = colors.onSurface,
                textAlign = TextAlign.Center,
                modifier = Modifier.weight(1f),
            )
            Box(modifier = Modifier.size(dimens.iconNav))
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = dimens.space18),
            verticalArrangement = Arrangement.spacedBy(dimens.space24),
        ) {
            AmountDisplay(
                amountText = displayAmount,
                isZero = !canProceed,
                showZeroValidation = showZeroValidation,
                zeroHelperMessage = stringResource(R.string.amount_must_be_greater_than_zero),
                modifier = Modifier.padding(top = dimens.space8),
            )

            CategoryPicker(
                defaultCategories = defaultLogCategories.map { it.id to it.label },
                customCategories = customLogCategories.map { it.id to it.label },
                selectedCategoryId = selectedCategoryId,
                onCategorySelected = onCategorySelected,
                onMoreClick = {},
            )
        }

        NumericKeypad(
            actionsEnabled = canProceed,
            onKey = { key ->
                onAmountChange(AmountInputLogic.applyKey(amount, key))
            },
            onBackspace = {
                onAmountChange(AmountInputLogic.applyKey(amount, "⌫"))
            },
            onSave = {
                if (canProceed) onSave() else showZeroValidation = true
            },
            onNext = {
                if (canProceed) onNext() else showZeroValidation = true
            },
            nextLabel = stringResource(R.string.next_with_chevron),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = dimens.space18, vertical = dimens.space16),
        )
    }
}

private fun formatAmountForDisplay(amount: String): String {
    if (amount.isBlank()) return "0"
    val display = AmountInputLogic.toDisplay(amount)
    return if (display.decimal != null) "${display.whole}.${display.decimal}" else display.whole
}

@Preview(
    name = "AddAmount — zero",
    widthDp = ProArtboard.PIXEL_9_PRO_WIDTH_DP,
    heightDp = ProArtboard.PIXEL_9_PRO_HEIGHT_DP,
    showBackground = true,
)
@Composable
private fun AddAmountZeroPreview() {
    ProExpenseTheme {
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

@Preview(
    name = "AddAmount — typed",
    widthDp = ProArtboard.PIXEL_9_PRO_WIDTH_DP,
    heightDp = ProArtboard.PIXEL_9_PRO_HEIGHT_DP,
    showBackground = true,
)
@Composable
private fun AddAmountTypedPreview() {
    ProExpenseTheme {
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
