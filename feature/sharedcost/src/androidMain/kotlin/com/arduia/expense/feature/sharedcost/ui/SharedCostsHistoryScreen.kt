package com.arduia.expense.feature.sharedcost.ui

import androidx.compose.foundation.background
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.arduia.expense.feature.sharedcost.R
import com.arduia.expense.feature.sharedcost.ui.components.SharedCostHistoryRow
import com.arduia.expense.feature.sharedcost.ui.preview.SharedCostHistoryItemUi
import com.arduia.expense.feature.sharedcost.ui.preview.previewSharedHistoryItems
import com.arduia.expense.ui.design.EmptyStateContent
import com.arduia.expense.ui.design.ProButton
import com.arduia.expense.ui.design.ProButtonSize
import com.arduia.expense.ui.design.ProFlatHeader
import com.arduia.expense.ui.design.ProIcon
import com.arduia.expense.ui.design.ProIconGlyph
import com.arduia.expense.ui.theme.ProArtboard
import com.arduia.expense.ui.theme.ProExpenseTheme

@Composable
fun SharedCostsHistoryScreen(
    items: List<SharedCostHistoryItemUi>,
    onNewSplit: () -> Unit,
    onItemClick: (SharedCostHistoryItemUi) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    isLoading: Boolean = false,
    onDeleteRequested: (SharedCostHistoryItemUi) -> Unit = {},
) {
    val colors = ProExpenseTheme.colors
    val dimens = ProExpenseTheme.dimensions
    val typography = ProExpenseTheme.typography

    Column(
        modifier =
            modifier
                .fillMaxSize()
                .background(colors.paper)
                .statusBarsPadding()
                .navigationBarsPadding()
                .verticalScroll(rememberScrollState()),
    ) {
        ProFlatHeader(
            title = stringResource(R.string.shared_costs_heading),
            eyebrow = stringResource(R.string.shared_bill_splitter),
            onBack = onBack,
            backContentDescription = stringResource(R.string.shared_back_more),
            modifier =
                Modifier
                    .padding(horizontal = dimens.screenPadding)
                    .padding(vertical = dimens.space14),
            trailing = {
                ProButton(
                    text = stringResource(R.string.shared_new_split),
                    onClick = onNewSplit,
                    size = ProButtonSize.Sm,
                    leading = {
                        ProIcon(
                            glyph = ProIconGlyph.Plus,
                            contentDescription = null,
                            tint = ProExpenseTheme.colors.onPrimaryWarm,
                            size = dimens.iconInline,
                        )
                    },
                )
            },
        )

        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = dimens.screenPadding)
                    .padding(bottom = dimens.space18),
        ) {
            // Splits load asynchronously after first composition — without this check, the
            // history would briefly show "No splits yet" before real data has had a chance to
            // load.
            if (!isLoading) {
                if (items.isEmpty()) {
                    EmptyStateContent(
                        title = stringResource(R.string.shared_history_empty_title),
                        subtitle = stringResource(R.string.shared_history_empty_body),
                        actionLabel = stringResource(R.string.shared_history_empty_action),
                        onActionClick = onNewSplit,
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .padding(top = dimens.space32),
                    )
                } else {
                    Text(
                        text = stringResource(R.string.shared_recent_splits),
                        style = typography.eyebrow,
                        color = colors.onSurfaceVariant,
                        modifier = Modifier.padding(bottom = dimens.space10),
                    )

                    Column(verticalArrangement = Arrangement.spacedBy(dimens.space8)) {
                        items.forEach { item ->
                            SwipeToDeleteRow(onDelete = { onDeleteRequested(item) }) {
                                SharedCostHistoryRow(
                                    title = item.title,
                                    meta =
                                        stringResource(
                                            R.string.shared_history_meta,
                                            item.peopleCount,
                                            item.perPersonLabel,
                                            item.dateLabel,
                                        ),
                                    total = item.totalLabel,
                                    onClick = { onItemClick(item) },
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

/** Swipe-left reveals a delete affordance; releasing past the threshold requests confirmation
 *  (never dismisses the row outright — [SharedCostsFlow] gates the actual delete behind a dialog). */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SwipeToDeleteRow(
    onDelete: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    val colors = ProExpenseTheme.colors
    val dimens = ProExpenseTheme.dimensions
    val dismissState =
        rememberSwipeToDismissBoxState(
            confirmValueChange = { value ->
                if (value == SwipeToDismissBoxValue.EndToStart) {
                    onDelete()
                }
                false
            },
        )

    SwipeToDismissBox(
        state = dismissState,
        modifier = modifier,
        enableDismissFromStartToEnd = false,
        enableDismissFromEndToStart = true,
        backgroundContent = {
            Row(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .clip(ProExpenseTheme.shapes.card)
                        .background(colors.danger)
                        .padding(horizontal = dimens.space20),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.End,
            ) {
                ProIcon(
                    glyph = ProIconGlyph.Close,
                    contentDescription = stringResource(R.string.shared_delete_action),
                    tint = colors.onPrimaryWarm,
                    size = dimens.iconInline,
                )
            }
        },
    ) {
        content()
    }
}

@Preview(
    name = "Shared costs — history",
    widthDp = ProArtboard.PIXEL_9_PRO_WIDTH_DP,
    heightDp = ProArtboard.PIXEL_9_PRO_HEIGHT_DP,
    showBackground = true,
)
@Composable
private fun SharedCostsHistoryPreview() {
    ProExpenseTheme {
        SharedCostsHistoryScreen(
            items = previewSharedHistoryItems,
            onNewSplit = {},
            onItemClick = {},
            onBack = {},
        )
    }
}

@Preview(
    name = "Shared costs — history (dark)",
    widthDp = ProArtboard.PIXEL_9_PRO_WIDTH_DP,
    heightDp = ProArtboard.PIXEL_9_PRO_HEIGHT_DP,
    showBackground = true,
)
@Composable
private fun SharedCostsHistoryDarkPreview() {
    ProExpenseTheme(darkTheme = true) {
        SharedCostsHistoryScreen(
            items = previewSharedHistoryItems,
            onNewSplit = {},
            onItemClick = {},
            onBack = {},
        )
    }
}

@Preview(
    name = "Shared costs — history empty",
    widthDp = ProArtboard.PIXEL_9_PRO_WIDTH_DP,
    heightDp = ProArtboard.PIXEL_9_PRO_HEIGHT_DP,
    showBackground = true,
)
@Composable
private fun SharedCostsHistoryEmptyPreview() {
    ProExpenseTheme {
        SharedCostsHistoryScreen(
            items = emptyList(),
            onNewSplit = {},
            onItemClick = {},
            onBack = {},
        )
    }
}

@Preview(
    name = "Shared costs — history loading",
    widthDp = ProArtboard.PIXEL_9_PRO_WIDTH_DP,
    heightDp = ProArtboard.PIXEL_9_PRO_HEIGHT_DP,
    showBackground = true,
)
@Composable
private fun SharedCostsHistoryLoadingPreview() {
    ProExpenseTheme {
        SharedCostsHistoryScreen(
            items = emptyList(),
            isLoading = true,
            onNewSplit = {},
            onItemClick = {},
            onBack = {},
        )
    }
}
