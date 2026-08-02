package com.arduia.expense.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp

@Immutable
data class ProShapes(
    val chip: Shape,
    val tile: Shape,
    val quickAccessChip: Shape,
    val card: Shape,
    val heroCard: Shape,
    val identityCard: Shape,
    val sheet: Shape,
    val buttonSm: Shape,
    val buttonMd: Shape,
    val buttonLg: Shape,
    val searchField: Shape,
    val keypadKey: Shape,
    val navBar: Shape,
    val toast: Shape,
    val phone: Shape,
    val addHintChip: Shape,
)

val LocalProShapes = staticCompositionLocalOf { ProDefaultShapes }

val ProDefaultShapes =
    ProShapes(
        chip = RoundedCornerShape(999.dp),
        tile = RoundedCornerShape(14.dp),
        quickAccessChip = RoundedCornerShape(11.dp),
        card = RoundedCornerShape(16.dp),
        // Home's floating hero cards (spend summary, active event) — canvas's vbCardBase uses
        // 20dp, one size up from the generic 16dp card used everywhere else.
        heroCard = RoundedCornerShape(20.dp),
        identityCard = RoundedCornerShape(20.dp),
        sheet = RoundedCornerShape(topStart = 26.dp, topEnd = 26.dp),
        buttonSm = RoundedCornerShape(10.dp),
        buttonMd = RoundedCornerShape(14.dp),
        buttonLg = RoundedCornerShape(16.dp),
        searchField = RoundedCornerShape(14.dp),
        keypadKey = RoundedCornerShape(12.dp),
        navBar = RoundedCornerShape(topStart = 18.dp, topEnd = 18.dp),
        toast = RoundedCornerShape(999.dp),
        phone = RoundedCornerShape(54.dp),
        addHintChip = RoundedCornerShape(6.dp),
    )
