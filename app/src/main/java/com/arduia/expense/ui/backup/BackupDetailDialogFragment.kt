package com.arduia.expense.ui.backup

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.arduia.expense.databinding.FragBackupDetailBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BackupDetailDialogFragment: BottomSheetDialogFragment(){

    private lateinit var viewBinding : FragBackupDetailBinding

    private  var fileUri: Uri? = null

    private val viewModel by viewModels<BackupDetailViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
       viewBinding = FragBackupDetailBinding.inflate(layoutInflater, container, false)

        return viewBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupView()
        setupViewModel()
    }

    private fun setupView(){
        viewBinding.btnDropClose.setOnClickListener {
            dismiss()
        }

        viewBinding.btnImport.setOnClickListener {
            viewModel.importData()
        }

    }

    private fun setupViewModel(){
        viewModel.fileName.observe(viewLifecycleOwner, Observer {
            viewBinding.tvNameValue.text = it
        })

        viewModel.totalCount.observe(viewLifecycleOwner, Observer {
            viewBinding.tvItemsValue.text = it
        })

        viewModel.setFileUri(fileUri?: throw Exception("Url not found exception!"))
    }

    fun showBackupDetail(fm: FragmentManager, uri: Uri){
        this.fileUri = uri
        show(fm, "BackupDetail")
    }

}
