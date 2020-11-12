package com.arduia.expense.ui.common

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.arduia.expense.data.local.ExpenseEnt
import com.arduia.expense.databinding.SheetExpenseDetailBinding
import com.arduia.expense.di.TopDropNavOption
import com.arduia.expense.ui.home.HomeFragmentDirections
import com.arduia.expense.ui.vto.ExpenseDetailsVto
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class ExpenseDetailDialog : BottomSheetDialogFragment() {

    private lateinit var viewBinding : SheetExpenseDetailBinding

    private var expenseDetail: ExpenseDetailsVto? = null

    @Inject
    @TopDropNavOption
    lateinit var entryNavOption: NavOptions

    private var dismissListener: (() -> Unit)? = null

    private var editOnClickListener: ((ExpenseDetailsVto)-> Unit)? = null

    private var deleteOnClickListener: ((ExpenseDetailsVto)-> Unit)? = null

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


    fun showDetail(fragmentManager: FragmentManager, detail: ExpenseDetailsVto) {
        expenseDetail = detail
        show(fragmentManager, TAG)
    }

    private fun setupView() {
        viewBinding.btnClose.setOnClickListener {
            dismiss()
        }
        viewBinding.btnEdit.setOnClickListener {
           val detail = expenseDetail ?: return@setOnClickListener
            editOnClickListener?.invoke(detail)
            dismiss()
        }
        viewBinding.btnDelete.setOnClickListener(::onDeleteClick)
    }

    private fun onDeleteClick(view: View){
        val item = expenseDetail?:return
        deleteOnClickListener?.invoke(item)
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        dismissListener?.invoke()
    }

    fun setDismissListener(listener: () -> Unit) {
        dismissListener = listener
    }

    fun setOnEditClickListener(listener: (ExpenseDetailsVto) -> Unit){
        editOnClickListener = listener
    }

    fun setOnDeleteClickListener(listener: (ExpenseDetailsVto) -> Unit){
        deleteOnClickListener = listener
    }

    companion object {
        private const val TAG = "TransactionDetail"
    }
}
