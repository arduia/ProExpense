package com.arduia.expense.ui.theme

import androidx.compose.ui.graphics.Color
import org.junit.Assert.assertEquals
import org.junit.Test

class ProDesignTokensTest {

    @Test
    fun primary_semantic_color_matches_handoff() {
        assertEquals(Color(0xFF039BE5), ProLightColors.primary)
    }

    @Test
    fun category_food_matches_handoff() {
        val food = ProLightColors.categoryFood
        assertEquals(Color(0xFF039BE5), food.accent)
        assertEquals(Color(0xFFE1F5FE), food.tint)
    }

    @Test
    fun category_lookup_returns_food_for_unknown_id() {
        assertEquals(ProLightColors.categoryFood, ProLightColors.category("unknown"))
    }

    @Test
    fun category_lookup_resolves_pet() {
        assertEquals(ProLightColors.categoryPet, ProLightColors.category("pet"))
    }

    @Test
    fun motion_pressed_scale_matches_handoff() {
        assertEquals(0.97f, ProDefaultMotion.pressedScale, 0.001f)
    }

    @Test
    fun dimensions_artboard_matches_roborazzi_viewport() {
        assertEquals(414f, ProDefaultDimens.artboardWidth.value)
        assertEquals(868f, ProDefaultDimens.artboardHeight.value)
    }
}
