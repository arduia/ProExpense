package com.arduia.expense.ui.language

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.arduia.expense.databinding.FragLangDialogBinding
import com.arduia.expense.ui.common.LanguageProviderImpl
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview

class LanguageDialogFragment : BottomSheetDialogFragment(){

    private lateinit var viewBinding : FragLangDialogBinding

    private lateinit var languageListAdapter: LanguageListAdapter

    private val viewModel by viewModels<LanguageViewModel>()

    private val langProvider by lazy {
        LanguageProviderImpl()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewBinding = FragLangDialogBinding.inflate(layoutInflater, container, false)

        return viewBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycle.addObserver(viewModel)
        setupView()
        setupViewModel()
    }

    @FlowPreview
    @OptIn(ExperimentalCoroutinesApi::class)
    private fun setupView(){

        languageListAdapter = LanguageListAdapter(layoutInflater)
        viewBinding.rvLanguages.adapter = languageListAdapter
        langProvider.init()
        languageListAdapter.languageLists = langProvider.getAvailableLanguages()

        viewBinding.btnLanguageClose.setOnClickListener {
            dismiss()
        }

        languageListAdapter.setOnItemClickListener {
            viewModel.selectLanguage(it.id)
            requireActivity().recreate()
            dismiss()
        }
    }

    private fun setupViewModel(){

        viewModel.selectedLanguage.observe(viewLifecycleOwner, Observer {
            val selectedLanguageVto = langProvider.getLanguageVtoByID(it)
            languageListAdapter.selectedLanguage  = selectedLanguageVto
        })

    }

    companion object{
        const val TAG = "LanguageDialogFragment"
    }

}