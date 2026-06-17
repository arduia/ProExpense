package com.arduia.expense.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val ProExpenseColorScheme = lightColorScheme(
    primary = Color(0xFF039BE5),
    onPrimary = Color.White,
    background = Color(0xFFEEEEEE),
    onBackground = Color(0xFF212121),
)

@Composable
fun ProExpenseTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = ProExpenseColorScheme,
        content = content,
    )
}
