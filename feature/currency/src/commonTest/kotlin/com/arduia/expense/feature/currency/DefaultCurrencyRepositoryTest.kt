package com.arduia.expense.feature.currency

import com.arduia.expense.data.CurrencySettingsRepository
import com.arduia.expense.data.Result
import com.arduia.expense.domain.CurrencyCode
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class DefaultCurrencyRepositoryTest {
    private class FakeCurrencySettingsRepository(
        var current: CurrencyCode? = null,
    ) : CurrencySettingsRepository {
        override suspend fun getHomeCurrency(): Result<CurrencyCode?> = Result.Success(current)

        override suspend fun setHomeCurrency(currency: CurrencyCode): Result<Unit> {
            current = currency
            return Result.Success(Unit)
        }
    }

    @Test
    fun getSettings_fallsBackToDefaultWhenUnset() =
        runTest {
            val repo = DefaultCurrencyRepository(FakeCurrencySettingsRepository(current = null))

            val result = repo.getSettings()

            assertTrue(result is Result.Success)
            assertEquals("USD", result.data.homeCurrency.code)
        }

    @Test
    fun setThenGet_returnsStoredHomeCurrency() =
        runTest {
            val fake = FakeCurrencySettingsRepository()
            val repo = DefaultCurrencyRepository(fake)

            repo.setHomeCurrency(CurrencyCode("JPY"))
            val result = repo.getSettings()

            assertTrue(result is Result.Success)
            assertEquals("JPY", result.data.homeCurrency.code)
        }

    @Test
    fun getSettings_propagatesError() =
        runTest {
            val repo =
                DefaultCurrencyRepository(
                    object : CurrencySettingsRepository {
                        override suspend fun getHomeCurrency(): Result<CurrencyCode?> = Result.Error("boom")

                        override suspend fun setHomeCurrency(currency: CurrencyCode): Result<Unit> = Result.Success(Unit)
                    },
                )

            val result = repo.getSettings()

            assertTrue(result is Result.Error)
            assertEquals("boom", result.message)
        }
}
