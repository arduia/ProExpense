package com.arduia.expense.shell.home

import com.arduia.expense.data.BudgetRepository
import com.arduia.expense.data.CategoryRepository
import com.arduia.expense.data.DebtRepository
import com.arduia.expense.data.DefaultCategoryRepository
import com.arduia.expense.data.EventRepository
import com.arduia.expense.data.FinanceRecordRepository
import com.arduia.expense.data.Result
import com.arduia.expense.data.SharedCostRepository
import com.arduia.expense.domain.Amount
import com.arduia.expense.domain.Category
import com.arduia.expense.domain.Debt
import com.arduia.expense.domain.Event
import com.arduia.expense.domain.EventStatus
import com.arduia.expense.domain.FinanceRecord
import com.arduia.expense.domain.Money
import com.arduia.expense.domain.RecordLink
import com.arduia.expense.domain.RecordType
import com.arduia.expense.domain.SharedCost
import com.arduia.expense.feature.currency.CurrencyRepository
import com.arduia.expense.feature.eventbudget.ComputeEventProgressUseCase
import com.arduia.expense.shared.StatefulViewModel
import com.arduia.expense.shared.currentEpochMillis
import com.arduia.expense.ui.design.AmountInput
import com.arduia.expense.ui.design.PlatformDateFormatter
import com.arduia.expense.ui.design.currencySymbol
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

/** The repositories Home reads from, bundled so the ViewModel keeps a short constructor. */
data class HomeDataSources(
    val records: FinanceRecordRepository,
    val categories: CategoryRepository,
    val events: EventRepository,
    val debts: DebtRepository,
    val sharedCosts: SharedCostRepository,
    val budget: BudgetRepository,
    val defaultCategory: DefaultCategoryRepository,
    val currency: CurrencyRepository,
)

/**
 * Everything the app shell renders from shared data. [home] is what a Home screen binds to on
 * either platform; the remaining fields are the already-loaded collections the Android shell hands
 * to its other tabs, kept here so all of it comes from one set of Flow subscriptions.
 */
data class ShellUiState(
    // Loading, not empty, until the first Flow emission arrives — otherwise the shell flashes the
    // empty-state illustration on cold start before real data has had a chance to load.
    val home: HomeUiState = HomeUiState(isLoading = true),
    val records: List<FinanceRecord> = emptyList(),
    val recordsLoading: Boolean = true,
    val categories: List<Category> = emptyList(),
    val events: List<Event> = emptyList(),
    val eventsLoading: Boolean = true,
    val debts: List<Debt> = emptyList(),
    val sharedCosts: List<SharedCost> = emptyList(),
    val spentByEvent: Map<String, Money> = emptyMap(),
    val linkNames: HomeLinkNames = HomeLinkNames(emptyMap(), emptyMap(), emptyMap(), emptyMap()),
    val defaultCategoryChips: List<Pair<String, String>> = emptyList(),
    val customCategoryChips: List<Pair<String, String>> = emptyList(),
    val categoryTypeById: Map<String, RecordType> = emptyMap(),
    val homeCurrencyCode: String = "USD",
    val userName: String = "",
    val monthlyBudget: Money? = null,
    val defaultCategoryId: String = "food",
) {
    val homeCurrencySymbol: String get() = currencySymbol(homeCurrencyCode)
}

/** Snapshot of the five observed collections — one `combine` emission. */
private data class ObservedData(
    val records: List<FinanceRecord>?,
    val categories: List<Category>?,
    val events: List<Event>?,
    val debts: List<Debt>,
    val sharedCosts: List<SharedCost>,
)

/**
 * Screen-level state for the app shell's Home surface (US-HOME-1…4). Lives in `commonMain` so the
 * Compose shell and the SwiftUI shell render the same computed state instead of each mapping
 * records to rows themselves.
 */
class HomeViewModel(
    private val sources: HomeDataSources,
    private val computeEventProgress: ComputeEventProgressUseCase,
    private val nowEpochMillis: () -> Long = { currentEpochMillis() },
) : StatefulViewModel<ShellUiState>(ShellUiState()) {
    init {
        // Compute the header (date/month labels) up front so the first frame isn't blank while the
        // record Flow is still loading — the Compose shell got this for free from `remember`.
        setState { it.recomputeHome() }
        viewModelScope.launch {
            combine(
                sources.records.observeAll(),
                sources.categories.observeAll(),
                sources.events.observeAll(),
                sources.debts.observeAll(),
                sources.sharedCosts.observeAll(),
            ) { records, categories, events, debts, sharedCosts ->
                ObservedData(records, categories, events, debts, sharedCosts)
            }.collect { data -> setState { it.withObservedData(data) } }
        }
        viewModelScope.launch { loadSettings() }
    }

    /** Re-reads the one-shot settings (budget, default category, home currency, profile name). */
    suspend fun loadSettings() {
        (sources.currency.getSettings() as? Result.Success)?.let { result ->
            setState { it.copy(homeCurrencyCode = result.data.homeCurrency.code).recomputeHome() }
        }
        (sources.budget.getMonthlyBudget() as? Result.Success)?.let { result ->
            setState { it.copy(monthlyBudget = result.data).recomputeHome() }
        }
        (sources.defaultCategory.getDefaultCategoryId() as? Result.Success)?.data?.let { id ->
            setState { it.copy(defaultCategoryId = id) }
        }
    }

    fun onUserNameChanged(name: String) = setState { it.copy(userName = name).recomputeHome() }

    fun onHomeCurrencyChanged(code: String) = setState { it.copy(homeCurrencyCode = code).recomputeHome() }

    fun onMonthlyBudgetChanged(budget: Money?) = setState { it.copy(monthlyBudget = budget).recomputeHome() }

    fun onDefaultCategoryChanged(categoryId: String) = setState { it.copy(defaultCategoryId = categoryId) }

    private fun ShellUiState.withObservedData(data: ObservedData): ShellUiState {
        val records = data.records.orEmpty()
        val categories = data.categories.orEmpty()
        val events = data.events.orEmpty()
        return copy(
            records = records,
            // null (not emptyList()) until the first emission arrives, so the empty-state
            // illustration doesn't flash on cold start before real data has had a chance to load.
            recordsLoading = data.records == null,
            categories = categories,
            events = events,
            eventsLoading = data.events == null,
            debts = data.debts,
            sharedCosts = data.sharedCosts,
            spentByEvent = spentByEvent(events, records),
            linkNames =
                HomeLinkNames(
                    eventNames = events.associate { it.id.value to it.name },
                    debtNames = data.debts.associate { it.id.value to it.personName },
                    sharedCostNames = data.sharedCosts.associate { it.id.value to it.title },
                    categoryNames = categories.associate { it.id.value to it.name },
                ),
            // Both expense and income categories show together — direction is decided by which
            // category the user picks, not by a separate toggle (US-LOG income).
            defaultCategoryChips = categories.filter { !it.isCustom }.toChips(),
            customCategoryChips = categories.filter { it.isCustom }.toChips(),
            categoryTypeById = categories.associate { it.id.value to it.type },
        ).recomputeHome()
    }

    private fun ShellUiState.recomputeHome(): ShellUiState =
        copy(
            home =
                buildHomeUiState(
                    records = records,
                    // Visible unconditionally (Debt & totals decision), but never counted toward
                    // spend/income totals — only a toggle-on debt (already a real, linked
                    // FinanceRecord in `records`) does that.
                    visibleDebts = debts.filter { !it.recordAsTransaction },
                    linkNames = linkNames,
                    homeCurrencySymbol = homeCurrencySymbol,
                    userName = userName,
                    monthlyBudget = monthlyBudget,
                    activeEvent = activeEventState(),
                    recordsLoading = recordsLoading,
                    nowEpochMillis = nowEpochMillis(),
                ),
        )

    private fun ShellUiState.activeEventState(): HomeActiveEventState? {
        val event = events.filter { it.status == EventStatus.ACTIVE }.maxByOrNull { it.startEpochMillis } ?: return null
        val progress = computeEventProgress(event, spentByEvent[event.id.value])
        return HomeActiveEventState(
            eventId = event.id.value,
            title = event.name,
            dateRange = eventDateRange(event),
            spentLabel = AmountInput.formatMoney(progress.spentCents, homeCurrencySymbol),
            budgetLabel = "of " + AmountInput.formatMoney(progress.budgetCents, homeCurrencySymbol),
            progress = progress.progress,
            isOverBudget = progress.isOverBudget,
        )
    }
}

private fun eventDateRange(event: Event): String =
    if (event.startEpochMillis == event.endEpochMillis) {
        PlatformDateFormatter.shortDateLabel(event.startEpochMillis)
    } else {
        PlatformDateFormatter.shortDateLabel(event.startEpochMillis) + " — " +
            PlatformDateFormatter.shortDateLabel(event.endEpochMillis)
    }

private fun List<Category>.toChips(): List<Pair<String, String>> =
    sortedBy { it.sortOrder }
        .map { it.id.value to it.name }

/**
 * Summed from the observed records Flow (not a one-shot `EventRepository.getSpent` call) so the
 * Budget tab and Event Detail summary react immediately to any add/edit/delete of a linked
 * expense, not just to the event itself changing.
 */
private fun spentByEvent(
    events: List<Event>,
    records: List<FinanceRecord>,
): Map<String, Money> =
    events.associate { event ->
        val spentCents =
            records
                .filter { (it.link as? RecordLink.ToEvent)?.eventId == event.id }
                .sumOf { it.homeCurrencyMoney.amount.valueInCents }
        event.id.value to Money(Amount(spentCents), event.budget.currency)
    }
