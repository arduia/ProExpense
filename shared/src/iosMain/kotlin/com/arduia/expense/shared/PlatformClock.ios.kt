package com.arduia.expense.shared

import platform.Foundation.NSDate
import platform.Foundation.timeIntervalSince1970

actual fun currentEpochMillis(): Long = (NSDate().timeIntervalSince1970 * 1000).toLong()
