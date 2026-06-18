package com.arduia.expense.feature.logging.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.arduia.expense.feature.logging.LogCategory
import com.arduia.expense.ui.theme.ProExpenseTheme

@Composable
fun LogCategoryBadge(
    category: LogCategory,
    modifier: Modifier = Modifier,
) {
    val dims = ProExpenseTheme.dimensions
    val tint = Color(category.tintArgb)
    val accent = Color(category.accentArgb)

    Box(
        modifier = modifier
            .size(dims.categoryBadgeSize)
            .clip(CircleShape)
            .background(tint),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = category.label.take(1).uppercase(),
            style = ProExpenseTheme.typography.bodyEmphasis,
            color = accent,
        )
    }
}

@Composable
fun LogCategoryChip(
    category: LogCategory,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = ProExpenseTheme.colors
    val typography = ProExpenseTheme.typography
    val dims = ProExpenseTheme.dimensions
    val shapes = ProExpenseTheme.shapes
    val accent = Color(category.accentArgb)
    val interaction = remember { MutableInteractionSource() }

    Row(
        modifier = modifier
            .clip(shapes.pill)
            .background(if (selected) accent else Color.Transparent)
            .border(
                width = dims.borderWidth,
                color = if (selected) accent else colors.outline,
                shape = shapes.pill,
            )
            .clickable(interactionSource = interaction, indication = ripple(), onClick = onClick)
            .padding(horizontal = dims.space12, vertical = dims.space7),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(dims.space6),
    ) {
        Box(
            modifier = Modifier
                .size(dims.categoryChipIconSize)
                .clip(CircleShape)
                .background(if (selected) Color.White.copy(alpha = 0.2f) else Color(category.tintArgb)),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = category.label.take(1).uppercase(),
                style = typography.caption,
                color = if (selected) colors.onPrimary else accent,
            )
        }
        Text(
            text = category.label,
            style = typography.buttonSmall,
            color = if (selected) colors.onPrimary else colors.onSurfaceVariant,
        )
    }
}
