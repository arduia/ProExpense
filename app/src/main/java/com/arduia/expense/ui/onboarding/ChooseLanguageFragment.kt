package com.arduia.expense.ui.onboarding

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.arduia.design.theme.ProExpenseTheme
import com.arduia.expense.ui.settings.ChooseLanguageScreen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ChooseLanguageFragment : Fragment() {

    private val viewModel: ChooseLanguageViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                ProExpenseTheme {
                    val languages by viewModel.language.observeAsState(emptyList())
                    ChooseLanguageScreen(
                        languages = languages,
                        onLanguageSelected = viewModel::selectLang,
                        onSearchQueryChange = viewModel::searchLang
                    )
                }
            }
        }
    }
}
