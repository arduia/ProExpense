package com.arduia.expense.di

import com.arduia.expense.data.Result
import com.arduia.expense.domain.CurrencyCode
import com.arduia.expense.feature.currency.CurrencyRepository
import com.arduia.expense.feature.history.DefaultHistoryRepository
import com.arduia.expense.feature.history.HistoryRepository
import org.koin.dsl.module

/**
 * Cross-feature wiring that can't live in `feature:history` or `feature:currency` themselves —
 * features must not depend on each other (design rule), so the composition root supplies the
 * home-currency lookup as a plain closure instead of a `CurrencyRepository` constructor param.
 */
val appModule = module {
    single<HistoryRepository> {
        val currencyRepository = get<CurrencyRepository>()
        DefaultHistoryRepository(
            financeRecordRepository = get(),
            homeCurrencyProvider = {
                when (val result = currencyRepository.getSettings()) {
                    is Result.Success -> result.data.homeCurrency
                    is Result.Error -> CurrencyCode("USD")
                }
            },
        )
    }
}
