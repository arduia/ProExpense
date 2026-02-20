package com.arduia.expense.ui.statistics

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.viewModels
import com.arduia.design.theme.ProExpenseTheme
import com.arduia.expense.domain.filter.ExpenseLogFilterInfo
import com.arduia.expense.ui.NavBaseFragment
import com.arduia.expense.ui.common.filter.ExpenseFilterDialogFragment
import com.arduia.expense.ui.statistics.molecule.StatisticEvent
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class StatisticsFragment : NavBaseFragment() {

    private val viewModel by viewModels<StatisticsViewModel>()
    private var filterDialog: ExpenseFilterDialogFragment? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                ProExpenseTheme {
                    val state by viewModel.state.collectAsState()

                    LaunchedEffect(state.showFilterDialogForInfo) {
                        state.showFilterDialogForInfo?.let { info ->
                            showDateRangeDialog(info)
                            viewModel.take(StatisticEvent.FilterDialogDismissed)
                        }
                    }

                    StatisticScreen(
                        state = state,
                        onEvent = viewModel::take,
                        onNavigationIconClick = {
                            navigationDrawer.openDrawer()
                        }
                    )
                }
            }
        }
    }

    private fun showDateRangeDialog(filterInfo: ExpenseLogFilterInfo) {
        filterDialog?.dismiss()
        filterDialog = ExpenseFilterDialogFragment(isSortingEnabled = false)
        filterDialog?.setOnFilterApplyListener {
            viewModel.take(StatisticEvent.FilterApplied(it))
        }
        filterDialog?.show(childFragmentManager, filterInfo)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        filterDialog?.dismiss()
        filterDialog = null
    }

}