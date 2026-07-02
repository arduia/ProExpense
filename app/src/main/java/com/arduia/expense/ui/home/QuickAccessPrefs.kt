package com.arduia.expense.ui.home

import android.content.Context

enum class QuickAccessTileType {
    Reports,
    Debt,
    Split,
    Events,
}

private const val PREFS_NAME = "quick_access_prefs"
private const val KEY_VISIBLE_TILES = "visible_tiles"

object QuickAccessPrefs {
    val defaultVisible: Set<QuickAccessTileType> = QuickAccessTileType.entries.toSet()

    fun load(context: Context): Set<QuickAccessTileType> {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val stored = prefs.getStringSet(KEY_VISIBLE_TILES, null) ?: return defaultVisible
        val tiles = stored.mapNotNull { name ->
            runCatching { QuickAccessTileType.valueOf(name) }.getOrNull()
        }.toSet()
        return tiles.ifEmpty { defaultVisible }
    }

    fun save(context: Context, visible: Set<QuickAccessTileType>) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putStringSet(KEY_VISIBLE_TILES, visible.map { it.name }.toSet()).apply()
    }
}
