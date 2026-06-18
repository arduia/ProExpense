package com.arduia.expense.feature.logging.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.arduia.expense.data.Result
import com.arduia.expense.domain.Amount
import com.arduia.expense.domain.CurrencyCode
import com.arduia.expense.domain.currencySymbolFor
import com.arduia.expense.domain.formatWithSymbol
import com.arduia.expense.feature.logging.AmountInputLogic
import com.arduia.expense.feature.logging.LogRecordInput
import com.arduia.expense.feature.logging.LoggingRepository
import com.arduia.expense.feature.logging.PreviewLoggingRepository
import com.arduia.expense.feature.logging.amountFromCents
import com.arduia.expense.ui.theme.ProExpenseTheme
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

private enum class QuickLogStep {
    Amount,
    Details,
}

@Composable
fun QuickLogFlow(
    homeCurrencyCode: String,
    loggingRepository: LoggingRepository,
    onDismiss: () -> Unit,
    onSaved: (String) -> Unit,
    modifier: Modifier = Modifier,
    zoneId: ZoneId = ZoneId.systemDefault(),
) {
    var step by rememberSaveable { mutableStateOf(QuickLogStep.Amount) }
    var amountInput by rememberSaveable { mutableStateOf("") }
    var categoryId by rememberSaveable { mutableStateOf("food") }
    var note by rememberSaveable { mutableStateOf("") }
    var showZeroError by rememberSaveable { mutableStateOf(false) }
    val recordedAtMillis = remember { System.currentTimeMillis() }
    val scope = rememberCoroutineScope()
    val currencySymbol = remember(homeCurrencyCode) { currencySymbolFor(homeCurrencyCode) }
    val canProceed = AmountInputLogic.canProceed(amountInput)

    val dateFormatter = remember { DateTimeFormatter.ofPattern("EEE, MMM d") }
    val timeFormatter = remember { DateTimeFormatter.ofPattern("h:mm a") }
    val zoned = remember(recordedAtMillis) {
        Instant.ofEpochMilli(recordedAtMillis).atZone(zoneId)
    }

    fun saveRecord(onComplete: () -> Unit) {
        val cents = AmountInputLogic.toCents(amountInput) ?: return
        scope.launch {
            val amount = amountFromCents(cents)
            when (
                val result = loggingRepository.createRecord(
                    LogRecordInput(
                        amount = amount,
                        currency = CurrencyCode(homeCurrencyCode),
                        homeCurrencyAmount = amount,
                        categoryId = categoryId,
                        note = note.ifBlank { null },
                        recordedAtEpochMillis = recordedAtMillis,
                    ),
                )
            ) {
                is Result.Success -> {
                    onSaved(result.data.id)
                    onComplete()
                }
                is Result.Error -> Unit
            }
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        when (step) {
            QuickLogStep.Amount -> AddAmountScreen(
                amountInput = amountInput,
                currencyCode = homeCurrencyCode,
                currencySymbol = currencySymbol,
                selectedCategoryId = categoryId,
                showZeroError = showZeroError,
                canProceed = canProceed,
                onKey = { key ->
                    amountInput = AmountInputLogic.applyKey(amountInput, key)
                    showZeroError = false
                },
                onCategorySelected = { categoryId = it },
                onCancel = onDismiss,
                onNext = {
                    if (canProceed) {
                        showZeroError = false
                        step = QuickLogStep.Details
                    } else {
                        showZeroError = true
                    }
                },
                onQuickSave = {
                    if (canProceed) {
                        saveRecord(onDismiss)
                    } else {
                        showZeroError = true
                    }
                },
            )
            QuickLogStep.Details -> {
                val cents = AmountInputLogic.toCents(amountInput) ?: 0L
                AddDetailsScreen(
                    formattedAmount = Amount(cents).formatWithSymbol(homeCurrencyCode),
                    selectedCategoryId = categoryId,
                    dateLabel = zoned.format(dateFormatter),
                    timeLabel = zoned.format(timeFormatter),
                    note = note,
                    onBack = { step = QuickLogStep.Amount },
                    onCategorySelected = { categoryId = it },
                    onNoteChange = { note = it },
                    onSave = { saveRecord(onDismiss) },
                )
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 414, heightDp = 868)
@Composable
private fun QuickLogFlowAmountPreview() {
    ProExpenseTheme {
        QuickLogFlow(
            homeCurrencyCode = "USD",
            loggingRepository = PreviewLoggingRepository,
            onDismiss = {},
            onSaved = {},
        )
    }
}
