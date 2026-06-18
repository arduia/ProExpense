package com.arduia.expense.feature.logging.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.arduia.expense.ui.design.IconChevronRight
import com.arduia.expense.ui.theme.ProExpenseTheme

private val keypadKeys = listOf("1", "2", "3", "4", "5", "6", "7", "8", "9", ".", "0", "⌫")

@Composable
fun NumericKeypad(
    canProceed: Boolean,
    onKey: (String) -> Unit,
    onSave: () -> Unit,
    onNext: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = ProExpenseTheme.colors
    val typography = ProExpenseTheme.typography
    val dims = ProExpenseTheme.dimensions
    val shapes = ProExpenseTheme.shapes

    Column(
        modifier = modifier.padding(horizontal = dims.space16, vertical = dims.space8),
        verticalArrangement = Arrangement.spacedBy(dims.space8),
    ) {
        keypadKeys.chunked(3).forEach { row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(dims.space8),
            ) {
                row.forEach { key ->
                    KeypadKey(
                        label = key,
                        onClick = { onKey(key) },
                        modifier = Modifier.weight(1f),
                    )
                }
            }
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(dims.space8),
        ) {
            KeypadActionButton(
                text = "Save",
                enabled = canProceed,
                primary = false,
                onClick = onSave,
                modifier = Modifier.weight(1f),
            )
            KeypadActionButton(
                text = "Next",
                enabled = canProceed,
                primary = true,
                onClick = onNext,
                trailing = { IconChevronRight(color = colors.onPrimary, size = dims.iconSizeNav) },
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
private fun KeypadKey(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = ProExpenseTheme.colors
    val typography = ProExpenseTheme.typography
    val dims = ProExpenseTheme.dimensions
    val shapes = ProExpenseTheme.shapes
    val interaction = remember { MutableInteractionSource() }

    Box(
        modifier = modifier
            .height(dims.keypadKeyHeight)
            .clip(shapes.quickTile)
            .background(colors.surface)
            .clickable(interactionSource = interaction, indication = ripple(), onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = label,
            style = typography.sectionHead,
            color = colors.onSurface,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun KeypadActionButton(
    text: String,
    enabled: Boolean,
    primary: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    trailing: (@Composable () -> Unit)? = null,
) {
    val colors = ProExpenseTheme.colors
    val typography = ProExpenseTheme.typography
    val dims = ProExpenseTheme.dimensions
    val shapes = RoundedCornerShape(dims.space14)
    val interaction = remember { MutableInteractionSource() }

    Row(
        modifier = modifier
            .height(dims.keypadActionHeight)
            .clip(shapes)
            .background(
                when {
                    primary && enabled -> colors.primary
                    primary -> colors.primary.copy(alpha = 0.45f)
                    else -> colors.surface
                },
            )
            .clickable(
                interactionSource = interaction,
                indication = ripple(),
                enabled = enabled,
                onClick = onClick,
            ),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = text,
            style = if (primary) typography.buttonLarge else typography.buttonMedium,
            color = when {
                primary && enabled -> colors.onPrimary
                primary -> colors.onPrimary.copy(alpha = 0.7f)
                enabled -> colors.onSurface
                else -> colors.onSurfaceVariant
            },
        )
        trailing?.invoke()
    }
}

@Preview(showBackground = true, widthDp = 414, heightDp = 320)
@Composable
private fun NumericKeypadEnabledPreview() {
    ProExpenseTheme {
        NumericKeypad(
            canProceed = true,
            onKey = {},
            onSave = {},
            onNext = {},
        )
    }
}

@Preview(showBackground = true, widthDp = 414, heightDp = 320)
@Composable
private fun NumericKeypadDisabledPreview() {
    ProExpenseTheme {
        NumericKeypad(
            canProceed = false,
            onKey = {},
            onSave = {},
            onNext = {},
        )
    }
}
