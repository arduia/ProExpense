package com.arduia.expense.ui.design

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.arduia.expense.ui.theme.ProExpenseTheme

@Composable
fun MoreHubRow(
    title: String,
    subtitle: String?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ProIconGlyph? = null,
    iconTint: Color = ProExpenseTheme.colors.primary,
    iconBackground: Color = ProExpenseTheme.colors.primarySoft,
) {
    val colors = ProExpenseTheme.colors
    val dimens = ProExpenseTheme.dimensions
    val typography = ProExpenseTheme.typography

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(ProExpenseTheme.shapes.card)
            .border(BorderStroke(1.dp, colors.line), ProExpenseTheme.shapes.card)
            .background(colors.surface)
            .proClickable(onClick = onClick, shape = ProExpenseTheme.shapes.card)
            .padding(dimens.space14),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(dimens.space12),
    ) {
        if (icon != null) {
            Box(
                modifier = Modifier
                    .size(dimens.quickAccessIconSize)
                    .clip(ProExpenseTheme.shapes.tile)
                    .background(iconBackground),
                contentAlignment = Alignment.Center,
            ) {
                ProIcon(
                    glyph = icon,
                    contentDescription = null,
                    tint = iconTint,
                    size = dimens.iconInline + 4.dp,
                )
            }
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = typography.bodySemiBold,
                color = colors.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            if (subtitle != null) {
                Text(
                    text = subtitle,
                    style = typography.caption,
                    color = colors.onSurfaceMuted,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(top = dimens.space2),
                )
            }
        }
        ProIcon(
            glyph = ProIconGlyph.ChevronRight,
            contentDescription = null,
            tint = colors.muted,
            size = dimens.iconInline,
        )
    }
}

@Composable
fun DebtRecordRow(
    personName: String,
    amount: String,
    dueLabel: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isSettled: Boolean = false,
    isLent: Boolean = true,
) {
    val colors = ProExpenseTheme.colors
    val dimens = ProExpenseTheme.dimensions
    val typography = ProExpenseTheme.typography
    val iconBackground = if (isLent) colors.successTint else colors.dangerTint
    val iconTint = if (isLent) colors.success else colors.danger

    Row(
        modifier = modifier
            .fillMaxWidth()
            .proClickable(onClick = onClick, shape = ProExpenseTheme.shapes.searchField)
            .padding(vertical = dimens.space12),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(dimens.space12),
    ) {
        Box(
            modifier = Modifier
                .size(dimens.quickAccessIconSize)
                .clip(ProExpenseTheme.shapes.tile)
                .background(iconBackground),
            contentAlignment = Alignment.Center,
        ) {
            ProIcon(
                glyph = ProIconGlyph.User,
                contentDescription = null,
                tint = iconTint,
                size = dimens.iconInline + 4.dp,
            )
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = personName,
                style = typography.bodySemiBold,
                color = if (isSettled) colors.muted else colors.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = dueLabel,
                style = typography.caption,
                color = colors.onSurfaceMuted,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
        Text(
            text = amount,
            style = typography.listAmount,
            color = if (isSettled) colors.muted else colors.onSurface,
        )
    }
}

@Composable
fun SharedPersonRow(
    name: String,
    amount: String,
    modifier: Modifier = Modifier,
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
        Box(
            modifier = Modifier
                .size(dimens.space32)
                .clip(ProExpenseTheme.shapes.tile)
                .background(colors.tagSoft),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = name.firstOrNull()?.uppercaseChar()?.toString().orEmpty(),
                style = typography.bodySemiBold,
                color = colors.tagDeep,
            )
        }
        Text(
            text = name,
            style = typography.bodyMedium,
            color = colors.onSurface,
            modifier = Modifier.weight(1f),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        Text(
            text = amount,
            style = typography.listAmount,
            color = colors.onSurface,
        )
    }
}

@Composable
fun ReportsCategoryRow(
    categoryId: String,
    categoryLabel: String,
    amount: String,
    progress: Float,
    modifier: Modifier = Modifier,
) {
    val colors = ProExpenseTheme.colors
    val dimens = ProExpenseTheme.dimensions
    val typography = ProExpenseTheme.typography

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = dimens.space8),
        verticalArrangement = Arrangement.spacedBy(dimens.space8),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(dimens.space12),
        ) {
            LogCategoryBadge(categoryId = categoryId, size = dimens.iconBadge)
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = categoryLabel,
                    style = typography.bodyMedium,
                    color = colors.onSurface,
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = dimens.space8)
                        .clip(ProExpenseTheme.shapes.chip)
                        .background(colors.paperAlt),
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(progress.coerceIn(0.05f, 1f))
                            .padding(vertical = 3.dp)
                            .clip(ProExpenseTheme.shapes.chip)
                            .background(colors.primary),
                    )
                }
            }
            Text(
                text = amount,
                style = typography.listAmount,
                color = colors.onSurface,
            )
        }
    }
}
