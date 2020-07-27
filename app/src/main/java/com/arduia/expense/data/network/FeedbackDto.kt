package com.arduia.expense.data.network

import com.google.gson.annotations.SerializedName

class FeedbackDto{

    data class Request(
        @SerializedName("name")
        val name: String,

        @SerializedName("email")
        val email: String,

        @SerializedName("comment")
        val comment: String,

        @SerializedName("key")
        val key: String
    )

    data class Response(
        @SerializedName("code")
        val code: Int,

        @SerializedName("message")
        val message: String
    )
}