package com.arduia.expense.ui.common.delete

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import com.arduia.expense.R
import com.arduia.expense.databinding.FragDeleteConfirmDialogBinding
import com.arduia.expense.ui.common.uimodel.DeleteInfoUiModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class DeleteConfirmFragment : BottomSheetDialogFragment() {

    private var _binding: FragDeleteConfirmDialogBinding? = null
    private val binding get() = _binding!!

    private var info: DeleteInfoUiModel? = null

    private var onConfirmListener: (() -> Unit)? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragDeleteConfirmDialogBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupView()
    }

    @SuppressLint("SetTextI18n")
    private fun setupView() {

        binding.btnConfirm.setOnClickListener {
            this.onConfirmListener?.invoke()
            dismiss()
        }

        binding.imvDropClose.setOnClickListener {
            dismiss()
        }

        val total = info?.itemTotal ?: return
        binding.tvItems.text = if(total == 1){
            getString(R.string.single_item_suffix)+ " "
        }else "$total ${getString(R.string.multi_item_suffix)} "
    }

    fun setOnConfirmListener(listener: (() -> Unit)?) {
        this.onConfirmListener = listener
    }

    fun show(fm: FragmentManager, info: DeleteInfoUiModel) {
        this.info = info
        show(fm, "DeleteConfirmFragment")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        info = null
        onConfirmListener = null
    }

}