package com.arduia.backup

import com.google.gson.annotations.SerializedName

data class FieldDto(
    @SerializedName("name")
    val name: String,

    @SerializedName("type")
    val type: String
)
