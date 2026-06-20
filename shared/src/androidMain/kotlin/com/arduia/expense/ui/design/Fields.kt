package com.arduia.expense.ui.design

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.arduia.expense.ui.theme.ProExpenseTheme

@Composable
fun ProfileNameField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
) {
    val colors = ProExpenseTheme.colors
    val dimens = ProExpenseTheme.dimensions
    val typography = ProExpenseTheme.typography

    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier
            .fillMaxWidth()
            .clip(ProExpenseTheme.shapes.searchField)
            .border(BorderStroke(1.dp, colors.primary), ProExpenseTheme.shapes.searchField)
            .background(colors.surface)
            .padding(horizontal = dimens.space14, vertical = dimens.space12),
        textStyle = typography.body.copy(color = colors.onSurface),
        cursorBrush = SolidColor(colors.primary),
        singleLine = true,
        decorationBox = { inner ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth(),
            ) {
                ProIcon(
                    glyph = ProIconGlyph.User,
                    contentDescription = null,
                    tint = colors.primary,
                    size = dimens.iconNav,
                )
                Box(modifier = Modifier.padding(start = dimens.space8)) {
                    if (value.isEmpty()) {
                        Text(text = placeholder, style = typography.body, color = colors.muted)
                    }
                    inner()
                }
            }
        },
    )
}

@Composable
fun SearchField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
) {
    val colors = ProExpenseTheme.colors
    val dimens = ProExpenseTheme.dimensions
    val typography = ProExpenseTheme.typography

    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier
            .fillMaxWidth()
            .clip(ProExpenseTheme.shapes.searchField)
            .border(BorderStroke(1.dp, colors.line), ProExpenseTheme.shapes.searchField)
            .background(colors.surface)
            .padding(horizontal = dimens.space14, vertical = dimens.space12),
        textStyle = typography.searchField.copy(color = colors.onSurface),
        cursorBrush = SolidColor(colors.primary),
        singleLine = true,
        decorationBox = { inner ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(dimens.space10),
                modifier = Modifier.fillMaxWidth(),
            ) {
                ProIcon(
                    glyph = ProIconGlyph.Search,
                    contentDescription = null,
                    tint = colors.muted,
                    size = dimens.iconInline,
                )
                Box(modifier = Modifier.weight(1f)) {
                    if (value.isEmpty()) {
                        Text(text = placeholder, style = typography.searchField, color = colors.muted)
                    }
                    inner()
                }
                if (value.isNotEmpty()) {
                    ProIcon(
                        glyph = ProIconGlyph.Close,
                        contentDescription = "Clear",
                        tint = colors.muted,
                        size = dimens.iconClear,
                        modifier = Modifier.proIconClickable(onClick = { onValueChange("") }),
                    )
                }
            }
        },
    )
}

@Composable
fun SearchFieldShell(
    query: String,
    onClear: () -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "Search notes, amount, category…",
    onClick: () -> Unit = {},
) {
    val colors = ProExpenseTheme.colors
    val dimens = ProExpenseTheme.dimensions
    val typography = ProExpenseTheme.typography
    val interactionSource = remember { MutableInteractionSource() }
    val shellShape = ProExpenseTheme.shapes.searchField

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(shellShape)
            .border(BorderStroke(1.dp, colors.line), shellShape)
            .background(colors.surface)
            .proClickable(
                onClick = onClick,
                shape = shellShape,
                interactionSource = interactionSource,
            )
            .padding(horizontal = dimens.space14, vertical = dimens.space12),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(dimens.space10),
    ) {
        ProIcon(
            glyph = ProIconGlyph.Search,
            contentDescription = null,
            tint = colors.muted,
            size = dimens.iconInline,
        )
        Text(
            text = query.ifEmpty { placeholder },
            style = typography.searchField,
            color = if (query.isEmpty()) colors.muted else colors.onSurface,
            modifier = Modifier.weight(1f),
        )
        if (query.isNotEmpty()) {
            ProIcon(
                glyph = ProIconGlyph.Close,
                contentDescription = "Clear",
                tint = colors.muted,
                size = dimens.iconClear,
                modifier = Modifier.proIconClickable(onClick = onClear),
            )
        }
    }
}

@Composable
fun FilterChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = ProExpenseTheme.colors
    val dimens = ProExpenseTheme.dimensions
    val typography = ProExpenseTheme.typography
    val interactionSource = remember { MutableInteractionSource() }
    val chipShape = ProExpenseTheme.shapes.chip
    val background = if (selected) colors.onSurface else Color.Transparent
    val contentColor = if (selected) colors.paper else colors.onSurfaceVariant
    val borderColor = if (selected) colors.onSurface else colors.lineStrong
    val textStyle = if (selected) typography.bodySemiBold.copy(fontSize = 12.sp) else typography.bodyMedium.copy(fontSize = 12.sp)

    Text(
        text = label,
        style = textStyle,
        color = contentColor,
        modifier = modifier
            .proPressScale(interactionSource)
            .clip(chipShape)
            .background(background)
            .border(BorderStroke(1.dp, borderColor), chipShape)
            .proRippleClickable(onClick = onClick, interactionSource = interactionSource)
            .padding(horizontal = dimens.space12, vertical = dimens.space6),
    )
}

@Composable
fun ProFilterChip(
    label: String,
    active: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    FilterChip(
        label = label,
        selected = active,
        onClick = onClick,
        modifier = modifier,
    )
}
