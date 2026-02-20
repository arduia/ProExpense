package com.arduia.expense.ui.about

import android.content.Context
import android.content.Intent
import androidx.compose.ui.test.junit4.createEmptyComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers.hasAction
import androidx.test.espresso.intent.matcher.IntentMatchers.hasData
import androidx.test.espresso.intent.matcher.IntentMatchers.hasExtra
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.arduia.expense.R
import com.arduia.expense.launchFragmentInHiltContainer
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.HiltTestApplication
import io.mockk.mockk
import io.mockk.verify
import org.hamcrest.Matchers.allOf
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
@Config(application = HiltTestApplication::class, sdk = [33], instrumentedPackages = ["androidx.loader.content"])
class AboutFragmentTest {

    @get:Rule(order = 0)
    var hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createEmptyComposeRule()

    private lateinit var mockNavController: NavController
    private lateinit var context: Context

    @Before
    fun init() {
        hiltRule.inject()
        Intents.init()
        context = ApplicationProvider.getApplicationContext()
        mockNavController = mockk(relaxed = true)
    }

    @After
    fun tearDown() {
        Intents.release()
    }

    private fun launchFragment() {
        launchFragmentInHiltContainer<AboutFragment> {
            Navigation.setViewNavController(requireView(), mockNavController)
        }
    }

    @Test
    fun `should navigate to WebFragment when Privacy Policy is clicked`() {
        // Given
        val privacyTitle = context.getString(R.string.privacy_policy)
        val privacyUrl = context.getString(R.string.policy_url)

        launchFragment()

        // When
        composeTestRule.onNodeWithText(privacyTitle)
            .performScrollTo()
            .performClick()

        // Then
        val expectedAction = AboutFragmentDirections.actionDestAboutToDestWeb(url = privacyUrl, title = privacyTitle)
        verify {
            mockNavController.navigate(expectedAction, any<androidx.navigation.NavOptions>())
        }
    }

    @Test
    fun `should navigate to WebFragment when Open Source is clicked`() {
        // Given
        val openSourceTitle = context.getString(R.string.open_source_lib)
        val openSourceUrl = context.getString(R.string.open_source_url)

        launchFragment()

        // When
        composeTestRule.onNodeWithText(openSourceTitle)
            .performScrollTo()
            .performClick()

        // Then
        val expectedAction = AboutFragmentDirections.actionDestAboutToDestWeb(url = openSourceUrl, title = openSourceTitle)
        verify {
            mockNavController.navigate(expectedAction, any<androidx.navigation.NavOptions>())
        }
    }

    @Test
    fun `should launch implicit ACTION_VIEW Intent when Contribute is clicked`() {
        // Given
        val contributeTitle = context.getString(R.string.contribute_app)
        val githubUrl = context.getString(R.string.github_link_url)

        launchFragment()

        // When
        composeTestRule.onNodeWithText(contributeTitle)
            .performScrollTo()
            .performClick()

        // Then
        Intents.intended(
            allOf(
                hasAction(Intent.ACTION_VIEW),
                hasData(android.net.Uri.parse(githubUrl))
            )
        )
    }

    @Test
    fun `should launch implicit ACTION_SENDTO Intent when Developer is clicked`() {
        // Given
        val developerTitle = context.getString(R.string.developer)
        val developerEmail = context.getString(R.string.developer_email)
        val appName = context.getString(R.string.app_name)

        launchFragment()

        // When
        composeTestRule.onNodeWithText(developerTitle)
            .performScrollTo()
            .performClick()

        // Then
        Intents.intended(
            allOf(
                hasAction(Intent.ACTION_SENDTO),
                hasData(android.net.Uri.parse("mailto:$developerEmail")),
                hasExtra(Intent.EXTRA_SUBJECT, appName)
            )
        )
    }

    @Test
    fun `should display app version dynamically fetched from PackageManager`() {
        // Given
        // The version name is loaded via packageManager.getPackageInfo. Usually "1.0" or similar via Roboelectric unless mocked.
        val pm = context.packageManager
        val info = pm.getPackageInfo(context.packageName, 0)
        val versionName = info.versionName ?: "N/A"

        // When
        launchFragment()

        // Then
        composeTestRule.onNodeWithText(versionName)
            .performScrollTo()
            .assertExists()
    }
}