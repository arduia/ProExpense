package com.arduia.expense.ui.events

import androidx.activity.ComponentActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onRoot
import com.arduia.expense.feature.eventbudget.R
import com.arduia.expense.feature.eventbudget.ui.EventBudgetListScreen
import com.arduia.expense.feature.eventbudget.ui.EventCreateSheetContent
import com.arduia.expense.feature.eventbudget.ui.EventDetailScreen
import com.arduia.expense.testing.ScreenshotTests
import com.arduia.expense.ui.design.HomeNavTab
import com.arduia.expense.ui.design.ProBottomSheetHost
import com.arduia.expense.feature.eventbudget.ui.preview.previewEventCreateErrors
import com.arduia.expense.feature.eventbudget.ui.preview.previewEventCreateValid
import com.arduia.expense.feature.eventbudget.ui.preview.previewEventDetail
import com.arduia.expense.feature.eventbudget.ui.preview.previewEventDetailClosed
import com.arduia.expense.feature.eventbudget.ui.preview.previewEventDetailWarn
import com.arduia.expense.feature.eventbudget.ui.preview.previewEventList
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
class EventBudgetScreenshotTest {

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
    fun event_empty() = capture {
        EventBudgetListScreen(
            events = emptyList(),
            onCreateEvent = {},
            onEventClick = {},
            selectedTab = HomeNavTab.Budget,
            onTabSelected = {},
            onAddClick = {},
        )
    }

    @Test
    fun event_list() = capture {
        EventBudgetListScreen(
            events = previewEventList,
            onCreateEvent = {},
            onEventClick = {},
            selectedTab = HomeNavTab.Budget,
            onTabSelected = {},
            onAddClick = {},
        )
    }

    @Test
    fun event_create() = capture {
        Box(Modifier.fillMaxSize()) {
            EventBudgetListScreen(
                events = previewEventList,
                onCreateEvent = {},
                onEventClick = {},
                selectedTab = HomeNavTab.Budget,
                onTabSelected = {},
                onAddClick = {},
            )
            ProBottomSheetHost(
                visible = true,
                title = stringResource(R.string.event_create_title),
                onClose = {},
            ) {
                EventCreateSheetContent(
                    form = previewEventCreateValid,
                    onNameChange = {},
                    onBudgetChange = {},
                    onPickStart = {},
                    onPickEnd = {},
                    onSave = {},
                )
            }
        }
    }

    @Test
    fun edge_event_errors() = capture {
        Box(Modifier.fillMaxSize()) {
            EventBudgetListScreen(
                events = previewEventList,
                onCreateEvent = {},
                onEventClick = {},
                selectedTab = HomeNavTab.Budget,
                onTabSelected = {},
                onAddClick = {},
            )
            ProBottomSheetHost(
                visible = true,
                title = stringResource(R.string.event_create_title),
                onClose = {},
            ) {
                EventCreateSheetContent(
                    form = previewEventCreateErrors,
                    onNameChange = {},
                    onBudgetChange = {},
                    onPickStart = {},
                    onPickEnd = {},
                    onSave = {},
                )
            }
        }
    }

    @Test
    fun event_detail() = capture {
        EventDetailScreen(
            state = previewEventDetail,
            onBack = {},
            onMore = {},
            onAddTagged = {},
            onExpenseClick = {},
        )
    }

    @Test
    fun edge_event_warn() = capture {
        EventDetailScreen(
            state = previewEventDetailWarn,
            onBack = {},
            onMore = {},
            onAddTagged = {},
            onExpenseClick = {},
        )
    }

    @Test
    fun edge_event_closed() = capture {
        EventDetailScreen(
            state = previewEventDetailClosed,
            onBack = {},
            onMore = {},
            onAddTagged = {},
            onExpenseClick = {},
        )
    }
}
