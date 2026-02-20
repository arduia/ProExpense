package com.arduia.expense.utils

import androidx.compose.runtime.Composable
import app.cash.molecule.RecompositionMode
import app.cash.molecule.moleculeFlow
import app.cash.turbine.ReceiveTurbine
import app.cash.turbine.test
import kotlin.time.Duration

/**
 * Extension method to test a Molecule Presenter using Turbine.
 */
suspend fun <T> testMolecule(
    timeout: Duration? = null,
    presenter: @Composable () -> T,
    validate: suspend ReceiveTurbine<T>.() -> Unit
) {
    moleculeFlow(RecompositionMode.Immediate) {
        presenter()
    }.test(timeout = timeout, validate = validate)
}
