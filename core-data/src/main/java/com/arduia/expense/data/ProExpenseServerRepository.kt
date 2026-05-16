package com.arduia.expense.data

import com.arduia.expense.data.network.CheckUpdateDto
import com.arduia.expense.data.network.ExpenseVersionDto
import com.arduia.expense.data.network.FeedbackDto
import com.arduia.expense.model.FlowResult
import com.arduia.expense.model.Result
import java.util.concurrent.Flow

interface ProExpenseServerRepository {

    fun postFeedback(comment: FeedbackDto.Request): FlowResult<FeedbackDto.Response>

    fun getVersionStatus(): FlowResult<ExpenseVersionDto>

    suspend fun getAboutUpdateSync(deviceInfo: CheckUpdateDto.Request): Result<CheckUpdateDto.Response>

}