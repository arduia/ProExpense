package com.arduia.expense.ui.home

import android.content.res.Configuration
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.arduia.design.components.ProExpenseCard
import com.arduia.design.theme.DarkGray
import com.arduia.design.theme.Gray300
import com.arduia.design.theme.ProExpenseTheme
import com.arduia.expense.R

// Placeholder for UI Model
data class ExpenseUiModel(
    val id: String,
    val name: String,
    val date: String,
    val amount: String,
    val currency: String,
    @DrawableRes val categoryIcon: Int,
    val colorStart: String = "#b3e5fc",
    val colorEnd: String = "#b3e5fc"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigationIconClick: () -> Unit = {},
    balanceDateRange: String = "Dec 2",
    income: String = "60,000",
    incomeCurrency: String = "MMK",
    outcome: String = "55,500",
    outcomeCurrency: String = "MMK",
    graphDateRange: String = "Jun 4-5",
    recentTransactions: List<ExpenseUiModel> = emptyList(),
    onMoreClick: () -> Unit = {},
    onTransactionClick: (ExpenseUiModel) -> Unit = {}
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(id = R.string.home)) },
                navigationIcon = {
                    IconButton(onClick = onNavigationIconClick) {
                        Icon(
                            imageVector = Icons.Default.Menu,
                            contentDescription = "Menu"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface,
                    navigationIconContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(paddingValues),
            contentAlignment = Alignment.TopCenter
        ) {
            Column(
                modifier = Modifier
                    .widthIn(max = 450.dp)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                // Balance Card
                HomeBalanceCard(
                    dateRange = balanceDateRange,
                    income = income,
                    incomeCurrency = incomeCurrency,
                    outcome = outcome,
                    outcomeCurrency = outcomeCurrency,
                    modifier = Modifier.padding(16.dp)
                )

                // Graph Card
                HomeGraphCard(
                    dateRange = graphDateRange,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )

                // Recent List Card
                if (recentTransactions.isNotEmpty()) {
                    HomeRecentTransactions(
                        transactions = recentTransactions,
                        onMoreClick = onMoreClick,
                        onTransactionClick = onTransactionClick,
                        modifier = Modifier.padding(16.dp)
                    )
                } else {
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}

@Composable
fun HomeBalanceCard(
    dateRange: String,
    income: String,
    incomeCurrency: String,
    outcome: String,
    outcomeCurrency: String,
    modifier: Modifier = Modifier
) {
    ProExpenseCard(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = stringResource(id = R.string.totals),
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = dateRange,
                style = MaterialTheme.typography.titleSmall, // Subtitle1
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                modifier = Modifier.padding(top = 4.dp)
            )

            // Income
            Text(
                text = stringResource(id = R.string.income),
                style = MaterialTheme.typography.labelSmall, // Overline
                modifier = Modifier.padding(top = 16.dp, start = 32.dp)
            )
            Row(
                modifier = Modifier.padding(start = 32.dp),
                verticalAlignment = Alignment.Bottom
            ) {
                Text(
                    text = income,
                    style = MaterialTheme.typography.headlineSmall, // CurrencyLarge approx
                )
                Text(
                    text = incomeCurrency,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(start = 4.dp, bottom = 4.dp)
                )
            }

            // Outcome
            Text(
                text = stringResource(id = R.string.outcome),
                style = MaterialTheme.typography.labelSmall, // Overline
                modifier = Modifier.padding(top = 16.dp, start = 32.dp)
            )
            Row(
                modifier = Modifier
                    .padding(start = 32.dp, bottom = 16.dp),
                verticalAlignment = Alignment.Bottom
            ) {
                Text(
                    text = outcome,
                    style = MaterialTheme.typography.headlineSmall, // CurrencyLarge approx
                )
                Text(
                    text = outcomeCurrency,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(start = 4.dp, bottom = 4.dp)
                )
            }
        }
    }
}

@Composable
fun HomeGraphCard(
    dateRange: String,
    modifier: Modifier = Modifier
) {
    ProExpenseCard(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Graph Placeholder
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                        RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Weekly Spend Graph",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = stringResource(id = R.string.expenses_in_this_week),
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(start = 16.dp)
            )
            Text(
                text = dateRange,
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                modifier = Modifier.padding(start = 16.dp, top = 4.dp)
            )
        }
    }
}

@Composable
fun HomeRecentTransactions(
    transactions: List<ExpenseUiModel>,
    onMoreClick: () -> Unit,
    onTransactionClick: (ExpenseUiModel) -> Unit,
    modifier: Modifier = Modifier
) {
    ProExpenseCard(modifier = modifier) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = stringResource(id = R.string.recent),
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(start = 16.dp, top = 16.dp, bottom = 8.dp)
            )

            transactions.take(3).forEach { transaction ->
                ExpenseLogItem(
                    transaction = transaction,
                    onClick = { onTransactionClick(transaction) }
                )
            }

            TextButton(
                onClick = onMoreClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                Text(text = stringResource(id = R.string.more))
            }
        }
    }
}

@Composable
fun ExpenseLogItem(
    transaction: ExpenseUiModel,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp, horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Category Icon Circle
        Box(
            modifier = Modifier
                .size(50.dp)
                .clip(CircleShape)
                .background(Color(android.graphics.Color.parseColor(transaction.colorStart))), // Simple hex parse for now
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = transaction.categoryIcon),
                contentDescription = null,
                modifier = Modifier.size(30.dp),
                colorFilter = ColorFilter.tint(Color.DarkGray)
            )
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 16.dp)
        ) {
            Text(
                text = transaction.name,
                style = MaterialTheme.typography.bodyLarge,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = transaction.date,
                style = MaterialTheme.typography.labelSmall, // Caption
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
        }

        Row(verticalAlignment = Alignment.Bottom) {
            Text(
                text = transaction.amount,
                style = MaterialTheme.typography.titleMedium, // CurrencySmall
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = transaction.currency,
                style = MaterialTheme.typography.labelSmall, // Caption
                modifier = Modifier.padding(start = 4.dp, bottom = 4.dp),
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    val mockData = listOf(
        ExpenseUiModel("1", "Groceries", "Dec 2, 2023", "12,000", "MMK",
            R.drawable.ic_home),
        ExpenseUiModel("2", "Transport", "Dec 2, 2023", "2,500", "MMK",
            R.drawable.ic_transportation),
        ExpenseUiModel("3", "Coffee", "Dec 1, 2023", "3,000", "MMK",
            R.drawable.ic_food)
    )
    ProExpenseTheme {
        HomeScreen(recentTransactions = mockData)
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Composable
fun HomeScreenDarkPreview() {
     val mockData = listOf(
        ExpenseUiModel("1", "Groceries", "Dec 2, 2023", "12,000", "MMK",
            R.drawable.ic_home
        )
    )
    ProExpenseTheme(darkTheme = true) {
        HomeScreen(recentTransactions = mockData)
    }
}

@Preview(locale = "my", showBackground = true)
@Composable
fun HomeScreenBurmesePreview() {
     val mockData = listOf(
        ExpenseUiModel("1", "Groceries", "Dec 2, 2023", "12,000", "MMK",
            R.drawable.ic_home)
    )
    ProExpenseTheme {
        HomeScreen(recentTransactions = mockData)
    }
}
@Preview(showBackground = true)
@Composable
fun HomeBalanceCardPreview() {
    ProExpenseTheme {
        HomeBalanceCard(
            dateRange = "Dec 2",
            income = "60,000",
            incomeCurrency = "MMK",
            outcome = "55,500",
            outcomeCurrency = "MMK",
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun HomeGraphCardPreview() {
    ProExpenseTheme {
        HomeGraphCard(
            dateRange = "Jun 4-5",
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun HomeRecentTransactionsPreview() {
    val mockData = listOf(
        ExpenseUiModel("1", "Groceries", "Dec 2, 2023", "12,000", "MMK", R.drawable.ic_home),
        ExpenseUiModel("2", "Transport", "Dec 2, 2023", "2,500", "MMK", R.drawable.ic_transportation)
    )
    ProExpenseTheme {
        HomeRecentTransactions(
            transactions = mockData,
            onMoreClick = {},
            onTransactionClick = {},
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ExpenseLogItemPreview() {
    ProExpenseTheme {
        ExpenseLogItem(
            transaction = ExpenseUiModel("1", "Groceries", "Dec 2, 2023", "12,000", "MMK", R.drawable.ic_home),
            onClick = {}
        )
    }
}
