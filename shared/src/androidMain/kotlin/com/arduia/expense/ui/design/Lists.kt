package com.arduia.expense.ui.design

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import com.arduia.expense.ui.theme.ProExpenseTheme

@Composable
fun TransactionRow(
    categoryId: String,
    note: String,
    meta: String,
    amount: String,
    modifier: Modifier = Modifier,
    tag: String? = null,
) {
    val colors = ProExpenseTheme.colors
    val dimens = ProExpenseTheme.dimensions
    val typography = ProExpenseTheme.typography

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = dimens.space12),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(dimens.space12),
    ) {
        LogCategoryBadge(categoryId = categoryId)
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = note.ifBlank { meta.substringBefore(" ·") },
                style = typography.bodyMedium,
                color = colors.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = meta,
                style = typography.caption,
                color = colors.onSurfaceMuted,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            if (tag != null) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(dimens.space4),
                    modifier = Modifier.padding(top = dimens.space4),
                ) {
                    ProIcon(
                        glyph = ProIconGlyph.At,
                        contentDescription = null,
                        tint = colors.tag,
                        size = dimens.iconInline,
                    )
                    Text(
                        text = tag,
                        style = typography.caption,
                        color = colors.tag,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
        }
        Text(
            text = amount,
            style = typography.listAmount,
            color = colors.onSurface,
        )
    }
}

@Composable
fun DayHeader(
    title: String,
    total: String,
    modifier: Modifier = Modifier,
) {
    val colors = ProExpenseTheme.colors
    val dimens = ProExpenseTheme.dimensions
    val typography = ProExpenseTheme.typography

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = dimens.space8),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = title,
            style = typography.sectionHead,
            color = colors.onSurface,
        )
        Text(
            text = total,
            style = typography.tabTimestamp,
            color = colors.muted,
        )
    }
}
