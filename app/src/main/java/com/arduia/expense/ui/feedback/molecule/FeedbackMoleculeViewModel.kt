package com.arduia.expense.ui.feedback.molecule

import androidx.work.WorkManager
import com.arduia.expense.ui.common.molecule.MoleculeViewModel
import com.arduia.expense.ui.common.molecule.Presenter
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class FeedbackMoleculeViewModel @Inject constructor(
    workManager: WorkManager
) : MoleculeViewModel<FeedbackEvent, FeedbackState>() {

    override val presenter: Presenter<FeedbackEvent, FeedbackState> = FeedbackPresenter(workManager)
}
