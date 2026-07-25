package com.arduia.expense.ui.theme

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.unit.em
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

@OptIn(ExperimentalTextApi::class, ExperimentalComposeUiApi::class)
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
    fun nav_inactive_matches_blue_banking_muted() {
        assertEquals(ProLightColors.muted, ProLightColors.navInactive)
    }

    @Test
    fun dark_palette_uses_navy_tinted_surfaces() {
        assertEquals(Color(0xFF0D1622), ProDarkColors.paper)
        assertEquals(Color(0xFF18232F), ProDarkColors.surface)
        assertEquals(Color(0xFF2BA9E8), ProDarkColors.primary)
        assertEquals(Color(0xFFF2F6FB), ProDarkColors.onSurface)
    }

    @Test
    fun highlight_family_matches_blue_banking_gold() {
        assertEquals(Color(0xFFF2B33D), ProLightColors.highlight)
        assertEquals(Color(0xFFB97D13), ProLightColors.highlightDeep)
    }

    @Test
    fun category_food_matches_handoff() {
        val food = ProLightColors.categoryFood
        assertEquals(Color(0xFF039BE5), food.accent)
        assertEquals(Color(0xFFE1F5FE), food.tint)
    }

    @Test
    fun category_lookup_returns_neutral_other_for_unknown_id() {
        assertEquals(ProLightColors.categoryOther, ProLightColors.category("unknown"))
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
    fun display_amount_uses_prompt_display_family() {
        assertEquals(ProDefaultTypography.amountFamily, ProDefaultTypography.displayAmount.fontFamily)
        assertEquals(58f, ProDefaultTypography.displayAmount.fontSize.value)
    }

    @Test
    fun list_amount_uses_prompt_at_15sp() {
        assertEquals(ProDefaultTypography.amountFamily, ProDefaultTypography.listAmount.fontFamily)
        assertEquals(15f, ProDefaultTypography.listAmount.fontSize.value)
        assertEquals(0.em, ProDefaultTypography.listAmount.letterSpacing)
    }

    @Test
    fun app_bar_title_uses_prompt_semibold_at_16sp() {
        val title = ProDefaultTypography.appBarTitle
        assertEquals(ProDefaultTypography.amountFamily, title.fontFamily)
        assertEquals(16f, title.fontSize.value)
        assertEquals(FontWeight.SemiBold, title.fontWeight)
        assertEquals(0.em, title.letterSpacing)
    }

    @Test
    fun hero_greeting_uses_prompt_semibold_with_matching_emphasis() {
        assertEquals(ProDefaultTypography.amountFamily, ProDefaultTypography.heroGreeting.fontFamily)
        assertEquals(24f, ProDefaultTypography.heroGreeting.fontSize.value)
        assertEquals(FontWeight.SemiBold, ProDefaultTypography.heroGreeting.fontWeight)
        assertEquals((-0.01).em, ProDefaultTypography.heroGreeting.letterSpacing)
        assertEquals(ProDefaultTypography.amountFamily, ProDefaultTypography.heroGreetingEmphasis.fontFamily)
    }

    @Test
    fun sheet_title_uses_prompt_at_22sp() {
        val title = ProDefaultTypography.sheetTitle
        assertEquals(ProDefaultTypography.amountFamily, title.fontFamily)
        assertEquals(22f, title.fontSize.value)
        assertEquals((-0.01).em, title.letterSpacing)
    }

    @Test
    fun section_head_uses_prompt_at_15_5sp() {
        val head = ProDefaultTypography.sectionHead
        assertEquals(ProDefaultTypography.amountFamily, head.fontFamily)
        assertEquals(15.5f, head.fontSize.value)
        assertEquals(18f, head.lineHeight.value)
        assertEquals(0.em, head.letterSpacing)
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
    fun summary_amount_uses_prompt() {
        assertEquals(ProDefaultTypography.amountFamily, ProDefaultTypography.summaryAmount.fontFamily)
        assertEquals(40f, ProDefaultTypography.summaryAmount.fontSize.value)
    }

    @Test
    fun nav_fab_protrusion_matches_spec() {
        assertEquals(4f, ProDefaultDimens.navFabProtrusion.value)
    }

    @Test
    fun nav_fab_offset_is_fab_size_minus_protrusion() {
        assertEquals(
            ProDefaultDimens.fabSize - ProDefaultDimens.navFabProtrusion,
            ProDefaultDimens.navFabOffset,
        )
    }

    @Test
    fun nav_bar_height_matches_handoff() {
        assertEquals(72f, ProDefaultDimens.navBarHeight.value)
    }

    @Test
    fun nav_shell_bottom_inset_accounts_for_fab_raise() {
        assertEquals(
            ProDefaultDimens.navBarHeight + ProDefaultDimens.fabSize + ProDefaultDimens.navFabProtrusion,
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
        assertEquals(0.12.em, ProDefaultTypography.eyebrow.letterSpacing)
    }

    @Test
    fun nav_label_uses_prompt_at_10_5sp() {
        assertEquals(ProDefaultTypography.amountFamily, ProDefaultTypography.navLabel.fontFamily)
        assertEquals(10.5f, ProDefaultTypography.navLabel.fontSize.value)
        assertEquals(0.01.em, ProDefaultTypography.navLabel.letterSpacing)
        assertEquals(FontWeight.Medium, ProDefaultTypography.navLabel.fontWeight)
    }

    @Test
    fun search_field_uses_manrope() {
        assertEquals(ProDefaultTypography.sansFamily, ProDefaultTypography.searchField.fontFamily)
        assertEquals(13f, ProDefaultTypography.searchField.fontSize.value)
    }

    @Test
    fun keypad_key_uses_prompt() {
        assertEquals(ProDefaultTypography.amountFamily, ProDefaultTypography.keypadKey.fontFamily)
        assertEquals(21f, ProDefaultTypography.keypadKey.fontSize.value)
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
        assertEquals(FontWeight.SemiBold, ProDefaultTypography.eyebrow.fontWeight)
        assertEquals(FontWeight.Normal, ProDefaultTypography.tabTimestamp.fontWeight)
        assertEquals(FontWeight.Medium, ProDefaultTypography.monoFigure.fontWeight)
        assertNotNull(ProDefaultTypography.monoFamily)
    }

    @Test
    fun hero_greeting_emphasis_matches_hero_greeting_face() {
        // Blue Banking renders "Hi, {name}" as a single Prompt SemiBold run — no italic face is
        // bundled, so emphasis reuses the same style as the prefix (color is applied separately).
        val emphasis = ProDefaultTypography.heroGreetingEmphasis
        assertEquals(ProDefaultTypography.amountFamily, emphasis.fontFamily)
        assertEquals(FontWeight.SemiBold, emphasis.fontWeight)
        assertEquals(FontStyle.Normal, emphasis.fontStyle)
    }

    @Test
    fun amount_styles_request_prompt_semibold() {
        val amountStyles =
            listOf(
                ProDefaultTypography.displayAmount,
                ProDefaultTypography.summaryAmount,
                ProDefaultTypography.detailsAmount,
                ProDefaultTypography.listAmount,
                ProDefaultTypography.keypadKey,
            )
        amountStyles.forEach { style ->
            assertEquals(FontWeight.SemiBold, style.fontWeight)
        }
    }

    @Test
    fun prompt_title_styles_request_semibold() {
        val promptTitleStyles =
            listOf(
                ProDefaultTypography.heroGreetingEmphasis,
                ProDefaultTypography.sheetTitle,
                ProDefaultTypography.sectionHead,
                ProDefaultTypography.screenHeaderTitle,
                ProDefaultTypography.onboardingSlideTitle,
                ProDefaultTypography.profileScreenTitle,
            )
        promptTitleStyles.forEach { style ->
            assertEquals(FontWeight.SemiBold, style.fontWeight)
        }
    }

    @Test
    fun title_and_amount_styles_use_figma_line_metrics() {
        val alignedStyles =
            listOf(
                ProDefaultTypography.displayAmount,
                ProDefaultTypography.summaryAmount,
                ProDefaultTypography.listAmount,
                ProDefaultTypography.appBarTitle,
                ProDefaultTypography.heroGreeting,
                ProDefaultTypography.heroGreetingEmphasis,
                ProDefaultTypography.sheetTitle,
                ProDefaultTypography.sectionHead,
                ProDefaultTypography.keypadKey,
            )
        alignedStyles.forEach { style ->
            assertEquals(PlatformTextStyle(includeFontPadding = false), style.platformStyle)
            assertEquals(LineHeightStyle.Alignment.Center, style.lineHeightStyle?.alignment)
            assertEquals(LineHeightStyle.Trim.Both, style.lineHeightStyle?.trim)
        }
    }

    @Test
    fun spec_capture_artboard_is_wider_than_phone() {
        assertEquals(1100, ProArtboard.SPEC_CAPTURE_WIDTH_DP)
        assertTrue(ProArtboard.SPEC_CAPTURE_WIDTH_DP > ProArtboard.PIXEL_9_PRO_WIDTH_DP)
    }
}
