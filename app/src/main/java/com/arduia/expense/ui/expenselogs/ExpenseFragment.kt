package com.arduia.expense.ui.expenselogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.viewModels
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.paging.compose.collectAsLazyPagingItems
import com.arduia.design.theme.ProExpenseTheme
import com.arduia.expense.di.TopDropNavOption
import com.arduia.expense.domain.filter.ExpenseLogFilterInfo
import com.arduia.expense.ui.NavBaseFragment
import com.arduia.expense.ui.common.delete.DeleteConfirmFragment
import com.arduia.expense.ui.common.expense.ExpenseDetailDialog
import com.arduia.expense.ui.common.expense.ExpenseDetailUiModel
import com.arduia.expense.ui.common.filter.ExpenseFilterDialogFragment
import com.arduia.expense.ui.common.uimodel.DeleteInfoUiModel
import com.arduia.expense.ui.expenselogs.molecule.ExpenseLogsEvent
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ExpenseFragment : NavBaseFragment() {

    private val viewModel by viewModels<ExpenseViewModel>()

    private var filterDialog: ExpenseFilterDialogFragment? = null

    @Inject
    @TopDropNavOption
    lateinit var entryNavOption: NavOptions

    private var deleteConfirmDialog: DeleteConfirmFragment? = null
    private var detailDialog: ExpenseDetailDialog? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                ProExpenseTheme {
                    val state by viewModel.state.collectAsState()
                    val pagingItems = viewModel.expenseListPagingData.collectAsLazyPagingItems()

                    LaunchedEffect(state.showFilterDialogForInfo) {
                        state.showFilterDialogForInfo?.let { showFilterDialog(it) } ?: filterDialog?.dismiss()
                    }

                    LaunchedEffect(state.showMultiDeleteConfirmCount) {
                        state.showMultiDeleteConfirmCount?.let { showDeleteConfirmDialog(it) } ?: deleteConfirmDialog?.dismiss()
                    }

                    LaunchedEffect(state.showSingleDeleteConfirm) {
                        if (state.showSingleDeleteConfirm) showSingleDeleteConfirmDialog() else deleteConfirmDialog?.dismiss()
                    }

                    LaunchedEffect(state.showDetailDialogFor) {
                        state.showDetailDialogFor?.let { showItemDetail(it) } ?: detailDialog?.dismiss()
                    }

                    ExpenseLogsScreen(
                        state = state,
                        pagingItems = pagingItems,
                        selectedIds = state.selectedIds,
                        onEvent = { event ->
                            when (event) {
                                is ExpenseLogsEvent.NavDrawerClicked -> navigationDrawer.openDrawer()
                                else -> viewModel.take(event)
                            }
                        },
                        onItemClick = { logItem ->
                            viewModel.take(ExpenseLogsEvent.ShowItemDetail(logItem))
                        },
                        onItemDeleteClick = { logItem ->
                            viewModel.take(ExpenseLogsEvent.SingleDeletePrepared(logItem.expenseLog.id))
                        },
                        onItemSelectionToggle = { id ->
                            viewModel.take(ExpenseLogsEvent.ToggleSelection(id))
                        }
                    )
                }
            }
        }
    }

    private fun showDeleteConfirmDialog(countTotal: Int) {
        deleteConfirmDialog?.dismiss()
        deleteConfirmDialog = DeleteConfirmFragment()
        deleteConfirmDialog?.setOnConfirmListener {
            viewModel.take(ExpenseLogsEvent.MultiDeleteConfirmed)
        }
        deleteConfirmDialog?.setOnDismissListener {
            viewModel.take(ExpenseLogsEvent.MultiDeleteDismissed)
        }
        deleteConfirmDialog?.show(childFragmentManager, DeleteInfoUiModel(countTotal, null))
    }

    private fun showSingleDeleteConfirmDialog() {
        deleteConfirmDialog?.dismiss()
        deleteConfirmDialog = DeleteConfirmFragment()
        deleteConfirmDialog?.setOnConfirmListener {
            viewModel.take(ExpenseLogsEvent.SingleDeleteConfirmed)
        }
        deleteConfirmDialog?.setOnDismissListener {
            viewModel.take(ExpenseLogsEvent.SingleDeleteDismissed)
        }
        deleteConfirmDialog?.show(childFragmentManager, DeleteInfoUiModel(1, null))
    }

    private fun showFilterDialog(filterEnt: ExpenseLogFilterInfo) {
        filterDialog?.dismiss()
        filterDialog = ExpenseFilterDialogFragment().apply {
            setOnFilterApplyListener {
                viewModel.take(ExpenseLogsEvent.FilterApplied(it))
            }
            setOnDismissListener {
                viewModel.take(ExpenseLogsEvent.FilterDialogDismissed)
            }
        }
        filterDialog?.show(childFragmentManager, filterEnt)
    }

    private fun showItemDetail(detail: ExpenseDetailUiModel) {
        detailDialog?.dismiss()
        detailDialog = ExpenseDetailDialog()
        detailDialog?.setOnDeleteClickListener {
            detailDialog?.dismiss()
            viewModel.take(ExpenseLogsEvent.DetailDialogDismissed)
            viewModel.take(ExpenseLogsEvent.SingleDeletePrepared(it.id))
        }
        detailDialog?.setOnEditClickListener {
            detailDialog?.dismiss()
            viewModel.take(ExpenseLogsEvent.DetailDialogDismissed)
            navigateToExpenseEntryFragment(detail.id)
        }
        detailDialog?.setDismissListener {
            viewModel.take(ExpenseLogsEvent.DetailDialogDismissed)
        }
        detailDialog?.showDetail(parentFragmentManager, detail, isDeleteEnabled = true)
    }

    private fun navigateToExpenseEntryFragment(id: Int) {
        val action = ExpenseFragmentDirections.actionExpenseToEntry(expenseId = id)
        findNavController().navigate(action, entryNavOption)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        filterDialog?.dismiss()
        filterDialog = null
        deleteConfirmDialog?.dismiss()
        deleteConfirmDialog = null
        detailDialog?.dismiss()
        detailDialog = null
    }
}
