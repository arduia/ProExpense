package com.arduia.expense.shell

import com.arduia.expense.data.ThemeMode
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * Backbone coverage for the settings hub.
 *
 * Traceability: US-MORE-1 (home currency), US-MORE-2 (clear all data), US-MORE-3 (theme mode) and
 * US-AUTH-4 Scenario 5 (the stay-unlocked opt-in is only meaningful once a PIN exists).
 */
@OptIn(ExperimentalCoroutinesApi::class)
class MoreViewModelTest {
    private fun TestScope.viewModel(
        pin: FakePinAuth = FakePinAuth(correctPin = null),
        currency: FakeCurrencySettings = FakeCurrencySettings(),
        clearData: FakeClearData = FakeClearData(),
    ): MoreViewModel =
        MoreViewModel(
            themeRepository = FakeTheme(),
            localeRepository = FakeLocale(),
            currencySettingsRepository = currency,
            pinAuthRepository = pin,
            clearDataRepository = clearData,
            dispatcher = StandardTestDispatcher(testScheduler),
        )

    @Test
    fun `it loads the current settings`() =
        runTest {
            val vm = viewModel()

            advanceUntilIdle()

            assertFalse(vm.uiState.value.isLoading)
            assertEquals(ThemeMode.SYSTEM, vm.uiState.value.themeMode)
            assertEquals("en", vm.uiState.value.languageTag)
            assertEquals(USD, vm.uiState.value.currencyCode)
        }

    @Test
    fun `changing the theme writes through and reflects what persisted`() =
        runTest {
            val vm = viewModel()
            advanceUntilIdle()

            vm.setThemeMode(ThemeMode.DARK)

            assertEquals(ThemeMode.DARK, vm.uiState.value.themeMode)
        }

    @Test
    fun `changing the home currency updates both the code and its symbol`() =
        runTest {
            val currency = FakeCurrencySettings()
            val vm = viewModel(currency = currency)
            advanceUntilIdle()

            vm.setHomeCurrency("EUR")

            assertEquals("EUR", currency.current)
            assertEquals("EUR", vm.uiState.value.currencyCode)
            assertEquals("€", vm.uiState.value.currencySymbol)
            assertEquals("Euro", vm.uiState.value.currencyName)
        }

    @Test
    fun `changing the language writes through`() =
        runTest {
            val vm = viewModel()
            advanceUntilIdle()

            vm.setLanguage("th")

            assertEquals("th", vm.uiState.value.languageTag)
        }

    @Test
    fun `session lock options stay hidden until a pin exists`() =
        runTest {
            val vm = viewModel(pin = FakePinAuth(correctPin = null))
            advanceUntilIdle()

            assertFalse(vm.uiState.value.pinConfigured)
            assertFalse(vm.uiState.value.showsSessionLockOptions)
        }

    @Test
    fun `session lock options appear once a pin is configured`() =
        runTest {
            val vm = viewModel(pin = FakePinAuth(correctPin = "123456"))
            advanceUntilIdle()

            assertTrue(vm.uiState.value.pinConfigured)
            assertTrue(vm.uiState.value.showsSessionLockOptions)
        }

    @Test
    fun `toggling stay-unlocked persists and reads back`() =
        runTest {
            val pin = FakePinAuth(correctPin = "123456")
            val vm = viewModel(pin = pin)
            advanceUntilIdle()

            vm.setStayUnlockedInBackground(enabled = true)

            assertTrue(vm.uiState.value.stayUnlockedInBackground)
        }

    @Test
    fun `clearing all data reports success and refreshes`() =
        runTest {
            val clearData = FakeClearData()
            val vm = viewModel(clearData = clearData)
            advanceUntilIdle()

            val cleared = vm.clearAllData()
            advanceUntilIdle()

            assertTrue(cleared)
            assertEquals(1, clearData.clearAllCalls)
            assertFalse(vm.uiState.value.isLoading)
        }

    @Test
    fun `biometric enrollment is surfaced`() =
        runTest {
            val pin = FakePinAuth(correctPin = "123456")
            pin.enrollBiometric()
            val vm = viewModel(pin = pin)

            advanceUntilIdle()

            assertTrue(vm.uiState.value.biometricEnrolled)
        }
}
