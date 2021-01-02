package com.arduia.graph

import java.lang.IllegalArgumentException


internal data class SpendPoint(val day: Int, val rate: Float) {
    init {
        validateDayOfWeek(day)
        validateSpendPointRate(rate)
    }
}

internal fun validateDayOfWeek(day: Int) {
    if (day !in 1..7) throw IllegalArgumentException("Day($day) must be between from 1 to 7")
}

internal fun validateSpendPointRate(rate: Float) {
    if (rate !in -1f..1f) throw  IllegalArgumentException("Rate($rate) must be between from -1f to 1f")
}