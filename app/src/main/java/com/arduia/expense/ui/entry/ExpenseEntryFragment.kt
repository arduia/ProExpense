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
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.arduia.core.extension.px
import com.arduia.expense.MainHost
import com.arduia.expense.R
import com.arduia.expense.databinding.FragExpenseEntryBinding
import com.arduia.expense.ui.common.EventObserver
import com.arduia.expense.ui.common.ExpenseCategoryProviderImpl
import com.arduia.expense.ui.common.MarginItemDecoration
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber
import java.lang.IllegalStateException

class ExpenseEntryFragment : Fragment(){

    private lateinit var viewBinding: FragExpenseEntryBinding

    private val args: ExpenseEntryFragmentArgs by navArgs()

    private val viewModel by viewModels<ExpenseEntryViewModel>()

    private var mainHost: MainHost? = null

    private lateinit var categoryAdapter: CategoryListAdapter

    private val categoryProvider by lazy {
        ExpenseCategoryProviderImpl(resources)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View?{
        viewBinding = FragExpenseEntryBinding.inflate(layoutInflater, container, false)

        return viewBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycle.addObserver(viewModel)
        setupView()
        setupViewModel()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
       fillCategoryLists()
    }

    private fun fillCategoryLists(){

        MainScope().launch (Dispatchers.Main){

            val aniDuration = resources.getInteger(R.integer.entry_pop_up_duration)
            delay(aniDuration.toLong())

            categoryAdapter.submitList( categoryProvider.getCategoryList())

            //Start Observe Selected Item
            viewModel.selectedCategory.observe(viewLifecycleOwner){
                categoryAdapter.selectedItem = it

                //Scroll to Selected Category
                val index = categoryProvider.getIndexByCategory(it)
                if(index <0 ) return@observe
                viewBinding.rvCategory.smoothScrollToPosition(index)

            }
        }

    }
    private fun setupView() {
        categoryAdapter = CategoryListAdapter(layoutInflater)
        viewBinding.rvCategory.adapter = categoryAdapter
        viewBinding.rvCategory.addItemDecoration(MarginItemDecoration(
            requireContext().px(4),
            requireContext().px(4),
            true
        ))

        mainHost = requireActivity() as MainHost

        viewBinding.btnEntryClose.setOnClickListener {
            findNavController().popBackStack()
        }

        viewBinding.edtAmount.addTextChangedListener {
            viewBinding.btnSave.isEnabled = !it.isNullOrEmpty()
        }

        categoryAdapter.setOnItemClickListener {
             viewModel.selectCategory(it)
        }

        categoryAdapter.submitList(categoryProvider.getCategoryList().take(1) )
    }

    private fun setupViewModel(){

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
        viewModel.dataInserted.observe(viewLifecycleOwner, EventObserver {
            findNavController().popBackStack()
        })

        viewModel.dataUpdated.observe(viewLifecycleOwner, EventObserver{
            mainHost?.showSnackMessage(getString(R.string.label_data_updated))
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
            viewBinding.edtName.setText(it.name)
            viewBinding.edtAmount.setText(it.amount)
            viewBinding.edtNote.setText(it.note)
            viewModel.selectCategory(it.category)
        })

        viewModel.selectCategory(categoryProvider.getCategoryByID(1))
    }


    private fun onUpdateMode(){
        viewBinding.tvEntryTitle.text = getString(R.string.label_update_data)
        viewBinding.btnSave.text = getString(R.string.label_update)
        viewBinding.btnSave.setOnClickListener {
            updateData()
        }
        viewModel.observeExpenseData(args.expenseId)
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

        val category = categoryAdapter.selectedItem!!

        viewModel.saveExpenseData(
            name = name,
            cost = cost.toLongOrNull() ?: throw IllegalStateException("Entry cost is not a Decimal"),
            description = description,
            category = category.id
        )

        clearSpendSheet()
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

        val category = categoryAdapter.selectedItem!!

        viewModel.updateExpenseData(
            id = args.expenseId,
            name = name,
            cost = cost.toLongOrNull() ?: throw IllegalStateException("Entry cost is not a Decimal"),
            description = description,
            category = category.id

        )

        clearSpendSheet()
    }

    private fun clearSpendSheet(){
        viewBinding.edtName.setText("")
        viewBinding.edtAmount.setText("")
        viewBinding.edtNote.setText("")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        val inputMethodManager =
            (requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager)
        inputMethodManager?.hideSoftInputFromWindow(viewBinding.edtName.windowToken, 0)
    }

    companion object{
        private const val TAG = "ExpenseUpdate"
    }

}
