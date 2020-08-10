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
import com.arduia.expense.ui.common.LanguageProviderImpl
import com.arduia.expense.ui.language.LanguageDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SettingsFragment: NavBaseFragment(){

    private lateinit var viewBinding: FragSettingsBinding

    private var languageChooseDialog: LanguageDialogFragment? = null

    private val viewModel by viewModels<SettingsViewModel>()

    @Inject
    lateinit var languageProvider: LanguageProvider

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        viewBinding =  FragSettingsBinding.inflate(layoutInflater, container, false)

        return viewBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycle.addObserver(viewModel)
        setupView()
        setupViewModel()
    }

    private fun setupView(){

        viewBinding.btnDrawerOpen.setOnClickListener{
            navigationDrawer?.openDrawer()
        }

        viewBinding.flLanguage.setOnClickListener {
            languageChooseDialog = LanguageDialogFragment()
            languageChooseDialog?.show(parentFragmentManager, LanguageDialogFragment.TAG)
        }

    }

    private fun setupViewModel(){
        viewModel.selectedLanguage.observe(viewLifecycleOwner, Observer {
            val languageVto = languageProvider.getLanguageVtoByID(it)
            viewBinding.imvLanguage.setImageResource(languageVto.flag)
        })

    }

}
