package com.arduia.expense.feature.sharedcost.ui

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.arduia.expense.feature.sharedcost.R
import com.arduia.expense.feature.sharedcost.SharedSplitMode
import com.arduia.expense.feature.sharedcost.ui.preview.SharedCostParticipantUi
import com.arduia.expense.feature.sharedcost.ui.preview.previewSharedEditPersonCustom
import com.arduia.expense.feature.sharedcost.ui.preview.previewSharedEditPersonEqual
import com.arduia.expense.ui.design.AmountInput
import com.arduia.expense.ui.design.DigitKeypadGrid
import com.arduia.expense.ui.design.ProButton
import com.arduia.expense.ui.design.ProButtonSize
import com.arduia.expense.ui.design.ProButtonVariant
import com.arduia.expense.ui.design.ProIcon
import com.arduia.expense.ui.design.ProIconGlyph
import com.arduia.expense.ui.design.proRippleClickable
import com.arduia.expense.ui.theme.ProArtboard
import com.arduia.expense.ui.theme.ProExpenseTheme
import com.arduia.expense.ui.theme.centeredGlyph

/** Starting scale for the name-text bounce-in on person switch — small enough to read as a
 *  deliberate "pop", not a jarring jump. */
private const val NAME_BOUNCE_START_SCALE = 0.82f

/**
 * Compact roster of every person in the split, shown at the top of the Edit-person sheet for
 * context and quick jump — the active person shows an edit glyph, a person who has actually been
 * visited/finished (via Next, Done, or picking someone else) shows a check, and anyone not yet
 * reached shows no trailing mark at all.
 */
@Composable
fun SharedCostPeopleRoster(
    people: List<SharedCostParticipantUi>,
    activeIndex: Int,
    onPick: (Int) -> Unit,
    modifier: Modifier = Modifier,
    visitedIndices: Set<Int> = emptySet(),
) {
    val colors = ProExpenseTheme.colors
    val dimens = ProExpenseTheme.dimensions
    val typography = ProExpenseTheme.typography
    val shape = ProExpenseTheme.shapes.tile

    Column(
        modifier =
            modifier
                .fillMaxWidth()
                .clip(ProExpenseTheme.shapes.card)
                .border(BorderStroke(1.dp, colors.line), ProExpenseTheme.shapes.card)
                .background(colors.paper)
                .padding(dimens.space8),
    ) {
        people.forEachIndexed { index, person ->
            val active = index == activeIndex
            Row(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .clip(shape)
                        .background(if (active) colors.primarySoft else Color.Transparent)
                        .proRippleClickable(onClick = { onPick(index) })
                        .padding(horizontal = dimens.space8, vertical = dimens.space8),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(dimens.space8),
            ) {
                Box(
                    modifier =
                        Modifier
                            .size(dimens.space26)
                            .clip(CircleShape)
                            .background(if (active) colors.primary else colors.primarySoft),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = (index + 1).toString(),
                        style = typography.captionMedium.centeredGlyph(),
                        color = if (active) colors.onPrimaryWarm else colors.primary,
                        textAlign = TextAlign.Center,
                    )
                }
                Text(
                    text = person.name,
                    style = if (active) typography.bodySemiBold else typography.captionMedium,
                    color = if (active) colors.onSurface else colors.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f),
                )
                Text(
                    text = person.shareLabel,
                    style = typography.captionMedium,
                    color = if (active) colors.onSurface else colors.onSurfaceVariant,
                )
                when {
                    active ->
                        ProIcon(
                            glyph = ProIconGlyph.Edit,
                            contentDescription = null,
                            tint = colors.primary,
                            size = dimens.iconTag,
                        )
                    index in visitedIndices ->
                        ProIcon(
                            glyph = ProIconGlyph.Check,
                            contentDescription = null,
                            tint = colors.success,
                            size = dimens.iconTag,
                        )
                }
            }
        }
    }
}

/**
 * Dedicated name + amount editor for one person, reached only from Screen 1's edit-icon buttons
 * (`EditPersonSheetV4` in the handoff). Name is always a real text field; the amount is editable
 * only in Custom mode, driven by an on-screen keypad (matching the Add Expense amount pattern)
 * rather than the system IME — Equal-mode amounts are computed, not entered.
 */
@Composable
fun SharedCostsEditPersonSheetContent(
    people: List<SharedCostParticipantUi>,
    activeIndex: Int,
    mode: SharedSplitMode,
    activeAmountRaw: String,
    equalShareLabel: String,
    onPickPerson: (Int) -> Unit,
    onNameChange: (String) -> Unit,
    // `freshEntry` is true for the first key/backspace right after the amount field is tapped —
    // the field arrives pre-filled with the computed equal share (or a previously-set custom
    // value), and typing is meant to overwrite it outright rather than append onto it, exactly
    // like the old inline field's "select all on focus" behavior it replaces.
    onAmountKey: (key: String, freshEntry: Boolean) -> Unit,
    onAmountBackspace: (freshEntry: Boolean) -> Unit,
    onDone: () -> Unit,
    onNext: () -> Unit,
    modifier: Modifier = Modifier,
    homeCurrencySymbol: String = "$",
    visitedIndices: Set<Int> = emptySet(),
) {
    val colors = ProExpenseTheme.colors
    val dimens = ProExpenseTheme.dimensions
    val typography = ProExpenseTheme.typography
    val shape = ProExpenseTheme.shapes.tile
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val isCustom = mode == SharedSplitMode.Custom
    var showAmountKeypad by remember(activeIndex) { mutableStateOf(false) }
    var amountFreshEntry by remember(activeIndex) { mutableStateOf(true) }
    var nameHasFocus by remember(activeIndex) { mutableStateOf(false) }
    val fieldActive = nameHasFocus || showAmountKeypad
    val activePerson = people.getOrNull(activeIndex)
    val activeAmountDisplay = AmountInput.formatDisplay(activeAmountRaw.ifEmpty { "0" })
    val nameFocusRequester = remember(activeIndex) { FocusRequester() }
    // Selecting the whole name on open/person-switch lets the first keystroke overwrite it
    // outright — the same "select all on focus" UX the amount field's freshEntry flag gives.
    var nameFieldValue by
        remember(activeIndex) {
            val name = activePerson?.name.orEmpty()
            mutableStateOf(TextFieldValue(text = name, selection = TextRange(0, name.length)))
        }
    LaunchedEffect(activeIndex) {
        nameFocusRequester.requestFocus()
        keyboardController?.show()
    }
    // A fresh Animatable per activeIndex starts under-scale and springs up to 1f with a small
    // overshoot — a quick bounce on just the name text itself (not the field's background/border)
    // to cue that a different person's name is now showing.
    val nameBounce = remember(activeIndex) { Animatable(NAME_BOUNCE_START_SCALE) }
    LaunchedEffect(activeIndex) {
        nameBounce.animateTo(
            targetValue = 1f,
            animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessMedium),
        )
    }
    val amountValueColor = if (isCustom) colors.onSurface else colors.onSurfaceVariant
    val currencySymbolColor = if (isCustom) colors.primary else colors.onSurfaceVariant
    val isLastPerson = activeIndex >= people.size - 1
    val nextLabel =
        if (isLastPerson) {
            stringResource(R.string.shared_review)
        } else {
            stringResource(R.string.shared_next)
        }

    Column(modifier = modifier.fillMaxWidth().fillMaxHeight()) {
        // Roster scrolls within the space left over by the sticky fields below — so a long
        // participant list never pushes the name/amount editor (or Done/Next) off-screen.
        Column(
            modifier = Modifier.weight(1f).verticalScroll(rememberScrollState()),
        ) {
            SharedCostPeopleRoster(
                people = people,
                activeIndex = activeIndex,
                onPick = onPickPerson,
                visitedIndices = visitedIndices,
            )
        }

        Column(
            modifier = Modifier.fillMaxWidth().padding(top = dimens.space16),
            verticalArrangement = Arrangement.spacedBy(dimens.space16),
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(dimens.space8)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text(
                        text = stringResource(R.string.shared_field_name),
                        style = typography.eyebrow,
                        color = colors.onSurfaceVariant,
                    )
                    if (isCustom) {
                        Text(
                            text = stringResource(R.string.shared_field_amount),
                            style = typography.eyebrow,
                            color = colors.onSurfaceVariant,
                        )
                    } else {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(dimens.space4),
                        ) {
                            ProIcon(
                                glyph = ProIconGlyph.Lock,
                                contentDescription = null,
                                tint = colors.muted,
                                size = dimens.iconTag,
                            )
                            Text(
                                text = stringResource(R.string.shared_field_equal_share),
                                style = typography.eyebrow,
                                color = colors.onSurfaceVariant,
                            )
                        }
                    }
                }

                Row(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .clip(shape)
                            .background(if (fieldActive) colors.surface else colors.paper)
                            .border(
                                BorderStroke(1.4.dp, if (fieldActive) colors.primary else colors.line),
                                shape,
                            ).padding(horizontal = dimens.space14, vertical = dimens.rowPaddingV),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(dimens.space10),
                ) {
                    Box(
                        modifier =
                            Modifier
                                .size(dimens.space32)
                                .clip(CircleShape)
                                .background(colors.primarySoft),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = (activeIndex + 1).toString(),
                            style = typography.bodySemiBold.centeredGlyph(),
                            color = colors.primary,
                            textAlign = TextAlign.Center,
                        )
                    }
                    BasicTextField(
                        value = nameFieldValue,
                        onValueChange = { new ->
                            nameFieldValue = new
                            onNameChange(new.text)
                        },
                        textStyle = typography.body.copy(color = colors.onSurface),
                        cursorBrush = SolidColor(colors.primary),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words),
                        modifier =
                            Modifier
                                .weight(1f)
                                .graphicsLayer {
                                    scaleX = nameBounce.value
                                    scaleY = nameBounce.value
                                }.focusRequester(nameFocusRequester)
                                .onFocusChanged { focusState ->
                                    nameHasFocus = focusState.isFocused
                                    if (focusState.isFocused) showAmountKeypad = false
                                },
                    )
                    Box(
                        modifier =
                            Modifier
                                .width(1.dp)
                                .height(dimens.space20)
                                .background(colors.line),
                    )
                    val amountFieldDescription = stringResource(R.string.shared_field_amount)
                    Row(
                        modifier =
                            Modifier
                                .semantics { contentDescription = amountFieldDescription }
                                .then(
                                    if (isCustom) {
                                        Modifier.proRippleClickable(
                                            onClick = {
                                                showAmountKeypad = true
                                                focusManager.clearFocus()
                                            },
                                        )
                                    } else {
                                        Modifier
                                    },
                                ),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = homeCurrencySymbol,
                            style = typography.listAmount.copy(fontFamily = typography.amountFamily, color = currencySymbolColor),
                        )
                        Text(
                            text = if (isCustom) activeAmountDisplay else equalShareLabel.dropWhile { !it.isDigit() },
                            style =
                                typography.listAmount.copy(
                                    fontFamily = typography.amountFamily,
                                    color = amountValueColor,
                                ),
                        )
                    }
                }

                if (isCustom) {
                    Text(
                        text = stringResource(R.string.shared_equal_share_would_be, equalShareLabel),
                        style = typography.caption,
                        color = colors.onSurfaceMuted,
                        textAlign = TextAlign.End,
                        modifier = Modifier.fillMaxWidth(),
                    )
                } else {
                    Text(
                        text = stringResource(R.string.shared_switch_to_custom_hint),
                        style = typography.caption,
                        color = colors.onSurfaceMuted,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            }

            if (showAmountKeypad) {
                DigitKeypadGrid(
                    onKey = { key ->
                        onAmountKey(key, amountFreshEntry)
                        amountFreshEntry = false
                    },
                    onBackspace = {
                        onAmountBackspace(amountFreshEntry)
                        amountFreshEntry = false
                    },
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(dimens.space8),
            ) {
                ProButton(
                    text = stringResource(R.string.shared_done),
                    onClick = onDone,
                    variant = ProButtonVariant.Secondary,
                    size = ProButtonSize.Lg,
                    fillMaxWidth = true,
                    modifier = Modifier.weight(1f),
                )
                ProButton(
                    text = nextLabel,
                    onClick = onNext,
                    size = ProButtonSize.Lg,
                    fillMaxWidth = true,
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }
}

@Preview(
    showBackground = true,
    widthDp = ProArtboard.PIXEL_9_PRO_WIDTH_DP,
    heightDp = ProArtboard.PIXEL_9_PRO_HEIGHT_DP,
)
@Composable
private fun SharedCostsEditPersonSheetEqualPreview() {
    ProExpenseTheme {
        SharedCostsEditPersonSheetContent(
            people = previewSharedEditPersonEqual,
            activeIndex = 0,
            mode = SharedSplitMode.Equal,
            activeAmountRaw = "30",
            equalShareLabel = "$30.00",
            onPickPerson = {},
            onNameChange = {},
            onAmountKey = { _, _ -> },
            onAmountBackspace = {},
            onDone = {},
            onNext = {},
        )
    }
}

@Preview(
    showBackground = true,
    widthDp = ProArtboard.PIXEL_9_PRO_WIDTH_DP,
    heightDp = ProArtboard.PIXEL_9_PRO_HEIGHT_DP,
)
@Composable
private fun SharedCostsEditPersonSheetCustomPreview() {
    ProExpenseTheme {
        SharedCostsEditPersonSheetContent(
            people = previewSharedEditPersonCustom,
            activeIndex = 2,
            mode = SharedSplitMode.Custom,
            activeAmountRaw = "40",
            equalShareLabel = "$30.00",
            onPickPerson = {},
            onNameChange = {},
            onAmountKey = { _, _ -> },
            onAmountBackspace = {},
            onDone = {},
            onNext = {},
        )
    }
}

/** Last participant — no next person to advance to, so the button reads "Review". */
@Preview(
    showBackground = true,
    widthDp = ProArtboard.PIXEL_9_PRO_WIDTH_DP,
    heightDp = ProArtboard.PIXEL_9_PRO_HEIGHT_DP,
)
@Composable
private fun SharedCostsEditPersonSheetLastPersonPreview() {
    ProExpenseTheme {
        SharedCostsEditPersonSheetContent(
            people = previewSharedEditPersonCustom,
            activeIndex = previewSharedEditPersonCustom.lastIndex,
            mode = SharedSplitMode.Custom,
            activeAmountRaw = "25",
            equalShareLabel = "$30.00",
            onPickPerson = {},
            onNameChange = {},
            onAmountKey = { _, _ -> },
            onAmountBackspace = {},
            onDone = {},
            onNext = {},
        )
    }
}
