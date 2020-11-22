package com.arduia.expense.ui.home

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.arduia.expense.data.CurrencyRepository
import com.arduia.expense.data.ExpenseRepository
import com.arduia.expense.data.local.ExpenseEnt
import com.arduia.expense.di.CurrencyDecimalFormat
import com.arduia.expense.model.Result
import com.arduia.expense.ui.common.*
import com.arduia.expense.ui.mapping.ExpenseMapper
import com.arduia.expense.ui.vto.ExpenseDetailsVto
import com.arduia.expense.ui.vto.ExpenseVto
import com.arduia.mvvm.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.DecimalFormat

class HomeViewModel @ViewModelInject constructor(
    private val currencyRepository: CurrencyRepository,
    private val mapper: ExpenseMapper,
    private val repo: ExpenseRepository,
    @CurrencyDecimalFormat private val currencyFormatter: DecimalFormat,
    calculatorFactory: ExpenseRateCalculator.Factory
) : ViewModel() {

    private val _recentData = BaseLiveData<List<ExpenseVto>>()
    val recentData get() = _recentData.asLiveData()

    private val _detailData = EventLiveData<ExpenseDetailsVto>()
    val detailData get() = _detailData.asLiveData()

    private val _costRates = BaseLiveData<Map<Int, Int>>()
    val costRate get() = _costRates.asLiveData()

    private val _onExpenseItemDeleted = EventLiveData<Unit>()
    val onExpenseItemDeleted get() = _onExpenseItemDeleted.asLiveData()

    private val _currencySymbol = BaseLiveData<String>()
    val currencySymbol get() = _currencySymbol.asLiveData()

    private val _onError = EventLiveData<Unit>()
    val onError get() = _onError.asLiveData()

    private val _weekIncome = BaseLiveData<String>()
    val weekIncome get() = _weekIncome.asLiveData()

    private val _weekOutcome = BaseLiveData<String>()
    val weekOutcome get() = _weekOutcome.asLiveData()

    private val _currentWeekDateRange = BaseLiveData<String>()
    val currentWeekDateRange get() = this._currentWeekDateRange.asLiveData()

    private val calculator = calculatorFactory.create(viewModelScope)

    init {
        init()
    }

    fun selectItemForDetail(selectedItem: ExpenseVto) {
        viewModelScope.launch(Dispatchers.IO) {
            when (val result = repo.getExpense(selectedItem.id).first()) {
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

                        val weekExpenses = it.data
                        calculator.setWeekExpenses(weekExpenses)

                        val totalOutcome = weekExpenses.getTotalOutcomeAsync()
                        val totalIncome = weekExpenses.getTotalIncomeAsync()

                        _weekOutcome post currencyFormatter.format(totalOutcome.await())
                        _weekIncome post currencyFormatter.format(totalIncome.await())
                    }
                }
            }
            .launchIn(viewModelScope)
    }

    private fun List<ExpenseEnt>.getTotalOutcomeAsync() = viewModelScope.async {
        this@getTotalOutcomeAsync.filter { expense ->
            expense.category != ExpenseCategory.INCOME
        }.sumByDouble { expense -> expense.amount.toDouble() }
    }

    private fun List<ExpenseEnt>.getTotalIncomeAsync() = viewModelScope.async {
        this@getTotalIncomeAsync.filter { expense ->
            expense.category == ExpenseCategory.INCOME
        }.sumByDouble { expense -> expense.amount.toDouble() }
    }

    private fun observeRate() {
        calculator.getRates()
            .flowOn(Dispatchers.IO)
            .onEach(_costRates::postValue)
            .launchIn(viewModelScope)
    }

}
