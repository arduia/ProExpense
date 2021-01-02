package com.arduia.expense.backup.schema.table

import com.google.gson.annotations.SerializedName

/**
 * Field info for every column in each expense backup tables
 */
data class Field(

    @SerializedName("name")
    val name: String,

    @SerializedName("type")
    val type: String

)
