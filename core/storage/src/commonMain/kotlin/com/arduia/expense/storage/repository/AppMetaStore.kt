package com.arduia.expense.storage.repository

interface AppMetaStore {
    suspend fun read(): AppMetaSnapshot

    suspend fun update(transform: (AppMetaSnapshot) -> AppMetaSnapshot): AppMetaSnapshot
}
