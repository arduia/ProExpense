package com.arduia.expense.feature.sharedcost

import com.arduia.expense.ui.design.AmountInput
import kotlin.math.roundToLong

enum class SharedSplitMode {
    Equal,
    Custom,
}

object SharedCostSplitLogic {
    private const val DEFAULT_NAME_TEMPLATE = "Person %1\$d"

    // The splitter is always participant #1 by default — naming them "You" rather than
    // "Person 1" makes clear the split belongs to them, not a template placeholder.
    private const val DEFAULT_FIRST_PERSON_NAME = "You"

    fun totalCents(rawTotal: String): Long {
        val value = AmountInput.numericValue(rawTotal) ?: 0.0
        return (value * 100).roundToLong()
    }

    fun canSave(rawTotal: String): Boolean = AmountInput.canProceed(rawTotal)

    fun equalShareCents(
        rawTotal: String,
        peopleCount: Int,
    ): Long {
        if (peopleCount <= 0) return 0L
        return totalCents(rawTotal) / peopleCount
    }

    fun formatCents(
        cents: Long,
        currencySymbol: String = "$",
    ): String {
        val whole = cents / 100
        val fraction = cents % 100
        return "$currencySymbol$whole.${fraction.toString().padStart(2, '0')}"
    }

    fun formatRawTotal(
        rawTotal: String,
        currencySymbol: String = "$",
    ): String {
        val display = AmountInput.formatDisplay(rawTotal.ifEmpty { "0" })
        return "$currencySymbol$display"
    }

    /**
     * [nameTemplate] is a `%1$d`-style format string (from `R.string.shared_default_person_name`
     * at real call sites) — defaults to the English fallback only for contexts with no Android
     * resources available (tests, previews). Substituted manually because `String.format` is
     * JVM-only and this must run on iOS. [firstPersonName] (from
     * `R.string.shared_default_person_you` at real call sites) always wins for participant #1 —
     * the split belongs to the splitter, so they default to "You" rather than a "Person 1"
     * placeholder.
     */
    fun defaultParticipantName(
        index: Int,
        nameTemplate: String = DEFAULT_NAME_TEMPLATE,
        firstPersonName: String = DEFAULT_FIRST_PERSON_NAME,
    ): String =
        if (index == 1) {
            firstPersonName
        } else {
            nameTemplate.replace("%1\$d", index.toString()).replace("%d", index.toString())
        }

    fun defaultNames(
        count: Int,
        nameTemplate: String = DEFAULT_NAME_TEMPLATE,
        firstPersonName: String = DEFAULT_FIRST_PERSON_NAME,
    ): List<String> = (1..count).map { defaultParticipantName(it, nameTemplate, firstPersonName) }

    fun previewNames(count: Int): List<String> =
        when (count) {
            4 -> listOf("Aiko", "Ben", "Carlos", "Dee")
            else -> defaultNames(count)
        }

    fun syncNames(
        current: List<String>,
        count: Int,
        nameTemplate: String = DEFAULT_NAME_TEMPLATE,
        firstPersonName: String = DEFAULT_FIRST_PERSON_NAME,
    ): List<String> {
        if (current.size == count) return current
        return (0 until count).map { index ->
            current.getOrNull(index) ?: defaultParticipantName(index + 1, nameTemplate, firstPersonName)
        }
    }

    /** Save-time normalization: a cleared (blank) name falls back to its "Person N" default
     *  ("You" for participant #1). */
    fun resolveNames(
        current: List<String>,
        count: Int,
        nameTemplate: String = DEFAULT_NAME_TEMPLATE,
        firstPersonName: String = DEFAULT_FIRST_PERSON_NAME,
    ): List<String> =
        syncNames(current, count, nameTemplate, firstPersonName).mapIndexed { index, name ->
            name.trim().ifEmpty { defaultParticipantName(index + 1, nameTemplate, firstPersonName) }
        }

    fun syncCustomShares(
        current: List<String>,
        count: Int,
        rawTotal: String,
    ): List<String> {
        if (current.size == count) return current
        val equalShare =
            (equalShareCents(rawTotal, count.coerceAtLeast(1)) / 100.0)
                .let { if (it % 1.0 == 0.0) it.toLong().toString() else it.toString() }
        return (0 until count).map { index ->
            current.getOrNull(index) ?: equalShare
        }
    }

    /** Sum of the custom per-person shares, in cents — used to show whether a Custom split
     *  "matches" the total or still diverges from it. Shares are never auto-rebalanced, so this
     *  can legitimately land above or below [totalCents]. */
    fun customShareSumCents(customShareRaws: List<String>): Long = customShareRaws.sumOf { totalCents(it) }

    fun formatShareRaw(
        rawShare: String,
        currencySymbol: String = "$",
    ): String {
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
        nameTemplate: String = DEFAULT_NAME_TEMPLATE,
    ): List<Pair<String, String>> {
        val resolvedNames = syncNames(names, peopleCount, nameTemplate)
        return resolvedNames.mapIndexed { index, name ->
            val shareLabel =
                when (mode) {
                    SharedSplitMode.Equal -> formatCents(equalShareCents(rawTotal, peopleCount), currencySymbol)
                    SharedSplitMode.Custom ->
                        formatShareRaw(
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
