package com.arduia.expense.data

import android.content.Context
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import timber.log.Timber

class SettingsRepositoryImpl(private val context: Context,
                             private val scope: CoroutineScope): SettingsRepository{

    private val preference by lazy {
        context.getSharedPreferences(SETTING_FILE_NAME, Context.MODE_PRIVATE)
    }

    @ExperimentalCoroutinesApi
    private val selectedLangChannel = BroadcastChannel<String>(10)

    @FlowPreview
    @ExperimentalCoroutinesApi
    override fun getSelectedLanguage(): Flow<String> {

        scope.launch(Dispatchers.IO){
            //Update Status
            val lang = getString(KEY_SELECTED_LANGUAGE, DEFAULT_SELECTED_LANGUAGE)
            Timber.d("getSelectd Language -> $lang")
            selectedLangChannel.send(lang)
        }

       return selectedLangChannel.asFlow()
    }

    @FlowPreview
    @ExperimentalCoroutinesApi
    override fun setSelectedLanguage(id: String) {
        scope.launch(Dispatchers.IO){
            setString(KEY_SELECTED_LANGUAGE, id)
            //Update Status
            val lang = getString(KEY_SELECTED_LANGUAGE, DEFAULT_SELECTED_LANGUAGE)
            selectedLangChannel.send(lang)
        }
    }

    private fun getString(key:String, defValue:String) = preference.getString(key, defValue)?:defValue
    private fun setString(key:String, value:String) = preference.edit().putString(key, value).apply()

    companion object{

        private const val SETTING_FILE_NAME = "settings.xml"

        private const val KEY_SELECTED_LANGUAGE = "selected_language"
        private const val DEFAULT_SELECTED_LANGUAGE = "en"

    }
}
