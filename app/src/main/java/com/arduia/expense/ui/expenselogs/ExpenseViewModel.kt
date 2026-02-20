package com.arduia.expense.ui.expenselogs

import androidx.lifecycle.*
import androidx.paging.*
import com.arduia.expense.data.CurrencyRepository
import com.arduia.expense.data.ExpenseRepository
import com.arduia.expense.ui.common.filter.DateRangeSortingEnt
import com.arduia.expense.ui.common.filter.Sorting
import com.arduia.expense.ui.common.formatter.DateRangeFormatter
import com.arduia.expense.ui.home.ExpenseDetailUiModelMapperFactory
import com.arduia.expense.ui.expenselogs.molecule.ExpenseLogsEvent
import com.arduia.expense.ui.expenselogs.molecule.ExpenseLogsPresenter
import com.arduia.expense.ui.expenselogs.molecule.ExpenseLogsState
import com.arduia.expense.ui.common.molecule.MoleculeViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class ExpenseViewModel @Inject constructor(
    private val expenseEntToLogMapperFactory: ExpenseEntToLogVoMapperFactory,
    private val expenseRepo: ExpenseRepository,
    private val currencyRepo: CurrencyRepository,
    private val expenseDetailMapperFactory: ExpenseDetailUiModelMapperFactory,
    @com.arduia.expense.di.StatisticDateRange private val dateRangeFormatter: DateRangeFormatter
) : MoleculeViewModel<ExpenseLogsEvent, ExpenseLogsState>() {

    override val presenter = ExpenseLogsPresenter(
        expenseRepo, currencyRepo, dateRangeFormatter, expenseDetailMapperFactory
    )

    /**
     * Paging flow derived reactively from the Presenter's state.
     * The filterConstraint and currencySymbol are computed inside the Presenter;
     * this ViewModel only builds the Pager from them and caches it in viewModelScope.
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    val expenseListPagingData: Flow<PagingData<ExpenseLogUiModel>> = state
        .map { it.filterConstraint to it.currencySymbol }
        .distinctUntilChanged()
        .filterNot { (constraint, _) -> constraint == null }
        .flatMapLatest { (filter, symbol) ->
            val constraint = filter!!
            Pager(
                config = PagingConfig(
                    pageSize = 50,
                    enablePlaceholders = false,
                    prefetchDistance = 10,
                    initialLoadSize = 100
                ),
                pagingSourceFactory = {
                    if (constraint.sorting == Sorting.DESC) {
                        expenseRepo.getExpenseRangeDescSource(
                            constraint.dateRange.start,
                            constraint.dateRange.end,
                            0,
                            Int.MAX_VALUE
                        )
                    } else {
                        expenseRepo.getExpenseRangeAscSource(
                            constraint.dateRange.start,
                            constraint.dateRange.end,
                            0,
                            Int.MAX_VALUE
                        )
                    }
                }
            ).flow.map { pagingData ->
                val mapper = expenseEntToLogMapperFactory.create { symbol }
                pagingData.map { mapper.map(it) }
            }
        }
        .cachedIn(viewModelScope)
}
