package com.arduia.expense.ui.expenselogs

import androidx.lifecycle.*
import androidx.paging.*
import com.arduia.core.arch.Mapper
import com.arduia.expense.data.CurrencyRepository
import com.arduia.expense.data.ExpenseRepository
import com.arduia.expense.data.local.ExpenseEnt
import com.arduia.expense.di.StatisticDateRange
import com.arduia.expense.domain.filter.DateRange
import com.arduia.expense.domain.filter.ExpenseDateRange
import com.arduia.expense.domain.filter.ExpenseLogFilterInfo
import com.arduia.expense.model.awaitValueOrError
import com.arduia.expense.model.getDataOrError
import com.arduia.expense.model.onError
import com.arduia.expense.model.onSuccess
import com.arduia.expense.ui.base.BaseViewModel
import com.arduia.expense.ui.common.expense.ExpenseDetailUiModel
import com.arduia.expense.ui.common.filter.DateRangeSortingEnt
import com.arduia.expense.ui.common.filter.Sorting
import com.arduia.expense.ui.common.formatter.DateRangeFormatter
import com.arduia.expense.ui.common.uimodel.DeleteInfoUiModel
import com.arduia.expense.ui.expenselogs.swipe.SwipeItemState
import com.arduia.expense.ui.expenselogs.swipe.SwipeStateHolder
import com.arduia.expense.ui.home.ExpenseDetailUiModelMapperFactory
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.*
import javax.inject.Inject

data class ExpenseLogsUiState(
    val expenseMode: ExpenseMode = ExpenseMode.NORMAL,
    val selectedCount: Int = 0,
    val isEmptyExpenseCount: Boolean = false,
    val filterInfo: String = "",
    val isCurrentListEmpty: Boolean = false
)

sealed class ExpenseLogsUiEffect {
    data class ShowDetail(val detail: ExpenseDetailUiModel) : ExpenseLogsUiEffect()
    data class ShowMultiDeleteConfirm(val count: Int) : ExpenseLogsUiEffect()
    object ShowSingleDeleteConfirm : ExpenseLogsUiEffect()
    data class ShowFilter(val filterInfo: ExpenseLogFilterInfo) : ExpenseLogsUiEffect()
    data class RestoreSwipeState(val state: SwipeStateHolder) : ExpenseLogsUiEffect()
}

@HiltViewModel
class ExpenseViewModel @Inject constructor(
    private val expenseEntToLogMapperFactory: ExpenseEntToLogVoMapperFactory,
    private val expenseRepo: ExpenseRepository,
    private val currencyRepo: CurrencyRepository,
    private val expenseDetailMapperFactory: ExpenseDetailUiModelMapperFactory,
    @StatisticDateRange private val dateRangeFormatter: DateRangeFormatter
) : BaseViewModel<ExpenseLogsUiState, ExpenseLogsUiEffect>(ExpenseLogsUiState()) {

    private var swipeStateHolder: SwipeStateHolder? = null

    private val filterLimit = MutableLiveData<DateRange>()
    private val filterConstraint = MutableLiveData<DateRangeSortingEnt>()

    val filterInfo: LiveData<String>
        get() = filterConstraint.switchMap { BaseLiveData(createFilterInfo(it)) }

    val expenseList: LiveData<PagedList<ExpenseLogUiModel>> = filterConstraint.switchMap { filter ->
        createSourcePagingLiveData(filter)
    }

    val isCurrentListEmpty: LiveData<Boolean> = expenseList.switchMap { BaseLiveData(it.size <= 0) }

    private val mapper: Mapper<ExpenseEnt, ExpenseLogUiModel>
    private var currencySymbol = ""
    private var singleDeleteItemId: Int? = null

    init {
        observeCurrencySymbol()
        observeMaxAndMinDateRange()
        mapper = expenseEntToLogMapperFactory.create { currencySymbol }
        expenseRepo.getExpenseTotalCount()
            .flowOn(Dispatchers.IO)
            .onSuccess { setState { copy(isEmptyExpenseCount = it == 0) } }
            .onError { setState { copy(isEmptyExpenseCount = true) } }
            .launchIn(viewModelScope)
    }

    private fun observeMaxAndMinDateRange() {
        expenseRepo.getMaxAndMiniDateRange()
            .flowOn(Dispatchers.IO)
            .onSuccess {
                filterLimit.postValue(ExpenseDateRange(start = it.minDate, end = it.maxDate))
                val constraint = filterConstraint.value
                if (constraint != null) {
                    val minDateConstraint = constraint.dateRange.start
                    val maxConstraint = constraint.dateRange.end
                    val min = if (minDateConstraint < it.minDate) it.minDate else minDateConstraint
                    val max = if (maxConstraint > it.maxDate) it.maxDate else maxConstraint
                    if (min == 0L || max == 0L) {
                        filterConstraint.postValue(DateRangeSortingEnt(ExpenseDateRange(Date().time, Date().time), Sorting.DESC))
                        return@onSuccess
                    }
                    if (min < max) {
                        filterConstraint.postValue(DateRangeSortingEnt(ExpenseDateRange(min, max), constraint.sorting))
                    } else {
                        filterConstraint.postValue(DateRangeSortingEnt(ExpenseDateRange(0, max), constraint.sorting))
                    }
                } else {
                    filterConstraint.postValue(DateRangeSortingEnt(ExpenseDateRange(it.minDate, it.maxDate)))
                }
            }
            .onError {
                val defaultDateRange = ExpenseDateRange(Date().time, Date().time)
                filterLimit.postValue(defaultDateRange)
                filterConstraint.postValue(DateRangeSortingEnt(dateRange = defaultDateRange))
            }
            .launchIn(viewModelScope)
    }

    private fun createFilterInfo(constraint: DateRangeSortingEnt): String {
        val dateRangeData = constraint.dateRange
        val dateRange = dateRangeFormatter.format(dateRangeData.start, dateRangeData.end)
        return "$dateRange . ${constraint.sorting}"
    }

    private fun observeCurrencySymbol() {
        currencyRepo.getSelectedCacheCurrency()
            .flowOn(Dispatchers.IO)
            .onEach {
                currencySymbol = it.getDataOrError().symbol
                expenseList.value?.dataSource?.invalidate()
            }
            .launchIn(viewModelScope)
    }

    private fun createSourcePagingLiveData(filter: DateRangeSortingEnt): LiveData<PagedList<ExpenseLogUiModel>> {
        val sourceFactory = if (filter.sorting == Sorting.DESC) {
            expenseRepo.getExpenseRangeDescSource(filter.dateRange.start, filter.dateRange.end, 0, Int.MAX_VALUE)
        } else {
            expenseRepo.getExpenseRangeAscSource(filter.dateRange.start, filter.dateRange.end, 0, Int.MAX_VALUE)
        }
        return sourceFactory.map(mapper::map).toLiveData(
            config = Config(50, maxSize = 100, enablePlaceholders = false, prefetchDistance = 10)
        )
    }

    fun storeState(state: SwipeStateHolder) {
        this.swipeStateHolder = state
        onSwipeStateChanged()
    }

    fun clearState() {
        this.swipeStateHolder?.clear()
        onRestoreState()
        onSwipeStateChanged()
    }

    fun setFilter(dateRangeEnt: ExpenseLogFilterInfo) {
        filterConstraint.value = DateRangeSortingEnt(dateRangeEnt.dateRangeSelected, dateRangeEnt.sorting)
    }

    fun onFilterPrepare() {
        val constraint = filterConstraint.value ?: return
        val limit = filterLimit.value ?: return
        sendEffect(ExpenseLogsUiEffect.ShowFilter(
            ExpenseLogFilterInfo(dateRangeLimit = limit, dateRangeSelected = constraint.dateRange, constraint.sorting)
        ))
    }

    fun onDeletePrepared() {
        val itemCount = uiState.value.selectedCount
        sendEffect(ExpenseLogsUiEffect.ShowMultiDeleteConfirm(itemCount))
    }

    fun onSingleDeletePrepared(id: Int) {
        singleDeleteItemId = id
        sendEffect(ExpenseLogsUiEffect.ShowSingleDeleteConfirm)
    }

    fun onMultiDeleteConfirmed() {
        viewModelScope.launch(Dispatchers.IO) {
            val deleteItems = swipeStateHolder?.getSelectIdList() ?: return@launch
            expenseRepo.deleteAllExpense(deleteItems)
            clearState()
        }
    }

    fun onShowItemDetail(item: ExpenseLogUiModel) {
        Timber.d("onShowItemDetail")
        if (item !is ExpenseLogUiModel.Log) return
        viewModelScope.launch(Dispatchers.IO) {
            val ent = expenseRepo.getExpense(item.expenseLog.id).awaitValueOrError()
            val mapper = expenseDetailMapperFactory.create { currencySymbol }
            sendEffect(ExpenseLogsUiEffect.ShowDetail(mapper.map(ent)))
        }
    }

    fun onSingleItemDeleteConfirmed() {
        val id = singleDeleteItemId ?: return
        viewModelScope.launch(Dispatchers.IO) {
            expenseRepo.deleteExpenseById(id)
            singleDeleteItemId = null
        }
    }

    private fun onSwipeStateChanged() {
        val selectCount = swipeStateHolder?.getCount(SwipeItemState.STATE_LOCK_START) ?: return
        if (selectCount > 0) {
            setState { copy(expenseMode = ExpenseMode.SELECTION, selectedCount = selectCount) }
        } else {
            setState { copy(expenseMode = ExpenseMode.NORMAL, selectedCount = 0) }
        }
    }

    fun onRestoreState() {
        val state = swipeStateHolder
        if (state != null) {
            sendEffect(ExpenseLogsUiEffect.RestoreSwipeState(state))
        }
    }

    override fun onCleared() {
        super.onCleared()
        swipeStateHolder = null
    }
}
