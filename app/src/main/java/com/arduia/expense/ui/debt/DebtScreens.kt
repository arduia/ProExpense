package com.arduia.expense.ui.debt

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.arduia.expense.R
import com.arduia.expense.ui.design.DebtRecordRow
import com.arduia.expense.ui.design.ProBottomSheetHost
import com.arduia.expense.ui.design.ProButton
import com.arduia.expense.ui.design.ProButtonSize
import com.arduia.expense.ui.design.ProButtonVariant
import com.arduia.expense.ui.design.ProTopBar
import com.arduia.expense.ui.design.ProfileNameField
import com.arduia.expense.ui.design.SegmentedToggle
import com.arduia.expense.ui.preview.DebtRecordItem
import com.arduia.expense.ui.preview.previewDebtLent
import com.arduia.expense.ui.preview.previewDebtOwe
import com.arduia.expense.ui.theme.ProArtboard
import com.arduia.expense.ui.theme.ProExpenseTheme

@Composable
fun DebtTrackerScreenContent(
    selectedTab: Int,
    lentRecords: List<DebtRecordItem>,
    oweRecords: List<DebtRecordItem>,
    onTabSelected: (Int) -> Unit,
    onAddClick: () -> Unit,
    onRecordClick: (String) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = ProExpenseTheme.colors
    val dimens = ProExpenseTheme.dimensions
    val typography = ProExpenseTheme.typography
    val records = if (selectedTab == 0) lentRecords else oweRecords

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(colors.paper)
            .statusBarsPadding()
            .navigationBarsPadding(),
    ) {
        ProTopBar(
            title = stringResource(R.string.debt_tracker_title),
            onBack = onBack,
            modifier = Modifier.padding(horizontal = dimens.space18),
        )
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(horizontal = dimens.space18, vertical = dimens.space16),
            verticalArrangement = Arrangement.spacedBy(dimens.space16),
        ) {
            SegmentedToggle(
                options = listOf(
                    stringResource(R.string.debt_lent),
                    stringResource(R.string.debt_owe),
                ),
                selectedIndex = selectedTab,
                onSelected = onTabSelected,
            )
            records.forEach { record ->
                DebtRecordRow(
                    personName = record.personName,
                    amount = record.amount,
                    dueLabel = record.dueLabel,
                    isSettled = record.isSettled,
                    onClick = { onRecordClick(record.id) },
                )
            }
            ProButton(
                text = stringResource(R.string.debt_add),
                onClick = onAddClick,
                size = ProButtonSize.Md,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

@Composable
fun DebtAddScreenContent(
    personName: String,
    onPersonNameChange: (String) -> Unit,
    amount: String,
    dueDate: String,
    onClose: () -> Unit,
    onSave: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = ProExpenseTheme.colors
    val dimens = ProExpenseTheme.dimensions
    val typography = ProExpenseTheme.typography

    ProBottomSheetHost(
        title = stringResource(R.string.debt_add_title),
        onClose = onClose,
        modifier = modifier,
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(dimens.space12)) {
            ProfileNameField(
                value = personName,
                onValueChange = onPersonNameChange,
                placeholder = stringResource(R.string.debt_person_hint),
            )
            Text(text = amount, style = typography.sectionHead, color = colors.onSurface)
            Text(text = dueDate, style = typography.caption, color = colors.onSurfaceMuted)
            ProButton(
                text = stringResource(R.string.save),
                onClick = onSave,
                enabled = personName.isNotBlank(),
                size = ProButtonSize.Lg,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

@Composable
fun DebtDetailScreenContent(
    personName: String,
    isLent: Boolean,
    amount: String,
    dueLabel: String,
    isSettled: Boolean,
    onBack: () -> Unit,
    onMarkSettled: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = ProExpenseTheme.colors
    val dimens = ProExpenseTheme.dimensions
    val typography = ProExpenseTheme.typography
    val title = if (isLent) {
        stringResource(R.string.debt_detail_lent, personName)
    } else {
        stringResource(R.string.debt_detail_owe, personName)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(colors.paper)
            .statusBarsPadding()
            .navigationBarsPadding(),
    ) {
        ProTopBar(title = title, onBack = onBack, modifier = Modifier.padding(horizontal = dimens.space18))
        Column(
            modifier = Modifier.padding(horizontal = dimens.space18, vertical = dimens.space24),
            verticalArrangement = Arrangement.spacedBy(dimens.space16),
        ) {
            Text(text = amount, style = typography.screenTitle, color = colors.onSurface)
            Text(text = dueLabel, style = typography.body, color = colors.onSurfaceMuted)
            if (isSettled) {
                Text(text = stringResource(R.string.debt_settled_label), style = typography.eyebrow, color = colors.success)
                ProButton(
                    text = stringResource(R.string.debt_delete_settled),
                    onClick = onDelete,
                    variant = ProButtonVariant.Ghost,
                    modifier = Modifier.fillMaxWidth(),
                )
            } else {
                ProButton(
                    text = stringResource(R.string.debt_mark_settled),
                    onClick = onMarkSettled,
                    variant = ProButtonVariant.Success,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
    }
}

@Preview(name = "Debt lent", widthDp = ProArtboard.PIXEL_9_PRO_WIDTH_DP, heightDp = ProArtboard.PIXEL_9_PRO_HEIGHT_DP, showBackground = true)
@Composable
private fun DebtLentPreview() {
    ProExpenseTheme {
        DebtTrackerScreenContent(
            selectedTab = 0,
            lentRecords = previewDebtLent,
            oweRecords = previewDebtOwe,
            onTabSelected = {},
            onAddClick = {},
            onRecordClick = {},
            onBack = {},
        )
    }
}

@Preview(name = "Debt add", widthDp = ProArtboard.PIXEL_9_PRO_WIDTH_DP, heightDp = ProArtboard.PIXEL_9_PRO_HEIGHT_DP, showBackground = true)
@Composable
private fun DebtAddPreview() {
    ProExpenseTheme {
        DebtAddScreenContent(
            personName = "John",
            onPersonNameChange = {},
            amount = "$50.00",
            dueDate = "Due May 30",
            onClose = {},
            onSave = {},
        )
    }
}

@Preview(name = "Debt detail lent", widthDp = ProArtboard.PIXEL_9_PRO_WIDTH_DP, heightDp = ProArtboard.PIXEL_9_PRO_HEIGHT_DP, showBackground = true)
@Composable
private fun DebtDetailLentPreview() {
    ProExpenseTheme {
        DebtDetailScreenContent(
            personName = "John",
            isLent = true,
            amount = "$50.00",
            dueLabel = "Due May 30",
            isSettled = false,
            onBack = {},
            onMarkSettled = {},
            onDelete = {},
        )
    }
}
