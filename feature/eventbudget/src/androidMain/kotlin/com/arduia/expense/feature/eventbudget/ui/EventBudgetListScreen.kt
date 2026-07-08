package com.arduia.expense.feature.eventbudget.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.arduia.expense.feature.eventbudget.R
import com.arduia.expense.ui.design.EventBudgetCard
import com.arduia.expense.ui.design.EventBudgetCardState
import com.arduia.expense.ui.design.HomeBottomNav
import com.arduia.expense.ui.design.HomeNavTab
import com.arduia.expense.ui.design.ProButton
import com.arduia.expense.ui.design.ProButtonSize
import com.arduia.expense.ui.design.ProButtonVariant
import com.arduia.expense.ui.design.ProIcon
import com.arduia.expense.ui.design.ProIconGlyph
import com.arduia.expense.ui.design.proClickable
import com.arduia.expense.feature.eventbudget.ui.preview.previewEventList
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
        modifier = modifier
            .fillMaxSize()
            .background(colors.paper)
            .statusBarsPadding()
            .navigationBarsPadding(),
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = dimens.screenPadding)
                    .padding(top = dimens.space14, bottom = dimens.space16),
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
                        color = colors.onSurfaceMuted,
                    )
                    Text(
                        text = stringResource(R.string.events_title),
                        style = typography.profileScreenTitle,
                        color = colors.onSurface,
                    )
                }
                if (events.isNotEmpty()) {
                    ProButton(
                        text = stringResource(R.string.event_new),
                        onClick = onCreateEvent,
                        size = ProButtonSize.Sm,
                        leading = {
                            ProIcon(
                                glyph = ProIconGlyph.Plus,
                                contentDescription = null,
                                tint = colors.onPrimaryWarm,
                                size = dimens.iconInline,
                            )
                        },
                    )
                }
            }

            if (isLoading) {
                // Events load asynchronously after first composition — without this, "no data
                // yet" and "genuinely no events" render identically and the empty-state
                // illustration flashes on every visit for a user who actually has events.
                Box(modifier = Modifier.weight(1f).fillMaxWidth())
            } else if (events.isEmpty()) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(bottom = dimens.navShellBottomInset),
                    contentAlignment = Alignment.Center,
                ) {
                    EventEmptyContent(onCreateEvent = onCreateEvent)
                }
            } else {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = dimens.screenPadding)
                        .padding(bottom = dimens.navShellBottomInset),
                    verticalArrangement = Arrangement.spacedBy(dimens.space12),
                ) {
                    events.forEach { event ->
                        EventBudgetCard(
                            state = event,
                            modifier = Modifier.proClickable(
                                onClick = { onEventClick(event.id) },
                                shape = ProExpenseTheme.shapes.card,
                            ),
                        )
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

@Composable
private fun EventEmptyContent(
    onCreateEvent: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = ProExpenseTheme.colors
    val dimens = ProExpenseTheme.dimensions
    val typography = ProExpenseTheme.typography
    val titleStyle = typography.bodySemiBold.copy(
        fontSize = typography.sectionHead.fontSize,
        lineHeight = typography.sectionHead.lineHeight,
    )

    Column(
        modifier = modifier.padding(horizontal = dimens.space32),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier = Modifier
                .size(dimens.space44 * 2)
                .clip(ProExpenseTheme.shapes.card)
                .background(colors.primaryTint),
            contentAlignment = Alignment.Center,
        ) {
            ProIcon(
                glyph = ProIconGlyph.FeatEvents,
                contentDescription = null,
                tint = colors.primaryDeep,
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
