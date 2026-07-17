package com.arduia.expense.data

import com.arduia.expense.domain.Money
import com.arduia.expense.domain.Participant
import com.arduia.expense.domain.SharedCost
import com.arduia.expense.domain.SharedCostId
import com.arduia.expense.domain.SplitStrategy
import kotlinx.coroutines.flow.Flow

data class SharedCostInput(
    val title: String,
    val total: Money,
    val participants: List<Participant>,
    val splitStrategy: SplitStrategy = SplitStrategy.EqualSplit,
    val recordedAtEpochMillis: Long,
    val recordAsTransaction: Boolean = false,
)

data class SettlementLine(
    val participant: Participant,
    val owedAmount: Money,
)

data class SettlementSummary(
    val sharedCostId: SharedCostId,
    val lines: List<SettlementLine>,
)

interface SharedCostRepository {
    suspend fun create(input: SharedCostInput): Result<SharedCost>

    suspend fun getAll(): Result<List<SharedCost>>

    suspend fun getById(id: SharedCostId): Result<SharedCost?>

    suspend fun update(sharedCost: SharedCost): Result<Unit>

    suspend fun delete(id: SharedCostId): Result<Unit>

    suspend fun archive(id: SharedCostId): Result<Unit>

    suspend fun getSettlement(sharedCostId: SharedCostId): Result<SettlementSummary>

    fun observeAll(): Flow<List<SharedCost>>
}
