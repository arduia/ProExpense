package com.arduia.expense.ui.more

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.arduia.expense.ui.design.CategoryPicker
import com.arduia.expense.ui.design.ProFlatHeader
import com.arduia.expense.ui.design.customExpenseCategories
import com.arduia.expense.ui.design.defaultExpenseCategories
import com.arduia.expense.ui.theme.ProArtboard
import com.arduia.expense.ui.theme.ProExpenseTheme

@Composable
fun MoreDefaultCategoryScreen(
    selectedCategoryId: String,
    onSelect: (String) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = ProExpenseTheme.colors
    val dimens = ProExpenseTheme.dimensions

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
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = dimens.screenPadding)
                    .padding(bottom = dimens.space24),
        ) {
            ProFlatHeader(
                title = "Default Category",
                onBack = onBack,
                modifier = Modifier.padding(vertical = dimens.space14),
            )
            CategoryPicker(
                defaultCategories = defaultExpenseCategories,
                customCategories = customExpenseCategories,
                selectedCategoryId = selectedCategoryId,
                onCategorySelected = onSelect,
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(top = dimens.space16),
            )
        }
    }
}

@Preview(
    name = "More default category",
    widthDp = ProArtboard.PIXEL_9_PRO_WIDTH_DP,
    heightDp = ProArtboard.PIXEL_9_PRO_HEIGHT_DP,
    showBackground = true,
)
@Composable
private fun MoreDefaultCategoryPreview() {
    ProExpenseTheme {
        MoreDefaultCategoryScreen(
            selectedCategoryId = "food",
            onSelect = {},
            onBack = {},
        )
    }
}

@Preview(
    name = "More default category (dark)",
    widthDp = ProArtboard.PIXEL_9_PRO_WIDTH_DP,
    heightDp = ProArtboard.PIXEL_9_PRO_HEIGHT_DP,
    showBackground = true,
)
@Composable
private fun MoreDefaultCategoryDarkPreview() {
    ProExpenseTheme(darkTheme = true) {
        MoreDefaultCategoryScreen(
            selectedCategoryId = "food",
            onSelect = {},
            onBack = {},
        )
    }
}
