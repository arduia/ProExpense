package com.arduia.backup

import com.google.gson.annotations.SerializedName

data class BackupDto(
    @SerializedName("end_date")
    val end_date: Long,

    @SerializedName("fields")
    val fields: List<FieldDto>,

    @SerializedName("file_name")
    val file_name: String,

    @SerializedName("item_total")
    val item_total: Int,

    @SerializedName("start_date")
    val start_date: Long
)
