package com.arduia.expense.ui.design

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.unit.dp
import com.arduia.expense.ui.theme.ProExpenseTheme

@Composable
fun ProExpenseCard(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    val colors = ProExpenseTheme.colors
    val shapes = ProExpenseTheme.shapes

    Surface(
        // Soft two-layer paper shadow per design/DESIGN-SYSTEM.md §6 — very low-alpha ink, not a
        // heavy Material elevation. Shadow colour is applied here; Surface keeps elevation at 0.
        modifier = modifier.shadow(
            elevation = 14.dp,
            shape = shapes.card,
            clip = false,
            ambientColor = colors.ink.copy(alpha = 0.04f),
            spotColor = colors.ink.copy(alpha = 0.04f),
        ),
        shape = shapes.card,
        color = colors.card,
        contentColor = colors.ink,
        border = BorderStroke(1.dp, colors.line),
        tonalElevation = 0.dp,
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            content = content,
        )
    }
}

@Composable
fun ProExpensePaperBackground(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Surface(
        modifier = modifier,
        color = ProExpenseTheme.colors.paper,
        contentColor = ProExpenseTheme.colors.ink,
    ) {
        content()
    }
}
