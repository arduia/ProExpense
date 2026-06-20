package com.arduia.expense.ui.design

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import com.arduia.expense.ui.theme.ProExpenseTheme

@Composable
fun Modifier.proCardSurface(
    shape: Shape = ProExpenseTheme.shapes.card,
    borderColor: Color = ProExpenseTheme.colors.line,
    backgroundColor: Color = ProExpenseTheme.colors.surface,
): Modifier {
    val cardElevation = ProExpenseTheme.elevation.card.firstOrNull()
    return this
        .then(
            if (cardElevation != null) {
                Modifier.shadow(
                    elevation = cardElevation.blur,
                    shape = shape,
                    spotColor = cardElevation.color,
                    ambientColor = cardElevation.color,
                )
            } else {
                Modifier
            },
        )
        .clip(shape)
        .border(BorderStroke(1.dp, borderColor), shape)
        .background(backgroundColor)
}
