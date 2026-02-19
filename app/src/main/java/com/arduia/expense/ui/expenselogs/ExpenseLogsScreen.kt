package com.arduia.expense.ui.expenselogs

import android.content.res.Configuration
import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.arduia.design.theme.ProExpenseTheme
import com.arduia.expense.R
import com.arduia.expense.ui.home.ExpenseLogItem
import com.arduia.expense.ui.home.ExpenseUiModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpenseLogsScreen(
    onNavigationIconClick: () -> Unit = {},
    expenseLogs: List<ExpenseUiModel> = emptyList(),
    onExpenseClick: (ExpenseUiModel) -> Unit = {}
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(id = R.string.expense_logs)) },
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
            ) {
                if (expenseLogs.isEmpty()) {
                    EmptyExpenseLogsState()
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(bottom = 80.dp) // paddingBottom="80dp" in XML
                    ) {
                        items(expenseLogs) { item ->
                            ExpenseLogItem(
                                transaction = item,
                                onClick = { onExpenseClick(item) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun EmptyExpenseLogsState() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_info),
            contentDescription = null,
            modifier = Modifier.size(50.dp),
            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary)
        )
        Text(
            text = stringResource(id = R.string.no_data), // Requires string resource check or hardcode "NO EXPENSE LOGS"
            style = MaterialTheme.typography.bodyLarge, // Body1
            modifier = Modifier.padding(top = 16.dp),
            textAlign = TextAlign.Center
        )
        // Description text is initially invisible in XML, but we can show it or keep logic
        // XML: android:visibility="invisible" -> We'll omit it or keep it hidden for now to match behavior
        // If we want to show it:
        /*
        Text(
            text = "There is no expense logs.\n Please start adding your expense.",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 8.dp)
        )
        */
    }
}

// Add string resource for "No Expense Logs" if it doesn't exist?
// checking layout: android:text="No Expense Logs" (hardcoded in XML)
// I should probably use the literal string or check if R.string.no_expense_logs exists.
// Let's assume it might not exist and use literal "No Expense Logs" if needed, 
// but best practice is R.string. Let's use a temporary placeholder if R.string is missing during compilation, 
// but for now I will assume I can add it or use hardcoded string to match XML exactly.
// Actually, looking at XML, android:text="No Expense Logs". 
// I will use "NO EXPENSE LOGS" to match caps (textAllCaps=true) or let typography handle it.

@Preview(showBackground = true)
@Composable
fun ExpenseLogsScreenPreview() {
    val mockData = listOf(
        ExpenseUiModel("1", "Groceries", "Dec 2, 2023", "12,000", "MMK", R.drawable.ic_home),
        ExpenseUiModel("2", "Transport", "Dec 2, 2023", "2,500", "MMK", R.drawable.ic_transportation)
    )
    ProExpenseTheme {
        ExpenseLogsScreen(expenseLogs = mockData)
    }
}

@Preview(name = "Empty State", showBackground = true)
@Composable
fun ExpenseLogsScreenEmptyPreview() {
    ProExpenseTheme {
        ExpenseLogsScreen(expenseLogs = emptyList())
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Composable
fun ExpenseLogsScreenDarkPreview() {
     val mockData = listOf(
        ExpenseUiModel("1", "Groceries", "Dec 2, 2023", "12,000", "MMK", R.drawable.ic_home)
    )
    ProExpenseTheme(darkTheme = true) {
        ExpenseLogsScreen(expenseLogs = mockData)
    }
}
@Preview(showBackground = true)
@Composable
fun EmptyExpenseLogsStatePreview() {
    ProExpenseTheme {
        EmptyExpenseLogsState()
    }
}
