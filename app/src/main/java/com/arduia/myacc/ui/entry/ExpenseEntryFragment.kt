package com.arduia.myacc.ui.entry

import android.os.Bundle
import android.util.Log.d
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.arduia.myacc.databinding.FragExpenseEntryBinding
import com.arduia.myacc.ui.vto.TransactionDetailsVto
import java.lang.IllegalStateException

class ExpenseEntryFragment : Fragment(){

    private val viewBinding by lazy {
        FragExpenseEntryBinding.inflate(layoutInflater)
    }

    private val args: ExpenseEntryFragmentArgs by navArgs()

    private val viewModel by viewModels<ExpenseEntryViewModel>()

    private var expenseData: TransactionDetailsVto? = null


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = viewBinding.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupView()
        setupViewModel()
    }

    private fun setupView(){

        val expenseId = args.expenseId
        d("Expense", "Expense Id $expenseId")


        expenseData?.let {
            viewBinding.edtName.setText(it.name)
            viewBinding.edtAmount.setText(it.amount)
            viewBinding.edtNote.setText(it.note)
            viewBinding.btnSave.isEnabled = it.amount.isNotBlank()
        }

        viewBinding.btnSave.setOnClickListener {
            updateExpense()
        }

        viewBinding.btnEntryClose.setOnClickListener {
            findNavController().popBackStack()
        }

        viewBinding.edtAmount.addTextChangedListener {
            viewBinding.btnSave.isEnabled = !it.isNullOrEmpty()
        }

    }

    private fun updateExpense(){

        val name = viewBinding.edtName.text.toString()
        val cost = viewBinding.edtAmount.text.toString()
        val description = viewBinding.edtNote.text.toString()

        //View Level Validation
        if(cost.isEmpty()){
            viewBinding.edtAmount.error = "Cost is Empty"
            return
        }

        viewModel.updateExpenseData(
            id = expenseData?.id ?: 0,
            name = name,
            cost = cost.toLongOrNull() ?: throw IllegalStateException("Entry cost is not a Decimal"),
            description = description,
            category = "Food"
        )
        clearSpendSheet()
    }

    private fun clearSpendSheet(){
        viewBinding.edtName.setText("")
        viewBinding.edtAmount.setText("")
        viewBinding.edtNote.setText("")
    }

    private fun setupViewModel(){
        viewModel.expenseDataInserted.observe(viewLifecycleOwner, Observer {
            findNavController().popBackStack()
        })
    }

    companion object{
        private const val TAG = "ExpenseUpdate"
    }


}
