package com.arduia.expense.storage.repository

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.arduia.expense.data.Result
import com.arduia.expense.data.SettlementLine
import com.arduia.expense.data.SettlementSummary
import com.arduia.expense.data.SharedCostInput
import com.arduia.expense.data.SharedCostRepository
import com.arduia.expense.domain.ParticipantId
import com.arduia.expense.domain.SharedCost
import com.arduia.expense.domain.SharedCostId
import com.arduia.expense.storage.catchingResult
import com.arduia.expense.storage.db.SharedCostQueries
import com.arduia.expense.storage.mapping.toDomain
import com.arduia.expense.storage.mapping.toParticipantsJson
import com.arduia.expense.storage.mapping.toStrategyJson
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import kotlin.random.Random

class SqlDelightSharedCostRepository(
    private val queries: SharedCostQueries,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
) : SharedCostRepository {

    override suspend fun create(input: SharedCostInput): Result<SharedCost> = withContext(dispatcher) {
        catchingResult {
            val id = generateSharedCostId()
            val sharedCost = SharedCost(
                id = id,
                title = input.title,
                total = input.total,
                participants = input.participants,
                splitStrategy = input.splitStrategy,
                recordedAtEpochMillis = input.recordedAtEpochMillis,
            )

            persist(sharedCost)
            sharedCost
        }
    }

    override suspend fun getAll(): Result<List<SharedCost>> = withContext(dispatcher) {
        catchingResult {
            queries.selectAllSharedCosts().executeAsList().map { it.toDomain() }
        }
    }

    override suspend fun getById(id: SharedCostId): Result<SharedCost?> = withContext(dispatcher) {
        catchingResult {
            queries.selectSharedCostById(id.value).executeAsOneOrNull()?.toDomain()
        }
    }

    override suspend fun update(sharedCost: SharedCost): Result<Unit> = withContext(dispatcher) {
        catchingResult {
            persist(sharedCost)
            Unit
        }
    }

    override suspend fun delete(id: SharedCostId): Result<Unit> = withContext(dispatcher) {
        catchingResult {
            queries.deleteSharedCost(id.value)
            Unit
        }
    }

    override fun observeAll(): Flow<List<SharedCost>> =
        queries.selectAllSharedCosts()
            .asFlow()
            .mapToList(dispatcher)
            .map { rows -> rows.mapNotNull { runCatching { it.toDomain() }.getOrNull() } }

    override suspend fun getSettlement(sharedCostId: SharedCostId): Result<SettlementSummary> =
        withContext(dispatcher) {
            catchingResult {
                val sharedCost = queries.selectAllSharedCosts()
                    .executeAsList()
                    .find { it.id == sharedCostId.value }
                    ?.toDomain()
                    ?: throw IllegalArgumentException("SharedCost not found: ${sharedCostId.value}")

                val shares = sharedCost.shares()

                val lines = sharedCost.participants.map { participant ->
                    val owedAmount = shares[participant.id]
                        ?: throw IllegalStateException("No share calculated for participant ${participant.id}")

                    SettlementLine(participant, owedAmount)
                }

                SettlementSummary(sharedCostId, lines)
            }
        }

    private fun persist(sharedCost: SharedCost) {
        queries.insertSharedCost(
            id = sharedCost.id.value,
            title = sharedCost.title,
            total_cents = sharedCost.total.amount.valueInCents,
            currency_code = sharedCost.total.currency.code,
            recorded_at = sharedCost.recordedAtEpochMillis,
            participants_json = sharedCost.participants.toParticipantsJson(),
            custom_shares_json = sharedCost.splitStrategy.toStrategyJson(),
        )
    }

    private fun generateSharedCostId(): SharedCostId {
        val chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789"
        return SharedCostId(
            (1..16)
                .map { chars[Random.nextInt(chars.length)] }
                .joinToString("")
        )
    }
}
