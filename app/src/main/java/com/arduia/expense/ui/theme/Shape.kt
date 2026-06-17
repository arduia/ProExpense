package com.arduia.expense.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.dp

@Immutable
data class ProExpenseShapes(
    val pill: RoundedCornerShape = RoundedCornerShape(99.dp),
    val quickTile: RoundedCornerShape = RoundedCornerShape(14.dp),
    val card: RoundedCornerShape = RoundedCornerShape(18.dp),
    val sheet: RoundedCornerShape = RoundedCornerShape(topStart = 22.dp, topEnd = 22.dp),
    val buttonSmall: RoundedCornerShape = RoundedCornerShape(10.dp),
    val buttonMedium: RoundedCornerShape = RoundedCornerShape(12.dp),
    val buttonLarge: RoundedCornerShape = RoundedCornerShape(14.dp),
    val searchField: RoundedCornerShape = RoundedCornerShape(14.dp),
    val badge: RoundedCornerShape = RoundedCornerShape(50),
)

val LocalProExpenseShapes = staticCompositionLocalOf { ProExpenseShapes() }

val ProExpenseMaterialShapes = Shapes(
    small = ProExpenseShapes().buttonSmall,
    medium = ProExpenseShapes().buttonMedium,
    large = ProExpenseShapes().card,
    extraLarge = ProExpenseShapes().sheet,
)
