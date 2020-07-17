package com.arduia.expense.ui.settings


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.arduia.expense.databinding.FragSettingsBinding
import com.arduia.expense.ui.NavBaseFragment
import com.arduia.expense.ui.language.LanguageDialogFragment

class SettingsFragment: NavBaseFragment(){

    private lateinit var viewBinding: FragSettingsBinding

    private var languageChooseDialog: LanguageDialogFragment? = null

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
        setupView()
    }

    private fun setupView(){

        viewBinding.btnDrawerOpen.setOnClickListener{
            openDrawer()
        }

        viewBinding.flLanguage.setOnClickListener {
            languageChooseDialog = LanguageDialogFragment()
            languageChooseDialog?.show(parentFragmentManager, LanguageDialogFragment.TAG)
        }

    }

}
