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
    val languageTag: String,
    val defaultCategoryId: String?,
    val themeMode: String,
    // US-AUTH-4 default is "always re-lock on background" — this stays false unless the user
    // opts in via Settings, so existing installs keep today's behavior after the migration.
    val stayUnlockedInBackground: Boolean,
    // Google Drive sync (opt-in, off by default — see docs/user_stories/sync/). OAuth tokens are
    // never stored here; see EncryptedSyncTokenStore (feature:sync androidMain).
    val syncConnected: Boolean,
    val syncAccountEmail: String?,
    val syncLastSyncedAt: Long?,
) {
    companion object {
        val DEFAULT =
            AppMetaSnapshot(
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
                languageTag = "en",
                defaultCategoryId = null,
                themeMode = "system",
                stayUnlockedInBackground = false,
                syncConnected = false,
                syncAccountEmail = null,
                syncLastSyncedAt = null,
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
    private val dispatcher: CoroutineDispatcher = Dispatchers.Default,
) : AppMetaStore {
    private val mutex = Mutex()

    override suspend fun read(): AppMetaSnapshot =
        withContext(dispatcher) {
            mutex.withLock { currentSnapshot() }
        }

    /** Atomically read-modify-write the row; returns the persisted snapshot. */
    override suspend fun update(transform: (AppMetaSnapshot) -> AppMetaSnapshot): AppMetaSnapshot =
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
            languageTag = row.language_tag,
            defaultCategoryId = row.default_category_id,
            themeMode = row.theme_mode,
            stayUnlockedInBackground = row.stay_unlocked_in_background != 0L,
            syncConnected = row.sync_connected != 0L,
            syncAccountEmail = row.sync_account_email,
            syncLastSyncedAt = row.sync_last_synced_at,
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
            language_tag = snapshot.languageTag,
            default_category_id = snapshot.defaultCategoryId,
            theme_mode = snapshot.themeMode,
            stay_unlocked_in_background = if (snapshot.stayUnlockedInBackground) 1L else 0L,
            sync_connected = if (snapshot.syncConnected) 1L else 0L,
            sync_account_email = snapshot.syncAccountEmail,
            sync_last_synced_at = snapshot.syncLastSyncedAt,
        )
    }

    private companion object {
        const val SINGLETON_ROW_ID = 1L
    }
}
