package com.arduia.expense.ui.more

import androidx.activity.ComponentActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onRoot
import com.arduia.expense.domain.CurrencyCode
import com.arduia.expense.feature.currency.ui.MoreCurrencyScreen
import com.arduia.expense.feature.importexport.ui.MoreClearScreen
import com.arduia.expense.feature.importexport.ui.MoreExportScreen
import com.arduia.expense.testing.ScreenshotTests
import com.arduia.expense.ui.design.HomeNavTab
import com.arduia.expense.feature.importexport.ui.preview.previewMoreClearOptions
import com.arduia.expense.feature.currency.ui.preview.previewMoreCurrencies
import com.arduia.expense.feature.importexport.ui.preview.previewMoreExportFiles
import com.arduia.expense.ui.preview.previewMoreHub
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
class MoreScreenshotTest {

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
    fun more_hub() = capture {
        MoreHubScreen(
            state = previewMoreHub,
            onFeatureClick = {},
            onSettingClick = {},
            onSettingToggle = { _, _ -> },
            selectedTab = HomeNavTab.More,
            onTabSelected = {},
            onAddClick = {},
        )
    }

    @Test
    fun edge_more_hub_pin_on() = capture {
        MoreHubScreen(
            state = previewMoreHub.copy(
                settings = previewMoreHub.settings.map { setting ->
                    when (setting.id) {
                        "pin" -> setting.copy(value = "On")
                        "biometric" -> setting.copy(enabled = true, toggleOn = true)
                        else -> setting
                    }
                },
            ),
            onFeatureClick = {},
            onSettingClick = {},
            onSettingToggle = { _, _ -> },
            selectedTab = HomeNavTab.More,
            onTabSelected = {},
            onAddClick = {},
        )
    }

    @Test
    fun more_default_category() = capture {
        MoreDefaultCategoryScreen(
            selectedCategoryId = "food",
            onSelect = {},
            onBack = {},
        )
    }

    @Test
    fun more_currency() = capture {
        MoreCurrencyScreen(
            items = previewMoreCurrencies,
            selectedCode = "USD",
            onSelect = {},
            onBack = {},
        )
    }

    @Test
    fun more_export() = capture {
        MoreExportScreen(
            files = previewMoreExportFiles,
            onExport = {},
            onBack = {},
        )
    }

    @Test
    fun more_clear() = capture {
        MoreClearScreen(
            options = previewMoreClearOptions,
            checkedIds = setOf("expenses"),
            onToggle = {},
            onClear = {},
            onBack = {},
        )
    }

    @Test
    fun more_budget_empty() = capture {
        MoreBudgetScreen(
            currentAmount = null,
            homeCurrency = CurrencyCode("USD"),
            onSave = {},
            onBack = {},
        )
    }

    @Test
    fun more_budget_set() = capture {
        MoreBudgetScreen(
            currentAmount = "$2,500.00",
            homeCurrency = CurrencyCode("USD"),
            onSave = {},
            onBack = {},
        )
    }
}
