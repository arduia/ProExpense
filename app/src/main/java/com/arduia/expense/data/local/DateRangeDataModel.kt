package com.arduia.expense.data.local

import com.google.gson.annotations.SerializedName

data class DateRangeDataModel(
    @SerializedName("maxDate")
    val maxDate: Long,
    @SerializedName("minDate")
    val minDate: Long
)
