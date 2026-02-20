package com.arduia.expense.ui.feedback

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.arduia.design.theme.ProExpenseTheme
import com.arduia.expense.R
import com.arduia.expense.ui.NavBaseFragment
import com.arduia.expense.ui.NavigationDrawer
import com.arduia.expense.ui.feedback.molecule.FeedbackMoleculeViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FeedbackFragment : NavBaseFragment() {

    private val viewModel by viewModels<FeedbackMoleculeViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                ProExpenseTheme {
                    val state by viewModel.state.collectAsState()
                    FeedbackScreen(
                        state = state,
                        onEvent = viewModel::take,
                        onNavigationIconClick = {
                            (requireActivity() as? NavigationDrawer)?.openDrawer()
                        },
                        onGoHomeClick = {
                            findNavController().navigate(R.id.dest_home)
                            // Alternative: pop back stack depending on app behavior
                        }
                    )
                }
            }
        }
    }
}
