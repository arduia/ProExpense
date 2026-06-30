package com.arduia.expense.feature.categories.di

import com.arduia.expense.data.CategoryRepository
import com.arduia.expense.feature.categories.DeleteCategoryUseCase
import com.arduia.expense.feature.categories.ReorderCategoriesUseCase
import com.arduia.expense.feature.categories.SaveCategoryUseCase
import kotlinx.datetime.Clock
import org.koin.dsl.module

val categoriesModule = module {
    factory { SaveCategoryUseCase(get<CategoryRepository>(), nowEpochMillis = { Clock.System.now().toEpochMilliseconds() }) }
    factory { DeleteCategoryUseCase(get<CategoryRepository>()) }
    factory { ReorderCategoriesUseCase(get<CategoryRepository>()) }
}
