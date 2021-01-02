package com.arduia.expense.ui.backup

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.arduia.core.view.asGone
import com.arduia.core.view.asVisible
import com.arduia.expense.R
import com.arduia.expense.databinding.FragBackupBinding
import com.arduia.expense.ui.MainHost
import com.arduia.expense.ui.NavBaseFragment
import com.arduia.expense.ui.common.delete.DeleteConfirmFragment
import com.arduia.expense.ui.common.uimodel.DeleteInfoUiModel
import com.arduia.expense.ui.common.helper.MarginItemDecoration
import com.arduia.mvvm.EventObserver
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class BackupFragment : NavBaseFragment() {

    private var _binding: FragBackupBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModels<BackupViewModel>()

    @Inject
    lateinit var mainHost: MainHost

    private var backupListAdapter: BackupListAdapter? = null
    private var backDetailDialog: ImportDialogFragment? = null 
    private var exportDialog: ExportDialogFragment? = null
    private var deleteDialog: DeleteConfirmFragment? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragBackupBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupView()
        setupViewModel()
    }

    private fun setupView() {

        backupListAdapter = BackupListAdapter(layoutInflater)

        binding.cvExport.setOnClickListener {
            showExportDialog()
        }

        binding.toolbar.setNavigationOnClickListener {
            navigationDrawer?.openDrawer()
        }
        binding.cvImport.setOnClickListener {
            openImportFolder()
        }

        //Setup Recycler View
        binding.rvBackupLogs.adapter = backupListAdapter
        binding.rvBackupLogs.addItemDecoration(
            MarginItemDecoration(
                spaceHeight = resources.getDimension(R.dimen.grid_1).toInt()
            )
        )
        backupListAdapter?.setItemClickListener(::showDeleteConfirmDialog)
    }

    private fun showDeleteConfirmDialog(backupItem: BackupUiModel){
        deleteDialog?.dismiss()
        deleteDialog = DeleteConfirmFragment()
        deleteDialog?.setOnConfirmListener {
            viewModel.onBackupDeleteConfirmed(backupItem)
        }
        deleteDialog?.show(childFragmentManager, DeleteInfoUiModel(1))
    }

    private fun setupViewModel() {
        viewModel.backupList.observe(viewLifecycleOwner, { list ->
            showBackupList(list)
        })

        viewModel.backupFilePath.observe(viewLifecycleOwner, EventObserver { fileUri ->
            showImportDialog(uri = fileUri)
        })

        viewModel.isEmptyBackupLogs.observe(viewLifecycleOwner){
            if(it){
                binding.tvNoData.asVisible()
            }else{
                binding.tvNoData.asGone()
            }
        }

        viewModel.isEmptyExpenseLogs.observe(viewLifecycleOwner){
                binding.cvExport.isEnabled = it.not()
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        val isDocResult = (requestCode == OPEN_DOC_CODE && resultCode == Activity.RESULT_OK)

        if (isDocResult) {
            val resultUri = data?.data ?: return
            viewModel.setImportUri(uri = resultUri)
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


    private fun hideExportButton() {
        binding.cvExport.visibility = View.INVISIBLE
    }

    private fun showExportButton() {
        binding.cvExport.visibility = View.VISIBLE
    }

    private fun showBackupList(list: List<BackupUiModel>) {
        backupListAdapter?.submitList(list)
    }

    private fun showExportDialog() {
        //Close Old Detail Dialog
        exportDialog?.dismiss()
        exportDialog = ExportDialogFragment()
        exportDialog?.show(parentFragmentManager, ExportDialogFragment.TAG)
    }

    private fun showImportDialog(uri: Uri) {
        //Close Old Detail Dialog
        backDetailDialog?.dismiss()
        backDetailDialog = ImportDialogFragment()
        backDetailDialog?.showDialog(parentFragmentManager, uri)
    }

    private fun openImportFolder() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "application/xls"
        }
        startActivityForResult(intent, OPEN_DOC_CODE)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.rvBackupLogs.adapter = null
        backupListAdapter = null
        backDetailDialog = null
        _binding = null
    }

    companion object {
        private const val STORAGE_PM_REQUEST_CODE = 3000
        private const val OPEN_DOC_CODE = 9000
    }
}
