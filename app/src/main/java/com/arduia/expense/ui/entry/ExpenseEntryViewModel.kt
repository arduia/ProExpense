package com.arduia.expense.ui.entry

import com.arduia.core.arch.Mapper
import com.arduia.expense.data.CurrencyRepository
import com.arduia.expense.data.ExpenseRepository
import com.arduia.expense.data.local.ExpenseEnt
import com.arduia.expense.ui.common.category.ExpenseCategoryProvider
import com.arduia.expense.ui.common.molecule.MoleculeViewModel
import com.arduia.expense.ui.entry.molecule.ExpenseEntryEvent
import com.arduia.expense.ui.entry.molecule.ExpenseEntryPresenter
import com.arduia.expense.ui.entry.molecule.ExpenseEntryState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ExpenseEntryViewModel @Inject constructor(
    private val repo: ExpenseRepository,
    private val mapper: Mapper<ExpenseEnt, ExpenseUpdateDataUiModel>,
    private val currencyRepo: CurrencyRepository,
    private val categoryProvider: ExpenseCategoryProvider
) : MoleculeViewModel<ExpenseEntryEvent, ExpenseEntryState>() {

    override val presenter = ExpenseEntryPresenter(
        repo, currencyRepo, mapper, categoryProvider
    )
}
