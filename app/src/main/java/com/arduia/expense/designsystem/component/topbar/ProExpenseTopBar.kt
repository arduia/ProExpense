package com.arduia.expense.designsystem.component.topbar

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import com.arduia.expense.R
import com.arduia.expense.designsystem.theme.ProExpenseTheme

sealed interface TopBarNavIcon {
    data object Drawer : TopBarNavIcon
    data object Back : TopBarNavIcon
    data object None : TopBarNavIcon
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProExpenseTopBar(
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    navIcon: TopBarNavIcon = TopBarNavIcon.Drawer,
    onNavIconClick: () -> Unit = {},
    actions: @Composable () -> Unit = {},
) {
    TopAppBar(
        modifier = modifier,
        title = {
            Text(text = title)
        },
        navigationIcon = {
            when (navIcon) {
                TopBarNavIcon.Drawer -> IconButton(onClick = onNavIconClick) {
                    Icon(
                        painter = painterResource(R.drawable.ic_menu),
                        contentDescription = "Open drawer",
                    )
                }
                TopBarNavIcon.Back -> IconButton(onClick = onNavIconClick) {
                    Icon(
                        painter = painterResource(R.drawable.ic_back),
                        contentDescription = "Navigate back",
                    )
                }
                TopBarNavIcon.None -> Unit
            }
        },
        actions = { actions() },
    )
}

@Preview
@Composable
private fun PreviewDrawer() {
    ProExpenseTheme {
        ProExpenseTopBar(title = "Home", navIcon = TopBarNavIcon.Drawer)
    }
}

@Preview
@Composable
private fun PreviewBack() {
    ProExpenseTheme {
        ProExpenseTopBar(title = "Add Expense", navIcon = TopBarNavIcon.Back)
    }
}
