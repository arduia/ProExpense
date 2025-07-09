package com.arduia.expense.ui.settings

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.arduia.expense.data.SettingsRepository
import com.arduia.expense.data.CurrencyRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.*
import org.junit.Assert.*

@ExperimentalCoroutinesApi
class SettingsViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = UnconfinedTestDispatcher()

    @Mock
    private lateinit var settingsRepository: SettingsRepository

    @Mock
    private lateinit var currencyRepository: CurrencyRepository

    private lateinit var viewModel: SettingsViewModel

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        Dispatchers.setMain(testDispatcher)
        
        // Setup default mocks
        whenever(settingsRepository.getSelectedLanguage()).thenReturn(flowOf("en"))
        whenever(settingsRepository.getSelectedTheme()).thenReturn(flowOf("light"))
        whenever(currencyRepository.getSelectedCurrency()).thenReturn(flowOf("USD"))
        whenever(currencyRepository.getAllCurrencies()).thenReturn(flowOf(emptyList()))
        
        viewModel = SettingsViewModel(
            settingsRepository = settingsRepository,
            currencyRepository = currencyRepository
        )
    }

    @After
    fun teardown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initialization loads all settings successfully`() = runTest {
        // Verify all repository methods are called during initialization
        verify(settingsRepository).getSelectedLanguage()
        verify(settingsRepository).getSelectedTheme()
        verify(currencyRepository).getSelectedCurrency()
        verify(currencyRepository).getAllCurrencies()
    }

    @Test
    fun `selectedLanguage updates correctly`() = runTest {
        // Given
        val expectedLanguage = "es"
        whenever(settingsRepository.getSelectedLanguage()).thenReturn(flowOf(expectedLanguage))

        // When
        val observer = mock<Observer<String>>()
        viewModel.selectedLanguage.observeForever(observer)

        // Then
        verify(observer).onChanged(expectedLanguage)
    }

    @Test
    fun `selectedTheme updates correctly`() = runTest {
        // Given
        val expectedTheme = "dark"
        whenever(settingsRepository.getSelectedTheme()).thenReturn(flowOf(expectedTheme))

        // When
        val observer = mock<Observer<String>>()
        viewModel.selectedTheme.observeForever(observer)

        // Then
        verify(observer).onChanged(expectedTheme)
    }

    @Test
    fun `selectedCurrency updates correctly`() = runTest {
        // Given
        val expectedCurrency = "EUR"
        whenever(currencyRepository.getSelectedCurrency()).thenReturn(flowOf(expectedCurrency))

        // When
        val observer = mock<Observer<String>>()
        viewModel.selectedCurrency.observeForever(observer)

        // Then
        verify(observer).onChanged(expectedCurrency)
    }

    @Test
    fun `availableCurrencies updates correctly`() = runTest {
        // Given
        val currencies = listOf(
            createMockCurrency("USD", "US Dollar"),
            createMockCurrency("EUR", "Euro"),
            createMockCurrency("GBP", "British Pound")
        )
        whenever(currencyRepository.getAllCurrencies()).thenReturn(flowOf(currencies))

        // When
        val observer = mock<Observer<List<Any>>>()
        viewModel.availableCurrencies.observeForever(observer)

        // Then
        verify(observer).onChanged(currencies)
    }

    @Test
    fun `updateLanguage saves language successfully`() = runTest {
        // Given
        val newLanguage = "fr"
        whenever(settingsRepository.setSelectedLanguage(newLanguage)).thenReturn(Unit)

        // When
        viewModel.updateLanguage(newLanguage)

        // Then
        verify(settingsRepository).setSelectedLanguage(newLanguage)
    }

    @Test
    fun `updateTheme saves theme successfully`() = runTest {
        // Given
        val newTheme = "dark"
        whenever(settingsRepository.setSelectedTheme(newTheme)).thenReturn(Unit)

        // When
        viewModel.updateTheme(newTheme)

        // Then
        verify(settingsRepository).setSelectedTheme(newTheme)
    }

    @Test
    fun `updateCurrency saves currency successfully`() = runTest {
        // Given
        val newCurrency = "JPY"
        whenever(currencyRepository.setSelectedCurrency(newCurrency)).thenReturn(Unit)

        // When
        viewModel.updateCurrency(newCurrency)

        // Then
        verify(currencyRepository).setSelectedCurrency(newCurrency)
    }

    @Test
    fun `getAvailableLanguages returns supported languages`() = runTest {
        // When
        val languages = viewModel.getAvailableLanguages()

        // Then
        assertNotNull(languages)
        assertTrue(languages.isNotEmpty())
        assertTrue(languages.contains("English"))
    }

    @Test
    fun `getAvailableThemes returns supported themes`() = runTest {
        // When
        val themes = viewModel.getAvailableThemes()

        // Then
        assertNotNull(themes)
        assertTrue(themes.isNotEmpty())
        assertTrue(themes.contains("Light") || themes.contains("Dark"))
    }

    @Test
    fun `enableNotifications updates notification setting`() = runTest {
        // Given
        val enabled = true
        whenever(settingsRepository.setNotificationsEnabled(enabled)).thenReturn(Unit)

        // When
        viewModel.enableNotifications(enabled)

        // Then
        verify(settingsRepository).setNotificationsEnabled(enabled)
    }

    @Test
    fun `disableNotifications updates notification setting`() = runTest {
        // Given
        val enabled = false
        whenever(settingsRepository.setNotificationsEnabled(enabled)).thenReturn(Unit)

        // When
        viewModel.enableNotifications(enabled)

        // Then
        verify(settingsRepository).setNotificationsEnabled(enabled)
    }

    @Test
    fun `isNotificationsEnabled returns current setting`() = runTest {
        // Given
        val expectedValue = true
        whenever(settingsRepository.isNotificationsEnabled()).thenReturn(flowOf(expectedValue))

        // When
        val observer = mock<Observer<Boolean>>()
        viewModel.isNotificationsEnabled().observeForever(observer)

        // Then
        verify(observer).onChanged(expectedValue)
    }

    @Test
    fun `updateAutoBackup saves backup setting`() = runTest {
        // Given
        val enabled = true
        whenever(settingsRepository.setAutoBackupEnabled(enabled)).thenReturn(Unit)

        // When
        viewModel.updateAutoBackup(enabled)

        // Then
        verify(settingsRepository).setAutoBackupEnabled(enabled)
    }

    @Test
    fun `isAutoBackupEnabled returns current setting`() = runTest {
        // Given
        val expectedValue = false
        whenever(settingsRepository.isAutoBackupEnabled()).thenReturn(flowOf(expectedValue))

        // When
        val observer = mock<Observer<Boolean>>()
        viewModel.isAutoBackupEnabled().observeForever(observer)

        // Then
        verify(observer).onChanged(expectedValue)
    }

    @Test
    fun `resetToDefaults restores default settings`() = runTest {
        // Given
        whenever(settingsRepository.resetToDefaults()).thenReturn(Unit)
        whenever(currencyRepository.resetToDefault()).thenReturn(Unit)

        // When
        viewModel.resetToDefaults()

        // Then
        verify(settingsRepository).resetToDefaults()
        verify(currencyRepository).resetToDefault()
    }

    @Test
    fun `language update exceptions are handled gracefully`() = runTest {
        // Given
        val newLanguage = "invalid"
        whenever(settingsRepository.setSelectedLanguage(newLanguage)).thenThrow(RuntimeException("Invalid language"))
        
        val errorObserver = mock<Observer<String>>()
        viewModel.errorMessage.observeForever(errorObserver)

        // When
        viewModel.updateLanguage(newLanguage)

        // Then
        verify(errorObserver).onChanged("Failed to update language")
    }

    @Test
    fun `theme update exceptions are handled gracefully`() = runTest {
        // Given
        val newTheme = "invalid"
        whenever(settingsRepository.setSelectedTheme(newTheme)).thenThrow(RuntimeException("Invalid theme"))
        
        val errorObserver = mock<Observer<String>>()
        viewModel.errorMessage.observeForever(errorObserver)

        // When
        viewModel.updateTheme(newTheme)

        // Then
        verify(errorObserver).onChanged("Failed to update theme")
    }

    @Test
    fun `currency update exceptions are handled gracefully`() = runTest {
        // Given
        val newCurrency = "INVALID"
        whenever(currencyRepository.setSelectedCurrency(newCurrency)).thenThrow(RuntimeException("Invalid currency"))
        
        val errorObserver = mock<Observer<String>>()
        viewModel.errorMessage.observeForever(errorObserver)

        // When
        viewModel.updateCurrency(newCurrency)

        // Then
        verify(errorObserver).onChanged("Failed to update currency")
    }

    @Test
    fun `empty currency list is handled correctly`() = runTest {
        // Given
        whenever(currencyRepository.getAllCurrencies()).thenReturn(flowOf(emptyList()))

        // When
        val observer = mock<Observer<List<Any>>>()
        viewModel.availableCurrencies.observeForever(observer)

        // Then
        verify(observer).onChanged(emptyList())
    }

    @Test
    fun `null language setting is handled correctly`() = runTest {
        // Given
        whenever(settingsRepository.getSelectedLanguage()).thenReturn(flowOf(null))

        // When
        val observer = mock<Observer<String>>()
        viewModel.selectedLanguage.observeForever(observer)

        // Then
        verify(observer).onChanged(null)
    }

    @Test
    fun `large currency list is handled correctly`() = runTest {
        // Given
        val largeCurrencyList = (1..100).map { createMockCurrency("CUR$it", "Currency $it") }
        whenever(currencyRepository.getAllCurrencies()).thenReturn(flowOf(largeCurrencyList))

        // When
        val observer = mock<Observer<List<Any>>>()
        viewModel.availableCurrencies.observeForever(observer)

        // Then
        verify(observer).onChanged(largeCurrencyList)
        assertEquals(100, largeCurrencyList.size)
    }

    @Test
    fun `repository initialization exceptions are handled gracefully`() = runTest {
        // Given
        whenever(settingsRepository.getSelectedLanguage()).thenThrow(RuntimeException("Database error"))

        // When/Then - Should not crash during initialization
        val observer = mock<Observer<String>>()
        viewModel.selectedLanguage.observeForever(observer)
        
        // Should handle error gracefully
        verify(observer).onChanged(any())
    }

    // Helper methods for creating mock objects
    private fun createMockCurrency(code: String, name: String) = mock<Any> {
        on { this.code } doReturn code
        on { this.name } doReturn name
    }
}