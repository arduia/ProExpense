package com.arduia.expense.shared

/**
 * Current wall-clock time as epoch millis via a platform seam (Android `System.currentTimeMillis`,
 * later iOS `NSDate`) — kept out of commonMain call sites' defaults so date formatting stays
 * portable without pulling in a `kotlinx-datetime` dependency.
 */
expect fun currentEpochMillis(): Long
