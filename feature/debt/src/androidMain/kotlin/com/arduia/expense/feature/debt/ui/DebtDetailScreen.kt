package com.arduia.expense.feature.debt.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.arduia.expense.feature.debt.R
import com.arduia.expense.ui.design.LogCategoryBadge
import com.arduia.expense.ui.design.ProButton
import com.arduia.expense.ui.design.ProButtonSize
import com.arduia.expense.ui.design.ProButtonVariant
import com.arduia.expense.ui.design.ProIcon
import com.arduia.expense.ui.design.ProIconGlyph
import com.arduia.expense.ui.design.ProTopBar
import com.arduia.expense.ui.design.ProTopBarAction
import com.arduia.expense.feature.debt.ui.preview.DebtDetailUiState
import com.arduia.expense.feature.debt.ui.preview.DebtSide
import com.arduia.expense.feature.debt.ui.preview.previewDebtLentDetail
import com.arduia.expense.feature.debt.ui.preview.previewDebtOweDetail
import com.arduia.expense.ui.theme.ProArtboard
import com.arduia.expense.ui.theme.ProExpenseTheme

@Composable
fun DebtDetailScreen(
    state: DebtDetailUiState,
    onBack: () -> Unit,
    onMore: () -> Unit,
    onEdit: () -> Unit,
    onMarkSettled: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = ProExpenseTheme.colors
    val dimens = ProExpenseTheme.dimensions

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
                backLabel = stringResource(R.string.debt_detail_back),
                action = ProTopBarAction.More,
                onAction = onMore,
            )
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = dimens.screenPadding)
                .padding(top = dimens.space8, bottom = dimens.space18),
            verticalArrangement = Arrangement.spacedBy(dimens.space20),
        ) {
            DebtDetailHeader(state)

            DebtFieldsCard(state)

            if (state.note != null) {
                DebtNoteCard(note = state.note)
            }

            if (state.linkedExpense != null) {
                DebtLinkedExpenseSection(state)
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = dimens.screenPadding)
                .padding(bottom = dimens.space18),
            horizontalArrangement = Arrangement.spacedBy(dimens.space12),
        ) {
            ProButton(
                text = stringResource(R.string.debt_edit),
                onClick = onEdit,
                variant = ProButtonVariant.Secondary,
                size = ProButtonSize.Lg,
                fillMaxWidth = true,
                modifier = Modifier.weight(1f),
            )
            ProButton(
                text = stringResource(R.string.debt_mark_as_settled),
                onClick = onMarkSettled,
                variant = if (state.side == DebtSide.Lent) {
                    ProButtonVariant.Success
                } else {
                    ProButtonVariant.Danger
                },
                size = ProButtonSize.Lg,
                fillMaxWidth = true,
                enabled = !state.settled,
                leading = {
                    ProIcon(
                        glyph = ProIconGlyph.Check,
                        contentDescription = null,
                        tint = colors.onPrimaryWarm,
                        size = dimens.iconInline,
                    )
                },
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
private fun DebtDetailHeader(state: DebtDetailUiState) {
    val colors = ProExpenseTheme.colors
    val dimens = ProExpenseTheme.dimensions
    val typography = ProExpenseTheme.typography
    val accent = if (state.side == DebtSide.Lent) colors.success else colors.danger
    val eyebrow = if (state.side == DebtSide.Lent) {
        stringResource(R.string.debt_you_lent)
    } else {
        stringResource(R.string.debt_you_owe_caps)
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(dimens.space8),
    ) {
        DebtAvatar(
            initial = state.initial,
            tint = accent,
            background = if (state.side == DebtSide.Lent) colors.successTint else colors.dangerTint,
            size = dimens.space44 + dimens.space24,
        )
        Text(
            text = state.name,
            style = typography.profileScreenTitle,
            color = colors.onSurface,
            maxLines = 1,
            overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
        )
        Text(
            text = eyebrow,
            style = typography.eyebrow,
            color = accent,
            textAlign = TextAlign.Center,
        )
        Text(
            text = state.amountLabel,
            style = typography.displayAmount,
            color = accent,
        )
    }
}

@Composable
private fun DebtFieldsCard(state: DebtDetailUiState) {
    val colors = ProExpenseTheme.colors

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(ProExpenseTheme.shapes.card)
            .border(BorderStroke(1.dp, colors.line), ProExpenseTheme.shapes.card)
            .background(colors.surface),
    ) {
        DebtFieldRow(
            label = stringResource(R.string.debt_date_recorded),
            value = state.dateRecordedLabel,
        )
        DebtFieldDivider()
        DebtFieldRow(
            label = stringResource(R.string.debt_due_date),
            value = state.dueLabel,
        )
        DebtFieldDivider()
        DebtFieldRow(
            label = stringResource(R.string.debt_status),
            value = state.statusLabel,
        )
    }
}

@Composable
private fun DebtFieldRow(label: String, value: String) {
    val colors = ProExpenseTheme.colors
    val dimens = ProExpenseTheme.dimensions
    val typography = ProExpenseTheme.typography

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = dimens.cardPadding, vertical = dimens.space14),
        horizontalArrangement = Arrangement.spacedBy(dimens.space12),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = label,
            style = typography.body,
            color = colors.muted,
        )
        Text(
            text = value,
            style = typography.bodyMedium,
            color = colors.onSurface,
            textAlign = TextAlign.End,
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
private fun DebtFieldDivider() {
    val colors = ProExpenseTheme.colors
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(colors.lineSoft),
    )
}

@Composable
private fun DebtNoteCard(note: String) {
    val colors = ProExpenseTheme.colors
    val dimens = ProExpenseTheme.dimensions
    val typography = ProExpenseTheme.typography

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(ProExpenseTheme.shapes.card)
            .border(BorderStroke(1.dp, colors.line), ProExpenseTheme.shapes.card)
            .background(colors.surface)
            .padding(dimens.cardPadding),
        horizontalArrangement = Arrangement.spacedBy(dimens.space12),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        ProIcon(
            glyph = ProIconGlyph.Note,
            contentDescription = null,
            tint = colors.muted,
            size = dimens.iconNav,
        )
        Text(
            text = note,
            style = typography.body.copy(fontStyle = FontStyle.Italic),
            color = colors.onSurfaceVariant,
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
private fun DebtLinkedExpenseSection(state: DebtDetailUiState) {
    val colors = ProExpenseTheme.colors
    val dimens = ProExpenseTheme.dimensions
    val typography = ProExpenseTheme.typography
    val expense = state.linkedExpense ?: return

    Column(verticalArrangement = Arrangement.spacedBy(dimens.space10)) {
        Text(
            text = stringResource(R.string.debt_linked_expense),
            style = typography.eyebrow,
            color = colors.onSurfaceVariant,
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(ProExpenseTheme.shapes.card)
                .border(BorderStroke(1.dp, colors.line), ProExpenseTheme.shapes.card)
                .background(colors.surface)
                .padding(horizontal = dimens.space16, vertical = dimens.space12),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(dimens.space12),
        ) {
            LogCategoryBadge(categoryId = expense.categoryId, size = dimens.iconBadge)
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = expense.title,
                    style = typography.bodySemiBold,
                    color = colors.onSurface,
                )
                Text(
                    text = stringResource(R.string.debt_reference_only),
                    style = typography.caption,
                    color = colors.muted,
                    modifier = Modifier.padding(top = dimens.space2),
                )
            }
            Text(
                text = expense.amountLabel,
                style = typography.listAmount,
                color = colors.onSurface,
            )
        }
    }
}

@Preview(
    name = "Debt detail — lent",
    widthDp = ProArtboard.PIXEL_9_PRO_WIDTH_DP,
    heightDp = ProArtboard.PIXEL_9_PRO_HEIGHT_DP,
    showBackground = true,
)
@Composable
private fun DebtDetailLentPreview() {
    ProExpenseTheme {
        DebtDetailScreen(
            state = previewDebtLentDetail,
            onBack = {},
            onMore = {},
            onEdit = {},
            onMarkSettled = {},
        )
    }
}

@Preview(
    name = "Debt detail — owe",
    widthDp = ProArtboard.PIXEL_9_PRO_WIDTH_DP,
    heightDp = ProArtboard.PIXEL_9_PRO_HEIGHT_DP,
    showBackground = true,
)
@Composable
private fun DebtDetailOwePreview() {
    ProExpenseTheme {
        DebtDetailScreen(
            state = previewDebtOweDetail,
            onBack = {},
            onMore = {},
            onEdit = {},
            onMarkSettled = {},
        )
    }
}
