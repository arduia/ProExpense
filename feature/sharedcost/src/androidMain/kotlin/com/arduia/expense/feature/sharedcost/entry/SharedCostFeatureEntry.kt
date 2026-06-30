package com.arduia.expense.feature.sharedcost.entry

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import com.arduia.expense.data.SharedCostRepository
import com.arduia.expense.domain.SharedCost
import com.arduia.expense.domain.SplitStrategy
import com.arduia.expense.feature.sharedcost.CreateSharedCostUseCase
import com.arduia.expense.feature.sharedcost.DeleteSharedCostUseCase
import com.arduia.expense.feature.sharedcost.SaveSharedCostInput
import com.arduia.expense.feature.sharedcost.SplitMode
import com.arduia.expense.feature.sharedcost.UpdateSharedCostUseCase
import com.arduia.expense.feature.sharedcost.ui.SharedCostsFlow
import com.arduia.expense.feature.sharedcost.ui.components.SharedSplitMode
import com.arduia.expense.feature.sharedcost.ui.preview.SharedCostHistoryItemUi
import com.arduia.expense.feature.sharedcost.ui.preview.SharedCostParticipantUi
import com.arduia.expense.feature.sharedcost.ui.preview.SharedCostUiState
import com.arduia.expense.ui.design.AmountInput
import com.arduia.expense.ui.design.shortDateLabel
import java.util.Locale
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

interface SharedCostFeatureEntry {
    @Composable
    fun SharedCostsOverlay(
        onDismiss: () -> Unit,
        modifier: Modifier = Modifier,
    )
}

internal class SharedCostFeatureEntryImpl : SharedCostFeatureEntry {
    @Composable
    override fun SharedCostsOverlay(onDismiss: () -> Unit, modifier: Modifier) {
        val scope = rememberCoroutineScope()
        val sharedCostRepository: SharedCostRepository = koinInject()
        val createSharedCost: CreateSharedCostUseCase = koinInject()
        val updateSharedCost: UpdateSharedCostUseCase = koinInject()
        val deleteSharedCost: DeleteSharedCostUseCase = koinInject()

        val sharedCosts by sharedCostRepository.observeAll().collectAsState(emptyList())

        val history = sharedCosts
            .sortedByDescending { it.recordedAtEpochMillis }
            .map { it.toHistoryItemUi() }
        val sharedCostDetails = sharedCosts.associate { it.id.value to it.toUiState() }

        SharedCostsFlow(
            onDismiss = onDismiss,
            history = history,
            sharedCostDetails = sharedCostDetails,
            onSaveSplit = { title, rawTotal, mode, names, customShareRaws ->
                scope.launch {
                    createSharedCost(SaveSharedCostInput(title, rawTotal, mode.toSplitMode(), names, customShareRaws))
                }
            },
            onUpdateSplit = onUpdateSplit@{ id, title, rawTotal, mode, names, customShareRaws ->
                val existing = sharedCosts.find { it.id.value == id } ?: return@onUpdateSplit
                scope.launch {
                    updateSharedCost(existing, SaveSharedCostInput(title, rawTotal, mode.toSplitMode(), names, customShareRaws))
                }
            },
            modifier = modifier,
        )
        // deleteSharedCost is wired and ready; SharedCostsFlow has no delete affordance yet —
        // adding one is a separate Compose UI task (new screen state + Roborazzi baselines).
    }
}

object SharedCostFeatureUi : SharedCostFeatureEntry by SharedCostFeatureEntryImpl()

private fun SharedSplitMode.toSplitMode(): SplitMode = when (this) {
    SharedSplitMode.Equal -> SplitMode.EQUAL
    SharedSplitMode.Custom -> SplitMode.CUSTOM
}

private fun SharedCost.toHistoryItemUi(): SharedCostHistoryItemUi {
    val shares = shares()
    val perPersonLabel = when (splitStrategy) {
        is SplitStrategy.EqualSplit -> moneyLabel(shares.values.first().amount.valueInCents)
        is SplitStrategy.CustomSplit -> "Varies"
    }
    return SharedCostHistoryItemUi(
        id = id.value,
        title = title,
        peopleCount = participants.size,
        perPersonLabel = perPersonLabel,
        dateLabel = shortDateLabel(recordedAtEpochMillis),
        totalLabel = moneyLabel(total.amount.valueInCents),
    )
}

private fun SharedCost.toUiState(): SharedCostUiState {
    val shares = shares()
    return SharedCostUiState(
        rawTotal = String.format(Locale.US, "%.2f", total.amount.valueInCents / 100.0),
        note = title,
        peopleCount = participants.size,
        mode = if (splitStrategy is SplitStrategy.CustomSplit) SharedSplitMode.Custom else SharedSplitMode.Equal,
        participants = participants.map { participant ->
            SharedCostParticipantUi(
                name = participant.name,
                shareLabel = moneyLabel(shares[participant.id]?.amount?.valueInCents ?: 0L),
            )
        },
        shareRaws = participants.map { participant ->
            String.format(Locale.US, "%.2f", (shares[participant.id]?.amount?.valueInCents ?: 0L) / 100.0)
        },
    )
}

private fun moneyLabel(valueInCents: Long): String =
    "$" + AmountInput.formatDisplay(String.format(Locale.US, "%.2f", valueInCents / 100.0))
