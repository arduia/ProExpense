package com.arduia.expense.feature.history.ui

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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.arduia.expense.feature.history.R
import com.arduia.expense.ui.design.LogCategoryBadge
import com.arduia.expense.ui.design.ProButton
import com.arduia.expense.ui.design.ProButtonSize
import com.arduia.expense.ui.design.ProButtonVariant
import com.arduia.expense.ui.design.ProIcon
import com.arduia.expense.ui.design.ProIconGlyph
import com.arduia.expense.ui.design.ProTopBar
import com.arduia.expense.ui.design.ProTopBarAction
import com.arduia.expense.ui.design.proClickable
import com.arduia.expense.feature.history.ui.preview.JournalDetailUiState
import com.arduia.expense.feature.history.ui.preview.JournalLinkedTagUi
import com.arduia.expense.feature.history.ui.preview.previewJournalDetail
import com.arduia.expense.ui.theme.ProArtboard
import com.arduia.expense.ui.theme.ProExpenseTheme

@Composable
fun JournalDetailScreen(
    state: JournalDetailUiState,
    onBack: () -> Unit,
    onActions: () -> Unit,
    onLinkedTagClick: () -> Unit,
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
        Box(modifier = Modifier.padding(horizontal = dimens.screenPadding)) {
            ProTopBar(
                title = "",
                onBack = onBack,
                backLabel = stringResource(R.string.journal_detail_back),
                action = ProTopBarAction.More,
                onAction = onActions,
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = dimens.screenPadding)
                .padding(top = dimens.space12, bottom = dimens.space18),
            verticalArrangement = Arrangement.spacedBy(dimens.space20),
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(dimens.space8),
            ) {
                LogCategoryBadge(
                    categoryId = state.categoryId,
                    size = dimens.space44 + dimens.space8,
                )
                Text(
                    text = state.categoryLabel,
                    style = typography.eyebrow,
                    color = colors.primary,
                )
                Text(
                    text = state.amountLabel,
                    style = typography.displayAmount,
                    color = colors.onSurface,
                )
                Text(
                    text = state.dateTimeLabel,
                    style = typography.caption,
                    color = colors.onSurfaceMuted,
                )
            }

            JournalSection(label = stringResource(R.string.journal_note_label)) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(ProExpenseTheme.shapes.card)
                        .border(BorderStroke(1.dp, colors.line), ProExpenseTheme.shapes.card)
                        .background(colors.surface)
                        .padding(dimens.cardPadding),
                ) {
                    Text(
                        text = "“${state.note}”",
                        style = typography.body.copy(fontStyle = FontStyle.Italic),
                        color = colors.onSurfaceVariant,
                    )
                }
            }

            if (state.linkedTag != null) {
                JournalSection(label = stringResource(R.string.journal_linked_to_label)) {
                    JournalLinkedTagCard(tag = state.linkedTag, onClick = onLinkedTagClick)
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = dimens.space4),
                horizontalArrangement = Arrangement.spacedBy(dimens.space10),
            ) {
                ProButton(
                    text = stringResource(R.string.journal_edit_action),
                    onClick = onEdit,
                    variant = ProButtonVariant.Secondary,
                    size = ProButtonSize.Lg,
                    fillMaxWidth = true,
                    modifier = Modifier.weight(1f),
                )
                ProButton(
                    text = stringResource(R.string.journal_delete_action),
                    onClick = onDelete,
                    variant = ProButtonVariant.DangerOutline,
                    size = ProButtonSize.Lg,
                    fillMaxWidth = true,
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }
}

@Composable
private fun JournalSection(
    label: String,
    content: @Composable () -> Unit,
) {
    val colors = ProExpenseTheme.colors
    val dimens = ProExpenseTheme.dimensions
    val typography = ProExpenseTheme.typography

    Column(verticalArrangement = Arrangement.spacedBy(dimens.space10)) {
        Text(text = label, style = typography.eyebrow, color = colors.onSurfaceVariant)
        content()
    }
}

@Composable
private fun JournalLinkedTagCard(
    tag: JournalLinkedTagUi,
    onClick: () -> Unit,
) {
    val colors = ProExpenseTheme.colors
    val dimens = ProExpenseTheme.dimensions
    val typography = ProExpenseTheme.typography

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(ProExpenseTheme.shapes.card)
            .border(BorderStroke(1.dp, colors.line), ProExpenseTheme.shapes.card)
            .background(colors.surface)
            .proClickable(onClick = onClick, shape = ProExpenseTheme.shapes.card)
            .padding(horizontal = dimens.space16, vertical = dimens.space12),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(dimens.space12),
    ) {
        Box(
            modifier = Modifier
                .size(dimens.iconBadge)
                .clip(CircleShape)
                .background(colors.tagTint),
            contentAlignment = Alignment.Center,
        ) {
            ProIcon(
                glyph = ProIconGlyph.At,
                contentDescription = null,
                tint = colors.tag,
                size = dimens.iconInline,
            )
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = tag.title,
                style = typography.bodySemiBold,
                color = colors.onSurface,
            )
            Text(
                text = tag.meta,
                style = typography.caption,
                color = colors.onSurfaceMuted,
                modifier = Modifier.padding(top = dimens.space2),
            )
        }
        ProIcon(
            glyph = ProIconGlyph.ChevronRight,
            contentDescription = null,
            tint = colors.muted,
            size = dimens.iconInline,
        )
    }
}

@Preview(
    name = "Journal detail — linked",
    widthDp = ProArtboard.PIXEL_9_PRO_WIDTH_DP,
    heightDp = ProArtboard.PIXEL_9_PRO_HEIGHT_DP,
    showBackground = true,
)
@Composable
private fun JournalDetailPreview() {
    ProExpenseTheme {
        JournalDetailScreen(
            state = previewJournalDetail,
            onBack = {},
            onActions = {},
            onLinkedTagClick = {},
            onEdit = {},
            onDelete = {},
        )
    }
}
