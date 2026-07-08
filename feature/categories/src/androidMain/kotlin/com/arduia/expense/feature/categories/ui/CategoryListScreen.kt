package com.arduia.expense.feature.categories.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.arduia.expense.feature.categories.R
import com.arduia.expense.ui.design.LogCategoryBadge
import com.arduia.expense.ui.design.ProButton
import com.arduia.expense.ui.design.ProButtonSize
import com.arduia.expense.ui.design.ProButtonVariant
import com.arduia.expense.ui.design.ProIcon
import com.arduia.expense.ui.design.ProIconGlyph
import com.arduia.expense.ui.design.ProTopBar
import com.arduia.expense.ui.design.ProTopBarAction
import com.arduia.expense.ui.design.proClickable
import com.arduia.expense.feature.categories.ui.preview.CategoryListUiState
import com.arduia.expense.feature.categories.ui.preview.CategoryRowUi
import com.arduia.expense.feature.categories.ui.preview.previewCategoryList
import com.arduia.expense.ui.theme.ProArtboard
import com.arduia.expense.ui.theme.ProExpenseTheme

@Composable
fun CategoryListScreen(
    state: CategoryListUiState,
    onBack: () -> Unit,
    onCreate: () -> Unit,
    modifier: Modifier = Modifier,
    onCustomRowClick: (CategoryRowUi) -> Unit = {},
    onReorder: (Int, Int) -> Unit = { _, _ -> },
) {
    val colors = ProExpenseTheme.colors
    val dimens = ProExpenseTheme.dimensions
    val typography = ProExpenseTheme.typography

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(colors.paper)
            .statusBarsPadding()
            .navigationBarsPadding(),
    ) {
        Box(modifier = Modifier.padding(horizontal = dimens.screenPadding)) {
            ProTopBar(
                title = stringResource(R.string.categories_title),
                onBack = onBack,
                backLabel = stringResource(R.string.categories_back),
                action = ProTopBarAction.Add,
                onAction = onCreate,
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = dimens.screenPadding)
                .padding(top = dimens.space8, bottom = dimens.space24),
            verticalArrangement = Arrangement.spacedBy(dimens.space16),
        ) {
            CategorySectionHeader(
                label = stringResource(R.string.categories_default_label),
                trailing = stringResource(R.string.categories_locked_word),
            )
            CategoryGroupCard(rows = state.defaults) { row ->
                CategoryRow(row = row, locked = true)
            }

            CategorySectionHeader(
                label = stringResource(R.string.categories_custom_label),
                trailing = pluralStringResource(
                    R.plurals.categories_count,
                    state.custom.size,
                    state.custom.size,
                ),
            )
            ReorderableCustomGroup(
                rows = state.custom,
                onRowClick = onCustomRowClick,
                onReorder = onReorder,
            )

            ProButton(
                text = stringResource(R.string.categories_create),
                onClick = onCreate,
                variant = ProButtonVariant.Secondary,
                size = ProButtonSize.Lg,
                fillMaxWidth = true,
                leading = {
                    ProIcon(
                        glyph = ProIconGlyph.Plus,
                        contentDescription = null,
                        tint = colors.onSurface,
                        size = dimens.iconInline,
                    )
                },
            )

            Text(
                text = stringResource(R.string.categories_footer),
                style = typography.caption,
                color = colors.onSurfaceMuted,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

@Composable
private fun CategorySectionHeader(label: String, trailing: String) {
    val colors = ProExpenseTheme.colors
    val typography = ProExpenseTheme.typography

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(text = label, style = typography.eyebrow, color = colors.onSurfaceVariant)
        Text(text = trailing, style = typography.caption, color = colors.onSurfaceMuted)
    }
}

@Composable
private fun CategoryGroupCard(
    rows: List<CategoryRowUi>,
    row: @Composable (CategoryRowUi) -> Unit,
) {
    val colors = ProExpenseTheme.colors

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(ProExpenseTheme.shapes.card)
            .border(BorderStroke(1.dp, colors.line), ProExpenseTheme.shapes.card)
            .background(colors.surface),
    ) {
        rows.forEachIndexed { index, item ->
            if (index > 0) {
                Box(Modifier.fillMaxWidth().height(1.dp).background(colors.lineSoft))
            }
            row(item)
        }
    }
}

@Composable
private fun CategoryRow(row: CategoryRowUi, locked: Boolean) {
    val colors = ProExpenseTheme.colors
    val dimens = ProExpenseTheme.dimensions
    val typography = ProExpenseTheme.typography

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = dimens.space14, vertical = dimens.space12),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(dimens.space12),
    ) {
        LogCategoryBadge(categoryId = row.categoryId, iconId = row.iconId, size = dimens.iconBadge)
        Text(
            text = row.label,
            style = typography.bodyMedium,
            color = colors.onSurface,
            modifier = Modifier.weight(1f),
        )
        if (locked) {
            Text(
                text = stringResource(R.string.categories_locked_pill),
                style = typography.eyebrow,
                color = colors.onSurfaceMuted,
                modifier = Modifier
                    .border(BorderStroke(1.dp, colors.lineStrong), ProExpenseTheme.shapes.chip)
                    .padding(horizontal = dimens.space10, vertical = dimens.space4),
            )
        } else {
            DragHandle()
        }
    }
}

@Composable
private fun ReorderableCustomGroup(
    rows: List<CategoryRowUi>,
    onRowClick: (CategoryRowUi) -> Unit,
    onReorder: (Int, Int) -> Unit,
) {
    val colors = ProExpenseTheme.colors

    var draggingIndex by remember { mutableStateOf<Int?>(null) }
    var dragOffsetY by remember { mutableFloatStateOf(0f) }
    var itemHeightPx by remember { mutableFloatStateOf(0f) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(ProExpenseTheme.shapes.card)
            .border(BorderStroke(1.dp, colors.line), ProExpenseTheme.shapes.card)
            .background(colors.surface),
    ) {
        rows.forEachIndexed { index, item ->
            key(item.label) {
                val dragging = index == draggingIndex
                Column(
                    modifier = Modifier
                        .zIndex(if (dragging) 1f else 0f)
                        .graphicsLayer { translationY = if (dragging) dragOffsetY else 0f }
                        .onSizeChanged { if (it.height > 0) itemHeightPx = it.height.toFloat() },
                ) {
                    if (index > 0) {
                        Box(Modifier.fillMaxWidth().height(1.dp).background(colors.lineSoft))
                    }
                    CustomCategoryRow(
                        row = item,
                        onClick = { onRowClick(item) },
                        dragHandleModifier = Modifier.pointerInput(rows.size) {
                            detectDragGesturesAfterLongPress(
                                onDragStart = { draggingIndex = index; dragOffsetY = 0f },
                                onDragEnd = { draggingIndex = null; dragOffsetY = 0f },
                                onDragCancel = { draggingIndex = null; dragOffsetY = 0f },
                                onDrag = { change, drag ->
                                    change.consume()
                                    val from = draggingIndex
                                    if (from != null) {
                                        dragOffsetY += drag.y
                                        val h = itemHeightPx
                                        if (h > 0f) {
                                            if (dragOffsetY > h / 2f && from < rows.lastIndex) {
                                                onReorder(from, from + 1)
                                                draggingIndex = from + 1
                                                dragOffsetY -= h
                                            } else if (dragOffsetY < -h / 2f && from > 0) {
                                                onReorder(from, from - 1)
                                                draggingIndex = from - 1
                                                dragOffsetY += h
                                            }
                                        }
                                    }
                                },
                            )
                        },
                    )
                }
            }
        }
    }
}

@Composable
private fun CustomCategoryRow(
    row: CategoryRowUi,
    onClick: () -> Unit,
    dragHandleModifier: Modifier,
) {
    val colors = ProExpenseTheme.colors
    val dimens = ProExpenseTheme.dimensions
    val typography = ProExpenseTheme.typography

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .proClickable(onClick = onClick, shape = RectangleShape, scaleOnPress = false)
            .padding(horizontal = dimens.space14, vertical = dimens.space12),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(dimens.space12),
    ) {
        LogCategoryBadge(categoryId = row.categoryId, iconId = row.iconId, size = dimens.iconBadge)
        Text(
            text = row.label,
            style = typography.bodyMedium,
            color = colors.onSurface,
            modifier = Modifier.weight(1f),
        )
        Box(
            modifier = dragHandleModifier.padding(start = dimens.space8, top = dimens.space4, bottom = dimens.space4),
            contentAlignment = Alignment.Center,
        ) {
            DragHandle()
        }
    }
}

@Composable
private fun DragHandle() {
    val colors = ProExpenseTheme.colors
    val dimens = ProExpenseTheme.dimensions

    Column(
        modifier = Modifier.size(dimens.space20),
        verticalArrangement = Arrangement.spacedBy(3.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        repeat(3) {
            Box(
                modifier = Modifier
                    .width(dimens.space16)
                    .height(1.5.dp)
                    .clip(RoundedCornerShape(1.dp))
                    .background(colors.muted),
            )
        }
    }
}

@Preview(
    name = "Categories — default + custom",
    widthDp = ProArtboard.PIXEL_9_PRO_WIDTH_DP,
    heightDp = ProArtboard.PIXEL_9_PRO_HEIGHT_DP,
    showBackground = true,
)
@Composable
private fun CategoryListPreview() {
    ProExpenseTheme {
        CategoryListScreen(previewCategoryList, {}, {})
    }
}
