package com.arduia.expense.ui.backup

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.arduia.expense.databinding.FragBackupDetailBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class BackupDetailDialogFragment: BottomSheetDialogFragment(){

    private lateinit var viewBinding : FragBackupDetailBinding


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
       viewBinding = FragBackupDetailBinding.inflate(layoutInflater, container, false)

        return viewBinding.root
    }

}