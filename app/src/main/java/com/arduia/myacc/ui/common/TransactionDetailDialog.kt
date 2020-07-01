package com.arduia.myacc.ui.common

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import com.arduia.core.extension.px
import com.arduia.myacc.databinding.SheetTransactionDetailBinding
import com.arduia.myacc.ui.vto.TransactionDetailsVto
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class TransactionDetailDialog: BottomSheetDialogFragment(){

    private val viewBinding by lazy { SheetTransactionDetailBinding.inflate(layoutInflater) }

    private var transactionDetail: TransactionDetailsVto? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        transactionDetail!!.bindDetailData()
        setupView()
        return viewBinding.root
    }

    fun showDetail(fm: FragmentManager, detail: TransactionDetailsVto){
       transactionDetail = detail
        show(fm, TAG)
    }

    private fun setupView(){
        viewBinding.btnDetailClose.setOnClickListener {
            dismiss()
        }
    }

    private fun TransactionDetailsVto.bindDetailData(){
        viewBinding.tvAmountValue.text = cost
        viewBinding.tvDateValue.text = date
        viewBinding.tvNameValue.text = name
        viewBinding.tvNoteValue.text = note
        viewBinding.chipCategory.setChipIconResource(category)
    }

    companion object{
        private const val TAG = "TransactionDetail"
    }
}
