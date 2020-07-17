package com.arduia.expense.ui.expense

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.arduia.expense.data.AccRepository
import com.arduia.expense.ui.mapping.ExpenseMapper

class ExpenseVMFactory(private val accMapper: ExpenseMapper,
                       private val accRepo: AccRepository
): ViewModelProvider.Factory{
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
       return modelClass.getConstructor(ExpenseMapper::class.java, AccRepository::class.java)
           .newInstance(accMapper, accRepo)
    }
}
