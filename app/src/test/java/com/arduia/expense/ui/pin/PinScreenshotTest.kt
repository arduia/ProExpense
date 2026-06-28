package com.arduia.expense.ui.pin

import androidx.activity.ComponentActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performClick
import com.arduia.expense.feature.auth.ui.PinEntryScreen
import com.arduia.expense.feature.auth.ui.PinRecoveryScreen
import com.arduia.expense.feature.auth.ui.PinSecurityQuestionScreen
import com.arduia.expense.feature.auth.ui.PinSetPinScreen
import com.arduia.expense.feature.auth.ui.PinSetupScreen
import com.arduia.expense.feature.auth.R
import com.arduia.expense.feature.auth.ui.preview.pinSecurityQuestions
import com.arduia.expense.feature.auth.ui.preview.previewPinEntry
import com.arduia.expense.feature.auth.ui.preview.previewPinLock
import com.arduia.expense.feature.auth.ui.preview.previewPinSetConfirmMismatch
import com.arduia.expense.feature.auth.ui.preview.previewPinSetRevealed
import com.arduia.expense.feature.auth.ui.preview.previewPinSetup
import com.arduia.expense.feature.auth.ui.preview.previewPinWrong
import com.arduia.expense.testing.ScreenshotTests
import com.arduia.expense.ui.theme.ProArtboard
import com.arduia.expense.ui.theme.ProExpenseTheme
import com.github.takahirom.roborazzi.captureRoboImage
import org.junit.Rule
import org.junit.Test
import org.junit.experimental.categories.Category
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(
    sdk = [33],
    qualifiers = "w${ProArtboard.PIXEL_9_PRO_WIDTH_DP}dp-h${ProArtboard.PIXEL_9_PRO_HEIGHT_DP}dp",
)
@Category(ScreenshotTests::class)
class PinScreenshotTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    private fun capture(content: @Composable () -> Unit) {
        composeTestRule.setContent {
            ProExpenseTheme {
                Box(
                    Modifier
                        .fillMaxSize()
                        .background(ProExpenseTheme.colors.paper),
                ) {
                    content()
                }
            }
        }
        composeTestRule.onRoot().captureRoboImage()
    }

    @Test
    fun pin_entry() = capture {
        PinEntryScreen(previewPinEntry, {}, {}, {}, {})
    }

    @Test
    fun edge_pin_wrong() = capture {
        PinEntryScreen(previewPinWrong, {}, {}, {}, {})
    }

    @Test
    fun pin_lock() = capture {
        PinEntryScreen(previewPinLock, {}, {}, {}, {})
    }

    @Test
    fun pin_setup() = capture {
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

    @Test
    fun edge_pin_mismatch() = capture {
        PinSetPinScreen(
            state = previewPinSetConfirmMismatch,
            headingRes = R.string.pin_confirm_heading,
            onDigit = {},
            onBackspace = {},
            onBack = {},
        )
    }

    @Test
    fun pin_set_revealed() {
        composeTestRule.setContent {
            ProExpenseTheme {
                Box(
                    Modifier
                        .fillMaxSize()
                        .background(ProExpenseTheme.colors.paper),
                ) {
                    PinSetPinScreen(
                        state = previewPinSetRevealed,
                        headingRes = R.string.pin_set_new_heading,
                        onDigit = {},
                        onBackspace = {},
                        onBack = {},
                    )
                }
            }
        }
        composeTestRule.onNodeWithContentDescription("Toggle PIN reveal").performClick()
        composeTestRule.onRoot().captureRoboImage()
    }

    @Test
    fun pin_security() = capture {
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

    @Test
    fun edge_pin_recovery() = capture {
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
