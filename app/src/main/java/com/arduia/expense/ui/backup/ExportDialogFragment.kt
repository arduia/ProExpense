package com.arduia.expense.ui.backup

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.arduia.expense.databinding.FragExportDialogBinding
import com.arduia.expense.ui.MainHost
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ExportDialogFragment : BottomSheetDialogFragment(){

    private lateinit var viewBinding: FragExportDialogBinding

    @Inject
    lateinit var mainHost: MainHost

    private val viewModel by viewModels<ExportViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewBinding = FragExportDialogBinding.inflate(layoutInflater, container, false)

        return viewBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycle.addObserver(viewModel)
        setupView()
        setupViewModel()
    }

    private fun setupView(){

        viewBinding.btnDrop.setOnClickListener {
            closeDialog()
        }

        viewBinding.btnExportNow.setOnClickListener {
            openExternalFileBrowserToSave()
        }
    }

    private fun setupViewModel(){

        viewModel.exportFileName.observe(viewLifecycleOwner, Observer {fileName ->
            viewBinding.edtName.setText(fileName)

            viewBinding.edtName.selectAll()
        })
    }

    private fun openExternalFileBrowserToSave(){

        val fileNameWithExtension =  getCurrentFileName() + ".xls"

        val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "application/xls"
            putExtra(Intent.EXTRA_TITLE, fileNameWithExtension)
        }

        startActivityForResult(intent, SAVE_AS_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        val isSaveAsRequestOK = (requestCode == SAVE_AS_REQUEST_CODE && resultCode == Activity.RESULT_OK)

        if(isSaveAsRequestOK){
            val selectedFileUri = data?.data ?: return
            onSaveFileUriReturn(fieUri = selectedFileUri)
        }
    }

    private fun onSaveFileUriReturn(fieUri: Uri){

        val currentFileName = getCurrentFileName()
        viewModel.exportData(fileName = currentFileName, fileUri = fieUri)
        closeDialog()
    }

    private fun getCurrentFileName() = viewBinding.edtName.text.toString()

    private fun closeDialog(){
        dismiss()
    }

    companion object{
        const val TAG = "BackupDialogFragment"
        private const val SAVE_AS_REQUEST_CODE = 8000
    }

}
