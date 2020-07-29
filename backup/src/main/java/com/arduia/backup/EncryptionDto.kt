package com.arduia.backup

import com.google.gson.annotations.SerializedName

data class EncryptionDto(
    @SerializedName("is_encrypted")
    val is_encrypted: Boolean
)
