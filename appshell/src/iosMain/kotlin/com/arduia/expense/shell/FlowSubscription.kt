package com.arduia.expense.shell

import kotlinx.coroutines.Job

/**
 * Handle returned to Swift for a running collection. Swift owns the lifetime: hold it for as long
 * as the view is on screen and call [cancel] from `deinit`/`onDisappear`.
 */
class FlowSubscription internal constructor(
    private val job: Job,
) {
    fun cancel() {
        job.cancel()
    }
}
