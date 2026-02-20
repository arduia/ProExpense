package com.arduia.expense.ui.settings

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
import com.arduia.expense.ui.NavBaseFragment
import com.arduia.expense.ui.about.AboutUpdateUiModel
import com.arduia.expense.ui.common.ext.restartActivity
import com.arduia.expense.ui.settings.molecule.SettingsEvent
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SettingsFragment : NavBaseFragment() {

    private val viewModel by viewModels<SettingsViewModel>()
    private var languageChooseDialog: ChooseLanguageDialog? = null
    private var currencyDialog: ChooseCurrencyDialog? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                ProExpenseTheme {
                    val state by viewModel.state.collectAsState()

                    LaunchedEffect(state.restartRequired) {
                        if (state.restartRequired) {
                            restartActivity()
                            viewModel.take(SettingsEvent.RestartHandled)
                        }
                    }

                    SettingsScreen(
                        state = state,
                        onEvent = viewModel::take,
                        onNavigationIconClick = {
                            navigationDrawer.openDrawer()
                        },
                        onLanguageClick = {
                            currencyDialog?.dismiss()
                            languageChooseDialog = ChooseLanguageDialog()
                            languageChooseDialog?.show(parentFragmentManager, "ChooseLanguageDialog")
                        },
                        onCurrencyClick = {
                            currencyDialog?.dismiss()
                            currencyDialog = ChooseCurrencyDialog()
                            currencyDialog?.show(childFragmentManager, "ChooseCurrencyDialog")
                        },
                        onOpenUpdateInfoClick = {
                            viewModel.take(SettingsEvent.OpenNewUpdateInfoClicked)
                        }
                    )
                }
            }
        }
    }
}
