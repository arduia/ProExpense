package com.arduia.expense.ui.common.molecule

import androidx.compose.runtime.Composable
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface Presenter<Event, State> {
    @Composable
    fun present(events: Flow<Event>): State
}
