package com.arduia.expense.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.arduia.expense.ui.design.IconBudget
import com.arduia.expense.ui.design.IconHome
import com.arduia.expense.ui.design.IconJournal
import com.arduia.expense.ui.design.IconMore
import com.arduia.expense.ui.design.IconPlus
import com.arduia.expense.ui.theme.ProExpenseTheme

@Composable
fun HomeBottomNav(
    selectedTab: HomeTab,
    onTabSelected: (HomeTab) -> Unit,
    onAddExpense: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = ProExpenseTheme.colors
    val dims = ProExpenseTheme.dimensions
    val shapes = ProExpenseTheme.shapes
    val navShadowColor = Color(0x1A000000)
    val chromeHeight = dims.bottomNavChromeHeight

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(chromeHeight)
            .graphicsLayer { clip = false }
            .navigationBarsPadding()
            .padding(horizontal = dims.bottomNavInsetH)
            .padding(bottom = dims.bottomNavBottomInset),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(dims.bottomNavHeight)
                .align(Alignment.BottomCenter)
                .shadow(
                    elevation = dims.bottomNavShadowElevation,
                    shape = shapes.bottomNav,
                    ambientColor = navShadowColor,
                    spotColor = navShadowColor,
                    clip = false,
                )
                .clip(shapes.bottomNav)
                .background(colors.navBackground),
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(dims.bottomNavHeight)
                .align(Alignment.BottomCenter),
            verticalAlignment = Alignment.Bottom,
        ) {
            BottomNavTab(
                tab = HomeTab.Home,
                selected = selectedTab == HomeTab.Home,
                onClick = { onTabSelected(HomeTab.Home) },
                modifier = Modifier.weight(1f),
            )
            BottomNavTab(
                tab = HomeTab.Budget,
                selected = selectedTab == HomeTab.Budget,
                onClick = { onTabSelected(HomeTab.Budget) },
                modifier = Modifier.weight(1f),
            )
            AddNavLabel(
                onClick = onAddExpense,
                modifier = Modifier.weight(1f),
            )
            BottomNavTab(
                tab = HomeTab.Journal,
                selected = selectedTab == HomeTab.Journal,
                onClick = { onTabSelected(HomeTab.Journal) },
                modifier = Modifier.weight(1f),
            )
            BottomNavTab(
                tab = HomeTab.More,
                selected = selectedTab == HomeTab.More,
                onClick = { onTabSelected(HomeTab.More) },
                modifier = Modifier.weight(1f),
            )
        }

        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .size(dims.homeFabSize)
                .zIndex(1f)
                .shadow(dims.buttonShadowElevation, CircleShape, clip = false)
                .clip(CircleShape)
                .background(colors.primary)
                .clickable(onClick = onAddExpense),
            contentAlignment = Alignment.Center,
        ) {
            IconPlus(color = colors.onPrimary, size = 24.dp, weight = 2f)
        }
    }
}

@Composable
private fun AddNavLabel(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = ProExpenseTheme.colors
    val typography = ProExpenseTheme.typography
    val dims = ProExpenseTheme.dimensions
    val interaction = remember { MutableInteractionSource() }

    Column(
        modifier = modifier
            .height(dims.bottomNavHeight)
            .clickable(
                interactionSource = interaction,
                indication = ripple(bounded = false, radius = dims.rippleRadiusIconButton),
                onClick = onClick,
            ),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Bottom,
    ) {
        Box(modifier = Modifier.height(dims.bottomNavIconBandHeight))
        Text(
            text = "Add",
            modifier = Modifier.padding(bottom = dims.bottomNavLabelPaddingBottom),
            style = typography.navTabLabel,
            color = colors.navInactive,
        )
    }
}

@Composable
private fun BottomNavTab(
    tab: HomeTab,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = ProExpenseTheme.colors
    val typography = ProExpenseTheme.typography
    val dims = ProExpenseTheme.dimensions
    val interaction = remember { MutableInteractionSource() }
    val tint = if (selected) colors.primary else colors.navInactive
    val iconWeight = if (selected) 2.1f else 1.7f
    val label = when (tab) {
        HomeTab.Home -> "Home"
        HomeTab.Budget -> "Budget"
        HomeTab.Journal -> "Journal"
        HomeTab.More -> "More"
    }

    Column(
        modifier = modifier
            .height(dims.bottomNavHeight)
            .clip(ProExpenseTheme.shapes.pill)
            .clickable(
                interactionSource = interaction,
                indication = ripple(bounded = false, radius = dims.rippleRadiusIconButton),
                onClick = onClick,
            ),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Bottom,
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(dims.bottomNavIconBandHeight),
            contentAlignment = Alignment.Center,
        ) {
            when (tab) {
                HomeTab.Home -> IconHome(color = tint, size = dims.iconSizeTabBar, weight = iconWeight)
                HomeTab.Budget -> IconBudget(color = tint, size = dims.iconSizeTabBar, weight = iconWeight)
                HomeTab.Journal -> IconJournal(color = tint, size = dims.iconSizeTabBar, weight = iconWeight)
                HomeTab.More -> IconMore(color = tint, size = dims.iconSizeTabBar, weight = iconWeight)
            }
        }
        Text(
            text = label,
            modifier = Modifier.padding(bottom = dims.bottomNavLabelPaddingBottom),
            style = typography.navTabLabel.copy(
                fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
            ),
            color = tint,
        )
    }
}

@Preview(showBackground = true, widthDp = 414, heightDp = 120)
@Composable
private fun HomeBottomNavPreview() {
    ProExpenseTheme {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(ProExpenseTheme.colors.paper)
                .padding(top = 8.dp),
        ) {
            HomeBottomNav(
                selectedTab = HomeTab.Home,
                onTabSelected = {},
                onAddExpense = {},
            )
        }
    }
}
