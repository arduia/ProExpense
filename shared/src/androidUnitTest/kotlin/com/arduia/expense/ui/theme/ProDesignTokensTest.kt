package com.arduia.expense.ui.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
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
    fun display_amount_uses_instrument_serif() {
        assertEquals(ProDefaultTypography.serifFamily, ProDefaultTypography.displayAmount.fontFamily)
    }

    @Test
    fun list_amount_uses_instrument_serif() {
        assertEquals(ProDefaultTypography.serifFamily, ProDefaultTypography.listAmount.fontFamily)
    }

    @Test
    fun app_bar_title_uses_manrope_at_17sp() {
        val title = ProDefaultTypography.appBarTitle
        assertEquals(ProDefaultTypography.sansFamily, title.fontFamily)
        assertEquals(17f, title.fontSize.value)
        assertEquals(0.em, title.letterSpacing)
    }

    @Test
    fun hero_greeting_uses_manrope_with_serif_emphasis() {
        assertEquals(ProDefaultTypography.sansFamily, ProDefaultTypography.heroGreeting.fontFamily)
        assertEquals(30f, ProDefaultTypography.heroGreeting.fontSize.value)
        assertEquals(32f, ProDefaultTypography.heroGreeting.lineHeight.value)
        assertEquals((-0.015).em, ProDefaultTypography.heroGreeting.letterSpacing)
        assertEquals(ProDefaultTypography.serifFamily, ProDefaultTypography.heroGreetingEmphasis.fontFamily)
    }

    @Test
    fun sheet_title_uses_instrument_serif_at_22sp() {
        val title = ProDefaultTypography.sheetTitle
        assertEquals(ProDefaultTypography.serifFamily, title.fontFamily)
        assertEquals(22f, title.fontSize.value)
        assertEquals((-0.01).em, title.letterSpacing)
    }

    @Test
    fun section_head_uses_instrument_serif_at_18sp() {
        val head = ProDefaultTypography.sectionHead
        assertEquals(ProDefaultTypography.serifFamily, head.fontFamily)
        assertEquals(18f, head.fontSize.value)
        assertEquals((-0.01).em, head.letterSpacing)
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
    fun nav_surface_alpha_matches_spec_glass() {
        assertEquals(0.86f, ProDefaultDimens.navSurfaceAlpha, 0.001f)
    }

    @Test
    fun summary_amount_uses_instrument_serif() {
        assertEquals(ProDefaultTypography.serifFamily, ProDefaultTypography.summaryAmount.fontFamily)
        assertEquals(40f, ProDefaultTypography.summaryAmount.fontSize.value)
    }

    @Test
    fun nav_fab_drop_offset_matches_handoff() {
        assertEquals(4f, ProDefaultDimens.navFabDropOffset.value)
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

    @Test
    fun body_and_caption_use_manrope() {
        assertEquals(ProDefaultTypography.sansFamily, ProDefaultTypography.body.fontFamily)
        assertEquals(ProDefaultTypography.sansFamily, ProDefaultTypography.caption.fontFamily)
    }

    @Test
    fun eyebrow_and_timestamp_use_geist_mono() {
        assertEquals(ProDefaultTypography.monoFamily, ProDefaultTypography.eyebrow.fontFamily)
        assertEquals(ProDefaultTypography.monoFamily, ProDefaultTypography.tabTimestamp.fontFamily)
        assertEquals(0.08.em, ProDefaultTypography.tabTimestamp.letterSpacing)
        assertEquals(0.10.em, ProDefaultTypography.eyebrow.letterSpacing)
    }

    @Test
    fun nav_label_uses_geist_mono() {
        assertEquals(ProDefaultTypography.monoFamily, ProDefaultTypography.navLabel.fontFamily)
        assertEquals(10f, ProDefaultTypography.navLabel.fontSize.value)
        assertEquals(0.08.em, ProDefaultTypography.navLabel.letterSpacing)
        assertEquals(FontWeight.Medium, ProDefaultTypography.navLabel.fontWeight)
    }

    @Test
    fun search_field_uses_manrope() {
        assertEquals(ProDefaultTypography.sansFamily, ProDefaultTypography.searchField.fontFamily)
        assertEquals(13f, ProDefaultTypography.searchField.fontSize.value)
    }

    @Test
    fun keypad_key_uses_instrument_serif() {
        assertEquals(ProDefaultTypography.serifFamily, ProDefaultTypography.keypadKey.fontFamily)
    }

    @Test
    fun manrope_is_primary_ui_typeface() {
        assertEquals(ProDefaultTypography.sansFamily, ProDefaultTypography.body.fontFamily)
        assertEquals(ProDefaultTypography.sansFamily, ProDefaultTypography.button.fontFamily)
        assertEquals(ProDefaultTypography.sansFamily, ProDefaultTypography.searchField.fontFamily)
        assertEquals(FontWeight.Medium, ProDefaultTypography.bodyMedium.fontWeight)
        assertEquals(FontWeight.SemiBold, ProDefaultTypography.bodySemiBold.fontWeight)
    }

    @Test
    fun geist_mono_covers_label_weights() {
        assertEquals(FontWeight.Medium, ProDefaultTypography.eyebrow.fontWeight)
        assertEquals(FontWeight.Normal, ProDefaultTypography.tabTimestamp.fontWeight)
        assertNotNull(ProDefaultTypography.monoFamily)
    }

    @Test
    fun spec_capture_artboard_is_wider_than_phone() {
        assertEquals(1100, ProArtboard.SPEC_CAPTURE_WIDTH_DP)
        assertTrue(ProArtboard.SPEC_CAPTURE_WIDTH_DP > ProArtboard.PIXEL_9_PRO_WIDTH_DP)
    }
}
