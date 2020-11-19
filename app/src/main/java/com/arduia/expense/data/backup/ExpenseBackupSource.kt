package com.arduia.expense.data.backup

import com.arduia.backup.BackupSource
import com.arduia.expense.data.ExpenseRepository
import com.arduia.expense.data.local.ExpenseEnt
import com.arduia.expense.model.awaitValueOrError
import com.arduia.expense.model.data
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.single
import java.lang.Exception

class ExpenseBackupSource (private val repo: ExpenseRepository): BackupSource<ExpenseEnt>{
    override suspend fun writeSingleItem(item: ExpenseEnt) {
        repo.insertExpense(item)
    }

    override suspend fun writeAllItem(items: List<ExpenseEnt>) {
        repo.insertExpenseAll(items)
    }

    override suspend fun readAllItem(): List<ExpenseEnt> {
       return repo.getExpenseAll().awaitValueOrError()
    }
}
