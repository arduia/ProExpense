package com.arduia.expense.ui.component.currency

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.arduia.expense.designsystem.theme.ProExpenseTheme

data class CurrencyUiModel(
    val code: String,
    val name: String,
    val symbol: String,
)

@Composable
fun CurrencyRow(
    currency: CurrencyUiModel,
    selected: Boolean,
    onSelect: (CurrencyUiModel) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onSelect(currency) }
            .padding(
                horizontal = ProExpenseTheme.spacing.grid3,
                vertical = ProExpenseTheme.spacing.grid2,
            ),
        horizontalArrangement = Arrangement.spacedBy(ProExpenseTheme.spacing.grid2),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        RadioButton(selected = selected, onClick = { onSelect(currency) })
        Text(
            modifier = Modifier.weight(1f),
            text = "${currency.code} – ${currency.name}",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface,
        )
        Text(
            text = currency.symbol,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Preview
@Composable
private fun PreviewCurrencyRow() {
    ProExpenseTheme {
        CurrencyRow(
            currency = CurrencyUiModel(code = "USD", name = "US Dollar", symbol = "$"),
            selected = true,
            onSelect = {},
        )
    }
}
