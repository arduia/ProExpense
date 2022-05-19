package com.arduia.expense.ui.backup

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.arduia.expense.R
import com.arduia.expense.databinding.FragBackupDetailBinding
import com.arduia.expense.ui.BackupMessageReceiver
import com.arduia.expense.ui.MainActivity
import com.arduia.expense.ui.MainHost
import com.arduia.mvvm.EventObserver
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ImportDialogFragment : BottomSheetDialogFragment() {

    private lateinit var viewBinding: FragBackupDetailBinding

    private var fileUri: Uri? = null

    private val viewModel by viewModels<ImportViewModel>()

    @Inject
    lateinit var mainHost: MainHost

    lateinit var backupMsgReceiver: BackupMessageReceiver

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

        backupMsgReceiver = requireActivity() as MainActivity
        setupView()
        setupViewModel()
    }

    private fun setupView() {
        viewBinding.btnDropClose.setOnClickListener {
            this.dismiss()
        }

        viewBinding.btnImport.setOnClickListener {
            viewModel.startImportData()
        }

    }

    private fun setupViewModel() {
        viewModel.fileName.observe(viewLifecycleOwner, Observer { name ->
            viewBinding.tvNameValue.text = name
        })

        viewModel.totalCount.observe(viewLifecycleOwner, Observer { count ->
            viewBinding.tvItemsValue.text = count
        })

        viewModel.closeEvent.observe(viewLifecycleOwner, EventObserver {
            this.dismiss()
        })

        viewModel.fileNotFoundEvent.observe(viewLifecycleOwner, EventObserver {
            mainHost.showSnackMessage(getString(R.string.file_not_exist))
        })

        viewModel.backupTaskEvent.observe(viewLifecycleOwner, EventObserver { id ->
            backupMsgReceiver.registerBackupTaskID(id)
        })

        viewModel.loadingEvent.observe(viewLifecycleOwner, EventObserver { isLoading ->
            if (isLoading) {
                showLoading()
                disableEditButton()
            } else {
                hideLoading()
                enableEditButton()
            }
        })

        val importFileUrl = this.fileUri ?: throw Exception("Url not found exception!")
        viewModel.setFileUri(importFileUrl)
    }

    fun showDialog(fm: FragmentManager, uri: Uri) {
        this.fileUri = uri
        show(fm, "BackupDetail")
    }

    private fun disableEditButton() {
        viewBinding.btnImport.isEnabled = false
    }

    private fun enableEditButton() {
        viewBinding.btnImport.isEnabled = true
    }

    private fun showLoading() {
        viewBinding.pbLoading.visibility = View.VISIBLE
    }

    private fun hideLoading() {
        viewBinding.pbLoading.visibility = View.INVISIBLE
    }
}
