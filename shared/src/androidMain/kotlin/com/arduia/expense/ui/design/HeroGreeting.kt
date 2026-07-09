package com.arduia.expense.ui.design

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
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
) {
    val colors = ProExpenseTheme.colors
    val typography = ProExpenseTheme.typography

    Text(
        text =
            buildAnnotatedString {
                withStyle(typography.heroGreeting.toSpanStyle().copy(color = colors.onSurface)) {
                    append(prefix)
                }
                withStyle(
                    typography.heroGreetingEmphasis.toSpanStyle().copy(color = colors.primary),
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
