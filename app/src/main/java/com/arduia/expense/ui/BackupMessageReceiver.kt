package com.arduia.expense.ui

import java.util.*

interface BackupMessageReceiver{

    fun addTaskID(id: UUID)

    fun removeTaskID(id: UUID)

}
