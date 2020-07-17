package com.arduia.expense.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.arduia.expense.data.AccRepository
import com.arduia.expense.ui.mapping.ExpenseMapper

class HomeViewModelFactory (
    private val expenseMapper: ExpenseMapper,
    private val accRepo: AccRepository,
    private val rateCalculator: ExpenseRateCalculator
): ViewModelProvider.Factory{

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return modelClass.getConstructor(ExpenseMapper::class.java,
            AccRepository::class.java,
            ExpenseRateCalculator::class.java).newInstance(expenseMapper, accRepo, rateCalculator)
    }
}
