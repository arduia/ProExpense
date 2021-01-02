package com.arduia.expense.backup.schema.table

import com.google.gson.annotations.SerializedName

/**
 * Backup Table Info
 */
data class Table(

    @SerializedName("names")
    val tableNames: List<String>,

    @SerializedName("fields")
    val fields: List<Field>

)
