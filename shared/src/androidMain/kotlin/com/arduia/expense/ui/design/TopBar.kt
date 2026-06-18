package com.arduia.expense.ui.design

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.arduia.expense.ui.theme.ProExpenseTheme

@Composable
fun ProTopBar(
    title: String,
    modifier: Modifier = Modifier,
    onBack: (() -> Unit)? = null,
    trailing: (@Composable () -> Unit)? = null,
) {
    val colors = ProExpenseTheme.colors
    val dims = ProExpenseTheme.dimensions
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(dims.topBarHeight)
            .padding(start = if (onBack != null) dims.space6 else dims.space16, end = dims.space8),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (onBack != null) {
            Box(
                modifier = Modifier
                    .size(dims.iconButtonSize)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = ripple(bounded = false, radius = dims.rippleRadiusIconButton),
                        onClick = onBack,
                    ),
                contentAlignment = Alignment.Center,
            ) {
                IconBack(color = colors.onSurface, size = dims.iconSizeNav)
            }
        }
        Text(
            text = title,
            style = ProExpenseTheme.typography.screenTitle,
            color = colors.onSurface,
            modifier = Modifier
                .weight(1f)
                .padding(start = if (onBack != null) dims.space4 else 0.dp),
        )
        trailing?.invoke()
    }
}
