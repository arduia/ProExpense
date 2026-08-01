package com.arduia.expense.shell.di

import com.arduia.expense.shell.home.HomeDataSources
import com.arduia.expense.shell.home.HomeViewModel
import org.koin.dsl.module

/**
 * App-shell bindings — the cross-feature screen state both platform shells render from. Composed
 * on top of every `feature:*` module, so it is always registered last.
 */
val shellModule =
    module {
        factory { HomeDataSources(get(), get(), get(), get(), get(), get(), get(), get()) }
        factory { HomeViewModel(get(), get()) }
    }
