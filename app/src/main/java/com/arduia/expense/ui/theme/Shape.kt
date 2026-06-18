package com.arduia.expense.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.dp

// Radius scale — DESIGN-SYSTEM.md §6.
@Immutable
data class ProExpenseShapes(
    val pill: RoundedCornerShape = RoundedCornerShape(99.dp),
    val quickTile: RoundedCornerShape = RoundedCornerShape(14.dp),
    val card: RoundedCornerShape = RoundedCornerShape(18.dp),
    val sheet: RoundedCornerShape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
    val button: RoundedCornerShape = RoundedCornerShape(28.dp),
    val field: RoundedCornerShape = RoundedCornerShape(6.dp),
    val searchField: RoundedCornerShape = RoundedCornerShape(14.dp),
    val badge: RoundedCornerShape = RoundedCornerShape(50),
)

val LocalProExpenseShapes = staticCompositionLocalOf { ProExpenseShapes() }

val ProExpenseMaterialShapes = Shapes(
    small = ProExpenseShapes().searchField,
    medium = ProExpenseShapes().card,
    large = ProExpenseShapes().card,
    extraLarge = ProExpenseShapes().sheet,
)
