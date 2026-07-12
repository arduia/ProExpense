package com.arduia.expense.ui.design

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.arduia.expense.ui.theme.CategoryColorPair
import com.arduia.expense.ui.theme.ProColors
import com.arduia.expense.ui.theme.ProExpenseTheme

@Composable
fun LogCategoryBadge(
    categoryId: String,
    modifier: Modifier = Modifier,
    size: Dp = ProExpenseTheme.dimensions.iconBadge,
    // A custom category's own id is a generated slug, not a catalogue key — its chosen iconId
    // (US-CAT-2) is the real lookup key. Blank/null falls back to categoryId (default categories,
    // whose id already is the catalogue key).
    iconId: String? = null,
    // A custom category's own chosen tint (US-CAT color option), independent of its icon. Blank/
    // null falls back to the icon-derived tint below.
    colorId: String? = null,
    // Split/Debt rows render a fixed glyph+tint identifying their kind instead of a category
    // lookup — set both together (see [rowKindBadgeOverride]) or leave both null for the default
    // category-driven badge.
    overrideGlyph: ProIconGlyph? = null,
    overrideColors: CategoryColorPair? = null,
) {
    val catalogueKey = iconId?.takeIf { it.isNotBlank() } ?: categoryId
    val resolvedColorId = colorId?.takeIf { it.isNotBlank() }
    val colors =
        when {
            overrideColors != null -> overrideColors
            resolvedColorId != null -> ProExpenseTheme.colors.category(resolvedColorId)
            else -> ProExpenseTheme.colors.category(catalogueKey)
        }
    val glyph = overrideGlyph ?: categoryIcon(catalogueKey)
    val iconSize = size * 0.52f
    Box(
        modifier =
            modifier
                .size(size)
                .clip(CircleShape)
                .background(colors.tint),
        contentAlignment = Alignment.Center,
    ) {
        ProIcon(
            glyph = glyph,
            contentDescription = null,
            tint = colors.accent,
            size = iconSize,
        )
    }
}

/** [ProIconGlyph] + [CategoryColorPair] pair for a non-category-driven row kind, or `null` for the
 * default category badge (plain [ProRowKind.EXPENSE]/[ProRowKind.INCOME]). */
fun rowKindBadgeOverride(
    rowKind: ProRowKind,
    colors: ProColors,
): Pair<ProIconGlyph, CategoryColorPair>? =
    when (rowKind) {
        ProRowKind.SPLIT -> ProIconGlyph.FeatSplit to CategoryColorPair(accent = colors.tagDeep, tint = colors.tagTint)
        // Badge color is independent of the amount's cash-flow color (see TransactionRow) —
        // Lent = success/green, Owe = danger/red, matching the Debt Tracker's own convention
        // (design-system-spec/screens/09-debt-tracker.md).
        ProRowKind.DEBT_LENT -> ProIconGlyph.FeatDebt to CategoryColorPair(accent = colors.success, tint = colors.successTint)
        ProRowKind.DEBT_OWED -> ProIconGlyph.FeatDebt to CategoryColorPair(accent = colors.danger, tint = colors.dangerTint)
        ProRowKind.EXPENSE, ProRowKind.INCOME -> null
    }

@Composable
fun CategoryChip(
    label: String,
    categoryId: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = ProExpenseTheme.colors
    val category = colors.category(categoryId)
    val dimens = ProExpenseTheme.dimensions
    val typography = ProExpenseTheme.typography
    val interactionSource = remember { MutableInteractionSource() }
    val chipShape = ProExpenseTheme.shapes.chip
    val background = if (selected) category.accent else Color.Transparent
    val contentColor = if (selected) colors.onPrimaryWarm else colors.onSurfaceVariant
    val borderColor = if (selected) category.accent else colors.lineStrong
    val textStyle =
        (if (selected) typography.bodySemiBold else typography.bodyMedium)
            .copy(fontSize = 12.5.sp)

    Row(
        modifier =
            modifier
                .proPressScale(interactionSource)
                .clip(chipShape)
                .background(background)
                .border(BorderStroke(dimens.chipBorderWidth, borderColor), chipShape)
                .proSelectableClickable(selected = selected, onClick = onClick, interactionSource = interactionSource)
                .padding(start = dimens.space8, top = 7.dp, end = dimens.space12, bottom = 7.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(dimens.space6),
    ) {
        ProIcon(
            glyph = categoryIcon(categoryId),
            contentDescription = null,
            tint = if (selected) colors.onPrimaryWarm else category.accent,
            size = dimens.iconChipLeading,
        )
        Text(text = label, style = textStyle, color = contentColor)
    }
}
