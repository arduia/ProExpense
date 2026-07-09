package com.arduia.expense.ui.home

import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [33])
class QuickAccessPrefsTest {
    private val context = ApplicationProvider.getApplicationContext<android.content.Context>()

    @Test
    fun save_then_load_roundTripsOrder() {
        val order = listOf(QuickAccessTileType.Split, QuickAccessTileType.Reports, QuickAccessTileType.Events)

        QuickAccessPrefs.save(context, order)

        assertEquals(order, QuickAccessPrefs.load(context))
    }

    @Test
    fun load_withNothingStored_returnsDefaultOrder() {
        assertEquals(QuickAccessTileType.entries, QuickAccessPrefs.load(context))
    }

    @Test
    fun load_withLegacyUnorderedStringSet_migratesToEnumDeclarationOrder() {
        val prefs = context.getSharedPreferences("quick_access_prefs", android.content.Context.MODE_PRIVATE)
        prefs
            .edit()
            .putStringSet(
                "visible_tiles",
                setOf(QuickAccessTileType.Events.name, QuickAccessTileType.Reports.name),
            ).apply()

        val loaded = QuickAccessPrefs.load(context)

        assertEquals(listOf(QuickAccessTileType.Reports, QuickAccessTileType.Events), loaded)
    }
}
