package com.arduia.expense.ui.logging

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.arduia.expense.R
import com.arduia.expense.feature.logging.customLogCategories
import com.arduia.expense.feature.logging.defaultLogCategories
import com.arduia.expense.ui.design.CategoryPicker
import com.arduia.expense.ui.design.DetailAmountSummaryCard
import com.arduia.expense.ui.design.DetailFieldCard
import com.arduia.expense.ui.design.DetailNoteField
import com.arduia.expense.ui.design.ProButton
import com.arduia.expense.ui.design.ProButtonSize
import com.arduia.expense.ui.design.ProIcon
import com.arduia.expense.ui.design.ProIconGlyph
import com.arduia.expense.ui.design.ProTopBar
import com.arduia.expense.ui.design.proPressScale
import com.arduia.expense.ui.design.proRippleClickable
import com.arduia.expense.ui.theme.ProArtboard
import com.arduia.expense.ui.theme.ProExpenseTheme

@Composable
fun AddDetailsScreen(
    amountLabel: String,
    selectedCategoryId: String,
    note: String,
    dateLabel: String,
    timeLabel: String,
    eventTag: String?,
    onNoteChange: (String) -> Unit,
    onCategorySelected: (String) -> Unit,
    onBack: () -> Unit,
    onEditAmount: () -> Unit,
    onClearTag: () -> Unit,
    onSave: () -> Unit,
    modifier: Modifier = Modifier,
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
        ProTopBar(
            title = stringResource(R.string.details),
            onBack = onBack,
            backLabel = stringResource(R.string.amount_step),
            modifier = Modifier.padding(horizontal = dimens.space18),
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = dimens.space18),
            verticalArrangement = Arrangement.spacedBy(dimens.space16),
        ) {
            DetailAmountSummaryCard(
                amountLabel = amountLabel,
                onEdit = onEditAmount,
            )

            CategoryPicker(
                defaultCategories = defaultLogCategories.map { it.id to it.label },
                customCategories = customLogCategories.map { it.id to it.label },
                selectedCategoryId = selectedCategoryId,
                onCategorySelected = onCategorySelected,
                showAddChip = true,
            )

            DetailFieldCard(
                leading = {
                    ProIcon(
                        glyph = ProIconGlyph.Calendar,
                        contentDescription = null,
                        tint = colors.muted,
                        size = dimens.iconNav,
                    )
                },
                trailing = {
                    ProIcon(
                        glyph = ProIconGlyph.ChevronRight,
                        contentDescription = null,
                        tint = colors.muted,
                        size = dimens.iconInline,
                    )
                },
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(dimens.space4)) {
                    Text(text = dateLabel, style = typography.bodySemiBold, color = colors.onSurface)
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(dimens.space6),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        ProIcon(
                            glyph = ProIconGlyph.Clock,
                            contentDescription = null,
                            tint = colors.muted,
                            size = dimens.iconInline,
                        )
                        Text(text = timeLabel, style = typography.caption, color = colors.onSurfaceMuted)
                    }
                }
            }

            DetailNoteField(
                value = note,
                onValueChange = onNoteChange,
                maxLength = 200,
            )

            if (eventTag != null) {
                DetailFieldCard(
                    leading = {
                        ProIcon(
                            glyph = ProIconGlyph.At,
                            contentDescription = null,
                            tint = colors.tag,
                            size = dimens.iconNav,
                        )
                    },
                    trailing = {
                        val chipShape = ProExpenseTheme.shapes.chip
                        val interactionSource = remember { MutableInteractionSource() }
                        Text(
                            text = stringResource(R.string.clear),
                            style = typography.bodyMedium,
                            color = colors.onSurfaceVariant,
                            modifier = Modifier
                                .proPressScale(interactionSource)
                                .clip(chipShape)
                                .border(
                                    width = dimens.chipBorderWidth,
                                    color = colors.lineStrong,
                                    shape = chipShape,
                                )
                                .proRippleClickable(
                                    onClick = onClearTag,
                                    interactionSource = interactionSource,
                                )
                                .padding(horizontal = dimens.space12, vertical = dimens.space6),
                        )
                    },
                ) {
                    Text(
                        text = "@ $eventTag",
                        style = typography.bodyMedium,
                        color = colors.tag,
                    )
                }
            }
        }

        ProButton(
            text = stringResource(R.string.save_expense, amountLabel),
            onClick = onSave,
            size = ProButtonSize.Lg,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = dimens.space18, vertical = dimens.space16),
        )
    }
}

@Preview(
    name = "AddDetails — tagged",
    widthDp = ProArtboard.PIXEL_9_PRO_WIDTH_DP,
    heightDp = ProArtboard.PIXEL_9_PRO_HEIGHT_DP,
    showBackground = true,
)
@Composable
private fun AddDetailsTaggedPreview() {
    ProExpenseTheme {
        AddDetailsScreen(
            amountLabel = "$12.50",
            selectedCategoryId = "food",
            note = "Lunch with M.",
            dateLabel = "Today, May 25",
            timeLabel = "12:30 PM",
            eventTag = "Bali Trip",
            onNoteChange = {},
            onCategorySelected = {},
            onBack = {},
            onEditAmount = {},
            onClearTag = {},
            onSave = {},
        )
    }
}
