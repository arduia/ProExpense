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
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
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
    DayGroup(
        title = "Today · May 25",
        total = "$42",
        transactions = listOf(
            ProTransactionRowModel(
                id = "food-lunch",
                categoryId = "food",
                note = "Lunch with M.",
                meta = "Food · 12:30 PM",
                amount = "$12.40",
            ),
            ProTransactionRowModel(
                id = "coffee-latte",
                categoryId = "coffee",
                note = "Oat latte",
                meta = "Coffee runs · 08:40 AM",
                amount = "$5.00",
            ),
            ProTransactionRowModel(
                id = "entertainment-movie",
                categoryId = "entertainment",
                note = "Movie · Dune",
                meta = "Entertainment",
                amount = "$18.00",
                tag = "Bali Trip",
            ),
        ),
        modifier = Modifier.width(360.dp),
    )
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
        QuickAccessTile(label = "Reports", icon = ProIconGlyph.FeatReports, onClick = {}, modifier = Modifier.width(78.dp))
        QuickAccessTile(label = "Debts", icon = ProIconGlyph.FeatDebt, onClick = {}, modifier = Modifier.width(78.dp))
        QuickAccessTile(label = "Split", icon = ProIconGlyph.FeatSplit, onClick = {}, modifier = Modifier.width(78.dp))
        QuickAccessTile(label = "Events", icon = ProIconGlyph.FeatEvents, onClick = {}, modifier = Modifier.width(78.dp))
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
        Text(
            text = buildAnnotatedString {
                withStyle(
                    ProExpenseTheme.typography.displayAmount
                        .copy(
                            fontSize = 22.sp,
                            fontFamily = typography.amountFamily,
                        )
                        .toSpanStyle()
                        .copy(color = colors.primary),
                ) {
                    append("$")
                }
                withStyle(
                    typography.displayAmount
                        .copy(
                            fontSize = 44.sp,
                            letterSpacing = (-0.025).em,
                            fontFamily = typography.amountFamily,
                        )
                        .toSpanStyle()
                        .copy(color = colors.muted2),
                ) {
                    append("0")
                }
            },
            style = typography.displayAmount,
        )
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

@Composable
fun SpecEmptyStateCapture() {
    EmptyStateContent(
        title = "No expenses yet",
        subtitle = "Start by logging your first one — it takes about five seconds.",
        actionLabel = "Log your first expense",
        onActionClick = {},
        addHintPrefix = "or tap ",
        addHintSuffix = " below",
        modifier = Modifier.width(360.dp),
    )
}

@Composable
fun SpecEmptyStateAddHintCapture() {
    EmptyStateAddHint(
        prefix = "or tap ",
        suffix = " below",
    )
}

@Composable
fun SpecNoticeBannerCapture() {
    NoticeBanner(
        title = "Protect your data with a PIN",
        body = "Set a 6-digit PIN so only you can open the app.",
        onClick = {},
        onDismiss = {},
        modifier = Modifier.width(360.dp),
    )
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
