package com.arduia.expense.ui.more

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.arduia.expense.data.ClearDataRepository
import com.arduia.expense.data.Result
import com.arduia.expense.feature.importexport.ClearSelectedDataUseCase
import com.arduia.expense.feature.importexport.ui.ClearDataFlow
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
 * US-MORE-4: clearing data is selective (only checked categories are wiped) and always gated
 * behind a confirmation dialog — cancelling must never clear anything.
 */
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(
    sdk = [33],
    qualifiers = "w${ProArtboard.PIXEL_9_PRO_WIDTH_DP}dp-h${ProArtboard.PIXEL_9_PRO_HEIGHT_DP}dp",
)
@Category(ComposeUiTests::class)
class ClearDataFlowInteractionTest {
    @get:Rule
    val rule = createAndroidComposeRule<ComponentActivity>()

    private fun render(fakeRepo: FakeClearDataRepository) {
        val testModule =
            module {
                single<ClearDataRepository> { fakeRepo }
                single { ClearSelectedDataUseCase(get()) }
            }
        rule.setContent {
            KoinApplication(application = { modules(testModule) }) {
                ProExpenseTheme {
                    ClearDataFlow(onBack = {})
                }
            }
        }
    }

    @Test
    fun tappingClear_showsConfirmDialog_beforeAnythingIsCleared() {
        val fakeRepo = FakeClearDataRepository()
        render(fakeRepo)

        rule.onNodeWithText("Clear selected…").performClick()

        rule.onNodeWithText("Clear selected data?").assertIsDisplayed()
        assert(fakeRepo.clearedCalls.isEmpty()) { "Nothing must clear before confirming, got ${fakeRepo.clearedCalls}" }
    }

    @Test
    fun confirming_clearsOnlyTheDefaultCheckedCategory() {
        val fakeRepo = FakeClearDataRepository()
        render(fakeRepo)

        rule.onNodeWithText("Clear selected…").performClick()
        rule.onNodeWithText("Clear").performClick()

        assert(fakeRepo.clearedCalls == listOf("expenses")) {
            "Expected only expenses cleared (the default-checked option), got ${fakeRepo.clearedCalls}"
        }
    }

    @Test
    fun checkingAdditionalCategory_clearsBothOnConfirm() {
        val fakeRepo = FakeClearDataRepository()
        render(fakeRepo)

        rule.onNodeWithText("Debts").performClick()
        rule.onNodeWithText("Clear selected…").performClick()
        rule.onNodeWithText("Clear").performClick()

        assert(fakeRepo.clearedCalls.toSet() == setOf("expenses", "debts")) {
            "Expected expenses+debts cleared, got ${fakeRepo.clearedCalls}"
        }
    }

    @Test
    fun cancelingConfirmDialog_neverClearsAnything() {
        val fakeRepo = FakeClearDataRepository()
        render(fakeRepo)

        rule.onNodeWithText("Clear selected…").performClick()
        rule.onNodeWithText("Cancel").performClick()

        assert(fakeRepo.clearedCalls.isEmpty()) { "Cancelling must never clear anything, got ${fakeRepo.clearedCalls}" }
        rule.onNodeWithText("Clear selected data?").assertDoesNotExist()
    }
}

private class FakeClearDataRepository : ClearDataRepository {
    val clearedCalls = mutableListOf<String>()

    override suspend fun clearExpenses(): Result<Unit> {
        clearedCalls += "expenses"
        return Result.Success(Unit)
    }

    override suspend fun clearEvents(): Result<Unit> {
        clearedCalls += "events"
        return Result.Success(Unit)
    }

    override suspend fun clearDebts(): Result<Unit> {
        clearedCalls += "debts"
        return Result.Success(Unit)
    }

    override suspend fun clearSharedCosts(): Result<Unit> {
        clearedCalls += "shared"
        return Result.Success(Unit)
    }

    override suspend fun clearAll(): Result<Unit> {
        clearedCalls += "all"
        return Result.Success(Unit)
    }
}
