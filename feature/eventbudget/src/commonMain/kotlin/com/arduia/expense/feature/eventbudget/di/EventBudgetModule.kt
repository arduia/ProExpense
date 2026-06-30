package com.arduia.expense.feature.eventbudget.di

import com.arduia.expense.data.EventRepository
import com.arduia.expense.feature.eventbudget.CloseEventUseCase
import com.arduia.expense.feature.eventbudget.ComputeEventProgressUseCase
import com.arduia.expense.feature.eventbudget.CreateEventUseCase
import com.arduia.expense.feature.eventbudget.UpdateEventUseCase
import kotlinx.datetime.Clock
import org.koin.dsl.module

val eventBudgetModule = module {
    factory { ComputeEventProgressUseCase() }
    factory { CreateEventUseCase(get<EventRepository>(), nowEpochMillis = { Clock.System.now().toEpochMilliseconds() }) }
    factory { UpdateEventUseCase(get<EventRepository>()) }
    factory { CloseEventUseCase(get<EventRepository>()) }
}
