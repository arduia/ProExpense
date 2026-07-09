package com.arduia.expense.ui.splash

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.arduia.expense.R
import com.arduia.expense.ui.theme.ProArtboard
import com.arduia.expense.ui.theme.ProExpenseTheme

@Composable
fun SplashScreen(modifier: Modifier = Modifier) {
    val colors = ProExpenseTheme.colors
    val dimens = ProExpenseTheme.dimensions
    val typography = ProExpenseTheme.typography

    // Brand-mark geometry is a one-off splash artboard; inline sizes are the
    // illustration exception to the tokenization rule.
    val logoSize = 96.dp

    Box(
        modifier =
            modifier
                .fillMaxSize()
                .background(colors.paper)
                .navigationBarsPadding(),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(dimens.space18),
        ) {
            Box(
                modifier =
                    Modifier
                        .size(logoSize)
                        .shadow(
                            elevation = dimens.space16,
                            shape = ProExpenseTheme.shapes.tile,
                            spotColor = colors.primary.copy(alpha = 0.35f),
                            ambientColor = colors.primary.copy(alpha = 0.35f),
                        ).clip(ProExpenseTheme.shapes.tile)
                        .background(colors.primary),
                contentAlignment = Alignment.Center,
            ) {
                Image(
                    painter = painterResource(R.drawable.ic_launcher_foreground),
                    contentDescription = null,
                    modifier = Modifier.size(logoSize),
                )
            }
            Text(
                text = stringResource(R.string.app_name),
                style = typography.heroGreeting.copy(fontFamily = typography.amountFamily),
                color = colors.onSurface,
                textAlign = TextAlign.Center,
            )
        }

        SplashLoadingDots(
            modifier =
                Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = dimens.space44 + dimens.space32),
        )
    }
}

@Composable
private fun SplashLoadingDots(modifier: Modifier = Modifier) {
    val colors = ProExpenseTheme.colors
    val dimens = ProExpenseTheme.dimensions

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(dimens.space8),
    ) {
        val alphas = listOf(0.45f, 0.75f, 1f)
        alphas.forEach { alpha ->
            Box(
                modifier =
                    Modifier
                        .size(dimens.pageIndicatorDotSize)
                        .clip(CircleShape)
                        .background(colors.primary.copy(alpha = alpha)),
            )
        }
    }
}

@Preview(
    name = "Splash",
    widthDp = ProArtboard.PIXEL_9_PRO_WIDTH_DP,
    heightDp = ProArtboard.PIXEL_9_PRO_HEIGHT_DP,
    showBackground = true,
)
@Composable
private fun SplashScreenPreview() {
    ProExpenseTheme {
        SplashScreen()
    }
}
