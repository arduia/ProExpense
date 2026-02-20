package com.arduia.expense.ui.backup

import com.arduia.core.arch.Mapper
import com.arduia.expense.data.BackupRepository
import com.arduia.expense.data.ExpenseRepository
import com.arduia.expense.data.local.BackupEnt
import com.arduia.expense.ui.backup.molecule.BackupEvent
import com.arduia.expense.ui.backup.molecule.BackupPresenter
import com.arduia.expense.ui.backup.molecule.BackupState
import com.arduia.expense.ui.common.molecule.MoleculeViewModel
import com.arduia.expense.ui.common.molecule.Presenter
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class BackupViewModel @Inject constructor(
    mapper: Mapper<BackupEnt, BackupUiModel>,
    backupRepo: BackupRepository,
    expenseRepo: ExpenseRepository
) : MoleculeViewModel<BackupEvent, BackupState>(){

    override val presenter: Presenter<BackupEvent, BackupState> =
        BackupPresenter(mapper, backupRepo, expenseRepo)

}
