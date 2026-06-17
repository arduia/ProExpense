package com.arduia.expense.ui.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.arduia.expense.ui.design.ProExpensePaperBackground
import com.arduia.expense.ui.theme.ProExpenseTheme

@Composable
fun SplashScreen(
    modifier: Modifier = Modifier,
) {
    val colors = ProExpenseTheme.colors
    val typography = ProExpenseTheme.typography

    Box(
        modifier = modifier.fillMaxSize(),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            AppIconGlyph()
            Spacer(modifier = Modifier.height(22.dp))
            Text(
                text = "Pro Expense",
                style = typography.screenTitle,
                color = colors.ink,
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Your finance notebook",
                style = typography.tagline,
                color = colors.muted,
            )
        }

        SplashLoadingDots(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 80.dp),
        )
    }
}

@Composable
private fun SplashLoadingDots(
    modifier: Modifier = Modifier,
) {
    val dotColor = ProExpenseTheme.colors.primary
    val opacities = listOf(0.3f, 0.5f, 0.7f)

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        opacities.forEach { alpha ->
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .clip(CircleShape)
                    .background(dotColor.copy(alpha = alpha)),
            )
        }
    }
}

@Preview(showBackground = true, widthDp = 414, heightDp = 868)
@Composable
private fun SplashScreenPreview() {
    ProExpenseTheme {
        ProExpensePaperBackground {
            SplashScreen()
        }
    }
}
