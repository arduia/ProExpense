package com.arduia.expense.ui.design

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.arduia.expense.ui.theme.ProExpenseTheme

@Composable
fun FilterChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = ProExpenseTheme.colors
    val background = if (selected) colors.ink else Color.Transparent
    val content = if (selected) colors.paperWarm else colors.ink2
    val borderColor = if (selected) colors.ink else colors.lineStrong

    Surface(
        onClick = onClick,
        modifier = modifier,
        shape = ProExpenseTheme.shapes.pill,
        color = background,
        contentColor = content,
        border = BorderStroke(1.dp, borderColor),
    ) {
        Text(
            text = label,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
            style = if (selected) {
                ProExpenseTheme.typography.bodyEmphasis
            } else {
                ProExpenseTheme.typography.body
            },
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
fun TransactionRow(
    note: String,
    meta: String,
    amount: String,
    category: CategoryStyle,
    modifier: Modifier = Modifier,
    eventTag: String? = null,
) {
    val colors = ProExpenseTheme.colors

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        CategoryBadge(label = category.name, style = category)
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = note,
                style = ProExpenseTheme.typography.bodyEmphasis,
                color = colors.ink,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = meta,
                    style = ProExpenseTheme.typography.caption,
                    color = colors.muted,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                if (eventTag != null) {
                    Text(
                        text = "@ $eventTag",
                        style = ProExpenseTheme.typography.caption,
                        color = colors.tag,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
        }
        Text(
            text = amount,
            style = ProExpenseTheme.typography.rowAmount,
            color = colors.ink,
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
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Bottom,
    ) {
        Text(
            text = title,
            style = ProExpenseTheme.typography.sectionHead,
            color = colors.ink,
        )
        Text(
            text = total,
            style = ProExpenseTheme.typography.monoCaption,
            color = colors.muted,
        )
    }
}
