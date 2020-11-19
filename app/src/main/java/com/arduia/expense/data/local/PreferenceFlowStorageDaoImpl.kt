package com.arduia.expense.data.local

import android.content.Context
import androidx.preference.PreferenceManager
import com.tfcporciuncula.flow.FlowSharedPreferences
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import timber.log.Timber

class PreferenceFlowStorageDaoImpl(private val context: Context) : PreferenceStorageDao {

    private val pef = PreferenceManager.getDefaultSharedPreferences(context)

    @ExperimentalCoroutinesApi
    private val flowPref = FlowSharedPreferences(pef)

    override fun getSelectedLanguage(): Flow<String> {
        return flowPref.getString(KEY_SELECTED_LANGUAGE, DEFAULT_SELECTED_LANGUAGE).asFlow()
    }

    override suspend fun setSelectedLanguage(id: String) {
        pef.edit().putString(KEY_SELECTED_LANGUAGE, id).apply()
    }

    override fun getFirstUser(): Flow<Boolean> {
        return flowPref.getBoolean(KEY_FIRST_USER, DEFAULT_FIRST_USER).asFlow()
    }

    override suspend fun setFirstUser(isFirstUser: Boolean) {
        pef.edit().putBoolean(KEY_FIRST_USER, isFirstUser).apply()
    }

    override fun getSelectedCurrencyNumber(): Flow<String> {
        Timber.d("getCurrency Num")
        return flowPref.getString(KEY_SELECTED_CURRENCY_NUM, DEFAULT_SELECTED_CURRENCY_NUM).asFlow()
    }

    override suspend fun setSelectedCurrencyNumber(num: String) {
        Timber.d("selected Currency Number $num")
        pef.edit().putString(KEY_SELECTED_CURRENCY_NUM, num).apply()
    }

    companion object {

        private const val KEY_SELECTED_LANGUAGE = "selected_language"
        private const val DEFAULT_SELECTED_LANGUAGE = "en"

        private const val KEY_FIRST_USER = "isFirstTime"
        private const val DEFAULT_FIRST_USER = true

        private const val KEY_SELECTED_CURRENCY_NUM = "selected_currency_number"
        private const val DEFAULT_SELECTED_CURRENCY_NUM = "840"

    }
}