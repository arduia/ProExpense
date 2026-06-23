package com.arduia.expense.feature.sharedcost

import com.arduia.expense.data.Result
import com.arduia.expense.domain.Money
import com.arduia.expense.domain.Participant
import com.arduia.expense.domain.SharedCost
import com.arduia.expense.domain.SharedCostId
import com.arduia.expense.domain.SplitStrategy

data class SharedCostInput(
    val title: String,
    val total: Money,
    val participants: List<Participant>,
    val splitStrategy: SplitStrategy = SplitStrategy.EqualSplit,
    val recordedAtEpochMillis: Long,
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

    suspend fun getSettlement(sharedCostId: SharedCostId): Result<SettlementSummary>
}
