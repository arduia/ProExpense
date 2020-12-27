package com.arduia.expense.ui.home

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.arduia.expense.data.CurrencyRepository
import com.arduia.expense.data.ExpenseRepository
import com.arduia.expense.data.local.ExpenseEnt
import com.arduia.expense.di.CurrencyDecimalFormat
import com.arduia.expense.domain.Amount
import com.arduia.expense.domain.times
import com.arduia.expense.model.*
import com.arduia.expense.ui.common.*
import com.arduia.expense.ui.common.formatter.DateRangeFormatter
import com.arduia.expense.ui.vto.ExpenseDetailsVto
import com.arduia.expense.ui.vto.ExpenseVto
import com.arduia.mvvm.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import timber.log.Timber
import java.lang.Exception
import java.math.BigDecimal
import java.text.NumberFormat
import java.util.*

class HomeViewModel @ViewModelInject constructor(
    private val currencyRepository: CurrencyRepository,
    private val expenseVoMapperFactory: ExpenseVoMapperFactory,
    private val expenseDetailMapperFactory: ExpenseDetailMapperFactory,
    private val repo: ExpenseRepository,
    @CurrencyDecimalFormat private val currencyFormatter: NumberFormat,
    private val dateRangeFormatter: DateRangeFormatter,
    calculatorFactory: ExpenseRateCalculator.Factory
) : ViewModel() {

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

    private val _recentData = BaseLiveData<List<ExpenseVto>>()
    val recentData get() = _recentData.asLiveData()

    private val _onDeleteConfirm = EventLiveData<DeleteInfoVo>()
    val onDeleteConfirm get() = _onDeleteConfirm.asLiveData()

    val isEmptyRecent = _recentData.switchMap {
        return@switchMap BaseLiveData(it.isEmpty())
    }

    private val calculator = calculatorFactory.create(viewModelScope)

    private var prepareDeleteExpenseId: Int? = null

    init {
        observeWeekExpenses()
        observeRate()
        updateWeekDateRange()
        observeCurrencySymbol()
    }

    fun selectItemForDetail(selectedItem: ExpenseVto) {
        viewModelScope.launch(Dispatchers.IO) {
            when (val result = repo.getExpense(selectedItem.id).first()) {
                is Result.Loading -> Unit
                is Result.Error -> Unit
                is Result.Success -> {
                    val symbol = currencySymbol.value ?: ""
                    val mapper = expenseDetailMapperFactory.create { symbol }
                    val detailData = mapper.map(result.data)
                    _detailData post event(detailData)
                }
            }
        }
    }

    fun onDeleteConfirmed() {
        val id = prepareDeleteExpenseId ?: return
        viewModelScope.launch(Dispatchers.IO) {
            repo.deleteExpenseById(id)
            _onExpenseItemDeleted post EventUnit
        }
    }

    fun onDeletePrepared(id: Int) {
        this.prepareDeleteExpenseId = id
        _onDeleteConfirm post event(DeleteInfoVo(0, null))
    }

    private fun observeCurrencySymbol() {
        currencyRepository.getSelectedCacheCurrency()
            .flowOn(Dispatchers.IO)
            .onSuccess {
                _currencySymbol post it.code
            }
            .launchIn(viewModelScope)
    }

    private fun updateWeekDateRange() {
        _currentWeekDateRange set getWeekDateRange()
    }

    private fun onErrorResult(e: Exception) {
        _onError post EventUnit
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

        repo.getRecentExpense()
            .flowOn(Dispatchers.IO)
            .combine(_currencySymbol.asFlow()) { recent, symbol ->
                val data = (recent as? SuccessResult)?.data ?: return@combine
                val mapper = expenseVoMapperFactory.create { symbol }
                _recentData post data.map(mapper::map)
            }
            .launchIn(viewModelScope)
    }

    private fun List<ExpenseEnt>.getTotalOutcomeAsync() = viewModelScope.async {
        this@getTotalOutcomeAsync.filter { expense ->
            expense.category != ExpenseCategory.INCOME
        }.sumByDouble { expense -> expense.amount.getActual().toDouble() }
    }

    private fun List<ExpenseEnt>.getTotalIncomeAsync() = viewModelScope.async {
        this@getTotalIncomeAsync.filter { expense ->
            expense.category == ExpenseCategory.INCOME
        }.sumByDouble { expense -> expense.amount.getActual().toDouble() }
    }

    private fun observeRate() {
        calculator.getRates()
            .flowOn(Dispatchers.IO)
            .onEach(_costRates::postValue)
            .launchIn(viewModelScope)
    }


    private fun getWeekDateRange(): String {
        val startTime = getWeekStartTime().time
        val endTime = getWeekEndTime().time
        return dateRangeFormatter.format(start = startTime, end = endTime)
    }

    private fun getWeekStartTime(): Date {

        val calendar = Calendar.getInstance()

        val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
        val dayOfYear = calendar.get(Calendar.DAY_OF_YEAR)
        val startSunDay = (dayOfYear - dayOfWeek) + 1

        calendar.set(Calendar.DAY_OF_YEAR, startSunDay)
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)

        return calendar.time
    }

    private fun getWeekEndTime(): Date{
        val calendar = Calendar.getInstance()

        val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
        val dayOfYear = calendar.get(Calendar.DAY_OF_YEAR)

        val startSunDay = (dayOfYear - dayOfWeek) + 1

        calendar.set(Calendar.DAY_OF_YEAR, startSunDay + 7)
        calendar.set(Calendar.HOUR_OF_DAY, 23)
        calendar.set(Calendar.MINUTE, 59)
        calendar.set(Calendar.SECOND, 59)

        return calendar.time
    }
}
