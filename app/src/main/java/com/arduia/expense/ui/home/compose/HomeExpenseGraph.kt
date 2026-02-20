package com.arduia.expense.ui.home.compose

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.arduia.design.theme.ProExpenseTheme
import com.arduia.design.theme.md_theme_light_primary
import com.arduia.expense.R
import com.arduia.expense.ui.home.WeeklyGraphUiModel

@Composable
fun HomeExpenseGraph(
    graphData: WeeklyGraphUiModel?,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors().copy(
            containerColor = MaterialTheme.colorScheme.surface
        )
        ) {
        Column(modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)) {
            Text(
                modifier = Modifier.padding(horizontal = 16.dp),
                text = stringResource(R.string.expenses_in_this_week),
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                modifier = Modifier.padding(start = 16.dp, end=16.dp, top = 4.dp),
                text = graphData?.dateRange ?: "",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            SpendGraph(
                rates = graphData?.rate ?: emptyMap(),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
                    .padding(top = 32.dp),
                dayColor = md_theme_light_primary
            )
        }
    }
}

@Preview
@Composable
private fun HomeExpenseGraphPreview() {
    ProExpenseTheme {
        HomeExpenseGraph(
            graphData = WeeklyGraphUiModel(
                dateRange = "FEB 8 - 15",
                rate = mapOf(1 to 0, 2 to 20, 3 to 10, 4 to 100, 5 to 60, 6 to 30, 7 to 0)
            )
        )
    }
}
