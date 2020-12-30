package com.arduia.expense.data.backup

import com.arduia.backup.BackupSource
import com.arduia.expense.data.ExpenseRepository
import com.arduia.expense.data.local.ExpenseEnt
import com.arduia.expense.model.awaitValueOrError
import com.arduia.expense.model.data
import com.arduia.expense.model.getDataOrError
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.single
import java.lang.Exception

class ExpenseBackupSource (private val repo: ExpenseRepository): BackupSource<ExpenseEnt>{
    override suspend fun write(item: ExpenseEnt) {
        repo.insertExpense(item)
    }

    override suspend fun writeAll(items: List<ExpenseEnt>) {
        repo.insertExpenseAll(items)
    }

    override suspend fun readAll(): List<ExpenseEnt> {
       return repo.getExpenseAllSync().getDataOrError()
    }

    override suspend fun totalCountAll(): Int {
        return repo.getExpenseTotalCountSync().getDataOrError()
    }
}
