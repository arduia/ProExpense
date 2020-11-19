package com.arduia.expense.ui.settings

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.arduia.core.extension.px
import com.arduia.expense.databinding.FragChooseLanguageDialogBinding
import com.arduia.expense.ui.common.MarginItemDecoration
import com.arduia.expense.ui.common.ext.restartActivity
import com.arduia.expense.ui.onboarding.ChooseLanguageViewModel
import com.arduia.expense.ui.onboarding.LangListAdapter
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ChooseLanguageDialog : BottomSheetDialogFragment() {

    private lateinit var binding: FragChooseLanguageDialogBinding

    private lateinit var adapter: LangListAdapter

    private val viewModel by viewModels<ChooseLanguageViewModel>()

    private var dismissListener: OnDismissListener? = null

    private var isSelected = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragChooseLanguageDialogBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupView()
        setupViewModel()
    }

    private fun setupView() {
        adapter = LangListAdapter(layoutInflater).apply {
            setOnItemClickListener {
                viewModel.selectLang(it)
                isSelected = true
            }
        }
        with(binding.rvLanguages) {
            addItemDecoration(
                MarginItemDecoration(
                    spaceSide = px(4), spaceHeight = px(4)
                )
            )
            itemAnimator = null
            adapter = this@ChooseLanguageDialog.adapter
        }
        binding.searchBox.setOnSearchTextChangeListener(viewModel::searchLang)
        binding.imvDropClose.setOnClickListener { dismiss() }
        binding.btnRestart.setOnClickListener {
            dismiss()
            restartActivity()
        }
    }

    private fun setupViewModel() {
        viewModel.language.observe(viewLifecycleOwner, adapter::submitList)
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        if(isSelected){
            restartActivity()
        }
        dismissListener?.onDismiss(isSelected)
    }

    fun setOnDismissListener(listener: OnDismissListener) {
        this.dismissListener = listener
    }

    fun interface OnDismissListener {
        fun onDismiss(isFinished: Boolean)
    }
}