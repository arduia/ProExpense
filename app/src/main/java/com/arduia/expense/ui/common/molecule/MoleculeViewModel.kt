package com.arduia.expense.ui.common.molecule

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.cash.molecule.RecompositionMode
import app.cash.molecule.launchMolecule
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow

abstract class MoleculeViewModel<Event, State> : ViewModel() {
    private val events = Channel<Event>(Channel.UNLIMITED)

    val state: StateFlow<State> by lazy {
        viewModelScope.launchMolecule(mode = RecompositionMode.ContextClock) {
            present(events.receiveAsFlow())
        }
    }

    protected abstract val presenter: Presenter<Event, State>

    fun take(event: Event) {
        events.trySend(event)
    }

    @androidx.compose.runtime.Composable
    private fun present(events: Flow<Event>): State {
        return presenter.present(events)
    }
}
