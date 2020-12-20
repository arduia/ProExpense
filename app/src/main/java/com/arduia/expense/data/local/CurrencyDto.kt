package com.arduia.expense.data.local

import com.google.gson.annotations.SerializedName

data class CurrencyDto(

    @SerializedName("rank")
    val rank: Int,

    @SerializedName("currency_name")
    val name: String,

    @SerializedName("code")
    val code: String,

    @SerializedName("symbol")
    val symbol: String,

    @SerializedName("number")
    val number: String

){
    override fun toString(): String {
        return "$rank $name $code $symbol $number"
    }

}