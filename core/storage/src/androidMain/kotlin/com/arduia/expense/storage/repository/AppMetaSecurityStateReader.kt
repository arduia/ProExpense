package com.arduia.expense.storage.repository

import com.arduia.expense.data.SecurityStateReader

/**
 * The schema has no dedicated PIN-hash column — PIN setup derives the SQLCipher key itself (design
 * §5.2) and stores a recovery answer hash. PIN is therefore considered configured when a recovery
 * answer hash exists.
 */
class AppMetaSecurityStateReader(
    private val store: AppMetaLocalStore,
) : SecurityStateReader {

    override suspend fun hasPinConfigured(): Boolean = store.read().securityAnswerHash != null
}
