package com.arduia.expense.feature.sharedcost.di

import com.arduia.expense.data.SharedCostRepository
import com.arduia.expense.feature.sharedcost.CreateSharedCostUseCase
import com.arduia.expense.feature.sharedcost.DeleteSharedCostUseCase
import com.arduia.expense.feature.sharedcost.UpdateSharedCostUseCase
import kotlinx.datetime.Clock
import org.koin.dsl.module

val sharedCostModule = module {
    factory { CreateSharedCostUseCase(get<SharedCostRepository>(), nowEpochMillis = { Clock.System.now().toEpochMilliseconds() }) }
    factory { UpdateSharedCostUseCase(get<SharedCostRepository>(), nowEpochMillis = { Clock.System.now().toEpochMilliseconds() }) }
    factory { DeleteSharedCostUseCase(get<SharedCostRepository>()) }
}
