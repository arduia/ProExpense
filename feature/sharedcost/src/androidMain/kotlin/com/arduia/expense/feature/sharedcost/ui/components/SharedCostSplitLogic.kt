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

    fun formatCents(cents: Long, currencySymbol: String = "$"): String {
        val whole = cents / 100
        val fraction = cents % 100
        return "$currencySymbol$whole.${fraction.toString().padStart(2, '0')}"
    }

    fun formatRawTotal(rawTotal: String, currencySymbol: String = "$"): String {
        val display = AmountInput.formatDisplay(rawTotal.ifEmpty { "0" })
        return "$currencySymbol$display"
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

    /** Save-time normalization: a cleared (blank) name falls back to its "Person N" default. */
    fun resolveNames(current: List<String>, count: Int): List<String> =
        syncNames(current, count).mapIndexed { index, name ->
            name.trim().ifEmpty { defaultParticipantName(index + 1) }
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

    fun formatShareRaw(rawShare: String, currencySymbol: String = "$"): String {
        val display = AmountInput.formatDisplay(rawShare.ifEmpty { "0" })
        return "$currencySymbol$display"
    }

    fun buildParticipants(
        rawTotal: String,
        peopleCount: Int,
        mode: SharedSplitMode,
        names: List<String>,
        customShareRaws: List<String>,
        currencySymbol: String = "$",
    ): List<Pair<String, String>> {
        val resolvedNames = syncNames(names, peopleCount)
        return resolvedNames.mapIndexed { index, name ->
            val shareLabel = when (mode) {
                SharedSplitMode.Equal -> formatCents(equalShareCents(rawTotal, peopleCount), currencySymbol)
                SharedSplitMode.Custom -> formatShareRaw(
                    customShareRaws.getOrElse(index) {
                        (equalShareCents(rawTotal, peopleCount) / 100).toString()
                    },
                    currencySymbol,
                )
            }
            name to shareLabel
        }
    }
}
