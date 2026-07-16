package com.arduia.expense.ui.design

import androidx.compose.ui.unit.dp
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Bugfix guard (2026-07): [sheetHeightCap] used to be `maxHeight * fraction - imeHeight`, which
 * shrinks the sheet by the keyboard's full height on top of a cap already below the full screen —
 * with a typical ~40% keyboard and the 0.78 max-height fraction, the visible sheet collapsed to
 * roughly half the screen instead of filling the room actually left above the keyboard.
 */
class BottomSheetTest {
    @Test
    fun noKeyboard_capIsFractionOfFullHeight() {
        val cap = sheetHeightCap(maxHeight = 800.dp, imeHeight = 0.dp, fraction = 0.78f)

        assertEquals(624.dp, cap)
    }

    @Test
    fun openKeyboard_capIsFractionOfRemainingHeight_notFractionMinusKeyboard() {
        val cap = sheetHeightCap(maxHeight = 800.dp, imeHeight = 300.dp, fraction = 0.78f)

        // (800 - 300) * 0.78, not (800 * 0.78) - 300 — the old formula would give 324.dp here.
        assertEquals(390.dp, cap)
    }

    @Test
    fun openKeyboard_neverOverflowsAboveTheScreen() {
        val maxHeight = 800.dp
        val imeHeight = 300.dp

        val cap = sheetHeightCap(maxHeight = maxHeight, imeHeight = imeHeight, fraction = 0.94f)

        assertTrue(
            "sheet content ($cap) plus keyboard ($imeHeight) must fit within the screen ($maxHeight)",
            cap + imeHeight <= maxHeight,
        )
    }

    @Test
    fun keyboardTallerThanBudget_capClampsToZero_insteadOfGoingNegative() {
        val cap = sheetHeightCap(maxHeight = 800.dp, imeHeight = 900.dp, fraction = 0.78f)

        assertEquals(0.dp, cap)
    }
}
