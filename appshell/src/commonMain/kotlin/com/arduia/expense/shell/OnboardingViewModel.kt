package com.arduia.expense.shell

import com.arduia.expense.data.Result
import com.arduia.expense.feature.onboarding.CompleteOnboardingUseCase
import com.arduia.expense.shared.CurrencyCatalog
import com.arduia.expense.shared.StatefulViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

/** The five carousel pages from `design-system-spec/screens/02-onboarding.md`. */
enum class OnboardingPage {
    Welcome,
    QuickLog,
    SharedCosts,
    EventBudget,
    Journal,
    ;

    companion object {
        val ordered: List<OnboardingPage> = entries
    }
}

/** Which of the two first-launch surfaces is showing. */
enum class OnboardingStep {
    /** 02 — the swipeable value-proposition carousel. */
    Carousel,

    /** 02P — name + home currency, merged onto one screen. */
    ProfileSetup,
}

data class CurrencyChoice(
    val code: String,
    val name: String,
    val symbol: String,
)

data class OnboardingUiState(
    val step: OnboardingStep = OnboardingStep.Carousel,
    val pageIndex: Int = 0,
    val displayName: String = "",
    val currencyCode: String = "USD",
    val currencyQuery: String = "",
    val isSaving: Boolean = false,
    val errorMessage: String? = null,
) {
    val page: OnboardingPage get() = OnboardingPage.ordered[pageIndex.coerceIn(0, OnboardingPage.ordered.lastIndex)]

    val isLastPage: Boolean get() = pageIndex >= OnboardingPage.ordered.lastIndex

    val currencySymbol: String get() = CurrencyCatalog.symbolFor(currencyCode)

    /**
     * A blank name is allowed — the PRD's onboarding is skippable and the greeting falls back — so
     * only an in-flight save blocks the button.
     */
    val canContinue: Boolean get() = !isSaving

    /** Catalog filtered by [currencyQuery] over both code and name. */
    val currencyOptions: List<CurrencyChoice>
        get() {
            val query = currencyQuery.trim()
            return CurrencyCatalog.ALL
                .filter { info ->
                    query.isEmpty() ||
                        info.code.contains(query, ignoreCase = true) ||
                        info.name.contains(query, ignoreCase = true)
                }.map { CurrencyChoice(code = it.code, name = it.name, symbol = it.symbol) }
        }
}

/**
 * First-launch flow: the 02 carousel and the 02P profile/currency form.
 *
 * Kept in `commonMain` so both shells agree on page order, the skip affordance, and what
 * "complete" means — completion delegates to [CompleteOnboardingUseCase], which already guarantees
 * the onboarding flag is written before the best-effort name/currency writes.
 */
class OnboardingViewModel(
    private val completeOnboarding: CompleteOnboardingUseCase,
    dispatcher: CoroutineDispatcher = Dispatchers.Default,
) : StatefulViewModel<OnboardingUiState>(OnboardingUiState(), dispatcher) {
    fun onPageChange(index: Int) {
        setState { it.copy(pageIndex = index.coerceIn(0, OnboardingPage.ordered.lastIndex)) }
    }

    fun onNext() {
        setState {
            if (it.isLastPage) {
                it.copy(step = OnboardingStep.ProfileSetup)
            } else {
                it.copy(pageIndex = it.pageIndex + 1)
            }
        }
    }

    fun onBack() {
        setState {
            when {
                it.step == OnboardingStep.ProfileSetup -> it.copy(step = OnboardingStep.Carousel)
                it.pageIndex > 0 -> it.copy(pageIndex = it.pageIndex - 1)
                else -> it
            }
        }
    }

    /** "Skip" jumps straight to the profile form — it does not bypass setup entirely (US-ONB-3). */
    fun onSkip() {
        setState { it.copy(step = OnboardingStep.ProfileSetup) }
    }

    fun onNameChange(name: String) {
        setState { it.copy(displayName = name) }
    }

    fun onCurrencySelected(code: String) {
        setState { it.copy(currencyCode = code, currencyQuery = "") }
    }

    fun onCurrencyQueryChange(query: String) {
        setState { it.copy(currencyQuery = query) }
    }

    /** Returns true once onboarding is durably marked complete, so the shell can advance its gate. */
    suspend fun finish(): Boolean {
        setState { it.copy(isSaving = true, errorMessage = null) }
        val state = currentState()
        val result = completeOnboarding(state.displayName.trim(), state.currencyCode)
        val succeeded = result is Result.Success
        setState {
            it.copy(
                isSaving = false,
                errorMessage = (result as? Result.Error)?.message,
            )
        }
        return succeeded
    }
}
