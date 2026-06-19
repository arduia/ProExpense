package com.arduia.expense.ui.design

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.arduia.expense.ui.theme.ProExpenseTheme

private val keypadKeys = listOf(
    listOf("1", "2", "3"),
    listOf("4", "5", "6"),
    listOf("7", "8", "9"),
    listOf(".", "0", "backspace"),
)

@Composable
fun NumericKeypad(
    actionsEnabled: Boolean,
    onKey: (String) -> Unit,
    onBackspace: () -> Unit,
    onSave: () -> Unit,
    onNext: () -> Unit,
    modifier: Modifier = Modifier,
    saveLabel: String = "Save",
    nextLabel: String = "Next",
) {
    val colors = ProExpenseTheme.colors
    val dimens = ProExpenseTheme.dimensions
    val motion = ProExpenseTheme.motion
    val actionAlpha = if (actionsEnabled) 1f else motion.keypadDisabledOpacity

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(dimens.space8),
    ) {
        keypadKeys.forEach { row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(dimens.space8),
            ) {
                row.forEach { key ->
                    KeypadKey(
                        label = key,
                        onClick = {
                            if (key == "backspace") onBackspace() else onKey(key)
                        },
                        modifier = Modifier.weight(1f),
                    )
                }
            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = dimens.space8)
                .alpha(actionAlpha),
            horizontalArrangement = Arrangement.spacedBy(dimens.space8),
        ) {
            ProButton(
                text = saveLabel,
                onClick = onSave,
                modifier = Modifier.weight(1f),
                variant = ProButtonVariant.Secondary,
                enabled = actionsEnabled,
            )
            ProButton(
                text = nextLabel,
                onClick = onNext,
                modifier = Modifier.weight(1f),
                enabled = actionsEnabled,
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
    val dimens = ProExpenseTheme.dimensions
    val typography = ProExpenseTheme.typography
    val interactionSource = remember { MutableInteractionSource() }
    val keyShape = ProExpenseTheme.shapes.keypadKey
    val isBackspace = label == "backspace"
    val rippleShape = if (isBackspace) CircleShape else keyShape

    Box(
        modifier = modifier
            .aspectRatio(1.45f)
            .proPressScale(interactionSource)
            .clip(rippleShape)
            .then(
                if (isBackspace) {
                    Modifier
                } else {
                    Modifier
                        .border(BorderStroke(1.dp, colors.line), keyShape)
                        .background(colors.surface)
                },
            )
            .proRippleClickable(
                onClick = onClick,
                interactionSource = interactionSource,
                role = Role.Button,
            )
            .defaultMinSize(minHeight = dimens.touchTargetMin),
        contentAlignment = Alignment.Center,
    ) {
        if (label == "backspace") {
            ProIcon(
                glyph = ProIconGlyph.Backspace,
                contentDescription = "Backspace",
                tint = colors.onSurface,
                size = dimens.iconNav,
            )
        } else {
            Text(
                text = label,
                style = typography.keypadKey,
                color = colors.onSurface,
            )
        }
    }
}
