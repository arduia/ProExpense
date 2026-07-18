package com.arduia.expense.ui.journal

import androidx.activity.ComponentActivity
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import com.arduia.expense.data.FinanceRecordRepository
import com.arduia.expense.data.RecordChangeSignal
import com.arduia.expense.data.RecordPageCursor
import com.arduia.expense.data.Result
import com.arduia.expense.domain.Amount
import com.arduia.expense.domain.CategoryId
import com.arduia.expense.domain.CurrencyCode
import com.arduia.expense.domain.FinanceRecord
import com.arduia.expense.domain.Money
import com.arduia.expense.domain.RecordId
import com.arduia.expense.domain.RecordType
import com.arduia.expense.feature.history.DeleteRecordUseCase
import com.arduia.expense.feature.history.HistoryRepository
import com.arduia.expense.feature.history.LoadJournalPageUseCase
import com.arduia.expense.feature.history.RecordHistoryFilter
import com.arduia.expense.feature.history.RecordSummary
import com.arduia.expense.feature.history.SummaryPeriod
import com.arduia.expense.feature.history.UpdateRecordNoteUseCase
import com.arduia.expense.feature.history.entry.HistoryFeatureUi
import com.arduia.expense.testing.ComposeUiTests
import com.arduia.expense.ui.design.HomeNavTab
import com.arduia.expense.ui.theme.ProArtboard
import com.arduia.expense.ui.theme.ProExpenseTheme
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
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
 * Journal's tab state is hoisted above tab switching (US-HIS: instant record history) — leaving
 * the tab and coming back must show the already-loaded rows immediately, without issuing another
 * first-page load. Also pins the initial visit to exactly one page load (the change-signal
 * subscription's initial echo must not trigger a second, blinking reload).
 */
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(
    // State-retention across tab switches touches lifecycle/state-saving, which has genuinely
    // shifted across Android versions — check min SDK (24), a milestone (30), and target (36).
    sdk = [24, 30, 36],
    qualifiers = "w${ProArtboard.PIXEL_9_PRO_WIDTH_DP}dp-h${ProArtboard.PIXEL_9_PRO_HEIGHT_DP}dp",
)
@Category(ComposeUiTests::class)
class JournalTabRevisitTest {
    @get:Rule
    val rule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun reselectingJournalTab_showsLoadedRowsWithoutReloadingFromScratch() {
        val fakeHistory = FakeHistoryRepository(listOf(record("r1", "Sushi dinner")))
        val testModule =
            module {
                single<HistoryRepository> { fakeHistory }
                single<FinanceRecordRepository> { UnusedFinanceRecordRepository() }
                factory { LoadJournalPageUseCase(get()) }
                factory { DeleteRecordUseCase(get()) }
                factory { UpdateRecordNoteUseCase(get()) }
            }
        val journalVisible = mutableStateOf(true)

        rule.setContent {
            KoinApplication(application = { modules(testModule) }) {
                ProExpenseTheme {
                    val state = HistoryFeatureUi.rememberJournalTabState()
                    if (journalVisible.value) {
                        HistoryFeatureUi.JournalTab(
                            state = state,
                            selectedTab = HomeNavTab.Journal,
                            onTabSelected = {},
                            onAddClick = {},
                            initialSelectedRowId = null,
                            onEditRecord = {},
                            categories = emptyList(),
                            events = emptyList(),
                            debts = emptyList(),
                            sharedCosts = emptyList(),
                        )
                    }
                }
            }
        }

        rule.waitUntil(timeoutMillis = 5_000) { fakeHistory.pageLoads >= 1 }
        rule.onNodeWithText("Sushi dinner").assertIsDisplayed()
        assert(fakeHistory.pageLoads == 1) {
            "Expected exactly one first-page load on first open, got ${fakeHistory.pageLoads}"
        }

        journalVisible.value = false
        rule.waitForIdle()
        journalVisible.value = true

        rule.onNodeWithText("Sushi dinner").assertIsDisplayed()
        assert(fakeHistory.pageLoads == 1) {
            "Reselecting Journal reloaded from scratch (${fakeHistory.pageLoads} page loads)"
        }
    }

    private fun record(
        id: String,
        note: String,
    ) = FinanceRecord(
        id = RecordId(id),
        money = Money(Amount(1240), CurrencyCode("USD")),
        homeCurrencyMoney = Money(Amount(1240), CurrencyCode("USD")),
        categoryId = CategoryId("food"),
        type = RecordType.EXPENSE,
        note = note,
        recordedAtEpochMillis = 1_750_000_000_000,
    )

    private class FakeHistoryRepository(
        private val page: List<FinanceRecord>,
    ) : HistoryRepository {
        var pageLoads = 0
            private set
        private val signal = MutableStateFlow(RecordChangeSignal(page.size.toLong(), 1L))

        override suspend fun getRecords(filter: RecordHistoryFilter): Result<List<FinanceRecord>> = Result.Success(page)

        override suspend fun getSummary(
            period: SummaryPeriod,
            anchorEpochMillis: Long,
        ): Result<RecordSummary> = error("unused in this test")

        override suspend fun getRecordsPage(
            filter: RecordHistoryFilter,
            cursor: RecordPageCursor?,
            limit: Int,
        ): Result<List<FinanceRecord>> {
            if (cursor == null) pageLoads++
            return Result.Success(page)
        }

        override suspend fun hasAnyRecordIn(categoryId: CategoryId): Result<Boolean> = Result.Success(false)

        override fun observeChangeSignal(): Flow<RecordChangeSignal> = signal
    }

    private class UnusedFinanceRecordRepository : FinanceRecordRepository {
        override suspend fun getAll(): Result<List<FinanceRecord>> = error("unused in this test")

        override suspend fun getById(id: RecordId): Result<FinanceRecord?> = error("unused in this test")

        override suspend fun upsert(record: FinanceRecord): Result<Unit> = error("unused in this test")

        override suspend fun delete(id: RecordId): Result<Unit> = error("unused in this test")

        override fun observeAll(): Flow<List<FinanceRecord>> = error("unused in this test")

        override suspend fun verifyIntegrity(id: RecordId): Result<Boolean> = error("unused in this test")

        override suspend fun getRecordsPage(
            filter: com.arduia.expense.data.RecordPageFilter,
            cursor: RecordPageCursor?,
            limit: Int,
        ): Result<List<FinanceRecord>> = error("unused in this test")

        override suspend fun existsByCategory(categoryId: CategoryId): Result<Boolean> = error("unused in this test")

        override fun observeChangeSignal(): Flow<RecordChangeSignal> = error("unused in this test")
    }
}
