package com.arduia.expense.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.arduia.expense.ui.design.IconFeatDebt
import com.arduia.expense.ui.design.IconFeatEvents
import com.arduia.expense.ui.design.IconFeatReports
import com.arduia.expense.ui.design.IconFeatSplit
import com.arduia.expense.ui.theme.ProExpensePalette
import com.arduia.expense.ui.theme.ProExpenseTheme

enum class QuickAccessDestination {
    Reports,
    Debt,
    Split,
    Events,
}

private data class QuickAccessStyle(
    val tint: Color,
    val accent: Color,
    val icon: @Composable (Color, Modifier) -> Unit,
)

private fun quickAccessStyle(destination: QuickAccessDestination): QuickAccessStyle = when (destination) {
    QuickAccessDestination.Reports -> QuickAccessStyle(
        tint = ProExpensePalette.PrimaryTint,
        accent = ProExpensePalette.PrimaryDeep,
        icon = { color, modifier -> IconFeatReports(color, modifier) },
    )
    QuickAccessDestination.Debt -> QuickAccessStyle(
        tint = ProExpensePalette.SuccessTint,
        accent = ProExpensePalette.Success,
        icon = { color, modifier -> IconFeatDebt(color, modifier) },
    )
    QuickAccessDestination.Split -> QuickAccessStyle(
        tint = ProExpensePalette.TagTint,
        accent = ProExpensePalette.TagDeep,
        icon = { color, modifier -> IconFeatSplit(color, modifier) },
    )
    QuickAccessDestination.Events -> QuickAccessStyle(
        tint = Color(0xFFFFF9C4),
        accent = Color(0xFFF9A825),
        icon = { color, modifier -> IconFeatEvents(color, modifier) },
    )
}

@Composable
fun QuickAccessTile(
    label: String,
    destination: QuickAccessDestination,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = ProExpenseTheme.colors
    val typography = ProExpenseTheme.typography
    val dims = ProExpenseTheme.dimensions
    val shapes = ProExpenseTheme.shapes
    val style = quickAccessStyle(destination)
    val interaction = remember { MutableInteractionSource() }

    Column(
        modifier = modifier
            .clip(shapes.quickTile)
            .border(dims.borderWidth, colors.lineStrong, shapes.quickTile)
            .background(colors.surface)
            .clickable(interactionSource = interaction, indication = ripple(), onClick = onClick)
            .padding(vertical = dims.space12, horizontal = dims.space8),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(dims.space8),
    ) {
        Box(
            modifier = Modifier
                .size(dims.quickAccessTileIconSize)
                .clip(shapes.quickTile)
                .background(style.tint),
            contentAlignment = Alignment.Center,
        ) {
            style.icon(style.accent, Modifier)
        }
        Text(
            text = label,
            style = typography.caption,
            color = colors.onSurfaceVariant,
        )
    }
}

@Preview(showBackground = true, widthDp = 88)
@Composable
private fun QuickAccessTilePreview() {
    ProExpenseTheme {
        QuickAccessTile(
            label = "Reports",
            destination = QuickAccessDestination.Reports,
            onClick = {},
            modifier = Modifier.height(104.dp),
        )
    }
}
