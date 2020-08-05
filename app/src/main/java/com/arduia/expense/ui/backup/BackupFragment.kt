package com.arduia.expense.ui.backup

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
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
import com.arduia.mvvm.EventObserver
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import java.lang.Exception
import javax.inject.Inject

@AndroidEntryPoint
class BackupFragment: NavBaseFragment(){

    private lateinit var viewBinding: FragBackupBinding

    private val viewModel by viewModels<BackupViewModel>()

    @Inject
    lateinit var mainHost: MainHost

    @Inject
    lateinit var backupListAdapter: BackupListAdapter

    private var backDetailDialog: BackupDetailDialogFragment? = null

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

        lifecycle.addObserver(viewModel)
        setupView()
        setupViewModel()
        checkRequestPermission()
    }


    private fun setupView(){

        viewBinding.btnExport.setOnClickListener {
            BackupDialogFragment().show(parentFragmentManager, BackupDialogFragment.TAG)
        }

        viewBinding.btnMenuOpen.setOnClickListener{
            openDrawer()
        }

        viewBinding.btnExportOpen.setOnClickListener {
            BackupDialogFragment().show(parentFragmentManager, BackupDialogFragment.TAG)
        }

        viewBinding.btnImportOpen.setOnClickListener {
            openImportFolder()
        }

        viewBinding.rvBackupList.adapter = backupListAdapter
        viewBinding.rvBackupList.addItemDecoration(
            MarginItemDecoration(
                resources.getDimension(R.dimen.space_between_items).toInt(),
                resources.getDimension(R.dimen.margin_list_item).toInt()
            )
        )

        backupListAdapter.setItemClickListener {
            viewModel.selectBackupItem(it.id)
        }

    }

    private fun openImportFolder(){
        val openDocumentIntent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "application/vnd.ms-excel"
        }
        startActivityForResult(openDocumentIntent, OPEN_DOC_CODE)
    }

    private fun checkRequestPermission(){
        val storagePm = ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
        when(storagePm == PackageManager.PERMISSION_GRANTED){
            true -> {
                Timber.d("Permission is already Granted!")
            }
            false -> {
                Timber.d("Permission is not Granted!")
                requestStoragePermission(STORAGE_PM_REQUEST_CODE)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == OPEN_DOC_CODE && resultCode == Activity.RESULT_OK){
           Timber.d("Open Document Result OK")
            viewModel.selectImportUri( data?.data ?: throw Exception("Uri Not Found!"))
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == STORAGE_PM_REQUEST_CODE && grantResults.isNotEmpty()){
            mainHost.showSnackMessage("Permission is Granted!")
        }
    }


    private fun setupViewModel(){
        viewModel.backupList.observe(viewLifecycleOwner, Observer {
            Timber.d("setupViewModel -> $it")
            backupListAdapter.submitList(it)
            viewBinding.btnExport.visibility = if(it.isNotEmpty()) View.INVISIBLE else View.VISIBLE
        })

        viewModel.backupFilePath.observe(viewLifecycleOwner, EventObserver{
            //Close Old Detail Dialog
            backDetailDialog?.dismiss()
            backDetailDialog = BackupDetailDialogFragment()
            backDetailDialog?.showBackupDetail(parentFragmentManager, it)

        })
    }

    companion object{
        private const val STORAGE_PM_REQUEST_CODE = 3000
        private const val OPEN_DOC_CODE = 9000
    }
}
