package com.arduia.expense.ui.journal

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.arduia.expense.R
import com.arduia.expense.ui.design.LogCategoryBadge
import com.arduia.expense.ui.design.ProBottomSheetHost
import com.arduia.expense.ui.design.ProButton
import com.arduia.expense.ui.design.ProButtonVariant
import com.arduia.expense.ui.design.ProIcon
import com.arduia.expense.ui.design.ProIconGlyph
import com.arduia.expense.ui.design.ProTopBar
import com.arduia.expense.ui.design.ProTopBarAction
import com.arduia.expense.ui.theme.ProArtboard
import com.arduia.expense.ui.theme.ProExpenseTheme

@Composable
fun JournalDetailScreenContent(
    categoryId: String,
    note: String,
    meta: String,
    amount: String,
    eventTag: String?,
    showActionsSheet: Boolean,
    onBack: () -> Unit,
    onMore: () -> Unit,
    onDismissActions: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
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
            title = stringResource(R.string.journal_detail),
            onBack = onBack,
            action = ProTopBarAction.More,
            onAction = onMore,
            modifier = Modifier.padding(horizontal = dimens.space18),
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = dimens.space18, vertical = dimens.space24),
            verticalArrangement = Arrangement.spacedBy(dimens.space24),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(dimens.space16),
            ) {
                LogCategoryBadge(categoryId = categoryId, size = dimens.space44)
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = note,
                        style = typography.sectionHead,
                        color = colors.onSurface,
                    )
                    Text(
                        text = meta,
                        style = typography.caption,
                        color = colors.onSurfaceMuted,
                        modifier = Modifier.padding(top = dimens.space4),
                    )
                }
                Text(
                    text = amount,
                    style = typography.listAmount,
                    color = colors.onSurface,
                )
            }

            if (eventTag != null) {
                Column(verticalArrangement = Arrangement.spacedBy(dimens.space8)) {
                    Text(
                        text = stringResource(R.string.journal_linked_event),
                        style = typography.eyebrow,
                        color = colors.muted,
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(dimens.space8),
                    ) {
                        ProIcon(
                            glyph = ProIconGlyph.At,
                            contentDescription = null,
                            tint = colors.tag,
                        )
                        Text(
                            text = eventTag,
                            style = typography.bodySemiBold,
                            color = colors.tag,
                        )
                    }
                }
            }
        }
    }

    if (showActionsSheet) {
        ProBottomSheetHost(
            title = stringResource(R.string.journal_actions_title),
            onClose = onDismissActions,
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(dimens.space12)) {
                ProButton(
                    text = stringResource(R.string.journal_edit),
                    onClick = onEdit,
                    variant = ProButtonVariant.Secondary,
                    modifier = Modifier.fillMaxWidth(),
                )
                ProButton(
                    text = stringResource(R.string.journal_delete),
                    onClick = onDelete,
                    variant = ProButtonVariant.Danger,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
    }
}

@Preview(
    name = "Journal detail — linked",
    widthDp = ProArtboard.PIXEL_9_PRO_WIDTH_DP,
    heightDp = ProArtboard.PIXEL_9_PRO_HEIGHT_DP,
    showBackground = true,
)
@Composable
private fun JournalDetailLinkedPreview() {
    ProExpenseTheme {
        JournalDetailScreenContent(
            categoryId = "entertainment",
            note = "Movie · Dune",
            meta = "Entertainment · May 25 · 08:10 PM",
            amount = "$18.00",
            eventTag = "Bali Trip",
            showActionsSheet = false,
            onBack = {},
            onMore = {},
            onDismissActions = {},
            onEdit = {},
            onDelete = {},
        )
    }
}

@Preview(
    name = "Journal detail — actions",
    widthDp = ProArtboard.PIXEL_9_PRO_WIDTH_DP,
    heightDp = ProArtboard.PIXEL_9_PRO_HEIGHT_DP,
    showBackground = true,
)
@Composable
private fun JournalDetailActionsPreview() {
    ProExpenseTheme {
        JournalDetailScreenContent(
            categoryId = "entertainment",
            note = "Movie · Dune",
            meta = "Entertainment · May 25 · 08:10 PM",
            amount = "$18.00",
            eventTag = "Bali Trip",
            showActionsSheet = true,
            onBack = {},
            onMore = {},
            onDismissActions = {},
            onEdit = {},
            onDelete = {},
        )
    }
}
