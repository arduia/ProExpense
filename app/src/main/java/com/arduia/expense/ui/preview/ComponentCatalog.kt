package com.arduia.expense.ui.preview

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.arduia.expense.R
import com.arduia.expense.designsystem.component.badge.CircularBadge
import com.arduia.expense.designsystem.component.card.DashboardCard
import com.arduia.expense.designsystem.component.card.InfoCard
import com.arduia.expense.designsystem.component.dialog.DialogTitleBar
import com.arduia.expense.designsystem.component.form.LabeledTextField
import com.arduia.expense.designsystem.component.form.SearchBox
import com.arduia.expense.designsystem.component.form.SettingsRow
import com.arduia.expense.designsystem.component.form.ToggleButtonGroup
import com.arduia.expense.designsystem.component.state.NoDataPlaceholder
import com.arduia.expense.designsystem.component.topbar.ProExpenseTopBar
import com.arduia.expense.designsystem.component.topbar.TopBarNavIcon
import com.arduia.expense.designsystem.theme.ColorNegative
import com.arduia.expense.designsystem.theme.ColorPositive
import com.arduia.expense.designsystem.theme.ColorWarning
import com.arduia.expense.designsystem.theme.ProExpenseTheme
import com.arduia.expense.designsystem.theme.ThemePreviews
import com.arduia.expense.designsystem.theme.White
import com.arduia.expense.ui.component.backup.BackupRow
import com.arduia.expense.ui.component.backup.BackupRowUiModel
import com.arduia.expense.ui.component.category.CategoryChip
import com.arduia.expense.ui.component.category.CategoryPicker
import com.arduia.expense.ui.component.category.CategoryStatisticRow
import com.arduia.expense.ui.component.category.CategoryStatisticUiModel
import com.arduia.expense.ui.component.category.CategoryUiModel
import com.arduia.expense.ui.component.currency.CurrencyRow
import com.arduia.expense.ui.component.currency.CurrencyUiModel
import com.arduia.expense.ui.component.dialog.AppTheme
import com.arduia.expense.ui.component.dialog.ChooseThemeDialog
import com.arduia.expense.ui.component.dialog.DeleteConfirmDialog
import com.arduia.expense.ui.component.dialog.ExportDialog
import com.arduia.expense.ui.component.dialog.ExportUiState
import com.arduia.expense.ui.component.dialog.FeedbackStatusDialog
import com.arduia.expense.ui.component.dialog.FilterDialog
import com.arduia.expense.ui.component.dialog.FilterUiState
import com.arduia.expense.ui.component.expense.DateSectionHeader
import com.arduia.expense.ui.component.expense.ExpenseDetailDialog
import com.arduia.expense.ui.component.expense.ExpenseDetailUiModel
import com.arduia.expense.ui.component.expense.ExpenseRow
import com.arduia.expense.ui.component.expense.ExpenseRowUiModel
import com.arduia.expense.ui.component.expense.SwipeableExpenseRow
import com.arduia.expense.ui.component.language.LanguageRow
import com.arduia.expense.ui.component.language.LanguageUiModel
import com.arduia.expense.ui.home.component.RecentListCard
import com.arduia.expense.ui.home.component.SpendGraph
import com.arduia.expense.ui.home.component.SpendGraphCard
import com.arduia.expense.ui.home.component.TotalsCard
import com.arduia.expense.ui.navigation.DrawerContent
import com.arduia.expense.ui.navigation.DrawerItem
import com.arduia.expense.ui.navigation.ExpenseLogRoute
import com.arduia.expense.ui.navigation.HomeRoute
import com.arduia.expense.ui.navigation.StatisticsRoute

// ---------------------------------------------------------------------------
// Shared sample data
// ---------------------------------------------------------------------------

private val sampleExpense = ExpenseRowUiModel(
    id = 1L, categoryInitial = "F", name = "Grocery",
    date = "Today, 09:15 AM", amount = "- $45.00", isExpense = true,
)
private val sampleIncome = ExpenseRowUiModel(
    id = 2L, categoryInitial = "S", name = "Salary",
    date = "May 1, 2026", amount = "+ $3,200.00", isExpense = false,
)
private val sampleCategories = listOf(
    CategoryUiModel(1, "Food"), CategoryUiModel(2, "Transport"),
    CategoryUiModel(3, "Health"), CategoryUiModel(4, "Shopping"),
    CategoryUiModel(5, "Bills"), CategoryUiModel(6, "Entertainment"),
)
private val weeklyData = listOf(100f, 250f, 80f, 320f, 150f, 200f, 90f)

// ---------------------------------------------------------------------------
// Section label helper
// ---------------------------------------------------------------------------

@Composable
private fun SectionHeader(title: String) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Spacer(modifier = Modifier.height(ProExpenseTheme.spacing.grid4))
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(horizontal = ProExpenseTheme.spacing.grid3),
        )
        HorizontalDivider(
            modifier = Modifier.padding(
                horizontal = ProExpenseTheme.spacing.grid3,
                vertical = ProExpenseTheme.spacing.grid1,
            ),
            color = ProExpenseTheme.colors.divider,
        )
    }
}

// ---------------------------------------------------------------------------
// Full catalog screen
// ---------------------------------------------------------------------------

@Composable
fun ComponentCatalog(modifier: Modifier = Modifier) {
    Surface(modifier = modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        LazyColumn(
            contentPadding = PaddingValues(bottom = ProExpenseTheme.spacing.grid5),
        ) {

            // ── Top Bar ──────────────────────────────────────────────────
            item { SectionHeader("TopBar — states") }
            item { ProExpenseTopBar(title = "Home", navIcon = TopBarNavIcon.Drawer) }
            item { ProExpenseTopBar(title = "Add Expense", navIcon = TopBarNavIcon.Back) }
            item { ProExpenseTopBar(title = "Detail", navIcon = TopBarNavIcon.None) }

            // ── Badges ───────────────────────────────────────────────────
            item { SectionHeader("CircularBadge — sizes & colors") }
            item {
                androidx.compose.foundation.layout.Row(
                    modifier = Modifier.padding(ProExpenseTheme.spacing.grid3),
                    horizontalArrangement = Arrangement.spacedBy(ProExpenseTheme.spacing.grid3),
                ) {
                    CircularBadge(label = "F", size = 24.dp)
                    CircularBadge(label = "T", size = 40.dp)
                    CircularBadge(label = "S", size = 56.dp)
                    CircularBadge(label = "H", backgroundColor = ColorNegative, contentColor = White)
                    CircularBadge(label = "W", backgroundColor = ColorWarning, contentColor = White)
                    CircularBadge(label = "I", backgroundColor = ColorPositive, contentColor = White)
                }
            }

            // ── Cards ────────────────────────────────────────────────────
            item { SectionHeader("Cards") }
            item {
                Column(
                    modifier = Modifier.padding(ProExpenseTheme.spacing.grid3),
                    verticalArrangement = Arrangement.spacedBy(ProExpenseTheme.spacing.grid3),
                ) {
                    DashboardCard { Text("DashboardCard — slot content") }
                    InfoCard(label = "Total Expenses", value = "$1,234.56")
                    InfoCard(label = "Balance", value = "$880.00")
                }
            }

            // ── Forms ────────────────────────────────────────────────────
            item { SectionHeader("LabeledTextField — states") }
            item {
                Column(
                    modifier = Modifier.padding(ProExpenseTheme.spacing.grid3),
                    verticalArrangement = Arrangement.spacedBy(ProExpenseTheme.spacing.grid3),
                ) {
                    LabeledTextField(label = "Name", value = "Grocery", onValueChange = {})
                    LabeledTextField(label = "Amount", value = "", onValueChange = {}, placeholder = "e.g. 12.50")
                    LabeledTextField(label = "Amount", value = "100", onValueChange = {}, suffix = "USD")
                    LabeledTextField(
                        label = "Amount", value = "", onValueChange = {},
                        isError = true, errorMessage = "Amount is required",
                    )
                    LabeledTextField(label = "Date", value = "May 15, 2026", onValueChange = {}, enabled = false)
                    LabeledTextField(
                        label = "Note", value = "Weekly grocery shopping\nat the supermarket",
                        onValueChange = {}, singleLine = false,
                    )
                }
            }

            item { SectionHeader("SearchBox — states") }
            item {
                Column(
                    modifier = Modifier.padding(ProExpenseTheme.spacing.grid3),
                    verticalArrangement = Arrangement.spacedBy(ProExpenseTheme.spacing.grid3),
                ) {
                    SearchBox(query = "", onQueryChange = {})
                    SearchBox(query = "Grocery", onQueryChange = {})
                }
            }

            item { SectionHeader("ToggleButtonGroup — selection") }
            item {
                Column(
                    modifier = Modifier.padding(ProExpenseTheme.spacing.grid3),
                    verticalArrangement = Arrangement.spacedBy(ProExpenseTheme.spacing.grid2),
                ) {
                    ToggleButtonGroup(options = listOf("Week", "Month", "Year"), selected = "Week", onSelect = {}, label = { it })
                    ToggleButtonGroup(options = listOf("Week", "Month", "Year"), selected = "Month", onSelect = {}, label = { it })
                    ToggleButtonGroup(options = listOf("Week", "Month", "Year"), selected = "Year", onSelect = {}, label = { it })
                }
            }

            // ── Dialog components ─────────────────────────────────────────
            item { SectionHeader("DialogTitleBar — states") }
            item { DialogTitleBar(title = "Expense Detail") }
            item {
                DialogTitleBar(
                    title = "Expense Detail",
                    actions = {
                        androidx.compose.material3.IconButton(onClick = {}) {
                            androidx.compose.material3.Icon(
                                imageVector = androidx.compose.material.icons.Icons.Default.Edit,
                                contentDescription = null,
                            )
                        }
                    },
                )
            }

            // ── State ────────────────────────────────────────────────────
            item { SectionHeader("NoDataPlaceholder — states") }
            item { NoDataPlaceholder(message = "No expenses yet.\nAdd your first expense!") }
            item {
                NoDataPlaceholder(
                    message = "No backup files yet",
                    icon = painterResource(R.drawable.ic_backup),
                )
            }

            // ── Settings rows ─────────────────────────────────────────────
            item { SectionHeader("SettingsRow — states") }
            item {
                Column {
                    SettingsRow(title = "Currency", subtitle = "USD – US Dollar", onClick = {})
                    SettingsRow(title = "Language", subtitle = "English", onClick = {})
                    SettingsRow(title = "Feedback", onClick = {})
                    SettingsRow(
                        title = "Notifications",
                        trailing = {
                            androidx.compose.material3.Switch(checked = true, onCheckedChange = {})
                        },
                    )
                    SettingsRow(title = "Privacy Policy", showDivider = false, onClick = {})
                }
            }

            // ── Expense rows ──────────────────────────────────────────────
            item { SectionHeader("Expense rows") }
            item { DateSectionHeader(date = "Today") }
            item { ExpenseRow(item = sampleExpense) }
            item { ExpenseRow(item = sampleIncome) }
            item {
                ExpenseRow(
                    item = sampleExpense.copy(
                        name = "Annual health insurance premium payment",
                        amount = "- $1,200.00",
                    ),
                )
            }

            item { SectionHeader("SwipeableExpenseRow") }
            item { DateSectionHeader(date = "Yesterday") }
            item { SwipeableExpenseRow(item = sampleExpense, onDelete = {}, onEdit = {}) }
            item { SwipeableExpenseRow(item = sampleIncome, onDelete = {}, onEdit = {}) }

            // ── Category ──────────────────────────────────────────────────
            item { SectionHeader("Category components") }
            item {
                androidx.compose.foundation.layout.Row(
                    modifier = Modifier.padding(ProExpenseTheme.spacing.grid3),
                    horizontalArrangement = Arrangement.spacedBy(ProExpenseTheme.spacing.grid2),
                ) {
                    CategoryChip(category = sampleCategories[0], selected = true, onSelect = {})
                    CategoryChip(category = sampleCategories[1], selected = false, onSelect = {})
                    CategoryChip(category = sampleCategories[2], selected = false, onSelect = {})
                }
            }
            item {
                CategoryPicker(
                    categories = sampleCategories, selectedId = 1, onCategorySelect = {},
                    modifier = Modifier.padding(horizontal = ProExpenseTheme.spacing.grid3),
                )
            }
            item {
                CategoryPicker(
                    categories = sampleCategories, selectedId = null, onCategorySelect = {},
                    modifier = Modifier.padding(horizontal = ProExpenseTheme.spacing.grid3),
                )
            }

            item { SectionHeader("CategoryStatisticRow — progress states") }
            item {
                Column {
                    CategoryStatisticRow(CategoryStatisticUiModel("Food & Drink", "$320.00", 1.0f))
                    CategoryStatisticRow(CategoryStatisticUiModel("Transport", "$210.00", 0.65f))
                    CategoryStatisticRow(CategoryStatisticUiModel("Health", "$85.00", 0.26f))
                    CategoryStatisticRow(CategoryStatisticUiModel("Entertainment", "$18.00", 0.06f))
                }
            }

            // ── Backup rows ───────────────────────────────────────────────
            item { SectionHeader("BackupRow — states") }
            item {
                Column {
                    BackupRow(BackupRowUiModel(1L, "expense_backup_2026_05.xls", "May 15, 2026", false))
                    BackupRow(BackupRowUiModel(2L, "expense_backup_2026_06.xls", "Exporting…", true))
                }
            }

            // ── Currency / Language rows ──────────────────────────────────
            item { SectionHeader("CurrencyRow — selected / unselected") }
            item {
                Column {
                    CurrencyRow(CurrencyUiModel("USD", "US Dollar", "$"), selected = true, onSelect = {})
                    CurrencyRow(CurrencyUiModel("EUR", "Euro", "€"), selected = false, onSelect = {})
                    CurrencyRow(CurrencyUiModel("MMK", "Myanmar Kyat", "K"), selected = false, onSelect = {})
                }
            }

            item { SectionHeader("LanguageRow — selected / unselected") }
            item {
                Column {
                    LanguageRow(LanguageUiModel("en", "English", "English"), selected = true, onSelect = {})
                    LanguageRow(LanguageUiModel("my", "မြန်မာ", "Burmese"), selected = false, onSelect = {})
                }
            }

            // ── Spend graph ───────────────────────────────────────────────
            item { SectionHeader("SpendGraph — data shapes") }
            item {
                Column(
                    modifier = Modifier.padding(ProExpenseTheme.spacing.grid3),
                    verticalArrangement = Arrangement.spacedBy(ProExpenseTheme.spacing.grid3),
                ) {
                    Text("Varied", style = MaterialTheme.typography.labelMedium)
                    SpendGraph(data = weeklyData)
                    Text("All zero", style = MaterialTheme.typography.labelMedium)
                    SpendGraph(data = listOf(0f, 0f, 0f, 0f, 0f, 0f, 0f))
                    Text("Flat (equal values)", style = MaterialTheme.typography.labelMedium)
                    SpendGraph(data = listOf(200f, 200f, 200f, 200f, 200f, 200f, 200f))
                    Text("Spike", style = MaterialTheme.typography.labelMedium)
                    SpendGraph(data = listOf(10f, 10f, 10f, 800f, 10f, 10f, 10f))
                }
            }

            // ── Home composite cards ──────────────────────────────────────
            item { SectionHeader("Home screen cards") }
            item {
                Column(
                    modifier = Modifier.padding(ProExpenseTheme.spacing.grid3),
                    verticalArrangement = Arrangement.spacedBy(ProExpenseTheme.spacing.grid3),
                ) {
                    TotalsCard(totalExpense = "$320.00", totalIncome = "$1,200.00", totalBalance = "$880.00")
                    SpendGraphCard(weeklyData = weeklyData)
                    RecentListCard(
                        expenses = listOf(sampleExpense, sampleIncome),
                        onSeeAllClick = {}, onExpenseClick = {},
                    )
                    RecentListCard(expenses = emptyList(), onSeeAllClick = {}, onExpenseClick = {})
                }
            }

            // ── Navigation Drawer ─────────────────────────────────────────
            item { SectionHeader("DrawerContent — current route") }
            item {
                val drawerItems = listOf(
                    DrawerItem("Home", HomeRoute),
                    DrawerItem("Expense Log", ExpenseLogRoute),
                    DrawerItem("Statistics", StatisticsRoute),
                )
                DrawerContent(
                    items = drawerItems,
                    currentRoute = HomeRoute,
                    onItemClick = {},
                    modifier = Modifier.height(280.dp),
                )
            }
            item {
                val drawerItems = listOf(
                    DrawerItem("Home", HomeRoute),
                    DrawerItem("Expense Log", ExpenseLogRoute),
                    DrawerItem("Statistics", StatisticsRoute),
                )
                DrawerContent(
                    items = drawerItems,
                    currentRoute = ExpenseLogRoute,
                    onItemClick = {},
                    modifier = Modifier.height(280.dp),
                )
            }
        }
    }
}

// ---------------------------------------------------------------------------
// Previews of the catalog itself
// ---------------------------------------------------------------------------

@ThemePreviews
@Composable
private fun PreviewComponentCatalog() {
    ProExpenseTheme {
        ComponentCatalog()
    }
}

// ---------------------------------------------------------------------------
// Dialog previews — shown separately since dialogs overlay the screen
// ---------------------------------------------------------------------------

@Preview(name = "DeleteConfirmDialog")
@Composable
private fun PreviewDialogDelete() {
    ProExpenseTheme { DeleteConfirmDialog(onConfirm = {}, onDismiss = {}) }
}

@Preview(name = "ChooseThemeDialog — System")
@Composable
private fun PreviewDialogThemeSystem() {
    ProExpenseTheme { ChooseThemeDialog(currentTheme = AppTheme.System, onThemeSelect = {}, onDismiss = {}) }
}

@Preview(name = "ChooseThemeDialog — Dark")
@Composable
private fun PreviewDialogThemeDark() {
    ProExpenseTheme { ChooseThemeDialog(currentTheme = AppTheme.Dark, onThemeSelect = {}, onDismiss = {}) }
}

@Preview(name = "FilterDialog — Month")
@Composable
private fun PreviewDialogFilter() {
    ProExpenseTheme {
        FilterDialog(
            state = FilterUiState(selectedRange = "Month", availableRanges = listOf("Week", "Month", "Year")),
            onRangeSelect = {}, onApply = {}, onDismiss = {},
        )
    }
}

@Preview(name = "ExportDialog — All")
@Composable
private fun PreviewDialogExport() {
    ProExpenseTheme {
        ExportDialog(
            state = ExportUiState(selectedRange = "All", availableRanges = listOf("Week", "Month", "Year", "All")),
            onRangeSelect = {}, onExport = {}, onDismiss = {},
        )
    }
}

@Preview(name = "FeedbackStatusDialog — Success")
@Composable
private fun PreviewDialogFeedbackSuccess() {
    ProExpenseTheme {
        FeedbackStatusDialog(title = "Feedback Sent", message = "Thank you! Your feedback has been received.", isSuccess = true, onDismiss = {})
    }
}

@Preview(name = "FeedbackStatusDialog — Error")
@Composable
private fun PreviewDialogFeedbackError() {
    ProExpenseTheme {
        FeedbackStatusDialog(title = "Failed to Send", message = "Check your internet connection and try again.", isSuccess = false, onDismiss = {})
    }
}

@Preview(name = "ExpenseDetailDialog — with note")
@Composable
private fun PreviewDialogExpenseDetail() {
    ProExpenseTheme {
        ExpenseDetailDialog(
            expense = ExpenseDetailUiModel(
                id = 1L, name = "Weekly Grocery", amount = "- $87.50",
                category = "Food & Drink", date = "May 15, 2026", note = "Supermarket + pharmacy",
            ),
            onDismiss = {}, onEdit = {}, onDelete = {},
        )
    }
}

@Preview(name = "ExpenseDetailDialog — no note")
@Composable
private fun PreviewDialogExpenseDetailNoNote() {
    ProExpenseTheme {
        ExpenseDetailDialog(
            expense = ExpenseDetailUiModel(
                id = 2L, name = "Bus Fare", amount = "- $2.50",
                category = "Transport", date = "May 15, 2026", note = "",
            ),
            onDismiss = {}, onEdit = {}, onDelete = {},
        )
    }
}
