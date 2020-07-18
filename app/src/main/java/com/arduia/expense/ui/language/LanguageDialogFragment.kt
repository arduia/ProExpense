package com.arduia.expense.ui.language

import android.app.ActivityOptions
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.arduia.expense.R
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

        viewBinding.btnRestart.isEnabled = false

        languageListAdapter.setOnItemClickListener {
           languageListAdapter.selectedLanguage = it
            viewBinding.btnRestart.isEnabled = true
        }

        viewBinding.btnRestart.setOnClickListener {
            viewModel.selectLanguage(languageListAdapter.selectedLanguage!!.id)
            dismiss()
            restartActivity()
        }
    }

    private fun restartActivity(){
        val currentActivity = requireActivity()
        val intent = currentActivity.intent
        currentActivity.finish()
        val animationBundle =
            ActivityOptions.makeCustomAnimation(requireContext(),
                R.anim.expense_enter_left, android.R.anim.fade_out).toBundle()
        startActivity(intent, animationBundle)
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
