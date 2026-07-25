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
import androidx.compose.foundation.border
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
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
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
private val SplashLogoSize = 84.dp
private val SplashLogoHaloSize = 168.dp
private const val SPLASH_LOGO_SCALE_REST = 0.92f
private const val SPLASH_LOGO_SCALE_RANGE = 0.08f
private const val SPLASH_DOT_REST_ALPHA = 0.35f
private const val SPLASH_DOT_STAGGER_DIVISOR = 6
private const val SPLASH_RING_OUTER_RADIUS_FACTOR = 0.62f
private const val SPLASH_RING_OUTER_ALPHA = 0.08f
private const val SPLASH_RING_OUTER_CENTER_X_FACTOR = 1.05f
private const val SPLASH_RING_OUTER_CENTER_Y_FACTOR = 0.1f
private const val SPLASH_RING_GLOW_RADIUS_FACTOR = 0.9f
private const val SPLASH_RING_GLOW_ALPHA = 0.04f
private const val SPLASH_RING_GLOW_CENTER_X_FACTOR = 0.25f
private const val SPLASH_RING_GLOW_CENTER_Y_FACTOR = 1.05f
private const val SPLASH_HALO_OUTER_SIZE_FACTOR = 0.79f
private const val SPLASH_HALO_OUTER_ROTATION_DEG = 12f
private const val SPLASH_HALO_OUTER_ALPHA = 0.22f
private const val SPLASH_HALO_INNER_SIZE_FACTOR = 0.62f
private const val SPLASH_HALO_INNER_ROTATION_DEG = -8f
private const val SPLASH_HALO_INNER_ALPHA = 0.14f

@Composable
fun SplashScreen(modifier: Modifier = Modifier) {
    val colors = ProExpenseTheme.colors
    val dimens = ProExpenseTheme.dimensions
    val typography = ProExpenseTheme.typography
    val motion = ProExpenseTheme.motion
    val reduceMotion = rememberProReduceMotion()
    val entrance = rememberSplashEntrance(motion = motion, reduceMotion = reduceMotion)
    val markScale = SPLASH_LOGO_SCALE_REST + SPLASH_LOGO_SCALE_RANGE * entrance
    val gradient =
        Brush.linearGradient(
            colors = colors.heroGradientStops,
            start = Offset(0f, 0f),
            end = Offset.Infinite,
        )

    Box(
        modifier =
            modifier
                .fillMaxSize()
                .background(gradient)
                .drawSplashRings()
                .navigationBarsPadding(),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(dimens.space8),
        ) {
            SplashLogoMark(scale = markScale, alpha = entrance)
            Text(
                text = stringResource(R.string.app_name),
                style = typography.heroGreeting.copy(fontFamily = typography.amountFamily),
                color = Color.White,
                textAlign = TextAlign.Center,
                modifier = Modifier.alpha(entrance).padding(top = dimens.space8),
            )
            Text(
                text = stringResource(R.string.splash_tagline),
                style = typography.body,
                color = Color.White.copy(alpha = 0.72f),
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

/** Two faint concentric ring outlines offset past the top-right and bottom-left corners (Blue Banking canvas). */
private fun Modifier.drawSplashRings(): Modifier =
    this.drawWithContent {
        drawContent()
        drawCircle(
            color = Color.White.copy(alpha = SPLASH_RING_OUTER_ALPHA),
            radius = size.width * SPLASH_RING_OUTER_RADIUS_FACTOR,
            center =
                Offset(
                    size.width * SPLASH_RING_OUTER_CENTER_X_FACTOR,
                    -size.width * SPLASH_RING_OUTER_CENTER_Y_FACTOR,
                ),
            style = Stroke(width = 1.5.dp.toPx()),
        )
        drawCircle(
            color = Color.White.copy(alpha = SPLASH_RING_GLOW_ALPHA),
            radius = size.width * SPLASH_RING_GLOW_RADIUS_FACTOR,
            center =
                Offset(
                    -size.width * SPLASH_RING_GLOW_CENTER_X_FACTOR,
                    size.height * SPLASH_RING_GLOW_CENTER_Y_FACTOR,
                ),
        )
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
    Box(
        modifier =
            modifier
                .size(SplashLogoHaloSize)
                .scale(scale)
                .alpha(alpha),
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier =
                Modifier
                    .size(SplashLogoHaloSize * SPLASH_HALO_OUTER_SIZE_FACTOR)
                    .rotate(SPLASH_HALO_OUTER_ROTATION_DEG)
                    .border(
                        width = 1.5.dp,
                        color = Color.White.copy(alpha = SPLASH_HALO_OUTER_ALPHA),
                        shape = ProExpenseTheme.shapes.tile,
                    ),
        )
        Box(
            modifier =
                Modifier
                    .size(SplashLogoHaloSize * SPLASH_HALO_INNER_SIZE_FACTOR)
                    .rotate(SPLASH_HALO_INNER_ROTATION_DEG)
                    .border(
                        width = 1.5.dp,
                        color = Color.White.copy(alpha = SPLASH_HALO_INNER_ALPHA),
                        shape = ProExpenseTheme.shapes.tile,
                    ),
        )
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
                        .background(Color.White.copy(alpha = alpha)),
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

@Preview(
    name = "Splash — dark",
    widthDp = ProArtboard.PIXEL_9_PRO_WIDTH_DP,
    heightDp = ProArtboard.PIXEL_9_PRO_HEIGHT_DP,
    showBackground = true,
)
@Composable
private fun SplashScreenDarkPreview() {
    ProExpenseTheme(darkTheme = true) {
        SplashScreen()
    }
}
