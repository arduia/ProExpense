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
    val navy: Color,
    val primary: Color,
    val primaryDeep: Color,
    val primarySoft: Color,
    val primaryTint: Color,
    val primaryTintSoft: Color,
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
    val highlightTint: Color,
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
    /** Base hue for card/floating-surface drop shadows — blue-tinted (navy) in light, neutral black in dark. Alpha applied at the call site (see [proElevation]). */
    val cardShadowTint: Color,
    /** 165° hero-header gradient stops, light→dark end (Blue Banking canvas). */
    val heroGradientStops: List<Color>,
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
    fun category(id: String): CategoryColorPair =
        when (id) {
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

private val lightHeroGradientStops =
    listOf(
        Color(0xFF01579B),
        Color(0xFF015D9F),
        Color(0xFF0472B3),
        Color(0xFF0288D1),
        Color(0xFF0293DC),
        Color(0xFF039BE5),
    )

val ProLightColors =
    ProColors(
        navy = Color(0xFF01579B),
        primary = Color(0xFF039BE5),
        primaryDeep = Color(0xFF0288D1),
        primarySoft = Color(0xFF4FC3F7),
        primaryTint = Color(0xFFE1F5FE),
        primaryTintSoft = Color(0xFFF0FAFF),
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
        successTint = Color(0xFFE6F4E7),
        highlight = Color(0xFFF2B33D),
        highlightSoft = Color(0xFFF7CE7A),
        highlightDeep = Color(0xFFB97D13),
        highlightTint = Color(0xFFFDF3DF),
        danger = Color(0xFFEF5350),
        dangerSoft = Color(0xFFE57373),
        dangerTint = Color(0xFFFDEAEA),
        warning = Color(0xFFC77F00),
        warningTint = Color(0xFFF5E6A3),
        tag = Color(0xFFFB8C00),
        tagDeep = Color(0xFFEF6C00),
        tagSoft = Color(0xFFFFB74D),
        tagTint = Color(0xFFFFF1E0),
        muted = Color(0xFF9E9E9E),
        muted2 = Color(0xFFBDBDBD),
        line = Color(0x19212121),
        lineSoft = Color(0x0F212121),
        lineStrong = Color(0xFFE0E0E0),
        scrim = Color(0x6B2B1F17),
        navInactive = Color(0xFF9E9E9E),
        cardShadowTint = Color(0xFF01579B),
        heroGradientStops = lightHeroGradientStops,
        categoryFood = CategoryColorPair(Color(0xFF039BE5), Color(0xFFE1F5FE)),
        categoryTransport = CategoryColorPair(Color(0xFF0288D1), Color(0xFFE1F5FE)),
        categoryShopping = CategoryColorPair(Color(0xFFEF5350), Color(0xFFFDEAEA)),
        categoryBills = CategoryColorPair(Color(0xFF757575), Color(0xFFEEEEEE)),
        categoryHealth = CategoryColorPair(Color(0xFF4CAF50), Color(0xFFE6F4E7)),
        categoryEntertainment = CategoryColorPair(Color(0xFF01579B), Color(0xFFE1F5FE)),
        categoryCoffee = CategoryColorPair(Color(0xFFB97D13), Color(0xFFFDF3DF)),
        categoryPet = CategoryColorPair(Color(0xFF66BB6A), Color(0xFFDCEDC8)),
        categoryOther = CategoryColorPair(Color(0xFF9E9E9E), Color(0xFFEEEEEE)),
    )

private val darkHeroGradientStops =
    listOf(
        Color(0xFF062339),
        Color(0xFF072D49),
        Color(0xFF083550),
        Color(0xFF0A3A5C),
        Color(0xFF0B4568),
        Color(0xFF0D4F79),
    )

val ProDarkColors =
    ProColors(
        navy = Color(0xFF081726),
        primary = Color(0xFF2BA9E8),
        primaryDeep = Color(0xFF0D4368),
        primarySoft = Color(0xFF4FC3F7),
        primaryTint = Color(0x262BA9E8),
        primaryTintSoft = Color(0x142BA9E8),
        onPrimary = Color(0xFFFFFFFF),
        onPrimaryWarm = Color(0xFFFFFFFF),
        surface = Color(0xFF18232F),
        onSurface = Color(0xFFF2F6FB),
        onSurfaceVariant = Color(0xFFC2CEDB),
        onSurfaceMuted = Color(0xFF8A97A7),
        paper = Color(0xFF0D1622),
        paperAlt = Color(0xFF18232F),
        keypadKeyPressed = Color(0xFF223140),
        success = Color(0xFF5CC86A),
        successSoft = Color(0xFF5CC86A),
        successTint = Color(0x295CC86A),
        highlight = Color(0xFFF2B33D),
        highlightSoft = Color(0xFFF2B33D),
        highlightDeep = Color(0xFFF0B84A),
        highlightTint = Color(0x29F2B33D),
        danger = Color(0xFFFF6F6B),
        dangerSoft = Color(0xFFFF6F6B),
        dangerTint = Color(0x2EFF6F6B),
        warning = Color(0xFFE0A030),
        warningTint = Color(0xFF3D341B),
        tag = Color(0xFFFF9A3D),
        tagDeep = Color(0xFFFFB15E),
        tagSoft = Color(0xFFFF9A3D),
        tagTint = Color(0x29FF9A3D),
        muted = Color(0xFF69768A),
        muted2 = Color(0xFF8A97A7),
        line = Color(0x19FFFFFF),
        lineSoft = Color(0x0EFFFFFF),
        lineStrong = Color(0xFF2E3A47),
        scrim = Color(0xCC000000),
        navInactive = Color(0xFF69768A),
        cardShadowTint = Color(0xFF000000),
        heroGradientStops = darkHeroGradientStops,
        categoryFood = CategoryColorPair(Color(0xFF2BA9E8), Color(0x262BA9E8)),
        categoryTransport = CategoryColorPair(Color(0xFF4FC3F7), Color(0x262BA9E8)),
        categoryShopping = CategoryColorPair(Color(0xFFFF6F6B), Color(0x2EFF6F6B)),
        categoryBills = CategoryColorPair(Color(0xFF8A97A7), Color(0xFF223140)),
        categoryHealth = CategoryColorPair(Color(0xFF5CC86A), Color(0x295CC86A)),
        categoryEntertainment = CategoryColorPair(Color(0xFF4FC3F7), Color(0x262BA9E8)),
        categoryCoffee = CategoryColorPair(Color(0xFFF0B84A), Color(0x29F2B33D)),
        categoryPet = CategoryColorPair(Color(0xFF5CC86A), Color(0x295CC86A)),
        categoryOther = CategoryColorPair(Color(0xFF8A97A7), Color(0xFF223140)),
    )
