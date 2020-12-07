package com.arduia.expense.ui.expense

import androidx.paging.DataSource
import androidx.paging.PageKeyedDataSource
import androidx.paging.PositionalDataSource
import com.arduia.core.arch.Mapper
import com.arduia.expense.data.ExpenseRepository
import com.arduia.expense.data.local.ExpenseEnt
import com.arduia.expense.model.awaitValueOrError
import com.arduia.expense.ui.expense.mapper.ExpenseLogTransform
import com.arduia.expense.ui.expense.swipe.SwipeStateHolder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Provider
import kotlin.math.log

class ExpenseProxySource(
    private val ioScope: CoroutineScope,
    private val expenseRepository: ExpenseRepository,
    private val logTransform: Mapper<List<ExpenseEnt>, List<ExpenseLogVo>>
):  PositionalDataSource<ExpenseLogVo>() {

    override fun loadInitial(
        params: LoadInitialParams,
        callback: LoadInitialCallback<ExpenseLogVo>
    ) {
        ioScope.launch(Dispatchers.IO) {
            val data = expenseRepository.getExpenseRange(
                limit = params.pageSize,
                offset = params.requestedStartPosition
            ).awaitValueOrError()
            val result = data.transform()
            callback.onResult(result, params.requestedStartPosition, result.size)
        }
    }

    override fun loadRange(params: LoadRangeParams, callback: LoadRangeCallback<ExpenseLogVo>) {
        ioScope.launch(Dispatchers.IO) {
            val data = expenseRepository.getExpenseRange(
                offset = params.startPosition,
                limit = params.loadSize
            ).awaitValueOrError()
            val result = data.transform()
            callback.onResult(result)
        }
    }

    private fun List<ExpenseEnt>.transform(): List<ExpenseLogVo> {
        return logTransform.map(this)
    }

    class Factory(
        private val provider: Provider<ExpenseProxySource>
    ) : DataSource.Factory<Int, ExpenseLogVo>() {
        override fun create(): DataSource<Int, ExpenseLogVo> {
            return provider.get()
        }
    }

}