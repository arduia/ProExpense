package com.arduia.expense.ui.onboarding.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.arduia.expense.designsystem.theme.ProExpenseTheme
import com.arduia.expense.ui.component.currency.CurrencyRow
import com.arduia.expense.ui.component.currency.CurrencyUiModel

@Composable
fun CurrencyPickerPage(
    currencies: List<CurrencyUiModel>,
    selectedCode: String,
    onSelect: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxSize()) {
        Text(
            modifier = Modifier.padding(ProExpenseTheme.spacing.grid3),
            text = "Choose Your Currency",
            style = MaterialTheme.typography.headlineMedium,
        )
        LazyColumn {
            items(currencies, key = { it.code }) { currency ->
                CurrencyRow(
                    currency = currency,
                    selected = currency.code == selectedCode,
                    onSelect = { onSelect(it.code) },
                )
            }
        }
    }
}

@Preview
@Composable
private fun PreviewCurrencyPickerPage() {
    ProExpenseTheme {
        CurrencyPickerPage(
            currencies = listOf(
                CurrencyUiModel(code = "USD", name = "US Dollar", symbol = "$"),
                CurrencyUiModel(code = "EUR", name = "Euro", symbol = "€"),
            ),
            selectedCode = "USD",
            onSelect = {},
        )
    }
}
