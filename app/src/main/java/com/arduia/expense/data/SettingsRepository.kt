package com.arduia.expense.data

import kotlinx.coroutines.flow.Flow

interface SettingsRepository{

    fun getSelectedLanguage(): Flow<String>

    fun setSelectedLanguage(id: String)

    fun getFirstUser(): Flow<Boolean>

    fun setFirstUser(isFirstUser: Boolean)

}