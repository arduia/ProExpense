package com.arduia.expense.ui

import com.arduia.expense.feature.auth.entry.AuthFeatureEntry
import com.arduia.expense.feature.auth.entry.AuthFeatureUi
import com.arduia.expense.feature.currency.entry.CurrencyFeatureEntry
import com.arduia.expense.feature.currency.entry.CurrencyFeatureUi
import com.arduia.expense.feature.history.entry.HistoryFeatureEntry
import com.arduia.expense.feature.history.entry.HistoryFeatureUi
import com.arduia.expense.feature.importexport.entry.ImportExportFeatureEntry
import com.arduia.expense.feature.importexport.entry.ImportExportFeatureUi
import com.arduia.expense.feature.logging.entry.LoggingFeatureEntry
import com.arduia.expense.feature.logging.entry.LoggingFeatureUi
import com.arduia.expense.feature.sharedcost.entry.SharedCostFeatureEntry
import com.arduia.expense.feature.sharedcost.entry.SharedCostFeatureUi

data class FeatureUiRegistry(
    val logging: LoggingFeatureEntry = LoggingFeatureUi,
    val auth: AuthFeatureEntry = AuthFeatureUi,
    val history: HistoryFeatureEntry = HistoryFeatureUi,
    val sharedCost: SharedCostFeatureEntry = SharedCostFeatureUi,
    val currency: CurrencyFeatureEntry = CurrencyFeatureUi,
    val importExport: ImportExportFeatureEntry = ImportExportFeatureUi,
)
