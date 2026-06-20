package com.proexpense.designsystem.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

/* Corner radii — see tokens.md §3. 1 css px = 1 dp. */
object ProRadius {
    val buttonSm = 10.dp
    val buttonMd = 12.dp
    val buttonLg = 14.dp
    val key = 12.dp
    val tile = 14.dp
    val field = 14.dp
    val card = 18.dp
    val sheetTop = 22.dp
    val navTop = 8.dp
    val phone = 54.dp
}

val ProShapes = Shapes(
    extraSmall = RoundedCornerShape(ProRadius.buttonSm),
    small = RoundedCornerShape(ProRadius.buttonMd),
    medium = RoundedCornerShape(ProRadius.card),
    large = RoundedCornerShape(ProRadius.sheetTop),
    extraLarge = RoundedCornerShape(ProRadius.sheetTop),
)
