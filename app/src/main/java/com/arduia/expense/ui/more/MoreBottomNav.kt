package com.arduia.expense.ui.more

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.arduia.expense.ui.theme.ProExpenseTheme

enum class BottomTab { Home, Budget, Journal, More }

/**
 * Floating iOS-style tab bar — a detached, fully-rounded bar inset from the screen edges with a
 * raised circular Add action in the centre. See design/DESIGN-SYSTEM.md §8.
 */
@Composable
fun ProExpenseBottomNav(
    activeTab: BottomTab,
    onTabSelect: (BottomTab) -> Unit,
    onAdd: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = ProExpenseTheme.colors

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 18.dp, vertical = 12.dp)
            .shadow(
                elevation = 18.dp,
                shape = ProExpenseTheme.shapes.pill,
                clip = false,
                ambientColor = colors.ink.copy(alpha = 0.12f),
                spotColor = colors.ink.copy(alpha = 0.12f),
            ),
        shape = ProExpenseTheme.shapes.pill,
        color = colors.navBackground,
        border = BorderStroke(1.dp, colors.line),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            NavTab(
                label = "Home",
                active = activeTab == BottomTab.Home,
                onClick = { onTabSelect(BottomTab.Home) },
                modifier = Modifier.weight(1f),
            ) { m, c -> HomeGlyph(color = c, modifier = m) }
            NavTab(
                label = "Budget",
                active = activeTab == BottomTab.Budget,
                onClick = { onTabSelect(BottomTab.Budget) },
                modifier = Modifier.weight(1f),
            ) { m, c -> WalletGlyph(color = c, modifier = m) }

            AddAction(onAdd = onAdd, modifier = Modifier.weight(1f))

            NavTab(
                label = "Journal",
                active = activeTab == BottomTab.Journal,
                onClick = { onTabSelect(BottomTab.Journal) },
                modifier = Modifier.weight(1f),
            ) { m, c -> JournalGlyph(color = c, modifier = m) }
            NavTab(
                label = "More",
                active = activeTab == BottomTab.More,
                onClick = { onTabSelect(BottomTab.More) },
                modifier = Modifier.weight(1f),
            ) { m, c -> MoreDotsGlyph(color = c, modifier = m) }
        }
    }
}

@Composable
private fun NavTab(
    label: String,
    active: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    glyph: @Composable (Modifier, Color) -> Unit,
) {
    val colors = ProExpenseTheme.colors
    val tint = if (active) colors.primary else colors.navText

    Column(
        modifier = modifier
            .clickable(onClick = onClick)
            .padding(vertical = 4.dp)
            .semantics { contentDescription = label },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        glyph(Modifier.size(24.dp), tint)
        Spacer(modifier = Modifier.height(3.dp))
        Text(
            text = label,
            style = ProExpenseTheme.typography.caption.copy(fontSize = 10.sp),
            color = tint,
        )
    }
}

@Composable
private fun AddAction(
    onAdd: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = ProExpenseTheme.colors

    Column(
        modifier = modifier
            .clickable(onClick = onAdd)
            .semantics { contentDescription = "Add" },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Surface(
            modifier = Modifier
                .size(46.dp)
                .shadow(elevation = 10.dp, shape = CircleShape, clip = false),
            shape = CircleShape,
            color = colors.primary,
        ) {
            Box(contentAlignment = Alignment.Center) {
                PlusGlyph(color = colors.paperWarm, modifier = Modifier.size(22.dp))
            }
        }
        Spacer(modifier = Modifier.height(3.dp))
        Text(
            text = "Add",
            style = ProExpenseTheme.typography.caption.copy(fontSize = 10.sp),
            color = colors.primary,
        )
    }
}
