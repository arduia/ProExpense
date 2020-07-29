package com.arduia.expense.ui.backup

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.arduia.expense.databinding.FragBackupDialogBinding
import com.arduia.expense.ui.MainHost
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class BackupDialogFragment : BottomSheetDialogFragment(){

    private lateinit var viewBinding: FragBackupDialogBinding

    @Inject
    lateinit var mainHost: MainHost

    private val viewModel by viewModels<BackupViewModel>()

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

        viewBinding.edtName.setText("BackupExpense")

        viewBinding.edtName.selectAll()

        viewBinding.btnDrop.setOnClickListener {
            dismiss()
        }

        viewBinding.btnExportNow.setOnClickListener {
            val backupName = viewBinding.edtName.text.toString()
            viewModel.exportBackup(backupName)
            dismiss()
        }
    }

    private fun setupViewModel(){

    }

    companion object{
        const val TAG = "BackupDialogFragment"
    }

}
