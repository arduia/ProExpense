package com.arduia.expense.data.network

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST


interface ExpenseNetworkDao {
    @GET("/api/version_status.json")
    suspend fun getVersionStatus(): ExpenseVersionDto

    @POST("/api/feedback_submit.json")
    suspend fun postFeedback(@Body feedback: FeedbackDto.Request): FeedbackDto.Response

    @POST("/api/check_update_info.json")
    suspend fun getCheckUpdateInfo(@Body deviceInfo: CheckUpdateDto.Request): CheckUpdateDto.Response
}