package com.arduia.expense.ui.design

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.layout.layout
import com.arduia.expense.ui.theme.ProExpenseTheme

/**
 * Material outlined text field with notched floating label — mirrors `MdOutlinedField`.
 */
@Composable
fun ProOutlinedField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    helper: String? = null,
    leading: (@Composable () -> Unit)? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
) {
    val colors = ProExpenseTheme.colors
    val typography = ProExpenseTheme.typography
    var focused by remember { mutableStateOf(false) }
    val borderColor = if (focused) colors.primary else colors.outline
    val labelColor = if (focused) colors.primaryDeep else colors.onSurfaceVariant
    val shape = RoundedCornerShape(6.dp)

    Box(modifier = modifier) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 56.dp)
                .border(
                    width = if (focused) 2.dp else 1.4.dp,
                    color = borderColor,
                    shape = shape,
                )
                .background(colors.surface, shape)
                .padding(horizontal = if (leading != null) 14.dp else 16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            leading?.invoke()
            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier
                    .weight(1f)
                    .padding(
                        start = if (leading != null) 12.dp else 0.dp,
                        top = 15.dp,
                        bottom = 15.dp,
                    )
                    .onFocusChanged { focused = it.isFocused },
                textStyle = typography.fieldValue.copy(color = colors.onSurface),
                cursorBrush = SolidColor(colors.primary),
                singleLine = true,
                keyboardOptions = keyboardOptions,
                keyboardActions = keyboardActions,
                decorationBox = { inner ->
                    Box {
                        if (value.isEmpty() && !focused) {
                            Text(
                                text = placeholder,
                                style = typography.fieldValue,
                                color = colors.onSurfaceVariant,
                            )
                        }
                        inner()
                    }
                },
            )
        }
        Text(
            text = label,
            style = typography.fieldLabel,
            color = labelColor,
            modifier = Modifier
                .align(Alignment.TopStart)
                .layout { measurable, constraints ->
                    val placeable = measurable.measure(constraints)
                    layout(placeable.width, placeable.height) {
                        placeable.placeRelative(
                            x = if (leading != null) 44.dp.roundToPx() else 14.dp.roundToPx(),
                            y = (-8).dp.roundToPx(),
                        )
                    }
                }
                .background(colors.surface)
                .padding(horizontal = 4.dp),
        )
    }
    if (helper != null) {
        Text(
            text = helper,
            style = typography.fieldHelper,
            color = colors.onSurfaceVariant,
            modifier = Modifier.padding(start = 16.dp, top = 5.dp),
        )
    }
}
