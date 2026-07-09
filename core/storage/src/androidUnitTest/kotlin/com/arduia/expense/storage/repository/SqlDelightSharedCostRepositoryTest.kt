package com.arduia.expense.storage.repository

import com.arduia.expense.data.Result
import com.arduia.expense.data.SharedCostInput
import com.arduia.expense.domain.Amount
import com.arduia.expense.domain.CurrencyCode
import com.arduia.expense.domain.Money
import com.arduia.expense.domain.Participant
import com.arduia.expense.domain.ParticipantId
import com.arduia.expense.domain.RecordLink
import com.arduia.expense.domain.SplitStrategy
import com.arduia.expense.storage.db.ProExpenseDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

private fun sharedCostRepo(database: ProExpenseDatabase): SqlDelightSharedCostRepository {
    val financeRecordRepository =
        SqlDelightFinanceRecordRepository(
            queries = database.financeRecordQueries,
            eventQueries = database.eventQueries,
            dispatcher = Dispatchers.Unconfined,
        )
    return SqlDelightSharedCostRepository(database.sharedCostQueries, financeRecordRepository, Dispatchers.Unconfined)
}

class SqlDelightSharedCostRepositoryTest {
    private val home = CurrencyCode("USD")

    @Test
    fun create_then_getById_returnsSame() =
        runTest {
            val repo = sharedCostRepo(inMemoryDatabase())
            val input =
                SharedCostInput(
                    title = "Dinner",
                    total = Money(Amount(100_00), home),
                    participants =
                        listOf(
                            Participant(ParticipantId("p1"), "Alice"),
                            Participant(ParticipantId("p2"), "Bob"),
                        ),
                    splitStrategy = SplitStrategy.EqualSplit,
                    recordedAtEpochMillis = 1000,
                )

            val created = repo.create(input)
            assertTrue(created is Result.Success)
            val createdId = (created as Result.Success<*>).data.let { it as com.arduia.expense.domain.SharedCost }.id

            val fetched = repo.getById(createdId)
            assertTrue(fetched is Result.Success)
            val fetchedData = (fetched as Result.Success<*>).data as com.arduia.expense.domain.SharedCost?
            assertNotNull(fetchedData)
            assertEquals(createdId, fetchedData.id)
            assertEquals("Dinner", fetchedData.title)
        }

    @Test
    fun update_replacesFields() =
        runTest {
            val repo = sharedCostRepo(inMemoryDatabase())
            val input =
                SharedCostInput(
                    title = "Original",
                    total = Money(Amount(100_00), home),
                    participants = listOf(Participant(ParticipantId("p1"), "Alice")),
                    splitStrategy = SplitStrategy.EqualSplit,
                    recordedAtEpochMillis = 1000,
                )

            val created = repo.create(input)
            assertTrue(created is Result.Success)
            val createdSharedCost = (created as Result.Success<*>).data as com.arduia.expense.domain.SharedCost
            val id = createdSharedCost.id

            val updated = createdSharedCost.copy(title = "Updated")
            val updateResult = repo.update(updated)
            assertTrue(updateResult is Result.Success)

            val fetched = repo.getById(id)
            assertTrue(fetched is Result.Success)
            val fetchedData = (fetched as Result.Success<*>).data as com.arduia.expense.domain.SharedCost?
            assertEquals("Updated", fetchedData?.title)
        }

    @Test
    fun delete_removesRow() =
        runTest {
            val repo = sharedCostRepo(inMemoryDatabase())
            val input =
                SharedCostInput(
                    title = "Dinner",
                    total = Money(Amount(100_00), home),
                    participants = listOf(Participant(ParticipantId("p1"), "Alice")),
                    splitStrategy = SplitStrategy.EqualSplit,
                    recordedAtEpochMillis = 1000,
                )

            val created = repo.create(input)
            assertTrue(created is Result.Success)
            val createdSharedCost = (created as Result.Success<*>).data as com.arduia.expense.domain.SharedCost
            val id = createdSharedCost.id

            val deleteResult = repo.delete(id)
            assertTrue(deleteResult is Result.Success)

            val fetched = repo.getById(id)
            assertTrue(fetched is Result.Success)
            val fetchedData = (fetched as Result.Success<*>).data as com.arduia.expense.domain.SharedCost?
            assertNull(fetchedData)
        }

    @Test
    fun observeAll_emitsCreated() =
        runTest {
            val repo = sharedCostRepo(inMemoryDatabase())
            val input =
                SharedCostInput(
                    title = "Dinner",
                    total = Money(Amount(100_00), home),
                    participants = listOf(Participant(ParticipantId("p1"), "Alice")),
                    splitStrategy = SplitStrategy.EqualSplit,
                    recordedAtEpochMillis = 1000,
                )

            val created = repo.create(input)
            assertTrue(created is Result.Success)

            val all = repo.observeAll().first()
            assertEquals(1, all.size)
            assertEquals("Dinner", all.single().title)
        }

    @Test
    fun observeAll_skipsRowWithUnmappableParticipants_insteadOfThrowing() =
        runTest {
            val database = inMemoryDatabase()
            val repo = sharedCostRepo(database)
            val input =
                SharedCostInput(
                    title = "Dinner",
                    total = Money(Amount(100_00), home),
                    participants = listOf(Participant(ParticipantId("p1"), "Alice")),
                    splitStrategy = SplitStrategy.EqualSplit,
                    recordedAtEpochMillis = 1000,
                )
            repo.create(input)
            // An empty participants list fails SharedCost's "at least one participant" invariant on
            // decode — must not take down the whole flow for every other, valid row.
            database.sharedCostQueries.insertSharedCost(
                id = "sc-bad",
                title = "Corrupt",
                total_cents = 100_00,
                currency_code = "USD",
                recorded_at = 1000,
                participants_json = "[]",
                custom_shares_json = null,
            )

            val all = repo.observeAll().first()
            assertEquals(listOf("Dinner"), all.map { it.title })
        }

    @Test
    fun create_writesLinkedFinanceRecordForTheTotal() =
        runTest {
            val database = inMemoryDatabase()
            val repo = sharedCostRepo(database)
            val input =
                SharedCostInput(
                    title = "Dinner",
                    total = Money(Amount(100_00), home),
                    participants =
                        listOf(
                            Participant(ParticipantId("p1"), "Alice"),
                            Participant(ParticipantId("p2"), "Bob"),
                        ),
                    splitStrategy = SplitStrategy.EqualSplit,
                    recordedAtEpochMillis = 1000,
                )

            val created = repo.create(input)
            val id = (created as Result.Success<com.arduia.expense.domain.SharedCost>).data.id

            val record = database.financeRecordQueries.selectRecordById(id.value).executeAsOneOrNull()
            assertNotNull(record)
            assertEquals(100_00L, record.amount_cents)
            assertEquals(
                RecordLink.ToSharedCost(id),
                com.arduia.expense.storage.mapping
                    .toRecordLink(record.tag_type, record.tag_id),
            )
        }

    @Test
    fun update_updatesTheSameLinkedRecordRatherThanCreatingASecondOne() =
        runTest {
            val database = inMemoryDatabase()
            val repo = sharedCostRepo(database)
            val input =
                SharedCostInput(
                    title = "Dinner",
                    total = Money(Amount(100_00), home),
                    participants = listOf(Participant(ParticipantId("p1"), "Alice")),
                    splitStrategy = SplitStrategy.EqualSplit,
                    recordedAtEpochMillis = 1000,
                )
            val created = (repo.create(input) as Result.Success<com.arduia.expense.domain.SharedCost>).data

            repo.update(created.copy(total = Money(Amount(200_00), home)))

            assertEquals(
                1,
                database.financeRecordQueries
                    .selectAllRecords()
                    .executeAsList()
                    .size,
            )
            val record = database.financeRecordQueries.selectRecordById(created.id.value).executeAsOneOrNull()
            assertEquals(200_00L, record?.amount_cents)
        }

    @Test
    fun delete_alsoDeletesTheLinkedFinanceRecord() =
        runTest {
            val database = inMemoryDatabase()
            val repo = sharedCostRepo(database)
            val input =
                SharedCostInput(
                    title = "Dinner",
                    total = Money(Amount(100_00), home),
                    participants = listOf(Participant(ParticipantId("p1"), "Alice")),
                    splitStrategy = SplitStrategy.EqualSplit,
                    recordedAtEpochMillis = 1000,
                )
            val created = (repo.create(input) as Result.Success<com.arduia.expense.domain.SharedCost>).data

            repo.delete(created.id)

            assertNull(database.financeRecordQueries.selectRecordById(created.id.value).executeAsOneOrNull())
        }
}
