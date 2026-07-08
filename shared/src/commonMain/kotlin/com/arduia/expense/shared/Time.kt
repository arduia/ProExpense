package com.arduia.expense.shared

/**
 * Current wall-clock time in epoch milliseconds.
 *
 * Declared as an `expect` seam (design's iOS-readiness principle #2) so storage/audit timestamps
 * stay in commonMain instead of calling `System.currentTimeMillis()` directly from androidMain.
 */
expect fun currentEpochMillis(): Long
