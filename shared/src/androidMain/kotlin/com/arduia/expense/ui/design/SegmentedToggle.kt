package com.arduia.expense.ui.design

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.arduia.expense.ui.theme.ProArtboard
import com.arduia.expense.ui.theme.ProExpenseTheme

@Composable
fun SegmentedToggle(
    options: List<String>,
    selectedIndex: Int,
    onSelected: (Int) -> Unit,
    modifier: Modifier = Modifier,
    usePrimarySelection: Boolean = false,
) {
    val colors = ProExpenseTheme.colors
    val dimens = ProExpenseTheme.dimensions
    val typography = ProExpenseTheme.typography
    val containerShape = ProExpenseTheme.shapes.chip

    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .clip(containerShape)
                .border(1.dp, colors.lineStrong, containerShape)
                .background(colors.surface)
                .padding(dimens.space4),
        horizontalArrangement = Arrangement.spacedBy(dimens.space4),
    ) {
        options.forEachIndexed { index, label ->
            val selected = index == selectedIndex
            val selectedBackground = if (usePrimarySelection) colors.primary else colors.onSurface
            Box(
                modifier =
                    Modifier
                        .weight(1f)
                        .defaultMinSize(minHeight = dimens.touchTargetMin)
                        .clip(containerShape)
                        .background(if (selected) selectedBackground else colors.surface)
                        .proSelectableClip(selected = selected, onClick = { onSelected(index) }, shape = containerShape)
                        .padding(vertical = dimens.space8),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = label,
                    style = if (selected) typography.bodySemiBold else typography.bodyMedium,
                    color = if (selected) colors.onPrimaryWarm else colors.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                )
            }
        }
    }
}

@Preview(
    name = "SegmentedToggle — default",
    widthDp = ProArtboard.PIXEL_9_PRO_WIDTH_DP,
    showBackground = true,
)
@Composable
private fun SegmentedTogglePreview() {
    ProExpenseTheme {
        var selected by remember { mutableStateOf(0) }
        SegmentedToggle(
            options = listOf("Month", "Week"),
            selectedIndex = selected,
            onSelected = { selected = it },
            modifier = Modifier.padding(16.dp),
        )
    }
}

@Preview(
    name = "SegmentedToggle — 200% font scale",
    widthDp = ProArtboard.PIXEL_9_PRO_WIDTH_DP,
    showBackground = true,
    fontScale = 2f,
)
@Composable
private fun SegmentedToggleLargeFontPreview() {
    ProExpenseTheme {
        var selected by remember { mutableStateOf(1) }
        SegmentedToggle(
            options = listOf("Equal split", "Custom split"),
            selectedIndex = selected,
            onSelected = { selected = it },
            modifier = Modifier.padding(16.dp),
        )
    }
}
