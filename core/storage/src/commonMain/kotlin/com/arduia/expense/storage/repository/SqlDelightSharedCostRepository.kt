package com.arduia.expense.storage.repository

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.arduia.expense.data.FinanceRecordRepository
import com.arduia.expense.data.Result
import com.arduia.expense.data.SettlementLine
import com.arduia.expense.data.SettlementSummary
import com.arduia.expense.data.SharedCostInput
import com.arduia.expense.data.SharedCostRepository
import com.arduia.expense.domain.CategoryId
import com.arduia.expense.domain.FinanceRecord
import com.arduia.expense.domain.ParticipantId
import com.arduia.expense.domain.RecordId
import com.arduia.expense.domain.RecordLink
import com.arduia.expense.domain.RecordType
import com.arduia.expense.domain.SharedCost
import com.arduia.expense.domain.SharedCostId
import com.arduia.expense.storage.catchingResult
import com.arduia.expense.storage.db.SharedCostQueries
import com.arduia.expense.storage.mapping.toDomain
import com.arduia.expense.storage.mapping.toParticipantsJson
import com.arduia.expense.storage.mapping.toStrategyJson
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import kotlin.random.Random

private const val SHARED_COST_DEFAULT_CATEGORY_ID = "shopping"

class SqlDelightSharedCostRepository(
    private val queries: SharedCostQueries,
    private val financeRecordRepository: FinanceRecordRepository,
    private val dispatcher: CoroutineDispatcher,
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
            financeRecordRepository.upsert(sharedCost.toFinanceRecord())
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
            // Same RecordId as create() — this updates the existing linked record in place
            // rather than creating a second one.
            financeRecordRepository.upsert(sharedCost.toFinanceRecord())
            Unit
        }
    }

    override suspend fun delete(id: SharedCostId): Result<Unit> = withContext(dispatcher) {
        catchingResult {
            queries.deleteSharedCost(id.value)
            // Deletion is atomic across both the split and its linked FinanceRecord (US-SHC-5).
            financeRecordRepository.delete(RecordId(id.value))
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

    /**
     * The shared cost's total is the single [FinanceRecord] the user's own spending history sees
     * (US-SHC-4) — reuses the [SharedCostId] string as the [RecordId] so create/update always
     * target the same record instead of accumulating duplicates.
     */
    private fun SharedCost.toFinanceRecord(): FinanceRecord = FinanceRecord(
        id = RecordId(id.value),
        money = total,
        homeCurrencyMoney = total,
        categoryId = CategoryId(SHARED_COST_DEFAULT_CATEGORY_ID),
        type = RecordType.EXPENSE,
        note = title,
        recordedAtEpochMillis = recordedAtEpochMillis,
        link = RecordLink.ToSharedCost(id),
    )

    private fun generateSharedCostId(): SharedCostId {
        val chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789"
        return SharedCostId(
            (1..16)
                .map { chars[Random.nextInt(chars.length)] }
                .joinToString("")
        )
    }
}
