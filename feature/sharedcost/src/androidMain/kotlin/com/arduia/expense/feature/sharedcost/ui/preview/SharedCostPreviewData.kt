package com.arduia.expense.feature.sharedcost.ui.preview

import com.arduia.expense.feature.sharedcost.SharedCostSplitLogic
import com.arduia.expense.feature.sharedcost.SharedSplitMode

data class SharedCostParticipantUi(
    val name: String,
    val shareLabel: String,
)

data class SharedCostUiState(
    val rawTotal: String = "",
    val note: String = "",
    val peopleCount: Int = 2,
    val mode: SharedSplitMode = SharedSplitMode.Equal,
    val participants: List<SharedCostParticipantUi> = emptyList(),
    val showZeroValidation: Boolean = false,
    val shareRaws: List<String> = emptyList(),
    val amountConfirmed: Boolean = false,
)

data class SharedCostHistoryItemUi(
    val id: String,
    val title: String,
    val peopleCount: Int,
    val perPersonLabel: String,
    val dateLabel: String,
    val totalLabel: String,
)

private fun buildParticipants(
    rawTotal: String,
    peopleCount: Int,
    names: List<String>,
): List<SharedCostParticipantUi> {
    val share = SharedCostSplitLogic.formatCents(
        SharedCostSplitLogic.equalShareCents(rawTotal, peopleCount),
    )
    return names.map { name ->
        SharedCostParticipantUi(name = name, shareLabel = share)
    }
}

val previewSharedInputEqual = SharedCostUiState(
    rawTotal = "120",
    note = "Dinner at Nobu",
    peopleCount = 4,
    mode = SharedSplitMode.Equal,
    participants = buildParticipants(
        rawTotal = "120",
        peopleCount = 4,
        names = SharedCostSplitLogic.previewNames(4),
    ),
)

val previewSharedSummary = SharedCostUiState(
    rawTotal = "240",
    note = "Dinner at Nobu",
    peopleCount = 4,
    mode = SharedSplitMode.Equal,
    participants = listOf(
        SharedCostParticipantUi("Aiko", "$60"),
        SharedCostParticipantUi("Kenji", "$60"),
        SharedCostParticipantUi("Sora", "$60"),
        SharedCostParticipantUi("You", "$60"),
    ),
)

val previewSharedZeroValidation = SharedCostUiState(
    rawTotal = "0",
    showZeroValidation = true,
)

val previewSharedCustomLimits = SharedCostUiState(
    rawTotal = "240",
    peopleCount = 20,
    mode = SharedSplitMode.Custom,
    participants = listOf(
        SharedCostParticipantUi("Person 1", "$80"),
        SharedCostParticipantUi("Person 2", "$60"),
        SharedCostParticipantUi("Person 3", "$100"),
    ),
)

val previewSharedHistoryItems = listOf(
    SharedCostHistoryItemUi(
        id = "1",
        title = "Dinner at Nobu",
        peopleCount = 4,
        perPersonLabel = "$60",
        dateLabel = "May 24",
        totalLabel = "$240",
    ),
    SharedCostHistoryItemUi(
        id = "2",
        title = "Airbnb · Bali",
        peopleCount = 3,
        perPersonLabel = "$400",
        dateLabel = "May 20",
        totalLabel = "$1,200",
    ),
    SharedCostHistoryItemUi(
        id = "3",
        title = "Taxi to airport",
        peopleCount = 2,
        perPersonLabel = "$18",
        dateLabel = "May 18",
        totalLabel = "$36",
    ),
)
