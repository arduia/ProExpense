package com.arduia.expense.ui.pin

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.draw.clip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.arduia.expense.R
import com.arduia.expense.ui.design.ProButton
import com.arduia.expense.ui.design.ProButtonSize
import com.arduia.expense.ui.design.ProButtonVariant
import com.arduia.expense.ui.design.ProIcon
import com.arduia.expense.ui.design.ProIconGlyph
import com.arduia.expense.ui.design.ProTopBar
import com.arduia.expense.ui.theme.ProArtboard
import com.arduia.expense.ui.theme.ProExpenseTheme

@Composable
fun PinRecoveryScreen(
    questionText: String,
    answer: String,
    attemptsLabel: String,
    onAnswerChange: (String) -> Unit,
    onVerify: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
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
                title = stringResource(R.string.pin_recover_appbar),
                onBack = onBack,
                backLabel = null,
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
            Box(
                modifier = Modifier
                    .size(dimens.space44 + dimens.space8)
                    .clip(CircleShape)
                    .background(colors.primaryTint),
                contentAlignment = Alignment.Center,
            ) {
                ProIcon(
                    glyph = ProIconGlyph.Sparkle,
                    contentDescription = null,
                    tint = colors.primary,
                    size = dimens.iconNav,
                )
            }
            Text(
                text = stringResource(R.string.pin_recover_heading),
                style = typography.profileScreenTitle.copy(fontFamily = typography.serifFamily),
                color = colors.onSurface,
            )

            Column(verticalArrangement = Arrangement.spacedBy(dimens.space8)) {
                Text(
                    text = stringResource(R.string.pin_security_question_label),
                    style = typography.eyebrow,
                    color = colors.onSurfaceVariant,
                )
                Text(text = questionText, style = typography.body, color = colors.onSurface)
            }

            PinAnswerField(value = answer, onValueChange = onAnswerChange)

            Text(
                text = attemptsLabel,
                style = typography.caption,
                color = colors.onSurfaceMuted,
            )
        }

        ProButton(
            text = stringResource(R.string.pin_recover_action),
            onClick = onVerify,
            variant = ProButtonVariant.Primary,
            size = ProButtonSize.Lg,
            fillMaxWidth = true,
            modifier = Modifier
                .padding(horizontal = dimens.screenPadding)
                .padding(top = dimens.space8, bottom = dimens.space18),
        )
    }
}

@Preview(
    name = "PIN — recovery",
    widthDp = ProArtboard.PIXEL_9_PRO_WIDTH_DP,
    heightDp = ProArtboard.PIXEL_9_PRO_HEIGHT_DP,
    showBackground = true,
)
@Composable
private fun PinRecoveryPreview() {
    ProExpenseTheme {
        PinRecoveryScreen(
            questionText = stringResource(R.string.security_question_pet),
            answer = "Biscuit",
            attemptsLabel = stringResource(R.string.pin_recover_attempts, 2, 5),
            onAnswerChange = {},
            onVerify = {},
            onBack = {},
        )
    }
}
