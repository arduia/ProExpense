package com.arduia.expense.ui.more

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
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.arduia.expense.R
import com.arduia.expense.domain.Amount
import com.arduia.expense.domain.CurrencyCode
import com.arduia.expense.domain.Money
import com.arduia.expense.ui.design.AmountDisplay
import com.arduia.expense.ui.design.AmountInput
import com.arduia.expense.ui.design.NumericKeypad
import com.arduia.expense.ui.design.ProTopBar
import com.arduia.expense.ui.design.currencySymbol
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
    var budgetEnabled by remember { mutableStateOf(currentAmount != null) }
    var rawAmount by remember {
        mutableStateOf(currentAmount?.filter { it.isDigit() || it == '.' } ?: "")
    }
    val displayAmount = AmountInput.formatDisplay(rawAmount.ifEmpty { "0" })
    val canProceed = AmountInput.canProceed(rawAmount)

    Column(
        modifier =
            modifier
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

        MoreBudgetToggleRow(
            checked = budgetEnabled,
            onCheckedChange = { on ->
                budgetEnabled = on
                // Turning the switch off is the disable action itself — mirrors the old
                // "Turn Off" button's immediate-save behavior instead of a separate confirm step.
                if (!on) onSave(null)
            },
            modifier =
                Modifier
                    .padding(top = dimens.space8, bottom = dimens.space8),
        )

        if (budgetEnabled) {
            AmountDisplay(
                amountText = displayAmount,
                currencySymbol = currencySymbol(homeCurrency.code),
                currencyCode = homeCurrency.code,
                isZero = !canProceed,
                showZeroValidation = false,
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(top = dimens.space8, bottom = dimens.space16),
            )

            NumericKeypad(
                actionsEnabled = true,
                onKey = { key -> rawAmount = AmountInput.applyKey(rawAmount, key) },
                onBackspace = { rawAmount = AmountInput.applyBackspace(rawAmount) },
                // showSaveAction = false renders only the "Next"-slot button, so the real save
                // logic goes there — leaving it on onSave left a second, dead "Next" button visible.
                onSave = {},
                onNext = {
                    if (canProceed) {
                        val cents = (AmountInput.numericValue(rawAmount) ?: 0.0) * 100
                        onSave(Money(Amount(cents.toLong()), homeCurrency))
                    }
                },
                nextLabel = "Save",
                showSaveAction = false,
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(bottom = dimens.space16),
            )
        }
    }
}

@Composable
private fun MoreBudgetToggleRow(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = ProExpenseTheme.colors
    val dimens = ProExpenseTheme.dimensions
    val typography = ProExpenseTheme.typography

    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(dimens.space12),
    ) {
        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(dimens.space4)) {
            Text(
                text = stringResource(R.string.more_monthly_budget),
                style = typography.bodyMedium,
                color = colors.onSurface,
            )
            Text(
                text = stringResource(R.string.more_budget_toggle_caption),
                style = typography.caption,
                color = colors.onSurfaceMuted,
            )
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors =
                SwitchDefaults.colors(
                    checkedThumbColor = colors.onPrimaryWarm,
                    checkedTrackColor = colors.primary,
                    uncheckedThumbColor = colors.surface,
                    uncheckedTrackColor = colors.lineStrong,
                    uncheckedBorderColor = colors.lineStrong,
                ),
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
