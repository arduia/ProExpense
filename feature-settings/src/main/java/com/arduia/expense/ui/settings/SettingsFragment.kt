package com.arduia.expense.ui.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.arduia.expense.feature.settings.databinding.FragmentSettingsBinding
import com.arduia.expense.ui.NavBaseFragment
import com.arduia.expense.ui.common.language.LanguageProvider
import com.arduia.expense.ui.common.ext.restartActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class SettingsFragment : NavBaseFragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    private var languageChooseDialog: ChooseLanguageDialog? = null

    private val viewModel by viewModels<SettingsViewModel>()

    private var currencyDialog: ChooseCurrencyDialog? = null

    private var themeDialog: ChooseThemeDialog? = null

    @Inject
    lateinit var languageProvider: LanguageProvider

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupView()
        collectState()
        collectEffects()
    }

    private fun setupView() {
        binding.tbSettings.setNavigationOnClickListener {
            navigationDrawer.openDrawer()
        }

        binding.flLanguage.setOnClickListener {
            currencyDialog?.dismiss()
            languageChooseDialog = ChooseLanguageDialog()
            languageChooseDialog?.show(parentFragmentManager, "ChooseLanguageDialog")
        }

        binding.flCurrency.setOnClickListener {
            currencyDialog?.dismiss()
            currencyDialog = ChooseCurrencyDialog()
            currencyDialog?.show(childFragmentManager)
        }

        binding.flTheme.setOnClickListener {
            viewModel.chooseTheme()
        }
    }

    private fun collectState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    if (state.currentLanguage.isNotEmpty()) {
                        val languageVto = languageProvider.getLanguageVtoByID(state.currentLanguage)
                        binding.imvLanguage.setImageResource(languageVto.flag)
                    }
                    if (state.currentCurrency.isNotEmpty()) {
                        binding.tvCurrencyValue.text = state.currentCurrency
                    }
                }
            }
        }
    }

    private fun collectEffects() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiEffect.collect { effect ->
                    when (effect) {
                        is SettingsUiEffect.OpenThemeChooser -> chooseTheme(effect.mode)
                        is SettingsUiEffect.ThemeChanged -> restartActivity()
                        is SettingsUiEffect.ShowAboutUpdate -> { /* handled in AboutFragment */ }
                    }
                }
            }
        }
    }

    private fun chooseTheme(mode: Int) {
        themeDialog?.dismiss()
        themeDialog = ChooseThemeDialog(requireContext())
        themeDialog?.setOnSaveListener(viewModel::setThemeMode)
        themeDialog?.showData(mode)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
