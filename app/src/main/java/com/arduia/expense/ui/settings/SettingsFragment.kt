package com.arduia.expense.ui.settings


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.arduia.expense.databinding.FragSettingsBinding
import com.arduia.expense.ui.NavBaseFragment
import com.arduia.expense.ui.common.LanguageProvider
import com.arduia.expense.ui.common.ext.restartActivity
import com.arduia.mvvm.EventObserver
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SettingsFragment: NavBaseFragment(){

    private var _binding: FragSettingsBinding? = null
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
    ): View  {
        _binding =  FragSettingsBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupView()
        setupViewModel()
    }

    private fun setupView(){

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

    private fun setupViewModel(){
        viewModel.selectedLanguage.observe(viewLifecycleOwner, Observer {
            val languageVto = languageProvider.getLanguageVtoByID(it)
            binding.imvLanguage.setImageResource(languageVto.flag)
        })

        viewModel.currencyValue.observe(viewLifecycleOwner, binding.tvCurrencyValue::setText)

        viewModel.onThemeOpenToChange.observe(viewLifecycleOwner, EventObserver(::chooseTheme))

        viewModel.onThemeChanged.observe(viewLifecycleOwner,EventObserver{
            restartActivity()
        })
    }

    private fun chooseTheme(mode: Int){
        themeDialog?.dismiss()
        themeDialog = ChooseThemeDialog(requireContext())
        themeDialog?.setOnSaveListener(viewModel::setThemeMode)
        themeDialog?.showData(mode)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        languageChooseDialog = null
        currencyDialog = null
        themeDialog = null
        _binding = null

    }
}
