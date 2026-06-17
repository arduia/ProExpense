package com.arduia.expense.ui.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.arduia.expense.ui.design.ProExpenseButton
import com.arduia.expense.ui.design.ProExpenseButtonSize
import com.arduia.expense.ui.design.ProExpensePaperBackground
import com.arduia.expense.ui.theme.ProExpenseTheme

@Composable
fun ProfileCurrencyScreen(
    selectedCode: String,
    onCurrencySelect: (ProfileCurrency) -> Unit,
    onSkip: () -> Unit,
    onStartTracking: () -> Unit,
    modifier: Modifier = Modifier,
    currencies: List<ProfileCurrency> = ProfileCurrencies,
) {
    Column(modifier = modifier.fillMaxSize()) {
        WizHeader(step = 2, total = 2, onSkip = onSkip)

        WizTitle(
            kicker = "PROFILE · 2 OF 2",
            title = "Pick your home currency",
            subtitle = "All entries default to this. You can still log in any currency per-expense.",
        )

        Spacer(modifier = Modifier.height(20.dp))

        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = ProfileHorizontalPadding),
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

        ProExpenseButton(
            text = "Start tracking",
            onClick = onStartTracking,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = ProfileHorizontalPadding)
                .padding(top = 16.dp, bottom = 38.dp),
            size = ProExpenseButtonSize.Large,
        )
    }
}

@Preview(showBackground = true, widthDp = 414, heightDp = 868)
@Composable
private fun ProfileCurrencyScreenPreview() {
    var selected by remember { mutableStateOf("USD") }
    ProExpenseTheme {
        ProExpensePaperBackground {
            ProfileCurrencyScreen(
                selectedCode = selected,
                onCurrencySelect = { selected = it.code },
                onSkip = {},
                onStartTracking = {},
            )
        }
    }
}
