package com.arduia.expense.ui.home

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.arduia.expense.data.CurrencyRepository
import com.arduia.expense.data.ExpenseRepository
import com.arduia.expense.data.SettingsRepository
import com.arduia.expense.model.Result
import com.arduia.expense.ui.common.*
import com.arduia.expense.ui.mapping.ExpenseMapper
import com.arduia.expense.ui.vto.ExpenseDetailsVto
import com.arduia.expense.ui.vto.ExpenseVto
import com.arduia.mvvm.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class HomeViewModel @ViewModelInject constructor(
    private val currencyRepository: CurrencyRepository,
    private val settingRepo: SettingsRepository,
    private val mapper: ExpenseMapper,
    private val repo: ExpenseRepository,
    private val calculator: ExpenseRateCalculator
) : ViewModel() {

    private val _recentData = BaseLiveData<List<ExpenseVto>>()
    val recentData get() = _recentData.asLiveData()

    private val _detailData = EventLiveData<ExpenseDetailsVto>()
    val detailData get() = _detailData.asLiveData()

    private val _totalCost = BaseLiveData<Float>()
    val totalCost get() = _totalCost.asLiveData()

    private val _costRates = BaseLiveData<Map<Int, Int>>()
    val costRate get() = _costRates.asLiveData()

    private val _onExpenseItemDeleted = EventLiveData<Unit>()
    val onExpenseItemDeleted get() = _onExpenseItemDeleted.asLiveData()

    private val _currencySymbol = BaseLiveData<String>()
    val currencySymbol get() = _currencySymbol.asLiveData()

    private val _onError = EventLiveData<Unit>()
    val onError get() = _onError.asLiveData()

    init {
        init()
    }

    fun selectItemForDetail(selectedItem: ExpenseVto) {
        viewModelScope.launch(Dispatchers.IO) {
            when(val result = repo.getExpense(selectedItem.id).first()){
                is Result.Loading -> Unit
                is Result.Error -> Unit
                is Result.Success -> {
                    val detailData = mapper.mapToDetailVto(result.data)
                    _detailData post event(detailData)
                }
            }
        }
    }

    fun deleteExpense(id: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            repo.deleteExpenseById(id)
            _onExpenseItemDeleted post EventUnit
        }
    }

    private fun init() {
        observeCurrencySymbol()
        observeRecentExpenses()
        observeWeekExpenses()
        observeRate()
    }

    private fun observeCurrencySymbol() {
        currencyRepository.getSelectedCacheCurrency()
            .flowOn(Dispatchers.IO)
            .onEach {
                when (it) {
                    is Result.Loading -> Unit
                    is Result.Error -> _onError post EventUnit
                    is Result.Success -> {
                        _currencySymbol post it.data.symbol
                    }
                }
            }
            .launchIn(viewModelScope)
    }

    private fun observeRecentExpenses() {
        repo.getRecentExpense()
            .flowOn(Dispatchers.IO)
            .onEach {
                when (it) {
                    is Result.Success -> _recentData post it.data.map(mapper::mapToVto)
                    is Result.Error -> _onError post EventUnit
                    is Result.Loading -> Unit
                }
            }
            .launchIn(viewModelScope)
    }

    private fun observeWeekExpenses() {
        repo.getWeekExpenses()
            .flowOn(Dispatchers.IO)
            .onEach {
                when (it) {
                    is Result.Loading -> Unit
                    is Result.Error -> _onError post EventUnit
                    is Result.Success -> {
                        calculator.setWeekExpenses(it.data)
                        val totalAmount = it.data.filter { expenseEnt ->
                            expenseEnt.category != ExpenseCategory.INCOME
                        }.map { expenseEnt -> expenseEnt.amount }.sum()
                        _totalCost post totalAmount
                    }
                }

            }
    }

    private fun observeRate(){
        calculator.getRates()
            .flowOn(Dispatchers.IO)
            .onEach(_costRates::postValue)
            .launchIn(viewModelScope)
    }

}
