package com.arduia.expense.data

import android.content.Context
import com.arduia.expense.data.exception.RepositoryException
import com.arduia.expense.data.local.PreferenceFlowStorageDaoImpl
import com.arduia.expense.data.local.PreferenceStorageDao
import com.arduia.expense.model.ErrorResult
import com.arduia.expense.model.FlowResult
import com.arduia.expense.model.Result
import com.arduia.expense.model.SuccessResult
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.lang.Exception

class SettingsRepositoryImpl(private val dao: PreferenceStorageDao) : SettingsRepository {
    override fun getSelectedLanguage(): FlowResult<String> =
        dao.getSelectedLanguage()
            .map { SuccessResult(it) }
            .catch { e -> ErrorResult(RepositoryException(e)) }

    override suspend fun setSelectedLanguage(id: String) {
        dao.setSelectedLanguage(id)
    }

    override fun getFirstUser(): FlowResult<Boolean> =
        dao.getFirstUser()
            .map { SuccessResult(it) }
            .catch { ErrorResult(RepositoryException(it)) }


    override suspend fun setFirstUser(isFirstUser: Boolean) {
        dao.setFirstUser(isFirstUser)
    }

    override fun getSelectedCurrencyNumber(): FlowResult<String> =
        dao.getSelectedCurrencyNumber()
            .map { SuccessResult(it) }
            .catch { ErrorResult(RepositoryException(it)) }


    override suspend fun setSelectedCurrencyNumber(num: String) {
        dao.setSelectedCurrencyNumber(num)
    }

    override suspend fun getSelectedLanguageSync(): Result<String> {
        return getResultSuccessOrError { dao.getSelectedLanguageSync() }
    }

    override suspend fun getFirstUserSync(): Result<Boolean> {
        return getResultSuccessOrError { dao.getFirstUserSync() }
    }

    override suspend fun getSelectedCurrencyNumberSync(): Result<String> {
        return getResultSuccessOrError { dao.getSelectedCurrencyNumberSync() }
    }

    private inline fun <T> getResultSuccessOrError(fetch: () -> T): Result<T> {
        return try {
            Result.Success(fetch())
        } catch (e: Exception) {
            Result.Error(RepositoryException(e))
        }
    }
}

object SettingRepositoryFactoryImpl : SettingsRepository.Factory {
    override fun create(context: Context): SettingsRepository {
        return SettingsRepositoryImpl(PreferenceFlowStorageDaoImpl(context))
    }
}