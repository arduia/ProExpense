package com.arduia.expense.ui

import com.arduia.expense.data.Result
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

/**
 * App-wide channel for one-shot UI messages (errors). ViewModels [post] a string resource id when a
 * repository call fails; [ExpenseApp] collects [messages] once and shows a single toast, so a failed
 * read/write is never silently swallowed without forcing per-screen error UI.
 */
class UiMessageBus {
    private val _messages = MutableSharedFlow<Int>(extraBufferCapacity = 8)
    val messages: SharedFlow<Int> = _messages.asSharedFlow()

    fun post(messageResId: Int) {
        _messages.tryEmit(messageResId)
    }
}

/** Unwrap a [Result], posting [errorResId] and returning [fallback] on failure. */
internal fun <T> Result<T>.orPost(bus: UiMessageBus, errorResId: Int, fallback: T): T = when (this) {
    is Result.Success -> data
    is Result.Error -> {
        bus.post(errorResId)
        fallback
    }
}

/** Post [errorResId] if this write [Result] failed; returns true on success. */
internal fun Result<Unit>.postIfError(bus: UiMessageBus, errorResId: Int): Boolean = when (this) {
    is Result.Success -> true
    is Result.Error -> {
        bus.post(errorResId)
        false
    }
}
