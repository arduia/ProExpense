package com.arduia.expense.shell

import com.arduia.expense.shared.StatefulViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

/**
 * Streams a [StatefulViewModel]'s UI state to Swift.
 *
 * Kotlin/Native cannot expose `Flow` or `suspend` functions to Swift, so this is the one seam every
 * SwiftUI `ObservableObject` in `iosApp/` uses. Collection runs on `Dispatchers.Main` because the
 * values land straight in `@Published` properties, which SwiftUI requires be mutated on the main
 * thread.
 *
 * The ViewModel is taken rather than its `uiState`, deliberately: a `StateFlow` parameter would
 * force the Swift call site to spell out the Objective-C name Kotlin/Native generates for a
 * kotlinx-coroutines class (`Kotlinx_coroutines_coreStateFlow`-style), which is derived from the
 * dependency's module name and is the least predictable part of the exported surface. Passing the
 * ViewModel keeps every type in the signature one this module owns.
 *
 * Kept free of top-level class declarations so the generated Swift name stays `FlowBridgeKt` —
 * [FlowSubscription] lives in its own file for the same reason.
 */
fun <S : Any> observeState(
    viewModel: StatefulViewModel<S>,
    onEach: (S) -> Unit,
): FlowSubscription {
    val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    val job =
        scope.launch {
            viewModel.uiState.collect { value -> onEach(value) }
        }
    job.invokeOnCompletion { scope.cancel() }
    return FlowSubscription(job)
}

/**
 * The state a SwiftUI view should render on its first frame, before [observeState]'s first emission
 * arrives — avoids a flash of the ViewModel's placeholder state.
 */
fun <S : Any> currentState(viewModel: StatefulViewModel<S>): S = viewModel.uiState.value
