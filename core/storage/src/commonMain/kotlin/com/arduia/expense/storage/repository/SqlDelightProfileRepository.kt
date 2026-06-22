package com.arduia.expense.storage.repository

import com.arduia.expense.data.ProfileRepository
import com.arduia.expense.data.Result
import com.arduia.expense.data.UserProfile
import com.arduia.expense.domain.CurrencyCode
import com.arduia.expense.storage.db.ProExpenseDatabase
import com.arduia.expense.storage.mapper.toBoolean
import com.arduia.expense.storage.mapper.toDb
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

internal class SqlDelightProfileRepository(
    private val database: ProExpenseDatabase,
    dispatcher: CoroutineDispatcher = Dispatchers.Default,
) : SqlDelightRepository(dispatcher), ProfileRepository {

    private val queries get() = database.appMetaQueries

    override suspend fun getProfile(): Result<UserProfile> = execute {
        queries.ensureMeta()
        val meta = queries.selectMeta().executeAsOne()
        UserProfile(
            name = meta.display_name,
            homeCurrency = CurrencyCode(meta.home_currency_code),
            onboardingCompleted = meta.onboarding_completed.toBoolean(),
        )
    }

    override suspend fun saveProfile(name: String, homeCurrency: CurrencyCode): Result<Unit> = execute {
        queries.ensureMeta()
        queries.updateProfile(display_name = name, home_currency_code = homeCurrency.code)
    }

    override suspend fun setOnboardingCompleted(completed: Boolean): Result<Unit> = execute {
        queries.ensureMeta()
        queries.updateOnboardingCompleted(completed.toDb())
    }
}
