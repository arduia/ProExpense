package com.arduia.expense.ui.statistics.molecule

import androidx.compose.runtime.*
import com.arduia.expense.data.ExpenseRepository
import com.arduia.expense.domain.filter.DateRange
import com.arduia.expense.domain.filter.ExpenseDateRange
import com.arduia.expense.domain.filter.ExpenseLogFilterInfo
import com.arduia.expense.model.Result
import com.arduia.expense.ui.common.filter.Sorting
import com.arduia.expense.ui.common.formatter.DateRangeFormatter
import com.arduia.expense.ui.common.molecule.Presenter
import com.arduia.expense.ui.statistics.CategoryAnalyzer
import com.arduia.expense.ui.statistics.CategoryStatisticUiModel
import kotlinx.coroutines.flow.collectLatest
import java.util.*

class StatisticPresenter(
    private val expenseRepo: ExpenseRepository,
    private val categoryAnalyzer: CategoryAnalyzer,
    private val dateRangeFormatter: DateRangeFormatter
) : Presenter<StatisticEvent, StatisticState> {

    @Composable
    override fun present(events: kotlinx.coroutines.flow.Flow<StatisticEvent>): StatisticState {
        var categoryStatisticList by remember { mutableStateOf(emptyList<CategoryStatisticUiModel>()) }
        var isEmptyExpenseData by remember { mutableStateOf(true) }
        var dateRangeInfo by remember { mutableStateOf("") }
        var showFilterDialogForInfo by remember { mutableStateOf<ExpenseLogFilterInfo?>(null) }

        var filterLimit by remember { mutableStateOf<DateRange?>(null) }
        var filterConstraint by remember { mutableStateOf<DateRange?>(null) }

        LaunchedEffect(Unit) {
            expenseRepo.getExpenseTotalCount().collectLatest { result ->
                if (result is Result.Success) {
                    isEmptyExpenseData = (result.data <= 0)
                }
            }
        }

        LaunchedEffect(Unit) {
            expenseRepo.getMaxAndMiniDateRange().collectLatest { result ->
                if (result is Result.Success) {
                    val dateRange = ExpenseDateRange(result.data.minDate, result.data.maxDate)
                    filterLimit = dateRange
                    if (filterConstraint == null) {
                        filterConstraint = dateRange
                    }
                } else if (result is Result.Error) {
                    val dateRange = ExpenseDateRange(0, Date().time)
                    filterLimit = dateRange
                    if (filterConstraint == null) {
                        filterConstraint = dateRange
                    }
                }
            }
        }

        LaunchedEffect(filterConstraint) {
            filterConstraint?.let { constraint ->
                dateRangeInfo = dateRangeFormatter.format(constraint.start, constraint.end)
                val expensesResult = expenseRepo.getExpenseRangeAsc(constraint.start, constraint.end, 0, Int.MAX_VALUE)
                expensesResult.collectLatest { result ->
                    if (result is Result.Success) {
                        categoryStatisticList = categoryAnalyzer.analyze(result.data)
                    }
                }
            }
        }

        LaunchedEffect(events) {
            events.collect { event ->
                when (event) {
                    is StatisticEvent.FilterIconClicked -> {
                        if (filterConstraint != null && filterLimit != null) {
                            showFilterDialogForInfo = ExpenseLogFilterInfo(
                                dateRangeLimit = filterLimit!!,
                                dateRangeSelected = filterConstraint!!,
                                sorting = Sorting.DESC
                            )
                        }
                    }
                    is StatisticEvent.FilterDialogDismissed -> {
                        showFilterDialogForInfo = null
                    }
                    is StatisticEvent.FilterApplied -> {
                        filterConstraint = event.filter.dateRangeSelected
                        showFilterDialogForInfo = null
                    }
                }
            }
        }

        return StatisticState(
            categoryStatisticList = categoryStatisticList,
            isEmptyExpenseData = isEmptyExpenseData,
            dateRangeInfo = dateRangeInfo,
            showFilterDialogForInfo = showFilterDialogForInfo
        )
    }
}
