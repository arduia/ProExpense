package com.arduia.expense.feature.eventbudget.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.arduia.expense.feature.eventbudget.R
import com.arduia.expense.feature.eventbudget.ui.preview.previewEventList
import com.arduia.expense.ui.design.EventBudgetCard
import com.arduia.expense.ui.design.EventBudgetCardState
import com.arduia.expense.ui.design.HomeBottomNav
import com.arduia.expense.ui.design.HomeNavTab
import com.arduia.expense.ui.design.ProButton
import com.arduia.expense.ui.design.ProButtonSize
import com.arduia.expense.ui.design.ProButtonVariant
import com.arduia.expense.ui.design.ProGradientHeader
import com.arduia.expense.ui.design.ProIcon
import com.arduia.expense.ui.design.ProIconGlyph
import com.arduia.expense.ui.design.proClickable
import com.arduia.expense.ui.theme.ProArtboard
import com.arduia.expense.ui.theme.ProExpenseTheme

@Composable
fun EventBudgetListScreen(
    events: List<EventBudgetCardState>,
    onCreateEvent: () -> Unit,
    onEventClick: (String) -> Unit,
    selectedTab: HomeNavTab,
    onTabSelected: (HomeNavTab) -> Unit,
    onAddClick: () -> Unit,
    modifier: Modifier = Modifier,
    isLoading: Boolean = false,
) {
    val colors = ProExpenseTheme.colors
    val dimens = ProExpenseTheme.dimensions
    val typography = ProExpenseTheme.typography

    Box(
        modifier =
            modifier
                .fillMaxSize()
                .background(colors.paper)
                .navigationBarsPadding(),
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            ProGradientHeader {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top,
                ) {
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(dimens.space8),
                    ) {
                        Text(
                            text = stringResource(R.string.events_eyebrow),
                            style = typography.eyebrow,
                            color = Color.White.copy(alpha = 0.7f),
                        )
                        Text(
                            text = stringResource(R.string.events_title),
                            style = typography.profileScreenTitle,
                            color = Color.White,
                        )
                    }
                    if (events.isNotEmpty()) {
                        EventNewOnGradientButton(onClick = onCreateEvent)
                    }
                }
            }

            Column(
                modifier =
                    Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .offset(y = -EventBudgetSheetOverlap)
                        .clip(ProExpenseTheme.shapes.sheet)
                        .background(colors.paper),
            ) {
                if (isLoading) {
                    // Events load asynchronously after first composition — without this, "no data
                    // yet" and "genuinely no events" render identically and the empty-state
                    // illustration flashes on every visit for a user who actually has events.
                    Box(modifier = Modifier.weight(1f).fillMaxWidth())
                } else if (events.isEmpty()) {
                    Box(
                        modifier =
                            Modifier
                                .weight(1f)
                                .fillMaxWidth()
                                .padding(bottom = dimens.navShellBottomInset),
                        contentAlignment = Alignment.Center,
                    ) {
                        EventEmptyContent(onCreateEvent = onCreateEvent)
                    }
                } else {
                    Column(
                        modifier =
                            Modifier
                                .weight(1f)
                                .fillMaxWidth()
                                .verticalScroll(rememberScrollState())
                                .padding(horizontal = dimens.screenPadding)
                                .padding(top = dimens.space16, bottom = dimens.navShellBottomInset),
                        verticalArrangement = Arrangement.spacedBy(dimens.space12),
                    ) {
                        events.forEach { event ->
                            EventBudgetCard(
                                state = event,
                                modifier =
                                    Modifier.proClickable(
                                        onClick = { onEventClick(event.id) },
                                        shape = ProExpenseTheme.shapes.card,
                                    ),
                            )
                        }
                    }
                }
            }
        }

        HomeBottomNav(
            selectedTab = selectedTab,
            onTabSelected = onTabSelected,
            onAddClick = onAddClick,
            modifier = Modifier.align(Alignment.BottomCenter),
        )
    }
}

private val EventBudgetSheetOverlap = 14.dp

/**
 * "New" pill on the gradient header — white/card fill with primary icon+text, not a filled
 * primary button (which would read low-contrast against the primary-blue gradient behind it).
 */
@Composable
private fun EventNewOnGradientButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = ProExpenseTheme.colors
    val dimens = ProExpenseTheme.dimensions
    val typography = ProExpenseTheme.typography
    val shape = ProExpenseTheme.shapes.chip

    Row(
        modifier =
            modifier
                .clip(shape)
                .background(colors.surface)
                .proClickable(onClick = onClick, shape = shape)
                .padding(horizontal = dimens.space12, vertical = dimens.space8),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(dimens.space4),
    ) {
        ProIcon(
            glyph = ProIconGlyph.Plus,
            contentDescription = null,
            tint = colors.primary,
            size = dimens.iconInline,
        )
        Text(
            text = stringResource(R.string.event_new),
            style = typography.bodySemiBold,
            color = colors.primary,
        )
    }
}

@Composable
private fun EventEmptyContent(
    onCreateEvent: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = ProExpenseTheme.colors
    val dimens = ProExpenseTheme.dimensions
    val typography = ProExpenseTheme.typography
    val titleStyle =
        typography.bodySemiBold.copy(
            fontSize = typography.sectionHead.fontSize,
            lineHeight = typography.sectionHead.lineHeight,
        )

    Column(
        modifier = modifier.padding(horizontal = dimens.space32),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier =
                Modifier
                    .size(dimens.space44 * 2)
                    .clip(ProExpenseTheme.shapes.card)
                    .background(colors.primaryTint),
            contentAlignment = Alignment.Center,
        ) {
            ProIcon(
                glyph = ProIconGlyph.FeatEvents,
                contentDescription = null,
                // primary, not primaryDeep — see EventBudgetCard's matching fix.
                tint = colors.primary,
                size = dimens.space32,
            )
        }
        Text(
            text = stringResource(R.string.event_empty_heading),
            style = titleStyle,
            color = colors.onSurface,
            modifier = Modifier.padding(top = dimens.space18),
        )
        Text(
            text = stringResource(R.string.event_empty_body),
            style = typography.body,
            color = colors.onSurfaceMuted,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = dimens.space8),
        )
        ProButton(
            text = stringResource(R.string.event_create_cta),
            onClick = onCreateEvent,
            variant = ProButtonVariant.Primary,
            size = ProButtonSize.Lg,
            leading = {
                ProIcon(
                    glyph = ProIconGlyph.Plus,
                    contentDescription = null,
                    tint = colors.onPrimaryWarm,
                    size = dimens.iconInline,
                )
            },
            modifier = Modifier.padding(top = dimens.space24),
        )
    }
}

@Preview(
    name = "Event budget — empty",
    widthDp = ProArtboard.PIXEL_9_PRO_WIDTH_DP,
    heightDp = ProArtboard.PIXEL_9_PRO_HEIGHT_DP,
    showBackground = true,
)
@Composable
private fun EventBudgetListEmptyPreview() {
    ProExpenseTheme {
        EventBudgetListScreen(
            events = emptyList(),
            onCreateEvent = {},
            onEventClick = {},
            selectedTab = HomeNavTab.Budget,
            onTabSelected = {},
            onAddClick = {},
        )
    }
}

@Preview(
    name = "Event budget — list (dark)",
    widthDp = ProArtboard.PIXEL_9_PRO_WIDTH_DP,
    heightDp = ProArtboard.PIXEL_9_PRO_HEIGHT_DP,
    showBackground = true,
)
@Composable
private fun EventBudgetListDarkPreview() {
    ProExpenseTheme(darkTheme = true) {
        EventBudgetListScreen(
            events = previewEventList,
            onCreateEvent = {},
            onEventClick = {},
            selectedTab = HomeNavTab.Budget,
            onTabSelected = {},
            onAddClick = {},
        )
    }
}

@Preview(
    name = "Event budget — loading",
    widthDp = ProArtboard.PIXEL_9_PRO_WIDTH_DP,
    heightDp = ProArtboard.PIXEL_9_PRO_HEIGHT_DP,
    showBackground = true,
)
@Composable
private fun EventBudgetListLoadingPreview() {
    ProExpenseTheme {
        EventBudgetListScreen(
            events = emptyList(),
            onCreateEvent = {},
            onEventClick = {},
            selectedTab = HomeNavTab.Budget,
            onTabSelected = {},
            onAddClick = {},
            isLoading = true,
        )
    }
}

@Preview(
    name = "Event budget — list",
    widthDp = ProArtboard.PIXEL_9_PRO_WIDTH_DP,
    heightDp = ProArtboard.PIXEL_9_PRO_HEIGHT_DP,
    showBackground = true,
)
@Composable
private fun EventBudgetListPreview() {
    ProExpenseTheme {
        EventBudgetListScreen(
            events = previewEventList,
            onCreateEvent = {},
            onEventClick = {},
            selectedTab = HomeNavTab.Budget,
            onTabSelected = {},
            onAddClick = {},
        )
    }
}
