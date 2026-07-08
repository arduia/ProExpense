package com.arduia.expense.storage.di

import com.arduia.expense.storage.ProExpenseStorage
import com.arduia.expense.storage.create
import org.koin.dsl.module

/** Opens the encrypted database once; the shared [storageBindings] expose `core:data` contracts. */
val storageModule = module {
    includes(storageBindings)
    single { ProExpenseStorage.create() }
}
