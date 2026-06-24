package com.arduia.expense.storage.repository

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

            queries.insertSharedCost(
                id = id.value,
                title = sharedCost.title,
                total_cents = sharedCost.total.amount.valueInCents,
                currency_code = sharedCost.total.currency.code,
                recorded_at = sharedCost.recordedAtEpochMillis,
                participants_json = sharedCost.participants.toParticipantsJson(),
                custom_shares_json = sharedCost.splitStrategy.toStrategyJson(),
            )

            sharedCost
        }
    }

    override suspend fun getAll(): Result<List<SharedCost>> = withContext(dispatcher) {
        catchingResult {
            queries.selectAllSharedCosts().executeAsList().map { it.toDomain() }
        }
    }

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

    private fun generateSharedCostId(): SharedCostId {
        val chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789"
        return SharedCostId(
            (1..16)
                .map { chars[Random.nextInt(chars.length)] }
                .joinToString("")
        )
    }
}
