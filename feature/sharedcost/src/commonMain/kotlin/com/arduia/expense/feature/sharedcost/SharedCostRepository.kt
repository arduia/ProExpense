package com.arduia.expense.feature.sharedcost

import com.arduia.expense.data.Result
import com.arduia.expense.domain.Amount
import com.arduia.expense.domain.CurrencyCode
import com.arduia.expense.domain.Participant
import com.arduia.expense.domain.SharedCost

data class SharedCostInput(
    val title: String,
    val totalAmount: Amount,
    val currency: CurrencyCode,
    val participants: List<Participant>,
    val recordedAtEpochMillis: Long,
    val splitMode: SplitMode = SplitMode.EVEN,
    val customShareCents: List<Long>? = null,
)

data class SettlementLine(
    val participant: Participant,
    val owedAmount: Amount,
)

data class SettlementSummary(
    val sharedCostId: String,
    val lines: List<SettlementLine>,
)

interface SharedCostRepository {
    suspend fun create(input: SharedCostInput): Result<SharedCost>

    suspend fun getAll(): Result<List<SharedCost>>

    suspend fun getSettlement(sharedCostId: String): Result<SettlementSummary>
}
