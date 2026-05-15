package com.arduia.expense.designsystem.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Immutable
data class Spacing(
    val grid1: Dp = 4.dp,
    val grid2: Dp = 8.dp,
    val grid3: Dp = 16.dp,
    val grid4: Dp = 32.dp,
    val grid5: Dp = 48.dp,
    val grid6: Dp = 64.dp,
)

val LocalSpacing = staticCompositionLocalOf { Spacing() }
