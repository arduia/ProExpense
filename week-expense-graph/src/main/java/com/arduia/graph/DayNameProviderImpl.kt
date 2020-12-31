package com.arduia.graph

import android.content.Context
import androidx.annotation.ColorInt
import java.lang.IllegalArgumentException

internal class DayNameProviderImpl(private val context: Context): DayNameProvider {

    override fun getName( day: Int) =
        when(day){
        1    -> "SU"
        2    -> "MO"
        3    -> "TU"
        4    -> "WE"
        5    -> "TH"
        6    -> "FR"
        7    -> "SA"
        else -> throw IllegalArgumentException("The day $day is not in the range of 1 to 7")
        }

}
