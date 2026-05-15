package com.arduia.expense.ui.entry

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.arduia.expense.designsystem.component.form.LabeledTextField
import com.arduia.expense.designsystem.component.topbar.ProExpenseTopBar
import com.arduia.expense.designsystem.component.topbar.TopBarNavIcon
import com.arduia.expense.designsystem.theme.ProExpenseTheme
import com.arduia.expense.ui.component.category.CategoryPicker

@Composable
fun ExpenseEntryScreen(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ExpenseEntryViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    ExpenseEntryContent(
        uiState = uiState,
        onNavigateBack = onNavigateBack,
        onNameChange = viewModel::onNameChange,
        onAmountChange = viewModel::onAmountChange,
        onNoteChange = viewModel::onNoteChange,
        onCategorySelect = viewModel::onCategorySelect,
        onSave = viewModel::onSave,
        modifier = modifier,
    )
}

@Composable
private fun ExpenseEntryContent(
    uiState: ExpenseEntryUiState,
    onNavigateBack: () -> Unit,
    onNameChange: (String) -> Unit,
    onAmountChange: (String) -> Unit,
    onNoteChange: (String) -> Unit,
    onCategorySelect: (Int) -> Unit,
    onSave: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            ProExpenseTopBar(
                title = if (uiState.isEditMode) "Edit Expense" else "Add Expense",
                navIcon = TopBarNavIcon.Back,
                onNavIconClick = onNavigateBack,
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(ProExpenseTheme.spacing.grid3)
                .verticalScroll(rememberScrollState())
                .imePadding(),
            verticalArrangement = Arrangement.spacedBy(ProExpenseTheme.spacing.grid3),
        ) {
            LabeledTextField(
                label = "Name",
                value = uiState.name,
                onValueChange = onNameChange,
                isError = uiState.nameError != null,
                errorMessage = uiState.nameError,
            )
            LabeledTextField(
                label = "Amount",
                value = uiState.amount,
                onValueChange = onAmountChange,
                suffix = uiState.currencySymbol,
                isError = uiState.amountError != null,
                errorMessage = uiState.amountError,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            )
            LabeledTextField(
                label = "Note (optional)",
                value = uiState.note,
                onValueChange = onNoteChange,
                singleLine = false,
            )
            CategoryPicker(
                categories = uiState.categories,
                selectedId = uiState.selectedCategoryId,
                onCategorySelect = { onCategorySelect(it.id) },
            )
            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = onSave,
                enabled = !uiState.isSaving,
            ) {
                Text(text = if (uiState.isEditMode) "Update" else "Save")
            }
        }
    }
}

@Preview
@Composable
private fun PreviewExpenseEntryContent() {
    ProExpenseTheme {
        ExpenseEntryContent(
            uiState = ExpenseEntryUiState(currencySymbol = "$"),
            onNavigateBack = {},
            onNameChange = {},
            onAmountChange = {},
            onNoteChange = {},
            onCategorySelect = {},
            onSave = {},
        )
    }
}
