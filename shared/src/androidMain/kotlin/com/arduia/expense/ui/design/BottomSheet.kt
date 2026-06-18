package com.arduia.expense.ui.design

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.arduia.expense.ui.theme.ProExpenseTheme

@Composable
fun BottomSheet(
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    val colors = ProExpenseTheme.colors
    val dims = ProExpenseTheme.dimensions
    val shapes = ProExpenseTheme.shapes
    val scrimInteraction = remember { MutableInteractionSource() }

    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        val maxSheetHeight = maxHeight * dims.sheetMaxHeightFraction
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clickable(
                    interactionSource = scrimInteraction,
                    indication = null,
                    onClick = onDismiss,
                )
                .background(colors.scrim),
        )
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .height(maxSheetHeight)
                .clip(shapes.sheet)
                .background(colors.surface),
        ) {
            Box(
                modifier = Modifier
                    .padding(top = dims.sheetHandlePaddingTop, bottom = dims.sheetHandlePaddingBottom)
                    .align(Alignment.CenterHorizontally)
                    .size(width = dims.sheetHandleWidth, height = dims.sheetHandleHeight)
                    .clip(RoundedCornerShape(99.dp))
                    .background(colors.ink.copy(alpha = 0.18f)),
            )
            content()
        }
    }
}
