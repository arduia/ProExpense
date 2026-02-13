package com.arduia.design.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

val ProExpenseShapes = Shapes(
    extraSmall = RoundedCornerShape(2.dp), // size_radius
    small = RoundedCornerShape(4.dp),    // grid_1
    medium = RoundedCornerShape(8.dp),   // grid_2
    large = RoundedCornerShape(16.dp),   // grid_3
    extraLarge = RoundedCornerShape(32.dp) // grid_4
)
