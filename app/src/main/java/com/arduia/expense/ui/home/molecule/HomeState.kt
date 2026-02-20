package com.arduia.expense.ui.home.molecule

import com.arduia.expense.ui.common.expense.ExpenseDetailUiModel
import com.arduia.expense.ui.common.uimodel.DeleteInfoUiModel
import com.arduia.expense.ui.expenselogs.ExpenseUiModel
import com.arduia.expense.ui.home.IncomeOutcomeUiModel
import com.arduia.expense.ui.home.WeeklyGraphUiModel

data class HomeState(
    val incomeOutcomeData: IncomeOutcomeUiModel? = null,
    val graphUiModel: WeeklyGraphUiModel? = null,
    val recentData: List<ExpenseUiModel> = emptyList(),
    
    // Dialog and Navigation States
    val showDetailDialogFor: ExpenseDetailUiModel? = null,
    val showDeleteConfirmFor: DeleteInfoUiModel? = null,
    val showSnackbar: String? = null,
    val openDrawer: Boolean = false,
    val navigateToLogs: Boolean = false,
    val navigateToEntryId: Int? = null,
    val isDeleteEnabledOnDetail: Boolean = true
)
