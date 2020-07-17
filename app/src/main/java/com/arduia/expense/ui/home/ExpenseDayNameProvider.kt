package com.arduia.expense.ui.home

import android.content.Context
import androidx.annotation.StringRes
import com.arduia.expense.R
import com.arduia.graph.DayNameProvider
import java.lang.IllegalArgumentException

class ExpenseDayNameProvider(private val context: Context): DayNameProvider {
    override fun getName(day: Int): String  =
        when (day) {
            1 -> getString(R.string.label_day_sun)
            2 -> getString(R.string.label_day_mon)
            3 -> getString(R.string.label_day_tue)
            4 -> getString(R.string.label_day_wed)
            5 -> getString(R.string.label_day_thu)
            6 -> getString(R.string.label_day_fri)
            7 -> getString(R.string.label_day_sat)
            else -> throw IllegalArgumentException("The day $day is not in the range of 1 to 7")
        }
    private fun getString(@StringRes id: Int) = context.getString(id)
}
