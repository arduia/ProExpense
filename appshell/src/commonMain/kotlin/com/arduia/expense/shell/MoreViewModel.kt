package com.arduia.expense.shell

import com.arduia.expense.data.ClearDataRepository
import com.arduia.expense.data.CurrencySettingsRepository
import com.arduia.expense.data.LocaleRepository
import com.arduia.expense.data.Result
import com.arduia.expense.data.ThemeMode
import com.arduia.expense.data.ThemeRepository
import com.arduia.expense.domain.CurrencyCode
import com.arduia.expense.feature.auth.PinAuthRepository
import com.arduia.expense.shared.CurrencyCatalog
import com.arduia.expense.shared.StatefulViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

data class MoreUiState(
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val languageTag: String = "en",
    val currencyCode: String = "USD",
    val pinConfigured: Boolean = false,
    val stayUnlockedInBackground: Boolean = false,
    val biometricEnrolled: Boolean = false,
    val isLoading: Boolean = true,
) {
    val currencySymbol: String get() = CurrencyCatalog.symbolFor(currencyCode)

    val currencyName: String get() = CurrencyCatalog.infoFor(currencyCode)?.name.orEmpty()

    /** "Stay unlocked" is meaningless without a PIN, so the row is hidden rather than disabled. */
    val showsSessionLockOptions: Boolean get() = pinConfigured
}

/**
 * 13 · More — the settings hub: theme, language, home currency, PIN, and destructive data actions.
 *
 * Every toggle writes through immediately and then re-reads, so the switch reflects what actually
 * persisted rather than optimistically flipping on a write that failed.
 */
class MoreViewModel(
    private val themeRepository: ThemeRepository,
    private val localeRepository: LocaleRepository,
    private val currencySettingsRepository: CurrencySettingsRepository,
    private val pinAuthRepository: PinAuthRepository,
    private val clearDataRepository: ClearDataRepository,
    dispatcher: CoroutineDispatcher = Dispatchers.Default,
) : StatefulViewModel<MoreUiState>(MoreUiState(), dispatcher) {
    init {
        viewModelScope.launch { reload() }
    }

    suspend fun setThemeMode(mode: ThemeMode) {
        themeRepository.setThemeMode(mode)
        reload()
    }

    suspend fun setLanguage(languageTag: String) {
        localeRepository.setLanguageTag(languageTag)
        reload()
    }

    suspend fun setHomeCurrency(code: String) {
        currencySettingsRepository.setHomeCurrency(CurrencyCode(code))
        reload()
    }

    suspend fun setStayUnlockedInBackground(enabled: Boolean) {
        pinAuthRepository.setStayUnlockedInBackgroundEnabled(enabled)
        reload()
    }

    /** Clears every table. Irreversible — the view must confirm before calling this. */
    suspend fun clearAllData(): Boolean {
        val result = clearDataRepository.clearAll()
        reload()
        return result is Result.Success
    }

    suspend fun reload() {
        val theme = (themeRepository.getThemeMode() as? Result.Success)?.data ?: ThemeMode.SYSTEM
        val language = (localeRepository.getLanguageTag() as? Result.Success)?.data ?: "en"
        val currency = (currencySettingsRepository.getHomeCurrency() as? Result.Success)?.data?.code ?: "USD"
        val pinConfigured = (pinAuthRepository.isPinConfigured() as? Result.Success)?.data ?: false
        val stayUnlocked = (pinAuthRepository.isStayUnlockedInBackgroundEnabled() as? Result.Success)?.data ?: false
        val biometric = (pinAuthRepository.isBiometricEnrolled() as? Result.Success)?.data ?: false
        setState {
            it.copy(
                themeMode = theme,
                languageTag = language,
                currencyCode = currency,
                pinConfigured = pinConfigured,
                stayUnlockedInBackground = stayUnlocked,
                biometricEnrolled = biometric,
                isLoading = false,
            )
        }
    }
}
