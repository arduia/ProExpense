package com.arduia.expense.ui.common.expense

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.navigation.NavOptions
import com.arduia.core.view.asInvisible
import com.arduia.core.view.asVisible
import com.arduia.expense.databinding.ExpenseDetailDialogBinding
import com.arduia.expense.di.TopDropNavOption
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlin.properties.Delegates

@AndroidEntryPoint
class ExpenseDetailDialog : BottomSheetDialogFragment() {

    private lateinit var viewBinding : ExpenseDetailDialogBinding

    private var expenseDetail: ExpenseDetailUiModel? = null

    @Inject
    @TopDropNavOption
    lateinit var entryNavOption: NavOptions

    private var dismissListener: (() -> Unit)? = null

    private var editOnClickListener: ((ExpenseDetailUiModel)-> Unit)? = null

    private var deleteOnClickListener: ((ExpenseDetailUiModel)-> Unit)? = null

    private var isDeleteEnabled by Delegates.notNull<Boolean>()

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
            viewBinding.tvCurrencySymbol.text = symbol
            viewBinding.imvCategory.setImageResource(category)

            if(note.isEmpty()) hideNoteTextView()
        }
    }

    private fun hideNoteTextView(){
        viewBinding.tvNote.visibility = View.INVISIBLE
        viewBinding.tvNoteValue.visibility = View.INVISIBLE
    }

    private fun initViewBinding(parent: ViewGroup?){
        viewBinding = ExpenseDetailDialogBinding.inflate(layoutInflater, parent, false)
    }


    fun showDetail(fragmentManager: FragmentManager, detail: ExpenseDetailUiModel, isDeleteEnabled: Boolean = false) {
        expenseDetail = detail
        show(fragmentManager, TAG)
        this.isDeleteEnabled = isDeleteEnabled
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
        if(isDeleteEnabled){
            viewBinding.btnDelete.asVisible()
        }else{
            viewBinding.btnDelete.asInvisible()
        }
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

    fun setOnEditClickListener(listener: (ExpenseDetailUiModel) -> Unit){
        editOnClickListener = listener
    }

    fun setOnDeleteClickListener(listener: (ExpenseDetailUiModel) -> Unit){
        deleteOnClickListener = listener
    }

    companion object {
        private const val TAG = "TransactionDetail"
    }
}
