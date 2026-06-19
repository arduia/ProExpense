package com.arduia.expense.feature.sharedcost

import com.arduia.expense.data.Result
import com.arduia.expense.domain.Amount
import com.arduia.expense.domain.CurrencyCode
import com.arduia.expense.domain.Participant
import com.arduia.expense.domain.SharedCost
import com.arduia.expense.storage.InMemoryDataStore
import java.util.UUID

class InMemorySharedCostRepository(
    private val store: InMemoryDataStore,
) : SharedCostRepository {
    override suspend fun create(input: SharedCostInput): Result<SharedCost> {
        val cost = SharedCost(
            id = UUID.randomUUID().toString(),
            title = input.title,
            totalAmount = input.totalAmount,
            currency = input.currency,
            participants = input.participants,
            recordedAtEpochMillis = input.recordedAtEpochMillis,
        )
        store.insertSharedCost(cost)
        return Result.Success(cost)
    }

    override suspend fun getAll(): Result<List<SharedCost>> = Result.Success(store.allSharedCosts())

    override suspend fun getSettlement(sharedCostId: String): Result<SettlementSummary> {
        val cost = store.allSharedCosts().firstOrNull { it.id == sharedCostId }
            ?: return Result.Error("Shared cost not found")
        val shares = calculateEvenSplit(cost.totalAmount.valueInCents, cost.participants.size)
        val lines = cost.participants.zip(shares).map { (participant, cents) ->
            SettlementLine(participant = participant, owedAmount = Amount(cents))
        }
        return Result.Success(SettlementSummary(sharedCostId = sharedCostId, lines = lines))
    }
}
