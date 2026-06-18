package com.arduia.expense.ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

@Immutable
data class CategoryColorPair(
    val accent: Color,
    val tint: Color,
)

@Immutable
data class ProColors(
    val primary: Color,
    val primaryDeep: Color,
    val primarySoft: Color,
    val primaryTint: Color,
    val onPrimary: Color,
    val onPrimaryWarm: Color,
    val surface: Color,
    val onSurface: Color,
    val onSurfaceVariant: Color,
    val onSurfaceMuted: Color,
    val paper: Color,
    val paperAlt: Color,
    val success: Color,
    val successSoft: Color,
    val successTint: Color,
    val highlight: Color,
    val highlightSoft: Color,
    val highlightDeep: Color,
    val danger: Color,
    val dangerSoft: Color,
    val dangerTint: Color,
    val tag: Color,
    val tagDeep: Color,
    val tagSoft: Color,
    val tagTint: Color,
    val muted: Color,
    val muted2: Color,
    val line: Color,
    val lineSoft: Color,
    val lineStrong: Color,
    val scrim: Color,
    val navInactive: Color,
    val categoryFood: CategoryColorPair,
    val categoryTransport: CategoryColorPair,
    val categoryShopping: CategoryColorPair,
    val categoryBills: CategoryColorPair,
    val categoryHealth: CategoryColorPair,
    val categoryEntertainment: CategoryColorPair,
    val categoryCoffee: CategoryColorPair,
    val categoryPet: CategoryColorPair,
) {
    fun category(id: String): CategoryColorPair = when (id) {
        "food" -> categoryFood
        "transport" -> categoryTransport
        "shopping" -> categoryShopping
        "bills" -> categoryBills
        "health" -> categoryHealth
        "entertainment" -> categoryEntertainment
        "coffee" -> categoryCoffee
        "pet" -> categoryPet
        else -> categoryFood
    }
}

val LocalProColors = staticCompositionLocalOf { ProLightColors }

val ProLightColors = ProColors(
    primary = Color(0xFF039BE5),
    primaryDeep = Color(0xFF0288D1),
    primarySoft = Color(0xFF4FC3F7),
    primaryTint = Color(0xFFB3E5FC),
    onPrimary = Color(0xFFFFFFFF),
    onPrimaryWarm = Color(0xFFFFFDF6),
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF212121),
    onSurfaceVariant = Color(0xFF424242),
    onSurfaceMuted = Color(0xFF757575),
    paper = Color(0xFFF5F5F5),
    paperAlt = Color(0xFFEEEEEE),
    success = Color(0xFF4CAF50),
    successSoft = Color(0xFF81C784),
    successTint = Color(0xFFC8E6C9),
    highlight = Color(0xFFFFEB3B),
    highlightSoft = Color(0xFFFFF176),
    highlightDeep = Color(0xFFF9A825),
    danger = Color(0xFFEF5350),
    dangerSoft = Color(0xFFE57373),
    dangerTint = Color(0xFFFFCDD2),
    tag = Color(0xFFFB8C00),
    tagDeep = Color(0xFFEF6C00),
    tagSoft = Color(0xFFFFB74D),
    tagTint = Color(0xFFFFE0B2),
    muted = Color(0xFF9E9E9E),
    muted2 = Color(0xFFBDBDBD),
    line = Color(0x19212121),
    lineSoft = Color(0x0F212121),
    lineStrong = Color(0xFFE0E0E0),
    scrim = Color(0x6B2B1F17),
    navInactive = Color(0xFF8E8E93),
    categoryFood = CategoryColorPair(Color(0xFF039BE5), Color(0xFFE1F5FE)),
    categoryTransport = CategoryColorPair(Color(0xFF0288D1), Color(0xFFB3E5FC)),
    categoryShopping = CategoryColorPair(Color(0xFFEF5350), Color(0xFFFFCDD2)),
    categoryBills = CategoryColorPair(Color(0xFF757575), Color(0xFFEEEEEE)),
    categoryHealth = CategoryColorPair(Color(0xFF4CAF50), Color(0xFFC8E6C9)),
    categoryEntertainment = CategoryColorPair(Color(0xFF0277BD), Color(0xFF81D4FA)),
    categoryCoffee = CategoryColorPair(Color(0xFF9E9E9E), Color(0xFFE0E0E0)),
    categoryPet = CategoryColorPair(Color(0xFF66BB6A), Color(0xFFDCEDC8)),
)
