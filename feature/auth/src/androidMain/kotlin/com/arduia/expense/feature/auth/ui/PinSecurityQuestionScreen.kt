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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.arduia.expense.feature.auth.R
import com.arduia.expense.feature.auth.ui.preview.PinSecurityQuestionUi
import com.arduia.expense.feature.auth.ui.preview.pinSecurityQuestions
import com.arduia.expense.ui.design.ProButton
import com.arduia.expense.ui.design.ProButtonSize
import com.arduia.expense.ui.design.ProButtonVariant
import com.arduia.expense.ui.design.ProFlatHeader
import com.arduia.expense.ui.design.proClickable
import com.arduia.expense.ui.theme.ProArtboard
import com.arduia.expense.ui.theme.ProExpenseTheme

@Composable
fun PinSecurityQuestionScreen(
    questions: List<PinSecurityQuestionUi>,
    selectedId: String,
    answer: String,
    onSelect: (String) -> Unit,
    onAnswerChange: (String) -> Unit,
    onEnable: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    val colors = ProExpenseTheme.colors
    val dimens = ProExpenseTheme.dimensions
    val typography = ProExpenseTheme.typography

    Column(
        modifier =
            modifier
                .fillMaxSize()
                .background(colors.paper)
                .statusBarsPadding()
                .navigationBarsPadding(),
    ) {
        ProFlatHeader(
            title = stringResource(R.string.pin_recovery_appbar),
            onBack = onBack,
            modifier =
                Modifier
                    .padding(horizontal = dimens.screenPadding)
                    .padding(vertical = dimens.space14),
        )

        Column(
            modifier =
                Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = dimens.screenPadding)
                    .padding(top = dimens.space8),
            verticalArrangement = Arrangement.spacedBy(dimens.space16),
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(dimens.space8)) {
                Text(
                    text = stringResource(R.string.pin_security_heading),
                    style = typography.profileScreenTitle,
                    color = colors.onSurface,
                )
                Text(
                    text = stringResource(R.string.pin_security_subheading),
                    style = typography.body,
                    color = colors.onSurfaceMuted,
                )
            }

            Column(verticalArrangement = Arrangement.spacedBy(dimens.space8)) {
                Text(
                    text = stringResource(R.string.pin_security_question_label),
                    style = typography.eyebrow,
                    color = colors.onSurfaceVariant,
                )
                questions.forEach { question ->
                    PinQuestionRow(
                        text = stringResource(question.textRes),
                        selected = question.id == selectedId,
                        onClick = { onSelect(question.id) },
                    )
                }
            }

            Column(verticalArrangement = Arrangement.spacedBy(dimens.space8)) {
                Text(
                    text = stringResource(R.string.pin_security_answer_label),
                    style = typography.eyebrow,
                    color = colors.onSurfaceVariant,
                )
                PinAnswerField(value = answer, onValueChange = onAnswerChange)
            }
        }

        ProButton(
            text = stringResource(R.string.pin_security_enable),
            onClick = onEnable,
            enabled = enabled,
            variant = ProButtonVariant.Primary,
            size = ProButtonSize.Lg,
            fillMaxWidth = true,
            modifier =
                Modifier
                    .padding(horizontal = dimens.screenPadding)
                    .padding(top = dimens.space8, bottom = dimens.space18),
        )
    }
}

@Composable
private fun PinQuestionRow(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
) {
    val colors = ProExpenseTheme.colors
    val dimens = ProExpenseTheme.dimensions
    val typography = ProExpenseTheme.typography
    val borderColor = if (selected) colors.primary else colors.line
    val background = if (selected) colors.primaryTint else colors.surface

    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .clip(ProExpenseTheme.shapes.card)
                .border(BorderStroke(1.dp, borderColor), ProExpenseTheme.shapes.card)
                .background(background)
                .proClickable(onClick = onClick, shape = ProExpenseTheme.shapes.card)
                .padding(dimens.space14),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(dimens.space12),
    ) {
        Box(
            modifier =
                Modifier
                    .size(dimens.space20)
                    .clip(CircleShape)
                    .border(BorderStroke(2.dp, if (selected) colors.primary else colors.lineStrong), CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            if (selected) {
                Box(
                    modifier =
                        Modifier
                            .size(dimens.space10)
                            .clip(CircleShape)
                            .background(colors.primary),
                )
            }
        }
        Text(
            text = text,
            style = typography.bodyMedium,
            color = colors.onSurface,
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
internal fun PinAnswerField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = ProExpenseTheme.colors
    val dimens = ProExpenseTheme.dimensions
    val typography = ProExpenseTheme.typography

    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        modifier =
            modifier
                .fillMaxWidth()
                .clip(ProExpenseTheme.shapes.searchField)
                .border(BorderStroke(1.dp, colors.primary), ProExpenseTheme.shapes.searchField)
                .background(colors.surface)
                .padding(horizontal = dimens.space14, vertical = dimens.space14),
        textStyle = typography.body.copy(color = colors.onSurface),
        cursorBrush = SolidColor(colors.primary),
        singleLine = true,
    )
}

@Preview(
    name = "PIN — security question",
    widthDp = ProArtboard.PIXEL_9_PRO_WIDTH_DP,
    heightDp = ProArtboard.PIXEL_9_PRO_HEIGHT_DP,
    showBackground = true,
)
@Composable
private fun PinSecurityQuestionPreview() {
    ProExpenseTheme {
        PinSecurityQuestionScreen(
            questions = pinSecurityQuestions,
            selectedId = "pet",
            answer = "Biscuit",
            onSelect = {},
            onAnswerChange = {},
            onEnable = {},
            onBack = {},
        )
    }
}

@Preview(
    name = "PIN — security question (dark)",
    widthDp = ProArtboard.PIXEL_9_PRO_WIDTH_DP,
    heightDp = ProArtboard.PIXEL_9_PRO_HEIGHT_DP,
    showBackground = true,
)
@Composable
private fun PinSecurityQuestionDarkPreview() {
    ProExpenseTheme(darkTheme = true) {
        PinSecurityQuestionScreen(
            questions = pinSecurityQuestions,
            selectedId = "pet",
            answer = "Biscuit",
            onSelect = {},
            onAnswerChange = {},
            onEnable = {},
            onBack = {},
        )
    }
}
