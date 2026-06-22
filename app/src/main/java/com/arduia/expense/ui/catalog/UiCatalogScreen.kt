package com.arduia.expense.ui.catalog

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.arduia.expense.ui.design.ProIcon
import com.arduia.expense.ui.design.ProIconGlyph
import com.arduia.expense.ui.design.proClickable
import com.arduia.expense.ui.theme.ProArtboard
import com.arduia.expense.ui.theme.ProExpenseTheme
import com.arduia.expense.ui.theme.centeredGlyph

/**
 * UI verification entry page: an index of every screen + edge-case state from the design spec / PRD.
 * Tapping a row renders that state full-screen exactly as it ships; system-back or the floating
 * control returns to the index.
 */
@Composable
fun UiCatalogScreen(modifier: Modifier = Modifier) {
    val sections = remember { uiCatalogSections() }
    var selectedId by rememberSaveable { mutableStateOf<String?>(null) }
    val selected = remember(selectedId, sections) {
        selectedId?.let { id -> sections.firstNotNullOfOrNull { it.entryOrNull(id) } }
    }

    if (selected == null) {
        UiCatalogIndex(
            sections = sections,
            onOpen = { selectedId = it },
            modifier = modifier,
        )
    } else {
        BackHandler { selectedId = null }
        Box(modifier.fillMaxSize()) {
            selected.content()
            CatalogReturnBar(
                label = selected.label,
                onBack = { selectedId = null },
                modifier = Modifier.align(Alignment.TopStart),
            )
        }
    }
}

@Composable
private fun UiCatalogIndex(
    sections: List<UiCatalogSection>,
    onOpen: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = ProExpenseTheme.colors
    val dimens = ProExpenseTheme.dimensions
    val typography = ProExpenseTheme.typography
    val total = sections.sumOf { it.entries.size }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(colors.paper)
            .statusBarsPadding()
            .navigationBarsPadding(),
        contentPadding = PaddingValues(
            start = dimens.screenPadding,
            end = dimens.screenPadding,
            top = dimens.space24,
            bottom = dimens.space24,
        ),
        verticalArrangement = Arrangement.spacedBy(dimens.space24),
    ) {
        item(key = "header") {
            Column(verticalArrangement = Arrangement.spacedBy(dimens.space8)) {
                Text(text = "UI CATALOG", style = typography.eyebrow, color = colors.muted)
                Text(
                    text = "Every screen & edge case",
                    style = typography.heroGreeting,
                    color = colors.onSurface,
                )
                Text(
                    text = "$total states across ${sections.size} flows — tap to verify against the spec.",
                    style = typography.body,
                    color = colors.onSurfaceVariant,
                )
            }
        }

        items(sections, key = { it.id }) { section ->
            CatalogSectionCard(section = section, onOpen = onOpen)
        }
    }
}

@Composable
private fun CatalogSectionCard(
    section: UiCatalogSection,
    onOpen: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = ProExpenseTheme.colors
    val dimens = ProExpenseTheme.dimensions
    val typography = ProExpenseTheme.typography

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(dimens.space10),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = section.title.uppercase(),
                style = typography.eyebrow,
                color = colors.onSurfaceVariant,
            )
            Text(text = section.specRef, style = typography.tabTimestamp, color = colors.muted)
        }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(colors.surface, ProExpenseTheme.shapes.card),
        ) {
            section.entries.forEachIndexed { index, entry ->
                if (index > 0) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = dimens.space16)
                            .height(1.dp)
                            .background(colors.lineSoft),
                    )
                }
                CatalogEntryRow(entry = entry, onOpen = onOpen)
            }
        }
    }
}

@Composable
private fun CatalogEntryRow(
    entry: UiCatalogEntry,
    onOpen: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = ProExpenseTheme.colors
    val dimens = ProExpenseTheme.dimensions
    val typography = ProExpenseTheme.typography

    Row(
        modifier = modifier
            .fillMaxWidth()
            .proClickable(onClick = { onOpen(entry.id) }, shape = RectangleShape)
            .padding(horizontal = dimens.space16, vertical = dimens.rowPaddingV),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(dimens.space12),
    ) {
        Text(
            text = entry.label,
            style = typography.bodyMedium,
            color = colors.onSurface,
            modifier = Modifier.weight(1f),
        )
        if (entry.edge) {
            Text(
                text = "EDGE",
                style = typography.navLabel,
                color = colors.tag,
                modifier = Modifier
                    .background(colors.tagTint, ProExpenseTheme.shapes.chip)
                    .padding(horizontal = dimens.space8, vertical = dimens.space2),
            )
        }
        ProIcon(
            glyph = ProIconGlyph.ChevronRight,
            contentDescription = null,
            tint = colors.muted,
            size = dimens.iconInline,
        )
    }
}

@Composable
private fun CatalogReturnBar(
    label: String,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = ProExpenseTheme.colors
    val dimens = ProExpenseTheme.dimensions
    val typography = ProExpenseTheme.typography

    Box(modifier = modifier.statusBarsPadding().padding(dimens.space8)) {
        Row(
            modifier = Modifier
                .shadow(8.dp, ProExpenseTheme.shapes.chip, clip = false)
                .background(colors.onSurface.copy(alpha = 0.9f), ProExpenseTheme.shapes.chip)
                .proClickable(onClick = onBack, shape = ProExpenseTheme.shapes.chip)
                .padding(horizontal = dimens.space12, vertical = dimens.space6),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(dimens.space4),
        ) {
            ProIcon(
                glyph = ProIconGlyph.Back,
                contentDescription = "Back to catalog",
                tint = colors.paper,
                size = dimens.iconInline,
            )
            Text(
                text = label,
                style = typography.navAction.centeredGlyph(),
                color = colors.paper,
            )
        }
    }
}

@Preview(
    name = "UI catalog — index",
    widthDp = ProArtboard.PIXEL_9_PRO_WIDTH_DP,
    heightDp = ProArtboard.PIXEL_9_PRO_HEIGHT_DP,
    showBackground = true,
)
@Composable
private fun UiCatalogIndexPreview() {
    ProExpenseTheme {
        UiCatalogIndex(sections = uiCatalogSections(), onOpen = {})
    }
}

@Preview(
    name = "UI catalog — return bar",
    widthDp = ProArtboard.PIXEL_9_PRO_WIDTH_DP,
    showBackground = true,
)
@Composable
private fun CatalogReturnBarPreview() {
    ProExpenseTheme {
        Box(Modifier.fillMaxWidth().background(ProExpenseTheme.colors.paper)) {
            CatalogReturnBar(label = "Detail · lent", onBack = {})
        }
    }
}
