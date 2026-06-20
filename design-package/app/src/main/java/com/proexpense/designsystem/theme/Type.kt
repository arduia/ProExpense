package com.proexpense.designsystem.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.googlefonts.Font
import androidx.compose.ui.text.googlefonts.GoogleFont
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import com.proexpense.R

/* ─────────────────────────────────────────────────────────────────────────
 * Typography — three families with one job each.
 *   Manrope          → all UI text (--sans)
 *   Instrument Serif → display: titles & amounts (--serif), Regular + Italic
 *   Geist Mono       → eyebrows, labels, tabular figures (--mono)
 *
 * Loaded via the Google Fonts provider. Requires:
 *   - dependency androidx.compose.ui:ui-text-google-fonts
 *   - res/values/font_certs.xml  (com_google_android_gms_fonts_certs)
 * Or swap to bundled res/font *.ttf and FontFamily(Font(R.font.xxx)).
 * ───────────────────────────────────────────────────────────────────────── */

val GoogleFontsProvider = GoogleFont.Provider(
    providerAuthority = "com.google.android.gms.fonts",
    providerPackage = "com.google.android.gms",
    certificates = R.array.com_google_android_gms_fonts_certs,
)

private fun family(name: String, vararg weights: Pair<FontWeight, FontStyle>) =
    FontFamily(weights.map { (w, s) -> Font(GoogleFont(name), GoogleFontsProvider, w, s) })

val Manrope = family(
    "Manrope",
    FontWeight.Light to FontStyle.Normal,
    FontWeight.Normal to FontStyle.Normal,
    FontWeight.Medium to FontStyle.Normal,
    FontWeight.SemiBold to FontStyle.Normal,
    FontWeight.Bold to FontStyle.Normal,
)

val InstrumentSerif = family(
    "Instrument Serif",
    FontWeight.Normal to FontStyle.Normal,
    FontWeight.Normal to FontStyle.Italic, // ships Regular + Italic only — never request Bold
)

val GeistMono = family(
    "Geist Mono",
    FontWeight.Normal to FontStyle.Normal,
    FontWeight.Medium to FontStyle.Normal,
    FontWeight.SemiBold to FontStyle.Normal,
)

/**
 * Trim Android's default vertical font padding so Instrument Serif matches the
 * web/Figma metrics. Apply to every serif (title/amount) style.
 */
private val SerifPlatform = PlatformTextStyle(includeFontPadding = false)
private val SerifLineHeight = LineHeightStyle(
    alignment = LineHeightStyle.Alignment.Center,
    trim = LineHeightStyle.Trim.Both,
)

private fun serif(size: Int, line: Int, tracking: Double, italic: Boolean = false) = TextStyle(
    fontFamily = InstrumentSerif,
    fontWeight = FontWeight.Normal,
    fontStyle = if (italic) FontStyle.Italic else FontStyle.Normal,
    fontSize = size.sp,
    lineHeight = line.sp,
    letterSpacing = tracking.em,
    platformStyle = SerifPlatform,
    lineHeightStyle = SerifLineHeight,
)

/* ── Named brand styles (titles & amounts) — see tokens.md §2a ── */
object ProType {
    // Amounts (always serif)
    val DisplayAmount = serif(64, 64, -0.025)
    val CardAmount = serif(40, 40, -0.02)
    val DetailsAmount = serif(26, 28, -0.01)
    val RowAmount = serif(18, 18, 0.0)
    val KeypadKey = serif(22, 22, 0.0)

    // Titles
    val HeroGreeting = serif(30, 32, -0.015)
    val HeroEmphasis = serif(30, 32, -0.015, italic = true) // colored Primary at call-site
    val SheetTitle = serif(22, 24, -0.01)
    val SectionHead = serif(18, 20, -0.01)
    val ScreenHeaderTitle = serif(17, 20, 0.0)

    // Mono labels / eyebrows
    val Eyebrow = TextStyle(
        fontFamily = GeistMono,
        fontWeight = FontWeight.SemiBold,
        fontSize = 11.sp,
        lineHeight = 14.sp,
        letterSpacing = 0.12.em,
    )
    val MonoFigure = TextStyle(
        fontFamily = GeistMono,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        letterSpacing = 0.04.em,
    )
}

/* ── Material 3 Typography (UI text = Manrope; display slots = serif) ── */
val ProTypography = Typography(
    displayLarge = ProType.DisplayAmount,
    headlineMedium = ProType.HeroGreeting,
    titleLarge = ProType.SheetTitle,
    titleMedium = ProType.SectionHead,
    bodyLarge = TextStyle(fontFamily = Manrope, fontWeight = FontWeight.Normal, fontSize = 15.sp, lineHeight = 22.sp),
    bodyMedium = TextStyle(fontFamily = Manrope, fontWeight = FontWeight.Normal, fontSize = 14.sp, lineHeight = 20.sp),
    bodySmall = TextStyle(fontFamily = Manrope, fontWeight = FontWeight.Normal, fontSize = 11.5.sp, lineHeight = 16.sp),
    labelLarge = TextStyle(fontFamily = Manrope, fontWeight = FontWeight.SemiBold, fontSize = 14.sp, letterSpacing = (-0.005).em),
    labelMedium = ProType.Eyebrow,
    labelSmall = TextStyle(fontFamily = Manrope, fontWeight = FontWeight.Medium, fontSize = 11.sp),
)
