package com.arduia.expense.ui.expenselogs.molecule

import android.util.Log
import androidx.compose.runtime.*
import com.arduia.expense.data.CurrencyRepository
import com.arduia.expense.data.ExpenseRepository
import com.arduia.expense.domain.filter.DateRange
import com.arduia.expense.domain.filter.ExpenseDateRange
import com.arduia.expense.domain.filter.ExpenseLogFilterInfo
import com.arduia.expense.model.awaitValueOrError
import com.arduia.expense.model.getDataOrError
import com.arduia.expense.ui.common.expense.ExpenseDetailUiModel
import com.arduia.expense.ui.common.filter.DateRangeSortingEnt
import com.arduia.expense.ui.common.filter.Sorting
import com.arduia.expense.ui.common.formatter.DateRangeFormatter
import com.arduia.expense.ui.common.molecule.Presenter
import com.arduia.expense.ui.expenselogs.ExpenseLogUiModel
import com.arduia.expense.ui.expenselogs.ExpenseMode
import com.arduia.expense.ui.home.ExpenseDetailUiModelMapperFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.withContext
import java.util.*

class ExpenseLogsPresenter(
    private val expenseRepo: ExpenseRepository,
    private val currencyRepo: CurrencyRepository,
    private val dateRangeFormatter: DateRangeFormatter,
    private val expenseDetailMapperFactory: ExpenseDetailUiModelMapperFactory
) : Presenter<ExpenseLogsEvent, ExpenseLogsState> {

    @Composable
    override fun present(events: kotlinx.coroutines.flow.Flow<ExpenseLogsEvent>): ExpenseLogsState {
        var mode by remember { mutableStateOf(ExpenseMode.NORMAL) }
        var selectedCount by remember { mutableStateOf(0) }
        var selectedIds by remember { mutableStateOf<Set<Int>>(emptySet()) }
        var isEmptyLogs by remember { mutableStateOf(true) }
        var isFilterEnabled by remember { mutableStateOf(false) }
        var filterDateRangeText by remember { mutableStateOf("") }
        var showFilterDialogForInfo by remember { mutableStateOf<ExpenseLogFilterInfo?>(null) }
        var showMultiDeleteConfirmCount by remember { mutableStateOf<Int?>(null) }
        var showSingleDeleteConfirm by remember { mutableStateOf(false) }
        var showDetailDialogFor by remember { mutableStateOf<ExpenseDetailUiModel?>(null) }
        var currencySymbol by remember { mutableStateOf("") }

        var filterLimit by remember { mutableStateOf<DateRange?>(null) }
        var filterConstraint by remember { mutableStateOf<DateRangeSortingEnt?>(null) }
        
        var pendingSingleDeleteId by remember { mutableStateOf<Int?>(null) }

        // Observe total count for filter enable/disable
        LaunchedEffect(Unit) {
            expenseRepo.getExpenseTotalCount().collectLatest { result ->
                if (result is com.arduia.expense.model.Result.Success) {
                    isFilterEnabled = (result.data > 0)
                    isEmptyLogs = (result.data == 0)
                }
            }
        }

        // Observe date range for filter
        LaunchedEffect(Unit) {
            expenseRepo.getMaxAndMiniDateRange().collectLatest { result ->
                if (result is com.arduia.expense.model.Result.Success) {
                    val dateRange = ExpenseDateRange(result.data.minDate, result.data.maxDate)
                    filterLimit = dateRange
                    
                    val currentConstraint = filterConstraint
                    if (currentConstraint != null) {
                        val min = if (currentConstraint.dateRange.start < dateRange.start) dateRange.start else currentConstraint.dateRange.start
                        val max = if (currentConstraint.dateRange.end > dateRange.end) dateRange.end else currentConstraint.dateRange.end
                        val newConstraint = DateRangeSortingEnt(ExpenseDateRange(if (min < max) min else 0, max), currentConstraint.sorting)
                        filterConstraint = newConstraint
                    } else {
                        filterConstraint = DateRangeSortingEnt(dateRange, Sorting.DESC)
                    }
                } else {
                    val defaultRange = ExpenseDateRange(Date().time, Date().time)
                    filterLimit = defaultRange
                    filterConstraint = DateRangeSortingEnt(defaultRange, Sorting.DESC)
                }
            }
        }

        // Observe currency symbol
        LaunchedEffect(Unit) {
            currencyRepo.getSelectedCacheCurrency().collectLatest { result ->
                try {
                    currencySymbol = result.getDataOrError().symbol
                } catch (e: Exception) {
                    Log.e("ExpenseLogsPresenter", "Failed to get currency symbol", e)
                }
            }
        }

        // Update filter text when constraint changes
        LaunchedEffect(filterConstraint) {
            filterConstraint?.let {
                filterDateRangeText = "${dateRangeFormatter.format(it.dateRange.start, it.dateRange.end)} . ${it.sorting}"
            }
        }

        // Process events
        LaunchedEffect(events) {
            events.collect { event ->
                when (event) {
                    is ExpenseLogsEvent.FilterButtonClicked -> {
                        if (filterLimit != null && filterConstraint != null) {
                            showFilterDialogForInfo = ExpenseLogFilterInfo(
                                dateRangeLimit = filterLimit!!,
                                dateRangeSelected = filterConstraint!!.dateRange,
                                sorting = filterConstraint!!.sorting
                            )
                        }
                    }
                    is ExpenseLogsEvent.FilterDialogDismissed -> {
                        showFilterDialogForInfo = null
                    }
                    is ExpenseLogsEvent.FilterApplied -> {
                        filterConstraint = DateRangeSortingEnt(event.filter.dateRangeSelected, event.filter.sorting)
                        showFilterDialogForInfo = null
                    }
                    is ExpenseLogsEvent.SwipeSelectionCountChanged -> {
                        selectedCount = event.count
                        mode = if (event.count > 0) ExpenseMode.SELECTION else ExpenseMode.NORMAL
                    }
                    is ExpenseLogsEvent.ToggleSelection -> {
                        val id = event.id
                        val newSet = if (selectedIds.contains(id)) {
                            selectedIds - id
                        } else {
                            selectedIds + id
                        }
                        selectedIds = newSet
                        selectedCount = newSet.size
                        mode = if (newSet.isNotEmpty()) ExpenseMode.SELECTION else ExpenseMode.NORMAL
                    }
                    is ExpenseLogsEvent.ClearSelection -> {
                        selectedIds = emptySet()
                        selectedCount = 0
                        mode = ExpenseMode.NORMAL
                    }
                    is ExpenseLogsEvent.SelectionCloseClicked -> {
                        selectedIds = emptySet()
                        selectedCount = 0
                        mode = ExpenseMode.NORMAL
                    }
                    is ExpenseLogsEvent.DeleteButtonClicked -> {
                        showMultiDeleteConfirmCount = selectedCount
                    }
                    is ExpenseLogsEvent.MultiDeleteDismissed -> {
                        showMultiDeleteConfirmCount = null
                    }
                    is ExpenseLogsEvent.MultiDeleteConfirmed -> {
                        showMultiDeleteConfirmCount = null
                        val deleteItems = selectedIds.toList()
                        if (deleteItems.isNotEmpty()) {
                            withContext(Dispatchers.IO) {
                                expenseRepo.deleteAllExpense(deleteItems)
                            }
                            selectedIds = emptySet()
                            selectedCount = 0
                            mode = ExpenseMode.NORMAL
                        }
                    }
                    is ExpenseLogsEvent.SingleDeletePrepared -> {
                        pendingSingleDeleteId = event.id
                        showSingleDeleteConfirm = true
                    }
                    is ExpenseLogsEvent.SingleDeleteDismissed -> {
                        showSingleDeleteConfirm = false
                        pendingSingleDeleteId = null
                    }
                    is ExpenseLogsEvent.SingleDeleteConfirmed -> {
                        showSingleDeleteConfirm = false
                        pendingSingleDeleteId?.let { id ->
                            withContext(Dispatchers.IO) {
                                expenseRepo.deleteExpenseById(id)
                            }
                        }
                        pendingSingleDeleteId = null
                    }
                    is ExpenseLogsEvent.ShowItemDetail -> {
                        val item = event.item
                        val ent = withContext(Dispatchers.IO) {
                            expenseRepo.getExpense(item.expenseLog.id).awaitValueOrError()
                        }
                        val mapper = expenseDetailMapperFactory.create { currencySymbol }
                        showDetailDialogFor = mapper.map(ent)
                    }
                    is ExpenseLogsEvent.ShowDetailDialog -> {
                        showDetailDialogFor = event.detail
                    }
                    is ExpenseLogsEvent.DetailDialogDismissed -> {
                        showDetailDialogFor = null
                    }
                    else -> Unit
                }
            }
        }

        return ExpenseLogsState(
            mode = mode,
            selectedCount = selectedCount,
            selectedIds = selectedIds,
            isEmptyLogs = isEmptyLogs,
            filterDateRangeText = filterDateRangeText,
            isFilterEnabled = isFilterEnabled,
            showFilterDialogForInfo = showFilterDialogForInfo,
            showMultiDeleteConfirmCount = showMultiDeleteConfirmCount,
            showSingleDeleteConfirm = showSingleDeleteConfirm,
            showDetailDialogFor = showDetailDialogFor,
            filterConstraint = filterConstraint,
            currencySymbol = currencySymbol
        )
    }
}
