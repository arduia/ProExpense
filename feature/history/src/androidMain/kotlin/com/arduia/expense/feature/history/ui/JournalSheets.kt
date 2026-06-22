package com.arduia.expense.feature.history.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.arduia.expense.feature.history.R
import com.arduia.expense.ui.design.LogCategoryBadge
import com.arduia.expense.ui.design.ProBottomSheetHost
import com.arduia.expense.ui.design.ProButton
import com.arduia.expense.ui.design.ProButtonSize
import com.arduia.expense.ui.design.ProButtonVariant
import com.arduia.expense.ui.design.ProIcon
import com.arduia.expense.ui.design.ProIconGlyph
import com.arduia.expense.ui.design.TransactionRow
import com.arduia.expense.ui.design.proClickable
import com.arduia.expense.feature.history.ui.preview.JournalQuickNoteUiState
import com.arduia.expense.feature.history.ui.preview.previewJournalQuickNote
import com.arduia.expense.ui.theme.ProArtboard
import com.arduia.expense.ui.theme.ProExpenseTheme

@Composable
fun JournalQuickNoteSheetContent(
    state: JournalQuickNoteUiState,
    onNoteChange: (String) -> Unit,
    onSave: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = ProExpenseTheme.colors
    val dimens = ProExpenseTheme.dimensions
    val typography = ProExpenseTheme.typography

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(dimens.space16),
    ) {
        TransactionRow(
            categoryId = state.row.categoryId,
            note = state.row.note,
            meta = state.row.meta,
            amount = state.row.amount,
            showDivider = true,
        )

        BasicTextField(
            value = state.note,
            onValueChange = onNoteChange,
            modifier = Modifier
                .fillMaxWidth()
                .clip(ProExpenseTheme.shapes.searchField)
                .border(BorderStroke(1.dp, colors.primary), ProExpenseTheme.shapes.searchField)
                .background(colors.surface)
                .heightIn(min = dimens.space44 + dimens.space24)
                .padding(horizontal = dimens.space14, vertical = dimens.space12),
            textStyle = typography.body.copy(color = colors.onSurface),
            cursorBrush = SolidColor(colors.primary),
            decorationBox = { inner ->
                if (state.note.isEmpty()) {
                    Text(
                        text = stringResource(R.string.journal_quick_note_placeholder),
                        style = typography.body,
                        color = colors.muted,
                    )
                }
                inner()
            },
        )

        ProButton(
            text = stringResource(R.string.journal_save_note),
            onClick = onSave,
            variant = ProButtonVariant.Primary,
            size = ProButtonSize.Lg,
            fillMaxWidth = true,
        )
    }
}

@Composable
fun JournalActionsSheetContent(
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
        JournalActionRow(
            icon = ProIconGlyph.Note,
            iconTint = colors.onSurfaceVariant,
            iconBackground = colors.paperAlt,
            title = stringResource(R.string.journal_action_edit_title),
            titleColor = colors.onSurface,
            subtitle = stringResource(R.string.journal_action_edit_subtitle),
            onClick = onEdit,
        )
        JournalActionRow(
            icon = ProIconGlyph.Close,
            iconTint = colors.danger,
            iconBackground = colors.dangerTint,
            title = stringResource(R.string.journal_action_delete_title),
            titleColor = colors.danger,
            subtitle = stringResource(R.string.journal_action_delete_subtitle),
            onClick = onDelete,
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(ProExpenseTheme.shapes.card)
                .border(BorderStroke(1.dp, colors.line), ProExpenseTheme.shapes.card)
                .background(colors.surface)
                .proClickable(onClick = onCancel, shape = ProExpenseTheme.shapes.card)
                .padding(vertical = dimens.space16),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = stringResource(R.string.journal_action_cancel),
                style = typography.bodySemiBold,
                color = colors.onSurface,
            )
        }
    }
}

@Composable
private fun JournalActionRow(
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
        modifier = Modifier
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
            modifier = Modifier
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
    name = "Journal — quick note sheet",
    widthDp = ProArtboard.PIXEL_9_PRO_WIDTH_DP,
    heightDp = ProArtboard.PIXEL_9_PRO_HEIGHT_DP,
    showBackground = true,
)
@Composable
private fun JournalQuickNoteSheetPreview() {
    ProExpenseTheme {
        ProBottomSheetHost(
            visible = true,
            title = stringResource(R.string.journal_quick_note_title),
            onClose = {},
        ) {
            JournalQuickNoteSheetContent(
                state = previewJournalQuickNote,
                onNoteChange = {},
                onSave = {},
            )
        }
    }
}

@Preview(
    name = "Journal — actions sheet",
    widthDp = ProArtboard.PIXEL_9_PRO_WIDTH_DP,
    heightDp = ProArtboard.PIXEL_9_PRO_HEIGHT_DP,
    showBackground = true,
)
@Composable
private fun JournalActionsSheetPreview() {
    ProExpenseTheme {
        ProBottomSheetHost(
            visible = true,
            title = null,
            onClose = {},
        ) {
            JournalActionsSheetContent(
                onEdit = {},
                onDelete = {},
                onCancel = {},
            )
        }
    }
}
