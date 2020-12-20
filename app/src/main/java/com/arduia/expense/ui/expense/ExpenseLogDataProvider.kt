package com.arduia.expense.ui.expense

import androidx.annotation.WorkerThread
import com.arduia.expense.data.local.ExpenseEnt

interface ExpenseLogDataProvider {

    @WorkerThread
    fun get(offset: Int = 0, limit: Int = 0): List<ExpenseEnt>

}