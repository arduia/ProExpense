package com.arduia.expense.ui.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import org.junit.Assert.assertEquals
import org.junit.Test

class ProDesignTokensTest {

    @Test
    fun primary_semantic_color_matches_handoff() {
        assertEquals(Color(0xFF039BE5), ProLightColors.primary)
    }

    @Test
    fun on_primary_warm_matches_handoff_screen_verified() {
        assertEquals(Color(0xFFFFFDF6), ProLightColors.onPrimaryWarm)
    }

    @Test
    fun nav_inactive_matches_handoff_screen_verified() {
        assertEquals(Color(0xFF8E8E93), ProLightColors.navInactive)
    }

    @Test
    fun highlight_deep_matches_events_tile_from_home_screen() {
        assertEquals(Color(0xFFF9A825), ProLightColors.highlightDeep)
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
    fun motion_keypad_disabled_opacity_matches_handoff() {
        assertEquals(0.55f, ProDefaultMotion.keypadDisabledOpacity, 0.001f)
    }

    @Test
    fun display_amount_uses_geist_mono() {
        assertEquals(ProDefaultTypography.monoFamily, ProDefaultTypography.displayAmount.fontFamily)
    }

    @Test
    fun list_amount_uses_geist_mono() {
        assertEquals(ProDefaultTypography.monoFamily, ProDefaultTypography.listAmount.fontFamily)
    }

    @Test
    fun screen_title_uses_manrope() {
        assertEquals(ProDefaultTypography.sansFamily, ProDefaultTypography.screenTitle.fontFamily)
    }

    @Test
    fun section_head_uses_manrope() {
        assertEquals(ProDefaultTypography.sansFamily, ProDefaultTypography.sectionHead.fontFamily)
    }

    @Test
    fun display_flourish_uses_instrument_serif() {
        assertEquals(ProDefaultTypography.serifFamily, ProDefaultTypography.displayFlourish.fontFamily)
    }

    @Test
    fun button_typography_matches_handoff() {
        val button = ProDefaultTypography.button
        assertEquals(ProDefaultTypography.sansFamily, button.fontFamily)
        assertEquals(FontWeight.SemiBold, button.fontWeight)
        assertEquals(14f, button.fontSize.value)
        assertEquals((-0.005).em, button.letterSpacing)
        assertEquals(19.6f, button.lineHeight.value)
    }

    @Test
    fun button_md_size_font_matches_handoff() {
        assertEquals(14f, ProDefaultDimens.buttonMd.fontSizeSp)
        assertEquals(12f, ProDefaultDimens.buttonSm.fontSizeSp)
        assertEquals(15f, ProDefaultDimens.buttonLg.fontSizeSp)
    }

    @Test
    fun dimensions_artboard_matches_pixel_9_pro_viewport() {
        assertEquals(ProArtboard.PIXEL_9_PRO_WIDTH_DP.toFloat(), ProDefaultDimens.artboardWidth.value)
        assertEquals(ProArtboard.PIXEL_9_PRO_HEIGHT_DP.toFloat(), ProDefaultDimens.artboardHeight.value)
    }

    @Test
    fun nav_surface_alpha_matches_handoff() {
        assertEquals(0.86f, ProDefaultDimens.navSurfaceAlpha, 0.001f)
    }

    @Test
    fun nav_bar_height_matches_handoff() {
        assertEquals(72f, ProDefaultDimens.navBarHeight.value)
    }

    @Test
    fun nav_shell_bottom_inset_accounts_for_fab_raise() {
        assertEquals(
            ProDefaultDimens.navBarHeight + ProDefaultDimens.navFabOffset,
            ProDefaultDimens.navShellBottomInset,
        )
    }
}
