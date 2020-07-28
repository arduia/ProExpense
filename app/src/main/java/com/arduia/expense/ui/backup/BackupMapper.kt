package com.arduia.expense.ui.backup

import com.arduia.expense.data.local.BackupEnt
import com.arduia.expense.ui.vto.BackupVto

interface BackupMapper {

    fun mapToBackupVto(ent: BackupEnt): BackupVto

}