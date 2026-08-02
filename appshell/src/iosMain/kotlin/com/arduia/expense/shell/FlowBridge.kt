package com.arduia.expense.shell

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

/**
 * Collects [flow] on the main dispatcher and pushes each value into [onEach].
 *
 * Kotlin/Native cannot expose `Flow` or `suspend` functions to Swift directly, so this is the one
 * seam every SwiftUI `ObservableObject` in `iosApp/` uses. Collection runs on `Dispatchers.Main`
 * because the values land straight in `@Published` properties, which SwiftUI requires be mutated
 * on the main thread.
 *
 * Kept free of top-level class declarations so the generated Swift name stays `FlowBridgeKt` —
 * [FlowSubscription] lives in its own file for the same reason.
 */
fun <T : Any> subscribe(
    flow: Flow<T>,
    onEach: (T) -> Unit,
): FlowSubscription {
    val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    val job =
        scope.launch {
            flow.collect { value -> onEach(value) }
        }
    job.invokeOnCompletion { scope.cancel() }
    return FlowSubscription(job)
}
