package com.arduia.expense.ui.design

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.arduia.expense.shared.R
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
    val labelRes: Int,
    val icon: ProIconGlyph,
)

private val homeNavLeadingItems = listOf(
    HomeNavItem(HomeNavTab.Home, R.string.nav_home, ProIconGlyph.Home),
    HomeNavItem(HomeNavTab.Budget, R.string.nav_budget, ProIconGlyph.Budget),
)

private val homeNavTrailingItems = listOf(
    HomeNavItem(HomeNavTab.Journal, R.string.nav_journal, ProIconGlyph.Journal),
    HomeNavItem(HomeNavTab.More, R.string.nav_more, ProIconGlyph.More),
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
            .height(dimens.navFabOffset + dimens.navBarHeight + dimens.navBottomMargin),
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(dimens.navBarHeight)
                .align(Alignment.BottomCenter)
                .padding(bottom = dimens.navBottomMargin)
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
            tonalElevation = 0.dp,
            shadowElevation = 0.dp,
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(
                        horizontal = dimens.space8,
                        vertical = dimens.navItemPaddingVertical,
                    ),
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
                Spacer(modifier = Modifier.weight(1f))
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
        HomeAddFab(
            onClick = onAddClick,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .offset(y = dimens.navFabDropOffset),
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
    val interactionSource = remember { MutableInteractionSource() }
    val tint = if (selected) colors.primary else colors.navInactive
    val label = stringResource(item.labelRes)
    val labelStyle = if (selected) {
        typography.caption.copy(
            fontFamily = typography.monoFamily,
            fontWeight = FontWeight.SemiBold,
        )
    } else {
        typography.caption.copy(fontFamily = typography.monoFamily)
    }

    Column(
        modifier = modifier
            .defaultMinSize(minHeight = dimens.touchTargetMin),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Column(
            modifier = Modifier
                .semantics { contentDescription = label }
                .proSelectable(
                    selected = selected,
                    onClick = onClick,
                    shape = RoundedCornerShape(dimens.navCornerRadius),
                    interactionSource = interactionSource,
                    role = Role.Tab,
                    showRipple = false,
                )
                .padding(horizontal = dimens.space8),
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
                text = label,
                style = labelStyle,
                color = tint,
            )
        }
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
    val fabElevation = ProExpenseTheme.elevation.fab.firstOrNull()
    val interactionSource = remember { MutableInteractionSource() }
    val addContentDescription = stringResource(R.string.nav_add)

    Box(
        modifier = modifier
            .defaultMinSize(minHeight = dimens.touchTargetMin)
            .proPressScale(interactionSource),
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
                .semantics { contentDescription = addContentDescription }
                .proCircularRippleClickable(
                    onClick = onClick,
                    interactionSource = interactionSource,
                    role = Role.Button,
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
                    size = dimens.iconNav,
                )
                Text(
                    text = stringResource(R.string.nav_add),
                    style = typography.caption.copy(fontFamily = typography.monoFamily),
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
