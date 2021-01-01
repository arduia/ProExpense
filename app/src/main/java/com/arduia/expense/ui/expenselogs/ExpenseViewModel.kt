package com.arduia.expense.ui.expenselogs

import androidx.hilt.lifecycle.ViewModelInject
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
import com.arduia.expense.ui.common.expense.ExpenseDetailUiModel
import com.arduia.expense.ui.common.filter.DateRangeSortingEnt
import com.arduia.expense.ui.common.filter.Sorting
import com.arduia.expense.ui.common.formatter.DateRangeFormatter
import com.arduia.expense.ui.expenselogs.swipe.SwipeItemState
import com.arduia.expense.ui.expenselogs.swipe.SwipeStateHolder
import com.arduia.expense.ui.home.ExpenseDetailUiModelMapperFactory
import com.arduia.mvvm.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.*


class ExpenseViewModel @ViewModelInject constructor(
    private val expenseEntToLogMapperFactory: ExpenseEntToLogVoMapperFactory,
    private val expenseRepo: ExpenseRepository,
    private val currencyRepo: CurrencyRepository,
    private val expenseDetailMapperFactory: ExpenseDetailUiModelMapperFactory,
    @StatisticDateRange private val dateRangeFormatter: DateRangeFormatter
) : ViewModel() {

    private var swipeStateHolder: SwipeStateHolder? = null

    private val _onRestoreSwipeState = EventLiveData<SwipeStateHolder>()
    val onRestoreSwipeState get() = _onRestoreSwipeState.asLiveData()

    private val _expenseLogMode = BaseLiveData<ExpenseMode>()
    val expenseLogMode get() = _expenseLogMode.asLiveData()

    private val _selectedCount = BaseLiveData<Int>()
    val selectedCount get() = _selectedCount.asLiveData()

    private val _onMultiDeleteConfirm = EventLiveData<Int>()
    val onMultiDeleteConfirm get() = _onMultiDeleteConfirm.asLiveData()

    private val _onSingleDeleteConfirm = EventLiveData<Unit>()
    val onSingleDeleteConfirm get() = _onSingleDeleteConfirm.asLiveData()

    private val _onFilterShow = EventLiveData<ExpenseLogFilterInfo>()
    val onFilterShow get() = _onFilterShow.asLiveData()

    private val filterLimit = BaseLiveData<DateRange>()
    private val filterConstraint = MutableLiveData<DateRangeSortingEnt>()

    private val _isEmptyExpenseCount = BaseLiveData<Boolean>()
    val isEmptyExpenseCount get() = _isEmptyExpenseCount.asLiveData()

    private val _onDetailShow = EventLiveData<ExpenseDetailUiModel>()
    val onDetailShow get() = _onDetailShow.asLiveData()


    val filterInfo
        get() = filterConstraint.switchMap {
            BaseLiveData(createFilterInfo(it))
        }

    val expenseList: LiveData<PagedList<ExpenseLogUiModel>> = filterConstraint.switchMap { filter ->
        return@switchMap createSourcePagingLiveData(filter)
    }

    val isCurrentListEmpty: LiveData<Boolean> = expenseList.switchMap {
        BaseLiveData(it.size <= 0)
    }

    private val mapper: Mapper<ExpenseEnt, ExpenseLogUiModel>

    private var currencySymbol = ""

    private var singleDeleteItemId: Int? = null

    init {
        observeCurrencySymbol()
        observeMaxAndMinDateRange()
        mapper = expenseEntToLogMapperFactory.create { currencySymbol }
        _expenseLogMode.value = ExpenseMode.NORMAL
        expenseRepo.getExpenseTotalCount()
            .flowOn(Dispatchers.IO)
            .onSuccess {
                _isEmptyExpenseCount post (it == 0)
            }
            .onError {
                _isEmptyExpenseCount post true
            }
            .launchIn(viewModelScope)
    }

    private fun observeMaxAndMinDateRange() {
        expenseRepo.getMaxAndMiniDateRange()
            .flowOn(Dispatchers.IO)
            .onSuccess {
                Timber.d("onSuccess $it")
                filterLimit post ExpenseDateRange(start = it.minDate, end = it.maxDate)
                val constraint = filterConstraint.value
                if (constraint != null) {
                    //If Already has Constraint
                    val minDateConstraint = constraint.dateRange.start
                    val maxConstraint = constraint.dateRange.end
                    val min = if (minDateConstraint < it.minDate) it.minDate else minDateConstraint
                    val max = if (maxConstraint > it.maxDate) it.maxDate else maxConstraint
                    if (min == 0L || max == 0L) {
                        filterConstraint post DateRangeSortingEnt(
                            ExpenseDateRange(Date().time, Date().time),
                            Sorting.DESC
                        )
                        return@onSuccess
                    }
                    filterConstraint post DateRangeSortingEnt(
                        ExpenseDateRange(min, max),
                        constraint.sorting
                    )
                } else {
                    //New Constraint
                    filterConstraint post DateRangeSortingEnt(
                        ExpenseDateRange(
                            it.minDate,
                            it.maxDate
                        )
                    )
                }
            }
            .onError {
                val defaultDateRange = getDefaultDateRange()
                filterLimit post defaultDateRange
                filterConstraint post DateRangeSortingEnt(dateRange = defaultDateRange)
            }
            .launchIn(viewModelScope)

    }

    private fun getDefaultDateRange(): ExpenseDateRange {
        return ExpenseDateRange(java.util.Date().time, java.util.Date().time)
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
        val sourceFactory =
            if (filter.sorting == Sorting.DESC) expenseRepo.getExpenseRangeDescSource(
                filter.dateRange.start,
                filter.dateRange.end,
                0,
                Int.MAX_VALUE
            )
            else expenseRepo.getExpenseRangeAscSource(
                filter.dateRange.start,
                filter.dateRange.end,
                0,
                Int.MAX_VALUE
            )

        return sourceFactory
            .map(mapper::map)
            .toLiveData(
                config = Config(
                    50,
                    maxSize = 100,
                    enablePlaceholders = false,
                    prefetchDistance = 10
                )
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
        this.filterConstraint set DateRangeSortingEnt(
            dateRangeEnt.dateRangeSelected,
            dateRangeEnt.sorting
        )
    }

    fun onFilterPrepare() {
        val constraint = filterConstraint.value ?: return
        val limit = filterLimit.value ?: return
        _onFilterShow post event(
            ExpenseLogFilterInfo(
                dateRangeLimit = limit,
                dateRangeSelected = constraint.dateRange,
                constraint.sorting
            )
        )
    }

    fun onDeletePrepared() {
        val itemCount = selectedCount.value ?: 0
        _onMultiDeleteConfirm post event(itemCount)
    }

    fun onSingleDeletePrepared(id: Int) {
        singleDeleteItemId = id
        _onSingleDeleteConfirm post EventUnit
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
            _onDetailShow post event(mapper.map(ent))
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

            if (_expenseLogMode.value != ExpenseMode.SELECTION) {
                _expenseLogMode post ExpenseMode.SELECTION
            }
            _selectedCount post selectCount

        } else {
            _expenseLogMode post ExpenseMode.NORMAL
            _selectedCount post 0
        }

    }

    fun onRestoreState() {
        val state = swipeStateHolder
        if (state != null) {
            _onRestoreSwipeState post event(state)
        }
    }

    override fun onCleared() {
        super.onCleared()
        swipeStateHolder = null
    }

}
