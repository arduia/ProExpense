package com.arduia.expense.ui.expense

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import androidx.paging.*
import com.arduia.core.arch.Mapper
import com.arduia.expense.data.CurrencyRepository
import com.arduia.expense.data.ExpenseRepository
import com.arduia.expense.data.local.ExpenseEnt
import com.arduia.expense.model.awaitValueOrError
import com.arduia.expense.model.getDataOrError
import com.arduia.expense.ui.common.filter.DateRangeSortingEnt
import com.arduia.expense.ui.common.filter.RangeSortingFilterEnt
import com.arduia.expense.ui.common.filter.Sorting
import com.arduia.expense.ui.common.formatter.DateRangeFormatter
import com.arduia.expense.ui.expense.swipe.SwipeItemState
import com.arduia.expense.ui.expense.swipe.SwipeStateHolder
import com.arduia.expense.ui.home.ExpenseDetailMapperFactory
import com.arduia.expense.ui.mapping.ExpenseEntToLogVoMapperFactory
import com.arduia.expense.ui.vto.ExpenseDetailsVto
import com.arduia.mvvm.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import timber.log.Timber


class ExpenseViewModel @ViewModelInject constructor(
    private val expenseEntToLogMapperFactory: ExpenseEntToLogVoMapperFactory,
    private val expenseRepo: ExpenseRepository,
    private val currencyRepo: CurrencyRepository,
    private val expenseDetailMapperFactory: ExpenseDetailMapperFactory,
    private val dateRangeFormatter: DateRangeFormatter
) : ViewModel() {

    private var swipeStateHolder: SwipeStateHolder? = null

    private val _onRestoreSwipeState = EventLiveData<SwipeStateHolder>()
    val onRestoreSwipeState get() = _onRestoreSwipeState.asLiveData()

    private val _expenseLogMode = BaseLiveData<ExpenseMode>()
    val expenseLogMode get() = _expenseLogMode.asLiveData()

    private val _selectedCount = BaseLiveData<Int>()
    val selectedCount get() = _selectedCount.asLiveData()

    private val _onMultiDeleteConfirm = EventLiveData<Unit>()
    val onMultiDeleteConfirm get() = _onMultiDeleteConfirm.asLiveData()

    private val _onSingleDeleteConfirm = EventLiveData<Unit>()
    val onSingleDeleteConfirm get() = _onSingleDeleteConfirm.asLiveData()

    private val _onFilterShow = EventLiveData<RangeSortingFilterEnt>()
    val onFilterShow get() = _onFilterShow.asLiveData()

    private val filterConstraint = BaseLiveData<DateRangeSortingEnt>()
    private lateinit var filterLimit: DateRangeSortingEnt

    private val _onDetailShow = EventLiveData<ExpenseDetailsVto>()
    val onDetailShow get() = _onDetailShow.asLiveData()

    val filterInfo get() = filterConstraint.switchMap {
        BaseLiveData(createFilterInfo(it))
    }

    val expenseList: LiveData<PagedList<ExpenseLogVo>> = filterConstraint.switchMap { filter ->
        return@switchMap createSourcePagingLiveData(filter)
    }

    private val mapper: Mapper<ExpenseEnt, ExpenseLogVo>

    private var currencySymbol = ""

    private var singleDeleteItemId: Int? = null

    init {
        observeCurrencySymbol()

        mapper = expenseEntToLogMapperFactory.create { currencySymbol }
        _expenseLogMode.value = ExpenseMode.NORMAL

        viewModelScope.launch(Dispatchers.IO) {
            val dateRecent = expenseRepo.getMostRecentDateSync().getDataOrError()
            val dateLatest = expenseRepo.getMostLatestDateSync().getDataOrError()
            filterConstraint post DateRangeSortingEnt(dateRecent, dateLatest, Sorting.ASC)
            filterLimit = DateRangeSortingEnt(dateRecent, dateLatest)
        }
    }

    private fun createFilterInfo(constraint: DateRangeSortingEnt): String{
        val dateRange = dateRangeFormatter.format(constraint.start, constraint.end)
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

    private fun createSourcePagingLiveData(filter: DateRangeSortingEnt): LiveData<PagedList<ExpenseLogVo>> {
        val sourceFactory =
            if (filter.sorting == Sorting.DESC) expenseRepo.getExpenseRangeDescSource(
                filter.start,
                filter.end,
                0,
                Int.MAX_VALUE
            )
            else expenseRepo.getExpenseRangeAscSource(filter.start, filter.end, 0, Int.MAX_VALUE)

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

    fun setFilter(dateRangeEnt: DateRangeSortingEnt) {
        this.filterConstraint post dateRangeEnt
    }

    fun onFilterPrepare() {
        val constraint = filterConstraint.value ?: return
        _onFilterShow post event(RangeSortingFilterEnt(filter = constraint, limit = filterLimit))
    }

    fun onDeletePrepared() {
        _onMultiDeleteConfirm post EventUnit
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

    fun onShowItemDetail(item: ExpenseLogVo) {
        Timber.d("onShowItemDetail")
        if (item !is ExpenseLogVo.Log) return
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
