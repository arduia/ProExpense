package com.arduia.expense.ui.design

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.arduia.expense.ui.theme.ProExpenseTheme

@Composable
fun ProToast(
    message: String,
    modifier: Modifier = Modifier,
) {
    val colors = ProExpenseTheme.colors
    val dimens = ProExpenseTheme.dimensions
    val typography = ProExpenseTheme.typography

    Row(
        modifier = modifier
            .background(colors.onSurface, ProExpenseTheme.shapes.toast)
            .padding(horizontal = dimens.space18, vertical = dimens.space12),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(dimens.space8),
    ) {
        ProIcon(
            glyph = ProIconGlyph.Check,
            contentDescription = null,
            tint = colors.success,
            size = dimens.iconInline,
        )
        Text(
            text = message,
            style = typography.bodySemiBold,
            color = colors.paper,
        )
    }
}
