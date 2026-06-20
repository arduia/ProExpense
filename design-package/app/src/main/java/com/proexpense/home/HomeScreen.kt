package com.proexpense.home

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.proexpense.designsystem.components.CategoryBadge
import com.proexpense.designsystem.components.NavTab
import com.proexpense.designsystem.components.ProBottomBar
import com.proexpense.designsystem.components.ProCard
import com.proexpense.designsystem.components.QuickAccessTile
import com.proexpense.designsystem.components.TransactionRow
import com.proexpense.designsystem.components.pressScale
import com.proexpense.designsystem.icons.ProIcons
import com.proexpense.designsystem.model.Category
import com.proexpense.designsystem.model.EventInfo
import com.proexpense.designsystem.model.Expense
import com.proexpense.designsystem.model.ExpenseTag
import com.proexpense.designsystem.model.HomePersona
import com.proexpense.designsystem.model.currencyFmt
import com.proexpense.designsystem.theme.Blue100
import com.proexpense.designsystem.theme.Blue500
import com.proexpense.designsystem.theme.Blue700
import com.proexpense.designsystem.theme.DarkGray
import com.proexpense.designsystem.theme.Gray100
import com.proexpense.designsystem.theme.Gray200
import com.proexpense.designsystem.theme.Gray500
import com.proexpense.designsystem.theme.Green500
import com.proexpense.designsystem.theme.Line
import com.proexpense.designsystem.theme.Manrope
import com.proexpense.designsystem.theme.ProType
import com.proexpense.designsystem.theme.TagTint
import com.proexpense.designsystem.theme.White
import com.proexpense.designsystem.theme.Yellow300

/* ─────────────────────────────────────────────────────────────────────────
 * HomeScreen — greeting, contextual header (3 persona states), quick access,
 * recent transactions. The header persona is the "all states" axis:
 * Casual / Budget / Event, plus an Empty list state. Mirrors HomeScreen in
 * flow-01-screens.jsx.
 * ───────────────────────────────────────────────────────────────────────── */

data class HomeUiState(
    val userName: String = "Maya",
    val dateLabel: String = "Wed · May 25",
    val persona: HomePersona = HomePersona.Casual,
    val expenses: List<Expense> = emptyList(),
    val event: EventInfo? = null,
    val freshId: String? = null,
)

@Composable
fun HomeScreen(
    state: HomeUiState,
    onAdd: () -> Unit,
    modifier: Modifier = Modifier,
    onCyclePersona: () -> Unit = {},
    onTab: (NavTab) -> Unit = {},
    activeTab: NavTab = NavTab.HomeTab,
) {
    Box(modifier = modifier.fillMaxSize().background(Gray100)) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(bottom = 120.dp),
        ) {
            item { GreetingRow(state) }
            item {
                Box(Modifier.padding(horizontal = 22.dp, vertical = 0.dp)) {
                    ContextHeader(state, onCyclePersona)
                }
            }
            item { QuickAccessRow() }
            item { RecentHeader() }
            if (state.expenses.isEmpty()) {
                item { EmptyState() }
            } else {
                items(state.expenses, key = { it.id }) { e ->
                    Box(Modifier.padding(horizontal = 22.dp)) {
                        TransactionRow(e, fresh = e.id == state.freshId)
                    }
                }
            }
        }

        ProBottomBar(
            active = activeTab,
            onTab = onTab,
            onAdd = onAdd,
            modifier = Modifier.align(Alignment.BottomCenter),
        )
    }
}

@Composable
private fun GreetingRow(state: HomeUiState) {
    Row(
        Modifier.fillMaxWidth().padding(start = 22.dp, end = 22.dp, top = 14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top,
    ) {
        Column {
            Text(state.dateLabel.uppercase(), style = ProType.Eyebrow, color = Gray500)
            Text(
                buildAnnotatedString {
                    append("Hi, ")
                    withStyle(SpanStyle(color = Blue500, fontStyle = FontStyle.Italic)) { append(state.userName) }
                },
                style = ProType.HeroGreeting,
                color = DarkGray,
                modifier = Modifier.padding(top = 4.dp),
            )
        }
        Box(
            Modifier.size(40.dp).clip(CircleShape).background(White)
                .androidBorderCircle().pressScale(onClick = {}),
            contentAlignment = Alignment.Center,
        ) { Icon(ProIcons.Bell, contentDescription = "Notifications", tint = DarkGray, modifier = Modifier.size(18.dp)) }
    }
}

@Composable
private fun ContextHeader(state: HomeUiState, onCycle: () -> Unit) {
    val total = state.expenses.sumOf { it.amount }
    ProCard(modifier = Modifier.fillMaxWidth().padding(top = 18.dp), onClick = onCycle) {
        when (state.persona) {
            HomePersona.Casual -> Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Top) {
                Column {
                    Text("SPENT · MAY", style = ProType.Eyebrow, color = Gray500)
                    Text(currencyFmt(total), style = ProType.CardAmount, color = DarkGray, modifier = Modifier.padding(top = 6.dp))
                    Text(
                        buildAnnotatedString {
                            withStyle(SpanStyle(color = Green500)) { append("↓ 12%") }
                            append(" from last month")
                        },
                        fontFamily = Manrope, fontSize = 12.sp, color = Gray500, modifier = Modifier.padding(top = 6.dp),
                    )
                }
                SparkLine(Modifier.size(width = 86.dp, height = 40.dp))
            }

            HomePersona.Budget -> Column {
                Text("THIS MONTH", style = ProType.Eyebrow, color = Gray500)
                Row(verticalAlignment = Alignment.Bottom, horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(top = 4.dp)) {
                    Text(currencyFmt(total), style = ProType.CardAmount.copy(fontSize = 36.sp), color = DarkGray)
                    Text("/ ${currencyFmt(state.budget())}", style = ProType.SectionHead, color = Gray500)
                }
                ProgressBar(fraction = (total / state.budget()).coerceIn(0.0, 1.0).toFloat(), track = DarkGray.copy(alpha = 0.08f), modifier = Modifier.padding(top = 14.dp))
                Row(Modifier.fillMaxWidth().padding(top = 8.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("${((total / state.budget()) * 100).toInt()}% used", fontFamily = Manrope, fontSize = 11.sp, color = Gray500)
                    Text("${currencyFmt(state.budget() - total)} left", fontFamily = Manrope, fontSize = 11.sp, color = Gray500)
                }
            }

            HomePersona.Event -> {
                val ev = state.event ?: EventInfo("Bali Trip", 1240.0, 2000.0, 14)
                val pct = ((ev.budget - ev.remaining) / ev.budget).coerceIn(0.0, 1.0).toFloat()
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("ACTIVE EVENT", style = ProType.Eyebrow, color = Gray500)
                    Text("${ev.days}d left", style = ProType.MonoFigure, color = Gray500)
                }
                Text(ev.name, style = ProType.SectionHead, color = Blue500, modifier = Modifier.padding(top = 6.dp))
                Row(verticalAlignment = Alignment.Bottom, horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(top = 6.dp)) {
                    Text(currencyFmt(ev.remaining), style = ProType.CardAmount.copy(fontSize = 34.sp), color = DarkGray)
                    Text("left of ${currencyFmt(ev.budget)}", fontFamily = Manrope, fontSize = 11.sp, color = Gray500)
                }
                ProgressBar(fraction = pct, track = Gray200, modifier = Modifier.padding(top = 14.dp))
            }
        }
    }
}

@Composable
private fun ProgressBar(fraction: Float, track: androidx.compose.ui.graphics.Color, modifier: Modifier = Modifier) {
    Box(modifier.fillMaxWidth().height(8.dp).clip(CircleShape).background(track)) {
        Box(Modifier.fillMaxWidth(fraction).height(8.dp).clip(CircleShape).background(Blue500))
    }
}

@Composable
private fun SparkLine(modifier: Modifier) {
    Canvas(modifier) {
        val pts = listOf(2f to 30f, 14f to 22f, 26f to 26f, 38f to 14f, 50f to 18f, 62f to 10f, 74f to 14f, 84f to 6f)
        val sx = size.width / 86f; val sy = size.height / 40f
        for (i in 0 until pts.size - 1) {
            drawLine(
                color = Blue500,
                start = Offset(pts[i].first * sx, pts[i].second * sy),
                end = Offset(pts[i + 1].first * sx, pts[i + 1].second * sy),
                strokeWidth = 1.6.dp.toPx(), cap = StrokeCap.Round,
            )
        }
        drawCircle(Blue500, radius = 3.dp.toPx(), center = Offset(84f * sx, 6f * sy))
    }
}

@Composable
private fun QuickAccessRow() {
    Column(Modifier.padding(start = 22.dp, end = 22.dp, top = 22.dp)) {
        Row(Modifier.fillMaxWidth().padding(bottom = 10.dp), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("QUICK ACCESS", style = ProType.Eyebrow, color = DarkGray)
            Text("Customize", fontFamily = Manrope, fontSize = 11.sp, color = Gray500)
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            QuickAccessTile("Reports", ProIcons.Reports, Blue100, Blue700, onClick = {}, modifier = Modifier.weight(1f))
            QuickAccessTile("Debts", ProIcons.Debt, com.proexpense.designsystem.theme.GreenTint, Green500, onClick = {}, modifier = Modifier.weight(1f))
            QuickAccessTile("Split", ProIcons.Split, TagTint, com.proexpense.designsystem.theme.TagDeep, onClick = {}, modifier = Modifier.weight(1f))
            QuickAccessTile("Events", ProIcons.Events, Yellow300, com.proexpense.designsystem.theme.TagDeep, onClick = {}, modifier = Modifier.weight(1f))
        }
    }
}

@Composable
private fun RecentHeader() {
    Row(
        Modifier.fillMaxWidth().padding(start = 22.dp, end = 22.dp, top = 24.dp, bottom = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text("RECENT", style = ProType.Eyebrow, color = DarkGray)
        Text("See all", fontFamily = Manrope, fontSize = 12.sp, color = Blue500)
    }
}

@Composable
private fun EmptyState() {
    Column(
        Modifier.fillMaxWidth().padding(horizontal = 22.dp, vertical = 40.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Box(Modifier.size(56.dp).clip(CircleShape).background(Blue100), contentAlignment = Alignment.Center) {
            Icon(ProIcons.Sparkle, contentDescription = null, tint = Blue500, modifier = Modifier.size(24.dp))
        }
        Text("No expenses yet", style = ProType.SectionHead, color = DarkGray)
        Text("Tap Add to log your first one.", fontFamily = Manrope, fontSize = 13.sp, color = Gray500)
    }
}

private fun HomeUiState.budget(): Double = com.proexpense.designsystem.model.SampleData.monthBudget

private fun Modifier.androidBorderCircle() =
    this.then(androidx.compose.foundation.border(1.dp, Line, CircleShape))
