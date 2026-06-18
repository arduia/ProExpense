package com.arduia.expense.ui.design

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.selectable
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
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import com.arduia.expense.ui.theme.ProArtboard
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

private val homeNavLeadingItems = listOf(
    HomeNavItem(HomeNavTab.Home, "Home", ProIconGlyph.Home),
    HomeNavItem(HomeNavTab.Budget, "Budget", ProIconGlyph.Budget),
)

private val homeNavTrailingItems = listOf(
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
    val navElevation = ProExpenseTheme.elevation.nav.firstOrNull()

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = dimens.space16),
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(dimens.navBarHeight)
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
                homeNavLeadingItems.forEach { item ->
                    HomeNavSlot(
                        item = item,
                        selected = selectedTab == item.tab,
                        onClick = { onTabSelected(item.tab) },
                        modifier = Modifier.weight(1f),
                    )
                }
                HomeAddFab(
                    onClick = onAddClick,
                    modifier = Modifier
                        .weight(1f)
                        .offset(y = -dimens.navFabOffset),
                )
                homeNavTrailingItems.forEach { item ->
                    HomeNavSlot(
                        item = item,
                        selected = selectedTab == item.tab,
                        onClick = { onTabSelected(item.tab) },
                        modifier = Modifier.weight(1f),
                    )
                }
            }
        }
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
            .defaultMinSize(minHeight = dimens.touchTargetMin)
            .scale(scale)
            .semantics {
                contentDescription = item.label
            }
            .selectable(
                selected = selected,
                onClick = onClick,
                role = Role.Tab,
                interactionSource = interactionSource,
                indication = null,
            )
            .padding(vertical = dimens.space4),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(dimens.space4),
    ) {
        ProIcon(
            glyph = item.icon,
            contentDescription = null,
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
    val fabElevation = ProExpenseTheme.elevation.fab.firstOrNull()
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()
    val scale = if (pressed) motion.pressedScale else 1f

    Box(
        modifier = modifier
            .defaultMinSize(minHeight = dimens.touchTargetMin)
            .scale(scale),
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier = Modifier
                .size(dimens.fabSize)
                .then(
                    if (fabElevation != null) {
                        Modifier.shadow(
                            elevation = fabElevation.blur,
                            shape = CircleShape,
                            spotColor = fabElevation.color,
                            ambientColor = fabElevation.color,
                        )
                    } else {
                        Modifier
                    },
                )
                .clip(CircleShape)
                .background(colors.primary)
                .semantics { contentDescription = "Add" }
                .clickable(
                    interactionSource = interactionSource,
                    indication = null,
                    onClick = onClick,
                ),
            contentAlignment = Alignment.Center,
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(dimens.space2),
            ) {
                ProIcon(
                    glyph = ProIconGlyph.Plus,
                    contentDescription = null,
                    tint = colors.onPrimaryWarm,
                    size = dimens.iconInline,
                )
                Text(
                    text = "Add",
                    style = typography.caption,
                    color = colors.onPrimaryWarm,
                )
            }
        }
    }
}

@Preview(
    name = "HomeBottomNav — Home active",
    widthDp = ProArtboard.PIXEL_9_PRO_WIDTH_DP,
    heightDp = 160,
    showBackground = true,
)
@Composable
private fun HomeBottomNavHomeActivePreview() {
    ProExpenseTheme {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = ProExpenseTheme.dimensions.space24),
            contentAlignment = Alignment.BottomCenter,
        ) {
            HomeBottomNav(
                selectedTab = HomeNavTab.Home,
                onTabSelected = {},
                onAddClick = {},
            )
        }
    }
}

@Preview(
    name = "HomeBottomNav — Journal active",
    widthDp = ProArtboard.PIXEL_9_PRO_WIDTH_DP,
    heightDp = 160,
    showBackground = true,
)
@Composable
private fun HomeBottomNavJournalActivePreview() {
    ProExpenseTheme {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = ProExpenseTheme.dimensions.space24),
            contentAlignment = Alignment.BottomCenter,
        ) {
            HomeBottomNav(
                selectedTab = HomeNavTab.Journal,
                onTabSelected = {},
                onAddClick = {},
            )
        }
    }
}
