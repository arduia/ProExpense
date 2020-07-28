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
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.arduia.core.extension.px
import com.arduia.expense.ui.MainHost
import com.arduia.expense.R
import com.arduia.expense.databinding.FragExpenseEntryBinding
import com.arduia.expense.ui.common.*
import com.arduia.expense.ui.vto.ExpenseDetailsVto
import com.arduia.mvvm.EventObserver
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class ExpenseEntryFragment : Fragment() {

    private lateinit var viewBinding: FragExpenseEntryBinding

    private val args: ExpenseEntryFragmentArgs by navArgs()

    private val viewModel by viewModels<ExpenseEntryViewModel>()

    private var mainHost: MainHost? = null

    private lateinit var categoryAdapter: CategoryListAdapter

    @Inject
    lateinit var categoryProvider: ExpenseCategoryProvider

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

            updateCategoryList()
        }

    }

    private fun updateCategoryList() {
        val selectedCategory = categoryAdapter.selectedItem


        selectedCategory?.let {
            val allCategory = categoryProvider.getCategoryList().toMutableList().apply {
                remove(it)
            }
            allCategory.add(0, it)
            categoryAdapter.submitList(allCategory)
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

            //Show Single Item Category First
            viewModel.selectCategory(it.category)
            categoryAdapter.submitList(listOf(it.category))
        })


        viewModel.selectedCategory.observe(viewLifecycleOwner, Observer {
            categoryAdapter.selectedItem = it
        })

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

        //Show Outcome Category FirstK
        val defaultCategory = categoryProvider.getCategoryByID(ExpenseCategory.OUTCOME)
        categoryAdapter.submitList(listOf(defaultCategory))
        viewModel.selectCategory(defaultCategory)
    }

    private fun saveData() {

        categoryAdapter.selectedItem?:return

        val currentExpenseDetail = getExpenseDetail()
        //View Level Validation
        if (currentExpenseDetail.amount.isEmpty()) {
            viewBinding.edtAmount.error = getString(R.string.label_cost_empty)
            return
        }
        viewModel.saveExpenseData(currentExpenseDetail)
    }

    private fun updateData() {

        categoryAdapter.selectedItem?:return

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
