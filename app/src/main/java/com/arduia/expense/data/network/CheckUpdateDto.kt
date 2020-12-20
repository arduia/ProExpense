package com.arduia.expense.data.network

import com.arduia.expense.data.local.AboutUpdateDataModel
import com.google.gson.annotations.SerializedName

class CheckUpdateDto {

    data class Request(
        @SerializedName("device_version_code")
        val currentVersion: Long,

        @SerializedName("key")
        val key: String
    )

    data class Response(
        @SerializedName("is_should_update")
        val isShouldUpdate: Boolean,

        @SerializedName("is_critical")
        val isCriticalUpdate: Boolean =  false,

        @SerializedName("info")
        val info: AboutUpdateDataModel? = null
    )
}