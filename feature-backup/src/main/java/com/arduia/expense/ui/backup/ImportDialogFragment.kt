package com.arduia.expense.ui.backup

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.arduia.expense.R
import com.arduia.expense.databinding.FragmentBackupDetailBinding
import com.arduia.expense.ui.BackupMessageReceiver
import com.arduia.expense.ui.MainActivity
import com.arduia.expense.ui.MainHost
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class ImportDialogFragment : BottomSheetDialogFragment() {

    private lateinit var viewBinding: FragmentBackupDetailBinding

    private var fileUri: Uri? = null

    private val viewModel by viewModels<ImportViewModel>()

    @Inject
    lateinit var mainHost: MainHost

    lateinit var backupMsgReceiver: BackupMessageReceiver

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewBinding = FragmentBackupDetailBinding.inflate(layoutInflater, container, false)
        return viewBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        backupMsgReceiver = requireActivity() as MainActivity
        setupView()
        collectState()
        collectEffects()

        val importFileUrl = this.fileUri ?: throw Exception("Url not found exception!")
        viewModel.setFileUri(importFileUrl)
    }

    private fun setupView() {
        viewBinding.btnDropClose.setOnClickListener {
            this.dismiss()
        }

        viewBinding.btnImport.setOnClickListener {
            viewModel.startImportData()
        }
    }

    private fun collectState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    viewBinding.tvNameValue.text = state.fileName
                    viewBinding.tvItemsValue.text = state.totalCount

                    if (state.isLoading) {
                        showLoading()
                        disableEditButton()
                    } else {
                        hideLoading()
                        enableEditButton()
                    }
                }
            }
        }
    }

    private fun collectEffects() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiEffect.collect { effect ->
                    when (effect) {
                        is ImportUiEffect.Close -> this@ImportDialogFragment.dismiss()
                        is ImportUiEffect.FileNotFound -> mainHost.showSnackMessage(getString(R.string.file_not_exist))
                        is ImportUiEffect.BackupTaskStarted -> backupMsgReceiver.registerBackupTaskID(effect.taskId)
                    }
                }
            }
        }
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
