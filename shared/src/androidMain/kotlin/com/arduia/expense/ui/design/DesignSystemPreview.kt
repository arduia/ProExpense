package com.arduia.expense.ui.design

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.arduia.expense.ui.theme.ProArtboard
import com.arduia.expense.ui.theme.ProExpenseTheme

@Composable
private fun DesignSystemSection(
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
        Text(
            text = title,
            style = typography.eyebrow,
            color = colors.muted,
        )
        content()
    }
}

@Composable
fun ProDesignSystemButtonsContent() {
    val dimens = ProExpenseTheme.dimensions
    Column(verticalArrangement = Arrangement.spacedBy(dimens.space12)) {
        Row(horizontalArrangement = Arrangement.spacedBy(dimens.space8)) {
            ProButton(text = "Primary", onClick = {}, variant = ProButtonVariant.Primary, size = ProButtonSize.Md)
            ProButton(text = "Deep", onClick = {}, variant = ProButtonVariant.PrimaryDeep, size = ProButtonSize.Md)
        }
        Row(horizontalArrangement = Arrangement.spacedBy(dimens.space8)) {
            ProButton(text = "Success", onClick = {}, variant = ProButtonVariant.Success, size = ProButtonSize.Md)
            ProButton(text = "Dark", onClick = {}, variant = ProButtonVariant.Dark, size = ProButtonSize.Md)
        }
        Row(horizontalArrangement = Arrangement.spacedBy(dimens.space8)) {
            ProButton(text = "Secondary", onClick = {}, variant = ProButtonVariant.Secondary, size = ProButtonSize.Md)
            ProButton(text = "Ghost", onClick = {}, variant = ProButtonVariant.Ghost, size = ProButtonSize.Md)
        }
        Row(horizontalArrangement = Arrangement.spacedBy(dimens.space8)) {
            ProButton(text = "Small", onClick = {}, size = ProButtonSize.Sm)
            ProButton(text = "Large", onClick = {}, size = ProButtonSize.Lg)
            ProButton(text = "Disabled", onClick = {}, enabled = false)
        }
    }
}

@Composable
fun ProDesignSystemCategoriesContent() {
    val dimens = ProExpenseTheme.dimensions
    Column(verticalArrangement = Arrangement.spacedBy(dimens.space16)) {
        Row(horizontalArrangement = Arrangement.spacedBy(dimens.space8)) {
            LogCategoryBadge(categoryId = "food")
            LogCategoryBadge(categoryId = "transport")
            LogCategoryBadge(categoryId = "shopping")
            LogCategoryBadge(categoryId = "health")
        }
        Row(
            modifier = Modifier.horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(dimens.space8),
        ) {
            CategoryChip(label = "Food", categoryId = "food", selected = true, onClick = {})
            CategoryChip(label = "Transport", categoryId = "transport", selected = false, onClick = {})
            CategoryChip(label = "Coffee runs", categoryId = "coffee", selected = false, onClick = {})
        }
    }
}

@Composable
fun ProDesignSystemFieldsContent() {
    val dimens = ProExpenseTheme.dimensions
    Column(verticalArrangement = Arrangement.spacedBy(dimens.space16)) {
        SearchField(value = "", onValueChange = {}, placeholder = "Search transactions")
        Row(horizontalArrangement = Arrangement.spacedBy(dimens.space8)) {
            FilterChip(label = "All", selected = true, onClick = {})
            FilterChip(label = "Food", selected = false, onClick = {})
            FilterChip(label = "Transport", selected = false, onClick = {})
            FilterChip(label = "More", selected = false, onClick = {})
        }
    }
}

@Composable
fun ProDesignSystemListsContent() {
    val dimens = ProExpenseTheme.dimensions
    Column(verticalArrangement = Arrangement.spacedBy(dimens.space8)) {
        DayHeader(title = "Today · May 25", total = "$80.90")
        TransactionRow(
            categoryId = "food",
            note = "Lunch with M.",
            meta = "Food · 12:30 PM",
            amount = "$12.40",
        )
        TransactionRow(
            categoryId = "entertainment",
            note = "Movie · Dune",
            meta = "Entertainment · 08:10 PM",
            amount = "$18.00",
            tag = "Bali Trip",
        )
    }
}

@Composable
fun ProDesignSystemChromeContent() {
    val colors = ProExpenseTheme.colors
    val dimens = ProExpenseTheme.dimensions
    Column(verticalArrangement = Arrangement.spacedBy(dimens.space16)) {
        ProTopBar(title = "Journal", onBack = {}, action = ProTopBarAction.More, onAction = {})
        Row(horizontalArrangement = Arrangement.spacedBy(dimens.space8)) {
            QuickAccessTile(
                label = "Reports",
                icon = ProIconGlyph.FeatReports,
                tint = colors.primaryTint,
                accent = colors.primaryDeep,
                onClick = {},
            )
            QuickAccessTile(
                label = "Debt",
                icon = ProIconGlyph.FeatDebt,
                tint = colors.successTint,
                accent = colors.success,
                onClick = {},
            )
            QuickAccessTile(
                label = "Split",
                icon = ProIconGlyph.FeatSplit,
                tint = colors.tagTint,
                accent = colors.tagDeep,
                onClick = {},
            )
            QuickAccessTile(
                label = "Events",
                icon = ProIconGlyph.FeatEvents,
                tint = colors.highlightSoft,
                accent = colors.highlightDeep,
                onClick = {},
            )
        }
        ProToast(message = "Expense saved")
    }
}

@Composable
fun ProDesignSystemComponents() {
    val colors = ProExpenseTheme.colors
    val dimens = ProExpenseTheme.dimensions

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.paper)
            .verticalScroll(rememberScrollState())
            .padding(dimens.space18),
        verticalArrangement = Arrangement.spacedBy(dimens.space24),
    ) {
        DesignSystemSection(title = "BUTTONS") { ProDesignSystemButtonsContent() }
        DesignSystemSection(title = "CATEGORIES") { ProDesignSystemCategoriesContent() }
        DesignSystemSection(title = "FIELDS") { ProDesignSystemFieldsContent() }
        DesignSystemSection(title = "LISTS") { ProDesignSystemListsContent() }
        DesignSystemSection(title = "CHROME") { ProDesignSystemChromeContent() }
    }
}

@Preview(name = "Design system — buttons", widthDp = ProArtboard.PIXEL_9_PRO_WIDTH_DP, heightDp = 480, showBackground = true)
@Composable
fun ProDesignSystemButtonsPreview() {
    ProExpenseTheme {
        Column(
            Modifier
                .fillMaxSize()
                .background(ProExpenseTheme.colors.paper)
                .padding(ProExpenseTheme.dimensions.space18),
        ) {
            ProDesignSystemButtonsContent()
        }
    }
}

@Preview(name = "Design system — categories", widthDp = ProArtboard.PIXEL_9_PRO_WIDTH_DP, heightDp = 360, showBackground = true)
@Composable
fun ProDesignSystemCategoriesPreview() {
    ProExpenseTheme {
        Column(
            Modifier
                .fillMaxSize()
                .background(ProExpenseTheme.colors.paper)
                .padding(ProExpenseTheme.dimensions.space18),
        ) {
            ProDesignSystemCategoriesContent()
        }
    }
}

@Preview(name = "Design system — fields", widthDp = ProArtboard.PIXEL_9_PRO_WIDTH_DP, heightDp = 360, showBackground = true)
@Composable
fun ProDesignSystemFieldsPreview() {
    ProExpenseTheme {
        Column(
            Modifier
                .fillMaxSize()
                .background(ProExpenseTheme.colors.paper)
                .padding(ProExpenseTheme.dimensions.space18),
        ) {
            ProDesignSystemFieldsContent()
        }
    }
}

@Preview(name = "Design system — lists", widthDp = ProArtboard.PIXEL_9_PRO_WIDTH_DP, heightDp = 480, showBackground = true)
@Composable
fun ProDesignSystemListsPreview() {
    ProExpenseTheme {
        Column(
            Modifier
                .fillMaxSize()
                .background(ProExpenseTheme.colors.paper)
                .padding(ProExpenseTheme.dimensions.space18),
        ) {
            ProDesignSystemListsContent()
        }
    }
}

@Preview(name = "Design system — chrome", widthDp = ProArtboard.PIXEL_9_PRO_WIDTH_DP, heightDp = 520, showBackground = true)
@Composable
fun ProDesignSystemChromePreview() {
    ProExpenseTheme {
        Column(
            Modifier
                .fillMaxSize()
                .background(ProExpenseTheme.colors.paper)
                .padding(ProExpenseTheme.dimensions.space18),
        ) {
            ProDesignSystemChromeContent()
        }
    }
}

@Preview(
    name = "Design system — full gallery",
    widthDp = ProArtboard.PIXEL_9_PRO_WIDTH_DP,
    heightDp = ProArtboard.PIXEL_9_PRO_HEIGHT_DP,
    showBackground = true,
)
@Composable
fun ProDesignSystemGalleryPreview() {
    ProExpenseTheme { ProDesignSystemComponents() }
}
