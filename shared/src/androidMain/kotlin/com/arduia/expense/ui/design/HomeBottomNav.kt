package com.arduia.expense.ui.design

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.arduia.expense.ui.theme.ProExpenseTheme

enum class HomeNavTab {
    Home,
    Budget,
    Add,
    Journal,
    More,
}

private data class HomeNavItem(
    val tab: HomeNavTab,
    val label: String,
    val icon: ProIconGlyph,
)

private val homeNavItems = listOf(
    HomeNavItem(HomeNavTab.Home, "Home", ProIconGlyph.Home),
    HomeNavItem(HomeNavTab.Budget, "Budget", ProIconGlyph.Budget),
    HomeNavItem(HomeNavTab.Journal, "Journal", ProIconGlyph.Journal),
    HomeNavItem(HomeNavTab.More, "More", ProIconGlyph.More),
)

@Composable
fun HomeBottomNav(
    selectedTab: HomeNavTab,
    onTabSelected: (HomeNavTab) -> Unit,
    onAddClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = ProExpenseTheme.colors
    val dimens = ProExpenseTheme.dimensions
    val typography = ProExpenseTheme.typography
    val motion = ProExpenseTheme.motion
    val navElevation = ProExpenseTheme.elevation.nav.firstOrNull()
    val barHeight = dimens.navBarHeight

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = dimens.space16),
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(barHeight)
                .align(Alignment.BottomCenter)
                .then(
                    if (navElevation != null) {
                        Modifier.shadow(
                            elevation = navElevation.blur,
                            shape = ProExpenseTheme.shapes.navBar,
                            spotColor = navElevation.color,
                            ambientColor = navElevation.color,
                        )
                    } else {
                        Modifier
                    },
                ),
            shape = ProExpenseTheme.shapes.navBar,
            color = colors.surface.copy(alpha = dimens.navSurfaceAlpha),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = dimens.space8, vertical = dimens.space8),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                HomeNavSlot(
                    item = homeNavItems[0],
                    selected = selectedTab == homeNavItems[0].tab,
                    onClick = { onTabSelected(homeNavItems[0].tab) },
                    modifier = Modifier.weight(1f),
                )
                HomeNavSlot(
                    item = homeNavItems[1],
                    selected = selectedTab == homeNavItems[1].tab,
                    onClick = { onTabSelected(homeNavItems[1].tab) },
                    modifier = Modifier.weight(1f),
                )
                Box(modifier = Modifier.weight(1f))
                HomeNavSlot(
                    item = homeNavItems[2],
                    selected = selectedTab == homeNavItems[2].tab,
                    onClick = { onTabSelected(homeNavItems[2].tab) },
                    modifier = Modifier.weight(1f),
                )
                HomeNavSlot(
                    item = homeNavItems[3],
                    selected = selectedTab == homeNavItems[3].tab,
                    onClick = { onTabSelected(homeNavItems[3].tab) },
                    modifier = Modifier.weight(1f),
                )
            }
        }
        HomeAddFab(
            onClick = onAddClick,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .offset(y = (-dimens.space24)),
        )
    }
}

@Composable
private fun HomeNavSlot(
    item: HomeNavItem,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = ProExpenseTheme.colors
    val dimens = ProExpenseTheme.dimensions
    val typography = ProExpenseTheme.typography
    val motion = ProExpenseTheme.motion
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()
    val scale = if (pressed) motion.pressedScale else 1f
    val tint = if (selected) colors.primary else colors.navInactive
    val labelStyle = if (selected) typography.bodySemiBold else typography.caption

    Column(
        modifier = modifier
            .scale(scale)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick,
            )
            .padding(vertical = dimens.space4),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(dimens.space4),
    ) {
        ProIcon(
            glyph = item.icon,
            contentDescription = item.label,
            tint = tint,
            size = dimens.iconNav,
        )
        Text(
            text = item.label,
            style = labelStyle,
            color = tint,
        )
    }
}

@Composable
private fun HomeAddFab(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = ProExpenseTheme.colors
    val dimens = ProExpenseTheme.dimensions
    val typography = ProExpenseTheme.typography
    val motion = ProExpenseTheme.motion
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()
    val scale = if (pressed) motion.pressedScale else 1f

    Column(
        modifier = modifier.scale(scale),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(dimens.space4),
    ) {
        Box(
            modifier = Modifier
                .size(dimens.fabSize)
                .shadow(8.dp, CircleShape)
                .clip(CircleShape)
                .background(colors.primary)
                .clickable(
                    interactionSource = interactionSource,
                    indication = null,
                    onClick = onClick,
                ),
            contentAlignment = Alignment.Center,
        ) {
            ProIcon(
                glyph = ProIconGlyph.Plus,
                contentDescription = "Add",
                tint = colors.onPrimaryWarm,
                size = dimens.iconNav,
            )
        }
        Text(
            text = "Add",
            style = typography.caption,
            color = colors.primary,
        )
    }
}
