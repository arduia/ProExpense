package com.arduia.expense.data.network

import com.google.gson.annotations.SerializedName

data class ExpenseVersionDto(
    @SerializedName("version_code")
    val version_code: Int,

    @SerializedName("version_name")
    val version_name: String
)