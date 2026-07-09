package com.arduia.expense.feature.categories.ui

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.arduia.expense.feature.categories.R
import com.arduia.expense.feature.categories.ui.preview.CategoryRowUi
import com.arduia.expense.ui.design.LogCategoryBadge
import com.arduia.expense.ui.design.ProBottomSheetHost
import com.arduia.expense.ui.design.ProIcon
import com.arduia.expense.ui.design.ProIconGlyph
import com.arduia.expense.ui.design.proClickable
import com.arduia.expense.ui.theme.ProArtboard
import com.arduia.expense.ui.theme.ProExpenseTheme

@Composable
fun CategoryActionsSheetContent(
    row: CategoryRowUi,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = ProExpenseTheme.colors
    val dimens = ProExpenseTheme.dimensions
    val typography = ProExpenseTheme.typography

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(dimens.space12),
    ) {
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .clip(ProExpenseTheme.shapes.card)
                    .border(BorderStroke(1.dp, colors.line), ProExpenseTheme.shapes.card)
                    .background(colors.surface)
                    .padding(dimens.space14),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(dimens.space12),
        ) {
            LogCategoryBadge(categoryId = row.categoryId, size = dimens.iconBadge)
            Text(
                text = row.label,
                style = typography.bodySemiBold,
                color = colors.onSurface,
            )
        }

        CategoryActionRow(
            icon = ProIconGlyph.Note,
            iconTint = colors.onSurfaceVariant,
            iconBackground = colors.paperAlt,
            title = stringResource(R.string.categories_action_edit_title),
            titleColor = colors.onSurface,
            subtitle = stringResource(R.string.categories_action_edit_subtitle),
            onClick = onEdit,
        )
        CategoryActionRow(
            icon = ProIconGlyph.Close,
            iconTint = colors.danger,
            iconBackground = colors.dangerTint,
            title = stringResource(R.string.categories_action_delete_title),
            titleColor = colors.danger,
            subtitle = stringResource(R.string.categories_action_delete_subtitle),
            onClick = onDelete,
        )
        Box(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .clip(ProExpenseTheme.shapes.card)
                    .border(BorderStroke(1.dp, colors.line), ProExpenseTheme.shapes.card)
                    .background(colors.surface)
                    .proClickable(onClick = onCancel, shape = ProExpenseTheme.shapes.card)
                    .padding(vertical = dimens.space16),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = stringResource(R.string.categories_action_cancel),
                style = typography.bodySemiBold,
                color = colors.onSurface,
            )
        }
    }
}

@Composable
private fun CategoryActionRow(
    icon: ProIconGlyph,
    iconTint: Color,
    iconBackground: Color,
    title: String,
    titleColor: Color,
    subtitle: String,
    onClick: () -> Unit,
) {
    val colors = ProExpenseTheme.colors
    val dimens = ProExpenseTheme.dimensions
    val typography = ProExpenseTheme.typography

    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .clip(ProExpenseTheme.shapes.card)
                .border(BorderStroke(1.dp, colors.line), ProExpenseTheme.shapes.card)
                .background(colors.surface)
                .proClickable(onClick = onClick, shape = ProExpenseTheme.shapes.card)
                .padding(dimens.space14),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(dimens.space12),
    ) {
        Box(
            modifier =
                Modifier
                    .size(dimens.iconBadge)
                    .clip(CircleShape)
                    .background(iconBackground),
            contentAlignment = Alignment.Center,
        ) {
            ProIcon(
                glyph = icon,
                contentDescription = null,
                tint = iconTint,
                size = dimens.iconInline,
            )
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, style = typography.bodySemiBold, color = titleColor)
            Text(
                text = subtitle,
                style = typography.caption,
                color = colors.onSurfaceMuted,
                modifier = Modifier.padding(top = dimens.space2),
            )
        }
    }
}

@Preview(
    name = "Categories — row actions",
    widthDp = ProArtboard.PIXEL_9_PRO_WIDTH_DP,
    heightDp = ProArtboard.PIXEL_9_PRO_HEIGHT_DP,
    showBackground = true,
)
@Composable
private fun CategoryActionsSheetPreview() {
    ProExpenseTheme {
        ProBottomSheetHost(
            visible = true,
            title = null,
            onClose = {},
        ) {
            CategoryActionsSheetContent(
                row = CategoryRowUi("coffee", "Coffee runs"),
                onEdit = {},
                onDelete = {},
                onCancel = {},
            )
        }
    }
}
