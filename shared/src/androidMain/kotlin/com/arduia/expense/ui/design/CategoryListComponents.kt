package com.arduia.expense.ui.design

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.arduia.expense.ui.theme.ProExpenseTheme

@Composable
fun CategorySectionHeader(
    title: String,
    trailing: String,
    modifier: Modifier = Modifier,
) {
    val colors = ProExpenseTheme.colors
    val typography = ProExpenseTheme.typography

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = title,
            style = typography.eyebrow,
            color = colors.muted,
        )
        Text(
            text = trailing,
            style = typography.caption,
            color = colors.onSurfaceMuted,
        )
    }
}

@Composable
fun CategoryListRow(
    categoryId: String,
    label: String,
    modifier: Modifier = Modifier,
    showLockedTag: Boolean = false,
    showDragHandle: Boolean = false,
) {
    val colors = ProExpenseTheme.colors
    val dimens = ProExpenseTheme.dimensions
    val typography = ProExpenseTheme.typography

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = dimens.space14, vertical = dimens.space12),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(dimens.space12),
    ) {
        LogCategoryBadge(categoryId = categoryId)
        Text(
            text = label,
            style = typography.bodyMedium,
            color = colors.onSurface,
            modifier = Modifier.weight(1f),
        )
        when {
            showLockedTag -> StatusPill(text = "LOCKED")
            showDragHandle -> DragHandle()
        }
    }
}

@Composable
private fun StatusPill(
    text: String,
    modifier: Modifier = Modifier,
) {
    val colors = ProExpenseTheme.colors
    val dimens = ProExpenseTheme.dimensions
    val typography = ProExpenseTheme.typography

    Box(
        modifier = modifier
            .clip(ProExpenseTheme.shapes.chip)
            .background(colors.paperAlt)
            .padding(horizontal = dimens.space8, vertical = dimens.space4),
    ) {
        Text(
            text = text,
            style = typography.caption,
            color = colors.onSurfaceMuted,
        )
    }
}

@Composable
private fun DragHandle(modifier: Modifier = Modifier) {
    val colors = ProExpenseTheme.colors
    val dimens = ProExpenseTheme.dimensions

    Column(
        modifier = modifier.width(dimens.space18),
        verticalArrangement = Arrangement.spacedBy(dimens.space4),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        repeat(3) {
            Box(
                modifier = Modifier
                    .size(width = dimens.space14, height = 2.dp)
                    .clip(ProExpenseTheme.shapes.chip)
                    .background(colors.lineStrong),
            )
        }
    }
}

@Composable
fun CreateCategoryButton(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = ProExpenseTheme.colors
    val dimens = ProExpenseTheme.dimensions
    val typography = ProExpenseTheme.typography
    val shape = ProExpenseTheme.shapes.card

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(shape)
            .proClickable(onClick = onClick, shape = shape)
            .padding(dimens.space16),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = "+  $label",
            style = typography.bodyMedium,
            color = colors.onSurfaceMuted,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
fun CategoryListCard(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    val dimens = ProExpenseTheme.dimensions

    Column(
        modifier = modifier
            .fillMaxWidth()
            .proCardSurface(),
    ) {
        content()
    }
}

@Composable
fun CategoryListDivider(modifier: Modifier = Modifier) {
    HorizontalDivider(
        modifier = modifier.padding(start = ProExpenseTheme.dimensions.space14 + 38.dp + 12.dp),
        thickness = 1.dp,
        color = ProExpenseTheme.colors.line,
    )
}
