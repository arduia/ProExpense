package com.arduia.expense.ui.statistics

import com.arduia.expense.data.ExpenseRepository
import com.arduia.expense.di.StatisticDateRange
import com.arduia.expense.ui.common.formatter.DateRangeFormatter
import com.arduia.expense.ui.common.molecule.MoleculeViewModel
import com.arduia.expense.ui.common.molecule.Presenter
import com.arduia.expense.ui.statistics.molecule.StatisticEvent
import com.arduia.expense.ui.statistics.molecule.StatisticPresenter
import com.arduia.expense.ui.statistics.molecule.StatisticState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class StatisticsViewModel @Inject constructor(
    expenseRepo: ExpenseRepository,
    categoryAnalyzer: CategoryAnalyzer,
    @StatisticDateRange dateRangeFormatter: DateRangeFormatter
) : MoleculeViewModel<StatisticEvent, StatisticState>() {

    override val presenter: Presenter<StatisticEvent, StatisticState> =
        StatisticPresenter(expenseRepo, categoryAnalyzer, dateRangeFormatter)

}