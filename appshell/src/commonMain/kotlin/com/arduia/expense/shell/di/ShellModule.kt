package com.arduia.expense.shell.di

import com.arduia.expense.shell.AddExpenseViewModel
import com.arduia.expense.shell.AppShellViewModel
import com.arduia.expense.shell.HomeViewModel
import com.arduia.expense.shell.JournalViewModel
import org.koin.dsl.module

/**
 * Shell-level presentation bindings shared by both platform shells. ViewModels are `factory`, not
 * `single` — each host owns its instance's lifecycle and calls `onCleared()` when it goes away.
 */
val shellModule =
    module {
        factory { AppShellViewModel(getOnboardingStatus = get(), pinAuthRepository = get()) }
        factory {
            HomeViewModel(
                financeRecordRepository = get(),
                categoryRepository = get(),
                profileRepository = get(),
                budgetRepository = get(),
                currencySettingsRepository = get(),
            )
        }
        factory {
            AddExpenseViewModel(
                logExpense = get(),
                categoryRepository = get(),
                currencySettingsRepository = get(),
            )
        }
        factory {
            JournalViewModel(
                financeRecordRepository = get(),
                categoryRepository = get(),
                currencySettingsRepository = get(),
            )
        }
    }
