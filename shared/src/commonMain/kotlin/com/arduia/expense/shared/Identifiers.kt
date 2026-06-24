package com.arduia.expense.shared

import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

/**
 * Multiplatform id source. Kotlin's `Uuid` is multiplatform (design §6), so no expect/actual seam is
 * needed — repositories and domain factories generate ids in commonMain.
 */
object Identifiers {
    @OptIn(ExperimentalUuidApi::class)
    fun newId(): String = Uuid.random().toString()
}
