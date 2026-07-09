package com.arduia.expense.feature.currency

import com.arduia.expense.data.Result
import com.arduia.expense.domain.CurrencyCode
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class SaveHomeCurrencyUseCaseTest {
    private class FakeCurrencyRepository(
        var settings: CurrencySettings = CurrencySettings(CurrencyCode("USD")),
        var setHomeCurrencyError: String? = null,
    ) : CurrencyRepository {
        override suspend fun getSettings(): Result<CurrencySettings> = Result.Success(settings)

        override suspend fun setHomeCurrency(currency: CurrencyCode): Result<Unit> {
            setHomeCurrencyError?.let { return Result.Error(it) }
            settings = settings.copy(homeCurrency = currency)
            return Result.Success(Unit)
        }
    }

    @Test
    fun invoke_storesHomeCurrencyCode() =
        runTest {
            val repo = FakeCurrencyRepository()
            val useCase = SaveHomeCurrencyUseCase(repo)

            val result = useCase("JPY")

            assertIs<Result.Success<Unit>>(result)
            assertEquals("JPY", repo.settings.homeCurrency.code)
        }

    @Test
    fun invoke_propagatesRepositoryError() =
        runTest {
            val repo = FakeCurrencyRepository(setHomeCurrencyError = "storage failure")
            val useCase = SaveHomeCurrencyUseCase(repo)

            val result = useCase("JPY")

            assertIs<Result.Error>(result)
            assertEquals("storage failure", (result as Result.Error).message)
        }
}
