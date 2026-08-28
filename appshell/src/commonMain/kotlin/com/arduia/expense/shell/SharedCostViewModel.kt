package com.arduia.expense.shell

import com.arduia.expense.data.CurrencySettingsRepository
import com.arduia.expense.data.Result
import com.arduia.expense.data.SharedCostRepository
import com.arduia.expense.domain.SharedCost
import com.arduia.expense.feature.sharedcost.ArchiveSharedCostUseCase
import com.arduia.expense.feature.sharedcost.CreateSharedCostUseCase
import com.arduia.expense.feature.sharedcost.DeleteSharedCostUseCase
import com.arduia.expense.feature.sharedcost.SaveSharedCostInput
import com.arduia.expense.feature.sharedcost.SharedCostSplitLogic
import com.arduia.expense.feature.sharedcost.SharedSplitMode
import com.arduia.expense.feature.sharedcost.SplitMode
import com.arduia.expense.shared.StatefulViewModel
import com.arduia.expense.ui.design.PlatformDateFormatter
import com.arduia.expense.ui.design.currencySymbol
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/** Default party size on a fresh split — the splitter plus one other person. */
private const val DEFAULT_PEOPLE_COUNT = 2

data class SharedCostRow(
    val sharedCostId: String,
    val title: String,
    val total: String,
    val dateLabel: String,
    val peopleCount: Int,
)

data class ParticipantShare(
    val name: String,
    val share: String,
)

data class SharedCostUiState(
    val rows: List<SharedCostRow> = emptyList(),
    val isLoading: Boolean = true,
    // Editor state
    val isEditorOpen: Boolean = false,
    val title: String = "",
    val rawTotal: String = "",
    val peopleCount: Int = DEFAULT_PEOPLE_COUNT,
    val mode: SharedSplitMode = SharedSplitMode.Equal,
    val names: List<String> = emptyList(),
    val customShareRaws: List<String> = emptyList(),
    val recordAsTransaction: Boolean = false,
    val currencySymbol: String = "$",
) {
    val isEmpty: Boolean get() = !isLoading && rows.isEmpty()

    /**
     * A blank title is rejected here because [SharedCost] itself requires one — without this the
     * save path throws from the domain constructor instead of showing a validation message.
     */
    val canSave: Boolean
        get() = title.isNotBlank() && SharedCostSplitLogic.canSave(rawTotal) && peopleCount > 0

    val totalLabel: String get() = SharedCostSplitLogic.formatRawTotal(rawTotal, currencySymbol)

    val perPersonLabel: String
        get() =
            SharedCostSplitLogic.formatCents(
                SharedCostSplitLogic.equalShareCents(rawTotal, peopleCount),
                currencySymbol,
            )

    val participants: List<ParticipantShare>
        get() =
            SharedCostSplitLogic
                .buildParticipants(rawTotal, peopleCount, mode, names, customShareRaws, currencySymbol)
                .map { (name, share) -> ParticipantShare(name, share) }

    /**
     * Custom shares are never auto-rebalanced (US-SHC-2/4), so the sum can legitimately diverge
     * from the total — the view surfaces that rather than silently correcting it.
     */
    val customSumMatchesTotal: Boolean
        get() =
            mode != SharedSplitMode.Custom ||
                SharedCostSplitLogic.customShareSumCents(customShareRaws) == SharedCostSplitLogic.totalCents(rawTotal)
}

/**
 * 10 · Shared Costs — split a bill across people.
 *
 * Every split rule (equal share, custom-share sync, name defaults and blank-name fallback, the
 * "does the custom sum match" check) comes from [SharedCostSplitLogic], which was already shared;
 * this ViewModel only holds the form and delegates persistence to the use cases.
 *
 * Over detekt's 11-function threshold because the split editor is a form: eight of these are
 * one-line field setters the SwiftUI/Compose bindings call directly. Splitting the form off would
 * add a type without reducing any real complexity.
 */
@Suppress("TooManyFunctions")
class SharedCostViewModel(
    private val sharedCostRepository: SharedCostRepository,
    private val currencySettingsRepository: CurrencySettingsRepository,
    private val createSharedCost: CreateSharedCostUseCase,
    private val deleteSharedCost: DeleteSharedCostUseCase,
    private val archiveSharedCost: ArchiveSharedCostUseCase,
    dispatcher: CoroutineDispatcher = Dispatchers.Default,
) : StatefulViewModel<SharedCostUiState>(SharedCostUiState(), dispatcher) {
    private var currencyCode: String = "USD"

    init {
        viewModelScope.launch {
            currencyCode = (currencySettingsRepository.getHomeCurrency() as? Result.Success)?.data?.code ?: "USD"
            setState { it.copy(currencySymbol = currencySymbol(currencyCode)) }
            sharedCostRepository.observeAll().collect { all ->
                setState { state ->
                    state.copy(rows = all.map { it.toRow() }, isLoading = false)
                }
            }
        }
    }

    fun openEditor() {
        setState {
            it.copy(
                isEditorOpen = true,
                title = "",
                rawTotal = "",
                peopleCount = DEFAULT_PEOPLE_COUNT,
                mode = SharedSplitMode.Equal,
                names = SharedCostSplitLogic.defaultNames(DEFAULT_PEOPLE_COUNT),
                customShareRaws = SharedCostSplitLogic.evenShareRaws(DEFAULT_PEOPLE_COUNT, ""),
                recordAsTransaction = false,
            )
        }
    }

    fun closeEditor() {
        setState { it.copy(isEditorOpen = false) }
    }

    fun onTitleChange(title: String) {
        setState { it.copy(title = title) }
    }

    fun onTotalChange(rawTotal: String) {
        setState { it.copy(rawTotal = rawTotal) }
    }

    /** The stepper re-splits evenly on every tap, for Custom as well as Equal (shared rule). */
    fun onPeopleCountChange(count: Int) {
        val safeCount = count.coerceAtLeast(1)
        setState {
            it.copy(
                peopleCount = safeCount,
                names = SharedCostSplitLogic.syncNames(it.names, safeCount),
                customShareRaws = SharedCostSplitLogic.evenShareRaws(safeCount, it.rawTotal),
            )
        }
    }

    fun onModeChange(mode: SharedSplitMode) {
        setState {
            it.copy(
                mode = mode,
                customShareRaws =
                    SharedCostSplitLogic.syncCustomShares(it.customShareRaws, it.peopleCount, it.rawTotal),
            )
        }
    }

    fun onNameChange(
        index: Int,
        name: String,
    ) {
        setState { state ->
            val names = SharedCostSplitLogic.syncNames(state.names, state.peopleCount).toMutableList()
            if (index in names.indices) names[index] = name
            state.copy(names = names)
        }
    }

    fun onCustomShareChange(
        index: Int,
        rawShare: String,
    ) {
        setState { state ->
            val shares =
                SharedCostSplitLogic
                    .syncCustomShares(state.customShareRaws, state.peopleCount, state.rawTotal)
                    .toMutableList()
            if (index in shares.indices) shares[index] = rawShare
            state.copy(customShareRaws = shares)
        }
    }

    fun onRecordAsTransactionChange(enabled: Boolean) {
        setState { it.copy(recordAsTransaction = enabled) }
    }

    suspend fun save(): Boolean {
        val state = currentState()
        if (!state.canSave) return false
        val saved =
            createSharedCost(
                SaveSharedCostInput(
                    title = state.title.trim(),
                    rawTotal = state.rawTotal,
                    mode = if (state.mode == SharedSplitMode.Custom) SplitMode.CUSTOM else SplitMode.EQUAL,
                    participantNames = SharedCostSplitLogic.resolveNames(state.names, state.peopleCount),
                    customShareRaws = state.customShareRaws,
                    recordAsTransaction = state.recordAsTransaction,
                ),
                currencyCode,
            )
        if (saved) closeEditor()
        return saved
    }

    suspend fun delete(sharedCostId: String) {
        deleteSharedCost(sharedCostId)
    }

    suspend fun archive(sharedCostId: String) {
        archiveSharedCost(sharedCostId)
    }

    private fun SharedCost.toRow(): SharedCostRow =
        SharedCostRow(
            sharedCostId = id.value,
            title = title,
            total = SharedCostSplitLogic.formatCents(total.amount.valueInCents, currencySymbol(currencyCode)),
            dateLabel = PlatformDateFormatter.shortDateLabel(recordedAtEpochMillis),
            peopleCount = participants.size,
        )
}
