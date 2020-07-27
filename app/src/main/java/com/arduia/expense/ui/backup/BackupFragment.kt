package com.arduia.expense.ui.backup

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.arduia.expense.R
import com.arduia.expense.databinding.FragBackupBinding
import com.arduia.expense.ui.MainHost
import com.arduia.expense.ui.NavBaseFragment
import com.arduia.expense.ui.common.MarginItemDecoration
import com.arduia.expense.ui.vto.BackupVto
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class BackupFragment: NavBaseFragment(){

    private lateinit var viewBinding: FragBackupBinding

    @Inject
    lateinit var mainHost: MainHost

    @Inject
    lateinit var backupListAdapter: BackupListAdapter


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
    }

    private fun setupView(){

        viewBinding.btnExport.setOnClickListener {
            BackupDialogFragment().show(parentFragmentManager, BackupDialogFragment.TAG)
        }

        viewBinding.btnMenuOpen.setOnClickListener{
            openDrawer()
        }

        viewBinding.rvBackupList.adapter = backupListAdapter
        viewBinding.rvBackupList.addItemDecoration(
            MarginItemDecoration(
                resources.getDimension(R.dimen.space_between_items).toInt(),
                resources.getDimension(R.dimen.margin_list_item).toInt()
            )
        )

        backupListAdapter.submitList(listOf(
            BackupVto("Backup1_sdf.expense",332, "12.2.2019", 40),
            BackupVto("Backup_some.expense" ,3423, "23.3.2020", 10)
        ))
    }

}
