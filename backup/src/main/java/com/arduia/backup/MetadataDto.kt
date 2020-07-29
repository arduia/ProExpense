package com.arduia.backup

import com.google.gson.annotations.SerializedName

data class MetadataDto(

    @SerializedName("backup")
    val backup: List<BackupDto>,

    @SerializedName("created_date")
    val created_date: Long,

    @SerializedName("encryption")
    val encryption: EncryptionDto,

    @SerializedName("name")
    val name: String,

    @SerializedName("version_code")
    val version_code: Int

)
