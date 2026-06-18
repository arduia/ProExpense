package com.arduia.expense.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.tooling.preview.Preview
import com.arduia.expense.ui.design.IconHome
import com.arduia.expense.ui.design.IconPlus
import com.arduia.expense.ui.theme.ProExpenseTheme

enum class HomeTab {
    Home,
    Budget,
    Journal,
    More,
}

@Composable
fun HomeShell(
    homeContent: @Composable () -> Unit,
    selectedTab: HomeTab,
    onTabSelected: (HomeTab) -> Unit,
    onAddExpense: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = ProExpenseTheme.colors
    val dims = ProExpenseTheme.dimensions

    Box(modifier = modifier.fillMaxSize()) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = colors.surface,
            bottomBar = {
                HomeBottomNav(
                    selectedTab = selectedTab,
                    onTabSelected = onTabSelected,
                )
            },
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
            ) {
                homeContent()
            }
        }

        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = dims.homeFabBottomOffset)
                .navigationBarsPadding(),
        ) {
            Box(
                modifier = Modifier
                    .size(dims.homeFabSize)
                    .shadow(dims.buttonShadowElevation, CircleShape)
                    .clip(CircleShape)
                    .background(colors.primary)
                    .clickable(onClick = onAddExpense),
                contentAlignment = Alignment.Center,
            ) {
                IconPlus(color = colors.onPrimary)
            }
        }
    }
}

@Composable
private fun HomeBottomNav(
    selectedTab: HomeTab,
    onTabSelected: (HomeTab) -> Unit,
) {
    val colors = ProExpenseTheme.colors
    val typography = ProExpenseTheme.typography
    val dims = ProExpenseTheme.dimensions

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(colors.surface)
            .padding(horizontal = dims.space16, vertical = dims.space12)
            .navigationBarsPadding(),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        HomeTab.entries.forEach { tab ->
            BottomNavItem(
                tab = tab,
                selected = tab == selectedTab,
                onClick = { onTabSelected(tab) },
            )
        }
    }
}

@Composable
private fun BottomNavItem(
    tab: HomeTab,
    selected: Boolean,
    onClick: () -> Unit,
) {
    val colors = ProExpenseTheme.colors
    val typography = ProExpenseTheme.typography
    val dims = ProExpenseTheme.dimensions
    val interaction = remember { MutableInteractionSource() }
    val label = when (tab) {
        HomeTab.Home -> "Home"
        HomeTab.Budget -> "Budget"
        HomeTab.Journal -> "Journal"
        HomeTab.More -> "More"
    }
    val tint = if (selected) colors.primary else colors.onSurfaceVariant

    Column(
        modifier = Modifier
            .clip(ProExpenseTheme.shapes.pill)
            .clickable(
                interactionSource = interaction,
                indication = ripple(bounded = false, radius = dims.rippleRadiusIconButton),
                onClick = onClick,
            )
            .padding(horizontal = dims.space12, vertical = dims.space4),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(dims.space4),
    ) {
        if (tab == HomeTab.Home) {
            IconHome(color = tint)
        } else {
            Box(
                modifier = Modifier.size(dims.iconSizeNav),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = label.take(1),
                    style = typography.monoCaption,
                    color = tint,
                )
            }
        }
        Text(
            text = label,
            style = typography.monoCaption,
            color = tint,
        )
    }
}

@Preview(showBackground = true, widthDp = 414, heightDp = 868)
@Composable
private fun HomeShellPreview() {
    ProExpenseTheme {
        HomeShell(
            homeContent = {
                HomeScreen(
                    state = HomeUiState(
                        profileName = "Maya",
                        homeCurrencyCode = "USD",
                        dateLabel = "Wed · May 25",
                        monthSpendLabel = "$0",
                        recentRecords = emptyList(),
                    ),
                    onSeeAll = {},
                )
            },
            selectedTab = HomeTab.Home,
            onTabSelected = {},
            onAddExpense = {},
        )
    }
}
