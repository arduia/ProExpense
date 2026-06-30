package com.arduia.expense.feature.importexport.di

import com.arduia.expense.feature.importexport.ClearSelectedDataUseCase
import com.arduia.expense.feature.importexport.ExportDataUseCase
import com.arduia.expense.feature.importexport.ImportDataUseCase
import com.arduia.expense.feature.importexport.PreviewImportUseCase
import org.koin.dsl.module

val importExportModule = module {
    factory { ExportDataUseCase(get()) }
    factory { PreviewImportUseCase(get()) }
    factory { ImportDataUseCase(get()) }
    factory { ClearSelectedDataUseCase(get()) }
}
