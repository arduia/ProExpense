package com.arduia.expense.feature.logging.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.arduia.expense.feature.logging.R
import com.arduia.expense.ui.design.ProButton
import com.arduia.expense.ui.design.ProButtonSize
import com.arduia.expense.ui.design.ProButtonVariant
import com.arduia.expense.ui.design.ProCard
import com.arduia.expense.ui.design.ProIcon
import com.arduia.expense.ui.design.ProIconGlyph
import com.arduia.expense.ui.theme.ProArtboard
import com.arduia.expense.ui.theme.ProExpenseTheme

@Composable
fun ExpenseDraftDialog(
    amountLabel: String,
    onContinue: () -> Unit,
    onDiscard: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = ProExpenseTheme.colors
    val dimens = ProExpenseTheme.dimensions
    val typography = ProExpenseTheme.typography

    ProCard(modifier = modifier.fillMaxWidth()) {
        Column(verticalArrangement = Arrangement.spacedBy(dimens.space16)) {
            Box(
                modifier = Modifier
                    .size(dimens.iconBadge)
                    .background(colors.primaryTint, CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                ProIcon(
                    glyph = ProIconGlyph.Note,
                    contentDescription = null,
                    tint = colors.primary,
                    size = dimens.iconNav,
                )
            }
            Text(
                text = stringResource(R.string.draft_restore_title),
                style = typography.sectionHead,
                color = colors.onSurface,
            )
            Text(
                text = stringResource(R.string.draft_restore_body, amountLabel),
                style = typography.body,
                color = colors.onSurfaceVariant,
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(dimens.space8),
            ) {
                ProButton(
                    text = stringResource(R.string.draft_discard),
                    onClick = onDiscard,
                    variant = ProButtonVariant.Secondary,
                    size = ProButtonSize.Md,
                    modifier = Modifier.weight(1f),
                )
                ProButton(
                    text = stringResource(R.string.draft_continue),
                    onClick = onContinue,
                    size = ProButtonSize.Md,
                    modifier = Modifier.weight(1f),
                    fillMaxWidth = true,
                )
            }
        }
    }
}

@Preview(
    name = "Expense draft restore",
    widthDp = ProArtboard.PIXEL_9_PRO_WIDTH_DP,
    showBackground = true,
)
@Composable
private fun ExpenseDraftDialogPreview() {
    ProExpenseTheme {
        ExpenseDraftDialog(
            amountLabel = "$12.50",
            onContinue = {},
            onDiscard = {},
            modifier = Modifier.padding(ProExpenseTheme.dimensions.screenPadding),
        )
    }
}
