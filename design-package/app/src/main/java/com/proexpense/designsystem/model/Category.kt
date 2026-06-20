package com.proexpense.designsystem.model

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.proexpense.designsystem.icons.ProIcons

/* ─────────────────────────────────────────────────────────────────────────
 * Category catalogue — icon + accent + tint per category. See category.md.
 * ───────────────────────────────────────────────────────────────────────── */
enum class Category(
    val label: String,
    val icon: ImageVector,
    val accent: Color,
    val tint: Color,
    val custom: Boolean = false,
) {
    Food("Food", ProIcons.CatFood, Color(0xFF039BE5), Color(0xFFE1F5FE)),
    Transport("Transport", ProIcons.CatTransport, Color(0xFF0288D1), Color(0xFFB3E5FC)),
    Shopping("Shopping", ProIcons.CatShopping, Color(0xFFEF5350), Color(0xFFFFCDD2)),
    Bills("Bills", ProIcons.CatBills, Color(0xFF757575), Color(0xFFEEEEEE)),
    Health("Health", ProIcons.CatHealth, Color(0xFF4CAF50), Color(0xFFC8E6C9)),
    Entertainment("Entertainment", ProIcons.CatEntertainment, Color(0xFF0277BD), Color(0xFF81D4FA)),
    Coffee("Coffee runs", ProIcons.CatCoffee, Color(0xFF9E9E9E), Color(0xFFE0E0E0), custom = true),
    Pet("Pet care", ProIcons.CatPet, Color(0xFF66BB6A), Color(0xFFDCEDC8), custom = true);

    companion object {
        val defaults = listOf(Food, Transport, Shopping, Bills, Health, Entertainment)
        val customs = listOf(Coffee, Pet)
    }
}
