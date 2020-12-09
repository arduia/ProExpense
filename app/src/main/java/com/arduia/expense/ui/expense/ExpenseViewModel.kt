package com.arduia.expense.ui.expense

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import androidx.paging.*
import androidx.room.InvalidationTracker
import com.arduia.core.arch.Mapper
import com.arduia.expense.data.ExpenseRepository
import com.arduia.expense.data.local.ExpenseEnt
import com.arduia.expense.model.awaitValueOrError
import com.arduia.expense.ui.expense.swipe.SwipeItemState
import com.arduia.expense.ui.expense.swipe.SwipeStateHolder
import com.arduia.mvvm.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber


class ExpenseViewModel @ViewModelInject constructor(
    logTransform: Mapper<List<ExpenseEnt>, List<ExpenseLogVo>>,
    private val expenseRepo: ExpenseRepository,
    invalidationTracker: InvalidationTracker
) : ViewModel(), LifecycleObserver {

    private var swipeStateHolder: SwipeStateHolder? = null

    private val _onRestoreSwipeState = EventLiveData<SwipeStateHolder>()
    val onRestoreSwipeState get() = _onRestoreSwipeState.asLiveData()

    private val _expenseLogMode = BaseLiveData<ExpenseMode>()
    val expenseLogMode get() = _expenseLogMode.asLiveData()

    private val dataProvider = object : ExpenseLogDataProvider{
        override fun get(offset: Int, limit: Int): List<ExpenseEnt> {
           return expenseRepo.getExpenseRange(limit, offset).awaitValueOrError()
        }
    }

    private val source =
        ExpenseProxySource(invalidationTracker, dataProvider, logTransform)

    val expenseList: LiveData<PagedList<ExpenseLogVo>>

    init {
        val factory = ExpenseProxySource.Factory { source }
        _expenseLogMode.value = ExpenseMode.NORMAL
        expenseList = factory.toLiveData(config = Config(10,maxSize = 100,enablePlaceholders = false,prefetchDistance = 10))
    }

    fun storeState(state: SwipeStateHolder){
        this.swipeStateHolder = state
        onSwipeStateChanged()
    }

    fun clearState(){
        this.swipeStateHolder?.clear()
        onRestoreState()
        onSwipeStateChanged()
    }

    fun deleteSelectedItems(){
        viewModelScope.launch(Dispatchers.IO){
            val deleteItems = swipeStateHolder?.getSelectIdList() ?: return@launch
            expenseRepo.deleteAllExpense(deleteItems)
        }
    }

    private fun onSwipeStateChanged(){
        val selectCount = swipeStateHolder?.getCount(SwipeItemState.STATE_LOCK_START) ?: return

        if(selectCount > 0){
            if(_expenseLogMode.value != ExpenseMode.SELECTION){
                _expenseLogMode post ExpenseMode.SELECTION
            }
        } else  _expenseLogMode post ExpenseMode.NORMAL
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    private fun onRestoreState(){
        val state = swipeStateHolder
        if(state !=null){
            _onRestoreSwipeState post event(state)
        }
    }

    override fun onCleared() {
        super.onCleared()
        source.release()
        swipeStateHolder = null
    }

}
