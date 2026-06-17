package com.arduia.expense.ui.design

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import com.arduia.expense.ui.theme.ProExpensePalette

@Immutable
data class CategoryStyle(
    val name: String,
    val accent: Color,
    val tint: Color,
    val isDefault: Boolean = true,
)

object DefaultCategories {
    val Food = CategoryStyle("Food", ProExpensePalette.Blue500, Color(0xFFE1F5FE))
    val Transport = CategoryStyle("Transport", ProExpensePalette.Blue700, ProExpensePalette.Blue100)
    val Shopping = CategoryStyle("Shopping", ProExpensePalette.Red400, Color(0xFFFFCDD2))
    val Bills = CategoryStyle("Bills", ProExpensePalette.Gray600, ProExpensePalette.Gray200)
    val Health = CategoryStyle("Health", ProExpensePalette.Green500, Color(0xFFC8E6C9))
    val Entertainment = CategoryStyle("Entertainment", Color(0xFF0277BD), ProExpensePalette.Blue200)
    val CoffeeRuns = CategoryStyle("Coffee runs", ProExpensePalette.Gray500, ProExpensePalette.Gray300, isDefault = false)
    val PetCare = CategoryStyle("Pet care", ProExpensePalette.Green400, Color(0xFFDCEDC8), isDefault = false)

    val all: List<CategoryStyle> = listOf(
        Food,
        Transport,
        Shopping,
        Bills,
        Health,
        Entertainment,
        CoffeeRuns,
        PetCare,
    )
}
