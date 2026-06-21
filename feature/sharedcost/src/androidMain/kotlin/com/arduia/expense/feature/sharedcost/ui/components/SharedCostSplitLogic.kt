package com.arduia.expense.feature.sharedcost.ui.components

import kotlin.math.roundToLong
import com.arduia.expense.ui.design.AmountInput

enum class SharedSplitMode {
    Equal,
    Custom,
}

object SharedCostSplitLogic {
    private fun totalCents(rawTotal: String): Long {
        val value = AmountInput.numericValue(rawTotal) ?: 0.0
        return (value * 100).roundToLong()
    }

    fun canSave(rawTotal: String): Boolean = AmountInput.canProceed(rawTotal)

    fun equalShareCents(rawTotal: String, peopleCount: Int): Long {
        if (peopleCount <= 0) return 0L
        return totalCents(rawTotal) / peopleCount
    }

    fun formatCents(cents: Long): String {
        val whole = cents / 100
        val fraction = cents % 100
        return "$$whole.${fraction.toString().padStart(2, '0')}"
    }

    fun formatRawTotal(rawTotal: String): String {
        val display = AmountInput.formatDisplay(rawTotal.ifEmpty { "0" })
        return "$$display"
    }

    fun defaultParticipantName(index: Int): String = "Person $index"

    fun defaultNames(count: Int): List<String> =
        (1..count).map { defaultParticipantName(it) }

    fun previewNames(count: Int): List<String> = when (count) {
        4 -> listOf("Aiko", "Ben", "Carlos", "Dee")
        else -> defaultNames(count)
    }

    fun syncNames(current: List<String>, count: Int): List<String> {
        if (current.size == count) return current
        return (0 until count).map { index ->
            current.getOrNull(index) ?: defaultParticipantName(index + 1)
        }
    }

    fun syncCustomShares(
        current: List<String>,
        count: Int,
        rawTotal: String,
    ): List<String> {
        if (current.size == count) return current
        val equalShare = (equalShareCents(rawTotal, count.coerceAtLeast(1)) / 100.0)
            .let { if (it % 1.0 == 0.0) it.toLong().toString() else it.toString() }
        return (0 until count).map { index ->
            current.getOrNull(index) ?: equalShare
        }
    }

    fun formatShareRaw(rawShare: String): String {
        val display = AmountInput.formatDisplay(rawShare.ifEmpty { "0" })
        return "$$display"
    }

    fun buildParticipants(
        rawTotal: String,
        peopleCount: Int,
        mode: SharedSplitMode,
        names: List<String>,
        customShareRaws: List<String>,
    ): List<Pair<String, String>> {
        val resolvedNames = syncNames(names, peopleCount)
        return resolvedNames.mapIndexed { index, name ->
            val shareLabel = when (mode) {
                SharedSplitMode.Equal -> formatCents(equalShareCents(rawTotal, peopleCount))
                SharedSplitMode.Custom -> formatShareRaw(
                    customShareRaws.getOrElse(index) {
                        (equalShareCents(rawTotal, peopleCount) / 100).toString()
                    },
                )
            }
            name to shareLabel
        }
    }
}
