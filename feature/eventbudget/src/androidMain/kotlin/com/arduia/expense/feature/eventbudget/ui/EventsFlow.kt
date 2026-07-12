package com.arduia.expense.feature.eventbudget.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.tooling.preview.Preview
import com.arduia.expense.feature.eventbudget.R
import com.arduia.expense.feature.eventbudget.ui.preview.EventCreateFormState
import com.arduia.expense.feature.eventbudget.ui.preview.EventDetailUiState
import com.arduia.expense.feature.eventbudget.ui.preview.previewEventList
import com.arduia.expense.ui.design.DateTimePickerSheet
import com.arduia.expense.ui.design.EventBudgetCardState
import com.arduia.expense.ui.design.EventBudgetSummaryState
import com.arduia.expense.ui.design.EventBudgetTone
import com.arduia.expense.ui.design.HomeNavTab
import com.arduia.expense.ui.design.PlatformDateFormatter
import com.arduia.expense.ui.design.ProAlertDialog
import com.arduia.expense.ui.design.ProBottomSheetHost
import com.arduia.expense.ui.design.ProButtonVariant
import com.arduia.expense.ui.design.ProIconGlyph
import com.arduia.expense.ui.theme.ProArtboard
import com.arduia.expense.ui.theme.ProExpenseTheme
import com.arduia.expense.ui.theme.rememberProReduceMotion
import com.arduia.expense.ui.theme.stepTransition

@Composable
fun EventsFlow(
    onTabSelected: (HomeNavTab) -> Unit,
    onAddClick: () -> Unit,
    events: List<EventBudgetCardState> = previewEventList,
    eventDetails: Map<String, EventDetailUiState> = emptyMap(),
    eventEditForms: Map<String, EventCreateFormState> = emptyMap(),
    // `onCreated` fires once the event is actually persisted (id resolved async) — the caller
    // uses it to navigate straight to the new event's detail instead of just closing the sheet.
    onCreateEvent: (
        name: String,
        budgetRaw: String,
        startEpochMillis: Long,
        endEpochMillis: Long,
        onCreated: (eventId: String) -> Unit,
    ) -> Unit = { _, _, _, _, _ -> },
    onUpdateEvent: (
        id: String,
        name: String,
        budgetRaw: String,
        startEpochMillis: Long,
        endEpochMillis: Long,
    ) -> Unit = { _, _, _, _, _ -> },
    onCloseEvent: (id: String) -> Unit = {},
    onArchiveEvent: (id: String) -> Unit = {},
    onDeleteEvent: (id: String) -> Unit = {},
    initialSelectedEventId: String? = null,
    onAddTaggedExpense: (eventId: String) -> Unit = { onAddClick() },
    onExpenseClick: (recordId: String) -> Unit = {},
    isLoading: Boolean = false,
    modifier: Modifier = Modifier,
) {
    val colors = ProExpenseTheme.colors
    val motion = ProExpenseTheme.motion
    val reduceMotion = rememberProReduceMotion()

    var selectedEventId by remember { mutableStateOf(initialSelectedEventId) }
    var showCreate by remember { mutableStateOf(false) }
    var editingEventId by remember { mutableStateOf<String?>(null) }
    var form by remember { mutableStateOf(EventCreateFormState()) }
    var showStartPicker by remember { mutableStateOf(false) }
    var showEndPicker by remember { mutableStateOf(false) }
    var showActionsSheet by remember { mutableStateOf(false) }
    var showCloseConfirm by remember { mutableStateOf(false) }
    var archiveTargetId by remember { mutableStateOf<String?>(null) }
    var deleteTargetId by remember { mutableStateOf<String?>(null) }

    fun isDuplicate(name: String): Boolean =
        name.isNotBlank() &&
            events.any { it.title.equals(name.trim(), ignoreCase = true) && it.id != editingEventId }

    Box(
        modifier =
            modifier
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
                    isLoading = isLoading,
                )
            } else {
                val detail = detailStateFor(targetId, events, eventDetails)
                EventDetailScreen(
                    state = detail,
                    onBack = { selectedEventId = null },
                    onMore = { if (!detail.readOnly) showActionsSheet = true },
                    onAddTagged = { onAddTaggedExpense(targetId) },
                    onExpenseClick = onExpenseClick,
                )
            }
        }

        ProBottomSheetHost(
            visible = showActionsSheet,
            title = null,
            onClose = { showActionsSheet = false },
        ) {
            EventActionsSheetContent(
                onEdit = {
                    showActionsSheet = false
                    selectedEventId?.let { id ->
                        editingEventId = id
                        form = eventEditForms[id] ?: EventCreateFormState()
                        showCreate = true
                    }
                },
                onClose = {
                    showActionsSheet = false
                    showCloseConfirm = true
                },
                onArchive = {
                    showActionsSheet = false
                    archiveTargetId = selectedEventId
                },
                onDelete = {
                    showActionsSheet = false
                    deleteTargetId = selectedEventId
                },
                onCancel = { showActionsSheet = false },
                showClose = selectedEventId?.let { !detailStateFor(it, events, eventDetails).isClosed } ?: true,
            )
        }

        ProBottomSheetHost(
            visible = showCreate,
            title = stringResource(if (editingEventId != null) R.string.event_edit_title else R.string.event_create_title),
            onClose = {
                showCreate = false
                editingEventId = null
            },
        ) {
            EventCreateSheetContent(
                form = form,
                onNameChange = { name ->
                    form = form.copy(name = name, isDuplicateName = isDuplicate(name))
                },
                onBudgetChange = { raw ->
                    form = form.copy(budgetRaw = raw, showBudgetError = false)
                },
                onPickStart = { showStartPicker = true },
                onPickEnd = { showEndPicker = true },
                onSave = {
                    if (!form.canSave) {
                        form = form.copy(showBudgetError = true, isDuplicateName = isDuplicate(form.name))
                    } else {
                        val editingId = editingEventId
                        // Not resetting `form` here: it's still read by this sheet's content
                        // while ProBottomSheetHost's exit animation plays, so clearing it now
                        // would flash an empty form for the animation's duration. Every entry
                        // point (create/edit) already sets a fresh `form` before showing again.
                        if (editingId != null) {
                            onUpdateEvent(editingId, form.name.trim(), form.budgetRaw, form.startEpochMillis, form.endEpochMillis)
                            showCreate = false
                            editingEventId = null
                        } else {
                            // Keep the sheet open until the new id resolves — closing it first
                            // would flash the List screen for a frame before Detail takes over.
                            onCreateEvent(
                                form.name.trim(),
                                form.budgetRaw,
                                form.startEpochMillis,
                                form.endEpochMillis,
                            ) { newId ->
                                selectedEventId = newId
                                showCreate = false
                            }
                        }
                    }
                },
            )
        }

        selectedEventId?.let { id ->
            val eventTitle = detailStateFor(id, events, eventDetails).title
            ProAlertDialog(
                visible = showCloseConfirm,
                icon = ProIconGlyph.Close,
                iconTint = colors.danger,
                iconBackground = colors.dangerTint,
                title = stringResource(R.string.event_close_confirm_title, eventTitle),
                body = buildAnnotatedString { append(stringResource(R.string.event_close_confirm_body)) },
                confirmLabel = stringResource(R.string.event_close_confirm),
                onConfirm = {
                    onCloseEvent(id)
                    showCloseConfirm = false
                },
                dismissLabel = stringResource(R.string.event_actions_cancel),
                onDismiss = { showCloseConfirm = false },
                confirmVariant = ProButtonVariant.Danger,
            )
        }

        archiveTargetId?.let { id ->
            val eventTitle = detailStateFor(id, events, eventDetails).title
            ProAlertDialog(
                visible = true,
                icon = ProIconGlyph.EyeOff,
                iconTint = colors.onSurfaceVariant,
                iconBackground = colors.paperAlt,
                title = stringResource(R.string.event_archive_confirm_title, eventTitle),
                body = buildAnnotatedString { append(stringResource(R.string.event_archive_confirm_body)) },
                confirmLabel = stringResource(R.string.event_archive_confirm),
                onConfirm = {
                    onArchiveEvent(id)
                    if (selectedEventId == id) selectedEventId = null
                    archiveTargetId = null
                },
                dismissLabel = stringResource(R.string.event_actions_cancel),
                onDismiss = { archiveTargetId = null },
                confirmVariant = ProButtonVariant.Warning,
            )
        }

        deleteTargetId?.let { id ->
            val eventTitle = detailStateFor(id, events, eventDetails).title
            ProAlertDialog(
                visible = true,
                icon = ProIconGlyph.Close,
                iconTint = colors.danger,
                iconBackground = colors.dangerTint,
                title = stringResource(R.string.event_delete_confirm_title, eventTitle),
                body = buildAnnotatedString { append(stringResource(R.string.event_delete_confirm_body)) },
                confirmLabel = stringResource(R.string.event_delete_confirm),
                onConfirm = {
                    onDeleteEvent(id)
                    if (selectedEventId == id) selectedEventId = null
                    deleteTargetId = null
                },
                dismissLabel = stringResource(R.string.event_actions_cancel),
                onDismiss = { deleteTargetId = null },
                confirmVariant = ProButtonVariant.Danger,
            )
        }

        DateTimePickerSheet(
            visible = showStartPicker,
            initialEpochMillis = form.startEpochMillis,
            onConfirm = { millis ->
                form = form.copy(startEpochMillis = millis, startLabel = PlatformDateFormatter.shortDateLabel(millis))
                showStartPicker = false
            },
            onDismiss = { showStartPicker = false },
        )

        DateTimePickerSheet(
            visible = showEndPicker,
            initialEpochMillis = form.endEpochMillis,
            onConfirm = { millis ->
                form = form.copy(endEpochMillis = millis, endLabel = PlatformDateFormatter.shortDateLabel(millis))
                showEndPicker = false
            },
            onDismiss = { showEndPicker = false },
        )
    }
}

private fun detailStateFor(
    id: String,
    events: List<EventBudgetCardState>,
    eventDetails: Map<String, EventDetailUiState>,
): EventDetailUiState {
    eventDetails[id]?.let { return it }
    val event = events.firstOrNull { it.id == id }
    return EventDetailUiState(
        id = id,
        title = event?.title.orEmpty(),
        subtitle = event?.dateRange.orEmpty(),
        statusEyebrow = "ACTIVE",
        summary =
            EventBudgetSummaryState(
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
