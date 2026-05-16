package com.arduia.expense.ui

import java.util.*

/**
 * Received Backup Tasks and show Progress state
 * with Toast message. Must be implemented by Host Activity
 */
interface BackupMessageReceiver{

    fun registerBackupTaskID(id: UUID)

    fun unregisterBackupTaskID(id: UUID)

}
