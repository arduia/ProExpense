package com.arduia.expense.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import com.arduia.expense.domain.FinanceRecord
import com.arduia.expense.domain.Amount
import com.arduia.expense.domain.formatWithSymbol
import com.arduia.expense.feature.logging.logCategoryById
import com.arduia.expense.feature.logging.ui.LogCategoryBadge
import com.arduia.expense.ui.design.IconBell
import com.arduia.expense.ui.theme.ProExpenseTheme
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

data class HomeUiState(
    val profileName: String,
    val homeCurrencyCode: String,
    val dateLabel: String,
    val monthSpendLabel: String,
    val recentRecords: List<FinanceRecord>,
    val highlightRecordId: String? = null,
)

@Composable
fun HomeScreen(
    state: HomeUiState,
    onSeeAll: () -> Unit,
    modifier: Modifier = Modifier,
    zoneId: ZoneId = ZoneId.systemDefault(),
) {
    val colors = ProExpenseTheme.colors
    val typography = ProExpenseTheme.typography
    val dims = ProExpenseTheme.dimensions
    val shapes = ProExpenseTheme.shapes
    val timeFormatter = remember { DateTimeFormatter.ofPattern("h:mm a") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(colors.surface),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = dims.homeHeaderPaddingH)
                .padding(top = dims.space14),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column {
                    Text(
                        text = state.dateLabel,
                        style = typography.eyebrow,
                        color = colors.onSurfaceVariant,
                    )
                    Text(
                        text = buildAnnotatedString {
                            append("Hi, ")
                            withStyle(SpanStyle(color = colors.primary, fontStyle = FontStyle.Italic)) {
                                append(state.profileName)
                            }
                        },
                        style = typography.screenTitle,
                        color = colors.onSurface,
                        modifier = Modifier.padding(top = dims.space4),
                    )
                }
                Box(
                    modifier = Modifier
                        .size(dims.iconButtonSize)
                        .clip(CircleShape)
                        .background(colors.surface)
                        .padding(dims.space8),
                    contentAlignment = Alignment.Center,
                ) {
                    IconBell(color = colors.onSurfaceVariant)
                }
            }

            MonthSpendCard(
                monthSpendLabel = state.monthSpendLabel,
                modifier = Modifier.padding(top = dims.space18),
            )
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(top = dims.space24),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = dims.homeHeaderPaddingH)
                    .padding(bottom = dims.space10),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "Recent",
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

            if (state.recentRecords.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = dims.homeHeaderPaddingH)
                        .padding(top = dims.space32),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = "No expenses yet — tap + to log your first one.",
                        style = typography.body,
                        color = colors.onSurfaceVariant,
                    )
                }
            } else {
                Column(
                    modifier = Modifier
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = dims.homeHeaderPaddingH)
                        .padding(bottom = dims.homeFabBottomOffset),
                ) {
                    state.recentRecords.forEach { record ->
                        RecentRecordRow(
                            record = record,
                            homeCurrencyCode = state.homeCurrencyCode,
                            highlighted = record.id == state.highlightRecordId,
                            timeLabel = Instant.ofEpochMilli(record.recordedAtEpochMillis)
                                .atZone(zoneId)
                                .format(timeFormatter),
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun MonthSpendCard(
    monthSpendLabel: String,
    modifier: Modifier = Modifier,
) {
    val colors = ProExpenseTheme.colors
    val typography = ProExpenseTheme.typography
    val dims = ProExpenseTheme.dimensions
    val shapes = ProExpenseTheme.shapes

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(shapes.card)
            .background(colors.surface)
            .padding(dims.space18),
    ) {
        Text(
            text = "Spent · This month",
            style = typography.eyebrow,
            color = colors.onSurfaceVariant,
        )
        Text(
            text = monthSpendLabel,
            style = typography.displayAmount.copy(fontSize = typography.displayAmount.fontSize * 0.66f),
            color = colors.onSurface,
            modifier = Modifier.padding(top = dims.space6),
        )
    }
}

@Composable
private fun RecentRecordRow(
    record: FinanceRecord,
    homeCurrencyCode: String,
    timeLabel: String,
    highlighted: Boolean,
) {
    val colors = ProExpenseTheme.colors
    val typography = ProExpenseTheme.typography
    val dims = ProExpenseTheme.dimensions
    val category = logCategoryById(record.categoryId)
    val interaction = remember { MutableInteractionSource() }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(ProExpenseTheme.shapes.quickTile)
            .background(if (highlighted) colors.primaryContainer.copy(alpha = 0.35f) else colors.surface)
            .clickable(interactionSource = interaction, indication = ripple(), onClick = {})
            .padding(horizontal = dims.space8, vertical = dims.space12),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(dims.space12),
    ) {
        LogCategoryBadge(category = category)
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = record.note?.takeIf { it.isNotBlank() } ?: category.label,
                style = typography.bodyEmphasis,
                color = colors.onSurface,
                maxLines = 1,
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(dims.space6),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(text = category.label, style = typography.caption, color = colors.onSurfaceVariant)
                Text(text = "·", style = typography.caption, color = colors.onSurfaceVariant)
                Text(text = timeLabel, style = typography.caption, color = colors.onSurfaceVariant)
            }
        }
        Text(
            text = record.homeCurrencyAmount.formatWithSymbol(homeCurrencyCode),
            style = typography.rowAmount,
            color = colors.onSurface,
        )
    }
}

@Preview(showBackground = true, widthDp = 414, heightDp = 868)
@Composable
private fun HomeScreenEmptyPreview() {
    ProExpenseTheme {
        HomeScreen(
            state = HomeUiState(
                profileName = "Maya",
                homeCurrencyCode = "USD",
                dateLabel = "Wed · May 25",
                monthSpendLabel = "$0",
                recentRecords = emptyList(),
            ),
            onSeeAll = {},
        )
    }
}

@Preview(showBackground = true, widthDp = 414, heightDp = 868)
@Composable
private fun HomeScreenWithRecordsPreview() {
    val record = FinanceRecord(
        id = "1",
        amount = com.arduia.expense.domain.Amount(1250),
        currency = com.arduia.expense.domain.CurrencyCode("USD"),
        homeCurrencyAmount = com.arduia.expense.domain.Amount(1250),
        categoryId = "food",
        type = com.arduia.expense.domain.RecordType.EXPENSE,
        note = "Lunch",
        recordedAtEpochMillis = System.currentTimeMillis(),
    )
    ProExpenseTheme {
        HomeScreen(
            state = HomeUiState(
                profileName = "Maya",
                homeCurrencyCode = "USD",
                dateLabel = "Wed · May 25",
                monthSpendLabel = "$12.50",
                recentRecords = listOf(record),
                highlightRecordId = "1",
            ),
            onSeeAll = {},
        )
    }
}
