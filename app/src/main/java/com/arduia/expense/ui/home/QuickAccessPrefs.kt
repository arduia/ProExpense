package com.arduia.expense.ui.home

import android.content.Context

enum class QuickAccessTileType {
    Reports,
    Debt,
    Split,
    Events,
}

private const val PREFS_NAME = "quick_access_prefs"
private const val KEY_TILE_ORDER = "tile_order"
private const val KEY_VISIBLE_TILES = "visible_tiles" // legacy, unordered — migrated on first load
private const val ORDER_SEPARATOR = ","

object QuickAccessPrefs {
    val defaultVisible: List<QuickAccessTileType> = QuickAccessTileType.entries

    /** Order carries both visibility (present = visible) and display order — nothing else needed. */
    fun load(context: Context): List<QuickAccessTileType> {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.getString(KEY_TILE_ORDER, null)?.let { stored ->
            val tiles = stored.split(ORDER_SEPARATOR).mapNotNull { name ->
                runCatching { QuickAccessTileType.valueOf(name) }.getOrNull()
            }.distinct()
            return tiles.ifEmpty { defaultVisible }
        }
        // No ordered prefs yet — fall back to the legacy unordered set, sorted into the
        // enum's declared order so migration doesn't produce a random-looking first order.
        val legacy = prefs.getStringSet(KEY_VISIBLE_TILES, null) ?: return defaultVisible
        val tiles = legacy.mapNotNull { name ->
            runCatching { QuickAccessTileType.valueOf(name) }.getOrNull()
        }.toSet()
        return QuickAccessTileType.entries.filter { it in tiles }.ifEmpty { defaultVisible }
    }

    fun save(context: Context, visible: List<QuickAccessTileType>) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit()
            .putString(KEY_TILE_ORDER, visible.joinToString(ORDER_SEPARATOR) { it.name })
            .remove(KEY_VISIBLE_TILES)
            .apply()
    }
}
