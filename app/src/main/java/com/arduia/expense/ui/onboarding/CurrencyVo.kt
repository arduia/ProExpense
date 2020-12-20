package com.arduia.expense.ui.onboarding

import android.view.View
import androidx.annotation.IntDef
import androidx.annotation.IntRange

data class CurrencyVo(
    val name: String,
    val symbol: String,
    val number: String,
    @IntVisibility
    val isSelectionVisible: Int
)

@IntDef(value = [View.VISIBLE,View.INVISIBLE])
annotation class IntVisibility