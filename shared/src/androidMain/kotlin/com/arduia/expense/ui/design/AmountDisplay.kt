package com.arduia.expense.ui.design

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.keyframes
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.arduia.expense.ui.theme.ProExpenseTheme

@Composable
fun AmountDisplay(
    amountText: String,
    modifier: Modifier = Modifier,
    currencySymbol: String = "$",
    currencyCode: String = "USD",
    isZero: Boolean = amountText.toDoubleOrNull()?.let { it <= 0.0 } ?: true,
    showZeroValidation: Boolean = false,
    zeroHelperMessage: String = "Amount must be greater than $0",
    eyebrowText: String? = null,
    usePrimaryAmount: Boolean = false,
    // When present, the amount text no longer force-fills the available width — the row hugs
    // the digits instead, so this sits immediately after the amount rather than pinned to the
    // far edge of a full-width row. The auto-shrink-to-fit safety net still holds: the loose
    // incoming max-width constraint from ancestors still bounds/clips very long totals.
    trailing: (@Composable () -> Unit)? = null,
) {
    val colors = ProExpenseTheme.colors
    val dimens = ProExpenseTheme.dimensions
    val typography = ProExpenseTheme.typography
    val motion = ProExpenseTheme.motion
    val shakeOffset = remember { Animatable(0f) }
    val amountAutoSize =
        remember(typography.displayAmount.fontSize) {
            TextAutoSize.StepBased(
                minFontSize = 28.sp,
                maxFontSize = typography.displayAmount.fontSize,
                stepSize = 0.5.sp,
            )
        }

    LaunchedEffect(showZeroValidation) {
        if (showZeroValidation) {
            shakeOffset.snapTo(0f)
            shakeOffset.animateTo(
                targetValue = 0f,
                animationSpec =
                    keyframes {
                        durationMillis = motion.shakeDurationMillis
                        0f at 0
                        4f at (motion.shakeDurationMillis * 0.2f).toInt()
                        (-4f) at (motion.shakeDurationMillis * 0.4f).toInt()
                        3f at (motion.shakeDurationMillis * 0.6f).toInt()
                        (-3f) at (motion.shakeDurationMillis * 0.8f).toInt()
                        0f at motion.shakeDurationMillis
                    },
            )
        }
    }

    Column(
        modifier = modifier.offset(x = shakeOffset.value.dp),
        horizontalAlignment = Alignment.Start,
    ) {
        Text(
            text = eyebrowText ?: "AMOUNT · $currencyCode",
            style = typography.eyebrow,
            color = colors.onSurfaceMuted,
        )
        Row(verticalAlignment = Alignment.Bottom) {
            BasicText(
                text =
                    buildAmountLine(
                        currencySymbol = currencySymbol,
                        amountText = amountText,
                        isZero = isZero,
                        primaryColor = colors.primary,
                        amountColor =
                            when {
                                isZero -> colors.muted2
                                usePrimaryAmount -> colors.primary
                                else -> colors.onSurface
                            },
                        decimalColor = colors.onSurfaceMuted,
                        amountFamily = typography.amountFamily,
                    ),
                style = typography.displayAmount,
                autoSize = amountAutoSize,
                maxLines = 1,
                softWrap = false,
                overflow = TextOverflow.Clip,
                modifier =
                    Modifier
                        .then(if (trailing == null) Modifier.fillMaxWidth() else Modifier)
                        .padding(top = dimens.space8),
            )
            if (trailing != null) {
                Column(modifier = Modifier.padding(start = dimens.space8, bottom = dimens.space4)) {
                    trailing()
                }
            }
        }
        if (isZero && showZeroValidation) {
            Text(
                text = zeroHelperMessage,
                style = typography.caption.copy(fontWeight = androidx.compose.ui.text.font.FontWeight.Medium),
                color = colors.primary,
                modifier = Modifier.padding(top = dimens.space8),
            )
        }
    }
}

private fun buildAmountLine(
    currencySymbol: String,
    amountText: String,
    isZero: Boolean,
    primaryColor: Color,
    amountColor: Color,
    decimalColor: Color,
    amountFamily: androidx.compose.ui.text.font.FontFamily,
) = buildAnnotatedString {
    withStyle(SpanStyle(color = primaryColor, fontFamily = amountFamily)) {
        append(currencySymbol)
    }
    if (isZero) {
        withStyle(SpanStyle(color = amountColor, fontFamily = amountFamily)) {
            append(amountText)
        }
        return@buildAnnotatedString
    }
    val decimalIndex = amountText.indexOf('.')
    if (decimalIndex < 0) {
        withStyle(SpanStyle(color = amountColor, fontFamily = amountFamily)) {
            append(amountText)
        }
    } else {
        withStyle(SpanStyle(color = amountColor, fontFamily = amountFamily)) {
            append(amountText.substring(0, decimalIndex))
        }
        withStyle(SpanStyle(color = decimalColor, fontFamily = amountFamily)) {
            append(amountText.substring(decimalIndex))
        }
    }
}
