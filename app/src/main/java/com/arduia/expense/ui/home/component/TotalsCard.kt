package com.arduia.expense.ui.home.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.arduia.expense.designsystem.component.card.DashboardCard
import com.arduia.expense.designsystem.component.card.InfoCard
import com.arduia.expense.designsystem.theme.ProExpenseTheme

@Composable
fun TotalsCard(
    totalExpense: String,
    totalIncome: String,
    totalBalance: String,
    modifier: Modifier = Modifier,
) {
    DashboardCard(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(ProExpenseTheme.spacing.grid2),
        ) {
            InfoCard(
                modifier = Modifier.weight(1f),
                label = "Expense",
                value = totalExpense,
            )
            InfoCard(
                modifier = Modifier.weight(1f),
                label = "Income",
                value = totalIncome,
            )
            InfoCard(
                modifier = Modifier.weight(1f),
                label = "Balance",
                value = totalBalance,
            )
        }
    }
}

@Preview
@Composable
private fun PreviewTotalsCard() {
    ProExpenseTheme {
        TotalsCard(
            totalExpense = "$320.00",
            totalIncome = "$1,200.00",
            totalBalance = "$880.00",
        )
    }
}
