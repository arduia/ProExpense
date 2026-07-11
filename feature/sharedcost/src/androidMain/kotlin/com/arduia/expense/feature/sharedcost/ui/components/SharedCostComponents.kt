package com.arduia.expense.feature.sharedcost.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.arduia.expense.feature.sharedcost.R
import com.arduia.expense.ui.design.DetailFieldCard
import com.arduia.expense.ui.design.ProIcon
import com.arduia.expense.ui.design.ProIconGlyph
import com.arduia.expense.ui.design.ProTextAction
import com.arduia.expense.ui.design.proCircularRippleClickable
import com.arduia.expense.ui.design.proClickable
import com.arduia.expense.ui.design.proIconClickable
import com.arduia.expense.ui.design.proPressScale
import com.arduia.expense.ui.design.proRippleClickable
import com.arduia.expense.ui.theme.ProArtboard
import com.arduia.expense.ui.theme.ProExpenseTheme
import com.arduia.expense.ui.theme.centeredGlyph

@Composable
fun SharedCostPeopleCard(
    count: Int,
    onDecrement: () -> Unit,
    onIncrement: () -> Unit,
    modifier: Modifier = Modifier,
    min: Int = 2,
    max: Int = 20,
    maxReachedHint: String? = null,
) {
    val colors = ProExpenseTheme.colors
    val dimens = ProExpenseTheme.dimensions
    val typography = ProExpenseTheme.typography
    val shape = ProExpenseTheme.shapes.card
    val canDecrement = count > min
    val canIncrement = count < max
    val countDescription = stringResource(R.string.shared_people_count_state, count)

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(dimens.space8),
    ) {
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .clip(shape)
                    .border(BorderStroke(1.dp, colors.line), shape)
                    .background(colors.surface)
                    .padding(horizontal = dimens.space16, vertical = dimens.rowPaddingV),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            SharedCostStepperButton(
                glyph = ProIconGlyph.Minus,
                enabled = canDecrement,
                onClick = onDecrement,
                filled = false,
                contentDescription = stringResource(R.string.shared_remove_person_cd),
            )
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                // Merged so TalkBack reads "N people" as one node, and liveRegion announces the
                // new count automatically on +/- without the user needing to move focus back here.
                modifier =
                    Modifier.semantics(mergeDescendants = true) {
                        stateDescription = countDescription
                        liveRegion = LiveRegionMode.Polite
                    },
            ) {
                Text(
                    text = count.toString(),
                    style = typography.displayAmount,
                    color = colors.onSurface,
                    textAlign = TextAlign.Center,
                )
                Text(
                    text = stringResource(R.string.shared_people_word),
                    style = typography.eyebrow,
                    color = colors.onSurfaceMuted,
                )
            }
            SharedCostStepperButton(
                glyph = ProIconGlyph.Plus,
                enabled = canIncrement,
                onClick = onIncrement,
                filled = true,
                contentDescription = stringResource(R.string.shared_add_person_cd),
            )
        }
        if (maxReachedHint != null && !canIncrement) {
            Text(
                text = maxReachedHint,
                style = typography.caption,
                color = colors.onSurfaceMuted,
                textAlign = TextAlign.Center,
            )
        }
    }
}

@Composable
private fun SharedCostStepperButton(
    glyph: ProIconGlyph,
    enabled: Boolean,
    onClick: () -> Unit,
    filled: Boolean,
    contentDescription: String,
    modifier: Modifier = Modifier,
) {
    val colors = ProExpenseTheme.colors
    val dimens = ProExpenseTheme.dimensions
    val motion = ProExpenseTheme.motion
    val shape = CircleShape
    val alpha = if (enabled) 1f else motion.keypadDisabledOpacity
    val background =
        when {
            filled && enabled -> colors.primary
            filled -> colors.primary.copy(alpha = alpha)
            else -> colors.surface
        }
    val tint =
        when {
            filled && enabled -> colors.onPrimaryWarm
            filled -> colors.onPrimaryWarm.copy(alpha = alpha)
            enabled -> colors.onSurface
            else -> colors.muted2
        }

    val interactionSource = remember { MutableInteractionSource() }

    Box(
        modifier =
            modifier
                .size(dimens.touchTargetMin)
                .proPressScale(interactionSource, enabled = enabled)
                .clip(shape)
                .then(
                    if (!filled) {
                        Modifier.border(BorderStroke(1.dp, colors.lineStrong), shape)
                    } else {
                        Modifier
                    },
                ).background(background)
                .then(
                    if (filled) {
                        Modifier.proRippleClickable(
                            onClick = onClick,
                            enabled = enabled,
                            interactionSource = interactionSource,
                            role = Role.Button,
                        )
                    } else {
                        Modifier.proIconClickable(
                            onClick = onClick,
                            enabled = enabled,
                            interactionSource = interactionSource,
                        )
                    },
                ),
        contentAlignment = Alignment.Center,
    ) {
        ProIcon(
            glyph = glyph,
            contentDescription = contentDescription,
            tint = tint,
            size = dimens.iconInline,
        )
    }
}

@Composable
fun SharedCostParticipantRow(
    index: Int,
    name: String,
    amount: String,
    modifier: Modifier = Modifier,
    onEditClick: (() -> Unit)? = null,
    editContentDescription: String = "",
) {
    val colors = ProExpenseTheme.colors
    val dimens = ProExpenseTheme.dimensions
    val typography = ProExpenseTheme.typography

    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .then(
                    // The whole row navigates to the same Edit-person sheet as the trailing icon —
                    // a bigger, easier tap target than the small icon alone.
                    if (onEditClick != null) {
                        Modifier.proClickable(onClick = onEditClick, shape = RectangleShape)
                    } else {
                        Modifier
                    },
                ).padding(vertical = dimens.rowPaddingV),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(dimens.space12),
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
                text = index.toString(),
                style = typography.bodySemiBold.centeredGlyph(),
                color = colors.primary,
                textAlign = TextAlign.Center,
            )
        }
        Text(
            text = name,
            style = typography.captionMedium,
            color = colors.onSurface,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f),
        )
        Text(
            text = amount,
            style = typography.listAmount,
            color = colors.onSurface,
        )
        if (onEditClick != null) {
            SharedCostEditIconButton(
                onClick = onEditClick,
                contentDescription = editContentDescription,
                size = dimens.space32,
                iconSize = dimens.iconChipLeading,
            )
        }
    }
}

/**
 * Small circular edit-icon affordance — deliberately sized explicitly (no
 * `minimumInteractiveComponentSize()` floor) since it always sits inside an already-tappable card
 * or dense row; a 48dp floor here would inflate the parent past its siblings (see
 * `.agents/skills/design-spec-to-compose/SKILL.md` guard on nesting `proIconClickable`). Exposed
 * (not `private`) so other Shared Costs surfaces — e.g. the total-amount edit action — can reuse
 * the exact same circular affordance rather than a bare pencil glyph.
 */
@Composable
fun SharedCostEditIconButton(
    onClick: () -> Unit,
    contentDescription: String,
    size: Dp,
    iconSize: Dp,
    modifier: Modifier = Modifier,
    filledBackground: Boolean = false,
    tint: Color = ProExpenseTheme.colors.onSurfaceVariant,
    borderColor: Color = ProExpenseTheme.colors.line,
) {
    val colors = ProExpenseTheme.colors
    val interactionSource = remember { MutableInteractionSource() }

    Box(
        modifier =
            modifier
                .size(size)
                .proPressScale(interactionSource)
                .clip(CircleShape)
                .background(if (filledBackground) colors.surface else Color.Transparent)
                .border(BorderStroke(1.dp, borderColor), CircleShape)
                .proCircularRippleClickable(
                    onClick = onClick,
                    interactionSource = interactionSource,
                    role = Role.Button,
                ),
        contentAlignment = Alignment.Center,
    ) {
        ProIcon(
            glyph = ProIconGlyph.Edit,
            contentDescription = contentDescription,
            tint = tint,
            size = iconSize,
        )
    }
}

@Composable
fun SharedCostHistoryRow(
    title: String,
    meta: String,
    total: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = ProExpenseTheme.colors
    val dimens = ProExpenseTheme.dimensions
    val typography = ProExpenseTheme.typography
    val shape = ProExpenseTheme.shapes.card

    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .defaultMinSize(minHeight = dimens.touchTargetMin)
                .clip(shape)
                .border(BorderStroke(1.dp, colors.line), shape)
                .background(colors.surface)
                .proClickable(onClick = onClick, shape = shape)
                .padding(dimens.cardPadding),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(dimens.space12),
    ) {
        Box(
            modifier =
                Modifier
                    .size(dimens.quickAccessIconSize)
                    .clip(ProExpenseTheme.shapes.tile)
                    .background(colors.primarySoft),
            contentAlignment = Alignment.Center,
        ) {
            ProIcon(
                glyph = ProIconGlyph.FeatSplit,
                contentDescription = null,
                tint = colors.primary,
                size = dimens.iconInline,
            )
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = typography.bodySemiBold,
                color = colors.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = meta,
                style = typography.caption,
                color = colors.onSurfaceMuted,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(top = dimens.space2),
            )
        }
        Text(
            text = total,
            style = typography.listAmount,
            color = colors.onSurface,
        )
    }
}

@Composable
fun SharedCostNoteField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
    focusRequester: FocusRequester? = null,
) {
    val colors = ProExpenseTheme.colors
    val typography = ProExpenseTheme.typography
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    DetailFieldCard(
        modifier = modifier,
        leading = {
            ProIcon(
                glyph = ProIconGlyph.Note,
                contentDescription = null,
                tint = colors.muted,
                size = ProExpenseTheme.dimensions.iconNav,
            )
        },
        trailing = {
            ProIcon(
                glyph = ProIconGlyph.Check,
                contentDescription = stringResource(R.string.shared_note_dismiss_cd),
                tint = colors.success,
                // proIconClickable already floors the touch target at 48dp — the glyph itself
                // was reading too small next to that generous tap area, so size it up to match.
                size = ProExpenseTheme.dimensions.iconInline,
                modifier =
                    Modifier.proIconClickable(
                        onClick = {
                            focusManager.clearFocus()
                            keyboardController?.hide()
                        },
                    ),
            )
        },
    ) {
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            textStyle = typography.body.copy(color = colors.onSurface),
            cursorBrush = SolidColor(colors.primary),
            singleLine = true,
            modifier =
                Modifier
                    .fillMaxWidth()
                    .then(
                        if (focusRequester != null) {
                            Modifier.focusRequester(focusRequester)
                        } else {
                            Modifier
                        },
                    ),
            decorationBox = { inner ->
                if (value.isEmpty()) {
                    Text(
                        text = placeholder,
                        style =
                            typography.body.copy(
                                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                            ),
                        color = colors.muted,
                    )
                }
                inner()
            },
        )
    }
}

/**
 * Unified per-person card for both Equal and Custom split modes — the handoff's `PersonRowV2`
 * renders identically either way, only the header eyebrow/caption and per-row edit target differ.
 * Every row is a tap target (via [onPersonEditClick]) that opens the Edit-person sheet, rather
 * than the old inline-editable name/amount fields.
 */
@Composable
fun SharedCostPerPersonCard(
    headerEyebrow: String,
    headerAmount: String,
    participants: List<Pair<String, String>>,
    modifier: Modifier = Modifier,
    headerCaption: String? = null,
    headerCaptionEmphasized: Boolean = false,
    onHeaderEditClick: (() -> Unit)? = null,
    headerEditContentDescription: String = "",
    onPersonEditClick: ((Int) -> Unit)? = null,
    personEditContentDescription: (Int) -> String = { "" },
) {
    val colors = ProExpenseTheme.colors
    val dimens = ProExpenseTheme.dimensions
    val typography = ProExpenseTheme.typography
    val shape = ProExpenseTheme.shapes.card
    // Matches AmountDisplay's shrink-to-fit behavior — a long custom-split amount (many digits,
    // still at the large display font) must shrink rather than overflow/wrap.
    val headerAmountAutoSize =
        remember(typography.displayAmount.fontSize) {
            TextAutoSize.StepBased(
                minFontSize = 28.sp,
                maxFontSize = typography.displayAmount.fontSize,
                stepSize = 0.5.sp,
            )
        }

    Column(
        modifier =
            modifier
                .fillMaxWidth()
                .clip(shape)
                .border(BorderStroke(1.dp, colors.line), shape)
                .background(colors.surface)
                .padding(dimens.cardPadding),
        verticalArrangement = Arrangement.spacedBy(dimens.space12),
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(dimens.space4)) {
            // Eyebrow and Edit share the top row — both are short, fixed-width labels, so
            // they never fight the (potentially long, large-font) amount for space. The
            // amount itself renders full-width on its own line below, so a long value never
            // squeezes the Edit action out.
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = headerEyebrow,
                    style = typography.eyebrow,
                    color = colors.onSurfaceMuted,
                )
                if (onHeaderEditClick != null) {
                    ProTextAction(
                        text = stringResource(R.string.shared_edit_action_label),
                        onClick = onHeaderEditClick,
                        style = typography.bodyMedium,
                        color = colors.primary,
                        leading = {
                            ProIcon(
                                glyph = ProIconGlyph.Edit,
                                contentDescription = null,
                                tint = colors.primary,
                                size = dimens.iconChipLeading,
                            )
                        },
                        // The visible label reads "Edit"; keep the richer a11y name ("Edit
                        // split") that existing content-description-based navigation/tests
                        // rely on.
                        modifier =
                            Modifier.semantics(mergeDescendants = true) {
                                contentDescription = headerEditContentDescription
                            },
                    )
                }
            }
            BasicText(
                text = headerAmount,
                style = typography.displayAmount.copy(color = colors.primary),
                autoSize = headerAmountAutoSize,
                maxLines = 1,
                softWrap = false,
                overflow = TextOverflow.Clip,
                modifier = Modifier.fillMaxWidth(),
            )
            if (headerCaption != null) {
                Text(
                    text = headerCaption,
                    style = typography.caption,
                    color = if (headerCaptionEmphasized) colors.success else colors.onSurfaceMuted,
                )
            }
        }
        participants.forEachIndexed { index, (name, amount) ->
            SharedCostParticipantRow(
                index = index + 1,
                name = name,
                amount = amount,
                onEditClick = onPersonEditClick?.let { callback -> { callback(index) } },
                editContentDescription = personEditContentDescription(index),
            )
        }
    }
}

@Preview(showBackground = true, widthDp = ProArtboard.PIXEL_9_PRO_WIDTH_DP)
@Composable
private fun SharedCostPeopleCardPreview() {
    ProExpenseTheme {
        val dimens = ProExpenseTheme.dimensions
        SharedCostPeopleCard(
            count = 4,
            onDecrement = {},
            onIncrement = {},
            modifier = Modifier.padding(dimens.space16),
        )
    }
}

@Preview(showBackground = true, widthDp = ProArtboard.PIXEL_9_PRO_WIDTH_DP)
@Composable
private fun SharedCostParticipantRowPreview() {
    ProExpenseTheme {
        val dimens = ProExpenseTheme.dimensions
        Column(Modifier.padding(dimens.space16)) {
            SharedCostParticipantRow(
                index = 1,
                name = "Aiko",
                amount = "$30",
                onEditClick = {},
                editContentDescription = "Edit Aiko",
            )
        }
    }
}

/** Regression guard: a near-max-digit amount (product cap is 999,999,999.99) must shrink to fit
 *  on one line rather than overflowing/wrapping past the card's edge. */
@Preview(showBackground = true, widthDp = ProArtboard.PIXEL_9_PRO_WIDTH_DP)
@Composable
private fun SharedCostPerPersonCardLargeAmountPreview() {
    ProExpenseTheme {
        val dimens = ProExpenseTheme.dimensions
        SharedCostPerPersonCard(
            headerEyebrow = "Custom shares",
            headerAmount = "$999,999,999.99",
            participants =
                listOf(
                    "Aiko" to "$500,000,000.00",
                    "Ben" to "$499,999,999.99",
                ),
            onHeaderEditClick = {},
            headerEditContentDescription = "Edit split",
            onPersonEditClick = {},
            personEditContentDescription = { "Edit person" },
            modifier = Modifier.padding(dimens.space16),
        )
    }
}
