package com.arduia.expense.storage.repository

import com.arduia.expense.data.Result
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

/**
 * Shared plumbing for SQLDelight-backed repositories: runs the (synchronous) query work off the
 * caller's thread on [dispatcher] and folds any failure into [Result.Error] so callers never see
 * a raw exception. Lives in commonMain — the only platform-specific piece is the driver itself.
 */
internal abstract class SqlDelightRepository(
    private val dispatcher: CoroutineDispatcher,
) {
    protected suspend fun <T> execute(block: () -> T): Result<T> =
        withContext(dispatcher) {
            try {
                Result.Success(block())
            } catch (t: Throwable) {
                Result.Error(t.message ?: "Database error", t)
            }
        }
}
