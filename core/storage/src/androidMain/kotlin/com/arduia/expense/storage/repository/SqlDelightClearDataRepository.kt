package com.arduia.expense.storage.repository

import com.arduia.expense.data.ClearDataRepository
import com.arduia.expense.data.Result
import com.arduia.expense.storage.catchingResult
import com.arduia.expense.storage.db.ProExpenseDatabase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SqlDelightClearDataRepository(
    private val database: ProExpenseDatabase,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
) : ClearDataRepository {

    override suspend fun clearExpenses(): Result<Unit> = withContext(dispatcher) {
        catchingResult {
            database.financeRecordQueries.deleteAllRecords()
            Unit
        }
    }

    override suspend fun clearEvents(): Result<Unit> = withContext(dispatcher) {
        catchingResult {
            database.eventQueries.deleteAllEvents()
            Unit
        }
    }

    override suspend fun clearDebts(): Result<Unit> = withContext(dispatcher) {
        catchingResult {
            database.debtQueries.deleteAllDebts()
            Unit
        }
    }

    override suspend fun clearSharedCosts(): Result<Unit> = withContext(dispatcher) {
        catchingResult {
            database.sharedCostQueries.deleteAllSharedCosts()
            Unit
        }
    }

    override suspend fun clearAll(): Result<Unit> = withContext(dispatcher) {
        catchingResult {
            database.financeRecordQueries.deleteAllRecords()
            database.eventQueries.deleteAllEvents()
            database.debtQueries.deleteAllDebts()
            database.sharedCostQueries.deleteAllSharedCosts()
            Unit
        }
    }
}
