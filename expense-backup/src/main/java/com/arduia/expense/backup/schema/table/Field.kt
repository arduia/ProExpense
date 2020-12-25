package com.arduia.expense.backup.schema.table

import com.google.gson.annotations.SerializedName

data class Field(

    @SerializedName("name")
    val name: String,

    @SerializedName("type")
    val type: String

)
