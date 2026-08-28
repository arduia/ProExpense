package com.arduia.expense.shell.di

import com.arduia.expense.shell.AddExpenseViewModel
import com.arduia.expense.shell.AppShellViewModel
import com.arduia.expense.shell.CategoriesViewModel
import com.arduia.expense.shell.DebtActions
import com.arduia.expense.shell.DebtViewModel
import com.arduia.expense.shell.EventBudgetActions
import com.arduia.expense.shell.EventBudgetViewModel
import com.arduia.expense.shell.HomeViewModel
import com.arduia.expense.shell.JournalDetailViewModel
import com.arduia.expense.shell.JournalViewModel
import com.arduia.expense.shell.MoreViewModel
import com.arduia.expense.shell.OnboardingViewModel
import com.arduia.expense.shell.PinEntryViewModel
import com.arduia.expense.shell.PinSetupViewModel
import com.arduia.expense.shell.ReportsViewModel
import com.arduia.expense.shell.SharedCostViewModel
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
        factory { OnboardingViewModel(completeOnboarding = get()) }
        factory { PinEntryViewModel(verifyPin = get(), pinAuthRepository = get()) }
        factory { PinSetupViewModel(setupPin = get(), disablePin = get()) }
        factory {
            JournalDetailViewModel(
                financeRecordRepository = get(),
                categoryRepository = get(),
                currencySettingsRepository = get(),
                deleteRecord = get(),
                updateRecordNote = get(),
            )
        }
        factory {
            MoreViewModel(
                themeRepository = get(),
                localeRepository = get(),
                currencySettingsRepository = get(),
                pinAuthRepository = get(),
                clearDataRepository = get(),
            )
        }
        factory {
            CategoriesViewModel(
                categoryRepository = get(),
                saveCategory = get(),
                deleteCategory = get(),
                reorderCategories = get(),
            )
        }
        factory {
            ReportsViewModel(
                financeRecordRepository = get(),
                categoryRepository = get(),
                currencySettingsRepository = get(),
                generateReportPeriod = get(),
            )
        }
        factory {
            EventBudgetViewModel(
                eventRepository = get(),
                financeRecordRepository = get(),
                categoryRepository = get(),
                currencySettingsRepository = get(),
                actions =
                    EventBudgetActions(
                        computeProgress = get(),
                        create = get(),
                        close = get(),
                        archive = get(),
                        delete = get(),
                    ),
            )
        }
        factory {
            DebtViewModel(
                debtRepository = get(),
                currencySettingsRepository = get(),
                actions =
                    DebtActions(
                        aggregate = get(),
                        create = get(),
                        settle = get(),
                        delete = get(),
                        checkConflict = get(),
                    ),
            )
        }
        factory {
            SharedCostViewModel(
                sharedCostRepository = get(),
                currencySettingsRepository = get(),
                createSharedCost = get(),
                deleteSharedCost = get(),
                archiveSharedCost = get(),
            )
        }
    }
