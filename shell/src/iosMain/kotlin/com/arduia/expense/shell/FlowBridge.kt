package com.arduia.expense.shell

import com.arduia.expense.shell.home.HomeViewModel
import com.arduia.expense.shell.home.ShellUiState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/** Handle returned by [ShellStateWatcher.watch]; Swift cancels it from `deinit`. */
class Cancellable internal constructor(
    private val scope: CoroutineScope,
) {
    fun cancel() {
        scope.cancel()
    }
}

/**
 * Swift-facing view of the shell's [StateFlow]. Kotlin `Flow` has no Objective-C representation a
 * SwiftUI view can bind to, so the shell hands Swift a closure subscription instead: [watch]
 * delivers every emission on the main queue and returns a [Cancellable].
 *
 * Deliberately concrete rather than `Watcher<T>`: Kotlin generics reach Objective-C as lightweight
 * generics that can erase to `id`, which would force unchecked casts in Swift for a type the Swift
 * side cannot be compile-checked against here. A second ViewModel gets its own watcher class.
 *
 * Also deliberately hand-rolled rather than adding a Swift-export compiler plugin — this compiles
 * and is verified on Linux by `verifyIosCompat`.
 */
class ShellStateWatcher internal constructor(
    private val flow: StateFlow<ShellUiState>,
) {
    /** Current value, for a synchronous first render before the first callback arrives. */
    val value: ShellUiState get() = flow.value

    fun watch(onEach: (ShellUiState) -> Unit): Cancellable {
        val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
        scope.launch { flow.collect { onEach(it) } }
        return Cancellable(scope)
    }
}

/** The shell state a SwiftUI view binds to. */
fun HomeViewModel.stateWatcher(): ShellStateWatcher = ShellStateWatcher(uiState)
