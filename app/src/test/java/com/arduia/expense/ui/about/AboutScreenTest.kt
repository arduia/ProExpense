package com.arduia.expense.ui.about

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import com.arduia.expense.ui.about.molecule.AboutEvent
import com.arduia.expense.ui.about.molecule.AboutState
import com.arduia.expense.utils.BaseComposeTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import androidx.test.core.app.ApplicationProvider
import android.content.Context
import com.arduia.expense.R

class AboutScreenTest : BaseComposeTest() {

    @Test
    fun `should display app name and version correctly`() {
        val testVersionName = "1.0.5"
        
        composeTestRule.setContent {
            AboutScreen(
                state = AboutState(),
                onEvent = {},
                versionName = testVersionName
            )
        }

        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        val betaStr = context.getString(R.string.beta_with_parenthness)
        val privacyStr = context.getString(R.string.privacy_policy)
        val openSourceStr = context.getString(R.string.open_source_lib)
        val contributeStr = context.getString(R.string.contribute_app)

        composeTestRule.onNodeWithText(appName).assertIsDisplayed()
        composeTestRule.onNodeWithText(betaStr).assertIsDisplayed()
        composeTestRule.onNodeWithText(testVersionName).assertIsDisplayed()
        composeTestRule.onNodeWithText(privacyStr).performScrollTo().assertIsDisplayed()
        composeTestRule.onNodeWithText(openSourceStr).performScrollTo().assertIsDisplayed()
        composeTestRule.onNodeWithText(contributeStr).performScrollTo().assertIsDisplayed()
    }

    @Test
    fun `should not show new version row if isNewVersionAvailable is false`() {
        composeTestRule.setContent {
            AboutScreen(
                state = AboutState(isNewVersionAvailable = false),
                onEvent = {}
            )
        }

        val context = ApplicationProvider.getApplicationContext<Context>()
        val newVersionStr = context.getString(R.string.new_version_available)
        composeTestRule.onNodeWithText(newVersionStr).assertDoesNotExist()
    }

    @Test
    fun `should show new version row and emit event if clicked`() {
        var eventEmitted: AboutEvent? = null

        composeTestRule.setContent {
            AboutScreen(
                state = AboutState(isNewVersionAvailable = true),
                onEvent = { eventEmitted = it }
            )
        }

        val context = ApplicationProvider.getApplicationContext<Context>()
        val newVersionStr = context.getString(R.string.new_version_available)

        composeTestRule.onNodeWithText(newVersionStr)
            .assertIsDisplayed()
            .performClick()

        assertEquals(AboutEvent.OpenUpdateInfo, eventEmitted)
    }

    @Test
    fun `should trigger callbacks for list items`() {
        var privacyClicked = false
        var openSourceClicked = false
        var contributeClicked = false
        var developerClicked = false

        composeTestRule.setContent {
            AboutScreen(
                state = AboutState(),
                onEvent = {},
                onPrivacyClick = { privacyClicked = true },
                onOpenSourceClick = { openSourceClicked = true },
                onContributeClick = { contributeClicked = true },
                onDeveloperClick = { developerClicked = true }
            )
        }

        val context = ApplicationProvider.getApplicationContext<Context>()
        
        composeTestRule.onNodeWithText(context.getString(R.string.privacy_policy))
            .performScrollTo()
            .performClick()
        assertTrue(privacyClicked)

        composeTestRule.onNodeWithText(context.getString(R.string.open_source_lib))
            .performScrollTo()
            .performClick()
        assertTrue(openSourceClicked)

        composeTestRule.onNodeWithText(context.getString(R.string.contribute_app))
            .performScrollTo()
            .performClick()
        assertTrue(contributeClicked)

        composeTestRule.onNodeWithText(context.getString(R.string.developer))
            .performScrollTo()
            .performClick()
        assertTrue(developerClicked)
    }

    @Test
    fun `should show update dialog when state has updateInfo`() {
        val testUpdateInfo = AboutUpdateUiModel(
            versionName = "v2.0",
            versionCode = "20",
            changeLogs = "Bug fixes and improvements"
        )
        
        var dismissedEvent: AboutEvent? = null
        var installClicked = false

        composeTestRule.setContent {
            AboutScreen(
                state = AboutState(updateInfo = testUpdateInfo),
                onEvent = { dismissedEvent = it },
                onInstallUpdateClick = { installClicked = true }
            )
        }

        // Assert Dialog Content
        composeTestRule.onNodeWithText("v2.0").assertIsDisplayed()
        composeTestRule.onNodeWithText("20").assertIsDisplayed()
        composeTestRule.onNodeWithText("Bug fixes and improvements").assertIsDisplayed()

        val context = ApplicationProvider.getApplicationContext<Context>()
        val installStr = context.getString(R.string.install)

        // Assert buttons
        composeTestRule.onNodeWithText(installStr).assertIsDisplayed().performClick()
        assertTrue(installClicked)
        assertEquals(AboutEvent.ClearUpdateInfo, dismissedEvent)
    }

    @Test
    fun `should dismiss update dialog when cancel is clicked`() {
        val testUpdateInfo = AboutUpdateUiModel(
            versionName = "v2.0",
            versionCode = "20",
            changeLogs = "Bug fixes and improvements"
        )
        
        var dismissedEvent: AboutEvent? = null

        composeTestRule.setContent {
            AboutScreen(
                state = AboutState(updateInfo = testUpdateInfo),
                onEvent = { dismissedEvent = it }
            )
        }

        val context = ApplicationProvider.getApplicationContext<Context>()
        val cancelStr = context.getString(R.string.cancel)

        composeTestRule.onNodeWithText(cancelStr).assertIsDisplayed().performClick()
        assertEquals(AboutEvent.ClearUpdateInfo, dismissedEvent)
    }
}
