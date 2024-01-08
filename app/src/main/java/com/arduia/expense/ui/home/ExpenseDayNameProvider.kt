package com.arduia.expense.ui.home

import android.content.Context
import androidx.annotation.StringRes
import com.arduia.expense.R
import com.arduia.graph.DayNameProvider
import dagger.hilt.android.qualifiers.ActivityContext
import dagger.hilt.android.qualifiers.ApplicationContext
import java.lang.IllegalArgumentException
import javax.inject.Inject

class ExpenseDayNameProvider @Inject constructor(@ApplicationContext val context: Context): DayNameProvider {
    override fun getName(day: Int): String  =
        when (day) {
            1 -> getString(R.string.day_sun)
            2 -> getString(R.string.day_mon)
            3 -> getString(R.string.day_tue)
            4 -> getString(R.string.day_wed)
            5 -> getString(R.string.day_thu)
            6 -> getString(R.string.day_fri)
            7 -> getString(R.string.day_sat)
            else -> throw IllegalArgumentException("The day $day is not in the range of 1 to 7")
        }
    private fun getString(@StringRes id: Int) = context.getString(id)
}
