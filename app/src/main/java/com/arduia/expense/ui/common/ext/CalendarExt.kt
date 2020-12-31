package com.arduia.expense.ui.common.ext

import java.util.*

fun Calendar.setDayAsStart(): Calendar {
    this[Calendar.HOUR_OF_DAY] = 0
    this[Calendar.MINUTE] = 0
    this[Calendar.MILLISECOND] = 0

    return this
}

fun Calendar.setDayAsEnd(): Calendar {
    this[Calendar.HOUR_OF_DAY] = 23
    this[Calendar.MINUTE] = 59
    this[Calendar.MILLISECOND] = 5900
    return this
}

