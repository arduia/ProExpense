package com.arduia.expense.ui.home

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
import com.arduia.design.theme.ProExpenseTheme
import com.arduia.expense.R
import com.arduia.expense.di.LefSideNavOption
import com.arduia.expense.di.TopDropNavOption
import com.arduia.expense.ui.MainHost
import com.arduia.expense.ui.NavBaseFragment
import com.arduia.expense.ui.common.DeleteConfirmDialogScreen
import androidx.compose.ui.window.Dialog
import com.arduia.expense.ui.common.expense.ExpenseDetailDialog
import com.arduia.expense.ui.home.compose.HomeScreen
import com.arduia.expense.ui.home.molecule.HomeEvent
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class HomeFragment : NavBaseFragment() {

    private val viewModel by viewModels<HomeViewModel>()

    @Inject
    @TopDropNavOption
    lateinit var entryNavOption: NavOptions

    @Inject
    @LefSideNavOption
    lateinit var moreRecentNavOption: NavOptions

    private var detailDialog: ExpenseDetailDialog? = null

    private val mainHost by lazy { requireActivity() as MainHost }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                ProExpenseTheme {
                    val state by viewModel.state.collectAsState()

                    // Handle Lifecycle events
                    LaunchedEffect(Unit) {
                        viewModel.take(HomeEvent.LifecycleOnResume)
                    }

                    // Native Navigation Events
                    LaunchedEffect(state.openDrawer) {
                        if (state.openDrawer) {
                            navigationDrawer.openDrawer()
                            viewModel.take(HomeEvent.NavigationHandled)
                        }
                    }

                    LaunchedEffect(state.navigateToLogs) {
                        if (state.navigateToLogs) {
                            navigateToExpenseLogs()
                            viewModel.take(HomeEvent.NavigationHandled)
                        }
                    }

                    LaunchedEffect(state.navigateToEntryId) {
                        state.navigateToEntryId?.let { id ->
                            navigateEntryFragment(id)
                            viewModel.take(HomeEvent.NavigationHandled)
                        }
                    }

                    // Dialog visibility
                    LaunchedEffect(state.showDetailDialogFor) {
                        state.showDetailDialogFor?.let { detail ->
                            detailDialog?.dismiss()
                            detailDialog = ExpenseDetailDialog().apply {
                                setOnDeleteClickListener {
                                    viewModel.take(HomeEvent.DeleteExpensePrepared(it.id))
                                }
                                setOnEditClickListener {
                                    viewModel.take(HomeEvent.EditExpenseClicked(it.id))
                                }
                                setDismissListener {
                                    viewModel.take(HomeEvent.DetailDialogDismissed)
                                    showAddButtonIfCurrentIsHomeDestination()
                                }
                            }
                            detailDialog?.showDetail(
                                parentFragmentManager,
                                detail,
                                isDeleteEnabled = state.isDeleteEnabledOnDetail
                            )
                            mainHost.hideAddButton()
                        } ?: run {
                            detailDialog?.dismiss()
                        }
                    }

                    // Delete Confirm Dialog in Compose
                    if (state.showDeleteConfirmFor != null) {
                        Dialog(onDismissRequest = { viewModel.take(HomeEvent.DeleteDialogDismissed) }) {
                            DeleteConfirmDialogScreen(
                                itemCount = state.showDeleteConfirmFor!!.itemTotal,
                                onConfirmClick = { viewModel.take(HomeEvent.DeleteExpenseConfirmed) },
                                onCloseClick = { viewModel.take(HomeEvent.DeleteDialogDismissed) }
                            )
                        }
                    }

                    // Snackbars
                    LaunchedEffect(state.showSnackbar) {
                        state.showSnackbar?.let { msg ->
                            mainHost.showSnackMessage(msg)
                            viewModel.take(HomeEvent.SnackbarShown)
                        }
                    }

                    // Main Compose UI
                    HomeScreen(
                        incomeOutcomeData = state.incomeOutcomeData,
                        graphData = state.graphUiModel,
                        recentData = state.recentData,
                        onMenuClick = { viewModel.take(HomeEvent.NavIconClicked) },
                        onMoreLogsClick = { viewModel.take(HomeEvent.MoreLogsClicked) },
                        onRecentItemClick = { viewModel.take(HomeEvent.RecentItemClicked(it)) },
                        onAddClick = {
                            viewModel.take(HomeEvent.AddExpenseClicked)
                        }
                    )
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        // Ensure data stays fresh when returning to the fragment
        viewModel.take(HomeEvent.LifecycleOnResume)
    }

    private fun showAddButtonIfCurrentIsHomeDestination() {
        if (findNavController().currentDestination?.id == R.id.dest_home) {
            mainHost.showAddButtonInstantly()
        }
    }

    private fun navigateToExpenseLogs() {
        findNavController().navigate(R.id.dest_expense_logs)
    }

    private fun navigateEntryFragment(id: Int?) {
        val action = if (id == null || id == -1) {
            HomeFragmentDirections
                .actionDestHomeToDestExpenseEntry()
        } else {
            HomeFragmentDirections
                .actionDestHomeToDestExpenseEntry(expenseId = id)
        }
        findNavController().navigate(action, entryNavOption)
    }

    companion object {
        private const val TAG = "MY_HomeFragment"
    }
}
