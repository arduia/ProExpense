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
import timber.log.Timber
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
            dismiss()
        }

        viewBinding.btnExportNow.setOnClickListener {
            openSaveAs()
        }
    }

    private fun setupViewModel(){

        viewModel.exportFileName.observe(viewLifecycleOwner, Observer {
            viewBinding.edtName.setText(it)

            viewBinding.edtName.selectAll()
        })
    }

    private fun openSaveAs(){

        val fileName =  viewBinding.edtName.text.toString() + ".xls"

        val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "application/xls"
            putExtra(Intent.EXTRA_TITLE, fileName)
        }

        startActivityForResult(intent, SAVE_AS_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Timber.d("onActivityResult -> ")
        if(requestCode == SAVE_AS_CODE && resultCode == Activity.RESULT_OK){
            Timber.d("onActivityResult -> ")
            data?.data?.also {
                Timber.d("FilePath -> $it")
                onUriReturn(uri = it)
            }
        }
    }

    private fun onUriReturn(uri: Uri){
        val fileName =  viewBinding.edtName.text.toString()
        viewModel.exportData(fileName, uri)
        dismiss()
    }

    companion object{
        const val TAG = "BackupDialogFragment"
        private const val SAVE_AS_CODE = 8000
    }

}
