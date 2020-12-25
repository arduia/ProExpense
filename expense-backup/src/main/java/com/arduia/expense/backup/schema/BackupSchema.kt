package com.arduia.expense.backup.schema

import com.arduia.expense.backup.Metadata
import com.arduia.expense.backup.schema.table.Table
import com.google.gson.annotations.SerializedName
import java.util.*

data class BackupSchema(

    @SerializedName("version")
    val version: Int = Metadata.VERSION_CODE,

    @SerializedName("app_version_code")
    val appVersionCode: Int,

    @SerializedName("export_date")
    val exportDate: Long = Date().time,

    @SerializedName("currency_code")
    val currencyCode: Int,

    @SerializedName("export_tables")
    val exportTables: List<Table>

)
