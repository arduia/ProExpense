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
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SettingsFragment: NavBaseFragment(){

    private lateinit var binding: FragSettingsBinding

    private var languageChooseDialog: ChooseLanguageDialog? = null

    private val viewModel by viewModels<SettingsViewModel>()

    private var currencyDialog: ChooseCurrencyDialog? = null

    @Inject
    lateinit var languageProvider: LanguageProvider

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =  FragSettingsBinding.inflate(layoutInflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupView()
        setupViewModel()
    }

    private fun setupView(){

        binding.tbSettings.setNavigationOnClickListener {
            navigationDrawer?.openDrawer()
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

    }

    private fun setupViewModel(){
        viewModel.selectedLanguage.observe(viewLifecycleOwner, Observer {
            val languageVto = languageProvider.getLanguageVtoByID(it)
            binding.imvLanguage.setImageResource(languageVto.flag)
        })

        viewModel.currencyValue.observe(viewLifecycleOwner, binding.tvCurrencyValue::setText)

    }

}
