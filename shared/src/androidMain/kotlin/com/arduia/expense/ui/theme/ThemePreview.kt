package com.arduia.expense.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Preview(
    name = "Design system foundation (Pixel 9 Pro)",
    widthDp = ProArtboard.PIXEL_9_PRO_WIDTH_DP,
    heightDp = ProArtboard.PIXEL_9_PRO_HEIGHT_DP,
    showBackground = true,
)
@Composable
fun ProExpenseThemeFoundationPreview() {
    ProExpenseThemeFoundation()
}

@Composable
fun ProExpenseThemeFoundation() {
    ProExpenseTheme {
        val colors = ProExpenseTheme.colors
        val typography = ProExpenseTheme.typography
        val dimens = ProExpenseTheme.dimensions

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(colors.paper)
                .padding(dimens.space18),
            verticalArrangement = Arrangement.spacedBy(dimens.space16),
        ) {
            Text(
                text = "Journal",
                style = typography.screenTitle,
                color = colors.onSurface,
            )
            Text(
                text = "\$1,240",
                style = typography.displayAmount,
                color = colors.onSurface,
            )
            Text(
                text = "AMOUNT · USD",
                style = typography.eyebrow,
                color = colors.muted,
            )
            Text(
                text = "Hi, Maya",
                style = typography.displayFlourish,
                color = colors.primary,
            )
            Text(
                text = "Lunch with M.",
                style = typography.bodyMedium,
                color = colors.onSurface,
            )
            Text(
                text = "Food · 12:30 PM",
                style = typography.caption,
                color = colors.onSurfaceMuted,
            )
            Text(
                text = "\$12.40",
                style = typography.listAmount,
                color = colors.onSurface,
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(dimens.space8),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                ProColorSwatch(color = colors.primary, label = "Primary")
                ProColorSwatch(color = colors.success, label = "Success")
                ProColorSwatch(color = colors.tag, label = "Tag")
                ProColorSwatch(color = colors.danger, label = "Danger")
            }
        }
    }
}

@Composable
private fun ProColorSwatch(
    color: androidx.compose.ui.graphics.Color,
    label: String,
) {
    val dimens = ProExpenseTheme.dimensions
    val typography = ProExpenseTheme.typography
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(dimens.space4),
    ) {
        Column(
            modifier = Modifier
                .size(44.dp)
                .background(color, CircleShape),
        ) {}
        Text(
            text = label,
            style = typography.caption,
            color = ProExpenseTheme.colors.onSurfaceMuted,
        )
    }
}
