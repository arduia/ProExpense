package com.arduia.expense.shell

import com.arduia.expense.data.Result
import com.arduia.expense.feature.auth.PinAuthRepository
import com.arduia.expense.feature.auth.shouldRelockOnBackground
import com.arduia.expense.feature.onboarding.GetOnboardingStatusUseCase
import com.arduia.expense.shared.StatefulViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

const val SPLASH_DURATION_MILLIS = 1800L

/** Which surface the shell should show before any tab content is reachable. */
enum class AppGate {
    /** Branding splash — also covers "onboarding status not loaded yet". */
    Splash,

    /** First launch: name/currency setup has not been completed. */
    Onboarding,

    /** A PIN exists and this session has not been unlocked. */
    PinLock,

    /** Fully resolved — the shell may show its tabs. */
    Ready,
}

data class AppShellUiState(
    val splashElapsed: Boolean = false,
    val onboardingComplete: Boolean? = null,
    val displayName: String = "",
    val pinConfigured: Boolean? = null,
    val unlocked: Boolean = false,
    val stayUnlockedInBackground: Boolean = false,
) {
    /**
     * Single derived answer both shells render from, so Compose and SwiftUI cannot disagree about
     * gate precedence. `null` for [onboardingComplete] or [pinConfigured] means "still loading" and
     * deliberately holds on Splash rather than falling through to an unlocked shell.
     */
    val gate: AppGate
        get() =
            when {
                !splashElapsed || onboardingComplete == null -> AppGate.Splash
                !onboardingComplete -> AppGate.Onboarding
                pinConfigured == null -> AppGate.Splash
                pinConfigured && !unlocked -> AppGate.PinLock
                else -> AppGate.Ready
            }
}

/**
 * Owns the launch gate — splash timing, onboarding status, PIN configuration and session unlock.
 *
 * Lives in `commonMain` so the SwiftUI shell resolves the same gate the Compose shell does; the
 * platform layer only observes [uiState] and reports lifecycle events via [onEnterBackground].
 */
class AppShellViewModel(
    private val getOnboardingStatus: GetOnboardingStatusUseCase,
    private val pinAuthRepository: PinAuthRepository,
    dispatcher: CoroutineDispatcher = Dispatchers.Default,
) : StatefulViewModel<AppShellUiState>(AppShellUiState(), dispatcher) {
    init {
        viewModelScope.launch {
            delay(SPLASH_DURATION_MILLIS)
            setState { it.copy(splashElapsed = true) }
        }
        viewModelScope.launch { refreshOnboardingStatus() }
    }

    suspend fun refreshOnboardingStatus() {
        val status = getOnboardingStatus()
        setState { it.copy(onboardingComplete = status.isComplete, displayName = status.displayName) }
        if (status.isComplete) {
            loadAuthState()
        }
    }

    /** Called by the platform shell once onboarding's own flow reports success. */
    fun onOnboardingCompleted(displayName: String) {
        setState { it.copy(onboardingComplete = true, displayName = displayName) }
        viewModelScope.launch { loadAuthState() }
    }

    fun onUnlocked() {
        setState { it.copy(unlocked = true) }
    }

    /** A PIN set from Settings must not lock the user out of the session that just created it. */
    fun onPinConfigured(configured: Boolean) {
        setState { it.copy(pinConfigured = configured, unlocked = true) }
    }

    fun onStayUnlockedInBackgroundChanged(enabled: Boolean) {
        setState { it.copy(stayUnlockedInBackground = enabled) }
    }

    /** Android `ON_STOP` / iOS `scenePhase == .background`. */
    fun onEnterBackground() {
        if (shouldRelockOnBackground(currentState().stayUnlockedInBackground)) {
            setState { it.copy(unlocked = false) }
        }
    }

    private suspend fun loadAuthState() {
        val configured =
            when (val result = pinAuthRepository.isPinConfigured()) {
                is Result.Success -> result.data
                is Result.Error -> false
            }
        val stayUnlocked =
            (pinAuthRepository.isStayUnlockedInBackgroundEnabled() as? Result.Success)?.data
                ?: currentState().stayUnlockedInBackground
        setState { it.copy(pinConfigured = configured, stayUnlockedInBackground = stayUnlocked) }
    }
}
