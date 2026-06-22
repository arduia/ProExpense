package com.arduia.expense.ui.format

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Currency
import java.util.Date
import java.util.Locale
import kotlin.math.abs

/**
 * Pure formatting helpers shared by the Home read-model and the entry mappers. Uses java.util
 * (SimpleDateFormat/Calendar) rather than java.time so it is safe on minSdk 24 without desugaring.
 */
object MoneyFormatter {

    fun symbolFor(currencyCode: String): String = try {
        Currency.getInstance(currencyCode).symbol
    } catch (e: IllegalArgumentException) {
        "$"
    }

    /** cents (in [currencyCode]) → e.g. "$1,240.00" / "-$22.00". */
    fun format(cents: Long, currencyCode: String): String {
        val symbol = symbolFor(currencyCode)
        val negative = cents < 0
        val absCents = abs(cents)
        val whole = absCents / 100
        val fraction = (absCents % 100).toInt()
        val grouped = String.format(Locale.US, "%,d", whole)
        val sign = if (negative) "-" else ""
        return "$sign$symbol$grouped.${fraction.toString().padStart(2, '0')}"
    }
}

object DateLabels {

    private val topBar = SimpleDateFormat("EEE · MMM d", Locale.US)
    private val month = SimpleDateFormat("MMM", Locale.US)
    private val dayMonth = SimpleDateFormat("MMM d", Locale.US)
    private val weekdayMonth = SimpleDateFormat("EEE · MMM d", Locale.US)
    private val detailDateTime = SimpleDateFormat("EEE, MMM d · h:mm a", Locale.US)
    private val monthYear = SimpleDateFormat("MMM yyyy", Locale.US)
    private val monthKeyFormat = SimpleDateFormat("yyyy-MM", Locale.US)
    private val time = SimpleDateFormat("h:mm a", Locale.US)

    fun topBar(nowMillis: Long): String = topBar.format(Date(nowMillis)).uppercase(Locale.US)

    fun monthLabel(nowMillis: Long): String = month.format(Date(nowMillis)).uppercase(Locale.US)

    /** "May 2026" — the Reports period heading. */
    fun monthYearLabel(millis: Long): String = monthYear.format(Date(millis))

    /** "yyyy-MM" — stable key for bucketing records by calendar month. */
    fun monthKey(millis: Long): String = monthKeyFormat.format(Date(millis))

    /**
     * Days counted toward a month's daily average: the elapsed day-of-month for the current month,
     * otherwise the full length of that (past) month.
     */
    fun daysElapsedInMonth(monthMillis: Long, nowMillis: Long): Int {
        val monthCal = Calendar.getInstance().apply { timeInMillis = monthMillis }
        val nowCal = Calendar.getInstance().apply { timeInMillis = nowMillis }
        val sameMonth = monthCal.get(Calendar.YEAR) == nowCal.get(Calendar.YEAR) &&
            monthCal.get(Calendar.MONTH) == nowCal.get(Calendar.MONTH)
        return if (sameMonth) {
            nowCal.get(Calendar.DAY_OF_MONTH)
        } else {
            monthCal.getActualMaximum(Calendar.DAY_OF_MONTH)
        }
    }

    fun timeLabel(millis: Long): String = time.format(Date(millis))

    /** "Wed, May 25 · 12:30 PM" — the journal detail header stamp. */
    fun detailDateTime(millis: Long): String = detailDateTime.format(Date(millis))

    /** "May 12 — May 26" (or a single day when start == end) — event date ranges. */
    fun eventRange(startMillis: Long, endMillis: Long): String {
        val start = dayMonth.format(Date(startMillis))
        val end = dayMonth.format(Date(endMillis))
        return if (start == end) start else "$start — $end"
    }

    /** "Today · May 25" / "Yesterday · May 24" / "Mon · May 23". */
    fun dayGroupTitle(dayMillis: Long, nowMillis: Long): String = when (dayDelta(dayMillis, nowMillis)) {
        0 -> "Today · ${dayMonth.format(Date(dayMillis))}"
        1 -> "Yesterday · ${dayMonth.format(Date(dayMillis))}"
        else -> weekdayMonth.format(Date(dayMillis))
    }

    /** Local start-of-day epoch millis for the day containing [millis]. */
    fun startOfDay(millis: Long): Long = Calendar.getInstance().apply {
        timeInMillis = millis
        atStartOfDay()
    }.timeInMillis

    /** Local start-of-month epoch millis for the month containing [nowMillis]. */
    fun startOfMonth(nowMillis: Long): Long = Calendar.getInstance().apply {
        timeInMillis = nowMillis
        set(Calendar.DAY_OF_MONTH, 1)
        atStartOfDay()
    }.timeInMillis

    /** Whole calendar days from [dayMillis] back to today (0 = same day). */
    private fun dayDelta(dayMillis: Long, nowMillis: Long): Int {
        val start = Calendar.getInstance().apply { timeInMillis = dayMillis; atStartOfDay() }
        val today = Calendar.getInstance().apply { timeInMillis = nowMillis; atStartOfDay() }
        val diffMs = today.timeInMillis - start.timeInMillis
        return (diffMs / DAY_MS).toInt()
    }

    private fun Calendar.atStartOfDay() {
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }

    private const val DAY_MS = 24L * 60 * 60 * 1000
}
