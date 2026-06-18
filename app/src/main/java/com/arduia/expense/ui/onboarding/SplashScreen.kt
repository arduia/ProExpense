package com.arduia.expense.ui.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.arduia.expense.ui.design.ProCircularProgress
import com.arduia.expense.ui.theme.ProExpenseTheme

/** Flow 04 — brand splash on cold start. */
@Composable
fun SplashScreen(
    modifier: Modifier = Modifier,
) {
    val colors = ProExpenseTheme.colors
    val typography = ProExpenseTheme.typography
    val dims = ProExpenseTheme.dimensions

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(colors.paper),
    ) {
        Column(
            modifier = Modifier.align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            AppIconGlyph()
            Spacer(Modifier.height(dims.space16))
            Text(
                text = "Pro Expense",
                style = typography.brandName,
                color = colors.onSurface,
            )
            Spacer(Modifier.height(dims.splashBrandingGap))
            Text(
                text = "Your finance notebook",
                style = typography.tagline,
                color = colors.onSurfaceMuted,
            )
        }

        ProCircularProgress(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = dims.splashProgressBottom),
        )
    }
}

@Preview(showBackground = true, widthDp = 414, heightDp = 868)
@Composable
private fun SplashScreenPreview() {
    ProExpenseTheme {
        SplashScreen()
    }
}
