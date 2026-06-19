package com.arduia.expense.ui.design

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import com.arduia.expense.ui.theme.ProExpenseTheme

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CategoryPicker(
    defaultCategories: List<Pair<String, String>>,
    customCategories: List<Pair<String, String>>,
    selectedCategoryId: String,
    onCategorySelected: (String) -> Unit,
    modifier: Modifier = Modifier,
    onMoreClick: (() -> Unit)? = null,
    showAddChip: Boolean = false,
    onAddClick: () -> Unit = {},
) {
    val colors = ProExpenseTheme.colors
    val dimens = ProExpenseTheme.dimensions
    val typography = ProExpenseTheme.typography

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(dimens.space12),
    ) {
        CategoryPickerSection(
            title = "DEFAULT",
            trailing = if (onMoreClick != null) {
                {
                    Text(
                        text = "+ More",
                        style = typography.bodyMedium,
                        color = colors.primary,
                        modifier = Modifier.proClickable(
                            onClick = onMoreClick,
                            shape = ProExpenseTheme.shapes.chip,
                        ),
                    )
                }
            } else {
                null
            },
        ) {
            FlowRow(
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

        if (customCategories.isNotEmpty() || showAddChip) {
            CategoryPickerSection(title = "CUSTOM") {
                FlowRow(
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

@Composable
private fun CategoryPickerSection(
    title: String,
    modifier: Modifier = Modifier,
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
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(text = title, style = typography.eyebrow, color = colors.muted)
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

    Text(
        text = "+ Add",
        style = typography.bodyMedium,
        color = colors.onSurfaceVariant,
        modifier = modifier
            .proPressScale(interactionSource)
            .clip(chipShape)
            .border(BorderStroke(dimens.chipBorderWidth, colors.lineStrong), chipShape)
            .proRippleClickable(onClick = onClick, interactionSource = interactionSource)
            .padding(horizontal = dimens.space14, vertical = dimens.space8),
    )
}
