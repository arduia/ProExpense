package com.arduia.expense.ui.settings

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.arduia.core.extension.px
import com.arduia.expense.R
import com.arduia.expense.databinding.FragChooseLanguageDialogBinding
import com.arduia.expense.ui.common.MarginItemDecoration
import com.arduia.expense.ui.common.ext.restartActivity
import com.arduia.expense.ui.onboarding.ChooseLanguageViewModel
import com.arduia.expense.ui.onboarding.LangListAdapter
import com.arduia.mvvm.EventObserver
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ChooseLanguageDialog : BottomSheetDialogFragment() {

    private var _binding: FragChooseLanguageDialogBinding? = null
    private val binding get() = _binding!!

    private var adapter: LangListAdapter? = null

    private val viewModel by viewModels<ChooseLanguageViewModel>()

    private var dismissListener: OnDismissListener? = null

    private var shouldRestartActivity = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragChooseLanguageDialogBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupView()
        setupViewModel()
    }

    private fun setupView() {
        binding.btnRestart.isEnabled = false
        adapter = LangListAdapter(layoutInflater).apply {
            setOnItemClickListener {
                viewModel.selectLang(it)
            }
        }
        with(binding.rvLanguages) {
            addItemDecoration(
                MarginItemDecoration(
                    spaceSide = resources.getDimension(R.dimen.grid_3).toInt(),
                    spaceHeight = px(4)
                )
            )
            itemAnimator = null
            adapter = this@ChooseLanguageDialog.adapter
        }
        binding.searchBox.setOnSearchTextChangeListener(viewModel::searchLang)

        binding.imvDropClose.setOnClickListener {
            viewModel.onExit()
        }

        binding.btnRestart.setOnClickListener {
            viewModel.onRestart()
        }
    }

    private fun setupViewModel() {

        viewModel.language.observe(viewLifecycleOwner) {
            adapter?.submitList(it)
        }

        viewModel.isRestartEnable.observe(viewLifecycleOwner) {
            binding.btnRestart.isEnabled = it
        }

        viewModel.onRestartAndDismiss.observe(viewLifecycleOwner, EventObserver {
            shouldRestartActivity = true
            dismiss()
        })

        viewModel.onDismiss.observe(viewLifecycleOwner, EventObserver {
            shouldRestartActivity = false
            dismiss()
        })

    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        if (shouldRestartActivity) {
            restartActivity()
        }
        dismissListener?.onDismiss(shouldRestartActivity)
    }

    fun setOnDismissListener(listener: OnDismissListener) {
        this.dismissListener = listener
    }

    fun interface OnDismissListener {
        fun onDismiss(isFinished: Boolean)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.rvLanguages.adapter = null
        adapter = null
        _binding = null
    }
}