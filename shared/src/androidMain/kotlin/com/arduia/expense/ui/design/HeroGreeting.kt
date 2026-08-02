package com.arduia.expense.ui.design

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import com.arduia.expense.ui.theme.ProArtboard
import com.arduia.expense.ui.theme.ProExpenseTheme

@Composable
fun HeroGreeting(
    name: String,
    modifier: Modifier = Modifier,
    prefix: String = "Hi, ",
    // Both default to the paper-surface pairing (prefix ink, name primary-accented). Screens
    // that render this over the hero gradient (Home) pass a single white for both instead —
    // Blue Banking never recolors the name when it's already on a colored surface.
    prefixColor: Color = ProExpenseTheme.colors.onSurface,
    emphasisColor: Color = ProExpenseTheme.colors.primary,
) {
    val typography = ProExpenseTheme.typography

    Text(
        text =
            buildAnnotatedString {
                withStyle(typography.heroGreeting.toSpanStyle().copy(color = prefixColor)) {
                    append(prefix)
                }
                withStyle(
                    typography.heroGreetingEmphasis.toSpanStyle().copy(color = emphasisColor),
                ) {
                    append(name)
                }
            },
        style = typography.heroGreeting,
        modifier = modifier,
    )
}

@Preview(showBackground = true, widthDp = ProArtboard.PIXEL_9_PRO_WIDTH_DP)
@Composable
private fun HeroGreetingPreview() {
    ProExpenseTheme {
        HeroGreeting(name = "Maya")
    }
}
