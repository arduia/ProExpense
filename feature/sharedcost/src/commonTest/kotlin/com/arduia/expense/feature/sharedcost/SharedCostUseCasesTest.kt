package com.arduia.expense.feature.sharedcost

import com.arduia.expense.data.Result
import com.arduia.expense.data.SettlementSummary
import com.arduia.expense.data.SharedCostInput
import com.arduia.expense.data.SharedCostRepository
import com.arduia.expense.domain.Amount
import com.arduia.expense.domain.CurrencyCode
import com.arduia.expense.domain.Money
import com.arduia.expense.domain.Participant
import com.arduia.expense.domain.ParticipantId
import com.arduia.expense.domain.SharedCost
import com.arduia.expense.domain.SharedCostId
import com.arduia.expense.domain.SplitStrategy
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

private fun sampleSharedCost(
    id: String = "sc1",
    title: String = "Dinner",
) = SharedCost(
    id = SharedCostId(id),
    title = title,
    total = Money(Amount(20_00), CurrencyCode("USD")),
    participants = listOf(Participant(ParticipantId("p1"), "Alex"), Participant(ParticipantId("p2"), "Bo")),
    splitStrategy = SplitStrategy.EqualSplit,
    recordedAtEpochMillis = 1_000L,
)

private class FakeSharedCostRepository(
    var createResult: Result<SharedCost>? = null,
    var updateResult: Result<Unit> = Result.Success(Unit),
) : SharedCostRepository {
    var lastCreateInput: SharedCostInput? = null
    var lastUpdate: SharedCost? = null

    override suspend fun create(input: SharedCostInput): Result<SharedCost> {
        lastCreateInput = input
        return createResult ?: Result.Success(
            SharedCost(
                id = SharedCostId("new"),
                title = input.title,
                total = input.total,
                participants = input.participants,
                splitStrategy = input.splitStrategy,
                recordedAtEpochMillis = input.recordedAtEpochMillis,
            ),
        )
    }

    override suspend fun getAll(): Result<List<SharedCost>> = Result.Success(emptyList())

    override suspend fun getById(id: SharedCostId): Result<SharedCost?> = Result.Success(null)

    override suspend fun update(sharedCost: SharedCost): Result<Unit> {
        lastUpdate = sharedCost
        return updateResult
    }

    var deletedId: SharedCostId? = null

    override suspend fun delete(id: SharedCostId): Result<Unit> {
        deletedId = id
        return Result.Success(Unit)
    }

    override suspend fun getSettlement(sharedCostId: SharedCostId): Result<SettlementSummary> = Result.Error("not implemented")

    override fun observeAll() = MutableStateFlow<List<SharedCost>>(emptyList()).asStateFlow()
}

private fun sampleInput(
    rawTotal: String = "20.00",
    mode: SplitMode = SplitMode.EQUAL,
    participantNames: List<String> = listOf("Alex", "Bo"),
    customShareRaws: List<String> = emptyList(),
) = SaveSharedCostInput(
    title = "Dinner",
    rawTotal = rawTotal,
    mode = mode,
    participantNames = participantNames,
    customShareRaws = customShareRaws,
)

class CreateSharedCostUseCaseTest {
    @Test
    fun invoke_createsEqualSplitWithParsedTotal() =
        runTest {
            val repo = FakeSharedCostRepository()
            val useCase = CreateSharedCostUseCase(repo, nowEpochMillis = { 1_000L })

            val result = useCase(sampleInput())

            assertTrue(result)
            assertEquals(
                2000L,
                repo.lastCreateInput
                    ?.total
                    ?.amount
                    ?.valueInCents,
            )
            assertEquals(SplitStrategy.EqualSplit, repo.lastCreateInput?.splitStrategy)
            assertEquals(2, repo.lastCreateInput?.participants?.size)
        }

    @Test
    fun invoke_buildsCustomSplitStoringSharesExactlyAsEnteredWithoutRebalancing() =
        runTest {
            val repo = FakeSharedCostRepository()
            val useCase = CreateSharedCostUseCase(repo, nowEpochMillis = { 1_000L })

            useCase(
                sampleInput(
                    rawTotal = "20.00",
                    mode = SplitMode.CUSTOM,
                    participantNames = listOf("Alex", "Bo"),
                    customShareRaws = listOf("5.00", "5.00"),
                ),
            )

            val strategy = repo.lastCreateInput?.splitStrategy
            assertTrue(strategy is SplitStrategy.CustomSplit)
            val shares = (strategy as SplitStrategy.CustomSplit).shares
            // Shares (5 + 5 = 10) don't sum to the $20 total — that's allowed; the total stays
            // authoritative and no participant's entered share is silently rebalanced.
            assertEquals(500L, shares[ParticipantId("alex-0-1000")]?.amount?.valueInCents)
            assertEquals(500L, shares[ParticipantId("bo-1-1000")]?.amount?.valueInCents)
        }

    @Test
    fun invoke_returnsFalseForUnparsableTotal() =
        runTest {
            val repo = FakeSharedCostRepository()
            val useCase = CreateSharedCostUseCase(repo, nowEpochMillis = { 1_000L })

            val result = useCase(sampleInput(rawTotal = "bad"))

            assertFalse(result)
            assertEquals(null, repo.lastCreateInput)
        }
}

class UpdateSharedCostUseCaseTest {
    @Test
    fun invoke_appliesNewTitleAndTotalToExisting() =
        runTest {
            val repo = FakeSharedCostRepository()
            val useCase = UpdateSharedCostUseCase(repo, nowEpochMillis = { 2_000L })
            val existing = sampleSharedCost()

            val result = useCase(existing, sampleInput(rawTotal = "30.00"))

            assertTrue(result)
            assertEquals(
                3000L,
                repo.lastUpdate
                    ?.total
                    ?.amount
                    ?.valueInCents,
            )
            assertEquals(SharedCostId("sc1"), repo.lastUpdate?.id)
        }

    @Test
    fun invoke_returnsFalseForUnparsableTotal() =
        runTest {
            val repo = FakeSharedCostRepository()
            val useCase = UpdateSharedCostUseCase(repo, nowEpochMillis = { 2_000L })

            val result = useCase(sampleSharedCost(), sampleInput(rawTotal = "bad"))

            assertFalse(result)
            assertEquals(null, repo.lastUpdate)
        }
}

class DeleteSharedCostUseCaseTest {
    @Test
    fun invoke_deletesSharedCostById() =
        runTest {
            val repo = FakeSharedCostRepository()
            val useCase = DeleteSharedCostUseCase(repo)

            useCase("sc1")

            assertEquals(SharedCostId("sc1"), repo.deletedId)
        }
}
