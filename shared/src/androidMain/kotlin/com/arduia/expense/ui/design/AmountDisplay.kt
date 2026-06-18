package com.arduia.expense.ui.design

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.keyframes
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
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
) {
    val colors = ProExpenseTheme.colors
    val dimens = ProExpenseTheme.dimensions
    val typography = ProExpenseTheme.typography
    val motion = ProExpenseTheme.motion
    val shakeOffset = remember { Animatable(0f) }

    LaunchedEffect(showZeroValidation) {
        if (showZeroValidation) {
            shakeOffset.snapTo(0f)
            shakeOffset.animateTo(
                targetValue = 0f,
                animationSpec = keyframes {
                    durationMillis = motion.shakeDurationMillis
                    0f at 0
                    8f at (motion.shakeDurationMillis * 0.2f).toInt()
                    (-8f) at (motion.shakeDurationMillis * 0.4f).toInt()
                    6f at (motion.shakeDurationMillis * 0.6f).toInt()
                    (-6f) at (motion.shakeDurationMillis * 0.8f).toInt()
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
            text = "AMOUNT · $currencyCode",
            style = typography.eyebrow,
            color = colors.muted,
        )
        Row(
            modifier = Modifier.padding(top = dimens.space8),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = currencySymbol,
                style = typography.displayAmount,
                color = colors.primary,
            )
            Text(
                text = amountText,
                style = typography.displayAmount,
                color = if (isZero) colors.muted2 else colors.onSurface,
            )
        }
        if (isZero && showZeroValidation) {
            Text(
                text = zeroHelperMessage,
                style = typography.bodyMedium,
                color = colors.primary,
                modifier = Modifier.padding(top = dimens.space8),
            )
        }
    }
}
