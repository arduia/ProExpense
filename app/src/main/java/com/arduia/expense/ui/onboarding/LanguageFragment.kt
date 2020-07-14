package com.arduia.expense.ui.onboarding

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.arduia.expense.R
import com.arduia.expense.databinding.FragLanguageBinding
import com.arduia.expense.ui.MainHost
import com.arduia.expense.ui.vto.LanguageVto

class LanguageFragment: Fragment(){

    private lateinit var viewBinding: FragLanguageBinding

    private lateinit var languageListAdapter: LanguageListAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewBinding = FragLanguageBinding.inflate(layoutInflater, container, false)

        return viewBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupView()
    }

    private fun setupView(){

        languageListAdapter = LanguageListAdapter(layoutInflater)

        viewBinding.rvLanguages.adapter = languageListAdapter

        languageListAdapter.languageLists = getSampleLanguageLists()

        languageListAdapter.setOnItemClickListener {
            (requireActivity() as? MainHost)?.showSnackMessage("${it.name} is Selected")
        }

        viewBinding.btnContinue.setOnClickListener {
            findNavController().popBackStack()
            findNavController().navigate(R.id.dest_onboard)
        }

    }

    private fun getSampleLanguageLists() = mutableListOf<LanguageVto>().apply {
        add(LanguageVto("1", R.drawable.tmp_myanmar, "Myanmar (Burma)"))
        add(LanguageVto("1", R.drawable.tmp_united_states, "United State (English)"))
        add(LanguageVto("1", R.drawable.tmp_china, "China (Chinese)"))
        add(LanguageVto("1", R.drawable.tmp_myanmar, "Indonesia (Indonesia)"))
    }

}