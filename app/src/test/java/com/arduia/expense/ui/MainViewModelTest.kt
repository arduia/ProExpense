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
import org.junit.After
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestWatcher
import org.junit.runner.Description
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.atLeastOnce
import org.mockito.Mockito.doNothing
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.Mockito.any
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class MainViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()

    @Mock
    private lateinit var settingsRepository: SettingsRepository

    @Mock
    private lateinit var currencyRepository: CurrencyRepository

    @Mock
    private lateinit var workManager: WorkManager

    private lateinit var viewModel: MainViewModel
    private lateinit var lifecycle: LifecycleRegistry

    @Before
    fun setUp() {
        lifecycle = LifecycleRegistry(mock(LifecycleOwner::class.java))
        viewModel = MainViewModel(
            settingsRepository,
            currencyRepository,
            workManager
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `test setup is working`() {
        // This is a simple test to verify the setup is working
        assertNotNull(viewModel)
        assertNotNull(settingsRepository)
        assertNotNull(currencyRepository)
        assertNotNull(workManager)
    }

    @Test
    fun `init should call observeAndCacheSelectedCurrency`() = runTest {
        // Given
        val currencyNumber = "840"
        val successResult = Result.Success(currencyNumber)
        `when`(settingsRepository.getSelectedCurrencyNumber()).thenReturn(flowOf(successResult))

        // When
        viewModel = MainViewModel(settingsRepository, currencyRepository, workManager)

        // Then
        verify(settingsRepository, atLeastOnce()).getSelectedCurrencyNumber()
    }

    @Test
    fun `observeAndCacheSelectedCurrency should handle error result`() = runTest {
        // Given
        val errorResult = Result.Error(Exception("Test error"))
        `when`(settingsRepository.getSelectedCurrencyNumber()).thenReturn(flowOf(errorResult))

        // When
        viewModel = MainViewModel(settingsRepository, currencyRepository, workManager)

        // Then
        verify(settingsRepository, atLeastOnce()).getSelectedCurrencyNumber()
        // The ViewModel should not crash and should handle the error gracefully
    }

    @Test
    fun `observeAndCacheSelectedCurrency should handle loading result`() = runTest {
        // Given
        val loadingResult = Result.Loading
        `when`(settingsRepository.getSelectedCurrencyNumber()).thenReturn(flowOf(loadingResult))

        // When
        viewModel = MainViewModel(settingsRepository, currencyRepository, workManager)

        // Then
        verify(settingsRepository, atLeastOnce()).getSelectedCurrencyNumber()
        // The ViewModel should handle loading state gracefully
    }

    @Test
    fun `viewModel should implement LifecycleObserver`() {
        // Given & When
        val observer = viewModel as? androidx.lifecycle.LifecycleObserver

        // Then
        assertNotNull(observer)
    }

    @Test
    fun `observeAndCacheSelectedCurrency should handle empty currency number`() = runTest {
        // Given
        val emptyCurrencyNumber = ""
        val successResult = Result.Success(emptyCurrencyNumber)
        `when`(settingsRepository.getSelectedCurrencyNumber()).thenReturn(flowOf(successResult))

        // When
        viewModel = MainViewModel(settingsRepository, currencyRepository, workManager)

        // Then
        verify(settingsRepository, atLeastOnce()).getSelectedCurrencyNumber()
        // The ViewModel should handle empty currency number gracefully
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