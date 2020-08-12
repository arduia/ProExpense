package com.arduia.expense.ui.common

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import com.arduia.expense.databinding.SheetExpenseDetailBinding
import com.arduia.expense.ui.vto.ExpenseDetailsVto
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class ExpenseDetailDialog : BottomSheetDialogFragment() {

    private lateinit var viewBinding : SheetExpenseDetailBinding

    private var expenseDetail: ExpenseDetailsVto? = null

    private var editClickListener: (ExpenseDetailsVto) -> Unit = {}

    private var dismissListener: (() -> Unit)? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        initViewBinding(container)
        return viewBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupView()
        showDetailData()
    }

    private fun showDetailData(){

        with(expenseDetail ?: return){
            viewBinding.tvAmountValue.text = amount
            viewBinding.tvDateValue.text = date
            viewBinding.tvNameValue.text = name
            viewBinding.tvNoteValue.text = note
            viewBinding.imvCategory.setImageResource(category)

            if(note.isEmpty()) hideNoteTextView()
        }
    }

    private fun hideNoteTextView(){
        viewBinding.tvNote.visibility = View.INVISIBLE
        viewBinding.tvNoteValue.visibility = View.INVISIBLE
    }

    private fun initViewBinding(parent: ViewGroup?){
        viewBinding = SheetExpenseDetailBinding.inflate(layoutInflater, parent, false)
    }

    fun setEditClickListener(listener: (ExpenseDetailsVto) -> Unit) {
        editClickListener = listener
    }

    fun showDetail(fragmentManager: FragmentManager, detail: ExpenseDetailsVto) {
        expenseDetail = detail
        show(fragmentManager, TAG)
    }

    private fun setupView() {
        viewBinding.btnClose.setOnClickListener {
            dismiss()
        }
        viewBinding.btnEdit.setOnClickListener {
            editClickListener.invoke(expenseDetail!!)
            dismiss()
        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        dismissListener?.invoke()
    }

    fun setDismissListener(listener: () -> Unit) {
        dismissListener = listener
    }


    companion object {
        private const val TAG = "TransactionDetail"
    }
}
