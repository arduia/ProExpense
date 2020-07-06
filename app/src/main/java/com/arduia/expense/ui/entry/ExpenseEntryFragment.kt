package com.arduia.expense.ui.entry

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.arduia.expense.R
import com.arduia.expense.databinding.FragExpenseEntryBinding
import com.arduia.expense.ui.common.EventObserver
import com.arduia.expense.ui.vto.ExpenseCategory
import com.google.android.material.chip.Chip
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import timber.log.Timber
import java.lang.IllegalStateException

class ExpenseEntryFragment : Fragment(){

    private val viewBinding by lazy {
        FragExpenseEntryBinding.inflate(layoutInflater).apply {
            setupView()
            setupViewModel()
        }
    }

    private val args: ExpenseEntryFragmentArgs by navArgs()

    private val viewModel by viewModels<ExpenseEntryViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = viewBinding.root

    private fun FragExpenseEntryBinding.setupView(){

        when{
            args.expenseId > 0 -> {
                // ID exist, Update Mode
                viewModel.setUpdateMode()
            }

            args.expenseId <= 0 -> {
                // ID is default, Save Mode
                viewModel.setSaveMode()
            }
        }

        btnEntryClose.setOnClickListener {
            findNavController().popBackStack()
        }

        edtAmount.addTextChangedListener {
            viewBinding.btnSave.isEnabled = !it.isNullOrEmpty()
        }
    }


    private fun FragExpenseEntryBinding.setupViewModel(){

        viewModel.dataInserted.observe(viewLifecycleOwner, EventObserver {

            findNavController().popBackStack()
        })

        viewModel.dataUpdated.observe(viewLifecycleOwner, EventObserver{

            findNavController().popBackStack()
        })

        viewModel.entryMode.observe(viewLifecycleOwner, EventObserver{
            when(it){
                ExpenseEntryMode.UPDATE -> {
                    onUpdateMode()
                }

                ExpenseEntryMode.INSERT -> {
                   onInsertMode()
                }
            }
        })

        viewModel.expenseData.observe(viewLifecycleOwner, Observer {
             edtName.setText(it.name)
             edtAmount.setText(it.amount)
             edtNote.setText(it.note)
        })
    }


    private fun onUpdateMode(){
        viewBinding.tvEntryTitle.text = getString(R.string.label_update_data)
        viewBinding.btnSave.text = getString(R.string.label_update)
        viewBinding.btnSave.setOnClickListener {
            updateData()
        }

        MainScope().launch(Dispatchers.IO){
            viewModel.observeExpenseData(args.expenseId)
        }
    }

    private fun onInsertMode(){
        viewBinding.tvEntryTitle.text = getString(R.string.label_expense_entry)
        viewBinding.btnSave.setOnClickListener {
            saveData()
        }

        viewBinding.edtName.requestFocus()
    }

    private fun saveData(){
        val name = viewBinding.edtName.text.toString()
        val cost = viewBinding.edtAmount.text.toString()
        val description = viewBinding.edtNote.text.toString()

        //View Level Validation
        if(cost.isEmpty()){
            viewBinding.edtAmount.error = "Cost is Empty"
            return
        }

        viewModel.saveExpenseData(
            name = name,
            cost = cost.toLongOrNull() ?: throw IllegalStateException("Entry cost is not a Decimal"),
            description = description,
            category = ExpenseCategory.ENTERTAINMENT.name
        )
        hideKeyboard()
    }

    private fun updateData(){

        val name = viewBinding.edtName.text.toString()
        val cost = viewBinding.edtAmount.text.toString()
        val description = viewBinding.edtNote.text.toString()

        //View Level Validation
        if(cost.isEmpty()){
            viewBinding.edtAmount.error = "Cost is Empty"
            return
        }

        viewModel.updateExpenseData(
            id = args.expenseId,
            name = name,
            cost = cost.toLongOrNull() ?: throw IllegalStateException("Entry cost is not a Decimal"),
            description = description,
            category = ExpenseCategory.SOCIAL.name
        )
        hideKeyboard()

    }

    private fun hideKeyboard(){
        val inputMethodManager =
            (requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager)
        inputMethodManager?.hideSoftInputFromWindow(viewBinding.edtName.windowToken, 0)
    }
    override fun onDestroyView() {
        super.onDestroyView()
        hideKeyboard()
    }

    companion object{
        private const val TAG = "ExpenseUpdate"
    }


}
