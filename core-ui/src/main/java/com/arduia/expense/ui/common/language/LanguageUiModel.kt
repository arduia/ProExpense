package com.arduia.expense.ui.common.language

import android.view.View
import androidx.annotation.DrawableRes
import com.arduia.expense.ui.onboarding.IntVisibility

data class LanguageUiModel(
    val id: String,
    @DrawableRes val flag: Int,
    val name: String,
    @IntVisibility val isSelectedVisible: Int = View.INVISIBLE
)