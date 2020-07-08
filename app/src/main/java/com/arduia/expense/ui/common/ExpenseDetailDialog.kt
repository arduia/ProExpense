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

class ExpenseDetailDialog: BottomSheetDialogFragment(){

    private val viewBinding by lazy { SheetExpenseDetailBinding.inflate(layoutInflater, null, false) }

    private var expenseDetail: ExpenseDetailsVto? = null

    private var editClickListener: (ExpenseDetailsVto) -> Unit = {}

    private var dismissListener: (() -> Unit)? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        expenseDetail!!.bindDetailData()
        setupView()
        return viewBinding.root
    }

    fun setEditClickListener( listener: (ExpenseDetailsVto)-> Unit){
        editClickListener = listener
    }

    fun showDetail(fm: FragmentManager, detail: ExpenseDetailsVto){
       expenseDetail = detail
        show(fm, TAG)
    }

    private fun setupView(){
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

    fun setDismissListener(listener: () -> Unit){
        dismissListener = listener
    }

    private fun ExpenseDetailsVto.bindDetailData(){
        viewBinding.tvAmountValue.text = amount
        viewBinding.tvDateValue.text = date
        viewBinding.tvNameValue.text = name
        viewBinding.tvNoteValue.text = note
        viewBinding.imvCategory.setImageResource(category)
    }

    companion object{
        private const val TAG = "TransactionDetail"
    }
}
