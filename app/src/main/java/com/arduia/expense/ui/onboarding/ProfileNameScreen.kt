package com.arduia.expense.ui.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.arduia.expense.R
import com.arduia.expense.ui.design.ProButton
import com.arduia.expense.ui.design.ProButtonSize
import com.arduia.expense.ui.design.ProfileNameField
import com.arduia.expense.ui.design.proClickable
import com.arduia.expense.ui.theme.ProArtboard
import com.arduia.expense.ui.theme.ProExpenseTheme

private const val PROFILE_SETUP_STEPS = 2

@Composable
fun ProfileNameScreen(
    onContinue: (String) -> Unit,
    onSkip: () -> Unit,
    modifier: Modifier = Modifier,
    initialName: String = "",
) {
    val colors = ProExpenseTheme.colors
    val dimens = ProExpenseTheme.dimensions
    val typography = ProExpenseTheme.typography
    var name by rememberSaveable { mutableStateOf(initialName) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(colors.paper)
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(horizontal = dimens.space18),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = dimens.space8),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            SetupStepIndicator(currentStep = 1, totalSteps = PROFILE_SETUP_STEPS)
            Text(
                text = stringResource(R.string.skip),
                style = typography.bodyMedium,
                color = colors.muted,
                modifier = Modifier
                    .proClickable(onClick = onSkip, shape = ProExpenseTheme.shapes.chip)
                    .padding(dimens.space8),
            )
        }

        Spacer(modifier = Modifier.height(dimens.space24))

        Text(
            text = stringResource(R.string.profile_setup_step, 1, PROFILE_SETUP_STEPS),
            style = typography.eyebrow,
            color = colors.primary,
        )
        Text(
            text = stringResource(R.string.profile_setup_title),
            style = typography.screenTitle,
            color = colors.onSurface,
            modifier = Modifier.padding(top = dimens.space12),
        )
        Text(
            text = stringResource(R.string.profile_setup_description),
            style = typography.body,
            color = colors.onSurfaceMuted,
            modifier = Modifier.padding(top = dimens.space16),
        )

        Spacer(modifier = Modifier.height(dimens.space32))

        Text(
            text = stringResource(R.string.profile_name_label),
            style = typography.eyebrow,
            color = colors.primary,
        )
        ProfileNameField(
            value = name,
            onValueChange = { name = it },
            placeholder = stringResource(R.string.profile_name_hint),
            modifier = Modifier.padding(top = dimens.space12),
        )
        Text(
            text = stringResource(R.string.profile_name_helper),
            style = typography.caption,
            color = colors.muted,
            modifier = Modifier.padding(top = dimens.space8),
        )

        Spacer(modifier = Modifier.weight(1f))

        ProButton(
            text = stringResource(R.string.continue_home),
            onClick = { onContinue(name.trim()) },
            enabled = name.isNotBlank(),
            size = ProButtonSize.Lg,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = dimens.space24),
        )
    }
}

@Composable
private fun SetupStepIndicator(
    currentStep: Int,
    totalSteps: Int,
    modifier: Modifier = Modifier,
) {
    val colors = ProExpenseTheme.colors
    val dimens = ProExpenseTheme.dimensions

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(dimens.space8),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        repeat(totalSteps) { index ->
            val step = index + 1
            val active = step <= currentStep
            Box(
                modifier = Modifier
                    .then(
                        if (step == currentStep) {
                            Modifier
                                .width(28.dp)
                                .height(8.dp)
                        } else {
                            Modifier.size(8.dp)
                        },
                    )
                    .clip(
                        if (step == currentStep) {
                            RoundedCornerShape(50)
                        } else {
                            CircleShape
                        },
                    )
                    .background(if (active) colors.primary else colors.lineStrong),
            )
        }
    }
}

@Preview(
    name = "Profile name setup",
    widthDp = ProArtboard.PIXEL_9_PRO_WIDTH_DP,
    heightDp = ProArtboard.PIXEL_9_PRO_HEIGHT_DP,
    showBackground = true,
)
@Composable
private fun ProfileNameScreenPreview() {
    ProExpenseTheme {
        ProfileNameScreen(
            onContinue = {},
            onSkip = {},
            initialName = "Maya",
        )
    }
}
