package com.arduia.expense.feature.currency.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.arduia.expense.feature.currency.ui.preview.previewMoreCurrencies
import com.arduia.expense.ui.theme.ProArtboard
import com.arduia.expense.ui.theme.ProExpenseTheme

@Composable
fun CurrencySettingsFlow(
    selectedCode: String,
    onSelect: (String) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    MoreCurrencyScreen(
        items = previewMoreCurrencies,
        selectedCode = selectedCode,
        onSelect = onSelect,
        onBack = onBack,
        modifier = modifier,
    )
}

@Preview(
    name = "Currency settings flow",
    widthDp = ProArtboard.PIXEL_9_PRO_WIDTH_DP,
    heightDp = ProArtboard.PIXEL_9_PRO_HEIGHT_DP,
    showBackground = true,
)
@Composable
private fun CurrencySettingsFlowPreview() {
    ProExpenseTheme {
        CurrencySettingsFlow(
            selectedCode = "USD",
            onSelect = {},
            onBack = {},
        )
    }
}
