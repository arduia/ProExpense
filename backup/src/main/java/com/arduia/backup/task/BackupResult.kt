package com.arduia.backup.task

import java.lang.Exception

interface BackupResult<T> {
    val data: T?
    val exception: Exception
    operator fun plus(value: BackupResult<T>): BackupResult<T>
}

internal class BackupCountResult(override val data: Int?) : BackupResult<Int> {
    override val exception: Exception
        get() = if (data != null) EmptyException() else Exception("Result Not Exit!")

    override fun plus(value: BackupResult<Int>): BackupResult<Int> {
        val plusData = value.data ?: return this
        val currentData = data ?: return BackupCountResult(plusData)
        return BackupCountResult(plusData + currentData)
    }

    companion object {
        fun empty(): BackupResult<Int> = BackupCountResult(data = null)
    }
}

internal class EmptyException() : Exception()

fun <T> BackupResult<T>.getDataOrError(): T = this.data ?: throw exception