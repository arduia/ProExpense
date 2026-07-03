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
    val keypadKeyPressed: Color,
    val success: Color,
    val successSoft: Color,
    val successTint: Color,
    val highlight: Color,
    val highlightSoft: Color,
    val highlightDeep: Color,
    val danger: Color,
    val dangerSoft: Color,
    val dangerTint: Color,
    val warning: Color,
    val warningTint: Color,
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
    val categoryOther: CategoryColorPair,
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
        else -> categoryOther
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
    keypadKeyPressed = Color(0xFFEEEEEE),
    success = Color(0xFF4CAF50),
    successSoft = Color(0xFF81C784),
    successTint = Color(0xFFC8E6C9),
    highlight = Color(0xFFFFEB3B),
    highlightSoft = Color(0xFFFFF176),
    highlightDeep = Color(0xFFF9A825),
    danger = Color(0xFFEF5350),
    dangerSoft = Color(0xFFE57373),
    dangerTint = Color(0xFFFFCDD2),
    warning = Color(0xFFC77F00),
    warningTint = Color(0xFFF5E6A3),
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
    categoryOther = CategoryColorPair(Color(0xFF9E9E9E), Color(0xFFEEEEEE)),
)

// Classic Darcula cool-charcoal neutrals (IntelliJ IDEA's dark theme) — #2B2B2B page background,
// #3C3F41 elevated panels, #A9B7C6 default text. Accent hues (primary/success/danger/warning/tag/
// category) are unchanged from the brand palette; only the neutral family follows Darcula.
val ProDarkColors = ProColors(
    primary = Color(0xFF4FC3F7),
    primaryDeep = Color(0xFF039BE5),
    primarySoft = Color(0xFF0288D1),
    primaryTint = Color(0xFF01579B),
    onPrimary = Color(0xFF2B2B2B),
    onPrimaryWarm = Color(0xFF2B2B2B),
    surface = Color(0xFF3C3F41),
    onSurface = Color(0xFFA9B7C6),
    onSurfaceVariant = Color(0xFF9DA5B0),
    onSurfaceMuted = Color(0xFF808080),
    paper = Color(0xFF2B2B2B),
    paperAlt = Color(0xFF313335),
    keypadKeyPressed = Color(0xFF454749),
    success = Color(0xFF81C784),
    successSoft = Color(0xFF66BB6A),
    successTint = Color(0xFF1B3D1F),
    highlight = Color(0xFFFFF176),
    highlightSoft = Color(0xFF5D4037),
    highlightDeep = Color(0xFFFFB74D),
    danger = Color(0xFFE57373),
    dangerSoft = Color(0xFFEF5350),
    dangerTint = Color(0xFF3E1F1F),
    warning = Color(0xFFE0A030),
    warningTint = Color(0xFF3D341B),
    tag = Color(0xFFFFB74D),
    tagDeep = Color(0xFFFF9800),
    tagSoft = Color(0xFFEF6C00),
    tagTint = Color(0xFF3E2723),
    muted = Color(0xFF808080),
    muted2 = Color(0xFF616366),
    line = Color(0x1FFFFFFF),
    lineSoft = Color(0x14FFFFFF),
    lineStrong = Color(0xFF515151),
    scrim = Color(0xCC181818),
    navInactive = Color(0xFF8E8E93),
    categoryFood = CategoryColorPair(Color(0xFF4FC3F7), Color(0xFF0D3B52)),
    categoryTransport = CategoryColorPair(Color(0xFF29B6F6), Color(0xFF0D3348)),
    categoryShopping = CategoryColorPair(Color(0xFFE57373), Color(0xFF3E1F1F)),
    categoryBills = CategoryColorPair(Color(0xFF9E9E9E), Color(0xFF3C3F41)),
    categoryHealth = CategoryColorPair(Color(0xFF81C784), Color(0xFF1B3D1F)),
    categoryEntertainment = CategoryColorPair(Color(0xFF4FC3F7), Color(0xFF0D3B52)),
    categoryCoffee = CategoryColorPair(Color(0xFFBDBDBD), Color(0xFF3C3F41)),
    categoryPet = CategoryColorPair(Color(0xFFA5D6A7), Color(0xFF1B3D1F)),
    categoryOther = CategoryColorPair(Color(0xFFBDBDBD), Color(0xFF3C3F41)),
)
