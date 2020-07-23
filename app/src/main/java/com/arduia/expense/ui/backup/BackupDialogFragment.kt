package com.arduia.expense.ui.backup

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.arduia.expense.databinding.FragBackupDialogBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class BackupDialogFragment : BottomSheetDialogFragment(){

    private lateinit var viewBinding: FragBackupDialogBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewBinding = FragBackupDialogBinding.inflate(layoutInflater, container, false)

        return viewBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupView()
    }

    private fun setupView(){
        viewBinding.btnDrop.setOnClickListener {
            dismiss()
        }

        viewBinding.btnExportNow.setOnClickListener {
            dismiss()
        }
    }
    companion object{
        const val TAG = "BackDialogFragment"
    }

}
