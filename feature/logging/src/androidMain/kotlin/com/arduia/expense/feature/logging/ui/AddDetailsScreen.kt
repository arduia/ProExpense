package com.arduia.expense.feature.logging.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.tooling.preview.Preview
import com.arduia.expense.feature.logging.customLogCategories
import com.arduia.expense.feature.logging.defaultLogCategories
import com.arduia.expense.ui.design.IconArrowLeft
import com.arduia.expense.ui.design.IconCalendar
import com.arduia.expense.ui.design.IconChevronRight
import com.arduia.expense.ui.design.IconClock
import com.arduia.expense.ui.design.IconNote
import com.arduia.expense.ui.design.ProFilledButton
import com.arduia.expense.ui.theme.ProExpenseTheme

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AddDetailsScreen(
    formattedAmount: String,
    selectedCategoryId: String,
    dateLabel: String,
    timeLabel: String,
    note: String,
    onBack: () -> Unit,
    onCategorySelected: (String) -> Unit,
    onNoteChange: (String) -> Unit,
    onSave: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = ProExpenseTheme.colors
    val typography = ProExpenseTheme.typography
    val dims = ProExpenseTheme.dimensions
    val shapes = ProExpenseTheme.shapes
    val noteCount = note.length

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(colors.surface),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = dims.space18, vertical = dims.space14),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.clickable(onClick = onBack),
            ) {
                IconArrowLeft(color = colors.onSurfaceVariant)
                Text(
                    text = "Amount",
                    style = typography.body,
                    color = colors.onSurfaceVariant,
                    modifier = Modifier.padding(start = dims.space4),
                )
            }
            Text(
                text = "Details",
                style = typography.sectionHead,
                color = colors.onSurface,
            )
            Spacer(modifier = Modifier.size(dims.space44))
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = dims.homeHeaderPaddingH)
                .padding(bottom = dims.space16),
            verticalArrangement = Arrangement.spacedBy(dims.space16),
        ) {
            AmountSummaryCard(
                formattedAmount = formattedAmount,
                onEdit = onBack,
            )

            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(dims.space6),
                verticalArrangement = Arrangement.spacedBy(dims.space6),
            ) {
                (defaultLogCategories + customLogCategories).forEach { category ->
                    LogCategoryChip(
                        category = category,
                        selected = selectedCategoryId == category.id,
                        onClick = { onCategorySelected(category.id) },
                    )
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(shapes.quickTile)
                    .background(colors.surface)
                    .padding(horizontal = dims.space16, vertical = dims.space14),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(dims.space12),
                ) {
                    Box(
                        modifier = Modifier
                            .size(dims.currencyBadgeSize)
                            .clip(shapes.quickTile)
                            .background(colors.surfaceVariant),
                        contentAlignment = Alignment.Center,
                    ) {
                        IconCalendar(color = colors.onSurfaceVariant)
                    }
                    Column {
                        Text(
                            text = dateLabel,
                            style = typography.bodyEmphasis,
                            color = colors.onSurface,
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(dims.space4),
                        ) {
                            IconClock(color = colors.onSurfaceVariant)
                            Text(
                                text = timeLabel,
                                style = typography.caption,
                                color = colors.onSurfaceVariant,
                            )
                        }
                    }
                }
                IconChevronRight(color = colors.onSurfaceVariant)
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(shapes.quickTile)
                    .background(colors.surface)
                    .padding(horizontal = dims.space14, vertical = dims.space10),
                horizontalArrangement = Arrangement.spacedBy(dims.space10),
                verticalAlignment = Alignment.Top,
            ) {
                IconNote(color = colors.onSurfaceVariant, modifier = Modifier.padding(top = dims.space2))
                BasicTextField(
                    value = note,
                    onValueChange = { if (it.length <= 200) onNoteChange(it) },
                    modifier = Modifier.weight(1f),
                    textStyle = typography.body.copy(color = colors.onSurface),
                    cursorBrush = SolidColor(colors.primary),
                    decorationBox = { inner ->
                        if (note.isEmpty()) {
                            Text(
                                text = "Add a note (optional)",
                                style = typography.body,
                                color = colors.onSurfaceVariant,
                            )
                        }
                        inner()
                    },
                )
                Text(
                    text = "$noteCount/200",
                    style = typography.monoCaption,
                    color = if (noteCount >= 200) colors.primary else colors.onSurfaceVariant,
                )
            }
        }

        ProFilledButton(
            text = "Save expense",
            onClick = onSave,
            modifier = Modifier
                .padding(horizontal = dims.space20)
                .padding(bottom = dims.space22),
        )
    }
}

@Composable
private fun AmountSummaryCard(
    formattedAmount: String,
    onEdit: () -> Unit,
) {
    val colors = ProExpenseTheme.colors
    val typography = ProExpenseTheme.typography
    val dims = ProExpenseTheme.dimensions
    val shapes = ProExpenseTheme.shapes
    val interaction = remember { MutableInteractionSource() }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shapes.quickTile)
            .background(colors.surface)
            .clickable(interactionSource = interaction, indication = ripple(), onClick = onEdit)
            .padding(horizontal = dims.space16, vertical = dims.space14),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Column {
            Text(
                text = "Amount",
                style = typography.eyebrow,
                color = colors.onSurfaceVariant,
            )
            Text(
                text = formattedAmount,
                style = typography.screenTitle.copy(fontSize = typography.screenTitle.fontSize * 0.81f),
                color = colors.onSurface,
                modifier = Modifier.padding(top = dims.space2),
            )
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(dims.space4),
        ) {
            IconArrowLeft(color = colors.primary, size = dims.iconSizeDefault)
            Text(
                text = "Edit",
                style = typography.buttonSmall,
                color = colors.primary,
            )
        }
    }
}

@Preview(showBackground = true, widthDp = 414, heightDp = 868)
@Composable
private fun AddDetailsScreenPreview() {
    ProExpenseTheme {
        AddDetailsScreen(
            formattedAmount = "$12.50",
            selectedCategoryId = "food",
            dateLabel = "Wed, May 25",
            timeLabel = "2:30 PM",
            note = "Lunch with team",
            onBack = {},
            onCategorySelected = {},
            onNoteChange = {},
            onSave = {},
        )
    }
}
