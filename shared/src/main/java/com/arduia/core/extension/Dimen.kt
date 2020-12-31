package com.arduia.core.extension

import android.content.Context
import android.view.View

/**
 * Dimension Converters from Pixel to Density Pixel or vice-visa.
 */
fun Context.dp(px: Float) = (px/resources.displayMetrics.density)

fun Context.px(dp: Float) = (dp* resources.displayMetrics.density)

fun Context.pxS(sp: Float) = (sp * resources.displayMetrics.scaledDensity)

fun Context.sp(px: Float) = (px/resources.displayMetrics.scaledDensity)

fun Context.dp(px: Int):Int = dp(px.toFloat()).toInt()

fun Context.px(dp: Int):Int = px(dp.toFloat()).toInt()

fun View.dp(px: Int) = context.dp(px)

fun View.px(dp: Int) = context.px(dp)

fun View.dp(px: Float) = context.dp(px)

fun View.px(dp: Float) = context.px(dp)

fun View.pxS(sp: Float) =  context.pxS(sp)


