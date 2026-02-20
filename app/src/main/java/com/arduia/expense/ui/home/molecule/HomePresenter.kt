package com.arduia.expense.ui.home.molecule

import androidx.compose.runtime.*
import com.arduia.expense.data.CurrencyRepository
import com.arduia.expense.data.ExpenseRepository
import com.arduia.expense.model.SuccessResult
import com.arduia.expense.ui.common.category.ExpenseCategory
import com.arduia.expense.ui.common.expense.ExpenseDetailUiModel
import com.arduia.expense.ui.common.formatter.DateRangeFormatter
import com.arduia.expense.ui.common.molecule.Presenter
import com.arduia.expense.ui.common.uimodel.DeleteInfoUiModel
import com.arduia.expense.ui.expenselogs.ExpenseUiModel
import com.arduia.expense.ui.home.ExpenseDetailUiModelMapperFactory
import com.arduia.expense.ui.home.ExpenseRateCalculator
import com.arduia.expense.ui.home.ExpenseUiModelMapperFactory
import com.arduia.expense.ui.home.IncomeOutcomeUiModel
import com.arduia.expense.ui.home.WeeklyGraphUiModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.NumberFormat
import java.util.Calendar
import java.util.Date

class HomePresenter(
    private val currencyRepository: CurrencyRepository,
    private val expenseVoMapperFactory: ExpenseUiModelMapperFactory,
    private val expenseDetailMapperFactory: ExpenseDetailUiModelMapperFactory,
    private val repo: ExpenseRepository,
    private val currencyFormatter: NumberFormat,
    private val dateRangeFormatter: DateRangeFormatter,
    private val calculatorFactory: ExpenseRateCalculator.Factory
) : Presenter<HomeEvent, HomeState> {

    @Composable
    override fun present(events: Flow<HomeEvent>): HomeState {
        var incomeOutcomeData by remember { mutableStateOf<IncomeOutcomeUiModel?>(null) }
        var graphUiModel by remember { mutableStateOf<WeeklyGraphUiModel?>(null) }
        var recentData by remember { mutableStateOf<List<ExpenseUiModel>>(emptyList()) }
        
        var showDetailDialogFor by remember { mutableStateOf<ExpenseDetailUiModel?>(null) }
        var showDeleteConfirmFor by remember { mutableStateOf<DeleteInfoUiModel?>(null) }
        var showSnackbar by remember { mutableStateOf<String?>(null) }
        var openDrawer by remember { mutableStateOf(false) }
        var navigateToLogs by remember { mutableStateOf(false) }
        var navigateToEntryId by remember { mutableStateOf<Int?>(null) }
        
        var currencySymbol by remember { mutableStateOf("") }
        var currentWeekDateRange by remember { mutableStateOf("") }
        
        var prepareDeleteExpenseId by remember { mutableStateOf<Int?>(null) }

        val scope = rememberCoroutineScope()
        val calculator = remember { calculatorFactory.create(scope) }

        // Initial Computations
        LaunchedEffect(Unit) {
            currentWeekDateRange = getWeekDateRange(dateRangeFormatter)
        }

        // Currency Symbol
        LaunchedEffect(Unit) {
            currencyRepository.getSelectedCacheCurrency().collectLatest { result ->
                if (result is SuccessResult) {
                    currencySymbol = result.data.code
                }
            }
        }

        // Week Expenses -> IncomeOutcome
        LaunchedEffect(currentWeekDateRange, currencySymbol) {
            repo.getWeekExpenses().collectLatest { result ->
                if (result is SuccessResult) {
                    val weekExpenses = result.data
                    calculator.setWeekExpenses(weekExpenses.filter { it.category != ExpenseCategory.INCOME })
                    
                    val totalOutcome = weekExpenses.filter { it.category != ExpenseCategory.INCOME }
                        .sumOf { it.amount.getActual().toDouble() }
                    val totalIncome = weekExpenses.filter { it.category == ExpenseCategory.INCOME }
                        .sumOf { it.amount.getActual().toDouble() }
                    
                    val weekOutcomeStr = currencyFormatter.format(totalOutcome)
                    val weekIncomeStr = currencyFormatter.format(totalIncome)
                    
                    incomeOutcomeData = IncomeOutcomeUiModel(
                        incomeValue = weekIncomeStr,
                        outComeValue = weekOutcomeStr,
                        currencySymbol = currencySymbol,
                        dateRange = currentWeekDateRange
                    )
                }
            }
        }

        // Rates -> Graph
        LaunchedEffect(currentWeekDateRange) {
            calculator.getRates().collectLatest { rates ->
                graphUiModel = WeeklyGraphUiModel(
                    dateRange = currentWeekDateRange,
                    rate = rates
                )
            }
        }

        // Recent Expenses
        LaunchedEffect(currencySymbol) {
            repo.getRecentExpense().collectLatest { result ->
                if (result is SuccessResult) {
                    val mapper = expenseVoMapperFactory.create { currencySymbol }
                    recentData = result.data.map(mapper::map)
                }
            }
        }

        // Process Events
        LaunchedEffect(events) {
            events.collect { event ->
                when (event) {
                    is HomeEvent.LifecycleOnResume -> {
                        launch(Dispatchers.IO) {
                            val recentResult = repo.getRecentExpenseSync()
                            if (recentResult is SuccessResult) {
                                val mapper = expenseVoMapperFactory.create { currencySymbol }
                                val mapped = recentResult.data.map(mapper::map)
                                withContext(Dispatchers.Main) { recentData = mapped }
                            }
                            
                            val weekResult = repo.getWeekExpensesSync()
                            if (weekResult is SuccessResult) {
                                val weekExpenses = weekResult.data
                                calculator.setWeekExpenses(weekExpenses.filter { it.category != ExpenseCategory.INCOME })
                                
                                val totalOutcome = weekExpenses.filter { it.category != ExpenseCategory.INCOME }
                                    .sumOf { it.amount.getActual().toDouble() }
                                val totalIncome = weekExpenses.filter { it.category == ExpenseCategory.INCOME }
                                    .sumOf { it.amount.getActual().toDouble() }
                                
                                val weekOutcomeStr = currencyFormatter.format(totalOutcome)
                                val weekIncomeStr = currencyFormatter.format(totalIncome)
                                
                                withContext(Dispatchers.Main) {
                                    incomeOutcomeData = IncomeOutcomeUiModel(
                                        incomeValue = weekIncomeStr,
                                        outComeValue = weekOutcomeStr,
                                        currencySymbol = currencySymbol,
                                        dateRange = currentWeekDateRange
                                    )
                                }
                            }
                        }
                    }
                    is HomeEvent.NavIconClicked -> openDrawer = true
                    is HomeEvent.MoreLogsClicked -> navigateToLogs = true
                    is HomeEvent.RecentItemClicked -> {
                        launch(Dispatchers.IO) {
                            val result = repo.getExpense(event.item.id).first()
                            if (result is SuccessResult) {
                                val mapper = expenseDetailMapperFactory.create { currencySymbol }
                                val detailData = mapper.map(result.data)
                                withContext(Dispatchers.Main) {
                                    showDetailDialogFor = detailData
                                }
                            }
                        }
                    }
                    is HomeEvent.EditExpenseClicked -> {
                        navigateToEntryId = event.expenseId
                        showDetailDialogFor = null
                    }

                    is HomeEvent.AddExpenseClicked -> {
                        navigateToEntryId = -1
                        showDetailDialogFor = null
                    }
                    is HomeEvent.DeleteExpensePrepared -> {
                        prepareDeleteExpenseId = event.expenseId
                        showDeleteConfirmFor = DeleteInfoUiModel(1, null)
                    }
                    is HomeEvent.DeleteExpenseConfirmed -> {
                        val id = prepareDeleteExpenseId ?: return@collect
                        launch(Dispatchers.IO) {
                            repo.deleteExpenseById(id)
                            withContext(Dispatchers.Main) {
                                showSnackbar = "Item Deleted"
                                showDetailDialogFor = null
                                showDeleteConfirmFor = null
                            }
                        }
                    }
                    is HomeEvent.DetailDialogDismissed -> showDetailDialogFor = null
                    is HomeEvent.DeleteDialogDismissed -> showDeleteConfirmFor = null
                    is HomeEvent.NavigationHandled -> {
                        openDrawer = false
                        navigateToLogs = false
                        navigateToEntryId = null
                    }
                    is HomeEvent.SnackbarShown -> showSnackbar = null
                }
            }
        }
        
        return HomeState(
            incomeOutcomeData = incomeOutcomeData,
            graphUiModel = graphUiModel,
            recentData = recentData,
            showDetailDialogFor = showDetailDialogFor,
            showDeleteConfirmFor = showDeleteConfirmFor,
            showSnackbar = showSnackbar,
            openDrawer = openDrawer,
            navigateToLogs = navigateToLogs,
            navigateToEntryId = navigateToEntryId,
            isDeleteEnabledOnDetail = true
        )
    }

    private fun getWeekDateRange(formatter: DateRangeFormatter): String {
        val startTime = getWeekStartTime().time
        val endTime = getWeekEndTime().time
        return formatter.format(start = startTime, end = endTime)
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
