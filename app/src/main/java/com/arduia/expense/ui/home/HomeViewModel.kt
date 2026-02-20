package com.arduia.expense.ui.home

import com.arduia.expense.data.CurrencyRepository
import com.arduia.expense.data.ExpenseRepository
import com.arduia.expense.di.CurrencyDecimalFormat
import com.arduia.expense.di.MonthlyDateRange
import com.arduia.expense.ui.common.formatter.DateRangeFormatter
import com.arduia.expense.ui.common.molecule.MoleculeViewModel
import com.arduia.expense.ui.home.molecule.HomeEvent
import com.arduia.expense.ui.home.molecule.HomePresenter
import com.arduia.expense.ui.home.molecule.HomeState
import dagger.hilt.android.lifecycle.HiltViewModel
import java.text.NumberFormat
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    currencyRepository: CurrencyRepository,
    expenseVoMapperFactory: ExpenseUiModelMapperFactory,
    expenseDetailMapperFactory: ExpenseDetailUiModelMapperFactory,
    repo: ExpenseRepository,
    @CurrencyDecimalFormat currencyFormatter: NumberFormat,
    @MonthlyDateRange dateRangeFormatter: DateRangeFormatter,
    calculatorFactory: ExpenseRateCalculator.Factory
) : MoleculeViewModel<HomeEvent, HomeState>() {

    override val presenter = HomePresenter(
        currencyRepository,
        expenseVoMapperFactory,
        expenseDetailMapperFactory,
        repo,
        currencyFormatter,
        dateRangeFormatter,
        calculatorFactory
    )
}
