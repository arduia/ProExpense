package com.arduia.expense.ui.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.unit.dp
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.arduia.expense.ui.theme.ProExpenseTheme
import com.github.takahirom.roborazzi.captureRoboImage
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(AndroidJUnit4::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [33], qualifiers = "w414dp-h868dp")
class OnboardingScreenshotTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun onboarding_welcomeSlide() {
        composeTestRule.setContent {
            ProExpenseTheme {
                OnboardingScreen(onFinish = {})
            }
        }
        composeTestRule.onRoot().captureRoboImage()
    }

    @Test
    fun onboarding_allIllustrations() {
        composeTestRule.setContent {
            ProExpenseTheme {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(ProExpenseTheme.colors.surface)
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    val labels = listOf("Welcome", "Quick Log", "Shared Costs", "Event Budget", "Journal")
                    onboardingIllustrations.forEachIndexed { i, illo ->
                        Text(labels[i], style = ProExpenseTheme.typography.eyebrow, color = ProExpenseTheme.colors.ink3)
                        illo(Modifier.size(width = 192.dp, height = 160.dp))
                    }
                }
            }
        }
        composeTestRule.onRoot().captureRoboImage()
    }
}
