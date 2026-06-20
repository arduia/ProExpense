package com.proexpense.designsystem.components

import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.proexpense.designsystem.icons.ProIcons
import com.proexpense.designsystem.theme.Blue500
import com.proexpense.designsystem.theme.DarkGray
import com.proexpense.designsystem.theme.Gray300
import com.proexpense.designsystem.theme.Gray600
import com.proexpense.designsystem.theme.Line
import com.proexpense.designsystem.theme.Manrope
import com.proexpense.designsystem.theme.Muted2
import com.proexpense.designsystem.theme.OnFilled
import com.proexpense.designsystem.theme.ProType
import com.proexpense.designsystem.theme.White
import kotlin.math.roundToInt

/* ─────────────────────────────────────────────────────────────────────────
 * Amount entry — input rules, display & keypad. See amount-entry.md.
 * ───────────────────────────────────────────────────────────────────────── */

/** Holds the raw amount string and applies every input rule from the spec. */
class AmountInput {
    var raw by mutableStateOf("")
        private set

    val value: Double get() = raw.toDoubleOrNull() ?: 0.0
    val canProceed: Boolean get() = value > 0.0
    val isPlaceholder: Boolean get() = raw.isEmpty()

    fun key(k: String) {
        var a = raw
        when (k) {
            "⌫" -> a = a.dropLast(1)
            "." -> if (!a.contains('.')) a = if (a.isEmpty()) "0." else "$a."
            else -> {
                if (a.contains('.')) {
                    val dec = a.substringAfter('.')
                    if (dec.length >= 2) return            // max 2 decimals
                } else if (a.length >= 7) return           // max 7 whole digits
                a += k
            }
        }
        if (Regex("^0\\d").containsMatchIn(a)) a = a.trimStart('0')   // strip leading zeros
        raw = a
    }
}

@Composable
fun rememberAmountInput(): AmountInput = remember { AmountInput() }

/** Builds the formatted amount: blue `$`, grouped whole, greyed decimals. */
private fun amountAnnotated(raw: String, placeholder: Boolean): AnnotatedString = buildAnnotatedString {
    withStyle(SpanStyle(color = Blue500, fontSize = 30.sp)) { append("$") }
    if (placeholder) {
        withStyle(SpanStyle(color = Muted2)) { append("0") }
        return@buildAnnotatedString
    }
    val whole = raw.substringBefore('.')
    val dec = if (raw.contains('.')) raw.substringAfter('.') else null
    val grouped = whole.ifEmpty { "0" }.reversed().chunked(3).joinToString(",").reversed()
    withStyle(SpanStyle(color = DarkGray)) { append(grouped) }
    if (dec != null) withStyle(SpanStyle(color = Gray600)) { append("." + dec.padEnd(2, '0').take(2)) }
}

@Composable
fun AmountDisplay(
    input: AmountInput,
    modifier: Modifier = Modifier,
    shakeTrigger: Int = 0,
) {
    val offsetX = remember { Animatable(0f) }
    LaunchedEffect(shakeTrigger) {
        if (shakeTrigger > 0) {
            val kf = listOf(0f, -4f, 4f, -3f, 3f, 0f)
            kf.forEach { offsetX.animateTo(it, animationSpec = androidx.compose.animation.core.tween(46)) }
        }
    }
    Text(
        text = amountAnnotated(input.raw, input.isPlaceholder),
        style = ProType.DisplayAmount,
        modifier = modifier.offset { IntOffset(offsetX.value.roundToInt(), 0) },
    )
}

@Composable
fun AmountKeypad(
    onKey: (String) -> Unit,
    canProceed: Boolean,
    onSave: () -> Unit,
    onNext: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val keys = listOf("1", "2", "3", "4", "5", "6", "7", "8", "9", ".", "0", "⌫")
    Column(modifier = modifier.padding(horizontal = 16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        keys.chunked(3).forEach { rowKeys ->
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                rowKeys.forEach { k -> KeyButton(k, Modifier.weight(1f), onKey) }
            }
        }
        Row(Modifier.fillMaxWidth().padding(top = 0.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            // Save — secondary
            Box(
                Modifier.weight(1f).clip(RoundedCornerShape(14.dp))
                    .background(White)
                    .border(1.4.dp, Gray300, RoundedCornerShape(14.dp))
                    .pressScale(enabled = canProceed, onClick = onSave)
                    .padding(vertical = 15.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text("Save", color = if (canProceed) DarkGray else Gray300, fontFamily = Manrope, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
            }
            // Next — primary
            Row(
                Modifier.weight(1f).clip(RoundedCornerShape(14.dp))
                    .background(if (canProceed) Blue500 else Blue500.copy(alpha = 0.55f))
                    .pressScale(enabled = canProceed, onClick = onNext)
                    .padding(vertical = 15.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text("Next", color = OnFilled, fontFamily = Manrope, fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
                Icon(ProIcons.ChevronRight, contentDescription = null, tint = OnFilled, modifier = Modifier.padding(start = 4.dp))
            }
        }
    }
}

@Composable
private fun KeyButton(k: String, modifier: Modifier, onKey: (String) -> Unit) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(White)
            .border(1.dp, Line, RoundedCornerShape(12.dp))
            .pressScale(onClick = { onKey(k) })
            .padding(vertical = 14.dp),
        contentAlignment = Alignment.Center,
    ) {
        if (k == "⌫") {
            Icon(ProIcons.Back, contentDescription = "Delete", tint = DarkGray)
        } else {
            Text(k, style = ProType.KeypadKey, color = DarkGray)
        }
    }
}
