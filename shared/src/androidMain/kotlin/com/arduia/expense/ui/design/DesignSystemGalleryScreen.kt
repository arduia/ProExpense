package com.arduia.expense.ui.design

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.arduia.expense.ui.theme.ProArtboard
import com.arduia.expense.ui.theme.ProExpenseTheme

@Composable
fun DesignSystemGalleryScreen(modifier: Modifier = Modifier) {
    val colors = ProExpenseTheme.colors
    val dimens = ProExpenseTheme.dimensions
    val typography = ProExpenseTheme.typography

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(colors.paper)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = dimens.space18, vertical = dimens.space24),
        verticalArrangement = Arrangement.spacedBy(dimens.space24),
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(dimens.space8)) {
            Text(
                text = "DESIGN SYSTEM",
                style = typography.eyebrow,
                color = colors.muted,
            )
            Text(
                text = "Component gallery",
                style = typography.heroGreeting,
                color = colors.onSurface,
            )
            Text(
                text = "Live renders from design-system-spec — scroll to inspect each component.",
                style = typography.body,
                color = colors.onSurfaceVariant,
            )
        }

        GallerySection(number = "00", title = "Typography") {
            GalleryStage("App bar title") {
                ProTopBar(title = "New expense", onBack = {}, backLabel = "Back")
            }
            GalleryStage("Hero greeting") {
                HeroGreeting(name = "Maya")
            }
            GalleryStage("Sheet title") {
                Text(
                    text = "Edit expense",
                    style = typography.sheetTitle,
                    color = colors.onSurface,
                )
            }
            GalleryStage("Section / day head") {
                Text(
                    text = "Today · May 25",
                    style = typography.sectionHead,
                    color = colors.onSurface,
                )
            }
        }

        GallerySection(number = "01", title = "Button") {
            GalleryStage("Variants") { SpecButtonVariantsCapture() }
            GalleryStage("Sizes") { SpecButtonSizesCapture() }
            GalleryStage("States") { SpecButtonStatesCapture() }
        }

        GallerySection(number = "02", title = "Category") {
            GalleryStage("Badges") { SpecCategoryBadgesCapture() }
            GalleryStage("Chips") { SpecCategoryChipsCapture() }
        }

        GallerySection(number = "03", title = "Filter chip") {
            GalleryStage("Idle / active") { SpecFilterChipsCapture() }
        }

        GallerySection(number = "04", title = "Transaction row") {
            GalleryStage("Day group") { SpecTransactionDayGroupCapture() }
        }

        GallerySection(number = "05", title = "Card & surfaces") {
            GalleryStage("Spent today") { SpecSpentCardCapture() }
        }

        GallerySection(number = "06", title = "Amount entry") {
            GalleryStage("Keypad") { SpecKeypadCapture() }
            GalleryStage("Validation") { SpecValidationCapture() }
        }

        GallerySection(number = "07", title = "Search field") {
            GalleryStage("Empty") { SpecSearchEmptyCapture() }
            GalleryStage("Filled") { SpecSearchFilledCapture() }
        }

        GallerySection(number = "08", title = "Quick access") {
            GalleryStage("Tiles") { SpecQuickAccessCapture() }
        }

        GallerySection(number = "09", title = "Bottom navigation") {
            GalleryStage("Home active") { SpecBottomNavHomeCapture() }
            GalleryStage("Budget active") { SpecBottomNavBudgetCapture() }
        }

        GallerySection(number = "10", title = "Bottom sheet") {
            GalleryStage("Sheet") { SpecBottomSheetCapture() }
        }

        GallerySection(number = "11", title = "Toast") {
            GalleryStage("Success") { SpecToastCapture() }
        }

        GallerySection(number = "12", title = "Iconography") {
            GalleryStage("Set") { SpecIconsCapture() }
        }

        GallerySection(number = "—", title = "Interactive samples") {
            GalleryStage("Logging") { ProDesignSystemLoggingContent() }
            GalleryStage("Auth") { ProDesignSystemAuthContent() }
            GalleryStage("Sheet") { ProDesignSystemSheetContent() }
        }
    }
}

@Composable
private fun GallerySection(
    number: String,
    title: String,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    val colors = ProExpenseTheme.colors
    val dimens = ProExpenseTheme.dimensions
    val typography = ProExpenseTheme.typography

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(dimens.space12),
    ) {
        HorizontalDivider(color = colors.lineSoft)
        Column(verticalArrangement = Arrangement.spacedBy(dimens.space4)) {
            Text(
                text = number,
                style = typography.tabTimestamp,
                color = colors.primaryDeep,
            )
            Text(
                text = title,
                style = typography.sectionHead,
                color = colors.onSurface,
            )
        }
        content()
    }
}

@Composable
private fun GalleryStage(
    label: String,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    val colors = ProExpenseTheme.colors
    val dimens = ProExpenseTheme.dimensions
    val typography = ProExpenseTheme.typography

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(dimens.space8),
    ) {
        Text(
            text = label.uppercase(),
            style = typography.eyebrow,
            color = colors.muted,
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(colors.surface, ProExpenseTheme.shapes.card)
                .padding(dimens.space16),
            contentAlignment = Alignment.Center,
        ) {
            content()
        }
    }
}

@Preview(
    name = "Design system gallery screen",
    widthDp = ProArtboard.PIXEL_9_PRO_WIDTH_DP,
    heightDp = ProArtboard.PIXEL_9_PRO_HEIGHT_DP,
    showBackground = true,
)
@Composable
private fun DesignSystemGalleryScreenPreview() {
    ProExpenseTheme {
        DesignSystemGalleryScreen()
    }
}
