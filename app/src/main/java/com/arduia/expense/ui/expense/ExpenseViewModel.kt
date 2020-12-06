package com.arduia.expense.ui.expense

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import androidx.paging.*
import com.arduia.core.arch.Mapper
import com.arduia.expense.data.ExpenseRepository
import com.arduia.expense.data.local.ExpenseEnt
import com.arduia.expense.ui.expense.swipe.SwipeStateHolder
import com.arduia.mvvm.*


class ExpenseViewModel @ViewModelInject constructor(
    logTransform: Mapper<List<ExpenseEnt>, List<ExpenseLogVo>>,
    expenseRepo: ExpenseRepository
) : ViewModel(), LifecycleObserver {

    val factory: DataSource.Factory<Int, ExpenseLogVo>

    private var swipeStateHolder: SwipeStateHolder? = null

    private val _onRestoreSwipeState = EventLiveData<SwipeStateHolder>()
    val onRestoreSwipeState get() = _onRestoreSwipeState.asLiveData()

    init {
        val source = ExpenseProxySource(viewModelScope,expenseRepo, logTransform)
        factory = ExpenseProxySource.Factory { source }
    }

    val expenseList get() = factory.toLiveData(config = Config(10,maxSize = 100,enablePlaceholders = false,prefetchDistance = 10))

    fun storeState(state: SwipeStateHolder){
        this.swipeStateHolder = state
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    private fun onCreate(){
        val state = swipeStateHolder
        if(state !=null){
            _onRestoreSwipeState post event(state)
        }
    }

    override fun onCleared() {
        super.onCleared()
        swipeStateHolder = null
    }

}
