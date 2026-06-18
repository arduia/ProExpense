package com.arduia.expense.ui.design

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import com.arduia.expense.ui.theme.ProExpenseTheme

/**
 * Material radio button — mirrors `MdRadio` from android-frame.jsx.
 */
@Composable
fun ProRadio(
    selected: Boolean,
    modifier: Modifier = Modifier,
) {
    val colors = ProExpenseTheme.colors
    val dims = ProExpenseTheme.dimensions
    Box(
        modifier = modifier
            .size(dims.radioOuterSize)
            .clip(CircleShape)
            .border(
                width = dims.radioBorderWidth,
                color = if (selected) colors.primary else colors.outline,
                shape = CircleShape,
            ),
        contentAlignment = Alignment.Center,
    ) {
        if (selected) {
            Box(
                modifier = Modifier
                    .size(dims.radioInnerSize)
                    .clip(CircleShape)
                    .background(colors.primary),
            )
        }
    }
}
