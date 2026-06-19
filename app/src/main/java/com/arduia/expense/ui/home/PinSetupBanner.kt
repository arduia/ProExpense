package com.arduia.expense.ui.home

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.arduia.expense.R
import com.arduia.expense.ui.design.ProIcon
import com.arduia.expense.ui.design.ProIconGlyph
import com.arduia.expense.ui.design.proClickable
import com.arduia.expense.ui.design.proIconClickable
import com.arduia.expense.ui.theme.ProArtboard
import com.arduia.expense.ui.theme.ProExpenseTheme

@Composable
fun PinSetupBanner(
    onTap: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
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
            .proClickable(onClick = onTap, shape = shape)
            .padding(dimens.space12),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(dimens.space12),
    ) {
        ProIcon(
            glyph = ProIconGlyph.Fingerprint,
            contentDescription = null,
            tint = colors.primary,
        )
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(dimens.space4),
        ) {
            Text(
                text = stringResource(R.string.home_pin_banner_title),
                style = typography.bodySemiBold,
                color = colors.onSurface,
            )
            Text(
                text = stringResource(R.string.home_pin_banner_body),
                style = typography.caption,
                color = colors.onSurfaceMuted,
            )
        }
        ProIcon(
            glyph = ProIconGlyph.Close,
            contentDescription = stringResource(R.string.dismiss),
            tint = colors.muted,
            modifier = Modifier.proIconClickable(onClick = onDismiss),
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PinSetupBannerPreview() {
    ProExpenseTheme {
        PinSetupBanner(onTap = {}, onDismiss = {})
    }
}
