package com.arduia.expense.model

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.runBlocking

typealias FlowResult<T> = Flow<Result<T>>

fun <T> FlowResult<T>.awaitValueOrError(): T = runBlocking<T> {

    var firstSuccess: T? = null

    try {
        collect {
            if (it is Result.Success) {
                firstSuccess = it.data
                throw AbortFlowException()
            }
            if (it is Result.Error) throw Exception(it.exception)
        }
    } catch (e: AbortFlowException) {
        e.printStackTrace()
    }

    return@runBlocking firstSuccess ?: throw Exception("Await Value: Empty Result")
}

class AbortFlowException : CancellationException()