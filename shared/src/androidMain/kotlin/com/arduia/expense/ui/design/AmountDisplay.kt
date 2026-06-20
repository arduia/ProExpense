package com.arduia.expense.ui.design

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.keyframes
import androidx.compose.foundation.layout.Column
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
) {
    val colors = ProExpenseTheme.colors
    val dimens = ProExpenseTheme.dimensions
    val typography = ProExpenseTheme.typography
    val motion = ProExpenseTheme.motion
    val shakeOffset = remember { Animatable(0f) }
    val amountAutoSize = remember(typography.displayAmount.fontSize) {
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
                animationSpec = keyframes {
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
            color = colors.muted,
        )
        BasicText(
            text = buildAmountLine(
                currencySymbol = currencySymbol,
                amountText = amountText,
                isZero = isZero,
                primaryColor = colors.primary,
                amountColor = when {
                    isZero -> colors.muted2
                    usePrimaryAmount -> colors.primary
                    else -> colors.onSurface
                },
                decimalColor = colors.onSurfaceMuted,
                serifFamily = typography.serifFamily,
            ),
            style = typography.displayAmount.copy(fontFamily = typography.serifFamily),
            autoSize = amountAutoSize,
            maxLines = 1,
            softWrap = false,
            overflow = TextOverflow.Clip,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = dimens.space8),
        )
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
    serifFamily: androidx.compose.ui.text.font.FontFamily,
) = buildAnnotatedString {
    withStyle(SpanStyle(color = primaryColor, fontFamily = serifFamily)) {
        append(currencySymbol)
    }
    if (isZero) {
        withStyle(SpanStyle(color = amountColor, fontFamily = serifFamily)) {
            append(amountText)
        }
        return@buildAnnotatedString
    }
    val decimalIndex = amountText.indexOf('.')
    if (decimalIndex < 0) {
        withStyle(SpanStyle(color = amountColor, fontFamily = serifFamily)) {
            append(amountText)
        }
    } else {
        withStyle(SpanStyle(color = amountColor, fontFamily = serifFamily)) {
            append(amountText.substring(0, decimalIndex))
        }
        withStyle(SpanStyle(color = decimalColor, fontFamily = serifFamily)) {
            append(amountText.substring(decimalIndex))
        }
    }
}
