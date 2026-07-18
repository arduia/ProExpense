package com.arduia.expense.ui.more

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import com.arduia.expense.data.BudgetRepository
import com.arduia.expense.data.DefaultCategoryRepository
import com.arduia.expense.data.LocaleRepository
import com.arduia.expense.data.ProfileRepository
import com.arduia.expense.data.Result
import com.arduia.expense.data.ThemeMode
import com.arduia.expense.data.ThemeRepository
import com.arduia.expense.domain.CurrencyCode
import com.arduia.expense.domain.Money
import com.arduia.expense.feature.auth.DisablePinUseCase
import com.arduia.expense.feature.auth.PinAuthRepository
import com.arduia.expense.feature.currency.CurrencyRepository
import com.arduia.expense.feature.currency.CurrencySettings
import com.arduia.expense.feature.currency.SaveHomeCurrencyUseCase
import com.arduia.expense.testing.ComposeUiTests
import com.arduia.expense.ui.FeatureUiRegistry
import com.arduia.expense.ui.design.HomeNavTab
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
 * US-AUTH-6 Scenario 1 — "Enabling requires PIN first": this build's actual implementation
 * doesn't show a disabled biometric toggle or an error message on tap (the story's literal AC
 * text); instead the row is omitted entirely from More's settings list until PIN is on (see
 * MoreFlow.kt's `flatMap` — "insert it right after the PIN row only once PIN is on, instead of
 * showing it always-but-disabled"). This test covers the actual behavior: biometric unlock is
 * unreachable without PIN, just via row-omission rather than a blocked tap.
 */
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(
    sdk = [33],
    qualifiers = "w${ProArtboard.PIXEL_9_PRO_WIDTH_DP}dp-h${ProArtboard.PIXEL_9_PRO_HEIGHT_DP}dp",
)
@Category(ComposeUiTests::class)
class MoreBiometricGatingTest {
    @get:Rule
    val rule = createAndroidComposeRule<ComponentActivity>()

    private fun testModule(pinConfigured: Boolean) =
        module {
            single<ProfileRepository> { FakeProfileRepository() }
            single<CurrencyRepository> { FakeCurrencyRepository() }
            single { SaveHomeCurrencyUseCase(get()) }
            single<ThemeRepository> { FakeThemeRepository() }
            single<LocaleRepository> { FakeLocaleRepository() }
            single<PinAuthRepository> { FakeGatingPinAuthRepository(pinConfigured = pinConfigured) }
            single { DisablePinUseCase(get()) }
            single<BudgetRepository> { FakeBudgetRepository() }
            single<DefaultCategoryRepository> { FakeDefaultCategoryRepository() }
        }

    private fun renderMoreFlow(pinConfigured: Boolean) {
        rule.setContent {
            KoinApplication(application = { modules(testModule(pinConfigured)) }) {
                ProExpenseTheme {
                    MoreFlow(
                        features = FeatureUiRegistry(),
                        selectedTab = HomeNavTab.More,
                        onTabSelected = {},
                        onAddClick = {},
                        onDebtClick = {},
                        onSharedClick = {},
                        pinConfigured = pinConfigured,
                    )
                }
            }
        }
    }

    @Test
    fun biometricRow_hidden_whenPinNotConfigured() {
        renderMoreFlow(pinConfigured = false)

        rule.onNodeWithText("Biometric unlock").assertDoesNotExist()
    }

    @Test
    fun biometricRow_shown_whenPinConfigured() {
        renderMoreFlow(pinConfigured = true)

        rule.onNodeWithText("Biometric unlock").assertIsDisplayed()
    }
}

private class FakeProfileRepository : ProfileRepository {
    override suspend fun setDisplayName(name: String): Result<Unit> = Result.Success(Unit)

    override suspend fun getDisplayName(): Result<String> = Result.Success("Maya")

    override suspend fun isOnboardingComplete(): Result<Boolean> = Result.Success(true)

    override suspend fun setOnboardingComplete(): Result<Unit> = Result.Success(Unit)
}

private class FakeCurrencyRepository : CurrencyRepository {
    override suspend fun getSettings(): Result<CurrencySettings> = Result.Success(CurrencySettings(CurrencyCode("USD")))

    override suspend fun setHomeCurrency(currency: CurrencyCode): Result<Unit> = Result.Success(Unit)
}

private class FakeThemeRepository : ThemeRepository {
    override suspend fun getThemeMode(): Result<ThemeMode> = Result.Success(ThemeMode.SYSTEM)

    override suspend fun setThemeMode(mode: ThemeMode): Result<Unit> = Result.Success(Unit)
}

private class FakeLocaleRepository : LocaleRepository {
    override suspend fun getLanguageTag(): Result<String> = Result.Success("en")

    override suspend fun setLanguageTag(languageTag: String): Result<Unit> = Result.Success(Unit)
}

private class FakeBudgetRepository : BudgetRepository {
    override suspend fun getMonthlyBudget(): Result<Money?> = Result.Success(null)

    override suspend fun setMonthlyBudget(money: Money?): Result<Unit> = Result.Success(Unit)
}

private class FakeDefaultCategoryRepository : DefaultCategoryRepository {
    override suspend fun getDefaultCategoryId(): Result<String?> = Result.Success("food")

    override suspend fun setDefaultCategoryId(categoryId: String?): Result<Unit> = Result.Success(Unit)
}

/** Only the methods MoreFlow's initial load actually calls need real behavior. */
private class FakeGatingPinAuthRepository(
    private val pinConfigured: Boolean,
) : PinAuthRepository {
    override suspend fun isPinConfigured(): Result<Boolean> = Result.Success(pinConfigured)

    override suspend fun setPin(pin: String): Result<Unit> = Result.Success(Unit)

    override suspend fun verifyPin(pin: String): Result<Boolean> = Result.Success(false)

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
