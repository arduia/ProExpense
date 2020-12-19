package com.arduia.expense.data.local

import com.google.gson.annotations.SerializedName

data class AboutUpdateDataModel(
    @SerializedName("name")
    val name: String,

    @SerializedName("code")
    val code: Long,

    @SerializedName("change_logs")
    val changeLogs: String
)