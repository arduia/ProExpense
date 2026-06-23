package com.arduia.expense.storage.repository

import com.arduia.expense.domain.Amount
import com.arduia.expense.domain.CurrencyCode
import com.arduia.expense.domain.Participant
import com.arduia.expense.domain.SharedCost
import com.arduia.expense.storage.getOrFail
import com.arduia.expense.storage.newTestDatabase
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class SqlDelightSharedCostRepositoryTest {

    private val database = newTestDatabase()
    private val repository = SqlDelightSharedCostRepository(database, UnconfinedTestDispatcher())

    private fun split(
        id: String,
        recordedAt: Long,
        participants: List<Participant> = listOf(Participant("p1", "Aiko"), Participant("p2", "Ben")),
        shares: List<Long>? = null,
    ) = SharedCost(
        id = id,
        title = "Dinner",
        totalAmount = Amount(24_000),
        currency = CurrencyCode("USD"),
        participants = participants,
        recordedAtEpochMillis = recordedAt,
        customShareCents = shares,
    )

    // Rule: a saved split reads back field-for-field, including participants and custom shares.
    @Test
    fun upsert_then_getAll_roundTrips() = runTest {
        val saved = split(
            id = "s1",
            recordedAt = 100,
            participants = listOf(Participant("p1", "Aiko"), Participant("p2", "Ben")),
            shares = listOf(14_000, 10_000),
        )
        repository.upsert(saved).getOrFail()

        assertEquals(saved, repository.getAll().getOrFail().single())
    }

    // Rule: participant names with quotes/commas/backslashes survive the JSON round-trip (G3).
    @Test
    fun participantNamesWithSpecialChars_roundTrip() = runTest {
        val tricky = listOf(
            Participant("p1", "O\"Brien, Jr."),
            Participant("p2", "back\\slash"),
        )
        repository.upsert(split(id = "s1", recordedAt = 1, participants = tricky)).getOrFail()

        assertEquals(tricky, repository.getAll().getOrFail().single().participants)
    }

    // Rule: getAll is ordered by recorded_at descending.
    @Test
    fun getAll_ordersByRecordedAtDescending() = runTest {
        repository.upsert(split(id = "old", recordedAt = 100)).getOrFail()
        repository.upsert(split(id = "new", recordedAt = 300)).getOrFail()

        assertEquals(listOf("new", "old"), repository.getAll().getOrFail().map { it.id })
    }

    // Rule: delete removes the targeted split.
    @Test
    fun delete_removesSplit() = runTest {
        repository.upsert(split(id = "s1", recordedAt = 1)).getOrFail()
        repository.delete("s1").getOrFail()

        assertEquals(emptyList(), repository.getAll().getOrFail())
    }
}
