package com.proexpense.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.proexpense.designsystem.icons.ProIcons
import com.proexpense.designsystem.theme.Blue500
import com.proexpense.designsystem.theme.NavInactive
import com.proexpense.designsystem.theme.OnFilled
import com.proexpense.designsystem.theme.White

/* ─────────────────────────────────────────────────────────────────────────
 * ProBottomBar — floating glass tab bar with a raised central Add FAB.
 * See bottom-nav.md. The center Add starts a fresh add flow.
 *
 * The white α.86 + real backdrop blur needs Modifier.blur of the content
 * behind it (RenderEffect, API 31+). Here the bar is a translucent surface;
 * wrap your scaffold content with a blurred layer if you want the full glass.
 * ───────────────────────────────────────────────────────────────────────── */

enum class NavTab(val label: String, val icon: ImageVector) {
    HomeTab("Home", ProIcons.Home),
    BudgetTab("Budget", ProIcons.Budget),
    JournalTab("Journal", ProIcons.Journal),
    MoreTab("More", ProIcons.More),
}

@Composable
fun ProBottomBar(
    active: NavTab,
    onTab: (NavTab) -> Unit,
    onAdd: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val barShape = RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp)
    Box(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .softShadow(elevation = 6.dp, shape = barShape)
                .clip(barShape)
                .background(White.copy(alpha = 0.92f))
                .padding(start = 12.dp, end = 12.dp, top = 8.dp, bottom = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Tab(NavTab.HomeTab, active, onTab, Modifier.weight(1f))
            Tab(NavTab.BudgetTab, active, onTab, Modifier.weight(1f))
            Box(Modifier.weight(1f)) // center slot for the raised FAB
            Tab(NavTab.JournalTab, active, onTab, Modifier.weight(1f))
            Tab(NavTab.MoreTab, active, onTab, Modifier.weight(1f))
        }

        // Raised center Add
        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .offset(y = (-24).dp)
                .size(64.dp)
                .clip(CircleShape)
                .background(Blue500)
                .border(3.dp, Blue500.copy(alpha = 0.8f), CircleShape)
                .pressScale(onClick = onAdd),
            contentAlignment = Alignment.Center,
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(ProIcons.Plus, contentDescription = "Add", tint = White, modifier = Modifier.size(26.dp))
                Text("Add", color = White, fontSize = 10.sp, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

@Composable
private fun Tab(tab: NavTab, active: NavTab, onTab: (NavTab) -> Unit, modifier: Modifier) {
    val on = tab == active
    Column(
        modifier = modifier.pressScale(onClick = { onTab(tab) }).padding(top = 5.dp, bottom = 3.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Icon(
            tab.icon,
            contentDescription = tab.label,
            tint = if (on) Blue500 else NavInactive,
            modifier = Modifier.size(25.dp),
        )
        Text(
            tab.label,
            color = if (on) Blue500 else NavInactive,
            fontSize = 10.sp,
            fontWeight = if (on) FontWeight.SemiBold else FontWeight.Medium,
        )
    }
}
