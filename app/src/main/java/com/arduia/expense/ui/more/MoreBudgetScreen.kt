package com.arduia.expense.ui.more

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.arduia.expense.domain.Amount
import com.arduia.expense.domain.CurrencyCode
import com.arduia.expense.domain.Money
import com.arduia.expense.ui.design.AmountDisplay
import com.arduia.expense.ui.design.AmountInput
import com.arduia.expense.ui.design.NumericKeypad
import com.arduia.expense.ui.design.currencySymbol
import com.arduia.expense.ui.design.ProButton
import com.arduia.expense.ui.design.ProButtonSize
import com.arduia.expense.ui.design.ProButtonVariant
import com.arduia.expense.ui.design.ProTopBar
import com.arduia.expense.ui.design.ProTopBarAction
import com.arduia.expense.ui.theme.ProArtboard
import com.arduia.expense.ui.theme.ProExpenseTheme

@Composable
fun MoreBudgetScreen(
    currentAmount: String?,
    homeCurrency: CurrencyCode,
    onSave: (Money?) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = ProExpenseTheme.colors
    val dimens = ProExpenseTheme.dimensions
    var rawAmount by remember {
        mutableStateOf(currentAmount?.filter { it.isDigit() || it == '.' } ?: "")
    }
    val displayAmount = AmountInput.formatDisplay(rawAmount.ifEmpty { "0" })
    val canProceed = AmountInput.canProceed(rawAmount)

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(colors.paper)
            .statusBarsPadding()
            .navigationBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = dimens.screenPadding),
    ) {
        ProTopBar(
            title = "Monthly Budget",
            onBack = onBack,
        )

        AmountDisplay(
            amountText = displayAmount,
            currencySymbol = currencySymbol(homeCurrency.code),
            currencyCode = homeCurrency.code,
            isZero = !canProceed,
            showZeroValidation = false,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = dimens.space8, bottom = dimens.space16),
        )

        NumericKeypad(
            actionsEnabled = true,
            onKey = { key -> rawAmount = AmountInput.applyKey(rawAmount, key) },
            onBackspace = { rawAmount = AmountInput.applyBackspace(rawAmount) },
            onSave = {
                if (canProceed) {
                    val cents = (AmountInput.numericValue(rawAmount) ?: 0.0) * 100
                    onSave(Money(Amount(cents.toLong()), homeCurrency))
                }
            },
            onNext = {},
            saveLabel = "Save",
            nextLabel = "Next",
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = dimens.space16),
        )

        ProButton(
            text = "Turn Off",
            onClick = { onSave(null) },
            variant = ProButtonVariant.Ghost,
            size = ProButtonSize.Lg,
            fillMaxWidth = true,
            modifier = Modifier
                .padding(bottom = dimens.space18),
        )
    }
}

@Preview(
    name = "More budget — empty",
    widthDp = ProArtboard.PIXEL_9_PRO_WIDTH_DP,
    heightDp = ProArtboard.PIXEL_9_PRO_HEIGHT_DP,
    showBackground = true,
)
@Composable
private fun MoreBudgetEmptyPreview() {
    ProExpenseTheme {
        MoreBudgetScreen(
            currentAmount = null,
            homeCurrency = CurrencyCode("USD"),
            onSave = {},
            onBack = {},
        )
    }
}

@Preview(
    name = "More budget — set",
    widthDp = ProArtboard.PIXEL_9_PRO_WIDTH_DP,
    heightDp = ProArtboard.PIXEL_9_PRO_HEIGHT_DP,
    showBackground = true,
)
@Composable
private fun MoreBudgetSetPreview() {
    ProExpenseTheme {
        MoreBudgetScreen(
            currentAmount = "$2,500.00",
            homeCurrency = CurrencyCode("USD"),
            onSave = {},
            onBack = {},
        )
    }
}
