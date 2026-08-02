package com.arduia.expense.shared

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * Minimal KMP-only ViewModel base — no androidx.lifecycle dependency, so it works unmodified on
 * Android and iOS. Per the iOS-readiness principle, screen state/business logic lives here in
 * commonMain; the platform UI layer only collects [uiState] and calls public methods.
 *
 * The platform host owns the lifecycle: call [onCleared] when the screen leaves composition
 * (e.g. `DisposableEffect(Unit) { onDispose { viewModel.onCleared() } }` on Android).
 */
abstract class ProViewModel(
    /** Overridable so tests can drive [viewModelScope] with a `TestDispatcher`'s virtual clock. */
    dispatcher: CoroutineDispatcher = Dispatchers.Default,
) {
    protected val viewModelScope: CoroutineScope = CoroutineScope(SupervisorJob() + dispatcher)

    open fun onCleared() {
        viewModelScope.cancel()
    }
}

/** Convenience for ViewModels exposing a single [StateFlow] of UI state. */
abstract class StatefulViewModel<S>(
    initialState: S,
    dispatcher: CoroutineDispatcher = Dispatchers.Default,
) : ProViewModel(dispatcher) {
    private val _uiState = MutableStateFlow(initialState)
    val uiState: StateFlow<S> = _uiState

    protected fun setState(reducer: (S) -> S) {
        _uiState.value = reducer(_uiState.value)
    }

    protected fun currentState(): S = _uiState.value
}
