package com.arduia.expense.data.network

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class ExpenseVersionDto(
    @SerializedName("version_code")
    val versionCode: Int,

    @SerializedName("version_name")
    val versionName: String
): Serializable