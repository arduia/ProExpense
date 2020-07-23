package com.arduia.expense.ui.backup

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.arduia.expense.databinding.FragBackupBinding
import com.arduia.expense.ui.MainHost
import com.arduia.expense.ui.NavBaseFragment
import timber.log.Timber

class BackupFragment: NavBaseFragment(){

    private lateinit var viewBinding: FragBackupBinding

    private val mainHost by lazy { requireActivity() as MainHost }

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
    }

}
