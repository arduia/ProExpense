package com.arduia.expense.ui.expense

import androidx.paging.DataSource
import androidx.paging.PositionalDataSource
import androidx.room.InvalidationTracker
import com.arduia.core.arch.Mapper
import com.arduia.expense.data.ExpenseRepository
import com.arduia.expense.data.local.ExpenseEnt
import com.arduia.expense.model.awaitValueOrError
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Provider

class ExpenseProxySource(
    private val invalidationTracker: InvalidationTracker,
    private val dataProvider: ExpenseLogDataProvider,
    private val logTransform: Mapper<List<ExpenseEnt>, List<ExpenseLogVo>>
):  PositionalDataSource<ExpenseLogVo>() {

    private val observer: InvalidationTracker.Observer

    init {
        observer = object : InvalidationTracker.Observer(ExpenseEnt.TABLE_NAME){
            override fun onInvalidated(tables: MutableSet<String>) {
                invalidate()
                Timber.d("invalidated!")
            }
        }
        invalidationTracker.addObserver(observer)
    }

    fun release() = invalidationTracker.removeObserver(observer)

    override fun loadInitial(
        params: LoadInitialParams,
        callback: LoadInitialCallback<ExpenseLogVo>
    ) {
            val data = dataProvider.get(
                limit = params.pageSize,
                offset = params.requestedStartPosition
            )
            val result = data.transform()
            callback.onResult(result, params.requestedStartPosition, result.size)
    }

    override fun invalidate() {
        super.invalidate()
        Timber.d("invalidate()")
    }

    override fun isInvalid(): Boolean {
        invalidationTracker.refreshVersionsAsync()
        return super.isInvalid()
    }

    override fun loadRange(params: LoadRangeParams, callback: LoadRangeCallback<ExpenseLogVo>) {
            val data = dataProvider.get(
                offset = params.startPosition,
                limit = params.loadSize
            )
            val result = data.transform()
            callback.onResult(result)
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