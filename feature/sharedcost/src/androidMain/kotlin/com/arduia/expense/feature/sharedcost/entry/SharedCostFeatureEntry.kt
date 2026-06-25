package com.arduia.expense.feature.sharedcost.entry

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import com.arduia.expense.data.SharedCostInput
import com.arduia.expense.data.SharedCostRepository
import com.arduia.expense.domain.Amount
import com.arduia.expense.domain.CurrencyCode
import com.arduia.expense.domain.Money
import com.arduia.expense.domain.Participant
import com.arduia.expense.domain.ParticipantId
import com.arduia.expense.domain.SharedCost
import com.arduia.expense.domain.SplitStrategy
import com.arduia.expense.feature.sharedcost.ui.SharedCostsFlow
import com.arduia.expense.feature.sharedcost.ui.components.SharedSplitMode
import com.arduia.expense.feature.sharedcost.ui.preview.SharedCostHistoryItemUi
import com.arduia.expense.feature.sharedcost.ui.preview.SharedCostParticipantUi
import com.arduia.expense.feature.sharedcost.ui.preview.SharedCostUiState
import com.arduia.expense.ui.design.AmountInput
import com.arduia.expense.ui.design.shortDateLabel
import java.util.Locale
import kotlin.math.roundToLong
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
                    val totalCents = ((AmountInput.numericValue(rawTotal) ?: 0.0) * 100).roundToLong()
                    val total = Money(Amount(totalCents), CurrencyCode("USD"))
                    val participants = names.mapIndexed { index, name ->
                        Participant(ParticipantId(newParticipantId(name, index)), name)
                    }
                    val splitStrategy = buildSplitStrategy(mode, customShareRaws, participants, total)
                    sharedCostRepository.create(
                        SharedCostInput(
                            title = title,
                            total = total,
                            participants = participants,
                            splitStrategy = splitStrategy,
                            recordedAtEpochMillis = System.currentTimeMillis(),
                        ),
                    )
                }
            },
            modifier = modifier,
        )
    }
}

object SharedCostFeatureUi : SharedCostFeatureEntry by SharedCostFeatureEntryImpl()

private fun newParticipantId(name: String, index: Int): String =
    name.trim().lowercase(Locale.US).replace(" ", "-") + "-" + index + "-" + System.currentTimeMillis()

private fun buildSplitStrategy(
    mode: SharedSplitMode,
    customShareRaws: List<String>,
    participants: List<Participant>,
    total: Money,
): SplitStrategy = when (mode) {
    SharedSplitMode.Equal -> SplitStrategy.EqualSplit
    SharedSplitMode.Custom -> {
        val rawCents = participants.indices.map { index ->
            val raw = customShareRaws.getOrElse(index) { "0" }
            ((AmountInput.numericValue(raw) ?: 0.0) * 100).roundToLong()
        }.toMutableList()
        val diff = total.amount.valueInCents - rawCents.sum()
        if (rawCents.isNotEmpty()) {
            rawCents[rawCents.lastIndex] = (rawCents.last() + diff).coerceAtLeast(0)
        }
        val shares = participants.mapIndexed { index, participant ->
            participant.id to Money(Amount(rawCents[index]), total.currency)
        }.toMap()
        SplitStrategy.CustomSplit(shares)
    }
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
        rawTotal = (total.amount.valueInCents / 100.0).toString(),
        note = title,
        peopleCount = participants.size,
        mode = if (splitStrategy is SplitStrategy.CustomSplit) SharedSplitMode.Custom else SharedSplitMode.Equal,
        participants = participants.map { participant ->
            SharedCostParticipantUi(
                name = participant.name,
                shareLabel = moneyLabel(shares[participant.id]?.amount?.valueInCents ?: 0L),
            )
        },
    )
}

private fun moneyLabel(valueInCents: Long): String =
    "$" + AmountInput.formatDisplay(String.format(Locale.US, "%.2f", valueInCents / 100.0))
