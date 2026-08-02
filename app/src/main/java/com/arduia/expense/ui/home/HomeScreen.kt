package com.arduia.expense.ui.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.arduia.expense.R
import com.arduia.expense.ui.design.DayGroup
import com.arduia.expense.ui.design.EmptyStateContent
import com.arduia.expense.ui.design.HeroGreeting
import com.arduia.expense.ui.design.NoticeBanner
import com.arduia.expense.ui.design.ProGradientHeader
import com.arduia.expense.ui.design.ProIcon
import com.arduia.expense.ui.design.ProIconGlyph
import com.arduia.expense.ui.design.ProSheetSurface
import com.arduia.expense.ui.design.ProTextAction
import com.arduia.expense.ui.design.ProTransactionRowModel
import com.arduia.expense.ui.design.QuickAccessTile
import com.arduia.expense.ui.design.proClickable
import com.arduia.expense.ui.design.proIconClickable
import com.arduia.expense.ui.preview.HomeActiveEventState
import com.arduia.expense.ui.preview.HomeUiState
import com.arduia.expense.ui.preview.previewHomeBudget
import com.arduia.expense.ui.preview.previewHomeCasual
import com.arduia.expense.ui.preview.previewHomeEmpty
import com.arduia.expense.ui.preview.previewHomeEvent
import com.arduia.expense.ui.preview.previewHomeLoading
import com.arduia.expense.ui.theme.ProArtboard
import com.arduia.expense.ui.theme.ProExpenseTheme
import kotlin.math.roundToInt

private val HomeSpendCardFloat = 30.dp

@Composable
fun HomeScreenContent(
    state: HomeUiState,
    onReportsClick: () -> Unit,
    onDebtClick: () -> Unit,
    onSplitClick: () -> Unit,
    onEventsClick: () -> Unit,
    modifier: Modifier = Modifier,
    showPinSetupBanner: Boolean = false,
    onPinBannerTap: () -> Unit = {},
    onPinBannerDismiss: () -> Unit = {},
    onActiveEventClick: (String) -> Unit = {},
    onLogFirstExpense: () -> Unit = {},
    onSeeAll: () -> Unit = {},
    onCustomizeQuickAccess: () -> Unit = {},
    onNotificationsClick: () -> Unit = {},
    onRowClick: (com.arduia.expense.ui.design.ProTransactionRowModel) -> Unit = {},
    visibleTiles: List<QuickAccessTileType> = QuickAccessPrefs.defaultVisible,
) {
    val dimens = ProExpenseTheme.dimensions

    // The loading/empty states are short, non-scrolling content, so they stay a plain fixed
    // Column. Once there are records, everything (header, cards, quick access) scrolls away
    // together with the list instead of pinning it below a fixed header — the list gets the
    // screen's full height rather than being squeezed into whatever space is left over.
    if (state.isLoading || state.isEmpty) {
        Column(modifier = modifier.fillMaxSize()) {
            HomeHeaderContent(
                state = state,
                showPinSetupBanner = showPinSetupBanner,
                onPinBannerTap = onPinBannerTap,
                onPinBannerDismiss = onPinBannerDismiss,
                onActiveEventClick = onActiveEventClick,
                onNotificationsClick = onNotificationsClick,
                onCustomizeQuickAccess = onCustomizeQuickAccess,
                onReportsClick = onReportsClick,
                onDebtClick = onDebtClick,
                onSplitClick = onSplitClick,
                onEventsClick = onEventsClick,
                visibleTiles = visibleTiles,
            )

            if (state.isLoading) {
                // Records load asynchronously after first composition — without this, "no data
                // yet" and "genuinely no records" render identically and the empty-state
                // illustration flashes on every cold start for a user who actually has records.
                Box(modifier = Modifier.weight(1f).fillMaxWidth())
            } else {
                HomeEmptyContent(
                    onLogFirstExpense = onLogFirstExpense,
                    modifier =
                        Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .padding(horizontal = dimens.screenPadding)
                            .padding(bottom = dimens.navShellBottomInset),
                )
            }
        }
    } else {
        LazyColumn(
            modifier = modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = dimens.navShellBottomInset),
        ) {
            item {
                HomeHeaderContent(
                    state = state,
                    showPinSetupBanner = showPinSetupBanner,
                    onPinBannerTap = onPinBannerTap,
                    onPinBannerDismiss = onPinBannerDismiss,
                    onActiveEventClick = onActiveEventClick,
                    onNotificationsClick = onNotificationsClick,
                    onCustomizeQuickAccess = onCustomizeQuickAccess,
                    onReportsClick = onReportsClick,
                    onDebtClick = onDebtClick,
                    onSplitClick = onSplitClick,
                    onEventsClick = onEventsClick,
                    visibleTiles = visibleTiles,
                )
            }
            item {
                HomeRecentHeader(
                    onSeeAll = onSeeAll,
                    modifier =
                        Modifier
                            .padding(horizontal = dimens.screenPadding)
                            .padding(top = dimens.space20),
                )
            }
            itemsIndexed(state.dayGroups, key = { _, group -> group.dayTitle }) { index, group ->
                DayGroup(
                    title = group.dayTitle,
                    total = group.dayTotal,
                    transactions =
                        group.transactions.map { item ->
                            ProTransactionRowModel(
                                id = item.id,
                                categoryId = item.categoryId,
                                note = item.note,
                                meta = item.meta,
                                amount = item.amount,
                                isIncome = item.isIncome,
                                tag = item.tag,
                                rowKind = item.rowKind,
                                linkedId = item.linkedId,
                                emphasizeOwedAsIncome = true,
                            )
                        },
                    cardWrapped = false,
                    onRowClick = onRowClick,
                    // The header row below HomeRecentHeader already carries a bottom(space10)
                    // gap before the first group; only later groups need the between-groups gap.
                    modifier =
                        Modifier
                            .padding(horizontal = dimens.screenPadding)
                            .then(if (index == 0) Modifier else Modifier.padding(top = dimens.space12)),
                )
            }
        }
    }
}

@Composable
private fun HomeHeaderContent(
    state: HomeUiState,
    showPinSetupBanner: Boolean,
    onPinBannerTap: () -> Unit,
    onPinBannerDismiss: () -> Unit,
    onActiveEventClick: (String) -> Unit,
    onNotificationsClick: () -> Unit,
    onCustomizeQuickAccess: () -> Unit,
    onReportsClick: () -> Unit,
    onDebtClick: () -> Unit,
    onSplitClick: () -> Unit,
    onEventsClick: () -> Unit,
    visibleTiles: List<QuickAccessTileType>,
    modifier: Modifier = Modifier,
) {
    val dimens = ProExpenseTheme.dimensions

    Column(modifier = modifier.fillMaxWidth()) {
        ProGradientHeader {
            HomeHeader(
                dateLabel = state.dateLabel,
                greetingName = state.greetingName,
                greetingPrefixRes = state.greetingPrefixRes,
                onNotificationsClick = onNotificationsClick,
            )
        }

        ProSheetSurface {
            // Canvas floats the spend card up into the gradient header (VBHomeClassicShell's
            // `marginTop: -30` card wrapper, on top of the sheet's own -14dp overlap) rather than
            // sitting it flush below the sheet's rounded edge — this offset reproduces that.
            Column(
                verticalArrangement = Arrangement.spacedBy(dimens.space16),
                modifier = Modifier.offset(y = -HomeSpendCardFloat),
            ) {
                if (showPinSetupBanner) {
                    NoticeBanner(
                        title = stringResource(R.string.home_pin_banner_title),
                        body = stringResource(R.string.home_pin_banner_body),
                        onClick = onPinBannerTap,
                        onDismiss = onPinBannerDismiss,
                        dismissContentDescription = stringResource(R.string.dismiss),
                    )
                }

                MonthSpendCard(
                    monthLabel = state.monthLabel,
                    monthSpend = state.monthSpend,
                    budgetSummary = state.budgetSummary,
                    monthDelta = state.monthDelta,
                    showEmptyHint = state.showEmptyHint && !state.isLoading,
                    showSparkline = !state.isEmpty && state.sparklinePoints.size >= 2,
                    sparklinePoints = state.sparklinePoints,
                )

                state.activeEvent?.let { event ->
                    HomeActiveEventCard(
                        event = event,
                        onClick = { onActiveEventClick(event.eventId) },
                    )
                }
            }

            HomeQuickAccessSection(
                showCustomize = !state.isEmpty,
                onCustomize = onCustomizeQuickAccess,
                onReportsClick = onReportsClick,
                onDebtClick = onDebtClick,
                onSplitClick = onSplitClick,
                onEventsClick = onEventsClick,
                visibleTiles = visibleTiles,
                modifier = Modifier.padding(top = dimens.space24),
            )
        }
    }
}

@Composable
private fun HomeHeader(
    dateLabel: String,
    greetingName: String,
    greetingPrefixRes: String,
    onNotificationsClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val dimens = ProExpenseTheme.dimensions
    val typography = ProExpenseTheme.typography
    val greetingPrefix =
        when (greetingPrefixRes) {
            "welcome" -> stringResource(R.string.home_welcome_prefix)
            else -> stringResource(R.string.home_greeting_prefix)
        }

    Row(
        // Canvas gives Home's header extra bottom padding (58px vs the ~16-24px other flat/
        // gradient headers use) specifically to reserve room for the spend card floating up into
        // it — without this, the card's -30dp float (below) lands on top of this greeting text
        // instead of leaving a gap beneath it.
        modifier = modifier.fillMaxWidth().padding(bottom = dimens.space26),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top,
    ) {
        Column {
            if (dateLabel.isNotBlank()) {
                Text(
                    text = dateLabel.uppercase(),
                    style = typography.eyebrow,
                    color = Color.White.copy(alpha = 0.7f),
                )
            }
            if (greetingName.isNotBlank()) {
                HeroGreeting(
                    name = greetingName,
                    prefix = greetingPrefix,
                    prefixColor = Color.White,
                    emphasisColor = Color.White,
                    // space2 — matches the canvas VBHeader's marginTop:2 between eyebrow and title.
                    modifier = Modifier.padding(top = dimens.space2),
                )
            }
        }
        // proIconClickable already enforces a >=48dp touch target internally — an explicit
        // .size(touchTargetMin) here forced the *visible* circle to that same 48dp, making it
        // bigger than it needs to look. Keep the tap target accessible but shrink the circle
        // itself by sizing only the inner visual layer.
        Box(
            modifier = Modifier.proIconClickable(onClick = onNotificationsClick),
            contentAlignment = Alignment.Center,
        ) {
            Box(
                modifier =
                    Modifier
                        .size(dimens.buttonSmallHeight)
                        .clip(CircleShape)
                        .border(BorderStroke(1.dp, Color.White.copy(alpha = 0.22f)), CircleShape)
                        .background(Color.White.copy(alpha = 0.14f)),
                contentAlignment = Alignment.Center,
            ) {
                ProIcon(
                    glyph = ProIconGlyph.Bell,
                    contentDescription = stringResource(R.string.notifications),
                    tint = Color.White,
                    size = dimens.iconInline,
                )
            }
        }
    }
}

@Composable
private fun MonthSpendCard(
    monthLabel: String,
    monthSpend: String,
    budgetSummary: com.arduia.expense.ui.preview.HomeBudgetSummaryState?,
    monthDelta: String?,
    showEmptyHint: Boolean,
    showSparkline: Boolean,
    sparklinePoints: List<Float> = emptyList(),
    modifier: Modifier = Modifier,
) {
    val colors = ProExpenseTheme.colors
    val dimens = ProExpenseTheme.dimensions
    val typography = ProExpenseTheme.typography
    val cardShape = ProExpenseTheme.shapes.heroCard
    val cardElevation = ProExpenseTheme.elevation.heroCard.firstOrNull()
    val periodLabel =
        if (monthLabel.isNotBlank()) {
            stringResource(R.string.home_spent_period, monthLabel.uppercase())
        } else {
            stringResource(R.string.home_spend_this_month)
        }

    Column(
        modifier =
            modifier
                .fillMaxWidth()
                .then(
                    if (cardElevation != null) {
                        Modifier.shadow(
                            elevation = cardElevation.blur,
                            shape = cardShape,
                            spotColor = cardElevation.color,
                            ambientColor = cardElevation.color,
                        )
                    } else {
                        Modifier
                    },
                ).clip(cardShape)
                .border(BorderStroke(1.dp, colors.line), cardShape)
                .background(colors.surface)
                .padding(dimens.cardPadding),
    ) {
        Text(
            text = periodLabel,
            style = typography.eyebrow,
            color = colors.onSurfaceVariant,
        )
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(top = dimens.space4),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = tagLeadingCurrency(monthSpend, colors.tag, typography.summaryAmount.fontSize),
                style = typography.summaryAmount,
                color = colors.onSurface,
            )
            if (showSparkline) {
                SpendSparkline(
                    points = sparklinePoints,
                    modifier =
                        Modifier
                            .width(86.dp)
                            .height(40.dp),
                )
            }
        }
        if (budgetSummary != null) {
            Text(
                text = "${budgetSummary.statusLabel} · ${budgetSummary.spentLabel} ${budgetSummary.budgetLabel}",
                style = typography.bodyMedium,
                color = if (budgetSummary.isOverBudget) colors.danger else colors.onSurfaceMuted,
                modifier = Modifier.padding(top = dimens.space6),
            )
        }
        when {
            monthDelta != null -> {
                val isIncrease = monthDelta.startsWith("+")
                val deltaText = monthDelta.removePrefix("+").removePrefix("-")
                Row(
                    horizontalArrangement = Arrangement.spacedBy(dimens.space4),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(top = dimens.space6),
                ) {
                    Text(
                        text = if (isIncrease) "↑" else "↓",
                        style = typography.caption.copy(fontWeight = FontWeight.SemiBold),
                        color = if (isIncrease) colors.danger else colors.success,
                    )
                    Text(
                        text = deltaText,
                        style = typography.caption,
                        color = if (isIncrease) colors.danger else colors.success,
                    )
                }
            }
            showEmptyHint -> {
                Text(
                    text = stringResource(R.string.home_empty_hint),
                    style = typography.body,
                    color = colors.onSurfaceMuted,
                    modifier = Modifier.padding(top = dimens.space6),
                )
            }
        }
    }
}

// Canvas shrinks the leading currency symbol (tag/gold) to roughly half the digit size on Home's
// hero amounts, leaving the digits in the default ink color — a non-digit leading character is
// assumed to be the symbol.
private fun tagLeadingCurrency(
    amount: String,
    tagColor: Color,
    baseFontSize: androidx.compose.ui.unit.TextUnit,
): androidx.compose.ui.text.AnnotatedString =
    androidx.compose.ui.text.buildAnnotatedString {
        if (amount.isNotEmpty() && !amount[0].isDigit()) {
            withStyle(
                SpanStyle(color = tagColor, fontSize = baseFontSize * CURRENCY_SYMBOL_SCALE),
            ) { append(amount[0]) }
            append(amount.substring(1))
        } else {
            append(amount)
        }
    }

private val HomeEventIconSize = 28.dp
private val HomeEventIconShape = RoundedCornerShape(9.dp)
private val HomeEventBarHeight = 6.dp
private const val PERCENT_SCALE = 100

// Canvas's own $-to-digit size ratios: MonthSpendCard 21/40≈0.53, event card 14/22≈0.64 — this
// splits the difference as one shared token rather than two untethered one-off sp values.
private const val CURRENCY_SYMBOL_SCALE = 0.55f

private data class HomeEventAccent(
    val ink: Color,
    val deep: Color,
    val tint: Color,
)

// Home's floating event card is canvas's own `VBCardSpendTrip` — visually distinct from the
// Events-list screen's `EventBudgetCard` (small @-tagged icon, tag/gold accent, inline "$X left
// of $Y" caption, percent-remaining pill), not a reuse of that shared component.
@Composable
private fun HomeActiveEventCard(
    event: HomeActiveEventState,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = ProExpenseTheme.colors
    val dimens = ProExpenseTheme.dimensions
    val cardShape = ProExpenseTheme.shapes.heroCard
    val cardElevation = ProExpenseTheme.elevation.heroCard.firstOrNull()
    val accent =
        if (event.isOverBudget) {
            HomeEventAccent(colors.danger, colors.danger, colors.dangerTint)
        } else {
            HomeEventAccent(colors.tag, colors.tagDeep, colors.tagTint)
        }

    Column(
        modifier =
            modifier
                .fillMaxWidth()
                .then(
                    if (cardElevation != null) {
                        Modifier.shadow(
                            elevation = cardElevation.blur,
                            shape = cardShape,
                            spotColor = cardElevation.color,
                            ambientColor = cardElevation.color,
                        )
                    } else {
                        Modifier
                    },
                ).clip(cardShape)
                .border(BorderStroke(1.dp, colors.line), cardShape)
                .background(colors.surface)
                .proClickable(onClick = onClick, shape = cardShape, scaleOnPress = false)
                .padding(horizontal = dimens.space18, vertical = dimens.space16),
        verticalArrangement = Arrangement.spacedBy(dimens.space12),
    ) {
        HomeEventHeaderRow(event = event, accent = accent)
        HomeEventAmountRow(event = event, accent = accent)
        Box(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .height(HomeEventBarHeight)
                    .clip(ProExpenseTheme.shapes.chip)
                    .background(accent.tint),
        ) {
            Box(
                modifier =
                    Modifier
                        .fillMaxWidth(event.progress.coerceIn(0f, 1f))
                        .height(HomeEventBarHeight)
                        .clip(ProExpenseTheme.shapes.chip)
                        .background(Brush.horizontalGradient(listOf(accent.ink, accent.deep)))
                        .align(Alignment.CenterStart),
            )
        }
    }
}

@Composable
private fun HomeEventHeaderRow(
    event: HomeActiveEventState,
    accent: HomeEventAccent,
) {
    val colors = ProExpenseTheme.colors
    val dimens = ProExpenseTheme.dimensions
    val typography = ProExpenseTheme.typography

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(dimens.space8),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier =
                    Modifier
                        .size(HomeEventIconSize)
                        .clip(HomeEventIconShape)
                        .background(accent.tint),
                contentAlignment = Alignment.Center,
            ) {
                ProIcon(
                    glyph = ProIconGlyph.At,
                    contentDescription = null,
                    tint = accent.ink,
                    size = dimens.iconTag + dimens.space4,
                )
            }
            Text(text = event.title, style = typography.bodySemiBold, color = colors.onSurface)
        }
        Text(text = event.dateRange.uppercase(), style = typography.eyebrow, color = colors.onSurfaceMuted)
    }
}

@Composable
private fun HomeEventAmountRow(
    event: HomeActiveEventState,
    accent: HomeEventAccent,
) {
    val colors = ProExpenseTheme.colors
    val dimens = ProExpenseTheme.dimensions
    val typography = ProExpenseTheme.typography
    val remainingPercent = ((1f - event.progress.coerceIn(0f, 1f)) * PERCENT_SCALE).roundToInt()

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Bottom,
    ) {
        Text(
            text =
                buildAnnotatedString {
                    withStyle(
                        SpanStyle(
                            color = accent.ink,
                            fontSize = typography.cardAmount.fontSize * CURRENCY_SYMBOL_SCALE,
                        ),
                    ) {
                        append(event.spentLabel.take(1))
                    }
                    withStyle(
                        SpanStyle(
                            textDecoration = if (event.isOverBudget) TextDecoration.LineThrough else null,
                        ),
                    ) {
                        append(event.spentLabel.drop(1))
                    }
                    append(' ')
                    withStyle(
                        SpanStyle(
                            fontSize = typography.caption.fontSize,
                            fontWeight = FontWeight.Medium,
                            color = colors.onSurfaceMuted,
                        ),
                    ) {
                        append(event.budgetLabel)
                    }
                },
            style = typography.cardAmount,
            color = if (event.isOverBudget) colors.danger else colors.onSurface,
        )
        Text(
            text = stringResource(R.string.home_event_remaining_percent, remainingPercent),
            style = typography.eyebrow,
            color = accent.deep,
            modifier =
                Modifier
                    .background(accent.tint, ProExpenseTheme.shapes.chip)
                    .padding(horizontal = dimens.space8, vertical = dimens.space4),
        )
    }
}

@Composable
private fun SpendSparkline(
    points: List<Float>,
    modifier: Modifier = Modifier,
) {
    val lineColor = ProExpenseTheme.colors.primary
    androidx.compose.foundation.Canvas(modifier = modifier) {
        if (points.size < 2) return@Canvas
        val maxValue = points.max()
        val minValue = points.min()
        val range = (maxValue - minValue).coerceAtLeast(1f)
        val stepX = size.width / (points.size - 1)

        fun pointAt(index: Int): androidx.compose.ui.geometry.Offset {
            val normalized = (points[index] - minValue) / range
            return androidx.compose.ui.geometry
                .Offset(stepX * index, size.height - normalized * size.height)
        }

        val path =
            Path().apply {
                moveTo(pointAt(0).x, pointAt(0).y)
                for (index in 1 until points.size) {
                    lineTo(pointAt(index).x, pointAt(index).y)
                }
            }
        drawPath(
            path = path,
            color = lineColor,
            style = Stroke(width = 2.5f, cap = StrokeCap.Round, join = androidx.compose.ui.graphics.StrokeJoin.Round),
        )
        drawCircle(
            color = lineColor,
            radius = 4f,
            center = pointAt(points.size - 1),
        )
    }
}

@Composable
private fun HomeQuickAccessSection(
    showCustomize: Boolean,
    onCustomize: () -> Unit,
    onReportsClick: () -> Unit,
    onDebtClick: () -> Unit,
    onSplitClick: () -> Unit,
    onEventsClick: () -> Unit,
    visibleTiles: List<QuickAccessTileType> = QuickAccessPrefs.defaultVisible,
    modifier: Modifier = Modifier,
) {
    val colors = ProExpenseTheme.colors
    val dimens = ProExpenseTheme.dimensions
    val typography = ProExpenseTheme.typography

    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = stringResource(R.string.quick_access_section),
                style = typography.eyebrow,
                color = colors.onSurfaceVariant,
            )
            if (showCustomize) {
                ProTextAction(
                    text = stringResource(R.string.customize),
                    onClick = onCustomize,
                    style = typography.caption,
                    color = colors.onSurfaceMuted,
                )
            }
        }
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(top = dimens.space10),
            horizontalArrangement = Arrangement.spacedBy(dimens.space12),
        ) {
            visibleTiles.forEach { tile ->
                val (icon, labelRes, onClick) =
                    when (tile) {
                        QuickAccessTileType.Reports ->
                            Triple(ProIconGlyph.FeatReports, R.string.quick_access_reports, onReportsClick)
                        QuickAccessTileType.Debt ->
                            Triple(ProIconGlyph.FeatDebt, R.string.quick_access_debt, onDebtClick)
                        QuickAccessTileType.Split ->
                            Triple(ProIconGlyph.FeatSplit, R.string.quick_access_split, onSplitClick)
                        QuickAccessTileType.Events ->
                            Triple(ProIconGlyph.FeatEvents, R.string.quick_access_events, onEventsClick)
                    }
                // Goals (Events) is the one tile canvas gives a gold accent — the other three
                // share the default blue tint.
                val isGoalsTile = tile == QuickAccessTileType.Events
                QuickAccessTile(
                    label = stringResource(labelRes),
                    icon = icon,
                    onClick = onClick,
                    modifier = Modifier.weight(1f),
                    iconTint = if (isGoalsTile) colors.highlightDeep else null,
                    iconBackground = if (isGoalsTile) colors.highlightTint else null,
                )
            }
        }
    }
}

@Composable
private fun HomeEmptyContent(
    onLogFirstExpense: () -> Unit,
    modifier: Modifier = Modifier,
) {
    EmptyStateContent(
        title = stringResource(R.string.home_empty_title),
        subtitle = stringResource(R.string.home_empty_subtitle),
        actionLabel = stringResource(R.string.home_log_first_expense),
        onActionClick = onLogFirstExpense,
        addHintPrefix = stringResource(R.string.home_empty_tap_add_prefix),
        addHintSuffix = stringResource(R.string.home_empty_tap_add_suffix),
        modifier = modifier,
    )
}

@Composable
private fun HomeRecentHeader(
    onSeeAll: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = ProExpenseTheme.colors
    val dimens = ProExpenseTheme.dimensions
    val typography = ProExpenseTheme.typography

    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .padding(bottom = dimens.space10),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = stringResource(R.string.recent_section),
            style = typography.eyebrow,
            color = colors.onSurface,
        )
        ProTextAction(
            text = stringResource(R.string.see_all),
            onClick = onSeeAll,
            style = typography.caption.copy(fontWeight = FontWeight.SemiBold),
            color = colors.primary,
        )
    }
}

@Preview(
    name = "Home — casual",
    widthDp = ProArtboard.PIXEL_9_PRO_WIDTH_DP,
    heightDp = ProArtboard.PIXEL_9_PRO_HEIGHT_DP,
    showBackground = true,
)
@Composable
private fun HomeCasualPreview() {
    ProExpenseTheme {
        HomeScreenContent(
            state = previewHomeCasual,
            onReportsClick = {},
            onDebtClick = {},
            onSplitClick = {},
            onEventsClick = {},
        )
    }
}

@Preview(
    name = "Home — casual (dark)",
    widthDp = ProArtboard.PIXEL_9_PRO_WIDTH_DP,
    heightDp = ProArtboard.PIXEL_9_PRO_HEIGHT_DP,
    showBackground = true,
)
@Composable
private fun HomeCasualDarkPreview() {
    ProExpenseTheme(darkTheme = true) {
        HomeScreenContent(
            state = previewHomeCasual,
            onReportsClick = {},
            onDebtClick = {},
            onSplitClick = {},
            onEventsClick = {},
        )
    }
}

@Preview(
    name = "Home — budget",
    widthDp = ProArtboard.PIXEL_9_PRO_WIDTH_DP,
    heightDp = ProArtboard.PIXEL_9_PRO_HEIGHT_DP,
    showBackground = true,
)
@Composable
private fun HomeBudgetPreview() {
    ProExpenseTheme {
        HomeScreenContent(
            state = previewHomeBudget,
            onReportsClick = {},
            onDebtClick = {},
            onSplitClick = {},
            onEventsClick = {},
        )
    }
}

@Preview(
    name = "Home — event",
    widthDp = ProArtboard.PIXEL_9_PRO_WIDTH_DP,
    heightDp = ProArtboard.PIXEL_9_PRO_HEIGHT_DP,
    showBackground = true,
)
@Composable
private fun HomeEventPreview() {
    ProExpenseTheme {
        HomeScreenContent(
            state = previewHomeEvent,
            onReportsClick = {},
            onDebtClick = {},
            onSplitClick = {},
            onEventsClick = {},
        )
    }
}

@Preview(
    name = "Home — event (dark)",
    widthDp = ProArtboard.PIXEL_9_PRO_WIDTH_DP,
    heightDp = ProArtboard.PIXEL_9_PRO_HEIGHT_DP,
    showBackground = true,
)
@Composable
private fun HomeEventDarkPreview() {
    ProExpenseTheme(darkTheme = true) {
        HomeScreenContent(
            state = previewHomeEvent,
            onReportsClick = {},
            onDebtClick = {},
            onSplitClick = {},
            onEventsClick = {},
        )
    }
}

@Preview(
    name = "Home — empty",
    widthDp = ProArtboard.PIXEL_9_PRO_WIDTH_DP,
    heightDp = ProArtboard.PIXEL_9_PRO_HEIGHT_DP,
    showBackground = true,
)
@Composable
private fun HomeEmptyPreview() {
    ProExpenseTheme {
        HomeScreenContent(
            state = previewHomeEmpty,
            onReportsClick = {},
            onDebtClick = {},
            onSplitClick = {},
            onEventsClick = {},
        )
    }
}

@Preview(
    name = "Home — loading",
    widthDp = ProArtboard.PIXEL_9_PRO_WIDTH_DP,
    heightDp = ProArtboard.PIXEL_9_PRO_HEIGHT_DP,
    showBackground = true,
)
@Composable
private fun HomeLoadingPreview() {
    ProExpenseTheme {
        HomeScreenContent(
            state = previewHomeLoading,
            onReportsClick = {},
            onDebtClick = {},
            onSplitClick = {},
            onEventsClick = {},
        )
    }
}

@Preview(
    name = "Home — casual with PIN banner",
    widthDp = ProArtboard.PIXEL_9_PRO_WIDTH_DP,
    heightDp = ProArtboard.PIXEL_9_PRO_HEIGHT_DP,
    showBackground = true,
)
@Composable
private fun HomeCasualWithPinBannerPreview() {
    ProExpenseTheme {
        HomeScreenContent(
            state = previewHomeCasual,
            onReportsClick = {},
            onDebtClick = {},
            onSplitClick = {},
            onEventsClick = {},
            showPinSetupBanner = true,
        )
    }
}
