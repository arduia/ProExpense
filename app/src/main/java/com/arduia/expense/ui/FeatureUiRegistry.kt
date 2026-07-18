package com.arduia.expense.ui

import com.arduia.expense.feature.auth.entry.AuthFeatureEntry
import com.arduia.expense.feature.auth.entry.AuthFeatureUi
import com.arduia.expense.feature.categories.entry.CategoriesFeatureEntry
import com.arduia.expense.feature.categories.entry.CategoriesFeatureUi
import com.arduia.expense.feature.currency.entry.CurrencyFeatureEntry
import com.arduia.expense.feature.currency.entry.CurrencyFeatureUi
import com.arduia.expense.feature.debt.entry.DebtFeatureEntry
import com.arduia.expense.feature.debt.entry.DebtFeatureUi
import com.arduia.expense.feature.eventbudget.entry.EventBudgetFeatureEntry
import com.arduia.expense.feature.eventbudget.entry.EventBudgetFeatureUi
import com.arduia.expense.feature.history.entry.HistoryFeatureEntry
import com.arduia.expense.feature.history.entry.HistoryFeatureUi
import com.arduia.expense.feature.importexport.entry.ImportExportFeatureEntry
import com.arduia.expense.feature.importexport.entry.ImportExportFeatureUi
import com.arduia.expense.feature.logging.entry.LoggingFeatureEntry
import com.arduia.expense.feature.logging.entry.LoggingFeatureUi
import com.arduia.expense.feature.onboarding.entry.OnboardingFeatureEntry
import com.arduia.expense.feature.onboarding.entry.OnboardingFeatureUi
import com.arduia.expense.feature.reports.entry.ReportsFeatureEntry
import com.arduia.expense.feature.reports.entry.ReportsFeatureUi
import com.arduia.expense.feature.sharedcost.entry.SharedCostFeatureEntry
import com.arduia.expense.feature.sharedcost.entry.SharedCostFeatureUi
import com.arduia.expense.feature.sync.entry.SyncFeatureEntry
import com.arduia.expense.feature.sync.entry.SyncFeatureUi

data class FeatureUiRegistry(
    val logging: LoggingFeatureEntry = LoggingFeatureUi,
    val auth: AuthFeatureEntry = AuthFeatureUi,
    val history: HistoryFeatureEntry = HistoryFeatureUi,
    val sharedCost: SharedCostFeatureEntry = SharedCostFeatureUi,
    val currency: CurrencyFeatureEntry = CurrencyFeatureUi,
    val importExport: ImportExportFeatureEntry = ImportExportFeatureUi,
    val sync: SyncFeatureEntry = SyncFeatureUi,
    val debt: DebtFeatureEntry = DebtFeatureUi,
    val eventBudget: EventBudgetFeatureEntry = EventBudgetFeatureUi,
    val reports: ReportsFeatureEntry = ReportsFeatureUi,
    val categories: CategoriesFeatureEntry = CategoriesFeatureUi,
    val onboarding: OnboardingFeatureEntry = OnboardingFeatureUi,
)
