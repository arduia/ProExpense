package com.arduia.expense.ui.design

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.arduia.expense.ui.theme.ProExpenseTheme

@Composable
fun SegmentedToggle(
    options: List<String>,
    selectedIndex: Int,
    onSelected: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = ProExpenseTheme.colors
    val dimens = ProExpenseTheme.dimensions
    val typography = ProExpenseTheme.typography
    val containerShape = ProExpenseTheme.shapes.chip

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(containerShape)
            .border(1.dp, colors.lineStrong, containerShape)
            .background(colors.surface)
            .padding(dimens.space4),
        horizontalArrangement = Arrangement.spacedBy(dimens.space4),
    ) {
        options.forEachIndexed { index, label ->
            val selected = index == selectedIndex
            Text(
                text = label,
                style = if (selected) typography.bodySemiBold else typography.bodyMedium,
                color = if (selected) colors.onPrimaryWarm else colors.onSurfaceVariant,
                modifier = Modifier
                    .weight(1f)
                    .clip(containerShape)
                    .background(if (selected) colors.onSurface else colors.surface)
                    .proClickable(onClick = { onSelected(index) }, shape = containerShape)
                    .padding(vertical = dimens.space8),
            )
        }
    }
}
