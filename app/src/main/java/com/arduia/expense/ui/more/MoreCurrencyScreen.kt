package com.arduia.expense.ui.more

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.arduia.expense.ui.design.ProExpensePaperBackground
import com.arduia.expense.ui.profile.CurrencyRow
import com.arduia.expense.ui.profile.ProfileCurrencies
import com.arduia.expense.ui.profile.ProfileCurrency
import com.arduia.expense.ui.theme.ProExpenseTheme

/**
 * Change the app's home currency. Single-select; selection applies immediately — no save button,
 * per design/screens/06-more/04-more-currency.
 */
@Composable
fun MoreCurrencyScreen(
    selectedCode: String,
    onCurrencySelect: (ProfileCurrency) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    currencies: List<ProfileCurrency> = ProfileCurrencies,
) {
    val colors = ProExpenseTheme.colors

    Column(modifier = modifier.fillMaxSize()) {
        MoreNavBar(title = "Currency", onBack = onBack)

        Text(
            text = "Your home currency. Applied to all entries — multi-currency per entry comes later.",
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = MoreHorizontalPadding),
            style = ProExpenseTheme.typography.body,
            color = colors.muted,
        )

        Spacer(modifier = Modifier.height(16.dp))

        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = MoreHorizontalPadding)
                .padding(bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            currencies.forEach { currency ->
                CurrencyRow(
                    currency = currency,
                    selected = currency.code == selectedCode,
                    onClick = { onCurrencySelect(currency) },
                )
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 414, heightDp = 868)
@Composable
private fun MoreCurrencyScreenPreview() {
    var selected by remember { mutableStateOf("USD") }
    ProExpenseTheme {
        ProExpensePaperBackground {
            MoreCurrencyScreen(
                selectedCode = selected,
                onCurrencySelect = { selected = it.code },
                onBack = {},
            )
        }
    }
}
