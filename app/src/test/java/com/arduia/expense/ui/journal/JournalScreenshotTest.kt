package com.arduia.expense.ui.journal

import androidx.activity.ComponentActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onRoot
import com.arduia.expense.feature.history.ui.JournalActionsSheetContent
import com.arduia.expense.feature.history.ui.JournalDateRangeSheet
import com.arduia.expense.feature.history.ui.JournalDetailScreen
import com.arduia.expense.feature.history.ui.JournalListScreen
import com.arduia.expense.feature.history.ui.JournalQuickNoteSheetContent
import com.arduia.expense.feature.history.R
import com.arduia.expense.feature.history.ui.preview.previewJournalDetail
import com.arduia.expense.feature.history.ui.preview.previewJournalList
import com.arduia.expense.feature.history.ui.preview.previewJournalQuickNote
import com.arduia.expense.feature.history.ui.preview.previewJournalSearchEmpty
import com.arduia.expense.testing.ScreenshotTests
import com.arduia.expense.ui.design.HomeNavTab
import com.arduia.expense.ui.design.ProBottomSheetHost
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
class JournalScreenshotTest {

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
    fun journal_list() = capture {
        JournalListScreen(
            state = previewJournalList,
            onQueryChange = {},
            onFilterSelected = {},
            onRowClick = {},
            onRowLongPress = {},
            selectedTab = HomeNavTab.Journal,
            onTabSelected = {},
            onAddClick = {},
        )
    }

    @Test
    fun journal_list_date_range_active() = capture {
        JournalListScreen(
            state = previewJournalList,
            onQueryChange = {},
            onFilterSelected = {},
            onRowClick = {},
            onRowLongPress = {},
            selectedTab = HomeNavTab.Journal,
            onTabSelected = {},
            onAddClick = {},
            dateRangeLabel = "May 1 – May 15",
        )
    }

    @Test
    fun edge_search() = capture {
        JournalListScreen(
            state = previewJournalSearchEmpty,
            onQueryChange = {},
            onFilterSelected = {},
            onRowClick = {},
            onRowLongPress = {},
            selectedTab = HomeNavTab.Journal,
            onTabSelected = {},
            onAddClick = {},
        )
    }

    @Test
    fun edge_search_with_date_range_active() = capture {
        // The date-range chip must stay visible during search — a "no matches" result while an
        // unseen date filter is still narrowing results would otherwise be misleading.
        JournalListScreen(
            state = previewJournalSearchEmpty,
            onQueryChange = {},
            onFilterSelected = {},
            onRowClick = {},
            onRowLongPress = {},
            selectedTab = HomeNavTab.Journal,
            onTabSelected = {},
            onAddClick = {},
            dateRangeLabel = "May 1 – May 15",
        )
    }

    @Test
    fun journal_detail() = capture {
        JournalDetailScreen(
            state = previewJournalDetail,
            onBack = {},
            onActions = {},
            onLinkedTagClick = {},
            onEdit = {},
            onDelete = {},
        )
    }

    @Test
    fun edge_quicknote() = capture {
        Box(Modifier.fillMaxSize()) {
            JournalListScreen(
                state = previewJournalList,
                onQueryChange = {},
                onFilterSelected = {},
                onRowClick = {},
                onRowLongPress = {},
                selectedTab = HomeNavTab.Journal,
                onTabSelected = {},
                onAddClick = {},
            )
            ProBottomSheetHost(
                visible = true,
                title = stringResource(R.string.journal_quick_note_title),
                onClose = {},
            ) {
                JournalQuickNoteSheetContent(
                    state = previewJournalQuickNote,
                    onNoteChange = {},
                    onSave = {},
                )
            }
        }
    }

    @Test
    fun edge_date_range_sheet() = capture {
        Box(Modifier.fillMaxSize()) {
            JournalListScreen(
                state = previewJournalList,
                onQueryChange = {},
                onFilterSelected = {},
                onRowClick = {},
                onRowLongPress = {},
                selectedTab = HomeNavTab.Journal,
                onTabSelected = {},
                onAddClick = {},
            )
            JournalDateRangeSheet(
                visible = true,
                initialStartEpochMillis = 1_716_600_000_000L,
                initialEndEpochMillis = 1_717_200_000_000L,
                onConfirm = { _, _ -> },
                onClear = {},
                onDismiss = {},
            )
        }
    }

    @Test
    fun journal_actions() = capture {
        Box(Modifier.fillMaxSize()) {
            JournalDetailScreen(
                state = previewJournalDetail,
                onBack = {},
                onActions = {},
                onLinkedTagClick = {},
                onEdit = {},
                onDelete = {},
            )
            ProBottomSheetHost(
                visible = true,
                title = null,
                onClose = {},
            ) {
                JournalActionsSheetContent(
                    onEdit = {},
                    onDelete = {},
                    onCancel = {},
                )
            }
        }
    }
}
