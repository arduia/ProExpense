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
import com.arduia.expense.ui.common.expense.ExpenseDetailUiModel
import com.arduia.expense.ui.common.filter.DateRangeSortingEnt
import com.arduia.expense.ui.common.filter.Sorting
import com.arduia.expense.ui.common.formatter.DateRangeFormatter
import com.arduia.expense.ui.expenselogs.swipe.SwipeItemState
import com.arduia.expense.ui.expenselogs.swipe.SwipeStateHolder
import com.arduia.expense.ui.home.ExpenseDetailUiModelMapperFactory
import com.arduia.expense.ui.expenselogs.molecule.ExpenseLogsEvent
import com.arduia.expense.ui.expenselogs.molecule.ExpenseLogsPresenter
import com.arduia.expense.ui.expenselogs.molecule.ExpenseLogsState
import com.arduia.expense.ui.common.molecule.MoleculeViewModel
import com.arduia.expense.ui.common.molecule.Presenter
import com.arduia.mvvm.EventLiveData
import com.arduia.mvvm.Event
import com.arduia.mvvm.BaseLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.*
import javax.inject.Inject

@HiltViewModel
class ExpenseViewModel @Inject constructor(
    private val expenseEntToLogMapperFactory: ExpenseEntToLogVoMapperFactory,
    private val expenseRepo: ExpenseRepository,
    private val currencyRepo: CurrencyRepository,
    private val expenseDetailMapperFactory: ExpenseDetailUiModelMapperFactory,
    @com.arduia.expense.di.StatisticDateRange private val dateRangeFormatter: DateRangeFormatter
) : MoleculeViewModel<ExpenseLogsEvent, ExpenseLogsState>() {

    override val presenter = ExpenseLogsPresenter(expenseRepo, dateRangeFormatter) { constraint ->
        filterConstraint.postValue(constraint)
    }

    private var swipeStateHolder: SwipeStateHolder? = null

    private val _onRestoreSwipeState = EventLiveData<SwipeStateHolder>()
    val onRestoreSwipeState get() = _onRestoreSwipeState.asLiveData()

    private val filterConstraint = MutableLiveData<DateRangeSortingEnt>()

    val expenseList: LiveData<PagedList<ExpenseLogUiModel>> = filterConstraint.switchMap { filter ->
        return@switchMap createSourcePagingLiveData(filter)
    }

    val isCurrentListEmpty: LiveData<Boolean> = expenseList.switchMap {
        BaseLiveData(it.size <= 0)
    }

    private val mapper: Mapper<ExpenseEnt, ExpenseLogUiModel>

    private var currencySymbol = ""

    init {
        observeCurrencySymbol()
        mapper = expenseEntToLogMapperFactory.create { currencySymbol }
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
        val selectCount = swipeStateHolder?.getCount(SwipeItemState.STATE_LOCK_START) ?: return
        take(ExpenseLogsEvent.SwipeSelectionCountChanged(selectCount))
    }

    fun clearState() {
        this.swipeStateHolder?.clear()
        onRestoreState()
        take(ExpenseLogsEvent.SwipeSelectionCountChanged(0))
    }

    fun onMultiDeleteConfirmed() {
        viewModelScope.launch(Dispatchers.IO) {
            val deleteItems = swipeStateHolder?.getSelectIdList() ?: return@launch
            expenseRepo.deleteAllExpense(deleteItems)
            clearState()
            take(ExpenseLogsEvent.MultiDeleteConfirmed)
        }
    }

    fun onShowItemDetail(item: ExpenseLogUiModel) {
        Timber.d("onShowItemDetail")
        if (item !is ExpenseLogUiModel.Log) return
        viewModelScope.launch(Dispatchers.IO) {
            val ent = expenseRepo.getExpense(item.expenseLog.id).awaitValueOrError()
            val mapper = expenseDetailMapperFactory.create { currencySymbol }
            take(ExpenseLogsEvent.ShowDetailDialog(mapper.map(ent)))
        }
    }

    fun onSingleItemDeleteConfirmed(id: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            expenseRepo.deleteExpenseById(id)
            take(ExpenseLogsEvent.SingleDeleteConfirmed)
        }
    }

    fun onRestoreState() {
        val state = swipeStateHolder
        if (state != null) {
            _onRestoreSwipeState.postValue(Event(state))
        }
    }

    override fun onCleared() {
        super.onCleared()
        swipeStateHolder = null
    }

}
