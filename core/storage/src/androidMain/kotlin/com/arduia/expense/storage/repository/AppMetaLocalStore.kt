package com.arduia.expense.storage.repository

import com.arduia.expense.storage.db.AppMetaQueries
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext

/**
 * Domain-friendly view of the single `app_meta` row.
 *
 * `home_currency_code` defaults to USD only as a placeholder before onboarding sets the real home
 * currency; budgets/amounts elsewhere are interpreted in this currency.
 */
data class AppMetaSnapshot(
    val monthlyBudgetCents: Long?,
    val homeCurrencyCode: String,
    val failedAttemptCount: Long,
    val lockoutUntil: Long?,
    val securityAnswerHash: String?,
    val securityQuestionId: String?,
    val biometricEnrolled: Boolean,
    val biometricWrappedKey: ByteArray?,
    val displayName: String,
    val onboardingCompleted: Boolean,
    val pinHash: String?,
) {
    companion object {
        val DEFAULT = AppMetaSnapshot(
            monthlyBudgetCents = null,
            homeCurrencyCode = "USD",
            failedAttemptCount = 0,
            lockoutUntil = null,
            securityAnswerHash = null,
            securityQuestionId = null,
            biometricEnrolled = false,
            biometricWrappedKey = null,
            displayName = "",
            onboardingCompleted = false,
            pinHash = null,
        )
    }
}

/**
 * Single owner of the `app_meta` row. The budget, lockout, and security-state repositories all back
 * onto this one row; routing every read-modify-write through one [Mutex] here prevents the lost-update
 * races that three independent writers on the same row would otherwise cause (design §3.2).
 */
class AppMetaLocalStore(
    private val queries: AppMetaQueries,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
) {
    private val mutex = Mutex()

    suspend fun read(): AppMetaSnapshot = withContext(dispatcher) {
        mutex.withLock { currentSnapshot() }
    }

    /** Atomically read-modify-write the row; returns the persisted snapshot. */
    suspend fun update(transform: (AppMetaSnapshot) -> AppMetaSnapshot): AppMetaSnapshot =
        withContext(dispatcher) {
            mutex.withLock {
                val updated = transform(currentSnapshot())
                persist(updated)
                updated
            }
        }

    private fun currentSnapshot(): AppMetaSnapshot {
        val row = queries.selectMeta().executeAsOneOrNull() ?: return AppMetaSnapshot.DEFAULT
        return AppMetaSnapshot(
            monthlyBudgetCents = row.monthly_budget_cents,
            homeCurrencyCode = row.home_currency_code,
            failedAttemptCount = row.failed_attempt_count,
            lockoutUntil = row.lockout_until,
            securityAnswerHash = row.security_answer_hash,
            securityQuestionId = row.security_question_id,
            biometricEnrolled = row.biometric_enrolled != 0L,
            biometricWrappedKey = row.biometric_wrapped_key,
            displayName = row.display_name,
            onboardingCompleted = row.onboarding_completed != 0L,
            pinHash = row.pin_hash,
        )
    }

    private fun persist(snapshot: AppMetaSnapshot) {
        queries.insertMeta(
            id = SINGLETON_ROW_ID,
            monthly_budget_cents = snapshot.monthlyBudgetCents,
            home_currency_code = snapshot.homeCurrencyCode,
            failed_attempt_count = snapshot.failedAttemptCount,
            lockout_until = snapshot.lockoutUntil,
            security_answer_hash = snapshot.securityAnswerHash,
            security_question_id = snapshot.securityQuestionId,
            biometric_enrolled = if (snapshot.biometricEnrolled) 1L else 0L,
            biometric_wrapped_key = snapshot.biometricWrappedKey,
            display_name = snapshot.displayName,
            onboarding_completed = if (snapshot.onboardingCompleted) 1L else 0L,
            pin_hash = snapshot.pinHash,
        )
    }

    private companion object {
        const val SINGLETON_ROW_ID = 1L
    }
}
