package com.arduia.expense.ui.design

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.tooling.preview.Preview
import com.arduia.expense.ui.theme.ProArtboard
import com.arduia.expense.ui.theme.ProExpenseTheme
import com.arduia.expense.ui.theme.centeredGlyph

/**
 * Live preview of the user's profile during setup: the chosen currency symbol becomes the
 * emblem and the name becomes the greeting, so the profile assembles as the user types.
 */
@Composable
fun ProfileIdentityCard(
    eyebrow: String,
    greeting: String,
    trackingLabel: String,
    currencySymbol: String,
    modifier: Modifier = Modifier,
) {
    val colors = ProExpenseTheme.colors
    val dimens = ProExpenseTheme.dimensions
    val typography = ProExpenseTheme.typography
    val onCard = colors.onPrimary

    Row(
        modifier = modifier
            .fillMaxWidth()
            .proCardShadow(ProExpenseTheme.shapes.identityCard)
            .clip(ProExpenseTheme.shapes.identityCard)
            .background(Brush.linearGradient(listOf(colors.primaryDeep, colors.primary)))
            .padding(horizontal = dimens.space18, vertical = dimens.space18),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(dimens.space16),
    ) {
        Box(
            modifier = Modifier
                .size(dimens.identityCardEmblem)
                .clip(ProExpenseTheme.shapes.tile)
                .background(onCard.copy(alpha = 0.16f)),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = currencySymbol,
                style = typography.detailsAmount.centeredGlyph(),
                color = onCard,
            )
        }
        Column(verticalArrangement = Arrangement.spacedBy(dimens.space4)) {
            Text(
                text = eyebrow,
                style = typography.eyebrow,
                color = onCard.copy(alpha = 0.70f),
            )
            Text(
                text = greeting,
                style = typography.sheetTitle,
                color = onCard,
            )
            Text(
                text = trackingLabel,
                style = typography.caption,
                color = onCard.copy(alpha = 0.82f),
            )
        }
    }
}

@Preview(widthDp = ProArtboard.PIXEL_9_PRO_WIDTH_DP, showBackground = true)
@Composable
private fun ProfileIdentityCardPreview() {
    ProExpenseTheme {
        ProfileIdentityCard(
            eyebrow = "YOUR PROFILE",
            greeting = "Hi, Maya",
            trackingLabel = "Tracking in US Dollar · USD",
            currencySymbol = "$",
            modifier = Modifier.padding(ProExpenseTheme.dimensions.screenPadding),
        )
    }
}
