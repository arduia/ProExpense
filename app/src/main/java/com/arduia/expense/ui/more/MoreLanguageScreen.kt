package com.arduia.expense.ui.more

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.arduia.expense.R
import com.arduia.expense.ui.design.AppLanguage
import com.arduia.expense.ui.design.ProIcon
import com.arduia.expense.ui.design.ProIconGlyph
import com.arduia.expense.ui.design.ProTopBar
import com.arduia.expense.ui.design.proClickable
import com.arduia.expense.ui.theme.ProArtboard
import com.arduia.expense.ui.theme.ProExpenseTheme

@Composable
fun MoreLanguageScreen(
    selectedLanguage: AppLanguage,
    onSelect: (AppLanguage) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = ProExpenseTheme.colors
    val dimens = ProExpenseTheme.dimensions
    val typography = ProExpenseTheme.typography

    Box(
        modifier =
            modifier
                .fillMaxSize()
                .background(colors.paper)
                .statusBarsPadding()
                .navigationBarsPadding(),
    ) {
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(horizontal = dimens.screenPadding)
                    .padding(bottom = dimens.space24),
        ) {
            ProTopBar(title = stringResource(R.string.more_language), onBack = onBack)

            Column(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(top = dimens.space8),
                verticalArrangement = Arrangement.spacedBy(dimens.space8),
            ) {
                AppLanguage.entries.forEach { language ->
                    val selected = language == selectedLanguage
                    Row(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .clip(ProExpenseTheme.shapes.searchField)
                                .background(if (selected) colors.primarySoft else colors.surface)
                                .proClickable(
                                    onClick = { onSelect(language) },
                                    shape = ProExpenseTheme.shapes.searchField,
                                ).padding(horizontal = dimens.space14, vertical = dimens.space14),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Text(text = language.displayName, style = typography.bodySemiBold, color = colors.onSurface)
                        if (selected) {
                            ProIcon(
                                glyph = ProIconGlyph.Check,
                                contentDescription = null,
                                tint = colors.primary,
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview(
    name = "More — language",
    widthDp = ProArtboard.PIXEL_9_PRO_WIDTH_DP,
    heightDp = ProArtboard.PIXEL_9_PRO_HEIGHT_DP,
    showBackground = true,
)
@Composable
private fun MoreLanguageScreenPreview() {
    ProExpenseTheme {
        MoreLanguageScreen(
            selectedLanguage = AppLanguage.ENGLISH,
            onSelect = {},
            onBack = {},
        )
    }
}
