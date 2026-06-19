package com.arduia.expense.ui.logging

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.arduia.expense.R
import com.arduia.expense.di.AppGraph
import com.arduia.expense.feature.logging.AddExpenseViewModel
import com.arduia.expense.feature.logging.AmountInputLogic
import com.arduia.expense.ui.design.ProBottomSheetHost
import com.arduia.expense.ui.design.ProButton
import com.arduia.expense.ui.navigation.stepTransition
import com.arduia.expense.ui.theme.ProArtboard
import com.arduia.expense.ui.theme.ProExpenseTheme
import com.arduia.expense.ui.theme.rememberProReduceMotion
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.Locale

enum class QuickLogStep {
    Amount,
    Details,
}

private val quickLogDateOptions = listOf(
    LocalDate.now(),
    LocalDate.now().minusDays(1),
    LocalDate.now().minusDays(2),
)

private val quickLogTimeOptions = listOf(
    LocalTime.of(12, 30),
    LocalTime.of(9, 0),
    LocalTime.of(18, 45),
)

@Composable
fun QuickLogFlow(
    viewModel: AddExpenseViewModel,
    onDismiss: () -> Unit,
    onSaved: () -> Unit,
    modifier: Modifier = Modifier,
    initialStep: QuickLogStep = QuickLogStep.Amount,
) {
    val uiState by viewModel.uiState.collectAsState()
    var step by rememberSaveable { mutableStateOf(initialStep) }
    var eventTag by rememberSaveable { mutableStateOf<String?>(null) }
    var showCustomCategories by rememberSaveable { mutableStateOf(false) }
    var dateIndex by rememberSaveable { mutableIntStateOf(0) }
    var timeIndex by rememberSaveable { mutableIntStateOf(0) }
    var showDateTimeSheet by rememberSaveable { mutableStateOf(false) }
    val motion = ProExpenseTheme.motion
    val reduceMotion = rememberProReduceMotion()
    val locale = LocalContext.current.resources.configuration.locales[0] ?: Locale.getDefault()
    val todayLabel = stringResource(R.string.today)
    val dateLabel = remember(dateIndex, locale, todayLabel) {
        formatQuickLogDate(quickLogDateOptions[dateIndex], locale, todayLabel)
    }
    val timeLabel = remember(timeIndex, locale) {
        quickLogTimeOptions[timeIndex].format(DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT).withLocale(locale))
    }

    AnimatedContent(
        targetState = step,
        modifier = modifier,
        transitionSpec = {
            stepTransition(
                motion = motion,
                fromIndex = initialState.ordinal,
                toIndex = targetState.ordinal,
                reduceMotion = reduceMotion,
            )
        },
        label = "quickLogNav",
    ) { currentStep ->
        when (currentStep) {
            QuickLogStep.Amount -> AddAmountScreen(
                modifier = Modifier,
                amount = uiState.amount,
                selectedCategoryId = uiState.selectedCategoryId,
                showCustomCategories = showCustomCategories,
                onAmountChange = viewModel::onAmountChanged,
                onCategorySelected = viewModel::onCategorySelected,
                onMoreCategoriesClick = { showCustomCategories = true },
                onClose = onDismiss,
                onSave = {
                    viewModel.onSaveFromAmountOnly()
                    onSaved()
                },
                onNext = { step = QuickLogStep.Details },
            )
            QuickLogStep.Details -> {
                val display = AmountInputLogic.toDisplay(uiState.amount)
                val amountLabel = "$${
                    if (display.decimal != null) "${display.whole}.${display.decimal}" else display.whole
                }"
                AddDetailsScreen(
                    modifier = Modifier,
                    amountLabel = amountLabel,
                    selectedCategoryId = uiState.selectedCategoryId,
                    note = uiState.note,
                    dateLabel = dateLabel,
                    timeLabel = timeLabel,
                    eventTag = eventTag,
                    onNoteChange = viewModel::onNoteChanged,
                    onCategorySelected = viewModel::onCategorySelected,
                    onBack = { step = QuickLogStep.Amount },
                    onEditAmount = { step = QuickLogStep.Amount },
                    onClearTag = { eventTag = null },
                    onDateTimeClick = { showDateTimeSheet = true },
                    onSave = {
                        viewModel.onSaveWithDetails()
                        onSaved()
                    },
                )
            }
        }
    }

    if (showDateTimeSheet) {
        ProBottomSheetHost(
            title = stringResource(R.string.date_time_picker_title),
            onClose = { showDateTimeSheet = false },
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(ProExpenseTheme.dimensions.space8)) {
                quickLogDateOptions.forEachIndexed { index, date ->
                    ProButton(
                        text = formatQuickLogDate(date, locale, todayLabel),
                        onClick = {
                            dateIndex = index
                            showDateTimeSheet = false
                        },
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
                quickLogTimeOptions.forEachIndexed { index, time ->
                    ProButton(
                        text = time.format(DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT).withLocale(locale)),
                        onClick = {
                            timeIndex = index
                            showDateTimeSheet = false
                        },
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            }
        }
    }
}

private fun formatQuickLogDate(date: LocalDate, locale: Locale, todayLabel: String): String {
    val prefix = if (date == LocalDate.now()) todayLabel else {
        date.format(DateTimeFormatter.ofPattern("EEE", locale))
    }
    val body = date.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM).withLocale(locale))
    return "$prefix, $body"
}

@Preview(
    name = "QuickLogFlow — amount",
    widthDp = ProArtboard.PIXEL_9_PRO_WIDTH_DP,
    heightDp = ProArtboard.PIXEL_9_PRO_HEIGHT_DP,
    showBackground = true,
)
@Composable
private fun QuickLogFlowAmountPreview() {
    val graph = AppGraph(kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.Main))
    val viewModel = graph.prewarmAddExpenseViewModel(onSaved = {}, onSaveFailed = {})
    ProExpenseTheme {
        QuickLogFlow(viewModel = viewModel, onDismiss = {}, onSaved = {})
    }
}
