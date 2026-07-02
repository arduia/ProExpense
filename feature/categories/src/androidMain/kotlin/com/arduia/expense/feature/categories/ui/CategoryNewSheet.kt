package com.arduia.expense.feature.categories.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.arduia.expense.feature.categories.R
import com.arduia.expense.ui.design.ProBottomSheetHost
import com.arduia.expense.ui.design.ProButton
import com.arduia.expense.ui.design.ProButtonSize
import com.arduia.expense.ui.design.ProButtonVariant
import com.arduia.expense.ui.design.ProIcon
import com.arduia.expense.ui.design.ProIconGlyph
import com.arduia.expense.ui.design.categoryIcon
import com.arduia.expense.ui.design.proClickable
import com.arduia.expense.feature.categories.ui.preview.CATEGORY_NAME_MAX
import com.arduia.expense.feature.categories.ui.preview.CategoryNewFormState
import com.arduia.expense.feature.categories.ui.preview.previewCategoryList
import com.arduia.expense.feature.categories.ui.preview.previewCategoryNewDuplicate
import com.arduia.expense.ui.theme.ProArtboard
import com.arduia.expense.ui.theme.ProExpenseTheme

@Composable
fun CategoryNewSheetContent(
    form: CategoryNewFormState,
    onNameChange: (String) -> Unit,
    onIconSelected: (String) -> Unit,
    onAdd: () -> Unit,
    modifier: Modifier = Modifier,
    confirmLabel: String = stringResource(R.string.categories_add),
) {
    val colors = ProExpenseTheme.colors
    val dimens = ProExpenseTheme.dimensions
    val typography = ProExpenseTheme.typography
    val borderColor = if (form.duplicate) colors.danger else colors.lineStrong

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(dimens.space12),
    ) {
        BasicTextField(
            value = form.name,
            onValueChange = { onNameChange(it.take(CATEGORY_NAME_MAX)) },
            modifier = Modifier
                .fillMaxWidth()
                .clip(ProExpenseTheme.shapes.searchField)
                .border(BorderStroke(1.dp, borderColor), ProExpenseTheme.shapes.searchField)
                .background(colors.surface)
                .padding(horizontal = dimens.space14, vertical = dimens.space14),
            textStyle = typography.body.copy(color = colors.onSurface),
            cursorBrush = SolidColor(colors.primary),
            singleLine = true,
            decorationBox = { inner ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.weight(1f)) {
                        if (form.name.isEmpty()) {
                            Text(
                                text = stringResource(R.string.categories_name_placeholder),
                                style = typography.body,
                                color = colors.muted,
                            )
                        }
                        inner()
                    }
                    Text(
                        text = stringResource(
                            R.string.categories_name_counter,
                            form.name.length,
                            CATEGORY_NAME_MAX,
                        ),
                        style = typography.caption,
                        color = if (form.duplicate) colors.danger else colors.onSurfaceMuted,
                        modifier = Modifier.padding(start = dimens.space8),
                    )
                }
            },
        )

        if (form.duplicate) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(dimens.space6),
            ) {
                ProIcon(
                    glyph = ProIconGlyph.Close,
                    contentDescription = null,
                    tint = colors.danger,
                    size = dimens.iconClear,
                )
                Text(
                    text = stringResource(R.string.categories_duplicate_error),
                    style = typography.caption,
                    color = colors.danger,
                )
            }
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(dimens.space12),
        ) {
            Text(
                text = stringResource(R.string.categories_icon_label),
                style = typography.bodyMedium,
                color = colors.onSurfaceMuted,
            )
            form.iconOptions.forEach { iconId ->
                CategoryIconTile(
                    iconId = iconId,
                    selected = iconId == form.selectedIconId,
                    onClick = { onIconSelected(iconId) },
                )
            }
        }

        ProButton(
            text = confirmLabel,
            onClick = onAdd,
            variant = ProButtonVariant.Primary,
            size = ProButtonSize.Lg,
            fillMaxWidth = true,
            enabled = form.canAdd,
            modifier = Modifier.padding(top = dimens.space4),
        )
    }
}

@Composable
private fun CategoryIconTile(
    iconId: String,
    selected: Boolean,
    onClick: () -> Unit,
) {
    val colors = ProExpenseTheme.colors
    val dimens = ProExpenseTheme.dimensions
    val shape = ProExpenseTheme.shapes.tile
    val background = if (selected) colors.primaryTint else colors.paperAlt
    val border = if (selected) colors.primary else colors.lineStrong

    Box(
        modifier = Modifier
            .size(dimens.space44)
            .clip(shape)
            .border(BorderStroke(1.dp, border), shape)
            .background(background)
            .proClickable(onClick = onClick, shape = shape),
        contentAlignment = Alignment.Center,
    ) {
        ProIcon(
            glyph = categoryIcon(iconId),
            contentDescription = null,
            tint = if (selected) colors.primary else colors.onSurfaceMuted,
            size = dimens.iconNav,
        )
    }
}

@Preview(
    name = "Categories — new (duplicate)",
    widthDp = ProArtboard.PIXEL_9_PRO_WIDTH_DP,
    heightDp = ProArtboard.PIXEL_9_PRO_HEIGHT_DP,
    showBackground = true,
)
@Composable
private fun CategoryNewDuplicatePreview() {
    ProExpenseTheme {
        Box(Modifier.fillMaxSize()) {
            CategoryListScreen(previewCategoryList, {}, {})
            ProBottomSheetHost(
                visible = true,
                title = stringResource(R.string.categories_new_title),
                onClose = {},
            ) {
                CategoryNewSheetContent(
                    form = previewCategoryNewDuplicate,
                    onNameChange = {},
                    onIconSelected = {},
                    onAdd = {},
                )
            }
        }
    }
}
