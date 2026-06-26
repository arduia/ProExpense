package com.arduia.expense.feature.auth.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.arduia.expense.feature.auth.R
import com.arduia.expense.ui.design.ProButton
import com.arduia.expense.ui.design.ProButtonSize
import com.arduia.expense.ui.design.ProButtonVariant
import com.arduia.expense.ui.design.ProIcon
import com.arduia.expense.ui.design.ProIconGlyph
import com.arduia.expense.ui.design.ProTopBar
import com.arduia.expense.ui.design.proClickable
import com.arduia.expense.ui.design.proIconClickable
import com.arduia.expense.feature.auth.ui.preview.PinSetupUiState
import com.arduia.expense.feature.auth.ui.preview.previewPinSetup
import com.arduia.expense.ui.theme.ProArtboard
import com.arduia.expense.ui.theme.ProExpenseTheme

@Composable
fun PinSetupScreen(
    state: PinSetupUiState,
    onTogglePin: (Boolean) -> Unit,
    onToggleBiometric: (Boolean) -> Unit,
    onRevealNew: () -> Unit,
    onRevealConfirm: () -> Unit,
    onRecoveryClick: () -> Unit,
    onSave: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    saveEnabled: Boolean = true,
    errorMessage: String? = null,
) {
    val colors = ProExpenseTheme.colors
    val dimens = ProExpenseTheme.dimensions
    val typography = ProExpenseTheme.typography

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(colors.paper)
            .statusBarsPadding()
            .navigationBarsPadding(),
    ) {
        Box(modifier = Modifier.padding(horizontal = dimens.screenPadding)) {
            ProTopBar(
                title = stringResource(R.string.pin_setup_appbar),
                onBack = onBack,
                backLabel = stringResource(R.string.pin_setup_back),
            )
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = dimens.screenPadding)
                .padding(top = dimens.space8),
            verticalArrangement = Arrangement.spacedBy(dimens.space16),
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(dimens.space8),
            ) {
                Box(
                    modifier = Modifier
                        .size(dimens.space44 + dimens.space8)
                        .clip(ProExpenseTheme.shapes.tile)
                        .background(colors.primaryTint),
                    contentAlignment = Alignment.Center,
                ) {
                    ProIcon(
                        glyph = ProIconGlyph.Fingerprint,
                        contentDescription = null,
                        tint = colors.primary,
                        size = dimens.iconNav,
                    )
                }
                Text(
                    text = stringResource(R.string.pin_setup_heading),
                    style = typography.profileScreenTitle,
                    color = colors.onSurface,
                )
                Text(
                    text = stringResource(R.string.pin_setup_subheading),
                    style = typography.caption,
                    color = colors.onSurfaceMuted,
                    textAlign = TextAlign.Center,
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(ProExpenseTheme.shapes.card)
                    .border(BorderStroke(1.dp, colors.line), ProExpenseTheme.shapes.card)
                    .background(colors.surface),
            ) {
                PinSwitchRow(
                    icon = ProIconGlyph.Eye,
                    title = stringResource(R.string.pin_setup_toggle_pin),
                    subtitle = null,
                    checked = state.pinAuthOn,
                    enabled = true,
                    onCheckedChange = onTogglePin,
                )
                Box(Modifier.fillMaxWidth().height(1.dp).background(colors.lineSoft))
                PinSwitchRow(
                    icon = ProIconGlyph.Fingerprint,
                    title = stringResource(R.string.pin_setup_toggle_biometric),
                    subtitle = stringResource(R.string.pin_setup_requires_pin),
                    checked = state.biometricOn,
                    enabled = false,
                    onCheckedChange = onToggleBiometric,
                )
            }

            PinFieldSection(
                label = stringResource(R.string.pin_setup_new_label),
                filled = state.newPinFilled,
                dotColor = colors.primary,
                active = true,
                onReveal = onRevealNew,
            )

            PinFieldSection(
                label = stringResource(R.string.pin_setup_confirm_label),
                filled = state.confirmPinFilled,
                dotColor = colors.onSurface,
                active = false,
                onReveal = onRevealConfirm,
            )

            Column(verticalArrangement = Arrangement.spacedBy(dimens.space8)) {
                Text(
                    text = stringResource(R.string.pin_setup_recovery_label),
                    style = typography.eyebrow,
                    color = colors.onSurfaceVariant,
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(ProExpenseTheme.shapes.card)
                        .border(BorderStroke(1.dp, colors.line), ProExpenseTheme.shapes.card)
                        .background(colors.surface)
                        .proClickable(onClick = onRecoveryClick, shape = ProExpenseTheme.shapes.card)
                        .padding(dimens.space14),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = stringResource(R.string.pin_setup_recovery_title),
                            style = typography.bodySemiBold,
                            color = colors.onSurface,
                        )
                        Text(
                            text = stringResource(R.string.pin_setup_recovery_subtitle),
                            style = typography.caption,
                            color = colors.onSurfaceMuted,
                            modifier = Modifier.padding(top = dimens.space2),
                        )
                    }
                    ProIcon(
                        glyph = ProIconGlyph.ChevronRight,
                        contentDescription = null,
                        tint = colors.muted,
                        size = dimens.iconInline,
                    )
                }
            }
        }

        if (errorMessage != null) {
            Text(
                text = errorMessage,
                style = typography.caption,
                color = colors.danger,
                modifier = Modifier
                    .padding(horizontal = dimens.screenPadding)
                    .padding(top = dimens.space8),
            )
        }

        ProButton(
            text = stringResource(R.string.pin_setup_save),
            onClick = onSave,
            enabled = saveEnabled,
            variant = ProButtonVariant.Primary,
            size = ProButtonSize.Lg,
            fillMaxWidth = true,
            modifier = Modifier
                .padding(horizontal = dimens.screenPadding)
                .padding(top = dimens.space8, bottom = dimens.space18),
        )
    }
}

@Composable
private fun PinSwitchRow(
    icon: ProIconGlyph,
    title: String,
    subtitle: String?,
    checked: Boolean,
    enabled: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    val colors = ProExpenseTheme.colors
    val dimens = ProExpenseTheme.dimensions
    val typography = ProExpenseTheme.typography

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = dimens.space14, vertical = dimens.space12),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(dimens.space12),
    ) {
        ProIcon(
            glyph = icon,
            contentDescription = null,
            tint = colors.onSurfaceMuted,
            size = dimens.iconNav,
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, style = typography.bodyMedium, color = colors.onSurface)
            if (subtitle != null) {
                Text(
                    text = subtitle,
                    style = typography.caption,
                    color = colors.onSurfaceMuted,
                    modifier = Modifier.padding(top = dimens.space2),
                )
            }
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            enabled = enabled,
            colors = SwitchDefaults.colors(
                checkedThumbColor = colors.onPrimaryWarm,
                checkedTrackColor = colors.primary,
                uncheckedThumbColor = colors.surface,
                uncheckedTrackColor = colors.lineStrong,
                uncheckedBorderColor = colors.lineStrong,
            ),
        )
    }
}

@Composable
private fun PinFieldSection(
    label: String,
    filled: Int,
    dotColor: Color,
    active: Boolean,
    onReveal: () -> Unit,
) {
    val colors = ProExpenseTheme.colors
    val dimens = ProExpenseTheme.dimensions
    val typography = ProExpenseTheme.typography
    val borderColor = if (active) colors.primary else colors.line

    Column(verticalArrangement = Arrangement.spacedBy(dimens.space8)) {
        Text(text = label, style = typography.eyebrow, color = colors.onSurfaceVariant)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(ProExpenseTheme.shapes.searchField)
                .border(BorderStroke(1.dp, borderColor), ProExpenseTheme.shapes.searchField)
                .background(colors.surface)
                .padding(horizontal = dimens.space16, vertical = dimens.space14),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.spacedBy(dimens.space12),
            ) {
                repeat(6) { index ->
                    val isFilled = index < filled
                    Box(
                        modifier = Modifier
                            .size(dimens.space10)
                            .clip(CircleShape)
                            .then(
                                if (isFilled) {
                                    Modifier.background(dotColor)
                                } else {
                                    Modifier.border(BorderStroke(1.5.dp, colors.lineStrong), CircleShape)
                                },
                            ),
                    )
                }
            }
            ProIcon(
                glyph = ProIconGlyph.Eye,
                contentDescription = stringResource(R.string.pin_setup_reveal_cd),
                tint = colors.muted,
                size = dimens.iconNav,
                modifier = Modifier.proIconClickable(onClick = onReveal),
            )
        }
    }
}

@Preview(
    name = "PIN setup — enable",
    widthDp = ProArtboard.PIXEL_9_PRO_WIDTH_DP,
    heightDp = ProArtboard.PIXEL_9_PRO_HEIGHT_DP,
    showBackground = true,
)
@Composable
private fun PinSetupPreview() {
    ProExpenseTheme {
        PinSetupScreen(
            state = previewPinSetup,
            onTogglePin = {},
            onToggleBiometric = {},
            onRevealNew = {},
            onRevealConfirm = {},
            onRecoveryClick = {},
            onSave = {},
            onBack = {},
        )
    }
}
