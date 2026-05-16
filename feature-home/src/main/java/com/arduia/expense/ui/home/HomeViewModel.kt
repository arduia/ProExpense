package com.arduia.expense.ui.home

import com.arduia.expense.data.CurrencyRepository
import com.arduia.expense.data.ExpenseRepository
import com.arduia.expense.data.local.ExpenseEnt
import com.arduia.expense.di.CurrencyDecimalFormat
import com.arduia.expense.di.MonthlyDateRange
import com.arduia.expense.model.Result
import com.arduia.expense.model.SuccessResult
import com.arduia.expense.model.awaitValueOrError
import com.arduia.expense.model.onSuccess
import com.arduia.expense.ui.base.BaseViewModel
import com.arduia.expense.ui.common.category.ExpenseCategory
import com.arduia.expense.ui.common.expense.ExpenseDetailUiModel
import com.arduia.expense.ui.common.formatter.DateRangeFormatter
import com.arduia.expense.ui.common.uimodel.DeleteInfoUiModel
import com.arduia.expense.ui.expenselogs.ExpenseUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import timber.log.Timber
import java.text.NumberFormat
import java.util.Calendar
import java.util.Date
import javax.inject.Inject

data class HomeUiState(
    val isLoading: Boolean = false,
    val totalIncome: String = "",
    val totalExpense: String = "",
    val totalBalance: String = "",
    val currencySymbol: String = "",
    val dateRange: String = "",
    val recentExpenses: List<ExpenseUiModel> = emptyList(),
    val weeklyGraphData: Map<Int, Float> = emptyMap()
)

sealed class HomeUiEffect {
    data class ShowDetail(val detail: ExpenseDetailUiModel) : HomeUiEffect()
    data class ShowDeleteConfirm(val info: DeleteInfoUiModel) : HomeUiEffect()
    object ShowItemDeleted : HomeUiEffect()
    object ShowError : HomeUiEffect()
}

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val currencyRepository: CurrencyRepository,
    private val expenseVoMapperFactory: ExpenseUiModelMapperFactory,
    private val expenseDetailMapperFactory: ExpenseDetailUiModelMapperFactory,
    private val repo: ExpenseRepository,
    @CurrencyDecimalFormat private val currencyFormatter: NumberFormat,
    @MonthlyDateRange private val dateRangeFormatter: DateRangeFormatter,
    calculatorFactory: ExpenseRateCalculator.Factory
) : BaseViewModel<HomeUiState, HomeUiEffect>(HomeUiState()) {

    private val currencySymbol = MutableStateFlow("")
    private val currentWeekDateRange = MutableStateFlow("")
    private val calculator = calculatorFactory.create(viewModelScope)
    private var prepareDeleteExpenseId: Int? = null

    init {
        observeWeekExpenses()
        observeRate()
        updateWeekDateRange()
        observeCurrencySymbol()
    }

    fun selectItemForDetail(selectedItem: ExpenseUiModel) {
        viewModelScope.launch(Dispatchers.IO) {
            when (val result = repo.getExpense(selectedItem.id).first()) {
                is Result.Loading -> Unit
                is Result.Error -> Unit
                is Result.Success -> {
                    val symbol = currencySymbol.value
                    val mapper = expenseDetailMapperFactory.create { symbol }
                    val detail = mapper.map(result.data)
                    sendEffect(HomeUiEffect.ShowDetail(detail))
                }
            }
        }
    }

    fun onDeleteConfirmed() {
        val id = prepareDeleteExpenseId ?: return
        viewModelScope.launch(Dispatchers.IO) {
            repo.deleteExpenseById(id)
            sendEffect(HomeUiEffect.ShowItemDeleted)
        }
    }

    fun onDeletePrepared(id: Int) {
        this.prepareDeleteExpenseId = id
        sendEffect(HomeUiEffect.ShowDeleteConfirm(DeleteInfoUiModel(1, null)))
    }

    private fun observeCurrencySymbol() {
        currencyRepository.getSelectedCacheCurrency()
            .flowOn(Dispatchers.IO)
            .onSuccess { currency ->
                currencySymbol.value = currency.code
            }
            .launchIn(viewModelScope)
    }

    private fun updateWeekDateRange() {
        currentWeekDateRange.value = getWeekDateRange()
    }

    private fun observeWeekExpenses() {
        repo.getWeekExpenses()
            .flowOn(Dispatchers.IO)
            .onEach {
                Timber.d("Home, expenses: $it")
                when (it) {
                    is Result.Loading -> Unit
                    is Result.Error -> sendEffect(HomeUiEffect.ShowError)
                    is Result.Success -> updateIncomeOutcome(it.data)
                }
            }
            .launchIn(viewModelScope)

        repo.getRecentExpense()
            .flowOn(Dispatchers.IO)
            .combine(currencySymbol) { recent, symbol ->
                val data = (recent as? SuccessResult)?.data ?: return@combine
                val mapper = expenseVoMapperFactory.create { symbol }
                setState { copy(recentExpenses = data.map(mapper::map)) }
            }
            .launchIn(viewModelScope)
    }

    private suspend fun updateIncomeOutcome(weekExpenses: List<ExpenseEnt>) {
        calculator.setWeekExpenses(weekExpenses.filter { it.category != ExpenseCategory.INCOME })

        val totalOutcome = weekExpenses.getTotalOutcomeAsync()
        val totalIncome = weekExpenses.getTotalIncomeAsync()

        val weekOutcome = currencyFormatter.format(totalOutcome.await())
        val weekIncome = currencyFormatter.format(totalIncome.await())
        val dateRange = currentWeekDateRange.value
        val symbol = currencyRepository.getSelectedCacheCurrency().awaitValueOrError().code

        setState {
            copy(
                totalIncome = weekIncome,
                totalExpense = weekOutcome,
                totalBalance = weekOutcome,
                currencySymbol = symbol,
                dateRange = dateRange
            )
        }
    }

    fun updateRecentData() {
        viewModelScope.launch(Dispatchers.IO) {
            val data = (repo.getRecentExpenseSync() as? SuccessResult)?.data ?: return@launch
            val mapper = expenseVoMapperFactory.create { currencySymbol.value }
            setState { copy(recentExpenses = data.map(mapper::map)) }
        }
        viewModelScope.launch(Dispatchers.IO) {
            val data = (repo.getWeekExpensesSync() as? SuccessResult)?.data ?: return@launch
            updateIncomeOutcome(data)
        }
    }

    private fun List<ExpenseEnt>.getTotalOutcomeAsync() = viewModelScope.async {
        filter { it.category != ExpenseCategory.INCOME }
            .sumByDouble { it.amount.getActual().toDouble() }
    }

    private fun List<ExpenseEnt>.getTotalIncomeAsync() = viewModelScope.async {
        filter { it.category == ExpenseCategory.INCOME }
            .sumByDouble { it.amount.getActual().toDouble() }
    }

    private fun observeRate() {
        calculator.getRates()
            .flowOn(Dispatchers.IO)
            .combine(currentWeekDateRange) { rate, dateRange ->
                WeeklyGraphUiModel(dateRange, rate)
            }
            .onEach { graphData ->
                setState { copy(weeklyGraphData = graphData.rate.mapValues { it.value.toFloat() }) }
            }
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

    private fun getWeekEndTime(): Date {
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
