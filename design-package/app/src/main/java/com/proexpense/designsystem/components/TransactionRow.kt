package com.proexpense.designsystem.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.proexpense.designsystem.icons.ProIcons
import com.proexpense.designsystem.model.Expense
import com.proexpense.designsystem.model.ExpenseTag
import com.proexpense.designsystem.model.currencyFmt
import com.proexpense.designsystem.theme.Blue100
import com.proexpense.designsystem.theme.DarkGray
import com.proexpense.designsystem.theme.Gray500
import com.proexpense.designsystem.theme.Line2
import com.proexpense.designsystem.theme.Manrope
import com.proexpense.designsystem.theme.ProType
import com.proexpense.designsystem.theme.Tag

/* TransactionRow — badge, two-line label, serif amount. See transaction-row.md.
 * `fresh` plays the insert highlight pulse (clayTint → transparent). */
@Composable
fun TransactionRow(
    expense: Expense,
    modifier: Modifier = Modifier,
    fresh: Boolean = false,
    onClick: (() -> Unit)? = null,
) {
    val pulse by animateColorAsState(
        targetValue = if (fresh) Blue100.copy(alpha = 0.0f) else Color.Transparent,
        animationSpec = tween(1800),
        label = "rowPulse",
    )
    val bg = if (fresh) Blue100 else pulse
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(bg)
            .let { if (onClick != null) it.pressScale(onClick = onClick) else it }
            .padding(horizontal = 8.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        CategoryBadge(expense.category, size = 38.dp)
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = expense.note.ifEmpty { expense.category.label },
                color = DarkGray,
                fontFamily = Manrope,
                fontWeight = FontWeight.Medium,
                fontSize = 14.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Text("${expense.category.label} · ${expense.time}", color = Gray500, fontFamily = Manrope, fontSize = 11.5.sp, maxLines = 1)
                (expense.tag as? ExpenseTag.Event)?.let { ev ->
                    Text("·", color = Gray500, fontSize = 11.5.sp)
                    Icon(ProIcons.At, contentDescription = null, tint = Tag, modifier = Modifier.padding(end = 2.dp))
                    Text(ev.label, color = Tag, fontFamily = Manrope, fontSize = 11.5.sp, maxLines = 1)
                }
            }
        }
        Text(currencyFmt(expense.amount), color = DarkGray, style = ProType.RowAmount)
    }
}

/* DayGroup — serif date header + mono running total + its rows. */
@Composable
fun DayGroup(
    title: String,
    expenses: List<Expense>,
    modifier: Modifier = Modifier,
    freshId: String? = null,
) {
    ProCard(modifier = modifier, padding = 0.dp) {
        Column(modifier = Modifier.padding(horizontal = 14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(start = 8.dp, end = 8.dp, top = 12.dp, bottom = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom,
            ) {
                Text(title, style = ProType.SectionHead, color = DarkGray)
                Text(currencyFmt(expenses.sumOf { it.amount }), style = ProType.MonoFigure, color = Gray500)
            }
            expenses.forEachIndexed { i, e ->
                TransactionRow(e, fresh = e.id == freshId)
                if (i < expenses.lastIndex) {
                    androidx.compose.material3.HorizontalDivider(thickness = 1.dp, color = Line2)
                }
            }
        }
    }
}
