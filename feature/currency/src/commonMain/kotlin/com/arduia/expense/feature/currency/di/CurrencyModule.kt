package com.arduia.expense.feature.currency.di

import com.arduia.expense.data.CurrencySettingsRepository
import com.arduia.expense.feature.currency.CurrencyRepository
import com.arduia.expense.feature.currency.DefaultCurrencyRepository
import com.arduia.expense.feature.currency.SaveHomeCurrencyUseCase
import org.koin.dsl.module

val currencyModule = module {
    single<CurrencyRepository> { DefaultCurrencyRepository(get<CurrencySettingsRepository>()) }
    factory { SaveHomeCurrencyUseCase(get()) }
}
