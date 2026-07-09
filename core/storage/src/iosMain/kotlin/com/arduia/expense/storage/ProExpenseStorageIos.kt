package com.arduia.expense.storage

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

/** iOS convenience overload: builds the driver/key-manager/key-value-store from iOS native primitives. */
fun ProExpenseStorage.Companion.create(
    keyManager: DatabaseKeyManager = IosDatabaseKeyManager(),
    dispatcher: CoroutineDispatcher = Dispatchers.Default,
): ProExpenseStorage =
    ProExpenseStorage.create(
        driverFactory = DatabaseDriverFactory(),
        keyManager = keyManager,
        keyValueStore = IosPlatformKeyValueStore(),
        dispatcher = dispatcher,
    )
