package com.arduia.expense.ui.backup

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.arduia.expense.R
import com.arduia.expense.databinding.FragBackupBinding
import com.arduia.expense.ui.MainHost
import com.arduia.expense.ui.NavBaseFragment
import com.arduia.expense.ui.common.MarginItemDecoration
import com.arduia.core.requestStoragePermission
import com.arduia.expense.ui.BackupMessageReceiver
import com.arduia.expense.ui.vto.BackupVto
import com.arduia.mvvm.EventObserver
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class BackupFragment: NavBaseFragment(){

    private lateinit var viewBinding: FragBackupBinding

    private val viewModel by viewModels<BackupViewModel>()

    @Inject
    lateinit var mainHost: MainHost

    @Inject
    lateinit var backupListAdapter: BackupListAdapter

    private var backDetailDialog: ImportDialogFragment? = null

    private var exportDialog: ExportDialogFragment? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewBinding = FragBackupBinding.inflate(layoutInflater, container, false)

        return viewBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupView()
        setupViewModel()
    }

    private fun setupView(){

        viewBinding.btnExport.setOnClickListener {
            showExportDialog()
        }

        viewBinding.btnMenuOpen.setOnClickListener{
            navigationDrawer?.openDrawer()
        }

        viewBinding.btnExportOpen.setOnClickListener {
            showExportDialog()
        }

        viewBinding.btnImportOpen.setOnClickListener {
            openImportFolder()
        }

        backupListAdapter.setItemClickListener { backupItem ->
            viewModel.onBackupItemSelect(id = backupItem.id)
        }

        //Setup Recycler View
        viewBinding.rvBackupList.adapter = backupListAdapter
        viewBinding.rvBackupList.addItemDecoration(
            MarginItemDecoration(
                resources.getDimension(R.dimen.space_between_items).toInt(),
                resources.getDimension(R.dimen.margin_list_item).toInt()
            )
        )
    }

    private fun setupViewModel(){

        lifecycle.addObserver(viewModel)

        viewModel.backupList.observe(viewLifecycleOwner, Observer { list ->
            showBackupList(list)

            when(list.isEmpty()){
                true -> showExportButton()
                false -> hideExportButton()
            }
        })

        viewModel.backupFilePath.observe(viewLifecycleOwner, EventObserver{ fileUri ->
            showImportDialog(uri = fileUri)
        })

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        val isDocResult =   (requestCode == OPEN_DOC_CODE && resultCode  == Activity.RESULT_OK)

        if(isDocResult){
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

        val isStoragePermissionGranted = (requestCode == STORAGE_PM_REQUEST_CODE && grantResults.isNotEmpty())

        if(isStoragePermissionGranted){
            mainHost.showSnackMessage("Permission is Granted!")
        }
    }


    private fun hideExportButton(){
        viewBinding.btnExport.visibility = View.INVISIBLE
    }

    private fun showExportButton(){
        viewBinding.btnExport.visibility = View.VISIBLE
    }

    private fun showBackupList(list: List<BackupVto>){
        backupListAdapter.submitList(list)
    }

    private fun showExportDialog(){
        //Close Old Detail Dialog
        exportDialog?.dismiss()
        exportDialog = ExportDialogFragment()
        exportDialog?.show(parentFragmentManager, ExportDialogFragment.TAG)
    }

    private fun showImportDialog(uri: Uri){
        //Close Old Detail Dialog
        backDetailDialog?.dismiss()
        backDetailDialog = ImportDialogFragment()
        backDetailDialog?.showDialog(parentFragmentManager, uri)
    }

    private fun openImportFolder(){
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "application/vnd.ms-excel"
        }
        startActivityForResult(intent, OPEN_DOC_CODE)
    }

    companion object{
        private const val STORAGE_PM_REQUEST_CODE = 3000
        private const val OPEN_DOC_CODE = 9000
    }
}
