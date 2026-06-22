package com.arduia.expense.feature.categories.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.tooling.preview.Preview
import com.arduia.expense.feature.categories.R
import com.arduia.expense.ui.design.ProAlertDialog
import com.arduia.expense.ui.design.ProBottomSheetHost
import com.arduia.expense.ui.design.ProButtonVariant
import com.arduia.expense.ui.design.ProIconGlyph
import com.arduia.expense.feature.categories.ui.preview.CategoryListUiState
import com.arduia.expense.feature.categories.ui.preview.CategoryNewFormState
import com.arduia.expense.feature.categories.ui.preview.CategoryRowUi
import com.arduia.expense.feature.categories.ui.preview.previewCategoryList
import com.arduia.expense.ui.theme.ProArtboard
import com.arduia.expense.ui.theme.ProExpenseTheme

@Composable
fun CategoryListFlow(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    state: CategoryListUiState = previewCategoryList,
    onCreate: (iconId: String, label: String) -> Unit = { _, _ -> },
    onUpdate: (oldId: String, iconId: String, label: String) -> Unit = { _, _, _ -> },
    onDelete: (id: String) -> Unit = {},
) {
    val colors = ProExpenseTheme.colors
    val defaults = state.defaults
    // Local copy drives drag-reorder; order is not persisted (no order column).
    var custom by remember(state.custom) { mutableStateOf(state.custom) }

    var showSheet by remember { mutableStateOf(false) }
    var editingRow by remember { mutableStateOf<CategoryRowUi?>(null) }
    var actionsRow by remember { mutableStateOf<CategoryRowUi?>(null) }
    var deleteRow by remember { mutableStateOf<CategoryRowUi?>(null) }
    var form by remember { mutableStateOf(CategoryNewFormState()) }

    fun takenNames(excluding: CategoryRowUi?): Set<String> =
        (defaults + custom)
            .filter { it.categoryId != excluding?.categoryId }
            .map { it.label.lowercase() }
            .toSet()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(colors.paper),
    ) {
        CategoryListScreen(
            state = state.copy(custom = custom),
            onBack = onBack,
            onCreate = {
                editingRow = null
                form = CategoryNewFormState()
                showSheet = true
            },
            onCustomRowClick = { actionsRow = it },
            onReorder = { from, to ->
                custom = custom.toMutableList().apply { add(to, removeAt(from)) }
            },
        )

        ProBottomSheetHost(
            visible = showSheet,
            title = stringResource(
                if (editingRow == null) R.string.categories_new_title else R.string.categories_edit_title,
            ),
            onClose = { showSheet = false },
        ) {
            CategoryNewSheetContent(
                form = form,
                onNameChange = { name ->
                    val taken = takenNames(editingRow)
                    form = form.copy(
                        name = name,
                        duplicate = name.isNotBlank() && name.trim().lowercase() in taken,
                    )
                },
                onIconSelected = { form = form.copy(selectedIconId = it) },
                onAdd = {
                    if (form.canAdd) {
                        val edited = editingRow
                        if (edited == null) {
                            onCreate(form.selectedIconId, form.name.trim())
                        } else {
                            onUpdate(edited.categoryId, form.selectedIconId, form.name.trim())
                        }
                        showSheet = false
                        editingRow = null
                    }
                },
                confirmLabel = stringResource(
                    if (editingRow == null) R.string.categories_add else R.string.categories_save,
                ),
            )
        }

        ProBottomSheetHost(
            visible = actionsRow != null,
            title = null,
            onClose = { actionsRow = null },
        ) {
            actionsRow?.let { row ->
                CategoryActionsSheetContent(
                    row = row,
                    onEdit = {
                        editingRow = row
                        form = CategoryNewFormState(
                            name = row.label,
                            selectedIconId = row.categoryId,
                            iconOptions = (listOf(row.categoryId) +
                                CategoryNewFormState().iconOptions).distinct().take(4),
                        )
                        actionsRow = null
                        showSheet = true
                    },
                    onDelete = {
                        deleteRow = row
                        actionsRow = null
                    },
                    onCancel = { actionsRow = null },
                )
            }
        }

        ProAlertDialog(
            visible = deleteRow != null,
            icon = ProIconGlyph.Close,
            iconTint = colors.danger,
            iconBackground = colors.dangerTint,
            title = stringResource(R.string.categories_delete_title),
            body = buildAnnotatedString { append(stringResource(R.string.categories_delete_body)) },
            confirmLabel = stringResource(R.string.categories_delete_confirm),
            onConfirm = {
                deleteRow?.let { row -> onDelete(row.categoryId) }
                deleteRow = null
            },
            dismissLabel = stringResource(R.string.categories_delete_cancel),
            onDismiss = { deleteRow = null },
            confirmVariant = ProButtonVariant.Danger,
        )
    }
}

@Preview(
    name = "Category list flow",
    widthDp = ProArtboard.PIXEL_9_PRO_WIDTH_DP,
    heightDp = ProArtboard.PIXEL_9_PRO_HEIGHT_DP,
    showBackground = true,
)
@Composable
private fun CategoryListFlowPreview() {
    ProExpenseTheme {
        CategoryListFlow(onBack = {})
    }
}
