package com.arduia.expense.ui.backup

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.viewModels
import com.arduia.design.theme.ProExpenseTheme
import com.arduia.expense.ui.MainHost
import com.arduia.expense.ui.NavBaseFragment
import com.arduia.expense.ui.backup.molecule.BackupEvent
import com.arduia.expense.ui.common.delete.DeleteConfirmFragment
import com.arduia.expense.ui.common.uimodel.DeleteInfoUiModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class BackupFragment : NavBaseFragment() {

    private val viewModel by viewModels<BackupViewModel>()

    @Inject
    lateinit var mainHost: MainHost

    private var backDetailDialog: ImportDialogFragment? = null 
    private var exportDialog: ExportDialogFragment? = null
    private var deleteDialog: DeleteConfirmFragment? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                ProExpenseTheme {
                    val state by viewModel.state.collectAsState()

                    LaunchedEffect(state.showExportDialog) {
                        if (state.showExportDialog) {
                            showExportDialog()
                            viewModel.take(BackupEvent.ExportDialogDismissed)
                        }
                    }

                    LaunchedEffect(state.importUri) {
                        state.importUri?.let { uri ->
                            showImportDialog(uri)
                            viewModel.take(BackupEvent.ImportDialogDismissed)
                        }
                    }
                    
                    LaunchedEffect(state.showDeleteConfirmFor) {
                        state.showDeleteConfirmFor?.let { item ->
                            showDeleteConfirmDialog(item)
                            viewModel.take(BackupEvent.DeleteConfirmDismissed)
                        }
                    }

                    BackupScreen(
                        state = state,
                        onEvent = { event ->
                            if (event is BackupEvent.ImportClicked) {
                                openImportFolder()
                            } else {
                                viewModel.take(event)
                            }
                        },
                        onNavigationIconClick = {
                            navigationDrawer?.openDrawer()
                        }
                    )
                }
            }
        }
    }

    private fun showDeleteConfirmDialog(backupItem: BackupUiModel){
        deleteDialog?.dismiss()
        deleteDialog = DeleteConfirmFragment()
        deleteDialog?.setOnConfirmListener {
            viewModel.take(BackupEvent.DeleteConfirmed(backupItem))
        }
        deleteDialog?.show(childFragmentManager, DeleteInfoUiModel(1))
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        val isDocResult = (requestCode == OPEN_DOC_CODE && resultCode == Activity.RESULT_OK)

        if (isDocResult) {
            val resultUri = data?.data ?: return
            viewModel.take(BackupEvent.ImportUriReceived(resultUri))
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        val isStoragePermissionGranted =
            (requestCode == STORAGE_PM_REQUEST_CODE && grantResults.isNotEmpty())

        if (isStoragePermissionGranted) {
            mainHost.showSnackMessage("Permission is Granted!")
        }
    }

    private fun showExportDialog() {
        exportDialog?.dismiss()
        exportDialog = ExportDialogFragment()
        exportDialog?.show(parentFragmentManager, ExportDialogFragment.TAG)
    }

    private fun showImportDialog(uri: android.net.Uri) {
        backDetailDialog?.dismiss()
        backDetailDialog = ImportDialogFragment()
        backDetailDialog?.showDialog(parentFragmentManager, uri)
    }

    private fun openImportFolder() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "application/vnd.ms-excel"
        }
        startActivityForResult(intent, OPEN_DOC_CODE)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        backDetailDialog = null
        exportDialog = null
        deleteDialog = null
    }

    companion object {
        private const val STORAGE_PM_REQUEST_CODE = 3000
        private const val OPEN_DOC_CODE = 9000
    }
}
