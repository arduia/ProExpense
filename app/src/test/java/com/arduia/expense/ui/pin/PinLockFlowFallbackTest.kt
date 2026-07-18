package com.arduia.expense.ui.pin

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.arduia.expense.data.ClearDataRepository
import com.arduia.expense.data.Result
import com.arduia.expense.feature.auth.DisablePinUseCase
import com.arduia.expense.feature.auth.PinAuthRepository
import com.arduia.expense.feature.auth.ResetPinUseCase
import com.arduia.expense.feature.auth.VerifyPinUseCase
import com.arduia.expense.feature.auth.VerifyRecoveryAnswerUseCase
import com.arduia.expense.feature.auth.ui.PinLockFlow
import com.arduia.expense.testing.ComposeUiTests
import com.arduia.expense.ui.theme.ProArtboard
import com.arduia.expense.ui.theme.ProExpenseTheme
import org.junit.Rule
import org.junit.Test
import org.junit.experimental.categories.Category
import org.junit.runner.RunWith
import org.koin.compose.KoinApplication
import org.koin.dsl.module
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

/**
 * US-AUTH-6 Scenario 2 / FR "Biometric failure or cancellation falls back to standard PIN entry,
 * never to a dead end" — Robolectric has no biometric hardware, so `BiometricAuthenticator`
 * (a hardcoded singleton wrapping the real `BiometricManager`/`BiometricPrompt` APIs, with no fake
 * seam) always reports unavailable here, meaning `PinLockFlow` never auto-triggers the real prompt
 * in this test. That's exactly the fallback state this test covers: PIN entry must be the reliable,
 * fully-functional unlock path on its own, not just a visual placeholder behind the biometric flow.
 */
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(
    sdk = [33],
    qualifiers = "w${ProArtboard.PIXEL_9_PRO_WIDTH_DP}dp-h${ProArtboard.PIXEL_9_PRO_HEIGHT_DP}dp",
)
@Category(ComposeUiTests::class)
class PinLockFlowFallbackTest {
    @get:Rule
    val rule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun pinEntry_unlocksSuccessfully_whenBiometricUnavailable() {
        var unlockedCalls = 0
        val fakeRepo = FakePinLockAuthRepository(pin = "123456")
        val testModule =
            module {
                single<PinAuthRepository> { fakeRepo }
                single { VerifyPinUseCase(get()) }
                single { VerifyRecoveryAnswerUseCase(get()) }
                single { ResetPinUseCase(get()) }
                single { DisablePinUseCase(get()) }
                single<ClearDataRepository> { FakeClearDataRepository() }
            }

        rule.setContent {
            KoinApplication(application = { modules(testModule) }) {
                ProExpenseTheme {
                    PinLockFlow(onUnlocked = { unlockedCalls++ })
                }
            }
        }

        "123456".forEach { digit -> rule.onNodeWithText(digit.toString()).performClick() }

        rule.waitUntil(timeoutMillis = 5_000) { unlockedCalls == 1 }
        assert(unlockedCalls == 1) { "Expected onUnlocked to fire once via PIN entry, got $unlockedCalls" }
    }
}

private class FakePinLockAuthRepository(
    private val pin: String,
) : PinAuthRepository {
    override suspend fun isPinConfigured(): Result<Boolean> = Result.Success(true)

    override suspend fun setPin(pin: String): Result<Unit> = Result.Success(Unit)

    override suspend fun verifyPin(pin: String): Result<Boolean> = Result.Success(pin == this.pin)

    override suspend fun clearPin(): Result<Unit> = Result.Success(Unit)

    override suspend fun setSecurityQuestion(
        questionId: String,
        answer: String,
    ): Result<Unit> = Result.Success(Unit)

    override suspend fun getSecurityQuestionId(): Result<String?> = Result.Success(null)

    override suspend fun verifySecurityAnswer(answer: String): Result<Boolean> = Result.Success(false)

    override suspend fun isBiometricEnrolled(): Result<Boolean> = Result.Success(false)

    override suspend fun enrollBiometric(): Result<Unit> = Result.Success(Unit)

    override suspend fun clearBiometric(): Result<Unit> = Result.Success(Unit)

    override suspend fun isStayUnlockedInBackgroundEnabled(): Result<Boolean> = Result.Success(false)

    override suspend fun setStayUnlockedInBackgroundEnabled(enabled: Boolean): Result<Unit> = Result.Success(Unit)

    override suspend fun getFailedAttemptCount(): Result<Long> = Result.Success(0)

    override suspend fun incrementFailedAttempts(): Result<Unit> = Result.Success(Unit)

    override suspend fun resetFailedAttempts(): Result<Unit> = Result.Success(Unit)

    override suspend fun getLockoutUntilMs(): Result<Long?> = Result.Success(null)

    override suspend fun setLockoutUntilMs(lockedUntilMs: Long): Result<Unit> = Result.Success(Unit)
}

private class FakeClearDataRepository : ClearDataRepository {
    override suspend fun clearExpenses(): Result<Unit> = Result.Success(Unit)

    override suspend fun clearEvents(): Result<Unit> = Result.Success(Unit)

    override suspend fun clearDebts(): Result<Unit> = Result.Success(Unit)

    override suspend fun clearSharedCosts(): Result<Unit> = Result.Success(Unit)

    override suspend fun clearAll(): Result<Unit> = Result.Success(Unit)
}
