package com.arduia.expense.ui.entry

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.arduia.core.extension.px
import com.arduia.expense.ui.MainHost
import com.arduia.expense.R
import com.arduia.expense.databinding.FragExpenseEntryBinding
import com.arduia.expense.ui.common.EventObserver
import com.arduia.expense.ui.common.ExpenseCategoryProviderImpl
import com.arduia.expense.ui.common.MarginItemDecoration
import com.arduia.expense.ui.vto.ExpenseDetailsVto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ExpenseEntryFragment : Fragment() {

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
    ): View? {
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

        MainScope().launch(Dispatchers.Main) {
            val aniDuration = resources.getInteger(R.integer.entry_pop_up_duration)
            delay(aniDuration.toLong())

            afterAnimation()
        }

    }

    private fun afterAnimation() {

        val categoryList = categoryProvider.getCategoryList()
        categoryAdapter.submitList(categoryList)

        viewBinding.btnSave.isEnabled = true

        //Start Observe Selected Item
        viewModel.selectedCategory.observe(viewLifecycleOwner) {
            categoryAdapter.selectedItem = it

            //Scroll to Selected Category
            val index = categoryProvider.getIndexByCategory(it)

            if (index < 0) return@observe
            viewBinding.rvCategory.smoothScrollToPosition(index)
        }

    }

    private fun setupView() {

        categoryAdapter = CategoryListAdapter(layoutInflater)

        viewBinding.rvCategory.adapter = categoryAdapter

        viewBinding.rvCategory.addItemDecoration(
            MarginItemDecoration(
                requireContext().px(4),
                requireContext().px(4),
                true
            )
        )

        mainHost = requireActivity() as MainHost

        viewBinding.btnEntryClose.setOnClickListener {
            findNavController().popBackStack()
        }

        categoryAdapter.setOnItemClickListener {
            viewModel.selectCategory(it)
        }

        val sampleCategory = categoryProvider.getCategoryList().take(1)
        categoryAdapter.submitList(sampleCategory)


    }

    private fun setupViewModel() {

        val defaultExpenseID =  resources.getInteger(R.integer.default_expense_id)

        when (args.expenseId == defaultExpenseID) {
            true -> {
                // ID is default, Save Mode
                viewModel.setSaveMode()
            }
            false -> {
                // ID exist, Update Mode
                viewModel.setUpdateMode()
            }
        }

        viewModel.dataInserted.observe(viewLifecycleOwner, EventObserver {
            findNavController().popBackStack()
        })

        viewModel.dataUpdated.observe(viewLifecycleOwner, EventObserver {
            mainHost?.showSnackMessage(getString(R.string.label_data_updated))
            findNavController().popBackStack()
        })

//        viewModel.isLoading.observe(viewLifecycleOwner, Observer {
//             viewBinding.pbLoading.visibility = when(it){
//                 true -> View.VISIBLE
//                 false -> View.INVISIBLE
//             }
//        })

        viewModel.entryMode.observe(viewLifecycleOwner, EventObserver {
            when (it) {
                ExpenseEntryMode.UPDATE -> {
                    changeUpdateMode()
                }

                ExpenseEntryMode.INSERT -> {
                    changeSaveMode()
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

    private fun changeUpdateMode() = with(viewBinding) {
        tvEntryTitle.text = getString(R.string.label_update_data)
        btnSave.text = getString(R.string.label_update)
        btnSave.setOnClickListener { updateData() }
        viewModel.setExpenseData(args.expenseId)
    }

    private fun changeSaveMode() = with(viewBinding) {
        tvEntryTitle.text = getString(R.string.label_expense_entry)
        btnSave.text = getString(R.string.label_save)
        btnSave.setOnClickListener { saveData() }
        edtName.requestFocus()
    }

    private fun saveData() {
        val currentExpenseDetail = getExpenseDetail()
        //View Level Validation
        if (currentExpenseDetail.amount.isEmpty()) {
            viewBinding.edtAmount.error = getString(R.string.label_cost_empty)
            return
        }
        viewModel.saveExpenseData(currentExpenseDetail)
    }

    private fun updateData() {
        val currentExpenseDetail = getExpenseDetail()
        //View Level Validation
        if (currentExpenseDetail.amount.isEmpty()) {
            viewBinding.edtAmount.error = getString(R.string.label_cost_empty)
            return
        }
        viewModel.updateExpenseData(currentExpenseDetail)
    }

    private fun getExpenseDetail(): ExpenseDetailsVto{

        val name = viewBinding.edtName.text.toString()
        val cost = viewBinding.edtAmount.text.toString()
        val description = viewBinding.edtNote.text.toString()

        val category = categoryAdapter.selectedItem ?: throw Exception("Category Item is not selected!")

        return ExpenseDetailsVto(
            args.expenseId,
            name,
            "",
            category.id,
            cost,
            "",
            description
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        val inputMethodManager =
            (requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager)
        inputMethodManager?.hideSoftInputFromWindow(viewBinding.edtName.windowToken, 0)
    }

    companion object {
        private const val TAG = "ExpenseUpdate"
    }

}
