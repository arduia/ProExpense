package com.arduia.expense.screenshot

import android.app.Application
import android.view.ContextThemeWrapper
import android.view.LayoutInflater
import android.view.View
import androidx.test.core.app.ApplicationProvider
import com.arduia.expense.R
import io.github.takahirom.roborazzi.captureRoboImage

internal fun inflateLayout(layoutId: Int): View {
    val base = ApplicationProvider.getApplicationContext<Application>()
    val context = ContextThemeWrapper(base, R.style.Theme_ProExpense)
    return LayoutInflater.from(context).inflate(layoutId, null, false)
}

internal fun View.measureAndCapture(filePath: String, widthDp: Int = 360, heightDp: Int = 720) {
    val density = resources.displayMetrics.density
    val widthPx = (widthDp * density).toInt()
    val heightPx = (heightDp * density).toInt()
    measure(
        View.MeasureSpec.makeMeasureSpec(widthPx, View.MeasureSpec.EXACTLY),
        View.MeasureSpec.makeMeasureSpec(heightPx, View.MeasureSpec.EXACTLY),
    )
    layout(0, 0, measuredWidth, measuredHeight)
    captureRoboImage(filePath)
}
