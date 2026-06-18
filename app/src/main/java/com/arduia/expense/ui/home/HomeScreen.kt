package com.arduia.expense.ui.home

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.arduia.expense.feature.logging.logCategoryById
import com.arduia.expense.ui.design.IconBell
import com.arduia.expense.ui.design.IconPlus
import com.arduia.expense.ui.design.ProButton
import com.arduia.expense.ui.design.ProButtonSize
import com.arduia.expense.ui.design.ProButtonVariant
import com.arduia.expense.ui.theme.ProExpenseTheme

data class HomeTransactionItem(
    val id: String,
    val categoryId: String,
    val note: String,
    val metaLabel: String,
    val amountLabel: String,
    val eventTag: String? = null,
)

data class HomeDayGroup(
    val title: String,
    val totalLabel: String? = null,
    val items: List<HomeTransactionItem>,
)

data class HomeUiState(
    val profileName: String,
    val homeCurrencyCode: String,
    val dateLabel: String,
    val monthLabel: String,
    val monthSpendLabel: String,
    val spendDeltaLabel: String? = null,
    val spendHelperText: String? = null,
    val recentGroups: List<HomeDayGroup> = emptyList(),
    val highlightRecordId: String? = null,
) {
    val isEmpty: Boolean get() = recentGroups.isEmpty()
    val greetingPrefix: String get() = if (isEmpty) "Welcome," else "Hi,"
}

@Composable
fun HomeScreen(
    state: HomeUiState,
    onSeeAll: () -> Unit,
    onLogFirstExpense: () -> Unit,
    onQuickAccess: (QuickAccessDestination) -> Unit,
    modifier: Modifier = Modifier,
    onCustomizeQuickAccess: () -> Unit = {},
) {
    val colors = ProExpenseTheme.colors
    val dims = ProExpenseTheme.dimensions

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(colors.paper),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = dims.homeHeaderPaddingH)
                .padding(top = dims.space14),
        ) {
            HomeHeader(
                dateLabel = state.dateLabel,
                greetingPrefix = state.greetingPrefix,
                profileName = state.profileName,
            )
            MonthSpendCard(
                monthLabel = state.monthLabel,
                monthSpendLabel = state.monthSpendLabel,
                spendDeltaLabel = state.spendDeltaLabel,
                spendHelperText = state.spendHelperText,
                showSparkline = !state.isEmpty,
                modifier = Modifier.padding(top = dims.space18),
            )
        }

        QuickAccessSection(
            onQuickAccess = onQuickAccess,
            onCustomize = onCustomizeQuickAccess,
            showCustomize = !state.isEmpty,
            modifier = Modifier.padding(top = dims.space24),
        )

        if (state.isEmpty) {
            HomeEmptyContent(
                onLogFirstExpense = onLogFirstExpense,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
            )
        } else {
            RecentSection(
                groups = state.recentGroups,
                highlightRecordId = state.highlightRecordId,
                onSeeAll = onSeeAll,
                modifier = Modifier
                    .weight(1f)
                    .padding(top = dims.space24),
            )
        }
    }
}

@Composable
private fun HomeHeader(
    dateLabel: String,
    greetingPrefix: String,
    profileName: String,
) {
    val colors = ProExpenseTheme.colors
    val typography = ProExpenseTheme.typography
    val dims = ProExpenseTheme.dimensions

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top,
    ) {
        Column {
            Text(
                text = dateLabel.uppercase(),
                style = typography.eyebrow,
                color = colors.onSurfaceVariant,
            )
            Text(
                text = greetingPrefix,
                style = typography.screenTitle,
                color = colors.onSurface,
                modifier = Modifier.padding(top = dims.space4),
            )
            Text(
                text = profileName,
                style = typography.screenTitle.copy(fontStyle = FontStyle.Italic),
                color = colors.primary,
            )
        }
        Box(
            modifier = Modifier
                .size(dims.iconButtonSize)
                .clip(CircleShape)
                .border(dims.borderWidth, colors.lineStrong, CircleShape)
                .background(colors.surface)
                .padding(dims.space8),
            contentAlignment = Alignment.Center,
        ) {
            IconBell(color = colors.onSurfaceVariant)
        }
    }
}

@Composable
private fun MonthSpendCard(
    monthLabel: String,
    monthSpendLabel: String,
    spendDeltaLabel: String?,
    spendHelperText: String?,
    showSparkline: Boolean,
    modifier: Modifier = Modifier,
) {
    val colors = ProExpenseTheme.colors
    val typography = ProExpenseTheme.typography
    val dims = ProExpenseTheme.dimensions
    val shapes = ProExpenseTheme.shapes

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(shapes.card)
            .border(dims.borderWidth, colors.lineStrong, shapes.card)
            .background(colors.surface)
            .padding(dims.cardPadding),
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "SPENT · ${monthLabel.uppercase()}",
                style = typography.eyebrow,
                color = colors.onSurfaceVariant,
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = dims.space6),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = monthSpendLabel,
                    style = typography.displayAmount.copy(
                        fontSize = typography.displayAmount.fontSize * 0.66f,
                    ),
                    color = colors.onSurface,
                )
                if (showSparkline) {
                    SpendSparkline(
                        modifier = Modifier
                            .width(96.dp)
                            .height(48.dp),
                    )
                }
            }
            when {
                spendHelperText != null -> Text(
                    text = spendHelperText,
                    style = typography.caption,
                    color = colors.onSurfaceMuted,
                    modifier = Modifier.padding(top = dims.space6),
                )
                spendDeltaLabel != null -> Row(
                    horizontalArrangement = Arrangement.spacedBy(dims.space4),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(top = dims.space6),
                ) {
                    Text(
                        text = "↓",
                        style = typography.caption.copy(fontWeight = FontWeight.SemiBold),
                        color = colors.success,
                    )
                    Text(
                        text = spendDeltaLabel,
                        style = typography.caption,
                        color = colors.success,
                    )
                }
            }
        }
    }
}

@Composable
private fun SpendSparkline(modifier: Modifier = Modifier) {
    val lineColor = ProExpenseTheme.colors.primarySoft
    Canvas(modifier) {
        val path = Path().apply {
            moveTo(0f, size.height * 0.72f)
            cubicTo(size.width * 0.2f, size.height * 0.55f, size.width * 0.35f, size.height * 0.8f, size.width * 0.5f, size.height * 0.45f)
            cubicTo(size.width * 0.65f, size.height * 0.2f, size.width * 0.8f, size.height * 0.5f, size.width, size.height * 0.28f)
        }
        drawPath(path, color = lineColor, style = Stroke(width = 2.5f, cap = StrokeCap.Round))
        drawCircle(
            color = lineColor,
            radius = 4f,
            center = Offset(size.width, size.height * 0.28f),
        )
    }
}

@Composable
private fun QuickAccessSection(
    onQuickAccess: (QuickAccessDestination) -> Unit,
    onCustomize: () -> Unit,
    showCustomize: Boolean,
    modifier: Modifier = Modifier,
) {
    val colors = ProExpenseTheme.colors
    val typography = ProExpenseTheme.typography
    val dims = ProExpenseTheme.dimensions

    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = dims.homeHeaderPaddingH),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "QUICK ACCESS",
                style = typography.eyebrow,
                color = colors.onSurfaceVariant,
            )
            if (showCustomize) {
                Text(
                    text = "Customize",
                    style = typography.caption,
                    color = colors.onSurfaceMuted,
                    modifier = Modifier.clickable(onClick = onCustomize),
                )
            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = dims.homeHeaderPaddingH)
                .padding(top = dims.space10),
            horizontalArrangement = Arrangement.spacedBy(dims.space10),
        ) {
            QuickAccessTile(
                label = "Reports",
                destination = QuickAccessDestination.Reports,
                onClick = { onQuickAccess(QuickAccessDestination.Reports) },
                modifier = Modifier.weight(1f),
            )
            QuickAccessTile(
                label = "Debt",
                destination = QuickAccessDestination.Debt,
                onClick = { onQuickAccess(QuickAccessDestination.Debt) },
                modifier = Modifier.weight(1f),
            )
            QuickAccessTile(
                label = "Split",
                destination = QuickAccessDestination.Split,
                onClick = { onQuickAccess(QuickAccessDestination.Split) },
                modifier = Modifier.weight(1f),
            )
            QuickAccessTile(
                label = "Events",
                destination = QuickAccessDestination.Events,
                onClick = { onQuickAccess(QuickAccessDestination.Events) },
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
private fun HomeEmptyContent(
    onLogFirstExpense: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = ProExpenseTheme.colors
    val typography = ProExpenseTheme.typography
    val dims = ProExpenseTheme.dimensions

    Column(
        modifier = modifier.padding(horizontal = dims.homeHeaderPaddingH),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        HomeEmptyIllustration(
            modifier = Modifier
                .width(dims.homeEmptyIllustrationWidth)
                .height(dims.homeEmptyIllustrationHeight),
        )
        Text(
            text = "No expenses yet",
            style = typography.sectionHead,
            color = colors.onSurface,
            modifier = Modifier.padding(top = dims.space18),
        )
        Text(
            text = "Start by logging your first one — it takes about five seconds.",
            style = typography.body,
            color = colors.onSurfaceMuted,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = dims.space8),
        )
        ProButton(
            text = "Log your first expense",
            onClick = onLogFirstExpense,
            variant = ProButtonVariant.Primary,
            size = ProButtonSize.Lg,
            fillMaxWidth = true,
            leading = { IconPlus(color = colors.onPrimary, size = dims.iconSizeDefault) },
            modifier = Modifier.padding(top = dims.space20),
        )
        Text(
            text = buildAnnotatedString {
                append("or tap ")
                withStyle(SpanStyle(color = colors.primary, fontWeight = FontWeight.SemiBold)) {
                    append("+")
                }
                append(" below")
            },
            style = typography.caption,
            color = colors.onSurfaceMuted,
            modifier = Modifier.padding(top = dims.space10),
        )
    }
}

@Composable
private fun RecentSection(
    groups: List<HomeDayGroup>,
    highlightRecordId: String?,
    onSeeAll: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = ProExpenseTheme.colors
    val typography = ProExpenseTheme.typography
    val dims = ProExpenseTheme.dimensions

    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = dims.homeHeaderPaddingH)
                .padding(bottom = dims.space10),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "RECENT",
                style = typography.eyebrow,
                color = colors.onSurfaceVariant,
            )
            Text(
                text = "See all",
                style = typography.buttonSmall,
                color = colors.primary,
                modifier = Modifier.clickable(onClick = onSeeAll),
            )
        }

        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(horizontal = dims.homeHeaderPaddingH)
                .padding(bottom = dims.homeFabBottomOffset),
        ) {
            groups.forEach { group ->
                DayHeader(title = group.title, totalLabel = group.totalLabel)
                group.items.forEach { item ->
                    TransactionRow(
                        note = item.note,
                        metaLabel = item.metaLabel,
                        amountLabel = item.amountLabel,
                        category = logCategoryById(item.categoryId),
                        eventTag = item.eventTag,
                        highlighted = item.id == highlightRecordId,
                        onClick = {},
                    )
                }
                Spacer(modifier = Modifier.height(dims.space8))
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 414, heightDp = 868)
@Composable
private fun HomeScreenEmptyPreview() {
    ProExpenseTheme {
        HomeScreen(
            state = HomeSampleData.empty,
            onSeeAll = {},
            onLogFirstExpense = {},
            onQuickAccess = {},
        )
    }
}

@Preview(showBackground = true, widthDp = 414, heightDp = 868)
@Composable
private fun HomeScreenCasualPreview() {
    ProExpenseTheme {
        HomeScreen(
            state = HomeSampleData.casual,
            onSeeAll = {},
            onLogFirstExpense = {},
            onQuickAccess = {},
        )
    }
}
