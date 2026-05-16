package com.arduia.expense.ui.statistics

import androidx.annotation.StringRes

data class CategoryStatisticUiModel(
    @StringRes
    val nameId: Int,
    val progress: Float,
    val progressText: String
)