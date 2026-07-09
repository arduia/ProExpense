package com.arduia.expense.ui.design

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import com.arduia.expense.ui.theme.ProArtboard
import com.arduia.expense.ui.theme.ProExpenseTheme

@Composable
fun ProfileCurrencyRow(
    code: String,
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = ProExpenseTheme.colors
    val dimens = ProExpenseTheme.dimensions
    val typography = ProExpenseTheme.typography
    val shape = ProExpenseTheme.shapes.searchField

    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .clip(shape)
                .border(
                    BorderStroke(
                        width = dimens.buttonBorderWidth,
                        color = if (selected) colors.primary else colors.line,
                    ),
                    shape = shape,
                ).background(if (selected) colors.primaryTint.copy(alpha = 0.45f) else colors.surface)
                .proClickable(onClick = onClick, shape = shape)
                .padding(horizontal = dimens.space14, vertical = dimens.space12),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(dimens.space12),
    ) {
        Box(
            modifier =
                Modifier
                    .size(dimens.currencySymbolSize)
                    .clip(CircleShape)
                    .background(if (selected) colors.surface else colors.paperAlt),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = currencySymbol(code),
                style = typography.bodySemiBold,
                color = if (selected) colors.primary else colors.muted,
            )
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = code,
                style = typography.bodySemiBold,
                color = colors.onSurface,
            )
            Text(
                text = label,
                style = typography.caption,
                color = colors.onSurfaceMuted,
            )
        }
        if (selected) {
            ProIcon(
                glyph = ProIconGlyph.Check,
                contentDescription = null,
                tint = colors.primary,
            )
        }
    }
}

@Preview(widthDp = ProArtboard.PIXEL_9_PRO_WIDTH_DP, showBackground = true)
@Composable
private fun ProfileCurrencyRowPreview() {
    ProExpenseTheme {
        Column(
            modifier = Modifier.padding(ProExpenseTheme.dimensions.screenPadding),
            verticalArrangement = Arrangement.spacedBy(ProExpenseTheme.dimensions.space8),
        ) {
            ProfileCurrencyRow(code = "USD", label = "US Dollar", selected = true, onClick = {})
            ProfileCurrencyRow(code = "EUR", label = "Euro", selected = false, onClick = {})
        }
    }
}
