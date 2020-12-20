package com.arduia.expense.ui.common

import android.content.Context
import android.graphics.Color
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import androidx.core.content.res.use
import androidx.fragment.app.Fragment

fun Fragment.getColorList(@ColorRes color: Int) = ContextCompat.getColorStateList(requireContext(), color)

@ColorInt
fun Context.themeColor(
    @AttrRes themeAttrs: Int
): Int{
    return obtainStyledAttributes(
        intArrayOf(themeAttrs)
    ).use {
        it.getColor(0, Color.MAGENTA)
    }
}