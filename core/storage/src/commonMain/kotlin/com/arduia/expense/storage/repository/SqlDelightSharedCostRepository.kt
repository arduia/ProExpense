package com.arduia.expense.storage.repository

import com.arduia.expense.data.Result
import com.arduia.expense.data.SharedCostRepository
import com.arduia.expense.domain.SharedCost
import com.arduia.expense.storage.db.ProExpenseDatabase
import com.arduia.expense.storage.mapper.SharedCostJson
import com.arduia.expense.storage.mapper.toDomain
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

internal class SqlDelightSharedCostRepository(
    private val database: ProExpenseDatabase,
    dispatcher: CoroutineDispatcher = Dispatchers.Default,
) : SqlDelightRepository(dispatcher), SharedCostRepository {

    private val queries get() = database.sharedCostQueries

    override suspend fun getAll(): Result<List<SharedCost>> = execute {
        queries.selectAllSharedCosts().executeAsList().map { it.toDomain() }
    }

    override suspend fun upsert(sharedCost: SharedCost): Result<Unit> = execute {
        queries.insertSharedCost(
            id = sharedCost.id,
            title = sharedCost.title,
            total_cents = sharedCost.totalAmount.valueInCents,
            currency_code = sharedCost.currency.code,
            recorded_at = sharedCost.recordedAtEpochMillis,
            participants_json = SharedCostJson.encodeParticipants(sharedCost.participants),
            custom_shares_json = SharedCostJson.encodeShares(sharedCost.customShareCents),
        )
    }

    override suspend fun delete(id: String): Result<Unit> = execute {
        queries.deleteSharedCost(id)
    }
}
