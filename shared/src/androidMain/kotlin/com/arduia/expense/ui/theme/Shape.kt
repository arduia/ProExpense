package com.arduia.expense.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.dp

// Radius scale — design_handoff_v2 / design-tokens.json → dimension.*.radius
@Immutable
data class ProExpenseShapes(
    val pill: RoundedCornerShape = RoundedCornerShape(99.dp),
    val quickTile: RoundedCornerShape = RoundedCornerShape(14.dp),
    val card: RoundedCornerShape = RoundedCornerShape(18.dp),
    val sheet: RoundedCornerShape = RoundedCornerShape(topStart = 22.dp, topEnd = 22.dp),
    val buttonSm: RoundedCornerShape = RoundedCornerShape(10.dp),
    val buttonMd: RoundedCornerShape = RoundedCornerShape(12.dp),
    val buttonLg: RoundedCornerShape = RoundedCornerShape(14.dp),
    val buttonFilled: RoundedCornerShape = RoundedCornerShape(14.dp),
    val buttonText: RoundedCornerShape = RoundedCornerShape(20.dp),
    val field: RoundedCornerShape = RoundedCornerShape(14.dp),
    val searchField: RoundedCornerShape = RoundedCornerShape(14.dp),
    val listRow: RoundedCornerShape = RoundedCornerShape(14.dp),
    val keypadKey: RoundedCornerShape = RoundedCornerShape(12.dp),
    val bottomNav: RoundedCornerShape = RoundedCornerShape(28.dp),
)

val LocalProExpenseShapes = staticCompositionLocalOf { ProExpenseShapes() }

val ProExpenseMaterialShapes = Shapes(
    small = ProExpenseShapes().searchField,
    medium = ProExpenseShapes().card,
    large = ProExpenseShapes().card,
    extraLarge = ProExpenseShapes().sheet,
)
