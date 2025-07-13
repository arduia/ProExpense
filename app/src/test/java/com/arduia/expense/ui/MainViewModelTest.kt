package com.arduia.expense.ui

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import com.arduia.expense.data.CurrencyRepository
import com.arduia.expense.data.SettingsRepository
import com.arduia.expense.model.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlinx.coroutines.test.advanceUntilIdle
import org.junit.After
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestWatcher
import org.junit.runner.Description
import org.junit.runner.RunWith
import io.mockk.*
import org.junit.runners.JUnit4

@ExperimentalCoroutinesApi
@RunWith(JUnit4::class)
class MainViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()

    private lateinit var settingsRepository: SettingsRepository
    private lateinit var currencyRepository: CurrencyRepository
    private lateinit var workManager: WorkManager

    private lateinit var viewModel: MainViewModel
    private lateinit var lifecycle: LifecycleRegistry

    @Before
    fun setUp() {
        // Use MockK for better Flow mocking
        settingsRepository = mockk(relaxed = true)
        currencyRepository = mockk(relaxed = true) 
        workManager = mockk(relaxed = true)
        
        lifecycle = LifecycleRegistry(mockk(relaxed = true))
        
        // Setup default mock behaviors
        every { settingsRepository.getSelectedCurrencyNumber() } returns flowOf(Result.Success("840"))
        coEvery { currencyRepository.setSelectedCacheCurrency(any()) } just Runs
        every { workManager.enqueue(any<OneTimeWorkRequest>()) } returns mockk()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        clearAllMocks()
    }

    @Test
    fun `test setup is working`() {
        // This is a simple test to verify the setup is working
        assertNotNull(settingsRepository)
        assertNotNull(currencyRepository)
        assertNotNull(workManager)
    }

    @Test
    fun `init should call observeAndCacheSelectedCurrency`() = runTest {
        // Given
        val currencyNumber = "840"
        val successResult = Result.Success(currencyNumber)
        every { settingsRepository.getSelectedCurrencyNumber() } returns flowOf(successResult)

        // When
        viewModel = MainViewModel(settingsRepository, currencyRepository, workManager)
        advanceUntilIdle() // Wait for all coroutines to complete

        // Then
        verify { settingsRepository.getSelectedCurrencyNumber() }
        coVerify { currencyRepository.setSelectedCacheCurrency(currencyNumber) }
    }

    @Test
    fun `observeAndCacheSelectedCurrency should handle error result`() = runTest {
        // Given
        val errorResult = Result.Error(Exception("Test error"))
        every { settingsRepository.getSelectedCurrencyNumber() } returns flowOf(errorResult)

        // When
        viewModel = MainViewModel(settingsRepository, currencyRepository, workManager)
        advanceUntilIdle() // Wait for all coroutines to complete

        // Then
        verify { settingsRepository.getSelectedCurrencyNumber() }
        // Should not call setSelectedCacheCurrency for error result
        coVerify(exactly = 0) { currencyRepository.setSelectedCacheCurrency(any()) }
    }

    @Test
    fun `observeAndCacheSelectedCurrency should handle loading result`() = runTest {
        // Given
        val loadingResult = Result.Loading
        every { settingsRepository.getSelectedCurrencyNumber() } returns flowOf(loadingResult)

        // When
        viewModel = MainViewModel(settingsRepository, currencyRepository, workManager)
        advanceUntilIdle() // Wait for all coroutines to complete

        // Then
        verify { settingsRepository.getSelectedCurrencyNumber() }
        // Should not call setSelectedCacheCurrency for loading result
        coVerify(exactly = 0) { currencyRepository.setSelectedCacheCurrency(any()) }
    }

    @Test
    fun `viewModel should implement LifecycleObserver`() {
        // Given
        every { settingsRepository.getSelectedCurrencyNumber() } returns flowOf(Result.Success("840"))
        
        // When
        viewModel = MainViewModel(settingsRepository, currencyRepository, workManager)
        val observer = viewModel as? androidx.lifecycle.LifecycleObserver

        // Then
        assertNotNull(observer)
    }

}

@ExperimentalCoroutinesApi
class MainCoroutineRule : TestWatcher() {
    private val testDispatcher = StandardTestDispatcher()

    override fun starting(description: Description) {
        super.starting(description)
        Dispatchers.setMain(testDispatcher)
    }

    override fun finished(description: Description) {
        super.finished(description)
        Dispatchers.resetMain()
    }
} 