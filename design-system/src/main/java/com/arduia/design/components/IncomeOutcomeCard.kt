package com.arduia.design.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.arduia.design.theme.ProExpenseTheme

import androidx.compose.ui.res.stringResource
import com.arduia.design.R

@Composable
fun IncomeOutcomeCard(
    dateRange: String,
    incomeAmount: String,
    incomeSymbol: String,
    outcomeAmount: String,
    outcomeSymbol: String,
    modifier: Modifier = Modifier
) {
    StandardCard(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        elevation = 0.dp
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = stringResource(id = R.string.label_totals),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
            Text(
                text = dateRange.uppercase(),
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                modifier = Modifier.padding(top = 2.dp, bottom = 16.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Income
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = stringResource(id = R.string.label_income),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = incomeAmount,
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = incomeSymbol,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(start = 4.dp),
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    }
                }

                // Outcome
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = stringResource(id = R.string.label_outcome),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = outcomeAmount,
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = outcomeSymbol,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(start = 4.dp),
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun PreviewIncomeOutcomeCard() {
    ProExpenseTheme {
        IncomeOutcomeCard(
            dateRange = "Feb 8 - 15",
            incomeAmount = "0",
            incomeSymbol = "USD",
            outcomeAmount = "0",
            outcomeSymbol = "USD"
        )
    }
}
