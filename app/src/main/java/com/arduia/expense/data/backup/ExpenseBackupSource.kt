package com.arduia.expense.data.backup

import com.arduia.backup.BackupSource
import com.arduia.expense.data.AccRepository
import com.arduia.expense.data.local.ExpenseEnt
import kotlinx.coroutines.flow.first

class ExpenseBackupSource (private val repo: AccRepository): BackupSource<ExpenseEnt>{
    override suspend fun write(item: ExpenseEnt) {
        repo.insertExpense(item)
    }

    override suspend fun writeAll(items: List<ExpenseEnt>) {
        repo.insertExpenseAll(items)
    }

    override suspend fun readAll(): List<ExpenseEnt> {
       return repo.getExpenseAll().first()
    }
}
