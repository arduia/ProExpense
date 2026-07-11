package com.arduia.expense.ui.design

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.arduia.expense.ui.theme.ProArtboard
import com.arduia.expense.ui.theme.ProExpenseTheme

/**
 * What kind of row this is — drives badge icon/tint and tap destination, independent of
 * [ProTransactionRowModel.categoryId] (a Split/Debt row still carries a category for
 * filter/report bucketing, but never renders its badge from it).
 */
enum class ProRowKind { EXPENSE, INCOME, SPLIT, DEBT_LENT, DEBT_OWED }

data class ProTransactionRowModel(
    val id: String,
    val categoryId: String,
    val note: String,
    val meta: String,
    val amount: String,
    /** True for an income record — [TransactionRow] renders the amount in the success/green tone. */
    val isIncome: Boolean = false,
    val tag: String? = null,
    /** Secondary line for the linked tag (e.g. its date range) shown on Journal Detail. */
    val tagSubtitle: String? = null,
    /** The record's actual note, pre-fallback — [note] substitutes a placeholder when blank. */
    val rawNote: String? = null,
    /** "Today · 12:44 AM" style label for Journal Detail — [meta] is category + time, not a date. */
    val detailDateTimeLabel: String? = null,
    /** Set when [tag] links to an event, so Journal Detail can navigate to that event's detail. */
    val linkedEventId: String? = null,
    val rowKind: ProRowKind = if (isIncome) ProRowKind.INCOME else ProRowKind.EXPENSE,
    /** Split/debt id to navigate to when [rowKind] isn't a plain [ProRowKind.EXPENSE]/[ProRowKind.INCOME]. */
    val linkedId: String? = null,
)

@Composable
fun TransactionRow(
    categoryId: String,
    note: String,
    meta: String,
    amount: String,
    modifier: Modifier = Modifier,
    isIncome: Boolean = false,
    tag: String? = null,
    showDivider: Boolean = true,
    fresh: Boolean = false,
    rowKind: ProRowKind = if (isIncome) ProRowKind.INCOME else ProRowKind.EXPENSE,
    onClick: (() -> Unit)? = null,
) {
    val colors = ProExpenseTheme.colors
    val dimens = ProExpenseTheme.dimensions
    val typography = ProExpenseTheme.typography
    val interactionSource = remember { MutableInteractionSource() }
    val pulse by animateColorAsState(
        targetValue = if (fresh) colors.primaryTint.copy(alpha = 0f) else Color.Transparent,
        animationSpec = tween(durationMillis = 1800),
        label = "transactionRowPulse",
    )
    val backgroundColor = if (fresh) colors.primaryTint else pulse

    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .then(
                    if (backgroundColor != Color.Transparent) {
                        Modifier.background(backgroundColor)
                    } else {
                        Modifier
                    },
                ).then(
                    if (onClick != null) {
                        Modifier.proClickable(
                            onClick = onClick,
                            shape = ProExpenseTheme.shapes.searchField,
                            interactionSource = interactionSource,
                        )
                    } else {
                        Modifier
                    },
                ).then(
                    if (showDivider) {
                        Modifier.drawBehind {
                            val stroke = 1.dp.toPx()
                            drawLine(
                                color = colors.lineSoft,
                                start = Offset(0f, size.height - stroke / 2f),
                                end = Offset(size.width, size.height - stroke / 2f),
                                strokeWidth = stroke,
                            )
                        }
                    } else {
                        Modifier
                    },
                ).padding(
                    horizontal = dimens.rowPaddingH,
                    vertical = dimens.rowPaddingV,
                ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(dimens.space12),
    ) {
        val badgeOverride = rowKindBadgeOverride(rowKind, colors)
        LogCategoryBadge(
            categoryId = categoryId,
            overrideGlyph = badgeOverride?.first,
            overrideColors = badgeOverride?.second,
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = note.ifBlank { meta.substringBefore(" ·") },
                style = typography.bodyMedium,
                color = colors.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(dimens.space6),
            ) {
                Text(
                    text = meta,
                    style = typography.caption,
                    color = colors.onSurfaceMuted,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                if (tag != null) {
                    Text(
                        text = "·",
                        style = typography.caption,
                        color = colors.onSurfaceMuted,
                    )
                    ProIcon(
                        glyph = ProIconGlyph.At,
                        contentDescription = null,
                        tint = colors.tag,
                        size = dimens.iconTag,
                    )
                    Text(
                        text = tag,
                        style = typography.caption,
                        color = colors.tag,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
        }
        val amountColor =
            when (rowKind) {
                // Recents/Journal is a cash-flow feed, so debt rows follow money direction here —
                // Lent sends cash out (danger), Owe/Borrowed brings cash in (success) — unlike the
                // dedicated Debt Tracker tab, which colors by balance health instead (see
                // design-system-spec/screens/09-debt-tracker.md; that convention is unchanged).
                ProRowKind.DEBT_LENT -> colors.danger
                ProRowKind.DEBT_OWED -> colors.success
                ProRowKind.INCOME -> colors.success
                ProRowKind.SPLIT, ProRowKind.EXPENSE -> colors.onSurface
            }
        Text(
            text = amount,
            style = typography.listAmount,
            color = amountColor,
        )
    }
}

@Composable
fun DayHeader(
    title: String,
    total: String,
    modifier: Modifier = Modifier,
) {
    val colors = ProExpenseTheme.colors
    val dimens = ProExpenseTheme.dimensions
    val typography = ProExpenseTheme.typography

    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .padding(
                    start = dimens.rowPaddingH,
                    end = dimens.rowPaddingH,
                    top = dimens.space12,
                    bottom = dimens.space6,
                ),
        verticalAlignment = Alignment.Bottom,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = title,
            style = typography.sectionHead,
            color = colors.onSurface,
        )
        Text(
            text = total,
            style = typography.monoFigure,
            color = colors.onSurfaceMuted,
        )
    }
}

@Composable
fun DayGroup(
    title: String,
    total: String,
    transactions: List<ProTransactionRowModel>,
    modifier: Modifier = Modifier,
    freshRowId: String? = null,
    onRowClick: ((ProTransactionRowModel) -> Unit)? = null,
    cardWrapped: Boolean = true,
) {
    val dimens = ProExpenseTheme.dimensions

    val groupContent: @Composable () -> Unit = {
        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .then(
                        if (cardWrapped) {
                            Modifier.padding(horizontal = dimens.space14, vertical = dimens.space6)
                        } else {
                            Modifier
                        },
                    ),
        ) {
            DayHeader(title = title, total = total)
            transactions.forEachIndexed { index, item ->
                TransactionRow(
                    categoryId = item.categoryId,
                    note = item.note,
                    meta = item.meta,
                    amount = item.amount,
                    isIncome = item.isIncome,
                    tag = item.tag,
                    fresh = item.id == freshRowId,
                    showDivider = index < transactions.lastIndex,
                    rowKind = item.rowKind,
                    onClick = onRowClick?.let { click -> { click(item) } },
                )
            }
        }
    }

    if (cardWrapped) {
        ProCard(modifier = modifier, padding = 0.dp) {
            groupContent()
        }
    } else {
        Box(modifier = modifier) {
            groupContent()
        }
    }
}

@Preview(showBackground = true, widthDp = ProArtboard.PIXEL_9_PRO_WIDTH_DP)
@Composable
private fun DayGroupCardPreview() {
    ProExpenseTheme {
        DayGroup(
            title = "Today · May 25",
            total = "$42.00",
            transactions =
                listOf(
                    ProTransactionRowModel(
                        id = "1",
                        categoryId = "food",
                        note = "Lunch with M.",
                        meta = "Food · 12:30 PM",
                        amount = "$12.40",
                    ),
                    ProTransactionRowModel(
                        id = "2",
                        categoryId = "entertainment",
                        note = "Movie · Dune",
                        meta = "Entertainment · 08:10 PM",
                        amount = "$18.00",
                        tag = "Bali Trip",
                    ),
                ),
            modifier = Modifier.padding(16.dp),
        )
    }
}

@Preview(showBackground = true, widthDp = ProArtboard.PIXEL_9_PRO_WIDTH_DP)
@Composable
private fun DayGroupFlatPreview() {
    ProExpenseTheme {
        DayGroup(
            title = "Today · May 25",
            total = "$42.00",
            transactions =
                listOf(
                    ProTransactionRowModel(
                        id = "1",
                        categoryId = "food",
                        note = "Lunch with M.",
                        meta = "Food · 12:30 PM",
                        amount = "$12.40",
                    ),
                ),
            cardWrapped = false,
            modifier =
                Modifier
                    .padding(16.dp)
                    .background(ProExpenseTheme.colors.paper),
        )
    }
}

@Preview(showBackground = true, widthDp = ProArtboard.PIXEL_9_PRO_WIDTH_DP)
@Composable
private fun DayGroupMixedIncomeExpensePreview() {
    ProExpenseTheme {
        DayGroup(
            title = "Today · May 25",
            total = "$487.60",
            transactions =
                listOf(
                    ProTransactionRowModel(
                        id = "1",
                        categoryId = "salary",
                        note = "Paycheck",
                        meta = "Salary · 09:00 AM",
                        amount = "$500.00",
                        isIncome = true,
                    ),
                    ProTransactionRowModel(
                        id = "2",
                        categoryId = "food",
                        note = "Lunch with M.",
                        meta = "Food · 12:30 PM",
                        amount = "$12.40",
                    ),
                ),
            modifier = Modifier.padding(16.dp),
        )
    }
}

@Preview(showBackground = true, widthDp = ProArtboard.PIXEL_9_PRO_WIDTH_DP)
@Composable
private fun DayGroupMixedRowKindsPreview() {
    ProExpenseTheme {
        DayGroup(
            title = "Today · May 25",
            total = "$12.40",
            transactions =
                listOf(
                    ProTransactionRowModel(
                        id = "1",
                        categoryId = "food",
                        note = "Lunch with M.",
                        meta = "Food · 12:30 PM",
                        amount = "$12.40",
                    ),
                    ProTransactionRowModel(
                        id = "2",
                        categoryId = "shopping",
                        note = "Shared - Dinner",
                        meta = "Shared split · 07:20 PM",
                        amount = "$49.00",
                        rowKind = ProRowKind.SPLIT,
                        linkedId = "sc1",
                    ),
                    ProTransactionRowModel(
                        id = "3",
                        categoryId = "",
                        note = "Lent to John",
                        meta = "Lent · 03:10 PM",
                        amount = "$50.00",
                        rowKind = ProRowKind.DEBT_LENT,
                        linkedId = "d1",
                    ),
                    ProTransactionRowModel(
                        id = "4",
                        categoryId = "",
                        note = "Owe Priya",
                        meta = "Owe · 09:00 AM",
                        amount = "$25.00",
                        rowKind = ProRowKind.DEBT_OWED,
                        linkedId = "d2",
                    ),
                ),
            modifier = Modifier.padding(16.dp),
        )
    }
}
