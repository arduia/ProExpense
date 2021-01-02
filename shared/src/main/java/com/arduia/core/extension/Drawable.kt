package com.arduia.core.extension

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat

/**
 * Wrap Custom Tint color on the drawable @id
 */
fun Context.getDrawable(@DrawableRes id: Int,@ColorInt color: Int = Color.BLACK): Drawable{

    val icon = ContextCompat.getDrawable(this, id)

    DrawableCompat.setTint(icon!!, color)

    return icon
}
