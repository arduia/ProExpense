package com.arduia.expense.ui.design

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import com.arduia.expense.ui.theme.ProArtboard
import com.arduia.expense.ui.theme.ProExpenseTheme

private val categoryIds = listOf(
    "food", "transport", "shopping", "bills", "health", "entertainment", "coffee", "pet",
)

@Composable
fun SpecCaptureHost(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    val colors = ProExpenseTheme.colors
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(colors.paper)
            .padding(28.dp),
        contentAlignment = Alignment.Center,
    ) {
        content()
    }
}

@Composable
fun SpecButtonVariantsCapture() {
    Row(horizontalArrangement = Arrangement.spacedBy(16.dp), verticalAlignment = Alignment.CenterVertically) {
        ProButton(text = "Primary", onClick = {}, variant = ProButtonVariant.Primary)
        ProButton(text = "Primary deep", onClick = {}, variant = ProButtonVariant.PrimaryDeep)
        ProButton(text = "Confirm", onClick = {}, variant = ProButtonVariant.Success)
        ProButton(text = "Dark", onClick = {}, variant = ProButtonVariant.Dark)
        ProButton(text = "Secondary", onClick = {}, variant = ProButtonVariant.Secondary)
        ProButton(text = "Ghost", onClick = {}, variant = ProButtonVariant.Ghost)
    }
}

@Composable
fun SpecButtonSizesCapture() {
    Row(horizontalArrangement = Arrangement.spacedBy(14.dp), verticalAlignment = Alignment.CenterVertically) {
        ProButton(text = "Small", onClick = {}, size = ProButtonSize.Sm)
        ProButton(text = "Medium", onClick = {}, size = ProButtonSize.Md)
        ProButton(text = "Large", onClick = {}, size = ProButtonSize.Lg)
    }
}

@Composable
fun SpecButtonStatesCapture() {
    Row(horizontalArrangement = Arrangement.spacedBy(18.dp), verticalAlignment = Alignment.CenterVertically) {
        ProButton(text = "Enabled", onClick = {})
        ProButton(text = "Disabled", onClick = {}, enabled = false)
    }
}

@Composable
fun SpecCategoryBadgesCapture() {
    Row(
        modifier = Modifier.width(520.dp),
        horizontalArrangement = Arrangement.spacedBy(26.dp, Alignment.CenterHorizontally),
    ) {
        categoryIds.forEach { id ->
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.width(78.dp),
            ) {
                LogCategoryBadge(categoryId = id, size = 48.dp)
                Text(
                    text = categoryLabel(id),
                    style = ProExpenseTheme.typography.caption,
                    color = ProExpenseTheme.colors.onSurfaceVariant,
                )
            }
        }
    }
}

@Composable
fun SpecCategoryChipsCapture() {
    Row(
        modifier = Modifier.width(460.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterHorizontally),
    ) {
        categoryIds.forEachIndexed { index, id ->
            CategoryChip(
                label = categoryLabel(id),
                categoryId = id,
                selected = index % 3 == 0,
                onClick = {},
            )
        }
    }
}

@Composable
fun SpecFilterChipsCapture() {
    Row(
        modifier = Modifier.width(360.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp, Alignment.CenterHorizontally),
    ) {
        FilterChip(label = "All", selected = true, onClick = {})
        FilterChip(label = "Food", selected = false, onClick = {})
        FilterChip(label = "Transport", selected = false, onClick = {})
        FilterChip(label = "Bills", selected = false, onClick = {})
        FilterChip(label = "More", selected = false, onClick = {})
    }
}

@Composable
fun SpecTransactionDayGroupCapture() {
    val colors = ProExpenseTheme.colors
    val dimens = ProExpenseTheme.dimensions
    val shape = ProExpenseTheme.shapes.card
    val cardElevation = ProExpenseTheme.elevation.card.firstOrNull()
    Column(
        modifier = Modifier
            .width(360.dp)
            .then(
                if (cardElevation != null) {
                    Modifier.shadow(
                        elevation = cardElevation.blur,
                        shape = shape,
                        spotColor = cardElevation.color,
                        ambientColor = cardElevation.color,
                    )
                } else {
                    Modifier
                },
            )
            .background(colors.surface, shape)
            .padding(horizontal = 14.dp, vertical = 6.dp),
    ) {
        DayHeader(title = "Today · May 25", total = "$42")
        TransactionRow(
            categoryId = "food",
            note = "Lunch with M.",
            meta = "Food · 12:30 PM",
            amount = "$12.40",
        )
        TransactionRow(
            categoryId = "coffee",
            note = "Oat latte",
            meta = "Coffee runs · 08:40 AM",
            amount = "$5.00",
        )
        TransactionRow(
            categoryId = "entertainment",
            note = "Movie · Dune",
            meta = "Entertainment",
            amount = "$18.00",
            tag = "Bali Trip",
            showDivider = false,
        )
    }
}

@Composable
fun SpecSpentCardCapture() {
    SpentTodayCard(
        eyebrow = "Spent today",
        amount = "$42.00",
        modifier = Modifier.width(320.dp),
    )
}

@Composable
fun SpecKeypadCapture() {
    NumericKeypad(
        actionsEnabled = true,
        onKey = {},
        onBackspace = {},
        onSave = {},
        onNext = {},
        modifier = Modifier.width(300.dp),
    )
}

@Composable
fun SpecSearchEmptyCapture() {
    SearchField(
        value = "",
        onValueChange = {},
        placeholder = "Search notes, amount, category…",
        modifier = Modifier.width(320.dp),
    )
}

@Composable
fun SpecSearchFilledCapture() {
    SearchField(
        value = "Lunch",
        onValueChange = {},
        placeholder = "Search notes, amount, category…",
        modifier = Modifier.width(320.dp),
    )
}

@Composable
fun SpecQuickAccessCapture() {
    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        SpecQuickAccessTile(
            label = "Reports",
            icon = ProIconGlyph.FeatReports,
            iconTint = ProExpenseTheme.colors.primaryDeep,
            iconBackground = ProExpenseTheme.colors.primaryTint,
        )
        SpecQuickAccessTile(
            label = "Debts",
            icon = ProIconGlyph.FeatDebt,
            iconTint = ProExpenseTheme.colors.success,
            iconBackground = ProExpenseTheme.colors.successTint,
        )
        SpecQuickAccessTile(
            label = "Split",
            icon = ProIconGlyph.FeatSplit,
            iconTint = ProExpenseTheme.colors.tagDeep,
            iconBackground = ProExpenseTheme.colors.tagTint,
        )
        SpecQuickAccessTile(
            label = "Events",
            icon = ProIconGlyph.FeatEvents,
            iconTint = ProExpenseTheme.colors.highlightDeep,
            iconBackground = ProExpenseTheme.colors.highlightSoft,
        )
    }
}

@Composable
private fun SpecQuickAccessTile(
    label: String,
    icon: ProIconGlyph,
    iconTint: Color,
    iconBackground: Color,
) {
    val colors = ProExpenseTheme.colors
    val dimens = ProExpenseTheme.dimensions
    val typography = ProExpenseTheme.typography
    Column(
        modifier = Modifier
            .width(78.dp)
            .background(colors.surface, ProExpenseTheme.shapes.tile)
            .padding(horizontal = 4.dp, vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(7.dp),
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .background(iconBackground, androidx.compose.foundation.shape.RoundedCornerShape(11.dp)),
            contentAlignment = Alignment.Center,
        ) {
            ProIcon(glyph = icon, contentDescription = null, tint = iconTint, size = 18.dp)
        }
        Text(text = label, style = typography.caption, color = colors.onSurfaceVariant, textAlign = TextAlign.Center)
    }
}

@Composable
fun SpecValidationCapture() {
    val colors = ProExpenseTheme.colors
    val typography = ProExpenseTheme.typography
    Column(
        modifier = Modifier
            .width(260.dp)
            .background(colors.surface, ProExpenseTheme.shapes.card)
            .padding(22.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Row(verticalAlignment = Alignment.Bottom) {
            Text(
                text = "$",
                style = typography.summaryAmount.copy(fontSize = 22.sp),
                color = colors.primary,
            )
            Text(
                text = "0",
                style = typography.summaryAmount.copy(fontSize = 44.sp, letterSpacing = (-0.025).em),
                color = colors.muted2,
            )
        }
        Text(
            text = "Amount must be greater than $0",
            style = typography.caption,
            color = colors.primary,
            modifier = Modifier.padding(top = 4.dp),
        )
    }
}

@Composable
fun SpecBottomNavHomeCapture() {
    Box(modifier = Modifier.width(390.dp).padding(top = 34.dp)) {
        HomeBottomNav(
            selectedTab = HomeNavTab.Home,
            onTabSelected = {},
            onAddClick = {},
        )
    }
}

@Composable
fun SpecBottomNavBudgetCapture() {
    Box(modifier = Modifier.width(390.dp).padding(top = 34.dp)) {
        HomeBottomNav(
            selectedTab = HomeNavTab.Budget,
            onTabSelected = {},
            onAddClick = {},
        )
    }
}

@Composable
fun SpecBottomSheetCapture() {
    val colors = ProExpenseTheme.colors
    Box(
        modifier = Modifier
            .width(360.dp)
            .height(200.dp)
            .background(colors.scrim),
    ) {
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .background(
                    colors.surface,
                    androidx.compose.foundation.shape.RoundedCornerShape(
                        topStart = 22.dp,
                        topEnd = 22.dp,
                    ),
                )
                .padding(horizontal = 20.dp, vertical = 0.dp)
                .padding(bottom = 20.dp),
        ) {
            Box(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(top = 10.dp, bottom = 14.dp)
                    .width(36.dp)
                    .height(4.dp)
                    .background(colors.scrim.copy(alpha = 0.18f), ProExpenseTheme.shapes.chip),
            )
            Text(
                text = "Edit expense",
                style = ProExpenseTheme.typography.sectionHead,
                color = colors.onSurface,
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 14.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                ProButton(
                    text = "Cancel",
                    onClick = {},
                    variant = ProButtonVariant.Ghost,
                    modifier = Modifier.weight(1f),
                )
                ProButton(
                    text = "Save",
                    onClick = {},
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }
}

@Composable
fun SpecToastCapture() {
    ProToast(message = "Saved $12.40 to Food")
}

@Composable
fun SpecIconsCapture() {
    val dimens = ProExpenseTheme.dimensions
    val icons = listOf(
        ProIconGlyph.Home,
        ProIconGlyph.Budget,
        ProIconGlyph.Journal,
        ProIconGlyph.More,
        ProIconGlyph.Plus,
        ProIconGlyph.Search,
        ProIconGlyph.Check,
        ProIconGlyph.CatFood,
        ProIconGlyph.CatTransport,
        ProIconGlyph.FeatReports,
        ProIconGlyph.FeatDebt,
    )
    Row(
        modifier = Modifier.width(420.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterHorizontally),
    ) {
        icons.forEach { glyph ->
            ProIcon(glyph = glyph, contentDescription = null, size = dimens.iconNav)
        }
    }
}

private fun categoryLabel(id: String): String = when (id) {
    "food" -> "Food"
    "transport" -> "Transport"
    "shopping" -> "Shopping"
    "bills" -> "Bills"
    "health" -> "Health"
    "entertainment" -> "Entertainment"
    "coffee" -> "Coffee runs"
    "pet" -> "Pet care"
    else -> id
}

@Preview(showBackground = true, widthDp = ProArtboard.PIXEL_9_PRO_WIDTH_DP)
@Composable
private fun SpecCapturesPreview() {
    ProExpenseTheme {
        Column(verticalArrangement = Arrangement.spacedBy(24.dp)) {
            SpecCaptureHost { SpecButtonVariantsCapture() }
        }
    }
}
