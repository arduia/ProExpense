package com.arduia.myacc.ui.common

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import com.arduia.myacc.databinding.SheetTransactionDetailBinding
import com.arduia.myacc.ui.vto.TransactionDetailsVto
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class TransactionDetailDialog: BottomSheetDialogFragment(){

    private val viewBinding by lazy { SheetTransactionDetailBinding.inflate(layoutInflater) }

    private var transactionDetail: TransactionDetailsVto? = null

    private var editClickListener: (TransactionDetailsVto) -> Unit = {}

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        transactionDetail!!.bindDetailData()
        setupView()
        return viewBinding.root
    }

    fun setEditClickListener( listener: (TransactionDetailsVto)-> Unit){
        editClickListener = listener
    }

    fun showDetail(fm: FragmentManager, detail: TransactionDetailsVto){
       transactionDetail = detail
        show(fm, TAG)
    }

    private fun setupView(){
        viewBinding.btnDetailClose.setOnClickListener {
            dismiss()
        }

        viewBinding.btnEdit.setOnClickListener {
            editClickListener.invoke(transactionDetail!!)
            dismiss()
        }

    }

    private fun TransactionDetailsVto.bindDetailData(){
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
