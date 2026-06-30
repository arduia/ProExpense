package com.arduia.expense.feature.currency

import com.arduia.expense.data.Result
import com.arduia.expense.domain.CurrencyCode

class SaveHomeCurrencyUseCase(private val currencyRepository: CurrencyRepository) {
    suspend operator fun invoke(currencyCode: String): Result<Unit> =
        currencyRepository.setHomeCurrency(CurrencyCode(currencyCode))
}
