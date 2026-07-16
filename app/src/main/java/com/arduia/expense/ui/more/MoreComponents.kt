package com.arduia.expense.ui.more

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.arduia.expense.ui.design.GlitchText
import com.arduia.expense.ui.design.ProIcon
import com.arduia.expense.ui.design.ProIconGlyph
import com.arduia.expense.ui.design.proClickable
import com.arduia.expense.ui.preview.MoreFeatureRowUi
import com.arduia.expense.ui.preview.MoreProfileUi
import com.arduia.expense.ui.preview.MoreSettingKind
import com.arduia.expense.ui.preview.MoreSettingRowUi
import com.arduia.expense.ui.theme.ProExpenseTheme
import com.arduia.expense.ui.theme.centeredGlyph

@Composable
fun MoreProfileCard(
    profile: MoreProfileUi,
    modifier: Modifier = Modifier,
) {
    val colors = ProExpenseTheme.colors
    val dimens = ProExpenseTheme.dimensions
    val typography = ProExpenseTheme.typography

    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .clip(ProExpenseTheme.shapes.card)
                .border(BorderStroke(1.dp, colors.line), ProExpenseTheme.shapes.card)
                .background(colors.surface)
                .padding(dimens.space14),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(dimens.space12),
    ) {
        Box(
            modifier =
                Modifier
                    .size(dimens.iconBadge)
                    .clip(CircleShape)
                    .background(colors.primaryTint),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = profile.initial,
                style = typography.bodySemiBold.centeredGlyph(),
                color = colors.primary,
                textAlign = TextAlign.Center,
            )
        }
        Column(modifier = Modifier.weight(1f)) {
            GlitchText(
                text = profile.name,
                style = typography.bodySemiBold,
                color = colors.onSurface,
                playbackKey = profile.name,
            )
            Text(
                text = profile.subtitle,
                style = typography.caption,
                color = colors.onSurfaceMuted,
                modifier = Modifier.padding(top = dimens.space2),
            )
        }
    }
}

@Composable
fun <T> MoreGroupCard(
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
            if (index > 0) MoreRowDivider()
            row(item)
        }
    }
}

@Composable
private fun MoreRowDivider() {
    val colors = ProExpenseTheme.colors
    Box(
        modifier =
            Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(colors.lineSoft),
    )
}

@Composable
fun MoreFeatureRow(
    feature: MoreFeatureRowUi,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = ProExpenseTheme.colors
    val dimens = ProExpenseTheme.dimensions
    val typography = ProExpenseTheme.typography

    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .proClickable(onClick = onClick, shape = androidx.compose.ui.graphics.RectangleShape)
                .padding(dimens.space14),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(dimens.space12),
    ) {
        Box(
            modifier =
                Modifier
                    .size(dimens.quickAccessIconSize)
                    .clip(ProExpenseTheme.shapes.tile)
                    .background(colors.primaryTint),
            contentAlignment = Alignment.Center,
        ) {
            ProIcon(
                glyph = feature.icon,
                contentDescription = null,
                tint = colors.primary,
                size = dimens.iconInline + 4.dp,
            )
        }
        Text(
            text = feature.label,
            style = typography.bodySemiBold,
            color = colors.onSurface,
            modifier = Modifier.weight(1f),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        ProIcon(
            glyph = ProIconGlyph.ChevronRight,
            contentDescription = null,
            tint = colors.muted,
            size = dimens.iconInline,
        )
    }
}

@Composable
fun MoreSettingRow(
    setting: MoreSettingRowUi,
    onClick: () -> Unit,
    onToggle: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = ProExpenseTheme.colors
    val dimens = ProExpenseTheme.dimensions
    val typography = ProExpenseTheme.typography
    val isNav = setting.kind == MoreSettingKind.Nav
    // A disabled Toggle row (e.g. Biometric while PIN is off) still needs to catch a tap so it
    // can explain why it's blocked — Switch(enabled = false) swallows touches entirely.
    val isRowClickable = isNav || !setting.enabled

    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .then(
                    if (isRowClickable) {
                        Modifier.proClickable(
                            onClick = onClick,
                            shape = androidx.compose.ui.graphics.RectangleShape,
                        )
                    } else {
                        Modifier
                    },
                ).padding(horizontal = dimens.space14, vertical = dimens.space12)
                .alpha(if (setting.enabled) 1f else 0.5f),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(dimens.space12),
    ) {
        Box(
            modifier =
                Modifier
                    .size(dimens.quickAccessIconSize)
                    .clip(CircleShape)
                    .background(colors.paperAlt),
            contentAlignment = Alignment.Center,
        ) {
            ProIcon(
                glyph = setting.icon,
                contentDescription = null,
                tint = colors.onSurfaceMuted,
                size = dimens.iconInline,
            )
        }
        Text(
            text = setting.label,
            style = typography.bodyMedium,
            color = colors.onSurface,
            modifier = Modifier.weight(1f),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        when (setting.kind) {
            MoreSettingKind.Toggle ->
                Switch(
                    checked = setting.toggleOn,
                    onCheckedChange = onToggle,
                    enabled = setting.enabled,
                    colors =
                        SwitchDefaults.colors(
                            checkedThumbColor = colors.onPrimaryWarm,
                            checkedTrackColor = colors.primary,
                            uncheckedThumbColor = colors.surface,
                            uncheckedTrackColor = colors.lineStrong,
                            uncheckedBorderColor = colors.lineStrong,
                        ),
                )
            else -> {
                if (setting.value != null) {
                    Text(
                        text = setting.value,
                        style = typography.monoFigure,
                        color = colors.onSurfaceMuted,
                    )
                }
                if (isNav) {
                    ProIcon(
                        glyph = ProIconGlyph.ChevronRight,
                        contentDescription = null,
                        tint = colors.muted,
                        size = dimens.iconInline,
                    )
                }
            }
        }
    }
}
