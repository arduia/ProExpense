package com.arduia.expense.ui.sharedcost

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import com.arduia.expense.data.Result
import com.arduia.expense.data.SettlementSummary
import com.arduia.expense.data.SharedCostInput
import com.arduia.expense.data.SharedCostRepository
import com.arduia.expense.domain.Amount
import com.arduia.expense.domain.CurrencyCode
import com.arduia.expense.domain.Money
import com.arduia.expense.domain.Participant
import com.arduia.expense.domain.ParticipantId
import com.arduia.expense.domain.SharedCost
import com.arduia.expense.domain.SharedCostId
import com.arduia.expense.domain.SplitStrategy
import com.arduia.expense.feature.sharedcost.ArchiveSharedCostUseCase
import com.arduia.expense.feature.sharedcost.CreateSharedCostUseCase
import com.arduia.expense.feature.sharedcost.DeleteSharedCostUseCase
import com.arduia.expense.feature.sharedcost.UpdateSharedCostUseCase
import com.arduia.expense.feature.sharedcost.entry.SharedCostFeatureUi
import com.arduia.expense.ui.theme.ProArtboard
import com.arduia.expense.ui.theme.ProExpenseTheme
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.compose.KoinApplication
import org.koin.dsl.module
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

/**
 * Bugfix guard: a Split-kind row tapped from Journal opens SharedCostsOverlay, which used to
 * always start its own fresh `observeAll()` collection from scratch — even though the caller
 * (ExpenseApp) already has the same data loaded. That forced every deep link through a visible
 * blank "Loading" placeholder until the fresh Flow emission arrived, however long that took.
 * `preloadedSharedCosts` seeds the overlay's own collector so an already-loaded deep link target
 * skips the placeholder entirely. Both tests here use a repository Flow that never emits, so any
 * content shown must have come from the seed, not from the Flow.
 */
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(
    sdk = [33],
    qualifiers = "w${ProArtboard.PIXEL_9_PRO_WIDTH_DP}dp-h${ProArtboard.PIXEL_9_PRO_HEIGHT_DP}dp",
)
class SharedCostsOverlayPreloadTest {
    @get:Rule
    val rule = createAndroidComposeRule<ComponentActivity>()

    private val split =
        SharedCost(
            id = SharedCostId("1"),
            title = "Airbnb weekend",
            total = Money(Amount(120_00), CurrencyCode("USD")),
            participants =
                listOf(
                    Participant(ParticipantId("p1"), "You"),
                    Participant(ParticipantId("p2"), "Person 2"),
                ),
            splitStrategy = SplitStrategy.EqualSplit,
            recordedAtEpochMillis = 1_000L,
        )

    @Test
    fun deepLinkedSplit_preloadedFromCaller_rendersWithoutWaitingOnTheFlow() {
        rule.setContent {
            KoinApplication(application = { modules(sharedCostTestModule(MutableSharedFlow())) }) {
                ProExpenseTheme {
                    SharedCostFeatureUi.SharedCostsOverlay(
                        onDismiss = {},
                        initialViewingId = split.id.value,
                        preloadedSharedCosts = listOf(split),
                    )
                }
            }
        }

        // The repository Flow above never emits — this can only be showing because the
        // preloaded list seeded the overlay's collector.
        rule.onNodeWithText("Airbnb weekend").assertIsDisplayed()
    }

    @Test
    fun deepLinkedSplit_withoutPreload_staysOnPlaceholderUntilTheFlowEmits() {
        rule.setContent {
            KoinApplication(application = { modules(sharedCostTestModule(MutableSharedFlow())) }) {
                ProExpenseTheme {
                    SharedCostFeatureUi.SharedCostsOverlay(
                        onDismiss = {},
                        initialViewingId = split.id.value,
                    )
                }
            }
        }

        // Documents the placeholder-only state the preload above skips past: with no seed and no
        // Flow emission yet, the deep link target never resolves.
        rule.onNodeWithText("Airbnb weekend").assertDoesNotExist()
    }

    private fun sharedCostTestModule(flow: Flow<List<SharedCost>>) =
        module {
            single<SharedCostRepository> { FakeSharedCostRepository(flow) }
            factory { CreateSharedCostUseCase(get()) { 0L } }
            factory { UpdateSharedCostUseCase(get()) { 0L } }
            factory { DeleteSharedCostUseCase(get()) }
            factory { ArchiveSharedCostUseCase(get()) }
        }

    private class FakeSharedCostRepository(
        private val flow: Flow<List<SharedCost>>,
    ) : SharedCostRepository {
        override suspend fun create(input: SharedCostInput): Result<SharedCost> = error("unused in this test")

        override suspend fun getAll(): Result<List<SharedCost>> = error("unused in this test")

        override suspend fun getById(id: SharedCostId): Result<SharedCost?> = error("unused in this test")

        override suspend fun update(sharedCost: SharedCost): Result<Unit> = error("unused in this test")

        override suspend fun delete(id: SharedCostId): Result<Unit> = error("unused in this test")

        override suspend fun archive(id: SharedCostId): Result<Unit> = error("unused in this test")

        override suspend fun getSettlement(sharedCostId: SharedCostId): Result<SettlementSummary> = error("unused")

        override fun observeAll(): Flow<List<SharedCost>> = flow
    }
}
