package com.arduia.expense.data

import com.arduia.expense.data.local.PreferenceStorageDao

class SettingsRepositoryImpl(private val preferenceStorageDao: PreferenceStorageDao) : SettingsRepository {
    override fun getSelectedLanguage() = preferenceStorageDao.getSelectedLanguage()

    override fun setSelectedLanguage(id: String) {
        preferenceStorageDao.setSelectedLanguage(id)
    }

    override fun getFirstUser() = preferenceStorageDao.getFirstUser()

    override fun setFirstUser(isFirstUser: Boolean) {
        preferenceStorageDao.setFirstUser(isFirstUser)
    }

    override fun getSelectedCurrencyNumber() = preferenceStorageDao.getSelectedCurrencyNumber()

    override fun setSelectedCurrencyNumber(num: String) {
        preferenceStorageDao.setSelectedCurrencyNumber(num)
    }
}
