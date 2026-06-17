package com.arduia.expense.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.arduia.expense.ui.theme.ProExpenseTheme

// Shared horizontal inset for the profile wizard content and CTA.
val ProfileHorizontalPadding = 24.dp

/**
 * Wizard header: progress pills on the left, a `Skip` link on the right.
 *
 * @param step 1-based index of the active step.
 * @param total number of steps in the wizard.
 */
@Composable
fun WizHeader(
    step: Int,
    total: Int,
    onSkip: () -> Unit,
    modifier: Modifier = Modifier,
    showSkip: Boolean = true,
) {
    val colors = ProExpenseTheme.colors
    val typography = ProExpenseTheme.typography

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = ProfileHorizontalPadding, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        WizProgress(step = step, total = total, modifier = Modifier.weight(1f))
        if (showSkip) {
            Text(
                text = "Skip",
                modifier = Modifier
                    .clickable(onClick = onSkip)
                    .padding(4.dp)
                    .semantics { contentDescription = "Skip profile setup" },
                style = typography.onboardingBody,
                color = colors.muted,
            )
        }
    }
}

@Composable
fun WizProgress(
    step: Int,
    total: Int,
    modifier: Modifier = Modifier,
) {
    val colors = ProExpenseTheme.colors

    Row(
        modifier = modifier.semantics {
            contentDescription = "Step $step of $total"
        },
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        repeat(total) { index ->
            val position = index + 1
            val active = position == step
            val completed = position < step
            val pillColor = when {
                active -> colors.primary
                completed -> colors.primarySoft
                else -> colors.lineStrong
            }
            Box(
                modifier = Modifier
                    .height(6.dp)
                    .width(if (active) 22.dp else 6.dp)
                    .clip(RoundedCornerShape(99.dp))
                    .background(pillColor),
            )
        }
    }
}

/**
 * Wizard title block: a mono kicker, a serif title, and a sans sub-paragraph.
 */
@Composable
fun WizTitle(
    kicker: String,
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier,
) {
    val colors = ProExpenseTheme.colors
    val typography = ProExpenseTheme.typography

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = ProfileHorizontalPadding),
    ) {
        Text(
            text = kicker,
            style = typography.eyebrow,
            color = colors.primaryDeep,
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = title,
            style = typography.screenTitle,
            color = colors.ink,
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = subtitle,
            style = typography.body,
            color = colors.ink2,
            textAlign = TextAlign.Start,
        )
    }
}
