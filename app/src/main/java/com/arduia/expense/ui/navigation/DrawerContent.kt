package com.arduia.expense.ui.navigation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.arduia.expense.designsystem.theme.ProExpenseTheme

data class DrawerItem(
    val label: String,
    val route: Any,
    val icon: @Composable () -> Unit = {},
)

@Composable
fun DrawerContent(
    items: List<DrawerItem>,
    currentRoute: Any?,
    onItemClick: (DrawerItem) -> Unit,
    modifier: Modifier = Modifier,
    header: @Composable () -> Unit = {},
    footer: @Composable () -> Unit = {},
) {
    Column(
        modifier = modifier
            .fillMaxHeight()
            .padding(ProExpenseTheme.spacing.grid2),
    ) {
        header()
        Spacer(modifier = Modifier.height(ProExpenseTheme.spacing.grid3))
        HorizontalDivider(color = ProExpenseTheme.colors.divider)
        Spacer(modifier = Modifier.height(ProExpenseTheme.spacing.grid2))
        items.forEach { item ->
            NavigationDrawerItem(
                label = { Text(text = item.label, style = MaterialTheme.typography.bodyLarge) },
                icon = item.icon,
                selected = item.route == currentRoute,
                onClick = { onItemClick(item) },
            )
        }
        Spacer(modifier = Modifier.weight(1f))
        HorizontalDivider(color = ProExpenseTheme.colors.divider)
        Spacer(modifier = Modifier.height(ProExpenseTheme.spacing.grid2))
        footer()
    }
}

@Preview
@Composable
private fun PreviewDrawerContent() {
    ProExpenseTheme {
        DrawerContent(
            items = listOf(
                DrawerItem(label = "Home", route = HomeRoute),
                DrawerItem(label = "Expense Log", route = ExpenseLogRoute),
                DrawerItem(label = "Statistics", route = StatisticsRoute),
                DrawerItem(label = "Settings", route = SettingsRoute),
                DrawerItem(label = "Backup", route = BackupRoute),
            ),
            currentRoute = HomeRoute,
            onItemClick = {},
        )
    }
}
