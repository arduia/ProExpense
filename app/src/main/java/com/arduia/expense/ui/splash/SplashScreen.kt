package com.arduia.expense.ui.splash

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.InfiniteTransition
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.StartOffset
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.arduia.expense.R
import com.arduia.expense.ui.theme.ProArtboard
import com.arduia.expense.ui.theme.ProExpenseTheme
import com.arduia.expense.ui.theme.ProMotion
import com.arduia.expense.ui.theme.rememberProReduceMotion

// Brand-mark geometry is a one-off splash artboard; inline sizes are the
// illustration exception to the tokenization rule.
private val SplashLogoSize = 96.dp
private const val SPLASH_LOGO_SCALE_REST = 0.92f
private const val SPLASH_LOGO_SCALE_RANGE = 0.08f
private const val SPLASH_DOT_REST_ALPHA = 0.35f
private const val SPLASH_DOT_STAGGER_DIVISOR = 6

@Composable
fun SplashScreen(modifier: Modifier = Modifier) {
    val colors = ProExpenseTheme.colors
    val dimens = ProExpenseTheme.dimensions
    val typography = ProExpenseTheme.typography
    val motion = ProExpenseTheme.motion
    val reduceMotion = rememberProReduceMotion()
    val entrance = rememberSplashEntrance(motion = motion, reduceMotion = reduceMotion)
    val markScale = SPLASH_LOGO_SCALE_REST + SPLASH_LOGO_SCALE_RANGE * entrance

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
            SplashLogoMark(scale = markScale, alpha = entrance)
            Text(
                text = stringResource(R.string.app_name),
                style = typography.heroGreeting.copy(fontFamily = typography.amountFamily),
                color = colors.onSurface,
                textAlign = TextAlign.Center,
                modifier = Modifier.alpha(entrance),
            )
        }

        SplashLoadingDots(
            reduceMotion = reduceMotion,
            modifier =
                Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = dimens.space44 + dimens.space32)
                    .alpha(entrance),
        )
    }
}

@Composable
private fun rememberSplashEntrance(
    motion: ProMotion,
    reduceMotion: Boolean,
): Float {
    var entrance by remember { mutableFloatStateOf(if (reduceMotion) 1f else 0f) }
    LaunchedEffect(reduceMotion) {
        if (!reduceMotion) {
            Animatable(0f).animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = motion.screenDurationMillis, easing = motion.standardEasing),
            ) { entrance = value }
        }
    }
    return entrance
}

@Composable
private fun SplashLogoMark(
    scale: Float,
    alpha: Float,
    modifier: Modifier = Modifier,
) {
    val colors = ProExpenseTheme.colors
    val dimens = ProExpenseTheme.dimensions

    Box(
        modifier =
            modifier
                .size(SplashLogoSize)
                .scale(scale)
                .alpha(alpha)
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
            modifier = Modifier.size(SplashLogoSize),
        )
    }
}

@Composable
private fun SplashLoadingDots(
    reduceMotion: Boolean,
    modifier: Modifier = Modifier,
) {
    val colors = ProExpenseTheme.colors
    val dimens = ProExpenseTheme.dimensions
    val motion = ProExpenseTheme.motion
    val restAlphas = listOf(0.45f, 0.75f, 1f)

    val transition = rememberInfiniteTransition(label = "splashDots")
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(dimens.space8),
    ) {
        restAlphas.forEachIndexed { index, restAlpha ->
            val alpha =
                if (reduceMotion) {
                    restAlpha
                } else {
                    dotPulseAlpha(transition = transition, motion = motion, index = index)
                }
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

@Composable
private fun dotPulseAlpha(
    transition: InfiniteTransition,
    motion: ProMotion,
    index: Int,
): Float {
    val dotProgress by transition.animateFloat(
        initialValue = SPLASH_DOT_REST_ALPHA,
        targetValue = 1f,
        animationSpec =
            infiniteRepeatable(
                animation =
                    tween(
                        durationMillis = motion.rowPulseDurationMillis / 3,
                        easing = motion.standardEasing,
                    ),
                repeatMode = RepeatMode.Reverse,
                initialStartOffset =
                    StartOffset(index * (motion.rowPulseDurationMillis / SPLASH_DOT_STAGGER_DIVISOR)),
            ),
        label = "splashDot$index",
    )
    return dotProgress
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
