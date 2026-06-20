package com.arduia.expense.ui.design

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.arduia.expense.ui.theme.ProArtboard
import com.arduia.expense.ui.theme.ProExpenseTheme

@Composable
fun NoticeBanner(
    title: String,
    body: String,
    onClick: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ProIconGlyph = ProIconGlyph.Fingerprint,
    dismissContentDescription: String = "Dismiss",
) {
    val colors = ProExpenseTheme.colors
    val dimens = ProExpenseTheme.dimensions
    val typography = ProExpenseTheme.typography
    val shape = ProExpenseTheme.shapes.card

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(shape)
            .background(colors.primaryTint)
            .proClickable(onClick = onClick, shape = shape)
            .padding(dimens.space12),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(dimens.space12),
    ) {
        ProIcon(
            glyph = icon,
            contentDescription = null,
            tint = colors.primary,
        )
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(dimens.space4),
        ) {
            Text(
                text = title,
                style = typography.bodySemiBold,
                color = colors.onSurface,
            )
            Text(
                text = body,
                style = typography.caption,
                color = colors.onSurfaceMuted,
            )
        }
        ProIcon(
            glyph = ProIconGlyph.Close,
            contentDescription = dismissContentDescription,
            tint = colors.muted,
            modifier = Modifier.proIconClickable(onClick = onDismiss),
        )
    }
}

@Preview(showBackground = true, widthDp = ProArtboard.PIXEL_9_PRO_WIDTH_DP)
@Composable
private fun NoticeBannerPreview() {
    ProExpenseTheme {
        NoticeBanner(
            title = "Protect your data with a PIN",
            body = "Set a 6-digit PIN so only you can open the app.",
            onClick = {},
            onDismiss = {},
            modifier = Modifier.padding(16.dp),
        )
    }
}
