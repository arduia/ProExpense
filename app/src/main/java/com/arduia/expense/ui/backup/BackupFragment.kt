package com.arduia.expense.ui.backup

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
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

    private val viewModel by viewModels<BackupViewModel>()

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

        lifecycle.addObserver(viewModel)
        setupView()
        setupViewModel()
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

    }

    private fun setupViewModel(){
        viewModel.backupList.observe(viewLifecycleOwner, Observer {
            backupListAdapter.submitList(it)
        })
    }
}
