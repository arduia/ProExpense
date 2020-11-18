    package com.arduia.expense.ui.home

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.arduia.expense.data.CurrencyRepository
import com.arduia.expense.data.ExpenseRepository
import com.arduia.expense.data.SettingsRepository
import com.arduia.expense.data.local.ExpenseEnt
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

    init {
        init()
    }

    fun selectItemForDetail(selectedItem: ExpenseVto) {
        viewModelScope.launch(Dispatchers.IO) {
            val item = repo.getExpense(selectedItem.id).first()
            val detailData = mapper.mapToDetailVto(item)
            _detailData post event(detailData)
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
    }

    private fun observeCurrencySymbol() {
        settingRepo.getSelectedCurrencyNumber()
            .flowOn(Dispatchers.IO)
            .onEach {
                currencyRepository.setSelectedCacheCurrency(it)
                _currencySymbol post currencyRepository.getSelectedCacheCurrency().symbol
            }
            .launchIn(viewModelScope)
    }

    private fun observeRecentExpenses() {
        viewModelScope.launch(Dispatchers.IO) {
            repo.getRecentExpense()
                .flowOn(Dispatchers.IO)
                .collect {
                    _recentData post it.map(mapper::mapToVto)
                }
        }
    }

    private fun observeWeekExpenses() {
        viewModelScope.launch(Dispatchers.IO) {
            repo.getWeekExpenses()
                .flowOn(Dispatchers.IO)
                .collect {
                    val totalAmount = it.filter { expenseEnt ->
                        expenseEnt.category != ExpenseCategory.INCOME
                    }.map { expenseEnt -> expenseEnt.amount }.sum()
                    _totalCost post totalAmount
                    calculator.setWeekExpenses(it)
                    _costRates post calculator.getRates()
                }
        }
    }

}
