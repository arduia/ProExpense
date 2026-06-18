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
import androidx.compose.ui.unit.dp
import com.arduia.expense.ui.design.ProCircularProgress
import com.arduia.expense.ui.theme.ProExpenseTheme

/**
 * Android 12+ splash — mirrors `AndSplash` from android-onboarding.jsx.
 * Centered app icon, circular spinner, bottom branding wordmark.
 */
@Composable
fun SplashScreen(
    modifier: Modifier = Modifier,
) {
    val colors = ProExpenseTheme.colors
    val typography = ProExpenseTheme.typography

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(colors.surface),
    ) {
        AppIconGlyph(
            modifier = Modifier.align(Alignment.Center),
        )

        ProCircularProgress(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = ProExpenseTheme.dimensions.splashProgressBottom),
        )

        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = ProExpenseTheme.dimensions.splashBrandingBottom),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = "Pro Expense",
                style = typography.brandName,
                color = colors.onSurface,
            )
            Spacer(Modifier.height(ProExpenseTheme.dimensions.splashBrandingGap))
            Text(
                text = "Your finance notebook",
                style = typography.tagline,
                color = colors.onSurfaceVariant,
            )
        }
    }
}

@Preview(showBackground = true, widthDp = 414, heightDp = 868)
@Composable
private fun SplashScreenPreview() {
    ProExpenseTheme {
        SplashScreen()
    }
}
