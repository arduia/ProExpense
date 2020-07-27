package com.arduia.expense.data.network

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST


interface ExpenseNetworkDao {

    @GET("/api/version_status.json")
    fun getVersionStatus(): Call<ExpenseVersionDto>

    @POST("/api/feedback_submit.json")
    fun postFeedback(@Body feedback: FeedbackDto.Request): Call<FeedbackDto.Response>

}