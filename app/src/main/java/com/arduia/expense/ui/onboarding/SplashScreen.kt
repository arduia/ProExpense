package com.arduia.expense.ui.onboarding

import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
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
            SplashBrandMark(modifier = Modifier.size(88.dp))
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
private fun SplashBrandMark(modifier: Modifier = Modifier) {
    val colors = ProExpenseTheme.colors

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(22.dp))
            .background(colors.primary),
        contentAlignment = Alignment.Center,
    ) {
        Canvas(modifier = Modifier.size(48.dp)) {
            val inset = 10.dp.toPx()
            drawRoundRect(
                color = colors.onPrimaryWarm,
                topLeft = Offset(inset, inset),
                size = Size(size.width - inset * 2, size.height - inset * 2),
                cornerRadius = CornerRadius(4.dp.toPx(), 4.dp.toPx()),
                style = Stroke(width = 2.5.dp.toPx()),
            )
            val handleR = 3.dp.toPx()
            listOf(
                Offset(inset, inset),
                Offset(size.width - inset, inset),
                Offset(inset, size.height - inset),
                Offset(size.width - inset, size.height - inset),
            ).forEach { center ->
                drawCircle(colors.onPrimaryWarm, radius = handleR, center = center)
            }
            val path = Path().apply {
                moveTo(size.width * 0.35f, size.height * 0.62f)
                lineTo(size.width * 0.48f, size.height * 0.75f)
                lineTo(size.width * 0.68f, size.height * 0.42f)
            }
            drawPath(path, colors.onPrimaryWarm, style = Stroke(2.5.dp.toPx(), cap = StrokeCap.Round))
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
        SplashScreenContent()
    }
}
