package com.arduia.expense.ui.entry

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.arduia.expense.data.AccRepository
import com.arduia.expense.ui.mapping.ExpenseMapper

class ExpenseEntryVMFactory(private val repository: AccRepository,
                            private val mapper: ExpenseMapper): ViewModelProvider.Factory{
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return modelClass.getConstructor(AccRepository::class.java, ExpenseMapper::class.java)
            .newInstance(repository, mapper)
    }
}
