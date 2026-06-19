package com.arduia.expense.ui.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.arduia.expense.R
import com.arduia.expense.ui.theme.ProArtboard
import com.arduia.expense.ui.theme.ProExpenseTheme
import kotlinx.coroutines.delay

private const val SPLASH_DURATION_MS = 1_800L

@Composable
fun SplashScreen(
    onFinished: () -> Unit,
    modifier: Modifier = Modifier,
    autoAdvance: Boolean = true,
) {
    if (autoAdvance) {
        LaunchedEffect(Unit) {
            delay(SPLASH_DURATION_MS)
            onFinished()
        }
    }
    SplashScreenContent(modifier = modifier)
}

@Composable
fun SplashScreenContent(modifier: Modifier = Modifier) {
    val colors = ProExpenseTheme.colors
    val dimens = ProExpenseTheme.dimensions
    val typography = ProExpenseTheme.typography

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(colors.paper)
            .statusBarsPadding()
            .navigationBarsPadding(),
    ) {
        Column(
            modifier = Modifier.align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            LauncherAppIcon(
                modifier = Modifier.size(88.dp),
                contentDescription = stringResource(R.string.app_name),
            )
            Text(
                text = stringResource(R.string.app_name),
                style = typography.screenTitle,
                color = colors.onSurface,
                modifier = Modifier.padding(top = dimens.space24),
            )
            Text(
                text = stringResource(R.string.splash_tagline),
                style = typography.body,
                color = colors.onSurfaceMuted,
                modifier = Modifier.padding(top = dimens.space8),
            )
        }

        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = dimens.space44),
            horizontalArrangement = Arrangement.spacedBy(dimens.space8),
        ) {
            repeat(3) { index ->
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(
                            if (index == 1) colors.primary else colors.primaryTint,
                        ),
                )
            }
        }
    }
}

@Composable
private fun LauncherAppIcon(
    @Suppress("UNUSED_PARAMETER") contentDescription: String,
    modifier: Modifier = Modifier,
) {
    SplashBrandMark(modifier = modifier)
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
        SplashScreenContent()
    }
}
