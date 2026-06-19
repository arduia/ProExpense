package com.arduia.expense.ui.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.arduia.expense.ui.theme.ProExpenseTheme

@Composable
fun SetupStepIndicator(
    currentStep: Int,
    totalSteps: Int,
    modifier: Modifier = Modifier,
) {
    val colors = ProExpenseTheme.colors
    val dimens = ProExpenseTheme.dimensions

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(dimens.space8),
        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
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
