package com.arduia.expense.storage.repository

import com.arduia.expense.data.SecurityStateReader

class AppMetaSecurityStateReader(
    private val store: AppMetaLocalStore,
) : SecurityStateReader {

    override suspend fun hasPinConfigured(): Boolean = store.read().pinHash != null
}
