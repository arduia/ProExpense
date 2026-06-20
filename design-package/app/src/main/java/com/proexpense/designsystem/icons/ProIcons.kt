package com.proexpense.designsystem.icons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.PathParser
import androidx.compose.ui.unit.dp

/* ─────────────────────────────────────────────────────────────────────────
 * ProIcons — one stroke-based set on a 24×24 grid, 1.6dp stroke, round caps.
 * Ported 1:1 from the Hi-Fi build (proto-ui.jsx). Tint with Icon(tint = …);
 * the tint ColorFilter recolors the stroke. Bump `sw` for the active weight.
 *
 * Zero-length "h0" subpaths render as round dots (markers).
 * ───────────────────────────────────────────────────────────────────────── */

private fun ic(name: String, vararg paths: String, sw: Float = 1.6f): ImageVector =
    ImageVector.Builder(name, 24.dp, 24.dp, 24f, 24f).apply {
        paths.forEach { d ->
            addPath(
                pathData = PathParser().parsePathString(d).toNodes(),
                fill = null,
                stroke = SolidColor(Color.Black),
                strokeLineWidth = sw,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round,
            )
        }
    }.build()

object ProIcons {
    // Navigation & chrome
    val Home = ic("Home", "M4 11.5 12 5l8 6.5V19a1 1 0 0 1-1 1h-4v-6h-6v6H5a1 1 0 0 1-1-1Z")
    val Budget = ic(
        "Budget",
        "M4 7h16v12a1 1 0 0 1-1 1H5a1 1 0 0 1-1-1Z",
        "M4 7V5a1 1 0 0 1 1-1h11l4 3",
        "M16 11.5a1.5 1.5 0 1 0 0 3 1.5 1.5 0 1 0 0-3Z",
    )
    val Journal = ic(
        "Journal",
        "M5 4h13a1 1 0 0 1 1 1v15l-3-2-3 2-3-2-3 2-3-2V5a1 1 0 0 1 1-1Z",
        "M9 8h6M9 12h6",
    )
    val More = ic("More", "M6 12h0M12 12h0M18 12h0", sw = 2.6f)
    val Plus = ic("Plus", "M12 5v14M5 12h14")
    val Minus = ic("Minus", "M5 12h14")
    val Back = ic("Back", "M15 6l-6 6 6 6")
    val Close = ic("Close", "M6 6l12 12M18 6 6 18")
    val ChevronDown = ic("ChevronDown", "M6 9l6 6 6-6")
    val ChevronRight = ic("ChevronRight", "M9 6l6 6-6 6")
    val Search = ic("Search", "M5 11a6 6 0 1 0 12 0 6 6 0 1 0-12 0Z", "M20 20l-4-4")
    val Bell = ic("Bell", "M6 16V11a6 6 0 1 1 12 0v5l1.5 2h-15Z", "M10 20a2 2 0 0 0 4 0")
    val Check = ic("Check", "M5 12l5 5L20 7")
    val Sparkle = ic(
        "Sparkle",
        "M12 4v4M12 16v4M4 12h4M16 12h4M6 6l3 3M15 15l3 3M6 18l3-3M15 9l3-3",
    )
    val At = ic("At", "M8 12a4 4 0 1 0 8 0 4 4 0 1 0-8 0Z", "M16 8v5a3 3 0 0 0 6 0v-1a10 10 0 1 0-4 8")
    val Calendar = ic(
        "Calendar",
        "M6 5h12a2 2 0 0 1 2 2v11a2 2 0 0 1-2 2H6a2 2 0 0 1-2-2V7a2 2 0 0 1 2-2Z",
        "M4 10h16M9 3v4M15 3v4",
    )
    val Clock = ic("Clock", "M4 12a8 8 0 1 0 16 0 8 8 0 1 0-16 0Z", "M12 8v4l3 2")
    val Note = ic("Note", "M6 4h9l4 4v12a1 1 0 0 1-1 1H6a1 1 0 0 1-1-1V5a1 1 0 0 1 1-1Z", "M15 4v4h4M9 13h6M9 17h4")

    // Category glyphs
    val CatFood = ic("CatFood", "M7 4v8a3 3 0 0 0 3 3v5M7 4v4M10 4v4M17 4c-2 0-4 2.5-4 6s2 4 4 4v6")
    val CatTransport = ic(
        "CatTransport",
        "M5 14V9a3 3 0 0 1 3-3h8a3 3 0 0 1 3 3v5",
        "M4.5 14h15a1.5 1.5 0 0 1 1.5 1.5v3.5H3v-3.5a1.5 1.5 0 0 1 1.5-1.5Z",
        "M8 9h8",
        "M8 19h0M16 19h0",
    )
    val CatShopping = ic("CatShopping", "M5 9h14l-1.2 11a1 1 0 0 1-1 .9H7.2a1 1 0 0 1-1-.9Z", "M9 9V6a3 3 0 0 1 6 0v3")
    val CatBills = ic("CatBills", "M6 3h12v18l-3-2-3 2-3-2-3 2Z", "M9 8h6M9 12h6M9 16h3")
    val CatHealth = ic("CatHealth", "M3 12a9 9 0 1 0 18 0 9 9 0 1 0-18 0Z", "M12 6v12M6 12h12")
    val CatEntertainment = ic("CatEntertainment", "M3 12a9 9 0 1 0 18 0 9 9 0 1 0-18 0Z", "M10 9l5 3-5 3Z")
    val CatCoffee = ic("CatCoffee", "M5 9h12v6a4 4 0 0 1-4 4H9a4 4 0 0 1-4-4Z", "M17 11h2a2 2 0 0 1 0 4h-2M8 5v2M11 4v3M14 5v2")
    val CatPet = ic(
        "CatPet",
        "M6 9h0M10 6h0M14 6h0M18 9h0",
        "M8 16c0-2.5 1.8-5 4-5s4 2.5 4 5c0 2-1.6 4-4 4s-4-2-4-4Z",
        sw = 1.6f,
    )

    // Feature shortcuts
    val Reports = ic("Reports", "M5 19V8M11 19V5M17 19v-7", "M3 19h18")
    val Debt = ic(
        "Debt",
        "M6 8a3 3 0 1 0 6 0 3 3 0 1 0-6 0Z",
        "M14 14a3 3 0 1 0 6 0 3 3 0 1 0-6 0Z",
        "M3 19c0-2.5 2.7-5 6-5M14 21c0-2 .8-4 3-4",
    )
    val Split = ic(
        "Split",
        "M5 8a3 3 0 1 0 6 0 3 3 0 1 0-6 0Z",
        "M14 8a3 3 0 1 0 6 0 3 3 0 1 0-6 0Z",
        "M9 14a3 3 0 1 0 6 0 3 3 0 1 0-6 0Z",
        "M10 10l3.5 4M14.5 10 12 14",
    )
    val Events = ic("Events", "M5 7h14l-2 13a1 1 0 0 1-1 .9H8a1 1 0 0 1-1-.9Z", "M8 7V5a4 4 0 0 1 8 0v2")
}
