package com.arduia.expense.ui.expense

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import androidx.paging.Config
import androidx.paging.DataSource
import androidx.paging.toLiveData
import com.arduia.core.arch.Mapper
import com.arduia.expense.data.ExpenseRepository
import com.arduia.expense.data.local.ExpenseEnt
import com.arduia.expense.model.SuccessResult
import com.arduia.expense.ui.vto.ExpenseDetailsVto
import com.arduia.mvvm.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import javax.inject.Provider


class ExpenseViewModel @ViewModelInject constructor(
    private val expenseDetailMapper: Mapper<ExpenseEnt, ExpenseDetailsVto>,
    expenseLogTransform: Mapper<List<ExpenseEnt>,List<ExpenseLogVo>>,
    expenseRepo: ExpenseRepository
) : ViewModel() {

    private val _selectedItems = BaseLiveData<List<Int>>()
    val selectedItems get() = _selectedItems.asLiveData()

    val factory: DataSource.Factory<Int, ExpenseLogVo>

    init {
        val source = ExpenseProxySource(viewModelScope,expenseRepo, expenseLogTransform)
        factory = ExpenseProxySource.Factory { source }
    }

    val expenseList get() = factory.toLiveData(config = Config(10,maxSize = 100,enablePlaceholders = false,prefetchDistance = 10))

}
