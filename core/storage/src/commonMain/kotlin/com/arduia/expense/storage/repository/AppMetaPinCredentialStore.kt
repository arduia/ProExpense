package com.arduia.expense.storage.repository

import com.arduia.expense.data.PinCredentialStore
import com.arduia.expense.data.PinCredentials

class AppMetaPinCredentialStore(
    private val store: AppMetaLocalStore,
) : PinCredentialStore {

    override suspend fun read(): PinCredentials = store.read().toPinCredentials()

    override suspend fun update(transform: (PinCredentials) -> PinCredentials): PinCredentials =
        store.update { snapshot ->
            val updated = transform(snapshot.toPinCredentials())
            snapshot.copy(
                pinHash = updated.pinHash,
                securityQuestionId = updated.securityQuestionId,
                securityAnswerHash = updated.securityAnswerHash,
                biometricEnrolled = updated.biometricEnrolled,
                biometricWrappedKey = updated.biometricWrappedKey,
                failedAttemptCount = updated.failedAttemptCount,
                lockoutUntil = updated.lockoutUntil,
            )
        }.toPinCredentials()

    private fun AppMetaSnapshot.toPinCredentials() = PinCredentials(
        pinHash = pinHash,
        securityQuestionId = securityQuestionId,
        securityAnswerHash = securityAnswerHash,
        biometricEnrolled = biometricEnrolled,
        biometricWrappedKey = biometricWrappedKey,
        failedAttemptCount = failedAttemptCount,
        lockoutUntil = lockoutUntil,
    )
}
