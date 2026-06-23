package com.arduia.expense.ui.currency

import com.arduia.expense.R
import com.arduia.expense.data.ProfileRepository
import com.arduia.expense.data.getOrNull
import com.arduia.expense.domain.CurrencyCode
import com.arduia.expense.ui.UiMessageBus
import com.arduia.expense.ui.postIfError
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * Owns the home-currency setting (single-currency MVP). Reads the current code from the profile and
 * persists a new selection without disturbing the stored name. Selecting a currency restamps how
 * every amount is formatted app-wide, so callers refresh their read-models afterwards.
 */
class CurrencyViewModel(
    private val profileRepository: ProfileRepository,
    private val scope: CoroutineScope,
    private val uiMessages: UiMessageBus = UiMessageBus(),
) {
    private var name: String = ""
    private val _selectedCode = MutableStateFlow("USD")
    val selectedCode: StateFlow<String> = _selectedCode.asStateFlow()

    init {
        refresh()
    }

    fun refresh() {
        scope.launch {
            val profile = profileRepository.getProfile().getOrNull()
            name = profile?.name.orEmpty()
            _selectedCode.value = profile?.homeCurrency?.code ?: "USD"
        }
    }

    fun select(code: String) {
        scope.launch {
            val currency = parseCurrency(code)
            if (profileRepository.saveProfile(name, currency).postIfError(uiMessages, R.string.data_load_error)) {
                _selectedCode.value = currency.code
            }
        }
    }

    private fun parseCurrency(code: String): CurrencyCode = try {
        CurrencyCode(code)
    } catch (e: IllegalArgumentException) {
        CurrencyCode("USD")
    }
}
