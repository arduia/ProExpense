package com.arduia.expense.data.ext

import com.arduia.expense.data.exception.RepositoryException
import com.arduia.expense.model.Result
import java.lang.Exception

inline fun <T> getResultSuccessOrError(fetch: () -> T): Result<T> {
    return try {
        Result.Success(fetch())
    } catch (e: Exception) {
        Result.Error(RepositoryException(e))
    }
}