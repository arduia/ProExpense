package com.arduia.expense.data.local

import android.content.Context
import androidx.preference.PreferenceManager
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow

class PreferenceStorageDaoImpl(private val context: Context, private val scope: CoroutineScope) :
    PreferenceStorageDao {

    private val preference by lazy {
        PreferenceManager.getDefaultSharedPreferences(context)
    }

    @ExperimentalCoroutinesApi
    private val selectedLangChannel = BroadcastChannel<String>(10)

    @ExperimentalCoroutinesApi
    private val firstUserChannel = BroadcastChannel<Boolean>(10)

    @ExperimentalCoroutinesApi
    private val currencyNumCh = ConflatedBroadcastChannel<String>()

    @FlowPreview
    @ExperimentalCoroutinesApi
    override fun getSelectedLanguage(): Flow<String> {

        scope.launch(Dispatchers.IO) {
            //Update Status
            val lang = getString(KEY_SELECTED_LANGUAGE, DEFAULT_SELECTED_LANGUAGE)
            selectedLangChannel.send(lang)
        }

        return selectedLangChannel.asFlow()
    }

    override suspend fun setSelectedLanguage(id: String) {
        setString(KEY_SELECTED_LANGUAGE, id)
        val lang = getString(KEY_SELECTED_LANGUAGE, DEFAULT_SELECTED_LANGUAGE)
        selectedLangChannel.send(lang)
    }

    @ExperimentalCoroutinesApi
    @FlowPreview
    override fun getFirstUser(): Flow<Boolean> {
        scope.launch(Dispatchers.IO) {
            val isFirstUser = getBoolean(KEY_FIRST_USER, DEFAULT_FIRST_USER)
            firstUserChannel.send(isFirstUser)
        }
        return firstUserChannel.asFlow()
    }

    override suspend fun setFirstUser(isFirstUser: Boolean) {
        setBoolean(KEY_FIRST_USER, isFirstUser)
    }

    override fun getSelectedCurrencyNumber(): Flow<String> {
        updateSelectedCurrencyNum()
        return currencyNumCh.asFlow()
    }

    override suspend fun setSelectedCurrencyNumber(num: String) {
        setString(KEY_SELECTED_CURRENCY_NUM, num)
        currencyNumCh.offer(num)
    }

    private fun updateSelectedCurrencyNum() {
        scope.launch(Dispatchers.IO) {
            val num = getString(KEY_SELECTED_CURRENCY_NUM, DEFAULT_SELECTED_CURRENCY_NUM)
            currencyNumCh.send(num)
        }
    }

    private fun getString(key: String, defValue: String) =
        preference.getString(key, defValue) ?: defValue

    private fun setString(key: String, value: String) =
        preference.edit().putString(key, value).apply()

    private fun getBoolean(key: String, defValue: Boolean) = preference.getBoolean(key, defValue)
    private fun setBoolean(key: String, defValue: Boolean) =
        preference.edit().putBoolean(key, defValue).apply()

    companion object {

        private const val KEY_SELECTED_LANGUAGE = "selected_language"
        private const val DEFAULT_SELECTED_LANGUAGE = "en"

        private const val KEY_FIRST_USER = "isFirstTime"
        private const val DEFAULT_FIRST_USER = true

        private const val KEY_SELECTED_CURRENCY_NUM = "selected_currency_number"
        private const val DEFAULT_SELECTED_CURRENCY_NUM = "840"

    }
}