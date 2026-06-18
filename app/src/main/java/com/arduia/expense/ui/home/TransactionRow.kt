package com.arduia.expense.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import com.arduia.expense.feature.logging.LogCategory
import com.arduia.expense.feature.logging.defaultLogCategories
import com.arduia.expense.feature.logging.ui.LogCategoryBadge
import com.arduia.expense.ui.design.IconAt
import com.arduia.expense.ui.theme.ProExpenseTheme

@Composable
fun TransactionRow(
    note: String,
    metaLabel: String,
    amountLabel: String,
    category: LogCategory,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    highlighted: Boolean = false,
    eventTag: String? = null,
) {
    val colors = ProExpenseTheme.colors
    val typography = ProExpenseTheme.typography
    val dims = ProExpenseTheme.dimensions
    val interaction = remember { MutableInteractionSource() }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(ProExpenseTheme.shapes.listRow)
            .background(if (highlighted) colors.primaryTint.copy(alpha = 0.35f) else colors.surface)
            .clickable(interactionSource = interaction, indication = ripple(), onClick = onClick)
            .padding(horizontal = dims.space8, vertical = dims.space12),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(dims.space12),
    ) {
        LogCategoryBadge(category = category)
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = note,
                style = typography.bodyEmphasis,
                color = colors.onSurface,
                maxLines = 1,
            )
            Text(
                text = metaLabel,
                style = typography.caption,
                color = colors.onSurfaceMuted,
                modifier = Modifier.padding(top = dims.space2),
            )
            if (eventTag != null) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(dims.space4),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(top = dims.space4),
                ) {
                    IconAt(color = colors.tag, size = dims.iconSizeInline)
                    Text(
                        text = eventTag,
                        style = typography.caption,
                        color = colors.tag,
                    )
                }
            }
        }
        Text(
            text = amountLabel,
            style = typography.rowAmount,
            color = colors.onSurface,
        )
    }
}

@Preview(showBackground = true, widthDp = 370)
@Composable
private fun TransactionRowPreview() {
    ProExpenseTheme {
        TransactionRow(
            note = "Movie · Dune",
            metaLabel = "Entertainment · 8:10 PM",
            amountLabel = "$18.00",
            category = defaultLogCategories.first { it.id == "entertainment" },
            eventTag = "Bali Trip",
            onClick = {},
        )
    }
}
