package com.arduia.expense.storage

import com.arduia.expense.data.Result

/**
 * Runs a storage operation and wraps the outcome in [Result], converting any thrown exception into
 * [Result.Error]. Kept here so storage repositories never leak raw exceptions past the contract.
 */
internal inline fun <T> catchingResult(block: () -> T): Result<T> =
    try {
        Result.Success(block())
    } catch (e: kotlin.coroutines.cancellation.CancellationException) {
        throw e  // never swallow coroutine cancellation
    } catch (throwable: Throwable) {
        Result.Error(throwable.message ?: "Storage operation failed", throwable)
    }
