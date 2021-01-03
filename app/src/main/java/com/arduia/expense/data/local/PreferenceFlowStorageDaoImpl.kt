package com.arduia.expense.data.local

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import androidx.preference.PreferenceManager
import com.tfcporciuncula.flow.FlowSharedPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class PreferenceFlowStorageDaoImpl @Inject constructor(@ApplicationContext private val context: Context) :
    PreferenceStorageDao {

    private val preferences = PreferenceManager.getDefaultSharedPreferences(context)

    @ExperimentalCoroutinesApi
    private val flowPref = FlowSharedPreferences(preferences)

    override fun getSelectedLanguage(): Flow<String> {
        return flowPref.getString(KEY_SELECTED_LANGUAGE, DEFAULT_SELECTED_LANGUAGE).asFlow()
    }

    override suspend fun setSelectedLanguage(id: String) {
        preferences.edit().putString(KEY_SELECTED_LANGUAGE, id).apply()
    }

    override fun getFirstUser(): Flow<Boolean> {
        return flowPref.getBoolean(KEY_FIRST_USER, DEFAULT_FIRST_USER).asFlow()
    }

    override suspend fun setFirstUser(isFirstUser: Boolean) {
        preferences.edit().putBoolean(KEY_FIRST_USER, isFirstUser).apply()
    }

    override fun getSelectedCurrencyNumber(): Flow<String> {
        return flowPref.getString(KEY_SELECTED_CURRENCY_NUM, DEFAULT_SELECTED_CURRENCY_NUM).asFlow()
    }

    override suspend fun setSelectedCurrencyNumber(num: String) {
        preferences.edit().putString(KEY_SELECTED_CURRENCY_NUM, num).apply()
    }

    override suspend fun getSelectedLanguageSync(): String {
        return preferences.getString(KEY_SELECTED_LANGUAGE, DEFAULT_SELECTED_LANGUAGE)
            ?: DEFAULT_SELECTED_LANGUAGE
    }

    override suspend fun getFirstUserSync(): Boolean {
        return preferences.getBoolean(KEY_FIRST_USER, DEFAULT_FIRST_USER)
    }

    override suspend fun getSelectedCurrencyNumberSync(): String {
        return preferences.getString(KEY_SELECTED_CURRENCY_NUM, DEFAULT_SELECTED_CURRENCY_NUM)
            ?: DEFAULT_SELECTED_CURRENCY_NUM
    }

    override suspend fun setSelectedThemeMode(mode: Int) {
        preferences.edit().putInt(KEY_THEME_MODE, mode).apply()
    }

    override suspend fun getSelectedThemeModeSync(): Int {
        return preferences.getInt(KEY_THEME_MODE, DEFAULT_THEME_MODE)
    }

    override fun getUpdateStatus(): Flow<Int> {
        return flowPref.getInt(KEY_UPDATE_STATUS, DEFAULT_UPDATE_STATUS).asFlow()
    }

    override suspend fun setUpdateStatus(status: Int) {
        preferences.edit().putInt(KEY_UPDATE_STATUS, status).apply()
    }

    override suspend fun getAboutUpdateSync(): AboutUpdateDataModel {
        val name = preferences.getString(KEY_ABOUT_UPDATE_NAME, DEFAULT_ABOUT_UPDATE_NAME)
            ?: DEFAULT_ABOUT_UPDATE_NAME
        val code = preferences.getLong(KEY_ABOUT_UPDATE_CODE, DEFAULT_ABOUT_UPDATE_CODE)
        val log = preferences.getString(KEY_ABOUT_UPDATE_LOG, DEFAULT_ABOUT_UPDATE_LOG)
            ?: DEFAULT_ABOUT_UPDATE_LOG
        return AboutUpdateDataModel(name, code, log)
    }

    override suspend fun setAboutUpdate(info: AboutUpdateDataModel) {
        val (name, code, log) = info
        with(preferences.edit()) {
            putString(KEY_ABOUT_UPDATE_NAME, name)
            putLong(KEY_ABOUT_UPDATE_CODE, code)
            putString(KEY_ABOUT_UPDATE_LOG, log)
            apply()
        }
    }

    companion object {

        private const val KEY_SELECTED_LANGUAGE = "selected_language"
        private const val DEFAULT_SELECTED_LANGUAGE = "en"

        private const val KEY_FIRST_USER = "isFirstTime"
        private const val DEFAULT_FIRST_USER = true

        private const val KEY_SELECTED_CURRENCY_NUM = "selected_currency_number"
        private const val DEFAULT_SELECTED_CURRENCY_NUM = "840"

        private const val KEY_THEME_MODE = "theme_mode"
        private const val DEFAULT_THEME_MODE = AppCompatDelegate.MODE_NIGHT_YES

        private const val KEY_UPDATE_STATUS = "update_status"
        private const val DEFAULT_UPDATE_STATUS = UpdateStatusDataModel.STATUS_NO_UPDATE

        private const val KEY_ABOUT_UPDATE_NAME = "about_update_name"
        private const val KEY_ABOUT_UPDATE_CODE = "about_update_code"
        private const val KEY_ABOUT_UPDATE_LOG = "about_update_log"

        private const val DEFAULT_ABOUT_UPDATE_NAME = ""
        private const val DEFAULT_ABOUT_UPDATE_CODE = 0L
        private const val DEFAULT_ABOUT_UPDATE_LOG = ""

    }
}