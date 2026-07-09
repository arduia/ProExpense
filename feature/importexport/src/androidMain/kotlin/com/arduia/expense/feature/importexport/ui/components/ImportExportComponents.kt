package com.arduia.expense.feature.importexport.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.arduia.expense.feature.importexport.ui.preview.MoreClearOptionUi
import com.arduia.expense.ui.design.ProIcon
import com.arduia.expense.ui.design.ProIconGlyph
import com.arduia.expense.ui.design.proClickable
import com.arduia.expense.ui.theme.ProExpenseTheme

@Composable
fun <T> ImportExportGroupCard(
    items: List<T>,
    modifier: Modifier = Modifier,
    row: @Composable (T) -> Unit,
) {
    val colors = ProExpenseTheme.colors

    Column(
        modifier =
            modifier
                .fillMaxWidth()
                .clip(ProExpenseTheme.shapes.card)
                .border(BorderStroke(1.dp, colors.line), ProExpenseTheme.shapes.card)
                .background(colors.surface),
    ) {
        items.forEachIndexed { index, item ->
            if (index > 0) {
                Box(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                            .background(colors.lineSoft),
                )
            }
            row(item)
        }
    }
}

@Composable
fun ClearDataCard(
    option: MoreClearOptionUi,
    checked: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = ProExpenseTheme.colors
    val dimens = ProExpenseTheme.dimensions
    val typography = ProExpenseTheme.typography
    val labelColor = if (option.destructive) colors.danger else colors.onSurface

    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .clip(ProExpenseTheme.shapes.card)
                .border(BorderStroke(1.dp, colors.line), ProExpenseTheme.shapes.card)
                .background(colors.surface)
                .proClickable(onClick = onToggle, shape = ProExpenseTheme.shapes.card)
                .padding(dimens.space14),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(dimens.space12),
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = option.label, style = typography.bodySemiBold, color = labelColor)
            Text(
                text = option.subtitle,
                style = typography.caption,
                color = colors.onSurfaceMuted,
                modifier = Modifier.padding(top = dimens.space2),
            )
        }
        ClearCheckBox(checked = checked)
    }
}

@Composable
private fun ClearCheckBox(checked: Boolean) {
    val colors = ProExpenseTheme.colors
    val dimens = ProExpenseTheme.dimensions
    val shape =
        androidx.compose.foundation.shape
            .RoundedCornerShape(dimens.space7)

    Box(
        modifier =
            Modifier
                .size(dimens.space24)
                .clip(shape)
                .then(
                    if (checked) {
                        Modifier.background(colors.primary)
                    } else {
                        Modifier.border(BorderStroke(1.5.dp, colors.lineStrong), shape)
                    },
                ),
        contentAlignment = Alignment.Center,
    ) {
        if (checked) {
            ProIcon(
                glyph = ProIconGlyph.Check,
                contentDescription = null,
                tint = colors.onPrimaryWarm,
                size = dimens.iconInline,
            )
        }
    }
}

@Composable
fun ExportFileRow(
    fileName: String,
    subtitle: String,
    modifier: Modifier = Modifier,
) {
    val colors = ProExpenseTheme.colors
    val dimens = ProExpenseTheme.dimensions
    val typography = ProExpenseTheme.typography

    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .padding(dimens.space14),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(dimens.space12),
    ) {
        ProIcon(
            glyph = ProIconGlyph.Note,
            contentDescription = null,
            tint = colors.muted,
            size = dimens.iconNav,
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(text = fileName, style = typography.monoFigure, color = colors.onSurface)
            Text(
                text = subtitle,
                style = typography.caption,
                color = colors.onSurfaceMuted,
                modifier = Modifier.padding(top = dimens.space2),
            )
        }
    }
}
