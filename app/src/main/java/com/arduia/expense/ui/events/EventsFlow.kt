package com.arduia.expense.ui.events

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.arduia.expense.R
import com.arduia.expense.ui.design.AmountInput
import com.arduia.expense.ui.design.EventBudgetCardState
import com.arduia.expense.ui.design.EventBudgetSummaryState
import com.arduia.expense.ui.design.EventBudgetTone
import com.arduia.expense.ui.design.HomeNavTab
import com.arduia.expense.ui.design.ProBottomSheetHost
import com.arduia.expense.ui.preview.EventCreateFormState
import com.arduia.expense.ui.preview.EventDetailUiState
import com.arduia.expense.ui.preview.previewEventDetail
import com.arduia.expense.ui.preview.previewEventDetailClosed
import com.arduia.expense.ui.preview.previewEventDetailWarn
import com.arduia.expense.ui.preview.previewEventList
import com.arduia.expense.ui.theme.ProArtboard
import com.arduia.expense.ui.theme.ProExpenseTheme
import com.arduia.expense.ui.theme.rememberProReduceMotion
import com.arduia.expense.ui.theme.stepTransition

@Composable
fun EventsFlow(
    onTabSelected: (HomeNavTab) -> Unit,
    onAddClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = ProExpenseTheme.colors
    val motion = ProExpenseTheme.motion
    val reduceMotion = rememberProReduceMotion()

    val events = remember { mutableStateListOf<EventBudgetCardState>().apply { addAll(previewEventList) } }
    var selectedEventId by remember { mutableStateOf<String?>(null) }
    var showCreate by remember { mutableStateOf(false) }
    var form by remember { mutableStateOf(EventCreateFormState()) }

    fun isDuplicate(name: String): Boolean =
        name.isNotBlank() && events.any { it.title.equals(name.trim(), ignoreCase = true) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(colors.paper),
    ) {
        AnimatedContent(
            targetState = selectedEventId,
            transitionSpec = {
                motion.stepTransition(
                    fromIndex = if (initialState == null) 0 else 1,
                    toIndex = if (targetState == null) 0 else 1,
                    reduceMotion = reduceMotion,
                )
            },
            label = "eventsStep",
        ) { targetId ->
            if (targetId == null) {
                EventBudgetListScreen(
                    events = events,
                    onCreateEvent = {
                        form = EventCreateFormState()
                        showCreate = true
                    },
                    onEventClick = { selectedEventId = it },
                    selectedTab = HomeNavTab.Budget,
                    onTabSelected = onTabSelected,
                    onAddClick = onAddClick,
                )
            } else {
                EventDetailScreen(
                    state = detailStateFor(targetId, events),
                    onBack = { selectedEventId = null },
                    onMore = {},
                    onAddTagged = onAddClick,
                    onExpenseClick = {},
                )
            }
        }

        ProBottomSheetHost(
            visible = showCreate,
            title = stringResource(R.string.event_create_title),
            onClose = { showCreate = false },
        ) {
            EventCreateSheetContent(
                form = form,
                onNameChange = { name ->
                    form = form.copy(name = name, isDuplicateName = isDuplicate(name))
                },
                onBudgetChange = { raw ->
                    form = form.copy(budgetRaw = raw, showBudgetError = false)
                },
                onPickStart = {},
                onPickEnd = {},
                onSave = {
                    if (!form.canSave) {
                        form = form.copy(showBudgetError = true, isDuplicateName = isDuplicate(form.name))
                    } else {
                        events.add(
                            0,
                            EventBudgetCardState(
                                id = form.name.trim().lowercase().replace(" ", "-"),
                                title = form.name.trim(),
                                dateRange = "${form.startLabel} — ${form.endLabel}",
                                spentLabel = "$0",
                                budgetLabel = "of $" + AmountInput.formatDisplay(form.budgetRaw),
                                progress = 0f,
                            ),
                        )
                        showCreate = false
                        form = EventCreateFormState()
                    }
                },
            )
        }
    }
}

private fun detailStateFor(
    id: String,
    events: List<EventBudgetCardState>,
): EventDetailUiState = when (id) {
    "bali" -> previewEventDetail
    "birthday" -> previewEventDetailWarn
    "wedding" -> previewEventDetailClosed
    else -> {
        val event = events.firstOrNull { it.id == id }
        EventDetailUiState(
            id = id,
            title = event?.title.orEmpty(),
            subtitle = event?.dateRange.orEmpty(),
            statusEyebrow = "ACTIVE",
            summary = EventBudgetSummaryState(
                eyebrow = "REMAINING",
                remainingLabel = event?.budgetLabel?.removePrefix("of ").orEmpty(),
                spentLabel = "$0",
                budgetLabel = event?.budgetLabel?.removePrefix("of ").orEmpty(),
                spentCaption = "Spent",
                budgetCaption = "Budget",
                progress = 0f,
                tone = EventBudgetTone.OnTrack,
            ),
            showAddTagged = true,
        )
    }
}

@Preview(
    name = "Events flow — list",
    widthDp = ProArtboard.PIXEL_9_PRO_WIDTH_DP,
    heightDp = ProArtboard.PIXEL_9_PRO_HEIGHT_DP,
    showBackground = true,
)
@Composable
private fun EventsFlowPreview() {
    ProExpenseTheme {
        EventsFlow(
            onTabSelected = {},
            onAddClick = {},
        )
    }
}
