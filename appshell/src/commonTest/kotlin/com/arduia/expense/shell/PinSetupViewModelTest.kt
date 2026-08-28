package com.arduia.expense.shell

import com.arduia.expense.feature.auth.DisablePinUseCase
import com.arduia.expense.feature.auth.SecurityQuestionCatalog
import com.arduia.expense.feature.auth.SetupPinUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

/**
 * Backbone coverage for PIN creation.
 *
 * Traceability: US-AUTH-1 (set a PIN with a recovery question), US-AUTH-1 Scenario "confirm
 * mismatch", and US-AUTH-5 (turning PIN lock off clears the PIN and any biometric enrollment).
 */
@OptIn(ExperimentalCoroutinesApi::class)
class PinSetupViewModelTest {
    private fun TestScope.viewModel(repository: FakePinAuth = FakePinAuth(correctPin = null)): PinSetupViewModel =
        PinSetupViewModel(
            setupPin = SetupPinUseCase(repository),
            disablePin = DisablePinUseCase(repository),
            dispatcher = StandardTestDispatcher(testScheduler),
        )

    private fun PinSetupViewModel.enter(digits: String) {
        digits.forEach { onDigit(it.digitToInt()) }
    }

    @Test
    fun `a full pin advances to the confirm step`() =
        runTest {
            val vm = viewModel()

            vm.enter("123456")

            assertEquals(PinSetupStage.Confirm, vm.uiState.value.stage)
            assertEquals("123456", vm.uiState.value.newPin)
        }

    @Test
    fun `a matching confirmation advances to the security question`() =
        runTest {
            val vm = viewModel()

            vm.enter("123456")
            vm.enter("123456")

            assertEquals(PinSetupStage.SecurityQuestion, vm.uiState.value.stage)
            assertFalse(vm.uiState.value.mismatch)
        }

    @Test
    fun `a mismatched confirmation is reported and does not advance`() =
        runTest {
            val vm = viewModel()

            vm.enter("123456")
            vm.enter("654321")

            assertEquals(PinSetupStage.Confirm, vm.uiState.value.stage)
            assertTrue(vm.uiState.value.mismatch)
            assertFalse(vm.uiState.value.canSave)
        }

    @Test
    fun `retrying the confirmation clears the mismatched digits`() =
        runTest {
            val vm = viewModel()
            vm.enter("123456")
            vm.enter("654321")

            vm.onRetryConfirm()

            assertEquals("", vm.uiState.value.confirmPin)
            assertFalse(vm.uiState.value.mismatch)
        }

    /** Regression: backspacing an empty confirm buffer stranded the user with no way back. */
    @Test
    fun `backspacing an empty confirmation returns to the pin entry step`() =
        runTest {
            val vm = viewModel()
            vm.enter("123456")
            assertEquals(PinSetupStage.Confirm, vm.uiState.value.stage)

            vm.onBackspace()

            assertEquals(PinSetupStage.Enter, vm.uiState.value.stage)
            assertEquals("12345", vm.uiState.value.newPin)
        }

    @Test
    fun `saving persists the pin, question and answer`() =
        runTest {
            val repository = FakePinAuth(correctPin = null)
            val vm = viewModel(repository)
            vm.enter("123456")
            vm.enter("123456")
            vm.onQuestionSelected(SecurityQuestionCatalog.CITY)
            vm.onAnswerChange("  Lisbon  ")

            val saved = vm.save()

            assertTrue(saved)
            assertTrue(vm.uiState.value.completed)
            assertEquals("123456", repository.configuredPin)
            assertEquals(SecurityQuestionCatalog.CITY, repository.securityQuestionId)
            // Trimmed on the way in, so a stray space can't lock the user out of recovery.
            assertEquals("Lisbon", repository.securityAnswer)
        }

    @Test
    fun `saving without an answer is refused`() =
        runTest {
            val vm = viewModel()
            vm.enter("123456")
            vm.enter("123456")

            assertFalse(vm.uiState.value.canSave)
            assertFalse(vm.save())
        }

    @Test
    fun `biometric is only enrolled when the user opted in`() =
        runTest {
            val repository = FakePinAuth(correctPin = null)
            val vm = viewModel(repository)
            vm.enter("123456")
            vm.enter("123456")
            vm.onAnswerChange("Rex")

            vm.save()

            assertFalse(repository.biometricEnrolled)
        }

    @Test
    fun `opting into biometric enrolls it alongside the pin`() =
        runTest {
            val repository = FakePinAuth(correctPin = null)
            val vm = viewModel(repository)
            vm.enter("123456")
            vm.enter("123456")
            vm.onAnswerChange("Rex")
            vm.onBiometricToggled(enabled = true)

            vm.save()

            assertTrue(repository.biometricEnrolled)
        }

    @Test
    fun `disabling clears both the pin and the biometric enrollment`() =
        runTest {
            val repository = FakePinAuth(correctPin = "123456")
            repository.enrollBiometric()
            val vm = viewModel(repository)

            val disabled = vm.disable()

            assertTrue(disabled)
            assertNull(repository.configuredPin)
            assertFalse(repository.biometricEnrolled)
        }

    @Test
    fun `the question list comes from the shared catalog`() =
        runTest {
            val vm = viewModel()

            assertEquals(SecurityQuestionCatalog.IDS, vm.uiState.value.questionIds)
        }
}
