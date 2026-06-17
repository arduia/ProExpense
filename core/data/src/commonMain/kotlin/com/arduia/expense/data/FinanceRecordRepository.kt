package com.arduia.expense.data

import com.arduia.expense.domain.Amount

interface FinanceRecordRepository {
    suspend fun getTotalRecorded(): Amount
}
