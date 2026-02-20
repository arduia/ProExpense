package com.arduia.expense.ui.home.compose

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.arduia.design.theme.ProExpenseTheme
import com.arduia.design.theme.md_theme_light_primaryContainer
import com.arduia.design.theme.md_theme_light_onPrimaryContainer
import com.arduia.expense.R
import com.arduia.expense.ui.expenselogs.ExpenseUiModel

@Composable
fun HomeRecentList(
    recentData: List<ExpenseUiModel>,
    onItemClick: (ExpenseUiModel) -> Unit,
    onMoreLogsClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (recentData.isEmpty()) return

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors().copy(
            containerColor = MaterialTheme.colorScheme.surface
        )
        ) {
        Column(modifier = Modifier.padding(top = 16.dp)) {
            Text(
                text = stringResource(R.string.recent),
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Column(modifier = Modifier.padding(top = 16.dp)) {
                recentData.take(3).forEach { item ->
                    RecentListItem(
                        item = item,
                        onClick = { onItemClick(item) }
                    )
                }
            }

            TextButton(
                onClick = onMoreLogsClick,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(vertical = 8.dp)
            ) {
                Text(text = stringResource(R.string.more))
            }
        }
    }
}

@Composable
private fun RecentListItem(
    item: ExpenseUiModel,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Icon Background Box
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                // Use a soft secondary color for the background, like primaryContainer
                .background(md_theme_light_primaryContainer),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = item.category),
                contentDescription = item.name,
                modifier = Modifier.size(24.dp),
                colorFilter = ColorFilter.tint(md_theme_light_onPrimaryContainer)
            )
        }

        // Title and Date
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(start = 16.dp, end = 8.dp)
        ) {
            Text(
                text = item.name,
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = item.date,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 2.dp)
            )
        }

        // Amount and Currency
        Row(verticalAlignment = Alignment.Bottom) {
            Text(
                text = item.amount,
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.End
            )
            Text(
                text = item.currencySymbol,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(start = 4.dp, bottom = 2.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun HomeRecentListPreview() {
    ProExpenseTheme {
        HomeRecentList(
            recentData = listOf(
                ExpenseUiModel(
                    id = 1,
                    name = "yttt",
                    date = "2025 Aug 15",
                    category = R.drawable.ic_menu,
                    amount = "12,580",
                    currencySymbol = "USD",
                    finance = "OUT"
                ),
                ExpenseUiModel(
                    id = 2,
                    name = "Sample Name",
                    date = "2025 Aug 15",
                    category = R.drawable.ic_food,
                    amount = "123,500",
                    currencySymbol = "USD",
                    finance = "OUT"
                ),
                ExpenseUiModel(
                    id = 3,
                    name = "Sample",
                    date = "2025 Aug 9",
                    category = R.drawable.ic_menu,
                    amount = "25,000",
                    currencySymbol = "USD",
                    finance = "OUT"
                )
            ),
            onItemClick = {},
            onMoreLogsClick = {}
        )
    }
}
