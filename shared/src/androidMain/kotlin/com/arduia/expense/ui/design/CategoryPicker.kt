package com.arduia.expense.ui.design

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.arduia.expense.shared.R
import com.arduia.expense.ui.theme.ProExpenseTheme

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CategoryPicker(
    defaultCategories: List<Pair<String, String>>,
    customCategories: List<Pair<String, String>>,
    selectedCategoryId: String,
    onCategorySelected: (String) -> Unit,
    modifier: Modifier = Modifier,
    showCustomSection: Boolean = true,
    onMoreClick: (() -> Unit)? = null,
    showAddChip: Boolean = false,
    onAddClick: () -> Unit = {},
    // Non-null caps that section to a fixed row count with horizontal swipe-to-see-more instead
    // of wrapping onto as many lines as needed — used by screens that must keep a fixed-height
    // control (e.g. a keypad) pinned below the picker. Null preserves the original wrap behavior.
    defaultSectionRows: Int? = null,
    customSectionRows: Int? = null,
    // Horizontal inset applied to section titles and chip rows. Leave 0 (default) when the caller
    // already wraps this picker in its own horizontal screen padding. Set to the screen's margin
    // when the caller instead renders this picker full-bleed, so a swipeable carousel row (see
    // defaultSectionRows/customSectionRows) can scroll all the way to the true screen edge instead
    // of being clipped at the margin — the inset is reproduced as inner spacing so the at-rest
    // layout still lines up with the rest of the screen.
    carouselEdgeInset: Dp = 0.dp,
) {
    val colors = ProExpenseTheme.colors
    val dimens = ProExpenseTheme.dimensions
    val typography = ProExpenseTheme.typography

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(dimens.space12),
    ) {
        CategoryPickerSection(
            title = stringResource(R.string.category_section_default),
            titlePadding = carouselEdgeInset,
            trailing =
                if (onMoreClick != null && !showCustomSection) {
                    {
                        ProTextAction(
                            text = stringResource(R.string.category_picker_more),
                            onClick = onMoreClick,
                            style = typography.bodyMedium,
                            color = colors.primary,
                        )
                    }
                } else {
                    null
                },
        ) {
            if (defaultSectionRows != null) {
                CategoryChipCarousel(
                    categories = defaultCategories,
                    rows = defaultSectionRows,
                    selectedCategoryId = selectedCategoryId,
                    onCategorySelected = onCategorySelected,
                    edgeInset = carouselEdgeInset,
                )
            } else {
                FlowRow(
                    modifier = Modifier.padding(horizontal = carouselEdgeInset),
                    horizontalArrangement = Arrangement.spacedBy(dimens.space8),
                    verticalArrangement = Arrangement.spacedBy(dimens.space8),
                ) {
                    defaultCategories.forEach { (id, label) ->
                        CategoryChip(
                            label = label,
                            categoryId = id,
                            selected = id == selectedCategoryId,
                            onClick = { onCategorySelected(id) },
                        )
                    }
                }
            }
        }

        if (showCustomSection && (customCategories.isNotEmpty() || showAddChip)) {
            CategoryPickerSection(
                title = stringResource(R.string.category_section_custom),
                titlePadding = carouselEdgeInset,
            ) {
                if (customSectionRows != null) {
                    CategoryChipCarousel(
                        categories = customCategories,
                        rows = customSectionRows,
                        selectedCategoryId = selectedCategoryId,
                        onCategorySelected = onCategorySelected,
                        edgeInset = carouselEdgeInset,
                        trailingChip = if (showAddChip) ({ AddCategoryChip(onClick = onAddClick) }) else null,
                    )
                } else {
                    FlowRow(
                        modifier = Modifier.padding(horizontal = carouselEdgeInset),
                        horizontalArrangement = Arrangement.spacedBy(dimens.space8),
                        verticalArrangement = Arrangement.spacedBy(dimens.space8),
                    ) {
                        customCategories.forEach { (id, label) ->
                            CategoryChip(
                                label = label,
                                categoryId = id,
                                selected = id == selectedCategoryId,
                                onClick = { onCategorySelected(id) },
                            )
                        }
                        if (showAddChip) {
                            AddCategoryChip(onClick = onAddClick)
                        }
                    }
                }
            }
        }
    }
}

/**
 * Distributes [categories] round-robin across [rows] independent horizontal rows (row-major —
 * matches natural left-to-right, top-to-bottom reading order, same as a wrapping [FlowRow] would),
 * each packed tightly at its own natural width and sharing one [ScrollState] so swiping moves
 * every row together. Column-major chunking (grouping consecutive items into a shared-width
 * column) was tried first, but a column's width is set by its widest chip, so a shorter chip
 * sharing a column with a longer one left a stray gap before the next column — packing each row
 * independently avoids that entirely.
 */
@Composable
private fun CategoryChipCarousel(
    categories: List<Pair<String, String>>,
    rows: Int,
    selectedCategoryId: String,
    onCategorySelected: (String) -> Unit,
    modifier: Modifier = Modifier,
    edgeInset: Dp = 0.dp,
    trailingChip: (@Composable () -> Unit)? = null,
) {
    val dimens = ProExpenseTheme.dimensions
    val scrollState = rememberScrollState()
    val rowItems =
        List(rows) { rowIndex ->
            categories.filterIndexed { index, _ -> index % rows == rowIndex }
        }

    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(dimens.space8)) {
        rowItems.forEachIndexed { rowIndex, items ->
            Row(
                modifier = Modifier.fillMaxWidth().horizontalScroll(scrollState),
                horizontalArrangement = Arrangement.spacedBy(dimens.space8),
            ) {
                if (edgeInset > 0.dp) {
                    Spacer(Modifier.width(edgeInset))
                }
                items.forEach { (id, label) ->
                    CategoryChip(
                        label = label,
                        categoryId = id,
                        selected = id == selectedCategoryId,
                        onClick = { onCategorySelected(id) },
                    )
                }
                if (rowIndex == rows - 1) {
                    trailingChip?.invoke()
                }
                if (edgeInset > 0.dp) {
                    Spacer(Modifier.width(edgeInset))
                }
            }
        }
    }
}

@Composable
private fun CategoryPickerSection(
    title: String,
    modifier: Modifier = Modifier,
    titlePadding: Dp = 0.dp,
    trailing: (@Composable () -> Unit)? = null,
    content: @Composable () -> Unit,
) {
    val colors = ProExpenseTheme.colors
    val dimens = ProExpenseTheme.dimensions
    val typography = ProExpenseTheme.typography

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(dimens.space8),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = titlePadding),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(text = title, style = typography.eyebrow, color = colors.onSurfaceMuted)
            trailing?.invoke()
        }
        content()
    }
}

@Composable
private fun AddCategoryChip(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = ProExpenseTheme.colors
    val dimens = ProExpenseTheme.dimensions
    val typography = ProExpenseTheme.typography
    val chipShape = ProExpenseTheme.shapes.chip
    val interactionSource = remember { MutableInteractionSource() }
    val textStyle = typography.bodyMedium.copy(fontSize = 12.5.sp)

    Row(
        modifier =
            modifier
                .proPressScale(interactionSource)
                .clip(chipShape)
                .border(BorderStroke(dimens.chipBorderWidth, colors.lineStrong), chipShape)
                .proRippleClickable(onClick = onClick, interactionSource = interactionSource)
                .padding(start = dimens.space8, top = 7.dp, end = dimens.space12, bottom = 7.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(dimens.space6),
    ) {
        ProIcon(
            glyph = ProIconGlyph.Plus,
            contentDescription = null,
            tint = colors.onSurfaceVariant,
            size = dimens.iconChipLeading,
        )
        Text(text = stringResource(R.string.category_picker_add), style = textStyle, color = colors.onSurfaceVariant)
    }
}
