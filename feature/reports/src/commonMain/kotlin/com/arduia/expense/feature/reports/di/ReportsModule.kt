package com.arduia.expense.feature.reports.di

import com.arduia.expense.feature.reports.GenerateReportPeriodUseCase
import org.koin.dsl.module

val reportsModule =
    module {
        factory { GenerateReportPeriodUseCase() }
    }
