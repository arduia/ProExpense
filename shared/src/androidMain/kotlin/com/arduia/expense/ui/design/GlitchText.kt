package com.arduia.expense.ui.design

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlin.random.Random

private const val GLITCH_STEP_COUNT = 7
private const val GLITCH_STEP_DURATION_MS = 40L
private const val GLITCH_MAX_OFFSET_DP = 3
private const val GLITCH_GHOST_ALPHA = 0.65f
private val GlitchRed = Color(0xFFFF3B5C)
private val GlitchCyan = Color(0xFF00E5FF)

/**
 * Plays a brief RGB-split glitch flicker on [text] once per [playbackKey], then settles into a
 * normal, single-color render. The offset copies decay to zero over [GLITCH_STEP_COUNT] steps
 * so the effect reads as a quick digital stutter rather than a looping animation.
 */
@Composable
fun GlitchText(
    text: String,
    style: TextStyle,
    color: Color,
    modifier: Modifier = Modifier,
    playbackKey: Any? = Unit,
) {
    var redOffsetDp by remember { mutableIntStateOf(0) }
    var cyanOffsetDp by remember { mutableIntStateOf(0) }
    var settled by remember { mutableStateOf(false) }

    LaunchedEffect(playbackKey) {
        settled = false
        val random = Random(playbackKey?.hashCode() ?: 0)
        for (step in GLITCH_STEP_COUNT downTo 1) {
            val magnitude = (GLITCH_MAX_OFFSET_DP * step) / GLITCH_STEP_COUNT
            redOffsetDp = if (magnitude > 0) random.nextInt(-magnitude, magnitude + 1) else 0
            cyanOffsetDp = if (magnitude > 0) random.nextInt(-magnitude, magnitude + 1) else 0
            delay(GLITCH_STEP_DURATION_MS)
        }
        redOffsetDp = 0
        cyanOffsetDp = 0
        settled = true
    }

    Box(modifier = modifier) {
        if (!settled) {
            Text(
                text = text,
                style = style,
                color = GlitchRed.copy(alpha = GLITCH_GHOST_ALPHA),
                modifier = Modifier.offset(x = redOffsetDp.dp),
            )
            Text(
                text = text,
                style = style,
                color = GlitchCyan.copy(alpha = GLITCH_GHOST_ALPHA),
                modifier = Modifier.offset(x = cyanOffsetDp.dp),
            )
        }
        Text(text = text, style = style, color = color)
    }
}
